pipeline {
    agent any
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
                sh 'docker build -t employee-svc:1.0 .'
            }
        }
    }
}