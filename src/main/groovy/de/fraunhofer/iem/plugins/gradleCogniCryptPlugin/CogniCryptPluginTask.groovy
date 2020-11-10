package de.fraunhofer.iem.plugins.gradleCogniCryptPlugin

import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class CogniCryptPluginTask extends DefaultTask {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @InputFile
    Property<File> file1
    @InputFile
    Property<File> file2
    @OutputFile
    File resultFile= new File("${project.buildDir}/result.txt")

    @TaskAction
    def diff(){
        log.info("Starting  sample task");
        String result
        if(file1.get().size()== file2.get().size()){
            result= "File has same size"
        }
        else {
            File largeFile= file1.get().size()>file2.get().size() ? file1.get(): file2.get()
            result="${largeFile.toString()} was the large file"
        }
        resultFile.write(result)
        println("File written to ${resultFile}")
        println(result)
        log.info("Successfully completed sample Task");

    }
}
