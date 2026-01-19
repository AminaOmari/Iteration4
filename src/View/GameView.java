package View;

import Model.*;
import Control.GameController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * Main view class for the MineSweeper game. Handles all GUI components and user
 * interface.
 * 
 * Updated to match corrected GameController API.
 */
public class GameView extends JFrame {
	private static final long serialVersionUID = 1L;
	// Main panels
	private JPanel mainPanel;
	private JPanel startPanel;
	private JPanel gamePanel;
	private JPanel statusPanel;
	private HistoryView historyView;
	private QuestionView questionView;

	// Start screen components
	private JTextField player1NameField;
	private JTextField player2NameField;
	private JComboBox<String> difficultyCombo;
	private JButton startButton;
	private JButton demoButton;

	// Game components
	private JPanel board1Panel;
	private JPanel board2Panel;
	private JButton[][] board1Buttons;
	private JButton[][] board2Buttons;

	// Pending questions panels
	private JPanel player1QuestionsPanel;
	private JPanel player2QuestionsPanel;

	// Status components
	private JLabel livesLabel;
	private JLabel currentPlayerLabel;
	private JLabel player1ScoreLabel;
	private JLabel player2ScoreLabel;
	private JLabel totalScoreLabel;
	private JLabel messageLabel;
	private JLabel minesLabel; // Global mines count (Center)
	private JLabel board1MinesLabel; // Board 1 specific
	private JLabel board2MinesLabel; // Board 2 specific
	private JButton helpButton;

	// Store player names for display
	private String player1Name = "Player 1";
	private String player2Name = "Player 2";

	// Controller reference
	private GameController controller;

	// Modern color scheme matching start screen
	private static final Color BACKGROUND_DARK = new Color(30, 20, 60); // Main background
	private static final Color BACKGROUND_MEDIUM = new Color(40, 30, 75); // Secondary background
	private static final Color STATUS_BAR = new Color(25, 20, 50); // Status panel
	private static final Color BOARD_CONTAINER = new Color(35, 25, 65); // Board containers

	// Tile colors - Modern purple/blue theme
	private static final Color REVEALED_COLOR = new Color(30, 40, 60); // Darker background for revealed tiles
	private static final Color MINE_COLOR = new Color(231, 76, 60); // Red for mines
	private static final Color FLAG_COLOR = new Color(244, 67, 54); // Red flag
	private static final Color QUESTION_COLOR = new Color(255, 193, 7); // Gold for questions
	private static final Color SURPRISE_COLOR = new Color(156, 39, 176); // Purple for surprises
	// private static final Color EMPTY_COLOR = new Color(50, 45, 75); // Empty
	// revealed

	// Number colors (1-8) - Bright colors for visibility
	private static final Color[] NUMBER_COLORS = { new Color(100, 181, 246), // 1 - Light Blue
			new Color(129, 199, 132), // 2 - Light Green
			new Color(239, 83, 80), // 3 - Light Red
			new Color(186, 104, 200), // 4 - Light Purple
			new Color(255, 167, 38), // 5 - Light Orange
			new Color(77, 208, 225), // 6 - Light Cyan
			new Color(255, 213, 79), // 7 - Light Yellow
			new Color(238, 238, 238) // 8 - Light Gray
	};

	/**
	 * Creates the main game window.
	 */
	public GameView() {
		setTitle("MineSweeper - Rhino Team");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setMinimumSize(new Dimension(1100, 750));
		setExtendedState(JFrame.MAXIMIZED_BOTH);

		mainPanel = new JPanel(new CardLayout());

		createStartPanel();
		createGamePanel();

		historyView = new HistoryView(this);
		questionView = new QuestionView(this);

		mainPanel.add(startPanel, "START");
		mainPanel.add(gamePanel, "GAME");
		mainPanel.add(historyView, "HISTORY");
		mainPanel.add(questionView, "QUESTIONS");

		add(mainPanel);
		showStartScreen();

		setLocationRelativeTo(null);
	}

	/**
	 * Creates the start/welcome screen panel with modern design.
	 */
	// Particle class for background animation

	private void createStartPanel() {
		// Custom panel with animated background
		// Custom panel with modern dark gradient
		startPanel = new JPanel(new BorderLayout()) {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				Graphics2D g2d = (Graphics2D) g;
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				// Deep purple background (Matches design)
				GradientPaint gp = new GradientPaint(0, 0, new Color(30, 10, 50), 0, getHeight(),
						new Color(20, 5, 30));
				g2d.setPaint(gp);
				g2d.fillRect(0, 0, getWidth(), getHeight());
			}
		};

		// 1. Header Navigation
		JPanel header = new JPanel(new BorderLayout());
		header.setOpaque(false);
		header.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

		JLabel logo = new JLabel("💣 MineSweeper");
		logo.setFont(new Font("Segoe UI", Font.BOLD, 26));
		logo.setForeground(Color.WHITE);
		header.add(logo, BorderLayout.WEST);

		JPanel nav = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
		nav.setOpaque(false);
		nav.add(createNavButton("🏠 Home", true));

		JButton manageBtn = createNavButton("📝 Questions", false);
		manageBtn.addActionListener(e -> showQuestionView());
		nav.add(manageBtn);

		JButton statsBtn = createNavButton("⏱ History", false);
		statsBtn.addActionListener(e -> showHistoryView());
		nav.add(statsBtn);

		JButton rulesBtn = createNavButton("📜 Rules", false);
		rulesBtn.addActionListener(e -> showGameRules());
		nav.add(rulesBtn);

		header.add(nav, BorderLayout.EAST);
		startPanel.add(header, BorderLayout.NORTH);

		// 2. Main Layout
		JPanel mainContent = new JPanel();
		mainContent.setLayout(new BoxLayout(mainContent, BoxLayout.Y_AXIS));
		mainContent.setOpaque(false);
		mainContent.setBorder(BorderFactory.createEmptyBorder(10, 20, 40, 20));

		mainContent.add(Box.createVerticalGlue());

		// Hero Title
		JLabel title = new JLabel("Welcome to MineSweeper");
		title.setFont(new Font("Segoe UI", Font.BOLD, 32));
		title.setForeground(Color.WHITE);
		title.setAlignmentX(Component.CENTER_ALIGNMENT);
		mainContent.add(title);

		JLabel subtitle = new JLabel("An exciting strategy game for two players with questions and surprises");
		subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		subtitle.setForeground(new Color(200, 200, 220));
		subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
		mainContent.add(subtitle);

		mainContent.add(Box.createVerticalStrut(15));

		// Two-Column Grid
		JPanel gridPanel = new JPanel(new GridLayout(1, 2, 40, 0));
		gridPanel.setOpaque(false);
		gridPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

