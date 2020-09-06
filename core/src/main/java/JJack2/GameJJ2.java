package JJack2;

import com.badlogic.gdx.ApplicationAdapter;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class GameJJ2 extends BaseGame {
    @Override
    public void create() {
        setScreen(new GameScreen(this));
    }
}