package io.github.spencerpark.ijava.magics;

import io.github.spencerpark.jupyter.kernel.magic.registry.CellMagic;
import io.github.spencerpark.jupyter.kernel.magic.registry.MagicsArgs;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;

public class CompileFile {
    private static final String coreDirectoryPrefix=new File("/opt/core/").getAbsolutePath();
    private static final String directoryPrefix="/opt/temp_project_"; 

    private static void copyDirectoryCompatibityMode(File source, File destination) throws IOException {
        if (source.isDirectory()) {
            copyDirectory(source, destination);
        } else {
            Files.copy(source.toPath(), destination.toPath());
            //copyFile(source, destination);
        }
    }

    private static void copyDirectory(File sourceDirectory, File destinationDirectory) throws IOException {
        if (!destinationDirectory.exists()) {
            destinationDirectory.mkdir();
        }
        for (String f : sourceDirectory.list()) {
            copyDirectoryCompatibityMode(new File(sourceDirectory, f), new File(destinationDirectory, f));
        }
    }

    public static void commonExecution(String body, String gradleArgs) throws Exception {
        //Patching this out since I'll need to know the path to the class file
        //System.out.println(coreDirectoryPrefix);
        //System.out.println(new File(coreDirectoryPrefix).exists());

        Integer projectNumber = 0;
        while (new File(directoryPrefix+projectNumber.toString()).exists()) {
            projectNumber++;
        }

        File project = new File(directoryPrefix+projectNumber.toString());
        //System.out.println("Creating a copy of the core project at " + project.getAbsolutePath());
        copyDirectory(new File(coreDirectoryPrefix), project);
        //System.out.println("Copied a copy of the project at " + project.getAbsolutePath());
        String result = "";
       
        StringBuilder nuBody = new StringBuilder("package core;");
        for (String bodyLine:body.split("\n")) {
            if (bodyLine.startsWith("public class")) {
                bodyLine = "public class App {";
            }

            nuBody.append("\n").append(bodyLine);
        }
        body = nuBody.toString();

        //https://stackoverflow.com/questions/9126142/output-the-result-of-a-bash-script
        try {
             File fileOut = new File(project.getAbsolutePath()+"/src/main/java/core/App.java");
            if (fileOut.exists()) {
                fileOut.delete();
            }

            //System.out.println("About to overwrite the file into the location " + fileOut.getAbsolutePath());
            ArrayList<String> linesToWrite = new ArrayList<String>();
            for (String string:body.split("\n")) {
                linesToWrite.add(string + "\n");
            }

            Files.write(fileOut.toPath(), linesToWrite);

            //System.out.println("Executing the gradle clean and build process");
            //https://stackoverflow.com/questions/9126142/output-the-result-of-a-bash-script
            Process process = Runtime.getRuntime().exec("./gradlew clean " + gradleArgs, null, project);

            BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                System.out.println(inputLine);
                result += inputLine;
            }
            in.close();
            System.out.println("Successfully ran the project at " + project.getAbsolutePath());

        } catch (IOException e) {
            System.out.println(e);
            throw new RuntimeException(e);
        }
        
    }

    @CellMagic
    public void compile(List<String> args, String body) throws Exception {
        commonExecution(body, "build")
    }

    @CellMagic
    public void execute(List<String> args, String body) throws Exception {
        commonExecution(body, "build run")
    }
}