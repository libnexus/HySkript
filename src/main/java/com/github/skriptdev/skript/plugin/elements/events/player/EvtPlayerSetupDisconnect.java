package com.github.skriptdev.skript.plugin.elements.events.player;

import com.github.skriptdev.skript.api.skript.event.EventRegistrationEvent;
import com.github.skriptdev.skript.api.skript.registration.SkriptRegistration;
import com.hypixel.hytale.server.core.event.events.player.PlayerSetupDisconnectEvent;
import io.github.syst3ms.skriptparser.lang.Expression;
import io.github.syst3ms.skriptparser.lang.Statement;
import io.github.syst3ms.skriptparser.lang.Trigger;
import io.github.syst3ms.skriptparser.lang.TriggerContext;
import io.github.syst3ms.skriptparser.parsing.ParseContext;

import java.util.UUID;

public class EvtPlayerSetupDisconnect extends EventRegistrationEvent {

    public static void register(SkriptRegistration reg) {
        reg.newEvent(EvtPlayerSetupDisconnect.class, "player setup disconnect")
            .name("Player Setup Disconnect")
            .description("Called when a player is disconnecting from the server.")
            .since("INSERT VERSION")
            .setHandledContexts(PlayerSetupDisconnectContext.class)
            .register();

        reg.addContextValue(PlayerSetupDisconnectContext.class, String.class, true, "name", PlayerSetupDisconnectContext::getUsername);
        reg.addContextValue(PlayerSetupDisconnectContext.class, UUID.class, true, "uuid", PlayerSetupDisconnectContext::getUuid);
        reg.addContextValue(PlayerSetupDisconnectContext.class, String.class, true, "reason", PlayerSetupDisconnectContext::getDisconnectReason);
    }

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, ParseContext parseContext) {
        applyListener(registry -> registry.registerGlobal(PlayerSetupDisconnectEvent.class, event -> {
            PlayerSetupDisconnectContext context = new PlayerSetupDisconnectContext(event);
            for (Trigger trigger : this.getTriggers()) {
                Statement.runAll(trigger, context);
            }
        }));
        return true;
    }

    @Override
    public boolean check(TriggerContext triggerContext) {
        return triggerContext instanceof PlayerSetupDisconnectContext;
    }

    @Override
    public String toString(TriggerContext ctx, boolean debug) {
        return "player setup disconnects";
    }

    private record PlayerSetupDisconnectContext(PlayerSetupDisconnectEvent event) implements TriggerContext {

        public String[] getUsername() {
            return new String[]{this.event.getUsername()};
        }

        public UUID[] getUuid() {
            return new UUID[]{this.event.getUuid()};
        }

        public String[] getDisconnectReason() {
            return new String[]{this.event.getDisconnectReason().getServerDisconnectReason()};
        }

        @Override
        public String getName() {
            return "player setup disconnect context";
        }
    }

}
