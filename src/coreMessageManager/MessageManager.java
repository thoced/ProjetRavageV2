package coreMessageManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.jsfml.system.Time;

import ravage.IBaseRavage;

public class MessageManager implements IBaseRavage 
{
	// Model
	private static Model m_model;
	
	// Lock
	private static Lock m_lock;
	
	public MessageManager()
	{
		// instance du model
		m_model = new Model();
		// lock
		m_lock = new ReentrantLock();
	}
	
	public static void sendMessage(MessageRavage message)
	{
		m_lock.lock();
		
		m_model.m_stackMessage.add(message); // push du message dans la pompe
		
		m_lock.unlock();
	}
	
	public static void registrationObject(RegistrationObject object)
	{
		m_model.m_stackObject.add(object); // enregistrement d'un objet voulant utilisé la pompe à messages
	}
	
	
	
	@Override
	public void init() 
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void update(Time deltaTime) 
	{
		// BOUCLE DANS LA POMPE A MESSAGE
		m_lock.lock();
		
		for(MessageRavage message : m_model.m_stackMessage)
		{
			
		}
		
		m_lock.unlock();

	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub

	}
	
	class Model
	{
		// parent controller
		private MessageManager m_controller;
		// pile de messages
		private List<MessageRavage> m_stackMessage;
		// pile d'objet attaché
		private ListRegistration m_stackObject;
		
		
		public Model()
		{
			// parent controller
			m_controller = MessageManager.this;
			// instance de la pile de messages
			m_stackMessage = new ArrayList<MessageRavage>();
			// instance de la pile d'objet atatché
			m_stackObject = new ListRegistration();
		
		}
	}

}
