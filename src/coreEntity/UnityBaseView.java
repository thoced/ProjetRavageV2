package coreEntity;

import org.jbox2d.common.Vec2;
import org.jsfml.graphics.Drawable;
import org.jsfml.graphics.IntRect;
import org.jsfml.graphics.RenderStates;
import org.jsfml.graphics.RenderTarget;
import org.jsfml.graphics.Sprite;
import org.jsfml.system.Vector2f;

import corePhysic.PhysicWorldManager;

public  class UnityBaseView implements Drawable
{
	protected Sprite sprite;
	
	protected UnityBaseController controller;
	
	protected  float elapsedAnimationTime; 				// temps écoulé pour les animations
	
	public enum TYPE_ANIMATION  {NON,WALK,STRIKE};	// type d'animatino possible
	
	protected TYPE_ANIMATION currentTypeAnimation;				// animation en cours

		
	protected  int NB_FRAME_BY_SECOND = 25;
	protected  int NB_TOTAL_FRAME = 27;
	protected  int MIN_IND_FOR_WALK = 0;
	protected  int MAX_IND_FOR_WALK = 10;  // 4
	protected  int MIN_IND_FOR_STRIKE = 11;
	protected  int MAX_IND_FOR_STRIKE = 26;
	
	protected int WIDTH_FRAME = 80;
	protected int HEIGHT_FRAME = 80;
	
	public UnityBaseView(UnityBaseModel model, UnityBaseController controller) {
		super();
		this.controller = controller;
		
		// appel à la méthode prépare animation pour instancier le rectAnimations
		//this.getModel().setAnimations(new Animations());
		this.prepareAnimations(this.getController().getModel().getAnimations());
	}
	
	public  void prepareAnimations(Animations animations)
	{
		
	}
	
	public void draw(RenderTarget arg0, RenderStates arg1)
	{
		// on modifie le sprite grace au model
		Vec2 pos = this.getController().getModel().getPosition();
		this.sprite.setPosition(this.toPixelVector2f(pos));
		this.sprite.setRotation((float)((this.getController().getModel().getBody().getAngle() * 180f) / Math.PI) % 360f);
		
		// animation
		switch(this.currentTypeAnimation)
		{
			case NON: this.noneAnimation();break;
		
			case WALK: this.walkAnimation();break;
			
			case STRIKE : this.strikeAnimation();break;
		}
		
		// affichage
		arg0.draw(sprite);
	}
	
	private void walkAnimation()
	{
		// on calcul l'animation
		int ind = (int) (NB_FRAME_BY_SECOND * this.elapsedAnimationTime);
		if(ind > MAX_IND_FOR_WALK)
		{
			ind = MIN_IND_FOR_WALK;
			this.elapsedAnimationTime = 0f;
		}
		this.sprite.setTextureRect(this.getController().getModel().getAnimations().getInd(ind));
	}
	
	private void strikeAnimation()
	{
		// on calcul l'animation
		int ind = (int) (NB_FRAME_BY_SECOND * this.elapsedAnimationTime) + MIN_IND_FOR_STRIKE; // 5 étant l'offset
		if(ind > MAX_IND_FOR_STRIKE)
		{
			ind = MIN_IND_FOR_STRIKE;
			this.playAnimation(TYPE_ANIMATION.NON);
			
		}
		this.sprite.setTextureRect(this.getController().getModel().getAnimations().getInd(ind));
		
	}
	
	private void noneAnimation()
	{
		this.sprite.setTextureRect(this.getController().getModel().getAnimations().getInd(0));
	}

	
	public UnityBaseController getController() {
		return controller;
	}

	public Sprite getSprite() {
		return sprite;
	}

	public void setSprite(Sprite sprite) {
		this.sprite = sprite;
	}
	
	

	public TYPE_ANIMATION getCurrentTypeAnimation() {
		return currentTypeAnimation;
	}

	public void playAnimation(TYPE_ANIMATION currentTypeAnimation) 
	{
		this.currentTypeAnimation = currentTypeAnimation; // spécifie le type d'animation.
						
		if(this.currentTypeAnimation != TYPE_ANIMATION.WALK) // si l'animation est de marcher, on ne place pas le temps à 0 car l'animation se joue en boucle
			this.elapsedAnimationTime = 0f;
	}

	protected Vec2 toMeterVec2(Vector2f v)
	{
		return new Vec2(v.x / PhysicWorldManager.getRatioPixelMeter(),v.y / PhysicWorldManager.getRatioPixelMeter());
	}
	
	protected Vector2f toPixelVector2f(Vec2 v)
	{
		return new Vector2f(v.x * PhysicWorldManager.getRatioPixelMeter(), v.y * PhysicWorldManager.getRatioPixelMeter());
	}

	public int getNB_FRAME_BY_SECOND() {
		return NB_FRAME_BY_SECOND;
	}

	public int getMAX_IND_FOR_STRIKE() {
		return MAX_IND_FOR_STRIKE;
	}

	public void setNB_FRAME_BY_SECOND(int nB_FRAME_BY_SECOND) {
		NB_FRAME_BY_SECOND = nB_FRAME_BY_SECOND;
	}

	public void setMAX_IND_FOR_STRIKE(int mAX_IND_FOR_STRIKE) {
		MAX_IND_FOR_STRIKE = mAX_IND_FOR_STRIKE;
	}

	public int getMIN_IND_FOR_WALK() {
		return MIN_IND_FOR_WALK;
	}

	public int getMAX_IND_FOR_WALK() {
		return MAX_IND_FOR_WALK;
	}

	public int getMIN_IND_FOR_STRIKE() {
		return MIN_IND_FOR_STRIKE;
	}

	public void setMIN_IND_FOR_WALK(int mIN_IND_FOR_WALK) {
		MIN_IND_FOR_WALK = mIN_IND_FOR_WALK;
	}

	public void setMAX_IND_FOR_WALK(int mAX_IND_FOR_WALK) {
		MAX_IND_FOR_WALK = mAX_IND_FOR_WALK;
	}

	public void setMIN_IND_FOR_STRIKE(int mIN_IND_FOR_STRIKE) {
		MIN_IND_FOR_STRIKE = mIN_IND_FOR_STRIKE;
	}

	public int getNB_TOTAL_FRAME() {
		return NB_TOTAL_FRAME;
	}

	public int getWIDTH_FRAME() {
		return WIDTH_FRAME;
	}

	public int getHEIGHT_FRAME() {
		return HEIGHT_FRAME;
	}

	public void setNB_TOTAL_FRAME(int nB_TOTAL_FRAME) {
		NB_TOTAL_FRAME = nB_TOTAL_FRAME;
	}

	public void setWIDTH_FRAME(int wIDTH_FRAME) {
		WIDTH_FRAME = wIDTH_FRAME;
	}

	public void setHEIGHT_FRAME(int hEIGHT_FRAME) {
		HEIGHT_FRAME = hEIGHT_FRAME;
	}

	
	
	
}
