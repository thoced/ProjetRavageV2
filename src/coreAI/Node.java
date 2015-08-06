package coreAI;

import org.jbox2d.common.Vec2;

import coreEntity.Unity;

public class Node 
{
	private int type; // wall, ground, ...
	
	private boolean isDiagonal;
	
	private float x,y;
	
	private Unity unityBusy;
	
	public Node(float x,float y,boolean isdiagonal)
	{
		this.x = x;
		this.y = y;
		this.isDiagonal  = isdiagonal;
		unityBusy = null;
	}
	
	public Vec2 getPositionVec2()
	{
		return new Vec2(this.x,this.y);
	}
	
	public void reset(boolean diagonal)
	{
	
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getType() {
		return type;
	}

	public Unity takeNode(Unity u)
	{
		if(unityBusy == null)
		{
			return unityBusy = u;
			
		}
		else
			return unityBusy; 
			
	}
	
	public void leaveNode()
	{
		unityBusy = null;
	}
	
	
	
}
