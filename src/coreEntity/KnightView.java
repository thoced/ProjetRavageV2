package coreEntity;

import org.jbox2d.common.Vec2;
import org.jsfml.graphics.IntRect;
import org.jsfml.graphics.RenderStates;
import org.jsfml.graphics.RenderTarget;

public class KnightView extends UnityBaseView
{
	
	private IntRect[] rectAnimationWalk;
	
	public KnightView(UnityBaseModel model, UnityBaseController controller) {
		super(model, controller);
		// création des animations
		rectAnimationWalk = new IntRect[6];
		for(int i=0;i<rectAnimationWalk.length;i++)
		{
			rectAnimationWalk[i] = new IntRect(0 + i * 32,0,32,32);
		}
	}

	@Override
	public void draw(RenderTarget arg0, RenderStates arg1) 
	{
		// on modifie le sprite grace au model
		Vec2 pos = this.getModel().getPosition();
		this.sprite.setPosition(this.toPixelVector2f(pos));
		
		
		// on calcul l'animation
		int ind = (int) (20 * this.elapsedAnimationTime);
		if(ind > 4)
			{
				ind = 0;
				this.elapsedAnimationTime = 0f;
			}
		this.sprite.setTextureRect(rectAnimationWalk[ind]);
		
		
		arg0.draw(sprite);

	}

}
