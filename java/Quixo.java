

import java.awt.*;
import java.io.*;
import java.util.*;
import java.awt.Graphics;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.event.*;

public class Quixo extends JPanel implements Runnable, MouseListener
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

	QuixoBoard board;

	boolean selected = false;
	int selX;
	int selY;
	int[][] movable;

	int turnNum = 0;

	boolean enableAI1 = true;
	boolean enableAI2 = true;

	AIPlayer player1;
	AIPlayer player2;

	boolean demoMode = true;
	boolean demoSwitch = false; //KEEP THIS AS FALSE ON INIT

	float fps = 5f;
	float aps = 2f;
	final boolean lockAPStoFPS = true;
	final boolean apsUnlock = false;
	final boolean fpsUnlock = false;

	long lastRenderTime = System.currentTimeMillis();
	long lastActionTime = System.currentTimeMillis();
	String log = "";

	boolean quit = false;
	boolean gameOver = false;
	int winner = 0;

	public void run()
	{
		//Start of Thread	
		log("log started");
		log("Start of Thread");

		resetVars();

		render();
		repaint();

		int[] aiMove = new int[4];

		while(!quit) {

			while(!gameOver)
			{
				long currentTime = System.currentTimeMillis();

				//AI stuff
				if((currentTime-lastActionTime>=1000f/aps || apsUnlock) && (enableAI1 || enableAI2)) {
					
					//if demoMode is false, !demoSwitch and demoSwitch should happen on the same iteration

					// !demoSwitch choosing a move and only drawing the selected tile
					if(!demoSwitch) {
						if(enableAI1 && turnNum%2==0) {//essential
							aiMove = player1.move(board); //essential
							//System.out.println(turnNum + " | blue | " + aiMove[0] + ", " + aiMove[1] + " | " + aiMove[2] + ", " + aiMove[3]);
							log(turnNum + " | blue | " + aiMove[0] + ", " + aiMove[1] + " | " + aiMove[2] + ", " + aiMove[3]);
						} else if(enableAI2 && turnNum%2==1) { //essential
							aiMove = player2.move(board); //essential
							//System.out.println(turnNum + " | red | " + aiMove[0] + ", " + aiMove[1] + " | " + aiMove[2] + ", " + aiMove[3]);
							log(turnNum + " | red | " + aiMove[0] + ", " + aiMove[1] + " | " + aiMove[2] + ", " + aiMove[3]);
						}

						if(QuixoRules.isValidMove(aiMove, board, turnNum%2==0))
							setSelectedTile(aiMove[0], aiMove[1]); //set selected tiles and movable ghosts
						else
							throw new IllegalArgumentException("AI tried to make an illegal move on turn " + turnNum);

						if(!demoMode) {
							demoSwitch = true;
						}
					}
					// demoSwitch make the move and update it
					if(demoSwitch) {
						QuixoRules.makeMove(aiMove, board, turnNum%2==0); // essential
						selected=false;
						turnNum++;
						if(!demoMode) {
							demoSwitch = false;
						}
					}
					// if demoMode = true, the two if statements above will only execute on alternating iterations
					if(demoMode) {
						demoSwitch = !demoSwitch;
					}

					
				}

				// Check if a player has won
				if(currentTime-lastActionTime>=1000f/aps || apsUnlock) { 
					winner = QuixoRules.checkForVictory(board); // essential
					if(winner != 0) { // essential
						gameOver = true; // essential
						//System.out.println("winner: " + winner);
						log("winner: "+winner);
					} 

					lastActionTime = currentTime;
				}
				

				//render
				if(currentTime-lastRenderTime>=1000f/fps || fpsUnlock) {
					render();
					repaint();
					lastRenderTime = currentTime;
				}

			}//while(!gameOver)
			//quit = true; ///////////////////////////////////quit
			try {
				Thread.sleep(100);
			} catch (Exception e){
				e.printStackTrace();
			};
		}//while(!quit)

		exportLog(log);
		System.out.println("End of Thread");
		System.exit(0);
		//End of Thread
	}

	private void exportLog(String s) {
		BufferedWriter writer = null;
		try {
			File logFile = new File("QuixoLog.txt");
			System.out.println("writing log to: " + logFile.getCanonicalPath());

			writer = new BufferedWriter(new FileWriter(logFile));
			writer.write(s);

			System.out.println("log successfully written");
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			try {
				writer.close();
			} catch (Exception e) {}
		}
	}

	private void log(String s) {
		log += "\n"+(new Date()).toString() + " | " + s;
	}

	void setSelectedTile(int x, int y) {
		selected = true;
		selX = x;
		selY = y;
		movable = QuixoRules.movableTiles(new int[]{x, y});
	}

	void resetVars() {
		player1 = new AIPlayer(true);
		player2 = new AIPlayer(false);
		turnNum = 0;
		selected=false;
		board = new QuixoBoard(new boolean[5][5], new boolean[5][5]);
		gameOver = false;
		winner = 0;
		lastRenderTime = System.currentTimeMillis();
		lastActionTime = System.currentTimeMillis();
	}

	boolean blueTurn() {
		boolean s = true;
		if(turnNum%2==1) {
			s = false;
		}
		return s;
	}


	/*******************************************really shouldn't need to look under here***********************************************************/

	public void render() {
		bufferGraphics.clearRect(0, 0, 800, 600);

		bufferGraphics.setColor(Color.BLACK);
		bufferGraphics.fillRect(0,0,800,600);

		bufferGraphics.setColor(Color.WHITE);
		String s = "blue";
		if(!blueTurn())
			s = "red";
		bufferGraphics.drawString("Turn#: " + (turnNum/2+1) + " for " + s + " player", 300, 50);
		bufferGraphics.drawString(s, 50, 250);
		
		//draw tiles
		for(int x=0;x<5;x++) {
			for(int y=0;y<5;y++) {
				bufferGraphics.setColor(Color.WHITE);
				if(board.isColored[x][y]) {
					if(board.isBlue[x][y])
						bufferGraphics.setColor(Color.BLUE);
					else
						bufferGraphics.setColor(Color.RED);
				}
				bufferGraphics.fillRect(border + x*(tileWidth+tileOffset), border + y*(tileWidth+tileOffset), tileWidth, tileWidth);
			}
		}

		//draw selection and movable ghosts
		if(selected) {
			//selected tile
			bufferGraphics.setColor(Color.LIGHT_GRAY);
			bufferGraphics.drawRect(border + selX*(tileWidth+tileOffset), border + selY*(tileWidth+tileOffset), tileWidth, tileWidth);
			bufferGraphics.setColor(Color.GREEN);
			for(int[] m:movable) {
				bufferGraphics.fillRect(border + m[0]*(tileWidth+tileOffset), border + m[1]*(tileWidth+tileOffset), tileWidth, tileWidth);
			}
		}
	}

	public void mouseClicked(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();

		//System.out.println(e.getButton());

		if(e.getButton() == 4) {
			quit = true;
		}

		//reset game
		if(e.getButton()==2) {
			resetVars();
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
							
							setSelectedTile(i,j);
							
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

			int[] m = new int[]{selX, selY, xTarget, yTarget};
			if(QuixoRules.isValidMove(m, board, turnNum%2==0)){
				QuixoRules.makeMove(m, board, turnNum%2==0);
				turnNum++;
			}
			
			selected = false;
			
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

	@Override
	public void paintComponent(Graphics g)
	{
		//double buffering clear image

		g.drawImage(offscreen, 0, 0, this);

	}//paint

	public static void main(String[] args) {
		new Quixo();
	}

	public Quixo()
	{
		if(lockAPStoFPS) { 
			aps = fps;
		}

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
}