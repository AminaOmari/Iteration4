package View;

import Model.Difficulty;
import Model.Question;
import Model.QuestionBank;

import javax.swing.*;
import java.awt.*;

import java.util.List;
import java.util.Set;
import java.util.HashSet;

public class QuestionView extends JPanel {
    private GameView mainView;
    private JPanel contentPanel;
    private JPanel questionsListPanel; // Panel to hold question cards
    private QuestionBank questionBank;

    // Colors
    private static final Color BG_DARK = new Color(30, 20, 60);
    private static final Color CARD_BG = new Color(45, 35, 75);
    private static final Color BUTTON_PINK = new Color(200, 60, 120);
    private static final Color TEXT_GRAY = new Color(200, 200, 220);
    private static final Color BUTTON_DANGER = new Color(220, 53, 69); // Red
    private static final Color BUTTON_EDIT = new Color(70, 100, 240); // Bright Blue

    public QuestionView(GameView mainView) {
        this.mainView = mainView;
        this.questionBank = QuestionBank.getInstance();

        setLayout(new BorderLayout());
        setBackground(BG_DARK);

        createNavBar();

        contentPanel = new JPanel();
        contentPanel.setLayout(new BorderLayout());
        contentPanel.setOpaque(false);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 60, 40, 60));

        // Top Section (Header + New Button)
        JPanel topSection = createHeaderSection();
        contentPanel.add(topSection, BorderLayout.NORTH);

        // Center Section (Scrollable List of Cards)
        questionsListPanel = new JPanel();
        questionsListPanel.setLayout(new BoxLayout(questionsListPanel, BoxLayout.Y_AXIS));
        questionsListPanel.setBackground(BG_DARK);
        questionsListPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10)); // Inner padding

        JScrollPane scroll = new JScrollPane(questionsListPanel);
        scroll.getViewport().setBackground(BG_DARK);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.setOpaque(false);

        contentPanel.add(scroll, BorderLayout.CENTER);

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
        newBtn.setPreferredSize(new Dimension(180, 45)); // Ensure width fits text
        newBtn.addActionListener(e -> showAddQuestionDialog());

        btnPanel.add(newBtn);
        p.add(btnPanel, BorderLayout.EAST);

        return p;
    }

    // --- Card Creation ---

    private JPanel createQuestionCard(Question q) {
        JPanel card = new RoundedPanel(15, CARD_BG);
        card.setLayout(new BorderLayout(0, 15));
        card.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));
        card.setMaximumSize(new Dimension(2000, 250));
        card.setPreferredSize(new Dimension(800, 220));

        // 1. Header Row
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JLabel badge = createLevelBadge(q.getLevel());
        header.add(badge, BorderLayout.WEST);

        // Actions with Custom Painted Icons
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        actions.setOpaque(false);

        JButton editBtn = createIconButton("EDIT", BUTTON_EDIT);
        editBtn.setToolTipText("Edit Question");
        editBtn.addActionListener(e -> showEditQuestionDialog(q.getId()));

        JButton delBtn = createIconButton("DELETE", BUTTON_DANGER);
        delBtn.setToolTipText("Delete Question");
        delBtn.addActionListener(e -> deleteQuestion(q.getId()));

        actions.add(editBtn);
        actions.add(delBtn);
        header.add(actions, BorderLayout.EAST);

        card.add(header, BorderLayout.NORTH);

        // 2. Question Text
        JLabel qText = new JLabel("<html>" + q.getQuestionText() + "</html>");
        qText.setFont(new Font("Segoe UI", Font.BOLD, 22));
        qText.setForeground(Color.WHITE);
        card.add(qText, BorderLayout.CENTER);

        // 3. Options Grid
        JPanel optionsGrid = new JPanel(new GridLayout(2, 2, 20, 10));
        optionsGrid.setOpaque(false);

        for (int i = 1; i <= 4; i++) {
            boolean isCorrect = (i == q.getCorrectAnswer());
            optionsGrid.add(createOptionPanel(i + ". " + q.getOption(i), isCorrect));
        }

        card.add(optionsGrid, BorderLayout.SOUTH);

        return card;
    }

    // --- Helpers ---

    private JLabel createLevelBadge(String level) {
        String text = level.toUpperCase();
        Color bg = new Color(76, 175, 80); // Default Green
        if (level.equalsIgnoreCase("Medium"))
            bg = new Color(255, 193, 7);
        else if (level.equalsIgnoreCase("Hard"))
            bg = new Color(244, 67, 54);
        else if (level.equalsIgnoreCase("Expert"))
            bg = new Color(139, 0, 0);

        JLabel lbl = new JLabel(" " + text + " ");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(Color.WHITE);
        if (level.equalsIgnoreCase("Medium"))
            lbl.setForeground(Color.BLACK);

        lbl.setOpaque(true);
        lbl.setBackground(bg);
        lbl.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        return lbl;
    }

    private JPanel createOptionPanel(String text, boolean isCorrect) {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        Color borderColor = isCorrect ? new Color(46, 204, 113) : new Color(80, 70, 110);
        Color bgColor = isCorrect ? new Color(46, 204, 113, 30) : new Color(0, 0, 0, 0);

        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderColor, 1),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)));
        p.setBackground(bgColor);

        JLabel lbl = new JLabel("<html>" + text + (isCorrect ? " ✓" : "") + "</html>");
        lbl.setForeground(isCorrect ? new Color(46, 204, 113) : TEXT_GRAY);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        p.add(lbl, BorderLayout.CENTER);

        return p;
    }

    // --- Custom Icon Button ---
    private JButton createIconButton(String type, Color color) {
        JButton btn = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // 1. Hover Background
                if (getModel().isRollover()) {
                    g2.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 40));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                }

                // 2. Icon Drawing
                g2.setColor(color);
                g2.setStroke(new BasicStroke(2f));

                int cx = getWidth() / 2;
                int cy = getHeight() / 2;

                if (type.equals("EDIT")) {
                    // Draw Box
                    g2.drawRoundRect(cx - 9, cy - 9, 18, 18, 5, 5);
                    // Draw Pencil Line
                    g2.drawLine(cx + 3, cy - 3, cx - 2, cy + 2);
                    // Draw Pencil Tip
                    g2.fillOval(cx - 4, cy + 2, 2, 2);
                } else if (type.equals("DELETE")) {
                    // Draw Trash Can
                    int ty = cy - 6;
                    // Lid
                    g2.drawLine(cx - 6, ty, cx + 6, ty);
                    g2.drawRect(cx - 2, ty - 2, 4, 2); // Handle
                    // Bin
                    g2.drawRect(cx - 5, ty + 2, 10, 10);
                    // Lines
                    g2.drawLine(cx - 2, ty + 4, cx - 2, ty + 10);
                    g2.drawLine(cx + 2, ty + 4, cx + 2, ty + 10);
                }
            }
        };
        btn.setPreferredSize(new Dimension(45, 45));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        return btn;
    }

    // --- Dialogs (Unchanged Logic, just Context) ---

    // ... [Same showAddQuestionDialog as before] ...
    private void showAddQuestionDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add Question", true);
        dialog.setSize(500, 500);
        dialog.setLocationRelativeTo(this);
        dialog.getContentPane().setBackground(BG_DARK);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        dialog.add(createWhiteLabel("Question:"), gbc);
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        JTextField questionField = new JTextField(30);
        dialog.add(questionField, gbc);

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

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 1;
        dialog.add(createWhiteLabel("Correct (1-4):"), gbc);
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        JSpinner correctSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 4, 1));
        dialog.add(correctSpinner, gbc);

        gbc.gridx = 0;
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 1;
        dialog.add(createWhiteLabel("Level:"), gbc);
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        JComboBox<String> levelCombo = new JComboBox<>(new String[] { "Easy", "Medium", "Hard", "Expert" });
        dialog.add(levelCombo, gbc);

        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 3;
        JPanel btnPanel = new JPanel(new FlowLayout());
        btnPanel.setBackground(BG_DARK);
        JButton saveBtn = new JButton("Save");
        JButton cancelBtn = new JButton("Cancel");
        saveBtn.addActionListener(e -> {
            if (validateQuestionInput(questionField.getText(), optionFields)) {
                if (isDuplicateQuestionText(questionField.getText(), -1)) {
                    JOptionPane.showMessageDialog(dialog, "Question already exists!");
                    return;
                }
                if (questionBank.addQuestion(questionField.getText(), optionFields[0].getText(),
                        optionFields[1].getText(), optionFields[2].getText(), optionFields[3].getText(),
                        (Integer) correctSpinner.getValue(),
                        Difficulty.valueOf(levelCombo.getSelectedItem().toString().toUpperCase()))) {
                    refresh();
                    dialog.dispose();
                    JOptionPane.showMessageDialog(this, "Question added!");
                } else
                    JOptionPane.showMessageDialog(this, "Error!");
            }
        });
        cancelBtn.addActionListener(e -> dialog.dispose());
        btnPanel.add(saveBtn);
        btnPanel.add(cancelBtn);
        dialog.add(btnPanel, gbc);
        dialog.setVisible(true);
    }

    private void showEditQuestionDialog(int questionId) {
        Question question = questionBank.findQuestionById(questionId);
        if (question == null)
            return;
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Edit Question", true);
        dialog.setSize(500, 500);
        dialog.setLocationRelativeTo(this);
        dialog.getContentPane().setBackground(BG_DARK);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        dialog.add(createWhiteLabel("Question:"), gbc);
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        JTextField questionField = new JTextField(question.getQuestionText(), 30);
        dialog.add(questionField, gbc);

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

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 1;
        dialog.add(createWhiteLabel("Correct (1-4):"), gbc);
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        JSpinner correctSpinner = new JSpinner(new SpinnerNumberModel(question.getCorrectAnswer(), 1, 4, 1));
        dialog.add(correctSpinner, gbc);

        gbc.gridx = 0;
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 1;
        dialog.add(createWhiteLabel("Level:"), gbc);
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        JComboBox<String> levelCombo = new JComboBox<>(new String[] { "Easy", "Medium", "Hard", "Expert" });
        String cl = question.getLevel();
        if (cl.equalsIgnoreCase("easy"))
            levelCombo.setSelectedItem("Easy");
        else if (cl.equalsIgnoreCase("medium"))
            levelCombo.setSelectedItem("Medium");
        else if (cl.equalsIgnoreCase("hard"))
            levelCombo.setSelectedItem("Hard");
        else
            levelCombo.setSelectedItem("Expert");
        dialog.add(levelCombo, gbc);

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
                    JOptionPane.showMessageDialog(dialog, "Duplicate!");
                    return;
                }
                if (questionBank.updateQuestion(questionId, questionField.getText(), optionFields[0].getText(),
                        optionFields[1].getText(), optionFields[2].getText(), optionFields[3].getText(),
                        (Integer) correctSpinner.getValue(),
                        Difficulty.valueOf(levelCombo.getSelectedItem().toString().toUpperCase()))) {
                    refresh();
                    dialog.dispose();
                    JOptionPane.showMessageDialog(this, "Updated!");
                } else
                    JOptionPane.showMessageDialog(this, "Error!");
            }
        });
        cancelBtn.addActionListener(e -> dialog.dispose());
        btnPanel.add(saveBtn);
        btnPanel.add(cancelBtn);
        dialog.add(btnPanel, gbc);
        dialog.setVisible(true);
    }

    private void deleteQuestion(int id) {
        int confirm = JOptionPane.showConfirmDialog(this, "Delete question?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (questionBank.deleteQuestion(id)) {
                refresh();
                JOptionPane.showMessageDialog(this, "Deleted!");
            } else
                JOptionPane.showMessageDialog(this, "Error!");
        }
    }

    public void refresh() {
        questionsListPanel.removeAll();
        List<Question> questions = questionBank.getAllQuestions();
        questionsListPanel.add(Box.createVerticalStrut(10));
        for (Question q : questions) {
            questionsListPanel.add(createQuestionCard(q));
            questionsListPanel.add(Box.createVerticalStrut(20));
        }
        questionsListPanel.revalidate();
        questionsListPanel.repaint();
    }

    private boolean validateQuestionInput(String questionText, JTextField[] optionFields) {
        if (questionText.trim().isEmpty())
            return false;
        Set<String> uniqueOptions = new HashSet<>();
        for (JTextField f : optionFields) {
            if (f.getText().trim().isEmpty() || !uniqueOptions.add(f.getText().trim().toLowerCase()))
                return false;
        }
        return true;
    }

    private boolean isDuplicateQuestionText(String text, int skipsId) {
        if (skipsId == -1)
            return questionBank.isDuplicateQuestion(text);
        for (Question q : questionBank.getAllQuestions()) {
            if (q.getId() != skipsId && q.getQuestionText().equalsIgnoreCase(text.trim()))
                return true;
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
        btn.setPreferredSize(new Dimension(180, 45));
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
