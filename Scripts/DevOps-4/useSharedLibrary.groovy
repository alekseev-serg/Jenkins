@Library('test-library') _

pipeline {
    agent any

    stages{
        stage('Test'){
            steps{
                buildNpm();
            }
        }
    }
}