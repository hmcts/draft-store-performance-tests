#!groovy

//noinspection GroovyAssignabilityCheck Jenkins requires this format
properties(
  [
    [
      $class: 'GithubProjectProperty',
      displayName: 'CMC Performance tests',
      projectUrlStr: 'https://github.com/hmcts/draft-store-performance-tests'
    ],
    pipelineTriggers([
      [
        $class: 'hudson.triggers.TimerTrigger',
        spec: '0 2 * * *'
      ]
    ])
  ]
)

//noinspection GroovyUnusedAssignment Jenkins requires an _ if not importing any classes
@Library('Reform') _

String channel = '#platform-engineering'

node('slave') {
  try {
    stage('Checkout') {
      deleteDir()
      checkout scm
    }

    stage('Run performance tests') {
      try {
        env.DRAFT_STORE_BASE_URL = 'https://devdraftstorelb.moneyclaim.reform.hmcts.net:4301/drafts'
        env.IDAM_URL = 'http://betadevbccidamapplb.reform.hmcts.net:80'
        env.S2S_URL  = 'http://betadevbccidams2slb.reform.hmcts.net:80'

        sh "./gradlew gatlingRun-uk.gov.hmcts.reform.draftstore.CreateMultipleDrafts"

      } finally {
        gatlingArchive()
      }
    }
  } catch (e) {
    notifyBuildFailure channel: channel
    throw e
  }

  notifyBuildFixed channel: channel
}
