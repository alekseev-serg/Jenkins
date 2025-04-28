node {

    checkout scm

    stage("Read From file"){
        sh "pwd"
        sh "ls -la"
        def config = readJSON(file: 'Scripts/DevOps-3/config.json')
        echo "Version = ${config.version}"
        echo "Environment = ${config.env}"
    }

    def stages = ['service-a', 'service-b', 'service-c']

    stages.each { stageName ->
        stage(stageName){
            buildService(stageName)
        }
    }
}

def buildService(String service) {
    if(service == 'service-a'){
        echo "Running stage: ${service}"
        echo "Build Python app"
    }
    if(service == 'service-b'){
        echo "Running stage: ${service}"
        echo "Build React app"
    }
    if(service == 'service-b'){
        echo "Running stage: ${service}"
        echo "Build Java app"
    }
}