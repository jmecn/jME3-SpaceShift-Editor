package com.ss.editor.ui.component.painting.terrain;

import static com.ss.editor.ui.component.painting.PaintingComponentContainer.FIELD_PERCENT;
import static com.ss.editor.ui.component.painting.PaintingComponentContainer.LABEL_PERCENT;
import static com.ss.rlib.util.ObjectUtils.notNull;
import static com.ss.rlib.util.array.ArrayFactory.toArray;
import com.jme3.material.MatParam;
import com.jme3.material.Material;
import com.jme3.material.MaterialDef;
import com.jme3.scene.Node;
import com.jme3.terrain.Terrain;
import com.jme3.terrain.geomipmap.TerrainQuad;
import com.ss.editor.Messages;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.annotation.JmeThread;
import com.ss.editor.control.painting.terrain.*;
import com.ss.editor.model.undo.editor.ChangeConsumer;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.component.editor.state.EditorState;
import com.ss.editor.ui.component.painting.PaintingComponentContainer;
import com.ss.editor.ui.component.painting.impl.AbstractPaintingComponent;
import com.ss.editor.ui.component.painting.terrain.paint.TextureLayerSettings;
import com.ss.editor.ui.control.property.operation.PropertyOperation;
import com.ss.editor.ui.css.CssClasses;
import com.ss.editor.util.MaterialUtils;
import com.ss.editor.util.NodeUtils;
import com.ss.rlib.ui.control.input.FloatTextField;
import com.ss.rlib.ui.util.FXUtils;
import com.ss.rlib.util.StringUtils;
import com.ss.rlib.util.array.Array;
import com.ss.rlib.util.array.ArrayFactory;
import com.ss.rlib.util.dictionary.DictionaryFactory;
import com.ss.rlib.util.dictionary.ObjectDictionary;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * The painting component to edit terrain.
 *
 * @author JavaSaBr
 */
public class TerrainPaintingComponent extends AbstractPaintingComponent<Node, TerrainPaintingStateWithEditorTool, TerrainToolControl> {

    /**
     * The constant TERRAIN_PARAM.
     */
    @NotNull
    public static final String TERRAIN_PARAM = "terrainParam";

    @NotNull
    private static final Function<Integer, String> LAYER_TO_SCALE_NAME = layer -> "DiffuseMap_" + layer + "_scale";

    @NotNull
    private static final Function<Integer, String> LAYER_TO_ALPHA_NAME = layer -> {

        final int alphaIdx = layer / 4; // 4 = rgba = 4 textures

        if (alphaIdx == 0) {
            return "AlphaMap";
        } else if (alphaIdx == 1) {
            return "AlphaMap_1";
        } else if (alphaIdx == 2) {
            return "AlphaMap_2";
        } else {
            return null;
        }
    };

    @NotNull
    private static final Function<Integer, String> LAYER_TO_DIFFUSE_NAME = layer -> {
        if (layer == 0) {
            return "DiffuseMap";
        } else {
            return "DiffuseMap_" + layer;
        }
    };

    @NotNull
    private static final Function<Integer, String> LAYER_TO_NORMAL_NAME = layer -> {
        if (layer == 0) {
            return "NormalMap";
        } else {
            return "NormalMap_" + layer;
        }
    };

    /**
     * The list of all tool controls.
     */
    @NotNull
    private final Array<TerrainToolControl> toolControls;

    /**
     * The list of all toggle buttons.
     */
    @NotNull
    private final Array<ToggleButton> toggleButtons;

    /**
     * The map with mapping toggle button to terrain control.
     */
    @NotNull
    private final ObjectDictionary<ToggleButton, TerrainToolControl> buttonToControl;

    /**
     * The map with mapping toggle button to its settings.
     */
    @NotNull
    private final ObjectDictionary<ToggleButton, Pane> buttonToSettings;

    /**
     * The control to raise/lowe terrain.
     */
    @NotNull
    private final RaiseLowerTerrainToolControl raiseLowerToolControl;

    /**
     * The control to smooth terrain.
     */
    @NotNull
    private final SmoothTerrainToolControl smoothToolControl;

    /**
     * The control to make rough surface terrain.
     */
    @NotNull
    private final RoughTerrainToolControl roughToolControl;

    /**
     * The control to make some levels terrain.
     */
    @NotNull
    private final LevelTerrainToolControl levelToolControl;

    /**
     * The control to make slopes on terrain.
     */
    @NotNull
    private final SlopeTerrainToolControl slopeToolControl;

    /**
     * The control to paint on terrain.
     */
    @NotNull
    private final PaintTerrainToolControl paintToolControl;

    /**
     * The button to raise/lower terrain.
     */
    @Nullable
    private ToggleButton raiseLowerButton;

    /**
     * The button to smooth terrain.
     */
    @Nullable
    private ToggleButton smoothButton;

    /**
     * The button to make rough terrain.
     */
    @Nullable
    private ToggleButton roughButton;

    /**
     * The button to change height of terrain by level.
     */
    @Nullable
    private ToggleButton levelButton;

    /**
     * The button to make slopes on terrain.
     */
    @Nullable
    private ToggleButton slopeButton;

