package main;

import java.awt.Canvas;
import java.awt.Component;

import javax.swing.JFrame;

public class Window extends Canvas
{
	private static final long serialVersionUID = -4810618286807932601L;
	
	private JFrame frame;
	
	public Window(String title, int w, int h, Component comp)
	{
		frame = new JFrame(title);
		
		frame.setSize(w, h);
		frame.setLocation(500, 10);
		
		frame.add(comp);
		
		frame.setResizable(false);
		frame.setVisible(true);
		
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	}
	
	public boolean isOpen()
	{
		return frame.isDisplayable();
	}
	
	public void dispose()
	{
		frame.dispose();
	}
	
	public void setPosition(int x, int y)
	{
		frame.setLocation(x, y);
	}
	
	public int getBordersWidth()
	{
		return frame.getInsets().left + frame.getInsets().right;
	}
	
	public int getBordersHeight()
	{
		return frame.getInsets().top + frame.getInsets().bottom;
	}
}
