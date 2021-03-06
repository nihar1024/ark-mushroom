import edu.ufl.digitalworlds.j4k.J4KSDK;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

public class MainApplication extends Application implements KinectHelperCallback
{
    private GraphicsContext cursorGraphicsContext;
    private GraphicsContext textureGraphicsContext;
    private Cursor handCursor;
    private Cursor brushCursor;
    private float oldX;
    private float oldY;
    private Image textureImage1;

    private Image textureImage2;

    private int bubbleFrameCount = 0;
    private Canvas textureCanvas;
    private boolean rightHandIsPushed = false;
    private ArrayList<TextureButton> textureButtons = new ArrayList<>();
    private ArrayList<PictureButton> pictureButtons = new ArrayList<>();
    private Button currentlySelectedButton = null;
    private Stage primaryStage;
    private KinectHelper kinect;
    private Scene primaryScene;
    private boolean drawingComplete = false;

    public static void main(String[] args)
    {
        launch(args);
    }

    @Override
    public void start(Stage aPrimaryStage) throws Exception
    {
        primaryStage = aPrimaryStage;

        primaryStage.setTitle(Constants.STAGE_TITLE);
        System.out.println("hey abhilash");

        VBox textureVBox = new VBox(32);
        textureVBox.setAlignment(Pos.CENTER);
        textureVBox.setPadding(new Insets(64, 32, 64, 32));
        setTextureButtons();
        for (TextureButton textureButton : this.textureButtons)
        {
            textureVBox.getChildren().add(textureButton.getImageView());
        }

        VBox pictureVBox = new VBox(32);
        pictureVBox.setAlignment(Pos.CENTER);
        pictureVBox.setPadding(new Insets(64, 32, 64, 32));
        setPictureButtons();
        for (PictureButton pictureButton : this.pictureButtons)
        {
            pictureVBox.getChildren().add(pictureButton.getImageView());
        }

        setBlackShadowToAllButtons();
        System.out.println("hey abhilash 1");
        AnchorPane anchorPane = new AnchorPane(textureVBox, pictureVBox);
        AnchorPane.setLeftAnchor(textureVBox, 0.0);
        AnchorPane.setRightAnchor(pictureVBox, 0.0);

        Canvas cursorCanvas = new Canvas(Constants.STAGE_WIDTH, Constants.STAGE_HEIGHT);
        cursorGraphicsContext = cursorCanvas.getGraphicsContext2D();

        this.textureCanvas = new Canvas(Constants.STAGE_WIDTH, Constants.STAGE_HEIGHT);
        textureGraphicsContext = textureCanvas.getGraphicsContext2D();

        StackPane rootStackPane = new StackPane(anchorPane, textureCanvas, cursorCanvas);
        rootStackPane.setAlignment(Pos.CENTER);

        primaryScene = new Scene(rootStackPane);

        primaryStage.setScene(primaryScene);
        primaryStage.setMaximized(true);
        primaryStage.setFullScreen(true);

        kinect = new KinectHelper(this);
        kinect.start(J4KSDK.SKELETON);

        handCursor = new Cursor();
        handCursor.setImage(new FileInputStream("images\\hand.png"));

        brushCursor = new Cursor();
        brushCursor.setImage(new FileInputStream("images\\brush.png"));

        initialiseTextures();

        System.out.println("hey abhilash 2");
        primaryStage.show();
    }

    private void setTextureButtons()
    {
        TextureButton textureButton1 = assignTextureImg("images\\texture_dots.png",1);
        TextureButton textureButton2 = assignTextureImg("images\\texture_tear.png", 2);

        textureButtons.add(textureButton1);
        textureButtons.add(textureButton2);
    }

    private TextureButton assignTextureImg(String imgPath, int id) {
        try {
            Image texture = new Image(new FileInputStream(imgPath));
            ImageView textureImageView = new ImageView(texture);
            TextureButton textureButton = new TextureButton(textureImageView);
            textureButton.setId(id);
            return textureButton;

        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
            System.exit(1);
            return null;
        }
    }

    private void setPictureButtons()
    {
        PictureButton pictureButton1 = assignPictureImg("images\\Symbol_Bird.png",1);
        PictureButton pictureButton2 = assignPictureImg("images\\Symbol_flower.png",2);
        PictureButton pictureButton3 = assignPictureImg("images\\lotus_image.png",3);

        pictureButtons.add(pictureButton1);
        pictureButtons.add(pictureButton2);
        pictureButtons.add(pictureButton3);
    }

    private PictureButton assignPictureImg(String imgPath, int id) {
        try {
            Image picture = new Image(new FileInputStream(imgPath));
            ImageView pictureImageView = new ImageView(picture);
            PictureButton textureButton = new PictureButton(pictureImageView, picture);
            textureButton.setId(id);
            return textureButton;
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
            System.exit(1);
            return null;
        }
    }

