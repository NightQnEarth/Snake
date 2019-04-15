package model;

public class SnakeEntity extends Entity {
    public int angle;

    public SnakeEntity(Images image, int angle) {
        this.image = image;
        this.angle = angle;
    }
}
