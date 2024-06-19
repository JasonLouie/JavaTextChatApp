package com.chat.server.handlers;

import com.chat.database.DatabaseAccessor;

/**
 * Abstract base class for message handlers.
 */
public abstract class AbstractMessageHandler implements MessageHandlerStrategy {
    protected final DatabaseAccessor databaseAccessor;

    public AbstractMessageHandler(DatabaseAccessor databaseAccessor) {
        this.databaseAccessor = databaseAccessor;
    }
}