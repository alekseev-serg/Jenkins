pipeline {
    agent any

    stages {
        stage('stage_name') {
            steps {
                script {
                    def kek = "get it"
                    withCredentials([string(credentialsId:"credentialsId", variable:"OSEToken")]) {
                        kek = sh (returnStdout: true, script: 'echo ${OSEToken}')
                    }
                    println kek
                }
            }
        }
    }
}
