package coreGuiRavage;

import org.jsfml.graphics.Drawable;
import org.jsfml.graphics.RenderStates;
import org.jsfml.graphics.RenderTarget;

public abstract class View implements Drawable
{

	@Override
	public abstract void draw(RenderTarget render, RenderStates state); 

}
