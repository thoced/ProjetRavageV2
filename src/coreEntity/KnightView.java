package coreEntity;

import org.jbox2d.common.Vec2;
import org.jsfml.graphics.IntRect;
import org.jsfml.graphics.RenderStates;
import org.jsfml.graphics.RenderTarget;

public class KnightView extends UnityBaseView
{
	
	private IntRect[] rectAnimationWalk;
	
	private final int NB_FRAME_BY_SECOND = 20;
	private final int MAX_IND_FOR_WALK = 4;
	private final int MAX_IND_FOR_STRIKE = 17;
	
	public KnightView(UnityBaseModel model, UnityBaseController controller) {
		super(model, controller);
		// création des animations
		rectAnimationWalk = new IntRect[18];
		for(int i=0;i<rectAnimationWalk.length;i++)
		{
			rectAnimationWalk[i] = new IntRect(0 + i * 32,0,32,32);
		}
	}

	@Override
	public void draw(RenderTarget arg0, RenderStates arg1) 
	{
		super.draw(arg0, arg1);
		
		switch(this.currentTypeAnimation)
		{
			case NON: this.noneAnimation();break;
		
			case WALK: this.walkAnimation();break;
			
			case STRIKE : this.strikeAnimation();break;
		}
		
		// affichage
		arg0.draw(sprite);

	}
	
	private void walkAnimation()
	{
		// on calcul l'animation
		int ind = (int) (NB_FRAME_BY_SECOND * this.elapsedAnimationTime);
		if(ind > MAX_IND_FOR_WALK)
		{
			ind = 0;
			this.elapsedAnimationTime = 0f;
		}
		this.sprite.setTextureRect(rectAnimationWalk[ind]);
	}
	
	private void strikeAnimation()
	{
		// on calcul l'animation
		int ind = (int) (NB_FRAME_BY_SECOND * this.elapsedAnimationTime) + 5; // 5 étant l'offset
		if(ind > MAX_IND_FOR_STRIKE)
		{
			ind = 5;
			this.elapsedAnimationTime = 0f;
		}
		this.sprite.setTextureRect(rectAnimationWalk[ind]);
	}
	
	private void noneAnimation()
	{
		this.sprite.setTextureRect(rectAnimationWalk[0]);
	}

}
