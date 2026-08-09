package olinolivia.whops.component;

import net.fabricmc.fabric.api.item.v1.ItemComponentTooltipProviderRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import olinolivia.whops.Whops;

public abstract class WhopsComponents {

    public static final DataComponentType<StoredPositionComponent> STORED_POSITION_COMPONENT_TYPE = Registry.register(
            BuiltInRegistries.DATA_COMPONENT_TYPE,
            Whops.id("stored_position"),
            DataComponentType.<StoredPositionComponent>builder()
                    .networkSynchronized(StoredPositionComponent.STREAM_CODEC)
                    .persistent(StoredPositionComponent.CODEC)
                    .build()
    );

    static {
        ItemComponentTooltipProviderRegistry.addAfter(DataComponents.DAMAGE, STORED_POSITION_COMPONENT_TYPE);
    }

    public static void init() {}

}
