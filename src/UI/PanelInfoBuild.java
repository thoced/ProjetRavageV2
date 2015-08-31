package UI;

import java.io.BufferedWriter;
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
import coreMessageManager.MessageManager;
import coreMessageManager.MessageRavage;

public class PanelInfoBuild extends Panel implements IButtonListener
{
	

	public PanelInfoBuild(float x, float y, Vector2f size)
			throws TextureCreationException 
	{
		super(x, y, size);
		
		// création des boutons
		
		Button button01 = new Button(new Vector2f(16f,10f),new Vector2f(64f,64f));
		if(EntityManager.campSelected == CAMP.BLUE)
			button01.setTexture(TexturesManager.GetTextureByName("ButtonPiquierBlue.png"));
		else
			button01.setTexture(TexturesManager.GetTextureByName("ButtonPiquierYellow.png"));
		// ajout du widget au panel
		this.addWidget(button01);
		button01.setAction("CREATE_PIQUIER"); // creation de l'action
		button01.addListener(this); // ajout du listener
		
		
	}

	@Override
	public void actionListener(String action, Object source)
	{
		switch(action)
		{
			case "CREATE_PIQUIER" :
			{
				EntityManager.createPiquier();break;
				
			}

		}
			
		
	}

}
