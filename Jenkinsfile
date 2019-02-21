#!/usr/bin/env groovy

pipeline {

  agent { label 'maven' }

  options {
    timeout(time: 1, unit: 'HOURS')
    buildDiscarder(logRotator(numToKeepStr: '5'))
  }

  triggers { cron('@daily') }

  stages {

    stage('license-check') {
      steps {
        container('maven-runner') {
          sh 'mvn -B license:check'
        }
      }
    }

    stage('build') {
      steps {
        container('maven-runner') {
          sh 'mvn -B clean compile'
        }
      }
    }

    stage('test') {
      steps {
        container('maven-runner') {
          sh 'mvn -B clean test'
        }
      }

      post {
        always {
          container('maven-runner') {
            junit '**/target/surefire-reports/TEST-*.xml'
          }
        }
      }
    }

    stage('deploy') {
      steps {
        container('maven-runner') {
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
