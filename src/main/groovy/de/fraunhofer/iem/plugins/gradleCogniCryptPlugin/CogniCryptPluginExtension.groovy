package de.fraunhofer.iem.plugins.gradleCogniCryptPlugin

import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property

import javax.inject.Inject


class CogniCryptPluginExtension {
    final Property<File> file1
    final Property<File> file2
    @Inject
    CogniCryptPluginExtension(ObjectFactory objectFactory){
        file1= objectFactory.property(File)
        file2= objectFactory.property(File)
    }
}
