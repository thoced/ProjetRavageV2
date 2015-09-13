package coreEntityManager;

import java.util.ArrayList;
import java.util.List;

import org.jbox2d.common.Vec2;
import org.jsfml.graphics.Drawable;
import org.jsfml.graphics.RenderStates;
import org.jsfml.graphics.RenderTarget;
import org.jsfml.system.Time;

import coreEntity.UnityBaseController;
import coreLevel.LevelManager;
import ravage.IBaseRavage;

// -------------------------------------
//
// Class FormationMovable (Controller)
//
// -------------------------------------

public class FormationMovable implements IBaseRavage
{
	// Model 
	private FormationMovableModel m_model;
	// Vue
	private FormationMovableView m_view;
		
	
	
	public FormationMovable()
	{
		super();
		
		// instance du model
		m_model = new FormationMovableModel();
		// instance de la vue
		m_view = new FormationMovableView();
		// création du point d'ancrage
		
	}
	
	public void moveFormation(List<IFormationMovable> unityMovables, Vec2 positionDestination)
	{
		// on regarde si l'emplacement de destination n'est pas sur un node noire
		if(!LevelManager.getLevel().getModel().isNodeObstacle((int)positionDestination.x,(int) positionDestination.y))
		{
			// ce n'est pas un obstacle, on détermine la forme (pattern) de la formation en fonction du nombre d'unité et de la spécificité du terrain
			int nbUnity = unityMovables.size();
			int nbCouche = (nbUnity - 1) / 8; 
			// utilisation d'un cercle concentrique
			
			int minY = -1;
			int minX = -1;
			int maxY = 1;
			int maxX = 1;
			
			for(int y=minY;y<=maxY;y++)
			{
				for(int x=minX;x<=maxX;x++)
				{
					if(!LevelManager.getLevel().getModel().isNodeObstacle(x,y))
					{
						// c'est libre on place le 
					}
				}
			}
			
		}
	}
	
	

	public FormationMovableModel getMmodel() {
		return m_model;
	}

	public FormationMovableView getView() {
		return m_view;
	}

	public void setModel(FormationMovableModel m_model) {
		this.m_model = m_model;
	}

	public void setView(FormationMovableView m_view) {
		this.m_view = m_view;
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub

	}

	@Override
	public void update(Time deltaTime) {
		// TODO Auto-generated method stub

	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub

	}
	
	// -------------------------------------
	//
	// Class Model
	//
	// -------------------------------------
	
	public class FormationMovableModel
	{
		// ancre
		private Anchor m_anchor;
		// List des Slots
		private List<SlotFormation> m_slots;
		
		public FormationMovableModel()
		{
			// instance de la liste des slots
			m_slots = new ArrayList<SlotFormation>();
			// instance de l'ancre
			m_anchor = new Anchor();
		}

		public Anchor getAnchor() {
			return m_anchor;
		}
		
		
	}
	
	
	
	// -------------------------------------
	//
	// Class View
	//
	// -------------------------------------
	
	public class FormationMovableView implements Drawable
	{

		@Override
		public void draw(RenderTarget render, RenderStates state) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	// -------------------------------------
	//
	// Class SlotFormation
	//
	// -------------------------------------
	
	public class SlotFormation
	{
		// position relative à l'ancre
		private Vec2 m_positionRelative; 
		
		// position absolue dans l'espace (map)
		private Vec2 m_positionAbsolue;
		
		// parent ancre
		private Anchor m_parentAnchor;
		
		// unité attribuée
		private IFormationMovable m_unityMovable;
		
		public SlotFormation(Anchor parentAnchor,Vec2 positionRelative)
		{
			// parent Anchor
			m_parentAnchor = parentAnchor;
			// position relative à l'ancre
			m_positionRelative = positionRelative;
			// calcul de la position absolue
			m_positionAbsolue = m_positionRelative.add(m_parentAnchor.m_position);
		}
		
		public void attributeUnity(IFormationMovable unityMovable)
		{
			m_unityMovable = unityMovable;
		}
		
		
		
	}
	
	// -------------------------------------
	//
	// Interface IFormationMovable
	//
	// -------------------------------------
	
	public interface IFormationMovable
	{
		public void setSlotPosition(Vec2 slotPosition);
		
	}
	
	// -------------------------------------
	//
	// Class Anchor
	//
	// -------------------------------------
	
	public class Anchor
	{
		// position de l'ancre
		public Vec2 m_position;
	}
	
	

}
