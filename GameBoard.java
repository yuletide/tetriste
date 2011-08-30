/**
 * @author Alex Yule
 * @date Dec 6, 2008
 */

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.Timer;

//import structure.Assert;

@SuppressWarnings("serial")
/**
 * A Tetriste GameBoard class to keep track of, and display, the Tetristonimoes.
 * @version 0.9.2b
 * Changes since 0.9.1b: removed paint timer, instead of setting hasChanged(true), methods simply call gb.repaint()! Paint on demand...
 */
public class GameBoard extends JPanel {

	/**
	 * To allow the board to access its {@link Tetriste} caller for controlling
	 * the game (score, levels)
	 */
	protected Tetriste apl;

	// STATIC FINAL VARS
	/**
	 * Empty cell
	 */
	public static final int EMPTY = 0;
	/**
	 *
	 */

	/**
	 * A color palette, each color corresponds to a piece type, with 0 = empty square (board background)
	 * Colors adapted from these palettes and colors:
	 * <ul><li>http://www.colourlovers.com/palette/177327/red_army_choir</li>
	 * <li>http://www.colourlovers.com/palette/42503/Soviet_bus_stop_no.1</li>
	 * <li>http://www.colourlovers.com/palette/393226/soviet_commander</li>
	 * <li>http://www.colourlovers.com/color/73100A/Soviet_Red</li>
	 * <li>http://www.colourlovers.com/color/364955/No_Dawn_Breaks</li>
	 * <li>http://www.colourlovers.com/color/D9D8D3/weathered_white</li>
	 * <li>http://www.colourlovers.com/color/AFDFE5/lucy_in_the_sky (text labels) </li>
	 * <li>http://www.colourlovers.com/color/DE8D1B/Tang</li></ul>
	 */
	protected static final Color[] COLORS = { new Color(217,216,211)/*Weathered White*/, new Color(61,61,61),
		new Color(115, 16, 10)/*Soviet Red*/,  new Color(50, 71, 59)/*Kalinka*/, new Color(94, 78, 37)/*Song of the Plains*/, new Color(54, 73, 85)/*No Dawn Breaks*/, new Color(222, 141, 27)/*Tang*/,
		new Color(48, 24, 24)/*Stone*/};

	// STATIC VARS
	/**
	 * Audio clips for sound effects (unimplemented)
	 * 
	 * protected static AudioClip clear; protected static AudioClip levelUp;
	 * protected static AudioClip youLose;
	 */

	/**
	 * size of one cell, in pixels
	 */
	protected static int CELLSIZE = 20;
	protected static int dx = 1;
	protected static int dy = 1;

	// STATIC METHODS
	/**
	 * static method print a string representation of a board to std out
	 * 
	 * @param gb
	 */
	public static void print(GameBoard gb) {
		System.out.print(gb.toString());
	}

	// INSTANCE VARS
	/**
	 * Player score
	 */
	protected int score = 0;

	/**
	 * the current level being played, used to calculate drop speed of pieces
	 */
	protected int level = 1;

	/**
	 * The number of total lines cleared so far
	 */
	protected int lines = 0;

	/**
	 * width of game board in cells (units)
	 */
	protected int unitWidth = 10;

	/**
	 * height of game board in cells (units)
	 */
	protected int unitHeight = 20;

	/**
	 * 2-dimensional vector of Integers to store cell values
	 */
	protected Vector<Vector<Integer>> board;

	/**
	 * boolean to track game status
	 */
	protected boolean gameOver = true;
	protected boolean paused = false;

	/**
	 * Timer to control dropping pieces
	 */
	protected Timer dropTimer;
	/**
	 * Default drop speed, overridden by getDropSpeed() method
	 */
	protected int dropSpeed = 1000;
	protected int dropPause = 500;


	// Objects //

	/**
	 * the active piece
	 */
	protected Piece thePiece = Piece.EMPTY;
	protected Graphics theG = null;

