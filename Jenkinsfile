pipeline {
    agent any
    environment {
        PATH="/usr/local/bin:/opt/homebrew/bin:/usr/bin:/bin:${env.PATH}"
        IMAGE_NAME="mindae/employee-svc"
    }
    stages {
        stage('Checkout') {
            steps {
                checkout scm
             }
        }
        stage('Build and Test') {
            steps {
                sh './mvnw clean test'
            }
        }
        stage('Package') {
            steps {
                sh './mvnw package -DskipTests'
            }
        }
        stage ('Docker Build') {
            steps {
                sh 'docker --version'
                sh 'docker build -t ${IMAGE_NAME}:${BUILD_NUMBER} .'
            }
        }
        stage('Docker login'){
            steps {
                withCredentials([usernamePassword(credentialsId: 'dockerhub-credentials',
                usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_TOKEN')]){
                    'sh echo "$DOCKER_TOKEN" | docker login -u "$DOCKER_USER" --password-stdin'
                }
            }
        }
        stage('Docker push'){
            steps {
                sh 'docker push ${IMAGE_NAME}:${BUILD_NUMBER}'
            }
        }
    }
}