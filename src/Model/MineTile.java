package Model;

/**
 * Represents a tile containing a mine. When revealed, causes the player to lose
 * a life and switches turns.
 * 
 * @author Team Rhino
 * @version 2.0 - Iteration 2
 */
public class MineTile extends Tile {

	/**
	 * Creates a new mine tile at the specified position.
	 * 
	 * @param row Row position
	 * @param col Column position
	 */
	public MineTile(int row, int col) {
		super(row, col);
	}

	@Override
	public String getType() {
		return "MINE";
	}

	@Override
	public boolean isMine() {
		return true;
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
		return false;
	}

	@Override
	public int getAdjacentMines() {
		return 0; // Mines don't have adjacent mine counts
	}

	@Override
	public void setAdjacentMines(int count) {
		// Mines don't track adjacent mines
	}

	@Override
	public String toDisplayString() {
		return "*";
	}
}
