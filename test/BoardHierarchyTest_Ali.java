
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import Model.*;

/**
 * JUnit tests for Board class with Tile hierarchy.
 * Tests board initialization, tile placement, and hierarchy integration.
 * 
 * @author Ali Ali (Developer 4)
 * @version 2.0 - Iteration 2
 */
public class BoardHierarchyTest_Ali {

    private Board easyBoard;
    private Board mediumBoard;

    @Before
    public void setUp() {
        easyBoard = new Board(Difficulty.EASY);
        mediumBoard = new Board(Difficulty.MEDIUM);
    }

    /**
     * Test ID: JU-025
     * Test Type: JUnit Test
     * Description: Test board initializes with correct size
     * Expected: Board size matches difficulty specification
     */
    @Test
    public void testBoardSize() {
        assertEquals(9, easyBoard.getSize());
        assertEquals(13, mediumBoard.getSize());

        Board hardBoard = new Board(Difficulty.HARD);
        assertEquals(16, hardBoard.getSize());
    }

    /**
     * Test ID: JU-026
     * Test Type: JUnit Test
     * Description: Test board contains correct number of mines
     * Expected: Number of MineTile objects equals difficulty mine count
     */
    @Test
    public void testMineCount() {
        int mineCount = 0;
        for (int row = 0; row < easyBoard.getSize(); row++) {
            for (int col = 0; col < easyBoard.getSize(); col++) {
                if (easyBoard.getTile(row, col).isMine()) {
                    mineCount++;
                }
            }
        }
        assertEquals(10, mineCount); // Easy has 10 mines
    }

    /**
     * Test ID: JU-027
     * Test Type: JUnit Test
     * Description: Test board contains different tile types
     * Expected: Board has EmptyTile, MineTile, QuestionTile, SurpriseTile
     */
    @Test
    public void testTileTypeVariety() {
        boolean hasEmpty = false;
        boolean hasMine = false;
        boolean hasQuestion = false;
        boolean hasSurprise = false;

        for (int row = 0; row < easyBoard.getSize(); row++) {
            for (int col = 0; col < easyBoard.getSize(); col++) {
                Tile tile = easyBoard.getTile(row, col);
                if (tile instanceof EmptyTile || tile instanceof NumberTile)
                    hasEmpty = true;
                if (tile instanceof MineTile)
                    hasMine = true;
                if (tile instanceof QuestionTile)
                    hasQuestion = true;
                if (tile instanceof SurpriseTile)
                    hasSurprise = true;
            }
        }

        assertTrue("Board should have empty/number tiles", hasEmpty);
        assertTrue("Board should have mines", hasMine);
        assertTrue("Board should have question tiles", hasQuestion);
        assertTrue("Board should have surprise tiles", hasSurprise);
    }

    /**
     * Test ID: JU-028
     * Test Type: JUnit Test
     * Description: Test NumberTile conversion from EmptyTile
     * Expected: Tiles near mines are NumberTile with correct count
     */
    @Test
    public void testNumberTileCreation() {
        boolean foundNumberTile = false;

        for (int row = 0; row < easyBoard.getSize(); row++) {
            for (int col = 0; col < easyBoard.getSize(); col++) {
                Tile tile = easyBoard.getTile(row, col);
                if (tile instanceof NumberTile) {
                    foundNumberTile = true;
                    int adjacentMines = tile.getAdjacentMines();
                    assertTrue("Number tile should have 1-8 adjacent mines",
                            adjacentMines >= 1 && adjacentMines <= 8);

                    // Verify the count is accurate
                    int actualCount = easyBoard.countAdjacentMines(row, col);
                    assertEquals(actualCount, adjacentMines);
                }
            }
        }

        assertTrue("Board should have at least one NumberTile", foundNumberTile);
    }

