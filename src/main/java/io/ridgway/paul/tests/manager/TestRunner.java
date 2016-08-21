package io.ridgway.paul.tests.manager;

import org.apache.thrift.transport.TTransportException;
import org.junit.runner.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TestRunner {

    private static final Logger L = LoggerFactory.getLogger(TestRunner.class);

    private static final Pattern CLASS_PATTERN = Pattern.compile("\\.class$", Pattern.CASE_INSENSITIVE);
    private static final Pattern SLASH_PATTERN = Pattern.compile("/", Pattern.LITERAL);

    public void run(final File testJar, final int port)
            throws IOException, URISyntaxException, TTransportException, ClassNotFoundException {
        L.info("Runner, jar: {}, port: {}", testJar.getPath(), port);

        //TODO: Move to method, better condition handling
        //TODO: Use in runner too?
        final Set<URI> classPaths = ClasspathHelper.enumerateClasspath();
        classPaths.forEach(uri -> L.info("Classpath contains: {}", uri));
        classPaths.stream()
                .filter(url -> url.equals(testJar.toURI()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Test jar does not exist on classpath"));

        final Set<Class> classes = extractTestClasses(testJar);
        L.info("Test classes: {}", classes);

        L.info("Running tests...");
        final long startedAt = System.currentTimeMillis();

        final TestManager testManager = new TestManager(port);
        testManager.addTests(classes);

        final Result result = testManager.run();
        final long finishedAt = System.currentTimeMillis();

        L.info("Shutting down manager...");
        testManager.shutdown();

        System.out.println("--------------------------------------------------------");
        System.out.println("Results:");
        System.out.println("--------------------------------------------------------");
        System.out.println(result.getRunCount());
        System.out.println(result.getFailureCount());
        System.out.println(result.getFailures());
        System.out.println(result.wasSuccessful());

        final long duration = finishedAt - startedAt;
        System.out.println("Run Time: " + duration + " ms");
    }

    private static Set<Class> extractTestClasses(final File testJar) throws IOException, ClassNotFoundException {
        final Set<Class> classes = new HashSet<>();
        final JarFile jarFile = new JarFile(testJar);
        final Enumeration<JarEntry> entries = jarFile.entries();
        while (entries.hasMoreElements()) {
            final JarEntry jarEntry = entries.nextElement();
            if (!jarEntry.isDirectory() && jarEntry.getName().endsWith("Test.class")) {
                String className = jarEntry.getName();
                className = CLASS_PATTERN.matcher(className).replaceAll(Matcher.quoteReplacement(""));
                className = SLASH_PATTERN.matcher(className).replaceAll(Matcher.quoteReplacement("."));
                L.info("Getting class reference for: {}", className);
                final Class<?> clazz = Class.forName(className);
                classes.add(clazz);
            }
        }
        return classes;
    }

}


