Started by user karthik nakka
Obtained Jenkinsfile from git https://github.com/Nk-dir/geolocator
org.codehaus.groovy.control.MultipleCompilationErrorsException: startup failed:
WorkflowScript: 20: unexpected token: ** @ line 20, column 70.
// This is the complete Jenkinsfile with a dedicated "Test" stage.
// This is a standard and professional pipeline structure.

pipeline {
    agent any

    environment {
        DOCKERHUB_USERNAME = "nkdir"
        IMAGE_NAME = "geolocator-app"
        IMAGE_TAG = "${env.BUILD_NUMBER}"
    }

    stages {

        // STAGE 1: Run unit tests first to validate the code quality.
        // If this stage fails, the pipeline stops immediately.
        stage('Test') {
            steps {
                echo "Running Maven unit tests inside a temporary container..."
                // This command starts a fresh Maven container for a clean test environment.
                sh 'docker run --rm -v "$(pwd)":/app -w /app maven:3.8-jdk-11 mvn test'
            }
        }

        // STAGE 2: If tests pass, build the final Docker image.
        stage('Build') {
            steps {
                echo "Building unique image: ${DOCKERHUB_USERNAME}/${IMAGE_NAME}:${IMAGE_TAG}"
                sh 'docker build --no-cache -t ${DOCKERHUB_USERNAME}/${IMAGE_NAME}:${IMAGE_TAG} .'
                sh 'docker tag ${DOCKERHUB_USERNAME}/${IMAGE_NAME}:${IMAGE_TAG} ${DOCKERHUB_USERNAME}/${IMAGE_NAME}:latest'
            }
        }

        // STAGE 3: If the build succeeds, push the image to the registry.
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

        // STAGE 4: If the push succeeds, deploy the new version.
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