    /**
     * The button to paint on terrain.
     */
    @Nullable
    private ToggleButton paintButton;

    /**
     * The container of control settings.
     */
    @Nullable
    private VBox controlSettings;

    /**
     * The settings of level control.
     */
    @Nullable
    private GridPane levelControlSettings;

    /**
     * The setting of using smoothly changing of terrain height.
     */
    @Nullable
    private CheckBox levelControlSmoothly;

    /**
     * The setting of using marker to detect a level.
     */
    @Nullable
    private CheckBox levelControlUseMarker;

    /**
     * The setting of target level.
     */
    @Nullable
    private FloatTextField levelControlLevelField;

    /**
     * The settings of rough control.
     */
    @Nullable
    private GridPane roughControlSettings;

    /**
     * The settings of roughness.
     */
    @Nullable
    private FloatTextField roughControlRoughnessField;

    /**
     * The settings of frequency.
     */
    @Nullable
    private FloatTextField roughControlFrequencyField;

    /**
     * The settings of lacunarity.
     */
    @Nullable
    private FloatTextField roughControlLacunarityField;

    /**
     * The settings of octaves.
     */
    @Nullable
    private FloatTextField roughControlOctavesField;

    /**
     * The settings of scale.
     */
    @Nullable
    private FloatTextField roughControlScaleField;

    /**
     * The settings of slope control.
     */
    @Nullable
    private GridPane slopeControlSettings;

    /**
     * The setting of using smoothly changing of terrain height.
     */
    @Nullable
    private CheckBox slopeControlSmoothly;

    /**
     * The setting of using limited between markers.
     */
    @Nullable
    private CheckBox slopeControlLimited;

    /**
     * The settings of painting control.
     */
    @Nullable
    private GridPane paintControlSettings;

    /**
     * The settings of texture layers.
     */
    @Nullable
    private TextureLayerSettings textureLayerSettings;

    /**
     * The box to use tri-planar.
     */
    @Nullable
    private CheckBox triPlanarCheckBox;

    /**
     * The shininess field.
     */
    @Nullable
    private FloatTextField shininessField;

    public TerrainPaintingComponent(@NotNull final PaintingComponentContainer container) {
        super(container);

        this.buttonToControl = DictionaryFactory.newObjectDictionary();
        this.buttonToSettings = DictionaryFactory.newObjectDictionary();
        this.raiseLowerToolControl = new RaiseLowerTerrainToolControl(this);
        this.smoothToolControl = new SmoothTerrainToolControl(this);
        this.roughToolControl = new RoughTerrainToolControl(this);
        this.levelToolControl = new LevelTerrainToolControl(this);
        this.slopeToolControl = new SlopeTerrainToolControl(this);
        this.paintToolControl = new PaintTerrainToolControl(this);
        this.toolControls = ArrayFactory.newArray(TerrainToolControl.class);
        this.toggleButtons = ArrayFactory.newArray(ToggleButton.class);
        this.toolControls.addAll(toArray(raiseLowerToolControl, smoothToolControl, roughToolControl,
                levelToolControl, slopeToolControl, paintToolControl));
        this.toggleButtons.addAll(toArray(raiseLowerButton, smoothButton, roughButton, levelButton,
                slopeButton, paintButton));

        final ToggleButton raiseLowerButton = getRaiseLowerButton();

        buttonToControl.put(raiseLowerButton, raiseLowerToolControl);
        buttonToControl.put(getSmoothButton(), smoothToolControl);
        buttonToControl.put(getRoughButton(), roughToolControl);
        buttonToControl.put(getLevelButton(), levelToolControl);
        buttonToControl.put(getSlopeButton(), slopeToolControl);
        buttonToControl.put(getPaintButton(), paintToolControl);
        buttonToSettings.put(getSlopeButton(), slopeControlSettings);
        buttonToSettings.put(getLevelButton(), levelControlSettings);
        buttonToSettings.put(getRoughButton(), roughControlSettings);
        buttonToSettings.put(getPaintButton(), paintControlSettings);

        raiseLowerButton.setSelected(true);

        setToolControl(raiseLowerToolControl);

        FXUtils.addClassTo(this, CssClasses.PROCESSING_COMPONENT_TERRAIN_EDITOR);
    }

    @Override
    @FromAnyThread
    protected @NotNull Class<TerrainPaintingStateWithEditorTool> getStateType() {
        return TerrainPaintingStateWithEditorTool.class;
    }

    @Override
    @FromAnyThread
    protected @NotNull Supplier<TerrainPaintingStateWithEditorTool> getStateConstructor() {
        return TerrainPaintingStateWithEditorTool::new;
    }

    @Override
    @FxThread
    public void loadState(@NotNull final EditorState editorState) {
        super.loadState(editorState);
        getLevelControlLevelField().setValue(state.getLevelValue());
        getLevelControlUseMarker().setSelected(state.isLevelUseMarker());
        getLevelControlSmoothly().setSelected(state.isLevelSmoothly());
        getSlopeControlLimited().setSelected(state.isSlopeLimited());
        getSlopeControlSmoothly().setSelected(state.isSlopeSmoothly());
        getRoughControlFrequencyField().setValue(state.getRoughtFrequency());
        getRoughControlLacunarityField().setValue(state.getRoughtLacunarity());
        getRoughControlOctavesField().setValue(state.getRoughtOctaves());
        getRoughControlRoughnessField().setValue(state.getRoughtRoughness());
        getRoughControlScaleField().setValue(state.getRoughtScale());
    }

