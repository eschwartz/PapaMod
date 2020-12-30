package com.example.examplemod;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;

public class BlasterShot extends Item {
    public BlasterShot() {
        super(new Item.Properties().maxStackSize(64).group(ItemGroup.COMBAT));
    }
}
