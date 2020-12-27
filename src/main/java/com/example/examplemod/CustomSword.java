package com.example.examplemod;

import net.minecraft.item.*;

public class CustomSword extends SwordItem {


    public CustomSword() {
        super(ItemTier.DIAMOND, 3, -3.0F, (new Item.Properties()).group(ItemGroup.COMBAT));
    }
}
