pipeline {
    agent any

    environment {
        APP_NAME        = "chat-service"
        NAMESPACE       = "next-me"
        REGISTRY        = "ghcr.io"
        GH_OWNER        = "sparta-next-me"
        IMAGE_REPO      = "chat-service"
        FULL_IMAGE      = "${REGISTRY}/${GH_OWNER}/${IMAGE_REPO}:latest"
        TZ              = "Asia/Seoul"
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build & Test') {
            steps {
                withCredentials([file(credentialsId: 'chat-service', variable: 'ENV_FILE')]) {
                    sh '''
                      set -a
                      . "$ENV_FILE"
                      set +a
                      chmod +x ./gradlew
                      # 의존성 캐시 문제를 방지하기 위해 refresh 옵션 추가
                      ./gradlew clean bootJar --no-daemon --refresh-dependencies
                    '''
                }
            }
        }

        stage('Docker Build & Push') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'ghcr-credential', usernameVariable: 'USER', passwordVariable: 'TOKEN')]) {
                    sh """
                      docker build -t ${FULL_IMAGE} .
                      echo "${TOKEN}" | docker login ${REGISTRY} -u "${USER}" --password-stdin
                      docker push ${FULL_IMAGE}
                    """
                }
            }
        }

        stage('Deploy to Kubernetes') {
            steps {
                withCredentials([
                    file(credentialsId: 'k3s-kubeconfig', variable: 'KUBECONFIG_FILE'),
                    file(credentialsId: 'chat-service', variable: 'ENV_FILE')
                ]) {
                    sh '''
                      export KUBECONFIG=${KUBECONFIG_FILE}

                      echo "Updating K8s Secret: chat-service..."
                      kubectl delete secret chat-service -n ${NAMESPACE} --ignore-not-found
                      kubectl create secret generic chat-service --from-env-file=${ENV_FILE} -n ${NAMESPACE}

                      echo "Applying manifests from chat-service.yaml..."
                      kubectl apply -f chat-service.yaml -n ${NAMESPACE}

                      echo "Monitoring rollout status..."
                      kubectl rollout status deployment/chat-service -n ${NAMESPACE}
                    '''
                }
            }
        }
    }

    post {
        always {
            echo "Cleaning up resources..."
            sh "docker rmi ${FULL_IMAGE} || true"
            sh "docker system prune -f"
        }
        success {
            echo "Successfully deployed ${APP_NAME}!"
        }
        failure {
            echo "Deployment failed. Check the logs."
        }
    }
}