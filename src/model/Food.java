package model;

import java.util.Random;

public class Food extends Entity {
    public Food() {
        Images[] foodList = new Images[]{Images.APPLE, Images.MUSHROOM};
        Random random = new Random();

        this.image = foodList[random.nextInt(foodList.length)];
    }
}