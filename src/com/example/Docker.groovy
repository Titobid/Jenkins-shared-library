#! /user/bin/env groovy
package com.example

class Docker implements Serializable {
    def script
    Docker(script){
        this.script=script
    }
    def buildDockerImage(String imageName){
        script.echo "building docker image.. "
        script.sh "docker build -t $imageName ."
        }
    def dockerLogin(){
        script.withCredentials([script.usernamePassword(credentialsId: 'docker-hub', passwordVariable: 'DOCKER_PASS', usernameVariable:'DOCKER_USER')]){
            script.sh "echo '${script.DOCKER_PASS}' | docker login -u '${script.DOCKER_USER}' --password-stdin"
        }
    }
    def dockerPush(String imageName){
        script.sh "docker push $imageName"
    }
    def gitCommit() {
        script.withCredentials([script.gitUsernamePassword(credentialsId: 'github-cred', gitToolName: 'Default')]) {
            script.sh 'git remote set-url origin https://github.com/Titobid/jenkins-project-08.git'
            script.sh 'git config --global user.email "jenkins@example.com" '
            script.sh 'git config --global user.name "jenkins" '
            script.sh 'git add app/package.json'
            script.sh '''if [ -f app/package-lock.json ]; then
                          git add app/package-lock.json
                          fi'''
            script.sh 'git commit -m "ci: bump version to $APP_VERSION"'
            script.sh 'git push origin HEAD:main'
            script.sh 'git status'
            script.sh 'git branch'
            script.sh 'git config --list'
        }
    }
    def runTests(){
        script.echo 'Installing dependencies...'
        script.sh 'npm install'
        script.echo 'Running tests...'
        script.sh 'npm test'
    }
    def incrementVersion(){
        script.echo 'incrementing app version ....'
        script.sh 'npm version minor --no-git-tag-version'
        script.env.APP_VERSION = script.sh(
                script: "node -p \"require('./package.json').version\"",
                returnStdout: true
        ).trim()
        script.env.IMAGE_TAG = "${script.env.APP_VERSION}-${script.env.BUILD_NUMBER}"
        script.echo "Application version: ${script.env.APP_VERSION}"
        script.echo "Docker image tag: ${script.env.IMAGE_TAG}"
    }
}