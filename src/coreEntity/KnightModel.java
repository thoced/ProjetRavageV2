package coreEntity;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.Shape;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.FixtureDef;

import corePhysic.PhysicWorldManager;

public class KnightModel extends UnityBaseModel
{

	public KnightModel() 
	{
		super();
		// création du body
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
