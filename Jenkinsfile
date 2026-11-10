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
                sh 'mvn --batch-mode clean package'
            }
        post {
            always {
                junit testResults: 'target/surefire-reports/*.xml'
            }
        }
        }
        stage('Build Docker image') {
            steps {
                sh 'docker build -f Dockerfile -t "blood-pressure:14" .'
    }
}
        stage('Push Docker image') {
            steps {
                withCredentials([usernamePassword(
            credentialsId: 'dockerhub-blood-pressure',
            usernameVariable: 'DOCKERHUB_USER',
            passwordVariable: 'DOCKERHUB_TOKEN'
        )]) {
            sh '''
                set +x
                export DOCKER_CONFIG="$(mktemp -d)"exit

                trap 'rm -rf "$DOCKER_CONFIG"' EXIT

                printf '%s' "$DOCKERHUB_TOKEN" |
                    docker login --username "$DOCKERHUB_USER" --password-stdin

                IMAGE="$DOCKERHUB_USER/blood-pressure:$BUILD_NUMBER"
                docker tag "blood-pressure:$BUILD_NUMBER" "$IMAGE"
                docker push "$IMAGE"
            '''
        }
    }
}
    } // Close stages
} // Close pipeline