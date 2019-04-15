package controller;

import model.Game;
import view.View;
import view.Window;

import java.util.Objects;

public class ThreadsController extends Thread {
    private final Game game;
    public static Direction controllerDirection = Direction.RIGHT;
    private final View window;
    private boolean inGame = true;


    public ThreadsController(View _window, String path) {
        game = new Game(path);
        window = Objects.requireNonNullElseGet(_window, Window::new);
    }

    public void run() {
        while (inGame) {
            window.repaint(game.field);
            if (game.levelChanged) {
                controllerDirection = Direction.RIGHT;
                game.levelChanged = false;
            }
            inGame = game.update(controllerDirection);
            pause();
        }
        System.out.println("Game over!");
    }

    private void pause() {
        try {
            long speed = 250;
            sleep(speed);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}