    /**
     * Get the list of all tool controls.
     *
     * @return the list of all tool controls.
     */
    @FxThread
    private @NotNull Array<TerrainToolControl> getToolControls() {
        return toolControls;
    }

    /**
     * Get the list of all toggle buttons.
     *
     * @return the list of all toggle buttons.
     */
    @FxThread
    private @NotNull Array<ToggleButton> getToggleButtons() {
        return toggleButtons;
    }

    /**
     * Get the map with mapping toggle button to terrain control.
     *
     * @return the map with mapping toggle button to terrain control.
     */
    @FxThread
    private @NotNull ObjectDictionary<ToggleButton, TerrainToolControl> getButtonToControl() {
        return buttonToControl;
    }

    /**
     * Get the map with mapping toggle button to its settings.
     *
     * @return the map with mapping toggle button to its settings.
     */
    @FxThread
    private @NotNull ObjectDictionary<ToggleButton, Pane> getButtonToSettings() {
        return buttonToSettings;
    }

    /**
     * Get the container of control settings.
     *
     * @return the container of control settings.
     */
    @FxThread
    private @NotNull VBox getControlSettings() {
        return notNull(controlSettings);
    }

    /**
     * Get the button to change height of terrain by level.
     *
     * @return the button to change height of terrain by level.
     */
    @FxThread
    private @NotNull ToggleButton getLevelButton() {
        return notNull(levelButton);
    }

    /**
     * Get the button to paint on terrain.
     *
     * @return the button to paint on terrain.
     */
    @FxThread
    private @NotNull ToggleButton getPaintButton() {
        return notNull(paintButton);
    }

    /**
     * Get the button to make slopes on terrain.
     *
     * @return the button to make slopes on terrain.
     */
    @FxThread
    private @NotNull ToggleButton getSlopeButton() {
        return notNull(slopeButton);
    }

    /**
     * Get the button to make rough terrain.
     *
     * @return the button to make rough terrain.
     */
    @FxThread
    private @NotNull ToggleButton getRoughButton() {
        return notNull(roughButton);
    }

    /**
     * Get the button to smooth terrain.
     *
     * @return the button to smooth terrain.
     */
    @FxThread
    private @NotNull ToggleButton getSmoothButton() {
        return notNull(smoothButton);
    }

    /**
     * Get the button to raise/lower terrain.
     *
     * @return the button to raise/lower terrain.
     */
    @FxThread
    private @NotNull ToggleButton getRaiseLowerButton() {
        return notNull(raiseLowerButton);
    }

    @Override
    @FxThread
    protected void createComponents() {
        super.createComponents();

        raiseLowerButton = new ToggleButton(StringUtils.EMPTY, new ImageView(Icons.TERRAIN_UP_32));
        raiseLowerButton.setOnAction(event -> switchMode((ToggleButton) event.getSource()));

        smoothButton = new ToggleButton(StringUtils.EMPTY, new ImageView(Icons.TERRAIN_SMOOTH_32));
        smoothButton.setOnAction(event -> switchMode((ToggleButton) event.getSource()));

        roughButton = new ToggleButton(StringUtils.EMPTY, new ImageView(Icons.TERRAIN_ROUGH_32));
        roughButton.setOnAction(event -> switchMode((ToggleButton) event.getSource()));

        levelButton = new ToggleButton(StringUtils.EMPTY, new ImageView(Icons.TERRAIN_LEVEL_32));
        levelButton.setOnAction(event -> switchMode((ToggleButton) event.getSource()));

        slopeButton = new ToggleButton(StringUtils.EMPTY, new ImageView(Icons.TERRAIN_SLOPE_32));
        slopeButton.setOnAction(event -> switchMode((ToggleButton) event.getSource()));

        paintButton = new ToggleButton(StringUtils.EMPTY, new ImageView(Icons.TERRAIN_PAINT_32));
        paintButton.setOnAction(event -> switchMode((ToggleButton) event.getSource()));

        final GridPane buttonsContainer = new GridPane();
        buttonsContainer.setAlignment(Pos.CENTER);
        buttonsContainer.setPadding(new Insets(2, 4, 2, 4));
        buttonsContainer.add(raiseLowerButton, 0, 0);
        buttonsContainer.add(smoothButton, 1, 0);
        buttonsContainer.add(roughButton, 2, 0);
        buttonsContainer.add(levelButton, 3, 0);
        buttonsContainer.add(slopeButton, 4, 0);
        buttonsContainer.add(paintButton, 5, 0);
        buttonsContainer.prefWidthProperty().bind(widthProperty());

        FXUtils.addClassTo(buttonsContainer, CssClasses.DEF_GRID_PANE);

        controlSettings = new VBox();
        controlSettings.prefWidthProperty().bind(widthProperty());

        FXUtils.addToPane(buttonsContainer, this);
        FXUtils.addToPane(createBrushSettings(), this);
        FXUtils.addToPane(controlSettings, this);

        createLevelControlSettings();
        createSlopeControlSettings();
        createRoughControlSettings();
        createPaintControlSettings();

        FXUtils.addClassesTo(raiseLowerButton, smoothButton, roughButton, levelButton, slopeButton, paintButton,
                CssClasses.MEDIUM_TOGGLE_BUTTON);

        FXUtils.addClassTo(controlSettings, CssClasses.DEF_VBOX);
    }

