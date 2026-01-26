package com.github.skriptdev.skript.plugin.elements.listeners;

import com.github.skriptdev.skript.plugin.elements.events.EvtPlayerChat;
import com.github.skriptdev.skript.plugin.elements.events.EvtPlayerChat.PlayerChatEventContext;
import com.hypixel.hytale.event.EventRegistry;
import com.hypixel.hytale.server.core.event.events.player.PlayerChatEvent;
import io.github.syst3ms.skriptparser.lang.Statement;
import io.github.syst3ms.skriptparser.lang.Trigger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayerListener {

    private final Map<String,List<Trigger>> chatTriggers = new HashMap<>();

    public PlayerListener(EventRegistry registry) {
        registry.registerAsyncGlobal(PlayerChatEvent.class, future -> {
            future.thenAccept(event -> {
                PlayerChatEventContext ctx = new PlayerChatEventContext(event.getContent(), event.getSender());
                for (Trigger trigger : this.chatTriggers.values().stream().flatMap(List::stream).toList()) {
                    Statement.runAll(trigger, ctx);
                }
                if (ctx.isMessageChanged()) event.setContent(ctx.getMessage()[0]);
                event.setCancelled(ctx.isCancelled());
            });
            return future;
        });
    }

    public void clearTriggers(String script) {
        if (script == null) {
            this.chatTriggers.clear();
        } else {
            this.chatTriggers.put(script, new ArrayList<>());
        }
    }

    public void handleTrigger(String script, Trigger trigger) {
        if (trigger.getEvent() instanceof EvtPlayerChat) {
            this.chatTriggers.computeIfAbsent(script, k -> new ArrayList<>()).add(trigger);
        }
    }

}
