import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import java.io.FileInputStream;

/**
 * This class is used to group all the characteristics of a TextureButton together.
 *
 * @author Nihar Thakkar
 * @version 1.0
 * @since 07-12-2017
 */

class TextureButton
{
    private Image image;
    private double positionX;
    private double positionY;
    private double width;
    private double height;

    TextureButton()
    {
        positionX = 0;
        positionY = 0;
    }

    void setImage(Image i)
    {
        image = i;
        width = i.getWidth();
        height = i.getHeight();
    }

    void setImage(FileInputStream fileInputStream)
    {
        Image i = new Image(fileInputStream);
        setImage(i);
    }

    void setPosition(double x, double y)
    {
        positionX = x;
        positionY = y;
    }

    void render(GraphicsContext gc)
    {
        gc.drawImage(image, positionX, positionY);
    }

    Rectangle2D getBoundary()
    {
        return new Rectangle2D(positionX, positionY, width, height);
    }
}