package com.ss.editor.plugin.api.property.control;

import static com.ss.rlib.util.ObjectUtils.notNull;
import com.ss.editor.plugin.api.property.PropertyDefinition;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.editor.ui.util.AutoCompleteComboBoxListener;
import com.ss.rlib.ui.util.FXUtils;
import com.ss.rlib.util.VarTable;
import com.ss.rlib.util.array.Array;
import javafx.scene.control.ComboBox;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.TextField;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The control to choose string value from list.
 *
 * @author JavaSaBr
 */
public class StringFromListPropertyEditorControl extends PropertyEditorControl<String> {

    /**
     * The list of available options of the string value.
     */
    @Nullable
    private ComboBox<String> comboBox;

    protected StringFromListPropertyEditorControl(@NotNull final VarTable vars, @NotNull final PropertyDefinition definition,
                                                  @NotNull final Runnable validationCallback, @NotNull final Array<?> options) {
        super(vars, definition, validationCallback);

        final ComboBox<String> comboBox = getComboBox();
        options.forEach(comboBox.getItems(), (option, items) -> items.add(option.toString()));
    }

    @Override
    protected void createComponents() {
        super.createComponents();

        comboBox = new ComboBox<>();
        comboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> change());
        comboBox.prefWidthProperty().bind(widthProperty().multiply(DEFAULT_FIELD_W_PERCENT));

        final TextField editor = comboBox.getEditor();

        AutoCompleteComboBoxListener.install(comboBox);

        FXUtils.addClassesTo(editor, CSSClasses.TRANSPARENT_TEXT_FIELD, CSSClasses.TEXT_FIELD_IN_COMBO_BOX);
        FXUtils.addClassTo(comboBox, CSSClasses.ABSTRACT_PARAM_CONTROL_COMBO_BOX);
        FXUtils.addToPane(comboBox, this);
    }

    /**
     * @return The list of available options of the string value.
     */
    @NotNull
    private ComboBox<String> getComboBox() {
        return notNull(comboBox);
    }

    @Override
    protected void reload() {
        super.reload();
        final String value = getPropertyValue();
        final ComboBox<String> enumComboBox = getComboBox();
        enumComboBox.getSelectionModel().select(value);
    }

    @Override
    protected void change() {
        final ComboBox<String> enumComboBox = getComboBox();
        final SingleSelectionModel<String> selectionModel = enumComboBox.getSelectionModel();
        setPropertyValue(selectionModel.getSelectedItem());
        super.change();
    }
}
