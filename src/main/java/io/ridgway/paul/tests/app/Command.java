package io.ridgway.paul.tests.app;

public abstract class Command<C> {

    private final C commandArguments;

    protected Command(final C commandArguments) {
        this.commandArguments = commandArguments;
    }

    public abstract String getName();

    public C getCommandArguments() {
        return commandArguments;
    }

    public void launch() throws Exception {
        launch(commandArguments);
    }

    public abstract void launch(final C commandArguments) throws Exception;

}

