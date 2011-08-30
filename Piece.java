/**
 * Piece.java
 * @author Alex Yule
 * @date December 6, 2008
 */

/**
 * A class of Tetristonimo pieces for Tetriste
 * @version 0.9.2b
 */
public class Piece {

	/**
	 * Constant representing a de-activated piece (an inactive, full cell)
	 */
	public static final int FULLCELL = 1;
	//public static final int EMPTYCELL = 0;
	
	/**
	 * Colors for each piece type
	 */
	public static final Piece EMPTY = null;

	/*
	 * Templates containing the four rotation states for each piece type, stored as 2-d arrays, plus an ID value for each unique shape
	 */
	public static final int[][][] iTemplates = { {{1,1,1,1}} , {{1},{1},{1},{1}} , {{1,1,1,1}} , {{1},{1},{1},{1}} };
	public static final int[][][] sTemplates = { {{0,1,1},{1,1,0}} , {{1,0},{1,1},{0,1}} , {{0,1,1},{1,1,0}} , {{1,0},{1,1},{0,1}} };
	public static final int[][][] zTemplates = { {{1,1,0},{0,1,1}} , {{0,1},{1,1},{1,0}} , {{1,1,0},{0,1,1}} , {{0,1},{1,1},{1,0}} };	
	public static final int[][][] lTemplates = { {{0,0,1},{1,1,1}} , {{1,0},{1,0},{1,1}} , {{1,1,1},{1,0,0}} , {{1,1},{0,1},{0,1}} };	
	public static final int[][][] jTemplates = { {{1,0,0},{1,1,1}} , {{1,1},{1,0},{1,0}} , {{1,1,1},{0,0,1}} , {{0,1},{0,1},{1,1}} };
	public static final int[][][] tTemplates = { {{0,1,0},{1,1,1}} , {{1,0},{1,1},{1,0}} , {{1,1,1},{0,1,0}} , {{0,1},{1,1},{0,1}} };
	public static final int[][][] oTemplates = { {{1,1},{1,1}} , {{1,1},{1,1}} , {{1,1},{1,1}} , {{1,1},{1,1}} };

	/**
	 * An enumeration to store the various types of pieces (tetrominoes)
	 * @author Alex Yule
	 * @date Dec 4, 2008
	 *
	 */
	public enum Shape {
		I (iTemplates),
		S (sTemplates),
		Z (zTemplates),
		L (lTemplates),
		J (jTemplates),
		T (tTemplates), //5
		O (oTemplates);

		/**
		 * Utility method to print an array
		 * @param ar
		 */
		/*static void printArray(int[] ar){
			System.out.println("Shape.printArray()");
			for (int i:ar)
				System.out.println(i);
		}*/

		/**
		 * Instance variable to store each shape's rotation states
		 */
		protected final int[][][] templates;

		/**
		 * constructor, takes a 3-d array containing
		 * @param _templates an array of shape templates, each containing a piece (2-d array of int's) and representing a different rotation state
		 */
		Shape(int[][][] _templates){ 
			this.templates = _templates;
		}

		/**
		 * return the piece for a shape's given orientation
		 * @param orientation
		 * @return a 2-d array of int's (the piece)
		 */
		public int[][] getPiece(int orientation){
			return templates[orientation];
		}
		/**
		 * Print a piece to standard out
		 * @param o orientation
		 */
		public void print(int o){
			System.out.print(toString(o));
		}

		public String toString(int o){
			int[][] temp = templates[o];
			String ret = "";
			for (int i=0; i<temp.length; i++){
				for (int j=0; j<temp[0].length; j++){
					ret+=temp[i][j];
				}
				ret+="\n";
			}
			return ret;
		}

		/**
		 * @param orientation
		 * @return width of the template at a given orientation
		 */
		public int getWidth(int orientation){
			return templates[orientation][0].length;
		}

		/**
		 * @param orientation
		 * @return height of the template at a given orientation
		 */
		public int getHeight(int orientation) {
			return templates[orientation].length;
		}
	}

	/* *****************************************************************************
	 * Instance Variables (Piece)
	 ******************************************************************************/
	/**
	 * stores an ID representing the type of piece (for coloring) <b>one-indexed!!!!!, since zero is empty</b>
	 */
	protected int pieceType = 0;

	/**
	 * stores the current orientation, defaults to 0, each increment represents a 90 degree clockwise rotation. max value of 3
	 */
	protected int theOrient = 0;

	/**
	 * Shape enum of piece
	 */
	protected Shape theShape;

	/**
	 * Current x coordinate of piece's top left block
	 */
	protected int xPos;
	/**
	 * Current y coordinate of piece's top left block
	 */
	protected int yPos;

	/**
	 * The {@link GameBoard} on which the piece will be placed
	 */
	protected GameBoard gb;

	/* **************************************************
	 * Constructors
	 */

