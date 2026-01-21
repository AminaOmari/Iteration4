# MineSweeper - Trivia Edition (Project Closure) 🦏

## Overview
Welcome to **MineSweeper - Trivia Edition**, the final polish of a modern, cooperative twist on the classic Minesweeper game developed by **Team Rhino**. 
In this version, two players (Human-Human or Human-AI) work together to clear their respective boards effectively. The game introduces new mechanics like shared lives, trivia questions, and surprise events that add strategy and excitement to the classic formula.

## ✨ Core Features
*   **Cooperative Multiplayer:** Two active boards where players share a global life pool. If lives reach zero, the team loses!
*   **Trivia Integration:** Dynamic **Question Tiles** (❓) that challenge your knowledge. Answer correctly to earn points and extra lives.
*   **Surprise System:** Random **Surprise Tiles** (🎁) that can grant huge bonuses or inflict penalty points (loaded from `good_surprises.csv` and `bad_surprises.csv`).
*   **Three Difficulty Modes:**
    *   **Easy:** 9x9 Board, 10 Mines.
    *   **Medium:** 13x13 Board, 26 Mines.
    *   **Hard:** 16x16 Board, 44 Mines.
*   **Standard Mechanics:** All classic Minesweeper rules (cascading reveals, flagging) apply, but with a modern "Dark Mode" aesthetic.

## 🌟 Bonus Features (Iteration 4 Highlights)
We have implemented several advanced features beyond the core requirements to enhance the user experience:

### 1. 🤖 Play With AI (Smart Bot)
A robust **AI Partner** implemented in `src/Control/DemoBot.java`.
*   Can play as Player 2 to help you clear the board.
*   Demonstrates human-like timing (1.5-second delays).
*   Correctly handles special tiles (Questions/Surprises) and game overs.

### 2. 🎨 Interactive Form & UI
The start screen is fully interactive and responsive:
*   **Dynamic Info Card:** Clicking a difficulty instantly updates a color-coded Stats Card (Green/Orange/Red) showing exact Board Size, Mine Count, and Life rules.
*   **Modern Aesthetics:** Deep purple/dark glassmorphism theme, custom rounded buttons, and vivid tile colors.

### 3. 💡 Smart In-Game Assistance
Two helper tools available during gameplay:
*   **Hint System:** Pays 2 points to analyze the board and highlight a mathematically guaranteed safe move (or warn of a trap).
*   **Rules Dialog:** A quick-access popup explaining all tile types and scoring rules without pausing the game.

### 4. 📝 Question Management System (CRUD)
A complete built-in editor for the Trivia Database:
*   **Manage Questions:** View, Edit, Add, and Delete questions directly from the "Questions" menu.
*   **Validation:** Prevents duplicate questions and ensures data integrity.
*   **Rich UI:** Custom "Card" based view for questions with difficulty badges.

### 5. 💾 "Save for Later" Strategy
Strategic depth for trivia questions:
*   Players can **defer** answering a question tile.
*   Saved questions appear in a dedicated side panel for each player.
*   Strategy: Clear the board first, then answer questions when you need lives!

### 6. 💬 Quick Chat & Visual Effects
Communication is key in co-op!
*   **Quick Actions:** Send instant messages ("Nice!", "Watch out", "Help!") with a single click.
*   **Speech Bubbles:** Messages appear as floating animated bubbles above the board.
*   **Particle Effects:** Celebratory confetti and visual feedback for interactions using a custom GlassPane overlay.
*   **Dynamic Titles:** Boards now proudly display custom player names.

## 🎮 How to Play

### The Goal
Clear all safe tiles on both boards OR correctly flag all mines.

### Controls
*   **Left Click:** Reveal a tile.
*   **Right Click:** Flag a tile (mark as Mine).
*   **Click Special Tile:** Activate a Question or Surprise (costs points!).

### Scoring & Lives
*   **Shared Lives:** Both players draw from the same heart pool. 
*   **Points:** Earn points by revealing safe tiles and answering questions.
*   **Total Team Score:** Combined score of both players displayed in the HUD.
*   **End Game Bonus:** Any lives remaining above 10 at the end of the game are converted into bonus points!

## 🚀 How to Run
**Prerequisites:**
*   **Java Development Kit (JDK) 19.0.2** or higher.

1.  **Compile the Project:**
    Open a terminal in the project root (`Rhino_iteration4`) and run:
    ```bash
    javac -d bin src/Model/*.java src/View/*.java src/Control/*.java src/Data/*.java src/Patterns/**/*.java
    ```

2.  **Run the Game:**
    ```bash
    java -cp bin Model.Main
    ```

## 📂 Project Structure
*   `src/Model`: Core game logic (GameState, Board, Tile hierarchy, Difficulty).
*   `src/View`: Swing GUI (GameView, TriviaDialog, QuestionView).
*   `src/Control`: Game flow (GameController, DemoBot).
*   `src/Data`: CSV Databases (`questions.csv`, `good_surprises.csv`, `bad_surprises.csv`).
*   `src/Patterns`: Design Patterns (Observer, Factory etc.).

---
**Developed by Team Rhino**
*Project Closure*
