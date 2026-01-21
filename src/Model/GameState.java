package Model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import Patterns.Observer.GameObserver;

/**
 * Manages the overall state of the MineSweeper game. Tracks players, turns,
 * shared lives, and game status. Based on prototype design from React
 * implementation.
 *
 * Updated in Iteration 2 to use Tile hierarchy with polymorphism and correct
 * QuestionTile scoring mechanics.
 *
 * Updated in Iteration 3 with Observer pattern support for game events.
 *
 * @author Team Rhino
 * @version 3.0 - Iteration 3
 */
public class GameState {
	private final Player player1;
	private final Player player2;
	private final Board board1;
	private final Board board2;
	private final Difficulty difficulty;
	private QuestionBank questionBank;
	private SurpriseManager surpriseManager;

	private int currentPlayerIndex; // 0 for player1, 1 for player2
	private int sharedLives;
	private int totalScore; // Shared score as in prototype
	private boolean gameOver;
	private boolean gameWon;
	private String gameEndMessage;

	// Pending questions for each player
	private List<QuestionTile> player1PendingQuestions;
	private List<QuestionTile> player2PendingQuestions;

	// Observer pattern - list of observers
	private List<GameObserver> observers;

	// Random instance for 50/50 logic in question answers
	private Random random;

	// Maximum lives cap
	private static final int MAX_LIVES = 10;

	/**
	 * Creates a new game state with the specified players and difficulty.
	 *
	 * @param player1Name Name of player 1
	 * @param player2Name Name of player 2
	 * @param difficulty  Game difficulty level
	 */
	public GameState(String player1Name, String player2Name, Difficulty difficulty) {
		this.difficulty = difficulty;
		this.player1 = new Player(player1Name, 1);
		this.player2 = new Player(player2Name, 2);
		this.board1 = new Board(difficulty);
		this.board2 = new Board(difficulty);
		this.currentPlayerIndex = 0;
		this.sharedLives = difficulty.getInitialLives();
		this.totalScore = 0;
		this.gameOver = false;
		this.gameWon = false;
		this.gameEndMessage = "";
		this.player1PendingQuestions = new ArrayList<>();
		this.player2PendingQuestions = new ArrayList<>();
		this.questionBank = QuestionBank.getInstance();
		this.surpriseManager = new SurpriseManager();

		this.observers = new ArrayList<>();
		this.random = new Random();

		// Assign questions to question tiles
		assignQuestionsToTiles();
	}

	/**
	 * Assigns random questions to all QuestionTiles on both boards.
	 */
	private void assignQuestionsToTiles() {
		assignQuestionsToBoard(board1);
		assignQuestionsToBoard(board2);
	}

	/**
	 * Assigns questions to all QuestionTiles on a specific board.
	 *
	 * @param board The board to assign questions to
	 */
	private void assignQuestionsToBoard(Board board) {
		int size = board.getSize();
		for (int row = 0; row < size; row++) {
			for (int col = 0; col < size; col++) {
				Tile tile = board.getTile(row, col);
				if (tile.isQuestion()) {
					QuestionTile qTile = (QuestionTile) tile;
					Question question = questionBank.getRandomQuestionMixed(difficulty);
					qTile.setQuestion(question);
				}
			}
		}
	}

	/**
	 * Gets the current active player.
	 *
	 * @return The player whose turn it is
	 */
	public Player getCurrentPlayer() {
		return currentPlayerIndex == 0 ? player1 : player2;
	}

	/**
	 * Gets the board of the current active player.
	 *
	 * @return The current player's board
	 */
	public Board getCurrentBoard() {
		return currentPlayerIndex == 0 ? board1 : board2;
	}

	/**
	 * Switches turn to the other player.
	 */
	public void switchTurn() {
		currentPlayerIndex = (currentPlayerIndex + 1) % 2;
		notifyTurnChanged(currentPlayerIndex + 1);
	}

