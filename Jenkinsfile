pipeline {
    agent any
    environment {
        IMAGE_NAME = "geolocator-app"
        DOCKERHUB_USERNAME = "Nk-dir" // replace if different
    }
    stages {
        stage('Build Docker Image') {
            steps {
                sh 'docker build -t $DOCKERHUB_USERNAME/$IMAGE_NAME .'
            }
        }
        stage('Push to Docker Hub') {
            steps {
                withCredentials([string(credentialsId: 'dockerhub-password', variable: 'DOCKER_PASS')]) {
                    sh '''
                        echo "$DOCKER_PASS" | docker login -u $DOCKERHUB_USERNAME --password-stdin
                        docker push $DOCKERHUB_USERNAME/$IMAGE_NAME
                    '''
                }
            }
        }
        stage('Trigger Ansible') {
            steps {
                sh 'ansible-playbook -i localhost, -c local ansible/deploy.yml'
            }
        }
    }
}
