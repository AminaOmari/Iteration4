package Model;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages game history persistence using CSV storage.
 * Handles saving and loading game records.
 * 
 * @author Team Rhino
 * @version 3.0 - Iteration 3
 */
public class HistoryManager {
	private static final String HISTORY_FILE = "src/DATA/history.csv";
    private static final String CSV_HEADER = "timestamp,player1,player2,player1Score,player2Score,totalScore,difficulty,winner,remainingLives";

    /**
     * Resolves the history file location.
     *
     * In Eclipse/IDE runs, the working directory can be "bin" or the project root.
     * We therefore try a few common locations and fall back to the current working directory.
     */
    private static Path resolveHistoryPath() {
        String userDir = System.getProperty("user.dir");
        String[] candidates = {
                HISTORY_FILE,
                "src/" + HISTORY_FILE,
                "../" + HISTORY_FILE,
                userDir + File.separator + HISTORY_FILE,
                userDir + File.separator + ".." + File.separator + HISTORY_FILE
        };

        for (String c : candidates) {
            try {
                Path p = Paths.get(c).normalize();
                if (Files.exists(p)) {
                    return p;
                }
            } catch (Exception ignored) {
            }
        }

        // Default: create in the working directory
        return Paths.get(HISTORY_FILE).normalize();
    }
    
    /**
     * Saves a game history record to the CSV file.
     * 
     * @param history The game history to save
     * @return true if successful, false otherwise
     */
    public static boolean saveHistory(GameHistory history) {
        try {
            Path historyPath = resolveHistoryPath();
            File file = historyPath.toFile();
            boolean fileExists = file.exists();
            
            // Ensure parent directory exists (if any)
            Path parent = historyPath.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }

            try (FileWriter fw = new FileWriter(file, true);
                 BufferedWriter bw = new BufferedWriter(fw);
                 PrintWriter out = new PrintWriter(bw)) {
                
                // Write header if new file
                if (!fileExists) {
                    out.println(CSV_HEADER);
                }
                
                // Write history record
                out.println(history.toCsvLine());
                return true;
            }
        } catch (IOException e) {
            System.err.println("Error saving history: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Loads all game history records from the CSV file.
     * 
     * @return List of game history records
     */
    public static List<GameHistory> loadHistory() {
        List<GameHistory> histories = new ArrayList<>();
        Path historyPath = resolveHistoryPath();
        File file = historyPath.toFile();
        
        if (!file.exists()) {
            return histories; // Return empty list if file doesn't exist
        }
        
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            boolean firstLine = true;
            
            while ((line = br.readLine()) != null) {
                // Skip header
                if (firstLine) {
                    firstLine = false;
                    continue;
                }
                
                GameHistory history = GameHistory.fromCsvLine(line);
                if (history != null) {
                    histories.add(history);
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading history: " + e.getMessage());
        }
        
        return histories;
    }
    
    /**
     * Clears all game history.
     * 
     * @return true if successful, false otherwise
     */
    public static boolean clearHistory() {
        try {
            Path historyPath = resolveHistoryPath();
            File file = historyPath.toFile();
            if (file.exists()) {
                file.delete();
            }
            
            // Create new file with just header
            try (PrintWriter out = new PrintWriter(new FileWriter(file))) {
                out.println(CSV_HEADER);
            }
            return true;
        } catch (IOException e) {
            System.err.println("Error clearing history: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Gets the total number of games played.
     * 
     * @return Number of games in history
     */
    public static int getGameCount() {
        return loadHistory().size();
    }
}