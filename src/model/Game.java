package model;

import controller.Direction;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;

public class Game {
    public Field field;
    public Snake snake;
    public boolean shouldUpdate = true;
    public int counter = 0;
    private int alcoholCount;
    private int poisonCount;
    public ArrayList<String> levels = new ArrayList<>();
    public int currentLevel = 0;
    public Invalid invalid;
    public boolean levelChanged = false;

    public Game(String path) {
        levels.add("Level1.ser");
        levels.add("Level2.ser");
        levels.add("Level3.ser");

        if (path != null) {
            field = deserializeField(path);
            snake = new Snake(field);
            invalid = new Invalid(field);
        }
        else
            System.exit(0);
    }

    public boolean update(Direction directionFromController) {
        if (snake.needChangeLevel) {
            counter = 0;
            currentLevel++;
            if (currentLevel < levels.size()) {
                field = deserializeField(levels.get(currentLevel));
                snake.field = field;
                snake.entities = field.field;
            }
            if (currentLevel == levels.size() - 1)
                snake.lastLevel = true;

            levelChanged = true;
            snake.needChangeLevel = false;
        }

        counter++;

        if (shouldUpdate) {
            if (counter % 50 == 0)
                if (poisonCount < 15) {
                    field.spawn(new Poison());
                    poisonCount++;
                }

            if (counter % 40 == 0)
                if (alcoholCount < 25) {
                    field.spawn(new Alcohol());
                    alcoholCount++;
                }
            if (!snake.ShouldInvalidDie)
                invalid.update();
            if (!invalid.ShouldSnakeDie)
                if (snake.update(directionFromController)) {
                    if (snake.ShouldInvalidDie) {
                        int count = invalid.invalidPositions.size() + 1;
                        for (int i = 0; i < count; i++)
                            invalid.isDying();
                    }
                    return true;
                }

            shouldUpdate = false;
        }
        snake.isDying();
        return snake.isDying();
    }

    private Field deserializeField(String filename) {
        try {
            FileInputStream fileIn = new FileInputStream(filename);
            ObjectInputStream in = new ObjectInputStream(fileIn);
            Field field = (Field) in.readObject();
            in.close();
            fileIn.close();

            field.spawn(new InvalidEntity(Images.INVALID_HEAD_RIGHT, 0),
                    new Tuple(1, 3));
            field.spawn(new InvalidEntity(Images.INVALID_SNAKE_BODY_HORIZONTAL, 0),
                    new Tuple(1, 2));
            field.spawn(new InvalidEntity(Images.INVALID_TAIL_RIGHT, 0),
                    new Tuple(1, 1));
            invalid = new Invalid(field);
            return field;
        }
        catch (IOException i) {
            i.printStackTrace();
            return null;
        }
        catch (ClassNotFoundException c) {
            System.out.println(filename + " not found");
            c.printStackTrace();
            return null;
        }

    }
}