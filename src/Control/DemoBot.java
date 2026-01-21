package Control;

import Model.Board;
import Model.Tile;
import java.util.Random;
import javax.swing.SwingUtilities;

/**
 * AI Bot Logic for "Play with AI" Demo Mode.
 * <p>
 * This class runs on a separate thread and simulates a second player.
 * It periodically checks if it's Player 2's turn and makes random valid moves.
 * <p>
 * 
 * @author Team Rhino
 * @version 1.0
 */
public class DemoBot implements Runnable {
    private final GameController controller;
    private final Random random = new Random();
    private volatile boolean running = true;

    public DemoBot(GameController controller) {
        this.controller = controller;
    }

    public void stop() {
        running = false;
    }

    @Override
    public void run() {
        try {
            while (running && !controller.isGameOver()) {
                Thread.sleep(1500); // Wait 1.5 second between moves

                SwingUtilities.invokeLater(() -> {
                    if (running && !controller.isGameOver()) {
                        // Only play if it's Player 2's turn (Index 1)
                        if (controller.getCurrentPlayerIndex() == 1) {
                            makeMove();
                        }
                    }
                });
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void makeMove() {
        // Simple random move logic for demo
        Board board = controller.getCurrentPlayerBoard();
        int size = board.getSize();
        int attempts = 0;

        while (attempts < 50) {
            int r = random.nextInt(size);
            int c = random.nextInt(size);
            Tile tile = board.getTile(r, c);

            if (!tile.isRevealed() && !tile.isFlagged()) {
                // Determine which board (1 or 2)
                int boardNum = controller.getCurrentPlayerIndex() + 1;
                controller.handleTileClick(boardNum, r, c);
                break;
            }
            attempts++;
        }
    }
}