	/**
	 * Reduces shared lives by the specified amount. Checks for game over condition.
	 *
	 * @param amount Number of lives to lose
	 */
	public void loseLives(int amount) {
		int oldLives = sharedLives;
		sharedLives -= amount;
		if (sharedLives <= 0) {
			sharedLives = 0;
			gameOver = true;
			gameWon = false;
			gameEndMessage = "Game Over! No lives remaining.";
			notifyGameOver(false, gameEndMessage);
		}
		notifyLivesChanged(sharedLives, sharedLives - oldLives);
	}

	/**
	 * Adds lives to the shared pool. If lives exceed MAX_LIVES, converts excess to
	 * bonus points.
	 *
	 * @param amount Number of lives to add
	 */
	public void addLives(int amount) {
		if (amount < 0) {
			loseLives(-amount);
			return;
		}
		int oldLives = sharedLives;
		int newLives = sharedLives + amount;
		if (newLives > MAX_LIVES) {
			int excessLives = newLives - MAX_LIVES;
			int bonusPoints = excessLives * difficulty.getQuestionPoints();
			addScore(bonusPoints);
			sharedLives = MAX_LIVES;
		} else {
			sharedLives = newLives;
		}
		notifyLivesChanged(sharedLives, sharedLives - oldLives);
	}

	/**
	 * Adds points to the total score.
	 *
	 * IMPORTANT FIX: - Do NOT clamp score to 0. - Allow negative totalScore so
	 * penalties show correctly and the game doesn't appear "stuck".
	 *
	 * @param points Points to add (can be negative)
	 */
	public void addScore(int points) {
		// FIX: allow negative shared score
		totalScore = totalScore + points;

		// Also add to current player's individual score for tracking
		Player currentPlayer = getCurrentPlayer();
		int oldScore = currentPlayer.getScore();
		currentPlayer.addScore(points);
		notifyScoreChanged(currentPlayerIndex + 1, currentPlayer.getScore(), points);
	}

	/**
	 * Handles the result of revealing a mine. Reduces lives and switches turn.
	 */
	public void handleMineHit() {
		loseLives(1);
		if (!gameOver) {
			switchTurn();
		}
	}

	public int getActivationCost() {
		return switch (difficulty) {
			case EASY -> 5;
			case MEDIUM -> 8;
			case HARD -> 12;
		};
	}

	/**
	 * Handles revealing a safe tile. Awards points based on tile type.
	 *
	 * @param tile The revealed tile
	 * @return Description of what happened
	 */
	public String handleSafeReveal(Tile tile) {
		String result = "";
		int scoreChange = 1; // Base points for revealing safe tile
		int livesChange = 0;
		boolean shouldSwitchTurn = true;

		if (tile.isQuestion()) {
			// Question tiles don't give immediate points - only when answered
			scoreChange = 0;
			result = "Question tile revealed! Answer it for points and bonuses!";
			shouldSwitchTurn = false;
		} else if (tile.isSurprise()) {
			SurpriseTile surpriseTile = (SurpriseTile) tile;

			if (surpriseTile.getSurprise() == null) {
				Surprise template = surpriseManager.getRandomSurprise();

				// Enforce Rules based on Difficulty
				// Easy: ±8 pts, ±1 life
				// Medium: ±12 pts, ±1 life
				// Hard: ±16 pts, ±1 life
				int diffPoints = difficulty.getSurprisePoints();

				int finalPoints = template.isGood() ? diffPoints : -diffPoints;
				int finalLives = template.isGood() ? 1 : -1;

				Surprise dynamicSurprise = new Surprise(
						template.getId(),
						template.getMessage(),
						finalPoints,
						finalLives,
						template.isGood());

				surpriseTile.setSurprise(dynamicSurprise);
			}

			Surprise surprise = surpriseTile.getSurprise();
			scoreChange = surprise.getPointsEffect();
			livesChange = surprise.getLivesEffect();
			result = surprise.getMessage();

			if (livesChange > 0) {
				addLives(livesChange);
			} else if (livesChange < 0) {
				loseLives(-livesChange);
			}
		} else {
			result = tile.getAdjacentMines() == 0 ? "Empty tile revealed!"
					: "Tile with " + tile.getAdjacentMines() + " adjacent mines.";
			// Per spec 2.4: "When the player reveals a tile[implied empty/number], the turn
			// passes to the second player"
			shouldSwitchTurn = true;
		}

		addScore(scoreChange);

		checkWinCondition();

		if (!gameOver && shouldSwitchTurn) {
			// Check if we should actually switch:
			// Q: "Does simply revealing a number tile pass the turn?"
			// A: The spec says "When a player reveals a tile -> pass turn."
			// BUT: Answering a question/surprise usually lets you continue or passes?
			// The highlighted text says: "When the player REVEALS A TILE, the turn passes
			// to the second player".
			// This likely refers to the standard "move" of revealing a hidden tile.
			// Special tiles (Q/S) might interrupt this flow differently, but for standard
			// tiles (Empty/Number), the turn definitely passes.
			switchTurn();
		}

		return result;
	}

