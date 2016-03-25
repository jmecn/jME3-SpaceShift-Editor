package com.ss.editor.ui.tooltip;

import com.ss.editor.manager.JavaFXImageManager;
import com.ss.editor.ui.css.CSSIds;

import java.nio.file.Path;

import javafx.geometry.Insets;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import rlib.ui.util.FXUtils;

/**
 * Реализация превью каналов изображения.
 *
 * @author Ronn
 */
public class ImageChannelPreview extends CustomTooltip {

    private static final JavaFXImageManager IMAGE_MANAGER = JavaFXImageManager.getInstance();

    /**
     * Картинка для отображения красного канала.
     */
    private final WritableImage redImage;

    /**
     * Картинка для отображения зеленого канала.
     */
    private final WritableImage greenImage;

    /**
     * Картинка для отображения синего канала.
     */
    private final WritableImage blueImage;

    /**
     * Картинка для отображения альфа канала.
     */
    private final WritableImage alphaImage;

    /**
     * Просмотр красного канала.
     */
    private ImageView redView;

    /**
     * Просмотр зеленого канала.
     */
    private ImageView greenView;

    /**
     * Просмотр синего канала.
     */
    private ImageView blueView;

    /**
     * Просмотр альфа канала.
     */
    private ImageView alphaView;

    public ImageChannelPreview() {
        redImage = new WritableImage(120, 120);
        greenImage = new WritableImage(120, 120);
        blueImage = new WritableImage(120, 120);
        alphaImage = new WritableImage(120, 120);
    }

    /**
     * @return картинка для отображения альфа канала.
     */
    private WritableImage getAlphaImage() {
        return alphaImage;
    }

    /**
     * @return картинка для отображения красного канала.
     */
    private WritableImage getRedImage() {
        return redImage;
    }

    /**
     * @return картинка для отображения синего канала.
     */
    private WritableImage getBlueImage() {
        return blueImage;
    }

    /**
     * @return картинка для отображения зеленого канала.
     */
    private WritableImage getGreenImage() {
        return greenImage;
    }

    @Override
    protected void createContent(final VBox root) {
        super.createContent(root);

        redView = new ImageView();
        redView.setId(CSSIds.IMAGE_CHANNEL_PREVIEW_IMAGE_CONTAINER);
        redView.setImage(getRedImage());

        greenView = new ImageView();
        greenView.setId(CSSIds.IMAGE_CHANNEL_PREVIEW_IMAGE_CONTAINER);
        greenView.setImage(getGreenImage());

        blueView = new ImageView();
        blueView.setId(CSSIds.IMAGE_CHANNEL_PREVIEW_IMAGE_CONTAINER);
        blueView.setImage(getBlueImage());

        alphaView = new ImageView();
        alphaView.setId(CSSIds.IMAGE_CHANNEL_PREVIEW_IMAGE_CONTAINER);
        alphaView.setImage(getAlphaImage());

        final HBox firstRow = new HBox();
        final HBox secondRow = new HBox();

        FXUtils.addToPane(redView, firstRow);
        FXUtils.addToPane(greenView, firstRow);
        FXUtils.addToPane(blueView, secondRow);
        FXUtils.addToPane(alphaView, secondRow);
        FXUtils.addToPane(firstRow, root);
        FXUtils.addToPane(secondRow, root);

        HBox.setMargin(redView, new Insets(0, 0, 0, 0));
        HBox.setMargin(greenView, new Insets(0, 0, 0, 10));
        HBox.setMargin(blueView, new Insets(0, 0, 0, 0));
        HBox.setMargin(alphaView, new Insets(0, 0, 0, 10));
        VBox.setMargin(firstRow, new Insets(10, 10, 10, 10));
        VBox.setMargin(secondRow, new Insets(0, 10, 10, 10));
    }

    /**
     * @return просмотр альфа канала.
     */
    private ImageView getAlphaView() {
        return alphaView;
    }

    /**
     * @return просмотр синего канала.
     */
    private ImageView getBlueView() {
        return blueView;
    }

    /**
     * @return просмотр зеленого канала.
     */
    private ImageView getGreenView() {
        return greenView;
    }

    /**
     * @return просмотр красного канала.
     */
    private ImageView getRedView() {
        return redView;
    }

    /**
     * Отобразить каналы для указанного файла.
     */
    public void showImage(final Path file) {

        final Image image = file == null ? null : IMAGE_MANAGER.getTexturePreview(file, 120, 120);

        if (file == null || image.getWidth() != 120) {

            final ImageView redView = getRedView();
            redView.setImage(null);

            final ImageView greenView = getGreenView();
            greenView.setImage(null);

            final ImageView blueView = getBlueView();
            blueView.setImage(null);

            final ImageView alphaView = getAlphaView();
            alphaView.setImage(null);
            return;
        }

        final PixelReader pixelReader = image.getPixelReader();

        final WritableImage alphaImage = getAlphaImage();
        final PixelWriter alphaWriter = alphaImage.getPixelWriter();

        final WritableImage redImage = getRedImage();
        final PixelWriter redWriter = redImage.getPixelWriter();

        final WritableImage greenImage = getGreenImage();
        final PixelWriter greenWriter = greenImage.getPixelWriter();

        final WritableImage blueImage = getBlueImage();
        final PixelWriter blueWriter = blueImage.getPixelWriter();

        for (int y = 0, height = (int) image.getHeight(); y < height; y++) {
            for (int x = 0, width = (int) image.getWidth(); x < width; x++) {

                final int argb = pixelReader.getArgb(x, y);

                final int alpha = argb >>> 24;
                final int red = (argb >> 16) & 0xff;
                final int green = (argb >> 8) & 0xff;
                final int blue = (argb) & 0xff;

                redWriter.setArgb(x, y, ((255 << 24) | (red << 16) | (red << 8) | red));
                greenWriter.setArgb(x, y, ((255 << 24) | (green << 16) | (green << 8) | green));
                blueWriter.setArgb(x, y, ((255 << 24) | (blue << 16) | (blue << 8) | blue));
                alphaWriter.setArgb(x, y, ((255 << 24) | (alpha << 16) | (alpha << 8) | alpha));
            }
        }

        final ImageView redView = getRedView();
        redView.setImage(null);
        redView.setImage(redImage);

        final ImageView greenView = getGreenView();
        greenView.setImage(null);
        greenView.setImage(greenImage);

        final ImageView blueView = getBlueView();
        blueView.setImage(null);
        blueView.setImage(blueImage);

        final ImageView alphaView = getAlphaView();
        alphaView.setImage(null);
        alphaView.setImage(alphaImage);
    }
}