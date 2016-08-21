package io.ridgway.paul.tests.app.worker;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

@Parameters()
public class WorkerArgs {

    private static final int DEFAULT_PORT = 10024;

    @Parameter(names = {"-h", "--host"}, description = "Runner host", required = true)
    private String runnerHost;

    @Parameter(names = {"-p", "--port"}, description = "Server port")
    private int port = DEFAULT_PORT;

    public String getRunnerHost() {
        return runnerHost;
    }

    public int getPort() {
        return port;
    }


}