package coreGuiRavage;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.RectangleShape;
import org.jsfml.graphics.RenderStates;
import org.jsfml.graphics.RenderTarget;
import org.jsfml.system.Vector2f;
import org.jsfml.window.Mouse.Button;
import org.jsfml.window.event.KeyEvent;

public class ProgressBar extends Widget 
{

	
	
	public ProgressBar(Vector2f position,Vector2f size,Vector2f minSize,Vector2f maxSize)
	{
		super();
		this.m_model = new ProgressBarModel(position,size,minSize,maxSize);
		this.m_view = new ProgressBarView();
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean onMouse(Vector2f position, Button mouseType) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onKeyboard(KeyEvent keyboardEvent) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onMouseMove(Vector2f position) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onMousePressed(Vector2f position, Button mouseType) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onMouseReleased(Vector2f position, Button mouseType) {
		// TODO Auto-generated method stub
		return false;
	}
	
	public class ProgressBarModel extends Model
	{
		private Vector2f m_minSize;
		private Vector2f m_maxSize;
		private Vector2f m_valueSize;
	
		
		public void setValue(int value)
		{
		
			this.updatePas();
		}
		
		
		public ProgressBarModel(Vector2f position, Vector2f size,Vector2f minSize,Vector2f maxSize) 
		{
			super(position, size);
			this.setM_minSize(minSize);
			this.setM_maxSize(maxSize);
			this.updatePas();
			// TODO Auto-generated constructor stub
		}
		
		
		
		public Vector2f getM_valueSize() {
			return m_valueSize;
		}


		public void setM_valueSize(Vector2f m_valueSize) {
			this.m_valueSize = m_valueSize;
		}


		public Vector2f getM_minSize() {
			return m_minSize;
		}



		public Vector2f getM_maxSize() {
			return m_maxSize;
		}



		public void setM_minSize(Vector2f m_minSize) {
			this.m_minSize = m_minSize;
		}



		public void setM_maxSize(Vector2f m_maxSize) {
			this.m_maxSize = m_maxSize;
		}



		private void updatePas()
		{
		}
		
	}
	
	public class ProgressBarView extends View
	{
		public ProgressBar m_controller;
		
		public RectangleShape m_shape;
		
		public ProgressBarView()
		{
			m_controller = ProgressBar.this;
			m_shape = new RectangleShape();
			m_shape.setPosition(m_controller.m_model.m_position);
			m_shape.setFillColor(new Color(128,128,128,256));
			m_shape.setSize(new Vector2f(0f,0f));
		}
		
		@Override
		public void draw(RenderTarget render, RenderStates state) 
		{
			// mise à jour du size
		//	m_shape.setSize(((ProgressBarModel)this.m_controller.m_model).);			
			render.draw(m_shape);
			
		}
		
	}

}
