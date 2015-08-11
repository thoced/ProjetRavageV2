package coreEntityManager;

import java.io.IOException;
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
import coreEntity.UnityNetController;
import coreEvent.IEventCallBack;
import coreGUI.IRegionSelectedCallBack;
import coreGUIInterface.GuiManager;
import coreGUIInterface.panelFormation;
import coreLevel.LevelManager;
import coreNet.INetManagerCallBack;
import coreNet.NetAddUnity;
import coreNet.NetBase.TYPE;
import coreNet.NetDataUnity;
import coreNet.NetHello;
import coreNet.NetKill;
import coreNet.NetManager;
import coreNet.NetMoveUnity;
import coreNet.NetSendThread;
import coreNet.NetStrike;
import coreNet.NetSynchronize;
import corePhysic.PhysicWorldManager;
import ravage.FrameWork;
import ravage.IBaseRavage;


public class EntityManager implements IBaseRavage,IEventCallBack,IRegionSelectedCallBack,INetManagerCallBack
{
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
	// vecteur des unity du joueur adverse (r�seau)
	//private static List<UnityNetController> vectorUnityNet;
	
	private static Hashtable<Integer,UnityNetController> vectorUnityNet;
	// vecteur net killed
	private static List<UnityNetController> vectorUnityNetKilled;
	
	// test clock
	private Clock clock;
	private Time delta;
	// listdes unités selectionés
	private List<UnityBaseController> listUnitySelected;
	
	// instance du ChooseAngleFormationDrawable
	private ChooseAngleFormationDrawable arrow;
	// variable de vecteur de direction de formation
	private Vec2 dirFormation;
	// variable sur les coordonn�es de souris
	private Vector2f posMouseWorld;

	private static int idTestUnity = 0;
	
	
	
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
		// liste des unités selectionnés
		listUnitySelected = new ArrayList<UnityBaseController>();
		// instance vectorunitynet
		//vectorUnityNet = new ArrayList<UnityNet>();
		vectorUnityNet = new Hashtable<Integer,UnityNetController>();
		vectorUnityNetKilled = new ArrayList<UnityNetController>();
		
		clock = new Clock();
		delta = Time.ZERO;
		
