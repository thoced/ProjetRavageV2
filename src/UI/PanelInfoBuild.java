package UI;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.stream.Stream;

import org.jsfml.graphics.TextureCreationException;
import org.jsfml.system.Vector2f;

import CoreTexturesManager.TexturesManager;
import coreEntityManager.EntityManager;
import coreEntityManager.EntityManager.CAMP;
import coreGuiRavage.Button;
import coreGuiRavage.IButtonListener;
import coreGuiRavage.Panel;
import coreGuiRavage.ProgressBar;
import coreGuiRavage.ProgressBar.IProgressBarListener;
import coreMessageManager.MessageManager;
import coreMessageManager.MessageRavage;

public class PanelInfoBuild extends Panel implements IButtonListener, IProgressBarListener
{
	private ProgressBar m_barPiquier;

	public PanelInfoBuild(float x, float y, Vector2f size)
			throws TextureCreationException 
	{
		super(x, y, size);
		
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
		// ajout du progress bas
		m_barPiquier = new ProgressBar(new Vector2f(16f,76f),new Vector2f(64f,8f),3f,this); // position,size,temps max, owner pour le call back
		this.addWidget(m_barPiquier);
		
		
		
	}

	@Override
	public void actionListener(String action, Object source)
	{
		switch(action)
		{
			case "CREATE_PIQUIER" :
			{
				// activation de la bare de progression de création de l'unité piquier
				m_barPiquier.startProgressBar();
				
			}

		
			
		
		}
	}

	@Override
	public void onActionProgressBar(ProgressBar owner)
	{
		// TODO Auto-generated method stub
		if(owner == m_barPiquier)
		{
			// création du piquer
			EntityManager.createPiquier();
		}
	}

}
