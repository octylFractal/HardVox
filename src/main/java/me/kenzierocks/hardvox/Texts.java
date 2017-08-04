package me.kenzierocks.hardvox;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

public class Texts {

    public static ITextComponent hardVoxMessage(String messageText) {
        return new TextComponentString(messageText).setStyle(new Style().setColor(TextFormatting.LIGHT_PURPLE));
    }

}
