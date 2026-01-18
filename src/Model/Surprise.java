package Model;

/**
 * Represents a surprise effect that can be good or bad.
 * 
 * @author Team Rhino
 * @version 3.0 - Iteration 3
 */
public class Surprise {
	private final int id;
	private final String message;
	private final int pointsEffect; // positive for good, negative for bad
	private final int livesEffect; // positive for good, negative for bad
	private final boolean isGood;

	/**
	 * Creates a new surprise effect.
	 * 
	 * @param id           Unique identifier
	 * @param message      Description message
	 * @param pointsEffect Points to add/subtract
	 * @param livesEffect  Lives to add/subtract
	 * @param isGood       Whether this is a good surprise
	 */
	public Surprise(int id, String message, int pointsEffect, int livesEffect, boolean isGood) {
		this.id = id;
		this.message = message;
		this.pointsEffect = pointsEffect;
		this.livesEffect = livesEffect;
		this.isGood = isGood;
	}

	public int getId() {
		return id;
	}

	public String getMessage() {
		return message;
	}

	public int getPointsEffect() {
		return pointsEffect;
	}

	public int getLivesEffect() {
		return livesEffect;
	}

	public boolean isGood() {
		return isGood;
	}

	@Override
	public String toString() {
		return message;
	}
}