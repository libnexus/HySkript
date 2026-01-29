package com.github.skriptdev.skript.plugin.elements.events.player;

import com.github.skriptdev.skript.api.skript.event.EventRegistrationEvent;
import com.github.skriptdev.skript.api.skript.registration.SkriptRegistration;
import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.event.events.player.AddPlayerToWorldEvent;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.syst3ms.skriptparser.lang.Expression;
import io.github.syst3ms.skriptparser.lang.Statement;
import io.github.syst3ms.skriptparser.lang.Trigger;
import io.github.syst3ms.skriptparser.lang.TriggerContext;
import io.github.syst3ms.skriptparser.parsing.ParseContext;

public class EvtPlayerAddToWorld extends EventRegistrationEvent {

    public static void register(SkriptRegistration reg) {
        reg.newEvent(EvtPlayerAddToWorld.class,
                "player added to world", "add player to world")
            .name("Player Add To World")
            .description("Called when a player joins a world.")
            .examples("on player added to world:",
                "\tsend \"Welcome to the world!\" to context-player")
            .since("INSERT VERSION")
            .register();

        reg.addContextValue(AddContext.class, World.class, true, "world", AddContext::getWorld);
        reg.addContextValue(AddContext.class, Player.class, true, "player", AddContext::getPlayer);
    }

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, ParseContext parseContext) {
        applyListener(registry -> registry.registerGlobal(AddPlayerToWorldEvent.class, event -> {
            AddContext context = new AddContext(event);
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
    public String toString(TriggerContext ctx, boolean debug) {
        return "";
    }

    private record AddContext(AddPlayerToWorldEvent event) implements TriggerContext {

        public World[] getWorld() {
            return new World[]{this.event.getWorld()};
        }

        public Player[] getPlayer() {
            Holder<EntityStore> holder = this.event.getHolder();
            Player component = holder.getComponent(Player.getComponentType());
            return new Player[]{component};
        }

        @Override
        public String getName() {
            return "add player to world context";
        }
    }

}
