package brokenkeyboard.brokensenchantoverhaul.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class GenericMobEffect extends MobEffect {

    public GenericMobEffect(int color) {
        super(MobEffectCategory.NEUTRAL, color);
    }
}
