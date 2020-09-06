package JJack2;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.utils.Array;

public class GameUtils {

    // creates an Animation from a single sprite sheet
    // with a subset of the frames, specified by an array

    public static Animation parseSpriteSheet(String fileName, int frameCols, int frameRows, int[] frameIndices,
                                             float frameDuration, PlayMode mode){
        Texture texture = new Texture(Gdx.files.internal(fileName));
        texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        int frameWidth = texture.getWidth() / frameCols;
        int frameHeight = texture.getHeight() / frameRows;

        TextureRegion[][] temp = TextureRegion.split(texture, frameWidth, frameHeight);
        TextureRegion[] frames = new TextureRegion[frameCols * frameRows];
        int index = 0;
        for (int i = 0; i < frameRows; i++) {
            for (int j = 0; j < frameCols; j++) {
                frames[index] = temp[i][j];
                index++;
            }
        }

        Array<TextureRegion> framesArray = new Array<TextureRegion>();
        for (int n = 0; n < frameIndices.length; n++) {

            int i = frameIndices[n];
            framesArray.add(frames[i]);

        }

        return new Animation(frameDuration, framesArray, mode);
    }

    // creates an Animation from a set of image files
    // assumes file name format: fileNamePrefix + N + fileNameSuffix, where 0 <= N < frameCount
    public static Animation parseImageFiles(String fileNamePrefix, String fileNameSuffix, int frameCount, float frameDuration, PlayMode mode){
        TextureRegion[] frames = new TextureRegion[frameCount];

        for (int n = 0; n < frameCount; n++) {
            String fileName = fileNamePrefix + n + fileNameSuffix;
            Texture texture = new Texture(Gdx.files.internal(fileName));
            texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
            frames[n] = new TextureRegion(texture);

        }

        Array<TextureRegion> framesArray = new Array<TextureRegion>(frames);
        return new Animation(frameDuration, framesArray, mode);
    }

    public static Object getContactObject(Contact theContact, Class theClass){
        Object objA = theContact.getFixtureA().getBody().getUserData();
        Object objB = theContact.getFixtureB().getBody().getUserData();

        if (objA.getClass().equals(theClass))
            return objA;
        else if (objB.getClass().equals(theClass))
            return objB;
        else
            return null;
    }

    public static Object getContactObject(Contact theContact, Class theClass, String fixtureName){
        Object objA = theContact.getFixtureA().getBody().getUserData();
        String nameA = (String)theContact.getFixtureA().getUserData();
        Object objB = theContact.getFixtureB().getBody().getUserData();
        String nameB = (String)theContact.getFixtureB().getUserData();

        if (objA.getClass().equals(theClass) && nameA.equals(fixtureName))
            return objA;
        else if (objB.getClass().equals(theClass) && nameB.equals(fixtureName))
            return objB;
        else return null;
    }
}
