package org.eremin.server;

import lombok.NoArgsConstructor;
import org.eremin.server.exception.FileNotFoundException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;

public class FileReader {

    private static String filePath;

    public FileReader(String filePath) {
        this.filePath = filePath;
    }

    public String read(String fileName) {
        try {
            try (var lines = Files.lines(Paths.get(filePath + fileName))) {
                return lines.collect(Collectors.joining(System.lineSeparator()));
            }
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
            throw new FileNotFoundException("File not found", e);
        }
    }
}
