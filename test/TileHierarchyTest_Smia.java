
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import Model.*;

/**
 * JUnit tests for NumberTile and QuestionTile classes.
 * Tests specific functionality of numbered and question tiles.
 * 
 * @author Smia Idres (Developer 2 + Scrum Master)
 * @version 2.0 - Iteration 2
 */
public class TileHierarchyTest_Smia {

    private NumberTile numberTile;
    private QuestionTile questionTile;

    @Before
    public void setUp() {
        numberTile = new NumberTile(0, 0, 5);
        questionTile = new QuestionTile(1, 1);
    }

    /**
     * Test ID: JU-009 / BB-01
     * Test Type: Black-Box Test + JUnit Test
     * Description: Test NumberTile initialization with adjacent mine count
     * Black-Box Coverage: Question difficulty level validation
     * Expected: NumberTile stores correct adjacent mine count from constructor
     */
    @Test
    public void testNumberTileInitialization() {
        assertEquals("NUMBER", numberTile.getType());
        assertEquals(5, numberTile.getAdjacentMines());
        assertTrue(numberTile instanceof NumberTile);
        assertFalse(numberTile.isMine());
        assertFalse(numberTile.isEmpty());
    }

    /**
     * Test ID: JU-010
     * Test Type: JUnit Test
     * Description: Test NumberTile displays correct number
     * Expected: toDisplayString() returns string representation of adjacent mine
     * count
     */
    @Test
    public void testNumberTileDisplay() {
        assertEquals("5", numberTile.toDisplayString());

        NumberTile tile2 = new NumberTile(2, 2, 8);
        assertEquals("8", tile2.toDisplayString());

        numberTile.setAdjacentMines(1);
        assertEquals("1", numberTile.toDisplayString());
    }

    /**
     * Test ID: JU-011
     * Test Type: JUnit Test
     * Description: Test QuestionTile type checking methods
     * Expected: isQuestion() returns true, other type checks return false
     */
    @Test
    public void testQuestionTileTypeChecks() {
        assertEquals("QUESTION", questionTile.getType());
        assertTrue(questionTile.isQuestion());
        assertFalse(questionTile.isMine());
        assertFalse(questionTile.isEmpty());
        assertFalse(questionTile.isSurprise());
    }

    /**
     * Test ID: JU-012 / WB-02
     * Test Type: White-Box Test + JUnit Test
     * Description: Test QuestionTile activation mechanism
     * White-Box Coverage: Statement coverage on activate() method
     * Expected: Initially not activated, becomes activated after activate() call
     */
    @Test
    public void testQuestionTileActivation() {
        assertFalse(questionTile.isActivated());

        questionTile.activate();
        assertTrue(questionTile.isActivated());

        // Activation should persist
        assertTrue(questionTile.isActivated());
    }

    /**
     * Test ID: JU-013
     * Test Type: JUnit Test
     * Description: Test QuestionTile display string
     * Expected: Always displays "?" regardless of activation status
     */
    @Test
    public void testQuestionTileDisplay() {
        assertEquals("?", questionTile.toDisplayString());

        questionTile.activate();
        assertEquals("?", questionTile.toDisplayString());
    }

    /**
     * Test ID: JU-014
     * Test Type: JUnit Test
     * Description: Test that NumberTile is not empty
     * Expected: isEmpty() always returns false for NumberTile
     */
    @Test
    public void testNumberTileNotEmpty() {
        assertFalse(numberTile.isEmpty());

        // Even with setAdjacentMines to 0, NumberTile is still not "empty"
        numberTile.setAdjacentMines(0);
        assertFalse(numberTile.isEmpty());
    }

    /**
     * Test ID: JU-015
     * Test Type: JUnit Test
     * Description: Test that QuestionTile doesn't track adjacent mines
     * Expected: getAdjacentMines() returns 0, setAdjacentMines() has no effect
     */
    @Test
    public void testQuestionTileNoAdjacentMines() {
        assertEquals(0, questionTile.getAdjacentMines());

        questionTile.setAdjacentMines(5);
        assertEquals(0, questionTile.getAdjacentMines());
    }

    /**
     * Test ID: JU-016
     * Test Type: JUnit Test
     * Description: Test polymorphism - tiles can be treated as base Tile class
     * Expected: Can store NumberTile and QuestionTile as Tile references
     */
    @Test
    public void testPolymorphism() {
        Tile tile1 = new NumberTile(0, 0, 3);
        Tile tile2 = new QuestionTile(1, 1);

        assertEquals("NUMBER", tile1.getType());
        assertEquals("QUESTION", tile2.getType());

        assertFalse(tile1.isMine());
        assertFalse(tile2.isMine());

        assertTrue(tile2.isQuestion());
        assertFalse(tile1.isQuestion());
    }
}