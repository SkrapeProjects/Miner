package main;

import java.util.ArrayList;
import java.util.List;

public class Packet 
{
	private int[] info = new int[3];
	private List<Block> blocks;
	private List<Block> numbers;
	private boolean flagged = false;
	private Field f;
	
	public Packet(int x, int y, Field f) 
	{
		info[0] = f.getNum(x, y);
		info[1] = x;
		info[2] = y;
		
		this.f = f;
		
		blocks = new ArrayList<Block>();
		numbers = new ArrayList<Block>();
		
		updateBlocksArray();
	}
	
	private void updateBlocksArray()
	{
		List<Block> surroundingBlocks = findSurroundingBlocks();
		
		for (Block b : surroundingBlocks)
		{
			if (f.getNum(b.x, b.y) >= 9) blocks.add(b);
			else numbers.add(b);
		}
	}
	
	private List<Block> findSurroundingBlocks()
	{	
		List<Block> toReturn = new ArrayList<Block>();
		
		int x = info[1], y = info[2];
		
		int[] surroundings = new int[8];

		if (x > 0) surroundings[7] = f.getNum(x - 1, y);
		if (x < (Main.FIELD_WIDTH / Main.BLOCK_WIDTH) - 1) surroundings[3] = f.getNum(x + 1, y);
		
		if (y > 0) surroundings[1] = f.getNum(x, y - 1);
		if (y < (Main.FIELD_HEIGHT / Main.BLOCK_HEIGHT) - 1) surroundings[5] = f.getNum(x, y + 1);
		
		if (y > 0 && x > 0) surroundings[0] = f.getNum(x - 1, y - 1);
		if (y > 0 && x < (Main.FIELD_WIDTH / Main.BLOCK_WIDTH) - 1) surroundings[2] = f.getNum(x + 1, y - 1);
		if (y < (Main.FIELD_HEIGHT / Main.BLOCK_HEIGHT) - 1 && x < (Main.FIELD_WIDTH / Main.BLOCK_WIDTH) - 1) surroundings[4] = f.getNum(x + 1, y + 1);
		if (y < (Main.FIELD_HEIGHT / Main.BLOCK_HEIGHT) - 1 && x > 0) surroundings[6] = f.getNum(x - 1, y + 1);
		
		for (int i = 0; i < surroundings.length; i ++)
		{
			if (surroundings[i] > 0 && surroundings[i] <= 10)
			{
				if (i == 0) toReturn.add(new Block(x - 1, y - 1));
				else if (i == 1) toReturn.add(new Block(x, y - 1));
				else if (i == 2) toReturn.add(new Block(x + 1, y - 1));
				else if (i == 3) toReturn.add(new Block(x + 1, y));
				else if (i == 4) toReturn.add(new Block(x + 1, y + 1));
				else if (i == 5) toReturn.add(new Block(x, y + 1));
				else if (i == 6) toReturn.add(new Block(x - 1, y + 1));
				else if (i == 7) toReturn.add(new Block(x - 1, y));
			}
		}
		
		return toReturn;
	}
	
	public void flagBlocks(Field f)
	{
		Block[] toFlag = new Block[8];
		
		int i = 0;
		
		for (Block b : blocks)
		{
			if (f.getNum(b.x, b.y) < 10) toFlag[i ++] = b;
		}
		
		for (Block b : toFlag)
		{
			if (b != null) f.setNum(b.x, b.y, 12);
		}
		
		flagged = true;
	}
	
	public void uncoverBlocks(Field f)
	{
		int flags = 0, i = 0;
		
		Block[] toUncover = new Block[8];
		
		for (Block b : blocks)
		{
			if (f.getNum(b.x, b.y) == 10 || f.getNum(b.x, b.y) == 12) flags ++;
			else if (f.getNum(b.x, b.y) < 10) toUncover[i ++] = b;
		}
		
		if (flags == info[0])
		{
			for (Block b : toUncover)
			{
				if (b != null) f.setNum(b.x, b.y, 13);
			}
		}
	}
	
	public int[] getPosInArray()
	{
		return new int[] { info[1], info[2] };
	}
	
	public int getNumberOfMines()
	{
		return info[0];
	}
	
	public boolean isFlagged()
	{
		return flagged;
	}
	
	public List<Block> getSurroundingBlocks()
	{
		return blocks;
	}
	
	public List<Block> getSurroundingNumbers()
	{
		return numbers;
	}
	
	public float getRatio()
	{	
		return ((float) info[0] / (float) blocks.size());
	}
	
	public float getBlocksOnlyRatio()
	{	
		List<Block> blocks = new ArrayList<Block>();
		
		for (Block b : this.blocks)
		{
			if (f.getNum(b.x, b.y) <= 9)
			{
				blocks.add(b);
			}
		}
		
		return ((float) info[0] / (float) blocks.size());
	}
}
