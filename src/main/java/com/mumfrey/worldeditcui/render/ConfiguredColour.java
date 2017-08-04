package com.mumfrey.worldeditcui.render;

import java.util.List;

import com.google.common.collect.ImmutableList;
import com.mumfrey.worldeditcui.render.RenderStyle.RenderType;

import me.kenzierocks.hardvox.config.Color;
import net.minecraft.client.resources.I18n;

/**
 * Stores style data for each type of line.
 * 
 * Each line has a normal line, and a hidden line. The normal line has an alpha
 * value of 0.8f, and the hidden line has an alpha value of 0.2f. They both have
 * a thickness of 3.0f.
 * 
 * @author yetanotherx
 * @author lahwran
 * @author Adam Mummery-Smith
 */
public enum ConfiguredColour {
    CUBOIDBOX("colour.cuboidedge", new Color("#CC3333CC")),
    CUBOIDGRID("colour.cuboidgrid", new Color("#CC4C4CCC")),
    CUBOIDPOINT1("colour.cuboidpoint1", new Color("#33CC33CC")),
    CUBOIDPOINT2("colour.cuboidpoint2", new Color("#3333CCCC")),
    POLYGRID("colour.polygrid", new Color("#CC3333CC")),
    POLYBOX("colour.polyedge", new Color("#CC4C4CCC")),
    POLYPOINT("colour.polypoint", new Color("#33CCCCCC")),
    ELLIPSOIDGRID("colour.ellipsoidgrid", new Color("#CC4C4CCC")),
    ELLIPSOIDCENTRE("colour.ellipsoidpoint", new Color("#CCCC33CC")),
    CYLINDERGRID("colour.cylindergrid", new Color("#CC3333CC")),
    CYLINDERBOX("colour.cylinderedge", new Color("#CC4C4CCC")),
    CYLINDERCENTRE("colour.cylinderpoint", new Color("#CC33CCCC")),
    CHUNKBOUNDARY("colour.chunkboundary", new Color("#33CC33CC")),
    CHUNKGRID("colour.chunkgrid", new Color("#4CCCAA99"));

    class Style implements RenderStyle {

        private RenderType renderType = RenderType.ANY;

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
        }

        @Override
        public Color getColor() {
            return ConfiguredColour.this.getColor();
        }

        @Override
        public List<LineStyle> getLines() {
            return ConfiguredColour.this.getLines();
        }
    }

    private String displayName;
    private Color defaultColor, color;
    private LineStyle normal, hidden;
    private List<LineStyle> lines;

    private ConfiguredColour(String displayName, Color color) {
        this.displayName = displayName;
        this.color = color;
        this.defaultColor = color;
        this.updateLines();
    }

    public String getDisplayName() {
        return I18n.format(this.displayName);
    }

    public RenderStyle style() {
        return new Style();
    }

    public void setColor(Color colour) {
        this.color = colour;
        this.updateLines();
    }

    public Color getColor() {
        return this.color;
    }

    public LineStyle getHidden() {
        return this.hidden;
    }

    public LineStyle getNormal() {
        return this.normal;
    }

    public List<LineStyle> getLines() {
        return this.lines;
    }

    public void setDefault() {
        this.color = this.defaultColor;
        this.updateLines();
    }

    public Color getDefault() {
        return this.defaultColor;
    }

    private void updateLines() {
        this.normal = new LineStyle(RenderType.VISIBLE, 3.0f, this.color.r, this.color.g, this.color.b, this.color.a);
        this.hidden = new LineStyle(RenderType.HIDDEN, 3.0f, this.color.r * 0.75F, this.color.g * 0.75F, this.color.b * 0.75F,
                this.color.a * 0.25F);
        this.lines = ImmutableList.of(normal, hidden);
    }
}
