package View;

import Model.*;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

/**
 * Management interface for questions and game history. Provides UI for viewing,
 * adding, editing, and removing questions, as well as viewing game history.
 * 
 * @author Team Rhino
 * @version 3.0 - Iteration 3 (FIXED)
 */
public class ManagementView extends JFrame {
	private static final long serialVersionUID = 1L;
	// Modern color scheme matching screenshot
	private static final Color DARK_NAVY = new Color(25, 35, 65);
	// private static final Color MEDIUM_NAVY = new Color(35, 45, 75);
	private static final Color TABLE_HEADER = new Color(40, 50, 80);
	private static final Color TABLE_ALTERNATE = new Color(245, 247, 250);
	private static final Color BUTTON_PRIMARY = new Color(70, 100, 180);
	private static final Color BUTTON_DANGER = new Color(220, 53, 69);
	private static final Color BUTTON_SUCCESS = new Color(40, 167, 69);

	private JTabbedPane tabbedPane;
	private QuestionBank questionBank;

	// Question management components
	private JTable questionsTable;
	private DefaultTableModel questionsTableModel;
	private JButton addQuestionBtn;
	private JButton editQuestionBtn;
	private JButton deleteQuestionBtn;

	// History components
	private JTable historyTable;
	private DefaultTableModel historyTableModel;
	private JButton clearHistoryBtn;
	private JButton refreshHistoryBtn;

