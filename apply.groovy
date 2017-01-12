freeStyleJob('maintenance-apply-dsl') {
    displayName('apply-dsl')
    description('Applies all the Jenkins DSLs in the jenkins-dsl repository.')

    checkoutRetryCount(3)

    properties {
        githubProjectUrl('https://github.com/jessfraz/jenkins-dsl')
    }

    logRotator {
        numToKeep(2)
        daysToKeep(2)
    }

    scm {
        git {
            remote {
                url('git@github.com:jessfraz/jenkins-dsl.git')
                credentials('jenkins-dsl-deploy-key')
            }
            branches('*/master')
            extensions {
                wipeOutWorkspace()
                cleanAfterCheckout()
            }
        }
    }

    triggers {
        scm('H/30 * * * *')
        githubPush()
    }

    wrappers { colorizeOutput() }

    steps {
        dsl {
            external('dsl/**/*.groovy')
            removeAction('DELETE')
            removeViewAction('DELETE')
            additionalClasspath('.')
        }
    }

    publishers {
        extendedEmail {
            recipientList('$DEFAULT_RECIPIENTS')
            contentType('text/plain')
            triggers {
                stillFailing {
                    attachBuildLog(true)
                }
            }
        }
    }
}
