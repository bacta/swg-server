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

        stage ('Build') {
            steps {
                script {
                    try {
                        notifySlack()
                        sh "mvn clean test install verify"

                    } catch (e) {
                        currentBuild.result = 'FAILURE'
                        throw e
                    } finally {
                        notifySlack(currentBuild.result)
                    }
                }
            }
        }
    }
}

def notifySlack(String buildStatus = 'STARTED') {
    // Build status of null means success.
    buildStatus = buildStatus ?: 'SUCCESS'

    def color

    if (buildStatus == 'STARTED') {
        color = '#D4DADF'
    } else if (buildStatus == 'SUCCESS') {
        color = '#BDFFC3'
    } else if (buildStatus == 'UNSTABLE') {
        color = '#FFFE89'
    } else {
        color = '#FF9FA1'
    }

    def msg = "${buildStatus}: `${env.JOB_NAME}` #${env.BUILD_NUMBER}:\n${env.BUILD_URL}"

    slackSend(color: color, message: msg)
}