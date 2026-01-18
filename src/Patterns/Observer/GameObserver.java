package Patterns.Observer;

/**
 * Observer interface for game events.
 * Implements Observer design pattern.
 * Observers are notified when game state changes.
 * 
 * @author Team Rhino
 * @version 3.0 - Iteration 3
 */
public interface GameObserver {
    /**
     * Called when a player's score changes.
     * 
     * @param playerNumber Player number (1 or 2)
     * @param newScore The new score
     * @param change Amount of change (can be negative)
     */
    void onScoreChanged(int playerNumber, int newScore, int change);
    
    /**
     * Called when shared lives change.
     * 
     * @param newLives Current number of lives
     * @param change Amount of change (can be negative)
     */
    void onLivesChanged(int newLives, int change);
    
    /**
     * Called when game is over.
     * 
     * @param won Whether the game was won
     * @param message End game message
     */
    void onGameOver(boolean won, String message);
    
    /**
     * Called when turn changes.
     * 
     * @param playerNumber New active player (1 or 2)
     */
    void onTurnChanged(int playerNumber);
}