    /**
     * Create settings of slope control.
     */
    @FxThread
    private void createSlopeControlSettings() {

        final Label smoothlyLabel = new Label(Messages.PAINTING_COMPONENT_SMOOTHLY + ":");
        smoothlyLabel.prefWidthProperty().bind(widthProperty().multiply(LABEL_PERCENT));

        slopeControlSmoothly = new CheckBox();
        slopeControlSmoothly.prefWidthProperty().bind(widthProperty().multiply(FIELD_PERCENT));
        slopeControlSmoothly.selectedProperty()
                .addListener((observable, oldValue, newValue) -> changeSlopeControlSmoothly(newValue));

        final Label limitedLabel = new Label(Messages.PAINTING_COMPONENT_LIMITED + ":");
        limitedLabel.prefWidthProperty().bind(widthProperty().multiply(LABEL_PERCENT));

        slopeControlLimited = new CheckBox();
        slopeControlLimited.prefWidthProperty().bind(widthProperty().multiply(FIELD_PERCENT));
        slopeControlLimited.selectedProperty()
                .addListener((observable, oldValue, newValue) -> changeSlopeControlLimited(newValue));

        slopeControlSettings = new GridPane();
        slopeControlSettings.add(smoothlyLabel, 0, 0);
        slopeControlSettings.add(slopeControlSmoothly, 1, 0);
        slopeControlSettings.add(limitedLabel, 0, 1);
        slopeControlSettings.add(slopeControlLimited, 1, 1);

        FXUtils.addClassTo(slopeControlSettings, CssClasses.DEF_GRID_PANE);
        FXUtils.addClassTo(smoothlyLabel, limitedLabel, CssClasses.ABSTRACT_PARAM_CONTROL_PARAM_NAME_SINGLE_ROW);
        FXUtils.addClassTo(slopeControlSmoothly, slopeControlLimited, CssClasses.ABSTRACT_PARAM_CONTROL_CHECK_BOX);
    }

    /**
     * Create settings of level control.
     */
    @FxThread
    private void createLevelControlSettings() {

        final Label smoothlyLabel = new Label(Messages.PAINTING_COMPONENT_SMOOTHLY + ":");
        smoothlyLabel.prefWidthProperty().bind(widthProperty().multiply(LABEL_PERCENT));

        levelControlSmoothly = new CheckBox();
        levelControlSmoothly.prefWidthProperty().bind(widthProperty().multiply(FIELD_PERCENT));
        levelControlSmoothly.selectedProperty()
                .addListener((observable, oldValue, newValue) -> changeLevelControlSmoothly(newValue));

        final Label useMarkerLabel = new Label(Messages.PAINTING_COMPONENT_USE_MARKER + ":");
        useMarkerLabel.prefWidthProperty().bind(widthProperty().multiply(LABEL_PERCENT));

        levelControlUseMarker = new CheckBox();
        levelControlUseMarker.prefWidthProperty().bind(widthProperty().multiply(FIELD_PERCENT));
        levelControlUseMarker.selectedProperty()
                .addListener((observable, oldValue, newValue) -> changeLevelControlUseMarker(newValue));

        final Label levelLabel = new Label(Messages.PAINTING_COMPONENT_LEVEL + ":");
        levelLabel.prefWidthProperty().bind(widthProperty().multiply(LABEL_PERCENT));

        levelControlLevelField = new FloatTextField();
        levelControlLevelField.prefWidthProperty().bind(widthProperty().multiply(FIELD_PERCENT));
        levelControlLevelField.setMinMax(0F, Integer.MAX_VALUE);
        levelControlLevelField.addChangeListener((observable, oldValue, newValue) -> changeLevelControlLevel(newValue));
        levelControlLevelField.disableProperty()
                .bind(levelControlUseMarker.selectedProperty());

        levelControlSettings = new GridPane();
        levelControlSettings.add(smoothlyLabel, 0, 0);
        levelControlSettings.add(levelControlSmoothly, 1, 0);
        levelControlSettings.add(useMarkerLabel, 0, 1);
        levelControlSettings.add(levelControlUseMarker, 1, 1);
        levelControlSettings.add(levelLabel, 0, 2);
        levelControlSettings.add(levelControlLevelField, 1, 2);

        FXUtils.addClassTo(smoothlyLabel, useMarkerLabel, levelLabel,
                CssClasses.ABSTRACT_PARAM_CONTROL_PARAM_NAME_SINGLE_ROW);

        FXUtils.addClassesTo(levelControlSettings, CssClasses.DEF_GRID_PANE);
        FXUtils.addClassTo(levelControlLevelField, CssClasses.ABSTRACT_PARAM_CONTROL_COMBO_BOX);
        FXUtils.addClassTo(levelControlSmoothly, levelControlUseMarker, CssClasses.ABSTRACT_PARAM_CONTROL_CHECK_BOX);
    }

