package coreGuiRavage;

import org.jsfml.graphics.RenderStates;
import org.jsfml.graphics.RenderTarget;
import org.jsfml.system.Vector2f;

public class Label extends Widget
{

	public Label(Vector2f position,Vector2f size)
	{
		super();
		
		// creatin de la view
		this.setM_model(new LabelModel(position,size));
		this.setM_view(new LabelView());
		
		
	}
	
}
// MODEL
class LabelModel extends Model
{
	private String m_text;

	
	
	public LabelModel(Vector2f position, Vector2f size) {
		super(position, size);
		// TODO Auto-generated constructor stub
	}

	public String getM_text() {
		return m_text;
	}

	public void setM_text(String m_text) {
		this.m_text = m_text;
	}
	
	
}


// VIEW
class LabelView extends View
{

	@Override
	public void draw(RenderTarget render, RenderStates state) {
		// TODO Auto-generated method stub
		
	}

}
