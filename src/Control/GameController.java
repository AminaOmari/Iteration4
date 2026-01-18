package Control;

import Model.*;
import View.GameView;
import View.TriviaDialog;
import Patterns.Observer.GameEventLogger;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import java.util.List;

/**
 * Controller class for the MineSweeper game. ONE-CLICK VERSION: Q and S tiles
 * activate immediately on first click.
 *
 * @author Team Rhino
 * @version 3.2 - One-click activation
 */
public class GameController {
	private GameState gameState;
	private final GameView view;
	private boolean historySaved = false;

	public GameController(GameView view) {
		this.view = view;
		this.view.setController(this);
		initializeStartScreen();
	}

	private void initializeStartScreen() {
		view.setStartButtonListener(e -> startNewGame());
		view.setDemoButtonListener(e -> startDemoMode());
	}

	public void startNewGame() {
		// Stop any running bot first
		if (demoBot != null) {
			demoBot.stop();
			demoBot = null;
		}

		String player1Name = view.getPlayer1Name();
		String player2Name = view.getPlayer2Name();

		// Validate that both names are entered
		if (player1Name.isEmpty()) {
			view.showMessage("Please enter Player 1 name!");
			return;
		}
		if (player2Name.isEmpty()) {
			view.showMessage("Please enter Player 2 name!");
			return;
		}

		Difficulty difficulty;
		switch (view.getSelectedDifficulty()) {
			case 1:
				difficulty = Difficulty.MEDIUM;
				break;
			case 2:
				difficulty = Difficulty.HARD;
				break;
			default:
				difficulty = Difficulty.EASY;
		}

		gameState = new GameState(player1Name, player2Name, difficulty);

		GameEventLogger logger = new GameEventLogger();
		logger.setEnabled(false);
		gameState.addObserver(logger);

		view.setPlayerNames(player1Name, player2Name);

		int boardSize = difficulty.getBoardSize();
		view.initializeBoards(boardSize);

		setupBoardListeners(boardSize);

		view.updatePendingQuestions(new java.util.ArrayList<>(), new java.util.ArrayList<>());

		QuestionBank.getInstance().resetUsedQuestions();

		updateView();

		view.showGameScreen();

		view.highlightActiveBoard(1);
	}

	private void setupBoardListeners(int boardSize) {
		JButton[][] board1Buttons = view.getBoard1Buttons();
		JButton[][] board2Buttons = view.getBoard2Buttons();

		for (int row = 0; row < boardSize; row++) {
			for (int col = 0; col < boardSize; col++) {
				final int r = row;
				final int c = col;

				board1Buttons[row][col].addMouseListener(new MouseAdapter() {
					@Override
					public void mousePressed(MouseEvent e) {
						if (SwingUtilities.isRightMouseButton(e)) {
							handleFlagToggle(1, r, c);
						} else if (SwingUtilities.isLeftMouseButton(e)) {
							handleTileClick(1, r, c);
						}
					}
				});
			}
		}

		for (int row = 0; row < boardSize; row++) {
			for (int col = 0; col < boardSize; col++) {
				final int r = row;
				final int c = col;

				board2Buttons[row][col].addMouseListener(new MouseAdapter() {
					@Override
					public void mousePressed(MouseEvent e) {
						if (SwingUtilities.isRightMouseButton(e)) {
							handleFlagToggle(2, r, c);
						} else if (SwingUtilities.isLeftMouseButton(e)) {
							handleTileClick(2, r, c);
						}
					}
				});
			}
		}
	}

