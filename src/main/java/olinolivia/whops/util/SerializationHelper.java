package olinolivia.whops.util;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec2;

public abstract class SerializationHelper {

    // codecs

    public static final Codec<ResourceKey<Level>> DIMENSION_CODEC = ResourceKey.codec(Registries.DIMENSION);

    // stream codecs

    public static final StreamCodec<RegistryFriendlyByteBuf, Vec2> VEC2_STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.FLOAT, vec2 -> vec2.x,
            ByteBufCodecs.FLOAT, vec2 -> vec2.y,
            Vec2::new
    );

    public static final StreamCodec<ByteBuf, ResourceKey<Level>> DIMENSION_STREAM_CODEC = ResourceKey.streamCodec(Registries.DIMENSION);

}
