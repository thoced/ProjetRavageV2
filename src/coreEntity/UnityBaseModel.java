package coreEntity;

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
import org.jsfml.system.Vector2f;
import org.newdawn.slick.util.pathfinding.Path;

import coreEntity.UnityBaseController.TYPEUNITY;
import coreEntityManager.EntityManager;
import corePhysic.PhysicWorldManager;

public class UnityBaseModel implements Serializable
{

	// appartenance � un camp
	protected EntityManager.CAMP myCamp = EntityManager.CAMP.YELLOW;
	//protected type d'unit�
	protected TYPEUNITY idType;
	
	protected Vec2 position; // en m�tre - est utilis� car pas de s�rialisation de l'objet Body
	
	protected Vec2 positionNode; // position en node;
	
	protected Vec2 positionlFinal; // position final de l'unit�
	
	protected Vec2 positionNodeFinal; // coordonn�e de position node
	
	protected float rotation;
	
	protected float speed; 			// vitesse;
	
	protected boolean isSelected;
	
	protected Vec2 dirFormation; // direction de formation que doit prendre l'unit�
	
	protected int id;
	
	protected transient UnityNetController enemy;
	
	protected int idEnemy;
	
	protected boolean isKnocking = false; // variable pass� � true quand l'unit� frappe.
	
	protected transient  Body body; // le body n'est pas s�rializable -> transient
	
	protected transient  Object lock;
	
	protected Path paths;
	protected  int  indicePaths = 0;

	//est on mort ?
	protected boolean isKilled = false;
	
	// information concernant l'origine du sprite
	protected Animations animations;
	// information sur l'origne du sprite
	protected Vector2f origineSprite;
	
	
	public UnityBaseModel() {
		super();
		
		lock = new Object();
		animations = new Animations(); // instance de l'objet animations
		
	}
	
	
	
	public void initModel()
	{
		// cr�ation du body
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
				
				// creation du body
				this.body.setTransform(this.position, this.rotation);
				// cr�ation du lock
				lock = new Object();
				
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

	
	
}