    /**
     * Create settings of rough control.
     */
    @FxThread
    private void createRoughControlSettings() {

        final Label roughnessLabel = new Label(Messages.PAINTING_COMPONENT_ROUGHNESS + ":");
        roughnessLabel.prefWidthProperty().bind(widthProperty().multiply(LABEL_PERCENT));

        roughControlRoughnessField = new FloatTextField();
        roughControlRoughnessField.prefWidthProperty().bind(widthProperty().multiply(FIELD_PERCENT));
        roughControlRoughnessField.setMinMax(0F, Integer.MAX_VALUE);
        roughControlRoughnessField.addChangeListener((observable, oldValue, newValue) -> changeRoughControlRoughness(newValue));

        final Label frequencyLabel = new Label(Messages.PAINTING_COMPONENT_FREQUENCY + ":");
        frequencyLabel.prefWidthProperty().bind(widthProperty().multiply(LABEL_PERCENT));

        roughControlFrequencyField = new FloatTextField();
        roughControlFrequencyField.prefWidthProperty().bind(widthProperty().multiply(FIELD_PERCENT));
        roughControlFrequencyField.setMinMax(0.1F, Integer.MAX_VALUE);
        roughControlFrequencyField.addChangeListener((observable, oldValue, newValue) -> changeRoughControlFrequency(newValue));

        final Label lacunarityLabel = new Label(Messages.PAINTING_COMPONENT_LACUNARITY + ":");
        lacunarityLabel.prefWidthProperty().bind(widthProperty().multiply(LABEL_PERCENT));

        roughControlLacunarityField = new FloatTextField();
        roughControlLacunarityField.prefWidthProperty().bind(widthProperty().multiply(FIELD_PERCENT));
        roughControlLacunarityField.setMinMax(1.1F, Integer.MAX_VALUE);
        roughControlLacunarityField.addChangeListener((observable, oldValue, newValue) -> changeRoughControlLacunarity(newValue));

        final Label octavesLabel = new Label(Messages.PAINTING_COMPONENT_OCTAVES + ":");
        octavesLabel.prefWidthProperty().bind(widthProperty().multiply(LABEL_PERCENT));

        roughControlOctavesField = new FloatTextField();
        roughControlOctavesField.prefWidthProperty().bind(widthProperty().multiply(FIELD_PERCENT));
        roughControlOctavesField.setMinMax(0F, Integer.MAX_VALUE);
        roughControlOctavesField.addChangeListener((observable, oldValue, newValue) -> changeRoughControlOctaves(newValue));

        final Label scaleLabel = new Label(Messages.PAINTING_COMPONENT_SCALE + ":");
        scaleLabel.prefWidthProperty().bind(widthProperty().multiply(LABEL_PERCENT));

        roughControlScaleField = new FloatTextField();
        roughControlScaleField.prefWidthProperty().bind(widthProperty().multiply(FIELD_PERCENT));
        roughControlScaleField.setMinMax(0F, Integer.MAX_VALUE);
        roughControlScaleField.addChangeListener((observable, oldValue, newValue) -> changeRoughControlScale(newValue));

        roughControlSettings = new GridPane();
        roughControlSettings.add(roughnessLabel, 0, 0);
        roughControlSettings.add(roughControlRoughnessField, 1, 0);
        roughControlSettings.add(frequencyLabel, 0, 1);
        roughControlSettings.add(roughControlFrequencyField, 1, 1);
        roughControlSettings.add(lacunarityLabel, 0, 2);
        roughControlSettings.add(roughControlLacunarityField, 1, 2);
        roughControlSettings.add(octavesLabel, 0, 3);
        roughControlSettings.add(roughControlOctavesField, 1, 3);
        roughControlSettings.add(scaleLabel, 0, 4);
        roughControlSettings.add(roughControlScaleField, 1, 4);

        FXUtils.addClassTo(roughnessLabel, frequencyLabel, lacunarityLabel, octavesLabel, scaleLabel,
                CssClasses.ABSTRACT_PARAM_CONTROL_PARAM_NAME_SINGLE_ROW);


        FXUtils.addClassesTo(roughControlSettings, CssClasses.DEF_GRID_PANE);
        FXUtils.addClassTo(roughControlRoughnessField, roughControlFrequencyField, roughControlLacunarityField,
                roughControlOctavesField, roughControlScaleField, CssClasses.ABSTRACT_PARAM_CONTROL_COMBO_BOX);
    }

