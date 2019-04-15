package view;

import model.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.HashMap;
import java.util.Map;


public class EditorView extends JFrame implements View {
    private static final long serialVersionUID = -2542001418764869760L;
    private static int rowsCount = 20;
    private static int columnsCount = 20;
    private static int verticalOffset = 32;
    private static int cellSize = 30;
    private static int fieldWidth = cellSize * columnsCount;
    private static int fieldHeight = cellSize * rowsCount;
    private static int panelSize = 100;
    private Graphics2D graphics;
    private JPanel panel;
    private JPanel buttonPanel;
    private Entity ent;
    private Selector selector;
    public Map<Images, BufferedImage> images = new HashMap<>();
    private Color transparent = new Color(0, 0, 0, 0);

    public EditorView(Field field) {
        this.setTitle("Snake");
        this.setSize(fieldWidth + panelSize, fieldHeight + verticalOffset);
        this.moveCenter();
        this.setResizable(false);
        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.graphics = (Graphics2D)getGraphics();
        this.panel = new JPanel();
        this.ent = new Food();
        this.selector = new Selector();

        buttonPanel = new JPanel();
        TextField textField = new TextField(8);

        Button button = new Button("Save!");
        button.addActionListener(e -> serializeField(field, textField.getText()));

        buttonPanel.add(button);
        buttonPanel.add(textField);
        this.add(buttonPanel);

        panel.paintComponents(graphics);

        this.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if(e.getX() <= fieldWidth && SwingUtilities.isLeftMouseButton(e)) {
                    int row = (e.getY() - verticalOffset) / cellSize;
                    int column = e.getX() / cellSize;
                    if (ent != null && field.getObjectAt(row, column) == null) {
                        field.spawn(ent, new Tuple(row, column));
                        paintComponent(column, row, images.get(ent.image));
                    }
                    else if (ent != null) {
                        field.spawn(ent, new Tuple(row, column));
                        repaint(field);
                    }
                }
                else if (e.getX() <= fieldWidth && SwingUtilities.isRightMouseButton(e)) {
                    field.resetCell((e.getY() - verticalOffset) / cellSize,
                            e.getX() / cellSize);
                    repaint(field);
                }
                else {
                    int row = (e.getY() - verticalOffset) / cellSize;
                    int column = (e.getX() - fieldWidth) / cellSize;
                    ent = selector.getObjectAt(row, column);
                    if (ent != null)
                        paintSelectorComponent(column, row, images.get(ent.image));
                    else if (SwingUtilities.isRightMouseButton(e))
                        repaintSelector(selector);
                }
            }
        });

        this.setVisible(false);
        this.setVisible(true);

        imagePut(Images.BACKGROUND, "src\\images\\background.jpg");
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
        imagePut(Images.TAIL_LEFT,"src\\images\\TailLeft.png");
        imagePut(Images.TAIL_RIGHT,"src\\images\\TailRight.png");
        imagePut(Images.HEAD_BOTTOM,"src\\images\\HeadBottom.png");
        imagePut(Images.HEAD_TOP,"src\\images\\HeadTop.png");
        imagePut(Images.HEAD_RIGHT,"src\\images\\HeadRight.png");
        imagePut(Images.HEAD_LEFT,"src\\images\\HeadLeft.png");
        imagePut(Images.ALCOHOL,"src\\images\\blueMushroom.png");
        imagePut(Images.POISON,"src\\images\\poison.png");
        imagePut(Images.BLOOD,"src\\images\\bang.png");
        imagePut(Images.RED_WALL, "src\\images\\RedWall.png");
        imagePut(Images.SELECTOR_BACKGROUND, "src\\images\\white_tree_color.jpg");
        imagePut(Images.DARK_WALL, "src\\images\\DarkWall.png");
        imagePut(Images.PORTAL, "src\\images\\portal.png");

        repaintSelector(selector);
    }

    private void imagePut(Images enumImage, String pathName) {
        BufferedImage image = null;
        try {
            image = ImageIO.read(new File(pathName));
        }
        catch (IOException e) {
        }
        this.images.put(enumImage, image);
    }

    private void moveCenter() {
        Dimension screenDimension = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (int)((screenDimension.getWidth() - this.getWidth()) / 2);
        int y = (int)((screenDimension.getHeight() - this.getHeight()) / 2);
        this.setLocation(x, y);
    }

    public void repaint(Field field) {
        graphics.drawImage(images.get(Images.BACKGROUND), 0,  verticalOffset, 600,
                600, transparent, null);
        for (int i = 0; i < rowsCount; i++)
            for (int j = 0; j < columnsCount; j++) {
                Entity entity = field.getObjectAt(i, j);
                if (entity != null)
                    paintComponent(j, i, images.get(entity.image));
            }
    }

    public void repaintSelector(Selector selector) {
        graphics.drawImage(images.get(Images.SELECTOR_BACKGROUND), fieldWidth, verticalOffset,
                panelSize, fieldHeight, transparent, null);

        for (int i = 0; i < selector.length(); i++)
            for (int j = 0; j < selector.length(); j++) {
                Entity entity = selector.getObjectAt(i, j);
                if (entity != null)
                    paintSelectorComponent(j, i, images.get(entity.image));
            }
    }

    private void paintComponent(int x, int y, BufferedImage image) {
        graphics.drawImage(image,x * 30 + 5, y * 30 + verticalOffset, 30, 30,
                transparent, null);
    }

    private void paintSelectorComponent(int x, int y, BufferedImage image) {
        graphics.drawImage(image,x * 30 + fieldWidth + 5, y * 30 + verticalOffset, 30, 30,
                transparent, null);
    }

    private void serializeField(Field field, String filename){
        try {
            if (!filename.equals("")) {
                filename = filename + ".ser";
                FileOutputStream fileOut = new FileOutputStream(filename);
                ObjectOutputStream out = new ObjectOutputStream(fileOut);
                out.writeObject(field);
                out.close();
                fileOut.close();
                System.out.printf("Serialized data is saved in " + filename);
            }
        }
        catch (IOException i) {
            i.printStackTrace();
        }
    }
}