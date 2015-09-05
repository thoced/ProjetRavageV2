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

	protected Vec2 vecStep = null; // vecteur de d�placement step

	protected Vec2 dir = null;

	protected Object lock;

	protected ETAPE sequencePath = ETAPE.NONE;

	protected Node nodeTake = null; // node pris lors d'un d�placement

	public enum ETAPE {
		GETSTEP, MOVE, NONE
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
		// pr�pare le model pour �tre envoy� au r�seau car body et enemy ne sont
		// pas s�rialis�s
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
		if (this.model != null) // si il existe d�ja un model, il faut supprimer
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
			return false; // retourne false car le node est occup�, il faut
							// stopper la progression
		}
		// on lib�re sur celui o� l'on se trouve
		if (nodeTake != null)
			nodeTake.releaseNode(this);
		// on take le node
		nodeTake = LevelManager.getLevel().getModel()
				.takeNode(step.getX(), step.getY(), this);

		return true; // pas de node occup�
	}

	private void computeNextStep() // on r�cup�re une �tape de chemion
	{
		try {
			step = this.getModel().getPaths()
					.getStep(this.getModel().getIndicePathsAndIncrement());
			// on v�rifie si le node n'est pas occup�
			// if(this.checkNodeFree() != true)
			// return;

			// calcul du vecteur de direction
			vecStep = new Vec2(step.getX(), step.getY());
			// ajout du 0.5 pour placer l'unit� au centre du node
			vecStep = vecStep.add(new Vec2(.5f, .5f));

			// soustraction pour d�terminer le vecteur de direction +
			// normalisation
			dir = vecStep.sub(this.getModel().getBody().getPosition());
			dir.normalize();
			// d�placement
			this.getModel().getBody()
					.setLinearVelocity(dir.mul(this.getModel().getSpeed()));
			// modification de l'animation
			this.getView().playAnimation(TYPE_ANIMATION.WALK);
			// modifiation de l'etape en move
			this.sequencePath = ETAPE.MOVE;

		} catch (IndexOutOfBoundsException iooe) {
			this.getModel().setPaths(null);
			this.getModel().getBody().setLinearVelocity(new Vec2(0, 0));
			this.getView().playAnimation(TYPE_ANIMATION.NON);
			this.sequencePath = ETAPE.NONE;
			nodeTake = LevelManager
					.getLevel()
					.getModel()
					.takeNode((int) this.getModel().getPositionNode().x,
							(int) this.getModel().getPositionNode().y, this);
		}
	}

	private void moveToNextStep() // d�placement jusqu'a la prochaine �tape
	{
		Vec2 diff = vecStep.sub(this.getModel().getBody().getPosition());
		if (diff.length() < 0.5f) {

			// si c'est le dernier node, il faut d�placer l'unit� jusque sa
			// position r�el finale
			if (this.getModel().getPaths().getLength() == this.getModel().getIndicePaths()) {
				vecStep = this.getModel().getPositionlFinal();
				Vec2 dir = vecStep.sub(this.getModel().getBody().getPosition());
				dir.normalize();

				// d�placement
				this.getModel().getBody()
						.setLinearVelocity(dir.mul(this.getModel().getSpeed()));
				this.getModel().getIndicePathsAndIncrement();

			} else {
				// l'unit� est arriv� sur une �tape (step)
				step = null;
				this.getModel().getBody().setLinearVelocity(new Vec2(0f, 0f));
				this.sequencePath = ETAPE.GETSTEP;

			}

		} else {
			// l'unit� n'est pas encore arriv�e sur une �tape (step)
			dir = vecStep.sub(this.getModel().getBody().getPosition());

			dir.normalize();

			this.computeRotation(dir); // calcul de la rotation

			this.getModel()
					.getBody()
					.setLinearVelocity(this.dir.mul(this.getModel().getSpeed()));

		}
	}

	@Override
	public void update(Time deltaTime) {
		// incr�mentation du temps �coul� pour les animations
		this.getView().elapsedAnimationTime += deltaTime.asSeconds();

		synchronized (lock) {

			// on stoppe le tout
			this.getModel().getBody().setLinearVelocity(new Vec2(0f, 0f));
			// switch pour les mouvements
			switch (sequencePath) {
			case NONE:
				this.computeRotation(this.getModel().dirFormation); // retourne// l'unit�// en// formation
				break;

			case GETSTEP:
				if (this.getModel().getPaths() != null) // r�cup�re une �tape de
														// chemin
					this.computeNextStep();
				break;

			case MOVE:
				this.moveToNextStep();
				break; // d�placement l'unit�

			default:
				this.moveToNextStep();
				break;

			}
		}

	}

	protected float lerp(float value, float start, float end) {
		return start + (end - start) * value;
	}

	protected void computeRotation(Vec2 vec) // calcul la rotation de l'unit�
	{
		if (vec != null) {

			// on cr�e la class de rotation
			Rot r = new Rot();
			r.s = vec.y;
			r.c = vec.x;
			// receptin de l'angle de rotation
			// float angle = r.getAngle();
			// assouplissement en utilisant un lerp
			float angle = lerp(0.2f, this.getModel().getBody().getAngle(),
					r.getAngle()); // angle de la tourelle d�termin�

			this.getModel()
					.getBody()
					.setTransform(this.getModel().getBody().getPosition(),
							angle);
		}
	}

	@Override
	public void onCallsearchPath(Path finalPath) {
		// r�ception du chemin calcul�

		synchronized (lock) {
			this.getModel().setIndicePaths(0);
			step = null;
			vecStep = null;
			this.getModel().setPaths(finalPath);
			this.sequencePath = ETAPE.GETSTEP;

			// emission sur le r�seau
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
		// on est frapp�, diminution de l'energie
		this.getModel().setEnergy(this.getModel().getEnergy() - hitStrenght);
		// on joue un peu de sang
		BloodManager.addBlood(this.getModel().getPosition());
		// si l'energie est �gale � 0 ou inf�rieur, on meurt
		if (this.getModel().getEnergy() <= 0)
		{
			// ensutie il faut le code r�seau
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

			// suppresin de l'unit� dans le vecteur
			EntityManager.getVectorUnityKilled().add(this);

			// ensutie il faut jouer la mort en view, on place un cadavre
			BloodManager.addUnityKilled(this.getModel().getPosition(), this.getModel().getMyCamp());
			
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

		// lib�ration du node take
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




}