	private void updateView() {
		Player player1 = gameState.getPlayer1();
		Player player2 = gameState.getPlayer2();

		view.updateScores(gameState.getTotalScore(), player1.getScore(), player2.getScore());
		view.updateLives(gameState.getSharedLives(), gameState.getDifficulty().getInitialLives());
		view.updateTurn(gameState.getCurrentPlayer().getName());

		// Calculate mines remaining
		int totalMines = gameState.getDifficulty().getMineCount() * 2;
		int flagsPlaced = gameState.getBoard1().getFlagCount() + gameState.getBoard2().getFlagCount();
		view.updateMineCount(totalMines - flagsPlaced);

		updateBoardDisplay(1);
		updateBoardDisplay(2);

		List<QuestionTile> player1Pending = gameState.getPendingQuestionsForPlayer(1);
		List<QuestionTile> player2Pending = gameState.getPendingQuestionsForPlayer(2);
		view.updatePendingQuestions(player1Pending, player2Pending);
		view.updateMinesCount(gameState.getBoard1().countUnrevealedMines(),
				gameState.getBoard2().countUnrevealedMines());
	}

	private void updateBoardDisplay(int boardNum) {
		Board board = (boardNum == 1) ? gameState.getBoard1() : gameState.getBoard2();
		int size = gameState.getDifficulty().getBoardSize();

		for (int row = 0; row < size; row++) {
			for (int col = 0; col < size; col++) {
				Tile tile = board.getTile(row, col);
				view.updateTile(boardNum, row, col, tile);
			}
		}
	}

	/**
	 * ONE-CLICK VERSION: Q and S tiles activate immediately.
	 */
	public void handleTileClick(int boardNum, int row, int col) {
		if (gameState.isGameOver()) {
			view.showMessage("Game is over! Start a new game.");
			return;
		}

		int currentPlayer = gameState.getCurrentPlayerIndex() + 1;
		if (boardNum != currentPlayer) {
			view.showMessage("Not your turn! Current player: " + gameState.getCurrentPlayer().getName());
			return;
		}

		Board board = (boardNum == 1) ? gameState.getBoard1() : gameState.getBoard2();
		Tile tile = board.getTile(row, col);

		if (tile.isFlagged()) {
			view.showMessage("Remove flag first! (Right-click)");
			return;
		}

		if (tile.isQuestion()) {
			QuestionTile qTile = (QuestionTile) tile;

			if (qTile.isActivated()) {
				view.showMessage("This question has already been answered!");
				return;
			}

			int cost = gameState.getActivationCost();
			int scoreBefore = gameState.getTotalScore();

			// ✅ allow activation when score == cost
			if (scoreBefore < cost) {
				view.showMessage("Not enough points to activate a question. Need " + cost + " points.");
				return;
			}

			// ✅ pay cost once
			gameState.addScore(-cost);

			// Reveal tile if not already revealed (may be revealed by cascade)
			if (!tile.isRevealed()) {
				board.revealTile(row, col);
			}

			String result = gameState.handleSafeReveal(tile);
			showTriviaDialog(qTile);

			view.showMessage(result);
			updateView();
			return;
		}

		// =================================================================
		// SURPRISE TILES - ONE-CLICK: Immediate activation
		// =================================================================
		if (tile.isSurprise()) {
			SurpriseTile sTile = (SurpriseTile) tile;

			if (sTile.isActivated()) {
				view.showMessage("This surprise has already been used!");
				return;
			}

			// Check cost for surprise same as question
			int cost = gameState.getActivationCost();
			if (gameState.getTotalScore() < cost) {
				view.showMessage("Not enough points to activate a surprise. Need " + cost + " points.");
				return;
			}

			// Pay cost
			gameState.addScore(-cost);

			if (!tile.isRevealed()) {
				board.revealTile(row, col);
			}

			// ✅ Use the model's logic (handles +/− points and +/− lives correctly)
			// and ensures bad surprises call loseLives() (so game-over works)
			String result = gameState.handleSafeReveal(tile);

			Surprise surprise = sTile.getSurprise();
			if (surprise != null) {
				String surpriseMessage = "🎁 SURPRISE! 🎁\n\n" + surprise.getMessage() + "\n\n";
				surpriseMessage += surprise.isGood() ? "✅ Good Surprise!\n" : "❌ Bad Surprise!\n";
				surpriseMessage += "Points: " + surprise.getPointsEffect() + "\n";
				surpriseMessage += "Lives: " + surprise.getLivesEffect();

				JOptionPane.showMessageDialog(view, surpriseMessage,
						surprise.isGood() ? "Good Surprise!" : "Bad Surprise!",
						surprise.isGood() ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.WARNING_MESSAGE);
			}

			view.showMessage(result);
			updateView();

			if (gameState.isGameOver()) {
				if (!historySaved) {
					GameHistory history = GameHistory.fromGameState(gameState);
					HistoryManager.saveHistory(history);
					historySaved = true;
				}
				view.showGameOver(gameState.getGameEndMessage(), gameState.isGameWon());
			} else {
				view.highlightActiveBoard(gameState.getCurrentPlayerIndex() + 1);
			}
			return;
		}

		// =================================================================
		// REGULAR TILES (Mine, Number, Empty)
		// =================================================================

		if (tile.isRevealed()) {
			view.showMessage("Tile already revealed!");
			return;
		}

		String result = gameState.revealTile(row, col);

		Tile tileToReveal = board.getTile(row, col);

		if (tileToReveal.isRevealed() && tileToReveal.isMine()) {
			Player playerWhoClicked = (gameState.getCurrentPlayerIndex() == 0) ? gameState.getPlayer2()
					: gameState.getPlayer1();

			String mineWarning = "💣 BOOM! 💣\n\n" + playerWhoClicked.getName() + " hit a mine!\n\n"
					+ "Lives lost: -1\n" + "Lives remaining: " + gameState.getSharedLives();

			if (gameState.isGameOver()) {
				mineWarning += "\n\n❌ GAME OVER!\nNo lives remaining.";
				JOptionPane.showMessageDialog(view, mineWarning, "Game Over!", JOptionPane.ERROR_MESSAGE);
			} else {
				JOptionPane.showMessageDialog(view, mineWarning, "Mine Hit!", JOptionPane.WARNING_MESSAGE);
			}
		}

		view.showMessage(result);

		updateView();

		if (gameState.isGameOver()) {
			if (!historySaved) {
				GameHistory history = GameHistory.fromGameState(gameState);
				HistoryManager.saveHistory(history);
				historySaved = true;
			}
			view.showGameOver(gameState.getGameEndMessage(), gameState.isGameWon());
		} else {
			view.highlightActiveBoard(gameState.getCurrentPlayerIndex() + 1);
		}
	}

