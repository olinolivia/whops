package olinolivia.whops.block.blockentity;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import olinolivia.whops.Whops;
import olinolivia.whops.block.WhopsBlocks;

public class WhopsBlockEntities {

    private static <T extends BlockEntity> BlockEntityType<T> register(
            String name,
            FabricBlockEntityTypeBuilder.Factory<? extends T> entityFactory,
            Block... blocks
    ) {
        Identifier id = Whops.id(name);
        return Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, id, FabricBlockEntityTypeBuilder.<T>create(entityFactory, blocks).build());
    }

    public static final BlockEntityType<CheckpointBlockEntity> CHECKPOINT = register("checkpoint", CheckpointBlockEntity::new, WhopsBlocks.CHECKPOINT_BLOCK);

    public static void init() {}

}
