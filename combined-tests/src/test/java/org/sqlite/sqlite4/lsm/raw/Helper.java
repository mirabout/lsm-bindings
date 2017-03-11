package org.sqlite.sqlite4.lsm.raw;

import sun.misc.Unsafe;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Helper {
    private Helper() {}

    static {
        loadNativeLibrary();
    }

    private static void loadNativeLibrary() {
        Path root = Paths.get(System.getProperty("user.dir")).getParent();
        try {
            List<Path> libsPaths = findLibsInPath(root);
            PriorityQueue<SortedPath> sortedPaths = new PriorityQueue<>();
            for (Path path: libsPaths)
                sortedPaths.add(new SortedPath(path));

            while (!sortedPaths.isEmpty()) {
                SortedPath recentlyModifiedLibPath = sortedPaths.remove();
                if (!recentlyModifiedLibPath.seemsValid())
                    break;
                Path path = recentlyModifiedLibPath.getPath();
                try {
                    String filename = path.toFile().getAbsolutePath();
                    System.load(filename);
                    System.out.println("The bindings library " + filename + " has been successfully loaded");
                    return;
                } catch (UnsatisfiedLinkError e) {
                    System.out.println("Cannot load " + path);
                    e.printStackTrace();
                }
            }

            StringBuilder sb = new StringBuilder("Cannot load a native library. Canditates are: [");
            String separator = "";
            for (Path path: libsPaths) {
                sb.append(separator);
                sb.append(path.toString());
                separator = ",";
            }
            sb.append(']');
            throw new RuntimeException(sb.toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static class SortedPath implements Comparable<SortedPath> {
        Path path;
        long lastModified = Long.MAX_VALUE;

        SortedPath(Path path) {
            this.path = path;
        }

        Path getPath() {
            return path;
        }

        boolean seemsValid() {
            return getLastModified() > 0;
        }

        private long getLastModified() {
            if (lastModified == Long.MAX_VALUE) {
                lastModified = path.toFile().lastModified();
            }
            return lastModified;
        }

        @Override
        public int compareTo(SortedPath o) {
            long thisLastModified = this.getLastModified();
            long thatLastModified = o.getLastModified();
            // The most recently modified file gets the first order
            return Long.compare(-thisLastModified, -thatLastModified);
        }
    }

    private static class LibFilesCollector implements FileVisitor<Path> {
        public interface FilePathFilter {
            boolean accept(Path filePath);
        }

        List<Path> output;
        FilePathFilter filter;

        public LibFilesCollector(List<Path> output, FilePathFilter filter) {
            this.output = output;
            this.filter = filter;
        }

        @Override
        public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
            if (filter.accept(file))
                output.add(file);
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
            return FileVisitResult.CONTINUE;
        }
    }

    private static final String baseLibraryName = "lsm-bindings-native-impl";

    private static final class FilenameAndExtensionFilter implements LibFilesCollector.FilePathFilter {
        String expectedName;
        boolean ignoreCase;

        public FilenameAndExtensionFilter(String extension, boolean ignoreCase) {
            this.expectedName = baseLibraryName + extension;
            this.ignoreCase = ignoreCase;
        }

        @Override
        public boolean accept(Path filePath) {
            String fileName = filePath.getFileName().toFile().getName();
            if (ignoreCase)
                return expectedName.equals(filePath.getFileName().toFile().getName());

            return expectedName.contentEquals(fileName);
        }
    }

    private static final class DefaultLibFilesFilter implements LibFilesCollector.FilePathFilter {
        private static final String nixName = baseLibraryName + ".so";
        private static final String macName = baseLibraryName + ".dylib";
        private static final String winName = baseLibraryName + ".dll";
        @Override
        public boolean accept(Path filePath) {
            String fileName = filePath.getFileName().toFile().getName();
            if (nixName.equals(fileName))
                return true;
            if (macName.equalsIgnoreCase(fileName))
                return true;
            return winName.equalsIgnoreCase(fileName);
        }
    }

    private static List<Path> findLibsInPathByExtension(Path root, LibFilesCollector.FilePathFilter filter) throws IOException {
        List<Path> result = new ArrayList<>();
        Files.walkFileTree(root, new LibFilesCollector(result, filter));
        return result;
    }

    private static List<Path> findLibsInPathByExtension(Path root, String extension, boolean ignoreCase) throws IOException {
        return findLibsInPathByExtension(root, new FilenameAndExtensionFilter(extension, ignoreCase));
    }

    private static List<Path> findLibsInPath(Path root) throws IOException {
        final List<Path> result = new ArrayList<>();

        String osName = System.getProperty("os.name");
        Pattern nixPattern = Pattern.compile("nix$");
        if (nixPattern.matcher(osName).find())
            return findLibsInPathByExtension(root, ".so", false);

        Pattern macPattern = Pattern.compile("^mac", Pattern.CASE_INSENSITIVE);
        if (macPattern.matcher(osName).find())
            return findLibsInPathByExtension(root, ".dylib", true);

        Pattern winPattern = Pattern.compile("^windows", Pattern.CASE_INSENSITIVE);
        if (winPattern.matcher(osName).find())
            return findLibsInPathByExtension(root,".dll", true);

        return findLibsInPathByExtension(root, new DefaultLibFilesFilter());
    }

    private static Unsafe unsafe = null;

    static Unsafe getUnsafe() {
        if (unsafe != null)
            return unsafe;

        try {
            Field field = Unsafe.class.getDeclaredField("theUnsafe");
            boolean wasAccessible = field.isAccessible();
            field.setAccessible(true);
            unsafe = (Unsafe)field.get(null);
            field.setAccessible(wasAccessible);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        return unsafe;
    }

    public static String getBytesAsAsciiString(long address, int dataSize) {
        if (address == 0)
            throw new AssertionError("Illegal zero address");
        if (dataSize < 0)
            throw new AssertionError("Illegal data size " + dataSize);

        Unsafe unsafe = getUnsafe();
        StringBuilder resultBuilder = new StringBuilder();
        for (int i = 0; i < dataSize; ++i)
            resultBuilder.append((char)unsafe.getByte(address + i));
        return resultBuilder.toString();
    }

    public static void putAsciiStringAsBytes(long address, String data) {
        if (address == 0)
            throw new AssertionError("Illegal zero address");

        Unsafe unsafe = getUnsafe();
        for (int i = 0; i < data.length(); ++i) {
            int c = data.charAt(i);
            if (c > Byte.MAX_VALUE)
                throw new AssertionError("Illegal byte " + c + "at index " + i + " (not in ASCII set)");
            unsafe.putByte(address + i, (byte)c);
        }
    }
}
