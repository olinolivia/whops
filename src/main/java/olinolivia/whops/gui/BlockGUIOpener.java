package olinolivia.whops.gui;

import olinolivia.whops.block.blockentity.CommandPadBlockEntity;

import java.util.function.Consumer;

public abstract class BlockGUIOpener {

    private static Consumer<CommandPadBlockEntity> commandPadGUIOpener = null;

    public static void setCommandPadGUIOpener(Consumer<CommandPadBlockEntity> opener) {
        commandPadGUIOpener = opener;
    }

    public static void openCommandPadGUI(CommandPadBlockEntity blockEntity) {
        commandPadGUIOpener.accept(blockEntity);
    }

}
