package coreEntity;

import java.io.Serializable;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.newdawn.slick.util.pathfinding.Path;

import coreEntity.Unity.TYPEUNITY;
import coreEntityManager.EntityManager;

public class UnityBaseModel implements Serializable
{
	// appartenance à un camp
	protected EntityManager.CAMP myCamp = EntityManager.CAMP.YELLOW;
	//protected type d'unité
	protected TYPEUNITY idType;
	
	protected Vec2 position; // en mètre - est utilisé car pas de sérialisation de l'objet Body
	
	protected Vec2 positionNode; // position en node;
	
	protected Vec2 positionlFinal; // position final de l'unité
	
	protected Vec2 positionNodeFinal; // coordonnée de position node
	
	protected float rotation;
	
	protected float speed; 			// vitesse;
	
	protected boolean isSelected;
	
	protected Vec2 dirFormation; // direction de formation que doit prendre l'unité
	
	protected int id;
	
	protected UnityBaseController enemy;
	
	protected transient  Body body; // le body n'est pas sérializable -> transient
	
	protected Path paths;
	protected int  indicePaths = 0;

	//est on mort ?
	protected boolean isKilled = false;
	
	
	public UnityBaseModel() {
		super();
		// création du body
		
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
		this.position = body.getPosition();
		return this.position;
	}

	public void setPosition(Vec2 position) 
	{
		//this.position = position.add(new Vec2(0.5f,0.5f));
		this.position = position;
		body.setTransform(this.position, rotation);
	}

	public float getRotation() {
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


	public void setRotation(float rotation) {
		this.rotation = rotation;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setKilled(boolean isKilled) {
		this.isKilled = isKilled;
	}

	public UnityBaseController getEnemy() {
		return enemy;
	}

	public void setEnemy(UnityBaseController enemy) {
		this.enemy = enemy;
	}

	public boolean isSelected() {
		return isSelected;
	}

	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}

	public Path getPaths() {
		return paths;
	}

	public void setPaths(Path paths) 
	{
		this.paths = paths;
		// on replace l'indice à 00
		this.setIndicePaths(0);
	}

	public int getIndicePathsAndIncrement() {
		return indicePaths++; // incrémente autamatiquement l'indice de chemin et commence par le 0
	}

	public void setIndicePaths(int indicePaths) {
		this.indicePaths = indicePaths;
	}

	public int getIndicePaths() {
		return indicePaths;
	}

	
	
}
