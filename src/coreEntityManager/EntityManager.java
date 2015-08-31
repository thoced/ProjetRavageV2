package coreEntityManager;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Random;

import org.jbox2d.collision.AABB;
import org.jbox2d.common.Rot;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jsfml.graphics.FloatRect;
import org.jsfml.graphics.RenderStates;
import org.jsfml.graphics.RenderTarget;
import org.jsfml.system.Clock;
import org.jsfml.system.Time;
import org.jsfml.system.Vector2f;
import org.jsfml.system.Vector2i;
import org.jsfml.window.Keyboard;
import org.jsfml.window.Mouse;
import org.jsfml.window.Keyboard.Key;
import org.jsfml.window.Mouse.Button;
import org.jsfml.window.event.Event;
import org.jsfml.window.event.KeyEvent;
import org.jsfml.window.event.MouseButtonEvent;
import org.jsfml.window.event.MouseEvent;

import UI.PanelInfoGold;
import coreAI.AstarManager;
import coreAI.Node;
import coreEntity.KnighController;
import coreEntity.Knight;
import coreEntity.Unity;
import coreEntity.Unity.ANIMATE;
import coreEntity.UnityBaseController;
import coreEntity.UnityBaseController.ETAPE;
import coreEntity.UnityBaseController.TYPEUNITY;
import coreEntity.UnityBaseModel;
import coreEntity.UnityBaseView;
import coreEntity.UnityBaseView.TYPE_ANIMATION;
import coreEntity.UnityNetController;
import coreEvent.IEventCallBack;
import coreGUI.IRegionSelectedCallBack;
import coreGuiRavage.IButtonListener;
import coreLevel.LevelManager;
import coreMessageManager.IPumpMessage;
import coreMessageManager.MessageManager;
import coreMessageManager.MessageRavage;
import coreMessageManager.RegistrationObject;
import coreNet.INetManagerCallBack;
import coreNet.NetBase.TYPE;
import coreNet.NetDataUnity;
import coreNet.NetHello;
import coreNet.NetManager;
import coreNet.NetSendThread;
import coreNet.NetStrike;
import corePhysic.PhysicWorldManager;
import ravage.FrameWork;
import ravage.IBaseRavage;


public class EntityManager implements IBaseRavage,IEventCallBack,IRegionSelectedCallBack,INetManagerCallBack
{
	// Data GamePlay
	private static DataGamePlay gamePlayModel;
	// enum des camps
	public static enum CAMP {BLUE,YELLOW};
	// camp du joueur
	public static CAMP campSelected;
	// compteur d'id 
	private static int cptIdUnity = -1;
	// vecteur des unity du player
	private static Hashtable<Integer,UnityBaseController> vectorUnity;
	// vecteur de killed
	private static List<UnityBaseController> vectorUnityKilled;
	// vecteur des unity du joueur adverse (réseau)
	//private static List<UnityNetController> vectorUnityNet;
	
	private static Hashtable<Integer,UnityNetController> vectorUnityNet;
	// vecteur net killed
	private static List<UnityNetController> vectorUnityNetKilled;
	
	// test clock
	private Clock clock;
	private Time delta;
	// listdes unitÃ©s selectionÃ©s
	private List<UnityBaseController> listUnitySelected;
	
	// instance du ChooseAngleFormationDrawable
	private ChooseAngleFormationDrawable arrow;
	// variable de vecteur de direction de formation
	private Vec2 dirFormation;
	// variable sur les coordonnées de souris
	private Vector2f posMouseWorld;

	private static int idTestUnity = 0;
	// temps écoulé pour la recherche d'enemi
	private float elapsedTimeForSearchNewEnemy = 0f;
	
	
	
	public static CAMP getCampSelected() {
		return campSelected;
	}

	public static void setCampSelected(CAMP campSelected) {
		EntityManager.campSelected = campSelected;
	}
	
	

	@Override
	public void init()
	{
		// TODO Auto-generated method stub
		vectorUnity = new Hashtable<Integer,UnityBaseController>();
		vectorUnityKilled = new ArrayList<UnityBaseController>();
		// liste des unitÃ©s selectionnÃ©s
		listUnitySelected = new ArrayList<UnityBaseController>();
		// instance vectorunitynet
		//vectorUnityNet = new ArrayList<UnityNet>();
		vectorUnityNet = new Hashtable<Integer,UnityNetController>();
		vectorUnityNetKilled = new ArrayList<UnityNetController>();
		
		clock = new Clock();
		delta = Time.ZERO;
		
		// on s'accroche au NetManager
		NetManager.attachCallBack(this);
		// gamePlay
		gamePlayModel = new DataGamePlay();
	
	}
	
	

