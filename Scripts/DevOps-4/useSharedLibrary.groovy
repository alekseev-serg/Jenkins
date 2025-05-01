@Library('test-library')

pipeline {
    agent any

    stages{
        stage('Test'){
            buildNpm();
        }
    }
}