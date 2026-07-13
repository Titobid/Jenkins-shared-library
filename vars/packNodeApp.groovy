#!/user/bin/env groovy

def call () {
    echo 'Packaging Node.js application...'
    sh 'npm pack'
}