    /**
     * Create settings of paint control.
     */
    @FxThread
    private void createPaintControlSettings() {

        final Label triPlanarLabelLabel = new Label(Messages.PAINTING_COMPONENT_TRI_PLANAR + ":");
        triPlanarLabelLabel.prefWidthProperty().bind(widthProperty().multiply(LABEL_PERCENT));

        triPlanarCheckBox = new CheckBox();
        triPlanarCheckBox.prefWidthProperty().bind(widthProperty().multiply(FIELD_PERCENT));
        triPlanarCheckBox.selectedProperty()
                .addListener((observable, oldValue, newValue) -> changePaintControlTriPlanar(newValue));

        final Label shininessLabel = new Label(Messages.PAINTING_COMPONENT_SHININESS + ":");
        shininessLabel.prefWidthProperty().bind(widthProperty().multiply(LABEL_PERCENT));

        shininessField = new FloatTextField();
        shininessField.prefWidthProperty().bind(widthProperty().multiply(FIELD_PERCENT));
        shininessField.setMinMax(0F, Integer.MAX_VALUE);
        shininessField.addChangeListener((observable, oldValue, newValue) -> changePaintControlShininess(newValue));

        textureLayerSettings = new TextureLayerSettings(this);

        paintControlSettings = new GridPane();
        paintControlSettings.add(shininessLabel, 0, 0);
        paintControlSettings.add(shininessField, 1, 0);
        paintControlSettings.add(triPlanarLabelLabel, 0, 1);
        paintControlSettings.add(triPlanarCheckBox, 1, 1);
        paintControlSettings.add(textureLayerSettings, 0, 2, 2, 1);

        FXUtils.addClassesTo(paintControlSettings, CssClasses.DEF_GRID_PANE);
        FXUtils.addClassTo(shininessLabel, triPlanarLabelLabel, CssClasses.ABSTRACT_PARAM_CONTROL_PARAM_NAME_SINGLE_ROW);
        FXUtils.addClassTo(triPlanarCheckBox, CssClasses.ABSTRACT_PARAM_CONTROL_CHECK_BOX);
        FXUtils.addClassTo(shininessField, CssClasses.ABSTRACT_PARAM_CONTROL_COMBO_BOX);
    }

    /**
     * Get the control to make some levels terrain.
     *
     * @return the control to make some levels terrain.
     */
    @FxThread
    private @NotNull LevelTerrainToolControl getLevelToolControl() {
        return levelToolControl;
    }

    /**
     * Get the control to make slopes on terrain.
     *
     * @return the control to make slopes on terrain.
     */
    @FxThread
    private @NotNull SlopeTerrainToolControl getSlopeToolControl() {
        return slopeToolControl;
    }

    /**
     * Get the control to make rough surface terrain.
     *
     * @return the control to make rough surface terrain.
     */
    @FxThread
    private @NotNull RoughTerrainToolControl getRoughToolControl() {
        return roughToolControl;
    }

    /**
     * Get the paint tool control.
     *
     * @return the control to paint textures.
     */
    @FxThread
    public @NotNull PaintTerrainToolControl getPaintToolControl() {
        return paintToolControl;
    }

    /**
     * Change the shininess value.
     */
    @FromAnyThread
    private void changePaintControlShininess(@NotNull final Float newValue) {

        if (isIgnoreListeners()) {
            return;
        }

        final Node paintedObject = notNull(getPaintedObject());
        final Material material = NodeUtils.findMateial(paintedObject, mat -> mat.getParam("Shininess") != null);
        final MatParam param = material == null ? null : material.getParam("Shininess");
        final float shininess = param == null ? 0F : (float) param.getValue();

        final PropertyOperation<ChangeConsumer, Node, Float> operation =
                new PropertyOperation<>(paintedObject, TERRAIN_PARAM, newValue, shininess);

        operation.setApplyHandler((terrainQuad, value) ->
                NodeUtils.visitMaterials(terrainQuad, mat ->
                        MaterialUtils.safeSet(mat, "Shininess", value)));

        final ModelChangeConsumer changeConsumer = getChangeConsumer();
        changeConsumer.execute(operation);
    }

    /**
     * Change using tri-planar textures.
     */
    @FromAnyThread
    private void changePaintControlTriPlanar(@NotNull final Boolean newValue) {

        if (isIgnoreListeners()) {
            return;
        }

        final Node paintedObject = notNull(getPaintedObject());
        final PropertyOperation<ChangeConsumer, Node, Boolean> operation =
                new PropertyOperation<>(paintedObject, TERRAIN_PARAM, newValue, !newValue);

        operation.setApplyHandler((terrainQuad, value) ->
                NodeUtils.visitMaterials(terrainQuad, mat ->
                        MaterialUtils.safeSet(mat, "useTriPlanarMapping", value)));

        final ModelChangeConsumer changeConsumer = getChangeConsumer();
        changeConsumer.execute(operation);
    }

    /**
     * Change using smoothly editing.
     */
    @FromAnyThread
    private void changeLevelControlSmoothly(@NotNull final Boolean newValue) {
        if (state != null) state.setLevelSmoothly(newValue);
        EXECUTOR_MANAGER.addJmeTask(() -> getLevelToolControl().setPrecision(!newValue));
    }

    /**
     * Change using marker for level control.
     */
    @FromAnyThread
    private void changeLevelControlUseMarker(@NotNull final Boolean newValue) {
        if (state != null) state.setLevelUseMarker(newValue);
        EXECUTOR_MANAGER.addJmeTask(() -> getLevelToolControl().setUseMarker(newValue));
    }

    /**
     * Change a level of a level control.
     */
    @FromAnyThread
    private void changeLevelControlLevel(@NotNull final Float newLevel) {
        if (state != null) state.setLevelValue(newLevel);
        EXECUTOR_MANAGER.addJmeTask(() -> getLevelToolControl().setLevel(newLevel));
    }

