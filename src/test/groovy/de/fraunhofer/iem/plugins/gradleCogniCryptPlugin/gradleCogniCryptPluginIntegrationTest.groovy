package de.fraunhofer.iem.plugins.gradleCogniCryptPlugin

import org.gradle.testkit.runner.GradleRunner
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

class gradleCogniCryptPluginIntegrationTest extends Specification{
    @Rule
    TemporaryFolder testProjectDir= new TemporaryFolder()
    File buildFile
    String rulesDirectory

    def setup(){
        buildFile=testProjectDir.newFile('build.gradle')
        buildFile <<"""
            plugins{
                id 'de.fraunhofer.iem.plugins.gradleCogniCryptPlugin'
            }
        """
    }

}
