package de.fraunhofer.iem.plugins.gradleCogniCryptPlugin

import javax.inject.Inject



class CogniCryptPluginExtension{

    private String rulesDirectory
    private String reportsFolderParameter
    private String outputFormat
    private boolean dynamicCg

    @Inject
    CogniCryptPluginExtension() {
        rulesDirectory
        reportsFolderParameter
        outputFormat
        dynamicCg
    }
}
