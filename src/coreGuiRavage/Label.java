package coreGuiRavage;

import java.io.IOException;

import org.jsfml.graphics.Font;
import org.jsfml.graphics.RenderStates;
import org.jsfml.graphics.RenderTarget;
import org.jsfml.graphics.Text;
import org.jsfml.system.Vector2f;

public class Label extends Widget
{

	public Label(Vector2f position,Vector2f size) throws IOException
	{
		super();
		// creatin de la view
		this.setM_model(new LabelModel(position,size));
		this.setM_view(new LabelView(this));
		
		
	}
	
	public void setText(String text)
	{
		// insértion du texte
		((LabelModel)this.m_model).setM_text(text);
		// update du texte à afficher
		((LabelView)this.m_view).updateTextSprite(text);
	}
	
}
// MODEL
class LabelModel extends Model
{
	private String m_text;
	
	
	public LabelModel(Vector2f position, Vector2f size) throws IOException 
	{
		super(position, size);
		
		
	}

	public String getM_text() {
		return m_text;
	}

	/**
	 * @param m_text the m_text to set
	 */
	public void setM_text(String m_text)
	{
		this.m_text = m_text;
		
	}

	
	
	
}


// VIEW
class LabelView extends View
{

	private Label m_controller;
	
	private Text m_textSprite;
	
	private static Font m_fontText;
	
	
	public LabelView(Label label) throws IOException
	{
		m_controller = label;
		
		// instance du Text
		m_fontText = new Font();
		m_fontText.loadFromStream(LabelModel.class.getResourceAsStream("/FONTS/text.ttf"));
		m_textSprite = new Text();
		m_textSprite.setCharacterSize(8);
		m_textSprite.setFont(m_fontText);
	}
	
	public void updateTextSprite(String text)
	{
		m_textSprite.setString(text);
	}
	
	@Override
	public void draw(RenderTarget render, RenderStates state) 
	{
		// on place la position
		m_textSprite.setPosition(m_controller.m_model.m_position);
		// on affiche le texte
		render.draw(m_textSprite);
		
	}

}
