import controller.Direction;
import controller.ThreadsController;
import javafx.stage.FileChooser;
import model.Entity;
import model.Field;
import model.Images;
import view.View;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.scene.canvas.GraphicsContext;


public class MainFX extends Application implements View {
    private GraphicsContext graphics;
    public final Map<Images, Image> images = new HashMap<>();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        File selectedFile = fileChooser.showOpenDialog(stage);


        Canvas canvas = new Canvas(600, 600);

        Group root = new Group();
        root.getChildren().add(canvas);

        graphics = canvas.getGraphicsContext2D();
        stage.setTitle("Snake");
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();

        imagePut(Images.BACKGROUND, "images\\background.jpg");
        imagePut(Images.APPLE, "images\\apple.png");
        imagePut(Images.MUSHROOM, "images\\mushroom.png");
        imagePut(Images.GRASS, "images\\space.png");
        imagePut(Images.SNAKE_RIGHT_BOTTOM, "images\\RightBottom.png");
        imagePut(Images.SNAKE_LEFT_BOTTOM, "images\\LeftBottom.png");
        imagePut(Images.SNAKE_LEFT_TOP, "images\\LeftTop.png");
        imagePut(Images.SNAKE_RIGHT_TOP, "images\\RightTop.png");
        imagePut(Images.SNAKE_BODY_HORIZONTAL, "images\\SnakeBodyHorizontal.png");
        imagePut(Images.SNAKE_BODY_VERTICAL, "images\\SnakeBodyVertical.png");
        imagePut(Images.TAIL_BOTTOM, "images\\TailBottom.png");
        imagePut(Images.TAIL_TOP, "images\\TailTop.png");
        imagePut(Images.TAIL_LEFT,"images\\TailLeft.png");
        imagePut(Images.TAIL_RIGHT,"images\\TailRight.png");
        imagePut(Images.HEAD_BOTTOM,"images\\HeadBottom.png");
        imagePut(Images.HEAD_TOP,"images\\HeadTop.png");
        imagePut(Images.HEAD_RIGHT,"images\\HeadRight.png");
        imagePut(Images.HEAD_LEFT,"images\\HeadLeft.png");
        imagePut(Images.ALCOHOL,"images\\blueMushroom.png");
        imagePut(Images.POISON,"images\\poison.png");
        imagePut(Images.BLOOD,"images\\bang.png");
        imagePut(Images.RED_WALL, "images\\RedWall.png");
        imagePut(Images.DARK_WALL, "images\\DarkWall.png");
        imagePut(Images.PORTAL, "images\\portal.png");

        imagePut(Images.INVALID_SNAKE_RIGHT_BOTTOM, "images\\blueRightBottom.png");
        imagePut(Images.INVALID_SNAKE_LEFT_BOTTOM, "images\\blueLeftBottom.png");
        imagePut(Images.INVALID_SNAKE_LEFT_TOP, "images\\blueLeftTop.png");
        imagePut(Images.INVALID_SNAKE_RIGHT_TOP, "images\\blueRightTop.png");
        imagePut(Images.INVALID_SNAKE_BODY_HORIZONTAL, "images\\blueBodyHorizontal.png");
        imagePut(Images.INVALID_SNAKE_BODY_VERTICAL, "images\\blueBodyVertical.png");
        imagePut(Images.INVALID_TAIL_BOTTOM, "images\\blueTailBottom.png");
        imagePut(Images.INVALID_TAIL_TOP, "images\\blueTailTop.png");
        imagePut(Images.INVALID_TAIL_LEFT,"images\\blueTailLeft.png");
        imagePut(Images.INVALID_TAIL_RIGHT,"images\\blueTailRight.png");
        imagePut(Images.INVALID_HEAD_BOTTOM,"images\\blueHeadBottom.png");
        imagePut(Images.INVALID_HEAD_TOP,"images\\blueHeadTop.png");
        imagePut(Images.INVALID_HEAD_RIGHT,"images\\blueHeadRight.png");
        imagePut(Images.INVALID_HEAD_LEFT,"images\\blueHeadLeft.png");

        scene.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case RIGHT:
                    ThreadsController.controllerDirection = Direction.RIGHT;
                    break;
                case UP:
                    ThreadsController.controllerDirection = Direction.UP;
                    break;
                case LEFT:
                    ThreadsController.controllerDirection = Direction.LEFT;
                    break;
                case DOWN:
                    ThreadsController.controllerDirection = Direction.DOWN;
                    break;
                default:
                    break;
            }
        });

        ThreadsController threadsController = new ThreadsController(this, selectedFile.getPath());
        threadsController.start();
    }

    private void imagePut(Images enumImage, String pathName) {
        Image image = new Image(pathName);
        this.images.put(enumImage, image);
    }

    public void repaint(Field field) {
        graphics.drawImage(images.get(Images.BACKGROUND),0,  0, 600, 600);
        int columnsCount = 20;
        int rowsCount = 20;
        for (int i = 0; i < rowsCount; i++)
            for (int j = 0; j < columnsCount; j++) {
                Entity entity = field.getObjectAt(i, j);
                if (entity != null)
                    paintComponent(j, i, images.get(entity.image));
            }
    }

    private void paintComponent(int x, int y, Image image) {
        graphics.drawImage(image,x * 30, y * 30, 30, 30);
    }
}