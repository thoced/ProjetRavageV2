package coreEntity;

import java.io.Serializable;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;

import coreEntity.Unity.TYPEUNITY;
import coreEntityManager.EntityManager;

public class UnityBaseModel implements Serializable
{
	// appartenance à un camp
	protected EntityManager.CAMP myCamp = EntityManager.CAMP.YELLOW;
	//protected type d'unité
	protected TYPEUNITY idType;
	
	protected Vec2 position; // en mètre
	
	protected Vec2 positionPixelFinal; // position final de l'unité
	
	protected Vec2 positionNodeFinal; // coordonnée de position node
	
	protected float rotation;
	
	protected boolean isSelected;
	
	protected Vec2 dirFormation; // direction de formation que doit prendre l'unité
	
	protected int id;
	
	protected UnityBaseController enemy;

	//est on mort ?
	protected boolean isKilled = false;

	public EntityManager.CAMP getMyCamp() {
		return myCamp;
	}

	public TYPEUNITY getIdType() {
		return idType;
	}
	
	
	

	

	public Vec2 getPositionPixelFinal() {
		return positionPixelFinal;
	}

	public Vec2 getPositionNodeFinal() {
		return positionNodeFinal;
	}

	public void setPositionPixelFinal(Vec2 positionPixelFinal) {
		this.positionPixelFinal = positionPixelFinal;
	}

	public void setPositionNodeFinal(Vec2 positionNodeFinal) {
		this.positionNodeFinal = positionNodeFinal;
	}

	public Vec2 getDirFormation() {
		return dirFormation;
	}

	public void setDirFormation(Vec2 dirFormation) {
		this.dirFormation = dirFormation;
	}

	public Vec2 getPosition() {
		return position;
	}

	public void setPosition(Vec2 position) {
		this.position = position;
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

	
	
}
