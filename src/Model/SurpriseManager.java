package Model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Manages surprise effects loaded from CSV files. Implements Strategy pattern
 * for different surprise loading strategies.
 * 
 * @author Team Rhino
 * @version 3.0 - Iteration 3
 */
public class SurpriseManager {
	private static final String GOOD_SURPRISES_FILE = "src/Data/good_surprises.csv";
	private static final String BAD_SURPRISES_FILE = "src/Data/bad_surprises.csv";

	private List<Surprise> goodSurprises;
	private List<Surprise> badSurprises;
	private Random random;

	/**
	 * Creates a new SurpriseManager and loads surprises from CSV files.
	 */
	public SurpriseManager() {
		this.random = new Random();
		this.goodSurprises = new ArrayList<>();
		this.badSurprises = new ArrayList<>();
		loadSurprises();
	}

	/**
	 * Loads surprises from both CSV files.
	 */
	private void loadSurprises() {
		goodSurprises = loadSurprisesFromFile(GOOD_SURPRISES_FILE, true);
		badSurprises = loadSurprisesFromFile(BAD_SURPRISES_FILE, false);

		// If files don't exist or are empty, create default surprises
		if (goodSurprises.isEmpty()) {
			createDefaultGoodSurprises();
		}
		if (badSurprises.isEmpty()) {
			createDefaultBadSurprises();
		}
	}

	/**
	 * Loads surprises from a specific CSV file.
	 * 
	 * @param filename The CSV file to load
	 * @param isGood   Whether these are good surprises
	 * @return List of loaded surprises
	 */
	private List<Surprise> loadSurprisesFromFile(String filename, boolean isGood) {
		List<Surprise> surprises = new ArrayList<>();

		// Try multiple likely locations (IDE vs JAR run)
		String[] possiblePaths = { filename, "src/" + filename, "../" + filename, "resources/" + filename };

		BufferedReader br = null;
		for (String path : possiblePaths) {
			try {
				java.io.File f = new java.io.File(path);
				if (f.exists()) {
					br = new BufferedReader(new FileReader(f));
					break;
				}
			} catch (Exception ignored) {
			}
		}

		if (br == null) {
			System.err.println("Could not load " + filename + ": file not found in expected locations");
			return surprises;
		}

		try (BufferedReader reader = br) {
			String line;
			boolean firstLine = true;

			while ((line = reader.readLine()) != null) {
				// Skip header
				if (firstLine) {
					firstLine = false;
					continue;
				}

				Surprise surprise = parseSurpriseLine(line, isGood);
				if (surprise != null) {
					surprises.add(surprise);
				}
			}
		} catch (IOException e) {
			System.err.println("Could not load " + filename + ": " + e.getMessage());
		}

		return surprises;
	}

	/**
	 * Parses a CSV line into a Surprise object.
	 * 
	 * @param line   The CSV line
	 * @param isGood Whether this is a good surprise
	 * @return Surprise object or null if parsing fails
	 */
	private Surprise parseSurpriseLine(String line, boolean isGood) {
		try {
			String[] parts = line.split(",");
			if (parts.length < 4) {
				return null;
			}

			int id = Integer.parseInt(parts[0].trim());
			String message = parts[1].trim();
			int pointsEffect = Integer.parseInt(parts[2].trim());
			int livesEffect = Integer.parseInt(parts[3].trim());

			return new Surprise(id, message, pointsEffect, livesEffect, isGood);
		} catch (Exception e) {
			System.err.println("Error parsing surprise line: " + e.getMessage());
			return null;
		}
	}

	/**
	 * Creates default good surprises if file is missing.
	 */
	private void createDefaultGoodSurprises() {
		goodSurprises.add(new Surprise(1, "Lucky find! +5 points and +1 life!", 5, 1, true));
		goodSurprises.add(new Surprise(2, "Power-up! +5 points and +1 life!", 5, 1, true));
		goodSurprises.add(new Surprise(3, "Bonus! +5 points and +1 life!", 5, 1, true));
	}

	/**
	 * Creates default bad surprises if file is missing.
	 */
	private void createDefaultBadSurprises() {
		badSurprises.add(new Surprise(1, "Trap! -5 points and -1 life!", -5, -1, false));
		badSurprises.add(new Surprise(2, "Curse! -5 points and -1 life!", -5, -1, false));
		badSurprises.add(new Surprise(3, "Bad luck! -5 points and -1 life!", -5, -1, false));
	}

	/**
	 * Gets a random good surprise.
	 * 
	 * @return Random good surprise
	 */
	public Surprise getRandomGoodSurprise() {
		if (goodSurprises.isEmpty()) {
			createDefaultGoodSurprises();
		}
		return goodSurprises.get(random.nextInt(goodSurprises.size()));
	}

	/**
	 * Gets a random bad surprise.
	 * 
	 * @return Random bad surprise
	 */
	public Surprise getRandomBadSurprise() {
		if (badSurprises.isEmpty()) {
			createDefaultBadSurprises();
		}
		return badSurprises.get(random.nextInt(badSurprises.size()));
	}

	/**
	 * Gets a random surprise (50/50 chance of good or bad).
	 * 
	 * @return Random surprise
	 */
	public Surprise getRandomSurprise() {
		return random.nextBoolean() ? getRandomGoodSurprise() : getRandomBadSurprise();
	}
}