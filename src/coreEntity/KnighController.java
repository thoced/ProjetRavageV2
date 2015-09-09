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
import coreEntityManager.ChoosePosition;
import coreEntityManager.EntityManager;
import coreEntityManager.ReservationManager;
import coreEntityManager.EntityManager.CAMP;
import coreEntityManager.NodeReserved;
import coreEvent.EventManager;
import coreLevel.LevelManager;
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
		this.getModel().setEnergyMax(50);
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
		
		if(elapsedTimeAttack > 2f)
		{
			
			if(this.getModel().getIdEnemy() != -1 && m_isLastStep)
			{
				// récupération de l'objet enemy
				UnityBaseController enemy = EntityManager.getVectorUnityNet().get(this.getModel().getIdEnemy());
				
				if(enemy != null)
				{
					Vec2 v = enemy.getModel().getPosition().sub(this.getModel().getPosition());
					if(v.length() < 2f)
					{
						this.strike();
						v.normalize();
						this.computeRotation(v);
						
						NetDataUnity data = new NetDataUnity();			// creatin du netdataunity
						data.setTypeMessage(NetBase.TYPE.UPDATE);		// on spécifie que c'est une update
						this.getModel().setKnocking(true);				// on spécifie au modèle que nous sommes en train de frapper
						this.getModel().setStreightStrike(10);			// on spécifie la force de frappe
						this.prepareModelToNet();						// préparation du model pour l'envoi sur le réseau
						try
						{
							data.setModel(this.getModel().clone());
						} catch (CloneNotSupportedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}					// placement du model dans le netdataunity
						NetSendThread.push(data);						// envoi sur le réseau
					}
					else
					{
						// 2) recheche d'une position libre
						Vec2 posNear = new ChoosePosition().findPositionForFight(this, enemy);
						//Vec2 posNear = enemy.getModel().getPositionNode();
						// 3) on libère la derniere position réserve
						/*if(this.nodeReserved != null)
							this.nodeReserved.bookNode(this);
						// 4) on réserve la node
						this.nodeReserved = LevelManager.getLevel().getModel().bookNode((int)posNear.x, (int)posNear.y, this);*/
						
						if(this.getModel().getNodeReserved() != null)
							ReservationManager.remove(this.getModel().getNodeReserved());
						ReservationManager.add(posNear, this);
						
						// 5) on se déplace
						Vec2 posFinal = posNear;
						posFinal = posFinal.add(new Vec2(.5f,.5f));
						Vec2 posEnemy = enemy.getModel().getPosition();
						Vec2 dir = posEnemy.sub(posFinal);
						dir.normalize();
						EntityManager.computeDestination(this, posFinal, posNear, dir);
					}
					
				}
				{
					this.getModel().setIdEnemy(-1);
				}
				
				
			}
			
		// remise à zero du temps d'attaque
			elapsedTimeAttack = 0f;
		}
		
		
		
	}

	@Override
	public boolean onKeyboard(KeyEvent keyboardEvent) {
		// TODO Auto-generated method stub
		super.onKeyboard(keyboardEvent);
		
		return false;
		
	}

	
}
