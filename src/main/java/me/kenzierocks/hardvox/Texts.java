package me.kenzierocks.hardvox;

import me.kenzierocks.hardvox.session.HVSession;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

public class Texts {

    public static ITextComponent hardVoxMessage(String messageText) {
        return new TextComponentString(messageText).setStyle(new Style().setColor(TextFormatting.LIGHT_PURPLE));
    }

    public static ITextComponent hardVoxError(String messageText) {
        return new TextComponentString(messageText).setStyle(new Style().setColor(TextFormatting.RED));
    }

    public static void error(HVSession target, Throwable e) {
        String msg = "An unexpected error occured, check the server console: " + e.getMessage();
        e.printStackTrace();
        target.sendMessage(hardVoxError(msg));
    }

}
