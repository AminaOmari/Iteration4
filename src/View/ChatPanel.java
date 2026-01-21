package View;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * A modern, collapsible Chat System panel.
 * Features:
 * - Real-time message logs
 * - Visual difference between P1 (Left/Blue), P2 (Right/Purple), and System
 * (Center/Grey)
 * - Quick Chat buttons
 * - Input field
 */
public class ChatPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private JPanel messagesArea;
    private JTextField inputField;
    private JScrollPane scrollPane;
    private String player1Name = "Player 1";
    private String player2Name = "Player 2";
    private boolean isPlayer1Turn = true; // Tracks who is sending the message

    // Modern Colors
    private static final Color BG_COLOR = new Color(25, 20, 45);
    private static final Color P1_BUBBLE = new Color(70, 130, 180); // Steel Blue
    private static final Color P2_BUBBLE = new Color(147, 112, 219); // Medium Purple
    private static final Color TEXT_COLOR = Color.WHITE;

    public ChatPanel() {
        setLayout(new BorderLayout());
        setBackground(BG_COLOR);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 1, 0, 0, new Color(60, 60, 90)), // Separator
                new EmptyBorder(0, 0, 0, 0)));

        // 1. Header
        add(createHeader(), BorderLayout.NORTH);

        // 2. Messages Area (Scrollable)
        messagesArea = new JPanel();
        messagesArea.setLayout(new BoxLayout(messagesArea, BoxLayout.Y_AXIS));
        messagesArea.setBackground(BG_COLOR);
        messagesArea.setBorder(new EmptyBorder(10, 10, 10, 10));

        scrollPane = new JScrollPane(messagesArea);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);

        // 3. Footer (Quick Actions + Input)
        add(createFooter(), BorderLayout.SOUTH);

        // Initial welcome message
        addSystemMessage("Welcome to the Game Chat! 💬");
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(35, 30, 60));
        header.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel title = new JLabel("💬 Team Chat");
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        title.setForeground(Color.WHITE);
        header.add(title, BorderLayout.CENTER);

        // Turn indicator for chat
        JLabel turnLbl = new JLabel("Target: Open");
        turnLbl.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        turnLbl.setForeground(Color.GRAY);
        // header.add(turnLbl, BorderLayout.EAST);

        return header;
    }

    private JPanel createFooter() {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);
        footer.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Quick Chat Panel
        JPanel quickPanel = new JPanel(new GridLayout(1, 3, 5, 0));
        quickPanel.setOpaque(false);
        quickPanel.setBorder(new EmptyBorder(0, 0, 5, 0));

        quickPanel.add(createQuickButton("👍 Nice!", "Nice move!"));
        quickPanel.add(createQuickButton("⚠️ Watch out", "Be careful there!"));
        quickPanel.add(createQuickButton("🆘 Help!", "I need help!"));

        footer.add(quickPanel, BorderLayout.NORTH);

        // Input Field + Send
        JPanel inputPanel = new JPanel(new BorderLayout(5, 0));
        inputPanel.setOpaque(false);

        inputField = new JTextField();
        inputField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        inputField.setBackground(new Color(60, 55, 80));
        inputField.setForeground(Color.WHITE);
        inputField.setCaretColor(Color.WHITE);
        inputField.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        inputField.addActionListener(e -> sendMessage());

        JButton sendBtn = new JButton("➤");
        sendBtn.setBackground(new Color(100, 80, 160));
        sendBtn.setForeground(Color.WHITE);
        sendBtn.setFocusPainted(false);
        sendBtn.setBorderPainted(false);
        sendBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        sendBtn.addActionListener(e -> sendMessage());

        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendBtn, BorderLayout.EAST);

        footer.add(inputPanel, BorderLayout.CENTER);

        return footer;
    }

    private JButton createQuickButton(String label, String message) {
        JButton btn = new JButton(label);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        btn.setBackground(new Color(50, 45, 75));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addActionListener(e -> {
            addMessage(isPlayer1Turn ? player1Name : player2Name, message, isPlayer1Turn);
        });
        return btn;
    }

    public void sendMessage() {
        String text = inputField.getText().trim();
        if (!text.isEmpty()) {
            addMessage(isPlayer1Turn ? player1Name : player2Name, text, isPlayer1Turn);
            inputField.setText("");
        }
    }

    public void addMessage(String sender, String message, boolean isP1) {
        JPanel row = new JPanel();
        row.setLayout(new BoxLayout(row, BoxLayout.X_AXIS));
        row.setOpaque(false);
        row.setBorder(new EmptyBorder(2, 0, 2, 0));

        // Bubble Logic
        JPanel bubble = new RoundedPanel(15, isP1 ? P1_BUBBLE : P2_BUBBLE);
        bubble.setLayout(new BorderLayout());
        bubble.setBorder(new EmptyBorder(8, 10, 8, 10));
        bubble.setMaximumSize(new Dimension(800, 1000)); // Increased width for bottom layout

        JLabel nameLbl = new JLabel(sender);
        nameLbl.setFont(new Font("Segoe UI", Font.BOLD, 10));
        nameLbl.setForeground(new Color(230, 230, 230));

        JLabel msgLbl = new JLabel("<html><body style='width: 500px'>" + message + "</body></html>");
        msgLbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        msgLbl.setForeground(TEXT_COLOR);

        bubble.add(nameLbl, BorderLayout.NORTH);
        bubble.add(msgLbl, BorderLayout.CENTER);

        if (isP1) {
            row.add(bubble);
            row.add(Box.createHorizontalGlue()); // Push to left
        } else {
            row.add(Box.createHorizontalGlue()); // Push to right
            row.add(bubble);
        }

        messagesArea.add(row);
        messagesArea.add(Box.createVerticalStrut(5));
        refreshScroll();
    }

    public void addSystemMessage(String text) {
        JPanel row = new JPanel();
        row.setLayout(new BoxLayout(row, BoxLayout.X_AXIS));
        row.setOpaque(false);
        row.setBorder(new EmptyBorder(5, 0, 5, 0));

        JLabel lbl = new JLabel("<html><center>" + text + "</center></html>");
        lbl.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lbl.setForeground(new Color(150, 150, 150));
        lbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        row.add(Box.createHorizontalGlue());
        row.add(lbl);
        row.add(Box.createHorizontalGlue());

        messagesArea.add(row);
        refreshScroll();
    }

    private void refreshScroll() {
        messagesArea.revalidate();
        messagesArea.repaint();
        // Auto scroll to bottom
        SwingUtilities.invokeLater(() -> {
            JScrollBar vertical = scrollPane.getVerticalScrollBar();
            vertical.setValue(vertical.getMaximum());
        });
    }

    public void setPlayerNames(String p1, String p2) {
        this.player1Name = p1;
        this.player2Name = p2;
    }

    public void setCurrentTurn(boolean isP1) {
        this.isPlayer1Turn = isP1;
        // Optionally update UI to show whose bubble will appear
    }

    // Helper Inner Class
    class RoundedPanel extends JPanel {
        private int radius;
        private Color bgColor;

        public RoundedPanel(int radius, Color bgColor) {
            this.radius = radius;
            this.bgColor = bgColor;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(bgColor);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
        }
    }
}
