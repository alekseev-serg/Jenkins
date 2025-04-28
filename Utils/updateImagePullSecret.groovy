pipeline {
    agent {
        label any
    }

    environment {
        OSE_URL = 'https://ose-some.domen.ru:6443'
        OSE_PROJECT = 'ose-project-dev'
        SECRET_NAME = 'test-secret'
    }

    stages {
        stage('Connect to OSE') {
            steps {
                script {
                    withCredentials([string(credentialsId: 'ose_auth_token', variable: 'OSE_TOKEN')]) {
                        sh 'oc login --token=${OSE_TOKEN} --server=${OSE_URL}'
                        sh 'oc project ${OSE_PROJECT}'
                    }
                }
            }
        }
        stage('Get current OSE Token') {
            steps {
                script {
                    def secretData = sh(
                        script: "oc get secret test-secret -o json | jq -r '.data[\".dockerconfigjson\"]'", returnStdout: true
                    ).trim()

                    def decodeToken = new String(secretData.decodeBase64())
                    sh "echo '${decodeToken}' > file.json"

                    def jsonData = readJSON file: "file.json"
                    def password = jsonData.auth['registry.nexus.ru'].password
                    env.CURRENT_PULL_PASSWORD = password
                }
            }
        }
        stage('Get credentials') {
            steps {
                script {
                    def configuration = [
                        vaultUrl: 'https://secret.vault.ru', vaultCredentialsId: 'vault_jen_approle',
                        engineVersion: 1, vaultNamespace: 'CE12342_CE349934'
                    ]
                    def vaultSecrets = [
                        [
                            path: "CE12342_CE349934/AD/domen.ru/creds/cred-sa-bdo58",
                            secretValues: [
                                [envVar: 'USERNAME', vaultKey: 'username'],
                                [envVar: 'PASSWORD', vaultKey: 'current_password']
                            ]
                        ]
                    ]
                    withVault([configuration: configuration, vaultSecrets: vaultSecrets]) {
                        def vars = sh(returnStdout: true, script: 'echo ${USERNAME} ${PASSWORD}').trim().split(" ")

                        def password = vars[1]

                        env.AD_PASSWORD = password
                    }
                }
            }
        }
        stage('Update pullsecret') {
            steps {
                script {
                    def multiLineString = """
                    imagePullSecretList:
                    - project:
                        registry: registry.nexus.ru
                        NameSecretImagePull: test-secret
                        AccountCredId: secman
                        OseProject: projectname
                        oseUrl: api.ose.ru:644
                    """

                    if (env.CURRENT_PULL_PASSWORD == env.AD_PASSWORD){
                        def runJob = build job: 'job_name',
                        parameters: [
                            string(name: 'list_params', value: multiLineString)
                        ]
                    } else {
                        echo "Pull secret are not equal"
                    }
                }
            }
        }
    }
}
