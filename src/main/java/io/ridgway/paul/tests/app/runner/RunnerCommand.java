package io.ridgway.paul.tests.app.runner;

import io.ridgway.paul.tests.app.Command;
import io.ridgway.paul.tests.manager.TestRunner;

public class RunnerCommand extends Command<RunnerArgs> {

    public RunnerCommand() {
        super(new RunnerArgs());
    }

    @Override
    public String getName() {
        return "runner";
    }

    @Override
    public void launch(final RunnerArgs commandArguments) throws Exception {
        new TestRunner().run(commandArguments.getTestJarFile(), commandArguments.getPort());
    }

}
