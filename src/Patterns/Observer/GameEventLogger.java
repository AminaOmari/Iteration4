package Patterns.Observer;

/**
 * Concrete Observer that logs game events to console.
 * Demonstrates Observer pattern implementation.
 * Can be used for debugging or game statistics tracking.
 * 
 * @author Team Rhino
 * @version 3.0 - Iteration 3
 */
public class GameEventLogger implements GameObserver {
    private boolean enabled;
    
    /**
     * Creates a new game event logger.
     */
    public GameEventLogger() {
        this.enabled = true;
    }
    
    /**
     * Enables or disables logging.
     * 
     * @param enabled true to enable logging
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    @Override
    public void onScoreChanged(int playerNumber, int newScore, int change) {
        if (enabled) {
            String changeStr = change >= 0 ? "+" + change : String.valueOf(change);
            System.out.println("[EVENT] Player " + playerNumber + " score changed: " + 
                             changeStr + " (New: " + newScore + ")");
        }
    }
    
    @Override
    public void onLivesChanged(int newLives, int change) {
        if (enabled) {
            String changeStr = change >= 0 ? "+" + change : String.valueOf(change);
            System.out.println("[EVENT] Lives changed: " + changeStr + " (Remaining: " + newLives + ")");
        }
    }
    
    @Override
    public void onGameOver(boolean won, String message) {
        if (enabled) {
            System.out.println("[EVENT] Game Over! Won: " + won + " | " + message);
        }
    }
    
    @Override
    public void onTurnChanged(int playerNumber) {
        if (enabled) {
            System.out.println("[EVENT] Turn changed to Player " + playerNumber);
        }
    }
}