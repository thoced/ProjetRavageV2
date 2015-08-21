package coreEntity;

import org.jsfml.system.Time;

import CoreTexturesManager.TexturesManager;
import coreEntity.UnityBaseView.TYPE_ANIMATION;
import coreEntityManager.EntityManager;
import coreEntityManager.EntityManager.CAMP;
import coreNet.NetBase;
import coreNet.NetDataUnity;
import coreNet.NetSendThread;

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
		
		// code pour les unités adverses réseaux
		
		// si l'unité est en train de frapper, on appel la methode play de la view
		if(this.getModel().isKnocking())
		{
			// on joue l'animation
			this.getView().playAnimation(TYPE_ANIMATION.STRIKE);
			this.getModel().setKnocking(false);
			// on frappe réelleemnt l'enemy
			UnityBaseController u = EntityManager.getVectorUnity().get(this.getModel().getIdEnemy());
			System.out.println("Enemy : " + u);
			if(u!=null)
				u.hit(this.getModel().getStreightStrike());
		}
		
		if(this.getModel().isKilled)
		{
			// on joue la mort
			EntityManager.getVectorUnityNetKilled().add(this);
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
		
		// création de l'origine pour le sprite
		this.getView().getSprite().setOrigin(this.getModel().getOrigineSprite());
		// spécifie à la vue l'animation à joué par défaut
		this.getView().playAnimation(TYPE_ANIMATION.NON);
		
	}
	
	
	
	

}
