package com.github.skriptdev.skript.plugin.elements.events.entity;

import com.github.skriptdev.skript.api.skript.event.EventRegistrationEvent;
import com.github.skriptdev.skript.api.skript.registration.SkriptRegistration;
import com.hypixel.hytale.server.core.entity.Entity;
import com.hypixel.hytale.server.core.event.events.entity.LivingEntityInventoryChangeEvent;
import io.github.syst3ms.skriptparser.lang.Expression;
import io.github.syst3ms.skriptparser.lang.Statement;
import io.github.syst3ms.skriptparser.lang.TriggerContext;
import io.github.syst3ms.skriptparser.parsing.ParseContext;

public class EvtLivingEntityInvChange extends EventRegistrationEvent {

    public static void register(SkriptRegistration reg) {
        reg.newEvent(EvtLivingEntityInvChange.class,
                "living entity inventory change", "living entity inventory changed", "living entity inventory change event")
            .name("Living Entity Inventory Change")
            .description("Called when a living entity's inventory changes.")
            .since("INSERT VERSION")
            .setHandledContexts(InvChangeContext.class)
            .register();

        reg.addContextValue(InvChangeContext.class, Entity.class, true, "entity", InvChangeContext::getEntity);
        // TODO more contexts for this event
    }

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, ParseContext parseContext) {
        applyListener(eventRegistry ->
            eventRegistry.registerGlobal(LivingEntityInventoryChangeEvent.class, event -> {
                InvChangeContext ctx = new InvChangeContext(event);
                getTriggers().forEach(trigger -> Statement.runAll(trigger, ctx));
            }));
        return true;
    }

    @Override
    public boolean check(TriggerContext triggerContext) {
        return false;
    }

    @Override
    public String toString(TriggerContext ctx, boolean debug) {
        return "living entity inventory change";
    }

    private static record InvChangeContext(LivingEntityInventoryChangeEvent event) implements TriggerContext {

        private Entity[] getEntity() {
            return new Entity[]{this.event.getEntity()};
        }

        @Override
        public String getName() {
            return "living entity inventory change context";
        }
    }

}
