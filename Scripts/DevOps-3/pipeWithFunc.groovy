def buildApp() {
    stage('Build'){
        echo "Сборка проекта"
    }
}

def runTests(){
    stage('Tests'){
        echo "Run Test"
    }
}

def greetUser(String name){
    echo "Hello, ${name}"
}

def deploy(){
    echo "This is Deploy func"
}

def notify(status){
    if(status == 'SUCCESS'){echo "Status: ${status}"}
    if(status == 'FAILURE'){echo "Status: ${status}"}
    if(status == 'ABORTED'){echo "Status: ${status}"}
}

node{
    checkout scm
    buildApp();
    runTests();
    stage('Hello User'){
        greetUser("Serg!");
    }

    stage('Deploy'){
        deploy();
    }

    stage('Message'){
        notify('SUCCESS');
    }
}