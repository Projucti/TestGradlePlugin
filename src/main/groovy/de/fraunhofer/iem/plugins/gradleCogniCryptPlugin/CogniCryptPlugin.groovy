package de.fraunhofer.iem.plugins.gradleCogniCryptPlugin

import org.gradle.api.Plugin
import org.gradle.api.Project

class CogniCryptPlugin implements Plugin<Project>{
    @Override
    void apply(Project project) {
        project.extensions.create('diff',CogniCryptPluginExtension.class)
        project.tasks.create('diff', CogniCryptPluginTask){
            file1= project.diff.file1
            file2= project.diff.file2
        }
    }
}
