package View;

import Model.GameHistory;
import Model.HistoryManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class HistoryView extends JPanel {
    private GameView mainView; // Reference to main view for navigation
    private JPanel contentPanel;
    private DefaultTableModel tableModel;

    // Modern Colors
    private static final Color BG_DARK = new Color(30, 20, 60);
    private static final Color CARD_BG = new Color(45, 35, 75);
    private static final Color ACCENT_PURPLE = new Color(170, 40, 180);
    private static final Color TEXT_GRAY = new Color(200, 200, 220);

    public HistoryView(GameView mainView) {
        this.mainView = mainView;
        setLayout(new BorderLayout());
        setBackground(BG_DARK);

        // 1. Navigation Bar (Replicating GameView nav)
        createNavBar();

        // 2. Main Content
        contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 40, 20));

        // Header
        addHeader();

        // Stats Cards
        addStatsCards();

        // Table / Empty State
        addHistoryContent();

        add(contentPanel, BorderLayout.CENTER);
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

        JButton manageBtn = createNavButton("📝 Questions", false);
        manageBtn.addActionListener(e -> mainView.showQuestionView());
        nav.add(manageBtn);

        JButton statsBtn = createNavButton("⏱ History", true); // Active
        nav.add(statsBtn);

        header.add(nav, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);
    }

    private void addHeader() {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        p.setMaximumSize(new Dimension(2000, 80));

        // Text Panel
        JPanel textP = new JPanel(new GridLayout(2, 1));
        textP.setOpaque(false);
        JLabel title = new JLabel("Game History");
        title.setFont(new Font("Segoe UI", Font.BOLD, 36));
        title.setForeground(Color.WHITE);
        textP.add(title);

        JLabel subtitle = new JLabel("View all games played in the system");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        subtitle.setForeground(TEXT_GRAY);
        textP.add(subtitle);

        p.add(textP, BorderLayout.WEST);

        // Buttons Panel
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnPanel.setOpaque(false);

        JButton refreshBtn = createStyledButton("Refresh", new Color(70, 100, 180));
        refreshBtn.addActionListener(e -> refresh());

        JButton clearBtn = createStyledButton("Clear History", new Color(220, 53, 69));
        clearBtn.addActionListener(e -> clearHistory());

        btnPanel.add(refreshBtn);
        btnPanel.add(clearBtn);

        p.add(btnPanel, BorderLayout.EAST);

        contentPanel.add(p);
        contentPanel.add(Box.createVerticalStrut(30));
    }

    private void addStatsCards() {
        List<GameHistory> history = HistoryManager.loadHistory();

        int totalGames = history.size();
        double avgScore = history.isEmpty() ? 0
                : history.stream()
                        .mapToInt(GameHistory::getTotalScore)
                        .average().orElse(0);

        Set<String> uniquePlayers = history.stream()
                .flatMap(h -> java.util.stream.Stream.of(h.getPlayer1Name(), h.getPlayer2Name()))
                .filter(n -> !n.trim().equalsIgnoreCase("AI Bot"))
                .collect(Collectors.toSet());
        int activePlayers = uniquePlayers.size();

        JPanel cardsPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        cardsPanel.setOpaque(false);
        cardsPanel.setMaximumSize(new Dimension(2000, 120));

        cardsPanel.add(createStatCard("Total Games", String.valueOf(totalGames), "🏆"));
        cardsPanel.add(createStatCard("Average Score", String.format("%.0f", avgScore), "🎯"));
        cardsPanel.add(createStatCard("Active Players", String.valueOf(activePlayers), "👥"));

        contentPanel.add(cardsPanel);
        contentPanel.add(Box.createVerticalStrut(30));
    }

    private void addHistoryContent() {
        List<GameHistory> history = HistoryManager.loadHistory();

        if (history.isEmpty()) {
            JPanel emptyPanel = new RoundedPanel(20, CARD_BG);
            emptyPanel.setLayout(new GridBagLayout());
            emptyPanel.setMaximumSize(new Dimension(2000, 400));
            emptyPanel.setPreferredSize(new Dimension(800, 400));

            JLabel icon = new JLabel("🏆");
            icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 64));
            icon.setForeground(new Color(100, 80, 160));

            JLabel text = new JLabel("No games in history yet");
            text.setFont(new Font("Segoe UI", Font.BOLD, 20));
            text.setForeground(Color.WHITE);

            JLabel sub = new JLabel("Start a new game to appear here!");
            sub.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            sub.setForeground(TEXT_GRAY);

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.insets = new Insets(0, 0, 20, 0);
            emptyPanel.add(icon, gbc);
            gbc.gridy++;
            gbc.insets = new Insets(0, 0, 10, 0);
            emptyPanel.add(text, gbc);
            gbc.gridy++;
            emptyPanel.add(sub, gbc);

            contentPanel.add(emptyPanel);
        } else {
            // Table
            String[] cols = { "Date", "Player 1", "Player 2", "Total Score", "Difficulty", "Result" };
            tableModel = new DefaultTableModel(cols, 0) {
                public boolean isCellEditable(int row, int col) {
                    return false;
                }
            };

            for (GameHistory h : history) {
                tableModel.addRow(new Object[] {
                        h.getTimestamp().format(java.time.format.DateTimeFormatter.ofPattern("MMM dd, HH:mm")),
                        h.getPlayer1Name(),
                        h.getPlayer2Name(),
                        h.getTotalScore(),
                        h.getDifficulty(),
                        h.getWinner()
                });
            }

            JTable table = new JTable(tableModel);
            table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            table.setRowHeight(40);

            javax.swing.table.DefaultTableCellRenderer centerRenderer = new javax.swing.table.DefaultTableCellRenderer();
            centerRenderer.setHorizontalAlignment(JLabel.CENTER);
            table.setDefaultRenderer(Object.class, centerRenderer);
            javax.swing.table.DefaultTableCellRenderer headerRenderer = new javax.swing.table.DefaultTableCellRenderer();
            headerRenderer.setHorizontalAlignment(JLabel.CENTER);
            headerRenderer.setBackground(new Color(60, 50, 90));
            headerRenderer.setForeground(Color.WHITE);
            headerRenderer.setFont(new Font("Segoe UI", Font.BOLD, 14));
            table.getTableHeader().setDefaultRenderer(headerRenderer);
            table.setBackground(new Color(45, 35, 75));
            table.setForeground(Color.WHITE);
            table.setShowGrid(false);
            table.setSelectionBackground(ACCENT_PURPLE);

            JScrollPane scroll = new JScrollPane(table);
            scroll.getViewport().setBackground(CARD_BG);
            scroll.setBorder(BorderFactory.createEmptyBorder());
            scroll.setOpaque(false);

            RoundedPanel wrapper = new RoundedPanel(20, CARD_BG);
            wrapper.setLayout(new BorderLayout());
            wrapper.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            wrapper.add(scroll);

            contentPanel.add(wrapper);
        }
    }

    private JPanel createStatCard(String title, String value, String iconStr) {
        RoundedPanel card = new RoundedPanel(15, CARD_BG);
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        JPanel textP = new JPanel(new GridLayout(2, 1, 0, 5));
        textP.setOpaque(false);

        JLabel t = new JLabel(title);
        t.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        t.setForeground(TEXT_GRAY);

        JLabel v = new JLabel(value);
        v.setFont(new Font("Segoe UI", Font.BOLD, 32));
        v.setForeground(Color.WHITE);

        textP.add(t);
        textP.add(v);

        JLabel i = new JLabel(iconStr);
        i.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 40));
        i.setForeground(new Color(150, 130, 200));

        card.add(textP, BorderLayout.CENTER);
        card.add(i, BorderLayout.EAST);

        return card;
    }

    private void clearHistory() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to clear all history? This cannot be undone!",
                "Confirm Clear", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            HistoryManager.clearHistory();
            refresh();
            JOptionPane.showMessageDialog(this, "History cleared successfully!");
        }
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

    // Reuse Nav Button Style
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

    // Helper Rounded Panel
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
            g2.setColor(bgColor != null ? bgColor : getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
        }
    }

    // Refresh data
    public void refresh() {
        contentPanel.removeAll();
        addHeader();
        addStatsCards();
        addHistoryContent();
        contentPanel.revalidate();
        contentPanel.repaint();
    }
}
