package olinolivia.whops;

import net.fabricmc.api.ModInitializer;

import net.minecraft.resources.Identifier;

import net.minecraft.server.level.ServerLevel;
import olinolivia.whops.checkpoint.WhopsCheckpoint;
import olinolivia.whops.command.CheckpointCommand;
import olinolivia.whops.gamerule.LegacyGameRules;
import olinolivia.whops.networking.ClientboundLegacyGameRulesPayload;
import olinolivia.whops.networking.ServerboundReturnPayload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Whops implements ModInitializer {
	public static final String MOD_ID = "whops";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {

		LegacyGameRules.setAccessor(level -> level instanceof ServerLevel serverLevel ? LegacyGameRules.extract(serverLevel.getServer()) : null);

		ClientboundLegacyGameRulesPayload.init();
		ServerboundReturnPayload.init();
		LegacyGameRules.init();
		WhopsCheckpoint.init();
		CheckpointCommand.init();

	}

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}
}