    @Override
    public void onRightHandMoved(float x, float y)
    {
        if (rightHandIsPushed)
        {
            // Draw the brush cursor
            cursorGraphicsContext.clearRect(oldX, oldY, 300, 100);
            brushCursor.setPosition(x, y);
            brushCursor.render(cursorGraphicsContext);

            if (currentlySelectedButton != null && currentlySelectedButton.getClass() == TextureButton.class)
            {
                if (x >= 250 && x <= 1100)
                {
                    switch (currentlySelectedButton.getId())
                    {
                        case 1:
                            bubbleFrameCount++;
                            if (bubbleFrameCount > 4)
                            {
                                textureGraphicsContext.drawImage(textureImage1, x, y, 40, 20);
                                bubbleFrameCount = 0;
                            }
                            break;
                        case 2:
                            bubbleFrameCount++;

                            if (bubbleFrameCount > 4)
                            {
                                textureGraphicsContext.drawImage(textureImage2, x, y, 40, 20);
                                bubbleFrameCount = 0;
                            }

                            break;
                        default:
                            break;
                    }
                }
            }
            else if (currentlySelectedButton != null && currentlySelectedButton.getClass() == PictureButton.class)
            {
                System.out.println("selected button");
                cursorGraphicsContext.drawImage(((PictureButton) currentlySelectedButton).getImage(), x, y);
            }
        }
        else
        {
            if (currentlySelectedButton != null && currentlySelectedButton.getClass() == PictureButton.class)
        {
//            System.out.println(x  + "," + y);
            if (x >= 250 && x <= 900)
            {
                textureGraphicsContext.drawImage(((PictureButton) currentlySelectedButton).getImage(), x, y);
                currentlySelectedButton = null;
                setBlackShadowToAllButtons();
            }
            else
            {
                currentlySelectedButton = null;
                setBlackShadowToAllButtons();
            }
        }

            // Draw the hand cursor
            cursorGraphicsContext.clearRect(oldX, oldY, 300, 100);
            handCursor.setPosition(x, y);
            handCursor.render(cursorGraphicsContext);
        }

        oldX = x;
        oldY = y;
    }

    @Override
    public void onRightHandPushed(boolean rightHandIsPushed)
    {
        this.rightHandIsPushed = rightHandIsPushed;

        if (rightHandIsPushed)
        {
            for (Button textureButton : this.textureButtons)
            {
                if (textureButton.getImageView().getBoundsInParent().intersects(handCursor.getPositionX(),
                        handCursor.getPositionY(), handCursor.getWidth(), handCursor.getHeight()))
                {
                    setBlackShadowToAllButtons();
                    currentlySelectedButton = textureButton;
                    DropShadow dropShadowRed = new DropShadow(50, Color.GREEN);
                    currentlySelectedButton.getImageView().setEffect(dropShadowRed);
                    break;
                }
            }

            for (PictureButton pictureButton : this.pictureButtons)
            {
                if (pictureButton.intersects(handCursor))
                {
                    setBlackShadowToAllButtons();
                    currentlySelectedButton = pictureButton;
                    DropShadow dropShadowRed = new DropShadow(16, Color.RED);
                    currentlySelectedButton.getImageView().setEffect(dropShadowRed);
                    break;
                }
            }
        }
    }

    private void setBlackShadowToAllButtons()
    {
        for (TextureButton textureButton : this.textureButtons)
        {
            DropShadow dropShadowBlack = new DropShadow(16, Color.BLACK);
            textureButton.getImageView().setEffect(dropShadowBlack);
        }

        for (PictureButton pictureButton : this.pictureButtons)
        {
            DropShadow dropShadowBlack = new DropShadow(16, Color.BLACK);
            pictureButton.getImageView().setEffect(dropShadowBlack);
        }
    }

    @Override
    public void onBothHandsRaised()
    {
        if (!drawingComplete)
        {
            pixelScaleAwareCanvasSnapshot(textureCanvas, 1);
        }
        drawingComplete = true;
        kinect.stop();
    }

    private void initialiseTextures()
    {
        try
        {
            textureImage1 = new Image(new FileInputStream("images\\texture_dots.png"));
            textureImage2 = new Image(new FileInputStream("images\\texture_tear.png"));

        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
            System.exit(1);
        }
    }

    private void pixelScaleAwareCanvasSnapshot(Canvas canvas, double pixelScale)
    {
        WritableImage writableImage = new WritableImage(Constants.STAGE_WIDTH, Constants.STAGE_HEIGHT);
        SnapshotParameters snapshotParameters = new SnapshotParameters();
        File file = new File("images\\CanvasImage.png");
        Platform.runLater(
                () ->
                {
                    try
                    {
                        ImageIO.write(SwingFXUtils.fromFXImage(canvas.snapshot(snapshotParameters, writableImage), null), "png", file);

                        Image image = new Image(new FileInputStream("images\\CanvasImage.png"));

                        PixelReader reader = image.getPixelReader();
                        WritableImage newImage = new WritableImage(reader, 250, 0, 800, 768);
                        ImageIO.write(SwingFXUtils.fromFXImage(newImage, null), "png", file);
                        repeater();
                    }
                    catch (Exception s)
                    {
                        s.printStackTrace();
                        System.exit(1);
                    }
                });
    }

    private void repeater()
    {
        TilePane tilePane = new TilePane(Orientation.HORIZONTAL);
        tilePane.setPrefColumns(4);
        tilePane.setAlignment(Pos.CENTER);
        tilePane.setPrefRows(4);
        WritableImage writableImage = null;
        Image image = null;
        try
        {
            image = new Image(new FileInputStream("images\\CanvasImage.png"));
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
            System.exit(1);
        }

        for (int i = 0; i < 12; i++)
        {
            ImageView imageView = new ImageView(image);
            imageView.setFitWidth(300);
            imageView.setFitHeight(271);
            tilePane.getChildren().add(imageView);
        }

        Scene scene = new Scene(tilePane);
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);
        primaryStage.setFullScreen(true);
    }
}