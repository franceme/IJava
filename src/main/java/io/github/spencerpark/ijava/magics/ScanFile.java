package io.github.spencerpark.ijava.magics;

import frontEnd.MessagingSystem.routing.structure.Scarf.AnalyzerReport;
import frontEnd.MessagingSystem.routing.structure.Default.Report;

import org.apache.commons.io.filefilter.WildcardFileFilter;

import io.github.spencerpark.jupyter.kernel.magic.registry.LineMagic;
import io.github.spencerpark.jupyter.kernel.magic.registry.MagicsArgs;

import io.github.spencerpark.ijava.execution.CodeEvaluator.eval;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;

public class ScanFile {
    private static final File cryptoguard=new File("/opt/cryptoguard.jar");

    //javaRetrieval
    private static void executeCryptoguard(String arguments) throws Exception {
            //https://stackoverflow.com/questions/9126142/output-the-result-of-a-bash-script
            //String command = "/bin/java_eight -jar " + cryptoguard.getAbsolutePath() + " " + arguments;
            //String command = System.getenv("JAVA8") + " -jar " + cryptoguard.getAbsolutePath() + " " + arguments;
            String command = javaRetrieval("JAVA8") + "/bin/java -jar " + cryptoguard.getAbsolutePath() + " " + arguments;
            System.out.println(command);

            Process process = Runtime.getRuntime().exec(command, null, new File("/opt"));

            BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                System.out.println(inputLine);
            }
            in.close();
    }

    private static AnalyzerReport retrieveResults(String resultsFile) throws Exception {
        return retrieveResultsByXML(resultsFile);
    }

    private static AnalyzerReport retrieveResultsByXML(String resultsFile) throws Exception {
        return AnalyzerReport.deserialize(new File(resultsFile));
    }

    private static Report retrieveResultsByJSON(String resultsFile) throws Exception {
        return Report.deserialize(new File(resultsFile));
    }

    @LineMagic
    public String checkVar(List<String> args) throws Exception {
        String output = "";
        MagicsArgs schema = MagicsArgs.builder().required("variable").onlyKnownKeywords().onlyKnownFlags().build();

        Map<String, List<String>> vals = schema.parse(args);
        String variable = vals.get("variable").get(0);
        try {
            output = eval(variable).toString();
        } catch (Exception e) {
            System.out.println(e);
        }
        return output;
    }

    @LineMagic(aliases = { "cryptoguard", "cguard" })
    public Report scan(List<String> args) throws Exception {
        try {
            MagicsArgs schema = MagicsArgs.builder().required("file").onlyKnownKeywords().onlyKnownFlags().build();

            Map<String, List<String>> vals = schema.parse(args);
            String filepath = vals.get("file").get(0);
            String fileResults = filepath + ".json";

            StringBuilder argBuilder = new StringBuilder();

            argBuilder.append("-s ").append(filepath).append(" ");
            argBuilder.append("-in class ");
            argBuilder.append("-o ").append(fileResults).append(" ");
            //argBuilder.append("-java /bin/java_eight");
            //argBuilder.append("-java " + System.getenv("JAVA8"));
            argBuilder.append("-java " + javaRetrieval("JAVA8"));

            executeCryptoguard(argBuilder.toString());
            return retrieveResultsByJSON(fileResults);
        } catch (IOException e) {
            System.out.println(e);
            throw new RuntimeException(e);
        }
    }

    @LineMagic(aliases = { "pcryptoguard", "pcguard" })
    public AnalyzerReport pscan(List<String> args) throws Exception {
        try {
            MagicsArgs schema = MagicsArgs.builder().required("file").onlyKnownKeywords().onlyKnownFlags().build();

            Map<String, List<String>> vals = schema.parse(args);
            String filepath = vals.get("file").get(0);
            String fileResults = filepath + ".xml";

            StringBuilder argBuilder = new StringBuilder();

            argBuilder.append("-s ").append(filepath).append(" ");
            argBuilder.append("-in class ");
            argBuilder.append("-o ").append(fileResults).append(" ");
            //argBuilder.append("-java /bin/java_eight");
            //argBuilder.append("-java " + System.getenv("JAVA8"));
            argBuilder.append("-java " + javaRetrieval("JAVA8"));

            executeCryptoguard(argBuilder.toString());
            return retrieveResultsByXML(fileResults);
        } catch (IOException e) {
            System.out.println(e);
            throw new RuntimeException(e);
        }
    }
    
    private static String getFileFromWildCard(String path) {
        String[] split = path.split("/");
        StringBuilder build = new StringBuilder();
        for (int itr = 0;itr < split.length - 1;itr ++)
            build.append(split[itr]).append("/");

        String[] files = new File(build.toString()).list(new WildcardFileFilter(split[split.length-1]));
        if (files.length > 0)
            return build.toString() + files[files.length - 1];
        else
            return null;
    }

    private static String javaRetrieval(String jvmVersion) {
        String cur_user = System.getProperty("user.name");

        if (jvmVersion.toLowerCase().equals("java7"))//java7
            return getFileFromWildCard("/home/" + cur_user + "/.sdkman/candidates/java/7*");
        else if (jvmVersion.toLowerCase().equals("java") || jvmVersion.toLowerCase().equals("java8"))//java8
            return getFileFromWildCard("/home/" + cur_user + "/.sdkman/candidates/java/8*");
        else //android
            return "/home/" + cur_user + "/.sdkman/candidates/android/current";
    }

    @LineMagic(aliases = { "jdk", "jvm" })
    public String vm(List<String> args) throws Exception {
        MagicsArgs schema = MagicsArgs.builder().required("jvm").onlyKnownKeywords().onlyKnownFlags().build();
        Map<String, List<String>> vals = schema.parse(args);
        String jvm_option = vals.get("jvm").get(0);

        return javaRetrieval(jvm_option);
    }

}