	@Override
	public void update(Time deltaTime) 
	{
		// update du modelGamePlay
		gamePlayModel.update(deltaTime);
		
		// mise à jour du temps pour la recherche des enemy
		elapsedTimeForSearchNewEnemy += deltaTime.asMilliseconds();
		
		// on parse les unitÃ©
		for(UnityBaseController unity : vectorUnity.values())
		{
			unity.update(deltaTime);
			// ajout test dans le netUnityDatagra
			
		}
		// on parse les unités adverses (réseau)
		for(UnityNetController unity :vectorUnityNet.values())
		{
			unity.update(deltaTime);
		}
		
		if(arrow!=null)
			arrow.update(deltaTime);
		
		
		// recherche des enemy dans les zones
		// pour chaque unity on lance la recherche
		
		if(elapsedTimeForSearchNewEnemy > 2f)
		{
			for(UnityBaseController unity : this.vectorUnity.values())
			{
				if(unity.getModel().getEnemy() == null) // si aucun enemy n'est encore attribué.
				{
					// on lance la recherche
					List<UnityNetController> listEnemy = this.searchEnemyZone(unity);
					if(listEnemy != null && listEnemy.size() > 0)
					{
						// on selectionne au hazard
						Random rand = new Random();
						int ind = rand.nextInt(listEnemy.size());
						// on récupère l'enemy
						UnityNetController enemy = listEnemy.get(ind);
						// on attribue l'enemy
						unity.getModel().setEnemy(enemy);
						
							
					}
						
						
				}
			}
			
			// mise à zero
			elapsedTimeForSearchNewEnemy = 0f;
		}
		
		
		
		// on regarde si il n'existe pas d'unité à supprimer dans le vecteur unitykilled
		if(this.vectorUnityKilled.size() > 0)
		{
			for(UnityBaseController u : this.vectorUnityKilled)
			{
				// destruction propre de l'objet
				u.destroy();
				// enlevement du vecteur
				this.vectorUnity.remove(u.getModel().getId());
			}
		}
		this.vectorUnityKilled.clear();
		
		// on regarde si il n'existe pas d'unité à supprimer dans le vecteur unitykilled Net
			if(this.vectorUnityNetKilled.size() > 0)
			{
				for(UnityNetController u : this.vectorUnityNetKilled)
				{
					// destruction propre de l'objet
					u.destroy();
					// enlevement du vecteur
					this.vectorUnityNet.remove(u.getModel().getId());
				}
			}
		this.vectorUnityNetKilled.clear();
		
	
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}


	/*public static List<UnityNet> getVectorUnityNet() {
		return vectorUnityNet;
	}

	public static void setVectorUnityNet(List<UnityNet> vectorUnityNet) {
		EntityManager.vectorUnityNet = vectorUnityNet;
	}*/

	public static List<UnityBaseController> getVectorUnityKilled() {
		return vectorUnityKilled;
	}

	public static void setVectorUnityKilled(
			List<UnityBaseController> vectorUnityKilled) {
		EntityManager.vectorUnityKilled = vectorUnityKilled;
	}

	public static Hashtable<Integer, UnityBaseController> getVectorUnity() {
		return vectorUnity;
	}

	public static void setVectorUnity(Hashtable<Integer, UnityBaseController> vectorUnity) {
		EntityManager.vectorUnity = vectorUnity;
	}

	

	public static Hashtable<Integer, UnityNetController> getVectorUnityNet() {
		return vectorUnityNet;
	}

	public static List<UnityNetController> getVectorUnityNetKilled() {
		return vectorUnityNetKilled;
	}

	public static void setVectorUnityNet(
			Hashtable<Integer, UnityNetController> vectorUnityNet) {
		EntityManager.vectorUnityNet = vectorUnityNet;
	}

	public static void setVectorUnityNetKilled(
			List<UnityNetController> vectorUnityNetKilled) {
		EntityManager.vectorUnityNetKilled = vectorUnityNetKilled;
	}

