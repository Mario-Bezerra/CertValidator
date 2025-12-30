package certValidator.scanner;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import certValidator.interfaces.ICertificateParser;
import certValidator.model.CertModel;

/**
 * Service responsible for scanning the file system to find and parse
 * certificates.
 */
public class ScannerService {
    /** Logger instance for this class. */
    final static Logger logger = LoggerFactory.getLogger(ScannerService.class);

    /** List of passwords to attempt when parsing keystores. */
    private final List<String> passwords;
    /** List of parsers to use for extracting certificate data. */
    private final List<ICertificateParser> parsers;
    /** Set of directory names that should be ignored during scanning. */
    private final Set<String> ignoredDirectories;

    /**
     * Constructs a ScannerService with the provided passwords and parsers.
     *
     * @param passwords List of potential passwords for protected keystores.
     * @param parsers   List of parsers supported by this service.
     */
    public ScannerService(List<String> passwords, List<ICertificateParser> parsers) {
        this.passwords = passwords;
        this.parsers = parsers;
        this.ignoredDirectories = new HashSet<>(Arrays.asList("node_modules",
                "target",
                "dist",
                "build",
                ".git",
                ".idea",
                ".vscode",
                "bin",
                "obj",
                "coverage",
                ".svn",
                ".hg"));
    }

    /**
     * Recursively scans the specified directory for certificate files.
     *
     * @param rootDir The root directory to start scanning.
     * @return A list of found certificates as CertModel instances.
     * @throws IOException If an error occurs during directory traversal.
     */
    public List<CertModel> scan(String rootDir) throws IOException {
        List<CertModel> results = new ArrayList<>();

        Files.walkFileTree(Paths.get(rootDir), new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                if (ignoredDirectories.contains(dir.getFileName().toString())) {
                    logger.debug("Skipping ignored directory: " + dir);
                    return FileVisitResult.SKIP_SUBTREE;
                }
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                results.addAll(delegateParsing(file));
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                logger.warn("Failed to visit file: " + file + " (" + exc.getMessage() + ")");
                return FileVisitResult.CONTINUE;
            }
        });

        return results;
    }

    private List<CertModel> delegateParsing(Path path) {
        return parsers.stream()
                .filter(p -> p.supports(path))
                .findFirst()
                .map(p -> p.parse(path, passwords))
                .orElse(Collections.emptyList());
    }
}