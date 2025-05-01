@Library('test-library') _

pipeline {
    agent any

    stages{
        stage('Test'){
            steps{
                script(
                    def notifier = notify();
                    notifier.info();
                )
                buildNpm();

            }
        }
    }
}