pipeline {
    agent any

    stages {
        stage('stage_name') {
            steps {
                script {
                    def configuration = [
                        vaultUrl: 'https://vault.site.ru', vaultCredentialId: 'jenkins_cred_id_approle', engineVersion: 1,
                        vaultNamespace: 'CE123534_CE12135'
                    ]
                    def vaultSecrets = [
                        [
                            path: "CE123534_CE12135/AD/creds.test.ru/creds/account",
                            secretValues: [
                                [envVar: 'USERNAME', vaultKey: 'username'],
                                [envVar: 'PASSWORD', vaultKey: 'current_password'],
                            ]
                        ]
                    ]
                    withVault([configuration: configuration, vaultSecrets: vaultSecrets]) {
                        get = sh(returnStdout: true, script: 'echo ${USERNAME} ${PASSWORD}')
                    }
                    println get
                }
            }
        }
    }
}
