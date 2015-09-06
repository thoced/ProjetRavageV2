package coreGuiRavage;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.IntRect;
import org.jsfml.graphics.RectangleShape;
import org.jsfml.graphics.RenderStates;
import org.jsfml.graphics.RenderTarget;
import org.jsfml.graphics.Texture;
import org.jsfml.system.Time;
import org.jsfml.system.Vector2f;

import coreEntity.UnityBaseController;

public class ImageEnergy extends Image 
{

	// parent unity
	private UnityBaseController m_unity;
	
	public ImageEnergy(UnityBaseController unity,Vector2f position) 
	{
		super((Texture)unity.getView().getSprite().getTexture(),unity.getModel().getAnimations().getInd(0), position);
		// TODO Auto-generated constructor stub
		this.m_view = new ImageEnergyView((Texture)unity.getView().getSprite().getTexture(),unity.getModel().getAnimations().getInd(0),position);
		m_unity = unity;
	
		
	}
	
	

	@Override
	public void update(Time deltaTime) 
	{
		// TODO Auto-generated method stub
		super.update(deltaTime);
		// mise à jour de l'énergie
		((ImageEnergyView)this.m_view).m_shape.setSize(new Vector2f((24f / m_unity.getModel().getEnergyMax()) * m_unity.getModel().getEnergy(),2f));
			
	}




	class ImageEnergyView extends ImageView
	{
		// shape de l'energy
		public RectangleShape m_shape;
		
		
		public ImageEnergyView(Texture texture,IntRect textureRect,Vector2f position) 
		{
			super(texture, textureRect, position);
			// instance du shape
			m_shape = new RectangleShape();
			m_shape.setPosition(Vector2f.sub(ImageEnergy.this.m_model.m_position,new Vector2f(16f,16f)));
			m_shape.setSize(new Vector2f(24f,2f));
			m_shape.setFillColor(Color.GREEN);
			m_shape.setOrigin(0f,0f);
			
		
		}

		public void setSize(Vector2f size)
		{
			m_shape.setSize(size);
		}


		@Override
		public void draw(RenderTarget render, RenderStates state) 
		{
		
			// affichage
			render.draw(m_shape);
			
			super.draw(render, state);
			
		}
		
	}
	
	

}
