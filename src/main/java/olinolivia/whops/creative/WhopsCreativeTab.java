package olinolivia.whops.creative;

import net.fabricmc.fabric.api.creativetab.v1.FabricCreativeModeTab;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import olinolivia.whops.Whops;
import olinolivia.whops.block.WhopsBlocks;
import olinolivia.whops.item.WhopsItems;

@SuppressWarnings("unused")
public abstract class WhopsCreativeTab {

    public static final ResourceKey<CreativeModeTab> TAB_KEY = ResourceKey.create(
            BuiltInRegistries.CREATIVE_MODE_TAB.key(),
            Whops.id("whops")
    );

    public static final CreativeModeTab TAB = FabricCreativeModeTab.builder()
            .icon(() -> new ItemStack(WhopsBlocks.CHECKPOINT_BLOCK.asItem()))
            .title(Component.literal("whops"))
            .displayItems(((params, output) -> {
                output.accept(WhopsBlocks.CHECKPOINT_BLOCK.asItem());
                output.accept(WhopsBlocks.GOAL_BLOCK.asItem());
                output.accept(WhopsBlocks.COMMAND_PAD_BLOCK.asItem());
                output.accept(WhopsItems.POSITION_SNAPSHOT);
            }))
            .build()
    ;

    static {
        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, TAB_KEY, TAB);
    }

    public static void init() {}

}
