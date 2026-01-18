package Model;

/**
 * Represents an empty tile with no adjacent mines or a tile that will show the
 * count of adjacent mines. When an empty tile with 0 adjacent mines is
 * revealed, it triggers recursive revealing of neighbors.
 * 
 * @author Team Rhino
 * @version 2.0 - Iteration 2
 */
public class EmptyTile extends Tile {
	private int adjacentMines;

	/**
	 * Creates a new empty tile at the specified position.
	 * 
	 * @param row Row position
	 * @param col Column position
	 */
	public EmptyTile(int row, int col) {
		super(row, col);
		this.adjacentMines = 0;
	}

	@Override
	public String getType() {
		return "EMPTY";
	}

	@Override
	public boolean isMine() {
		return false;
	}

	@Override
	public boolean isEmpty() {
		return adjacentMines == 0;
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
		return adjacentMines == 0 ? " " : String.valueOf(adjacentMines);
	}
}
