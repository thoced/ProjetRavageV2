package coreGuiRavage;

import java.util.ArrayList;
import java.util.List;

import javax.json.stream.JsonParser.Event;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.FloatRect;
import org.jsfml.graphics.IntRect;
import org.jsfml.graphics.RectangleShape;
import org.jsfml.graphics.RenderStates;
import org.jsfml.graphics.RenderTarget;
import org.jsfml.graphics.RenderTexture;
import org.jsfml.graphics.Sprite;
import org.jsfml.graphics.TextureCreationException;
import org.jsfml.graphics.Transform;
import org.jsfml.system.Vector2f;
import org.jsfml.window.Mouse;
import org.jsfml.window.Mouse.Button;
import org.jsfml.window.event.Event.Type;
import org.jsfml.window.event.MouseButtonEvent;
import org.jsfml.window.event.MouseEvent;

import ravage.FrameWork;
import CoreTexturesManager.TexturesManager;

 public class Panel extends Gui 
{
	 
	// différence entre la position du panel et la position du click
    private Vector2f m_diffPosClick;
	
	public Panel(float x,float y,Vector2f size) throws TextureCreationException {
		super();
		
		// transformation en pixels
		Vector2f pos = new Vector2f(FrameWork.getWindow().getSize().x * x,FrameWork.getWindow().getSize().y * y);
		
		// instance du model et de la vue
		this.setM_model(new PanelModel(pos,size));
		this.setM_view(new PanelView(this));
	}

	
	public boolean onMouseMove(Vector2f position) 
	{
	
			Vector2f p = this.m_model.m_position;
			Vector2f s = this.m_model.m_size;
			FloatRect bound = new FloatRect(p,s);
			if(bound.contains(position))
			{
				if(this.getM_model().isPressed())
				{
					this.m_model.m_position = Vector2f.sub(position, this.m_diffPosClick);
					
				}
				return true;
			}
		return false;
		
	}



	public boolean onMouseReleased(Vector2f position, Button mouseType) 
	{
		this.m_model.setPressed(false);
		return false;
	}


	@Override
	public boolean onMousePressed(Vector2f position, Mouse.Button button)
	{
		Vector2f p = this.m_model.m_position;
		Vector2f s = this.m_model.m_size;
		FloatRect bound = new FloatRect(p,s);
		if(bound.contains(position))
		{
			this.getM_model().setPressed(true); // on indique au gui qu'un coli 
			this.m_diffPosClick = Vector2f.sub(position, this.m_model.m_position);
			return true;
		}
		else
		{
			this.getM_model().setPressed(false);
			return false;
		}
	}


	
	
}

class PanelModel extends Model
{
	// list des gui contenu dans le panel
	private List<Gui> m_guis;
	
	
	public PanelModel(Vector2f posPanel,Vector2f size)
	{
		super(posPanel,size);
		// instance de la liste
		m_guis = new ArrayList<Gui>();
	}

}

class PanelView extends View
{
	// controller parent
	private Panel m_controller;
	// Sprite
	private Sprite m_spritePanel;
	private RectangleShape m_rectangle;
	// IntRect de la texture
	private IntRect m_textureIntRect;
	
	public PanelView(Panel controller) throws TextureCreationException
	{
		
		// réception du parent controller
		m_controller = controller;
		
		// création du render texture
		m_render = new RenderTexture();
		m_render.create((int)controller.m_model.m_size.x, (int)controller.m_model.m_size.y);
		// création du sprite final du render
		m_spriteRender = new Sprite(m_render.getTexture());
		
		// chargement du sprite panelBackground.png
		m_spritePanel = new Sprite(TexturesManager.GetTextureByName("panelBackground.png"));
		m_rectangle = new RectangleShape();
		m_rectangle.setTexture(TexturesManager.GetTextureByName("panelBackground.png"));
		
		// initilisaion de l'origin
		this.m_origin = new Vector2f(0f,0f);
		if(m_spritePanel != null)
			m_spritePanel.setOrigin(this.m_origin);
		
		
	}
	
	@Override
	public void draw(RenderTarget render, RenderStates state) 
	{
		// affichage du panel
		Vector2f l_size = m_controller.m_model.m_size;
		Vector2f l_pos = m_controller.m_model.m_position;
		m_spritePanel.setOrigin(m_origin);
		m_textureIntRect = new IntRect(0,0,(int)l_size.x,(int)l_size.y);
		m_spritePanel.setPosition(new Vector2f(0f,0f));
		m_spritePanel.setTextureRect(m_textureIntRect);
		
		// rectangle
		m_rectangle.setSize(l_size);
		m_rectangle.setPosition(new Vector2f(0f,0f));
		m_rectangle.setOutlineThickness(-6f);
		
		Color c = new Color(128, 128, 128, 128);
		
		
		m_rectangle.setOutlineColor(c);
		
		// affichage de la texture du panel dans le rendertexture
		m_render.clear();
		m_render.draw(m_rectangle);
		// on renvoie le sprite du render texture dans le render appelant
		// positionnement du m_spriteRender
		m_spriteRender.setPosition(m_controller.m_model.m_position);
		render.draw(m_spriteRender);
		
		
		
	}
	
}
