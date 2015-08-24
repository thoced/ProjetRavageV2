package coreGuiRavage;

import org.jsfml.window.event.KeyEvent;
import org.jsfml.window.event.MouseButtonEvent;
import org.jsfml.window.event.MouseEvent;

import coreEvent.IEventCallBack;

public class Gui implements IEventCallBack 
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

}
