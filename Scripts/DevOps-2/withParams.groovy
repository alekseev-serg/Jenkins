pipeline {
    agent any

    parameters{
        string(name: 'USERNAME', defaultValue: 'devops', description: 'Введите имя пользователя:')
    }

    stages{
        stage('Print Params'){
            steps{
                echo "Hello ${env.USERNAME}"
            }
        }
    }
}