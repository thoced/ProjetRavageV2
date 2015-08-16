package coreLevel;

import java.util.ArrayList;
import java.util.List;

import org.jbox2d.collision.shapes.ChainShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jsfml.graphics.Sprite;

import CoreLoader.TiledObjectPolylinePoint;
import coreAI.Node;
import coreEntity.UnityBaseController;
import corePhysic.PhysicWorldManager;

public class LevelModel 
{
	// Tableau des valeurs Tiled (pour la recherche de chemin)
		private Node[] nodes;
		// Obstacles box2d
		private Body obstacles;
		
	public LevelModel() 
	{
		super();
		// instance des nodes
		nodes = new Node[375*250];
	}
	
	
	
	public Node[] getNodes() {
		return nodes;
	}

 

	public void setNodes(Node[] nodes) {
		this.nodes = nodes;
	}



	public void setObstacles(Body obstacles) {
		this.obstacles = obstacles;
	}



	public Body getObstacles() {
		return obstacles;
	}



	public boolean isNodeObstacle(int x,int y)
	{
		Node n = this.nodes[(375 * y) + x];
		if(n.getType() != 0)
			return true;
		else
			return false;
	}
	
	public boolean isNodeFree(int x,int y,UnityBaseController u)
	{
		return this.nodes[(375 * y) + x].isNodeFree(u);
	}
	
	public Node takeNode(int x,int y,UnityBaseController u)
	{
		this.nodes[(375 * y) + x].takeNode(u);
		return this.nodes[(375 * y) + x];
	}
	
	public void releaseNode(int x,int y,UnityBaseController u)
	{
		this.nodes[(375 * y) + x].releaseNode(u);
	}
	
	public void InsertObstacle(List<TiledObjectPolylinePoint> listePoint,int x,int y,String typeobstacle) throws java.lang.RuntimeException
	{
		// ajout d'un obstacle de type polyline
		
		// on créer un bodydef
		BodyDef bdef = new BodyDef();
		bdef.type = BodyType.STATIC;
		bdef.bullet = false;
		// on determine 
		
		// on crée la chainshape
		ChainShape cs = new ChainShape();
		// on créer un vecteur de vec2 correspondant au nombre de point
		Vec2[] vectors = new Vec2[listePoint.size()];
		// on instance la liste des vecteurs
		for(int i=0;i<vectors.length;i++)
			vectors[i] = new Vec2();
		// on ajoute les vecteurs
		
		// on récupère la position initial de polyline
		float bx = x / PhysicWorldManager.getRatioPixelMeter();
		float by = y / PhysicWorldManager.getRatioPixelMeter();
		
		int ind = 0;
		for(Vec2 v : vectors)
		{
			// onrécupère les points de la listes
			float diffx = listePoint.get(ind).x / PhysicWorldManager.getRatioPixelMeter();
			float diffy = listePoint.get(ind).y / PhysicWorldManager.getRatioPixelMeter();
			// on ajoute la différence entre les coordonnées du pont initial et la liste des points
			v.set(bx + diffx,by + diffy);
			ind++;
			
		}
		// on ajoute le tout dans le chainshape
		try
		{
			cs.createChain(vectors, vectors.length);
			
			// création du body
			Body bodyObstacle = PhysicWorldManager.getWorld().createBody(bdef);
			bodyObstacle.setUserData(typeobstacle);
			
			// creation dufixture
			FixtureDef fixture = new FixtureDef();
			fixture.shape = cs;
			fixture.friction = 0.6f;
			fixture.density = 1f;
			fixture.restitution = 0.0f;
	
			// ajout dans le body
			bodyObstacle.createFixture(fixture);
		}
		catch(java.lang.RuntimeException e)
		{
			
		}
		
		
	}
		
		
}
