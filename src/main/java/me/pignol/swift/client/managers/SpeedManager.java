package me.pignol.swift.client.managers;

import me.pignol.swift.api.interfaces.Globals;
import net.minecraft.util.math.MathHelper;

public class SpeedManager implements Globals {

    private static final SpeedManager INSTANCE = new SpeedManager();

    public static SpeedManager getInstance() {
        return INSTANCE;
    }

    public double speed = 0.0D;

    public void update() {
        if (isNull()) return;
        double distTraveledLastTickX = mc.player.posX - mc.player.prevPosX;
        double distTraveledLastTickZ = mc.player.posZ - mc.player.prevPosZ;
        this.speed = distTraveledLastTickX * distTraveledLastTickX + distTraveledLastTickZ * distTraveledLastTickZ;
    }

    public double turnIntoKpH(double input) {
        return MathHelper.sqrt(input) * 71.2729367892;
    }

    public double getSpeedKpH() {
        double speedometerkphdouble = turnIntoKpH(speed);
        speedometerkphdouble = Math.round(10.0 * speedometerkphdouble) / 10.0;
        return speedometerkphdouble;
    }

}
