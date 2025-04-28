pipeline {
    agent{
        label any
    }
    parameters{      
        choice( name: "EVENT",
                choices: [
                    'EVENT_START',
                    'EVENT_STOP'
                ]
        )
        string( name: "RAM_VALUE",
                description: "Сколько процентов необходмо занять",
                defaultValue: "60",
                trim: true
        )
    }
    tools{
        ansible 'ansible-2.11'
    }
    stages{
        stage('Checkout SCM'){
            steps{
                checkout scm
            }
        }

        stage('Определяем переменные запуска/остановки'){
            steps {
                echo "Обрабатываем событие: ${env.EVENT} скрипт"
                script {
                    currentBuild.displayName = '#' + env.BUILD_NUMBER + ' ' + env.EVENT
                }
            }
        }

        stage('Устанавливаем значение утилизации'){
            when{
                expression {env.EVENT == "EVENT_START"}
            }
            steps {
                sh "sed -i 's/maintain_memory_load(target_usage=60)/maintain_memory_load(target_usage=${env.RAM_VALUE})/' utils/RAM.py"
                sh "tail -n 3 utils/RAM.py"
            }
        }
        
        stage('Подготовка Inventory'){
            steps{
                script{
                    def configuration = [
                        vaultUrl: 'https://vault.host.ru', 
                        vaultCredentialId: 'approle_for_vault', 
                        engineVersion: 1, 
                        vaultNamespace: 'NAMESPACE'
                    ]
                    def vaultSecrets = [
                        path: "NAMESPACE/A/123213/123123/JEN/HOST",
                        secretValues: [
                            [envVar: 'USERNAME', vaultKey: 'nginx_username'],
                            [envVar: 'PASSWORD', vaultKey: 'nginx_password']
                        ]
                    ]
                    withVault([configuration: configuration, vaultSecrets: vaultSecrets]) {
                        get = sh(returnStdout: true, script: 'echo ${USERNAME}').trim()
                    }
                    def parts = get.split(" ")
                    def nginxUser = parts[0]
                    def nginxPass = parts[1]

                    sh """
                        sed -E -i 's/ansible_user="username"/ansible_user="${nginxUser}"/; s/ansible_ssh_pass="password"/ansible_ssh_pass="${nginxPass}"/' ansible/inventory
                    """
                }
            }
        }

        stage('Подготовка ключей и деплой'){
            steps {
                script {
                    withCredentials([[$class: 'VaultSignedSSHKeyCredentialsBinding',
                    credentialsId: 'signed_ssh_key',
                    vaultAddr: 'https://vault.host.ru',
                    signedPublicKey: 'SIGNED_SSH_PUBLIC_KEY',
                    publicKeyVar: 'SSH_PUBLIC_KEY',
                    privateKeyVar: 'SSH_PRIVATE_KEY',
                    passphraseVar: 'SSH_PRIVATE_KEY_PASS']]) {

                        sshagent([]){
                            sh '''
                            set +x
                            echo -e "#!/bin/sh\necho \\SSH_PRIVATE_KEY_PASS" > send_ps.sh
                            chmod +x ./send_ps.sh
                            DISPLAY=1 SSH_ASKPASS="./send_ps.sh" ssh-add $SSH_PRIVATE_KEY < /dev/null
                            '''
                            try {
                                if(env.EVENT == "EVENT_START"){sh "ansible-playbook ansuble/run.yaml -i ansible/inventory"}
                                if(env.EVENT == "EVENT_STOP"){sh "ansible-playbook ansuble/stop.yaml -i ansible/inventory"}
                            }
                            catch(error){
                                currentBuild.result = 'FAILURE'
                                println(error)
                            }
                        }
                    }
                }
            }
        }
    }

    post{
        always {
            script {
                cleanWs disableDeferredWipeout: true, deleteDirs: true
            }
        }
    }
}