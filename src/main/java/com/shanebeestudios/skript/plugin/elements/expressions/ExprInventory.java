package com.shanebeestudios.skript.plugin.elements.expressions;

import com.hypixel.hytale.server.core.entity.LivingEntity;
import com.hypixel.hytale.server.core.inventory.Inventory;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import io.github.syst3ms.skriptparser.lang.Expression;
import io.github.syst3ms.skriptparser.lang.TriggerContext;
import io.github.syst3ms.skriptparser.parsing.ParseContext;
import io.github.syst3ms.skriptparser.registration.SkriptRegistration;
import io.github.syst3ms.skriptparser.types.changers.ChangeMode;

import java.util.Optional;

public class ExprInventory implements Expression<Inventory> {

    public static void register(SkriptRegistration registration) {
        registration.addExpression(ExprInventory.class, Inventory.class, false,
            "inventory of %livingentity%");
    }

    private Expression<LivingEntity> entity;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, ParseContext parseContext) {
        this.entity = (Expression<LivingEntity>) expressions[0];
        return true;
    }

    @Override
    public Inventory[] getValues(TriggerContext ctx) {
        Optional<? extends LivingEntity> single = this.entity.getSingle(ctx);
        if (single.isEmpty()) return null;
        LivingEntity livingEntity = single.get();
        return new Inventory[]{livingEntity.getInventory()};
    }

    @Override
    public Optional<Class<?>[]> acceptsChange(ChangeMode mode) {
        if (mode == ChangeMode.ADD) return Optional.of(new Class<?>[]{ItemStack.class});
        return Optional.empty();
    }

    @Override
    public void change(TriggerContext ctx, ChangeMode changeMode, Object[] changeWith) {
        Inventory[] toChange = getValues(ctx);
        if (changeMode == ChangeMode.ADD) {
            for (Inventory inventory : toChange) {
                for (Object o : changeWith) {
                    if (o instanceof ItemStack itemStack) {
                        inventory.getCombinedEverything().addItemStack(itemStack);
                    }
                }
            }
        }
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public String toString(TriggerContext ctx, boolean debug) {
        return "inventory of " + this.entity.toString(ctx, debug);
    }

}