	/**
	 * Create a random piece (rerturns an int from 1 to Shape.values().length-1)
	 * @param gb The hosting {@link GameBoard}
	 */
	public Piece(GameBoard gb){
		this(gb, (int)(Math.floor(Math.random()*(Shape.values().length-1))));
		//System.out.println(Math.random()*(Shape.values().length-1));
		//System.out.println(this.theShape);
	}

	/**
	 * Create a given type of piece
	 * @param gb
	 * @param pieceType
	 */
	public Piece(GameBoard gb, int pieceType){
		this.gb = gb;
		System.out.println("Creating piece of type: "+pieceType);
		if (pieceType>-1 && pieceType<Shape.values().length){
			theShape = Shape.values()[pieceType];
			this.pieceType = pieceType+1;
		} else {
			System.err.println("Error creating new piece! pieceType: "+pieceType+" max piecetype: "+(Shape.values().length-1));
		}
	}

	/**
	 * A copy constructor
	 * @param old The {@link Piece} to copy
	 */
	/*public Piece(Piece old){
		this(old.gb, old.getPieceType());
	}*/

	/* ***********************************
	 * Getters and setters
	 */
	public int getPieceType(){
		return this.pieceType;
	}

	public int getX(){
		return xPos;
	}

	public int getY(){
		return yPos;
	}

	public void setX(int x) throws Exception{
		if (gb.isFilled(x, yPos))
			throw new Exception();
		xPos = x;
	}

	public void setY(int y) throws Exception{
		if (gb.isFilled(xPos, y))
			throw new Exception();
		yPos = y;
	}
	public int getLocalWidth(){
		return theShape.getWidth(theOrient);
	}

	public int getLocalHeight(){
		return theShape.getHeight(theOrient);
	}
	/**
	 * Return a string representation of the current orientation
	 */
	public String toString(){
		return theShape.toString(theOrient);
	}

	/**
	 * Print current orientation to stdout
	 */
	public void print(){
		System.out.println(toString());
	}

	/**
	 * The value of the cell at <code>x, y</code> in the piece's current orientation template
	 * @param x
	 * @param y
	 * @return the cell value
	 */
	public int getCell(int x, int y){
		return getCell(this.theShape.getPiece(theOrient), x, y);
	}

	/**
	 * The value of the cell at <code>x, y</code> in a given template <code>theCells</code>
	 * @param theCells
	 * @param x
	 * @param y
	 * @return the value of the cell at <code>x,y</code> in the piece template <code>theCells</code>
	 */
	public int getCell(int[][] theCells, int x, int y){
		return theCells[y][x];
	}

	/**
	 * Move the piece: clearFootPrint(), increment x and/or y, fillFootPrint()
	 * @param dx
	 * @param dy
	 */
	public synchronized void move(int dx, int dy){
		//System.out.println("Piece.move() xPos: "+xPos+" yPos: "+yPos+" dx: "+dx+" dy: "+dy);
		clearFootPrint();
		if (testMove(dx, dy)){ // test if move is valid
			xPos+=dx;
			yPos+=dy;
			gb.repaint();
			gb.checkBoard();
		} else {
			System.out.println("INVALID MOVE");
		}
		fillFootPrint();
	}

	/**
	 * Rotates the piece: clearFootPrint(), increment the  orientation pointer, fillFootPrint()
	 */
	public synchronized void rotate(){
		clearFootPrint();
		if (testRotate()){
			theOrient = nextOrient();
			gb.repaint();
		}
		fillFootPrint();
	}

	/**
	 * Wrapper method to testMove and kill if a down-move is deemed invalid
	 * @param dx
	 * @param dy
	 * @return whether or not the move is feasible (false = failed test = bad move)
	 */
	public synchronized boolean testMove(int dx, int dy){
		return testMove(dx, dy, true);
	}

