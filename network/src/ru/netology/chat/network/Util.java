package ru.netology.chat.network;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class Util {

    public static String getSettings(String inputFilePath, int lineNumber) {
        String line;
        try (Stream<String> lines = Files.lines(Paths.get(inputFilePath))) {
            line = lines.skip(lineNumber - 1).findFirst().orElse(null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return line;
    }
}
