package Model;

import java.io.*;
import java.util.*;

/**
 * Manages the question bank loaded from CSV file. Provides methods to retrieve
 * random questions by difficulty level.
 *
 * Updated to support:
 * - Mixed difficulty distribution per game level
 * - No-repeat question tracking per game session
 * - EXPERT questions support (Iteration 3+)
 *
 * @author Team Rhino
 * @version 3.0
 */
public class QuestionBank {
	private List<Question> easyQuestions;
	private List<Question> mediumQuestions;
	private List<Question> hardQuestions;
	private List<Question> expertQuestions;

	private Set<Integer> usedQuestionIds; // Track used questions in current game
	private Random random;

	private static final String CSV_FILE = "src/DATA/questions.csv";

	// Singleton instance
	private static QuestionBank instance;

	/**
	 * Gets the singleton instance of QuestionBank.
	 *
	 * @return The QuestionBank instance
	 */
	public static QuestionBank getInstance() {
		if (instance == null) {
			instance = new QuestionBank();
		}
		return instance;
	}

	/**
	 * Creates a new QuestionBank and loads questions from CSV.
	 */
	private QuestionBank() {
		this.easyQuestions = new ArrayList<>();
		this.mediumQuestions = new ArrayList<>();
		this.hardQuestions = new ArrayList<>();
		this.expertQuestions = new ArrayList<>();
		this.usedQuestionIds = new HashSet<>();
		this.random = new Random();
		loadQuestionsFromCSV();
	}

	/**
	 * Resets the used questions tracker for a new game. Call this at the start of
	 * each game.
	 */
	public void resetUsedQuestions() {
		usedQuestionIds.clear();
	}

	/**
	 * Marks a question as used in the current game.
	 *
	 * @param questionId The ID of the question to mark as used
	 */
	public void markQuestionUsed(int questionId) {
		usedQuestionIds.add(questionId);
	}

	/**
	 * Loads questions from the CSV file. CSV format:
	 * id,question,option1,option2,option3,option4,correct,level
	 */
	private void loadQuestionsFromCSV() {
		// Try multiple possible locations for the CSV file
		String[] possiblePaths = {
				CSV_FILE, // src/DATA/questions.csv
				"DATA/questions.csv", // From src folder
				"src/DATA/questions.csv", // From project root
				"questions.csv", // Fallback to root
				"../questions.csv" // Parent directory fallback
		};

		BufferedReader reader = null;
		boolean fileFound = false;

		for (String path : possiblePaths) {
			try {
				File file = new File(path);
				if (file.exists()) {
					reader = new BufferedReader(new FileReader(file));
					fileFound = true;
					System.out.println("Found questions.csv at: " + path);
					break;
				}
			} catch (FileNotFoundException e) {
				// Try next path
			}
		}

		if (!fileFound) {
			System.err.println("Warning: questions.csv not found. Loading default questions.");
			loadDefaultQuestions();
			return;
		}

		try {
			String line;
			boolean firstLine = true;

			while ((line = reader.readLine()) != null) {
				// Skip header line
				if (firstLine) {
					firstLine = false;
					continue;
				}

				// Skip empty lines
				if (line.trim().isEmpty()) {
					continue;
				}

				try {
					Question question = parseQuestionFromLine(line);
					if (question != null) {
						addQuestionToList(question);
					}
				} catch (Exception e) {
					System.err.println("Error parsing question line: " + line);
					e.printStackTrace();
				}
			}

			reader.close();

			System.out.println("Loaded questions: " + getTotalQuestions() + " total");
			System.out.println("  Easy: " + easyQuestions.size());
			System.out.println("  Medium: " + mediumQuestions.size());
			System.out.println("  Hard: " + hardQuestions.size());
			System.out.println("  Expert: " + expertQuestions.size()); // ✅ FIXED

		} catch (IOException e) {
			System.err.println("Error reading questions file: " + e.getMessage());
			loadDefaultQuestions();
		}
	}

