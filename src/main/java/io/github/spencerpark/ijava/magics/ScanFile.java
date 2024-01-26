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

public class ScanFile {
    private static final File cryptoguard=new File("/opt/cryptoguard.jar");

    private static void executeCryptoguard(String arguments) {
            //https://stackoverflow.com/questions/9126142/output-the-result-of-a-bash-script
            Process process = Runtime.getRuntime().exec("java -jar " + cryptoguard.getAbsolutePath() + " " + arguments, null, project);

            BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                System.out.println(inputLine);
                result += inputLine;
            }
            in.close();
    }
}