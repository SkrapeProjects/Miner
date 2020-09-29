package main;

public class Executer 
{	
	public void executeClicks(Field f, String s)
	{	
		for (int x = 0; x < Main.FIELD_WIDTH / Main.BLOCK_WIDTH; x ++)
		{
			for (int y = 0; y < Main.FIELD_HEIGHT / Main.BLOCK_HEIGHT; y ++)
			{
				if (f.getNum(x, y) == 12 && s.equalsIgnoreCase("right")) click(Main.FIELD_X + x * Main.BLOCK_WIDTH + Main.BLOCK_WIDTH / 2, Main.FIELD_Y + y * Main.BLOCK_HEIGHT + Main.BLOCK_HEIGHT / 2, "Right");
				if (f.getNum(x, y) == 13 && s.equalsIgnoreCase("left")) click(Main.FIELD_X + x * Main.BLOCK_WIDTH + Main.BLOCK_WIDTH / 2, Main.FIELD_Y + y * Main.BLOCK_HEIGHT + Main.BLOCK_HEIGHT / 2, "Left");
			}
		}
	}
	
	public void click(int x, int y, String s)
	{	
		try {
			
			s = "data/" + s + "Click.exe " + x + " " + y;
			
			Process p = Runtime.getRuntime().exec(s);
			p.waitFor();
			
			p.destroy();
			
		} catch (Exception e) { e.printStackTrace(); }
	}
	
	public void requestFocus()
	{
		try {
			
			Process p = Runtime.getRuntime().exec("cscript data\\Focus.vbs");
			p.waitFor();
			p.destroy();
			
		} catch (Exception e) { e.printStackTrace(); }
	}
}
