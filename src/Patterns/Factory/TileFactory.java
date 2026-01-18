package Patterns.Factory;

import Model.*;

/**
 * Factory class for creating Tile objects.
 * Implements the Factory Method design pattern to encapsulate tile creation logic.
 * 
 * This factory centralizes tile instantiation, making it easier to:
 * - Add new tile types without modifying client code
 * - Maintain consistency in tile creation
 * - Follow the Open/Closed Principle
 * 
 * @author Team Rhino
 * @version 3.0 - Iteration 3 (Factory Method Pattern)
 */
public class TileFactory {
	
	/**
	 * Enum defining all possible tile types that can be created.
	 */
	public enum TileType {
		EMPTY,
		NUMBER,
		MINE,
		QUESTION,
		SURPRISE
	}
	
	/**
	 * Factory method that creates a Tile of the specified type.
	 * 
	 * @param type The type of tile to create
	 * @param row Row position on the board
	 * @param col Column position on the board
	 * @return A new Tile instance of the requested type
	 * @throws IllegalArgumentException if tile type is null
	 */
	public static Tile createTile(TileType type, int row, int col) {
		if (type == null) {
			throw new IllegalArgumentException("Tile type cannot be null");
		}
		
		return switch (type) {
			case EMPTY -> new EmptyTile(row, col);
			case NUMBER -> new NumberTile(row, col, 0); // Adjacent mines set later
			case MINE -> new MineTile(row, col);
			case QUESTION -> new QuestionTile(row, col);
			case SURPRISE -> new SurpriseTile(row, col);
		};
	}
	
	/**
	 * Factory method that creates a NumberTile with a specific adjacent mine count.
	 * 
	 * @param row Row position on the board
	 * @param col Column position on the board
	 * @param adjacentMines Number of adjacent mines
	 * @return A new NumberTile instance
	 */
	public static Tile createNumberTile(int row, int col, int adjacentMines) {
		return new NumberTile(row, col, adjacentMines);
	}
	
	/**
	 * Convenience method to create an empty tile.
	 * 
	 * @param row Row position
	 * @param col Column position
	 * @return A new EmptyTile
	 */
	public static Tile createEmptyTile(int row, int col) {
		return createTile(TileType.EMPTY, row, col);
	}
	
	/**
	 * Convenience method to create a mine tile.
	 * 
	 * @param row Row position
	 * @param col Column position
	 * @return A new MineTile
	 */
	public static Tile createMineTile(int row, int col) {
		return createTile(TileType.MINE, row, col);
	}
	
	/**
	 * Convenience method to create a question tile.
	 * 
	 * @param row Row position
	 * @param col Column position
	 * @return A new QuestionTile
	 */
	public static Tile createQuestionTile(int row, int col) {
		return createTile(TileType.QUESTION, row, col);
	}
	
	/**
	 * Convenience method to create a surprise tile.
	 * 
	 * @param row Row position
	 * @param col Column position
	 * @return A new SurpriseTile
	 */
	public static Tile createSurpriseTile(int row, int col) {
		return createTile(TileType.SURPRISE, row, col);
	}
}