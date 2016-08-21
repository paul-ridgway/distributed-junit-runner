package io.ridgway.paul.tests.app.worker;

import io.ridgway.paul.tests.app.Command;
import io.ridgway.paul.tests.worker.TestWorker;

public class WorkerCommand extends Command<WorkerArgs> {

    public WorkerCommand() {
        super(new WorkerArgs());
    }

    @Override
    public String getName() {
        return "worker";
    }

    @Override
    public void launch(final WorkerArgs commandArguments) throws Exception {
        new TestWorker().run(commandArguments.getRunnerHost(), commandArguments.getPort());
    }

}
