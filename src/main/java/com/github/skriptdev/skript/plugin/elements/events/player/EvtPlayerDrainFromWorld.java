package com.github.skriptdev.skript.plugin.elements.events.player;

import com.github.skriptdev.skript.api.skript.event.EventRegistrationEvent;
import com.github.skriptdev.skript.api.skript.registration.SkriptRegistration;
import com.hypixel.hytale.server.core.event.events.player.DrainPlayerFromWorldEvent;
import com.hypixel.hytale.server.core.universe.world.World;
import io.github.syst3ms.skriptparser.lang.Expression;
import io.github.syst3ms.skriptparser.lang.Statement;
import io.github.syst3ms.skriptparser.lang.Trigger;
import io.github.syst3ms.skriptparser.lang.TriggerContext;
import io.github.syst3ms.skriptparser.parsing.ParseContext;
import org.jetbrains.annotations.NotNull;

public class EvtPlayerDrainFromWorld extends EventRegistrationEvent {

    public static void register(SkriptRegistration reg) {
        reg.newEvent(EvtPlayerDrainFromWorld.class,
                "drain player from world", "player drained from world", "player drain from world")
            .name("Player Drain From World")
            .description("Really not sure...") // TODO put real docs
            .since("INSERT VERSION")
            .register();
        reg.addContextValue(DrainContext.class, World.class, true, "world", DrainContext::getWorld);
    }

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, ParseContext parseContext) {
        applyListener(registry -> registry.registerGlobal(DrainPlayerFromWorldEvent.class, event -> {
            DrainContext context = new DrainContext(event);
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
        return "drain player from world";
    }

    private record DrainContext(DrainPlayerFromWorldEvent event) implements TriggerContext {

        public World[] getWorld() {
            return new World[]{this.event.getWorld()};
        }

        @Override
        public String getName() {
            return "drain context";
        }
    }

}
