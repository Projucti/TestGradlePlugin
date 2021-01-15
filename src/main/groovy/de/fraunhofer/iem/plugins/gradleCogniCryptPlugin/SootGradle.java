package de.fraunhofer.iem.plugins.gradleCogniCryptPlugin;


import org.gradle.api.Project;
import org.gradle.api.tasks.*;


@CacheableTask
public abstract class SootGradle extends SourceTask implements VerificationTask{


    @TaskAction
    public void doExecute(Project project){
        run(project);
    }

    public void run(Project project) {

        project.getLogger().info("analysis plugin working!");
    }

}
