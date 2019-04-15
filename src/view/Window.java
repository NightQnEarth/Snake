package view;

import controller.KeyboardListener;
import model.Entity;
import model.Field;
import model.Images;
import model.Tuple;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class Window extends JFrame implements View {
    private static final long serialVersionUID = -2542001418764869760L;
    private static final int rowsCount = 20;
    private static final int columnsCount = 20;
    private final Graphics2D graphics;
    public final Map<Images, BufferedImage> images = new HashMap<>();
    private final Color transparent = new Color(0, 0, 0, 0);
    private Field previousStepField;
    public int repaintedCount = 0;

    public Window() {
        this.setTitle("Snake");
        this.setSize(660, 690);
        this.moveCenter();
        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.addKeyListener(new KeyboardListener());
        this.graphics = (Graphics2D) getGraphics();
        JPanel panel = new JPanel();
        panel.paintComponents(graphics);

        imagePut(Images.BACKGROUND, "src\\images" + File.separator + "background.jpg");
        imagePut(Images.APPLE, "src\\images\\apple.png");
        imagePut(Images.MUSHROOM, "src\\images\\mushroom.png");
        imagePut(Images.GRASS, "src\\images\\space.png");
        imagePut(Images.SNAKE_RIGHT_BOTTOM, "src\\images\\RightBottom.png");
        imagePut(Images.SNAKE_LEFT_BOTTOM, "src\\images\\LeftBottom.png");
        imagePut(Images.SNAKE_LEFT_TOP, "src\\images\\LeftTop.png");
        imagePut(Images.SNAKE_RIGHT_TOP, "src\\images\\RightTop.png");
        imagePut(Images.SNAKE_BODY_HORIZONTAL, "src\\images\\SnakeBodyHorizontal.png");
        imagePut(Images.SNAKE_BODY_VERTICAL, "src\\images\\SnakeBodyVertical.png");
        imagePut(Images.TAIL_BOTTOM, "src\\images\\TailBottom.png");
        imagePut(Images.TAIL_TOP, "src\\images\\TailTop.png");
        imagePut(Images.TAIL_LEFT, "src\\images\\TailLeft.png");
        imagePut(Images.TAIL_RIGHT, "src\\images\\TailRight.png");
        imagePut(Images.HEAD_BOTTOM, "src\\images\\HeadBottom.png");
        imagePut(Images.HEAD_TOP, "src\\images\\HeadTop.png");
        imagePut(Images.HEAD_RIGHT, "src\\images\\HeadRight.png");
        imagePut(Images.HEAD_LEFT, "src\\images\\HeadLeft.png");
        imagePut(Images.ALCOHOL, "src\\images\\blueMushroom.png");
        imagePut(Images.POISON, "src\\images\\poison.png");
        imagePut(Images.BLOOD, "src\\images\\bang.png");
        imagePut(Images.RED_WALL, "src\\images\\RedWall.png");
        imagePut(Images.DARK_WALL, "src\\images\\DarkWall.png");
        imagePut(Images.PORTAL, "src\\images\\portal.png");
    }

    private void imagePut(Images enumImage, String pathName) {
        BufferedImage image = null;
        try {
            image = ImageIO.read(new File(pathName));
        }
        catch (IOException ignored) {
        }
        this.images.put(enumImage, image);
    }

    private void moveCenter() {
        Dimension screenDimension = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (int) ((screenDimension.getWidth() - this.getWidth()) / 2);
        int y = (int) ((screenDimension.getHeight() - this.getHeight()) / 2);
        this.setLocation(x, y);
    }

    public void repaint(Field field) {
        if (repaintedCount <= 2)
            repaintedCount++;

        if(previousStepField == null) {
            previousStepField = new Field(20,20);
            repaintWithoutCheck(field);
            return;
        }

        for (int i = 0; i < rowsCount; i++)
            for (int j = 0; j < columnsCount; j++) {
                Entity entity = field.getObjectAt(i, j);
                if (entity == previousStepField.getObjectAt(i,j)) {
                    if (repaintedCount == 2 && entity == null)
                        paintComponent(j, i, images.get(Images.GRASS));
                    continue;
                }
                paintComponent(j, i, images.get(Images.GRASS));

                if (entity != null)
                    paintComponent(j, i, images.get(entity.image));
                previousStepField.spawn(entity,new Tuple(i,j));
            }
    }

    private void repaintWithoutCheck(Field field) {
        for (int i = 0; i < rowsCount; i++)
            for (int j = 0; j < columnsCount; j++) {
                Entity entity = field.getObjectAt(i, j);
                paintComponent(j, i, images.get(Images.GRASS));
                if (entity != null)
                    paintComponent(j, i, images.get(entity.image));
            }
    }

    private void paintComponent(int x, int y, BufferedImage image) {
        graphics.drawImage(image, x * 30 + 30, y * 30 + 60, 30, 30,
                transparent, null);
    }
}