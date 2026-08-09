package olinolivia.whops.item;

import net.minecraft.core.component.DataComponents;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec2;
import olinolivia.whops.block.WhopsBlocks;
import olinolivia.whops.component.StoredPositionComponent;
import olinolivia.whops.component.WhopsComponents;
import org.jspecify.annotations.NonNull;

public class PositionSnapshotItem extends Item {

    public PositionSnapshotItem(Properties properties) {
        super(properties);
    }

    @Override
    public @NonNull InteractionResult useOn(@NonNull UseOnContext context) {
        Player player = context.getPlayer();
        if (context.getLevel().getBlockState(context.getClickedPos()).is(WhopsBlocks.CHECKPOINT_BLOCK) && (player == null || !player.isCrouching()))
            return InteractionResult.SUCCESS;
        return InteractionResult.PASS;
    }

    @Override
    public @NonNull InteractionResult use(
            @NonNull Level level,
            @NonNull Player player,
            @NonNull InteractionHand hand
    ) {
        ItemStack item = player.getItemInHand(hand);
        item.set(WhopsComponents.STORED_POSITION_COMPONENT_TYPE,
                new StoredPositionComponent(player.position(), new Vec2(player.getXRot(), player.getYRot()))
        );
        item.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true);
        player.playSound(SoundEvents.UI_CARTOGRAPHY_TABLE_TAKE_RESULT);
        return InteractionResult.SUCCESS;
    }

}
