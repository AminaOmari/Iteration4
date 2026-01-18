package Model;

/**
 * Represents a tile containing a surprise effect. When revealed, triggers a
 * random positive or negative effect (50/50 chance). Surprises are loaded from
 * CSV files.
 * 
 * @author Team Rhino
 * @version 3.0 - Iteration 3
 */
public class SurpriseTile extends Tile {
	private Surprise surprise; // null until revealed

	/**
	 * Creates a new surprise tile at the specified position.
	 * 
	 * @param row Row position
	 * @param col Column position
	 */
	public SurpriseTile(int row, int col) {
		super(row, col);
		this.surprise = null;
	}

	/**
	 * Sets the surprise for this tile.
	 * 
	 * @param surprise The surprise to assign
	 */
	public void setSurprise(Surprise surprise) {
		this.surprise = surprise;
	}

	/**
	 * Gets the surprise for this tile.
	 * 
	 * @return The surprise, or null if not yet determined
	 */
	public Surprise getSurprise() {
		return surprise;
	}

	/**
	 * Checks if this surprise was good. Returns null if not yet determined.
	 * 
	 * @return true if good, false if bad, null if not yet determined
	 */
	public Boolean wasGoodSurprise() {
		return surprise != null ? surprise.isGood() : null;
	}

	/**
	 * Checks if this surprise tile has been activated (used).
	 * 
	 * @return true if the surprise has been triggered
	 */
	public boolean isActivated() {
		return surprise != null;
	}

	@Override
	public String getType() {
		return "SURPRISE";
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
		return false;
	}

	@Override
	public boolean isSurprise() {
		return true;
	}

	@Override
	public int getAdjacentMines() {
		return 0; // Surprise tiles don't have adjacent mine counts
	}

	@Override
	public void setAdjacentMines(int count) {
		// Surprise tiles don't track adjacent mines
	}

	@Override
	public String toDisplayString() {
		return "!";
	}
}