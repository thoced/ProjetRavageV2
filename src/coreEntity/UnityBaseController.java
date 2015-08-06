package coreEntity;

import java.util.List;

import org.jbox2d.dynamics.Body;
import org.jsfml.system.Time;
import org.newdawn.slick.util.pathfinding.Path;
import org.newdawn.slick.util.pathfinding.navmesh.NavPath;

import coreAI.ICallBackAStar;
import coreAI.Node;
import ravage.IBaseRavage;

public  class UnityBaseController implements IBaseRavage,ICallBackAStar
{
	protected UnityBaseView view;
	
	protected UnityBaseModel model;
	
	protected Body body;
	
	

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

	public Body getBody() {
		return body;
	}

	public void setBody(Body body) {
		this.body = body;
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
		
	}

	@Override
	public void onCallSearchPath(List<Node> finalPath) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onCallSearchPath(NavPath finalPath) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onCallsearchPath(Path finalPath) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}
	
	
		
}