		// --- LEFT COLUMN: Start Game Form ---
		JPanel formCard = createCardPanel();
		formCard.setLayout(new GridBagLayout());
		formCard.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(10, 0, 5, 0);
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 1.0;

		JLabel formTitle = new JLabel("▷ Start New Game");
		formTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
		formTitle.setForeground(Color.WHITE);
		formCard.add(formTitle, gbc);

		gbc.gridy++;
		gbc.insets = new Insets(10, 0, 5, 0);
		formCard.add(createLabel("Player 1"), gbc);
		gbc.gridy++;
		gbc.insets = new Insets(0, 0, 10, 0);
		player1NameField = createStyledTextField("Enter player 1 name");
		formCard.add(player1NameField, gbc);

		gbc.gridy++;
		gbc.insets = new Insets(10, 0, 5, 0);
		formCard.add(createLabel("Player 2"), gbc);
		gbc.gridy++;
		gbc.insets = new Insets(0, 0, 10, 0);
		player2NameField = createStyledTextField("Enter player 2 name");
		formCard.add(player2NameField, gbc);

		// Difficulty Selector
		gbc.gridy++;
		gbc.insets = new Insets(0, 0, 15, 0); // Spacing
		JPanel diffToggle = new JPanel(new GridLayout(1, 3, 10, 0));
		diffToggle.setOpaque(false);

		// Hidden combo for compatibility
		difficultyCombo = new JComboBox<>(new String[] { "Easy (9x9)", "Medium (13x13)", "Hard (16x16)" });
		difficultyCombo.setVisible(false);
		startPanel.add(difficultyCombo);

		JButton btnEasy = createDiffButton("Easy", true);
		JButton btnMed = createDiffButton("Medium", false);
		JButton btnHard = createDiffButton("Hard", false);

		diffToggle.add(btnEasy);
		diffToggle.add(btnMed);
		diffToggle.add(btnHard);
		formCard.add(diffToggle, gbc);

		// Dynamic Info Panel
		gbc.gridy++;
		RoundedPanel diffInfoPanel = new RoundedPanel(20, new Color(40, 180, 100));
		diffInfoPanel.setLayout(new BoxLayout(diffInfoPanel, BoxLayout.Y_AXIS));
		diffInfoPanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

		// Labels for info panel
		JLabel dTitle = new JLabel("Easy");
		dTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
		dTitle.setForeground(Color.WHITE);
		dTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

		JLabel dBoard = new JLabel("✏ Board size: 9x9");
		dBoard.setFont(new Font("Segoe UI", Font.PLAIN, 16));
		dBoard.setForeground(Color.WHITE);
		dBoard.setAlignmentX(Component.LEFT_ALIGNMENT);

		JLabel dMines = new JLabel("💣 Mines: 10 per board");
		dMines.setFont(new Font("Segoe UI", Font.PLAIN, 16));
		dMines.setForeground(Color.WHITE);
		dMines.setAlignmentX(Component.LEFT_ALIGNMENT);

		JLabel dLives = new JLabel("♥ Starting lives: 10");
		dLives.setFont(new Font("Segoe UI", Font.PLAIN, 16));
		dLives.setForeground(Color.WHITE);
		dLives.setAlignmentX(Component.LEFT_ALIGNMENT);

		JLabel dContent = new JLabel("📦 Content: 6 Questions, 2 Surprises");
		dContent.setFont(new Font("Segoe UI", Font.PLAIN, 16));
		dContent.setForeground(Color.WHITE);
		dContent.setAlignmentX(Component.LEFT_ALIGNMENT);

		JLabel dRules = new JLabel("⚖ Cost: 5pts | Bonus: ±8");
		dRules.setFont(new Font("Segoe UI", Font.PLAIN, 16));
		dRules.setForeground(Color.WHITE);
		dRules.setAlignmentX(Component.LEFT_ALIGNMENT);

		JLabel dTag = new JLabel("Perfect for beginners");
		dTag.setFont(new Font("Segoe UI", Font.ITALIC, 14));
		dTag.setForeground(new Color(230, 230, 230));
		dTag.setAlignmentX(Component.LEFT_ALIGNMENT);

		diffInfoPanel.add(dTitle);
		diffInfoPanel.add(Box.createVerticalStrut(5));
		diffInfoPanel.add(dBoard);
		diffInfoPanel.add(Box.createVerticalStrut(5));
		diffInfoPanel.add(dMines);
		diffInfoPanel.add(Box.createVerticalStrut(5));
		diffInfoPanel.add(dLives);
		diffInfoPanel.add(Box.createVerticalStrut(5));
		diffInfoPanel.add(dContent);
		diffInfoPanel.add(Box.createVerticalStrut(5));
		diffInfoPanel.add(dRules);
		diffInfoPanel.add(Box.createVerticalStrut(5));
		diffInfoPanel.add(dTag);

		formCard.add(diffInfoPanel, gbc);

		// Update Logic
		Runnable updateDiffDiff = () -> {
			if (btnEasy.getBackground().equals(Color.WHITE)) {
				difficultyCombo.setSelectedIndex(0);
				diffInfoPanel.setGradient(new Color(46, 204, 113), new Color(39, 174, 96)); // Green
				dTitle.setText("Easy");
				dBoard.setText("✏ Board: 9x9");
				dMines.setText("💣 Mines: 10");
				dLives.setText("♥ Lives: 10");
				dContent.setText("📦 6 Questions, 2 Surprises");
				dRules.setText("⚖ Cost: 5pts | Effect: ±8");
				dTag.setText("Perfect for beginners");
			} else if (btnMed.getBackground().equals(Color.WHITE)) {
				difficultyCombo.setSelectedIndex(1);
				diffInfoPanel.setGradient(new Color(255, 167, 38), new Color(251, 140, 0)); // Orange
				dTitle.setText("Medium");
				dBoard.setText("✏ Board: 13x13");
				dMines.setText("💣 Mines: 26");
				dLives.setText("♥ Lives: 8");
				dContent.setText("📦 7 Questions, 3 Surprises");
				dRules.setText("⚖ Cost: 8pts | Effect: ±12");
				dTag.setText("An exciting challenge");
			} else {
				difficultyCombo.setSelectedIndex(2);
				diffInfoPanel.setGradient(new Color(239, 83, 80), new Color(229, 57, 53)); // Red/Pink
				dTitle.setText("Hard");
				dBoard.setText("✏ Board: 16x16");
				dMines.setText("💣 Mines: 44");
				dLives.setText("♥ Lives: 6");
				dContent.setText("📦 11 Questions, 4 Surprises");
				dRules.setText("⚖ Cost: 12pts | Effect: ±16");
				dTag.setText("Only for the brave");
			}
			diffInfoPanel.repaint();
		};

