import java.util.*;

public class AIPlayer {
	Random random = new Random();

	boolean color;
	boolean[][] isColored;
	boolean[][] isBlue;
	int[][] outerTiles;

	ArrayList<int[]> allPossibleMoves = new ArrayList<int[]>();

	public AIPlayer(boolean playerColor) {
		color = playerColor;
		generateOuterTiles();
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

	public int[] move(boolean[][] inColor, boolean[][] inBlue) {
		
		allPossibleMoves.clear();

		int[] movesOutput = new int[4];
		isColored=inColor;
		isBlue=inBlue;

		generateAllMoves();

		int m = (int) (random.nextDouble()*allPossibleMoves.size());
		int[] ans = allPossibleMoves.get(m);

		movesOutput[0] = ans[0];
		movesOutput[1] = ans[1];
		movesOutput[2] = ans[2];
		movesOutput[3] = ans[3];

		return(movesOutput);
	}

	void generateAllMoves() {
		System.out.println("color: " + color);
		for(int[] i:outerTiles) {

			if(!isColored[i[0]][i[1]] || (isBlue[i[0]][i[1]]==color)) {
				for(int[] j:getTargets(i)) {
					allPossibleMoves.add(new int[]{i[0],i[1],j[0],j[1]});
				}
			}
		}

		/*for(int[] i:allPossibleMoves) {
			System.out.println("From: " + i[0] + ", " + i[1] + " | To: "+ i[2] + ", " + i[3]);
		}*/
	}

	double rateBoard(boolean[][] c, boolean[][] b) {

	}

	

	boolean[][][] getNewBoard(int[] move, boolean[][] c, boolean[][] b) {
		int selX = move[0];
		int selY = move[1];
		int xTarget = move[2];
		int yTarget = move[3];

		boolean[][] colored = new boolean[c.length][c[0].length];
		boolean[][] blue = new boolean[b.length][b[0].length];

		for(int i = 0;i<colored.length;i++) {
			for(int j=0;j<colored[i].length;j++) {
				colored[i][j] = c[i][j];
				blue[i][j] = b[i][j];
			}
		}
		
		//shift ignore target
		if(selX == xTarget) {
			if(selY > yTarget) {
				for(int i=selY;i>0;i--) {
					colored[selX][i] = colored[selX][i-1];
					blue[selX][i] = blue[selX][i-1];
				}
			}
			if(selY <yTarget) {
				for(int i=selY;i<4;i++) {
					colored[selX][i] = colored[selX][i+1];
					blue[selX][i] = blue[selX][i+1];
				}
			}
		}
		if(selY == yTarget) {
			if(selX > xTarget) {
				for(int i=selX;i>0;i--) {
					colored[i][selY] = colored[i-1][selY];
					blue[i][selY] = blue[i-1][selY];
				}
			}
			if(selX < xTarget) {
				for(int i=selX;i<4;i++) {
					colored[i][selY] = colored[i+1][selY];
					blue[i][selY] = blue[i+1][selY];
				}
			}
		}

		//put target
		colored[xTarget][yTarget] = true;
		blue[xTarget][yTarget] = color;

		return(new boolean[][][]{colored, blue});
	}

	int[][] getTargets(int[] m) {
		int sX = m[0];
		int sY = m[1];
		int[][] moves = new int[0][0];

		if(sX==0) {
			//corners
			if(sY == 0 || sY ==4) { 
				moves = new int[2][];
				if(sY==0) {
					moves[0]= new int[]{0,4};
					moves[1]= new int[]{4,0};
				}
				if(sY==4) {
					moves[0]= new int[]{0,0};
					moves[1]= new int[]{4,4};
				}
				
			} else {
				moves = new int[3][];
				moves[0]= new int[]{0,0};
				moves[1]= new int[]{0,4};
				moves[2]= new int[]{4,sY};
			}	
		} else if(sX==4) {
			//corners
			if(sY == 0 || sY ==4) { 
				moves = new int[2][];
				if(sY==0) {
					moves[0]= new int[]{0,0};
					moves[1]= new int[]{4,4};
				}
				if(sY==4) {
					moves[0]= new int[]{0,4};
					moves[1]= new int[]{4,0};
				}
				
			} else {
				moves = new int[3][];
				moves[0]= new int[]{4,0};
				moves[1]= new int[]{4,4};
				moves[2]= new int[]{0,sY};
			}	
		}

		else {
			moves = new int[3][];
			if(sY==0) {
				moves[0]= new int[]{sX,4};
				moves[1]= new int[]{0,0};
				moves[2]= new int[]{4,0};
			}
			if(sY==4) {
				moves[0]= new int[]{sX,0};
				moves[1]= new int[]{4,4};
				moves[2]= new int[]{0,4};
			}
		}
		return moves;
	}
}