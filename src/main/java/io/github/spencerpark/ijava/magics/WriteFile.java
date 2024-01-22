



package io.github.spencerpark.ijava.magics;

import io.github.spencerpark.jupyter.kernel.magic.registry.CellMagic;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Scanner;

public class ExecMagics {
    @CellMagic
    public void loadFromPOM(List<String> args, String body) throws Exception {
        try {
            File tempPomPath = File.createTempFile("ijava-maven-", ".pom").getAbsoluteFile();


            String rawPom = this.solidifyPartialPOM(body);
            Files.write(tempPomPath.toPath(), rawPom.getBytes(Charset.forName("utf-8")));

            List<String> loadArgs = new ArrayList<>(args.size() + 1);
            loadArgs.add(tempPomPath.getAbsolutePath());
            loadArgs.addAll(args);

            this.loadFromPOM(loadArgs);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}