	/**
	 * Handles flag placement on a tile.
	 *
	 * @param tile The flagged tile
	 * @return true if flag was placed on mine
	 */
	public boolean handleFlag(Tile tile) {
		if (tile.isFlagged()) {
			// ✅ Spec:
			// - Flagging a mine: -1 point
			// - Flagging any non-mine (number/empty/question/surprise): -3 points
			if (tile.isMine()) {
				addScore(-1);
				checkWinCondition();
				return true;
			}
			addScore(-3);
			checkWinCondition();
			return false;
		}
		return false;
	}

	/**
	 * Handles answering a question on a QuestionTile. Implements the full
	 * specification scoring matrix based on BOTH game difficulty and question
	 * difficulty levels.
	 *
	 * @param tile    The QuestionTile being activated
	 * @param correct Whether the answer was correct
	 * @return Result message
	 */
	public String handleQuestionAnswer(QuestionTile tile, boolean correct) {
		if (tile.isActivated()) {
			return "This question has already been answered!";
		}

		tile.activate();

		// Get question difficulty level
		Question question = tile.getQuestion();
		String questionLevel = question.getLevel().toUpperCase(); // "EASY", "MEDIUM", "HARD", "EXPERT"

		String result;
		if (correct) {
			result = handleCorrectAnswer(questionLevel);
		} else {
			result = handleWrongAnswer(questionLevel);
		}

		checkWinCondition();
		return result;
	}

	/**
	 * Handles correct answer scoring based on game and question difficulty.
	 */
	private String handleCorrectAnswer(String questionLevel) {
		int reward = 0;
		int lifeBonus = 0;
		boolean mineRevealed = false;
		int tilesRevealed = 0;

		switch (difficulty) {
			case EASY:
				switch (questionLevel) {
					case "EASY":
						reward = 3;
						lifeBonus = 1;
						break;
					case "MEDIUM":
						reward = 6;
						lifeBonus = 0;
						// SPECIAL BONUS: Auto-reveal one random mine
						Board currentBoard = getCurrentBoard();
						mineRevealed = currentBoard.revealRandomMine();
						break;
					case "HARD":
						reward = 10;
						lifeBonus = 0;
						// SPECIAL BONUS: Reveal random 3x3 area
						Board board = getCurrentBoard();
						tilesRevealed = board.revealRandom3x3Area();
						break;
					case "EXPERT":
						reward = 15;
						lifeBonus = 2;
						break;
					default:
						reward = 3;
						lifeBonus = 1;
				}
				break;

			case MEDIUM:
				switch (questionLevel) {
					case "EASY":
						reward = 8;
						lifeBonus = 1;
						break;
					case "MEDIUM":
						reward = 10;
						lifeBonus = 1;
						break;
					case "HARD":
						reward = 15;
						lifeBonus = 1;
						break;
					case "EXPERT":
						reward = 20;
						lifeBonus = 2;
						break;
					default:
						reward = 8;
						lifeBonus = 1;
				}
				break;

			case HARD:
				switch (questionLevel) {
					case "EASY":
						reward = 10;
						lifeBonus = 1;
						break;
					case "MEDIUM":
						reward = 15;
						lifeBonus = random.nextBoolean() ? 1 : 2;
						break;
					case "HARD":
						reward = 20;
						lifeBonus = 2;
						break;
					case "EXPERT":
						reward = 40;
						lifeBonus = 3;
						break;
					default:
						reward = 10;
						lifeBonus = 1;
				}
				break;
		}

		addScore(reward);
		addLives(lifeBonus);

		// Build result message
		String result = "✓ Correct! +" + reward + " points";
		if (lifeBonus > 0) {
			result += " and +" + lifeBonus + " life";
		}
		if (mineRevealed) {
			result += " | BONUS: Mine revealed!";
		}
		if (tilesRevealed > 0) {
			result += " | BONUS: " + tilesRevealed + " tiles revealed!";
		}
		return result + "!";
	}

