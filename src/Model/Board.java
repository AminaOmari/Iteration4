package Model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import Patterns.Factory.TileFactory;

/**
 * Represents the game board for MineSweeper. Contains tiles arranged in a grid
 * with mines, questions, and surprises. Handles mine placement and neighbor
 * counting algorithms.
 * 
 * Updated in Iteration 2 to use Tile hierarchy with polymorphism.
 * 
 * @author Team Rhino
 * @version 2.0 - Iteration 2 (Using Tile Hierarchy)
 */
public class Board {
	private final Tile[][] tiles;
	private final int size;
	private final Difficulty difficulty;
	private int revealedCount;
	private int totalSafeTiles;
	private final Random random;

	/**
	 * Creates a new game board with the specified difficulty. Initializes tiles,
	 * places mines, questions, surprises, and calculates neighbors.
	 * 
	 * @param difficulty The difficulty level determining board size and mine count
	 */
	public Board(Difficulty difficulty) {
		this.difficulty = difficulty;
		this.size = difficulty.getBoardSize();
		this.tiles = new Tile[size][size];
		this.revealedCount = 0;
		this.random = new Random();

		initializeTiles();
		placeSpecialTiles();
		calculateAllAdjacentMines();

		// Calculate total safe tiles (all tiles minus mines)
		this.totalSafeTiles = (size * size) - difficulty.getMineCount();
	}

	/**
	 * Initializes all tiles on the board as empty tiles.
	 * Updated for Iteration 3: Uses TileFactory (Factory Method pattern).
	 */
	private void initializeTiles() {
		for (int row = 0; row < size; row++) {
			for (int col = 0; col < size; col++) {
				tiles[row][col] = TileFactory.createEmptyTile(row, col);
			}
		}
	}

	/**
	 * Places mines, questions, and surprises randomly on the board. Uses
	 * Fisher-Yates shuffle for random placement.
	 * Updated for Iteration 3: Uses TileFactory (Factory Method pattern).
	 */
	private void placeSpecialTiles() {
		// Create list of all positions
		List<int[]> positions = new ArrayList<>();
		for (int row = 0; row < size; row++) {
			for (int col = 0; col < size; col++) {
				positions.add(new int[] { row, col });
			}
		}

		// Shuffle positions for random placement
		Collections.shuffle(positions, random);

		int index = 0;

		// Place mines - use TileFactory
		for (int i = 0; i < difficulty.getMineCount() && index < positions.size(); i++) {
			int[] pos = positions.get(index++);
			tiles[pos[0]][pos[1]] = TileFactory.createMineTile(pos[0], pos[1]);
		}

		// The following code snippet appears to be misplaced and contains references to
		// GameState variables (newLives, MAX_LIVES, addScore, sharedLives)
		// that do not exist in the Board class. Inserting it directly would cause
		// compilation errors.
		// As per instructions, the change must be syntactically correct.
		// Therefore, this part of the requested change cannot be applied to the Board
		// class as provided.
		// If this logic is intended for a GameState class, it should be applied there.
		// The duplicate loop for question tiles is also syntactically incorrect as a
		// standalone block.

		// Place question tiles - use TileFactory
		for (int i = 0; i < difficulty.getQuestionCount() && index < positions.size(); i++) {
			int[] pos = positions.get(index++);
			tiles[pos[0]][pos[1]] = TileFactory.createQuestionTile(pos[0], pos[1]);
		}

		// Place surprise tiles - use TileFactory
		for (int i = 0; i < difficulty.getSurpriseCount() && index < positions.size(); i++) {
			int[] pos = positions.get(index++);
			tiles[pos[0]][pos[1]] = TileFactory.createSurpriseTile(pos[0], pos[1]);
		}
	}

	/**
	 * Calculates adjacent mine count for all non-mine tiles.
	 * Updated for Iteration 3: Uses TileFactory (Factory Method pattern).
	 */
	private void calculateAllAdjacentMines() {
		for (int row = 0; row < size; row++) {
			for (int col = 0; col < size; col++) {
				Tile tile = tiles[row][col];
				if (!tile.isMine()) {
					int count = countAdjacentMines(row, col);

					// If it's an EmptyTile and has adjacent mines, convert to NumberTile
					if (count > 0 && tile instanceof EmptyTile) {
						tiles[row][col] = TileFactory.createNumberTile(row, col, count);
					} else {
						tile.setAdjacentMines(count);
					}
				}
			}
		}
	}

