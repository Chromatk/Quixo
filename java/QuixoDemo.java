//Demo shows selection and placement of blocks by AI to more easily visualize moves

import java.awt.*;
import java.util.*;
import java.awt.Graphics;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.event.*;

public class QuixoDemo extends JPanel implements Runnable, MouseListener
{
	//variable declaration

	//double buffering stuff
	Graphics bufferGraphics;
	Image offscreen;
	JFrame frame;

	Thread thread;

	final int tileWidth = 30;
	final int tileOffset = 5;
	final int border = 20;

	boolean[][] isColored = new boolean[5][5];
	boolean[][] isBlue = new boolean[5][5];

	boolean selected = false;
	int selX;
	int selY;
	int[] movX;
	int[] movY;

	int turnNum = 0;

	boolean enableAI1 = true;
	boolean enableAI2 = true;

	AIPlayer player1;
	AIPlayer player2;

	float fps = 5f;

	public static void main(String[] args) {
		new QuixoDemo();
	}

	public QuixoDemo()
	{
		frame = new JFrame();
		frame.setTitle("java");
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(this);
        frame.setVisible(true);


		//double buffering stuff
		offscreen = createImage(800, 600);
		bufferGraphics = offscreen.getGraphics();
		addMouseListener(this);

		thread = new Thread(this);
		thread.start();

	}//init

	void initVars() {
		player1 = new AIPlayer(true);
		player2 = new AIPlayer(false);
	}

	public void render() {
		bufferGraphics.setColor(Color.WHITE);
		bufferGraphics.clearRect(0, 0, 800, 600);
		//bufferGraphics.drawImage(player.picAlive, player.xPos, player.yPos, player.xSize, player.ySize, this);
		//bufferGraphics.drawString("Welcome to Germ Eater", xWorldSize/2 - 50, yWorldSize*1/5);
		bufferGraphics.setColor(Color.BLACK);
		bufferGraphics.fillRect(0,0,800,600);

		bufferGraphics.setColor(Color.WHITE);
		String playerString = "blue";
		if(turnNum%2==1)
			playerString = "red";
		bufferGraphics.drawString("Turn#: " + (turnNum/2+1) + " for " + playerString + " player", 300, 50);
		
		for(int x=0;x<5;x++) {
			for(int y=0;y<5;y++) {
				bufferGraphics.setColor(Color.WHITE);
				if(isColored[x][y]) {
					if(isBlue[x][y])
						bufferGraphics.setColor(Color.BLUE);
					else
						bufferGraphics.setColor(Color.RED);
				}
				bufferGraphics.fillRect(border + x*(tileWidth+tileOffset), border + y*(tileWidth+tileOffset), tileWidth, tileWidth);
			}
		}

		if(selected) {
			//selected tile
			bufferGraphics.setColor(Color.LIGHT_GRAY);
			bufferGraphics.drawRect(border + selX*(tileWidth+tileOffset), border + selY*(tileWidth+tileOffset), tileWidth, tileWidth);
			bufferGraphics.setColor(Color.GREEN);
			for(int i=0;i<movX.length;i++) {
				bufferGraphics.fillRect(border + movX[i]*(tileWidth+tileOffset), border + movY[i]*(tileWidth+tileOffset), tileWidth, tileWidth);
			}
		}
	}

	@Override
	public void paintComponent(Graphics g)
	{
		//double buffering clear image

		g.drawImage(offscreen, 0, 0, this);

	}//paint

	public void run()
	{
		//Start of Thread
		System.out.println("Start of Thread");
	
		initVars();
		boolean gameOver = false;
		int winner = 0; // 0 -draw 1 -red 2 -blue

		render();
		repaint();
		long lastRenderTime = System.currentTimeMillis();
		long lastActionTime = System.currentTimeMillis();
		boolean demoSwitch = false;
		int[] AImove = new int[2];
		while(!gameOver)
		{
			long currentTime = System.currentTimeMillis();
			if(currentTime-lastActionTime>=1000f/fps) {
				if(enableAI1 && turnNum%2==0) {
					
					if(!demoSwitch) {
						AImove = player1.move(isColored, isBlue);
						System.out.println(turnNum + " | blue | " + AImove[0] + ", " + AImove[1] + " | " + AImove[2] + ", " + AImove[3]);
						selectTile(AImove[0], AImove[1]);
						demoSwitch = true;
					} else {
						makeMove(AImove[2], AImove[3]);
						demoSwitch = false;
						if(checkForBlueVictory()) {
							gameOver = true;
							winner = 2;
						}
					}
					
				}

				else if(enableAI2 && turnNum%2==1) {
					if(!demoSwitch) {
						AImove = player2.move(isColored, isBlue);
						System.out.println(turnNum + " | red | " + AImove[0] + ", " + AImove[1] + " | " + AImove[2] + ", " + AImove[3]);
						selectTile(AImove[0], AImove[1]);
						demoSwitch = true;
					} else {
						makeMove(AImove[2], AImove[3]);
						demoSwitch = false;
						if(checkForRedVictory()) {
							gameOver = true;
							winner = 1;
						}
					}	
				}
				lastActionTime = currentTime;

				render();
				repaint();
				lastRenderTime = currentTime;
			}

		}
		System.out.println(winner);
		System.out.println("End of Thread");
		//End of Thread
	}

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

