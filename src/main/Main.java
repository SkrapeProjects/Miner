package main;

import java.awt.AWTException;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;

public class Main extends Canvas implements Runnable, MouseListener
{
	private static final long serialVersionUID = 3613520433568203849L;
	
	private Thread thread;
	private Robot rob;
	private Process p;
	private Reader reader;
	private Field field;
	private Solver solver;
	private Executer exer;
	private Window win;
	private Window imgDisplay;
	private Renderer renderer;
	
	public final int W = 164 , H = 369;
	
	public static int BLOCK_WIDTH = 16, BLOCK_HEIGHT = 16;
	public static int FIELD_WIDTH = BLOCK_WIDTH * 9, FIELD_HEIGHT = BLOCK_HEIGHT * 9;
	public static int FIELD_X = 89, FIELD_Y = 194;
	public static int FACE_X = 0, FACE_Y = 0;
	
	public static boolean execute = false;
	public static boolean launched = true;
	public static boolean stop = true;
	public static int STATUS = 1;
	
	public String selector = "X";
	
	public Main()
	{
		init();
	}
	
	public static void main(String[] args) 
	{	
		new Main();
	}

	@Override
	public void run() 
	{
		loop();
	}
	
	private void init()
	{
		loadConfiguration();
		
		thread = new Thread(this, "MAIN-THREAD");
		
		try { rob = new Robot(); } catch (AWTException e) { e.printStackTrace(); }
		
		try { p = Runtime.getRuntime().exec("data/Minesweeper.exe"); } catch (IOException e) { e.printStackTrace(); }
		
		reader = new Reader();
		
		solver = new Solver();
		
		exer = new Executer();
		
		this.addMouseListener(this);
		
		win = new Window("Miner", W, H, this);
		
		renderer = new Renderer(this);
		
		win.setPosition(800, 10);
		
		imgDisplay = new Window("RobotView", 366, 220, renderer);
		imgDisplay.setPosition(800 + W + 10, 10);
		
		start();
	}
	
	private synchronized void start()
	{
		if (System.getProperty("os.name").contains("Mac")) thread.run();
		else thread.start();
		
		renderer.start(imgDisplay);
	}
	
	private synchronized void stop()
	{
		try {
			
			p.destroyForcibly();
			
			saveConfiguration();
			
			System.exit(0);
			
			renderer.stop();
			
			thread.join();
			
		} catch (Exception e) { e.printStackTrace(); }
	}
	
	private void render()
	{
		BufferStrategy bs = getBufferStrategy();
		
		if (bs == null)
		{
			createBufferStrategy(3);
			return;
		}
		
		Graphics g = bs.getDrawGraphics();
		
		// RENDER
		
		g.setColor(Color.black);
		g.fillRect(0, 0, W, H);
		
		g.setColor(Color.WHITE);
		
		g.drawRect(32, 10, 100, 50);
		g.setFont(new Font("Arial", Font.BOLD, 26));
		g.drawString("START", 39, 44);
		
		g.drawRect(32, 70, 100, 50);
		g.setFont(new Font("Arial", Font.BOLD, 30));
		g.drawString("NEXT", 43, 105);
		
		g.drawImage(getFace(), 70, 130, null);
		
		g.drawRect(87, 164, 45, 45);
		g.setFont(new Font("Arial", Font.BOLD, 40));
		g.drawString("+", 100, 200);
		
		g.drawRect(32, 164, 45, 45);
		g.setFont(new Font("Arial", Font.BOLD, 38));
		g.drawString("_", 45, 181);
		
		g.drawRect(32, 219, 100, 50);
		g.setFont(new Font("Arial", Font.BOLD, 35));
		g.drawString(selector, 71, 257);
		
		if (Main.stop) g.setColor(Color.GREEN);
		else g.setColor(Color.RED);
		
		g.drawRect(32, 279, 100, 50);
		g.setFont(new Font("Arial", Font.BOLD, 30));
		g.drawString("STOP", 43, 315);
		
		// RENDER

		g.dispose();
		bs.show();
	}
	
	private void update()
	{	
		if (!launched)
		{	
			Random r = new Random();
				
			execute = true;
				
			exer.click(r.nextInt(Main.FIELD_WIDTH / Main.BLOCK_WIDTH) * Main.BLOCK_WIDTH + Main.BLOCK_WIDTH / 2 + Main.FIELD_X, r.nextInt(Main.FIELD_HEIGHT / Main.BLOCK_HEIGHT) * Main.BLOCK_HEIGHT + Main.BLOCK_HEIGHT / 2 + Main.FIELD_Y, "Left");
			
			if (Main.stop) execute = false;
			
			STATUS = 0;
		}
		
		field = reader.read(getScreenCapture(), field);
		
		if (field.find(0)) launched = true;
		
		if (launched && STATUS == 0) STATUS = reader.analyzeFace(getFace());
		
		if (STATUS == 2) startGame();
		
		if (execute && STATUS == 0 && launched) solver.solve(exer, field);
	}
	
