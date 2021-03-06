package com.ss.editor.ui.control.tree.action.impl.control;

import com.jme3.scene.Spatial;
import com.jme3.scene.control.Control;
import com.jme3.scene.control.LightControl;
import com.ss.editor.Messages;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.tree.NodeTree;
import com.ss.editor.ui.control.tree.node.TreeNode;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The action to create a {@link com.jme3.scene.control.LightControl}.
 *
 * @author JavaSaBr
 */
public class CreateLightControlAction extends AbstractCreateControlAction {

    public CreateLightControlAction(@NotNull final NodeTree<?> nodeTree, @NotNull final TreeNode<?> node) {
        super(nodeTree, node);
    }

    @Override
    @FxThread
    protected @Nullable Image getIcon() {
        return Icons.LIGHT_16;
    }

    @Override
    @FxThread
    protected @NotNull String getName() {
        return Messages.MODEL_NODE_TREE_ACTION_ADD_CONTROL_LIGHT;
    }

    @Override
    @FxThread
    protected @NotNull Control createControl(@NotNull final Spatial parent) {
        return new LightControl();
    }
}