	/**
	 * Counts the number of mines adjacent to a specific tile. Checks all 8
	 * neighboring tiles.
	 * 
	 * @param row Row of the tile
	 * @param col Column of the tile
	 * @return Number of adjacent mines (0-8)
	 */
	public int countAdjacentMines(int row, int col) {
		int count = 0;
		for (int dr = -1; dr <= 1; dr++) {
			for (int dc = -1; dc <= 1; dc++) {
				if (dr == 0 && dc == 0)
					continue;
				int newRow = row + dr;
				int newCol = col + dc;
				if (isValidPosition(newRow, newCol) && tiles[newRow][newCol].isMine()) {
					count++;
				}
			}
		}
		return count;
	}

	/**
	 * Reveals a tile at the specified position. If the tile is empty (0 adjacent
	 * mines), recursively reveals neighbors.
	 * 
	 * @param row Row of the tile to reveal
	 * @param col Column of the tile to reveal
	 * @return The revealed tile, or null if position is invalid
	 */
	public Tile revealTile(int row, int col) {
		if (!isValidPosition(row, col)) {
			return null;
		}

		Tile tile = tiles[row][col];
		if (tile.isRevealed() || tile.isFlagged()) {
			return null;
		}

		tile.reveal();
		revealedCount++;

		// If empty tile with no adjacent mines, reveal neighbors recursively
		if (tile.isEmpty() && tile.getAdjacentMines() == 0) {
			revealEmptyNeighbors(row, col);
		}

		return tile;
	}

	/**
	 * Recursively reveals all empty neighboring tiles. Stops at numbered tiles or
	 * board edges.
	 * 
	 * @param row Starting row
	 * @param col Starting column
	 */
	private void revealEmptyNeighbors(int row, int col) {
		for (int dr = -1; dr <= 1; dr++) {
			for (int dc = -1; dc <= 1; dc++) {
				if (dr == 0 && dc == 0)
					continue;
				int newRow = row + dr;
				int newCol = col + dc;

				if (isValidPosition(newRow, newCol)) {
					Tile neighbor = tiles[newRow][newCol];

					// Reveal non-mine, non-flagged tiles during cascade
					if (!neighbor.isRevealed() && !neighbor.isFlagged() && !neighbor.isMine()) {
						neighbor.reveal();
						revealedCount++;

						// Continue cascade logic:
						// "The cascade continues recursively as long as the tiles revealed are GREEN (0
						// mines)."
						// "It stops when it reaches a tile with a number (1-8), revealing it but not
						// going further."
						if (neighbor.getAdjacentMines() == 0 && neighbor.isEmpty()) {
							revealEmptyNeighbors(newRow, newCol);
						}
						// Note: Q/S tiles are now revealed but NOT activated
						// User must click them again to activate (answer question/trigger surprise)
					}
				}
			}
		}
	}

	/**
	 * Gets all revealed but unactivated question tiles on this board. Used to
	 * handle questions revealed through cascade. Excludes questions already marked
	 * as pending.
	 * 
	 * @return List of QuestionTile objects that need handling
	 */
	public java.util.List<QuestionTile> getRevealedUnactivatedQuestions() {
		java.util.List<QuestionTile> questions = new java.util.ArrayList<>();
		for (int row = 0; row < size; row++) {
			for (int col = 0; col < size; col++) {
				Tile tile = tiles[row][col];
				if (tile.isRevealed() && tile.isQuestion()) {
					QuestionTile qTile = (QuestionTile) tile;
					// Only add if not activated AND not already in pending list
					if (!qTile.isActivated() && !qTile.isPendingInList()) {
						questions.add(qTile);
					}
				}
			}
		}
		return questions;
	}

	/**
	 * Toggles flag on a tile at the specified position.
	 * 
	 * @param row Row of the tile
	 * @param col Column of the tile
	 * @return true if flag was toggled, false otherwise
	 */
	public boolean toggleFlag(int row, int col) {
		if (!isValidPosition(row, col)) {
			return false;
		}
		return tiles[row][col].toggleFlag();
	}

