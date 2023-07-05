package me.pignol.swift.client.modules.misc;

import me.pignol.swift.api.util.BlockUtil;
import me.pignol.swift.api.util.GLSLSandboxShader;
import me.pignol.swift.api.util.TimerUtil;
import me.pignol.swift.api.value.Value;
import me.pignol.swift.client.event.events.DisconnectEvent;
import me.pignol.swift.client.event.events.Render2DEvent;
import me.pignol.swift.client.event.events.UpdateEvent;
import me.pignol.swift.client.modules.Category;
import me.pignol.swift.client.modules.Module;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

public class Dot5Module extends Module {

    private final Value<Mode> mode = new Value<>("Mode", Mode.JUMPSCARE);
    private final Value<Integer> inHole = new Value<>("InHoleTime", 5000, 0, 10000);

    private final TimerUtil holeTimer = new TimerUtil();

    private GLSLSandboxShader shader;

    private boolean inHoleForTooLong;
    private long time;

    public Dot5Module() {
        super("Dot5", Category.MISC);
        try {
            shader = new GLSLSandboxShader("/shaders/skulls.fsh");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        time = System.currentTimeMillis();
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onUpdate(UpdateEvent event) {
        if (isNull()) {
            return;
        }

        setSuffix(String.format("%.1f", holeTimer.getTimePassed() / 1000F));

        if (!BlockUtil.isSafe(mc.player, 0, true)) {
            holeTimer.reset();
        }

        if (holeTimer.hasReached(inHole.getValue())) {
            inHoleForTooLong = true;
            if (mode.getValue() == Mode.KILL) {
                mc.player.sendChatMessage("/kill");
                holeTimer.reset();
            }
        } else {
            inHoleForTooLong = false;
        }
    }

    @SubscribeEvent
    public void onRender2D(Render2DEvent event) {
        if (inHoleForTooLong) {
            GlStateManager.disableCull();
            shader.useShader(1920, 1080, 0, 0, (System.currentTimeMillis() - time) / 1000F);
            GL11.glBegin(GL11.GL_QUADS);
            GL11.glVertex2f(-1f, -1f);
            GL11.glVertex2f(-1f, 1f);
            GL11.glVertex2f(1f, 1f);
            GL11.glVertex2f(1f, -1f);
            GL11.glEnd();
            GL20.glUseProgram(0);
        }
    }

    @SubscribeEvent
    public void onDisconnect(DisconnectEvent event) {
        setEnabled(false);
    }

    public enum Mode {
        KILL,
        JUMPSCARE
    }

}
