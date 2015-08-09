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
import coreEvent.IEventCallBack;
import coreNet.NetDataUnity;
import coreNet.NetHeader;
import coreNet.NetHeader.TYPE;
import coreNet.NetManager;
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

	public void setModel(UnityBaseModel model) {
		this.model = model;
	}
	
	private void computeNextStep()  // on récupère une étape de chemion
	{
		try
		{
			step = this.getModel().getPaths().getStep(this.getModel().getIndicePathsAndIncrement());
			// calcul du vecteur de direction
			vecStep = new Vec2(step.getX(),step.getY());
			// ajout du 0.5 pour placer l'unité au centre du node
			vecStep = vecStep.add(new Vec2(.5f,.5f));
			
			// soustraction pour déterminer le vecteur de direction + normalisation
			dir = vecStep.sub(this.getModel().getBody().getPosition());
			System.out.println("dir " + dir);
			dir.normalize();
			// déplacement 
			this.getModel().getBody().setLinearVelocity(dir.mul(this.getModel().getSpeed()));
			System.out.println(dir);
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
			System.out.println("index out");
			this.sequencePath = ETAPE.NONE;
		}
	}
	
	private void moveToNextStep()
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
	
	protected void computeRotation(Vec2 vec)
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
			NetHeader header = new NetHeader();
			NetDataUnity data = new NetDataUnity();
			this.prepareModelToNet();
			data.setModel(this.getModel());
			header.setMessage(data);
			header.setTypeMessage(TYPE.UPDATE);
			try 
			{
				NetManager.PackMessage(header);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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
	public void destroy() {
		// TODO Auto-generated method stub
		
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