	public void handleFlagToggle(int boardNum, int row, int col) {
		if (gameState.isGameOver()) {
			return;
		}

		int currentPlayer = gameState.getCurrentPlayerIndex() + 1;
		if (boardNum != currentPlayer) {
			return;
		}

		Board board = (boardNum == 1) ? gameState.getBoard1() : gameState.getBoard2();
		Tile tile = board.getTile(row, col);

		if (tile.isRevealed()) {
			return;
		}

		tile.toggleFlag();

		gameState.handleFlag(tile);

		updateView();

		if (gameState.isGameOver()) {
			if (!historySaved) {
				GameHistory history = GameHistory.fromGameState(gameState);
				HistoryManager.saveHistory(history);
				historySaved = true;
			}
			view.showGameOver(gameState.getGameEndMessage(), gameState.isGameWon());
		}
	}

	private void showTriviaDialog(QuestionTile questionTile) {
		Question question = questionTile.getQuestion();
		if (question == null) {
			view.showMessage("No question available!");
			return;
		}

		TriviaDialog dialog = new TriviaDialog(view, question, 0);
		dialog.setVisible(true);

		// If the player did NOT answer - we save for later and do NOT score now
		if (!dialog.wasAnswered()) {
			int playerNumber = gameState.getCurrentPlayerIndex() + 1;
			gameState.addPendingQuestion(questionTile, playerNumber);

			gameState.completeQuestionTileInteraction();

			view.showMessage("Question saved for later. Answer it anytime!");
			updateView();

			if (!gameState.isGameOver()) {
				view.highlightActiveBoard(gameState.getCurrentPlayerIndex() + 1);
			}
			return;
		}

		// Player answered -> score/lives are applied now
		int beforeScore = gameState.getTotalScore();

		int selectedAnswer = dialog.getSelectedAnswer();
		boolean correct = question.isCorrect(selectedAnswer);

		String result = gameState.handleQuestionAnswer(questionTile, correct);

		int afterScore = gameState.getTotalScore();
		int delta = afterScore - beforeScore;

		// Show result + accurate net score change (includes whatever actually happened)
		String finalMsg = result + "\nNet score change: " + (delta >= 0 ? "+" : "") + delta;
		view.showMessage(finalMsg);

		gameState.completeQuestionTileInteraction();
		updateView();

		if (gameState.isGameOver()) {
			if (!historySaved) {
				GameHistory history = GameHistory.fromGameState(gameState);
				HistoryManager.saveHistory(history);
				historySaved = true;
			}
			view.showGameOver(gameState.getGameEndMessage(), gameState.isGameWon());
		} else {
			view.highlightActiveBoard(gameState.getCurrentPlayerIndex() + 1);
		}
	}

