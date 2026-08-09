package olinolivia.whops.block;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import olinolivia.whops.networking.ClientboundFinishFeedbackPayload;
import org.jspecify.annotations.NonNull;

import static olinolivia.whops.course.CourseHelper.*;

public class GoalBlock extends PadBlock {

    public GoalBlock(Properties props) {
        super(props);
    }

    @Override
    boolean shouldTrigger(@NonNull BlockState state, @NonNull Level level, @NonNull BlockPos pos, ServerPlayer steppingPlayer) {
        return isInCourse(steppingPlayer);
    }

    @Override
    void trigger(@NonNull BlockState state, @NonNull Level level, @NonNull BlockPos pos, ServerPlayer steppingPlayer) {
        setFinished(steppingPlayer, true);
        switchCourses(steppingPlayer, null);
        ServerPlayNetworking.send(steppingPlayer, new ClientboundFinishFeedbackPayload(true));
    }

}
