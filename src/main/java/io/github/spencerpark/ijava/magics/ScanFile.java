package io.github.spencerpark.ijava.magics;

import frontEnd.MessagingSystem.routing.structure.Scarf.AnalyzerReport;

import io.github.spencerpark.jupyter.kernel.magic.registry.LineMagic;
import io.github.spencerpark.jupyter.kernel.magic.registry.MagicsArgs;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;

public class ScanFile {
    private static final File cryptoguard=new File("/opt/cryptoguard.jar");

    private static void executeCryptoguard(String arguments) throws Exception {
            //https://stackoverflow.com/questions/9126142/output-the-result-of-a-bash-script
            Process process = Runtime.getRuntime().exec("java -jar " + cryptoguard.getAbsolutePath() + " " + arguments, null, new File("/opt"));

            BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                System.out.println(inputLine);
            }
            in.close();
    }

    private static AnalyzerReport retrieveResults(String resultsFile) throws Exception {
        return AnalyzerReport.deserialize(new File(resultsFile));
    }

    @LineMagic(aliases = { "cryptoguard", "cguard" })
    public AnalyzerReport scan(List<String> args) throws Exception {
        try {
            MagicsArgs schema = MagicsArgs.builder().required("file").onlyKnownKeywords().onlyKnownFlags().build();

            Map<String, List<String>> vals = schema.parse(args);
            String filepath = vals.get("file").get(0);
            String fileResults = filepath + ".xml";

            StringBuilder argBuilder = new StringBuilder();

            argBuilder.append("-s ").append(filepath).append(" ");
            argBuilder.append("-in class ");
            argBuilder.append("-o ").append(fileResults).append(" ");

            executeCryptoguard(argBuilder.toString());
            return retrieveResults(fileResults);
        } catch (IOException e) {
            System.out.println(e);
            throw new RuntimeException(e);
        }
        return null;
    }
}