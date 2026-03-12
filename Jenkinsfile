pipeline{
	agent any

	options {
		timestamps()
		disableConcurrentBuilds()
		buildDiscarder(logRotator(numToKeepStr: '20'))
	}

	parameters {
        string(
            name: 'IMAGE_TAG',
            description: 'Docker image tag to deploy (from GitHub Actions)'
        )
    }


	environment{
// 		DOCKERHUB_USER	=	credentials('dockerhub-username') #DOCKERHUB
// 		IMAGE_NAME		=	credentials('dockerhub-image-name') #DOCKERHUB
		GCP_PROJECT		=	credentials('gcp-project-id')
		GKE_CLUSTER		=	credentials('gke-cluster-name')
		GKE_ZONE		=	credentials('gke-zone')
		K8S_NAMESPACE	=	credentials('k8s-namespace')

		IMAGE_REPO    = "asia-south1-docker.pkg.dev/${GCP_PROJECT}/fixmate-repo/fixmate-backend"
        NEW_IMAGE     = "${IMAGE_REPO}:${IMAGE_TAG}"

// 		NEW_IMAGE		=	"${DOCKERHUB_USER}/${IMAGE_NAME}:${IMAGE_TAG}" #DOCKERHUB
	}

	stages{
		stage('Checkout'){
			steps{
				checkout scm
			}
		}

		stage('GCloud Auth & Connect to GKE'){
			steps{
				withCredentials([file(credentialsId: 'gcp-sa-key', variable: 'GCLOUD_KEY')]) {
					sh """
						echo Authenticating to Google Cloud...
                        gcloud auth activate-service-account --key-file=${GCLOUD_KEY}
                        gcloud config set project ${GCP_PROJECT}
                        gcloud container clusters get-credentials ${GKE_CLUSTER} --zone ${GKE_ZONE} --project ${GCP_PROJECT}
                    """

				}
			}
		}

		stage('Apply Kubernetes Manifests'){
			steps{
				sh """
					echo Applying Kubernetes Manifests...
					kubectl apply -f K8s/ -n ${K8S_NAMESPACE}
				"""
			}
		}

        stage('Restart Deployment') {
            steps {
                sh """
                    echo Restarting deployment...
                    kubectl rollout restart deployment/fixmate-backend -n ${K8S_NAMESPACE}
                """
            }
        }

		stage('Update Deployment Image'){
			steps{
				sh """
					echo Updating deployment image to ${NEW_IMAGE}
					kubectl set image deployment/fixmate-backend fixmate-backend=${NEW_IMAGE} -n ${K8S_NAMESPACE}
				"""
			}

		}

		stage('Verify Rollout') {
            steps {
                script {
                    try {
                        sh """
                            echo Verifying rollout...
                            kubectl rollout status deployment/fixmate-backend -n ${K8S_NAMESPACE} --timeout=500s
                            echo Current pods:
                            kubectl get pods -n ${K8S_NAMESPACE}
                        """
                    } catch (err) {
                        echo "Rollout failed — showing pod details before rollback..."
                        sh """
                            kubectl get pods -n ${K8S_NAMESPACE}
                            kubectl describe deployment fixmate-backend -n ${K8S_NAMESPACE}
                            kubectl rollout undo deployment/fixmate-backend -n ${K8S_NAMESPACE}
                        """
                        error("Rollback executed due to failed deployment.")
                    }
                }
            }
        }
	}

	post {
		success {
			emailext (
				subject: "FixMate Deployment SUCCESS",
				body: """\
					Deployment Completed Successfully!

					Environment: Production (GKE)
					Image: ${NEW_IMAGE}

					FixMate backend is now live.
					""",
				to: "janitha1717@gmail.com"
			)
		}

		failure {
			emailext (
				subject: "FixMate Deployment FAILED",
				body: """\
					Deployment Failed!

					Automatic rollback was executed.

					Check Jenkins console logs for detailed error.
					""",
				to: "janitha1717@gmail.com"
			)
		}
	}
}