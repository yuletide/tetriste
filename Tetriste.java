import java.applet.AudioClip;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;


/**
 * <h1>TETRISTE</h1>
 * A game of the ages, brought to you by Mother Russia, Atari, Nintendo, and anyone who has ever played a video game. This is Tetriste. Like Tetris, but classier! (All copyrights are property of their respective holders and don't sue me or send the KGB, puleeez. kthxbaii)
 * <br/>Written for CS201 for Professor Tim Huang, Homework 8 (final project)
 * <br/>All it doesn't really do at this point is detect game over. Levels should work, each with own background music, point values (for accellerated drop, space-bar drop, and all types of clears, with TETRISTE's garnering the most points (4 full lines)), and drop speed).
 * <br/>The start/stop button does work, though. So if things get gnarly, just hit the button twice to start over.<br/>
 * <br/>HOSTED ONLINE <a href = "http://alexyule.com/tetriste/">HERE</a>: http://alexyule.com/tetriste/<br/>
 * 
 * <h3>Credits</h3>
 * <ul>
 * <li>1931 Soviet Propaganda Poster Image credit:  from the exhibit: <a href="http://news-service.stanford.edu/news/2005/november30/crowds-113005.html">"Revolutionary Tides: The Art of the Political Poster, 1914-1989"</a></li>
 * <li>Soviet font: <a href="http://www.iconian.com/">iconian fonts</a> found at <a href="http://www.dafont.com/soviet.font">dafont</a></li>
 * <li>Colors; various people on <a href="http://colourlovers.com">colourlovers</a> see {@link GameBoard} for details</li>
 * </ul>

 * TODO: v0.9.3 Next Piece Display, High score system.
 * TODO: ? Wrap in JAR
 * 
 * @author Alex Yule
 * @date Dec 6, 2008
 * @version 0.9.2b
 * Changes since v0.9.1b: Added music loader to asynchronously load music for next level to avoid lags in gameplay
 */
@SuppressWarnings("serial")
public class Tetriste extends JApplet implements KeyListener {
	public static void main(String[] args) {
		
	}
	
	/**
	 * The game board
	 */
	protected GameBoard gb = null;
	/**
	 * background music
	 */
	private static AudioClip bgMusic = null;
	protected static AudioClip bgMusicNext = null;
	public static AudioClip bgMusicNext(){ return bgMusicNext; }
	/**
	 * @param bgMusic the bgMusic to set
	 */
	public static void setBgMusic(AudioClip bgMusic) {
		Tetriste.bgMusic = bgMusic;
	}

	/**
	 * @return the bgMusic
	 */
	public static AudioClip getBgMusic() {
		return bgMusic;
	}
	String[] audioPath = {"music/INFLAMES_moonshieldC64.au", "music/tetris1.mid", "music/COB_downfall.mid", "music/tetris2.mid", "music/tetris3.mid", "music/INFLAMES_jestersdance.au",};
	protected MusicLoader mLoader;
	protected JButton playButton;
	protected JButton pauseButton;
	protected Boolean paused = false;
	/**
	 * Label to display score
	 */
	protected JLabel scoreLabel;
	/**
	 * Label to display current level
	 */
	protected JLabel levelLabel;
	/**
	 * Label to display number of cleared lines
	 */
	protected JLabel linesLabel;
	/**
	 * Label to display the game title (or game over)
	 */
	protected JLabel titleLabel;
	/**
	 * A special surprise if you lose
	 */
	protected Image lenin;
	/**
	 * Shortcut to load the image
	 */
	public ImageIcon leninIcon;
	/**
	 * @throws HeadlessException
	 */
	public Tetriste() throws HeadlessException {

		System.out.println("Tetriste.Tetriste()");

		/*
		lPanel.getGraphics().drawRect(4, 4, 100, 50);
		 */
		// TODO Auto-generated constructor stub
	}

	public void init() {
		System.out.println("Tetriste.init()");
		//Execute a job on the event-dispatching thread:
		//creating this applet's GUI.
		try {
			javax.swing.SwingUtilities.invokeAndWait(new Runnable() {
				public void run() {
					System.out.println("Loading background music...");
					setBgMusic(getAudioClip(getCodeBase(),"music/INFLAMES_moonshieldC64.au"));
					//jestersdance = getAudioClip(getCodeBase(), jestersdancepath);
					createGUI();
					System.out.println("createGUI complete");


				}
			});
		} catch (Exception e) {
			System.err.println("createGUI didn't successfully complete");
			e.printStackTrace();
		}
		System.out.println("LOADING LENIN!");
		ImageIcon leninIcon = new ImageIcon(Tetriste.class.getResource("images/poster_lenin.jpg"));
		lenin = leninIcon.getImage();
		mLoader = new MusicLoader();

	}

