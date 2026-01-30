package com.github.skriptdev.skript.api.skript.event;

import com.github.skriptdev.skript.plugin.HySk;
import com.hypixel.hytale.component.ComponentRegistryProxy;
import com.hypixel.hytale.component.system.ISystem;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.syst3ms.skriptparser.lang.event.SkriptEvent;

/**
 * A {@link SkriptEvent} that is handled by a Hytale {@link ISystem}
 *
 * @param <S> System type
 */
public abstract class SystemEvent<S extends ISystem<EntityStore>> extends SkriptEvent {

    protected static final ComponentRegistryProxy<EntityStore> REGISTRY = HySk.getInstance().getEntityStoreRegistry();

    /**
     * Apply a system to this event
     *
     * @param system System to apply
     */
    protected void applySystem(S system) {
        REGISTRY.registerSystem(system);
    }

}
