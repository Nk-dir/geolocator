// This is the complete Jenkinsfile with the --no-cache flag added
// to the Docker build command to ensure all files are fresh.

pipeline {
    agent any

    environment {
        DOCKERHUB_USERNAME = "nkdir"
        IMAGE_NAME = "geolocator-app"
    }

    stages {
        
        // STAGE 1: Build the Docker image, forcing a rebuild of all layers.
        stage('Build Docker Image') {
            steps {
                echo "Building Docker image with --no-cache to ensure freshness: ${DOCKERHUB_USERNAME}/${IMAGE_NAME}"
                // The --no-cache flag is critical for solving stale file issues.
                sh 'docker build --no-cache -t ${DOCKERHUB_USERNAME}/${IMAGE_NAME} .'
            }
        }

        // STAGE 2: Log in to Docker Hub and push the fresh image.
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

        // STAGE 3: Use Ansible to deploy the new container.
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
