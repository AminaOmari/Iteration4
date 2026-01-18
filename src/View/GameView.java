package View;

import Model.*;
import Control.GameController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform; // Added for animation

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

	/**
	 * Creates the main game window.
	 */
	public GameView() {
		setTitle("MineSweeper - Rhino Team");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setMinimumSize(new Dimension(1400, 900));

		mainPanel = new JPanel(new CardLayout());

		createStartPanel();
		createGamePanel();

		mainPanel.add(startPanel, "START");
		mainPanel.add(gamePanel, "GAME");

		add(mainPanel);
		showStartScreen();

		setLocationRelativeTo(null);
	}

	/**
	 * Creates the start/welcome screen panel with modern design.
	 */
	// Particle class for background animation
	private class Particle {
		double x, y, speed, size, angle;
		int type; // 0=mine, 1=question, 2=flag

		Particle(int width, int height) {
			reset(width, height, true);
		}

		void reset(int width, int height, boolean scatter) {
			x = scatter ? Math.random() * width : Math.random() * width;
			y = scatter ? Math.random() * height : height + 50;
			speed = 0.5 + Math.random() * 1.5;
			size = 15 + Math.random() * 20;
			angle = Math.random() * 360;
			type = (int) (Math.random() * 3);
		}

		void update(int width, int height) {
			y -= speed;
			angle += 1;
			if (y < -50)
				reset(width, height, false);
		}

		void draw(Graphics2D g2) {
			AffineTransform old = g2.getTransform();
			g2.translate(x, y);
			g2.rotate(Math.toRadians(angle));
			g2.setColor(new Color(255, 255, 255, 30)); // Transparent white
			g2.setFont(new Font("Segoe UI Emoji", Font.PLAIN, (int) size));
			String symbol = type == 0 ? "💣" : (type == 1 ? "❓" : "🚩");
			g2.drawString(symbol, 0, 0);
			g2.setTransform(old);
		}
	}

	private void createStartPanel() {
		// Custom panel with animated background
		startPanel = new JPanel(new GridBagLayout()) {
			private java.util.List<Particle> particles = new java.util.ArrayList<>();
			private Timer animTimer;

			{
				// Initialize particles
				for (int i = 0; i < 30; i++)
					particles.add(new Particle(1200, 800));

				// Start animation loop
				animTimer = new Timer(30, e -> {
					for (Particle p : particles)
						p.update(getWidth(), getHeight());
					repaint();
				});
				animTimer.start();
			}

			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				Graphics2D g2d = (Graphics2D) g;
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

				// Modern purple-blue gradient
				GradientPaint gp = new GradientPaint(0, 0, new Color(30, 20, 60), 0, getHeight(),
						new Color(50, 35, 90));
				g2d.setPaint(gp);
				g2d.fillRect(0, 0, getWidth(), getHeight());

				// Draw particles
				for (Particle p : particles)
					p.draw(g2d);
			}
		};

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(12, 15, 12, 15);
		gbc.fill = GridBagConstraints.HORIZONTAL;

		// Title with icon
		JLabel titleLabel = new JLabel("MineSweeper - Trivia Edition", SwingConstants.CENTER);
		titleLabel.setFont(new Font("Arial", Font.BOLD, 52));
		titleLabel.setForeground(Color.WHITE);
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 2;
		startPanel.add(titleLabel, gbc);

		// Subtitle with modern styling
		JLabel subtitleLabel = new JLabel("Two Player Cooperative Game", SwingConstants.CENTER);
		subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 20));
		subtitleLabel.setForeground(new Color(180, 180, 220));
		gbc.gridy = 1;
		startPanel.add(subtitleLabel, gbc);

		// Team badge
		JLabel teamLabel = new JLabel("Team Rhino 🦏", SwingConstants.CENTER);
		teamLabel.setFont(new Font("Arial", Font.BOLD, 16));
		teamLabel.setForeground(new Color(150, 150, 200));
		gbc.gridy = 2;
		startPanel.add(teamLabel, gbc);

		// Spacer
		gbc.gridy = 3;
		startPanel.add(Box.createVerticalStrut(40), gbc);

		// Player input panel with rounded style
		JPanel inputPanel = new JPanel(new GridBagLayout()) {
			@Override
			protected void paintComponent(Graphics g) {
				Graphics2D g2d = (Graphics2D) g;
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2d.setColor(new Color(45, 35, 75, 200));
				g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
			}
		};
		inputPanel.setOpaque(false);
		inputPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

		GridBagConstraints inputGbc = new GridBagConstraints();
		inputGbc.insets = new Insets(10, 10, 10, 10);
		inputGbc.fill = GridBagConstraints.HORIZONTAL;

		// Player 1 name
		inputGbc.gridwidth = 1;
		inputGbc.gridy = 0;
		inputGbc.gridx = 0;
		JLabel p1Label = createModernLabel("👤 Player 1:");
		inputPanel.add(p1Label, inputGbc);

		inputGbc.gridx = 1;
		player1NameField = createModernTextField("Enter Player 1 Name");
		inputPanel.add(player1NameField, inputGbc);

		// Player 2 name
		inputGbc.gridy = 1;
		inputGbc.gridx = 0;
		JLabel p2Label = createModernLabel("👤 Player 2:");
		inputPanel.add(p2Label, inputGbc);

		inputGbc.gridx = 1;
		player2NameField = createModernTextField("Enter Player 2 Name");
		inputPanel.add(player2NameField, inputGbc);

		// Difficulty selection
		inputGbc.gridy = 2;
		inputGbc.gridx = 0;
		JLabel diffLabel = createModernLabel("⚙️ Difficulty:");
		inputPanel.add(diffLabel, inputGbc);

		inputGbc.gridx = 1;
		difficultyCombo = new JComboBox<>(new String[] { "Easy (9x9)", "Medium (13x13)", "Hard (16x16)" });
		difficultyCombo.setFont(new Font("Arial", Font.PLAIN, 16));
		difficultyCombo.setBackground(Color.WHITE);
		difficultyCombo.setForeground(Color.BLACK);
		difficultyCombo.setPreferredSize(new Dimension(250, 50));
		inputPanel.add(difficultyCombo, inputGbc);

		gbc.gridy = 4;
		gbc.gridwidth = 2;
		gbc.gridx = 0;
		startPanel.add(inputPanel, gbc);

		// Info button for difficulty details
		gbc.gridy = 5;
		gbc.insets = new Insets(20, 15, 10, 15);
		JButton infoButton = createGradientButton("📊 Show Difficulty Details", new Color(70, 100, 180),
				new Color(90, 120, 200));
		infoButton.addActionListener(e -> showDifficultyInfo());
		startPanel.add(infoButton, gbc);

		// Management button
		gbc.gridy = 6;
		gbc.insets = new Insets(10, 15, 10, 15);
		JButton managementButton = createGradientButton("📝 Question & History Management", new Color(100, 80, 160),
				new Color(120, 100, 180));
		managementButton.addActionListener(e -> showManagementView());
		startPanel.add(managementButton, gbc);

		// Start button - prominent
		gbc.gridy = 7;
		gbc.insets = new Insets(30, 15, 20, 15);
		startButton = createGradientButton("🚀 START GAME", new Color(40, 167, 69), new Color(60, 187, 89));
		startButton.setFont(new Font("Arial", Font.BOLD, 24));
		startButton.setPreferredSize(new Dimension(300, 60));
		startPanel.add(startButton, gbc);

		// AI Demo button
		gbc.gridy = 8;
		gbc.insets = new Insets(10, 15, 20, 15);
		demoButton = createGradientButton("🤖 Play with AI", new Color(255, 140, 0), new Color(255, 160, 20));
		demoButton.setFont(new Font("Arial", Font.BOLD, 18));
		demoButton.setPreferredSize(new Dimension(300, 50));
		startPanel.add(demoButton, gbc);

		// Statistics button
		gbc.gridy = 9;
		gbc.insets = new Insets(10, 15, 20, 15);
		JButton statsButton = createGradientButton("📊 Statistics Dashboard", new Color(100, 149, 237),
				new Color(65, 105, 225));
		statsButton.addActionListener(e -> showStatisticsView());
		startPanel.add(statsButton, gbc);
	}

	private void showStatisticsView() {
		SwingUtilities.invokeLater(() -> {
			new StatisticsView().setVisible(true);
		});
	}

	/**
	 * Creates a modern styled label for the start screen.
	 */
	private JLabel createModernLabel(String text) {
		JLabel label = new JLabel(text);
		label.setFont(new Font("Arial", Font.BOLD, 18));
		label.setForeground(Color.WHITE);
		return label;
	}

	/**
	 * Creates a modern styled text field.
	 */
	private JTextField createModernTextField(String placeholderText) {
		JTextField field = new JTextField(15);
		field.setFont(new Font("Arial", Font.PLAIN, 16));
		field.setBackground(Color.WHITE);
		field.setForeground(Color.BLACK);
		field.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(new Color(150, 150, 180), 1),
				BorderFactory.createEmptyBorder(8, 12, 8, 12)));
		field.setPreferredSize(new Dimension(250, 40));

		// Add placeholder text
		field.setText("");
		field.putClientProperty("JTextField.placeholderText", placeholderText);

		// Custom painting for placeholder
		field.addFocusListener(new java.awt.event.FocusAdapter() {
			@Override
			public void focusGained(java.awt.event.FocusEvent e) {
				field.repaint();
			}

			@Override
			public void focusLost(java.awt.event.FocusEvent e) {
				field.repaint();
			}
		});

		// Override paint to show placeholder
		field.setUI(new javax.swing.plaf.basic.BasicTextFieldUI() {
			@Override
			protected void paintSafely(Graphics g) {
				super.paintSafely(g);
				JTextField textField = (JTextField) getComponent();
				if (textField.getText().isEmpty() && !textField.hasFocus()) {
					Graphics2D g2 = (Graphics2D) g.create();
					g2.setColor(new Color(150, 150, 150));
					g2.setFont(textField.getFont());
					g2.drawString(placeholderText, textField.getInsets().left,
							g2.getFontMetrics().getAscent() + textField.getInsets().top);
					g2.dispose();
				}
			}
		});

		return field;
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
	 * Shows difficulty information dialog with styled cards.
	 */
	private void showDifficultyInfo() {
		// Create custom dialog
		JDialog dialog = new JDialog(this, "Difficulty Information", true);
		dialog.setSize(700, 650);
		dialog.setLocationRelativeTo(this);

		// Main panel with gradient
		JPanel mainPanel = new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				Graphics2D g2d = (Graphics2D) g;
				GradientPaint gp = new GradientPaint(0, 0, new Color(40, 30, 70), 0, getHeight(),
						new Color(60, 45, 95));
				g2d.setPaint(gp);
				g2d.fillRect(0, 0, getWidth(), getHeight());
			}
		};
		mainPanel.setLayout(new BorderLayout(0, 20));
		mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

		// Title
		JLabel titleLabel = new JLabel("⚙️ Difficulty Levels", SwingConstants.CENTER);
		titleLabel.setFont(new Font("Arial", Font.BOLD, 32));
		titleLabel.setForeground(Color.WHITE);
		mainPanel.add(titleLabel, BorderLayout.NORTH);

		// Cards panel
		JPanel cardsPanel = new JPanel();
		cardsPanel.setLayout(new BoxLayout(cardsPanel, BoxLayout.Y_AXIS));
		cardsPanel.setOpaque(false);

		// Easy card
		cardsPanel.add(createDifficultyCard("🟢 EASY", new Color(76, 175, 80), "9 × 9 Board (81 tiles)",
				"💣 Mines: 10",
				"❓ Questions: 6", "🎁 Surprises: 2", "❤️ Lives: 10", "⭐ Question Cost: 5 pts", "🎲 Surprise: ±8 pts"));

		cardsPanel.add(Box.createVerticalStrut(15));

		// Medium card
		cardsPanel.add(createDifficultyCard("🟡 MEDIUM", new Color(255, 193, 7), "13 × 13 Board (169 tiles)",
				"💣 Mines: 26", "❓ Questions: 7", "🎁 Surprises: 3", "❤️ Lives: 8", "⭐ Question Cost: 8 pts",
				"🎲 Surprise: ±12 pts"));

		cardsPanel.add(Box.createVerticalStrut(15));

		// Hard card
		cardsPanel.add(createDifficultyCard("🔴 HARD", new Color(244, 67, 54), "16 × 16 Board (256 tiles)",
				"💣 Mines: 44", "❓ Questions: 11", "🎁 Surprises: 4", "❤️ Lives: 6", "⭐ Question Cost: 12 pts",
				"🎲 Surprise: ±16 pts"));

		JScrollPane scrollPane = new JScrollPane(cardsPanel);
		scrollPane.setOpaque(false);
		scrollPane.getViewport().setOpaque(false);
		scrollPane.setBorder(null);
		mainPanel.add(scrollPane, BorderLayout.CENTER);

		// Close button
		JButton closeBtn = new JButton("Got it!") {
			@Override
			protected void paintComponent(Graphics g) {
				Graphics2D g2d = (Graphics2D) g;
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2d.setColor(getModel().isPressed() ? new Color(70, 100, 180)
						: getModel().isRollover() ? new Color(100, 130, 210) : new Color(80, 110, 190));
				g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
				super.paintComponent(g);
			}
		};
		closeBtn.setFont(new Font("Arial", Font.BOLD, 16));
		closeBtn.setForeground(Color.WHITE);
		closeBtn.setContentAreaFilled(false);
		closeBtn.setBorderPainted(false);
		closeBtn.setFocusPainted(false);
		closeBtn.setPreferredSize(new Dimension(150, 45));
		closeBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
		closeBtn.addActionListener(e -> dialog.dispose());

		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		buttonPanel.setOpaque(false);
		buttonPanel.add(closeBtn);
		mainPanel.add(buttonPanel, BorderLayout.SOUTH);

		dialog.add(mainPanel);
		dialog.setVisible(true);
	}

	/**
	 * Creates a modern difficulty info card.
	 */
	private JPanel createDifficultyCard(String title, Color accentColor, String... details) {
		JPanel card = new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				Graphics2D g2d = (Graphics2D) g;
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

				// Card background
				g2d.setColor(new Color(55, 45, 85, 230));
				g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);

				// Accent border on left
				g2d.setColor(accentColor);
				g2d.fillRoundRect(0, 0, 6, getHeight(), 15, 15);
			}
		};
		card.setOpaque(false);
		card.setLayout(new BorderLayout(15, 10));
		card.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));
		card.setMaximumSize(new Dimension(600, 150));

		// Title with badge
		JLabel titleLabel = new JLabel(title);
		titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
		titleLabel.setForeground(Color.WHITE);
		card.add(titleLabel, BorderLayout.NORTH);

		// Details grid
		JPanel detailsPanel = new JPanel(new GridLayout(0, 2, 15, 8));
		detailsPanel.setOpaque(false);

		for (String detail : details) {
			JLabel detailLabel = new JLabel(detail);
			detailLabel.setFont(new Font("Arial", Font.PLAIN, 14));
			detailLabel.setForeground(new Color(220, 220, 240));
			detailsPanel.add(detailLabel);
		}

		card.add(detailsPanel, BorderLayout.CENTER);

		return card;
	}

	/**
	 * Creates the main game panel with boards and status with modern gradient
	 * theme.
	 */
	private void createGamePanel() {
		// Custom panel with gradient background
		gamePanel = new JPanel(new BorderLayout(10, 10)) {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				Graphics2D g2d = (Graphics2D) g;
				g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

				// Gradient from dark purple to medium purple
				GradientPaint gp = new GradientPaint(0, 0, BACKGROUND_DARK, 0, getHeight(), BACKGROUND_MEDIUM);
				g2d.setPaint(gp);
				g2d.fillRect(0, 0, getWidth(), getHeight());
			}
		};
		gamePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		// Status panel at top - modern theme
		statusPanel = new JPanel();
		statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.Y_AXIS));
		statusPanel.setBackground(STATUS_BAR);
		statusPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

		// Top row: Lives and Turn (centered) with Help button
		JPanel topRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 50, 0));
		topRow.setBackground(STATUS_BAR);
		topRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

		livesLabel = createLargeStatusLabel("Lives: 0/10");
		minesLabel = createLargeStatusLabel("💣 0"); // Mines remaining
		currentPlayerLabel = createLargeStatusLabel("⏳ Turn: -");

		// Help button with modern styling
		helpButton = new JButton("Rules") {
			@Override
			protected void paintComponent(Graphics g) {
				Graphics2D g2d = (Graphics2D) g;
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2d.setColor(getModel().isPressed() ? new Color(70, 100, 180)
						: getModel().isRollover() ? new Color(90, 120, 200) : new Color(80, 110, 190));
				g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
				super.paintComponent(g);
			}
		};
		helpButton.setFont(new Font("Arial", Font.BOLD, 14));
		helpButton.setForeground(Color.WHITE);
		helpButton.setPreferredSize(new Dimension(100, 50));
		helpButton.setToolTipText("Game Rules");
		helpButton.addActionListener(e -> showGameRules());
		helpButton.setFocusPainted(false);
		helpButton.setContentAreaFilled(false);
		helpButton.setBorderPainted(false);
		helpButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

		topRow.add(livesLabel);
		topRow.add(Box.createHorizontalStrut(20));
		// Removed minesLabel per user request
		topRow.add(Box.createHorizontalStrut(20));
		topRow.add(currentPlayerLabel);
		topRow.add(helpButton);

		// Ask AI Button
		JButton hintButton = new JButton("🤖 Hint") {
			@Override
			protected void paintComponent(Graphics g) {
				Graphics2D g2d = (Graphics2D) g;
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2d.setColor(getModel().isPressed() ? new Color(180, 140, 0)
						: getModel().isRollover() ? new Color(220, 180, 20) : new Color(200, 160, 0));
				g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
				super.paintComponent(g);
			}
		};
		hintButton.setFont(new Font("Arial", Font.BOLD, 14));
		hintButton.setForeground(Color.WHITE);
		hintButton.setPreferredSize(new Dimension(100, 50));
		hintButton.setToolTipText("Ask Smart Assistant");
		hintButton.addActionListener(e -> {
			if (controller != null)
				controller.requestHint();
		});
		hintButton.setFocusPainted(false);
		hintButton.setContentAreaFilled(false);
		hintButton.setBorderPainted(false);
		hintButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

		topRow.add(Box.createHorizontalStrut(5));
		topRow.add(hintButton);

		// Middle row: Player Scores (centered)
		JPanel middleRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 50, 0));
		middleRow.setBackground(STATUS_BAR);
		middleRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));

		player1ScoreLabel = createStatusLabel("Player 1: 0 pts");
		player2ScoreLabel = createStatusLabel("Player 2: 0 pts");

		middleRow.add(player1ScoreLabel);
		middleRow.add(player2ScoreLabel);

		// Bottom row: Message and Mines Count
		JPanel bottomRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 0));
		bottomRow.setBackground(STATUS_BAR);
		bottomRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

		messageLabel = createStatusLabel("");
		messageLabel.setFont(new Font("Arial", Font.BOLD, 13));

		minesLabel = createStatusLabel("💣 Mines: 0 | 0");
		minesLabel.setFont(new Font("Arial", Font.BOLD, 13));

		bottomRow.add(messageLabel);
		bottomRow.add(minesLabel);

		statusPanel.add(topRow);
		statusPanel.add(Box.createVerticalStrut(8));
		statusPanel.add(middleRow);
		statusPanel.add(Box.createVerticalStrut(5));
		statusPanel.add(bottomRow);

		gamePanel.add(statusPanel, BorderLayout.NORTH);

		// Main content: sidebars + boards
		JPanel mainContentPanel = new JPanel(new BorderLayout(10, 0));
		mainContentPanel.setOpaque(false);

		// LEFT SIDEBAR - Player 1 Pending Questions
		player1QuestionsPanel = createPendingQuestionsPanel("Player 1 Questions");
		mainContentPanel.add(player1QuestionsPanel, BorderLayout.WEST);

		// Boards panel in center
		JPanel boardsContainer = new JPanel(new GridLayout(1, 2, 20, 0));
		boardsContainer.setOpaque(false);

		// Player 1 board - modern container
		JPanel board1Container = new JPanel(new BorderLayout(5, 5));
		board1Container.setBackground(BOARD_CONTAINER);
		board1Container
				.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(80, 70, 110), 2),
						BorderFactory.createEmptyBorder(10, 10, 10, 10)));
		JLabel board1Title = new JLabel("Board A", SwingConstants.CENTER);
		board1Title.setFont(new Font("Arial", Font.BOLD, 16));
		board1Title.setForeground(Color.WHITE);
		board1Container.add(board1Title, BorderLayout.NORTH);
		board1Panel = new JPanel();
		board1Panel.setOpaque(false);
		board1Container.add(board1Panel, BorderLayout.CENTER);

		// Player 2 board - modern container
		JPanel board2Container = new JPanel(new BorderLayout(5, 5));
		board2Container.setBackground(BOARD_CONTAINER);
		board2Container
				.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(80, 70, 110), 2),
						BorderFactory.createEmptyBorder(10, 10, 10, 10)));
		JLabel board2Title = new JLabel("Board B", SwingConstants.CENTER);
		board2Title.setFont(new Font("Arial", Font.BOLD, 16));
		board2Title.setForeground(Color.WHITE);
		board2Container.add(board2Title, BorderLayout.NORTH);
		board2Panel = new JPanel();
		board2Panel.setOpaque(false);
		board2Container.add(board2Panel, BorderLayout.CENTER);

		boardsContainer.add(board1Container);
		boardsContainer.add(board2Container);

		mainContentPanel.add(boardsContainer, BorderLayout.CENTER);

		// RIGHT SIDEBAR - Player 2 Pending Questions
		player2QuestionsPanel = createPendingQuestionsPanel("Player 2 Questions");
		mainContentPanel.add(player2QuestionsPanel, BorderLayout.EAST);

		gamePanel.add(mainContentPanel, BorderLayout.CENTER);

		// New game button at bottom with modern styling
		JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		bottomPanel.setOpaque(false);

		JButton newGameButton = new JButton("🏠 Back to Start") {
			@Override
			protected void paintComponent(Graphics g) {
				Graphics2D g2d = (Graphics2D) g;
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

				Color color1 = new Color(220, 53, 69);
				Color color2 = new Color(240, 73, 89);

				Color startColor = getModel().isPressed() ? color1.darker() : getModel().isRollover() ? color2 : color1;
				Color endColor = getModel().isPressed() ? color2.darker()
						: getModel().isRollover() ? color2.brighter() : color2;

				GradientPaint gp = new GradientPaint(0, 0, startColor, 0, getHeight(), endColor);
				g2d.setPaint(gp);
				g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);

				super.paintComponent(g);
			}
		};
		newGameButton.setFont(new Font("Arial", Font.BOLD, 14));
		newGameButton.setForeground(Color.WHITE);
		newGameButton.setFocusPainted(false);
		newGameButton.setContentAreaFilled(false);
		newGameButton.setBorderPainted(false);
		newGameButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
		newGameButton.setPreferredSize(new Dimension(200, 40));
		newGameButton.addActionListener(e -> showStartScreen());
		bottomPanel.add(newGameButton);
		gamePanel.add(bottomPanel, BorderLayout.SOUTH);
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
		player1ScoreLabel.setText("👤 " + player1Name + ": " + p1Score + " pts");
		player2ScoreLabel
				.setText((player2Name.contains("Bot") ? "🤖 " : "👤 ") + player2Name + ": " + p2Score + " pts");
		// Don't overwrite messageLabel - it's used for game messages
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
		minesLabel.setText("💣 Mines: " + board1Mines + " | " + board2Mines);
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
		String rules = "🎮 MineSweeper - Game Rules 🎮\n\n" + "═══════════════════════════════════\n\n"
				+ "🎯 OBJECTIVE:\n" + "Reveal all non-mine tiles without hitting mines!\n"
				+ "Work together with your partner to maximize score.\n\n" + "👥 GAMEPLAY:\n"
				+ "• Two players take turns\n" + "• Each player has their own board\n"
				+ "• Both players share lives and compete for score\n\n" + "🖱️ CONTROLS:\n"
				+ "• LEFT-CLICK: Reveal a tile\n" + "  - First click: Reveals Q/S tile (+1 pt)\n"
				+ "  - Second click: Activates Q/S tile (costs points)\n" + "• RIGHT-CLICK: Place/remove flag 🚩\n\n"
				+ "🔢 TILE TYPES:\n" + "• 💣 Mine: Loses 1 life\n" + "• 1-8 Number: Shows nearby mine count\n"
				+ "• ✓ Empty: No nearby mines\n" + "• Q Question: Click twice to answer!\n"
				+ "• S Surprise: Click twice to activate!\n\n" + "⭐ SCORING:\n" + "• Reveal safe tile: +1 point\n"
				+ "• Activate Question: -5/8/12 pts (difficulty)\n" + "• Activate Surprise: -5/8/12 pts (difficulty)\n"
				+ "• Correct answer: Bonus points!\n\n" + "❤️ LIVES:\n" + "• Shared between both players\n"
				+ "• Game over when lives reach 0\n\n" + "🏆 WINNING:\n" + "Clear all non-mine tiles!\n\n"
				+ "═══════════════════════════════════\n" + "Good luck! 🍀";

		JTextArea textArea = new JTextArea(rules);
		textArea.setEditable(false);
		textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
		textArea.setBackground(new Color(245, 245, 245));
		textArea.setForeground(new Color(33, 33, 33));

		JScrollPane scrollPane = new JScrollPane(textArea);
		scrollPane.setPreferredSize(new Dimension(500, 600));

		JOptionPane.showMessageDialog(this, scrollPane, "Game Rules", JOptionPane.INFORMATION_MESSAGE);
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
	private void showManagementView() {
		ManagementView managementView = new ManagementView();
		managementView.setVisible(true);
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