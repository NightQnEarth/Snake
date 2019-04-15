package levelEditorController;

import model.Field;
import view.EditorView;
import view.View;

public class levelEditorThreadsController extends Thread {
    private final Field field;
    private final View window;

    public levelEditorThreadsController() {
        field = new Field(20, 20);
        window = new EditorView(field);
    }

    public void run() {
        window.repaint(field);
    }
}