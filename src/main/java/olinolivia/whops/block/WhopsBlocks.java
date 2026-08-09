package olinolivia.whops.block;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.references.BlockItemId;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.material.MapColor;
import olinolivia.whops.Whops;

import java.util.function.Function;

public abstract class WhopsBlocks {

    private static <T extends Block> T register(String path, Function<Properties, T> blockFactory, Properties props, Item.Properties itemProps) {
        Identifier id = Whops.id(path);
        BlockItemId blockItemid = BlockItemId.create(id, id);
        T block = Registry.register(BuiltInRegistries.BLOCK, blockItemid.block(), blockFactory.apply(props.setId(blockItemid.block())));
        BlockItem blockItem = new BlockItem(block, itemProps.setId(blockItemid.item()));
        Registry.register(BuiltInRegistries.ITEM, id, blockItem);
        return block;
    }

    public static final CheckpointBlock CHECKPOINT_BLOCK = register(
            "checkpoint",
            CheckpointBlock::new,
            Properties.of()
                    .destroyTime(-1)
                    .mapColor(MapColor.EMERALD)
                    .noCollision(),
            new Item.Properties()
    );

    public static final GoalBlock GOAL_BLOCK = register(
            "goal",
            GoalBlock::new,
            Properties.of()
                    .destroyTime(-1)
                    .mapColor(MapColor.GOLD)
                    .noCollision(),
            new Item.Properties()
    );

    public static void init() {}

}
