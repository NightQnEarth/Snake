package controller;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class KeyboardListener extends KeyAdapter {

    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_RIGHT:
                    ThreadsController.controllerDirection = Direction.RIGHT;
                break;
            case KeyEvent.VK_UP:
                    ThreadsController.controllerDirection = Direction.UP;
                break;
            case KeyEvent.VK_LEFT:
                    ThreadsController.controllerDirection = Direction.LEFT;
                break;
            case KeyEvent.VK_DOWN:
                    ThreadsController.controllerDirection = Direction.DOWN;
                break;
            default:
                break;
        }
    }

}
