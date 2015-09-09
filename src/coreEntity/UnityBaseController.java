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
import coreEntityManager.BloodManager;
import coreEntityManager.EntityManager;
import coreEntityManager.NodeReserved;
import coreEntityManager.ReservationManager;
import coreEvent.IEventCallBack;
import coreLevel.LevelManager;
import coreMessageManager.IPumpMessage;
import coreMessageManager.MessageRavage;
import coreNet.NetBase.TYPE;
import coreNet.NetDataUnity;
import coreNet.NetManager;
import coreNet.NetSendThread;
import corePhysic.PhysicWorldManager;
import ravage.IBaseRavage;

public class UnityBaseController implements IBaseRavage, ICallBackAStar,
		IEventCallBack,IPumpMessage {
	protected UnityBaseView view;

	protected UnityBaseModel model;

	protected Step step = null; // step du chemin

	protected Vec2 vecStep = null; // vecteur de déplacement step

	protected Vec2 dir = null;

	protected Object lock;

	protected ETAPE m_sequencePath = ETAPE.NONE;

	protected Node nodeTake = null; // node pris lors d'un déplacement
	
	protected boolean m_isOnNewNextStep = false;
	
	protected boolean	 m_isLastStep = true;
	
	protected Vec2	  m_nextStep;
	
	

	public enum ETAPE {
		 MOVE, STRIKE,NONE
	};

	public enum TYPEUNITY {
		KNIGHT
	};

	public UnityBaseController() {
		super();
		lock = new Object();
		// instance de la vue et du model

	}

	public void prepareModelToNet() {
		// prépare le model pour être envoyé au réseau car body et enemy ne sont
		// pas sérialisés
		// this.getModel().setPosition(this.getModel().getBody().getPosition());
		// this.getModel().setRotation(this.getModel().getBody().getAngle());
		/*if (this.getModel().getEnemy() != null)
			this.getModel().setIdEnemy(
					this.getModel().getEnemy().getModel().getId());*/
		this.getModel()
				.setOrigineSprite(this.getView().getSprite().getOrigin());

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
		if (this.model != null) // si il existe déja un model, il faut supprimer
								// le body
		{
			PhysicWorldManager.getWorld().destroyBody(this.model.getBody());
		}
		this.model = model;
	}

	private boolean checkNodeFree() {
		if (LevelManager.getLevel().getModel()
				.isNodeFree(step.getX(), step.getY(), this) != true) {
			this.getModel().DecrementIndice();
			this.getModel().getBody().setLinearVelocity(new Vec2(0, 0));
			// this.getView().playAnimation(TYPE_ANIMATION.NON);
			// on assigne le nouvelle enemy
			// this.getModel().setEnemy(LevelManager.getLevel().getModel().getUnityOnNode(step.getX(),
			// step.getY()));
			return false; // retourne false car le node est occupé, il faut
							// stopper la progression
		}
		// on libère sur celui où l'on se trouve
		if (nodeTake != null)
			nodeTake.releaseNode(this);
		// on take le node
		nodeTake = LevelManager.getLevel().getModel()
				.takeNode(step.getX(), step.getY(), this);

		return true; // pas de node occupé
	}

	
	
	private void computeNextStep() // on récupère une étape de chemion
	{
		
	}

	private void moveToNextStep() // déplacement jusqu'a la prochaine étape
	{
		
	}
	
	public void move()
	{
		this.setSequence(ETAPE.MOVE);
		m_isOnNewNextStep = true;
		m_isLastStep = false;
		this.getView().playAnimation(TYPE_ANIMATION.WALK);
	}
	
	public void stop()
	{
		this.getModel().getBody().setLinearVelocity(new Vec2(0f,0f));
		if(m_isLastStep)
		{
			this.getModel().setPaths(null);
			this.setSequence(ETAPE.NONE);
			this.getView().playAnimation(TYPE_ANIMATION.NON);
		}
	}
	
	public void strike()
	{
		this.setSequence(ETAPE.STRIKE);
		this.getView().playAnimation(TYPE_ANIMATION.STRIKE);
		
	}
	
	public void updateMove()
	{	
		try
		{
			
			if(m_isOnNewNextStep && !m_isLastStep)
			{
				m_nextStep = this.getNextStep();
				m_isOnNewNextStep = false;
			}
			else
			{
				if(m_nextStep != null)
				{
					Vec2 dirStep = m_nextStep.sub(this.getModel().getPosition());
					dirStep.normalize();
					Vec2 velocity = dirStep.mul(this.getModel().getSpeed());
					this.getModel().getBody().setLinearVelocity(velocity);
					
					// calcul la rotation
					this.computeRotation(dirStep);
					
					if(this.getModel().getPosition().sub(m_nextStep).length() < 0.5f)
					{
						m_isOnNewNextStep = true;
						this.stop();
					}
				}
					
	
			}
		}
		catch(LastStepException lse)
		{
			m_isLastStep = true;
		}
	}
	
	// -------------------------------------------------------
	// récupère la prochaine position dans le chemin déterminé
	// retourne un VEC2
	// lance une exception si c'est la dernière destination avec la position finale
	//
	// -------------------------------------------------------
	
	
	private Vec2 getNextStep() throws LastStepException
	{
		if(this.getModel().getPaths() != null)
		{
			if(this.getModel().getIndicePaths() < this.getModel().getPaths().getLength())
			{
				Step stepPath = this.getModel().getPaths().getStep(this.getModel().getIndicePathsAndIncrement());
				Vec2 step = new Vec2(stepPath.getX(),stepPath.getY());
				step = step.add(new Vec2(0.5f,0.5f));
				return step;
			}
			else
			{
				
				throw new LastStepException(this.getModel().getPositionlFinal());
			}
		}
		
		return null;
		
	}
	
	
	
	

	@Override
	public void update(Time deltaTime) {
		// incrémentation du temps écoulé pour les animations
		this.getView().elapsedAnimationTime += deltaTime.asSeconds();
		
		// arret constant de la velocity
		this.getModel().getBody().setLinearVelocity(new Vec2(0f,0f));
		
		switch(m_sequencePath)
		{
			case NONE: this.computeRotation(this.getModel().getDirFormation());break;
			
			
			case MOVE: this.updateMove();break;
			
			case STRIKE: break;
			
			
			
			
		}
		
		
	}

	protected float lerp(float value, float start, float end) {
		return start + (end - start) * value;
	}

	protected void computeRotation(Vec2 vec) // calcul la rotation de l'unité
	{
		if (vec != null) {

			// on crée la class de rotation
			Rot r = new Rot();
			r.s = vec.y;
			r.c = vec.x;
			// receptin de l'angle de rotation
			// float angle = r.getAngle();
			// assouplissement en utilisant un lerp
			float angle = lerp(0.2f, this.getModel().getBody().getAngle(),
					r.getAngle()); // angle de la tourelle déterminé

			this.getModel()
					.getBody()
					.setTransform(this.getModel().getBody().getPosition(),
							angle);
		}
	}

	@Override
	public void onCallsearchPath(Path finalPath) {
		// réception du chemin calculé

		synchronized (lock) 
		{
			this.getModel().setIndicePaths(0);
			this.getModel().setPaths(finalPath);
			this.move();

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

	public void hit(int hitStrenght) {
		// on est frappé, diminution de l'energie
		this.getModel().setEnergy(this.getModel().getEnergy() - hitStrenght);
		// on joue un peu de sang
		BloodManager.addBlood(this.getModel().getPosition());
		// si l'energie est égale à 0 ou inférieur, on meurt
		if (this.getModel().getEnergy() <= 0)
		{
			// ensutie il faut le code réseau
			this.getModel().setKilled(true);
			NetDataUnity data = new NetDataUnity();
			data.setTypeMessage(TYPE.UPDATE);
			this.prepareModelToNet();
			try {
				data.setModel(this.getModel().clone());
				NetSendThread.push(data);
			} catch (CloneNotSupportedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// suppresin de l'unité dans le vecteur
			this.destroy();
			EntityManager.getVectorUnity().remove(this.getModel().getId());
			

			// ensutie il faut jouer la mort en view, on place un cadavre
			BloodManager.addUnityKilled(this.getModel().getPosition(), this.getModel().getMyCamp());
			
		}
	}

	public ETAPE getSequencePath() {
		return m_sequencePath;
	}

	public void setSequence(ETAPE sequencePath)
	{
		m_sequencePath = sequencePath;
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub

	}

	@Override
	public void destroy() {

		// libération du node take
		if (this.nodeTake != null)
			this.nodeTake.releaseNode(this);
		// destruction du body
		if (this.getModel().getBody() != null)
			PhysicWorldManager.getWorld()
					.destroyBody(this.getModel().getBody());

	}

	@Override
	public boolean onMouse(MouseEvent buttonEvent) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onKeyboard(KeyEvent keyboardEvent) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onMouseMove(MouseEvent event) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onMousePressed(MouseButtonEvent event) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onMouseReleased(MouseButtonEvent event) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void OnPumpMessage(MessageRavage message) {
		// TODO Auto-generated method stub
		
		
	}
	
	public class LastStepException extends Exception
	{
		private Vec2 m_lastStep;

		public LastStepException(Vec2 m_lastStep)
		{
			this.m_lastStep = m_lastStep;
		}
		
		public Vec2 getLastStep() {
			return m_lastStep;
		}

		public void setLastStep(Vec2 m_lastStep) {
			this.m_lastStep = m_lastStep;
		}
		
		
	}
	



}
