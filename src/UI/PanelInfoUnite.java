package UI;

import java.util.List;

import org.jsfml.graphics.IntRect;
import org.jsfml.graphics.Texture;
import org.jsfml.graphics.TextureCreationException;
import org.jsfml.system.Vector2f;

import coreEntity.UnityBaseController;
import coreGuiRavage.Image;
import coreGuiRavage.ImageEnergy;
import coreGuiRavage.Panel;

public class PanelInfoUnite extends Panel {

	public PanelInfoUnite(float x, float y, Vector2f size)
			throws TextureCreationException {
		super(x, y, size);
		
		// on spécifie au panel qu'il ne peut être déplacé
				this.setM_isMovable(false);
	}
	
	public  void setGroupUnity(List<UnityBaseController> list)
	{
		if(list!=null)
		{
			// suppression de tous les widgets
			this.removeAllWidget();
			// création de la variable x
			float x = 24f;
			// on parse
			for(UnityBaseController u : list)
			{
				ImageEnergy ima = new ImageEnergy(u,new Vector2f(x,24f));
				this.addWidget(ima);
				x+=36f;
				
			}
		}
	}



}
