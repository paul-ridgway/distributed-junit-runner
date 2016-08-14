package io.ridgway.paul.tests.manager;

import io.ridgway.paul.tests.executor.TestExecutor;
import org.apache.thrift.transport.TTransportException;
import org.junit.runner.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TestRunner {

    private static final Logger L = LoggerFactory.getLogger(TestRunner.class);

    private static final Pattern CLASS_PATTERN = Pattern.compile("\\.class$", Pattern.CASE_INSENSITIVE);
    private static final Pattern SLASH_PATTERN = Pattern.compile("/", Pattern.LITERAL);

    public static void main(final String[] args) throws IOException, URISyntaxException, ClassNotFoundException, TTransportException {

        final String testJarPath = "/home/paul/Documents/Code/java-test-set/target/java-test-set-1.0-SNAPSHOT-tests.jar";
        final File testJar = new File(testJarPath);

        //TODO: Move to method, better condition handling
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

        final TestManager testManager = new TestManager();
        final List<TestExecutor> executors = new ArrayList<>();
        executors.add(new TestExecutor());
        executors.forEach(TestExecutor::start);

        testManager.addTests(classes);

        final Result result = testManager.run();
        final long finishedAt = System.currentTimeMillis();

        L.info("Shutting down executors...");
        executors.forEach(TestExecutor::shutdown);

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

    //        core.addListener(new JUnitResultFormatterAsRunListener(new XMLJUnitResultFormatter()) {
//            @Override
//            public void testStarted(final Description description) throws Exception {
//                System.err.println( description.getDisplayName());
////                formatter.setOutput(System.out);
////                formatter.setOutput(new FileOutputStream(new File(reportDir, "TEST-" + description.getDisplayName() + ".xml")));
//                super.testStarted(description);
//            }
//        });

}


