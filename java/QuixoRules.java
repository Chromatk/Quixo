/* QuixoRules.java
 * Description: Quixo specific rules and operations are handled here.
 *				Class and methods are meant to be accessed statically
 * see README or check https://github.com/Chromatk/Quixo for documentation and contact info
 */

public class QuixoRules {

	// returns a Quixoboard which is the result of player p moving chip (m[0], m[1]) to (m[2],m[3])
	// p: blue = true, red = false
	public static void makeMove(int[] m, QuixoBoard b, boolean p) {
		int xStart = m[0];
		int yStart = m[1];
		int xTarget = m[2];
		int yTarget = m[3];
		boolean[][] isColored = b.isColored;
		boolean[][] isBlue = b.isBlue;

		//shifting 
		if(xStart == xTarget) {
			if(yStart > yTarget) {
				for(int i=yStart;i>0;i--) {
					isColored[xStart][i] = isColored[xStart][i-1];
					isBlue[xStart][i] = isBlue[xStart][i-1];
				}
			}
			if(yStart <yTarget) {
				for(int i=yStart;i<4;i++) {
					isColored[xStart][i] = isColored[xStart][i+1];
					isBlue[xStart][i] = isBlue[xStart][i+1];
				}
			}
		}
		if(yStart == yTarget) {
			if(xStart > xTarget) {
				for(int i=xStart;i>0;i--) {
					isColored[i][yStart] = isColored[i-1][yStart];
					isBlue[i][yStart] = isBlue[i-1][yStart];
				}
			}
			if(xStart < xTarget) {
				for(int i=xStart;i<4;i++) {
					isColored[i][yStart] = isColored[i+1][yStart];
					isBlue[i][yStart] = isBlue[i+1][yStart];
				}
			}
		}

		//put target
		isColored[xTarget][yTarget] = true;
		isBlue[xTarget][yTarget] = p;
	}

	// returns the number of tiles the same color as p on QuixoBoard b
	// p: blue = true, red = false
	public static int countColoredTiles(QuixoBoard b, boolean p) {
		int tiles = 0;
		for(int x=0;x<5;x++) {
			for(int y=0;y<5;y++) {

				if(b.isColored[x][y] && (b.isBlue[x][y] == p)) {
					tiles++;
				}

			}
		}

		return(tiles);
	}

	// returns value base on number of consecutive tiles on each row, column, and major/minor diagonal for player p on board b
	// prototype board analysis/rating heuristic
	public static int countConsecutive(QuixoBoard b, boolean p) {
		int r = 0;
		int nr = 0;
		int mod = 2;
		int nMod = 1;
		//horizontal
		for(int y=0;y<5;y++) {
			int inRow = 0;
			int nInRow = 0;
			for(int x=0;x<5;x++) {
				if(b.isColored[x][y]) {
					if(b.isBlue[x][y]==p) {
						inRow++;
					} else {
						nInRow++;;
					}
				}
				r+=inRow*inRow;
				nr+=nInRow*nInRow;
			}
		}

		for(int x=0;x<5;x++) {
			int inCol = 0;
			int nInCol = 0;
			for(int y=0;y<5;y++) {
				if(b.isColored[x][y]) {
					if(b.isBlue[x][y] == p) {
						inCol++;
					} else {
						nInCol++;
					}
				}
				r+=inCol*inCol;
				nr+=nInCol*nInCol;
			}
		}

		return r-nr;
	}

	// checks if a player has won
	// return:  0 none, 1 red, 2 blue, 3 both
	public static int checkForVictory(QuixoBoard b) {
		int victory = 0;
		boolean bVic = checkForPlayerVictory(true, b);
		boolean rVic = checkForPlayerVictory(false, b);

		if(bVic && rVic) {
			victory = 3;
		} else if(bVic) {
			victory = 2;
		} else if(rVic) {
			victory = 1;
		}

		return victory;
	}

	// checks if player p has won on board b
	// intended for implementation in checkForVictory(QuixoBoard b)
	public static boolean checkForPlayerVictory(boolean p, QuixoBoard b) {

		boolean vic;
		//horizontal
		for(int y=0;y<5;y++) {
			vic = true;
			for(int x=0;x<5;x++) {
				if(!b.isColored[x][y]) {
					vic=false;
				} else if(b.isBlue[x][y] != p) {
					vic = false;
				}
			}
			if(vic) {
				return true;
			}
		}

		//vertical
		for(int x=0;x<5;x++) {
			vic = true;
			for(int y=0;y<5;y++) {
				if(!b.isColored[x][y]) {
					vic=false;
				} else if(b.isBlue[x][y] != p) {
					vic = false;
				}
			}
			if(vic){
				return true;
			}
		}
		//diags
		vic = true;
		for(int i=0;i<5;i++) {
			if(!b.isColored[i][i]) {
				vic = false;
			} else if(b.isBlue[i][i] != p) {
				vic = false;
			}
		}
		if(vic){
			return true;
		}

		vic = true;
		for(int i=0;i<5;i++) {
			if(!b.isColored[i][4-i]) {
				vic = false;
			} else if(b.isBlue[i][4-i] != p) {
				vic = false;
			}
		}
		if(vic) {
			return true;
		}

		
		return false;
	}

	//checks if tile t is a valid tile for player player to choose on board b
	public static boolean playerCanChooseTile(int[] t, QuixoBoard b, boolean player) {
		if(t.length!=2)
			throw new IllegalArgumentException ("playerCanChooseTile in QuixoRules");

		int x = t[0];
		int y = t[1];
		if(x==0||x==4||y==0||y==4) {
			if(b.isColored[x][y]) { 
				if(b.isBlue[x][y] == player) { 
					return true;
				}
			} else {
				return true;
			}
		}
		return false;			
	}

