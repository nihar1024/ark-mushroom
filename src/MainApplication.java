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
import javafx.scene.transform.Transform;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

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
    private Image textureImage3;
    private Image textureImage4;

    private Image textureImage5;
    private  Image textureImage6;


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

    private void restart()
    {
        drawingComplete = false;
        bubbleFrameCount = 0;
        currentlySelectedButton = null;
        setBlackShadowToAllButtons();
        textureGraphicsContext.clearRect(0, 0, Constants.STAGE_WIDTH, Constants.STAGE_HEIGHT);
        cursorGraphicsContext.clearRect(0, 0, Constants.STAGE_WIDTH, Constants.STAGE_HEIGHT);
    }

    @Override
    public void start(Stage aPrimaryStage) throws Exception
    {
        primaryStage = aPrimaryStage;

        primaryStage.setTitle(Constants.STAGE_TITLE);

        StackPane stackPane = new StackPane();
        stackPane.setAlignment(Pos.CENTER);

        ImageView arkImageView = new ImageView("http://www.theark.in/images/logo_white.png");
        arkImageView.setPreserveRatio(true);
        arkImageView.setFitWidth(400);
        arkImageView.setFitHeight(300);

        stackPane.getChildren().addAll(arkImageView);

        Scene launchScene = new Scene(stackPane);
        launchScene.setFill(Color.web("#16272E"));
        primaryStage.setScene(launchScene);
        primaryStage.setMaximized(true);
        primaryStage.setFullScreen(true);
        primaryStage.show();

        TimeUnit.SECONDS.sleep(5);

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
    }

    private void setTextureButtons()
    {
        try
        {
            Image texture1 = new Image(new FileInputStream("images\\dots_texture.png"));
            ImageView textureImageView1 = new ImageView(texture1);
            TextureButton textureButton1 = new TextureButton(textureImageView1);
            textureButton1.setId(1);

            Image texture2 = new Image(new FileInputStream("images\\texture2.jpg"));
            ImageView textureImageView2 = new ImageView(texture2);
            TextureButton textureButton2 = new TextureButton(textureImageView2);
            textureButton2.setId(2);

            Image texture3 = new Image(new FileInputStream("images\\red_brush.png"));
            ImageView textureImageView3 = new ImageView(texture3);
            TextureButton textureButton3 = new TextureButton(textureImageView3);
            textureButton3.setId(3);

            Image texture4 = new Image(new FileInputStream("images\\8251.png"));
            ImageView textureImageView4 = new ImageView(texture4);
            TextureButton textureButton4 = new TextureButton(textureImageView4);
            textureButton4.setId(4);

            Image texture5 = new Image(new FileInputStream("images\\colorFull.png"));
            ImageView textureImageView5 = new ImageView(texture5);
            TextureButton textureButton5 = new TextureButton(textureImageView5);
            textureButton5.setId(5);

            Image texture6 = new Image(new FileInputStream("images\\multicolor.png"));
            ImageView textureImageView6 = new ImageView(texture6);
            TextureButton textureButton6 = new TextureButton(textureImageView6);
            textureButton6.setId(6);


            textureButtons.add(textureButton1);
            textureButtons.add(textureButton2);

            textureButtons.add(textureButton3);

            textureButtons.add(textureButton4);

            textureButtons.add(textureButton5);

            textureButtons.add(textureButton6);
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
            System.exit(1);
        }
    }

    private void setPictureButtons()
    {
        try
        {
            Image picture1 = new Image(new FileInputStream("images\\lotus_image.png"));
            ImageView pictureImageView1 = new ImageView(picture1);
            PictureButton pictureButton1 = new PictureButton(pictureImageView1, picture1);
            pictureButton1.setId(1);

            Image picture2 = new Image(new FileInputStream("images\\peacock.png"));
            ImageView pictureImageView2 = new ImageView(picture2);
            PictureButton pictureButton2 = new PictureButton(pictureImageView2, picture2);
            pictureButton2.setId(2);

            Image picture3 = new Image(new FileInputStream("images\\peacock.png"));
            ImageView pictureImageView3 = new ImageView(picture3);
            PictureButton pictureButton3 = new PictureButton(pictureImageView3, picture3);
            pictureButton3.setId(3);

            pictureButtons.add(pictureButton1);
            pictureButtons.add(pictureButton2);
            pictureButtons.add(pictureButton3);
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
            System.exit(1);
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
                        case 3:
                            bubbleFrameCount++;

                            if (bubbleFrameCount > 4)
                            {
                                textureGraphicsContext.drawImage(textureImage3, x, y, 40, 20);
                                bubbleFrameCount = 0;
                            }
                            break;
                        case 4:
                            bubbleFrameCount++;

                            if (bubbleFrameCount > 4)
                            {
                                textureGraphicsContext.drawImage(textureImage4, x, y, 40, 20);
                                bubbleFrameCount = 0;
                            }
                            break;
                        case 5:
                            bubbleFrameCount++;

                            if (bubbleFrameCount > 4)
                            {
                                textureGraphicsContext.drawImage(textureImage5, x, y, 40, 20);
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
                System.out.println("selected button                            ");
                cursorGraphicsContext.drawImage(((PictureButton) currentlySelectedButton).getImage(), x, y);
            }
        }
        else
        {
            if (currentlySelectedButton != null && currentlySelectedButton.getClass() == PictureButton.class)
            {
                if (x >= 250 && x <= 1300)
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
            for (TextureButton textureButton : this.textureButtons)
            {
                if (textureButton.getImageView().getBoundsInParent().intersects(handCursor.getPositionX(),
                        handCursor.getPositionY(), handCursor.getWidth(), handCursor.getHeight()))
                {
                    setBlackShadowToAllButtons();
                    currentlySelectedButton = textureButton;
                    DropShadow dropShadowRed = new DropShadow(16, Color.RED);
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

    @Override
    public void onBothHandsClasped()
    {
        Platform.runLater(
                () ->
                {
                    if (primaryStage.getScene() != primaryScene)
                    {
                        primaryStage.setScene(primaryScene);
                        primaryStage.setFullScreen(true);
                        primaryStage.setMaximized(true);
                    }
                });

        restart();
    }

    private void initialiseTextures()
    {
        try
        {
            textureImage1 = new Image(new FileInputStream("images\\dots_texture.png"));
            textureImage2 = new Image(new FileInputStream("images\\texture2.jpg"));
            textureImage3 = new Image(new FileInputStream("images\\red_brush.png"));
            textureImage4 = new Image(new FileInputStream("images\\8251.png"));

            textureImage5 = new Image(new FileInputStream("images\\colorFull.png"));

            textureImage6 = new Image(new FileInputStream("images\\multicolor.png"));

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
                        WritableImage newImage = new WritableImage(reader, 250, 0, 1036, 768);
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

        for (int i = 0; i < 16; i++)
        {
            ImageView imageView = new ImageView(image);
            imageView.setFitWidth(384);
            imageView.setFitHeight(185);
            tilePane.getChildren().add(imageView);
        }

        Scene scene = new Scene(tilePane);
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);
        primaryStage.setFullScreen(true);
    }
}