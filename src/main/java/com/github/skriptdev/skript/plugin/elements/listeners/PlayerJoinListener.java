package com.github.skriptdev.skript.plugin.elements.listeners;

import com.hypixel.hytale.event.EventRegistry;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.event.events.player.PlayerConnectEvent;
import com.hypixel.hytale.server.core.event.events.player.PlayerDisconnectEvent;
import com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent;
import com.github.skriptdev.skript.api.skript.eventcontext.PlayerEventContext;
import io.github.syst3ms.skriptparser.lang.Statement;
import io.github.syst3ms.skriptparser.lang.Trigger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayerJoinListener {

    private final Map<String,List<Trigger>> join = new HashMap<>();
    private final Map<String,List<Trigger>> ready = new HashMap<>();
    private final Map<String,List<Trigger>> quit = new HashMap<>();

    public PlayerJoinListener(EventRegistry registry) {
        registry.register(PlayerConnectEvent.class, this::onConnect);
        registry.registerGlobal(PlayerReadyEvent.class, this::onReady);
        registry.register(PlayerDisconnectEvent.class, this::onQuit);
    }

    public void addTrigger(String script, Trigger trigger, int type) {
        switch (type) {
            case 0 -> this.join.computeIfAbsent(script, k -> new ArrayList<>()).add(trigger);
            case 1 -> this.ready.computeIfAbsent(script, k -> new ArrayList<>()).add(trigger);
            case 2 -> this.quit.computeIfAbsent(script, k -> new ArrayList<>()).add(trigger);
        }
    }

    public void clearTriggers(String script) {
        this.join.clear();
        this.ready.clear();
        this.quit.clear();
    }

    public void onConnect(PlayerConnectEvent event) {
        Player player = event.getHolder().getComponent(Player.getComponentType());
        for (Trigger trigger : this.join.values().stream().flatMap(List::stream).toList()) {
            Statement.runAll(trigger, new PlayerEventContext(player, 0));
        }

    }

    public void onReady(PlayerReadyEvent event) {
        Player player = event.getPlayer();
        for (Trigger trigger : this.ready.values().stream().flatMap(List::stream).toList()) {
            Statement.runAll(trigger, new PlayerEventContext(player, 1));
        }

    }

    public void onQuit(PlayerDisconnectEvent event) {
        // TODO
    }

}
