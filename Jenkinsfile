pipeline {
    agent any

    environment {
        DOCKERHUB_USERNAME = "nkdir"
        IMAGE_NAME = "geolocator-app"
    }

    stages {
        stage('Build Docker Image') {
            steps {
                echo "Building Docker image: ${DOCKERHUB_USERNAME}/${IMAGE_NAME}"
                sh 'docker build -t ${DOCKERHUB_USERNAME}/${IMAGE_NAME} .'
            }
        }

        stage('Push to Docker Hub') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'dockerhub-password', usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
                    echo "Logging into Docker Hub as user: ${DOCKER_USER}"
                    sh '''
                        echo "$DOCKER_PASS" | docker login -u "$DOCKER_USER" --password-stdin
                    '''
                    sh 'docker push ${DOCKERHUB_USERNAME}/${IMAGE_NAME}'
                }
            }
        }

        stage('Deploy with Ansible') {
            steps {
                echo "Running Ansible playbook to deploy the new container..."
                sh 'ansible-playbook -i localhost, -c local ansible/deploy.yml'
            }
        }
    }
    
    post {
        always {
            echo "Pipeline finished. Logging out from Docker Hub."
            sh 'docker logout'
        }
    }
}
