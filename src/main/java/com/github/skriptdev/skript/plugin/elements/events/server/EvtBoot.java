package com.github.skriptdev.skript.plugin.elements.events.server;

import com.github.skriptdev.skript.api.skript.event.EventRegistrationEvent;
import com.github.skriptdev.skript.api.skript.registration.SkriptRegistration;
import com.hypixel.hytale.server.core.event.events.BootEvent;
import io.github.syst3ms.skriptparser.lang.Expression;
import io.github.syst3ms.skriptparser.lang.Statement;
import io.github.syst3ms.skriptparser.lang.Trigger;
import io.github.syst3ms.skriptparser.lang.TriggerContext;
import io.github.syst3ms.skriptparser.parsing.ParseContext;
import org.jetbrains.annotations.NotNull;

public class EvtBoot extends EventRegistrationEvent {

    public static void register(SkriptRegistration reg) {
        reg.newEvent(EvtBoot.class, "boot", "server boot")
            .name("Server Boot")
            .description("Called when the server is starting up.")
            .since("INSERT VERSION")
            .register();
    }

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, ParseContext parseContext) {
        applyListener(registry -> registry.registerGlobal(BootEvent.class, event -> {
            BootContext bootContext = new BootContext(event);
            for (Trigger trigger : this.getTriggers()) {
                Statement.runAll(trigger, bootContext);
            }
        }));
        return true;
    }

    @Override
    public boolean check(TriggerContext triggerContext) {
        return triggerContext instanceof BootContext;
    }

    @Override
    public String toString(@NotNull TriggerContext ctx, boolean debug) {
        return "server boot";
    }

    private record BootContext(BootEvent event) implements TriggerContext {
        @Override
        public String getName() {
            return "boot context";
        }
    }

}
