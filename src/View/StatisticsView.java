package View;

import Model.*;
import javax.swing.*;
import java.awt.*;

import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Dashboard for displaying game statistics and trends.
 * Includes custom painted charts for data visualization.
 * 
 * @author Team Rhino
 * @version 1.0
 */
public class StatisticsView extends JFrame {
    private static final long serialVersionUID = 1L;
    private static final Color BG_DARK = new Color(30, 30, 45);
    private static final Color BG_PANEL = new Color(45, 45, 65);
    private static final Color TEXT_MAIN = new Color(240, 240, 250);
    private static final Color ACCENT_COLOR = new Color(70, 130, 180);

    public StatisticsView() {
        setTitle("Statistics Dashboard 📊");
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setBackground(BG_DARK);
        setLayout(new BorderLayout(15, 15));

        // Load data
        List<GameHistory> history = HistoryManager.loadHistory();

        // Header
        add(createHeader(), BorderLayout.NORTH);

        // Main Content - Tabbed Pane
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(BG_PANEL);
        tabbedPane.setForeground(Color.BLACK); // Visible text on tabs
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 14));

        tabbedPane.addTab("Overview", createOverviewPanel(history));
        tabbedPane.addTab("Difficulty Analysis", createDifficultyPanel(history));
        tabbedPane.addTab("Score Trends", createTrendsPanel(history));
        tabbedPane.addTab("Player Stats", createPlayerComparisonPanel(history));

        add(tabbedPane, BorderLayout.CENTER);

        // Footer - Close Button
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footer.setBackground(BG_DARK);
        footer.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton closeBtn = new JButton("Close");
        styleButton(closeBtn);
        closeBtn.addActionListener(e -> dispose());
        footer.add(closeBtn);

        add(footer, BorderLayout.SOUTH);
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(BG_DARK);
        header.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

        JLabel title = new JLabel("Game Statistics Dashboard");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(TEXT_MAIN);
        header.add(title, BorderLayout.CENTER);

        return header;
    }

    // --- Overview Panel ---
    private JPanel createOverviewPanel(List<GameHistory> history) {
        JPanel panel = new JPanel(new GridLayout(2, 2, 15, 15));
        panel.setBackground(BG_DARK);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        int totalGames = history.size();
        long totalWins = history.stream().filter(h -> h.getRemainingLives() > 0).count();
        int totalScoreAll = history.stream().mapToInt(GameHistory::getTotalScore).sum();
        double avgScore = totalGames > 0 ? (double) totalScoreAll / totalGames : 0;
        // Determine most frequent winner
        Map<String, Long> winsByPlayer = history.stream()
                .collect(Collectors.groupingBy(GameHistory::getWinner, Collectors.counting()));
        String topPlayer = winsByPlayer.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey).orElse("N/A");

        panel.add(createStatCard("Total Games Played", String.valueOf(totalGames), new Color(100, 149, 237)));
        panel.add(createStatCard("Total Wins",
                totalWins + " (" + (totalGames > 0 ? (totalWins * 100 / totalGames) : 0) + "%)",
                new Color(76, 175, 80)));
        panel.add(createStatCard("Average Team Score", String.format("%.1f", avgScore), new Color(255, 193, 7)));
        panel.add(createStatCard("Top Player", topPlayer, new Color(156, 39, 176)));

        return panel;
    }

    private JPanel createStatCard(String title, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(BG_PANEL);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 5, 0, 0, color),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)));

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        titleLbl.setForeground(new Color(200, 200, 200));

        JLabel valueLbl = new JLabel(value);
        valueLbl.setFont(new Font("Segoe UI", Font.BOLD, 36));
        valueLbl.setForeground(TEXT_MAIN);

        card.add(titleLbl, BorderLayout.NORTH);
        card.add(valueLbl, BorderLayout.CENTER);
        return card;
    }

    // --- Difficulty Analysis Panel (Win/Loss Chart) ---
    private JPanel createDifficultyPanel(List<GameHistory> history) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG_DARK);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        Map<Difficulty, Long> gamesPerDiff = history.stream()
                .collect(Collectors.groupingBy(GameHistory::getDifficulty, Collectors.counting()));
        Map<Difficulty, Long> winsPerDiff = history.stream()
                .filter(h -> h.getRemainingLives() > 0)
                .collect(Collectors.groupingBy(GameHistory::getDifficulty, Collectors.counting()));

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (Difficulty d : Difficulty.values()) {
            long games = gamesPerDiff.getOrDefault(d, 0L);
            long wins = winsPerDiff.getOrDefault(d, 0L);
            long losses = games - wins;

            dataset.addValue(wins, "Wins", d.name());
            dataset.addValue(losses, "Losses", d.name());
        }

        JComponent chart = new SimpleBarChart(dataset);
        panel.add(createTitle("Win/Loss Ratio by Difficulty"), BorderLayout.NORTH);
        panel.add(chart, BorderLayout.CENTER);

        return panel;
    }

    // --- Trends Panel (Score over time) ---
    private JPanel createTrendsPanel(List<GameHistory> history) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG_DARK);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Take last 10 games
        List<GameHistory> recent = history.stream()
                .sorted(Comparator.comparing(GameHistory::getTimestamp)) // Sort by date ascending
                .skip(Math.max(0, history.size() - 20)) // Last 20
                .collect(Collectors.toList());

        List<Integer> scores = recent.stream().map(GameHistory::getTotalScore).collect(Collectors.toList());

        JComponent chart = new SimpleLineChart(scores);
        panel.add(createTitle("Total Score Trend (Last 20 Games)"), BorderLayout.NORTH);
        panel.add(chart, BorderLayout.CENTER);

        return panel;
    }

    // --- Player Comparison Panel ---
    private JPanel createPlayerComparisonPanel(List<GameHistory> history) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG_DARK);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Aggregate scores by "Player 1" slot vs "Player 2" slot averages
        double avgP1 = history.stream().mapToInt(GameHistory::getPlayer1Score).average().orElse(0);
        double avgP2 = history.stream().mapToInt(GameHistory::getPlayer2Score).average().orElse(0);

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue((int) avgP1, "Avg Score", "Player 1");
        dataset.addValue((int) avgP2, "Avg Score", "Player 2");

        JComponent chart = new SimpleBarChart(dataset);
        panel.add(createTitle("Average Score: Player 1 vs Player 2"), BorderLayout.NORTH);
        panel.add(chart, BorderLayout.CENTER);

        return panel;
    }

    private JLabel createTitle(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 18));
        label.setForeground(TEXT_MAIN);
        label.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        return label;
    }

    private void styleButton(JButton btn) {
        btn.setBackground(ACCENT_COLOR);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    // ==========================================================
    // Custom Lightweight Chart Components
    // ==========================================================

    // Simple dataset structure for Bar Chart
    private static class DefaultCategoryDataset {
        Map<String, Map<String, Number>> data = new LinkedHashMap<>();

        void addValue(Number value, String series, String category) {
            data.computeIfAbsent(category, k -> new LinkedHashMap<>()).put(series, value);
        }
    }

    private static class SimpleBarChart extends JPanel {
        private DefaultCategoryDataset dataset;

        public SimpleBarChart(DefaultCategoryDataset dataset) {
            this.dataset = dataset;
            setBackground(BG_PANEL);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (dataset.data.isEmpty())
                return;

            int w = getWidth();
            int h = getHeight();
            int padding = 40;
            int graphW = w - 2 * padding;
            int graphH = h - 2 * padding;

            // Axis
            g2.setColor(Color.LIGHT_GRAY);
            g2.drawLine(padding, h - padding, w - padding, h - padding); // X
            g2.drawLine(padding, padding, padding, h - padding); // Y

            // Find max value
            double maxVal = 0;
            for (Map<String, Number> cat : dataset.data.values()) {
                for (Number n : cat.values())
                    maxVal = Math.max(maxVal, n.doubleValue());
            }
            if (maxVal == 0)
                maxVal = 10;

            int numCategories = dataset.data.size();
            int barWidth = Math.min(60, graphW / (numCategories * 2));
            int gap = (graphW - (numCategories * barWidth)) / (numCategories + 1);

            int x = padding + gap;

            // Draw Bars
            Color[] colors = { new Color(76, 175, 80), new Color(220, 53, 69), new Color(33, 150, 243) };

            for (Map.Entry<String, Map<String, Number>> entry : dataset.data.entrySet()) {
                String category = entry.getKey();
                Map<String, Number> seriesData = entry.getValue();

                int groupX = x;
                int seriesIdx = 0;
                int seriesWidth = barWidth / Math.max(1, seriesData.size()); // Split bar width if multiple series

                // Draw category label
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Arial", Font.PLAIN, 12));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(category, groupX + (barWidth - fm.stringWidth(category)) / 2, h - padding + 20);

                for (Map.Entry<String, Number> series : seriesData.entrySet()) {
                    double val = series.getValue().doubleValue();
                    int barH = (int) ((val / maxVal) * graphH);

                    g2.setColor(colors[seriesIdx % colors.length]);
                    g2.fillRect(groupX, h - padding - barH, seriesWidth, barH);

                    // Value label
                    g2.setColor(Color.WHITE);
                    String valStr = String.valueOf((int) val);
                    g2.drawString(valStr, groupX + (seriesWidth - fm.stringWidth(valStr)) / 2, h - padding - barH - 5);

                    groupX += seriesWidth;
                    seriesIdx++;
                }
                x += barWidth + gap;
            }

            // Legend
            int ly = padding;
            int lx = w - 150;
            int i = 0;
            // Just verify first category keys to get series names
            if (!dataset.data.isEmpty()) {
                for (String series : dataset.data.values().iterator().next().keySet()) {
                    g2.setColor(colors[i % colors.length]);
                    g2.fillRect(lx, ly + (i * 20), 10, 10);
                    g2.setColor(Color.WHITE);
                    g2.drawString(series, lx + 15, ly + (i * 20) + 10);
                    i++;
                }
            }
        }
    }

    private static class SimpleLineChart extends JPanel {
        private List<Integer> values;

        public SimpleLineChart(List<Integer> values) {
            this.values = values;
            setBackground(BG_PANEL);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (values == null || values.isEmpty())
                return;

            int w = getWidth();
            int h = getHeight();
            int padding = 40;
            int graphW = w - 2 * padding;
            int graphH = h - 2 * padding;

            // Axis
            g2.setColor(Color.LIGHT_GRAY);
            g2.drawLine(padding, h - padding, w - padding, h - padding);
            g2.drawLine(padding, padding, padding, h - padding);

            int maxVal = values.stream().max(Integer::compareTo).orElse(10);
            int minVal = values.stream().min(Integer::compareTo).orElse(0);
            if (minVal > 0)
                minVal = 0;
            if (maxVal == minVal)
                maxVal = minVal + 10;
            int range = maxVal - minVal;

            double xStep = (double) graphW / Math.max(1, values.size() - 1);

            g2.setStroke(new BasicStroke(2f));
            g2.setColor(new Color(33, 150, 243));

            for (int i = 0; i < values.size() - 1; i++) {
                int val1 = values.get(i);
                int val2 = values.get(i + 1);

                int x1 = padding + (int) (i * xStep);
                int y1 = h - padding - (int) (((val1 - minVal) / (double) range) * graphH);
                int x2 = padding + (int) ((i + 1) * xStep);
                int y2 = h - padding - (int) (((val2 - minVal) / (double) range) * graphH);

                g2.drawLine(x1, y1, x2, y2);
                g2.fillOval(x1 - 3, y1 - 3, 6, 6);
            }
            // Last point
            int lastX = padding + (int) ((values.size() - 1) * xStep);
            int lastY = h - padding - (int) (((values.get(values.size() - 1) - minVal) / (double) range) * graphH);
            g2.fillOval(lastX - 3, lastY - 3, 6, 6);
        }
    }
}
