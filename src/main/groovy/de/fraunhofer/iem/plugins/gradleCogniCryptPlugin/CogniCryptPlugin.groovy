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

import org.gradle.api.artifacts.Configuration
import org.gradle.api.component.Artifact
import org.gradle.api.plugins.quality.CodeQualityExtension
import org.gradle.api.plugins.quality.internal.AbstractCodeQualityPlugin
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputDirectory

import soot.SceneTransformer
import soot.SootMethod
import soot.Transformer
import soot.Unit

import java.util.regex.Pattern

class CogniCryptPlugin extends AbstractCodeQualityPlugin<SootGradle> {

    private String reportsFolderParameter;
    private String outputFormat;
    private boolean dynamicCg;
    public String rulesDirectory;
    private Optional<File> targetDir = Optional.empty();

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
        //extension.setRulesets();
        //extension.setRuleSetFiles(project.getLayout().files());
        //conventionMappingOf(extension).map("targetJdk", ()->getDefaultTargetJdk(getJavaPluginConvention().getSourceCompatibility()));
        validateParameters();
        createAnalysisTransformer();
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

    @OutputDirectory
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
    /**
     * Receives the set of rules form a given directory.
     */
    @InputFiles
    private List<CrySLRule> getRules() throws CryptoAnalysisException {
        return CrySLRuleReader.readFromDirectory(new File(rulesDirectory));
    }

    private void validateParameters() throws GradleException{
        if (!new File(rulesDirectory).exists() || !new File(rulesDirectory).isDirectory()) {
            throw new GradleException("Failed to locate the folder of the CrySL rules. " +
                    "Specify -Dcognicrypt.rulesDirectory=<PATH-TO-CRYSL-RULES>.")
        }
        if (!Pattern.matches("(standard|sarif)", outputFormat)) {
            throw new GradleException("Incorrect output format specified. " +
                    "Use -Dcognicrypt.outputFormat=[standard|sarif].")
        }
    }

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
                    fileReporter = new SARIFReporter(getReportFolder().getAbsolutePath(), rules,
                            new SourceCodeLocater(project.hasParent() ?
                                    project.getParent() : project.getBasedir()))
                }
                else {
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

    public File getProjectTargetDir() {
        if (!targetDir.isPresent()){
            File td = new File(project.getBuild().getDirectory());
            targetDir = Optional.of(td);
        }
        return targetDir.get();
    }



}
