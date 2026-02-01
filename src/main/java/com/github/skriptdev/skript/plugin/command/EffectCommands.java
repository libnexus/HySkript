package com.github.skriptdev.skript.plugin.command;

import com.github.skriptdev.skript.api.utils.Utils;
import com.github.skriptdev.skript.plugin.Skript;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.event.events.player.PlayerChatEvent;
import com.hypixel.hytale.server.core.permissions.PermissionsModule;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.syst3ms.skriptparser.lang.Effect;
import io.github.syst3ms.skriptparser.lang.TriggerContext;
import io.github.syst3ms.skriptparser.log.LogEntry;
import io.github.syst3ms.skriptparser.log.SkriptLogger;
import io.github.syst3ms.skriptparser.parsing.ParserState;
import io.github.syst3ms.skriptparser.parsing.SyntaxParser;
import io.github.syst3ms.skriptparser.registration.context.ContextValue.Usage;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public class EffectCommands {

    public static void register(Skript skript, String token, boolean allowOps, String permission) {
        skript.getSkriptRegistration().newContextValue(PlayerEffectContext.class, Player.class, true, "player", PlayerEffectContext::getPlayer)
            .setUsage(Usage.EXPRESSION_OR_ALONE)
            .register();
        skript.getSkriptRegistration().newContextValue(PlayerEffectContext.class, Player.class, true, "me", PlayerEffectContext::getPlayer)
            .setUsage(Usage.EXPRESSION_OR_ALONE)
            .register();

        skript.getPlugin().getEventRegistry().registerGlobal(PlayerChatEvent.class, event -> {
            if (event.getContent().startsWith(token)) {
                PlayerRef sender = event.getSender();

                // PERM CHECK
                PermissionsModule perm = PermissionsModule.get();
                Set<String> groupsForUser = perm.getGroupsForUser(sender.getUuid());
                if (!allowOps) {
                    if (groupsForUser.contains("OP") && !perm.hasPermission(sender.getUuid(), permission)) {
                        return;
                    }
                } else if (!perm.hasPermission(sender.getUuid(), permission)) {
                    return;
                }

                event.setCancelled(true);

                ParserState parserState = new ParserState();
                parserState.setCurrentContexts(Set.of(PlayerEffectContext.class));
                SkriptLogger skriptLogger = new SkriptLogger(true);

                String effectString = event.getContent().substring(1);
                Optional<? extends Effect> optionalEffect = SyntaxParser.parseEffect(effectString, parserState, skriptLogger);

                if (optionalEffect.isEmpty()) {
                    skriptLogger.finalizeLogs();
                    for (LogEntry logEntry : skriptLogger.close()) {
                        Utils.log(sender, logEntry);
                    }
                    return;
                }

                Effect effect = optionalEffect.get();
                Ref<EntityStore> reference = sender.getReference();
                if (reference == null) return;

                UUID worldUuid = sender.getWorldUuid();
                if (worldUuid == null) return;

                World world = Universe.get().getWorld(worldUuid);
                if (world == null) return;

                skriptLogger.info("Executing: '" + effectString + "'");
                skriptLogger.finalizeLogs();
                for (LogEntry logEntry : skriptLogger.close()) {
                    Utils.log(sender, logEntry);
                }

                if (world.isInThread()) {
                    Player player = world.getEntityStore().getStore().getComponent(reference, Player.getComponentType());
                    effect.walk(new PlayerEffectContext(player));
                } else {
                    world.execute(() -> {
                        Player player = world.getEntityStore().getStore().getComponent(reference, Player.getComponentType());
                        effect.walk(new PlayerEffectContext(player));
                    });
                }

            }
        });
    }

    private record PlayerEffectContext(Player player) implements TriggerContext {

        public Player[] getPlayer() {
            return new Player[]{this.player};
        }

        @Override
        public String getName() {
            return "player effect context";
        }
    }

}
