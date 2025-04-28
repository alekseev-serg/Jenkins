def cfg [
    workDir: 'testDir'
    repository: [
        credentialsId: 'git-ssh',
        sshUrl: 'ssh://git@sst-some-url.ru/repo/repo.git',
        branch: 'master'
    ]
];

pipeline {
    agent {
        label 'clearAgent'
    }

    stages {
        stage('Lead list repository'){
            steps {
                dir(cfg.workDir) {
                    checkout([
                        $class: 'GitSCM',
                        branches: [[name: cfg.repository.branch]],
                        userRemoteConfigs: [[
                            url: cfg.repository.sshUrl,
                            credentialsId: cfg.repository.credentialsId
                        ]],
                        extensions: [[$class: 'SubmoduleOption',
                            disableSubmodules: false,
                            parentCredentials: true,
                            recursiveSubmodules: true,
                            reference: '',
                            trackingSubmodules: false]],
                    ])
                }
            }
        }
        stage('GET_ENV'){
            steps {
                script {
                    env.APP_NAME = env.APP_NAME.toLowerCase()
                    env.APP_REPO = env.APP_REPO.replace('[','').replace(']','')
                }
            }
        }
        stage('Search in repo-list') {
            steps {
                script {
                    dir(cfg.workDir) {
                        def projects = readJSON file: "./repo-list.json"
                        println env.APP_REPO
                        projects.each {key, value ->
                            if (value.sshUrl == env.APP_REPO.replace('"','')){
                                env.APP_NAME = "${key}".toLowerCase()
                                env.APP_REPO = "${value.sshUrl}"
                            }
                        }
                    }
                }
            }
        }
        stage('run neighborhood job') {
            steps {
                script {
                    def runJob = build job: 'job_name', parameters: [
                        string(name: "APP_NAME", value: "${env.APP_NAME}"),
                        string(name: "APP_REPO", value: "${env.APP_REPO}"),
                        string(name: "APP_BRANCH", value: "${env.APP_BRANCH}"),
                    ]
                    def buildStatus = waitForStatus('job_name', runJob.id)
                    if (buildStatus=='SUCCESS') {
                        echo 'CI JOB SUCCESSFULLY'
                    } else {
                        echo 'CI JOB FAILED'
                    }
                }
            }
        }
    }
}