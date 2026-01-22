package com.shanebeestudios.skript.plugin.elements.types;

import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.asset.type.item.config.Item;
import com.hypixel.hytale.server.core.command.system.CommandSender;
import com.hypixel.hytale.server.core.entity.Entity;
import com.hypixel.hytale.server.core.entity.LivingEntity;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.inventory.Inventory;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.shanebeestudios.skript.api.skript.ItemUtils;
import com.shanebeestudios.skript.api.utils.Utils;
import io.github.syst3ms.skriptparser.registration.SkriptRegistration;
import io.github.syst3ms.skriptparser.types.changers.ChangeMode;
import io.github.syst3ms.skriptparser.types.changers.Changer;
import org.jetbrains.annotations.NotNull;

public class Types {

    public static void register(SkriptRegistration registration) {
        Utils.log("Setting up Types");
        registerServerTypes(registration);
        registerEntityTypes(registration);
        registerItemTypes(registration);
        registerBlockTypes(registration);
    }

    private static void registerServerTypes(SkriptRegistration registration) {
        registration.newType(CommandSender.class, "commandsender", "commandSender@s")
            .toStringFunction(CommandSender::getDisplayName)
            .register();
    }

    private static void registerEntityTypes(SkriptRegistration registration) {
        registration.newType(Entity.class, "entity", "entit@y@ies")
            .toStringFunction(Entity::toString) // TODO get its name or something
            .register();
        registration.newType(LivingEntity.class, "livingentity", "livingEntit@y@ies")
            .toStringFunction(LivingEntity::toString) // TODO get its name or something
            .register();
        registration.newType(Player.class, "player", "player@s")
            .toStringFunction(Player::getDisplayName)
            .register();
    }

    private static void registerItemTypes(SkriptRegistration registration) {
        registration.newType(Item.class, "item", "item@s")
            .literalParser(ItemUtils::parseItem)
            .toStringFunction(Item::getId)
            .register();
        registration.newType(ItemStack.class, "itemstack", "itemstack@s")
            .toStringFunction(ItemStack::getItemId)
            .register();
        registration.newType(Inventory.class, "inventory", "inventor@y@ies")
            .toStringFunction(Inventory::toString)
            .register();
    }

    private static void registerBlockTypes(SkriptRegistration registration) {
        registration.newType(BlockType.class, "blocktype", "blockType@s")
            .toStringFunction(BlockType::getId)
            .register();
    }


}
