package com.shanebeestudios.skript.plugin.elements.expressions;

import com.hypixel.hytale.server.core.asset.type.item.config.Item;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import io.github.syst3ms.skriptparser.lang.Expression;
import io.github.syst3ms.skriptparser.lang.TriggerContext;
import io.github.syst3ms.skriptparser.parsing.ParseContext;
import io.github.syst3ms.skriptparser.registration.SkriptRegistration;

import java.util.Optional;

public class ExprItemStack implements Expression<ItemStack> {

    public static void register(SkriptRegistration registration) {
        registration.newExpression(ExprItemStack.class, ItemStack.class, false,
                "item[ ]stack of %item%")
            .register();
    }

    private Expression<Item> item;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, ParseContext parseContext) {
        this.item = (Expression<Item>) expressions[0];
        return true;
    }

    @Override
    public ItemStack[] getValues(TriggerContext ctx) {
        Optional<? extends Item> single = this.item.getSingle(ctx);
        if (single.isEmpty()) return null;
        ItemStack itemStack = new ItemStack(single.get().getId());
        return new ItemStack[]{itemStack};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<? extends ItemStack> getReturnType() {
        return ItemStack.class;
    }

    @Override
    public String toString(TriggerContext ctx, boolean debug) {
        return "itemstack of " + this.item.toString(ctx, debug);
    }

}
