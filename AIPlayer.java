public class AIPlayer { 
	boolean color;
	boolean[][] isColored;
	boolean[][] isBlue;

	public AIPlayer(boolean playerColor) {
		color = playerColor;
	}

	public int[] move(boolean[][] inColor, boolean[][] inBlue) {
		int[] movesOutput = new int[4];
		isColored=inColor;
		isBlue=inBlue;

		movesOutput[0] = 0;
		movesOutput[1] = 4;
		movesOutput[2] = 0;
		movesOutput[3] = 0;

		return(movesOutput);
	}
}