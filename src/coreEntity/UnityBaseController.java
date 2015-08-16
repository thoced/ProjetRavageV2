package coreEntity;

import java.io.IOException;
import java.util.List;

import org.jbox2d.common.Rot;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jsfml.system.Time;
import org.jsfml.window.event.KeyEvent;
import org.jsfml.window.event.MouseButtonEvent;
import org.jsfml.window.event.MouseEvent;
import org.newdawn.slick.util.pathfinding.Path;
import org.newdawn.slick.util.pathfinding.Path.Step;
import org.newdawn.slick.util.pathfinding.navmesh.NavPath;

import coreAI.ICallBackAStar;
import coreAI.Node;
import coreEntity.Unity.ANIMATE;
import coreEntity.UnityBaseView.TYPE_ANIMATION;
import coreEntityManager.EntityManager;
import coreEvent.IEventCallBack;
import coreLevel.LevelManager;
import coreNet.NetBase.TYPE;
import coreNet.NetDataUnity;
import coreNet.NetManager;
import coreNet.NetSendThread;
import corePhysic.PhysicWorldManager;
import ravage.IBaseRavage;

public  class UnityBaseController implements IBaseRavage,ICallBackAStar,IEventCallBack
{
	protected UnityBaseView view;
	
	protected UnityBaseModel model;
	
	protected Step step = null; // step du chemin
	
	protected Vec2 vecStep = null;	// vecteur de déplacement step
	
	protected Vec2 dir = null;
	
	protected Object lock;
	
	protected ETAPE sequencePath = ETAPE.NONE;
	
	protected Node nodeTake = null;
	
	public enum ETAPE {GETSTEP,MOVE,NONE};
	
	public  enum TYPEUNITY {KNIGHT};
	

	public UnityBaseController() {
		super();
		lock = new Object();
		// instance de la vue et du model
		
	}
	
	public void prepareModelToNet()
	{
		// prépare le model pour être envoyé au réseau car body et enemy ne sont pas sérialisés
	//	this.getModel().setPosition(this.getModel().getBody().getPosition());
	//	this.getModel().setRotation(this.getModel().getBody().getAngle());
		if(this.getModel().getEnemy() != null)
			this.getModel().setIdEnemy(this.getModel().getEnemy().getModel().getId());
		this.getModel().setOrigineSprite(this.getView().getSprite().getOrigin());
	
		
	}
	

	public UnityBaseView getView() {
		return view;
	}

	public UnityBaseModel getModel() {
		return model;
	}


	public void setView(UnityBaseView view) {
		this.view = view;
	}

	public void setModel(UnityBaseModel model) 
	{
		if(this.model != null) // si il existe déja un model, il faut supprimer le body
		{
			PhysicWorldManager.getWorld().destroyBody(this.model.getBody());
		}
		this.model = model;
	}
	
	private boolean checkNodeFree()
	{
		if(LevelManager.getLevel().getModel().isNodeFree(step.getX(), step.getY(),this) != true)
		{
			this.getModel().DecrementIndice();
			this.getModel().getBody().setLinearVelocity(new Vec2(0,0));
			this.getView().playAnimation(TYPE_ANIMATION.NON);
			return false;  // retourne false car le node est occupé, il faut stopper la progression
		}
		// on libère sur celui où l'on se trouve
		if(nodeTake != null)
			nodeTake.releaseNode(this);
		// on take le node
		nodeTake = LevelManager.getLevel().getModel().takeNode(step.getX(), step.getY(), this);
		
		return true; // pas de node occupé
	}
	
	private void computeNextStep()  // on récupère une étape de chemion
	{
		try
		{
			step = this.getModel().getPaths().getStep(this.getModel().getIndicePathsAndIncrement());
			// on vérifie si le node n'est pas occupé
			if(this.checkNodeFree() != true)
				return;
			
			// calcul du vecteur de direction
			vecStep = new Vec2(step.getX(),step.getY());
			// ajout du 0.5 pour placer l'unité au centre du node
			vecStep = vecStep.add(new Vec2(.5f,.5f));
			
			// soustraction pour déterminer le vecteur de direction + normalisation
			dir = vecStep.sub(this.getModel().getBody().getPosition());
			dir.normalize();
			// déplacement 
			this.getModel().getBody().setLinearVelocity(dir.mul(this.getModel().getSpeed()));
			// modification de l'animation 
			this.getView().playAnimation(TYPE_ANIMATION.WALK);
			// modifiation de l'etape en move
			this.sequencePath = ETAPE.MOVE;
			
		}
		catch(IndexOutOfBoundsException iooe)
		{
			this.getModel().setPaths(null);
			this.getModel().getBody().setLinearVelocity(new Vec2(0,0));
			this.getView().playAnimation(TYPE_ANIMATION.NON);
			this.sequencePath = ETAPE.NONE;
			nodeTake = LevelManager.getLevel().getModel().takeNode((int)this.getModel().getPositionNode().x, (int)this.getModel().getPositionNode().y, this);
		}
	}
	
