package View;

import Model.*;
import Control.GameController;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
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
	private JLabel minesLabel;
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

	// Chat Component
	private ChatPanel chatPanel;
	private JPanel rightSidebarContainer; // Holds P2 Questions + Chat

	/**
	 * Creates the main game window.
	 */
	public GameView() {
		setTitle("MineSweeper - Rhino Team");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setMinimumSize(new Dimension(1100, 750));
		setExtendedState(JFrame.MAXIMIZED_BOTH);

		mainPanel = new JPanel(new CardLayout());

		// Initialize Chat Panel Early
		chatPanel = new ChatPanel();
		chatPanel.setPreferredSize(new Dimension(280, 0)); // Fixed width for chat

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

		setupGlassPane();

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

		JButton btnEasy = createDiffButton("Easy", false);
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
		JLabel dTitle = new JLabel("Select Level");
		dTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
		dTitle.setForeground(Color.WHITE);
		dTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

		JLabel dBoard = new JLabel("✏ Board size: -");
		dBoard.setFont(new Font("Segoe UI", Font.PLAIN, 16));
		dBoard.setForeground(Color.WHITE);
		dBoard.setAlignmentX(Component.LEFT_ALIGNMENT);

		JLabel dMines = new JLabel("💣 Mines: -");
		dMines.setFont(new Font("Segoe UI", Font.PLAIN, 16));
		dMines.setForeground(Color.WHITE);
		dMines.setAlignmentX(Component.LEFT_ALIGNMENT);

		JLabel dLives = new JLabel("♥ Starting lives: -");
		dLives.setFont(new Font("Segoe UI", Font.PLAIN, 16));
		dLives.setForeground(Color.WHITE);
		dLives.setAlignmentX(Component.LEFT_ALIGNMENT);

		JLabel dContent = new JLabel("📦 Content: -");
		dContent.setFont(new Font("Segoe UI", Font.PLAIN, 16));
		dContent.setForeground(Color.WHITE);
		dContent.setAlignmentX(Component.LEFT_ALIGNMENT);

		JLabel dRules = new JLabel("⚖ Cost: -");
		dRules.setFont(new Font("Segoe UI", Font.PLAIN, 16));
		dRules.setForeground(Color.WHITE);
		dRules.setAlignmentX(Component.LEFT_ALIGNMENT);

		JLabel dTag = new JLabel("Please select a difficulty to view details");
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
			} else if (btnHard.getBackground().equals(Color.WHITE)) {
				difficultyCombo.setSelectedIndex(2);
				diffInfoPanel.setGradient(new Color(239, 83, 80), new Color(229, 57, 53)); // Red/Pink
				dTitle.setText("Hard");
				dBoard.setText("✏ Board: 16x16");
				dMines.setText("💣 Mines: 44");
				dLives.setText("♥ Lives: 6");
				dContent.setText("📦 11 Questions, 4 Surprises");
				dRules.setText("⚖ Cost: 12pts | Effect: ±16");
				dTag.setText("Only for the brave");
			} else {
				// No selection
				diffInfoPanel.setGradient(new Color(70, 80, 100), new Color(90, 100, 120)); // Neutral Grey
				dTitle.setText("Select Level");
				dBoard.setText("✏ Board: -");
				dMines.setText("💣 Mines: -");
				dLives.setText("♥ Lives: -");
				dContent.setText("📦 Content: -");
				dRules.setText("⚖ Cost: -");
				dTag.setText("Choose specific difficulty...");
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

	// --- Main Game Panel Creation ---
	/**
	 * Creates the main game panel with boards and status with modern gradient
	 * theme.
	 */
	private void createGamePanel() {
		// Custom panel with gradient background based on user preference
		gamePanel = new JPanel(new BorderLayout(0, 0)) {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				Graphics2D g2d = (Graphics2D) g;
				g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
				// Darker purple background as requested
				GradientPaint gp = new GradientPaint(0, 0, new Color(20, 15, 30), 0, getHeight(),
						new Color(30, 25, 40));
				g2d.setPaint(gp);
				g2d.fillRect(0, 0, getWidth(), getHeight());
			}
		};

		// 1. TOP BAR (New Design)
		JPanel topBar = new JPanel(new BorderLayout());
		topBar.setBackground(STATUS_BAR);
		topBar.setPreferredSize(new Dimension(0, 80)); // Taller for better spacing
		topBar.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(60, 50, 80)));

		// --- LEFT: SCORES ---
		JPanel scorePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 25, 20));
		scorePanel.setOpaque(false);

		totalScoreLabel = new JLabel("⭐ Team Score: 0");
		totalScoreLabel.setFont(new Font("Segoe UI", Font.BOLD, 22)); // Larger font
		totalScoreLabel.setForeground(new Color(255, 215, 0)); // Gold

		player1ScoreLabel = new JLabel("A: 0");
		player1ScoreLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
		player1ScoreLabel.setForeground(Color.WHITE);

		player2ScoreLabel = new JLabel("B: 0");
		player2ScoreLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
		player2ScoreLabel.setForeground(Color.WHITE);

		scorePanel.add(totalScoreLabel);
		scorePanel.add(new JLabel(" | ")).setForeground(Color.GRAY);
		scorePanel.add(player1ScoreLabel);
		scorePanel.add(new JLabel(" | ")).setForeground(Color.GRAY);
		scorePanel.add(player2ScoreLabel);

		// --- CENTER: TURN INDICATOR ---
		JPanel turnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 20));
		turnPanel.setOpaque(false);

		currentPlayerLabel = new JLabel("Turn: Player 1");
		currentPlayerLabel.setFont(new Font("Segoe UI", Font.BOLD, 28)); // Much larger
		currentPlayerLabel.setForeground(Color.WHITE);
		turnPanel.add(currentPlayerLabel);

		// --- RIGHT: STATS & TOOLS ---
		JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 20)); // Spaced out
		statsPanel.setOpaque(false);

		livesLabel = new JLabel("Lives: 5");
		livesLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
		livesLabel.setForeground(Color.WHITE);

		minesLabel = new JLabel("💣 Mines: 0");
		minesLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
		minesLabel.setForeground(Color.LIGHT_GRAY);

		rulesBtn = new JButton("Rules");
		styleButton(rulesBtn, new Color(70, 70, 90));
		rulesBtn.setPreferredSize(new Dimension(80, 35));
		rulesBtn.addActionListener(e -> showGameRules());

		JButton hintBtn = new JButton("Hint");
		styleButton(hintBtn, new Color(200, 160, 0)); // Goldish
		hintBtn.setForeground(Color.BLACK);
		hintBtn.setPreferredSize(new Dimension(80, 35));
		hintBtn.addActionListener(e -> {
			if (controller != null)
				controller.requestHint();
		});

		statsPanel.add(livesLabel);
		statsPanel.add(new JLabel(" | ")).setForeground(Color.GRAY);
		statsPanel.add(minesLabel);
		statsPanel.add(Box.createHorizontalStrut(15)); // Extra Spacer
		statsPanel.add(rulesBtn);
		statsPanel.add(hintBtn);

		topBar.add(scorePanel, BorderLayout.WEST);
		topBar.add(turnPanel, BorderLayout.CENTER);
		topBar.add(statsPanel, BorderLayout.EAST);

		gamePanel.add(topBar, BorderLayout.NORTH);

		// CENTER AREA: Title + Columns + Chat
		JPanel centerContainer = new JPanel();
		centerContainer.setLayout(new BoxLayout(centerContainer, BoxLayout.Y_AXIS));
		centerContainer.setOpaque(false);

		// 2. TITLE (5% height)
		JLabel titleLabel = new JLabel("Welcome to MineSweeper!");
		titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 30));
		titleLabel.setForeground(Color.WHITE);
		titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

		// Initialize messageLabel here to prevent NPE
		messageLabel = createStatusLabel(" "); // Start empty or with welcome
		messageLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
		messageLabel.setForeground(new Color(255, 230, 0));
		messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

		centerContainer.add(Box.createVerticalStrut(10));
		centerContainer.add(titleLabel);
		centerContainer.add(Box.createVerticalStrut(5));
		centerContainer.add(messageLabel); // Add it to UI under title
		centerContainer.add(Box.createVerticalStrut(15));

		// 3. MAIN GAME AREA (Squares + Boards)
		JPanel mainGameArea = new JPanel(new GridBagLayout());
		mainGameArea.setOpaque(false);
		// mainGameArea.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = new Insets(0, 10, 0, 10); // 10px gap between columns -> 20px total

		// Column 1: P1 Sidebar (Questions + Actions)
		gbc.gridx = 0;
		gbc.weightx = 0.16;
		gbc.weighty = 1.0;

		JPanel p1Sidebar = new JPanel(new BorderLayout());
		p1Sidebar.setOpaque(false);

		player1QuestionsPanel = createPendingQuestionsPanel("Player 1 Questions");
		p1Sidebar.add(player1QuestionsPanel, BorderLayout.CENTER);

		JPanel p1Actions = createQuickActionPanel(1);
		p1Sidebar.add(p1Actions, BorderLayout.SOUTH);

		mainGameArea.add(p1Sidebar, gbc);

		// Column 2: Board A (34%)
		gbc.gridx = 1;
		gbc.weightx = 0.34;

		JPanel board1Wrapper = new JPanel(new GridBagLayout()); // Center board in its slot
		board1Wrapper.setOpaque(false);

		JPanel board1Frame = new JPanel(new BorderLayout());
		board1Frame.setOpaque(false);
		// Use class field for dynamic update
		board1Title = new JLabel("Board A", SwingConstants.CENTER);
		board1Title.setFont(new Font("Segoe UI", Font.BOLD, 16));
		board1Title.setForeground(Color.WHITE);
		board1Frame.add(board1Title, BorderLayout.NORTH);

		board1Panel = new JPanel(); // Will be initialized later
		board1Panel.setOpaque(false);
		board1Frame.add(board1Panel, BorderLayout.CENTER);

		board1Wrapper.add(board1Frame);
		mainGameArea.add(board1Wrapper, gbc);

		// Column 3: Board B (34%)
		gbc.gridx = 2;
		gbc.weightx = 0.34;

		JPanel board2Wrapper = new JPanel(new GridBagLayout());
		board2Wrapper.setOpaque(false);

		JPanel board2Frame = new JPanel(new BorderLayout());
		board2Frame.setOpaque(false);
		// Use class field
		board2Title = new JLabel("Board B", SwingConstants.CENTER);
		board2Title.setFont(new Font("Segoe UI", Font.BOLD, 16));
		board2Title.setForeground(Color.WHITE);
		board2Frame.add(board2Title, BorderLayout.NORTH);

		board2Panel = new JPanel();
		board2Panel.setOpaque(false);
		board2Frame.add(board2Panel, BorderLayout.CENTER);

		board2Wrapper.add(board2Frame);
		mainGameArea.add(board2Wrapper, gbc);

		// Column 4: P2 Sidebar (Questions + Actions)
		gbc.gridx = 3;
		gbc.weightx = 0.16;

		JPanel p2Sidebar = new JPanel(new BorderLayout());
		p2Sidebar.setOpaque(false);

		player2QuestionsPanel = createPendingQuestionsPanel("Player 2 Questions");
		p2Sidebar.add(player2QuestionsPanel, BorderLayout.CENTER);

		JPanel p2Actions = createQuickActionPanel(2);
		p2Sidebar.add(p2Actions, BorderLayout.SOUTH);

		mainGameArea.add(p2Sidebar, gbc);

		centerContainer.add(mainGameArea);

		// Back Button Area (Directly below main area, no chat)
		JPanel bottomArea = new JPanel(new FlowLayout(FlowLayout.CENTER));
		bottomArea.setBackground(new Color(20, 15, 30));
		bottomArea.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
		JButton backBtn = createGradientButton("🏠 Back to Start", new Color(200, 60, 60), new Color(220, 80, 80));
		backBtn.setPreferredSize(new Dimension(200, 40));
		backBtn.addActionListener(e -> showStartScreen());
		bottomArea.add(backBtn);

		gamePanel.add(centerContainer, BorderLayout.CENTER);
		gamePanel.add(bottomArea, BorderLayout.SOUTH);
	}

	/**
	 * Creates a quick action panel with 3 localized chat buttons.
	 */
	private JPanel createQuickActionPanel(int playerNum) {
		JPanel panel = new JPanel(new GridLayout(3, 1, 0, 10));
		panel.setOpaque(false);
		panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

		// Button 1: Nice
		JButton btnNice = new JButton("👍 Nice!");
		styleActionButton(btnNice);
		btnNice.addActionListener(e -> showBubble(playerNum, "Nice move!"));

		// Button 2: Watch out
		JButton btnWarn = new JButton("⚠️ Watch out");
		styleActionButton(btnWarn);
		btnWarn.addActionListener(e -> showBubble(playerNum, "Watch out!"));

		// Button 3: Help
		JButton btnHelp = new JButton("🆘 Help!");
		styleActionButton(btnHelp);
		btnHelp.addActionListener(e -> showBubble(playerNum, "Help me!"));

		panel.add(btnNice);
		panel.add(btnWarn);
		panel.add(btnHelp);

		return panel;
	}

	private void styleActionButton(JButton btn) {
		btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
		btn.setBackground(new Color(60, 50, 80));
		btn.setForeground(Color.WHITE);
		btn.setFocusPainted(false);
		btn.setBorder(BorderFactory.createLineBorder(new Color(80, 70, 100), 1));
		btn.setPreferredSize(new Dimension(0, 35));
	}

	/**
	 * Updates turn indicator and chat context.
	 */
	public void updateTurn(String currentPlayerName) {
		currentPlayerLabel.setText("Turn: " + currentPlayerName);

		// Map simple turn name to specific player boolean for chat
		boolean isP1 = currentPlayerName.equalsIgnoreCase(player1Name);
		if (chatPanel != null) {
			chatPanel.setCurrentTurn(isP1);
		}
	}

	/**
	 * Sets the player names for display in status bar and chat.
	 */
	public void setPlayerNames(String p1Name, String p2Name) {
		this.player1Name = p1Name;
		this.player2Name = p2Name;

		// Update board titles as per user request
		if (board1Title != null)
			board1Title.setText(p1Name + " - Board");
		if (board2Title != null)
			board2Title.setText(p2Name + " - Board");

		if (chatPanel != null) {
			chatPanel.setPlayerNames(p1Name, p2Name);
			chatPanel.addSystemMessage("Game started! Good luck " + p1Name + " and " + p2Name + "!");
		}
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
		int gap = 2;

		// Explicit, Tuned Tile Sizes for perfect visual fit
		// Easy (9x9) -> Large tiles
		// Medium (13x13) -> Medium tiles
		// Hard (16x16) -> Compact tiles
		// Adjusted Width to be safe within the 34% column width
		int availableWidth = 480;
		int availableHeight = 600;

		int candidateWidth = (availableWidth - (size - 1) * gap) / size;
		int candidateHeight = (availableHeight - (size - 1) * gap) / size;

		int btnSize = Math.min(candidateWidth, candidateHeight);
		btnSize = Math.max(22, Math.min(65, btnSize));

		int btnWidth = btnSize;
		int btnHeight = btnSize;

		int boardPixelWidth = (btnWidth * size) + ((size - 1) * gap);
		int boardPixelHeight = (btnHeight * size) + ((size - 1) * gap);
		Dimension boardDim = new Dimension(boardPixelWidth, boardPixelHeight);

		// Initialize board 1
		board1Panel.removeAll();
		board1Panel.setLayout(new GridLayout(size, size, gap, gap));
		board1Panel.setOpaque(false);
		board1Panel.setPreferredSize(boardDim);
		board1Panel.setMinimumSize(boardDim);
		board1Panel.setMaximumSize(boardDim);

		board1Buttons = new JButton[size][size];

		for (int row = 0; row < size; row++) {
			for (int col = 0; col < size; col++) {
				JButton btn = createTileButton(btnWidth, btnHeight);
				board1Buttons[row][col] = btn;
				board1Panel.add(btn);
			}
		}

		// Initialize board 2
		board2Panel.removeAll();
		board2Panel.setLayout(new GridLayout(size, size, gap, gap));
		board2Panel.setOpaque(false);
		board2Panel.setPreferredSize(boardDim);
		board2Panel.setMinimumSize(boardDim);
		board2Panel.setMaximumSize(boardDim);

		board2Buttons = new JButton[size][size];

		for (int row = 0; row < size; row++) {
			for (int col = 0; col < size; col++) {
				JButton btn = createTileButton(btnWidth, btnHeight);
				board2Buttons[row][col] = btn;
				board2Panel.add(btn);
			}
		}

		// Force re-layout of the entire game panel to accommodate size changes
		gamePanel.revalidate();
		gamePanel.repaint();
	}

	/**
	 * Creates a tile button with standard styling.
	 */
	private JButton createTileButton(int width, int height) {
		JButton btn = new JButton();
		btn.setPreferredSize(new Dimension(width, height));
		// Modern unrevealed color (dark blue-grey)
		btn.setBackground(new Color(60, 70, 90));
		// Font size based on smallest dimension to fit
		btn.setFont(new Font("Segoe UI", Font.BOLD, (int) (Math.min(width, height) / 2.5)));
		btn.setFocusPainted(false);
		// Raised Bevel Border gives a nice "clickable button" 3D look
		btn.setBorder(BorderFactory.createRaisedBevelBorder());
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
			btn.setBorder(BorderFactory.createRaisedBevelBorder());
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
						// Dynamically adjust font size to fit. Use floating point division for
						// precision.
						int fontSize = Math.max(10, (int) (btn.getWidth() / 2.8));
						btn.setFont(new Font("Arial", Font.BOLD, fontSize));
						btn.setBackground(new Color(180, 180, 80)); // Dimmed gold
						btn.setForeground(Color.WHITE);
						btn.setBorder(BorderFactory.createLineBorder(new Color(140, 140, 60), 1));
					} else {
						btn.setText("❓"); // Show "Q" for unanswered
						int fontSize = Math.min(28, (int) (btn.getWidth() / 2.5));
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
						int fontSize = Math.max(10, (int) (btn.getWidth() / 2.8));
						btn.setFont(new Font("Arial", Font.BOLD, fontSize));
						btn.setBackground(new Color(120, 70, 140)); // Dimmed purple (used)
						btn.setForeground(Color.WHITE);
						btn.setBorder(BorderFactory.createLineBorder(new Color(90, 50, 110), 1));
					} else {
						btn.setText("🎁"); // Show "S" for unused
						int fontSize = Math.min(28, (int) (btn.getWidth() / 2.5));
						btn.setFont(new Font("Arial", Font.BOLD, fontSize));
						btn.setBackground(SURPRISE_COLOR); // Bright purple (unused)
						btn.setForeground(Color.WHITE);
					}
					break;
				case "EMPTY":
					btn.setText("·");
					// Dynamic font for empty dot
					btn.setFont(new Font("Arial", Font.BOLD, Math.max(16, (int) (btn.getHeight() / 2.0))));
					btn.setBackground(new Color(60, 80, 100));
					btn.setForeground(new Color(120, 140, 160));
					break;
				case "NUMBER":
					int num = tile.getAdjacentMines();
					btn.setText(String.valueOf(num));
					// Dynamic font for numbers
					btn.setFont(new Font("Arial", Font.BOLD, Math.max(14, (int) (btn.getHeight() / 2.2))));
					btn.setForeground(NUMBER_COLORS[num - 1]);
					btn.setBackground(REVEALED_COLOR);
					break;
			}
		}
	}

	// Board Title Labels
	private JLabel board1Title;
	private JLabel board2Title;

	// --- Visual Bonuses: Particles & Animations ---

	// Particle System Logic
	private void setupGlassPane() {
		this.setGlassPane(new ParticlePanel());
		this.getGlassPane().setVisible(true);
	}

	public void triggerExplosion(int boardNum, int row, int col) {
		JButton[][] buttons = (boardNum == 1) ? board1Buttons : board2Buttons;
		JButton btn = buttons[row][col];

		// Convert button location to GlassPane coordinates
		Point p = SwingUtilities.convertPoint(btn, 0, 0, this.getGlassPane());
		int centerX = p.x + btn.getWidth() / 2;
		int centerY = p.y + btn.getHeight() / 2;

		((ParticlePanel) this.getGlassPane()).addExplosion(centerX, centerY, MINE_COLOR);
	}

	public void showBubble(int playerNum, String text) {
		JPanel targetPanel = (playerNum == 1) ? board1Panel : board2Panel;
		if (targetPanel != null && targetPanel.isShowing()) {
			Point p = SwingUtilities.convertPoint(targetPanel, targetPanel.getWidth() / 2, targetPanel.getHeight() / 3,
					this.getGlassPane());
			((ParticlePanel) this.getGlassPane()).addBubble(p.x, p.y, text, playerNum == 1);
		}
	}

	// Inner class for drawing particles and speech bubbles
	private class ParticlePanel extends JComponent {
		private java.util.List<Particle> particles = new java.util.ArrayList<>();
		private java.util.List<SpeechBubble> bubbles = new java.util.ArrayList<>();
		private Timer animationTimer;

		public ParticlePanel() {
			setOpaque(false);
			animationTimer = new Timer(16, e -> updateVisuals());
		}

		public void addExplosion(int x, int y, Color color) {
			for (int i = 0; i < 30; i++) { // 30 particles per explosion
				particles.add(new Particle(x, y, color));
			}
			if (!animationTimer.isRunning()) {
				animationTimer.start();
			}
		}

		public void addBubble(int x, int y, String text, boolean isLeft) {
			bubbles.add(new SpeechBubble(x, y, text, isLeft));
			if (!animationTimer.isRunning()) {
				animationTimer.start();
			}
			repaint();
		}

		private void updateVisuals() {
			boolean active = false;

			// Update Particles
			if (!particles.isEmpty()) {
				active = true;
				for (int i = 0; i < particles.size(); i++) {
					Particle p = particles.get(i);
					p.update();
					if (!p.isAlive()) {
						particles.remove(i);
						i--;
					}
				}
			}

			// Update Bubbles
			if (!bubbles.isEmpty()) {
				active = true;
				for (int i = 0; i < bubbles.size(); i++) {
					SpeechBubble b = bubbles.get(i);
					b.update();
					if (!b.isAlive()) {
						bubbles.remove(i);
						i--;
					}
				}
			}

			if (!active) {
				animationTimer.stop();
			}
			repaint();
		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2 = (Graphics2D) g;
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

			for (Particle p : particles) {
				p.draw(g2);
			}

			for (SpeechBubble b : bubbles) {
				b.draw(g2);
			}
		}
	}

	private class SpeechBubble {
		private int x, y;
		private String text;
		private float alpha = 1.0f;
		private boolean isLeft; // Blue or Purple style
		private int life = 120; // 2 seconds at 60fps

		public SpeechBubble(int x, int y, String text, boolean isLeft) {
			this.x = x;
			this.y = y;
			this.text = text;
			this.isLeft = isLeft;
		}

		public void update() {
			life--;
			y -= 1; // Float up
			if (life < 20) {
				alpha = life / 20.0f;
			}
		}

		public boolean isAlive() {
			return life > 0;
		}

		public void draw(Graphics2D g2) {
			Composite originalComposite = g2.getComposite();
			g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));

			Font font = new Font("Segoe UI", Font.BOLD, 14);
			g2.setFont(font);
			FontMetrics fm = g2.getFontMetrics();
			int w = fm.stringWidth(text) + 20;
			int h = fm.getHeight() + 10;

			int drawX = x - w / 2;
			int drawY = y - h;

			Color bg = isLeft ? new Color(70, 130, 200) : new Color(147, 112, 219);
			g2.setColor(bg);
			g2.fillRoundRect(drawX, drawY, w, h, 20, 20);

			// Tail
			int[] tx = { x, x - 10, x + 10 };
			int[] ty = { y, y - h + 5, y - h + 5 };
			g2.fillPolygon(tx, ty, 3);

			g2.setColor(Color.WHITE);
			g2.drawString(text, drawX + 10, drawY + h - 10);

			g2.setComposite(originalComposite);
		}
	}

	private class Particle {
		private double x, y;
		private double vx, vy;
		private float alpha = 1.0f;
		private Color color;
		private int size;

		public Particle(int startX, int startY, Color c) {
			this.x = startX;
			this.y = startY;
			this.color = c;

			// Random velocity
			double angle = Math.random() * Math.PI * 2;
			double speed = Math.random() * 5 + 2;
			this.vx = Math.cos(angle) * speed;
			this.vy = Math.sin(angle) * speed;

			this.size = (int) (Math.random() * 8 + 4);
		}

		public void update() {
			x += vx;
			y += vy;
			vy += 0.2; // Gravity
			alpha -= 0.02f; // Fade out
		}

		public boolean isAlive() {
			return alpha > 0;
		}

		public void draw(Graphics2D g2) {
			g2.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), (int) (alpha * 255)));
			g2.fillOval((int) x, (int) y, size, size);
		}
	}

	// --- Animated Counters Logic ---

	private int displayedTotalScore = 0;
	// We track the target separately, updateScores is called with the target.

	/**
	 * Updates scores display. Simplified method that only takes scores as
	 * parameters.
	 */
	public void updateScores(int totalScore, int p1Score, int p2Score) {
		// Animate Total Score
		animateLabel(totalScoreLabel, displayedTotalScore, totalScore, "⭐ Team Score: ", "");
		displayedTotalScore = totalScore;

		player1ScoreLabel.setText("A: " + p1Score);
		player2ScoreLabel.setText("B: " + p2Score);
	}

	private void animateLabel(JLabel label, int start, int end, String prefix, String suffix) {
		if (start == end) {
			label.setText(prefix + end + suffix);
			return;
		}

		Timer timer = new Timer(20, null);
		long startTime = System.currentTimeMillis();
		int duration = 500; // 500ms animation

		timer.addActionListener(e -> {
			long now = System.currentTimeMillis();
			float fraction = (float) (now - startTime) / duration;

			if (fraction >= 1f) {
				label.setText(prefix + end + suffix);
				timer.stop();
			} else {
				// Linear interpolation
				int current = (int) (start + (end - start) * fraction);
				label.setText(prefix + current + suffix);
			}
		});
		timer.start();
	}

	public void updateMineCount(int count) {
		minesLabel.setText("💣 " + count);
	}

	/**
	 * Updates lives display.
	 */
	public void updateLives(int lives, int maxLives) {
		StringBuilder heartsHTML = new StringBuilder("<html>");
		// Active hearts (Red)
		for (int i = 0; i < lives; i++) {
			heartsHTML.append("<span style='color:#E74C3C;'>♥</span>");
		}
		// Lost hearts (Grey)
		for (int i = 0; i < (maxLives - lives); i++) {
			heartsHTML.append("<span style='color:#505050;'>♥</span>"); // Darker grey for modern look
		}
		heartsHTML.append(" Lives: ").append(lives).append("</html>");
		livesLabel.setText(heartsHTML.toString());
	}

	/**
	 * Updates mines count display.
	 */
	public void updateMinesCount(int board1Mines, int board2Mines) {
		minesLabel.setText("💣 Mines: " + (board1Mines + board2Mines) + " total"); // Simplified
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
		Border activeBorder = BorderFactory.createLineBorder(new Color(255, 215, 0), 2); // Thin Golden Border

		if (playerNum == 1) {
			board1Panel.setBorder(activeBorder);
			board2Panel.setBorder(null);
		} else {
			board1Panel.setBorder(null);
			board2Panel.setBorder(activeBorder);
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
		// Reduced width to allow more space for the boards
		panel.setPreferredSize(new Dimension(155, 0));

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

	/**
	 * Shows a standard blocking dialog.
	 */
	public void showDialog(String message, String title, int messageType) {
		JOptionPane.showMessageDialog(this, message, title, messageType);
	}

	/**
	 * Shows a dialog that automatically closes after a set time.
	 * Used for AI turns to avoid blocking the game loop indefinitely.
	 */
	public void showAutoClosingMessage(String message, String title, int messageType, int timeoutMs) {
		JOptionPane pane = new JOptionPane(message, messageType);
		JDialog dialog = pane.createDialog(this, title);
		dialog.setModal(false); // Non-modal so it doesn't block EDT completely if we want async
		// However, with invokeLater logic in bot, we might want it to block "briefly"
		// or overlay.
		// Making it non-modal ensures flow continues, but we want the user to SEE it.

		// Timer to close
		Timer timer = new Timer(timeoutMs, e -> dialog.dispose());
		timer.setRepeats(false);
		timer.start();

		dialog.setVisible(true);
	}

	private void styleButton(JButton btn, Color bgColor) {
		btn.setBackground(bgColor);
		btn.setForeground(Color.WHITE);
		btn.setFocusPainted(false);
		btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
		btn.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
	}
}