	/**
	 * Reveals a random unrevealed mine on the board. Used as bonus reward for
	 * answering Medium question correctly in Easy game. Per spec: no points given
	 * for auto-revealed mine.
	 * 
	 * @return true if a mine was revealed, false if no unrevealed mines exist
	 */
	public boolean revealRandomMine() {
		List<Tile> unrevealedMines = new ArrayList<>();

		// Find all unrevealed mines
		for (int row = 0; row < size; row++) {
			for (int col = 0; col < size; col++) {
				Tile tile = tiles[row][col];
				if (tile.isMine() && !tile.isRevealed() && !tile.isFlagged()) {
					unrevealedMines.add(tile);
				}
			}
		}

		// Reveal random mine if any exist
		if (!unrevealedMines.isEmpty()) {
			int randomIndex = random.nextInt(unrevealedMines.size());
			Tile mineTile = unrevealedMines.get(randomIndex);
			mineTile.reveal();
			revealedCount++;
			return true;
		}

		return false;
	}

	/**
	 * Reveals a random 3x3 area on the board. Used as bonus reward for answering
	 * Hard question correctly in Easy game. Triggers cascade for any empty tiles.
	 * 
	 * @return Number of tiles revealed (0 if board too small)
	 */
	public int revealRandom3x3Area() {
		// Find valid starting positions (must have room for 3x3)
		List<int[]> validStarts = new ArrayList<>();
		for (int row = 0; row <= size - 3; row++) {
			for (int col = 0; col <= size - 3; col++) {
				validStarts.add(new int[] { row, col });
			}
		}

		if (validStarts.isEmpty()) {
			return 0; // Board too small
		}

		// Pick random starting position
		int randomIndex = random.nextInt(validStarts.size());
		int[] start = validStarts.get(randomIndex);
		int startRow = start[0];
		int startCol = start[1];

		// Reveal 3x3 area
		int revealed = 0;
		for (int dr = 0; dr < 3; dr++) {
			for (int dc = 0; dc < 3; dc++) {
				int row = startRow + dr;
				int col = startCol + dc;
				Tile tile = tiles[row][col];

				// Only reveal if not already revealed, not flagged, and not a mine
				if (!tile.isRevealed() && !tile.isFlagged() && !tile.isMine()) {
					tile.reveal();
					revealedCount++;
					revealed++;

					// If empty tile with no adjacent mines, trigger cascade
					if (tile.isEmpty() && tile.getAdjacentMines() == 0) {
						revealEmptyNeighbors(row, col);
					}
				}
			}
		}

		return revealed;
	}

	/**
	 * Checks if a position is within board boundaries.
	 * 
	 * @param row Row to check
	 * @param col Column to check
	 * @return true if position is valid
	 */
	public boolean isValidPosition(int row, int col) {
		return row >= 0 && row < size && col >= 0 && col < size;
	}

	/**
	 * Gets the tile at the specified position.
	 * 
	 * @param row Row of the tile
	 * @param col Column of the tile
	 * @return The tile at the position, or null if invalid
	 */
	public Tile getTile(int row, int col) {
		if (!isValidPosition(row, col)) {
			return null;
		}
		return tiles[row][col];
	}

	/**
	 * Checks if all safe tiles have been revealed (win condition).
	 * 
	 * @return true if all non-mine tiles are revealed
	 */
	public boolean allSafeTilesRevealed() {
		return revealedCount >= totalSafeTiles;
	}

	/**
	 * Gets all neighboring tiles of a position.
	 * 
	 * @param row Center row
	 * @param col Center column
	 * @return List of neighboring tiles
	 */
	public List<Tile> getNeighbors(int row, int col) {
		List<Tile> neighbors = new ArrayList<>();
		for (int dr = -1; dr <= 1; dr++) {
			for (int dc = -1; dc <= 1; dc++) {
				if (dr == 0 && dc == 0)
					continue;
				int newRow = row + dr;
				int newCol = col + dc;
				if (isValidPosition(newRow, newCol)) {
					neighbors.add(tiles[newRow][newCol]);
				}
			}
		}
		return neighbors;
	}

	// Getters
	public int getSize() {
		return size;
	}

	public Difficulty getDifficulty() {
		return difficulty;
	}

	public int getRevealedCount() {
		return revealedCount;
	}

	public int getTotalSafeTiles() {
		return totalSafeTiles;
	}

	public Tile[][] getTiles() {
		return tiles;
	}