	private void loop()
	{
		while (win.isOpen()) 
		{	
			update();
			render();
		}
		
		stop();
	}
	
	public BufferedImage getScreenCapture()
	{
		return rob.createScreenCapture(new Rectangle(FIELD_X, FIELD_Y, FIELD_WIDTH, FIELD_HEIGHT));
	}
	
	public BufferedImage getFace()
	{
		return rob.createScreenCapture(new Rectangle((FIELD_X + (FIELD_WIDTH / 2) - 12) + FACE_X, FIELD_Y + FACE_Y, 24, 24));
	}
	
	@Override
	public void mouseClicked(MouseEvent e) 
	{
		if (e.getX() > 32 && e.getX() < 132 && e.getY() > 70 && e.getY() < 120)
		{
			execute = true;
		}
		
		if (e.getX() > 32 && e.getX() < 132 && e.getY() > 279 && e.getY() < 329)
		{
			if (stop) stop = false;
			else stop = true;
		}
		
		if (e.getX() > 32 && e.getX() < 132 && e.getY() > 10 && e.getY() < 60)
		{
			startGame();
		}
		
		if (e.getX() > 32 && e.getX() < 132 && e.getY() > 219 && e.getY() < 269)
		{
			if (selector == "X") selector = "Y";
			else if (selector == "Y") selector = "W";
			else if (selector == "W") selector = "H";
			else if (selector == "H") selector = ">";
			else if (selector == ">") selector = "^";
			else selector = "X";
		}
	}
	
	private void saveConfiguration()
	{
		try {
			
			PrintWriter w = new PrintWriter(new FileWriter(new File("data/Configuration.cfg")));
			
			w.println(FIELD_X);
			w.println(FIELD_Y);
			w.println(FIELD_WIDTH / BLOCK_WIDTH);
			w.println(FIELD_HEIGHT / BLOCK_HEIGHT);
			w.println(FACE_X);
			w.println(FACE_Y);
			
			w.close();
			
		} catch (IOException e) { e.printStackTrace(); }
	}
	
	private void loadConfiguration()
	{
		try {
			
			BufferedReader r = new BufferedReader(new FileReader(new File("data/Configuration.cfg")));
			
			FIELD_X = Integer.parseInt(r.readLine());
			FIELD_Y = Integer.parseInt(r.readLine());
			FIELD_WIDTH = Integer.parseInt(r.readLine()) * BLOCK_WIDTH;
			FIELD_HEIGHT = Integer.parseInt(r.readLine()) * BLOCK_HEIGHT;
			FACE_X = Integer.parseInt(r.readLine());
			FACE_Y = Integer.parseInt(r.readLine());
			
			r.close();
			
		} catch (IOException e) { e.printStackTrace(); ;}
	}
	
	private void startGame()
	{
		exer.requestFocus();
		exer.click((FIELD_X + (FIELD_WIDTH / 2) - 12) + FACE_X + 12, FIELD_Y + FACE_Y + 12, "left");

		launched = false;
	}
	
	public Dimension getScaledDimension(Dimension imageSize, Dimension boundary) 
	{
	    double widthRatio = boundary.getWidth() / imageSize.getWidth();
	    double heightRatio = boundary.getHeight() / imageSize.getHeight();
	    double ratio = Math.min(widthRatio, heightRatio);

	    return new Dimension((int) (imageSize.width * ratio), (int) (imageSize.height * ratio));
	}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}

	@Override
	public void mousePressed(MouseEvent e) 
	{
		if (e.getX() > 87 && e.getX() < 132 && e.getY() > 164 && e.getY() < 209)
		{
			if (selector == "X") FIELD_X ++;
			else if (selector == "Y") FIELD_Y ++;
			else if (selector == "W") FIELD_WIDTH += BLOCK_WIDTH;
			else if (selector == "H") FIELD_HEIGHT += BLOCK_HEIGHT;
			else if (selector == ">") FACE_X ++;
			else if (selector == "^") FACE_Y ++;
		}
		
		if (e.getX() > 32 && e.getX() < 77 && e.getY() > 164 && e.getY() < 209)
		{
			if (selector == "X") FIELD_X --;
			else if (selector == "Y") FIELD_Y --;
			else if (selector == "W") FIELD_WIDTH -= BLOCK_WIDTH;
			else if (selector == "H") FIELD_HEIGHT -= BLOCK_HEIGHT;
			else if (selector == ">") FACE_X --;
			else if (selector == "^") FACE_Y --;
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {}
}
