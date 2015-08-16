package coreEntity;

import java.io.IOException;
import java.util.List;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.Shape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.FixtureDef;
import org.jsfml.graphics.IntRect;
import org.jsfml.system.Clock;
import org.jsfml.system.Time;
import org.jsfml.system.Vector2f;
import org.jsfml.window.Keyboard.Key;
import org.jsfml.window.event.KeyEvent;
import org.newdawn.slick.util.pathfinding.Path;
import org.newdawn.slick.util.pathfinding.navmesh.NavPath;

import CoreTexturesManager.TexturesManager;
import coreAI.Node;
import coreEntity.Unity.TYPEUNITY;
import coreEntity.UnityBaseView.TYPE_ANIMATION;
import coreEntityManager.EntityManager;
import coreEntityManager.EntityManager.CAMP;
import coreEvent.EventManager;
import coreNet.NetBase;
import coreNet.NetDataUnity;
import coreNet.NetHeader;
import coreNet.NetSendThread;
import coreNet.NetHeader.TYPE;
import coreNet.NetManager;
import coreNet.NetStrike;
import corePhysic.PhysicWorldManager;

public class KnighController extends UnityBaseController 
{
	private float elapsedTimeAttack = 0f;
	
	
	public KnighController() {
		super();
		// instance de la vue et du model
		
		this.setModel(new KnightModel(this));
		this.setView(new KnightView(this.getModel(),this));
		
		// energy
		this.getModel().setEnergy(50);
	}

	@Override
	public void init()
	{
		// initialisation de la vue avec un sprite
		if(this.getModel().getMyCamp() == CAMP.YELLOW)
			this.getView().setSprite(TexturesManager.GetSpriteByName("ANIM_Piquiers_Jaunes.png"));
		if(this.getModel().getMyCamp() == CAMP.BLUE)
			this.getView().setSprite(TexturesManager.GetSpriteByName("ANIM_Piquiers_Bleus.png"));
		
		
		
		// initialisa la vue avec l'origine du sprite
		this.getView().getSprite().setOrigin(new Vector2f(40f,40f));
		// spécifie à la vue l'animation à joué par défaut
		this.getView().playAnimation(TYPE_ANIMATION.NON);
		
		// ajout au event manager
		EventManager.addCallBack(this);
		
	}

	

	@Override
	public void update(Time deltaTime) 
	{
		// TODO Auto-generated method stub
		super.update(deltaTime);
		
		// mise à jour du temps écoulé pour l'attaque
		elapsedTimeAttack += deltaTime.asSeconds();
		
		// attaque enemy
		if(this.getModel().getEnemy() != null  && elapsedTimeAttack > 3f )
		{
			
			if(this.getModel().getEnemy().getModel().getPosition().sub(this.getModel().getPosition()).length() > 2f )  // si l'enemy est plus loin que 2
			{
				// on recherche un emplacement à coté
				// on cherche un position à côté de l'enemy
				Vec2 positionNearEnemy = EntityManager.searchPosition(this, this.getModel().getEnemy());
				// on recherche le chemin
				Vec2 positionNode = positionNearEnemy;
				positionNode.x = (int)positionNearEnemy.x;
				positionNode.y = (int)positionNearEnemy.y;
				// calcul du vecteur direction
				Vec2 dir = this.getModel().getEnemy().getModel().getPosition().sub(positionNearEnemy);
				dir.normalize();
				EntityManager.computeDestination(this, positionNearEnemy, positionNode, dir);
				
			}
			else
			{
			
				System.out.println("KnightController : Hit");
				// emission sur le réseau
				this.getModel().setKnocking(true);	
				// on place la force de frappe
				this.getModel().setStreightStrike(10);
				// emission réseau
				NetDataUnity data = new NetDataUnity();
				data.setTypeMessage(NetBase.TYPE.UPDATE);
				this.prepareModelToNet();
				try
				{
					data.setModel(this.getModel().clone());
				} catch (CloneNotSupportedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				NetSendThread.push(data);
				// on joue l'animation de frappe
				System.out.println("KnightController : PlayAnimation");
				this.getView().playAnimation(TYPE_ANIMATION.STRIKE);
				
			}
			
			// remise à zero du compteur temps
			elapsedTimeAttack = 0f;
			
		}
		
		
		
	}

	@Override
	public void onKeyboard(KeyEvent keyboardEvent) {
		// TODO Auto-generated method stub
		super.onKeyboard(keyboardEvent);
		
		if(keyboardEvent.key == Key.W)
		{
			this.getView().playAnimation(TYPE_ANIMATION.WALK);
		}
		
		if(keyboardEvent.key == Key.S)
		{
			this.getView().playAnimation(TYPE_ANIMATION.STRIKE);
		}
		
		
	}

	
}