	// returns all spaces tile m can be placed (doesn't take player color and board into account)
	// i.e. up down left right
	public static int[][] movableTiles(int[] m) {
		int x = m[0];
		int y = m[1];
		int[][] moves = new int[0][0];


		// weird ass thing. because you always get edge tiles, one of the coords are always 0 or 4.
		// well, turns out (0+4)%8 = 4 and (4+4)%8 = 0.
		// not sure why right now. too tired to think about it.
		// it lets this method become a lot shorter than the last wreck tho

		//corners
		if( (x==0 || x==4) && (y==0 || y==4) ) {
			moves = new int[2][];
			moves[0] = new int[]{(x+4)%8, y};
			moves[1] = new int[]{x, (y+4)%8};
		} else {
			//other tiles
			moves = new int[3][];
			if(x==0 || x==4) {
				moves[0] = new int[]{(x+4)%8, y};
				moves[1] = new int[]{x, 0};
				moves[2] = new int[]{x, 4};
			} else {
				moves[0] = new int[]{x, (y+4)%8};
				moves[1] = new int[]{0, y};
				moves[2] = new int[]{4, y};
			}
		}

		return moves;
	}

	//return if tile (m[0], m[1]) can be moved to (m[2], m[3])
	//@TODO: ACTUALLY WRITE THIS METHOD
	public static boolean isValidMove(int[] m, QuixoBoard b, boolean player) {
		int[] start = new int[]{m[0], m[1]};
		int[] end = new int[]{m[2], m[3]};

		if(!playerCanChooseTile(start, b, player))
			return false;

		int[][] moves = movableTiles(start);
		for(int[] i:moves) {
			if((i[0]==end[0]) && (i[1]==end[1])) {
				return true;
			}
		}

		return false;
	}


////////////////////////////////////////////////////////////////////////////////////////////////

/*	

	boolean checkForBlueVictory() {
		//horizontal
		boolean vic;
		for(int y=0;y<5;y++) {
			vic = true;
			for(int x=0;x<5;x++) {
				if(!isBlue[x][y])
					vic=false;
			}
			if(vic) {
				return true;
			}
		}
		//vertical
		for(int x=0;x<5;x++) {
			vic = true;
			for(int y=0;y<5;y++) {
				if(!isBlue[x][y])
					vic=false;
			}
			if(vic) {
				return true;
			}
		}
		//diags
		if(isBlue[0][0] && isBlue[1][1] && isBlue[2][2] && isBlue[3][3] && isBlue[4][4]) {
			return true;
		}

		if(isBlue[0][4] && isBlue[1][3] && isBlue[2][2] && isBlue[3][1] && isBlue[4][1]) {
			return true;
		}

		return false;
	}

	boolean checkForRedVictory() {
		boolean vic;
		for(int y=0;y<5;y++) {
			vic = true;
			for(int x=0;x<5;x++) {
				if(isBlue[x][y] || !isColored[x][y])
					vic=false;
			}
			if(vic) {
				return true;
			}
		}

		for(int x=0;x<5;x++) {
			vic = true;
			for(int y=0;y<5;y++) {
				if(isBlue[x][y] || !isColored[x][y])
					vic=false;
			}
			if(vic) {

				return true;
			}
		}

		if(!(isBlue[0][0] || isBlue[1][1] || isBlue[2][2] || isBlue[3][3] || isBlue[4][4])) {
			if(isColored[0][0] && isColored[1][1] && isColored[2][2] && isColored[3][3] && isColored[4][4])
				
				return true;
		}

		if(!(isBlue[0][4] || isBlue[1][3] || isBlue[2][2] || isBlue[3][1] || isBlue[4][1])) {
			if(isColored[0][4] && isColored[1][3] && isColored[2][2] && isColored[3][1] && isColored[4][1])
				return true;
		}

		return false;
	}

	void computeMovable() {
		int sX = selX;
		int sY = selY;

		if(sX==0) {
			//corners
			if(sY == 0 || sY ==4) { 
				movX = new int[2];
				movY = new int[2];
				if(sY==0) {
					movX[0]=0; movY[0]=4;
					movX[1]=4; movY[1]=0;
				}
				if(sY==4) {
					movX[0]=0; movY[0]=0;
					movX[1]=4; movY[1]=4;
				}
				
			} else {
				movX = new int[3];
				movY = new int[3];
				movX[0]=0; movY[0]=0;
				movX[1]=0; movY[1]=4;
				movX[2]=4; movY[2]=sY;
			}	
		} else if(sX==4) {
			//corners
			if(sY == 0 || sY ==4) { 
				movX = new int[2];
				movY = new int[2];
				if(sY==0) {
					movX[0]=0; movY[0]=0;
					movX[1]=4; movY[1]=4;
				}
				if(sY==4) {
					movX[0]=4; movY[0]=0;
					movX[1]=0; movY[1]=4;
				}
				
			} else {
				movX = new int[3];
				movY = new int[3];
				movX[0]=4; movY[0]=0;
				movX[1]=4; movY[1]=4;
				movX[2]=0; movY[2]=sY;
			}	
		}

		else {
			movX = new int[3];
			movY = new int[3];
			if(sY==0) {
				movX[0]=sX; movY[0]=4;
				movX[1]=0; movY[1]=0;
				movX[2]=4; movY[2]=0;
			}
			if(sY==4) {
				movX[0]=sX; movY[0]=0;
				movX[1]=4; movY[1]=4;
				movX[2]=0; movY[2]=4;
			}
			
		}
	}
*/
	
}