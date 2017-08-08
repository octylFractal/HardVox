package me.kenzierocks.hardvox.client.gui;

import me.kenzierocks.hardvox.HardVox;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.config.GuiConfig;

public class GuiHVConfig extends GuiConfig {

    public GuiHVConfig(GuiScreen parentScreen) {
        super(parentScreen, HardVox.MODID, HardVox.MODID + ".config.title.gui");
    }

}
