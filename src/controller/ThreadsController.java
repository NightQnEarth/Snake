package controller;

import model.Game;
import view.View;
import view.Window;

public class ThreadsController extends Thread {
    private Game game;
    private long speed = 250;
    public static Direction controllerDirection = Direction.RIGHT;
    private View window;
    private boolean inGame = true;
    public String path;


    public ThreadsController(View _window, String path) {
        game = new Game(path);
        if (_window == null)
            window = new Window();
        else
            window = _window;
        this.path = path;
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
            sleep(speed);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}