package de.fraunhofer.iem.plugins.gradleCogniCryptPlugin

import org.apache.maven.artifact.Artifact
import org.apache.maven.shared.transfer.repository.RepositoryManager
import org.codehaus.plexus.util.StringUtils
import org.gradle.api.DefaultTask
import org.gradle.api.artifacts.Configuration
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.TaskExecutionException
import org.slf4j.Logger;
import org.slf4j.LoggerFactory


class CogniCryptPluginTask extends DefaultTask {

        private final Logger log = LoggerFactory.getLogger(this.getClass());
        private String rulesDirectory;
        private File outputDir;
        protected String localRepoProperty;
        private RepositoryManager repositoryManager;

        @OutputDirectory
        public File getOutputDir() { return this.outputDir; }


        @TaskAction
         void sampleTaskPlugin() throws TaskExecutionException {
            log.info("Starting sample Task")
            try {CogniCryptPluginExtension extension = getProject().getExtensions()
                    .findByType(CogniCryptPluginExtension.class);
                String filePath = extension.getSampleFilePath();
                log.debug("Sample file path is: {}",filePath);
                log.info("my code part starts")
                //my logic comes



                Set<Artifact> artifacts;
                project.configurations.compile.resolvedConfiguration.resolvedArtifacts.each {
                    //artifacts=println it.name // << the artifact name
                    artifacts=println it.file // << the file reference
                }
                //Configuration configuration= project.getConfigurations()
                //println(rulesDirectory)
                for (Artifact a : artifacts) {
                    //String file = a.getFile().getPath();
                    String file = a.getPath();
                    // substitute the property for the local repo path to make the classpath file
                    // portable.
                    if (StringUtils.isNotEmpty(localRepoProperty)) {
                        File localBasedir = repositoryManager
                                .getLocalRepositoryBasedir(session.getProjectBuildingRequest());

                        file = StringUtils.replace(file, localBasedir.getAbsolutePath(), localRepoProperty);
                    }
                    classFolders.add(file);
                }



                log.info("Successfully completed sample Task");
            }
            catch (Exception e) {
                log.error("", e)
                throw new TaskExecutionException(this, new Exception("Exception occurred while processing Task ", e))
            }
        }

    }




