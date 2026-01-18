package View;

import Model.Difficulty;
import Model.Question;
import Model.QuestionBank;

import javax.swing.*;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

public class QuestionView extends JPanel {
    private GameView mainView;
    private JPanel contentPanel;
    private DefaultTableModel tableModel;
    private JTable questionsTable;
    private QuestionBank questionBank;

    // Colors
    private static final Color BG_DARK = new Color(30, 20, 60);
    private static final Color CARD_BG = new Color(45, 35, 75);
    private static final Color BUTTON_PINK = new Color(200, 60, 120);
    private static final Color TEXT_GRAY = new Color(200, 200, 220);
    private static final Color BUTTON_DANGER = new Color(220, 53, 69);
    private static final Color TABLE_HEADER = new Color(60, 50, 90);

    public QuestionView(GameView mainView) {
        this.mainView = mainView;
        this.questionBank = QuestionBank.getInstance();

        setLayout(new BorderLayout());
        setBackground(BG_DARK);

        createNavBar();

        contentPanel = new JPanel();
        contentPanel.setLayout(new BorderLayout()); // Changed to BorderLayout for Table fill
        contentPanel.setOpaque(false);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 60, 40, 60));

        // Top Section (Header + Buttons)
        JPanel topSection = createHeaderSection();
        contentPanel.add(topSection, BorderLayout.NORTH);

        // Center Section (Table)
        JPanel tableSection = createTableSection();
        contentPanel.add(tableSection, BorderLayout.CENTER);

        add(contentPanel, BorderLayout.CENTER);

        refresh();
    }

    private void createNavBar() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder(20, 40, 0, 40));

        JLabel logo = new JLabel("💣 MineSweeper");
        logo.setFont(new Font("Segoe UI", Font.BOLD, 26));
        logo.setForeground(Color.WHITE);
        header.add(logo, BorderLayout.WEST);

        JPanel nav = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        nav.setOpaque(false);

        JButton homeBtn = createNavButton("🏠 Home", false);
        homeBtn.addActionListener(e -> mainView.showStartScreen());
        nav.add(homeBtn);

        JButton manageBtn = createNavButton("📝 Questions", true); // Active
        nav.add(manageBtn);

        JButton statsBtn = createNavButton("⏱ History", false);
        statsBtn.addActionListener(e -> mainView.showHistoryView());
        nav.add(statsBtn);

        header.add(nav, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);
    }

    private JPanel createHeaderSection() {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        p.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JPanel textP = new JPanel(new GridLayout(2, 1));
        textP.setOpaque(false);
        JLabel title = new JLabel("Question Management");
        title.setFont(new Font("Segoe UI", Font.BOLD, 36));
        title.setForeground(Color.WHITE);
        textP.add(title);

        JLabel subtitle = new JLabel("Manage the question bank for the game");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        subtitle.setForeground(TEXT_GRAY);
        textP.add(subtitle);

        p.add(textP, BorderLayout.WEST);

        // Buttons Panel
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnPanel.setOpaque(false);

        JButton newBtn = createStyledButton("+ New Question", BUTTON_PINK);
        newBtn.addActionListener(e -> showAddQuestionDialog());

        JButton editBtn = createStyledButton("Edit", new Color(70, 100, 180));
        editBtn.addActionListener(e -> showEditQuestionDialog());

        JButton deleteBtn = createStyledButton("Delete", BUTTON_DANGER);
        deleteBtn.addActionListener(e -> deleteSelectedQuestion());

        btnPanel.add(newBtn);
        btnPanel.add(editBtn);
        btnPanel.add(deleteBtn);

        p.add(btnPanel, BorderLayout.EAST);

        return p;
    }

    private JPanel createTableSection() {
        JPanel wrapper = new RoundedPanel(20, CARD_BG);
        wrapper.setLayout(new BorderLayout());
        wrapper.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Full columns as requested
        String[] columnNames = { "ID", "Question", "Option 1", "Option 2", "Option 3", "Option 4", "Correct", "Level" };
        tableModel = new DefaultTableModel(columnNames, 0) {
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        questionsTable = new JTable(tableModel);
        questionsTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        questionsTable.setRowHeight(35);
        questionsTable.setBackground(CARD_BG);
        questionsTable.setForeground(Color.WHITE);
        questionsTable.setGridColor(new Color(60, 50, 90));
        questionsTable.setShowGrid(true);
        questionsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        questionsTable.setSelectionBackground(new Color(100, 50, 150));
        questionsTable.setSelectionForeground(Color.WHITE);

        // Header Style
        JTableHeader header = questionsTable.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(TABLE_HEADER);
        header.setForeground(Color.WHITE);
        header.setBorder(BorderFactory.createLineBorder(TABLE_HEADER));

        // Column Widths
        questionsTable.getColumnModel().getColumn(0).setPreferredWidth(40);
        questionsTable.getColumnModel().getColumn(1).setPreferredWidth(250);
        questionsTable.getColumnModel().getColumn(6).setPreferredWidth(60);
        questionsTable.getColumnModel().getColumn(7).setPreferredWidth(80);

        JScrollPane scroll = new JScrollPane(questionsTable);
        scroll.getViewport().setBackground(CARD_BG);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setOpaque(false);

        wrapper.add(scroll, BorderLayout.CENTER);
        return wrapper;
    }

    public void refresh() {
        tableModel.setRowCount(0);
        List<Question> questions = questionBank.getAllQuestions();
        for (Question q : questions) {
            Object[] row = {
                    q.getId(),
                    q.getQuestionText(),
                    q.getOption(1), q.getOption(2), q.getOption(3), q.getOption(4),
                    q.getCorrectAnswer(),
                    q.getLevel()
            };
            tableModel.addRow(row);
        }
    }

    // --- Dialogs (Restored from ManagementView) ---

    private void showAddQuestionDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add Question", true);
        dialog.setSize(500, 500);
        dialog.setLocationRelativeTo(this);
        dialog.getContentPane().setBackground(BG_DARK);
        dialog.setLayout(new GridBagLayout());

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
        btnPanel.setBackground(BG_DARK);

        JButton saveBtn = new JButton("Save"); // Simplified style for dialog
        JButton cancelBtn = new JButton("Cancel");

        saveBtn.addActionListener(e -> {
            if (validateQuestionInput(questionField.getText(), optionFields)) {
                if (isDuplicateQuestionText(questionField.getText(), -1)) {
                    JOptionPane.showMessageDialog(dialog, "Question already exists!");
                    return;
                }
                boolean success = questionBank.addQuestion(questionField.getText(), optionFields[0].getText(),
                        optionFields[1].getText(), optionFields[2].getText(), optionFields[3].getText(),
                        (Integer) correctSpinner.getValue(),
                        Difficulty.valueOf(levelCombo.getSelectedItem().toString().toUpperCase()));

                if (success) {
                    refresh();
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

    private void showEditQuestionDialog() {
        int selectedRow = questionsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a question to edit!");
            return;
        }

        int questionId = (Integer) tableModel.getValueAt(selectedRow, 0);
        Question question = questionBank.findQuestionById(questionId);

        if (question == null) {
            JOptionPane.showMessageDialog(this, "Question not found!");
            return;
        }

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Edit Question", true);
        dialog.setSize(500, 500);
        dialog.setLocationRelativeTo(this);
        dialog.getContentPane().setBackground(BG_DARK);
        dialog.setLayout(new GridBagLayout());

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

        // Options
        JTextField[] optionFields = new JTextField[4];
        for (int i = 0; i < 4; i++) {
            gbc.gridx = 0;
            gbc.gridy = i + 1;
            gbc.gridwidth = 1;
            dialog.add(createWhiteLabel("Option " + (i + 1) + ":"), gbc);
            gbc.gridx = 1;
            gbc.gridwidth = 2;
            optionFields[i] = new JTextField(question.getOption(i + 1), 30);
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
        if (currentLevel.equalsIgnoreCase("easy"))
            levelCombo.setSelectedItem("Easy");
        else if (currentLevel.equalsIgnoreCase("medium"))
            levelCombo.setSelectedItem("Medium");
        else if (currentLevel.equalsIgnoreCase("hard"))
            levelCombo.setSelectedItem("Hard");
        dialog.add(levelCombo, gbc);

        // Buttons
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 3;
        JPanel btnPanel = new JPanel(new FlowLayout());
        btnPanel.setBackground(BG_DARK);

        JButton saveBtn = new JButton("Save");
        JButton cancelBtn = new JButton("Cancel");

        saveBtn.addActionListener(e -> {
            if (validateQuestionInput(questionField.getText(), optionFields)) {
                if (isDuplicateQuestionText(questionField.getText(), questionId)) {
                    JOptionPane.showMessageDialog(dialog, "Question already exists!");
                    return;
                }
                boolean success = questionBank.updateQuestion(questionId, questionField.getText(),
                        optionFields[0].getText(), optionFields[1].getText(), optionFields[2].getText(),
                        optionFields[3].getText(), (Integer) correctSpinner.getValue(),
                        Difficulty.valueOf(levelCombo.getSelectedItem().toString().toUpperCase()));

                if (success) {
                    refresh();
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

    private void deleteSelectedQuestion() {
        int selectedRow = questionsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a question to delete!");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this question?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            int questionId = (Integer) tableModel.getValueAt(selectedRow, 0);
            boolean success = questionBank.deleteQuestion(questionId);

            if (success) {
                refresh();
                JOptionPane.showMessageDialog(this, "Question deleted successfully!");
            } else {
                JOptionPane.showMessageDialog(this, "Error deleting question!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // --- Helpers ---

    private boolean validateQuestionInput(String questionText, JTextField[] optionFields) {
        if (questionText.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Question text cannot be empty!");
            return false;
        }

        Set<String> uniqueOptions = new HashSet<>();
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

    private boolean isDuplicateQuestionText(String text, int skipsId) {
        // Reuse bank methods or manual check as per ManagementView
        if (skipsId == -1) {
            return questionBank.isDuplicateQuestion(text);
        }
        for (Question q : questionBank.getAllQuestions()) {
            if (q.getId() != skipsId && q.getQuestionText().equalsIgnoreCase(text.trim())) {
                return true;
            }
        }
        return false;
    }

    private JLabel createWhiteLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        return label;
    }

    private JButton createNavButton(String text, boolean active) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setForeground(active ? Color.WHITE : new Color(180, 180, 180));
        btn.setBackground(active ? new Color(100, 50, 150) : new Color(0, 0, 0, 0));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(active);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JButton createStyledButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setForeground(Color.WHITE);
        btn.setBackground(bg);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setPreferredSize(new Dimension(140, 40));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    class RoundedPanel extends JPanel {
        private int radius;
        private Color bgColor;

        public RoundedPanel(int radius, Color bgColor) {
            this.radius = radius;
            this.bgColor = bgColor;
            setOpaque(false);
        }

        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(bgColor);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
        }
    }
}
