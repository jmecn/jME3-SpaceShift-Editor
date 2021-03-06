package com.ss.editor.ui.control.property.builder;

import com.ss.editor.annotation.FxThread;
import com.ss.editor.model.undo.editor.ChangeConsumer;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javafx.scene.layout.VBox;

/**
 * The interface to implement property builder to create property controls for some objects.
 *
 * @author JavaSaBr
 */
public interface PropertyBuilder extends Comparable<PropertyBuilder> {

    /**
     * Build properties controls for the object to the container.
     *
     * @param object         the object for building property controls.
     * @param parent         the parent og the object.
     * @param container      the container for containing these controls.
     * @param changeConsumer the consumer for working between controls and editor.
     */
    @FxThread
    void buildFor(@NotNull Object object, @Nullable Object parent, @NotNull VBox container,
                  @NotNull ChangeConsumer changeConsumer);

    /**
     * Get the priority of this builder.
     *
     * @return the priority of this builder.
     */
    default int getPriority() {
        return 0;
    }

    @Override
    default int compareTo(@NotNull final PropertyBuilder o) {
        return o.getPriority() - getPriority();
    }
}
