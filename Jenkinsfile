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

        // STAGE 1: Run unit tests first to validate the code qualityOf course. You are absolutely right to want the full, complete file to ensure there.
        // If this stage fails, the pipeline stops immediately.
        stage('Test') {
            steps are no mistakes.

Here is the complete and corrected Jenkinsfile that includes the new **"Run Unit Tests" {
                echo "Running Maven unit tests inside a temporary container..."
                // This command starts a fresh Maven container for** stage. This is the version you should use for this step.

---

### **Complete Jenkinsfile a clean test environment.
                // It mounts the current project directory into the container's /app directory.
 with Test Stage**

```groovy
// This is the complete Jenkinsfile with the new 'Run Unit Tests' stage                // Then it runs 'mvn test'. If any test fails, this command will exit with an error,
                // which added.
// This stage acts as a quality gate before pushing and deploying.

pipeline {
    agent any

    environment automatically fails this stage and stops the pipeline.
                sh 'docker run --rm -v "$(pwd)":/app - {
        DOCKERHUB_USERNAME = "nkdir"
        IMAGE_NAME = "geolocw /app maven:3.8-jdk-11 mvn test'
            }
        }

        // STAGEator-app"
        IMAGE_TAG = "${env.BUILD_NUMBER}" // A unique tag for every build 2: If tests pass, build the final Docker image.
        stage('Build') {
            steps {
                echo
    }

    stages {
        
        // STAGE 1: Build the Docker image.
        // Note "Building unique image: ${DOCKERHUB_USERNAME}/${IMAGE_NAME}:${IMAGE_TAG}"
                sh 'docker build --no-cache -t ${DOCKERHUB_USERNAME}/${IMAGE_NAME}:${IMAGE_TAG} .'
: The Docker build itself runs 'mvn clean install', so the code is compiled here.
        stage('Build Docker Image') {
            steps {
                echo "Building unique image: ${DOCKERHUB_USERNAME}/${IMAGE                sh 'docker tag ${DOCKERHUB_USERNAME}/${IMAGE_NAME}:${IMAGE_TAG} ${DOCKERHUB_USERNAME}/${IMAGE_NAME}:latest'
            }
        }

        // STAGE 3: If_NAME}:${IMAGE_TAG}"
                sh 'docker build --no-cache -t ${DOCKERHUB_USERNAME}/${IMAGE_NAME}:${IMAGE_TAG} .'
                sh 'docker tag ${DOCKERHUB_ the build succeeds, push the image to the registry.
        stage('Push') {
            steps {
                withUSERNAME}/${IMAGE_NAME}:${IMAGE_TAG} ${DOCKERHUB_USERNAME}/${IMAGE_NAME}:latest'Credentials([usernamePassword(credentialsId: 'dockerhub-password', usernameVariable: 'DOCKER_USER',
            }
        }

        // STAGE 2: Run unit tests as a quality check.
        // passwordVariable: 'DOCKER_PASS')]) {
                    echo "Logging into Docker Hub as user: ${DOCKER_USER}"
                    sh 'echo "$DOCKER_PASS" | docker login -u "$DOCK This stage will fail the pipeline if any test does not pass.
        stage('Run Unit Tests') {
            steps {ER_USER" --password-stdin'
                    sh 'docker push ${DOCKERHUB_USERNAME}/${IMAGE
                echo "Running Maven unit tests inside a temporary container..."
                // This command starts a Maven container, mounts_NAME}:${IMAGE_TAG}'
                    sh 'docker push ${DOCKERHUB_USERNAME}/${IMAGE_NAME the current project
                // directory into it, and then runs the 'mvn test' command.
                // --rm}:latest'
                }
            }
        }

        // STAGE 4: If the push succeeds, deploy the new version.
        stage('Deploy') {
            steps {
                echo "Running Ansible playbook to deploy the new container automatically cleans up the container after it exits.
                sh 'docker run --rm -v $(pwd):/app..."
                sh 'ansible-playbook -i localhost, -c local ansible/deploy.yml'
             -w /app maven:3.8-jdk-11 mvn test'
            }
        }

        // STAGE 3: Log in to Docker Hub and push the image.
        // This stage will only}
        }
    }
    
    post {
        always {
            echo "Pipeline finished. Logging out from Docker Hub."
            sh 'docker logout'
        }
    }
}
