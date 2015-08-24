package coreGuiRavage;

import java.util.ArrayList;
import java.util.List;

import org.jsfml.graphics.Drawable;
import org.jsfml.graphics.RenderStates;
import org.jsfml.graphics.RenderTarget;
import org.jsfml.system.Time;
import org.jsfml.window.event.KeyEvent;
import org.jsfml.window.event.MouseButtonEvent;
import org.jsfml.window.event.MouseEvent;

import ravage.IBaseRavage;
import coreEvent.IEventCallBack;

public class GuiRavageManager implements IBaseRavage,IEventCallBack, Drawable 
{

	private List<Panel> m_panels;
	
	public GuiRavageManager()
	{
		m_panels = new ArrayList<Panel>();
	}
	
	public void addPanel(Panel p)
	{
		m_panels.add(p);
	}
	
	
	
	@Override
	public void draw(RenderTarget render, RenderStates state) 
	{
		for(Panel p : m_panels)
			p.m_view.draw(render, state);
	}

	@Override
	public boolean onMouse(MouseEvent buttonEvent) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onKeyboard(KeyEvent keyboardEvent) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onMouseMove(MouseEvent event) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onMousePressed(MouseButtonEvent event) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onMouseReleased(MouseButtonEvent event) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void update(Time deltaTime) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}

}