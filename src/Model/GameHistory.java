package Model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents a single game history record. Stores information about a completed
 * game.
 * 
 * @author Team Rhino
 * @version 3.0 - Iteration 3
 */
public class GameHistory {
	private final String player1Name;
	private final String player2Name;
	private final int player1Score;
	private final int player2Score;
	private final int totalScore;
	private final Difficulty difficulty;
	private final String winner;
	private final LocalDateTime timestamp;
	private final int remainingLives;

	/**
	 * Creates a new game history record.
	 * 
	 * @param player1Name    Name of player 1
	 * @param player2Name    Name of player 2
	 * @param player1Score   Score of player 1
	 * @param player2Score   Score of player 2
	 * @param totalScore     Total combined score
	 * @param difficulty     Game difficulty
	 * @param winner         Name of winner (or "Tie")
	 * @param remainingLives Lives remaining at game end
	 */
	public GameHistory(String player1Name, String player2Name, int player1Score, int player2Score, int totalScore,
			Difficulty difficulty, String winner, int remainingLives) {
		this(player1Name, player2Name, player1Score, player2Score, totalScore, difficulty, winner, LocalDateTime.now(),
				remainingLives);
	}

	/**
	 * Internal constructor used when loading history from CSV.
	 */
	private GameHistory(String player1Name, String player2Name, int player1Score, int player2Score, int totalScore,
			Difficulty difficulty, String winner, LocalDateTime timestamp, int remainingLives) {
		this.player1Name = player1Name;
		this.player2Name = player2Name;
		this.player1Score = player1Score;
		this.player2Score = player2Score;
		this.totalScore = totalScore;
		this.difficulty = difficulty;
		this.winner = winner;
		this.timestamp = timestamp;
		this.remainingLives = remainingLives;
	}

	/**
	 * Creates a game history record from a GameState.
	 * 
	 * @param gameState The completed game state
	 * @return New GameHistory instance
	 */
	public static GameHistory fromGameState(GameState gameState) {
		String winner = gameState.isGameWon() ? "Won" : "Lost";

		return new GameHistory(gameState.getPlayer1().getName(), gameState.getPlayer2().getName(),
				gameState.getPlayer1().getScore(), gameState.getPlayer2().getScore(), gameState.getTotalScore(),
				gameState.getDifficulty(), winner, gameState.getSharedLives());
	}

	/**
	 * Converts this history to a CSV line.
	 * 
	 * @return CSV formatted string
	 */
	public String toCsvLine() {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		return String.format("%s,%s,%s,%d,%d,%d,%s,%s,%d", timestamp.format(formatter), player1Name, player2Name,
				player1Score, player2Score, totalScore, difficulty.name(), winner, remainingLives);
	}

	/**
	 * Creates a GameHistory from a CSV line.
	 * 
	 * @param csvLine CSV formatted string
	 * @return GameHistory instance or null if parsing fails
	 */
	public static GameHistory fromCsvLine(String csvLine) {
		try {
			String[] parts = csvLine.split(",");
			if (parts.length != 9) {
				return null;
			}

			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
			LocalDateTime ts = LocalDateTime.parse(parts[0].trim(), formatter);

			return new GameHistory(parts[1], // player1Name
					parts[2], // player2Name
					Integer.parseInt(parts[3]), // player1Score
					Integer.parseInt(parts[4]), // player2Score
					Integer.parseInt(parts[5]), // totalScore
					Difficulty.valueOf(parts[6]), // difficulty
					parts[7], // winner
					ts, Integer.parseInt(parts[8]) // remainingLives
			);
		} catch (Exception e) {
			System.err.println("Error parsing CSV line: " + e.getMessage());
			return null;
		}
	}

	// Getters
	public String getPlayer1Name() {
		return player1Name;
	}

	public String getPlayer2Name() {
		return player2Name;
	}

	public int getPlayer1Score() {
		return player1Score;
	}

	public int getPlayer2Score() {
		return player2Score;
	}

	public int getTotalScore() {
		return totalScore;
	}

	public Difficulty getDifficulty() {
		return difficulty;
	}

	public String getWinner() {
		// Handle legacy data or new format
		if ("Won".equalsIgnoreCase(winner) || "Lost".equalsIgnoreCase(winner)) {
			return winner;
		}
		// If legacy name/Tie: infer result from lives
		return remainingLives > 0 ? "Won together" : "Lost together";
	}

	public LocalDateTime getTimestamp() {
		return timestamp;
	}

	public int getRemainingLives() {
		return remainingLives;
	}

	@Override
	public String toString() {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
		return String.format("%s | %s vs %s | Winner: %s | Total Score: %d | Difficulty: %s",
				timestamp.format(formatter), player1Name, player2Name, winner, totalScore, difficulty.name());
	}
}