		ActionListener diffAction = e -> {
			JButton source = (JButton) e.getSource();
			normalizeDiffButton(btnEasy);
			normalizeDiffButton(btnMed);
			normalizeDiffButton(btnHard);
			source.setBackground(Color.WHITE);
			source.setForeground(Color.BLACK);
			updateDiffDiff.run();
		};

		btnEasy.addActionListener(diffAction);
		btnMed.addActionListener(diffAction);
		btnHard.addActionListener(diffAction);

		// Initial State
		updateDiffDiff.run();

		// Actions
		gbc.gridy++;
		gbc.insets = new Insets(15, 0, 10, 0);
		startButton = createGradientButton("▷ Start Game", new Color(170, 40, 180), new Color(200, 60, 210));
		startButton.setFont(new Font("Segoe UI", Font.BOLD, 18));
		startButton.setPreferredSize(new Dimension(200, 65));
		formCard.add(startButton, gbc);

		gbc.gridy++;
		gbc.insets = new Insets(0, 0, 0, 0);
		demoButton = createGradientButton("🤖 Play with AI", new Color(60, 60, 80), new Color(80, 80, 100));
		demoButton.setPreferredSize(new Dimension(200, 65));
		formCard.add(demoButton, gbc);

		gridPanel.add(formCard);

		// --- RIGHT COLUMN: Info Cards ---
		JPanel rightCol = new JPanel();
		rightCol.setLayout(new BoxLayout(rightCol, BoxLayout.Y_AXIS));
		rightCol.setOpaque(false);

		// 1. How to Play Card
		JPanel helpCard = createCardPanel();
		helpCard.setLayout(new BorderLayout());
		helpCard.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

		JLabel helpTitle = new JLabel("◎ How to Play?");
		helpTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
		helpTitle.setForeground(Color.WHITE);
		helpCard.add(helpTitle, BorderLayout.NORTH);

		JLabel helpText = new JLabel(
				"<html><body style='width: 300px; color: #E0E0E0; font-family: Segoe UI; font-size: 13px; margin-top: 10px;'>"
						+
						"<div style='margin-bottom: 8px;'><b>🎯 Goal:</b> Reveal all cells without hitting mines</div>"
						+
						"<div style='margin-bottom: 8px;'><b>👥 Players:</b> Each player has a board, take turns</div>"
						+
						"<div style='margin-bottom: 8px;'><b>❤ Lives:</b> Shared pool of lives</div>" +
						"<div style='margin-bottom: 8px;'><b>⭐ Score:</b> Earn points for safe moves</div>" +
						"<div style='margin-bottom: 8px;'><b>❓ Questions:</b> Answer correctly for bonuses</div>" +
						"<div><b>🎁 Surprises:</b> Special rewards or penalties</div>" +
						"</body></html>");
		helpCard.add(helpText, BorderLayout.CENTER);

		rightCol.add(helpCard);
		rightCol.add(Box.createVerticalStrut(20));

		// 2. Cell Types Card
		JPanel legendCard = createCardPanel();
		legendCard.setLayout(new BorderLayout());
		legendCard.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

		JLabel legendTitle = new JLabel("⚡ Cell Types");
		legendTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
		legendTitle.setForeground(Color.WHITE);
		legendCard.add(legendTitle, BorderLayout.NORTH);

