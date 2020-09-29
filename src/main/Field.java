package main;

public class Field 
{
	private int[][] field;
	private int w, h;
	private int x = 0, y = 0;
	
	public Field(int w, int h) 
	{
		field = new int[w][h];
		this.w = w;
		this.h = h;
	}
	
	public void add(int v)
	{
		if (!(y < h)) { y = 0; x ++; }
		
		if (!(x < w)) return;
		
		field[x][y ++] = v;
	}
	
	public String toString()
	{
		String s = "";
		
		for (int y = 0; y < h; y ++)
		{
			for (int x = 0; x < w; x ++)
			{
				s += field[x][y] + " ";
			}
			
			s += "\n";
		}
		
		return s;
	}
	
	public int getNum(int x, int y)
	{
		return field[x][y];
	}
	
	public void setNum(int x, int y, int val)
	{
		field[x][y] = val;
	}
	
	public boolean find(int i)
	{
		for (int y = 0; y < h; y ++)
		{
			for (int x = 0; x < w; x ++)
			{
				if (getNum(x, y) == i || getNum(x, y) == 11) return true;
			}
		}
		
		return false;
	}
}
