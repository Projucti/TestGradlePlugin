package de.fraunhofer.iem.plugins.gradleCogniCryptPlugin

import boomerang.callgraph.BoomerangICFG
import boomerang.callgraph.ObservableDynamicICFG
import boomerang.callgraph.ObservableICFG
import boomerang.callgraph.ObservableStaticICFG
import boomerang.preanalysis.BoomerangPretransformer
import crypto.analysis.CrySLResultsReporter
import crypto.analysis.CryptoScanner
import crypto.exceptions.CryptoAnalysisException
import crypto.reporting.CommandLineReporter
import crypto.reporting.ErrorMarkerListener
import crypto.reporting.SARIFReporter
import crypto.reporting.SourceCodeLocater
import crypto.rules.CrySLRule
import crypto.rules.CrySLRuleReader
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import soot.SceneTransformer
import soot.SootMethod
import soot.Transformer
import soot.Unit

import java.util.regex.Pattern

class CogniCryptPlugin extends SootGradle implements Plugin<Project>{

    //private String rulesDirectory= System.getProperty("rulesDirectory");
    private String reportsFolderParameter="cognicrypt-reports";
    private String outputFormat="standard";
    private boolean dynamicCg= "true";
    @Override
    void apply(Project project) {

        project.extensions.create('difff',CogniCryptPluginExtension.class)
        project.tasks.create('myTask', CogniCryptPluginTask.class){
            validateParameters();
            super.doExecute(project);
        }
    }

    @Override
    protected Transformer createAnalysisTransformer() {
        return new SceneTransformer() {

            @Override
            protected void internalTransform(String phaseName, Map<String, String> options) {
                BoomerangPretransformer.v().reset()
                BoomerangPretransformer.v().apply()
                final CrySLResultsReporter reporter = new CrySLResultsReporter()
                ErrorMarkerListener fileReporter

                System.out.println("Fetching CogniCrypt Rules.")
                List<CrySLRule> rules
                try {
                    rules = getRules()
                } catch (Exception e) {
                    System.out.println("Failed fetching rules: " + e.getMessage())
                    return
                }

                if (outputFormat.equalsIgnoreCase("standard")) {
                    fileReporter = new CommandLineReporter(getReportFolder().getAbsolutePath(), rules)
                } else if (outputFormat.equalsIgnoreCase("sarif")) {
                     project = getProject()
                    fileReporter = new SARIFReporter(getReportFolder().getAbsolutePath(), rules,
                            new SourceCodeLocater(project.hasParent() ?
                                    project.getParent().getBasedir() :
                                    project.getBasedir()))
                } else {
                    throw new RuntimeException("Illegal state")
                }
                reporter.addReportListener(fileReporter)

                System.out.println("Creating ICFG!")
                ObservableICFG<Unit, SootMethod> icfg
                if (!dynamicCg) {
                    icfg = new ObservableStaticICFG(new BoomerangICFG(true))
                } else {
                    icfg = new ObservableDynamicICFG(false)
                }
                CryptoScanner scanner = new CryptoScanner() {

                    @Override
                    public ObservableICFG<Unit, SootMethod> icfg() {
                        return icfg
                    }

                    @Override
                    public CrySLResultsReporter getAnalysisListener() {
                        return reporter
                    }
                }

                System.out.println("Starting CogniCrypt Analysis!")
                scanner.scan(rules)
            }
        }
    }

    private void validateParameters() {
        if (!new File(rulesDirectory).exists() || !new File(rulesDirectory).isDirectory()) {
            throw new GradleException("Failed to locate the folder of the CrySL rules. " +
                    "Specify -Dcognicrypt.rulesDirectory=<PATH-TO-CRYSL-RULES>.")
        }
        if (!Pattern.matches("(standard|sarif)", outputFormat)) {
            throw new GradleException("Incorrect output format specified. " +
                    "Use -Dcognicrypt.outputFormat=[standard|sarif].")
        }
    }
    /**
     * Receives the set of rules form a given directory.
     */
    private List<CrySLRule> getRules() throws CryptoAnalysisException {
        return CrySLRuleReader.readFromDirectory(new File(rulesDirectory));
    }


    private File getReportFolder() {
        File reportsFolder = new File(reportsFolderParameter);
        if (!reportsFolder.isAbsolute()) {
            reportsFolder = new File(getProjectTargetDir(), reportsFolderParameter);
        }
        if (!reportsFolder.exists()) {
            reportsFolder.mkdirs();
        }
        return reportsFolder;
    }
}
