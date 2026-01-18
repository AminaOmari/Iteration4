package Model;

/**
 * Abstract base class representing a single tile on the MineSweeper game board.
 * Each tile has a position, revealed/flagged state, and can be revealed.
 * Subclasses implement specific behavior for different tile types.
 * 
 * @author Team Rhino
 * @version 2.0 - Iteration 2 (Hierarchy Implementation)
 */
public abstract class Tile {
	protected boolean revealed;
	protected boolean flagged;
	protected final int row;
	protected final int col;

	/**
	 * Creates a new tile at the specified position.
	 * 
	 * @param row Row position on the board
	 * @param col Column position on the board
	 */
	public Tile(int row, int col) {
		this.row = row;
		this.col = col;
		this.revealed = false;
		this.flagged = false;
	}

	/**
	 * Reveals this tile if not already revealed or flagged.
	 * 
	 * @return true if the tile was successfully revealed, false otherwise
	 */
	public boolean reveal() {
		if (!revealed && !flagged) {
			revealed = true;
			return true;
		}
		return false;
	}

	/**
	 * Toggles the flag status of this tile. Can only flag/unflag unrevealed tiles.
	 * 
	 * @return true if flag status was changed, false otherwise
	 */
	public boolean toggleFlag() {
		if (!revealed) {
			flagged = !flagged;
			return true;
		}
		return false;
	}

	/**
	 * Gets the type of this tile as a string. Subclasses should return their
	 * specific type.
	 * 
	 * @return String representing the tile type
	 */
	public abstract String getType();

	/**
	 * Checks if this tile is a mine.
	 * 
	 * @return true if this is a MineTile
	 */
	public abstract boolean isMine();

	/**
	 * Checks if this tile is empty (no adjacent mines).
	 * 
	 * @return true if this is an EmptyTile with no adjacent mines
	 */
	public abstract boolean isEmpty();

	/**
	 * Checks if this tile is a question tile.
	 * 
	 * @return true if this is a QuestionTile
	 */
	public abstract boolean isQuestion();

	/**
	 * Checks if this tile is a surprise tile.
	 * 
	 * @return true if this is a SurpriseTile
	 */
	public abstract boolean isSurprise();

	/**
	 * Gets the number of adjacent mines for this tile. Only relevant for NumberTile
	 * and EmptyTile.
	 * 
	 * @return Number of adjacent mines (0-8)
	 */
	public abstract int getAdjacentMines();

	/**
	 * Sets the number of adjacent mines for this tile. Only relevant for NumberTile
	 * and EmptyTile.
	 * 
	 * @param count Number of adjacent mines
	 */
	public abstract void setAdjacentMines(int count);

	/**
	 * Returns a string representation of this tile for display.
	 * 
	 * @return String representation
	 */
	public abstract String toDisplayString();

	// Getters and Setters
	public boolean isRevealed() {
		return revealed;
	}

	public void setRevealed(boolean revealed) {
		this.revealed = revealed;
	}

	public boolean isFlagged() {
		return flagged;
	}

	public void setFlagged(boolean flagged) {
		this.flagged = flagged;
	}

	public int getRow() {
		return row;
	}

	public int getCol() {
		return col;
	}

	@Override
	public String toString() {
		if (!revealed) {
			return flagged ? "F" : ".";
		}
		return toDisplayString();
	}
}
