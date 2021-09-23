package artifality.mixin.common;

import artifality.enchantment.ArtifalityEnchantments;
import artifality.item.ArtifalityItems;
import artifality.item.BalloonItem;
import artifality.item.UkuleleItem;
import artifality.item.base.TieredItem;
import artifality.util.TrinketsUtils;
import dev.emi.trinkets.api.TrinketsApi;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {

    PlayerEntity self = (PlayerEntity)(Object)this;

    @Inject(method = "getAttackCooldownProgressPerTick", at = @At("HEAD"), cancellable = true)
    void getAttackCooldownProgressPerTick(CallbackInfoReturnable<Float> cir){
        if(EnchantmentHelper.get(self.getStackInHand(Hand.MAIN_HAND)).containsKey(ArtifalityEnchantments.LUNAR_DAMAGE)){
            int level = EnchantmentHelper.getLevel(ArtifalityEnchantments.LUNAR_DAMAGE, self.getStackInHand(Hand.MAIN_HAND));
            cir.setReturnValue((float)(1.0D / self.getAttributeValue(EntityAttributes.GENERIC_ATTACK_SPEED) * 20.0D) + level + 2);
        }
    }

    @Inject(method = "damage", at = @At("HEAD"))
    void damage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir){
        if(source.getAttacker() != null && source.getAttacker() instanceof LivingEntity attacker){
            if(TrinketsUtils.containsTrinket(self, ArtifalityItems.UKULELE)){
                if(!self.getItemCooldownManager().isCoolingDown(ArtifalityItems.UKULELE)){
                    UkuleleItem.createCloudEffect(attacker.world, attacker,
                            UkuleleItem.NEGATIVE_EFFECTS.get(attacker.world.getRandom().nextInt(UkuleleItem.NEGATIVE_EFFECTS.size())),
                            10, 1.5F, 1);
                    self.getItemCooldownManager().set(ArtifalityItems.UKULELE, 20 * 20);
                }
            }
        }
    }

    @Inject(method = "jump", at = @At("TAIL"))
    void jump(CallbackInfo ci){
        if(!self.world.isClient){
            if(self.getStackInHand(Hand.MAIN_HAND).isOf(ArtifalityItems.BALLOON)||
                    self.getStackInHand(Hand.OFF_HAND).isOf(ArtifalityItems.BALLOON) || BalloonItem.hasBalloonOnHead(self)){
                self.addStatusEffect(new StatusEffectInstance(StatusEffects.LEVITATION, 14, 2, false, false));
            }
        }
    }

    @Inject(method = "tick", at = @At("TAIL"))
    void tick(CallbackInfo ci){
        if(!self.world.isClient){
            if(!self.isOnGround() && !self.isFallFlying() && !self.isTouchingWater() && !self.hasStatusEffect(StatusEffects.LEVITATION)){

                if(BalloonItem.hasBalloonOnHead(self)){
                    TrinketsUtils.getTrinketsAsArray(self).forEach(stack -> {
                        if(stack.isOf(ArtifalityItems.BALLOON)){
                            if(stack.getDamage() != stack.getMaxDamage()){
                                giveSlowFall();
                                if(self.getRandom().nextInt(30 * TieredItem.getCurrentTier(stack)) == 0){
                                    stack.setDamage(stack.getDamage() + 1);
                                }
                            }
                        }
                    });
                }
            }
        }
    }


    @Unique
    void giveSlowFall(){
        if (!self.hasStatusEffect(StatusEffects.SLOW_FALLING)) {
            self.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOW_FALLING, 10, 0, false, false));
        }else if (self.getActiveStatusEffects().get(StatusEffects.SLOW_FALLING).getDuration() == 1) {
            self.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOW_FALLING, 10, 0, false, false));
        }
    }
}
