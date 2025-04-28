import hudson.model.*

node(label: 'builder'){

    checkout scm;

    stage('Example'){
        try{
            echo 'Hello World'
        }catch(e){
            currentBuild.result = 'FAILURE'
            echo "Build: ${currentBuild.result}"
        }
    }

    stage('def variables'){
        currentBuild.displayName = "#${BUILD_NUMBER} Test"
        currentBuild.description = "Build release"
    }

    stage('Set Unstable Status'){
        try {
            sh 'test'
        } catch (Exception e){
            echo "${e.message}"
            currentBuild.result = 'SUCCESS'
        }
    }

    stage('info'){
        echo "Result: ${currentBuild.rawBuild.getResult()}"
        echo "Cause: ${currentBuild.rawBuild.getCauses()}"

        def cause = currentBuild.rawBuild.getCauses()[0];
        echo "Triggered by: ${cause}"
    }

    stage('load new script'){
        def utils = load('loadedFile.groovy')
        utils.sayHello();
    }
}