    /**
     * Change using smoothly editing.
     */
    @FromAnyThread
    private void changeSlopeControlSmoothly(@NotNull final Boolean newValue) {
        if (state != null) state.setSlopeSmoothly(newValue);
        EXECUTOR_MANAGER.addJmeTask(() -> getSlopeToolControl().setPrecision(!newValue));
    }

    /**
     * Change using limited editing.
     */
    @FromAnyThread
    private void changeSlopeControlLimited(@NotNull final Boolean newValue) {
        if (state != null) state.setSlopeLimited(newValue);
        EXECUTOR_MANAGER.addJmeTask(() -> getSlopeToolControl().setLock(newValue));
    }

    /**
     * Change scale of a rough control.
     */
    @FromAnyThread
    private void changeRoughControlScale(@NotNull final Float newScale) {
        if (state != null) state.setRoughtScale(newScale);
        EXECUTOR_MANAGER.addJmeTask(() -> getRoughToolControl().setScale(newScale));
    }

    /**
     * Change frequency of a rough control.
     */
    @FromAnyThread
    private void changeRoughControlFrequency(@NotNull final Float newFrequency) {
        if (state != null) state.setRoughtFrequency(newFrequency);
        EXECUTOR_MANAGER.addJmeTask(() -> getRoughToolControl().setFrequency(newFrequency));
    }

    /**
     * Change lacunarity of a rough control.
     */
    @FromAnyThread
    private void changeRoughControlLacunarity(@NotNull final Float newLacunarity) {
        if (state != null) state.setRoughtLacunarity(newLacunarity);
        EXECUTOR_MANAGER.addJmeTask(() -> getRoughToolControl().setLacunarity(newLacunarity));
    }

    /**
     * Change octaves of a rough control.
     */
    @FromAnyThread
    private void changeRoughControlOctaves(@NotNull final Float newOctaves) {
        if (state != null) state.setRoughtOctaves(newOctaves);
        EXECUTOR_MANAGER.addJmeTask(() -> getRoughToolControl().setOctaves(newOctaves));
    }

    /**
     * Change roughness of a rough control.
     */
    @FromAnyThread
    private void changeRoughControlRoughness(@NotNull final Float newRoughness) {
        if (state != null) state.setRoughtRoughness(newRoughness);
        EXECUTOR_MANAGER.addJmeTask(() -> getRoughToolControl().setRoughness(newRoughness));
    }

    @Override
    @JmeThread
    protected void setBrushSize(@NotNull final Float size) {
        final Array<TerrainToolControl> toolControls = getToolControls();
        toolControls.forEach(size, TerrainToolControl::setBrushSize);
    }

    @Override
    @JmeThread
    protected void setBrushPower(@NotNull final Float power) {
        final Array<TerrainToolControl> toolControls = getToolControls();
        toolControls.forEach(power, TerrainToolControl::setBrushPower);
    }

    /**
     * Get the box to use tri-planar.
     *
     * @return the box to use tri-planar.
     */
    @FxThread
    protected @NotNull CheckBox getTriPlanarCheckBox() {
        return notNull(triPlanarCheckBox);
    }

    /**
     * Get the the shininess field.
     *
     * @return the shininess field.
     */
    @FxThread
    protected @NotNull FloatTextField getShininessField() {
        return notNull(shininessField);
    }

    /**
     * Get the setting of using smoothly changing of terrain height.
     *
     * @return the setting of using smoothly changing of terrain height.
     */
    @FxThread
    private @NotNull CheckBox getLevelControlSmoothly() {
        return notNull(levelControlSmoothly);
    }

    /**
     * Get the setting of using marker to detect a level.
     *
     * @return the setting of using marker to detect a level.
     */
    @FxThread
    private @NotNull CheckBox getLevelControlUseMarker() {
        return notNull(levelControlUseMarker);
    }

    /**
     * Get the setting of target level.
     *
     * @return the setting of target level.
     */
    @FxThread
    private @NotNull FloatTextField getLevelControlLevelField() {
        return notNull(levelControlLevelField);
    }

    /**
     * Get the setting of using limited between markers.
     *
     * @return the setting of using limited between markers.
     */
    @FxThread
    private @NotNull CheckBox getSlopeControlLimited() {
        return notNull(slopeControlLimited);
    }

    /**
     * Get the setting of using smoothly changing of terrain height.
     *
     * @return the setting of using smoothly changing of terrain height.
     */
    @FxThread
    private @NotNull CheckBox getSlopeControlSmoothly() {
        return notNull(slopeControlSmoothly);
    }

    /**
     * Get the settings of frequency.
     *
     * @return the settings of frequency.
     */
    @FxThread
    private @NotNull FloatTextField getRoughControlFrequencyField() {
        return notNull(roughControlFrequencyField);
    }

    /**
     * Get the settings of lacunarity.
     *
     * @return the settings of lacunarity.
     */
    @FxThread
    private @NotNull FloatTextField getRoughControlLacunarityField() {
        return notNull(roughControlLacunarityField);
    }

