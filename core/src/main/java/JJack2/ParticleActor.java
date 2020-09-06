package JJack2;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class ParticleActor extends Actor {

    private ParticleEffect particleEffect;

    public ParticleActor(){
        super();
        particleEffect = new ParticleEffect();
    }

    public void load(String pfxFile, String imageDirectory){
        particleEffect.load(Gdx.files.internal(pfxFile), Gdx.files.internal(imageDirectory));
    }

    public void start(){
        particleEffect.start();
    }

    //pauses continuous emitters
    public void stop(){
        particleEffect.allowCompletion();
    }

    public boolean isRunning(){
        return !particleEffect.isComplete();
    }

    public void setPosition(float px, float py){
        for (ParticleEmitter emitter : particleEffect.getEmitters())
            emitter.setPosition(px, py);
    }

    public void act(float dt){
        super.act(dt);
        particleEffect.update(dt);
        if (particleEffect.isComplete() && !particleEffect.getEmitters().first().isContinuous()){
            particleEffect.dispose();
            this.remove();
        }
    }

    public void draw(Batch batch, float parentAlpha){
        particleEffect.draw(batch);
    }

    public ParticleActor clone(){
        ParticleActor newbie = new ParticleActor();
        newbie.particleEffect = new ParticleEffect(this.particleEffect);
        return newbie;
    }
}
