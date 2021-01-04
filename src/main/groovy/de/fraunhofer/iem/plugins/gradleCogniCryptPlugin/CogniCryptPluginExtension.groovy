package de.fraunhofer.iem.plugins.gradleCogniCryptPlugin

import org.gradle.api.Project
import org.gradle.api.plugins.quality.CodeQualityExtension
import org.gradle.api.plugins.quality.TargetJdk
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.OutputFile
import javax.inject.Inject

/**
 * Configuration options for the plugin.
 *
 */

class CogniCryptPluginExtension extends CodeQualityExtension{

    private final Project project;
    private final String rulesDirectory
    private final String reportsFolderParameter
    private final String outputFormat
    private final boolean dynamicCg

    @InputDirectory
    String getrulesDirectory(){
        return this.rulesDirectory
    }

    @OutputFile
    String getoutputFormat(){
        return this.outputFormat
    }

    CogniCryptPluginExtension() {
        this.project=project;
        rulesDirectory=System.getProperty("rulesDirectory")
        reportsFolderParameter
        outputFormat= System.getProperty("outputFormat")
        dynamicCg
    }
    public TargetJdk getTargetJdk() {
        return targetJdk;
    }

    public void setTargetJdk(TargetJdk targetJdk) {
        this.targetJdk = targetJdk;
    }


}