    /**
     * Get the settings of octaves.
     *
     * @return the settings of octaves.
     */
    @FxThread
    private @NotNull FloatTextField getRoughControlOctavesField() {
        return notNull(roughControlOctavesField);
    }

    /**
     * Get the settings of roughness.
     *
     * @return the settings of roughness.
     */
    @FxThread
    private @NotNull FloatTextField getRoughControlRoughnessField() {
        return notNull(roughControlRoughnessField);
    }

    /**
     * Get the settings of scale.
     *
     * @return the settings of scale.
     */
    @FxThread
    private @NotNull FloatTextField getRoughControlScaleField() {
        return notNull(roughControlScaleField);
    }

    /**
     * Get the settings of painting control.
     *
     * @return the settings of painting control.
     */
    @FxThread
    private @NotNull TextureLayerSettings getTextureLayerSettings() {
        return notNull(textureLayerSettings);
    }

    /**
     * Switch editing mode.
     */
    @FxThread
    private void switchMode(@NotNull final ToggleButton source) {

        if (!source.isSelected()) {
            source.setSelected(true);
            return;
        }

        getToggleButtons().forEach(source, (button, arg) -> button !=
                arg, (toggleButton, arg) -> toggleButton.setSelected(false));

        final ObjectDictionary<ToggleButton, Pane> buttonToSettings = getButtonToSettings();
        final Pane settings = buttonToSettings.get(source);

        final VBox controlSettings = getControlSettings();
        final ObservableList<javafx.scene.Node> children = controlSettings.getChildren();
        children.clear();

        if (settings != null) {
            children.add(settings);
        }

        final ObjectDictionary<ToggleButton, TerrainToolControl> buttonToControl = getButtonToControl();
        final TerrainToolControl toolControl = buttonToControl.get(source);

        setToolControl(toolControl);

        if (!isShowed()) {
            return;
        }

        EXECUTOR_MANAGER.addJmeTask(() -> {
            final Node cursorNode = getCursorNode();
            cursorNode.removeControl(TerrainToolControl.class);
            cursorNode.addControl(toolControl);
        });
    }

    @Override
    @FxThread
    public void startPainting(@NotNull final Object object) {
        super.startPainting(object);

        refreshProperties();

        final TextureLayerSettings settings = getTextureLayerSettings();
        final ToggleButton paintButton = getPaintButton();

        if (object instanceof Terrain) {

            final Terrain terrain = (Terrain) object;
            final Material material = terrain.getMaterial();
            final MaterialDef materialDef = material.getMaterialDef();

            if (materialDef.getAssetName().equals("Common/MatDefs/Terrain/TerrainLighting.j3md")) {
                settings.setLayerToScaleName(LAYER_TO_SCALE_NAME);
                settings.setLayerToAlphaName(LAYER_TO_ALPHA_NAME);
                settings.setLayerToDiffuseName(LAYER_TO_DIFFUSE_NAME);
                settings.setLayerToNormalName(LAYER_TO_NORMAL_NAME);
                settings.setMaxLevels(12);
            }

            settings.refresh();
            settings.setVisible(true);
            paintButton.setVisible(true);

        } else {

            settings.setVisible(false);
            paintButton.setVisible(false);

            if (paintButton.isSelected()) {

                final ToggleButton raiseLowerButton = getRaiseLowerButton();
                raiseLowerButton.setSelected(true);

                switchMode(raiseLowerButton);
            }
        }
    }

    /**
     * Refresh terrain properties.
     */
    @FxThread
    private void refreshProperties() {
        setIgnoreListeners(true);
        try {

            final Node paintedObject = notNull(getPaintedObject());
            final Material material = NodeUtils.findMateial(paintedObject, mat -> mat.getParam("Shininess") != null);
            final MatParam shininessParam = material == null ? null : material.getParam("Shininess");
            final MatParam triPlanarMappingParam = material == null ? null : material.getParam("useTriPlanarMapping");
            final float shininess = shininessParam == null ? 0F : (float) shininessParam.getValue();
            final boolean triPlanarMapping = triPlanarMappingParam != null && (boolean) triPlanarMappingParam.getValue();

            final FloatTextField shininessField = getShininessField();
            shininessField.setValue(shininess);

            final CheckBox triPlanarCheckBox = getTriPlanarCheckBox();
            triPlanarCheckBox.setSelected(triPlanarMapping);

        } finally {
            setIgnoreListeners(false);
        }
    }

    @Override
    @FxThread
    public void notifyChangeProperty(@NotNull final Object object, @NotNull final String propertyName) {
        refreshProperties();

        if (getPaintedObject() instanceof Terrain) {
            getTextureLayerSettings().notifyChangeProperty();
        }
    }

    @Override
    @FxThread
    public boolean isSupport(@NotNull final Object object) {
        return object instanceof Node &&
                NodeUtils.findSpatial((Node) object, TerrainQuad.class::isInstance) != null;
    }

    @Override
    @FromAnyThread
    public @NotNull String getName() {
        return Messages.PAINTING_COMPONENT_TERRAIN_EDITOR;
    }

    @Override
    public @NotNull Image getIcon() {
        return Icons.TERRAIN_16;
    }
}
