package main;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

public class Reader 
{
	public Field read(BufferedImage img, Field f)
	{
		BufferedImage[] blocks = splitIntoBlocks(img, Main.BLOCK_WIDTH, Main.BLOCK_HEIGHT);
		
		f = new Field(img.getWidth() / Main.BLOCK_WIDTH, img.getHeight() / Main.BLOCK_HEIGHT);
		
		for (int i = 0; i < blocks.length; i ++)
		{
			BufferedImage tmp = blocks[i];
			
			Map m = new Map();
			
			for (int x = 0; x < tmp.getWidth(); x ++)
			{
				for (int y = 0; y < tmp.getHeight(); y ++)
				{
					int clr = tmp.getRGB(x, y);
					
					int r = (clr >> 16) & 0xff;
					int g = (clr >> 8) & 0xff;
					int b = (clr) & 0xff;
					
					if ((r == 192 && g == 192 && b == 192) || (r == 128 && g == 128 && b == 128)) m.add(0);
					else m.add(1);
				}
			}
			
			f.add(m.getNumber());
		}
		
		return f;
	}
	
	private BufferedImage convertToBW(BufferedImage img)
	{
		BufferedImage out = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_BYTE_BINARY);
		
		Graphics g = out.createGraphics();
		
		g.drawImage(img, 0, 0, Color.WHITE, null);
		
		g.dispose();
		
		return out;
	}
	
	public int analyzeFace(BufferedImage img)
	{
		int blackSpots = 0;
		
		img = convertToBW(img);
		
		for (int x = 0; x < img.getWidth(); x ++)
		{
			for (int y = 0; y < img.getHeight(); y ++)
			{
				int clr = img.getRGB(x, y);
				
				int r = (clr >> 16) & 0xff;
				int g = (clr >> 8) & 0xff;
				int b = (clr) & 0xff;
				
				if (r == 0 && g == 0 && b == 0) blackSpots ++;
			}
		}
		
		if (blackSpots == 61) return 0;
		else if (blackSpots == 88) return 1;
		else return 2;
	}
	
	private BufferedImage[] splitIntoBlocks(BufferedImage img, int w, int h)
	{
		int x = (img.getWidth() / w);
		int y = (img.getHeight() / h);
		int size = x * y;
		
		BufferedImage[] blocks = new BufferedImage[size];
		
		int i = 0;
		
		for (int xx = 0; xx < x; xx ++)
		{
			for (int yy = 0; yy < y; yy ++)
			{
				blocks[i ++] = img.getSubimage(xx * w, yy * h, w, h);
			}
		}
		
		return blocks;
	}
}
