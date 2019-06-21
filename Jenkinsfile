#!/usr/bin/env groovy

pipeline {

  agent {
      kubernetes {
          label "voms-api-java-${env.JOB_BASE_NAME}-${env.BUILD_NUMBER}"
          cloud 'Kube mwdevel'
          defaultContainer 'jnlp'
          inheritFrom 'ci-template'
      }
  }

  options {
    timeout(time: 1, unit: 'HOURS')
    buildDiscarder(logRotator(numToKeepStr: '5'))
  }

  triggers { cron('@daily') }

  stages {

    stage('license-check') {
      steps {
        container('runner') {
          sh 'mvn -B license:check'
        }
      }
    }

    stage('build') {
      steps {
        container('runner') {
          sh 'mvn -B clean compile'
        }
      }
    }

    stage('test') {
      steps {
        container('runner') {
          sh 'mvn -B clean test'
        }
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
        container('runner') {
          sh 'mvn -B deploy' 
        }
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
