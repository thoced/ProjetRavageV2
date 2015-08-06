package coreEntityManager;

import java.util.ArrayList;
import java.util.List;

import org.jbox2d.callbacks.QueryCallback;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.Fixture;

import coreEntity.UnityBaseController;
import coreEntity.UnityNet;

public class ListBodyEnemyForOneRegion implements QueryCallback {

	
	// list des body dans la région
	private List<UnityBaseController> listUnityEnemy;
	
	public ListBodyEnemyForOneRegion()
	{
		// instance de listbody
		this.listUnityEnemy = new ArrayList<UnityBaseController>();
	}
	
	@Override
	public boolean reportFixture(Fixture arg0) 
	{
		//on récupère le body
		Body b = arg0.getBody();
		// si le userdate provient de la class unity on incrément le nb
		if(b.getUserData() != null && b.getUserData().getClass() != String.class && b.getUserData().getClass().getSuperclass() == UnityNet.class)
		{
			// ajout dans la liste body
			this.listUnityEnemy.add((UnityBaseController)b.getUserData());
		}
		
		return true;
	}
	
	public int getNbBodyInList()
	{
		// return du nb de body
		return this.listUnityEnemy.size();
	}

	public List<UnityBaseController> getListEnemy() {
		return listUnityEnemy;
	}

	
	

}
