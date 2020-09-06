package JJack2;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.HashMap;

public class AnimatedActor extends BaseActor
{
    private float elapsedTime;
    private boolean pauseAnimation;
    private Animation activeAnim;
    private String activeName;
    private HashMap<String,Animation> animationStorage;
    
    public AnimatedActor()
    {
        super();
        elapsedTime = 0;
        activeAnim = null;
        activeName = null;
        pauseAnimation = false;
        animationStorage = new HashMap<String,Animation>();
    }

    public void storeAnimation(String name, Animation anim)
    {
        animationStorage.put(name, anim);
        if (activeName == null)
            setActiveAnimation(name);
    }
    
    public void storeAnimation(String name, Texture tex)
    {
        TextureRegion reg = new TextureRegion(tex);
        TextureRegion[] frames = { reg };
        Animation anim = new Animation(1.0f, frames);
        storeAnimation(name, anim);
    }
    
    public void setActiveAnimation(String name)
    {
        if ( !animationStorage.containsKey(name) )
        {
            System.out.println("No animation: " + name);
            return;
        }
            
        if (name.equals(activeName))
            return;
        activeName = name;
        activeAnim = animationStorage.get(name);
        elapsedTime = 0;

        if (getWidth() == 0 || getHeight() == 0) {
            Texture tex = ((TextureRegion) activeAnim.getKeyFrame(0)).getTexture();
            setWidth(tex.getWidth());
            setHeight(tex.getHeight());
        }
    }

    public void setAnimationFrame(int n){
        elapsedTime = n * activeAnim.getFrameDuration();
    }

    public void pauseAnimation(){
        pauseAnimation = true;
    }

    public void startAnimation(){
        pauseAnimation = false;
    }

    public void copy(AnimatedActor original){
        super.copy(original);
        this.elapsedTime = 0;
        this.animationStorage = original.animationStorage;
        this.activeName = new String(original.activeName);
        this.activeAnim = this.animationStorage.get(this.activeName);
    }

    public AnimatedActor clone(){
        AnimatedActor newbie = new AnimatedActor();
        newbie.copy(this);
        return newbie;
    }
    
    public String getAnimationName()
    {  
        return activeName;  
    }

    public void act(float dt)
    {
        super.act( dt );
        if (!pauseAnimation)
            elapsedTime += dt;
    }

    public void draw(Batch batch, float parentAlpha) 
    {
        region.setRegion((TextureRegion) activeAnim.getKeyFrame(elapsedTime) );
        super.draw(batch, parentAlpha);
    }
}