def pacticipant = 'componentA'
def pacticipantVersion
def integrationEnvTag = 'develop'

pipeline {
    agent any

    stages {
        stage('Setup') {
            steps {
                echo 'downloading pact standalone tools'
                sh "curl -LO https://github.com/pact-foundation/pact-ruby-standalone/releases/download/v1.88.26/pact-1.88.26-linux-x86_64.tar.gz"
                sh "tar xzf pact-1.88.26-linux-x86_64.tar.gz"

                script {
                    // TODO: retrieve component version dynamically reading VERSION file (or by other means)
                    def commit = sh(
                            script: "printf \$(git rev-parse --short HEAD)",
                            returnStdout: true
                    )
                    pacticipantVersion = '0.0.1.' + commit
                }
            }
        }

        stage('Run tests. Generate contracts') {
            steps {
                sh "./gradlew clean test"
            }
        }

        stage('Publish contracts') {
            steps {
                sh "./gradlew pactPublish -Dtags=${env.BRANCH_NAME} -DconsumerVersion=${pacticipantVersion} -DpactBrokerUrl=http://pact-broker:9292"
            }
        }

        stage('Can I deploy?') {
            steps {
                sh "./pact/bin/pact-broker can-i-deploy --broker-base-url http://pact-broker:9292 --broker-username pact --broker-password password --pacticipant ${pacticipant} --version ${pacticipantVersion} --to ${integrationEnvTag} --retry-interval 30 --retry-while-unknown 10"
            }
        }
    }
}
