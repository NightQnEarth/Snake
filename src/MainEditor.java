import levelEditorController.levelEditorThreadsController;

public class MainEditor {
    public static void main(String[] args) {
        levelEditorThreadsController threadsController = new levelEditorThreadsController();
        threadsController.start();
    }
}