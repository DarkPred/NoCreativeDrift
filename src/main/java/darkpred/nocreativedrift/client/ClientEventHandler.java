package darkpred.nocreativedrift.client;

import com.blakebr0.ironjetpacks.util.JetpackUtils;
import com.mrcrayfish.controllable.Controllable;
import com.mrcrayfish.controllable.client.ButtonBindings;
import com.mrcrayfish.controllable.client.Controller;
import darkpred.nocreativedrift.NoCreativeDrift;
import darkpred.nocreativedrift.config.ClientConfig;
import me.pieking1215.invmove.InvMove;
import mekanism.common.item.interfaces.IJetpackItem;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayDeque;
import java.util.Deque;

@Mod.EventBusSubscriber(Dist.CLIENT)
public class ClientEventHandler {
    private static final Deque<Drift> DRIFT_QUEUE = new ArrayDeque<>();
    private static boolean keyJumpPressed;
    private static boolean keySneakPressed;
    private static boolean keyToggleDriftPressed;

    protected static float hudOpacity = 5.0f;

    static {
        DRIFT_QUEUE.add(Drift.VANILLA);
        DRIFT_QUEUE.add(Drift.STRONG);
        DRIFT_QUEUE.add(Drift.WEAK);
        DRIFT_QUEUE.add(Drift.DISABLED);
    }

    @SubscribeEvent
    public static void onPlayerTickEvent(PlayerTickEvent event) {
        hudOpacity = Math.max(hudOpacity - 0.05f, 0);
        //Ensures that the code is only run once on the logical client
        if (event.phase == Phase.END && event.side == LogicalSide.CLIENT) {
            // Making sure that the player is creative flying
            if ((ClientConfig.isRuleEnabled(
                    ClientConfig.DISABLE_NON_CREATIVE_DRIFT) || event.player.isCreative()) && event.player.getAbilities().flying) {
                stopDrift(event);
            }
            if (ClientConfig.isRuleEnabled(ClientConfig.DISABLE_JETPACK_DRIFT)) {
                if (isJetpackOn(event.player)) {
                    stopDrift(event);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onKeyInputEvent(InputEvent.Key event) {
        if (keyToggleDriftPressed != KeyBindList.TOGGLE_DRIFT.isDown()) {
            if (!keyToggleDriftPressed) {
                DRIFT_QUEUE.add(DRIFT_QUEUE.pop());
                hudOpacity = 5.0f;
                ClientConfig.DRIFT_STRENGTH.set(DRIFT_QUEUE.peek().ordinal());
            }
            keyToggleDriftPressed = KeyBindList.TOGGLE_DRIFT.isDown();
        }
    }

    private static void stopDrift(PlayerTickEvent event) {
        Vec3 motion = event.player.getDeltaMovement();
        //If no movement keys are pressed slow down player. Seems to work fine with pistons and stuff
        if (!horizontalControlsUsed()) {
            // Sets the players horizontal motion to current * drift multiplier
            event.player.setDeltaMovement(motion.x() * getCurDrift().getMulti(), motion.y(), motion.z() * getCurDrift().getMulti());
        }
        if (!ClientConfig.isRuleEnabled(ClientConfig.DISABLE_VERTICAL_DRIFT)) {
            return;
        }
        if (ClientConfig.isRuleEnabled(ClientConfig.DISABLE_JETPACK_DRIFT) && isJetpackOn(event.player)) {
            return;
        }

        if (keyJumpPressed && !isJumpPressed()) {
            //Multiplier only applied once but that's fine because there is barely any drift anyway
            event.player.setDeltaMovement(motion.x(), motion.y() * getCurDrift().getMulti(), motion.z());
            keyJumpPressed = false;
        } else if (isJumpPressed()) {
            keyJumpPressed = true;
        }
        if (keySneakPressed && !isSneakPressed()) {
            event.player.setDeltaMovement(motion.x(), motion.y() * getCurDrift().getMulti(), motion.z());
            keySneakPressed = false;
        } else if (isSneakPressed()) {
            keySneakPressed = true;
        }
    }

    private static boolean isJumpPressed() {
        boolean pressed = false;
        if (NoCreativeDrift.isControllableLoaded() && ClientConfig.isRuleEnabled(ClientConfig.ENABLE_CONTROLLER_SUPPORT)) {
            pressed = ButtonBindings.JUMP.isButtonDown();
        }

        Options opt = Minecraft.getInstance().options;
        if (isKeyDown(opt.keyJump)) {
            pressed = true;
        }
        return pressed;
    }

    private static boolean isSneakPressed() {
        boolean pressed = false;
        if (NoCreativeDrift.isControllableLoaded() && ClientConfig.isRuleEnabled(ClientConfig.ENABLE_CONTROLLER_SUPPORT)) {
            pressed = ButtonBindings.SNEAK.isButtonDown();
        }
        Options opt = Minecraft.getInstance().options;
        if (isKeyDown(opt.keyShift)) {
            pressed = true;
        }
        return pressed;
    }

    private static boolean horizontalControlsUsed() {
        boolean pressed = false;
        if (NoCreativeDrift.isControllableLoaded() && ClientConfig.isRuleEnabled(ClientConfig.ENABLE_CONTROLLER_SUPPORT)) {
            Controller controller = Controllable.getController();
            if (controller != null) {
                float deadZone = com.mrcrayfish.controllable.Config.CLIENT.options.deadZone.get().floatValue();
                pressed = Math.abs(controller.getLThumbStickXValue()) >= deadZone || Math.abs(controller.getLThumbStickYValue()) >= deadZone;
            }
        }
        Options opt = Minecraft.getInstance().options;
        if (isKeyDown(opt.keyUp) || isKeyDown(opt.keyDown) || isKeyDown(opt.keyLeft) || isKeyDown(opt.keyRight)) {
            pressed = true;
        }
        return pressed;
    }

    private static boolean isKeyDown(KeyMapping key) {
        if (NoCreativeDrift.isInvMoveLoaded()) {
            return InvMove.rawIsKeyDown(key);
        } else {
            return key.isDown();
        }
    }

    public static boolean isJetpackOn(Player player) {
        return isMekanismJetpackOn(player) || isIronJetpackOn(player);
    }

    private static boolean isMekanismJetpackOn(Player player) {
        if (NoCreativeDrift.isMekanismLoaded()) {
            ItemStack itemStack = IJetpackItem.getActiveJetpack(player);
            if (!itemStack.isEmpty()) {
                IJetpackItem.JetpackMode mode = ((IJetpackItem) itemStack.getItem()).getJetpackMode(itemStack);
                return mode == IJetpackItem.JetpackMode.NORMAL || mode == IJetpackItem.JetpackMode.HOVER;
            }
        }
        return false;
    }

    private static boolean isIronJetpackOn(Player player) {
        if (NoCreativeDrift.isIronJetpacksLoaded()) {
            ItemStack itemStack = JetpackUtils.getEquippedJetpack(player);
            if (itemStack.getItem() instanceof com.blakebr0.ironjetpacks.item.JetpackItem) {
                return JetpackUtils.isFlying(player);
            }
        }
        return false;
    }

    public static Drift getCurDrift() {
        Drift ret = DRIFT_QUEUE.peek();
        if (ret == null) {
            ret = Drift.DISABLED;
        }
        return ret;
    }

    public static void setCurDrift(Drift drift) {
        for (int i = 0; i < DRIFT_QUEUE.size(); i++) {
            if (DRIFT_QUEUE.peek() != drift) {
                DRIFT_QUEUE.add(DRIFT_QUEUE.pop());
            }
        }
    }
}
