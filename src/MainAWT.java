import controller.ThreadsController;

public class MainAWT {
	public static void main(String[] args) {
		ThreadsController threadsController = new ThreadsController(null, "Level1.ser");
		threadsController.start();
	}
}