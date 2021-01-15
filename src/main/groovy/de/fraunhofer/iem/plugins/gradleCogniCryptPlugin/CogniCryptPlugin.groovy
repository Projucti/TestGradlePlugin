package de.fraunhofer.iem.plugins.gradleCogniCryptPlugin

import org.gradle.api.artifacts.Configuration
import org.gradle.api.component.Artifact
import org.gradle.api.plugins.quality.CodeQualityExtension
import org.gradle.api.plugins.quality.internal.AbstractCodeQualityPlugin


class CogniCryptPlugin extends AbstractCodeQualityPlugin<SootGradle> {

    @Override
    protected String getToolName() {
        return "CogniCrypt"
    }

    @Override
    protected Class<SootGradle> getTaskType() {
        return SootGradle.class;
    }
    @Override
    protected CodeQualityExtension createExtension() {
        extension=project.getExtensions().create("cognicryptExtension", CogniCryptPluginExtension.class,project);
        extension.setToolVersion(project.properties.get('version'));
        return extension;
    }


    @Override
    protected void configureConfiguration(Configuration configuration) {
        configureDefaultDependencies(configuration);

    }


    private void configureDefaultDependencies(Configuration configuration) {
        Set<Artifact> artifacts;
        configuration.compile.resolvedConfiguration.resolvedArtifacts.each {
            //artifacts=println it.name // << the artifact name
            artifacts=println it.file // << the file reference
        }
    }


    @Override
    protected void configureTaskDefaults(SootGradle task, String baseName) {
        Configuration configuration = project.getConfigurations().getAt(getConfigurationName());
    }

}
