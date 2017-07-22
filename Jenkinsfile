pipeline {
    agent any
    tools {
        maven 'Maven 3.5.0'
        jdk 'JDK8 131'
    }
    stages {
        stage ('Initialize') {
            steps {
                sh '''
                    echo "PATH = ${PATH}"
                    echo "M2_HOME = ${M2_HOME}"
                '''
            }
        }

        stage('Clone sources') {
            steps {
                git url: 'https://github.com/bacta/swg-server.git'
            }
        }

        stage ('Build') {
            steps {
                if (isUnix()) {
                    sh "mvn -Dmaven.test.failure.ignore=true install"
                } else {
                    bat(/"mvn" -Dmaven.test.failure.ignore clean install/)
                }
            }
            post {
                success {
                    junit 'target/surefire-reports/**/*.xml'
                }
            }
        }
    }
}