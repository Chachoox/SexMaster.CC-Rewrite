package me.pignol.swift.client.blowbui.glowclient.clickgui.buttons;

import me.pignol.swift.client.blowbui.glowclient.clickgui.BaseButton;

public class SubColor extends BaseButton {

    public SubColor(int x, int y, int width, int height) {
        super(x, y, width, height);
    }

    @Override
    public String getName() {
        return "Color";
    }

    @Override
    public void draw(int x, int y) {
    }

    @Override
    public int getColor() {
        return 0;
    }

}
