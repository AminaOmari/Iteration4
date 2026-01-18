# MineSweeper - Trivia Edition 🦏

## Overview
Welcome to **MineSweeper - Trivia Edition**, a modern, cooperative twist on the classic Minesweeper game developed by **Team Rhino**. 
In this version, two players (human-human or human-AI) work together to clear their respective boards effectively. The game introduces new mechanics like shared lives, trivia questions, and surprise events that add strategy and excitement to the classic formula.

## ✨ Key Features
*   **Cooperative Multiplayer:** Two active boards where players share a global life pool. If lives reach zero, the team loses!
*   **Trivia Integration:** dynamic **Question Tiles** (❓) that challenge your knowledge. Answer correctly to earn points and extra lives.
*   **Surprise System:** Random **Surprise Tiles** (🎁) that can grant huge bonuses or inflict penalty points.
*   **Three Difficulty Modes:**
    *   **Easy:** 9x9 Board, 10 Mines.
    *   **Medium:** 13x13 Board, 26 Mines.
    *   **Hard:** 16x16 Board, 44 Mines.
*   **AI Partner:** A built-in "Smart Bot" that can play as Player 2, helping you clear the board.
*   **Modern UI:** Features smooth animations, particle effects, and a clean, flat aesthetic.

## 🎮 How to Play

### The Goal
Clear all safe tiles on both boards OR correctly flag all mines.

### Controls
*   **Left Click:** Reveal a tile.
*   **Right Click:** Flag a tile (mark as Mine).

### Tile Types
*   **Standard Tiles:**
    *   **Empty:** Safe to click. Triggers a cascade opening of nearby empty tiles.
    *   **Number:** Indicates how many mines are adjacent to this tile.
*   **Special Tiles:**
    *   **💣 Mine:** Losing a life if revealed. Try to flag these!
    *   **❓ Question:** Click to pay points and answer a question. Correct answers award points, lives, or board reveals.
    *   **🎁 Surprise:** Click to pay points and reveal a random effect (Good or Bad).

### Scoring & Lives
*   **Shared Lives:** Both players draw from the same heart pool. 
*   **Points:** Earn points by revealing safe tiles and answering questions.
*   **End Game Bonus:** Any lives remaining above 10 at the end of the game are converted into bonus points!

## 🚀 How to Run
1.  Compile the Java source files in `src`.
2.  Run the main class:
    ```bash
    java Model.Main
    ```
    *(Make sure your classpath is correctly set if running from terminal)*

## 📂 Project Structure
*   `src/Model`: Contains the core game logic (GameState, Board, Tile hierarchy).
*   `src/View`: Handles the Graphical User Interface (Swing, Custom Painting).
*   `src/Control`: Manages game flow and user input (Controller, DemoBot).
*   `src/Data`: Stores the CSV databases for questions and surprises.

---
**Developed by Team Rhino**
