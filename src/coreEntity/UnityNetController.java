package coreEntity;

import org.jsfml.system.Time;

import CoreTexturesManager.TexturesManager;
import coreEntity.UnityBaseView.TYPE_ANIMATION;
import coreEntityManager.EntityManager.CAMP;

public class UnityNetController extends UnityBaseController
{
	

	public UnityNetController() 
	{
		super();
		
	}

	@Override
	public void update(Time deltaTime) 
	{
		// TODO Auto-generated method stub
		super.update(deltaTime);
		
		// code pour les unit�s adverses r�seaux
		
		// si l'unit� est en train de frapper, on appel la methode play de la view
		if(this.getModel().isKnocking())
		{
			this.getView().playAnimation(TYPE_ANIMATION.STRIKE);
			this.getModel().setKnocking(false);
		}
	}

	@Override
	public void init() 
	{
		// TODO Auto-generated method stub
		super.init();
		
		// chargement des textures en fonction du model
		if(this.getModel().getMyCamp() == CAMP.YELLOW)
		{
			if(this.getModel().getIdType() == TYPEUNITY.KNIGHT)
			{
				this.getView().setSprite(TexturesManager.GetSpriteByName("ANIM_Piquiers_Jaunes.png"));
			}
		}
		
		if(this.getModel().getMyCamp() == CAMP.BLUE)
		{
			if(this.getModel().getIdType() == TYPEUNITY.KNIGHT)
			{
				this.getView().setSprite(TexturesManager.GetSpriteByName("ANIM_Piquiers_Bleus.png"));
			}
		}
		
		// cr�ation de l'origine pour le sprite
		this.getView().getSprite().setOrigin(this.getModel().getOrigineSprite());
		// sp�cifie � la vue l'animation � jou� par d�faut
		this.getView().playAnimation(TYPE_ANIMATION.NON);
		
	}
	
	

}
