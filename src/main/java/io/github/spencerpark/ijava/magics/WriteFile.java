package io.github.spencerpark.ijava.magics;

import io.github.spencerpark.jupyter.kernel.magic.registry.CellMagic;
import io.github.spencerpark.jupyter.kernel.magic.registry.MagicsArgs;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.io.*;

public class WriteFile {
    @CellMagic
    public void writeOut(List<String> args, String body) throws Exception {
        try {
            MagicsArgs schema = MagicsArgs.builder().required("filepath").onlyKnownKeywords().onlyKnownFlags().build();

            Map<String, List<String>> vals = schema.parse(args);
            String filepath = vals.get("filepath").get(0);

            File fileOut = new File(filepath);
            if (fileOut.exists()) {
                fileOut.delete();
            }

            try (PrintWriter out = new PrintWriter(filepath)) {
                out.println(body);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}