package coreEntity;

import java.io.IOException;
import java.util.List;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.Shape;
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
import coreEntityManager.EntityManager.CAMP;
import coreEvent.EventManager;
import coreNet.NetDataUnity;
import coreNet.NetHeader;
import coreNet.NetHeader.TYPE;
import coreNet.NetManager;
import corePhysic.PhysicWorldManager;

public class KnighController extends UnityBaseController {

	
	
	public KnighController() {
		super();
		// instance de la vue et du model
		
		this.setModel(new KnightModel(this));
		this.setView(new KnightView(this.getModel(),this));
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
	public void update(Time deltaTime) {
		// TODO Auto-generated method stub
		super.update(deltaTime);
		
	
		
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
