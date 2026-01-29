package com.github.skriptdev.skript.api.skript.event;

import com.github.skriptdev.skript.plugin.HySk;
import com.hypixel.hytale.event.EventRegistration;
import com.hypixel.hytale.event.EventRegistry;
import io.github.syst3ms.skriptparser.lang.event.SkriptEvent;

import java.util.function.Function;

/**
 * A base {@link SkriptEvent} for holding event registrations.
 */
public abstract class EventRegistrationEvent extends SkriptEvent {

    private EventRegistration<?, ?> listener;

    /**
     * Apply a listener to this registration
     *
     * @param registrationFunction Listener to apply
     */
    protected void applyListener(Function<EventRegistry, EventRegistration<?, ?>> registrationFunction) {
        if (this.listener != null) return;
        this.listener = registrationFunction.apply(HySk.getInstance().getEventRegistry());
    }

    @Override
    public void clearTrigger(String scriptName) {
        super.clearTrigger(scriptName);
        if (this.getTriggers().isEmpty()) {
            this.listener.unregister();
        }
    }

}
