package olinolivia.whops.util;

import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;

import java.util.HashMap;
import java.util.Map;

public abstract class EffectHelper {

    public static Map<Holder<MobEffect>, MobEffectInstance> copyEffectMap(Map<Holder<MobEffect>, MobEffectInstance> effects) {
        HashMap<Holder<MobEffect>, MobEffectInstance> r = new HashMap<>();
        for (Holder<MobEffect> effect : effects.keySet()) r.put(effect, new MobEffectInstance(effects.get(effect)));
        return r;
    }

    public static void applyEffectMap(Map<Holder<MobEffect>, MobEffectInstance> effects, Player player) {
        player.removeAllEffects();
        for (Holder<MobEffect> effect : effects.keySet()) player.addEffect(new MobEffectInstance(effects.get(effect)));
    }

}
