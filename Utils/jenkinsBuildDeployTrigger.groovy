pipeline {
    agent{
        label any
    }

    environment {
        SSH_CREDENTIALS_ID = 'bitbucket-ssh'
        BUILD_JOB_NAME = 'CI_JOB_NAME'
        JENKINS_URL = 'https://my-home-jenkins.local/ci/job/DEPLOY_JOB'
        CONFIG_DIR = 'DEV'
        OSE_CLUSTER_DEV = 'dev.cluster.local'
        OSE_CLUSTER_PROD = 'prod.cluster.loca'
        PLAYBOOKS_OSE_NGINX_DEPLOY = 'false,false,false,true,false,false,true,'
        PLAYBOOKS_NGINX_DEPLOY = 'false,false,false,true,false,false,true,'
    }

    stages{
        stage('Получение перменных для запуска'){
            steps {
                script {
                    sh "env"
                    dev webhookPayload = readJSON text: env.JSON_PAYLOAD
                    if (webhookPayload.repository){
                        env.APP_NAME = webhookPayload.repository.name.toLowerCase()
                        def APP_REPO_LIST = env.APP_REPO.replace('[','').replace(']','').replace('"','').split(',')
                        def lowerCaseAppName = APP_NAME.toLowerCase()
                        def APP_REPO = APP_REPO_LIST.find { it.contains("${lowerCaseAppName}.git") }
                    } else if (){
                        env.APP_NAME = webhookPayload.pullRequest.fromRef.repository.name.toLowerCase()
                        def APP_REPO_LIST = env.APP_REPO.replace('[','').replace(']','').replace('"','').split(',')
                        def lowerCaseAppName = APP_NAME.toLowerCase()
                        def APP_REPO = APP_REPO_LIST.find { it.contains("${lowerCaseAppName}.git") }
                        env.APP_BRANCH = webhookPayload.pullRequest.toRef.displayId
                    }
                }
            }
        }

        stage('Проверка наличия собственного файла Pipeline'){
            steps {
                script {
                    if (env.APP_REPO){
                        echo "--------------------ПРОВЕРКА РЕПОЗИТОРИЯ: ${APP_REPO}-----------------------"
                        sshagent([env.SSH_CREDENTIALS_ID]) {
                            def tempDir = "${pwd()}/tempRepo"
                            sh "mkdir -p ${tempDir}"
                            sh "git clone ${APP_REPO} ${tempDir}"
                            sh "cd ${tempDir} & ls -la"
                            def fileNames = ['Jenkinsfile', 'Jenkinsfile.groovy', 'jenkinsfile.groovy']
                            // проверяем наличие Jenkinsfile
                            for (jenkinsFileName in fileNames) {
                                def jenkinsFileExist = fileExist("${tempDir}/distrib-config/${jenkinsFileName}")
                                if (jenkinsFileExist){
                                    echo "############### В репозитории пристуствует файл сборки ##################"
                                    env.JENKINSFILE = 'True'
                                }
                            }
                            sh "rm -rf ${tempDir}"
                        }
                        if (env.JENKINSFILE) {
                            echo "Переменная запуска выставленна в ${env.JENKINSFILE}"
                        } else {
                            echo "Переменная запуска выставленна в ${env.JENKINSFILE}"
                        }
                    }
                }
            }
        }
        stage('Запуск сборки'){
            steps {
                script {
                    if (env.JENKINSFILE){
                        env.CONFIG_REPO = env.APP_REPO
                        env.CONFIG_BRANCH = env.APP_BRANCH
                        echo "Запускаем сборку с параметрами CONFIG_REPO: ${CONFIG_REPO}, CONFIG_BRANCH: ${CONFIG_BRANCH}"
                        def runJob = build job: env.BUILD_JOB_NAME, parameters: [
                            string(name: 'CONFIG_REPO', value: "${CONFIG_REPO}"),
                            string(name: 'CONFIG_BRANCH', value: "${CONFIG_BRANCH}"),
                        ], propagate: false
                    } else {
                        echo "-------------------Запускаем общим пайплайном--------------------"
                        echo "APP NAME: ${APP_NAME}"
                        echo "APP REPO: ${APP_REPO}"
                        echo "APP BRANCH: ${APP_BRANCH}"
                        echo "VERSION: ${CONFIG_DIR}"
                        echo "-------------------Идёт сборка дистрибутива--------------------"
                        def runJob = build job: env.BUILD_JOB_NAME, parameters: [
                            string(name: 'APP_NAME', value: "${APP_NAME}"),
                            string(name: 'APP_REPO', value: "${APP_REPO}"),
                            string(name: 'APP_BRANCH', value: "${APP_BRANCH}"),
                            string(name: 'VERSION', value: "${CONFIG_DIR}"),
                        ], propagate: false
                        try {
                            def artifactVersion = runJob.getBuildVariables().VERSION
                            echo "--------------------- Сборка завершилась со статусом: ${runJob.result}--------------------"
                            env.DISTRIB_VERSION = artifactVersion
                            echo "Версия дистрибутива: ${artifactVersion}"
                        } catch (e) {
                            echo "--------------------- Сборка завершилась со статусом: ${runJob.result}--------------------"
                        }
                    }
                }
            }
        }
        stage('Обработка переменных деплоя'){
            steps {
                script {
                    echo "----------------------------Обработка переменных деплоя----------------------------------"
                    env.SUBSYSTEM = APP_NAME.toUpperCase().replace('-','_')
                    echo "CONFIG_DIR=${CONFIG_DIR}"
                    echo "Тип развёртывания=обычный,"
                    echo "SUBSYSTEM=${SUBSYSTEM}"
                    echo "COMPONENTS=Основная ФП ${SUBSYSTEM},"
                    echo "DISTRIB_VERSION=${DISTRIB_VERSION},"
                    echo "SECTOR=sbercost,"
                    echo "OSE_CLUSTERS=${OSE_CLUSTER_DEV}"
                    echo "PARAMS=${PLAYBOOKS_NGINX_DEPLOY}"
                }
            }
        }
        stage('Запуск деплоя дистрибутива'){
            steps {
                script {
                    echo "----------------------------Запуск деплоя ${SUBSYSTEM}----------------------------------"
                    def initPlaybooks = ''
                    if (env.SUBSYSTEM == 'DF_CORE'){
                        initPlaybooks = env.PLAYBOOKS_OSE_NGINX_DEPLOY
                    } else {
                        initPlaybooks = env.PLAYBOOKS_NGINX_DEPLOY
                    }
                    def params = [
                        "CONFIG_DIR=${CONFIG_DIR}",
                        "Тип развёртывания=обычный,",
                        "SUBSYSTEM=${SUBSYSTEM}",
                        "COMPONENTS=Основная ФП ${SUBSYSTEM},",
                        "DISTRIB_VERSION=${DISTRIB_VERSION},",
                        "SECTOR=sbercost,",
                        "OSE_CLUSTERS=${OSE_CLUSTER_DEV},",
                        "PARAMS=${initPlaybooks}",
                        "DEPLOY_TO_OSE_NAMESPACE_MULTICLUSTER=",
                    ]
                    withCredentials([string(credentialsId: 'deploy_token', variable: 'API_TOKEN')]){
                        ciJobHandler = triggerRemoteJob(
                            job: "${JENKINS_URL}",
                            auth: TokenAuth(apiToken: hudson.util.Secret.fromString(API_TOKEN), userName: "username"),
                            blockBuildUntilComplete: true, // ожидание завершения Job
                            pollInterval: 100, // частота с которой опрашивается статус джобы
                            httpPostReadTimeout: 10000, //  Тайм-аут в миллисекундах для ожидания ответа сервера
                            parameters: params.join("\n"),
                        )
                    }
                }
            }
        }
    }
}