package olinolivia.whops.item;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Item.Properties;
import olinolivia.whops.Whops;

import java.util.function.Function;

public abstract class WhopsItems {

    public static <T extends Item> T register(String path, Function<Properties, T> itemFactory, Properties props) {
        Identifier id = Whops.id(path);
        return Registry.register(BuiltInRegistries.ITEM, id, itemFactory.apply(props.setId(ResourceKey.create(BuiltInRegistries.ITEM.key(), id))));
    }

    public static final Item POSITION_SNAPSHOT = register("position_snapshot", PositionSnapshotItem::new, new Properties());

    public static void init() {}

}
