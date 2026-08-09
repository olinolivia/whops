package olinolivia.whops.component;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipProvider;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import olinolivia.whops.util.SerializationHelper;
import org.jspecify.annotations.NonNull;

import java.util.function.Consumer;

public record StoredPositionComponent(Vec3 pos, Vec2 rot) implements TooltipProvider {

    @Override
    public void addToTooltip(Item.@NonNull TooltipContext context, Consumer<Component> consumer, @NonNull TooltipFlag flag, @NonNull DataComponentGetter components) {
        String text = String.format("[%.3f %.3f %.3f] [%.1f %.1f]", pos.x, pos.y, pos.z, rot.y, rot.x);
        int color = 0xaaaaaa;
        consumer.accept(Component.literal(text).withColor(color));
    }

    public static final Codec<StoredPositionComponent> CODEC = Codec.mapPair(Vec3.CODEC.fieldOf("pos"), Vec2.CODEC.fieldOf("rot")).xmap(
            pair -> new StoredPositionComponent(pair.getFirst(), pair.getSecond()),
            component -> new Pair<>(component.pos, component.rot)
    ).codec();

    public static final StreamCodec<RegistryFriendlyByteBuf, StoredPositionComponent> STREAM_CODEC = StreamCodec.composite(
            Vec3.STREAM_CODEC, StoredPositionComponent::pos,
            SerializationHelper.VEC2_STREAM_CODEC, StoredPositionComponent::rot,
            StoredPositionComponent::new
    );

}