		JPanel legendList = new JPanel(new GridLayout(5, 1, 0, 8));
		legendList.setOpaque(false);
		legendList.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));

		legendList.add(createLegendItem(" Mine", "Loses lives", new Color(160, 40, 40)));
		legendList.add(createLegendItem("1,2,3... Number", "Nearby count", new Color(40, 70, 120)));
		legendList.add(createLegendItem(" Empty", "No nearby mines", new Color(60, 70, 80)));
		legendList.add(createLegendItem("❓ Question", "Bonus query", new Color(80, 40, 110)));
		legendList.add(createLegendItem("🎁 Surprise", "Mystery effect", new Color(100, 70, 30)));

		legendCard.add(legendList, BorderLayout.CENTER);

		rightCol.add(legendCard);

		// Fill remaining space
		rightCol.add(Box.createVerticalGlue());

		gridPanel.add(rightCol);

		mainContent.add(gridPanel);

		// Responsiveness listener
		startPanel.addComponentListener(new java.awt.event.ComponentAdapter() {
			@Override
			public void componentResized(java.awt.event.ComponentEvent e) {
				if (startPanel.getWidth() < 950) {
					gridPanel.setLayout(new GridLayout(2, 1, 0, 30));
				} else {
					gridPanel.setLayout(new GridLayout(1, 2, 40, 0));
				}
				gridPanel.revalidate();
			}
		});

		mainContent.add(Box.createVerticalGlue());

		JScrollPane scrollFrame = new JScrollPane(mainContent);
		scrollFrame.setBorder(null);
		scrollFrame.setOpaque(false);
		scrollFrame.getViewport().setOpaque(false);
		scrollFrame.getVerticalScrollBar().setUnitIncrement(16);

		startPanel.add(scrollFrame, BorderLayout.CENTER);
	}

	public void showHistoryView() {
		CardLayout cl = (CardLayout) mainPanel.getLayout();
		cl.show(mainPanel, "HISTORY");
		if (historyView != null)
			historyView.refresh();
	}

	// --- Helper Methods ---
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

	private JPanel createCardPanel() {
		return new RoundedPanel(25, new Color(35, 25, 55));
	}

	private JLabel createLabel(String text) {
		JLabel lbl = new JLabel(text);
		lbl.setForeground(new Color(200, 200, 200));
		lbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		return lbl;
	}

	private JTextField createStyledTextField(String placeholder) {
		JTextField field = new JTextField();
		field.setFont(new Font("Segoe UI", Font.PLAIN, 16));
		field.setForeground(Color.WHITE);
		field.setBackground(new Color(60, 50, 80));
		field.setCaretColor(Color.WHITE);
		field.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(new Color(80, 70, 100)),
				BorderFactory.createEmptyBorder(10, 10, 10, 10)));
		return field;
	}

	private JButton createDiffButton(String text, boolean selected) {
		JButton btn = new JButton(text);
		btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
		btn.setFocusPainted(false);
		btn.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
		btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
		if (selected) {
			btn.setBackground(Color.WHITE);
			btn.setForeground(Color.BLACK);
		} else {
			normalizeDiffButton(btn);
		}
		return btn;
	}

	private void normalizeDiffButton(JButton btn) {
		btn.setBackground(new Color(30, 20, 50));
		btn.setForeground(Color.GRAY);
		btn.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 80)));
	}

	private JPanel createLegendItem(String title, String desc, Color bg) {
		JPanel p = new RoundedPanel(10, bg);
		p.setLayout(new BorderLayout());
		p.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		JLabel t = new JLabel(title);
		t.setForeground(Color.WHITE);
		t.setFont(new Font("Segoe UI", Font.BOLD, 14));
		JLabel d = new JLabel(desc);
		d.setForeground(new Color(200, 200, 200));
		d.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		p.add(t, BorderLayout.WEST);
		p.add(d, BorderLayout.EAST);
		return p;
	}

	class RoundedPanel extends JPanel {
		private int radius;
		private Color bgColor;
		private Color bgStart, bgEnd;

		public RoundedPanel(int radius, Color bgColor) {
			this.radius = radius;
			this.bgColor = bgColor;
			setOpaque(false);
		}

		public void setGradient(Color start, Color end) {
			this.bgStart = start;
			this.bgEnd = end;
			this.bgColor = null; // Prioritize gradient
		}

		public void setBackground(Color bg) {
			this.bgColor = bg;
			this.bgStart = null;
			this.bgEnd = null;
			super.setBackground(bg);
		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2 = (Graphics2D) g;
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			if (bgStart != null && bgEnd != null) {
				GradientPaint gp = new GradientPaint(0, 0, bgStart, getWidth(), getHeight(), bgEnd);
				g2.setPaint(gp);
			} else {
				g2.setColor(bgColor != null ? bgColor : getBackground());
			}
			g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
		}
	}

	/**
	 * Creates a modern gradient button.
	 */
	private JButton createGradientButton(String text, Color color1, Color color2) {
		JButton button = new JButton(text) {
			@Override
			protected void paintComponent(Graphics g) {
				Graphics2D g2d = (Graphics2D) g;
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

				Color startColor = getModel().isPressed() ? color1.darker() : getModel().isRollover() ? color2 : color1;
				Color endColor = getModel().isPressed() ? color2.darker()
						: getModel().isRollover() ? color2.brighter() : color2;

				GradientPaint gp = new GradientPaint(0, 0, startColor, 0, getHeight(), endColor);
				g2d.setPaint(gp);
				g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);

				super.paintComponent(g);
			}
		};

		button.setFont(new Font("Arial", Font.BOLD, 16));
		button.setForeground(Color.WHITE);
		button.setFocusPainted(false);
		button.setBorderPainted(false);
		button.setContentAreaFilled(false);
		button.setOpaque(false);
		button.setPreferredSize(new Dimension(350, 45));
		button.setCursor(new Cursor(Cursor.HAND_CURSOR));

		return button;
	}

	/**
	 * Creates the main game panel with boards and status with modern gradient
	 * theme.
	 */
	private void createGamePanel() {
		// Custom panel with gradient background (Consistent Theme)
		gamePanel = new JPanel(new BorderLayout(0, 0)) {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				Graphics2D g2d = (Graphics2D) g;
				g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
				GradientPaint gp = new GradientPaint(0, 0, BACKGROUND_DARK, 0, getHeight(), BACKGROUND_MEDIUM);
				g2d.setPaint(gp);
				g2d.fillRect(0, 0, getWidth(), getHeight());
			}
		};

		// --- 1. HEADER (Top Bar) ---
		JPanel header = new JPanel(new BorderLayout());
		header.setOpaque(false);
		header.setBorder(BorderFactory.createEmptyBorder(15, 30, 15, 30));

		// Logo / Title
		JPanel branding = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
		branding.setOpaque(false);
		JLabel logoIcon = new JLabel("💣"); // Placeholder icon
		logoIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
		logoIcon.setForeground(new Color(255, 100, 150));

		JPanel titles = new JPanel(new GridLayout(2, 1));
		titles.setOpaque(false);
		JLabel appTitle = new JLabel("MineSweeper");
		appTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
		appTitle.setForeground(Color.WHITE);
		JLabel appSubtitle = new JLabel("Two Player Game");
		appSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		appSubtitle.setForeground(new Color(180, 180, 200));
		titles.add(appTitle);
		titles.add(appSubtitle);

		branding.add(logoIcon);
		branding.add(titles);
		header.add(branding, BorderLayout.WEST);

		// Navigation
		JPanel nav = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
		nav.setOpaque(false);

		JButton homeBtn = createNavButton("🏠 Home", false);
		homeBtn.setPreferredSize(new Dimension(100, 35)); // Bigger buttons
		homeBtn.addActionListener(e -> showStartScreen());

		JButton qBtn = createNavButton("📝 Questions", false);
		qBtn.setPreferredSize(new Dimension(130, 35));
		qBtn.addActionListener(e -> showQuestionView());

		JButton hBtn = createNavButton("⏱ History", false);
		hBtn.setPreferredSize(new Dimension(110, 35));
		hBtn.addActionListener(e -> showHistoryView());

		nav.add(homeBtn);
		nav.add(qBtn);
		nav.add(hBtn);
		header.add(nav, BorderLayout.EAST);

		// --- 2. STATUS BAR (Score, Lives, Players) ---
		// Container for Status + Messages
		JPanel topContainer = new JPanel();
		topContainer.setLayout(new BoxLayout(topContainer, BoxLayout.Y_AXIS));
		topContainer.setOpaque(false);
		topContainer.setBorder(BorderFactory.createEmptyBorder(0, 30, 10, 30));
		topContainer.add(header);

		// Rounded Status Bar
		JPanel statusBar = new JPanel(new BorderLayout()) {
			@Override
			protected void paintComponent(Graphics g) {
				Graphics2D g2d = (Graphics2D) g;
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2d.setColor(new Color(45, 35, 75, 200)); // Semi-transparent dark
				g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
				super.paintComponent(g);
			}
		};
		statusBar.setOpaque(false);
		statusBar.setBorder(BorderFactory.createEmptyBorder(15, 30, 15, 30));
		statusBar.setMaximumSize(new Dimension(1200, 100));

		// Status: Left (Player 1)
		JPanel p1Status = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
		p1Status.setOpaque(false);
		JLabel p1Icon = new JLabel("❶");
		p1Icon.setFont(new Font("Segoe UI", Font.BOLD, 32));
		p1Icon.setForeground(new Color(0, 180, 255)); // Blue

		player1ScoreLabel = createStatusLabel("Player 1");
		player1ScoreLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));

		p1Status.add(p1Icon);
		p1Status.add(player1ScoreLabel);

		// Status: Center (Score & Lives)
		JPanel centerStatus = new JPanel(new GridLayout(2, 1, 0, 5));
		centerStatus.setOpaque(false);

		// Team Score
		JPanel scoreRow = new JPanel(new FlowLayout(FlowLayout.CENTER));
		scoreRow.setOpaque(false);
		JLabel scoreIcon = new JLabel("🏆 Score");
		scoreIcon.setForeground(new Color(255, 215, 0));
		scoreIcon.setFont(new Font("Segoe UI", Font.BOLD, 14));
		totalScoreLabel = new JLabel("0");
		totalScoreLabel.setForeground(Color.WHITE);
		totalScoreLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
		scoreRow.add(scoreIcon);
		scoreRow.add(Box.createHorizontalStrut(10));
		scoreRow.add(totalScoreLabel);

		// Lives
		JPanel livesRow = new JPanel(new FlowLayout(FlowLayout.CENTER));
		livesRow.setOpaque(false);
		JLabel heartIcon = new JLabel("❤ Lives");
		heartIcon.setForeground(new Color(255, 80, 80));
		heartIcon.setFont(new Font("Segoe UI", Font.BOLD, 14));
		livesLabel = new JLabel("❤❤❤❤❤❤❤❤❤❤"); // Visual hearts
		livesLabel.setForeground(new Color(255, 80, 80));
		livesLabel.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 18));
		livesRow.add(heartIcon);
		livesRow.add(Box.createHorizontalStrut(10));
		livesRow.add(livesLabel);

		centerStatus.add(scoreRow);
		centerStatus.add(livesRow);

		// Status: Right (Player 2)
		JPanel p2Status = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
		p2Status.setOpaque(false);

		player2ScoreLabel = createStatusLabel("Player 2");
		player2ScoreLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));

		JLabel p2Icon = new JLabel("❷");
		p2Icon.setFont(new Font("Segoe UI", Font.BOLD, 32));
		p2Icon.setForeground(new Color(255, 100, 150)); // Pink/Red

		p2Status.add(player2ScoreLabel);
		p2Status.add(p2Icon);

		statusBar.add(p1Status, BorderLayout.WEST);
		statusBar.add(centerStatus, BorderLayout.CENTER);
		statusBar.add(p2Status, BorderLayout.EAST);

		topContainer.add(statusBar);
		topContainer.add(Box.createVerticalStrut(15));

		// Message Label (Now at TOP)
		messageLabel = createStatusLabel("Ready to start!");
		messageLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
		messageLabel.setForeground(new Color(100, 255, 150));
		messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		topContainer.add(messageLabel);

		gamePanel.add(topContainer, BorderLayout.NORTH);

		// --- 3. BOARDS (Center) ---
		JPanel boardsWrapper = new JPanel(new GridBagLayout()); // Centers content
		boardsWrapper.setOpaque(false);

		JPanel boardsContainer = new JPanel(new GridLayout(1, 2, 30, 0));
		boardsContainer.setOpaque(false);

		// Player 1 Board
		JPanel board1Container = new JPanel(new BorderLayout());
		board1Container.setOpaque(false);

		// Header for Board 1
		JPanel b1Head = createBoardHeader("Player 1", "Board 1", new Color(0, 180, 255));
		board1Container.add(b1Head, BorderLayout.NORTH);

		// Board Panel
		board1Panel = new JPanel();
		board1Panel.setOpaque(false);
		board1Container.add(board1Panel, BorderLayout.CENTER);

		// Player 2 Board
		JPanel board2Container = new JPanel(new BorderLayout());
		board2Container.setOpaque(false);

		// Header for Board 2
		JPanel b2Head = createBoardHeader("Player 2", "Board 2", new Color(255, 100, 150));
		board2Container.add(b2Head, BorderLayout.NORTH);

		// Board Panel
		board2Panel = new JPanel();
		board2Panel.setOpaque(false);
		board2Container.add(board2Panel, BorderLayout.CENTER);

		boardsContainer.add(board1Container);
		boardsContainer.add(board2Container);

		boardsWrapper.add(boardsContainer); // Add to GridBag to center
		gamePanel.add(boardsWrapper, BorderLayout.CENTER);

		// Footer Buttons (Rules/Hint reused or new?)
		// User asked for "Rules" and "Hint" to be readable and big.
		// Use bottom panel for Actions
		JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
		bottomPanel.setOpaque(false);
		bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));

		JButton rulesBtn = createGradientButton("📜 Game Rules", new Color(70, 70, 100), new Color(90, 90, 140));
		rulesBtn.setPreferredSize(new Dimension(160, 45));
		rulesBtn.addActionListener(e -> showGameRules());

		JButton hintBtn = createGradientButton("🤖 AI Hint", new Color(255, 170, 0), new Color(255, 200, 50));
		hintBtn.setPreferredSize(new Dimension(160, 45));
		hintBtn.addActionListener(e -> {
			if (controller != null)
				controller.requestHint();
		});

		bottomPanel.add(rulesBtn);
		bottomPanel.add(hintBtn);

		gamePanel.add(bottomPanel, BorderLayout.SOUTH);
	}

	// Helper for Board Headers
	private JPanel createBoardHeader(String pName, String sub, Color accent) {
		JPanel p = new JPanel(new BorderLayout()) {
			@Override
			protected void paintComponent(Graphics g) {
				Graphics2D g2d = (Graphics2D) g;
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2d.setColor(new Color(60, 50, 90, 200));
				g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
				super.paintComponent(g);
			}
		};
		p.setOpaque(false);
		p.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

		JPanel left = new JPanel(new GridLayout(2, 1));
		left.setOpaque(false);
		JLabel name = new JLabel(pName);
		name.setFont(new Font("Segoe UI", Font.BOLD, 16));
		name.setForeground(Color.WHITE);
		JLabel subL = new JLabel(sub);
		subL.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		subL.setForeground(new Color(200, 200, 200));
		left.add(name);
		left.add(subL);

		JPanel right = new JPanel(new GridLayout(2, 1));
		right.setOpaque(false);
		JLabel mLabel = new JLabel("Mines Left", SwingConstants.RIGHT);
		mLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
		mLabel.setForeground(new Color(200, 200, 200));
		JLabel mCount = new JLabel("?", SwingConstants.RIGHT); // Will update
		mCount.setFont(new Font("Segoe UI", Font.BOLD, 16));
		mCount.setForeground(Color.WHITE);

		right.add(mLabel);
		right.add(mCount);

		// Assign to correct field
		if (pName.contains("1")) {
			board1MinesLabel = mCount;
		} else {
			board2MinesLabel = mCount;
		}

		p.add(left, BorderLayout.WEST);
		p.add(right, BorderLayout.EAST);

		return p;
	}

	/**
	 * Creates a status label with white text.
	 */
	private JLabel createStatusLabel(String text) {
		JLabel label = new JLabel(text, SwingConstants.CENTER);
		label.setFont(new Font("Arial", Font.BOLD, 14));
		label.setForeground(Color.WHITE);
		return label;
	}

	/**
	 * Creates a large status label for important info.
	 */
	private JLabel createLargeStatusLabel(String text) {
		JLabel label = new JLabel(text, SwingConstants.CENTER);
		label.setFont(new Font("Arial", Font.BOLD, 18));
		label.setForeground(Color.WHITE);
		return label;
	}

	/**
	 * Initializes the game boards with buttons.
	 * 
	 * @param size The board size (NxN)
	 */
	public void initializeBoards(int size) {
		// Calculate button size based on board size
		int buttonSize = Math.max(35, Math.min(55, 600 / size));

		// Initialize board 1
		board1Panel.removeAll();
		board1Panel.setLayout(new GridLayout(size, size, 2, 2));
		board1Panel.setOpaque(false);
		board1Buttons = new JButton[size][size];

		for (int row = 0; row < size; row++) {
			for (int col = 0; col < size; col++) {
				JButton btn = createTileButton(buttonSize);
				board1Buttons[row][col] = btn;
				board1Panel.add(btn);
			}
		}

		// Initialize board 2
		board2Panel.removeAll();
		board2Panel.setLayout(new GridLayout(size, size, 2, 2));
		board2Panel.setOpaque(false);
		board2Buttons = new JButton[size][size];

		for (int row = 0; row < size; row++) {
			for (int col = 0; col < size; col++) {
				JButton btn = createTileButton(buttonSize);
				board2Buttons[row][col] = btn;
				board2Panel.add(btn);
			}
		}

		board1Panel.revalidate();
		board1Panel.repaint();
		board2Panel.revalidate();
		board2Panel.repaint();
	}

	/**
	 * Creates a tile button with standard styling.
	 */
	private JButton createTileButton(int size) {
		JButton btn = new JButton();
		btn.setPreferredSize(new Dimension(size, size));
		// Modern unrevealed color (dark blue-grey)
		btn.setBackground(new Color(60, 70, 90));
		btn.setFont(new Font("Segoe UI", Font.BOLD, size / 2));
		btn.setFocusPainted(false);
		// Flat style border for unrevealed
		btn.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, new Color(80, 90, 110)));
		btn.setMargin(new Insets(0, 0, 0, 0));

		// Add hover effect
		btn.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mouseEntered(java.awt.event.MouseEvent evt) {
				if (!btn.getBackground().equals(REVEALED_COLOR) && !btn.getBackground().equals(FLAG_COLOR)) {
					btn.setBackground(new Color(80, 90, 110)); // Lighter on hover
				}
			}

			@Override
			public void mouseExited(java.awt.event.MouseEvent evt) {
				if (!btn.getBackground().equals(REVEALED_COLOR) && !btn.getBackground().equals(FLAG_COLOR)) {
					// Check if we should revert to unrevealed color
					// Since we don't have tile state here, we rely on the fact that
					// updateTile manages the persistent colors.
					// A safe bet is to revert to standard unrevealed for interactive tiles.
					// However, if the tile updates while hovering, this might flicker.
					// But standard unrevealed color is safe for exit.
					btn.setBackground(new Color(60, 70, 90));
				}
			}
		});

		return btn;
	}

	/**
	 * Updates a tile's appearance based on its state.
	 * 
	 * @param boardNum Board number (1 or 2)
	 * @param row      Tile row
	 * @param col      Tile column
	 * @param tile     The tile to display
	 */
	public void updateTile(int boardNum, int row, int col, Tile tile) {
		JButton btn = boardNum == 1 ? board1Buttons[row][col] : board2Buttons[row][col];

		if (tile.isFlagged()) {
			btn.setText("🚩");
			btn.setBackground(FLAG_COLOR); // Red
			btn.setForeground(Color.WHITE);
			btn.setBorder(BorderFactory.createLineBorder(new Color(200, 50, 40), 1));
		} else if (!tile.isRevealed()) {
			btn.setText("");
			btn.setBackground(new Color(60, 70, 90)); // Unrevealed
			btn.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, new Color(80, 90, 110)));
		} else {
			// Revealed: Inset/Flat look
			btn.setBorder(BorderFactory.createLineBorder(new Color(30, 30, 50), 1));

			String tileType = tile.getType();
			switch (tileType) {
				case "MINE":
					btn.setText("💣");
					btn.setBackground(MINE_COLOR);
					btn.setForeground(Color.WHITE);
					break;
				case "QUESTION":
					QuestionTile qTile = (QuestionTile) tile;
					// Show if activated/used
					if (qTile.isActivated()) {
						btn.setText("❓✔"); // Q with checkmark for answered
						// Dynamically adjust font size to fit "Q✓" in small tiles
						int fontSize = Math.min(18, btn.getWidth() / 3);
						btn.setFont(new Font("Arial", Font.BOLD, fontSize));
						btn.setBackground(new Color(180, 180, 80)); // Dimmed gold
						btn.setForeground(Color.WHITE);
						btn.setBorder(BorderFactory.createLineBorder(new Color(140, 140, 60), 1));
					} else {
						btn.setText("❓"); // Show "Q" for unanswered
						int fontSize = Math.min(20, btn.getWidth() / 2);
						btn.setFont(new Font("Arial", Font.BOLD, fontSize));
						btn.setBackground(QUESTION_COLOR);
						btn.setForeground(new Color(20, 20, 20));
					}
					break;
				case "SURPRISE":
					SurpriseTile sTile = (SurpriseTile) tile;

					// Check if surprise has been activated
					if (sTile.isActivated()) {
						btn.setText("🎁✔"); // S with checkmark for used
						// Dynamically adjust font size
						int fontSize = Math.min(18, btn.getWidth() / 3);
						btn.setFont(new Font("Arial", Font.BOLD, fontSize));
						btn.setBackground(new Color(120, 70, 140)); // Dimmed purple (used)
						btn.setForeground(Color.WHITE);
						btn.setBorder(BorderFactory.createLineBorder(new Color(90, 50, 110), 1));
					} else {
						btn.setText("🎁"); // Show "S" for unused
						int fontSize = Math.min(20, btn.getWidth() / 2);
						btn.setFont(new Font("Arial", Font.BOLD, fontSize));
						btn.setBackground(SURPRISE_COLOR); // Bright purple (unused)
						btn.setForeground(Color.WHITE);
					}
					break;
				case "EMPTY":
					btn.setText("·");
					btn.setFont(new Font("Arial", Font.BOLD, 24));
					btn.setBackground(new Color(60, 80, 100));
					btn.setForeground(new Color(120, 140, 160));
					break;
				case "NUMBER":
					int num = tile.getAdjacentMines();
					btn.setText(String.valueOf(num));
					btn.setFont(new Font("Arial", Font.BOLD, 16));
					btn.setForeground(NUMBER_COLORS[num - 1]);
					btn.setBackground(REVEALED_COLOR);
					break;
			}
		}
	}

	/**
	 * Updates scores display. Simplified method that only takes scores as
	 * parameters.
	 */
	public void updateScores(int totalScore, int p1Score, int p2Score) {
		if (totalScoreLabel != null) {
			totalScoreLabel.setText(String.valueOf(totalScore));
		}
		player1ScoreLabel.setText(player1Name + " (" + p1Score + ")");
		player2ScoreLabel.setText(player2Name + " (" + p2Score + ")");
	}

	public void updateMineCount(int count) {
		minesLabel.setText("💣 " + count);
	}

	/**
	 * Updates lives display.
	 */
	public void updateLives(int lives, int maxLives) {
		StringBuilder heartsHTML = new StringBuilder("<html>Lives: ");
		// Active hearts (Red)
		for (int i = 0; i < lives; i++) {
			heartsHTML.append("<span style='color:#E74C3C;'>♥</span> ");
		}
		// Lost hearts (Grey)
		for (int i = 0; i < (maxLives - lives); i++) {
			heartsHTML.append("<span style='color:#808080;'>♥</span> ");
		}
		heartsHTML.append("</html>");
		livesLabel.setText(heartsHTML.toString());
	}

	/**
	 * Updates turn indicator.
	 */
	public void updateTurn(String currentPlayerName) {
		currentPlayerLabel.setText("Turn: " + currentPlayerName);
	}

	/**
	 * Updates mines count display.
	 */
	public void updateMinesCount(int board1Mines, int board2Mines) {
		if (board1MinesLabel != null)
			board1MinesLabel.setText(String.valueOf(board1Mines));
		if (board2MinesLabel != null)
			board2MinesLabel.setText(String.valueOf(board2Mines));
	}

	/**
	 * Sets the player names for display in status bar.
	 */
	public void setPlayerNames(String p1Name, String p2Name) {
		this.player1Name = p1Name;
		this.player2Name = p2Name;
	}

	/**
	 * Displays game rules in a dialog.
	 */
	private void showGameRules() {
		JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Game Rules", true);
		dialog.setSize(500, 650);
		dialog.setLocationRelativeTo(this);

		// Main Panel with Gradient Background
		JPanel mainPanel = new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				Graphics2D g2d = (Graphics2D) g;
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				GradientPaint gp = new GradientPaint(0, 0, new Color(30, 25, 45), 0, getHeight(),
						new Color(15, 10, 20));
				g2d.setPaint(gp);
				g2d.fillRect(0, 0, getWidth(), getHeight());
			}
		};
		mainPanel.setLayout(new BorderLayout());

		// Header
		JPanel header = new JPanel(new BorderLayout());
		header.setOpaque(false);
		header.setBorder(BorderFactory.createEmptyBorder(25, 30, 20, 30));

		JLabel title = new JLabel("📚 Game Rules");
		title.setFont(new Font("Segoe UI", Font.BOLD, 28));
		title.setForeground(Color.WHITE);
		header.add(title, BorderLayout.CENTER);

		mainPanel.add(header, BorderLayout.NORTH);

		// Content
		JPanel contentHost = new JPanel();
		contentHost.setLayout(new BoxLayout(contentHost, BoxLayout.Y_AXIS));
		contentHost.setOpaque(false);
		contentHost.setBorder(BorderFactory.createEmptyBorder(0, 30, 20, 30));

		String htmlContent = "<html><body style='width: 350px; font-family: Segoe UI; color: #E0E0E0; font-size: 14px; line-height: 1.5;'>"
				+ "<h3 style='color: #FF79C6; margin-bottom: 5px;'>🎯 Objective</h3>"
				+ "Reveal all safe tiles without hitting mines. Work together!"
				+ "<br><br>"

				+ "<h3 style='color: #8BE9FD; margin-bottom: 5px;'>👥 Gameplay</h3>"
				+ "<ul>"
				+ "<li><b>Turn-Based:</b> Players take turns revealing tiles.</li>"
				+ "<li><b>Shared Lives:</b> Hitting a mine hurts the team!</li>"
				+ "</ul>"

				+ "<h3 style='color: #50FA7B; margin-bottom: 5px;'>🖱️ Controls</h3>"
				+ "<ul>"
				+ "<li><b>Left Click:</b> Reveal a tile.</li>"
				+ "<li><b>Right Click:</b> Flag/Unflag a mine 🚩.</li>"
				+ "</ul>"

				+ "<h3 style='color: #BD93F9; margin-bottom: 5px;'>⚡ Tile Types</h3>"
				+ "<ul>"
				+ "<li><b>💣 Mine:</b> -1 Life</li>"
				+ "<li><b>🔢 Number:</b> Shows mines around</li>"
				+ "<li><b>🟦 Empty:</b> No mines around</li>"
				+ "<li><b>❓ Question:</b> Answer for bonus</li>"
				+ "<li><b>🎁 Surprise:</b> Good or Bad effect!</li>"
				+ "</ul>"

				+ "<h3 style='color: #F1FA8C; margin-bottom: 5px;'>⭐ Scoring (Easy/Med/Hard)</h3>"
				+ "<ul>"
				+ "<li><b>Safe Reveal:</b> +10 points</li>"
				+ "<li><b>Activation Cost:</b> -5 / 8 / 12 pts</li>"
				+ "<li><b>Surprise Effect:</b> ±8 / 12 / 16 pts</li>"
				+ "<li><b>Correct Answer:</b> Bonus points!</li>"
				+ "</ul>"
				+ "</body></html>";

		JLabel contentLabel = new JLabel(htmlContent);
		contentHost.add(contentLabel);
		contentHost.add(Box.createVerticalGlue());

		JScrollPane scroll = new JScrollPane(contentHost);
		scroll.setOpaque(false);
		scroll.getViewport().setOpaque(false);
		scroll.setBorder(null);
		scroll.getVerticalScrollBar().setUnitIncrement(16);

		mainPanel.add(scroll, BorderLayout.CENTER);

		// Footer
		JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER));
		footer.setOpaque(false);
		footer.setBorder(BorderFactory.createEmptyBorder(20, 0, 30, 0));

		JButton closeBtn = createGradientButton("Got it!", new Color(100, 50, 150), new Color(120, 70, 180));
		closeBtn.setPreferredSize(new Dimension(140, 45));
		closeBtn.addActionListener(e -> dialog.dispose());

		footer.add(closeBtn);
		mainPanel.add(footer, BorderLayout.SOUTH);

		dialog.add(mainPanel);
		dialog.setVisible(true);
	}

	/**
	 * Displays a message to the user with a consistent size.
	 */
	/**
	 * Displays a message to the user.
	 */
	public void showMessage(String message) {
		messageLabel.setText(message);
	}

	/**
	 * Shows the game over dialog and returns to start screen.
	 */
	public void showGameOver(String message, boolean won) {
		String title = won ? "Congratulations!" : "Game Over";
		int messageType = won ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.WARNING_MESSAGE;
		JOptionPane.showMessageDialog(this, message, title, messageType);

		showStartScreen();
	}

	/**
	 * Shows the start screen.
	 */
	public void showStartScreen() {
		// Reset player 2 field in case it was locked by AI mode
		if (player2NameField != null) {
			player2NameField.setEditable(true);
			player2NameField.setText("");
		}

		CardLayout cl = (CardLayout) mainPanel.getLayout();
		cl.show(mainPanel, "START");
	}

	/**
	 * Shows the game screen.
	 */
	public void showGameScreen() {
		CardLayout cl = (CardLayout) mainPanel.getLayout();
		cl.show(mainPanel, "GAME");
	}

	/**
	 * Highlights the active player's board.
	 */
	public void highlightActiveBoard(int playerNum) {
		if (playerNum == 1) {
			board1Panel.setBorder(BorderFactory.createLineBorder(Color.YELLOW, 3));
			board2Panel.setBorder(null);
		} else {
			board1Panel.setBorder(null);
			board2Panel.setBorder(BorderFactory.createLineBorder(Color.YELLOW, 3));
		}
	}

	// Getters for components
	public String getPlayer1Name() {
		return player1NameField.getText().trim();
	}

	public String getPlayer2Name() {
		return player2NameField.getText().trim();
	}

	public int getSelectedDifficulty() {
		return difficultyCombo.getSelectedIndex();
	}

	public void setStartButtonListener(ActionListener listener) {
		startButton.addActionListener(listener);
	}

	public void setBoardButtonListener(int boardNum, int row, int col, ActionListener listener) {
		if (boardNum == 1) {
			board1Buttons[row][col].addActionListener(listener);
		} else {
			board2Buttons[row][col].addActionListener(listener);
		}
	}

	public void setController(GameController controller) {
		this.controller = controller;
	}

	public JButton[][] getBoard1Buttons() {
		return board1Buttons;
	}

	public JButton[][] getBoard2Buttons() {
		return board2Buttons;
	}

	public void setDemoButtonListener(ActionListener listener) {
		demoButton.addActionListener(listener);
	}

	public void setPlayerNamesForAI() {
		String p1 = player1NameField.getText().trim();
		if (p1.isEmpty())
			p1 = "Player 1";
		player1NameField.setText(p1);

		player2NameField.setText("AI Bot");
		player2NameField.setEditable(false); // Lock it so user knows
	}

	/**
	 * Creates a pending questions sidebar panel.
	 */
	private JPanel createPendingQuestionsPanel(String title) {
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.setBackground(BOARD_CONTAINER);
		panel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(70, 60, 100), 2),
				BorderFactory.createEmptyBorder(10, 10, 10, 10)));
		panel.setPreferredSize(new Dimension(200, 0));

		// Title
		JLabel titleLabel = new JLabel(title);
		titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
		titleLabel.setForeground(new Color(200, 180, 220));
		titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		panel.add(titleLabel);
		panel.add(Box.createVerticalStrut(10));

		return panel;
	}

	/**
	 * Updates the pending questions display for both players.
	 */
	public void updatePendingQuestions(java.util.List<QuestionTile> player1Questions,
			java.util.List<QuestionTile> player2Questions) {
		updatePlayerQuestions(player1QuestionsPanel, player1Questions, "Player 1");
		updatePlayerQuestions(player2QuestionsPanel, player2Questions, "Player 2");
	}

	/**
	 * Updates a single player's pending questions panel.
	 */
	private void updatePlayerQuestions(JPanel panel, java.util.List<QuestionTile> questions, String playerName) {
		// Clear existing content except title
		panel.removeAll();

		// Re-add title
		JLabel titleLabel = new JLabel(playerName + " Questions");
		titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
		titleLabel.setForeground(Color.WHITE);
		titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		panel.add(titleLabel);
		panel.add(Box.createVerticalStrut(10));

		if (questions.isEmpty()) {
			JLabel emptyLabel = new JLabel("No pending questions");
			emptyLabel.setFont(new Font("Arial", Font.ITALIC, 12));
			emptyLabel.setForeground(new Color(150, 150, 150));
			emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
			panel.add(emptyLabel);
		} else {
			for (QuestionTile qTile : questions) {
				JButton questionBtn = new JButton("Q");
				questionBtn.setFont(new Font("Arial", Font.BOLD, 16));
				questionBtn.setBackground(QUESTION_COLOR);
				questionBtn.setForeground(new Color(20, 20, 20));
				questionBtn.setFocusPainted(false);
				questionBtn.setMaximumSize(new Dimension(180, 40));
				questionBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

				// Tooltip showing question level
				if (qTile.getQuestion() != null) {
					questionBtn.setToolTipText(qTile.getQuestion().getLevel() + " Question - Click to answer");
				}

				// Add click listener to answer the question
				questionBtn.addActionListener(e -> {
					if (controller != null) {
						controller.answerPendingQuestion(qTile);
					}
				});

				panel.add(questionBtn);
				panel.add(Box.createVerticalStrut(5));
			}
		}

		panel.revalidate();
		panel.repaint();
	}

	/**
	 * Shows the management window for questions and history.
	 */
	public void showQuestionView() {
		CardLayout cl = (CardLayout) mainPanel.getLayout();
		cl.show(mainPanel, "QUESTIONS");
		if (questionView != null)
			questionView.refresh();
	}

	/**
	 * Highlights a specific tile for a hint.
	 */
	public void highlightHint(int boardNum, int row, int col, boolean isMine) {
		JButton[][] buttons = (boardNum == 1) ? board1Buttons : board2Buttons;
		JButton btn = buttons[row][col];

		// Highlight color based on type
		Color highlightColor = isMine ? new Color(255, 100, 100) : new Color(100, 255, 100);

		btn.setBackground(highlightColor);
		btn.setBorder(BorderFactory.createLineBorder(Color.YELLOW, 3));

		// Reset color after delay
		Timer timer = new Timer(2000, e -> {
			if (controller != null) {
				controller.refreshView();
			}
		});
		timer.setRepeats(false);
		timer.start();
	}
}