	// Contstructors //
	/**
	 * copy constructor
	 * 
	 * @param gb
	 */
	public GameBoard(GameBoard gb) {
		this(gb.getUnitWidth(), gb.getUnitHeight(), gb.getApplet());// ,
		// gb.theG);
	}

	public GameBoard(Tetriste apl) {
		this(10, 20, apl);// , g);
	}

	/**
	 * constructs a GameBoard of a given size
	 * 
	 * @param width
	 * @param height
	 * @param g
	 */
	public GameBoard(int width, int height, Tetriste apl) {// , Graphics g){
		this.apl = apl;
		this.setOpaque(true);
		System.out.println("Creating new GameBoard");
		this.setDoubleBuffered(true);
		// theG = g;
		this.unitWidth = width;
		this.unitHeight = height;
		// initialize the internal board vector
		this.initBoard();

		/**
		 * Timer to drop the pieces
		 */
		dropTimer = new Timer(getDropSpeed(), new PieceDropper());
		dropTimer.setCoalesce(false);
		dropTimer.setInitialDelay(dropPause);
		dropTimer.setDelay(getDropSpeed());

	}

	// Init Methods //
	/**
	 * (Re)Initialize the board, filling it with EMPTY value cells
	 */
	public void initBoard() {
		board = new Vector<Vector<Integer>>(unitWidth);
		for (int i = 0; i < unitWidth; i++) { // populate board with EMPTY
			// values (0)
			board.add(i, new Vector<Integer>(unitHeight));
			for (int j = 0; j < unitHeight; j++) {
				// System.out.print(" "+(i+width*j)+ " " );
				board.get(i).add(GameBoard.EMPTY);
			}
		}
	}

