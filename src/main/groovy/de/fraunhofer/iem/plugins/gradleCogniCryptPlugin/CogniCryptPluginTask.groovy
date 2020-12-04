package de.fraunhofer.iem.plugins.gradleCogniCryptPlugin


import org.gradle.api.DefaultTask

import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.TaskExecutionException
import org.slf4j.Logger;
import org.slf4j.LoggerFactory


class CogniCryptPluginTask extends DefaultTask {

        private final Logger log = LoggerFactory.getLogger(this.getClass());

        @TaskAction
        public void sampleTaskPlugin() throws TaskExecutionException {
            log.info("Starting sample Task")
            try {
                CogniCryptPlugin plugin = getProject().getExtensions().findByType(CogniCryptPlugin.class);
                log.info("my code part starts")
                plugin.doExecute();
            }
            catch (Exception e) {
                log.error("", e)
                throw new TaskExecutionException(this, new Exception("Exception occurred while processing Task ", e))
            }
        }


    }