	/**
	 * Checks destination cells for out-of-bounds, or presence of other blocks. If move is down and invalid, kill the piece if allowed
	 * @param dx
	 * @param dy
	 * @param killer Whether or not to kill pieces if a killable move is tested (down, but invalid)
	 * @return
	 */
	public synchronized boolean testMove(int dx, int dy, boolean killer){
		if (!gb.isPaused()){
			//try {
			//System.out.println("Piece.testMove("+dx+dy+")");
			if (getY()+getLocalHeight()==gb.getUnitHeight() && dy>0){ // if piece is already at the bottom, and move is not horizontal 
				if (killer)
					gb.add(); //kill it ((becomes part of board)
				System.out.println("MOVE TEST FAIL already at bottom");
				return false;
			} else if (getX()+getLocalWidth()+dx>gb.getUnitWidth() || getX()+dx<0 || (getY()+getLocalHeight()+dy>gb.getUnitHeight())){ // check for x out of bounds
				System.out.println("MOVE TEST FAIL out of bounds");
				//System.out.println("X destination "+(xPos+getLocalWidth()+dx)+" > width "+gb.getLocalWidth());
				return false;
			} else { // loop through and check for block conflicts
				int cellX;
				int cellY;
				int nextX;
				int nextY;
				for (int i=0; i<getLocalWidth(); i++)
					for (int j=0; j<getLocalHeight(); j++){
						cellX = getX()+i; //coords of current cell in the piece
						cellY = getY()+j;
						nextX = cellX + dx;
						nextY = cellY + dy;
						//System.out.println("X: "+(getX()+i+dx)+" Y: "+(getY()+j+dy)+" value: "+gb.getCell(getX()+i+dx, getY()+j+dy));

						if (dy==0 && this.isFilled(i,j) && gb.isFilled(nextX, cellY)){
							System.out.println("Pieces in the way of horizontal move");
							return false;
						} else if (dx==0 && this.isFilled(i,j) && gb.isFilled(cellX, nextY)){
							System.out.println("cellx="+cellX+" nexty="+nextY+" this.isFilled"+this.isFilled(i,j)+" gb.isFilled(cellX,nextY)="+gb.isFilled(cellX, nextY)+ " gb.getCell(cellX,nextY)="+gb.getCell(cellX, nextY));
							if (killer) 
								gb.add(); // do you feel lucky, punk?
							System.out.println("Piece in the way of downward move");
							return false;
						}
					}
				return true;
			}
			/*} catch (Exception e) {
			e.printStackTrace();
			System.out.println("MOVE FAILED for SOME STRANGE REASON");
			return false;
		}*/
		} else
			return false; 
	}

	/**
	 * Tests if the piece is rotatable
	 * @return Whether the piece is rotatable
	 */
	public synchronized boolean testRotate(){
		if (!gb.isPaused()){
			//System.out.println("Piece.testRotate() test: "+(getY()+getLocalWidth()>gb.getUnitHeight()-1)+"x:"+getX()+" y:"+getY()+" height:"+getLocalHeight()+" w:"+getLocalWidth()+" gbheight:"+gb.getUnitHeight()+" gbw:"+gb.getUnitWidth());
			if ( (getY()+getLocalWidth()>=gb.getUnitHeight()) || (getX()+getLocalHeight()>=gb.getUnitWidth()) )
				return false;

			// cache the next orientation
			int[][] theCells = theShape.getPiece(nextOrient());
			for (int i=0; i<getLocalHeight(); i++)
				for (int j=0; j<getLocalWidth(); j++){
					if (isFilled(theCells, i, j) && gb.isFilled(getX()+i, getY()+j)){
						return false;
					} else { 
						return true; 
					}
				}
			return true;
		} else
			return false;
	}

	/**
	 * Increment the orientation pointer
	 * @return An int referring to the next orientation
	 */
	public int nextOrient(){
		if (theOrient==3)
			return 0;
		else
			return theOrient+1;
	}

	/**
	 * Test current orientation for a filled cell
	 * @param x
	 * @param y
	 * @return whether a cell is filled
	 */
	public boolean isFilled(int x, int y){
		return getCell(x,y)==Piece.FULLCELL;
	}

	/**
	 * Test a given template (int[][]) for a filled cell
	 * @param theCells
	 * @param x
	 * @param y
	 * @return Whether x,y in template <code>theCells</code> is filled (and inactive)
	 * TODO: change to checking if value is greater than 1 (use for active pieces), to allow for multiple "filled cell" values
	 */
	public boolean isFilled(int[][] theCells, int x, int y){
		return getCell(theCells, x, y)==Piece.FULLCELL;
	}

	/**
	 * Wrapper method to the piece one cell down
	 */
	public synchronized void moveDown(){
		move(0,1);
	}

	/**
	 * Draw the current piece on the board using the appropriate value
	 * @param v
	 */
	public synchronized void fillFootPrint(){
		//System.out.println("Piece.fillFootPrint()");
		for (int i=0; i<getLocalWidth(); i++)
			for (int j=0; j<getLocalHeight(); j++)
				if (getCell(i, j)==FULLCELL)
					if (gb.isFilled(getX()+i, getY()+j)){
						System.err.println("ERROR FILLING CELLS WTF");//gb.endGame();//ENDGAME
						(new Error()).printStackTrace(); //TODO: handle the whole "end of game" thing better!!
					} else
						gb.setCell(getX()+i, getY()+j,getPieceType());
	}

	/**
	 * Erase the piece from the board
	 * TODO: move this to GameBoard?
	 */
	public synchronized void clearFootPrint(){
		//System.out.println("Piece.clearFootPrint()");
		for (int i=0; i<getLocalWidth(); i++)
			for (int j=0; j<getLocalHeight(); j++){
				if (this.isFilled(i, j))
					gb.clearCell(getX()+i, getY()+j);
			}
	}

	/**
	 * Drop the piece as far as possible, awarding 2 points for each level dropped
	 */
	public synchronized int fullDrop() {
		int i=1;
		clearFootPrint();
		while (this.testMove(0,i,false)){ //check for farthest possible move, but don't kill the piece yet
			i++;
		}
		System.out.println("i: "+i);
		i--;
		move(0, i);
		gb.add();
		return 2*i;
	}
}