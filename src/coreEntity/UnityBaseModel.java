package coreEntity;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.Shape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.FixtureDef;
import org.jsfml.graphics.IntRect;
import org.jsfml.system.Vector2f;
import org.newdawn.slick.util.pathfinding.Path;

import coreEntity.UnityBaseController.TYPEUNITY;
import coreEntityManager.EntityManager;
import coreEntityManager.EntityManager.CAMP;
import corePhysic.PhysicWorldManager;

public class UnityBaseModel implements Serializable
{
	protected transient UnityBaseController controller;
	// appartenance � un camp
	protected EntityManager.CAMP myCamp = EntityManager.CAMP.YELLOW;
	//protected type d'unit�
	protected TYPEUNITY idType;
	
	protected Vec2 position ; // en m�tre - est utilis� car pas de s�rialisation de l'objet Body
	
	protected Vec2 positionNode ; // position en node;
	
	protected Vec2 positionlFinal ; // position final de l'unit�
	
	protected Vec2 positionNodeFinal ; // coordonn�e de position node
	
	protected float rotation;
	
	protected float speed; 			// vitesse;
	
	protected boolean isSelected;
	
	protected Vec2 dirFormation ; // direction de formation que doit prendre l'unit�
	
	protected int id;
	
	protected transient UnityNetController enemy;
	
	protected int idEnemy;
	
	protected boolean isKnocking = false; // variable pass� � true quand l'unit� frappe.
	
	protected transient  Body body; // le body n'est pas s�rializable -> transient
	
	protected transient  Object lock;
	
	protected Path paths ;
	protected  int  indicePaths = 0;

	//est on mort ?
	protected boolean isKilled = false;
	
	// information concernant l'origine du sprite
	protected Animations animations;
	// information sur l'origne du sprite
	protected Vector2f origineSprite;
	
	
	public UnityBaseModel(UnityBaseController controller) {
		super();
		
		this.controller = controller;
		lock = new Object();
		animations = new Animations(); // instance de l'objet animations
		
	}
	
	
	
	
	
	@Override
	public UnityBaseModel clone() throws CloneNotSupportedException {
		// TODO Auto-generated method stub
		UnityBaseModel clone = new UnityBaseModel(this.controller);
		clone.setMyCamp(this.getMyCamp());
		clone.setIdType(this.getIdType());
		clone.setId(this.getId());
		clone.setPosition(this.getPosition());
		clone.setPositionNode(this.getPositionNode());
		clone.setPositionlFinal(this.getPositionlFinal());
		clone.setRotation(this.getRotation());
		clone.setSpeed(this.getSpeed());
		clone.setSelected(this.isSelected());
		clone.setDirFormation(this.getDirFormation());
		clone.setIdEnemy(this.getIdEnemy());
		clone.setKnocking(this.isKnocking());
		clone.setPaths(this.getPaths());
		clone.setIndicePaths(this.getIndicePaths());
		clone.setKilled(this.isKilled());
		clone.setAnimations(this.getAnimations());
		clone.setOrigineSprite(this.getOrigineSprite());
		
		

		return clone;
	}





	public void initModel(UnityBaseController controller)
	{
		this.controller = controller;
		// cr�ation du body
				// initialisation du body
				BodyDef bdef = new BodyDef();
				bdef.active = true;
				bdef.bullet = false;
				bdef.type = BodyType.KINEMATIC;
				bdef.fixedRotation = false;
				bdef.userData = controller;
			
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
				
				// creation du body
				this.body.setTransform(this.position, this.rotation);
				// cr�ation du lock
				lock = new Object();
				
				// on replace le path et l'indice du path � 0 pour �viter les t�l�portation r�seau
				//this.setIndicePaths(0); // positionnement � 0 
				//this.setPosition(new Vec2(this.getPaths().getStep(0).getX(),this.getPaths().getStep(0).getY())); // positionnement de la position � 0
				
	}
	
	

	public Vector2f getOrigineSprite() {
		return origineSprite;
	}