	// Paint methods //
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		//System.out.println("GameBoard.paintComponent()");
		paintBoard(g);
		apl.setScore(score);
		apl.setLevel(level);
	}

	/**
	 * paint the board, using different colors for the various cell values
	 */
	public void paintBoard(Graphics g) {
		// System.out.println(g);
		// Image temp = new Image
		//System.out.println("GameBoard.paintBoard()");
		g.setColor(Color.BLACK);
		g.drawRect(0, 0, getWidth(), getHeight()); // board outline
		for (int j = 0; j < getUnitHeight(); j++) {
			for (int i = 0; i < getUnitWidth(); i++) {
				// System.out.println("CELL: "+i);
				// else if (tempCell==GameBoard.FULL)
				// g.setColor(Color.BLUE);
				g.setColor(COLORS[getCell(i, j)]);
				g.fillRect(i * CELLSIZE, j * CELLSIZE, CELLSIZE, CELLSIZE); // fill
				// the
				// cell!
			}
		}
	}

	// board querying and control methods //
	/**
	 * @return the Vector of Vectors of Integers representing the board itself
	 */
	public Vector<Vector<Integer>> getBoard() {
		return board;
	}

	public Tetriste getApplet() {
		return this.apl;
	}

	/**
	 * Zero-indexed cell lookup function
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public synchronized Integer getCell(int x, int y) {
		if (x > -1 && x < unitWidth && y < unitHeight && board.size() > 0)
			return board.get(x).get(y);
		else {
			System.err
			.println("x>-1 && x<unitWidth && y<unitHeight && board.size()>0   "
					+ (x > -1)
					+ (x)
					+ (unitWidth)
					+ (y)
					+ (unitHeight)
					+ (board.size() > 0));
			throw new ArrayIndexOutOfBoundsException();

		}
	}

	/**
	 * Fill a cell with a given value
	 * 
	 * @param x
	 * @param y
	 * @param v
	 */
	public synchronized void setCell(int x, int y, int v) {
		// System.out.println("FILLING CELL "+x+", "+y+" -->"+v);
		if (x > -1 && x < unitWidth && y > -1 && y < unitHeight){
			board.get(x).set(y, v);
		} else {
			throw new ArrayIndexOutOfBoundsException();
		}
	}

	/**
	 * Clear a cell (fill with empty value)
	 * 
	 * @param x
	 * @param y
	 */
	public synchronized void clearCell(int x, int y) {
		setCell(x, y, EMPTY);
	}

	/**
	 * Check board for, then clear, completed lines
	 */
	public void checkBoard() {
		//System.out.println("GameBoard.checkBoard()");
		loseTest();
		int clearedLines = 0;
		//System.out.println("GameBoard.checkLines() " + unitHeight);
		for (int j = 0; j < unitHeight; j++) {
			//	System.out.println("Checking row " + j);
			if (checkRow(j)) {
				clearedLines++;
				clearRow(j);
			}
		}
		lines += clearedLines;
		apl.setLines(lines);
		switch (clearedLines) {
		case 0:
			break;
		case 1:
			score(level * 100);
			break;
		case 2:
			score(level * 300);
			break;
		case 3:
			score(level * 500);
			break;
		case 4:
			score(level * 800);
			break;
		default:
			break;
		}
		System.out.println(score);
	}

	/**
	 * Add some points to the score, send new score up to applet
	 * 
	 * @param points
	 */
	public void score(int points) {
		System.out.println(score + " + "+points+" level check: "+(2000*Math.pow(2,getLevel()-1)));
		score += points;
		apl.setScore(score);
		if (score >= (1000 * Math.pow(2,getLevel()-1)))
			nextLevel();
	}

	/**
	 * Check if a line is filled
	 * 
	 * @param y
	 * @return
	 */
	public synchronized boolean checkRow(int y) {
		for (int i = 0; i < getUnitWidth(); i++) {
			if (isEmpty(i, y)) {
				// System.out.println("EMPTY cells at row "+y);
				return false;
			}
		}
		return true;
	}

	/**
	 * Clear the cells in a row and drop all blocks above
	 * 
	 * @param row
	 */
	public void clearRow(int row) {
		System.out.println("GameBoard.clearRow(" + row + ")");
		for (int i = 0; i < getUnitWidth(); i++)
			clearCell(i, row);
		// TODO add delay in here and/or flickering

		for (int j = row; j > 0; j--)
			for (int i = 0; i < getUnitWidth(); i++)
				setCell(i, j, getCell(i, j - 1)); // drop all blocks down

	}

	/**
	 * Fill a row with a certain value
	 * 
	 * @param row
	 * @param v
	 */
	public void fillRow(int row, int v) {
		for (int i = 0; i < getUnitWidth(); i++)
			setCell(i, row, v);
	}

	/**
	 * return component preferred size
	 * 
	 * @return a new dimension object corresponding to the size of the board
	 */
	public Dimension getPreferredSize() {
		return new Dimension(CELLSIZE * getUnitWidth(), CELLSIZE
				* getUnitHeight());
	}

	public Dimension getMaximumSize() {
		return getPreferredSize();
	}

	/**
	 * Compute a drop speed based on current level
	 * 
	 * @return
	 */
	protected int getDropSpeed() {
		return ((2/level)*600 + 400);
	}

	// Game querying and control

	/**
	 * Begin the game, pause before starting the timer to allow everything to
	 * settle
	 * 
	 * @throws InterruptedException
	 */
	public void startGame() throws InterruptedException {
		Tetriste.getBgMusic().loop();
		score = 0;
		level = 1;
		paused = false;
		gameOver = false;
		initBoard();
		add();
		Thread.sleep(100);

		dropTimer.start();
	}

	/**
	 * End Game
	 */
	public void endGame() {
		gameOver = true;
		dropTimer.stop();
		apl.playButton.setText("Play");
		apl.pauseButton.setText("Pause");
		apl.removeKeyListener(apl);
		Tetriste.getBgMusic().stop();

	}

	/**
	 * @return whether game is paused
	 */
	public boolean isPaused() {
		return paused;
	}

	/**
	 * Pause or unpause the game
	 * 
	 * @return the resulting value of <code>paused</code>()
	 */
	public boolean pauseToggle() {
		if (isPlaying() && !paused) {
			dropTimer.stop();
			paused = !paused;
		} else if (isPlaying()) {
			dropTimer.start();
			paused = !paused;
		}
		return paused;
	}

	public int getLevel() {
		return level;
	}

	// Piece querying and control

	/**
	 * @param x
	 * @param y
	 * @return whether a cell is not empty (full)
	 */
	public synchronized boolean isFilled(int x, int y) {
		return !isEmpty(x, y);
	}

	/**
	 * Return whether a cell value is equal to the EMPTY value, or False if
	 * query is out of bounds (for move validity checking)
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public boolean isEmpty(int x, int y) {
		// System.out.println("GameBoard.isEmpty()"+x+" "+y+" "+(getCell(x,
		// y)==EMPTY));
		if (y < getUnitHeight() && x < getUnitWidth())
			return getCell(x, y) == EMPTY;
		else
			return false;
	}

	/**
	 * Move the current piece
	 * 
	 * @param dx
	 * @param dy
	 */
	public synchronized void movePiece(int dx, int dy) {
		thePiece.move(dx, dy);

	}

	/**
	 * Rotate teh current piece
	 */
	public synchronized void rotatePiece() {
		thePiece.rotate();
	}

	/**
	 * return a string representation of the game board
	 */
	public String toString() {
		String ret = "";

		for (int j = 0; j < unitHeight; j++) {
			ret = " ";
			for (int i = 0; i < unitWidth; i++)
				ret += isFilled(i, j) ? "[" + getCell(i, j) + "]" : "[ ]";
				System.out.println(ret);
		}
		return ret;
	}

	/**
	 * print a string representation of the board to std out
	 */
	public void print() {
		print(this);
	}

	/**
	 * Add a piece to the center of the top of the game board (by filling in the
	 * corresponding board cells with the piece's type value)
	 * 
	 * @param p
	 *            the piece to add
	 */
	public void add(Piece p) {
		checkBoard();
		System.out.println("GameBoard.add(Piece p) gb:"+p.gb+" "+Piece.Shape.values()[0].getWidth(0));
		thePiece = p;
		try {
			thePiece.setX((unitWidth-p.getLocalWidth())/ 2);
			thePiece.setY(0);
			repaint(); //hasChanged(true);
		} catch (Exception e) {
			System.out.println("ERROR adding piece, game over!");
			apl.setTitle("GAME OVER");
			endGame();
			//System.out.println(apl.leninIcon.getImageLoadStatus());
			apl.getGraphics().drawImage(apl.lenin, this.getX(), this.getY(), this.getWidth(), this.getHeight(), this);
		}
	}

	// TODO: figure out how to reliably test for loss
	private boolean loseTest() {
		return false;
	}

	/**
	 * Add a new random piece
	 */
	public void add() {
		add(new Piece(this));
	}

	/**
	 * Drops instantly, triggered by spacebar. Add 2 points per line dropped
	 */
	public synchronized void fullDrop() {
		score(thePiece.fullDrop());
	}

	/**
	 * User-triggered drop, add 1 point per line dropped
	 */
	public synchronized void fastDrop() {
		score(1);
		System.out.println(score);
		thePiece.moveDown();
	}

	public boolean isPlaying() {
		return !isOver();
	}

	public boolean isOver() {
		return gameOver;
	}

	public int getUnitWidth() {
		return unitWidth;
	}

	public int getUnitHeight() {
		return unitHeight;
	}

	/**
	 * advance to next level
	 */
	public int nextLevel() {
		System.out.println("LEVEL UP!!! NEW SPEED: "+getDropSpeed());
		level++;
		dropTimer.setDelay(getDropSpeed());
		apl.setMusic(level);
		//apl.setBackground(new Color((int) (255 * Math.random()),
		//	(int) (255 * Math.random()), (int) (255 * Math.random())));
		return level;
	}




	class PieceDropper implements ActionListener {
		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent
		 * )
		 */
		public synchronized void actionPerformed(ActionEvent e) {
			thePiece.moveDown();
		}
	}

}

