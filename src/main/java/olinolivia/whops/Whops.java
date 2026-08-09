package olinolivia.whops;

import net.fabricmc.api.ModInitializer;

import net.minecraft.resources.Identifier;

import net.minecraft.server.level.ServerLevel;
import olinolivia.whops.block.WhopsBlocks;
import olinolivia.whops.block.blockentity.WhopsBlockEntities;
import olinolivia.whops.command.CourseCommand;
import olinolivia.whops.command.FlagCommand;
import olinolivia.whops.command.argument.FlagArgument;
import olinolivia.whops.component.WhopsComponents;
import olinolivia.whops.course.WhopsCheckpoint;
import olinolivia.whops.command.CheckpointCommand;
import olinolivia.whops.course.WhopsCourse;
import olinolivia.whops.course.WorldCourseData;
import olinolivia.whops.creative.WhopsCreativeTab;
import olinolivia.whops.gamerule.LegacyGameRules;
import olinolivia.whops.item.WhopsItems;
import olinolivia.whops.networking.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Whops implements ModInitializer {
	public static final String MOD_ID = "whops";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {

		LegacyGameRules.setAccessor(level -> level instanceof ServerLevel serverLevel ? LegacyGameRules.extract(serverLevel.getServer()) : null);

		WhopsBlocks.init();
		WhopsBlockEntities.init();
		ClientboundCheckpointFeedbackPayload.init();
		ClientboundLegacyGameRulesPayload.init();
		ClientboundListCoursesPayload.init();
		ClientboundReplaceTimerPayload.init();
		ServerboundPlayCoursePayload.init();
		ServerboundRequestCoursesPayload.init();
		ServerboundReturnPayload.init();
		LegacyGameRules.init();
		WhopsCheckpoint.init();
		WhopsCourse.init();
		WorldCourseData.init();
		WhopsCreativeTab.init();
		FlagArgument.init();
		CheckpointCommand.init();
		CourseCommand.init();
		FlagCommand.init();
		WhopsComponents.init();
		WhopsItems.init();

	}

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}
}
