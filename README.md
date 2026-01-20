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

## Enhanced User Experience Features

### 1. Interactive Difficulty Selection
Instead of a static menu, the start screen features a dynamic **Difficulty Info Card** that updates instantly when you select a level:
*   **Visual Feedback:** Click Easy, Medium, or Hard to see the card change color and content.
*   **Comprehensive Stats:** Instantly view Board Size, Mine Count, and Starting Lives for the selected level.
*   **Mechanics Preview:** Shows the specific configuration for Questions (quantity) and Scorable Rules (cost/effect) for that difficulty.
*   **No Guesswork:** Players know exactly what they are getting into before hitting Start.

### 2. Play With AI Button & Implementation
Dedicated **'🤖 Play With AI'** button enables single-player mode against a computer team-mate:
*   Prominent button placement on start screen for easy access
*   AI opponent implemented with intelligent move strategy
*   Automatic gameplay with strategic timing (1-second delays for natural feel)
*   Full integration with game mechanics (trivia, surprises, scoring)
*   File: `Control/DemoBot.java` provides the AI logic

### 3. In-Game Assistance Buttons
Enhanced gameplay with two helpful buttons available during the game:

**Hint Button:**
*   Provides strategic hints about safe tiles or potential mine locations
*   Uses game state analysis to suggest optimal next moves
*   Helps new players learn strategy without spoiling the challenge

**Rules Button:**
*   Quick access to game rules without leaving the game
*   Explains tile types, scoring system, trivia mechanics, and win conditions
*   Professional popup dialog with formatted, easy-to-read instructions

### 4. Question Save Feature
Innovative feature allowing players to defer trivia questions for strategic gameplay:
*   **'Save for Later'** button in trivia dialog allows deferring questions
*   Saved questions displayed in a side panel showing question text and difficulty
*   Players can answer saved questions at any time by clicking them in the panel
*   Strategic depth: players can focus on board exploration and answer questions when ready
*   Visual counter shows number of saved questions (e.g., 'Saved Questions: 3')

### 5. Question Management System
A comprehensive administration interface to manage the in-game trivia database:
*   **'Questions'** button on the start screen navigation bar
*   **Add/Edit/Delete:** Full control over the question bank
*   **Editor UI:** Create questions with specific difficulties, correct answers, and multiple choice options
*   **Validation:** Prevents duplicate questions and ensures data integrity

### 6. Enhanced Game HUD
*   **Total Team Score:** Prominently displayed at the top to track combined progress
*   **Turn Indicators:** Clear visual cues for whose turn it is
*   **Side Panels:** "Pending Questions" panels for each player to manage saved trivia strategies


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
*   **Total Team Score:** Combined score of both players displayed in the HUD.
*   **End Game Bonus:** Any lives remaining above 10 at the end of the game are converted into bonus points!

## 🚀 How to Run
**Prerequisites:**
*   **Java Development Kit (JDK) 19.0.2** or higher.

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
