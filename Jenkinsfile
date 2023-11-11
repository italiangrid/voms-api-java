pipeline {

  agent { label 'java11' }

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

}
