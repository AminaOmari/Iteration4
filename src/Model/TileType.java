package Model;

/**
 * Enum representing all possible types of tiles in the MineSweeper game. Each
 * tile on the board can be one of these types.
 */
public enum TileType {
	/** Empty tile with no adjacent mines */
	EMPTY,
	/** Tile containing a mine */
	MINE,
	/** Tile showing number of adjacent mines (1-8) */
	NUMBER,
	/** Tile containing a trivia question */
	QUESTION,
	/** Tile containing a surprise (positive or negative effect) */
	SURPRISE
}
