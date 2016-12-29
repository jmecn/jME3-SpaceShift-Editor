package com.ss.editor.ui.component.asset.tree.context.menu.action;

import com.ss.editor.JFXApplication;
import com.ss.editor.Messages;
import com.ss.editor.config.EditorConfig;
import com.ss.editor.ui.component.asset.tree.resource.ResourceElement;
import com.ss.editor.ui.dialog.ConfirmDialog;
import com.ss.editor.ui.scene.EditorFXScene;

import java.nio.file.Path;

import javafx.scene.control.MenuItem;
import rlib.util.FileUtils;

/**
 * The action for deleting a file.
 *
 * @author JavaSaBr
 */
public class DeleteFileAction extends MenuItem {

    private static final JFXApplication JFX_APPLICATION = JFXApplication.getInstance();

    /**
     * The node in the tree.
     */
    private final ResourceElement element;

    public DeleteFileAction(final ResourceElement element) {
        this.element = element;
        setText(Messages.ASSET_COMPONENT_RESOURCE_TREE_CONTEXT_MENU_DELETE_FILE);
        setOnAction(event -> processDelete());
    }

    /**
     * Handle deleting.
     */
    private void processDelete() {

        final EditorConfig editorConfig = EditorConfig.getInstance();
        final Path currentAsset = editorConfig.getCurrentAsset();
        if (currentAsset == null) return;

        final Path file = element.getFile();

        if (currentAsset.equals(file)) {
            //TODO нужно варнинг показать
            return;
        }

        String question = Messages.ASSET_COMPONENT_RESOURCE_TREE_CONTEXT_MENU_DELETE_FILE_QUESTION;
        question = question.replace("%file_name%", file.getFileName().toString());

        final EditorFXScene scene = JFX_APPLICATION.getScene();
        final ConfirmDialog confirmDialog = new ConfirmDialog(result -> handle(file, result), question);
        confirmDialog.show(scene.getWindow());
    }

    /**
     * Handle the answer.
     */
    private void handle(final Path file, final Boolean result) {
        if (!result) return;
        FileUtils.delete(file);
    }
}