	public void answerPendingQuestion(QuestionTile questionTile) {
		if (gameState.isGameOver()) {
			view.showMessage("Game is over!");
			return;
		}

		Question question = questionTile.getQuestion();
		if (question == null) {
			view.showMessage("No question available!");
			return;
		}

		TriviaDialog dialog = new TriviaDialog(view, question, 0);
		dialog.setVisible(true);

		if (dialog.wasAnswered()) {
			int selectedAnswer = dialog.getSelectedAnswer();
			boolean correct = question.isCorrect(selectedAnswer);

			String result = gameState.handleQuestionAnswer(questionTile, correct);
			view.showMessage(result);

			int playerNumber = gameState.getCurrentPlayerIndex() + 1;
			gameState.removePendingQuestion(questionTile, playerNumber);

			updateView();

			if (gameState.isGameOver()) {
				if (!historySaved) {
					GameHistory history = GameHistory.fromGameState(gameState);
					HistoryManager.saveHistory(history);
					historySaved = true;
				}
				view.showGameOver(gameState.getGameEndMessage(), gameState.isGameWon());
			}
		}
	}

	/**
	 * Refreshes the view (public method for View callbacks).
	 */
	public void refreshView() {
		updateView();
	}

	/**
	 * Handles a request for a smart hint.
	 */
	public void requestHint() {
		if (gameState.isGameOver())
			return;

		// Only allow hint if user has enough points (cost: 2 points)
		int cost = 2;
		if (gameState.getTotalScore() < cost) {
			view.showMessage("Hints cost 2 points! You need more points.");
			return;
		}

		Board currentBoard = gameState.getCurrentBoard();
		Hint hint = currentBoard.getSmartHint();

		if (hint != null) {
			gameState.addScore(-cost); // Deduct cost
			int boardNum = gameState.getCurrentPlayerIndex() + 1;
			view.showMessage("Hint (-2 pts): " + hint.getMessage());
			view.updateScores(gameState.getTotalScore(), gameState.getPlayer1().getScore(),
					gameState.getPlayer2().getScore());
			view.highlightHint(boardNum, hint.getRow(), hint.getCol(), hint.isMine());
		} else {
			view.showMessage("No guaranteed moves found at this moment.");
		}
	}

	private Thread demoThread;
	private DemoBot demoBot;

	public void startDemoMode() {
		// Set names for AI (Player 1 = Human, Player 2 = Bot)
		view.setPlayerNamesForAI();
		startNewGame(); // Start standard game

		// Launch bot
		if (demoBot != null) {
			demoBot.stop();
		}
		demoBot = new DemoBot(this);
		demoThread = new Thread(demoBot);
		demoThread.start();
	}

	public boolean isGameOver() {
		return gameState != null && gameState.isGameOver();
	}

	public Board getCurrentPlayerBoard() {
		return gameState.getCurrentBoard();
	}

	public int getCurrentPlayerIndex() {
		return gameState.getCurrentPlayerIndex();
	}
}