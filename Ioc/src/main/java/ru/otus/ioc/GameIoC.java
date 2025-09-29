package ru.otus.ioc;

import ru.otus.command.Command;

 public class GameIoC implements IoC {

    private Strategy strategy;

    public GameIoC() {
        Strategy defaultStrategy = new DefaultStrategy();
        this.strategy = defaultStrategy;
    }

    @Override
    public <T> T resolve(String key, Object... args) {

        return (T) strategy.resolve(key, args);
    }

    class DefaultStrategy implements Strategy {

        @Override
        public Object resolve(String key, Object... args) {
            if ("IoC.SetupStrategy".equals(key)) {
                return new SetStrategyCommand((Strategy) args[0]);
            } else if ("IoC.Default".equals(key)) {
                return this;
            } else {
                throw new IllegalArgumentException(String.format("Unknown key %s", key));
            }
        }
    }

    class SetStrategyCommand implements Command {

        private final Strategy newStrategy;

        public SetStrategyCommand(Strategy newStrategy) {
            this.newStrategy = newStrategy;
        }

        @Override
        public void execute() {
            GameIoC.this.strategy = newStrategy;
        }
    }
}
