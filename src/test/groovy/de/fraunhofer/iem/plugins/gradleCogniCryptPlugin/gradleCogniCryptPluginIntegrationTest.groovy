package de.fraunhofer.iem.plugins.gradleCogniCryptPlugin

import org.gradle.testfixtures.ProjectBuilder
import org.junit.Rule
import org.junit.Test
import org.gradle.api.Project
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

class gradleCogniCryptPluginIntegrationTest extends Specification{

    @Rule
    TemporaryFolder testProjectDir = new TemporaryFolder()
    File buildFile
    @Test
     void greetingTest(){
        String rulesDirectory= System.getProperty("rulesDirectory");
        buildFile = testProjectDir.newFile('build.gradle')
        buildFile << """
            plugins {
                id 'com.tomgregory.file-diff'
            }
        """
        Project project = ProjectBuilder.builder().build();
        project.getPluginManager().apply("de.fraunhofer.iem.plugins.gradleCogniCryptPlugin");
        val task = project.tasks.findByName("sampleTaskPlugin")

        assertTrue(project.getPluginManager()
                .hasPlugin("de.fraunhofer.iem.plugins.gradleCogniCryptPlugin"));
        assertNotNull(task)

        assertNotNull(project.getTasks().getByName("sampleTaskPlugin"));
    }

}
