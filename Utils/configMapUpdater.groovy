def reRead_job_params = env.reRead_job_params ? env.reRead_job_params : 'true'
def SLAVE_LABLE = 'workerName'
def EX_ENVID = "${env.envId}"

ansiColor('xterm'){
    timestamps {
        node(SLAVE_LABLE){
            if(reRead_job_params == 'true'){
                stage('Read Config')
                reInitProperties(EX_ENVID)
                echo 'Params is over'
            }else{

                oc_tool = tool(name: 'oc-3.11', type: 'oc')

                stage("Download_scripts"){
                    print_blue('Clone repository DevOps')
                    try {
                        checkout scm
                        sh "cp -r ./inventory/${env.envId}"
                    } catch (e) {
                        println e 
                        err = e 
                    }
                }

                stage('Config'){
                    print_blue("Run configmap update")
                    sh ("cat ${env.WORKSPACE}/inventories/${env.envId}/${env.name_config}.yml")

                    withCredentials([string(credentialsId: "${oc_cred}", variable: 'oc_token')]) {
                            sh "${oc_tool}/oc login --token=${oc_token} --server=${oc_server_url}"
                    }
                    sh "${oc_tool}/oc project ${oc_namespace}"
                    sh "oc apply -f ${env.WORKSPACE}/inventories/${env.envId}/${env.name_config}.yml"

                    MAIL(add_emails)
                }
            }
        }
    }
}

def print_blue(message) {
    echo "\033[1m \033[34m"
    echo "${message}"
    echo "\033[0m"
}

def MAIL(add_emails){
    date_start = new Date(currentBuild.startTimeInMillis).format("HH:mm dd.MM.yyyy")
    date_end = "${currentBuild.durationString}" - "and counting"
    username = ''
    usermail = ''
    wrap([$class: 'BuildUser']) {
        usermail = env.BUILD_USER_EMAIL
        username = env.BUILD_USER
    }
    body = getBody(date_start, date_end, username, usermail)
    subject = "ConfigMap deploy" + currentBuild.currentResult
    emailext body: "${body}", 
    mimeType: 'text/html', 
    attachLog: true , 
    subject: "${subject}", 
    to: "${env.add_emails}", 
    recipientProviders: [requestor()]
}

def getBody(date_start,date_end,user,user_mail)
{
    body = """
        <style>
            table {
                border: 1px solid silver;
                border-collapse: collapse;
                font-family: Arial, Helvetica, sans-serif;
                font-size: 14px;
            }
            TH {
                border: 1px solid silver;
                padding: 5px;
                text-align: center;
                width: 150px;
            }
            TD {
                border: 1px solid silver;
                padding: 5px;
                text-align: center;
            }
            .success {
                background-color: lightgreen;
            }
            .failure {
                background-color: lightpink;
            }
            .unstable {
                background-color: lightyellow;
            }
            A {
                color: #336699;
                text-decoration: none;
            }
            A:hover {
                color: darkred;
            }
            </style>
            <table>
            <TR><TH>Джоба</TH><TD>${JOB_NAME}</TD></TR>
            <TR><TH>Сборка</TH><TD><A href='${BUILD_URL}'>${BUILD_URL}</A></TD></TR>
            <TR><TH>Дата</TH><TD>${date_start}</TD></TR>
            <TR><TH>Запустил</TH><TD>${user} (${user_mail})</TD></TR>
            <TR><TH>Описание</TH><TD>${currentBuild.description}</TD></TR>
            <TR><TH>Время</TH><TD>${date_end}</TD></TR>
            <TR><TH>Результат</TH><TD class='${currentBuild.currentResult.toLowerCase()}'>${currentBuild.currentResult}</TD></TR>
            </table>
    """
    return body
}

// Метод перечитывания параметров
def reInitProperties(EX_ENVID) {
    properties(
        [parameters
            ([
                booleanParam(
                    defaultValue: false,
                    description: 'Выставить флаг, если были изменения параметров в pipeline',
                    name: 'reRead_job_parameters'
                ),
                choice(
                    choices: "DEV\nIFT\nNT\nPSI\nPROM",
                    description: "Выбор стенда",
                    name: 'envId'
                    
                ),
                choice(
                    choices: "https://api.devhost.ru:6443\nhttps://api.devhost.ru:6443",
                    description: '',
                    name:'oc_server_url'
                ),
                string(
                    defaultValue: "",
                    description: 'Наименование проекта openshift',
                    name:'oc_namespace',
                    trim: true
                ),
                string(
                    defaultValue: "",
                    description: 'Наименование ConfigMap',
                    name:'name_config',
                    trim: true
                ),
                credentials(
                    credentialType: 'org.jenkinsci.plugins.plaincredentials.impl.StringCredentialsImpl', 
                    defaultValue: "${env.oc_cred}", 
                    description: '', 
                    name: 'oc_cred', 
                    required: false
                ),
                string(
                    defaultValue: "mail@mail.ru",
                    description: 'Список получателей отчета о работе Job, разделенные пробелом',
                    name: 'add_emails'
                )                            
                          
            ]),
            pipelineTriggers([])
        ]
    )
}