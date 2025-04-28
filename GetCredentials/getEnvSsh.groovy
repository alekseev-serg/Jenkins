pipeline {
    agent {
        label any
    }

    stages {
        stage('stage_name') {
            steps {
                script {
                    def kek = ""
                    withCredentials([sshUserPrivateKey(keyFileVariable:"key", credentialsId:"ssh-key")]) {
                        kek = sh (returnStdout: true, script: 'cat ${key}')
                    }
                    println kek
                }
            }
        }
    }
}