package com.ss.editor.ui.component.editing.terrain;

import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Sphere;
import com.ss.editor.Editor;
import com.ss.editor.control.editing.impl.AbstractEditingControl;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The base implementation of terrain tool.
 *
 * @author JavaSaBr
 */
public class TerrainToolControl extends AbstractEditingControl {

    protected static final Editor EDITOR = Editor.getInstance();

    /**
     * The editing component.
     */
    @NotNull
    protected final TerrainEditingComponent component;

    /**
     * The brush geometry.
     */
    @NotNull
    protected final Geometry brush;

    /**
     * The brush size.
     */
    private float brushSize;

    /**
     * The brush power.
     */
    private float brushPower;

    public TerrainToolControl(@NotNull final TerrainEditingComponent component) {
        this.component = component;

        final Material material = new Material(EDITOR.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        material.getAdditionalRenderState().setWireframe(true);
        material.setColor("Color", getBrushColor());

        this.brush = new Geometry("Brush", new Sphere(8, 8, 1));
        this.brush.setMaterial(material);

        //FIXME need to remove
        setBrushSize(8);
        setBrushPower(2);
    }

    /**
     * @return the brush color.
     */
    @NotNull
    protected ColorRGBA getBrushColor() {
        return ColorRGBA.Black;
    }

    @Override
    public void setSpatial(final Spatial spatial) {

        final Spatial prev = getSpatial();
        if (prev instanceof Node) {
            ((Node) prev).detachChild(brush);
        }

        super.setSpatial(spatial);

        if (spatial instanceof Node) {
            ((Node) spatial).attachChild(brush);
        }
    }

    @Nullable
    @Override
    public Spatial getEditedModel() {
        return component.getEditedObject();
    }

    /**
     * @return the change consumer.
     */
    @NotNull
    protected ModelChangeConsumer getChangeConsumer() {
        return component.getChangeConsumer();
    }

    /**
     * @return the brush geometry.
     */
    @NotNull
    protected Geometry getBrush() {
        return brush;
    }

    /**
     * @param brushSize the brush size.
     */
    public void setBrushSize(final float brushSize) {
        this.brushSize = brushSize;
        getBrush().setLocalScale(brushSize);
    }

    /**
     * @return the brush size.
     */
    public float getBrushSize() {
        return brushSize;
    }

    /**
     * @param brushPower the brush power.
     */
    public void setBrushPower(final float brushPower) {
        this.brushPower = brushPower;
    }

    /**
     * @return the brush power.
     */
    public float getBrushPower() {
        return brushPower;
    }
}