		// on s'accroche au NetManager
		NetManager.attachCallBack(this);
	}
	
	

	@Override
	public void update(Time deltaTime) 
	{
		// on parse les unité
		for(UnityBaseController unity : vectorUnity.values())
		{
			unity.update(deltaTime);
			// ajout test dans le netUnityDatagra
			
		}
		// on parse les unit�s adverses (r�seau)
		for(UnityNetController unity :vectorUnityNet.values())
		{
			unity.update(deltaTime);
		}
		
		
		
		
		if(arrow!=null)
			arrow.update(deltaTime);
		
		
		// recherche des enemy dans les zones
		// pour chaque unity on lance la recherche
		
		for(UnityBaseController unity : this.vectorUnity.values())
		{
			if(unity.getModel().getEnemy() == null) // si aucun enemy n'est encore attribu�.
			{
				// on lance la recherche
				List<UnityNetController> listEnemy = this.searchEnemyZone(unity);
				if(listEnemy != null && listEnemy.size() > 0)
				{
					// on selectionne au hazard
					Random rand = new Random();
					int ind = rand.nextInt(listEnemy.size());
					// on r�cup�re l'enemy
					UnityNetController enemy = listEnemy.get(ind);
					// on attribue l'enemy
					
					unity.getModel().setEnemy(enemy);
					
				}
			}
		}
		
		
		// on regarde si il n'existe pas d'unit� � supprimer dans le vecteur unitykilled
		if(this.vectorUnityKilled.size() > 0)
		{
			for(UnityBaseController u : this.vectorUnityKilled)
			{
				this.vectorUnity.remove(u.getModel().getId());
			}
		}
		this.vectorUnityKilled.clear();
		
		// on regarde si il n'existe pas d'unit� � supprimer dans le vecteur unitykilled Net
			if(this.vectorUnityNetKilled.size() > 0)
			{
				for(UnityNetController u : this.vectorUnityNetKilled)
				{
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
	public void onMousePressed(MouseButtonEvent mouseEvent) 
	{
		
		posMouseWorld = FrameWork.getWindow().mapPixelToCoords(mouseEvent.position);
		float pixels =  PhysicWorldManager.getRatioPixelMeter();
		
		// si c'est un click gauche
		
		if(mouseEvent.asMouseButtonEvent().button == Mouse.Button.LEFT)
		{
					
			// on vide la liste des objets selectionné
			this.listUnitySelected.clear();
			
			Vec2 mousePos = new Vec2(posMouseWorld.x / pixels,posMouseWorld.y / pixels ); 
			for(UnityBaseController unity : vectorUnity.values())
			{
				// si la souris est sur l'unité
				if(unity.getModel().getPosition().sub(mousePos).length() < .5f)
				{
					unity.getModel().setSelected(true);
					this.listUnitySelected.add(unity);
					break;
				}
				
			}
			
			
		}
		else if(mouseEvent.asMouseButtonEvent().button == Mouse.Button.RIGHT)
		{
			Vector2f p = Vector2f.div(posMouseWorld, PhysicWorldManager.getRatioPixelMeter());
			// on teste si on ne clic pas sur un ennimi, si non en rentre dans le code de formation et de destination
			UnityNetController unityPicked = this.getUnityPicked(p.x,p.y);
			if(unityPicked != null)
			{
			
				
					// pour toutes les unit�s selectionn�es, on attribue l'ennemy
					for(UnityBaseController u : this.listUnitySelected)
						u.getModel().setEnemy(unityPicked);
					// pour toutes les unit�s, on cr�e leur position de formation pour attaquer
					this.computeFormationStrike(unityPicked, listUnitySelected, new Vec2(1,0));
				
			}
			else
			{
				// cr�ation de la fleche de selection d'angle de formation
			    if(arrow == null)
			    	arrow  = new ChooseAngleFormationDrawable(posMouseWorld,posMouseWorld);
			}
		}
		
		
	}

	@Override
	public void onKeyboard(KeyEvent keyboardEvent) 
	{
		
		
		if(keyboardEvent.key == Keyboard.Key.A )
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
			// emission sur le r�seau de l'unit�
			NetDataUnity create = new NetDataUnity();
			knight.prepareModelToNet();
			create.setModel(knight.getModel());
			create.setTypeMessage(TYPE.CREATE);
			System.out.println("envoie du header : " );
			NetSendThread.push(create);
		
			
			
			// on ajoute une unit�
			/*Knight knight = new Knight();
			knight.init();
			knight.setPosition(NetManager.getPosxStartFlag(),NetManager.getPosyStartFlag());
			// r�ception de l'id unique pour l'unit�
			knight.setId(EntityManager.getNewIdUnity());
			knight.setMyCamp(EntityManager.getCampSelected());
			EntityManager.getVectorUnity().put(knight.getId(), knight);
			// on envoie sur le r�seau
			NetHeader header = new NetHeader();
			header.setTypeMessage(TYPE.ADD);
			NetAddUnity add = new NetAddUnity();
			add.setPosx(knight.getPositionMeterX());
			add.setPosy(knight.getPositionMeterY());
			add.setTypeUnity(knight.getIdType());
			add.setIdUnity(knight.getId());
			add.setCampUnity(knight.getMyCamp());
			header.setMessage(add)
			try 
			{
				NetManager.PackMessage(header);
				
				//NetManager.SendMessage(header);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
		}
		
		 
		
	}

	@Override
	public void onMouseMove(MouseEvent event) 
	{
		
	}
	
	
	@Override
	public void onMouseReleased(MouseButtonEvent event) 
	{
		
		if(event.button == Button.RIGHT)
		{
		
				// on relache le clic, on r�cup�re la dirction de formation et on lance la formation
				if(arrow != null)
				{
					// on r�cup�re le vecteur de direction pour la formation
					dirFormation = arrow.getVectorDirectionFormation();
					// on calcul la formation
					Vector2f pos = Vector2f.div(posMouseWorld, PhysicWorldManager.getRatioPixelMeter());
				    computeFormation(listUnitySelected,new Vec2(pos.x,pos.y),dirFormation);
				    
				   
				    // suppression de la fleche dans les callback
					arrow.destroy();
					arrow = null;
				}
			
		}

	}

	@Override
	public void onRegionSelected(FloatRect region) 
	{
		
		// on vient de receptionné la region selectionné
		for(UnityBaseController unity : vectorUnity.values())
		{
			if(region.contains(new Vector2f(unity.getModel().getBody().getPosition().x * PhysicWorldManager.getRatioPixelMeter(),
					unity.getModel().getPosition().y * PhysicWorldManager.getRatioPixelMeter())))
					{
						unity.getModel().setSelected(true);
						this.listUnitySelected.add(unity);
						
						// on sort de la s�lection si on d�passe 32
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
		// g�n�ration d'un id unique pour les unit�s
		EntityManager.cptIdUnity++;
		return EntityManager.cptIdUnity;
	}

	

	@Override
	public void onMouse(MouseEvent buttonEvent) {
		// TODO Auto-generated method stub
		
	}
	
	public float getRadian(float degre)
	{
		float radian = (float) (degre * (Math.PI / 180));
		return radian;
	}
	
	
	
	public void computeFormationStrike(UnityBaseController enemy,List<UnityBaseController> listUnity, Vec2 dir)
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
		
		// pour chaque couche on d�termine le nombre de recherche
		while(indListUnity < listUnity.size() && maxboucle < 32)
		{
			UnityBaseController unity = listUnity.get(indListUnity);
			rot.set(this.getRadian(pasDegre));
			vector = new Vec2(rot.s,rot.c);
			
			// on recheche les emplacements
			Vec2 centre = enemy.getModel().getPosition();
			Vec2 pos = centre.add(vector.mul(distance));
			
			boolean obstacle = LevelManager.getLevel().isNodeObstacle((int)pos.x, (int)pos.y);
			if(!obstacle)
			{
				// on d�termine la rotation finale
				Vec2 vFinal = new Vec2(pos.x,pos.y);
				Vec2 vEnemy = enemy.getModel().getPosition();
				// on d�termine le vecteur de direction
				Vec2 vDir = vEnemy.sub(vFinal);
				vDir.normalize();
				
				
				computeDestination(unity,pos,pos, vDir);
				indListUnity++;
			}
			// augmentation du pas
			pasDegre += 45f;
			// max boucle pour la s�curit�
			maxboucle++;
		}
		
	}

	public void computeFormation(List<UnityBaseController> listUnity,Vec2 positionFinal,Vec2 dir)
	{
		
		// variable d�finissant le nombre maximal d'unit� sur une ligne
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
			// on r�cup�re une unit�
			UnityBaseController u = listUnity.get(i);
			// on calcul sa destination
			Vec2 positionFinalAdd = positionFinal.add(offset.add(dir.negate().mul(cptLine * 1.5f )));
			Vec2 posNodeFinal = new Vec2();
			posNodeFinal.x = (int)positionFinalAdd.x;
			posNodeFinal.y = (int)positionFinalAdd.y;
			// ajout du vecteur skew � l'offset
			offset = offset.add(skew.mul(1.5f));
			
			if(this.computeDestination(u, positionFinalAdd, posNodeFinal, dir) == false)
			{
				// aucune position possible pour l'unit�, on la place derri�re
				cptLine++;																	// incr�mentation du nombre de ligne
				positionFinal = cpyPositionFinal;
				offset.setZero();
				i--;

			}
			else
			{
				nbUnityPerLine++;
				if(nbUnityPerLine > 7) 														// le nombre d'unit� sur une ligne d�passe 7
				{
					positionFinal = cpyPositionFinal.clone();									// on replace la position initial
					nbUnityPerLine = 0;														// nbunity par ligne est �gale � 0
					cptLine++;																// incr�mentation du nombre de ligne
					offset.setZero();														// on positionne l'offset � 0
					
				}
				
				
			}
			
			if(cptLine > 12)
				break;
			
			
		}
		
		
		
	
		// on d�fini les positions
		/*float dx = px;
		
		Vector2f posInitial = new Vector2f(dx,py);
		Vector2f dep = posInitial;
	
		Vec2 skew = dir.skew();
		
		for(int i=0;i<listUnity.size();i++)
		{
		
			// on r�cup�re une unit�
			UnityBaseController u = listUnity.get(i);
			// on calcul la position ecran
			Vector2f pos = Vector2f.div(new Vector2f(dep.x,dep.y), PhysicWorldManager.getRatioPixelMeter());
			// on envoie l'unit� sur sa positoin
			if(computeDestination(u,new Vec2(dep.x,dep.y),new Vec2((int)pos.x + 1,(int) pos.y + 1),dir) == false)
			{
				// aucune position possible pour l'unit�, on la place derri�re
				
				//dep = posInitial;
				cptLine++;
				dep = Vector2f.add(posInitial, Vector2f.mul(Vector2f.neg(new Vector2f(dir.x * 20  ,dir.y * 20)),cptLine));
				i--;
				nbUnityPerLine = 0;
				
			}
			else
			{
				// on a d�fini la position final de l'unit�, on incr�mente le nb d'unit� par ligne
				nbUnityPerLine++;
			}
			//dx = dx + 20;
			dep = Vector2f.add(dep, new Vector2f(skew.x * 20,skew.y * 20));
			
			if(nbUnityPerLine > 7)
			{
				//si un ligne est compl�te, on place en dessous
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
		
			// on r�cup�re une unit�
			Unity u = listUnity.get(i);
			// on calcul la position ecran
			Vector2f pos = Vector2f.div(new Vector2f(dx,py), PhysicWorldManager.getRatioPixelMeter());
			// on envoie l'unit� sur sa positoin
			if(u.setTargetPosition(dx,py,(int)pos.x+1,(int)pos.y+1) == false)
			{
				// aucune position possible pour l'unit�, on la place derri�re
				dx = px;
				py = py + 20;	
				i--;
				nbUnityPerLine = 0;
				
			}
			else
			{
				// on a d�fini la position final de l'unit�, on incr�mente le nb d'unit� par ligne
				nbUnityPerLine++;
			}
			dx = dx + 20;
			//dep = Vector2f.add(dep, new Vector2f(skew.x * 20,skew.y * 20));
			dx = dep.x;
			py = dep.y;
			
			if(nbUnityPerLine > 7)
			{
				//si un ligne est compl�te, on place en dessous
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

		   // on r�cup�re la liste des bodys dans la r�gion
		   List<UnityNetController> list = listRegion.getListEnemy();
		   if(list != null)
		   {
			   for(UnityNetController unity : list) // on liste et on r�cup�re l'objet picked
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
		// l'unit� est morte, elle demande � �tre d�truite de la liste
		vectorUnityKilled.add(unity);
		
	}
	
	public static void IamKilledNet(Unity unity)
	{
		// l'unit� est morte, elle demande � �tre d�truite de la liste
			//	vectorUnityNetKilled.add(unity);
	}

	// methode qui recherche les enemys � proximit�
	public List<UnityNetController> searchEnemyZone(UnityBaseController unity)
	{
		// cr�atin du AABB
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
		// cr�atin du AABB
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
	
	public static Vec2 searchPosition(Unity unity,Unity enemy) // recherche d'une place � cot� d'un enemy
	{
		// on trace un vecteur
		Vec2 diff = enemy.getBody().getPosition().sub(unity.getBody().getPosition());
		diff.normalize();
		// on multiplie par 1 pour obtenir la distance 
		Vec2 pos = enemy.getBody().getPosition().add(diff.mul(1f).negate());
		
		return pos;
		
	}
	
	public boolean computeDestination(UnityBaseController unity,Vec2 posFinal,Vec2 posNodeFinal,Vec2 dir)
	{
		// instance de vecteur de formation finale
		unity.getModel().setDirFormation(dir);
		
		unity.getModel().setPositionlFinal(posFinal); // on sp�cifie la position final
		
		// une demande de chemin va �tre effectu�e, on stoppe l'unit� pour �viter le ph�nom�ne de rebond
		unity.getModel().getBody().setLinearVelocity(new Vec2(0f,0f)); // il est arrivé à destination
		
		//this.targetPosition = new Vec2((float)tx + 0.5f,(float)ty + 0.5f);
		unity.getModel().setPositionNodeFinal(posNodeFinal);
		
		
		
		try 
		{
			// si le target position est sur un node noir, on ne fait aucune recherche
			// si on est hors de la map
			if(posNodeFinal.x < 0  || posNodeFinal.x > 374 || posNodeFinal.y < 0 || posNodeFinal.y > 250)
				return false;
				
			if(LevelManager.getLevel().getNodes()[(int) ((posNodeFinal.y * 375) + posNodeFinal.x)].getType() == 0)
			{
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
		// cr�ation d'une unit� enemy r�seau
		UnityNetController controller = new UnityNetController(); // creation du controller pour l'enemy
		controller.setModel(unity.getModel());					  // placement du model obtenu par le r�seau
		controller.getModel().initModel(controller);						  // initialisation du model
		
		UnityBaseView view = new UnityBaseView(controller.getModel(),controller); // cr�ation de la vue pour l'enemy
		controller.setView(view);								  // on sp�cifie la vue au controller
		controller.init();										  // initilisation du controller
		this.vectorUnityNet.put(controller.getModel().getId(), controller); // ajout du controller dans le vecteur
		
	}

	@Override
	public void onUpdateUnity(NetDataUnity unity)
	{
		// r�ception d'une update sur un enemy
		// on r�cup�re le controller dans le vecteur enemy
		UnityNetController controller = this.vectorUnityNet.get(unity.getModel().getId());
		if(controller != null)
		{
			// on remplace le model par le nouveau model arriv�
			controller.setModel(unity.getModel());
			// on initialise le model
			controller.getModel().initModel(controller);
			controller.setSequencePath(ETAPE.GETSTEP); // on sp�cifie au controller la sequence � adopter pourl a recheche de chemin
		}
		else
		{
			this.onCreateUnity(unity); // si l'unit� n'avait pas �t� cr�er (suite probleme r�seau), alors on cr�e l'unit� enemy
		}
			
		
	}


	

}
