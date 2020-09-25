package game;

public class GameState {

	// static class members //
	private static String actingTeam = "cross";
	private static byte[][] gameField = new byte[3][3];
	private static boolean gameIsOver = false;
	
	// Method clear the game field
	public static void clearField() {
		for(int i = 0; i < 3; i++)
		{
			for(int k = 0; k < 3; k++)
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
	public static void setGameOver() {
		gameIsOver = true;
	}
	
	// Return the game over status
	public static boolean isGameOver() {
		return gameIsOver;
	}
}
