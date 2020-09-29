package main;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;

public class Renderer extends Canvas implements Runnable
{
	private static final long serialVersionUID = 1244977893925737685L;
	
	private Thread thread;
	private Main main;
	private Window win;
	
	private boolean isRunning = false;
	
	public Renderer(Main m) 
	{
		main = m;
		thread = new Thread(this, "RENDER-THREAD");
	}
	
	public synchronized void start(Window win)
	{
		isRunning = true;
		
		this.win = win;
		
		if (System.getProperty("os.name").contains("Mac")) thread.run();
		else thread.start();
	}
	
	public synchronized void stop() throws Exception
	{
		isRunning = false;
		
		win.dispose();
		
		thread.join();
	}

	@Override
	public void run() 
	{
		renderLoop();
	}
	
	private void renderLoop()
	{
		while (isRunning)
		{
			BufferStrategy bs = this.getBufferStrategy();
			
			if (bs == null)
			{
				this.createBufferStrategy(3);
				continue;
			}
			
			Graphics g = bs.getDrawGraphics();
			
			g.setColor(Color.BLACK);
			g.fillRect(0, 0, 366, 220);
			
			BufferedImage fieldImg = main.getScreenCapture();
			
			Dimension d = main.getScaledDimension(new Dimension(fieldImg.getWidth(), fieldImg.getHeight()), new Dimension(366 - win.getBordersWidth(), 220 - win.getBordersHeight()));
			
			g.drawImage(fieldImg.getScaledInstance(d.width, d.height, BufferedImage.SCALE_FAST), (366 - win.getBordersWidth()) / 2 - d.width / 2, (220 - win.getBordersHeight()) / 2 - d.height / 2, null);
		
			g.dispose();
			bs.show();
		}
	}
}
