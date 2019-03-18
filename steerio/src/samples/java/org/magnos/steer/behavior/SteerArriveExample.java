
package org.magnos.steer.behavior;

import com.gameprogblog.engine.*;
import org.magnos.steer.test.SteerSprite;
import org.magnos.steer.vec.Vec2;

import java.awt.*;


public class SteerArriveExample extends SteerBasicExample
{

	public static void main( String[] args )
	{
		Game game = new SteerArriveExample( DEFAULT_WIDTH, DEFAULT_HEIGHT );
		GameLoop loop = new GameLoopVariable( 0.1f );
		GameScreen screen = new GameScreen( DEFAULT_WIDTH, DEFAULT_HEIGHT, true, loop, game );
		screen.setBackground( Color.black );
		GameScreen.showWindow( screen, "SteerArriveExample" );
	}
	
	private SteerArrive<Vec2> arrive;
	private SteerSprite sprite;
	
	public SteerArriveExample(int w, int h)
	{
		super( w, h );
	}

	@Override
	public void start( Scene scene )
	{
		sprite = newSprite( Color.blue, 15, 300, 
			arrive = new SteerArrive<Vec2>( 1000, mouse, 100, 0, false )
		);
	}
	
	@Override
	public void draw( GameState state, Graphics2D gr, Scene scene )
	{
		super.draw( state, gr, scene );

		if ( drawCircles )
		{
			drawCircle( gr, Color.lightGray, sprite.position, arrive.caution, false );
		}
	}

}