	@Override
	public boolean onMousePressed(MouseButtonEvent mouseEvent) 
	{
		
		posMouseWorld = FrameWork.getWindow().mapPixelToCoords(mouseEvent.position);
		float pixels =  PhysicWorldManager.getRatioPixelMeter();
		
		// si c'est un click gauche
		
		if(mouseEvent.asMouseButtonEvent().button == Mouse.Button.LEFT)
		{
					
			// on vide la liste des objets selectionnÃ©
			this.listUnitySelected.clear();
			
			Vec2 mousePos = new Vec2(posMouseWorld.x / pixels,posMouseWorld.y / pixels ); 
			for(UnityBaseController unity : vectorUnity.values())
			{
				// si la souris est sur l'unitÃ©
				if(unity.getModel().getPosition().sub(mousePos).length() < .5f)
				{
					unity.getModel().setSelected(true);
					this.listUnitySelected.add(unity);
					break;
				}
				
			}
			
			return true;
		}
		else if(mouseEvent.asMouseButtonEvent().button == Mouse.Button.RIGHT)
		{
			Vector2f p = Vector2f.div(posMouseWorld, PhysicWorldManager.getRatioPixelMeter());
			// on teste si on ne clic pas sur un ennimi, si non en rentre dans le code de formation et de destination
			UnityNetController unityPicked = this.getUnityPicked(p.x,p.y);
			if(unityPicked != null)
			{
			
				
					// pour toutes les unités selectionnées, on attribue l'ennemy
					for(UnityBaseController u : this.listUnitySelected)
						u.getModel().setEnemy(unityPicked);
					// pour toutes les unités, on crée leur position de formation pour attaquer
					//this.computeFormationStrike(unityPicked, listUnitySelected, new Vec2(1,0));
				
			}
			else
			{	
				// création de la fleche de selection d'angle de formation
			    if(arrow == null)
			    	arrow  = new ChooseAngleFormationDrawable(posMouseWorld,posMouseWorld);
			}
			
			return true;
		}
		
		return false;
	}
	