	/**
	 * Parses a question from a CSV line.
	 */
	private Question parseQuestionFromLine(String line) {
		// Remove carriage returns
		line = line.replace("\r", "");

		// Split CSV safely (supports quoted commas)
		String[] parts = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");

		if (parts.length < 8) {
			System.err.println("Invalid question format (expected 8 fields): " + line);
			return null;
		}

		try {
			int id = Integer.parseInt(parts[0].trim());
			String questionText = parts[1].trim().replace("\"", "");
			String option1 = parts[2].trim().replace("\"", "");
			String option2 = parts[3].trim().replace("\"", "");
			String option3 = parts[4].trim().replace("\"", "");
			String option4 = parts[5].trim().replace("\"", "");
			int correctAnswer = Integer.parseInt(parts[6].trim());
			String level = parts[7].trim().replace("\"", "").toUpperCase();

			return new Question(id, questionText, option1, option2, option3, option4, correctAnswer, level);

		} catch (NumberFormatException e) {
			System.err.println("Error parsing question numbers: " + line);
			return null;
		}
	}

	/**
	 * Adds a question to the appropriate difficulty list.
	 */
	private void addQuestionToList(Question question) {
		String level = question.getLevel().toUpperCase();

		switch (level) {
			case "EASY":
				easyQuestions.add(question);
				break;
			case "MEDIUM":
				mediumQuestions.add(question);
				break;
			case "HARD":
				hardQuestions.add(question);
				break;
			case "EXPERT":
				expertQuestions.add(question);
				break;
			default:
				System.err.println("Unknown difficulty level: " + level);
				mediumQuestions.add(question); // Default to medium
		}
	}

	/**
	 * Gets a random question with mixed difficulty based on game level.
	 * Avoids repeating questions that have been used in the current game.
	 *
	 * @param gameDifficulty The current game difficulty level
	 * @return Random question with appropriate difficulty mix, or null if none
	 *         available
	 */
	public Question getRandomQuestionMixed(Difficulty gameDifficulty) {
		int roll = random.nextInt(100); // 0-99
		String questionLevel;

		switch (gameDifficulty) {
			case EASY:
				// EASY game: mostly EASY/MEDIUM, rare HARD, tiny EXPERT
				if (roll < 55) {
					questionLevel = "EASY"; // 55%
				} else if (roll < 85) {
					questionLevel = "MEDIUM"; // 30%
				} else if (roll < 98) {
					questionLevel = "HARD"; // 13%
				} else {
					questionLevel = "EXPERT"; // 2%
				}
				break;

			case MEDIUM:
				// MEDIUM game: mostly MEDIUM, some HARD, a bit EASY, small EXPERT
				if (roll < 15) {
					questionLevel = "EASY"; // 15%
				} else if (roll < 65) {
					questionLevel = "MEDIUM"; // 50%
				} else if (roll < 92) {
					questionLevel = "HARD"; // 27%
				} else {
					questionLevel = "EXPERT"; // 8%
				}
				break;

			case HARD:
				// HARD game: mostly HARD, some MEDIUM, rare EASY, meaningful EXPERT
				if (roll < 8) {
					questionLevel = "EASY"; // 8%
				} else if (roll < 25) {
					questionLevel = "MEDIUM"; // 17%
				} else if (roll < 80) {
					questionLevel = "HARD"; // 55%
				} else {
					questionLevel = "EXPERT"; // 20%
				}
				break;

			default:
				questionLevel = "MEDIUM";
		}

		Question question = getRandomQuestionByLevel(questionLevel);

		// Try to find unused question (max 50 attempts)
		int attempts = 0;
		while (question != null && usedQuestionIds.contains(question.getId()) && attempts < 50) {
			question = getRandomQuestionByLevel(questionLevel);
			attempts++;
		}

		// If all questions of this level are used, try other levels
		if (question != null && usedQuestionIds.contains(question.getId())) {
			question = getAnyUnusedQuestion();
		}

		// Mark as used if found
		if (question != null) {
			markQuestionUsed(question.getId());
		}

		return question;
	}

	/**
	 * Gets a random question by difficulty level string.
	 *
	 * @param level The difficulty level ("EASY", "MEDIUM", "HARD", "EXPERT")
	 * @return Random question of that level, or null if none available
	 */
	private Question getRandomQuestionByLevel(String level) {
		List<Question> questions;

		switch (level.toUpperCase()) {
			case "EASY":
				questions = easyQuestions;
				break;
			case "MEDIUM":
				questions = mediumQuestions;
				break;
			case "HARD":
				questions = hardQuestions; // ✅ FIXED
				break;
			case "EXPERT":
				questions = expertQuestions; // ✅ ADDED
				break;
			default:
				questions = mediumQuestions;
		}

		if (questions.isEmpty()) {
			return null;
		}

		int index = random.nextInt(questions.size());
		return questions.get(index);
	}

