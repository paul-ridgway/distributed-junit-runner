package io.ridgway.paul.tests.app;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import io.ridgway.paul.tests.app.runner.RunnerCommand;
import io.ridgway.paul.tests.app.worker.WorkerCommand;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestApp {

    public static void main(final String[] args) throws Exception {
        final TestApp testApp = new TestApp();
        testApp.run(args);
    }

    private final JCommander jCommander;
    private final Map<String, Command> commandMap = new HashMap<>();

    public TestApp() {
        final List<Command> commands = new ArrayList<>();
        commands.add(new RunnerCommand());
        commands.add(new WorkerCommand());
        this.jCommander = new JCommander(this);
        for (final Command command : commands) {
            jCommander.addCommand(command.getName(), command.getCommandArguments());
            commandMap.put(command.getName(), command);
        }
    }

    private void run(final String[] args) throws Exception {
        try {
            jCommander.parse(args);
        } catch (final ParameterException e) {
            usage(e.getMessage() + ". ");
            System.exit(-1);
            return;
        }

        if (jCommander.getParsedCommand() == null) {
            usage("Command not specified.");
            System.exit(-1);
            return;
        }

        final String commandName = jCommander.getParsedCommand();
        System.out.println("Command: " + commandName);
        final Command command = commandMap.get(commandName);
        if (command == null) {
            System.err.println("Unsupported function: " + commandName);
            System.exit(-1);
            return;
        }
        command.launch();
    }

    private void usage(final String message) {
        System.err.println(message + " Usage: ");
        final StringBuilder sb = new StringBuilder();
        jCommander.usage(sb);
        System.err.println(sb);
    }

}
