package com.github.skriptdev.skript.plugin.elements.events.entity;

import com.github.skriptdev.skript.api.skript.event.EventRegistrationEvent;
import com.github.skriptdev.skript.api.skript.registration.SkriptRegistration;
import com.hypixel.hytale.server.core.entity.Entity;
import com.hypixel.hytale.server.core.event.events.entity.EntityRemoveEvent;
import com.hypixel.hytale.server.core.universe.world.World;
import io.github.syst3ms.skriptparser.lang.Expression;
import io.github.syst3ms.skriptparser.lang.Statement;
import io.github.syst3ms.skriptparser.lang.Trigger;
import io.github.syst3ms.skriptparser.lang.TriggerContext;
import io.github.syst3ms.skriptparser.parsing.ParseContext;

public class EvtEntityRemove extends EventRegistrationEvent {

    public static void register(SkriptRegistration reg) {
        reg.newEvent(EvtEntityRemove.class,
                "entity remove", "entity removed", "entity removal")
            .name("Entity Remove")
            .description("Called when an entity is removed from the world.")
            .since("INSERT VERSION")
            .setHandledContexts(EntityRemoveEventContext.class)
            .register();
        reg.addContextValue(EntityRemoveEventContext.class, Entity.class, true, "entity", EntityRemoveEventContext::getEntity);
        reg.addContextValue(EntityRemoveEventContext.class, World.class, true, "world", EntityRemoveEventContext::getWorld);
    }

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, ParseContext parseContext) {
        applyListener(registry -> {
            return registry.registerGlobal(EntityRemoveEvent.class, event -> {
                EntityRemoveEventContext context = new EntityRemoveEventContext(event);
                for (Trigger trigger : this.getTriggers()) {
                    Statement.runAll(trigger, context);
                }
            });
        });
        return true;
    }

    @Override
    public boolean check(TriggerContext triggerContext) {
        return triggerContext instanceof EntityRemoveEventContext;
    }

    @Override
    public String toString(TriggerContext ctx, boolean debug) {
        return "entity remove";
    }

    private record EntityRemoveEventContext(EntityRemoveEvent event) implements TriggerContext {
        @Override
        public String getName() {
            return "entity remove context";
        }

        private Entity[] getEntity() {
            return new Entity[]{this.event.getEntity()};
        }

        private World[] getWorld() {
            return new World[]{this.event.getEntity().getWorld()};
        }
    }

}