	/**
	 * Creates the management window with modern styling.
	 */
	public ManagementView() {
		setTitle("Question & History Management");
		setSize(1350, 750);
		setLocationRelativeTo(null);
		getContentPane().setBackground(Color.WHITE);

		questionBank = QuestionBank.getInstance(); // Use singleton

		// Create modern tabbed pane
		tabbedPane = new JTabbedPane();
		tabbedPane.setFont(new Font("Arial", Font.BOLD, 14));
		tabbedPane.setBackground(Color.LIGHT_GRAY);
		tabbedPane.setForeground(Color.DARK_GRAY);

		// Add tabs
		tabbedPane.addTab("Questions", createQuestionsPanel());
		tabbedPane.addTab("Game History", createHistoryPanel());

		add(tabbedPane);

		// Load initial data
		refreshQuestionsTable();
		refreshHistoryTable();

		// Close handler
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				dispose();
			}
		});
	}

	/**
	 * Creates the questions management panel with modern design.
	 */
	private JPanel createQuestionsPanel() {
		JPanel panel = new JPanel(new BorderLayout(0, 0));
		panel.setBackground(DARK_NAVY);

		// Title panel with dark navy background
		JPanel titlePanel = new JPanel(new BorderLayout());
		titlePanel.setBackground(DARK_NAVY);
		titlePanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

		JLabel titleLabel = new JLabel("Question Bank Management");
		titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
		titleLabel.setForeground(Color.WHITE);
		titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
		titlePanel.add(titleLabel, BorderLayout.CENTER);

		panel.add(titlePanel, BorderLayout.NORTH);

		// Table with modern styling
		String[] columnNames = { "ID", "Question", "Option 1", "Option 2", "Option 3", "Option 4", "Correct", "Level" };
		questionsTableModel = new DefaultTableModel(columnNames, 0) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};

		questionsTable = new JTable(questionsTableModel);

		// Modern table styling
		questionsTable.setFont(new Font("Arial", Font.PLAIN, 13));
		questionsTable.setRowHeight(35);
		questionsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		questionsTable.setBackground(Color.WHITE);
		questionsTable.setForeground(Color.BLACK);
		questionsTable.setGridColor(new Color(220, 220, 220));
		questionsTable.setShowGrid(true);
		questionsTable.setIntercellSpacing(new Dimension(1, 1));

		// Striped rows effect
		questionsTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
					boolean hasFocus, int row, int column) {
				Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

				if (isSelected) {
					c.setBackground(new Color(100, 149, 237));
					c.setForeground(Color.WHITE);
				} else {
					if (row % 2 == 0) {
						c.setBackground(Color.WHITE);
					} else {
						c.setBackground(TABLE_ALTERNATE);
					}
					c.setForeground(Color.BLACK);
				}

				// Center align ID, Correct, and Level columns
				if (column == 0 || column == 6 || column == 7) {
					((DefaultTableCellRenderer) c).setHorizontalAlignment(SwingConstants.CENTER);
				} else {
					((DefaultTableCellRenderer) c).setHorizontalAlignment(SwingConstants.LEFT);
				}

				return c;
			}
		});

		// Modern header styling
		JTableHeader header = questionsTable.getTableHeader();
		header.setBackground(TABLE_HEADER);
		header.setForeground(Color.WHITE);
		header.setFont(new Font("Arial", Font.BOLD, 13));
		header.setBorder(BorderFactory.createLineBorder(TABLE_HEADER));
		header.setPreferredSize(new Dimension(header.getPreferredSize().width, 40));

		// Column widths
		questionsTable.getColumnModel().getColumn(0).setPreferredWidth(40);
		questionsTable.getColumnModel().getColumn(1).setPreferredWidth(250);
		questionsTable.getColumnModel().getColumn(2).setPreferredWidth(150);
		questionsTable.getColumnModel().getColumn(3).setPreferredWidth(150);
		questionsTable.getColumnModel().getColumn(4).setPreferredWidth(150);
		questionsTable.getColumnModel().getColumn(5).setPreferredWidth(150);
		questionsTable.getColumnModel().getColumn(6).setPreferredWidth(70);
		questionsTable.getColumnModel().getColumn(7).setPreferredWidth(90);

		// Wrap in scroll pane
		JScrollPane scrollPane = new JScrollPane(questionsTable);
		scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 30, 20, 30));
		scrollPane.getViewport().setBackground(Color.WHITE);

		panel.add(scrollPane, BorderLayout.CENTER);

		// Modern buttons panel
		JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
		buttonsPanel.setBackground(DARK_NAVY);
		buttonsPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

		addQuestionBtn = createModernButton("Add Question", BUTTON_SUCCESS);
		editQuestionBtn = createModernButton("Edit Question", BUTTON_PRIMARY);
		deleteQuestionBtn = createModernButton("Delete Question", BUTTON_DANGER);
		JButton refreshBtn = createModernButton("Refresh", BUTTON_PRIMARY);

		addQuestionBtn.addActionListener(e -> showAddQuestionDialog());
		editQuestionBtn.addActionListener(e -> showEditQuestionDialog());
		deleteQuestionBtn.addActionListener(e -> deleteSelectedQuestion());
		refreshBtn.addActionListener(e -> refreshQuestionsTable());

		buttonsPanel.add(addQuestionBtn);
		buttonsPanel.add(editQuestionBtn);
		buttonsPanel.add(deleteQuestionBtn);
		buttonsPanel.add(refreshBtn);

		panel.add(buttonsPanel, BorderLayout.SOUTH);

		return panel;
	}

	/**
	 * Creates the history viewing panel with modern design.
	 */
	private JPanel createHistoryPanel() {
		JPanel panel = new JPanel(new BorderLayout(0, 0));
		panel.setBackground(DARK_NAVY);

		// Title panel
		JPanel titlePanel = new JPanel(new BorderLayout());
		titlePanel.setBackground(DARK_NAVY);
		titlePanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

		JLabel titleLabel = new JLabel("Game History");
		titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
		titleLabel.setForeground(Color.WHITE);
		titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
		titlePanel.add(titleLabel, BorderLayout.CENTER);

		panel.add(titlePanel, BorderLayout.NORTH);

		// Table
		String[] columnNames = { "Date & Time", "Player 1", "Player 2", "Player 1 Score", "Player 2 Score",
				"Total Score", "Difficulty",
				"Winner", "Lives Left" };
		historyTableModel = new DefaultTableModel(columnNames, 0) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};

		historyTable = new JTable(historyTableModel);

		// Modern table styling
		historyTable.setFont(new Font("Arial", Font.PLAIN, 13));
		historyTable.setRowHeight(35);
		historyTable.setBackground(Color.WHITE);
		historyTable.setForeground(Color.BLACK);
		historyTable.setGridColor(new Color(220, 220, 220));
		historyTable.setShowGrid(true);

		// Striped rows
		historyTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
					boolean hasFocus, int row, int column) {
				Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

				if (isSelected) {
					c.setBackground(new Color(100, 149, 237));
					c.setForeground(Color.WHITE);
				} else {
					c.setBackground(row % 2 == 0 ? Color.WHITE : TABLE_ALTERNATE);
					c.setForeground(Color.BLACK);
				}

				((DefaultTableCellRenderer) c).setHorizontalAlignment(SwingConstants.CENTER);
				return c;
			}
		});

		// Modern header
		JTableHeader header = historyTable.getTableHeader();
		header.setBackground(TABLE_HEADER);
		header.setForeground(Color.WHITE);
		header.setFont(new Font("Arial", Font.BOLD, 13));
		header.setBorder(BorderFactory.createLineBorder(TABLE_HEADER));
		header.setPreferredSize(new Dimension(header.getPreferredSize().width, 40));

		JScrollPane scrollPane = new JScrollPane(historyTable);
		scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 30, 20, 30));
		scrollPane.getViewport().setBackground(Color.WHITE);

		panel.add(scrollPane, BorderLayout.CENTER);

		// Buttons panel
		JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
		buttonsPanel.setBackground(DARK_NAVY);
		buttonsPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

		refreshHistoryBtn = createModernButton("Refresh", BUTTON_PRIMARY);
		clearHistoryBtn = createModernButton("Clear All History", BUTTON_DANGER);

		refreshHistoryBtn.addActionListener(e -> refreshHistoryTable());
		clearHistoryBtn.addActionListener(e -> clearHistory());

		buttonsPanel.add(refreshHistoryBtn);
		buttonsPanel.add(clearHistoryBtn);

		panel.add(buttonsPanel, BorderLayout.SOUTH);

		return panel;
	}

	/**
	 * Creates a modern styled button with rounded corners.
	 */
	private JButton createModernButton(String text, Color bgColor) {
		JButton button = new JButton(text) {
			@Override
			protected void paintComponent(Graphics g) {
				Graphics2D g2d = (Graphics2D) g;
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

				if (getModel().isPressed()) {
					g2d.setColor(bgColor.darker());
				} else if (getModel().isRollover()) {
					g2d.setColor(bgColor.brighter());
				} else {
					g2d.setColor(bgColor);
				}

				g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);

				super.paintComponent(g);
			}
		};

		button.setFont(new Font("Arial", Font.BOLD, 14));
		button.setForeground(Color.WHITE);
		button.setFocusPainted(false);
		button.setBorderPainted(false);
		button.setContentAreaFilled(false);
		button.setOpaque(false);
		button.setPreferredSize(new Dimension(180, 40));
		button.setCursor(new Cursor(Cursor.HAND_CURSOR));

		return button;
	}

	/**
	 * Refreshes the questions table with current data.
	 */
	private void refreshQuestionsTable() {
		questionsTableModel.setRowCount(0);
		List<Question> questions = questionBank.getAllQuestions();

		for (Question q : questions) {
			Object[] row = { q.getId(), q.getQuestionText(), q.getOption(1), // getOption expects 1-4
					q.getOption(2), q.getOption(3), q.getOption(4), q.getCorrectAnswer(), q.getLevel() };
			questionsTableModel.addRow(row);
		}
	}

	/**
	 * Refreshes the history table with current data.
	 */
	private void refreshHistoryTable() {
		historyTableModel.setRowCount(0);
		List<GameHistory> histories = HistoryManager.loadHistory();

		java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

		for (GameHistory h : histories) {
			Object[] row = { h.getTimestamp().format(formatter), h.getPlayer1Name(), h.getPlayer2Name(),
					h.getPlayer1Score(), h.getPlayer2Score(), h.getTotalScore(), h.getDifficulty().name(),
					h.getWinner(), h.getRemainingLives() };
			historyTableModel.addRow(row);
		}
	}

	/**
	 * Shows dialog to add a new question.
	 */
	private void showAddQuestionDialog() {
		JDialog dialog = new JDialog(this, "Add Question", true);
		dialog.setSize(500, 400);
		dialog.setLocationRelativeTo(this);
		dialog.setLayout(new GridBagLayout());
		dialog.getContentPane().setBackground(DARK_NAVY);

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(5, 5, 5, 5);
		gbc.fill = GridBagConstraints.HORIZONTAL;

		// Question text
		gbc.gridx = 0;
		gbc.gridy = 0;
		dialog.add(createWhiteLabel("Question:"), gbc);
		gbc.gridx = 1;
		gbc.gridwidth = 2;
		JTextField questionField = new JTextField(30);
		dialog.add(questionField, gbc);

		// Options
		JTextField[] optionFields = new JTextField[4];
		for (int i = 0; i < 4; i++) {
			gbc.gridx = 0;
			gbc.gridy = i + 1;
			gbc.gridwidth = 1;
			dialog.add(createWhiteLabel("Option " + (i + 1) + ":"), gbc);
			gbc.gridx = 1;
			gbc.gridwidth = 2;
			optionFields[i] = new JTextField(30);
			dialog.add(optionFields[i], gbc);
		}

		// Correct answer
		gbc.gridx = 0;
		gbc.gridy = 5;
		gbc.gridwidth = 1;
		dialog.add(createWhiteLabel("Correct (1-4):"), gbc);
		gbc.gridx = 1;
		gbc.gridwidth = 2;
		JSpinner correctSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 4, 1));
		dialog.add(correctSpinner, gbc);

		// Level
		gbc.gridx = 0;
		gbc.gridy = 6;
		gbc.gridwidth = 1;
		dialog.add(createWhiteLabel("Level:"), gbc);
		gbc.gridx = 1;
		gbc.gridwidth = 2;
		JComboBox<String> levelCombo = new JComboBox<>(new String[] { "Easy", "Medium", "Hard" });
		dialog.add(levelCombo, gbc);

		// Buttons
		gbc.gridx = 0;
		gbc.gridy = 7;
		gbc.gridwidth = 3;
		JPanel btnPanel = new JPanel(new FlowLayout());
		btnPanel.setBackground(DARK_NAVY);

		JButton saveBtn = createModernButton("Save", BUTTON_SUCCESS);
		JButton cancelBtn = createModernButton("Cancel", new Color(108, 117, 125));

		saveBtn.addActionListener(e -> {
			if (validateQuestionInput(questionField.getText(), optionFields)) {
				if (isDuplicateQuestionText(questionField.getText(), -1)) {
					JOptionPane.showMessageDialog(this, "Question already exists!");
					return;
				}
				boolean success = questionBank.addQuestion(questionField.getText(), optionFields[0].getText(),
						optionFields[1].getText(), optionFields[2].getText(), optionFields[3].getText(),
						(Integer) correctSpinner.getValue(),
						Difficulty.valueOf(levelCombo.getSelectedItem().toString().toUpperCase()));

				if (success) {
					refreshQuestionsTable();
					dialog.dispose();
					JOptionPane.showMessageDialog(this, "Question added successfully!");
				} else {
					JOptionPane.showMessageDialog(this, "Error adding question!", "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		});

		cancelBtn.addActionListener(e -> dialog.dispose());

		btnPanel.add(saveBtn);
		btnPanel.add(cancelBtn);
		dialog.add(btnPanel, gbc);

		dialog.setVisible(true);
	}

	/**
	 * Shows dialog to edit selected question.
	 */
	private void showEditQuestionDialog() {
		int selectedRow = questionsTable.getSelectedRow();
		if (selectedRow == -1) {
			JOptionPane.showMessageDialog(this, "Please select a question to edit!");
			return;
		}

		int questionId = (Integer) questionsTableModel.getValueAt(selectedRow, 0);
		Question question = questionBank.findQuestionById(questionId);

		if (question == null) {
			JOptionPane.showMessageDialog(this, "Question not found!");
			return;
		}

		// Similar to add dialog but pre-filled
		JDialog dialog = new JDialog(this, "Edit Question", true);
		dialog.setSize(500, 400);
		dialog.setLocationRelativeTo(this);
		dialog.setLayout(new GridBagLayout());
		dialog.getContentPane().setBackground(DARK_NAVY);

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(5, 5, 5, 5);
		gbc.fill = GridBagConstraints.HORIZONTAL;

		// Question text
		gbc.gridx = 0;
		gbc.gridy = 0;
		dialog.add(createWhiteLabel("Question:"), gbc);
		gbc.gridx = 1;
		gbc.gridwidth = 2;
		JTextField questionField = new JTextField(question.getQuestionText(), 30);
		dialog.add(questionField, gbc);

		// Options - FIXED: getOption(1-4) not getOption(0-3)
		JTextField[] optionFields = new JTextField[4];
		for (int i = 0; i < 4; i++) {
			gbc.gridx = 0;
			gbc.gridy = i + 1;
			gbc.gridwidth = 1;
			dialog.add(createWhiteLabel("Option " + (i + 1) + ":"), gbc);
			gbc.gridx = 1;
			gbc.gridwidth = 2;
			optionFields[i] = new JTextField(question.getOption(i + 1), 30); // FIXED: i+1 not i
			dialog.add(optionFields[i], gbc);
		}

		// Correct answer
		gbc.gridx = 0;
		gbc.gridy = 5;
		gbc.gridwidth = 1;
		dialog.add(createWhiteLabel("Correct (1-4):"), gbc);
		gbc.gridx = 1;
		gbc.gridwidth = 2;
		JSpinner correctSpinner = new JSpinner(new SpinnerNumberModel(question.getCorrectAnswer(), 1, 4, 1));
		dialog.add(correctSpinner, gbc);

		// Level
		gbc.gridx = 0;
		gbc.gridy = 6;
		gbc.gridwidth = 1;
		dialog.add(createWhiteLabel("Level:"), gbc);
		gbc.gridx = 1;
		gbc.gridwidth = 2;
		JComboBox<String> levelCombo = new JComboBox<>(new String[] { "Easy", "Medium", "Hard" });
		String currentLevel = question.getLevel();
		if (currentLevel.equalsIgnoreCase("easy")) {
			levelCombo.setSelectedItem("Easy");
		} else if (currentLevel.equalsIgnoreCase("medium")) {
			levelCombo.setSelectedItem("Medium");
		} else if (currentLevel.equalsIgnoreCase("hard")) {
			levelCombo.setSelectedItem("Hard");
		}
		dialog.add(levelCombo, gbc);

		// Buttons
		gbc.gridx = 0;
		gbc.gridy = 7;
		gbc.gridwidth = 3;
		JPanel btnPanel = new JPanel(new FlowLayout());
		btnPanel.setBackground(DARK_NAVY);

		JButton saveBtn = createModernButton("Save", BUTTON_SUCCESS);
		JButton cancelBtn = createModernButton("Cancel", new Color(108, 117, 125));

		saveBtn.addActionListener(e -> {
			if (validateQuestionInput(questionField.getText(), optionFields)) {
				if (isDuplicateQuestionText(questionField.getText(), questionId)) {
					JOptionPane.showMessageDialog(this, "Question already exists!");
					return;
				}
				boolean success = questionBank.updateQuestion(questionId, questionField.getText(),
						optionFields[0].getText(), optionFields[1].getText(), optionFields[2].getText(),
						optionFields[3].getText(), (Integer) correctSpinner.getValue(),
						Difficulty.valueOf(levelCombo.getSelectedItem().toString().toUpperCase()));

				if (success) {
					refreshQuestionsTable();
					dialog.dispose();
					JOptionPane.showMessageDialog(this, "Question updated successfully!");
				} else {
					JOptionPane.showMessageDialog(this, "Error updating question!", "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		});

		cancelBtn.addActionListener(e -> dialog.dispose());

		btnPanel.add(saveBtn);
		btnPanel.add(cancelBtn);
		dialog.add(btnPanel, gbc);

		dialog.setVisible(true);
	}

	/**
	 * Deletes the selected question.
	 */
	private void deleteSelectedQuestion() {
		int selectedRow = questionsTable.getSelectedRow();
		if (selectedRow == -1) {
			JOptionPane.showMessageDialog(this, "Please select a question to delete!");
			return;
		}

		int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this question?",
				"Confirm Delete", JOptionPane.YES_NO_OPTION);

		if (confirm == JOptionPane.YES_OPTION) {
			int questionId = (Integer) questionsTableModel.getValueAt(selectedRow, 0);
			boolean success = questionBank.deleteQuestion(questionId);

			if (success) {
				refreshQuestionsTable();
				JOptionPane.showMessageDialog(this, "Question deleted successfully!");
			} else {
				JOptionPane.showMessageDialog(this, "Error deleting question!", "Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	/**
	 * Clears all game history.
	 */
	private void clearHistory() {
		int confirm = JOptionPane.showConfirmDialog(this,
				"Are you sure you want to clear all game history? This cannot be undone!", "Confirm Clear",
				JOptionPane.YES_NO_OPTION);

		if (confirm == JOptionPane.YES_OPTION) {
			boolean success = HistoryManager.clearHistory();
			if (success) {
				refreshHistoryTable();
				JOptionPane.showMessageDialog(this, "History cleared successfully!");
			} else {
				JOptionPane.showMessageDialog(this, "Error clearing history!", "Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	/**
	 * Validates question input fields.
	 */
	private boolean validateQuestionInput(String questionText, JTextField[] optionFields) {
		if (questionText.trim().isEmpty()) {
			JOptionPane.showMessageDialog(this, "Question text cannot be empty!");
			return false;
		}

		java.util.Set<String> uniqueOptions = new java.util.HashSet<>();
		for (int i = 0; i < 4; i++) {
			String opt = optionFields[i].getText().trim();
			if (opt.isEmpty()) {
				JOptionPane.showMessageDialog(this, "All options must be filled!");
				return false;
			}
			if (!uniqueOptions.add(opt.toLowerCase())) {
				JOptionPane.showMessageDialog(this, "Answers cannot be duplicates!");
				return false;
			}
		}

		return true;
	}

	/**
	 * Checks if the question text already exists in the bank.
	 * skipsId can be -1 if adding new.
	 */
	private boolean isDuplicateQuestionText(String text, int skipsId) {
		QuestionBank bank = QuestionBank.getInstance();
		// We can't use bank.isDuplicateQuestion directly if we want to skip an ID (for
		// edit)
		// But for now, let's just reuse the bank's method for adds.
		// For edits, we'd need a more specific check.
		// Since the user asked "When adding a question", strict check on add is
		// priority.

		if (skipsId == -1) {
			return bank.isDuplicateQuestion(text);
		}

		// For edit: check if exists and is NOT the current ID
		for (Question q : bank.getAllQuestions()) {
			if (q.getId() != skipsId && q.getQuestionText().equalsIgnoreCase(text.trim())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Creates a white label for dark background.
	 */
	private JLabel createWhiteLabel(String text) {
		JLabel label = new JLabel(text);
		label.setForeground(Color.WHITE);
		label.setFont(new Font("Arial", Font.PLAIN, 14));
		return label;
	}
}