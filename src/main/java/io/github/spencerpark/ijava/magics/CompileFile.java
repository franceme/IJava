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
            Files.copy(src.toPath(), dest.toPath());
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

    @CellMagic
    public void compile(List<String> args, String body) throws Exception {
        //Patching this out since I'll need to know the path to the class file
        Boolean deleteProject = false;

        Integer projectNumber = 0;
        while (new File(directoryPrefix+projectNumber.toString()).exists()) {
            projectNumber++;
        }

        File project = new File(directoryPrefix+projectNumber.toString());
        System.out.println("Creating a copy of the core project at " + project.getAbsolutePath());
        copyDirectory(new File(coreDirectoryPrefix), project);
        System.out.println("Copied a copy of the project at " + project.getAbsolutePath());
        String result = "";
       
        StringBuilder nuBody = new StringBuilder("package core;");
        for (String bodyLine:body.split("\n")) {
            if (bodyLine.startsWith("public class")) {
                bodyLine = "public class App {\n";
            }

            nuBody.append(bodyLine);
        }
        body = nuBody.toString();

        //https://stackoverflow.com/questions/9126142/output-the-result-of-a-bash-script
        try {
             File fileOut = new File(project.getAbsolutePath()+"/src/main/java/core/App.java");
            if (fileOut.exists()) {
                fileOut.delete();
            }

            System.out.println("About to overwrite the file into the location " + fileOut.getAbsolutePath());
            try (PrintWriter out = new PrintWriter(fileOut)) {
                out.println("package core;");
                out.println(body);
            }

            System.out.println("Executing the gradle clean and build process");
            //https://stackoverflow.com/questions/9126142/output-the-result-of-a-bash-script
            Process process = Runtime.getRuntime().exec("cd " + project.getAbsolutePath() + " && ./gradlew clean build");

            BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                System.out.println(inputLine);
                result += inputLine;
            }
            in.close();

            System.out.println(result);

        } catch (IOException e) {
            System.out.println(e);
            throw new RuntimeException(e);
        }
        
    }
}