package main;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Solver 
{
	private int prevFlagged = 0;
	
	public void solve(Executer ex, Field f)
	{
		if (Main.stop) ex.requestFocus();
		
		List<Packet> packets = createPackets(f);
		
		Packet[] sortedPackets = flag(f, packets);
		
		ex.executeClicks(f, "right");
		
		uncover(f, sortedPackets);
		
		ex.executeClicks(f, "left");
		
		if (Main.stop) Main.execute = false;
	}
	
	private Packet[] flag(Field f, List<Packet> packets)
	{
		Packet[] sortedPackets = sortPackets(packets);
		
		int flagged = 0;
		
		for (Packet p : sortedPackets)
		{
			if (p.getRatio() == 1 && !p.isFlagged()) 
			{
				p.flagBlocks(f);
				flagged ++;
			}
		}
		
		if (flagged == 0 || prevFlagged == flagged) randomClick(sortedPackets, f);
		
		prevFlagged = flagged;
		
		return sortedPackets;
	}
	
	private void randomClick(Packet[] packets, Field f)
	{
		if (packets.length < 1) return;
		
		Random r = new Random();
		
		List<Block> blocks = new ArrayList<Block>();
		
		Packet packet = packets[0];
		
		int prevLowestBlocks = 9;
		int prevHighestRatio = 0;
		
			for (Packet p : packets)
		{
			int blocksFound = 0;
			
			for (Block b : p.getSurroundingBlocks())
			{
				if (f.getNum(b.x, b.y) <= 9) blocksFound ++;
			}
			
			if (blocksFound > 0 && blocksFound <= prevLowestBlocks && p.getBlocksOnlyRatio() > prevHighestRatio)
			{
				packet = p;
			}
		}
		
		for (Block b : packet.getSurroundingBlocks())
		{
			if (f.getNum(b.x, b.y) <= 9) blocks.add(b);
		}
		
		if (blocks.size() < 1) return;
		
		Block b = blocks.get(r.nextInt(blocks.size()));
		
		f.setNum(b.x, b.y, 13);
	}
	
	private void uncover(Field f, Packet[] packets)
	{
		for (Packet p : packets)
		{
			p.uncoverBlocks(f);
		}
	}

	private List<Packet> createPackets(Field f)
	{
		List<Packet> packets = new ArrayList<Packet>();
		
		for (int x = 0; x < Main.FIELD_WIDTH / Main.BLOCK_WIDTH; x ++)
		{
			for (int y = 0; y < Main.FIELD_HEIGHT / Main.BLOCK_HEIGHT; y ++)
			{
				int b = f.getNum(x, y);
				
				if (b > 0 && b < 9)
				{
					packets.add(new Packet(x, y, f));
				}
			}
		}
		
		return packets;
	}
	
	private Packet[] sortPackets(List<Packet> packets)
	{
		Packet[] out = new Packet[packets.size()];
		
		for (int i = 0; i < out.length; i ++)
		{
			Packet highestRatio = packets.get(0);
			
			for (int j = 0; j < packets.size(); j ++)
			{
				if (highestRatio.getRatio() <= packets.get(j).getRatio()) highestRatio = packets.get(j);
			}
			
			out[i] = highestRatio;
			
			packets.remove(highestRatio);
		}
		
		return out;
	}
}
