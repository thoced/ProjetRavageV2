package corePhysic;

import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.collision.Manifold;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.contacts.Contact;

import coreEntity.UnityBaseController;
import coreEntity.UnityNetController;

public class ContactManager implements ContactListener {

	private final Vec2 ZERO_VECTOR = new Vec2(0f,0f);
	
	public ContactManager()
	{
		
	}
	
	@Override
	public void beginContact(Contact arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void endContact(Contact arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void postSolve(Contact arg0, ContactImpulse arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void preSolve(Contact l_contact, Manifold arg1)
	{
		// d�sactiovation du contact
		l_contact.setEnabled(false);
		
		// r�ception du bodyA et bodyB
		Body m_bodyA = l_contact.getFixtureA().getBody();
		Body m_bodyB = l_contact.getFixtureB().getBody();
		// r�ception des userData
		Object m_userDataA = m_bodyA.getUserData();
		Object m_userDataB = m_bodyB.getUserData();
		
		
		try
		{
			// il s'agit d'un contact avec autre chose ques des unit�s, on sort
			if(m_userDataA.getClass() == String.class  || m_userDataB.getClass() == String.class)	
				return;
			
			// si il s'agit de deux unit� diff�rente, on active le contact
			if(m_userDataA.getClass() != m_userDataB.getClass())
			{
				// on active le contact
				l_contact.setEnabled(true);
				// on arr�te sur place l'unit�
				m_bodyA.setLinearVelocity(ZERO_VECTOR);
				m_bodyB.setLinearVelocity(ZERO_VECTOR);
				// on attribue l'enemy
				if(m_userDataA.getClass() == UnityNetController.class)
				{
					((UnityBaseController)m_userDataB).getModel().setEnemy((UnityNetController)m_userDataA);
				}
				if(m_userDataB.getClass() == UnityNetController.class)
				{
					((UnityBaseController)m_userDataA).getModel().setEnemy((UnityNetController)m_userDataB);
				}
			}
		
		}
		catch(NullPointerException npe)
		{
			
		}
		

	}

}
