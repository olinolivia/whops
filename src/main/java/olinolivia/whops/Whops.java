package olinolivia.whops;

import net.fabricmc.api.ModInitializer;

import net.minecraft.resources.Identifier;

import net.minecraft.server.level.ServerLevel;
import olinolivia.whops.command.CourseCommand;
import olinolivia.whops.course.WhopsCheckpoint;
import olinolivia.whops.command.CheckpointCommand;
import olinolivia.whops.course.WorldCourseData;
import olinolivia.whops.gamerule.LegacyGameRules;
import olinolivia.whops.networking.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Whops implements ModInitializer {
	public static final String MOD_ID = "whops";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {

		LegacyGameRules.setAccessor(level -> level instanceof ServerLevel serverLevel ? LegacyGameRules.extract(serverLevel.getServer()) : null);

		ClientboundLegacyGameRulesPayload.init();
		ClientboundListCoursesPayload.init();
		ServerboundPlayCoursePayload.init();
		ServerboundRequestCoursesPayload.init();
		ServerboundReturnPayload.init();
		LegacyGameRules.init();
		WhopsCheckpoint.init();
		WorldCourseData.init();
		CheckpointCommand.init();
		CourseCommand.init();

	}

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}
}
