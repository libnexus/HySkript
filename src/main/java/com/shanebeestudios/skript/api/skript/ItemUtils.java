package com.shanebeestudios.skript.api.skript;

import com.hypixel.hytale.assetstore.map.DefaultAssetMap;
import com.hypixel.hytale.server.core.asset.type.item.config.Item;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ItemUtils {

    private static final Map<String, Item> ITEMS = new HashMap<>();

    public static void init() {
        DefaultAssetMap<String, Item> assetMap = Item.getAssetMap();
        assetMap.getAssetMap().forEach((key, value) -> ITEMS.put(key.toLowerCase(Locale.ROOT), value));
    }

    public static Item parseItem(String item) {
        return ITEMS.get(item.toLowerCase(Locale.ROOT).replace(" ", "_"));
    }

}