	private void moveToNextStep() // déplacement jusqu'a la prochaine étape
	{
		Vec2 diff = vecStep.sub(this.getModel().getBody().getPosition());
		if(diff.length() < 0.2f)
		{
			
			// 	si c'est le dernier node, il faut déplacer l'unité jusque sa position réel finale
			if(this.getModel().getPaths().getLength() == this.getModel().getIndicePaths())
			{
				vecStep = this.getModel().getPositionlFinal();
				Vec2 dir = vecStep.sub(this.getModel().getBody().getPosition());
				dir.normalize();
				
				// déplacement 
				this.getModel().getBody().setLinearVelocity(dir.mul(this.getModel().getSpeed()));
				this.getModel().getIndicePathsAndIncrement();

			}
			else
			{
				// l'unité est arrivé sur une étape (step)
				step = null;
				this.getModel().getBody().setLinearVelocity(new Vec2(0f,0f));
				this.sequencePath = ETAPE.GETSTEP;
			
			}
			
		}
		else
		{
			// l'unité n'est pas encore arrivée sur une étape (step)
			dir = vecStep.sub(this.getModel().getBody().getPosition());
												
			dir.normalize();
			
			this.computeRotation(dir); // calcul de la rotation
			
			this.getModel().getBody().setLinearVelocity(this.dir.mul(this.getModel().getSpeed()));
			
		}
	}
	
	

	@Override
	public void update(Time deltaTime) 
	{
		// incrémentation du temps écoulé pour les animations
		this.getView().elapsedAnimationTime += deltaTime.asSeconds();
			
		synchronized(lock)
		{
	
			// switch pour les mouvements
			switch(sequencePath)
			{
				case NONE:  	this.computeRotation(this.getModel().dirFormation); // retourne l'unité en formation
								break;
			
				case GETSTEP : 	if(this.getModel().getPaths() != null)  			// récupère une étape de chemin
									this.computeNextStep();
									break;
					
				case MOVE: 		this.moveToNextStep();
								break; 												// déplacement l'unité
				
				default : 		this.moveToNextStep();
								break;
	
			}
		}
		
		
		
	}
	
	protected float lerp(float value, float start, float end)
	{
	    return start + (end - start) * value;
	}
	
	protected void computeRotation(Vec2 vec) // calcul la rotation de l'unité
	{
		if(vec != null)
		{
			// on crée la class de rotation
			Rot r = new Rot();
			r.s = vec.y;
			r.c = vec.x;
			// receptin de l'angle de rotation
			//float angle = r.getAngle(); 
			// assouplissement en utilisant un lerp
			float angle = lerp(0.2f,this.getModel().getBody().getAngle(), r.getAngle() ); // angle de la tourelle déterminé
			
			this.getModel().getBody().setTransform(this.getModel().getBody().getPosition(), angle);
		}
	}

	
	@Override
	public void onCallsearchPath(Path finalPath) 
	{
		// réception du chemin calculé
		synchronized(lock)
		{
			this.getModel().setIndicePaths(0);
			step = null;
			vecStep = null;
			this.getModel().setPaths(finalPath);
			this.sequencePath = ETAPE.GETSTEP;
			
			// emission sur le réseau
			NetDataUnity data = new NetDataUnity();
			this.prepareModelToNet();
			try {
				data.setModel(this.getModel().clone());
			} catch (CloneNotSupportedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			data.setTypeMessage(TYPE.UPDATE);
			NetSendThread.push(data);
		}

		
	}
	
	public void hit(int hitStrenght)
	{
		// on est frappé, diminution de l'energie
		this.getModel().setEnergy(this.getModel().getEnergy() - hitStrenght);
		// si l'energie est égale à 0 ou inférieur, on meurt
		if(this.getModel().getEnergy() <= 0)
		{
			// ensutie il faut le code réseau
						this.getModel().setKilled(true);
						NetDataUnity data = new NetDataUnity();
						data.setTypeMessage(TYPE.UPDATE);
						this.prepareModelToNet();
						try
						{
							data.setModel(this.getModel().clone());
							NetSendThread.push(data);
						} catch (CloneNotSupportedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
			
			EntityManager.getVectorUnityKilled().add(this);
			
			// ensutie il faut jouer la mort en view
		}
	}
	
	


	public ETAPE getSequencePath() {
		return sequencePath;
	}

	public void setSequencePath(ETAPE sequencePath) {
		this.sequencePath = sequencePath;
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void destroy() 
	{
		
		// libération du node take
		if(this.nodeTake != null)
			this.nodeTake.releaseNode(this);
		// destruction du body
		if(this.getModel().getBody() != null)
			PhysicWorldManager.getWorld().destroyBody(this.getModel().getBody());
		
	}

	@Override
	public void onMouse(MouseEvent buttonEvent) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onKeyboard(KeyEvent keyboardEvent) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMouseMove(MouseEvent event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMousePressed(MouseButtonEvent event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMouseReleased(MouseButtonEvent event) {
		// TODO Auto-generated method stub
		
	}
	
	
		
}
