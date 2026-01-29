package com.github.skriptdev.skript.api.skript.event;

import io.github.syst3ms.skriptparser.lang.TriggerContext;

/**
 * Represents a TriggerContext for an event that can be canceled.
 */
public interface CancellableContext extends TriggerContext {

    boolean isCancelled();

    void setCancelled(boolean cancelled);

}
