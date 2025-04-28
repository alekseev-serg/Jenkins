pipeline {
    agent any

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Install Dependencies') {
            steps {
                sh 'pod install'
            }
        }

        stage('Build') {
            steps {
                sh 'xcodebuild -workspace YourApp.xcworkspace -scheme YourApp -sdk iphonesimulator build'
            }
        }

        stage('Test') {
            steps {
                sh 'xcodebuild test -workspace YourApp.xcworkspace -scheme YourApp -sdk iphonesimulator'
            }
        }

        stage('Archive') {
            steps {
                sh 'xcodebuild archive -workspace YourApp.xcworkspace -scheme YourApp -sdk iphoneos -archivePath build/YourApp.xcarchive'
            }
        }

        stage('Export IPA') {
            steps {
                sh 'xcodebuild -exportArchive -archivePath build/YourApp.xcarchive -exportPath build -exportOptionsPlist ExportOptions.plist'
            }
        }

        stage('Deploy') {
            steps {
                sh 'fastlane beta'
            }
        }
    }
}