pipeline {
    agent any

    environment {
        MAVEN_HOME = tool 'Maven 3.8'
        JAVA_HOME = tool 'JDK 17'
        NEXUS_URL = 'http://nexus.example.com/repository/maven-releases/'  // URL репозитория Nexus
        NEXUS_CREDENTIALS_ID = 'nexus-credentials'  // ID учетных данных Nexus в Jenkins
        DOCKER_REGISTRY = 'docker-registry.example.com'
        DOCKER_CREDENTIALS_ID = 'docker-credentials' // ID учетных данных Docker Registry
        APP_NAME = 'my-java-app'
        VERSION = "1.0.${env.BUILD_NUMBER}"
    }

    stages {
        stage('Checkout') {
            steps {
                // Получаем исходный код из репозитория
                checkout scm
            }
        }

        stage('Build') {
            steps {
                // Сборка с помощью Maven
                withMaven(maven: 'Maven 3.8', jdk: 'JDK 17') {
                    sh 'mvn clean install -DskipTests'
                }
            }
        }

        stage('Unit Tests') {
            steps {
                // Запуск юнит-тестов
                withMaven(maven: 'Maven 3.8', jdk: 'JDK 17') {
                    sh 'mvn test'
                }
            }
        }

        stage('Package') {
            steps {
                // Упаковка приложения
                sh 'mv target/*.jar target/app.jar'
            }
        }

        stage('Build Docker Image') {
            steps {
                script {
                    // Строим Docker-образ с JAR-файлом
                    docker.build("${DOCKER_REGISTRY}/${APP_NAME}:${VERSION}", "-f Dockerfile .")
                }
            }
        }

        stage('Push to Docker Registry') {
            steps {
                script {
                    // Пушим Docker-образ в Docker Registry
                    docker.withRegistry("https://${DOCKER_REGISTRY}", DOCKER_CREDENTIALS_ID) {
                        docker.image("${DOCKER_REGISTRY}/${APP_NAME}:${VERSION}").push()
                    }
                }
            }
        }

        stage('Publish to Nexus') {
            steps {
                script {
                    // Публикация JAR-файла в Nexus
                    withCredentials([usernamePassword(credentialsId: NEXUS_CREDENTIALS_ID, usernameVariable: 'NEXUS_USER', passwordVariable: 'NEXUS_PASS')]) {
                        sh """
                            curl -u ${NEXUS_USER}:${NEXUS_PASS} --upload-file target/app.jar \
                            ${NEXUS_URL}/${APP_NAME}/${VERSION}/app.jar
                        """
                    }
                }
            }
        }
    }

    post {
        always {
            // Архивируем отчет о тестировании и собранные артефакты
            archiveArtifacts artifacts: 'target/*.jar', allowEmptyArchive: true
            junit 'target/surefire-reports/*.xml'
        }
        success {
            echo 'Сборка завершена успешно!'
        }
        failure {
            echo 'Сборка завершилась с ошибками.'
        }
    }
}
