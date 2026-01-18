package Model;

/**
 * Represents a numbered tile showing the count of adjacent mines. Displays a
 * number from 1-8 indicating nearby mines.
 * 
 * @author Team Rhino
 * @version 2.0 - Iteration 2
 */
public class NumberTile extends Tile {
	private int adjacentMines;

	/**
	 * Creates a new numbered tile at the specified position.
	 * 
	 * @param row           Row position
	 * @param col           Column position
	 * @param adjacentMines Initial count of adjacent mines
	 */
	public NumberTile(int row, int col, int adjacentMines) {
		super(row, col);
		this.adjacentMines = adjacentMines;
	}

	@Override
	public String getType() {
		return "NUMBER";
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
		return false;
	}

	@Override
	public int getAdjacentMines() {
		return adjacentMines;
	}

	@Override
	public void setAdjacentMines(int count) {
		this.adjacentMines = count;
	}

	@Override
	public String toDisplayString() {
		return String.valueOf(adjacentMines);
	}
}
