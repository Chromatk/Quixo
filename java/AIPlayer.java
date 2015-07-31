import java.util.*;

public class AIPlayer {
	Random random = new Random();

	boolean color;
	QuixoBoard board;
	int[][] outerTiles;
	String type;

	public AIPlayer(boolean playerColor, String type) {
		color = playerColor;
		generateOuterTiles();
		this.type = type;
	}

	public AIPlayer(boolean playerColor) {
		color = playerColor;
		generateOuterTiles();
		type = "random";
	}

	void generateOuterTiles() {
		outerTiles = new int[][]{
			{0,0},
			{0,1},
			{0,2},
			{0,3},
			{0,4},
			{1,0},
			{2,0},
			{3,0},
			{4,0},
			{4,1},
			{4,2},
			{4,3},
			{4,4},
			{3,4},
			{2,4},
			{1,4},
		};

	}

	public int[] move(QuixoBoard b) {
		board = b.copyOf();
		
		int[] movesOutput = new int[4];
		if(type.equals("greedy")) {
			movesOutput = getGreedyHillClimbingMove(b);
		} else {
			movesOutput = getRandomMove(b);
		}
		

		System.out.println("From: " + movesOutput[0] + ", " + movesOutput[1] + " | To: "+ movesOutput[2] + ", " + movesOutput[3]);
		return(movesOutput);
	}

	ArrayList<int[]> generateAllMoves(QuixoBoard b) {
		//System.out.println("color: " + color);
		ArrayList<int[]> allPossibleMoves = new ArrayList<int[]>();

		for(int[] i:outerTiles) {

			if(!b.isColored[i[0]][i[1]] || (b.isBlue[i[0]][i[1]]==color)) {
				for(int[] j:QuixoRules.movableTiles(i)) {
					allPossibleMoves.add(new int[]{i[0],i[1],j[0],j[1]});
				}
			}
		}

		return(allPossibleMoves);
	}

	int[] getRandomMove(QuixoBoard b) {
		ArrayList<int[]> allMoves = generateAllMoves(b);

		int m = (int) (random.nextDouble()*allMoves.size());
		int[] ans = allMoves.get(m);

		return(ans);
	}

	int[] getGreedyHillClimbingMove(QuixoBoard b) {
		ArrayList<int[]> allMoves = generateAllMoves(b);

		double maxRating = -999999.9;
		int[] bestMove = new int[4];
		for(int[] i:allMoves) {
			QuixoBoard newBoard = b.copyOf();
			QuixoRules.makeMove(i, newBoard, color);
			double r = heuristicRating(newBoard);

			if(r > maxRating) {
				maxRating = r;
				bestMove = i;
			}
		}
		System.out.println(maxRating);

		return bestMove;
	}

	double rateBoard(QuixoBoard b) {
		return 0.0;
	}

	double heuristicRating(QuixoBoard b) {
		double rating = 0.0;

		//rating = QuixoRules.countColoredTiles(b, color);
		rating += QuixoRules.countConsecutive(b, color);
		return rating;
	}

	
}