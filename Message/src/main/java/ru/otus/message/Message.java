package ru.otus.message;

public class Message {
    private final String gameId;

    private final String objectId;

    private final String operationId;
    private final Object[] args;

    public Message(String gameId, String objectId, String operationId, Object[] args) {
        this.gameId = gameId;
        this.objectId = objectId;
        this.operationId = operationId;
        this.args = args;
    }

    public String getGameId() {
        return gameId;
    }

    public String getObjectId() {
        return objectId;
    }

    public String getOperationId() {
        return operationId;
    }

    public Object[] getArgs() {
        return args;
    }

    public static class Builder {
        private String gameId;
        private String objectId;
        private String operationId;
        private Object[] args;

        public Builder setGameId(String gameId) {
            this.gameId = gameId;
            return this;
        }

        public Builder setObjectId(String objectId) {
            this.objectId = objectId;
            return this;
        }

        public Builder setOperationId(String operationId) {
            this.operationId = operationId;
            return this;
        }

        public Builder setArgs(Object[] args) {
            this.args = args;
            return this;
        }

        public Message build() {
            return new Message(gameId, objectId, operationId, args);
        }
    }
}