	/**
	 * Handles wrong answer penalties based on game and question difficulty.
	 */
	private String handleWrongAnswer(String questionLevel) {
		int penalty = 0;
		int lifeLoss = 0;
		boolean noPenalty = false;

		switch (difficulty) {
			case EASY:
				switch (questionLevel) {
					case "EASY":
						// 50/50: -3pts or nothing
						if (random.nextBoolean()) {
							penalty = -3;
							lifeLoss = 0;
						} else {
							noPenalty = true;
						}
						break;
					case "MEDIUM":
						// 50/50: -6pts or nothing
						if (random.nextBoolean()) {
							penalty = -6;
							lifeLoss = 0;
						} else {
							noPenalty = true;
						}
						break;
					case "HARD":
						penalty = -10;
						lifeLoss = 0;
						break;
					case "EXPERT":
						penalty = -15;
						lifeLoss = 1;
						break;
					default:
						penalty = -3;
						lifeLoss = 0;
				}
				break;

			case MEDIUM:
				switch (questionLevel) {
					case "EASY":
						penalty = -8;
						lifeLoss = 0;
						break;
					case "MEDIUM":
						// 50/50: (-10pts & -1 life) or nothing
						if (random.nextBoolean()) {
							penalty = -10;
							lifeLoss = 1;
						} else {
							noPenalty = true;
						}
						break;
					case "HARD":
						penalty = -15;
						lifeLoss = 1;
						break;
					case "EXPERT":
						// 50/50: -1 or -2 lives (both lose -20 pts)
						penalty = -20;
						lifeLoss = random.nextBoolean() ? 1 : 2;
						break;
					default:
						penalty = -8;
						lifeLoss = 0;
				}
				break;

			case HARD:
				switch (questionLevel) {
					case "EASY":
						penalty = -10;
						lifeLoss = 1;
						break;
					case "MEDIUM":
						// 50/50: -1 or -2 lives (both lose -15 pts)
						penalty = -15;
						lifeLoss = random.nextBoolean() ? 1 : 2;
						break;
					case "HARD":
						penalty = -20;
						lifeLoss = 2;
						break;
					case "EXPERT":
						penalty = -40;
						lifeLoss = 3;
						break;
					default:
						penalty = -10;
						lifeLoss = 1;
				}
				break;
		}

		addScore(penalty);
		if (lifeLoss > 0) {
			loseLives(lifeLoss);
		}

		// Build result message
		String result;
		if (noPenalty) {
			result = "✗ Incorrect, but you got lucky - no penalty!";
		} else {
			result = "✗ Incorrect. " + penalty + " points";
			if (lifeLoss > 0) {
				result += " and -" + lifeLoss + " life!";
			} else {
				result += ".";
			}
		}

		return result;
	}

	/**
	 * Completes the QuestionTile interaction and switches turn. Called after the
	 * player answers the question or saves it.
	 */
	public void completeQuestionTileInteraction() {
		if (!gameOver) {
			switchTurn();
		}
	}

