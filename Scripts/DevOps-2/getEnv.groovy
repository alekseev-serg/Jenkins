node{
    stage('Get Env'){
        sh "env"
        echo "Building on node: ${env.NODE_NAME}"
    }

    stage('Get more env'){
        echo "Job Name: ${env.JOB_NAME}"
        echo "Build Number: ${env.BUILD_NUMBER}"
        echo "Workspace: ${env.WORKSPACE}"
    }

    stage('User Input'){
        def userInput = 'Serg'
        // def userInput = input(
        //     message: "Input name",
        //     parameters: [
        //         string(name: "USERNAME", defaultValue: 'devops', description: "Enter your name: ")
        //     ]
        // )
        echo "Hello ${userInput}"
    }

    stage('Fail Handling'){
        try{
            sh 'exit 1'
        }catch(Exception e){
            echo "Error: ${e.message}"
        }
    }
}