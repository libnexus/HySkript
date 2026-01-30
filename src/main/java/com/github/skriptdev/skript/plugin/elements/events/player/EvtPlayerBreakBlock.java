package com.github.skriptdev.skript.plugin.elements.events.player;

import com.github.skriptdev.skript.api.hytale.Block;
import com.github.skriptdev.skript.api.skript.event.CancellableContext;
import com.github.skriptdev.skript.api.skript.event.SystemEvent;
import com.github.skriptdev.skript.api.skript.registration.SkriptRegistration;
import com.github.skriptdev.skript.plugin.HySk;
import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.EntityEventSystem;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.event.events.ecs.BreakBlockEvent;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.syst3ms.skriptparser.lang.Expression;
import io.github.syst3ms.skriptparser.lang.Statement;
import io.github.syst3ms.skriptparser.lang.Trigger;
import io.github.syst3ms.skriptparser.lang.TriggerContext;
import io.github.syst3ms.skriptparser.parsing.ParseContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EvtPlayerBreakBlock extends SystemEvent<EntityEventSystem<EntityStore, BreakBlockEvent>> {

    private static final Map<String, List<Trigger>> TRIGGER_MAP = new HashMap<>();
    private static BlockBreakEventSystem SYSTEM;

    public List<Trigger> getTriggers() {
        return TRIGGER_MAP.values().stream().flatMap(List::stream).toList();
    }

    public void addTrigger(String scriptName, Trigger trigger) {
        if (!TRIGGER_MAP.containsKey(scriptName)) {
            TRIGGER_MAP.put(scriptName, new ArrayList<>());
        }
        TRIGGER_MAP.get(scriptName).add(trigger);
    }

    public void clearTrigger(String scriptName) {
        TRIGGER_MAP.put(scriptName, new ArrayList<>());
    }


    public static void register(SkriptRegistration reg) {
        reg.newEvent(EvtPlayerBreakBlock.class, "player block break", "player break block", "player breaks block")
            .name("Player Block Break")
            .description("Called when a player breaks a block.")
            .since("INSERT VERSION")
            .examples("on player break block:",
                "\tif player doesn't have permission \"le.breaky.break\":",
                "\t\tcancel event")
            .setHandledContexts(BreakBlockEventContext.class)
            .register();

        reg.addContextValue(BreakBlockEventContext.class, Player.class, true, "player", BreakBlockEventContext::getPlayer);
        reg.addContextValue(BreakBlockEventContext.class, Block.class, true, "block", BreakBlockEventContext::getBlock);
        reg.addContextValue(BreakBlockEventContext.class, World.class, true, "world", BreakBlockEventContext::getWorld);
        reg.addContextValue(BreakBlockEventContext.class, BlockType.class, true, "blocktype", BreakBlockEventContext::getBlockType);
        reg.addContextValue(BreakBlockEventContext.class, ItemStack.class, true, "item-in-hand", BreakBlockEventContext::getItemInHand);
    }

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, ParseContext parseContext) {
        if (SYSTEM == null) {
            // Only register once
            SYSTEM = new BlockBreakEventSystem(BreakBlockEvent.class);
            applySystem(SYSTEM);
        }
        return true;
    }

    @Override
    public boolean check(TriggerContext ctx) {
        return ctx instanceof BreakBlockEventContext;
    }

    @Override
    public String toString(@NotNull TriggerContext ctx, boolean debug) {
        return "player block break";
    }

    private record BreakBlockEventContext(BreakBlockEvent event, Player player) implements TriggerContext, CancellableContext {

        private Player[] getPlayer() {
            return new Player[]{this.player};
        }

        private Block[] getBlock() {
            BlockType blockType = event.getBlockType();
            Vector3i targetBlock = event.getTargetBlock();
            World world = this.player.getWorld();
            if (world == null || blockType == BlockType.EMPTY) return null;
            Block block = new Block(world, targetBlock, blockType);
            return new Block[]{block};
        }

        private World[] getWorld() {
            return new World[]{this.player.getWorld()};
        }

        private BlockType[] getBlockType() {
            return new BlockType[]{this.event.getBlockType()};
        }

        private ItemStack[] getItemInHand() {
            return new ItemStack[]{this.event.getItemInHand()};
        }

        @Override
        public String getName() {
            return "break block context";
        }

        @Override
        public boolean isCancelled() {
            return this.event.isCancelled();
        }

        @Override
        public void setCancelled(boolean cancelled) {
            this.event.setCancelled(cancelled);
        }
    }

    private static class BlockBreakEventSystem extends EntityEventSystem<EntityStore, BreakBlockEvent> {

        protected BlockBreakEventSystem(@NotNull Class<BreakBlockEvent> eventType) {
            super(eventType);
        }

        @Override
        public void handle(int i, @NotNull ArchetypeChunk<EntityStore> archetypeChunk, @NotNull Store<EntityStore> store,
                           @NotNull CommandBuffer<EntityStore> commandBuffer, @NotNull BreakBlockEvent event) {

            if (event.getBlockType() == BlockType.EMPTY) return;

            Ref<EntityStore> ref = archetypeChunk.getReferenceTo(i);
            Player player = store.getComponent(ref, Player.getComponentType());
            if (player == null) return;

            BreakBlockEventContext context = new BreakBlockEventContext(event, player);
            for (Trigger trigger : TRIGGER_MAP.values().stream().flatMap(List::stream).toList()) {
                Statement.runAll(trigger, context);
            }
        }

        @Override
        public @Nullable Query<EntityStore> getQuery() {
            return PlayerRef.getComponentType();
        }
    }

}
