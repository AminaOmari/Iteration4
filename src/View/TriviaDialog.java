package View;

import Model.Question;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Dialog for displaying trivia questions to the player. Shows the question text
 * and 4 answer buttons. Returns the selected answer (1-4) when closed.
 * 
 * @author Team Rhino
 * @version 2.0 - Iteration 2
 */
public class TriviaDialog extends JDialog {
	private static final long serialVersionUID = 1L;
	private int selectedAnswer = -1; // -1 means no answer, 1-4 for answers
	private Question question;
	private JButton[] answerButtons;
	private boolean answered = false;

	/**
	 * Creates a new trivia dialog with the specified question.
	 * 
	 * @param parent    Parent frame
	 * @param question  The trivia question to display
	 * @param pointCost How many points it costs to answer
	 */
	public TriviaDialog(JFrame parent, Question question, int pointCost) {
		super(parent, "Trivia Question", true); // Modal dialog
		this.question = question;
		this.answerButtons = new JButton[4];

		initializeUI(pointCost);

		setSize(750, 550); // Larger, modern size
		setLocationRelativeTo(parent);

		// Allow closing - treat as "no answer"
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

		// Add window listener to detect when user closes without answering
		addWindowListener(new java.awt.event.WindowAdapter() {
			@Override
			public void windowClosing(java.awt.event.WindowEvent e) {
				// If they close without answering, selectedAnswer remains -1
				answered = false;
			}
		});
	}

