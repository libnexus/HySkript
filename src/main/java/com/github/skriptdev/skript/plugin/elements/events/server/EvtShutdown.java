package com.github.skriptdev.skript.plugin.elements.events.server;

import com.github.skriptdev.skript.api.skript.event.EventRegistrationEvent;
import com.github.skriptdev.skript.api.skript.registration.SkriptRegistration;
import com.hypixel.hytale.server.core.event.events.ShutdownEvent;
import io.github.syst3ms.skriptparser.lang.Expression;
import io.github.syst3ms.skriptparser.lang.Statement;
import io.github.syst3ms.skriptparser.lang.Trigger;
import io.github.syst3ms.skriptparser.lang.TriggerContext;
import io.github.syst3ms.skriptparser.parsing.ParseContext;

public class EvtShutdown extends EventRegistrationEvent {

    public static void register(SkriptRegistration reg) {
        reg.newEvent(EvtShutdown.class, "shutdown", "server shutdown")
            .name("Server Shutdown")
            .description("Called when the server is shutting down.")
            .since("INSERT VERSION")
            .register();
    }

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, ParseContext parseContext) {
        applyListener(registry -> registry.registerGlobal(ShutdownEvent.class, event -> {
            ShutdownContext shutdownContext = new ShutdownContext(event);
            for (Trigger trigger : this.getTriggers()) {
                Statement.runAll(trigger, shutdownContext);
            }
        }));
        return true;
    }

    @Override
    public boolean check(TriggerContext triggerContext) {
        return false;
    }

    @Override
    public String toString(TriggerContext ctx, boolean debug) {
        return "server shutdown";
    }

    private record ShutdownContext(ShutdownEvent event) implements TriggerContext {
        @Override
        public String getName() {
            return "shutdown context";
        }
    }

}