	public static void createPiquier()
	{
		if(gamePlayModel.pay(10))
		{
		
			KnighController knight = new KnighController();
			knight.getModel().setPosition(new Vec2(NetManager.getPosxStartFlag(),NetManager.getPosyStartFlag()));
			knight.getModel().setSpeed(6f);
			knight.getModel().setId((EntityManager.getNewIdUnity()));
			knight.getModel().setMyCamp(EntityManager.getCampSelected());
			knight.getModel().setIdType(TYPEUNITY.KNIGHT);
			knight.getModel().initModel(knight);
			knight.init();
			EntityManager.getVectorUnity().put(knight.getModel().getId(), knight);
			// emission sur le réseau de l'unité
			NetDataUnity create = new NetDataUnity();
			knight.prepareModelToNet();
			try {
				create.setModel(knight.getModel().clone());
			} catch (CloneNotSupportedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			create.setTypeMessage(TYPE.CREATE);
			
			System.out.println("envoie du header : " );
			NetSendThread.push(create);
			
			// gamePlayModel
			gamePlayModel.setM_nbUnity(getVectorUnity().size());
		}
		
	}

	@Override
	public boolean onKeyboard(KeyEvent keyboardEvent) 
	{
		
		 return false;
		
	}

	
	
	@Override
	public boolean onMouseReleased(MouseButtonEvent event) 
	{
		
		if(event.button == Button.RIGHT)
		{
		
				// on relache le clic, on récupère la dirction de formation et on lance la formation
				if(arrow != null)
				{
					
					
					// on récupère le vecteur de direction pour la formation
					dirFormation = arrow.getVectorDirectionFormation();
					// on calcul la formation
					Vector2f pos = Vector2f.div(posMouseWorld, PhysicWorldManager.getRatioPixelMeter());
				    computeFormation(listUnitySelected,new Vec2(pos.x,pos.y),dirFormation);
				    
				   
				    // suppression de la fleche dans les callback
					arrow.destroy();
					arrow = null;
					
					return true;
				}
				
				
			
		}
		
		return false;

	}

	@Override
	public void onRegionSelected(FloatRect region) 
	{
		
		// on vient de receptionnÃ© la region selectionnÃ©
		for(UnityBaseController unity : vectorUnity.values())
		{
			if(region.contains(new Vector2f(unity.getModel().getBody().getPosition().x * PhysicWorldManager.getRatioPixelMeter(),
					unity.getModel().getPosition().y * PhysicWorldManager.getRatioPixelMeter())))
					{
						unity.getModel().setSelected(true);
						this.listUnitySelected.add(unity);
						
						// on sort de la sélection si on dépasse 32
						if(this.listUnitySelected.size() > 31)
							break;
						
					}
		}
		
	}

	@Override
	public void onHello(NetHello hello) 
	{
		// TODO Auto-generated method stub
		
	}

	

	public static int getNewIdUnity()
	{
		// génération d'un id unique pour les unités
		EntityManager.cptIdUnity++;
		return EntityManager.cptIdUnity;
	}

	

	
	
	public static float getRadian(float degre)
	{
		float radian = (float) (degre * (Math.PI / 180));
		return radian;
	}
	
	
	
	public static void computeFormationStrike(UnityBaseController enemy,List<UnityBaseController> listUnity, Vec2 dir)
	{
		// en partant du centre (enemy), par couche, on fait un tour en utilisant l'objet Rot
		int couche = 0;
		int nbpercouche = 8;
		float pasDegre = 0f;
		float pasRadian = 0f;
		Vec2 vector;
		float distance = 1f;
		int indListUnity = 0;
		int maxboucle = 0;
		
		Rot rot = new Rot();
		rot.set(0f);
		
		// pour chaque couche on détermine le nombre de recherche
		while(indListUnity < listUnity.size() && maxboucle < 32)
		{
			UnityBaseController unity = listUnity.get(indListUnity);
			rot.set(getRadian(pasDegre));
			vector = new Vec2(rot.s,rot.c);
			
			// on recheche les emplacements
			Vec2 centre = enemy.getModel().getPosition();
			Vec2 pos = centre.add(vector.mul(distance));
			
			boolean obstacle = LevelManager.getLevel().getModel().isNodeObstacle((int)pos.x, (int)pos.y);
			if(!obstacle)
			{
				// on détermine la rotation finale
				Vec2 vFinal = new Vec2(pos.x,pos.y);
				Vec2 vEnemy = enemy.getModel().getPosition();
				// on détermine le vecteur de direction
				Vec2 vDir = vEnemy.sub(vFinal);
				vDir.normalize();
				
				
				computeDestination(unity,pos,pos, vDir);
				indListUnity++;
			}
			// augmentation du pas
			pasDegre += 45f;
			// max boucle pour la sécurité
			maxboucle++;
		}
		
	}

	public void computeFormation(List<UnityBaseController> listUnity,Vec2 positionFinal,Vec2 dir)
	{
		
		// variable définissant le nombre maximal d'unité sur une ligne
		int nbUnityPerLine = 0;
		int cptLine = 0;
		Vec2 cpyPositionFinal = positionFinal.clone();
		
		// calcul de l'offset
		dir.normalize();
		//Vec2 offset = dir.skew();
		Vec2 skew = dir.skew();
		Vec2 offset = new Vec2();
		offset.setZero();
	
		for(int i=0;i<listUnity.size();i++)
		{
			// on calcul l'offset
			//Vec2 offset = dir.skew().mul(i*1.5f);
			// on récupère une unité
			UnityBaseController u = listUnity.get(i);
			// on déselectionne tout enemi attribué.
			u.getModel().setIdEnemy(-1);
			
			// on calcul sa destination
			Vec2 positionFinalAdd = positionFinal.add(offset.add(dir.negate().mul(cptLine * 1.5f )));
			Vec2 posNodeFinal = new Vec2();
			posNodeFinal.x = (int)positionFinalAdd.x;
			posNodeFinal.y = (int)positionFinalAdd.y;
			// ajout du vecteur skew à l'offset
			offset = offset.add(skew.mul(1.5f));
			
			if(this.computeDestination(u, positionFinalAdd, posNodeFinal, dir) == false)
			{
				// aucune position possible pour l'unité, on la place derrière
				cptLine++;																	// incrémentation du nombre de ligne
				positionFinal = cpyPositionFinal;
				offset.setZero();
				i--;

			}
			else
			{
				nbUnityPerLine++;
				if(nbUnityPerLine > 7) 														// le nombre d'unité sur une ligne dépasse 7
				{
					positionFinal = cpyPositionFinal.clone();									// on replace la position initial
					nbUnityPerLine = 0;														// nbunity par ligne est égale à 0
					cptLine++;																// incrémentation du nombre de ligne
					offset.setZero();														// on positionne l'offset à 0
					
				}
				
				
			}
			
			if(cptLine > 12)
				break;
			
			
		}
		
		
		
	
		// on défini les positions
		/*float dx = px;
		
		Vector2f posInitial = new Vector2f(dx,py);
		Vector2f dep = posInitial;
	
		Vec2 skew = dir.skew();
		
		for(int i=0;i<listUnity.size();i++)
		{
		
			// on récupère une unité
			UnityBaseController u = listUnity.get(i);
			// on calcul la position ecran
			Vector2f pos = Vector2f.div(new Vector2f(dep.x,dep.y), PhysicWorldManager.getRatioPixelMeter());
			// on envoie l'unité sur sa positoin
			if(computeDestination(u,new Vec2(dep.x,dep.y),new Vec2((int)pos.x + 1,(int) pos.y + 1),dir) == false)
			{
				// aucune position possible pour l'unité, on la place derrière
				
				//dep = posInitial;
				cptLine++;
				dep = Vector2f.add(posInitial, Vector2f.mul(Vector2f.neg(new Vector2f(dir.x * 20  ,dir.y * 20)),cptLine));
				i--;
				nbUnityPerLine = 0;
				
			}
			else
			{
				// on a défini la position final de l'unité, on incrémente le nb d'unité par ligne
				nbUnityPerLine++;
			}
			//dx = dx + 20;
			dep = Vector2f.add(dep, new Vector2f(skew.x * 20,skew.y * 20));
			
			if(nbUnityPerLine > 7)
			{
				//si un ligne est complète, on place en dessous
				nbUnityPerLine = 0;
				cptLine++;
				
				dep = Vector2f.add(posInitial, Vector2f.mul(Vector2f.neg(new Vector2f(dir.x * 20  ,dir.y * 20)),cptLine));
				//dep = Vector2f.add(dep, Vector2f.neg(new Vector2f(dir.x * 20,dir.y * 20)));

			}
			
			
			if(cptLine > 12)
				break;
			
		}
		
		/*
		for(int i=0;i<listUnity.size();i++)
		{
		
			// on récupère une unité
			Unity u = listUnity.get(i);
			// on calcul la position ecran
			Vector2f pos = Vector2f.div(new Vector2f(dx,py), PhysicWorldManager.getRatioPixelMeter());
			// on envoie l'unité sur sa positoin
			if(u.setTargetPosition(dx,py,(int)pos.x+1,(int)pos.y+1) == false)
			{
				// aucune position possible pour l'unité, on la place derrière
				dx = px;
				py = py + 20;	
				i--;
				nbUnityPerLine = 0;
				
			}
			else
			{
				// on a défini la position final de l'unité, on incrémente le nb d'unité par ligne
				nbUnityPerLine++;
			}
			dx = dx + 20;
			//dep = Vector2f.add(dep, new Vector2f(skew.x * 20,skew.y * 20));
			dx = dep.x;
			py = dep.y;
			
			if(nbUnityPerLine > 7)
			{
				//si un ligne est complète, on place en dessous
				nbUnityPerLine = 0;
				dx = px;
				py = py + 20;
				
			
				
				
			}
			
		}*/
		

	}
	
	public UnityNetController getUnityPicked(float x,float y)
	{
		   AABB tree = new AABB();
		   tree.lowerBound.set(new Vec2(x - 2f,y - 2f));
		   tree.upperBound.set(new Vec2(x + 2f,y + 2f));
		   ListBodyEnemyForOneRegion listRegion = new ListBodyEnemyForOneRegion();
		   PhysicWorldManager.getWorld().queryAABB(listRegion, tree);

		   // on récupère la liste des bodys dans la région
		   List<UnityNetController> list = listRegion.getListEnemy();
		   if(list != null)
		   {
			   for(UnityNetController unity : list) // on liste et on récupère l'objet picked
			   {
	
				   Vec2 diff = new Vec2(x,y).sub(unity.getModel().getPosition());
				   System.out.println("lenght : " + diff.length() );
				    
				   if(diff.length() < 0.5f)
				   {
					   // on est sur le body qui est sur la position de la souris
					   return unity;
				   }
			   }
		   }
		
		return null;
	}
	
	public static void IamKilled(UnityBaseController unity)
	{
		// l'unité est morte, elle demande à être détruite de la liste
		vectorUnityKilled.add(unity);
		
	}
	
	public static void IamKilledNet(Unity unity)
	{
		// l'unité est morte, elle demande à être détruite de la liste
			//	vectorUnityNetKilled.add(unity);
	}

	// methode qui recherche les enemys à proximité
	public List<UnityNetController> searchEnemyZone(UnityBaseController unity)
	{
		// créatin du AABB
		AABB region = new AABB();
		region.lowerBound.set(unity.getModel().getPosition().sub(new Vec2(10,10)));
		region.upperBound.set(unity.getModel().getPosition().add(new Vec2(10,10)));
		
		// recherche
		ListBodyEnemyForOneRegion queryCallBack = new ListBodyEnemyForOneRegion();
		PhysicWorldManager.getWorld().queryAABB(queryCallBack, region);
		
		return queryCallBack.getListEnemy();
		
	}
	
	public static UnityNetController searchEnemyZoneNear(Unity unity)
	{
		// créatin du AABB
		AABB region = new AABB();
		region.lowerBound.set(unity.getBody().getPosition().sub(new Vec2(10,10)));
		region.upperBound.set(unity.getBody().getPosition().add(new Vec2(10,10)));
		
		// recherche
		ListBodyEnemyForOneRegion queryCallBack = new ListBodyEnemyForOneRegion();
		PhysicWorldManager.getWorld().queryAABB(queryCallBack, region);
		
		List<UnityNetController> list = queryCallBack.getListEnemy();
		// on regarde l'enemy le plus proche
		if(list.size() > 0)
		{
			UnityNetController best = list.get(0); // on prend le premier
			for(UnityNetController u : list)
			{
				if((best.getModel().getPosition().sub(unity.getBody().getPosition()).length() > (u.getModel().getBody().getPosition().sub(u.getModel().getBody().getPosition()).length())))
					{
						best = u;
					}
			}
			
			return best;
		}
		
		return null;
	}
	
	public static Vec2 searchPosition(UnityBaseController owner,UnityNetController enemy) // recherche d'une place à coté d'un enemy
	{
		// on trace un vecteur
		Vec2 diff = enemy.getModel().getPosition().sub(owner.getModel().getPosition());
		diff.normalize();
		// on multiplie par 1 pour obtenir la distance 
		return  enemy.getModel().getPosition().add(diff.mul(1f).negate());
			
	}
	
	public static boolean computeDestination(UnityBaseController unity,Vec2 posFinal,Vec2 posNodeFinal,Vec2 dir)
	{
		// instance de vecteur de formation finale
		unity.getModel().setDirFormation(dir);
		
		unity.getModel().setPositionlFinal(posFinal); // on spécifie la position final
		
		// une demande de chemin va être effectuée, on stoppe l'unité pour éviter le phénomène de rebond
		unity.getModel().getBody().setLinearVelocity(new Vec2(0f,0f)); // il est arrivÃ© Ã  destination
		
		//this.targetPosition = new Vec2((float)tx + 0.5f,(float)ty + 0.5f);
		unity.getModel().setPositionNodeFinal(posNodeFinal);
		
		
		
		try 
		{
			// si le target position est sur un node noir, on ne fait aucune recherche
			// si on est hors de la map
			if(posNodeFinal.x < 0  || posNodeFinal.x > 374 || posNodeFinal.y < 0 || posNodeFinal.y > 250)
				return false;
				
			if(LevelManager.getLevel().getModel().getNodes()[(int) ((posNodeFinal.y * 375) + posNodeFinal.x)].getType() == 0)
			{
				// enlevement de la réservation du node
				if(unity.getModel().getNodeReserved() != null)
					ReservationManager.remove(unity.getModel().getNodeReserved());
				// réservation du node
				ReservationManager.add(unity.getModel().getPositionNodeFinal(), unity);
				// Lancement recherche
				AstarManager.askPath(unity, unity.getModel().getPositionNode(), unity.getModel().getPositionNodeFinal()); // classic
				
				return true;
			}
			else
			{
				return false; // return false car il n'y pas de destination possible
			}
			
		} catch (InterruptedException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		  catch(ArrayIndexOutOfBoundsException ex)
		{
			  ex.printStackTrace();
		}
		return false;
	}

	@Override
	public void onCreateUnity(NetDataUnity unity) 
	{
		// création d'une unité enemy réseau
		UnityNetController controller = new UnityNetController(); // creation du controller pour l'enemy
		controller.setModel(unity.getModel());					  // placement du model obtenu par le réseau
		controller.getModel().initModel(controller);			  // initialisation du model
		
		UnityBaseView view = new UnityBaseView(controller.getModel(),controller); // création de la vue pour l'enemy
		controller.setView(view);								  // on spécifie la vue au controller
		controller.init();										  // initilisation du controller
		this.vectorUnityNet.put(controller.getModel().getId(), controller); // ajout du controller dans le vecteur
		
	}

	@Override
	public void onUpdateUnity(NetDataUnity unity)
	{
		// réception d'une update sur un enemy
		// on récupère le controller dans le vecteur enemy
		UnityNetController controller = this.vectorUnityNet.get(unity.getModel().getId());
		if(controller != null)
		{
			// on remplace le model par le nouveau model arrivé
			controller.setModel(unity.getModel());
			System.out.println("STREI " + unity.getModel().getStreightStrike());
			// on initialise le model
			controller.getModel().initModel(controller);
			controller.setSequencePath(ETAPE.GETSTEP); // on spécifie au controller la sequence à adopter pourl a recheche de chemin
			
			// ----------------------------------------------------------------------------------------
			// vérification de la mort
			
		}
		else
		{
			this.onCreateUnity(unity); // si l'unité n'avait pas été créer (suite probleme réseau), alors on crée l'unité enemy
		}
			
		
	}

	@Override
	public boolean onMouse(MouseEvent buttonEvent) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onMouseMove(MouseEvent event) {
		// TODO Auto-generated method stub
		return false;
		
		
	}
	
	public static DataGamePlay getGamePlayModel()
	{
		return gamePlayModel;
	}
	
	// Class contenant le model gameplay du jeu
	class DataGamePlay implements IBaseRavage
	{
		private final int m_maxUnity = 128;
		
		private int m_nbUnity = 0;
		
		private int m_goldCoin = 250;
		
		private float elapsedTimeForGold = 0f;
		
		
		
		public DataGamePlay()
		{
			
		}

		/**
		 * @return the m_maxUnity
		 */
		public int getM_maxUnity() {
			return m_maxUnity;
		}

		/**
		 * @return the m_nbUnity
		 */
		public int getM_nbUnity() {
			return m_nbUnity;
		}

		/**
		 * @return the m_goldCoin
		 */
		public int getM_goldCoin() {
			return m_goldCoin;
		}


		/**
		 * @param m_nbUnity the m_nbUnity to set
		 */
		public void setM_nbUnity(int m_nbUnity) 
		{
			this.m_nbUnity = m_nbUnity;
			// on modifie l'affichage du Panel
			PanelInfoGold.setM_labelNbTroops(this.m_nbUnity);
		}

		/**
		 * @param m_goldCoin the m_goldCoin to set
		 */
		public void setM_goldCoin(int m_goldCoin) {
			this.m_goldCoin = m_goldCoin;
		}
		
		public boolean pay(int goldPay)
		{
			this.m_goldCoin-= goldPay;
			if(this.m_goldCoin < 0)
			{
				this.m_goldCoin+= goldPay;
				return false;
			}
			return true;
		}
		
		public boolean win(int goldWin)
		{
			this.m_goldCoin+= goldWin;
			return true;
		}
		
		

		@Override
		public void init() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void update(Time deltaTime) 
		{
			elapsedTimeForGold  += deltaTime.asSeconds();
			if(elapsedTimeForGold > 1f)
			{
				elapsedTimeForGold = 0f;
				this.win(1);
			}
			// update des affichages
			PanelInfoGold.setM_labelNbTroops(this.m_nbUnity);
			PanelInfoGold.setM_labelGoldCoin(this.m_goldCoin);
			
		}

		@Override
		public void destroy() {
			// TODO Auto-generated method stub
			
		}
	}

	

	

}