	/**
	 * Initializes the user interface components with modern design.
	 */
	private void initializeUI(int pointCost) {
		setLayout(new BorderLayout(0, 0));

		// Modern gradient background colors (dark purple to blue gradient)
		Color bgDark = new Color(30, 20, 60);

		// Main container with gradient effect
		JPanel mainPanel = new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				Graphics2D g2d = (Graphics2D) g;
				g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
				int w = getWidth(), h = getHeight();
				GradientPaint gp = new GradientPaint(0, 0, new Color(45, 30, 90), 0, h, new Color(60, 40, 110));
				g2d.setPaint(gp);
				g2d.fillRect(0, 0, w, h);
			}
		};
		mainPanel.setLayout(new BorderLayout(0, 20));
		mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

		// Top panel - Difficulty badge and category
		JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
		topPanel.setOpaque(false);

		// Difficulty badge
		String difficultyText = question.getLevel().substring(0, 1).toUpperCase()
				+ question.getLevel().substring(1).toLowerCase();
		JLabel difficultyLabel = new JLabel(difficultyText);
		difficultyLabel.setFont(new Font("Arial", Font.BOLD, 14));
		difficultyLabel.setForeground(Color.BLACK);
		difficultyLabel.setOpaque(true);

		// Color code by difficulty
		Color badgeColor;
		switch (question.getLevel().toLowerCase()) {
			case "easy":
				badgeColor = new Color(76, 175, 80); // Green
				break;
			case "medium":
				badgeColor = new Color(255, 193, 7); // Yellow/Gold
				break;
			case "hard":
			case "expert":
				badgeColor = new Color(244, 67, 54); // Red
				break;
			default:
				badgeColor = new Color(158, 158, 158); // Gray
		}
		difficultyLabel.setBackground(badgeColor);
		difficultyLabel.setBorder(
				BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(badgeColor.darker(), 1, true),
						BorderFactory.createEmptyBorder(5, 12, 5, 12)));

		// Category/Topic label
		JLabel categoryLabel = new JLabel("💡 Software Engineering");
		categoryLabel.setFont(GameView.getEmojiFont().deriveFont(Font.PLAIN, 14f));
		categoryLabel.setForeground(new Color(200, 200, 220));

		topPanel.add(difficultyLabel);
		topPanel.add(categoryLabel);

		mainPanel.add(topPanel, BorderLayout.NORTH);

		// Center panel - Question text
		JPanel questionPanel = new JPanel(new BorderLayout());
		questionPanel.setOpaque(false);
		questionPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));

		JTextArea questionText = new JTextArea(question.getQuestionText());
		questionText.setFont(new Font("Arial", Font.BOLD, 20));
		questionText.setForeground(Color.WHITE);
		questionText.setOpaque(false);
		questionText.setLineWrap(true);
		questionText.setWrapStyleWord(true);
		questionText.setEditable(false);
		questionText.setFocusable(false);
		questionPanel.add(questionText, BorderLayout.CENTER);

		mainPanel.add(questionPanel, BorderLayout.CENTER);

		// Bottom panel - Answer options in 2x2 grid
		JPanel answersPanel = new JPanel(new GridLayout(2, 2, 15, 15));
		answersPanel.setOpaque(false);

		// Create 4 modern answer buttons
		for (int i = 0; i < 4; i++) {
			final int answerNum = i + 1;

			// Create custom styled button
			JButton btn = new JButton() {
				@Override
				protected void paintComponent(Graphics g) {
					Graphics2D g2d = (Graphics2D) g;
					g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

					// Background
					if (isEnabled()) {
						g2d.setColor(getBackground());
					} else {
						g2d.setColor(getBackground());
					}
					g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);

					// Border
					g2d.setColor(getBackground().darker());
					g2d.setStroke(new BasicStroke(2));
					g2d.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 12, 12);

					super.paintComponent(g);
				}
			};

			String optionText = (i + 1) + ". " + question.getOption(answerNum);
			btn.setText("<html><div style='padding:5px'>" + optionText + "</div></html>");
			btn.setFont(GameView.getEmojiFont().deriveFont(Font.PLAIN, 15f));
			btn.setBackground(new Color(60, 50, 90));
			btn.setForeground(Color.WHITE);
			btn.setFocusPainted(false);
			btn.setBorderPainted(false);
			btn.setContentAreaFilled(false);
			btn.setOpaque(false);
			btn.setHorizontalAlignment(SwingConstants.LEFT);
			btn.setVerticalAlignment(SwingConstants.CENTER);
			btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

			// Hover effect
			btn.addMouseListener(new java.awt.event.MouseAdapter() {
				Color originalBg = btn.getBackground();

				@Override
				public void mouseEntered(java.awt.event.MouseEvent e) {
					if (btn.isEnabled()) {
						btn.setBackground(new Color(80, 65, 120));
						btn.repaint();
					}
				}

				@Override
				public void mouseExited(java.awt.event.MouseEvent e) {
					if (btn.isEnabled()) {
						btn.setBackground(originalBg);
						btn.repaint();
					}
				}
			});

			btn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					handleAnswer(answerNum);
				}
			});

			answerButtons[i] = btn;
			answersPanel.add(btn);
		}

		mainPanel.add(answersPanel, BorderLayout.SOUTH);

		add(mainPanel, BorderLayout.CENTER);

		// Bottom button panel for "Save for Later"
		JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		bottomPanel.setBackground(bgDark);
		bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 15, 0));

		JButton saveBtn = new JButton("💾 Save for Later");
		saveBtn.setFont(GameView.getEmojiFont().deriveFont(Font.PLAIN, 14f));
		saveBtn.setBackground(new Color(100, 100, 120));
		saveBtn.setForeground(Color.WHITE);
		saveBtn.setFocusPainted(false);
		saveBtn.setBorderPainted(false);
		saveBtn.setPreferredSize(new Dimension(200, 40));
		saveBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
		saveBtn.addActionListener(e -> {
			selectedAnswer = -1;
			answered = false;
			dispose();
		});

		bottomPanel.add(saveBtn);
		add(bottomPanel, BorderLayout.SOUTH);
	}

	/**
	 * Handles when player selects an answer with modern visual feedback.
	 * 
	 * @param answerNum Answer number (1-4)
	 */
	private void handleAnswer(int answerNum) {
		if (answered)
			return; // Prevent multiple clicks

		answered = true;
		selectedAnswer = answerNum;

		// Check if answer is correct
		boolean correct = question.isCorrect(answerNum);

		// Color scheme for feedback
		Color correctColor = new Color(76, 175, 80); // Green
		Color incorrectColor = new Color(244, 67, 54); // Red
		Color neutralColor = new Color(60, 50, 90); // Original gray

		// Highlight all answers with visual feedback
		for (int i = 0; i < 4; i++) {
			answerButtons[i].setEnabled(false);

			if (i == answerNum - 1) {
				// The button they clicked
				answerButtons[i].setBackground(correct ? correctColor : incorrectColor);
				if (correct) {
					answerButtons[i].setText(answerButtons[i].getText() + " ✓");
				} else {
					answerButtons[i].setText(answerButtons[i].getText() + " ✗");
				}
			} else if (i == question.getCorrectAnswer() - 1) {
				// Show the correct answer if they were wrong
				answerButtons[i].setBackground(correctColor);
				answerButtons[i].setText(answerButtons[i].getText() + " ✓");
			} else {
				// Other wrong answers stay gray
				answerButtons[i].setBackground(neutralColor.darker());
			}
			answerButtons[i].repaint();
		}

		// Show modern feedback dialog
		String title = correct ? "✅ Correct!" : "❌ Incorrect";
		String message;

		if (correct) {
			message = "Great job! You earned bonus points and rewards!";
		} else {
			message = "The correct answer was option " + question.getCorrectAnswer() + ".\n"
					+ "You'll lose points and possibly lives.";
		}

		// Create custom option pane with modern styling
		JOptionPane pane = new JOptionPane(message,
				correct ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.WARNING_MESSAGE);
		JDialog dialog = pane.createDialog(this, title);

		// Auto-close after showing
		Timer timer = new Timer(2000, e -> {
			dialog.dispose();
			dispose(); // Close the trivia dialog too
		});
		timer.setRepeats(false);
		timer.start();

		dialog.setVisible(true);
	}

	/**
	 * Gets the answer selected by the player.
	 * 
	 * @return Answer number (1-4), or -1 if no answer selected
	 */
	public int getSelectedAnswer() {
		return selectedAnswer;
	}

	/**
	 * Checks if the player actually answered the question.
	 * 
	 * @return true if an answer was selected, false if canceled
	 */
	public boolean wasAnswered() {
		return answered;
	}

	/**
	 * Checks if the selected answer is correct.
	 * 
	 * @return true if answer is correct
	 */
	public boolean isCorrect() {
		return question.isCorrect(selectedAnswer);
	}

	/**
	 * Shows the dialog and returns whether answer was correct.
	 * 
	 * @return true if answer was correct, false otherwise
	 */
	public boolean showAndGetResult() {
		setVisible(true); // Blocks until dialog is closed
		return isCorrect();
	}
}