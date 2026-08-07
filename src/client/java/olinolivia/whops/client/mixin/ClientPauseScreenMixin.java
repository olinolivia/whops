package olinolivia.whops.client.mixin;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import olinolivia.whops.client.gui.CoursesScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PauseScreen.class)
public abstract class ClientPauseScreenMixin extends Screen {

    public ClientPauseScreenMixin() {super(Component.literal(""));}

    @Inject(method = "createPauseMenu", at = @At("RETURN"))
    private void addCoursesButton(CallbackInfo ci) {
        this.addRenderableWidget(
                Button.builder(Component.literal("Courses"), _ -> minecraft.gui.setScreen(new CoursesScreen(this)))
                        .bounds((width - 120) / 2, height - 40, 120, 20).build()
        );
    }

}
