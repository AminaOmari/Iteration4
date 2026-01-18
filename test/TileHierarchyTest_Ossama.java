
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import Model.*;

/**
 * JUnit tests for SurpriseTile class.
 * Tests surprise tile behavior and surprise assignment from CSV.
 * 
 * @author Ossama Ziadat (Developer 3)
 * @version 3.0 - Iteration 3
 */
public class TileHierarchyTest_Ossama {

    private SurpriseTile surpriseTile;
    private SurpriseManager surpriseManager;

    @Before
    public void setUp() {
        surpriseTile = new SurpriseTile(2, 2);
        surpriseManager = new SurpriseManager();
    }

    /**
     * Test ID: JU-017
     * Test Type: JUnit Test
     * Description: Test SurpriseTile type identification
     * Expected: getType() returns "SURPRISE", isSurprise() returns true
     */
    @Test
    public void testSurpriseTileType() {
        assertEquals("SURPRISE", surpriseTile.getType());
        assertTrue(surpriseTile.isSurprise());
        assertTrue(surpriseTile instanceof SurpriseTile);
        assertFalse(surpriseTile.isMine());
        assertFalse(surpriseTile.isEmpty());
        assertFalse(surpriseTile.isQuestion());
    }

    /**
     * Test ID: JU-018
     * Test Type: JUnit Test
     * Description: Test surprise is initially null
     * Expected: getSurprise() and wasGoodSurprise() return null before surprise is
     * set
     */
    @Test
    public void testSurpriseInitiallyUndetermined() {
        assertNull(surpriseTile.getSurprise());
        assertNull(surpriseTile.wasGoodSurprise());
    }

    /**
     * Test ID: JU-019 / BB-03
     * Test Type: Black-Box Test + JUnit Test
     * Description: Test surprise can be assigned and persists
     * Black-Box Coverage: Surprise assignment
     * Expected: setSurprise() assigns surprise and getSurprise() returns it
     */
    @Test
    public void testSurpriseAssignmentPersistence() {
        Surprise surprise = surpriseManager.getRandomGoodSurprise();
        surpriseTile.setSurprise(surprise);

        assertNotNull(surpriseTile.getSurprise());
        assertEquals(surprise, surpriseTile.getSurprise());
        assertTrue(surpriseTile.wasGoodSurprise());
    }

    /**
     * Test ID: JU-020
     * Test Type: JUnit Test
     * Description: Test good and bad surprises work correctly
     * Expected: Good surprises have isGood()=true, bad surprises have
     * isGood()=false
     */
    @Test
    public void testGoodAndBadSurprises() {
        // Test good surprise
        Surprise goodSurprise = surpriseManager.getRandomGoodSurprise();
        assertTrue(goodSurprise.isGood());
        assertTrue(goodSurprise.getPointsEffect() > 0);
        assertTrue(goodSurprise.getLivesEffect() > 0);

        // Test bad surprise
        Surprise badSurprise = surpriseManager.getRandomBadSurprise();
        assertFalse(badSurprise.isGood());
        assertTrue(badSurprise.getPointsEffect() < 0);
        assertTrue(badSurprise.getLivesEffect() < 0);
    }

    /**
     * Test ID: JU-021 / WB-03
     * Test Type: White-Box Test + JUnit Test
     * Description: Test surprise randomness - create multiple tiles
     * White-Box Coverage: Random surprise selection
     * Expected: Over many tiles, we should see both good and bad surprises
     */
    @Test
    public void testSurpriseRandomness() {
        int goodCount = 0;
        int badCount = 0;
        int iterations = 100;

        for (int i = 0; i < iterations; i++) {
            Surprise surprise = surpriseManager.getRandomSurprise();
            if (surprise.isGood()) {
                goodCount++;
            } else {
                badCount++;
            }
        }

        // With 100 iterations, we should have some of both (very unlikely to get 0)
        assertTrue("Should have some good surprises", goodCount > 0);
        assertTrue("Should have some bad surprises", badCount > 0);

        // Check distribution is roughly 50/50 (allow 30-70 range for randomness)
        assertTrue("Distribution should be reasonable", goodCount >= 30 && goodCount <= 70);
    }

    /**
     * Test ID: JU-022
     * Test Type: JUnit Test
     * Description: Test SurpriseTile display string
     * Expected: toDisplayString() returns "!"
     */
    @Test
    public void testSurpriseTileDisplay() {
        assertEquals("!", surpriseTile.toDisplayString());

        // Display should be same regardless of surprise assignment
        surpriseTile.setSurprise(surpriseManager.getRandomSurprise());
        assertEquals("!", surpriseTile.toDisplayString());
    }

    /**
     * Test ID: JU-023
     * Test Type: JUnit Test
     * Description: Test SurpriseTile doesn't track adjacent mines
     * Expected: getAdjacentMines() returns 0, setAdjacentMines() has no effect
     */
    @Test
    public void testSurpriseTileNoAdjacentMines() {
        assertEquals(0, surpriseTile.getAdjacentMines());

        surpriseTile.setAdjacentMines(5);
        assertEquals(0, surpriseTile.getAdjacentMines());
    }

    /**
     * Test ID: JU-024
     * Test Type: JUnit Test
     * Description: Test SurpriseTile can be cast from Tile reference
     * Expected: Instanceof check and casting work correctly
     */
    @Test
    public void testSurpriseTileCasting() {
        Tile tile = new SurpriseTile(0, 0);

        assertTrue(tile instanceof SurpriseTile);
        assertTrue(tile.isSurprise());

        // Safe to cast
        SurpriseTile surprise = (SurpriseTile) tile;
        assertNotNull(surprise);

        // Assign a surprise
        surprise.setSurprise(surpriseManager.getRandomSurprise());
        assertNotNull(surprise.getSurprise());
    }

    /**
     * Test ID: JU-025
     * Test Type: JUnit Test
     * Description: Test surprise messages are loaded from CSV
     * Expected: Surprises have non-empty messages
     */
    @Test
    public void testSurpriseMessagesFromCSV() {
        Surprise surprise = surpriseManager.getRandomSurprise();
        assertNotNull(surprise.getMessage());
        assertFalse(surprise.getMessage().isEmpty());
    }
}