	/**
	 * Gets any unused question from any difficulty level.
	 *
	 * @return An unused question, or any question if all are used
	 */
	private Question getAnyUnusedQuestion() {
		List<Question> allQuestions = new ArrayList<>();
		allQuestions.addAll(easyQuestions);
		allQuestions.addAll(mediumQuestions);
		allQuestions.addAll(hardQuestions);
		allQuestions.addAll(expertQuestions); // ✅ ADDED

		if (allQuestions.isEmpty()) {
			return null;
		}

		for (Question q : allQuestions) {
			if (!usedQuestionIds.contains(q.getId())) {
				return q;
			}
		}

		// If all used, return random (allows repeats if necessary)
		return allQuestions.get(random.nextInt(allQuestions.size()));
	}

	/**
	 * Gets a random question by game difficulty (old method for compatibility).
	 *
	 * NOTE: Game Difficulty enum is EASY/MEDIUM/HARD (not EXPERT). EXPERT is a
	 * question level.
	 */
	public Question getRandomQuestion(Difficulty difficulty) {
		List<Question> questions;

		switch (difficulty) {
			case EASY:
				questions = easyQuestions;
				break;
			case MEDIUM:
				questions = mediumQuestions;
				break;
			case HARD:
				questions = hardQuestions;
				break;
			default:
				questions = mediumQuestions;
		}

		if (questions.isEmpty()) {
			System.err.println("No questions available for difficulty: " + difficulty);
			return null;
		}

		return questions.get(random.nextInt(questions.size()));
	}

	/**
	 * Gets total number of questions loaded.
	 *
	 * @return Total questions
	 */
	public int getTotalQuestions() {
		return easyQuestions.size() + mediumQuestions.size() + hardQuestions.size() + expertQuestions.size(); // ✅ FIXED
	}

	/**
	 * Gets all questions as a list (for management UI).
	 *
	 * @return List of all questions
	 */
	public List<Question> getAllQuestions() {
		List<Question> allQuestions = new ArrayList<>();
		allQuestions.addAll(easyQuestions);
		allQuestions.addAll(mediumQuestions);
		allQuestions.addAll(hardQuestions);
		allQuestions.addAll(expertQuestions); // ✅ ADDED
		return allQuestions;
	}

	/**
	 * Loads default questions if CSV file is not found.
	 */
	private void loadDefaultQuestions() {
		// Easy questions
		easyQuestions.add(new Question(1, "What does MVC stand for?", "Model View Controller", "Many Virtual Computers",
				"Modern Visual Code", "Multiple Version Control", 1, "EASY"));

		easyQuestions.add(
				new Question(2, "What does HTML stand for?", "Hyper Text Markup Language", "High Tech Modern Language",
						"Home Tool Markup Language", "Hyperlinks and Text Markup Language", 1, "EASY"));

		// Medium questions
		mediumQuestions
				.add(new Question(3, "What is polymorphism in OOP?", "The ability of objects to take multiple forms",
						"A type of loop", "A design pattern", "A testing method", 1, "MEDIUM"));

		// Hard questions
		hardQuestions.add(new Question(4, "What is the time complexity of QuickSort in average case?", "O(n log n)",
				"O(n²)", "O(n)", "O(log n)", 1, "HARD"));

		// Expert questions (optional fallback examples)
		expertQuestions.add(new Question(5, "In Java, what does the 'volatile' keyword guarantee?",
				"Visibility of writes across threads", "Mutual exclusion", "Faster execution",
				"Automatic garbage collection",
				1, "EXPERT"));

		System.out.println("Loaded " + getTotalQuestions() + " default questions.");
	}

	/**
	 * Adds a new question to the bank.
	 *
	 * @param question The question to add
	 */
	public void addQuestion(Question question) {
		addQuestionToList(question);
	}

	/**
	 * Adds a new question with individual parameters. Convenience method for UI
	 * forms.
	 *
	 * @return true if added successfully
	 */
	public boolean addQuestion(String questionText, String opt1, String opt2, String opt3, String opt4,
			int correctAnswer, Difficulty level) {
		try {
			int newId = 1;
			List<Question> allQuestions = getAllQuestions();
			if (!allQuestions.isEmpty()) {
				newId = allQuestions.stream().mapToInt(Question::getId).max().orElse(0) + 1;
			}

			if (isDuplicateQuestion(questionText)) {
				System.err.println("Error: Question text already exists.");
				return false;
			}

			if (areOptionsUnique(opt1, opt2, opt3, opt4) == false) {
				System.err.println("Error: Duplicate options found.");
				return false;
			}

			Question newQuestion = new Question(newId, questionText, opt1, opt2, opt3, opt4, correctAnswer,
					level.name().toUpperCase());
			addQuestion(newQuestion);

			return saveQuestionsToCSV();
		} catch (Exception e) {
			System.err.println("Error adding question: " + e.getMessage());
			return false;
		}
	}

