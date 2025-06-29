pipeline {
    agent any

    environment {
        DOCKERHUB_USERNAME = "nkdir"
        IMAGE_NAME = "geolocator-app"
        IMAGE_TAG = "${env.BUILD_NUMBER}"
    }

    stages {

        stage('Test') {
            steps {
                echo "Running Maven unit tests..."
                sh 'docker run --rm -v "$(pwd)":/app -w /app maven:3.8-jdk-11 mvn test'
            }
        }

        stage('Build') {
            steps {
                echo "Building unique image: ${DOCKERHUB_USERNAME}/${IMAGE_NAME}:${IMAGE_TAG}"
                sh 'docker build --no-cache -t ${DOCKERHUB_USERNAME}/${IMAGE_NAME}:${IMAGE_TAG} .'
                sh 'docker tag ${DOCKERHUB_USERNAME}/${IMAGE_NAME}:${IMAGE_TAG} ${DOCKERHUB_USERNAME}/${IMAGE_NAME}:latest'
            }
        }

        stage('Push') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'dockerhub-password', usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
                    echo "Logging into Docker Hub as user: ${DOCKER_USER}"
                    sh 'echo "$DOCKER_PASS" | docker login -u "$DOCKER_USER" --password-stdin'
                    sh 'docker push ${DOCKERHUB_USERNAME}/${IMAGE_NAME}:${IMAGE_TAG}'
                    sh 'docker push ${DOCKERHUB_USERNAME}/${IMAGE_NAME}:latest'
                }
            }
        }

        stage('Deploy') {
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
