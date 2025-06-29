/ ----- FINAL, SECURE Jenkinsfile -----
// This version uses usernamePassword() to match your credential type in Jenkins.

pipeline {
    // This pipeline will run on any available Jenkins agent.
    agent any

    // These are global variables available to all stages.
    environment {
        // Your Docker Hub username. The image will be pushed to this user's repository.
        DOCKERHUB_USERNAME = "nkdir"
        // The name of the Docker image.
        IMAGE_NAME = "geolocator-app"
    }

    // The pipeline is divided into sequential stages.
    stages {
        
        // STAGE 1: Build the Docker image from the Dockerfile.
        stage('Build Docker Image') {
            steps {
                echo "Building Docker image: ${DOCKERHUB_USERNAME}/${IMAGE_NAME}"
                // The 'sh' step executes a shell command.
                sh 'docker build -t ${DOCKERHUB_USERNAME}/${IMAGE_NAME} .'
            }
        }

        // STAGE 2: Log in to Docker Hub and push the image.
        stage('Push to Docker Hub') {
            steps {
                // This is the secure way to handle credentials in Jenkins.
                // It fetches the credential with the ID 'dockerhub-password'.
                // The credential must be of type "Username with password".
                withCredentials([usernamePassword(credentialsId: 'dockerhub-password', usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
                    
                    // The DOCKER_USER and DOCKER_PASS variables are now available inside this block.
                    echo "Logging into Docker Hub as user: ${DOCKER_USER}"
                    
                    // The 'sh' step with triple quotes allows for multi-line shell scripts.
                    sh '''
                        # Pipe the password to the docker login command. This is more secure than using the -p flag.
                        echo "$DOCKER_PASS" | docker login -u "$DOCKER_USER" --password-stdin
                    '''
                    
                    // Push the image to the repository on Docker Hub.
                    sh 'docker push ${DOCKERHUB_USERNAME}/${IMAGE_NAME}'
                }
            }
        }

        // STAGE 3: Use Ansible to deploy the new container.
        stage('Deploy with Ansible') {
            steps {
                echo "Running Ansible playbook to deploy the new container..."
                // This command executes the Ansible playbook locally.
                sh 'ansible-playbook -i localhost, -c local ansible/deploy.yml'
            }
        }
    }
    
    // The 'post' section defines actions that run after the main stages are complete.
    post {
        // 'always' means this block will run whether the pipeline succeeded or failed.
        always {
            echo "Pipeline finished. Logging out from Docker Hub."
            // It's a good security practice to log out.
            sh 'docker logout'
        }
    }
}
