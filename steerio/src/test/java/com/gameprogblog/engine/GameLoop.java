
package com.gameprogblog.engine;

import com.gameprogblog.engine.input.GameInput;

import java.awt.*;


public interface GameLoop
{

	public void onStart( Game game, GameState state );

	public boolean onLoop( Game game, GameState state, GameInput input, Graphics2D gr, Scene scene );
}
