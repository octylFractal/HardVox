package com.mumfrey.worldeditcui.render;

import static org.lwjgl.opengl.GL11.glColor4f;
import static org.lwjgl.opengl.GL11.glDepthFunc;
import static org.lwjgl.opengl.GL11.glLineWidth;

import com.mumfrey.worldeditcui.render.RenderStyle.RenderType;

/**
 * Stores data about a line that can be rendered
 * 
 * @author lahwran
 * @author yetanotherx
 * @author Adam Mummery-Smith
 */
public class LineStyle {

    public final float lineWidth, red, green, blue, alpha;
    public final RenderType renderType;

    public LineStyle(RenderType renderType, float lineWidth, float red, float green, float blue) {
        this(renderType, lineWidth, red, green, blue, 1.0f);
    }

    public LineStyle(RenderType renderType, float lineWidth, float red, float green, float blue, float alpha) {
        this.lineWidth = lineWidth;
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.alpha = alpha;
        this.renderType = renderType;
    }

    /**
     * Sets the lineWidth and depthFunction based on this style
     */
    public boolean prepare(RenderType renderType) {
        if (this.renderType.matches(renderType)) {
            glLineWidth(this.lineWidth);
            glDepthFunc(this.renderType.depthFunc);
            return true;
        }

        return false;
    }

    public void applyColour() {
        glColor4f(this.red / 255.0f, this.green / 255.0f, this.blue / 255.0f, this.alpha / 255.0f);
    }

    public void applyColour(float tint) {
        glColor4f(this.red / 255.0f, this.green / 255.0f, this.blue / 255.0f, (this.alpha * tint) / 255.0f);
    }
}
