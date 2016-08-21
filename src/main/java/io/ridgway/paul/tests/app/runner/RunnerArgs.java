package io.ridgway.paul.tests.app.runner;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.google.common.base.Preconditions;

import java.io.File;

@Parameters()
public class RunnerArgs {

    private static final int DEFAULT_PORT = 10024;

    @Parameter(names = {"-j", "--jar"}, description = "Test JAR Path", required = true)
    private String testJarFile;

    @Parameter(names = {"-p", "--port"}, description = "Server port")
    private int port = DEFAULT_PORT;

    public File getTestJarFile() {
        return asExistingFile(testJarFile);
    }

    public int getPort() {
        return port;
    }

    private File asExistingFile(final String path) {
        final File folder = new File(path);
        Preconditions.checkArgument(folder.exists(), "Folder does not exist: " + path);
        Preconditions.checkArgument(folder.isFile(), "Path is not a file: " + path);
        return folder;
    }

}