package com.ss.editor.util;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * The class with utility methods for working with geometry.
 *
 * @author JavaSaBr.
 */
public class GeomUtils {

    /**
     * Get the UP vector from the rotation.
     */
    @NotNull
    public static Vector3f getUp(@NotNull final Quaternion rotation, @NotNull final Vector3f store) {
        return rotation.getRotationColumn(1, store);
    }

    /**
     * Get the Left vector from the rotation.
     */
    @NotNull
    public static Vector3f getLeft(@NotNull final Quaternion rotation, @NotNull final Vector3f store) {
        return rotation.getRotationColumn(0, store);
    }

    /**
     * Get the Direction vector from the rotation.
     */
    @NotNull
    public static Vector3f getDirection(@NotNull final Quaternion rotation, @NotNull final Vector3f store) {
        return rotation.getRotationColumn(2, store);
    }

    /**
     * Get the index of the object in the model.
     */
    public static int getIndex(@NotNull final Spatial model, @NotNull final Object object) {

        Spatial parent = model;
        int parentIndex = 0;

        while (parent != null) {
            if (Objects.equals(parent, object)) return parentIndex;
            parent = parent.getParent();
            parentIndex--;
        }

        if (!(model instanceof Node)) {
            return -1;
        }

        final AtomicInteger counter = new AtomicInteger(0);
        final Node node = (Node) model;

        final List<Spatial> children = node.getChildren();

        for (final Spatial child : children) {
            if (getIndex(child, object, counter)) return counter.get();
        }

        return -1;
    }

    /**
     * Get the index of the object in the model.
     */
    private static boolean getIndex(@NotNull final Object model, @NotNull final Object object,
                                    @NotNull final AtomicInteger counter) {
        counter.incrementAndGet();

        if (Objects.equals(model, object)) {
            return true;
        } else if (model instanceof Geometry) {
            return getIndex(((Geometry) model).getMesh(), object, counter);
        } else if (!(model instanceof Node)) {
            return false;
        }

        final Node node = (Node) model;
        final List<Spatial> children = node.getChildren();

        for (final Spatial child : children) {
            if (getIndex(child, object, counter)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Find the object by the index in the model.
     */
    @Nullable
    public static Object getObjectByIndex(@NotNull final Spatial model, final int index) {

        Spatial parent = model;
        int parentIndex = 0;

        while (parent != null) {
            if (parentIndex == index) return parent;
            parent = parent.getParent();
            parentIndex--;
        }

        if (!(model instanceof Node)) {
            return null;
        }

        final AtomicInteger counter = new AtomicInteger(0);
        final Node node = (Node) model;

        final List<Spatial> children = node.getChildren();

        for (final Spatial child : children) {
            final Object object = getObjectByIndex(child, index, counter);
            if (object != null) return object;
        }

        return null;
    }

    /**
     * Find the object by the index in the model.
     */
    @Nullable
    private static Object getObjectByIndex(@NotNull final Object model, final int index,
                                           @NotNull final AtomicInteger counter) {

        if (counter.incrementAndGet() == index) {
            return model;
        } else if (model instanceof Geometry) {
            return getObjectByIndex(((Geometry) model).getMesh(), index, counter);
        } else if (!(model instanceof Node)) {
            return null;
        }

        final Node node = (Node) model;
        final List<Spatial> children = node.getChildren();

        for (final Spatial child : children) {
            final Object object = getObjectByIndex(child, index, counter);
            if (object != null) return object;
        }

        return null;
    }

    /**
     * @return true if the spatial can be attached to the node.
     */
    public static boolean canAttach(@NotNull final Node node, @NotNull final Spatial spatial) {

        Spatial parent = node;

        while (parent != null) {
            if (parent == spatial) return false;
            parent = parent.getParent();
        }

        return true;
    }
}
