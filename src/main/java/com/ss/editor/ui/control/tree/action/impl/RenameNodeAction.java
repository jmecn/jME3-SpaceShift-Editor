package com.ss.editor.ui.control.tree.action.impl;

import com.ss.editor.Messages;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.tree.NodeTree;
import com.ss.editor.ui.control.tree.action.AbstractNodeAction;
import com.ss.editor.ui.control.tree.node.TreeNode;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The action to rename a model node.
 *
 * @author JavaSaBr
 */
public class RenameNodeAction extends AbstractNodeAction<ModelChangeConsumer> {

    public RenameNodeAction(@NotNull final NodeTree<?> nodeTree, @NotNull TreeNode<?> node) {
        super(nodeTree, node);
    }

    @Override
    @FxThread
    protected @Nullable Image getIcon() {
        return Icons.EDIT_16;
    }

    @Override
    @FxThread
    protected @NotNull String getName() {
        return Messages.MODEL_NODE_TREE_ACTION_RENAME;
    }

    @Override
    @FxThread
    protected void process() {
        super.process();
        final NodeTree<ModelChangeConsumer> nodeTree = getNodeTree();
        nodeTree.startEdit(getNode());
    }
}
