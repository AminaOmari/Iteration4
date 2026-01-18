package Model;

import java.awt.Color;

/**
 * Represents a player in the MineSweeper game. Each player has a name, score,
 * and associated board color.
 */
public class Player {
	private final String name;
	private int score;
	private final Color boardColor;
	private final int playerNumber;

	/**
	 * Creates a new player with the specified name and player number.
	 * 
	 * @param name         Player's name
	 * @param playerNumber Player number (1 or 2)
	 */
	public Player(String name, int playerNumber) {
		this.name = name;
		this.playerNumber = playerNumber;
		this.score = 0;
		// Player 1 gets blue, Player 2 gets green
		this.boardColor = playerNumber == 1 ? new Color(70, 130, 180) : new Color(60, 179, 113);
	}

	/**
	 * Adds points to the player's score.
	 * 
	 * @param points Points to add (can be negative)
	 */
	public void addScore(int points) {
		this.score += points;
	}

	/**
	 * Resets the player's score to zero.
	 */
	public void resetScore() {
		this.score = 0;
	}

	// Getters
	public String getName() {
		return name;
	}

	public int getScore() {
		return score;
	}

	public Color getBoardColor() {
		return boardColor;
	}

	public int getPlayerNumber() {
		return playerNumber;
	}

	@Override
	public String toString() {
		return String.format("Player %d: %s (Score: %d)", playerNumber, name, score);
	}
}