	/**
	 * Cleanup on close, hopefully stops the music from continuing after applet termination
	 */
	public void destroy(){
		getBgMusic().stop();
	}



	/**
	 * Create the GUI...
	 */
	private void createGUI() {
		System.out.println("Creating GUI...");

		// Panels
		JPanel contentPanel = new JPanel();
		gb = new GameBoard(this);
		gb.setBorder(BorderFactory.createLineBorder(Color.black));

		JPanel rPanel = new JPanel();

		// setup box layout-managers
		contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.LINE_AXIS));
		rPanel.setLayout(new BoxLayout(rPanel, BoxLayout.PAGE_AXIS));
		JPanel labelPanel = new JPanel();
		labelPanel.setLayout(new BoxLayout(labelPanel, BoxLayout.PAGE_AXIS));

		rPanel.setBackground(new Color(120,113,70)/*mud*/);
		contentPanel.setBackground(new Color(120,113,70/*mud*/));

		// Button Listener
		ButtonListener bl = new ButtonListener();

		// Buttons
		playButton = new JButton("Play");
		pauseButton = new JButton("Pause");
		playButton.addActionListener(bl);
		pauseButton.addActionListener(bl);
		playButton.setHorizontalAlignment(SwingConstants.RIGHT);
		pauseButton.setHorizontalAlignment(SwingConstants.RIGHT);
		playButton.setFocusable(false);
		pauseButton.setFocusable(false);
		// Fonts
		Font fancyI = new Font("Garamond",Font.BOLD, 12);
		Font titleFont = new Font("Garamond", Font.BOLD, 26);

		try {
			titleFont = Font.createFont(Font.TRUETYPE_FONT, Tetriste.class.getResourceAsStream("fonts/Soviet2be.ttf")).deriveFont((float)60);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (FontFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Color labelText = new Color(175,223,229);//102,12,12);
		// Labels
		titleLabel = new JLabel("Tetriste", SwingConstants.LEFT);
		titleLabel.setForeground(new Color(102,12,12)); // lucy in the sky
		titleLabel.setBackground(new Color(120,113,70)); // mud
		titleLabel.setFont(titleFont);
		titleLabel.setOpaque(true);
		JLabel author = new JLabel("by Alex Yule", SwingConstants.RIGHT);
		author.setFont(fancyI); // set to fancy font!
		//for (String font:GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames())
		//System.out.println(font);
		if (author.getFont().getFontName()!="Garamond"){ // if running a system without Garamond (i.e. lab 632 machines) use Bitstream Vera Serif instead
			System.out.println("Lame font");
			author.setFont(new Font("Bitstream Vera Serif", Font.BOLD, 12));
		}
		scoreLabel = new JLabel("Score: 0");
		levelLabel = new JLabel("Level: 0");
		linesLabel = new JLabel("Lines: 0");

		levelLabel.setFont(new Font("Dialog", Font.BOLD, 16));

		levelLabel.setForeground(labelText);		
		linesLabel.setForeground(labelText);
		scoreLabel.setForeground(labelText);

		// Color panels for debugging
		/*
		contentPanel.setBackground(Color.MAGENTA);
		rPanel.setBackground(Color.GREEN);
		lPanel.setBackground(Color.RED);
		gb.setBackground(Color.CYAN);
		 */

		labelPanel.setOpaque(false);
		// Add components to panels
		rPanel.add(Box.createVerticalStrut(20));
		labelPanel.add(levelLabel);
		labelPanel.add(Box.createVerticalStrut(3));
		labelPanel.add(scoreLabel);
		labelPanel.add(Box.createVerticalStrut(3));
		labelPanel.add(linesLabel);
		rPanel.add(labelPanel);
		rPanel.add(Box.createVerticalGlue());

		rPanel.add(playButton);
		rPanel.add(Box.createVerticalStrut(5));
		rPanel.add(pauseButton);
		rPanel.add(Box.createVerticalStrut(15));
		rPanel.add(author);
		contentPanel.add(Box.createHorizontalStrut(25));
		contentPanel.add(gb);
		contentPanel.add(Box.createHorizontalStrut(10));
		contentPanel.add(rPanel);
		// Add panels to applet
		add(titleLabel, BorderLayout.NORTH);
		add(contentPanel);
		//add(gb, BorderLayout.WEST);
	}

	/**
	 * update the score label
	 * @param points
	 */
	public void setScore(int points){
		scoreLabel.setText("Score: "+points);
	}


	public void setTitle(String title){
		titleLabel.setText(title);
	}

	public void setTitle(){
		setTitle("Tetriste");
	}

	/**
	 * Update the level label
	 * @param level
	 */
	public void setLevel(int level){
		levelLabel.setText("Level: "+level);
	}

	public void setLines(int lines){
		linesLabel.setText("Lines: "+lines);
	}

	/**
	 * Set the music for the current level
	 * @param level
	 */
	public void setMusic(int level){
		System.out.println("Tetriste.setMusic("+level+")");
		if (level == 1)
			setBgMusic(getAudioClip(getCodeBase(), audioPath[0]));
		if (level >1){
			(new Thread(mLoader)).start();
		}
	}

	/**
	 * Start playing the game, initialize the gameboard and call the {@link GameBoard}'s startGame method
	 */
	public void startGame(){
		setTitle();
		setScore(0);
		setLevel(1);
		setMusic(1);
		gb.initBoard();
		gb.setFocusable(true);
		try {
			addKeyListener(this);
			gb.startGame();
			requestFocusInWindow();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}



	/**
	 * Start or stop the game (called by the play/stop button)
	 */
	public void playToggle(){
		if (!gb.isPlaying()){
			playButton.setText("Stop");
			startGame();
		} else {
			playButton.setText("Play");
			pauseButton.setText("Pause");
			gb.endGame();
			removeKeyListener(this);
		}
		this.validate();
	}

	/**
	 * Pause/unpause the game (called by pause button)
	 */
	public void pauseToggle(){
		System.out.println("Tetriste.pauseToggle() "+gb.isPlaying()+" "+gb.isPaused());
		if (gb.isPlaying()){
			if (gb.pauseToggle())
				pauseButton.setText("Return");
			else
				pauseButton.setText("Pause");
		}
	}

	public void keyPressed(KeyEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_LEFT:
			System.out.println("LEFT");
			gb.thePiece.move(-1, 0);
			break;
		case KeyEvent.VK_RIGHT:
			System.out.println("RIGHT");
			gb.thePiece.move(1, 0);
			break;
		case KeyEvent.VK_UP:
			System.out.println("UP");
			gb.rotatePiece();
			break;
		case KeyEvent.VK_DOWN:
			System.out.println("DOWN");
			gb.fastDrop();
			break;
		case KeyEvent.VK_SPACE:
			System.out.println("SPACE");
			gb.fullDrop();
			break;
		case KeyEvent.VK_M:
			System.out.println("M");
			setMusic(gb.getLevel()+1);
			break;
		case KeyEvent.VK_L:
			System.out.println("5");
			gb.nextLevel();
		default:
			break;
		}

	}

	/* (non-Javadoc)
	 * @see java.awt.event.KeyListener#keyReleased(java.awt.event.KeyEvent)
	 */
	public void keyReleased(KeyEvent e) {
	}

	/* (non-Javadoc)
	 * @see java.awt.event.KeyListener#keyReleased(java.awt.event.KeyEvent)
	 */
	public void keyTyped(KeyEvent e) {

	}


	/**
	 * A ButtonListener to handle the button events
	 * @author Alex Yule
	 * @date Dec 5, 2008
	 *
	 */
	class ButtonListener implements ActionListener {

		/* (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			if (e.getSource()==playButton)
				playToggle();
			else if (e.getSource()==pauseButton)
				pauseToggle();
		}
	}

	/**
	 * A runnable class to asynchronously load music for the following level
	 * @author Alex Yule
	 * @date Dec 7, 2008
	 */
	class MusicLoader implements Runnable {
		protected JApplet tetriste;
		/* (non-Javadoc)
		 * @see java.lang.Runnable#run()
		 */
		public void run() {
			System.out.println("Loading next music...");
			bgMusicNext = newAudioClip(Tetriste.class.getResource(audioPath[(gb.getLevel()-1)%audioPath.length]));
			getBgMusic().stop();
			setBgMusic(bgMusicNext());
			getBgMusic().loop();
			System.out.println("Next BGMusic loaded...");
		}
	}
}
