package Model;

/**
 * Represents a trivia question with multiple choice answers. Questions are
 * loaded from CSV file and displayed when QuestionTiles are activated.
 * 
 * @author Team Rhino
 * @version 2.0 - Iteration 2
 */
public class Question {
	private int id;
	private String questionText;
	private String option1;
	private String option2;
	private String option3;
	private String option4;
	private int correctAnswer; // 1-4
	private String level; // "Easy", "Medium", "Hard"

	/**
	 * Creates a new Question with all fields.
	 * 
	 * @param id            Unique question identifier
	 * @param questionText  The question text
	 * @param option1       First answer option
	 * @param option2       Second answer option
	 * @param option3       Third answer option
	 * @param option4       Fourth answer option
	 * @param correctAnswer Correct answer number (1-4)
	 * @param level         Difficulty level
	 */
	public Question(int id, String questionText, String option1, String option2, String option3, String option4,
			int correctAnswer, String level) {
		this.id = id;
		this.questionText = questionText;
		this.option1 = option1;
		this.option2 = option2;
		this.option3 = option3;
		this.option4 = option4;
		this.correctAnswer = correctAnswer;
		this.level = level;
	}

	// Getters
	public int getId() {
		return id;
	}

	public String getQuestionText() {
		return questionText;
	}

	public String getOption1() {
		return option1;
	}

	public String getOption2() {
		return option2;
	}

	public String getOption3() {
		return option3;
	}

	public String getOption4() {
		return option4;
	}

	public int getCorrectAnswer() {
		return correctAnswer;
	}

	public String getLevel() {
		return level;
	}

	/**
	 * Gets the answer option by number (1-4).
	 * 
	 * @param optionNum Option number (1-4)
	 * @return The option text
	 */
	public String getOption(int optionNum) {
		switch (optionNum) {
		case 1:
			return option1;
		case 2:
			return option2;
		case 3:
			return option3;
		case 4:
			return option4;
		default:
			return "";
		}
	}

	/**
	 * Checks if the given answer is correct.
	 * 
	 * @param answerNum Answer number (1-4)
	 * @return true if answer is correct
	 */
	public boolean isCorrect(int answerNum) {
		return answerNum == correctAnswer;
	}

	@Override
	public String toString() {
		return "Question #" + id + " [" + level + "]: " + questionText;
	}
}