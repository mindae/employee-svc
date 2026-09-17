pipeline {
    agent any
    environment {
        PATH="PATH = "/usr/local/bin:/opt/homebrew/bin:/usr/bin:/bin:${env.PATH}"
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
                sh 'docker build -t employee-svc:1.0 .'
            }
        }
    }
}