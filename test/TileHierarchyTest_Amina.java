
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import Model.*;

/**
 * JUnit tests for EmptyTile and MineTile classes.
 * Tests tile hierarchy polymorphism and basic tile operations.
 * 
 * @author Amina Omari (Developer 1 + Product Owner)
 * @version 2.0 - Iteration 2
 */
public class TileHierarchyTest_Amina {

    private Tile emptyTile;
    private Tile mineTile;

    @Before
    public void setUp() {
        emptyTile = new EmptyTile(0, 0);
        mineTile = new MineTile(1, 1);
    }

    /**
     * Test ID: JU-001
     * Test Type: JUnit Test
     * Description: Test that EmptyTile correctly reports its type
     * Expected: getType() returns "EMPTY", isMine() returns false
     */
    @Test
    public void testEmptyTileType() {
        assertEquals("EMPTY", emptyTile.getType());
        assertFalse(emptyTile.isMine());
        assertTrue(emptyTile instanceof EmptyTile);
        assertTrue(emptyTile instanceof Tile);
    }

    /**
     * Test ID: JU-002
     * Test Type: JUnit Test
     * Description: Test that MineTile correctly reports its type
     * Expected: getType() returns "MINE", isMine() returns true
     */
    @Test
    public void testMineTileType() {
        assertEquals("MINE", mineTile.getType());
        assertTrue(mineTile.isMine());
        assertTrue(mineTile instanceof MineTile);
        assertTrue(mineTile instanceof Tile);
    }

    /**
     * Test ID: JU-003
     * Test Type: JUnit Test
     * Description: Test isEmpty() method for EmptyTile with 0 adjacent mines
     * Expected: isEmpty() returns true when adjacentMines == 0
     */
    @Test
    public void testEmptyTileIsEmpty() {
        emptyTile.setAdjacentMines(0);
        assertTrue(emptyTile.isEmpty());

        emptyTile.setAdjacentMines(3);
        assertFalse(emptyTile.isEmpty());
    }

    /**
     * Test ID: JU-004 / WB-01
     * Test Type: White-Box Test + JUnit Test
     * Description: Test reveal() functionality for tiles
     * White-Box Coverage: Branch coverage on reveal() method
     * Expected: Tile reveals successfully, cannot reveal when flagged
     */
    @Test
    public void testTileReveal() {
        // Test revealing unrevealed tile
        assertFalse(emptyTile.isRevealed());
        assertTrue(emptyTile.reveal());
        assertTrue(emptyTile.isRevealed());

        // Test cannot reveal already revealed tile
        assertFalse(emptyTile.reveal());

        // Test cannot reveal flagged tile
        mineTile.toggleFlag();
        assertFalse(mineTile.reveal());
        assertFalse(mineTile.isRevealed());
    }

    /**
     * Test ID: JU-005
     * Test Type: JUnit Test
     * Description: Test flag toggle functionality
     * Expected: Flag toggles on/off, cannot flag revealed tile
     */
    @Test
    public void testTileFlag() {
        // Test flagging unrevealed tile
        assertFalse(mineTile.isFlagged());
        assertTrue(mineTile.toggleFlag());
        assertTrue(mineTile.isFlagged());

        // Test unflagging
        assertTrue(mineTile.toggleFlag());
        assertFalse(mineTile.isFlagged());

        // Test cannot flag revealed tile
        emptyTile.reveal();
        assertFalse(emptyTile.toggleFlag());
        assertFalse(emptyTile.isFlagged());
    }

    /**
     * Test ID: JU-006
     * Test Type: JUnit Test
     * Description: Test adjacent mines tracking for EmptyTile
     * Expected: Adjacent mines count is correctly stored and retrieved
     */
    @Test
    public void testAdjacentMines() {
        assertEquals(0, emptyTile.getAdjacentMines());

        emptyTile.setAdjacentMines(5);
        assertEquals(5, emptyTile.getAdjacentMines());

        // MineTile doesn't track adjacent mines
        mineTile.setAdjacentMines(3);
        assertEquals(0, mineTile.getAdjacentMines());
    }

    /**
     * Test ID: JU-007
     * Test Type: JUnit Test
     * Description: Test display string for different tile types
     * Expected: Correct display character for each tile type
     */
    @Test
    public void testTileDisplayString() {
        // Empty tile with 0 adjacent mines
        emptyTile.setAdjacentMines(0);
        assertEquals(" ", emptyTile.toDisplayString());

        // Empty tile with adjacent mines
        emptyTile.setAdjacentMines(3);
        assertEquals("3", emptyTile.toDisplayString());

        // Mine tile
        assertEquals("*", mineTile.toDisplayString());
    }

    /**
     * Test ID: JU-008
     * Test Type: JUnit Test
     * Description: Test toString() output for revealed vs unrevealed tiles
     * Expected: Shows "." when unrevealed, "F" when flagged, display string when
     * revealed
     */
    @Test
    public void testTileToString() {
        // Unrevealed tile
        assertEquals(".", emptyTile.toString());

        // Flagged tile
        emptyTile.toggleFlag();
        assertEquals("F", emptyTile.toString());

        // Revealed tile (need to unflag first)
        emptyTile.toggleFlag();
        emptyTile.reveal();
        emptyTile.setAdjacentMines(2);
        assertEquals("2", emptyTile.toString());
    }
}