    /**
     * Test ID: JU-029 / BB-02
     * Test Type: Black-Box Test + JUnit Test
     * Description: Test tile reveal functionality with hierarchy
     * Black-Box Coverage: Tile reveal validation
     * Expected: Revealing tile returns correct type and updates state
     */
    @Test
    public void testTileRevealWithHierarchy() {
        // Find an empty tile to reveal
        Tile tile = null;
        int row = -1, col = -1;

        for (int r = 0; r < easyBoard.getSize(); r++) {
            for (int c = 0; c < easyBoard.getSize(); c++) {
                Tile t = easyBoard.getTile(r, c);
                if (!t.isMine() && !t.isQuestion() && !t.isSurprise()) {
                    tile = t;
                    row = r;
                    col = c;
                    break;
                }
            }
            if (tile != null)
                break;
        }

        assertNotNull("Should find a safe tile", tile);
        assertFalse(tile.isRevealed());

        Tile revealed = easyBoard.revealTile(row, col);
        assertNotNull(revealed);
        assertTrue(revealed.isRevealed());
        assertEquals(tile, revealed);
    }

    /**
     * Test ID: JU-030 / WB-04
     * Test Type: White-Box Test + JUnit Test
     * Description: Test countAdjacentMines works correctly with MineTile objects
     * White-Box Coverage: Branch coverage on countAdjacentMines() method
     * Expected: Count matches actual number of adjacent MineTile objects
     */
    @Test
    public void testCountAdjacentMinesWithHierarchy() {
        // Find a tile and manually count adjacent mines
        for (int row = 1; row < easyBoard.getSize() - 1; row++) {
            for (int col = 1; col < easyBoard.getSize() - 1; col++) {
                Tile tile = easyBoard.getTile(row, col);
                if (!tile.isMine()) {
                    int count = easyBoard.countAdjacentMines(row, col);

                    // Manually verify by checking all neighbors
                    int manualCount = 0;
                    for (int dr = -1; dr <= 1; dr++) {
                        for (int dc = -1; dc <= 1; dc++) {
                            if (dr == 0 && dc == 0)
                                continue;
                            if (easyBoard.getTile(row + dr, col + dc).isMine()) {
                                manualCount++;
                            }
                        }
                    }

                    assertEquals("Adjacent mine count should match manual count",
                            manualCount, count);
                    return; // Test one tile is sufficient
                }
            }
        }
    }

    /**
     * Test ID: JU-031
     * Test Type: JUnit Test
     * Description: Test board respects tile polymorphism
     * Expected: All tiles can be accessed as Tile base class
     */
    @Test
    public void testBoardTilePolymorphism() {
        Tile[][] tiles = easyBoard.getTiles();

        for (int row = 0; row < easyBoard.getSize(); row++) {
            for (int col = 0; col < easyBoard.getSize(); col++) {
                Tile tile = tiles[row][col];

                // Every tile should be a Tile instance
                assertNotNull(tile);
                assertTrue(tile instanceof Tile);

                // Every tile should have a type
                assertNotNull(tile.getType());

                // Type-specific checks work
                if (tile.isMine()) {
                    assertTrue(tile instanceof MineTile);
                }
                if (tile.isQuestion()) {
                    assertTrue(tile instanceof QuestionTile);
                }
                if (tile.isSurprise()) {
                    assertTrue(tile instanceof SurpriseTile);
                }
            }
        }
    }

    /**
     * Test ID: JU-032 / BB-04
     * Test Type: Black-Box Test + JUnit Test
     * Description: Test total safe tiles calculation
     * Black-Box Coverage: Pending questions management
     * Expected: Total safe tiles = total tiles - mine count
     */
    @Test
    public void testTotalSafeTiles() {
        int totalTiles = easyBoard.getSize() * easyBoard.getSize();
        int mineCount = Difficulty.EASY.getMineCount();
        int expectedSafe = totalTiles - mineCount;

        assertEquals(expectedSafe, easyBoard.getTotalSafeTiles());
    }
}