	public void setOrigineSprite(Vector2f origineSprite) {
		this.origineSprite = origineSprite;
	}

	public int getIdEnemy() {
		return idEnemy;
	}

	public Animations getAnimations() {
		return animations;
	}

	public void setIdEnemy(int idEnemy) {
		this.idEnemy = idEnemy;
	}

	public void setAnimations(Animations animations) {
		this.animations = animations;
	}

	public EntityManager.CAMP getMyCamp() {
		return myCamp;
	}

	public TYPEUNITY getIdType() {
		return idType;
	}
	
	

	public void setBody(Body body) {
		this.body = body;
	}

	public Body getBody() {
		return body;
	}

	

	public float getSpeed() {
		return speed;
	}

	public void setSpeed(float speed) {
		this.speed = speed;
	}

	public Vec2 getPositionNodeFinal() {
		return positionNodeFinal;
	}

	

	public Vec2 getPositionlFinal() {
		return positionlFinal;
	}

	public void setPositionlFinal(Vec2 positionlFinal) {
		this.positionlFinal = positionlFinal;
	}

	public void setPositionNodeFinal(Vec2 positionNodeFinal) {
		this.positionNodeFinal = positionNodeFinal;
	}
	
	public Vec2 getPositionNode() 
	{
		positionNode = this.getPosition();
		positionNode.x = (int)positionNode.x;
		positionNode.y = (int)positionNode.y;
		return positionNode;
	}

	public void setPositionNode(Vec2 positionNode) {
		this.positionNode = positionNode;
	}

	public Vec2 getDirFormation() {
		return dirFormation;
	}

	public void setDirFormation(Vec2 dirFormation) {
		this.dirFormation = dirFormation;
	}

	public Vec2 getPosition() 
	{
			if(body!=null)
				this.position = body.getPosition();
		return this.position;
	}

	public void setPosition(Vec2 position) 
	{
		//this.position = position.add(new Vec2(0.5f,0.5f));
		this.position = position;
		if(body!=null)
			body.setTransform(this.position, rotation);
	}

	public float getRotation() 
	{
		if(body != null)
			rotation = body.getAngle();
		return rotation;
	}

	public int getId() {
		return id;
	}

	public boolean isKilled() {
		return isKilled;
	}

	public void setMyCamp(EntityManager.CAMP myCamp) {
		this.myCamp = myCamp;
	}

	public void setIdType(TYPEUNITY idType) {
		this.idType = idType;
	}


	public void setRotation(float rotation) 
	{
		this.rotation = rotation;
		if(body != null)
			body.setTransform(this.position, this.rotation);
		
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setKilled(boolean isKilled) {
		this.isKilled = isKilled;
	}

	public UnityNetController getEnemy() {
		return enemy;
	}

	public void setEnemy(UnityNetController enemy) {
		this.enemy = enemy;
	}

	public boolean isSelected() {
		return isSelected;
	}

	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}
	
	

	public boolean isKnocking() {
		return isKnocking;
	}

	public void setKnocking(boolean isKnocking) {
		this.isKnocking = isKnocking;
	}

	public Path getPaths() 
	{
		synchronized(lock)
		{
			return paths;
		}
		
	}

	public void setPaths(Path paths) 
	{
		synchronized(lock)
		{
			this.paths = paths;
			// on replace l'indice � 00
			this.setIndicePaths(0);
	
		}
	}

	public int getIndicePathsAndIncrement()
	{
		synchronized(lock)
		{
			return indicePaths++; // incr�mente autamatiquement l'indice de chemin et commence par le 0
		}
	
	}

	public void setIndicePaths(int indicePaths)
	{
		synchronized(lock)
		{
		this.indicePaths = indicePaths;
		}
	}

	public int getIndicePaths() 
	{	synchronized(lock)
		{
		return indicePaths;
		}
	}



	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "id : " + this.getId() + " position : " + this.getPosition();
	}




