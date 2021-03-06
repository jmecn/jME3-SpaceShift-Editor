package com.ss.editor.ui.dialog.asset.file;

import com.ss.editor.annotation.FxThread;
import com.ss.editor.ui.component.asset.tree.resource.ResourceElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * The implementation of the {@link AssetEditorDialog} to choose the {@link Path} from asset.
 *
 * @author JavaSaBr
 */
public class FolderAssetEditorDialog extends AssetEditorDialog<Path> {

    public FolderAssetEditorDialog(@NotNull final Consumer<Path> consumer) {
        super(consumer);
        setOnlyFolders(true);
    }

    public FolderAssetEditorDialog(@NotNull final Consumer<Path> consumer,
                                   @Nullable final Function<Path, String> validator) {
        super(consumer, validator);
        setOnlyFolders(true);
    }

    @Override
    @FxThread
    protected void processOpen(@NotNull final ResourceElement element) {
        super.processOpen(element);
        final Consumer<Path> consumer = getConsumer();
        consumer.accept(element.getFile());
    }

    @Override
    @FxThread
    protected @Nullable Path getObject(@NotNull final ResourceElement element) {
        return element.getFile();
    }
}
