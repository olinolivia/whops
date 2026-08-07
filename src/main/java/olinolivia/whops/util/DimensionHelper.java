package olinolivia.whops.util;

import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

public abstract class DimensionHelper {

    public static ResourceKey<Level> getDimension(ServerPlayer player) {
        return getDimension(player.level());
    }

    public static ResourceKey<Level> getDimension(ServerLevel level) {
        return level.dimension();
    }

}