	/**
	 * Returns a string representation of the board for console display.
	 * 
	 * @return String showing the board state
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		// Column headers
		sb.append("   ");
		for (int col = 0; col < size; col++) {
			sb.append(String.format("%2d ", col));
		}
		sb.append("\n");

		// Separator
		sb.append("   ");
		for (int col = 0; col < size; col++) {
			sb.append("---");
		}
		sb.append("\n");

		// Rows
		for (int row = 0; row < size; row++) {
			sb.append(String.format("%2d|", row));
			for (int col = 0; col < size; col++) {
				sb.append(" ").append(tiles[row][col].toString()).append(" ");
			}
			sb.append("|\n");
		}

		// Bottom separator
		sb.append("   ");
		for (int col = 0; col < size; col++) {
			sb.append("---");
		}
		sb.append("\n");

		return sb.toString();
	}

	/**
	 * Counts how many mines are still unrevealed (not flagged and not revealed).
	 * Used for display purposes to show progress.
	 * 
	 * @return Number of unrevealed mines
	 */
	public int countUnrevealedMines() {
		int count = 0;
		for (int row = 0; row < size; row++) {
			for (int col = 0; col < size; col++) {
				Tile tile = tiles[row][col];
				// Count mines that are not revealed and not flagged
				if (tile.isMine() && !tile.isRevealed() && !tile.isFlagged()) {
					count++;
				}
			}
		}
		return count;
	}

	/**
	 * Checks if all mines are correctly flagged and no safe tiles are flagged.
	 * 
	 * @return true if won by flagging
	 */
	public boolean areAllMinesCorrectlyFlagged() {
		for (int row = 0; row < size; row++) {
			for (int col = 0; col < size; col++) {
				Tile tile = tiles[row][col];

				if (tile.isMine()) {
					// Mine is correctly handled if flagged OR revealed (found via flag check)
					if (!tile.isFlagged() && !tile.isRevealed())
						return false; // Mine not handled
				} else {
					if (tile.isFlagged())
						return false; // Safe tile flagged (wrong!)
				}
			}
		}
		return true;
	}

	/**
	 * Counts how many tiles are currently flagged.
	 */
	public int getFlagCount() {
		int count = 0;
		for (int row = 0; row < size; row++) {
			for (int col = 0; col < size; col++) {
				if (tiles[row][col].isFlagged()) {
					count++;
				}
			}
		}
		return count;
	}

	/**
	 * Counts how many mines have been revealed (found).
	 */
	public int getRevealedMineCount() {
		int count = 0;
		for (int row = 0; row < size; row++) {
			for (int col = 0; col < size; col++) {
				Tile tile = tiles[row][col];
				if (tile.isMine() && tile.isRevealed()) {
					count++;
				}
			}
		}
		return count;
	}

	/**
	 * Analyzes the board to find a mathematically guaranteed move.
	 * 
	 * @return A Hint object if a move is found, null otherwise
	 */
	public Hint getSmartHint() {
		for (int row = 0; row < size; row++) {
			for (int col = 0; col < size; col++) {
				Tile tile = tiles[row][col];

				// Only analyze revealed number tiles
				if (tile.isRevealed() && !tile.isMine() && tile.getAdjacentMines() > 0) {
					List<Tile> neighbors = getNeighbors(row, col);
					List<Tile> unrevealed = new ArrayList<>();
					int flaggedCount = 0;

					for (Tile n : neighbors) {
						if (n.isFlagged()) {
							flaggedCount++;
						} else if (!n.isRevealed()) {
							unrevealed.add(n);
						}
					}

					if (unrevealed.isEmpty())
						continue;

					int mineCount = tile.getAdjacentMines();

					// Rule 1: If flagged count equals mine count, all other unrevealed neighbors
					// are safe
					if (flaggedCount == mineCount) {
						Tile safeTile = unrevealed.get(0);
						return new Hint(safeTile.getRow(), safeTile.getCol(), false,
								"Safe! All mines around (" + row + "," + col + ") are accounted for.");
					}

					// Rule 2: If unrevealed count + flagged count equals mine count, all unrevealed
					// are mines
					if (unrevealed.size() + flaggedCount == mineCount) {
						Tile mineTile = unrevealed.get(0);
						return new Hint(mineTile.getRow(), mineTile.getCol(), true,
								"Danger! This tile must contain a mine based on (" + row + "," + col + ").");
					}
				}
			}
		}
		return null;
	}
}