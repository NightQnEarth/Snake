import controller.ThreadsController;

public class MainAWT {
	public static void main(String[] args) {
		ThreadsController threadsController = new ThreadsController(null, "1.ser");
		threadsController.start();
	}
}