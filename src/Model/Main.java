package Model;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import Control.GameController;
import View.GameView;

/**
 * Main entry point for the MineSweeper game. Initializes the GUI and starts the
 * application.
 *
 * @author Rhino Team
 * @version 3.0 - Iteration 3
 */
public class Main {

	/**
	 * Main method - entry point of the application.
	 *
	 * @param args Command line arguments (not used)
	 */
	public static void main(String[] args) {
		// Set look and feel to system default for better appearance
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			// If system look and feel fails, continue with default
			System.out.println("Using default look and feel");
		}

		// Run GUI on Event Dispatch Thread
		SwingUtilities.invokeLater(() -> {
			// Create view
			GameView view = new GameView();

			// Create controller (connects to view)
			new GameController(view);

			// Show the window
			view.setVisible(true);

			System.out.println("MineSweeper - Rhino Team");
			System.out.println("Iteration 3 - Core Game Logic");

		});
	}
}