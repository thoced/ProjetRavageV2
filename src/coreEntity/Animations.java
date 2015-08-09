package coreEntity;

import org.jsfml.graphics.IntRect;

public class Animations 
{
	private IntRect[] animations;

	public IntRect[] getAnimations() {
		return animations;
	}
	
	
	public void makeAnimation(int totalFrame)
	{
		animations = new IntRect[totalFrame];
	}
	
	public  IntRect getInd(int i)
	{
		return animations[i];
	}
	
	
}