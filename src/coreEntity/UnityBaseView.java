package coreEntity;

import org.jbox2d.common.Vec2;
import org.jsfml.graphics.Drawable;
import org.jsfml.graphics.RenderStates;
import org.jsfml.graphics.RenderTarget;
import org.jsfml.graphics.Sprite;
import org.jsfml.system.Vector2f;

import corePhysic.PhysicWorldManager;

public abstract class UnityBaseView implements Drawable
{
	protected Sprite sprite;
	
	protected UnityBaseModel model;
	
	protected UnityBaseController controller;
	
	protected  float elapsedAnimationTime; // temps écoulé pour les animations

	public UnityBaseView(UnityBaseModel model, UnityBaseController controller) {
		super();
		this.model = model;
		this.controller = controller;
	}
	
	public abstract void draw(RenderTarget arg0, RenderStates arg1);

	public UnityBaseModel getModel() {
		return model;
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

	protected Vec2 toMeterVec2(Vector2f v)
	{
		return new Vec2(v.x / PhysicWorldManager.getRatioPixelMeter(),v.y / PhysicWorldManager.getRatioPixelMeter());
	}
	
	protected Vector2f toPixelVector2f(Vec2 v)
	{
		return new Vector2f(v.x * PhysicWorldManager.getRatioPixelMeter(), v.y * PhysicWorldManager.getRatioPixelMeter());
	}

	
	
	
}
