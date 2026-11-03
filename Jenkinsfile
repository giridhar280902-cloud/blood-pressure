pipeline {
    agent any
    tools {
        maven 'maven3'
    }

    stages {
        stage('Welcome') {
            steps {
                echo 'Starting the blood-pressure-ci pipeline'
            }
        } // Close Welcome before starting the next stage
        stage('checkout code') {
            steps{
                git branch: 'main',
                    url:'https://github.com/giridhar280902-cloud/blood-pressure.git'
            }
        }

        stage('Check Environment') {
            steps {
                sh '''
                    java --version
                    git --version
                '''
            }
        }

        stage('Inspection') {
            steps {
                sh '''
                    pwd
                    ls -ls
                '''
            }
        }
        stage('mvn test'){
            steps {
                sh 'mvn --version'
                sh 'mvn --batch-mode clean test'
            }
        post {
            always {
                junit testResults: 'target/surefire-reports/*.xml'
            }
        }
        }
    } // Close stages
} // Close pipeline