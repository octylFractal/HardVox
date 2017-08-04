package com.mumfrey.worldeditcui.render;

import java.util.Arrays;
import java.util.List;

import me.kenzierocks.hardvox.config.Color;

/**
 * Server-defined style for multi selections
 * 
 * @author Adam Mummery-Smith
 */
public class CustomStyle implements RenderStyle {

    private Color color;
    private RenderType renderType = RenderType.ANY;
    private final List<LineStyle> lines = Arrays.asList(null, null);

    public CustomStyle(Color colour) {
        this.setColor(colour);
    }

    @Override
    public void setRenderType(RenderType renderType) {
        this.renderType = renderType;
    }

    @Override
    public RenderType getRenderType() {
        return this.renderType;
    }

    @Override
    public void setColor(Color color) {
        this.color = color;
        this.lines.set(0, new LineStyle(RenderType.HIDDEN, 3.0f, color.r * 0.75F, color.g * 0.75F, color.b * 0.75F, color.a * 0.25F));
        this.lines.set(1, new LineStyle(RenderType.VISIBLE, 3.0f, color.r, color.g, color.b, color.a));
    }

    @Override
    public Color getColor() {
        return this.color;
    }

    @Override
    public List<LineStyle> getLines() {
        return this.lines;
    }
}
