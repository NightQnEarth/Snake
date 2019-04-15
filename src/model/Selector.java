package model;

public class Selector {

    private final Entity[][] selector;

    public Selector() {
        this.selector = new Entity[4][3];

        Entity apple = new Food();
        apple.image = Images.APPLE;

        Entity mushroom = new Food();
        mushroom.image = Images.MUSHROOM;

        selector[0][0] = apple;
        selector[0][1] = mushroom;
        selector[0][2] = new Poison();
        selector[1][0] = new Alcohol();
        selector[1][1] = new SnakeEntity(Images.HEAD_RIGHT, 0);
        selector[1][2] = new SnakeEntity(Images.SNAKE_BODY_HORIZONTAL, 0);
        selector[2][0] = new SnakeEntity(Images.TAIL_RIGHT, 0);
        selector[2][1] = new RedWall();
        selector[2][2] = new DarkWall();
        selector[3][0] = new Portal();
    }

    public Entity getObjectAt(int row, int column) {
        if (row < 4 && column < 3)
            return selector[row][column];
        else
            return null;
    }

    public int length() {
        return selector.length;
    }
}