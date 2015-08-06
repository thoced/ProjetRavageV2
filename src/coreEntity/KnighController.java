package coreEntity;

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
import org.newdawn.slick.util.pathfinding.Path;
import org.newdawn.slick.util.pathfinding.navmesh.NavPath;

import CoreTexturesManager.TexturesManager;
import coreAI.Node;
import coreEntity.Unity.TYPEUNITY;
import coreEntityManager.EntityManager.CAMP;
import corePhysic.PhysicWorldManager;

public class KnighController extends UnityBaseController {

	
	
	public KnighController() {
		super();
		// instance de la vue et du model
		this.setModel(new KnightModel());
		this.setView(new KnightView(this.getModel(),this));
	}

	@Override
	public void init()
	{
		// initialisation de la vue avec un sprite
		if(this.getModel().getMyCamp() == CAMP.YELLOW)
			this.getView().setSprite(TexturesManager.GetSpriteByName("ANIM_KNIGHT_YELLOW.png"));
		if(this.getModel().getMyCamp() == CAMP.BLUE)
			this.getView().setSprite(TexturesManager.GetSpriteByName("ANIM_KNIGHT_BLUE.png"));
		

		this.getView().getSprite().setOrigin(new Vector2f(16f,16f));
		
	}

	

	@Override
	public void update(Time deltaTime) {
		// TODO Auto-generated method stub
		super.update(deltaTime);
		
		
	}

	@Override
	public void createBody() 
	{
		// initialisation du body
				BodyDef bdef = new BodyDef();
				bdef.active = true;
				bdef.bullet = false;
				bdef.type = BodyType.KINEMATIC;
				bdef.fixedRotation = false;
				bdef.userData = this;
			
				//bdef.gravityScale = 0.0f;
				
				// creation du body
				this.setBody(PhysicWorldManager.getWorld().createBody(bdef));
				
				Shape shape = new CircleShape();
				shape.m_radius = 0.55f;
				
				FixtureDef fDef = new FixtureDef();
				fDef.shape = shape;
				fDef.density = 1.0f;
				
				fDef.friction = 0.0f;
				fDef.restitution = 0.0f;
			
				Fixture fix = this.getBody().createFixture(fDef);
		
	}

}