/*
	@Override
	public void readExternal(ObjectInput arg0) throws IOException,
			ClassNotFoundException 
	{
		// Camp et Id Type
		this.myCamp = CAMP.values()[arg0.readInt()];
		this.idType = TYPEUNITY.values()[arg0.readInt()];
		// Position
		this.position = new Vec2(arg0.readFloat(),arg0.readFloat());
		// Position Node
		this.positionNode = new Vec2(arg0.readFloat(),arg0.readFloat());
		// Position Final
		this.positionlFinal = new Vec2(arg0.readFloat(),arg0.readFloat());
		// Position Node Final
		this.positionNodeFinal = new Vec2(arg0.readFloat(),arg0.readFloat());
		// Rotaton
		this.rotation = arg0.readFloat();
		// Speed
		this.speed = arg0.readFloat();
		// Is Selected
		this.isSelected = arg0.readBoolean();
		// Dir Formation
		this.dirFormation  = new Vec2(arg0.readFloat(),arg0.readFloat());
		// id
		this.id = arg0.readInt();
		// Id Ennemy
		this.idEnemy = arg0.readInt();
		// Knocking
		this.isKnocking = arg0.readBoolean();
		// Paths
		int l = arg0.readInt();
		this.paths = new Path();
		for(int i=0;i<l;i++)
			this.paths.appendStep(arg0.readInt(), arg0.readInt());
				
		// Indice path
		this.indicePaths = arg0.readInt();
		// Is Killed
		this.isKilled = arg0.readBoolean();
		// animation
	//	this.animations = (Animations)arg0.readObject();
		
		int size = arg0.readInt();
		this.animations = new Animations();
		for(int j=0;j<size;j++)
		{
			IntRect r = new IntRect(arg0.readInt(),arg0.readInt(),arg0.readInt(),arg0.readInt());
			this.animations.append(j, r);
		}
		
		// Position
		this.origineSprite = new Vector2f(arg0.readFloat(),arg0.readFloat());
		
	}





	@Override
	public void writeExternal(ObjectOutput out) throws IOException 
	{
		// Camp et Id Type
		out.writeInt(this.myCamp.ordinal());
		out.writeInt(this.idType.ordinal());
		// Position
		out.writeFloat(this.position.x);
		out.writeFloat(this.position.y);
		// Position Node
		out.writeFloat(this.positionNode.x);
		out.writeFloat(this.positionNode.y);
		// Position Final
		out.writeFloat(this.positionlFinal.x);
		out.writeFloat(this.positionlFinal.y);
		// Position Node Final
		out.writeFloat(this.positionNodeFinal.x);
		out.writeFloat(this.positionNodeFinal.y);
		// Rotaton
		out.writeFloat(this.rotation);
		// Speed
		out.writeFloat(this.speed);
		// Is Selected
		out.writeBoolean(this.isSelected);
		// Dir Formation
		out.writeFloat(this.dirFormation.x);
		out.writeFloat(this.dirFormation.y);
		// id
		out.writeInt(this.id);
		// Id Ennemy
		out.writeInt(this.idEnemy);
		// Knocking
		out.writeBoolean(this.isKnocking);
		// Paths
		out.writeInt(this.paths.getLength());
		for(int i=0;i<this.paths.getLength();i++)
		{
			out.writeInt(this.paths.getX(i));
			out.writeInt(this.paths.getY(i));
		}
		// Indice path
		out.writeInt(this.indicePaths);
		// Is Killed
		out.writeBoolean(this.isKilled);
		// animation
		//out.writeObject(this.animations);
		
		int size = this.animations.getAnimations().length;
		out.writeInt(size);
		for(int j=0;j<size;j++)
		{
			IntRect r = this.animations.getAnimations()[j];
			out.writeInt(r.left);
			out.writeInt(r.top);
			out.writeInt(r.width);
			out.writeInt(r.height);
		}
		
		// Position
		out.writeFloat(this.origineSprite.x);
		out.writeFloat(this.origineSprite.y);
		
	}

	*/
	
}
