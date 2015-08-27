package coreGuiRavage;

import org.jsfml.system.Vector2f;
import org.jsfml.window.Mouse;
import org.jsfml.window.event.KeyEvent;
import org.jsfml.window.event.MouseButtonEvent;
import org.jsfml.window.event.MouseEvent;

import coreEvent.IEventCallBack;
import coreEvent.IEventInterfaceCallBack;

public class Gui 
{
	protected Model m_model;
	
	protected View m_view;
	

	public Model getM_model() {
		return m_model;
	}

	public View getM_view() {
		return m_view;
	}

	public void setM_model(Model m_model) {
		this.m_model = m_model;
	}

	public void setM_view(View m_view) {
		this.m_view = m_view;
	}


	public boolean onMouse(Vector2f position, Mouse.Button mouseType) {
		// TODO Auto-generated method stub
		return false;
	}

	
	public boolean onKeyboard(KeyEvent keyboardEvent) {
		// TODO Auto-generated method stub
		return false;
	}


	public boolean onMouseMove(Vector2f position) 
	{
		// TODO Auto-generated method stub
		return false;
	}

	
	public boolean onMousePressed(Vector2f position, Mouse.Button mouseType) {
		// TODO Auto-generated method stub
		return false;
	}

	
	public boolean onMouseReleased(Vector2f position, Mouse.Button mouseType) {
		// TODO Auto-generated method stub
		return false;
	}


}
