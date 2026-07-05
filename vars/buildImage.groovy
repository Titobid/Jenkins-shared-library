#!/user/bin/env groovy

def call() {
    echo "building docker image.. "
    withCredentials([usernamePassword(credentialsId: 'github-cred', passwordVariable: 'PASS', usernameVariable:'USER')])
    sh "docker build -t titobid/jenkins-app:1.0"
    sh "echo $PASS | docker login -u $USER --password-stdin"
    sh "docker push titobid/jenkins-app:1.0"
}