	boolean selectTile(int inI, int inJ) {
		int i = inI;
		int j = inJ;
		if(i==0||i==4||j==0||j==4) {
			if(isColored[i][j]) { 
				if(isBlue[i][j] == (turnNum%2==0)) { 
					selected = true;
					selX = i;
					selY = j;
					computeMovable();
					return true;
				}
			} else {
				selected = true;
				selX = i;
				selY = j;
				computeMovable();	
				return true;
			}
		}
		return false;			
	}

	void makeMove(int inTx, int inTy) {
		int xTarget = inTx;
		int yTarget = inTy;
		boolean clickValid = false;
			
		for(int i=0;i<movX.length;i++) {
			if(movX[i] == xTarget && movY[i] == yTarget) {
				clickValid = true;
			}
		}
		//escape
		if(!clickValid) {
			selected = false;
		} else {
			//shift ignore target
			if(selX == xTarget) {
				if(selY > yTarget) {
					for(int i=selY;i>0;i--) {
						isColored[selX][i] = isColored[selX][i-1];
						isBlue[selX][i] = isBlue[selX][i-1];
					}
				}
				if(selY <yTarget) {
					for(int i=selY;i<4;i++) {
						isColored[selX][i] = isColored[selX][i+1];
						isBlue[selX][i] = isBlue[selX][i+1];
					}
				}
			}
			if(selY == yTarget) {
				if(selX > xTarget) {
					for(int i=selX;i>0;i--) {
						isColored[i][selY] = isColored[i-1][selY];
						isBlue[i][selY] = isBlue[i-1][selY];
					}
				}
				if(selX < xTarget) {
					for(int i=selX;i<4;i++) {
						isColored[i][selY] = isColored[i+1][selY];
						isBlue[i][selY] = isBlue[i+1][selY];
					}
				}
			}

			//put target
			isColored[xTarget][yTarget] = true;
			isBlue[xTarget][yTarget] = (turnNum%2==0);

			selected = false;
			turnNum++;
		}
	}

	public void mouseClicked(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();

		//reset game
		if(e.getButton()==2) {
			turnNum=0;
			selected=false;
			isColored = new boolean[5][5];
			isBlue = new boolean[5][5];
		}

		//reset select
		if(e.getButton()==3) {
			selected=false;
			return;
		}
		//not selected
		if(!selected) {
			for(int i=0;i<5;i++) {
				for(int j=0;j<5;j++) {
					if(x>border+i*(tileWidth+tileOffset) && x<border+i*(tileWidth+tileOffset)+tileWidth) {
						if(y>border+j*(tileWidth+tileOffset) && y<border+j*(tileWidth+tileOffset)+tileWidth) {
							
							selectTile(i,j);
							
						}

					}
				}
			}
		}
		//selected
		else {
			int xTarget = 0;
			int yTarget = 0;
			for(int i=0;i<5;i++) {
				for(int j=0;j<5;j++) {
					if(x>border+i*(tileWidth+tileOffset) && x<border+i*(tileWidth+tileOffset)+tileWidth) {
						if(y>border+j*(tileWidth+tileOffset) && y<border+j*(tileWidth+tileOffset)+tileWidth) {
							
							xTarget = i;
							yTarget = j;
							
						}

					}
				}
			}

			makeMove(xTarget, yTarget);
			
			//check if clicked in box
			//
		}
		
	}
	public void mouseEntered(MouseEvent e) {
		//
	}
	public void mouseExited(MouseEvent e) {
		//
	}
	public void mousePressed(MouseEvent e) {
		//
	}
	public void mouseReleased(MouseEvent e) {
		//
	}
}