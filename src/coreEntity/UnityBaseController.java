package coreEntity;

import java.util.List;

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
import coreEvent.IEventCallBack;
import ravage.IBaseRavage;

public  class UnityBaseController implements IBaseRavage,ICallBackAStar,IEventCallBack
{
	protected UnityBaseView view;
	
	protected UnityBaseModel model;
	
	protected Step step = null; // step du chemin
	
	

	public UnityBaseController() {
		super();
		
		// instance de la vue et du model
		
	}
	
	public  void createBody()
	{
		
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

	@Override
	public void update(Time deltaTime) 
	{
		// incrémentation du temps écoulé pour les animations
		this.getView().elapsedAnimationTime += deltaTime.asSeconds();
		// calcul du déplacement
		if(this.getModel().getPaths() != null && step == null)
		{
			try
			{
				step = this.getModel().getPaths().getStep(this.getModel().getIndicePathsAndIncrement());
				// calcul du vecteur de direction
				Vec2 vecStep = new Vec2(step.getX(),step.getY());
				Vec2 dir = vecStep.sub(this.getModel().getBody().getPosition());
				dir.normalize();
				this.getModel().getBody().setLinearVelocity(dir.mul(6));
			}
			catch(IndexOutOfBoundsException iooe)
			{
				this.getModel().getBody().setLinearVelocity(new Vec2(0,0));
			}
		}
		else
		{
			if(step != null)
			{
				// l'unité arrive à destination du step, on place le step à null
				// on vérifie la distance entre le step et la position de l'unité
				Vec2 vecStep = new Vec2(step.getX(),step.getY());
				Vec2 diff = vecStep.sub(this.getModel().getBody().getPosition());
				if(diff.length() < 0.2f)
				{
					step = null;
					
				}
			}
		}
		
		
	
		
				
	}

	
	@Override
	public void onCallsearchPath(Path finalPath) 
	{
		// réception du chemin calculé
		this.getModel().setPaths(finalPath);
		this.getModel().setIndicePaths(0);
		step = null;
		
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
