package de.fraunhofer.iem.plugins.gradleCogniCryptPlugin


class gradleCogniCryptPluginIntegrationTest {

    def setup() {
        buildFile << """
            apply plugin: "java"
        """
    }

    String getMainTask() {
        return "check"
    }

}
