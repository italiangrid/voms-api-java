pipeline {

  agent { label 'java17' }

  options {
    ansiColor('xterm')
    buildDiscarder(logRotator(numToKeepStr: '5'))
    timeout(time: 1, unit: 'HOURS')
    timestamps()
  }

  triggers { cron('@daily') }

  stages {

    stage('license-check') {
      steps {
        sh 'mvn -B license:check'
      }
    }

    stage('build') {
      steps {
        sh 'mvn -B clean compile'
      }
    }

    stage('test') {
      steps {
        sh 'mvn -B clean test'
      }

      post {
        always {
          container('runner') {
            junit '**/target/surefire-reports/TEST-*.xml'
          }
        }
      }
    }

    stage('deploy') {
      steps {
        sh 'mvn -B deploy'
      }
    }

  }

  post {
    success {
      slackSend channel: "#voms", color: 'good', message: "${env.JOB_NAME} - #${env.BUILD_NUMBER} Success (<${env.BUILD_URL}|Open>)" 
    }

    unstable {
      slackSend channel: "#voms", color: 'danger', message: "${env.JOB_NAME} - #${env.BUILD_NUMBER} Unstable (<${env.BUILD_URL}|Open>)" 
    }

    failure {
      slackSend channel: "#voms", color: 'danger', message: "${env.JOB_NAME} - #${env.BUILD_NUMBER} Failure (<${env.BUILD_URL}|Open>)" 
    }
  }
}
