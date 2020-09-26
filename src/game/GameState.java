package game;

public class GameState {

	// static class members //
	private static String actingTeam = "cross";
	private static byte[][] gameField = new byte[3][3];
	private static boolean gameIsOver = false;
	private static byte winner = 0;
	
	// constants //
	private final static byte gridSize = 3;
	
	// Method clear the game field
	public static void clearField() {
		for(int i = 0; i < gridSize; i++)
		{
			for(int k = 0; k < gridSize; k++)
			{
				gameField[i][k] = 0;
			}
		}
	}
	
	// Method switches the team that is currently allowed to make a move
	public static void switchActingTeam() {
		if(actingTeam.equalsIgnoreCase("cross")) {
			actingTeam = "circle";
		} else if(actingTeam.equalsIgnoreCase("circle")) {
			actingTeam = "cross";
		}
	}
	
	// Return the acting team
	public static String getActingTeam() {
		return actingTeam;
	}
	
	// Method for overwritig the current game field state with a new/updated one
	public static void updateField(byte[][] newField) {
		gameField = newField;
	}
	
	// Return the current state of the game field
	public static byte[][] getCurrentFieldState() {
		return gameField;
	}
	
	// Method for marking the game to be over
	public static void checkForGameOver() {
		
		// Check if every cell of the game field grid is full
		boolean gameFieldFull = true;
		for(int i = 0; i < gridSize; i++)
		{
			for(int j = 0; j < gridSize; j++)
			{
				if(gameField[i][j] == 0) {
					gameFieldFull = false;
				}
			}
		}
		
		// Run the winner detection 
		checkSomeoneWon();
		
		// If either a player has won or the game field is full, the game is over
		gameIsOver = (winner > 0) || gameFieldFull;
	}
	
	// Return the game over status
	public static boolean isGameOver() {
		return gameIsOver;
	}
	
	// Winner detection
	private static void checkSomeoneWon() {
		
		// Obtain the current field state from the Game State class
		byte[][] gameField = GameState.getCurrentFieldState();
		
		for(byte i = 0; i < gridSize; i++) {
			boolean rowWin = true; 
			for(byte j = 0; j < gridSize-1; j++) {
				if(gameField[i][j] == 0 || gameField[i][j] != gameField[i][j+1]) {
					rowWin = false;
				}
			}
			if(rowWin) {
				winner = gameField[i][0];
				return;
			}
		}
		for(byte j = 0; j < gridSize; j++) {
			boolean columnWin = true; 
			for(byte i = 0; i < gridSize-1; i++) {
				if(gameField[i][j] == 0 || gameField[i][j] != gameField[i+1][j]) {
					columnWin = false;
				}
			}
			if(columnWin) {
				winner = gameField[0][j];
				return;
			}
		}
		boolean diagonalWinLUDR = true; 
		for(byte i = 0; i < gridSize-1; i++) {
			if(gameField[i][i] == 0 || gameField[i][i] != gameField[i+1][i+1]) {
				diagonalWinLUDR = false;
			}
		}
		if(diagonalWinLUDR) {
			winner = gameField[0][0];
			return;
		}
		boolean diagonalWinRUDL = true; 
		for(byte i = 0; i < gridSize-1; i++) {
			if(gameField[0][gridSize-1] == 0 || gameField[i][gridSize-1-i] != gameField[i+1][gridSize-2-i]) {
				diagonalWinRUDL = false;
			}
			
		}
		if(diagonalWinRUDL) {
			winner = gameField[0][gridSize-1];
			return;
		}
	}
}
