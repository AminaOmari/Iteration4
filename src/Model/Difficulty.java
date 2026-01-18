package Model;

/**
 * Enum representing game difficulty levels. Each difficulty has specific board
 * size, mine count, question/surprise tiles, and lives.
 * 
 * Updated in Iteration 3 to include activation costs for question and surprise
 * tiles.
 * 
 * @author Team Rhino
 * @version 3.1 - Fixed activation costs
 */
public enum Difficulty {
	/** Easy: 9x9 board, 10 mines, 6 questions, 2 surprises, 10 lives */
	EASY(9, 10, 6, 2, 10, 5, 8),
	/** Medium: 13x13 board, 26 mines, 7 questions, 3 surprises, 8 lives */
	MEDIUM(13, 26, 7, 3, 8, 8, 12),
	/** Hard: 16x16 board, 44 mines, 11 questions, 4 surprises, 6 lives */
	HARD(16, 44, 11, 4, 6, 12, 16);

	private final int boardSize;
	private final int mineCount;
	private final int questionCount;
	private final int surpriseCount;
	private final int initialLives;
	private final int questionPoints;
	private final int surprisePoints;

	/**
	 * Constructor for Difficulty enum.
	 * 
	 * @param boardSize      Size of the board (NxN)
	 * @param mineCount      Number of mines on the board
	 * @param questionCount  Number of question tiles
	 * @param surpriseCount  Number of surprise tiles
	 * @param initialLives   Starting lives for players
	 * @param questionPoints Points cost for activating a question/surprise tile
	 * @param surprisePoints Points gained/lost from surprise tiles
	 */
	Difficulty(int boardSize, int mineCount, int questionCount, int surpriseCount, int initialLives, int questionPoints,
			int surprisePoints) {
		this.boardSize = boardSize;
		this.mineCount = mineCount;
		this.questionCount = questionCount;
		this.surpriseCount = surpriseCount;
		this.initialLives = initialLives;
		this.questionPoints = questionPoints;
		this.surprisePoints = surprisePoints;
	}

	public int getBoardSize() {
		return boardSize;
	}

	public int getMineCount() {
		return mineCount;
	}

	public int getQuestionCount() {
		return questionCount;
	}

	public int getSurpriseCount() {
		return surpriseCount;
	}

	public int getInitialLives() {
		return initialLives;
	}

	/**
	 * Gets the points value for question rewards/penalties. This is the base points
	 * for answering questions.
	 * 
	 * @return Question points (5 for Easy, 8 for Medium, 12 for Hard)
	 */
	public int getQuestionPoints() {
		return questionPoints;
	}

	/**
	 * Gets the points value for surprise effects.
	 * 
	 * @return Surprise points (8 for Easy, 12 for Medium, 16 for Hard)
	 */
	public int getSurprisePoints() {
		return surprisePoints;
	}

	/**
	 * Gets the cost in points to activate a question tile. According to
	 * specification: - Easy: 5 points - Medium: 8 points - Hard: 12 points
	 * 
	 * @return Activation cost in points
	 */
	public int getQuestionCost() {
		return questionPoints; // Uses same values as questionPoints (5/8/12)
	}

	/**
	 * Gets the cost in points to activate a surprise tile. Same cost as question
	 * tiles per specification.
	 * 
	 * @return Activation cost in points
	 */
	public int getSurpriseCost() {
		return questionPoints; // Same cost as questions (5/8/12)
	}
}