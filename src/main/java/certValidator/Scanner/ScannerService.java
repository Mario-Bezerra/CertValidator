package certValidator.Scanner;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import certValidator.Interfaces.ICertificateParser;
import certValidator.Model.CertModel;

public class ScannerService {
    final static Logger logger = LoggerFactory.getLogger(ScannerService.class);
    
    private final List<String> passwords;
    private final List<ICertificateParser> parsers;

    public ScannerService(List<String> passwords, List<ICertificateParser> parsers) {
        this.passwords = passwords;
        this.parsers = parsers;
    }

    public List<CertModel> scan(String rootDir) throws IOException {
        try (Stream<Path> paths = Files.walk(Paths.get(rootDir))) {
            return paths
                .filter(Files::isRegularFile)
                .map(this::delegateParsing)
                .flatMap(List::stream)
                .collect(Collectors.toList());
        }
    }

    private List<CertModel> delegateParsing(Path path) {
        return parsers.stream()
                .filter(p -> p.supports(path))
                .findFirst()
                .map(p -> p.parse(path, passwords))
                .orElse(Collections.emptyList());
    }
}