	/**
	 * Checks if the game has been won.
	 */
	private void checkWinCondition() {
		boolean board1Complete = checkBoardComplete(board1) || board1.areAllMinesCorrectlyFlagged();
		boolean board2Complete = checkBoardComplete(board2) || board2.areAllMinesCorrectlyFlagged();

		if (board1Complete || board2Complete) {
			gameOver = true;
			gameWon = true;
			if (board1Complete && board2Complete) {
				determineWinner();
			} else if (board1Complete) {
				gameEndMessage = player1.getName() + " cleared their board first! Final Score: " + totalScore;
			} else {
				gameEndMessage = player2.getName() + " cleared their board first! Final Score: " + totalScore;
			}
			notifyGameOver(true, gameEndMessage);
		}
	}

	/**
	 * Checks if all non-mine tiles on a board are revealed.
	 */
	private boolean checkBoardComplete(Board board) {
		int size = board.getSize();
		for (int row = 0; row < size; row++) {
			for (int col = 0; col < size; col++) {
				Tile tile = board.getTile(row, col);
				if (!tile.isRevealed() && !tile.isMine()) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Determines the winner based on scores.
	 * Converts remaining lives to bonus points and updates the final game message.
	 */
	private void determineWinner() {
		// Convert remaining hearts to points (heart value = price of a question)
		int pointsPerHeart = switch (difficulty) {
			case EASY -> 5;
			case MEDIUM -> 8;
			case HARD -> 12;
		};

		int bonusPoints = sharedLives * pointsPerHeart;
		addScore(bonusPoints);

		gameEndMessage = "Game converted " + sharedLives + " lives to " + bonusPoints + " points!\n";

		if (player1.getScore() > player2.getScore()) {
			gameEndMessage += player1.getName() + " wins with " + player1.getScore() + " points!";
		} else if (player2.getScore() > player1.getScore()) {
			gameEndMessage += player2.getName() + " wins with " + player2.getScore() + " points!";
		} else {
			gameEndMessage += "It's a tie! Both players have " + player1.getScore() + " points!";
		}
	}

	/**
	 * Processes a tile reveal action at the specified position.
	 */
	public String revealTile(int row, int col) {
		if (gameOver) {
			return "Game is over!";
		}

		Board currentBoard = getCurrentBoard();
		Tile tile = currentBoard.revealTile(row, col);

		if (tile == null) {
			return "Invalid move or tile already revealed.";
		}

		if (tile.isMine()) {
			addScore(1);
			handleMineHit();
			if (gameOver) {
				return "Hit a mine! Game Over - No lives remaining!";
			}
			return getCurrentPlayer().getName() + " hit a mine! Lost 1 life. Lives remaining: " + sharedLives;
		} else {
			return handleSafeReveal(tile);
		}
	}

	/**
	 * Gets a status summary of the current game state.
	 */
	public String getStatusSummary() {
		StringBuilder sb = new StringBuilder();
		sb.append("=== Game Status ===\n");
		sb.append("Difficulty: ").append(difficulty.name()).append("\n");
		sb.append("Total Score: ").append(totalScore).append("\n");
		sb.append("Lives: ").append(sharedLives).append("/").append(MAX_LIVES).append("\n");
		sb.append("Current Turn: ").append(getCurrentPlayer().getName()).append("\n");
		sb.append(player1.toString()).append("\n");
		sb.append(player2.toString()).append("\n");
		if (gameOver) {
			sb.append("GAME OVER: ").append(gameEndMessage).append("\n");
		}
		return sb.toString();
	}

	// Getters
	public Player getPlayer1() {
		return player1;
	}

	public Player getPlayer2() {
		return player2;
	}

	public Board getBoard1() {
		return board1;
	}

	public Board getBoard2() {
		return board2;
	}

	public Difficulty getDifficulty() {
		return difficulty;
	}

	public int getSharedLives() {
		return sharedLives;
	}

	public int getTotalScore() {
		return totalScore;
	}

	public boolean isGameOver() {
		return gameOver;
	}

	public boolean isGameWon() {
		return gameWon;
	}

	public String getGameEndMessage() {
		return gameEndMessage;
	}

	public int getCurrentPlayerIndex() {
		return currentPlayerIndex;
	}

	public QuestionBank getQuestionBank() {
		return questionBank;
	}

	/**
	 * Adds a question tile to the specified player's pending questions list.
	 */
	public void addPendingQuestion(QuestionTile questionTile, int playerNumber) {
		questionTile.markAsPending();

		if (playerNumber == 1) {
			player1PendingQuestions.add(questionTile);
		} else {
			player2PendingQuestions.add(questionTile);
		}
	}

	/**
	 * Gets pending questions for a specific player.
	 */
	public List<QuestionTile> getPendingQuestionsForPlayer(int playerNumber) {
		if (playerNumber == 1) {
			return new ArrayList<>(player1PendingQuestions);
		} else {
			return new ArrayList<>(player2PendingQuestions);
		}
	}

	/**
	 * Removes a question tile from the specified player's pending list.
	 */
	public void removePendingQuestion(QuestionTile questionTile, int playerNumber) {
		questionTile.clearPending();

		if (playerNumber == 1) {
			player1PendingQuestions.remove(questionTile);
		} else {
			player2PendingQuestions.remove(questionTile);
		}
	}

	/**
	 * Gets pending questions for the current player.
	 */
	public List<QuestionTile> getCurrentPlayerPendingQuestions() {
		return getPendingQuestionsForPlayer(currentPlayerIndex + 1);
	}

	// ========== Observer Pattern Methods ==========

	public void addObserver(GameObserver observer) {
		if (observer != null && !observers.contains(observer)) {
			observers.add(observer);
		}
	}

	public void removeObserver(GameObserver observer) {
		observers.remove(observer);
	}

	private void notifyScoreChanged(int playerNumber, int newScore, int change) {
		for (GameObserver observer : observers) {
			observer.onScoreChanged(playerNumber, newScore, change);
		}
	}

	private void notifyLivesChanged(int newLives, int change) {
		for (GameObserver observer : observers) {
			observer.onLivesChanged(newLives, change);
		}
	}

	private void notifyGameOver(boolean won, String message) {
		for (GameObserver observer : observers) {
			observer.onGameOver(won, message);
		}
	}

	private void notifyTurnChanged(int playerNumber) {
		for (GameObserver observer : observers) {
			observer.onTurnChanged(playerNumber);
		}
	}

	public SurpriseManager getSurpriseManager() {
		return surpriseManager;
	}

	public void setSurpriseManager(SurpriseManager surpriseManager) {
		this.surpriseManager = surpriseManager;
	}

	public List<QuestionTile> getPlayer1PendingQuestions() {
		return player1PendingQuestions;
	}

	public void setPlayer1PendingQuestions(List<QuestionTile> player1PendingQuestions) {
		this.player1PendingQuestions = player1PendingQuestions;
	}

	public List<QuestionTile> getPlayer2PendingQuestions() {
		return player2PendingQuestions;
	}

	public void setPlayer2PendingQuestions(List<QuestionTile> player2PendingQuestions) {
		this.player2PendingQuestions = player2PendingQuestions;
	}

	public List<GameObserver> getObservers() {
		return observers;
	}

	public void setObservers(List<GameObserver> observers) {
		this.observers = observers;
	}

	public Random getRandom() {
		return random;
	}

	public void setRandom(Random random) {
		this.random = random;
	}

	public static int getMaxLives() {
		return MAX_LIVES;
	}

	public void setQuestionBank(QuestionBank questionBank) {
		this.questionBank = questionBank;
	}

	public void setCurrentPlayerIndex(int currentPlayerIndex) {
		this.currentPlayerIndex = currentPlayerIndex;
	}

	public void setSharedLives(int sharedLives) {
		this.sharedLives = sharedLives;
	}

	public void setTotalScore(int totalScore) {
		this.totalScore = totalScore;
	}

	public void setGameOver(boolean gameOver) {
		this.gameOver = gameOver;
	}

	public void setGameWon(boolean gameWon) {
		this.gameWon = gameWon;
	}

	public void setGameEndMessage(String gameEndMessage) {
		this.gameEndMessage = gameEndMessage;
	}

	public int getScore() {
		return totalScore;
	}

}