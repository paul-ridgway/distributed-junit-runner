package io.ridgway.paul.tests.manager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashSet;
import java.util.Set;

public class ClasspathHelper {

    private static final Logger L = LoggerFactory.getLogger(ClasspathHelper.class);

    private ClasspathHelper() {
    }

    public static Set<URI> enumerateClasspath() throws IOException, URISyntaxException {
        final Set<URI> set = new HashSet<>();
        dumpClasspath(Thread.currentThread().getContextClassLoader(), set);
        return set;
    }

    private static void dumpClasspath(final ClassLoader loader, final Set<URI> target) throws IOException, URISyntaxException {
        L.info("Scanning: {}", loader);
        if (loader instanceof URLClassLoader) {
            final URLClassLoader ucl = (URLClassLoader) loader;
            for (final URL url : ucl.getURLs()) {
                target.add(url.toURI());
            }

        } else {
            L.warn("Cannot display components as not a URLClassLoader");
        }

        if (loader.getParent() != null) {
            dumpClasspath(loader.getParent(), target);
        }
    }

}
