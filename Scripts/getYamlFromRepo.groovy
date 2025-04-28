stage("clone app repo") { //скачиваем проект, в котором лежит файл YAML
    print_blue("Clone application repository")
    print_blue("")
    git branch: "${env.branch}", credentialsId: "${cred}", url: "${repository_url}" //надо подставить криденшелз под репозиторий
    if (hashCommit != "") {
        sh "git checkout ${hashCommit}"
    }
    def matches = findFiles(glob: 'devsecops_config/*.yml')
}