package Model;

/**
 * Represents a tile containing a trivia question. When revealed, displays a
 * question that costs points to activate. Correct answers award points and
 * additional lives.
 * 
 * @author Team Rhino
 * @version 2.0 - Iteration 2
 */
public class QuestionTile extends Tile {
	private boolean activated;
	private Question question; // The trivia question for this tile
	private boolean pendingInList; // Track if this question is in pending list

	/**
	 * Creates a new question tile at the specified position.
	 * 
	 * @param row Row position
	 * @param col Column position
	 */
	public QuestionTile(int row, int col) {
		super(row, col);
		this.activated = false;
		this.question = null;
		this.pendingInList = false;
	}

	/**
	 * Checks if this question tile has been activated.
	 * 
	 * @return true if the question has been activated
	 */
	public boolean isActivated() {
		return activated;
	}

	/**
	 * Activates this question tile. Called when the player pays the points to
	 * answer the question.
	 */
	public void activate() {
		this.activated = true;
	}

	/**
	 * Sets the trivia question for this tile.
	 * 
	 * @param question The question to assign
	 */
	public void setQuestion(Question question) {
		this.question = question;
	}

	/**
	 * Gets the trivia question for this tile.
	 * 
	 * @return The question
	 */
	public Question getQuestion() {
		return question;
	}

	/**
	 * Checks if this tile has a question assigned.
	 * 
	 * @return true if question is assigned
	 */
	public boolean hasQuestion() {
		return question != null;
	}

	/**
	 * Checks if this question is already in a pending list.
	 * 
	 * @return true if question is in pending list
	 */
	public boolean isPendingInList() {
		return pendingInList;
	}

	/**
	 * Marks this question as being in a pending list.
	 */
	public void markAsPending() {
		this.pendingInList = true;
	}

	/**
	 * Removes the pending flag (called when question is answered).
	 */
	public void clearPending() {
		this.pendingInList = false;
	}

	@Override
	public String getType() {
		return "QUESTION";
	}

	@Override
	public boolean isMine() {
		return false;
	}

	@Override
	public boolean isEmpty() {
		return false;
	}

	@Override
	public boolean isQuestion() {
		return true;
	}

	@Override
	public boolean isSurprise() {
		return false;
	}

	@Override
	public int getAdjacentMines() {
		return 0; // Question tiles don't have adjacent mine counts
	}

	@Override
	public void setAdjacentMines(int count) {
		// Question tiles don't track adjacent mines
	}

	@Override
	public String toDisplayString() {
		return "?";
	}
}