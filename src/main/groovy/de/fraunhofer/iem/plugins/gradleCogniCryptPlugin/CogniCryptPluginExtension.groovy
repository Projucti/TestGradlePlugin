package de.fraunhofer.iem.plugins.gradleCogniCryptPlugin

import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory

import javax.inject.Inject



class CogniCryptPluginExtension{

    private final String rulesDirectory
    private final String reportsFolderParameter
    private final String outputFormat
    private final boolean dynamicCg

    @Input
    String getrulesDirectory(){
        return this.rulesDirectory
    }

    @Input
    String getoutputFormat(){
        return this.outputFormat
    }


    @Inject
    CogniCryptPluginExtension() {
        rulesDirectory=System.getProperty("rulesDirectory")
        reportsFolderParameter
        outputFormat= System.getProperty("outputFormat")
        dynamicCg
    }
}
