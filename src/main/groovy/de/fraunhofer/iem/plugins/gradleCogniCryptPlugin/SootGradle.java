package de.fraunhofer.iem.plugins.gradleCogniCryptPlugin;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import org.gradle.api.Project;
import org.gradle.api.reporting.Reporting;

import org.gradle.api.tasks.*;
import soot.PackManager;
import soot.Transform;
import soot.Transformer;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@CacheableTask
public abstract class SootGradle extends SourceTask implements VerificationTask{

    private List<String> classFolders = Lists.newLinkedList();
    private Optional<File> targetDir = Optional.empty();
    protected String localRepoProperty;
    private String callGraph;
    private boolean includeDependencies;
    private String excludedPackages;


    @TaskAction
    public void doExecute(Project project){

            if (!targetDir.isPresent()){
                File td = project.getBuildFile();
                targetDir = Optional.of(td);
            }


        run(targetDir.get(),project);


    }

    public void run(File targetDir, Project project) {
        final File classFolder = new File(targetDir.getAbsolutePath() + File.separator + "classes");
        if(!classFolder.exists()) {
            project.getLogger().info("No class folder found at " + classFolder + "");
            return;
        }

        new SootSetup(CreateSootSetupData()).run();
        analyse();
        project.getLogger().info("Soot analysis done!");
    }

    protected abstract Transformer createAnalysisTransformer();

    private SootSetupData CreateSootSetupData() {
        List<String> excludeList = getExcludeList();
        List<String> appCp = buildApplicationClassPath();
        String sootCp = buildSootClassPath(classFolders, appCp);
        boolean modular = JavaUtils.isModularProject(new File(targetDir.get(), "classes"));
        return new SootSetupData(callGraph, sootCp, appCp, modular, excludeList);
    }

    private void analyse() {
        PackManager.v().getPack("wjap").add(new Transform("wjap.ifds", createAnalysisTransformer()));
        PackManager.v().runPacks();
    }



    private List<String> getExcludeList() {
        return  Lists.newArrayList(excludedPackages.split(","));
    }

    @Classpath
    private List<String> buildApplicationClassPath() {
        final File classFolder = new File(targetDir.get().getAbsolutePath(), "classes");
        return Lists.newArrayList(classFolder.getAbsolutePath());
    }

    @Classpath
    private String buildSootClassPath(List<String> dependencies, List<String> applicationCp) {
        List<String> sootCp = Stream.of(dependencies, applicationCp)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
        String javaPath = JavaUtils.getJavaRuntimePath().getAbsolutePath();
        sootCp.add(0, javaPath);
        List<String> distinctSootCp = sootCp.stream().distinct().collect(Collectors.toList());
        return Joiner.on(File.pathSeparator).join(distinctSootCp);
    }
}