	/**
	 * Checks if a question with the same text already exists (case-insensitive).
	 * 
	 * @param text Question text
	 * @return true if exists
	 */
	public boolean isDuplicateQuestion(String text) {
		List<Question> all = getAllQuestions();
		for (Question q : all) {
			if (q.getQuestionText().trim().equalsIgnoreCase(text.trim())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks if options are unique.
	 * 
	 * @return true if all options are unique
	 */
	public boolean areOptionsUnique(String o1, String o2, String o3, String o4) {
		Set<String> opts = new HashSet<>();
		opts.add(o1.trim().toLowerCase());
		opts.add(o2.trim().toLowerCase());
		opts.add(o3.trim().toLowerCase());
		opts.add(o4.trim().toLowerCase());
		return opts.size() == 4;
	}

	/**
	 * Updates an existing question.
	 *
	 * @return true if updated successfully
	 */
	public boolean updateQuestion(int id, String questionText, String opt1, String opt2, String opt3, String opt4,
			int correctAnswer, Difficulty level) {
		try {
			removeQuestion(id);

			Question updatedQuestion = new Question(id, questionText, opt1, opt2, opt3, opt4, correctAnswer,
					level.name().toUpperCase());
			addQuestion(updatedQuestion);

			return saveQuestionsToCSV();
		} catch (Exception e) {
			System.err.println("Error updating question: " + e.getMessage());
			return false;
		}
	}

	/**
	 * Deletes a question by ID.
	 *
	 * @param id The question ID to delete
	 * @return true if deleted successfully
	 */
	public boolean deleteQuestion(int id) {
		try {
			boolean removed = removeQuestion(id);
			if (removed) {
				return saveQuestionsToCSV();
			}
			return false;
		} catch (Exception e) {
			System.err.println("Error deleting question: " + e.getMessage());
			return false;
		}
	}

	/**
	 * Removes a question from the bank by ID.
	 *
	 * @param id The question ID to remove
	 * @return true if question was found and removed
	 */
	public boolean removeQuestion(int id) {
		boolean removed = easyQuestions.removeIf(q -> q.getId() == id);
		removed |= mediumQuestions.removeIf(q -> q.getId() == id);
		removed |= hardQuestions.removeIf(q -> q.getId() == id);
		removed |= expertQuestions.removeIf(q -> q.getId() == id); // ✅ ADDED
		return removed;
	}

	/**
	 * Finds a question by ID.
	 *
	 * @param id The question ID
	 * @return The question, or null if not found
	 */
	public Question findQuestionById(int id) {
		for (Question q : easyQuestions) {
			if (q.getId() == id)
				return q;
		}
		for (Question q : mediumQuestions) {
			if (q.getId() == id)
				return q;
		}
		for (Question q : hardQuestions) {
			if (q.getId() == id)
				return q;
		}
		for (Question q : expertQuestions) { // ✅ ADDED
			if (q.getId() == id)
				return q;
		}
		return null;
	}

	/**
	 * Saves all questions back to CSV file.
	 *
	 * @return true if save was successful
	 */
	public boolean saveQuestionsToCSV() {
		try (PrintWriter writer = new PrintWriter(new FileWriter(CSV_FILE))) {
			writer.println("id,question,option1,option2,option3,option4,correct,level");

			List<Question> allQuestions = getAllQuestions();
			for (Question q : allQuestions) {
				writer.println(String.format("%d,\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",%d,%s",
						q.getId(),
						q.getQuestionText(),
						q.getOption1(),
						q.getOption2(),
						q.getOption3(),
						q.getOption4(),
						q.getCorrectAnswer(),
						q.getLevel()));
			}

			System.out.println("Saved " + allQuestions.size() + " questions to " + CSV_FILE);
			return true;

		} catch (IOException e) {
			System.err.println("Error saving questions: " + e.getMessage());
			return false;
		}
	}
}