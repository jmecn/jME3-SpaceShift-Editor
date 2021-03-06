package com.ss.editor.ui.control.property.impl;

import static com.ss.rlib.util.ObjectUtils.notNull;
import com.jme3.math.Vector3f;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.model.undo.editor.ChangeConsumer;
import com.ss.editor.ui.control.property.PropertyControl;
import com.ss.editor.ui.css.CssClasses;
import com.ss.editor.ui.util.UiUtils;
import com.ss.rlib.function.SixObjectConsumer;
import com.ss.rlib.ui.control.input.FloatTextField;
import com.ss.rlib.ui.util.FXUtils;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;

/**
 * The implementation of the {@link PropertyControl} to edit {@link Vector3f} values.
 *
 * @param <C> the type of a {@link ChangeConsumer}
 * @param <T> the type of an editing object.
 * @author JavaSaBr
 */
public class Vector3FPropertyControl<C extends ChangeConsumer, T> extends PropertyControl<C, T, Vector3f> {

    /**
     * The field X.
     */
    @Nullable
    private FloatTextField xField;

    /**
     * The field Y.
     */
    @Nullable
    private FloatTextField yField;

    /**
     * The field Z.
     */
    @Nullable
    private FloatTextField zField;

    public Vector3FPropertyControl(@Nullable final Vector3f propertyValue, @NotNull final String propertyName,
                                   @NotNull final C changeConsumer) {
        super(propertyValue, propertyName, changeConsumer);
    }

    public Vector3FPropertyControl(@Nullable final Vector3f propertyValue, @NotNull final String propertyName,
                                    @NotNull final C changeConsumer,
                                    @Nullable final SixObjectConsumer<C, T, String, Vector3f, Vector3f, BiConsumer<T, Vector3f>> changeHandler) {
        super(propertyValue, propertyName, changeConsumer, changeHandler);
    }

    @Override
    @FxThread
    protected void createComponents(@NotNull final HBox container) {
        super.createComponents(container);

        final Label xLabel = new Label("x:");

        xField = new FloatTextField();
        xField.setOnKeyReleased(this::updateVector);
        xField.addChangeListener((observable, oldValue, newValue) -> updateVector(null));
        xField.prefWidthProperty().bind(widthProperty().divide(3));
        xField.setScrollPower(getScrollPower());

        final Label yLabel = new Label("y:");

        yField = new FloatTextField();
        yField.setOnKeyReleased(this::updateVector);
        yField.addChangeListener((observable, oldValue, newValue) -> updateVector(null));
        yField.prefWidthProperty().bind(widthProperty().divide(3));
        yField.setScrollPower(getScrollPower());

        final Label zLabel = new Label("z:");

        zField = new FloatTextField();
        zField.setOnKeyReleased(this::updateVector);
        zField.addChangeListener((observable, oldValue, newValue) -> updateVector(null));
        zField.prefWidthProperty().bind(widthProperty().divide(3));
        zField.setScrollPower(getScrollPower());

        FXUtils.addToPane(xLabel, container);
        FXUtils.addToPane(xField, container);
        FXUtils.addToPane(yLabel, container);
        FXUtils.addToPane(yField, container);
        FXUtils.addToPane(zLabel, container);
        FXUtils.addToPane(zField, container);

        FXUtils.addClassTo(xLabel, yLabel, zLabel, CssClasses.ABSTRACT_PARAM_CONTROL_NUMBER_LABEL);
        FXUtils.addClassesTo(container, CssClasses.DEF_HBOX, CssClasses.TEXT_INPUT_CONTAINER,
                CssClasses.ABSTRACT_PARAM_CONTROL_INPUT_CONTAINER);
        FXUtils.addClassesTo(xField, yField, zField, CssClasses.ABSTRACT_PARAM_CONTROL_VECTOR3F_FIELD,
                CssClasses.TRANSPARENT_TEXT_FIELD);

        UiUtils.addFocusBinding(container, xField, yField, zField);
    }

    @Override
    @FxThread
    protected void setPropertyValue(@Nullable final Vector3f vector) {
        super.setPropertyValue(vector == null ? null : vector.clone());
    }

    /**
     * Gets scroll power.
     *
     * @return the scroll power.
     */
    @FxThread
    protected float getScrollPower() {
        return 10F;
    }

    /**
     * Gets x field.
     *
     * @return the field X.
     */
    @FxThread
    protected @NotNull FloatTextField getXField() {
        return notNull(xField);
    }

    /**
     * Gets y filed.
     *
     * @return the field Y.
     */
    @FxThread
    protected @NotNull FloatTextField getYFiled() {
        return notNull(yField);
    }

    /**
     * Gets z field.
     *
     * @return the field Z.
     */
    @FxThread
    protected @NotNull FloatTextField getZField() {
        return notNull(zField);
    }

    @Override
    @FxThread
    protected void reload() {

        final Vector3f vector = getPropertyValue() == null ? Vector3f.ZERO : getPropertyValue();

        final FloatTextField xField = getXField();
        xField.setValue(vector.getX());
        xField.positionCaret(xField.getText().length());

        final FloatTextField yFiled = getYFiled();
        yFiled.setValue(vector.getY());
        yFiled.positionCaret(xField.getText().length());

        final FloatTextField zField = getZField();
        zField.setValue(vector.getZ());
        zField.positionCaret(xField.getText().length());
    }

    /**
     * Update the vector.
     *
     * @param event the event
     */
    @FxThread
    protected void updateVector(@Nullable final KeyEvent event) {

        if (isIgnoreListener() || (event != null && event.getCode() != KeyCode.ENTER)) {
            return;
        }

        final FloatTextField xField = getXField();
        final float x = xField.getValue();

        final FloatTextField yFiled = getYFiled();
        final float y = yFiled.getValue();

        final FloatTextField zField = getZField();
        final float z = zField.getValue();

        final Vector3f oldValue = getPropertyValue() == null ? Vector3f.ZERO : getPropertyValue();
        final Vector3f newValue = new Vector3f();
        newValue.set(x, y, z);

        changed(newValue, oldValue.clone());
    }
}
