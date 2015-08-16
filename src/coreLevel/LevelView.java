package coreLevel;

import java.util.ArrayList;
import java.util.List;

import org.jsfml.graphics.Drawable;
import org.jsfml.graphics.FloatRect;
import org.jsfml.graphics.RenderStates;
import org.jsfml.graphics.RenderTarget;
import org.jsfml.graphics.Sprite;

import coreCamera.CameraManager;

public class LevelView implements Drawable
{
	// Textures de background
	private List<Sprite> backgrounds;
	// Textures de foreground - Arbres et toits
	private List<Sprite> foregrounds;
	
	
	
	public LevelView()
	{
		super();
		// instances du background
		backgrounds = new ArrayList<Sprite>();
		// instance de foreground
		foregrounds = new ArrayList<Sprite>();
	}



	public List<Sprite> getBackgrounds() {
		return backgrounds;
	}



	public List<Sprite> getForegrounds() {
		return foregrounds;
	}

	public void drawBackground(RenderTarget render,RenderStates state)
	{
		// on affiche le background du level
		
				for(Sprite s : backgrounds)
				{
					FloatRect result = s.getGlobalBounds().intersection(CameraManager.getCameraBounds());
					if(result != null)
						render.draw(s);
				}
	}
	
	public void drawForeground(RenderTarget render,RenderStates state)
	{
		// on affiche le foreground
				for(Sprite f : foregrounds)
				{
					FloatRect result = f.getGlobalBounds().intersection(CameraManager.getCameraBounds());
					if(result != null)
						render.draw(f);
				}
	}

	@Override
	public void draw(RenderTarget render, RenderStates state)
	{
		for(Sprite s : backgrounds)
		{
			FloatRect result = s.getGlobalBounds().intersection(CameraManager.getCameraBounds());
			if(result != null)
				render.draw(s);
		}
		
		// on affiche le foreground
		for(Sprite f : foregrounds)
		{
			FloatRect result = f.getGlobalBounds().intersection(CameraManager.getCameraBounds());
			if(result != null)
				render.draw(f);
		}
		
	}

}
