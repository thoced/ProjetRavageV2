package UI;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.stream.Stream;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.IntRect;
import org.jsfml.graphics.TextureCreationException;
import org.jsfml.system.Time;
import org.jsfml.system.Vector2f;

import CoreTexturesManager.TexturesManager;
import coreEntityManager.EntityManager;
import coreEntityManager.EntityManager.CAMP;
import coreGuiRavage.Button;
import coreGuiRavage.IButtonListener;
import coreGuiRavage.Image;
import coreGuiRavage.Label;
import coreGuiRavage.Panel;
import coreGuiRavage.ProgressBar;
import coreGuiRavage.ProgressBar.IProgressBarListener;
import coreMessageManager.MessageManager;
import coreMessageManager.MessageRavage;

public class PanelInfoBuild extends Panel implements IButtonListener, IProgressBarListener
{
	private enum TYPE_ACTION_POLL {CREATE_PIQUIER,CREATE_KNIGHT};
	// file d'attnte de construction
	private ArrayBlockingQueue<TYPE_ACTION_POLL> m_pollCreatePiquier; 
	private ArrayBlockingQueue<TYPE_ACTION_POLL> m_pollCreateKnight; 
	// bar de progression pour les piquier
	private ProgressBar m_barPiquier;
	private Label       m_labelFilePiquier;
	// progression knight
	private ProgressBar m_barKnight;
	private Label		m_labelFileKnight;

	public PanelInfoBuild(float x, float y, Vector2f size)
			throws TextureCreationException, IOException 
	{
		super(x, y, size);
		
		// instance de la file d'attente
		m_pollCreatePiquier = new ArrayBlockingQueue<TYPE_ACTION_POLL>(256);
		m_pollCreateKnight = new ArrayBlockingQueue<TYPE_ACTION_POLL>(256);
		
		
		// création des boutons knight
		Button buttonKnight = new Button(new Vector2f(16f,84f),new Vector2f(64f,64f));
		if(EntityManager.campSelected == CAMP.BLUE)
			buttonKnight.setTexture(TexturesManager.GetTextureByName("ButtonKnightBlue.png"));
		else
			buttonKnight.setTexture(TexturesManager.GetTextureByName("ButtonKnightYellow.png"));
		// ajout du widget au panel
		this.addWidget(buttonKnight);
		buttonKnight.setAction("CREATE_KNIGHT"); // creation de l'action
		buttonKnight.addListener(this); // ajout du listener
		// creation du progress bar piquier
		m_barKnight = new ProgressBar(new Vector2f(16f,150f),new Vector2f(64f,4f),1f,this);
		this.addWidget(m_barKnight);
		// création du label de file d'attente pour le piquier
		m_labelFileKnight = new Label(new Vector2f(20f,86f));
		m_labelFileKnight.setColor(new Color(128,128,128,256));
		m_labelFileKnight.setText("");
		this.addWidget(m_labelFileKnight);
		
		
		
		// création des boutons
		// Bouton piquier
		Button button01 = new Button(new Vector2f(16f,10f),new Vector2f(64f,64f));
		if(EntityManager.campSelected == CAMP.BLUE)
			button01.setTexture(TexturesManager.GetTextureByName("ButtonPiquierBlue.png"));
		else
			button01.setTexture(TexturesManager.GetTextureByName("ButtonPiquierYellow.png"));
		// ajout du widget au panel
		this.addWidget(button01);
		button01.setAction("CREATE_PIQUIER"); // creation de l'action
		button01.addListener(this); // ajout du listener
		// creation du progress bar piquier
		m_barPiquier = new ProgressBar(new Vector2f(16f,76f),new Vector2f(64f,4f),1f,this);
		this.addWidget(m_barPiquier);
		// création du label de file d'attente pour le piquier
		m_labelFilePiquier = new Label(new Vector2f(20f,12f));
		m_labelFilePiquier.setColor(new Color(128,128,128,256));
		m_labelFilePiquier.setText("");
		this.addWidget(m_labelFilePiquier);
				
	}
	
	
	private void pollProgress(ArrayBlockingQueue<TYPE_ACTION_POLL> queue,ProgressBar bar)
	{
		if(bar != null && !bar.isInAction())
		{
			if((queue.poll()) != null)
			{
				bar.startProgressBar();
			}
		}
	}

	@Override
	public void actionListener(String action, Object source)
	{
		switch(action)
		{
			case "CREATE_PIQUIER" :
			{
				// ajout du progress bas
					 // position,size,temps max, owner pour le call back
				     // dans la file d'attente
									
					m_pollCreatePiquier.add(TYPE_ACTION_POLL.CREATE_PIQUIER);
					pollProgress(m_pollCreatePiquier,m_barPiquier); // poll
					if(m_pollCreatePiquier.size() > 0) 
						m_labelFilePiquier.setText(String.valueOf(m_pollCreatePiquier.size()));
					else
						m_labelFilePiquier.setText("");
					break;
				
				
			}
			
			case "CREATE_KNIGHT":
				
				m_pollCreateKnight.add(TYPE_ACTION_POLL.CREATE_KNIGHT);
				pollProgress(m_pollCreateKnight,m_barKnight); // poll
				if(m_pollCreateKnight.size() > 0) 
					m_labelFileKnight.setText(String.valueOf(m_pollCreateKnight.size()));
				else
					m_labelFileKnight.setText("");
				break;

		
			
		
		}
		
		
	}

	@Override
	public void onActionProgressBar(ProgressBar owner)
	{
		// TODO Auto-generated method stub
		if(owner == m_barPiquier)
		{
			if(EntityManager.getGamePlayModel().pay(10)) // on pay
			{
					// création du piquer
					EntityManager.createPiquier();
				
				// on regarde si il existe d'autres actions dans la file d'attente
				if((m_pollCreatePiquier.poll()) != null)
				{
					// déclenchement du progress
					m_barPiquier.startProgressBar();
					
					// mise à jour du label de construction
					if(m_pollCreatePiquier.size() > 0) 
						m_labelFilePiquier.setText(String.valueOf(m_pollCreatePiquier.size()));
					else
						m_labelFilePiquier.setText("");
				}
				else
					m_labelFilePiquier.setText("");
			}
		}
		
		if(owner == m_barKnight)
		{
			if(EntityManager.getGamePlayModel().pay(10)) // on pay
			{
					// création du knight
					EntityManager.createKnight();
				
				// on regarde si il existe d'autres actions dans la file d'attente
				if((m_pollCreateKnight.poll()) != null)
				{
					// déclenchement du progress
					m_barKnight.startProgressBar();
					
					// mise à jour du label de construction
					if(m_pollCreateKnight.size() > 0) 
						m_labelFileKnight.setText(String.valueOf(m_pollCreateKnight.size()));
					else
						m_labelFileKnight.setText("");
				}
				else
					m_labelFileKnight.setText("");
			}
		}
	}

}
