package org.eremin.server;

import org.eremin.server.exception.FileNotFoundException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;

public class FileHelper {

    private final String filePath;

    public FileHelper(String filePath) {
        this.filePath = filePath;
    }

    public String readFile(String fileName) {
        try {
            try (var lines = Files.lines(Paths.get(filePath + fileName))) {
                return lines.collect(Collectors.joining(System.lineSeparator()));
            }
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
            throw new FileNotFoundException("File not found", e);
        }
    }

    public void writeToFile(String fileName, String content) {
        System.out.println(fileName);
        Path path = Paths.get(filePath + fileName);
        try {
            Files.createDirectories(path.getParent());
            Files.write(path, content.getBytes());
            System.out.println("File written: " + path.toAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("IOException: " + e.getMessage());
        }
    }
}
