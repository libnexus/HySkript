package com.github.skriptdev.skript.plugin.elements.listeners;

import com.github.skriptdev.skript.api.skript.eventcontext.ScriptLoadContext;
import com.github.skriptdev.skript.plugin.Skript;
import com.github.skriptdev.skript.plugin.elements.events.EvtLoad;
import com.github.skriptdev.skript.plugin.elements.events.EvtPlayerJoin;
import com.hypixel.hytale.event.EventRegistry;
import io.github.syst3ms.skriptparser.event.EvtPeriodical;
import io.github.syst3ms.skriptparser.event.PeriodicalContext;
import io.github.syst3ms.skriptparser.lang.SkriptEvent;
import io.github.syst3ms.skriptparser.lang.Statement;
import io.github.syst3ms.skriptparser.lang.Trigger;
import io.github.syst3ms.skriptparser.util.ThreadUtils;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListenerHandler {

    private final Skript skript;
    private final PlayerJoinListener playerJoinListener;
    private final PlayerListener playerListener;
    private final Map<String, List<Trigger>> onLoadTriggers = new HashMap<>();
    private final Map<String, List<Trigger>> periodicalTriggers = new HashMap<>();

    public ListenerHandler(Skript skript) {
        this.skript = skript;
        EventRegistry eventRegistry = skript.getPlugin().getEventRegistry();
        this.playerListener = new PlayerListener(eventRegistry);
        this.playerJoinListener = new PlayerJoinListener(eventRegistry);
    }

    public void handleTrigger(String script, Trigger trigger) {
        SkriptEvent event = trigger.getEvent();

        if (!this.skript.canHandleEvent(event))
            return;

        switch (event) {
            case EvtLoad ignored -> this.onLoadTriggers.computeIfAbsent(script, k -> new ArrayList<>()).add(trigger);
            case EvtPeriodical ignored ->
                this.periodicalTriggers.computeIfAbsent(script, k -> new ArrayList<>()).add(trigger);
            case EvtPlayerJoin evtPlayerJoin ->
                this.playerJoinListener.addTrigger(script, trigger, evtPlayerJoin.getPattern());
            default -> this.playerListener.handleTrigger(script, trigger);
        }
    }

    public void finishedLoading() {
        for (Trigger trigger : this.onLoadTriggers.values().stream().flatMap(List::stream).toList()) {
            Statement.runAll(trigger, new ScriptLoadContext());
        }
        for (Trigger trigger : this.periodicalTriggers.values().stream().flatMap(List::stream).toList()) {
            PeriodicalContext ctx = new PeriodicalContext();
            Duration dur = ((EvtPeriodical) trigger.getEvent()).getDuration().getSingle(ctx).orElseThrow(AssertionError::new);
            ThreadUtils.runPeriodically(() -> Statement.runAll(trigger, ctx), dur);
        }
    }

    public void clearTriggers(@Nullable String script) {
        this.playerJoinListener.clearTriggers(script);
        this.playerListener.clearTriggers(script);
        if (script == null) {
            this.onLoadTriggers.clear();
            this.periodicalTriggers.clear();
        } else {
            this.onLoadTriggers.put(script, new ArrayList<>());
            this.periodicalTriggers.put(script, new ArrayList<>());
        }
    }

}
