package com.github.skriptdev.skript.plugin.elements.events.player;

import com.github.skriptdev.skript.api.skript.event.CancellableContext;
import com.github.skriptdev.skript.api.skript.event.EventRegistrationEvent;
import com.github.skriptdev.skript.api.skript.registration.SkriptRegistration;
import com.hypixel.hytale.server.core.event.events.player.PlayerSetupConnectEvent;
import io.github.syst3ms.skriptparser.lang.Expression;
import io.github.syst3ms.skriptparser.lang.Statement;
import io.github.syst3ms.skriptparser.lang.Trigger;
import io.github.syst3ms.skriptparser.lang.TriggerContext;
import io.github.syst3ms.skriptparser.parsing.ParseContext;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class EvtPlayerSetupConnect extends EventRegistrationEvent {

    public static void register(SkriptRegistration reg) {
        reg.newEvent(EvtPlayerSetupConnect.class, "player setup connect")
            .name("Player Setup Connect")
            .description("Called when a player is connecting to the server.")
            .since("INSERT VERSION")
            .setHandledContexts(PlayerSetupConnectContext.class)
            .register();

        reg.addContextValue(PlayerSetupConnectContext.class, String.class, true, "name", PlayerSetupConnectContext::getUsername);
        reg.addContextValue(PlayerSetupConnectContext.class, UUID.class, true, "uuid", PlayerSetupConnectContext::getUuid);
        reg.addContextValue(PlayerSetupConnectContext.class, String.class, true, "reason", PlayerSetupConnectContext::getReason);
    }

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, ParseContext parseContext) {
        applyListener(registry -> registry.registerGlobal(PlayerSetupConnectEvent.class, event -> {
            PlayerSetupConnectContext context = new PlayerSetupConnectContext(event);
            for (Trigger trigger : this.getTriggers()) {
                Statement.runAll(trigger, context);
            }
        }));
        return true;
    }

    @Override
    public boolean check(TriggerContext triggerContext) {
        return false;
    }

    @Override
    public String toString(@NotNull TriggerContext ctx, boolean debug) {
        return "player setup connect";
    }

    private record PlayerSetupConnectContext(
        PlayerSetupConnectEvent event) implements TriggerContext, CancellableContext {

        public String[] getUsername() {
            return new String[]{this.event.getUsername()};
        }

        public UUID[] getUuid() {
            return new UUID[]{this.event.getUuid()};
        }

        public String[] getReason() {
            return new String[]{this.event.getReason()};
        }

        @Override
        public boolean isCancelled() {
            return this.event.isCancelled();
        }

        @Override
        public void setCancelled(boolean cancelled) {
            this.event.setCancelled(cancelled);
        }

        @Override
        public String getName() {
            return "player setup connect context";
        }
    }

}
