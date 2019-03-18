
package org.magnos.steer.behavior;

import com.gameprogblog.engine.*;
import org.magnos.steer.SteerSet;
import org.magnos.steer.target.TargetLocal;
import org.magnos.steer.test.SteerSprite;
import org.magnos.steer.vec.Vec2;

import java.awt.*;


public class SteerToExample extends SteerBasicExample
{
	
	public static void main( String[] args )
	{
		Game game = new SteerToExample( DEFAULT_WIDTH, DEFAULT_HEIGHT );
		GameLoop loop = new GameLoopVariable( 0.1f );
		GameScreen screen = new GameScreen( DEFAULT_WIDTH, DEFAULT_HEIGHT, true, loop, game );
		screen.setBackground( Color.black );
		GameScreen.showWindow( screen, "SteerToExample" );
	}

	private SteerSprite sprite;
	private TargetLocal<Vec2> targetLocal;

	public SteerToExample( int w, int h )
	{
		super( w, h );
	}
	
	@Override
	public void start( Scene scene )
	{
		sprite = newSprite( Color.blue, 15, 300, new SteerSet<Vec2>( 1000, 
			new SteerTo<Vec2>( 1000, targetLocal = new TargetLocal<Vec2>( mouse, 200 ), false ), 
			new SteerDrive<Vec2>( 100, 0, 0, 100 )
		));
	}

	@Override
	public void draw( GameState state, Graphics2D gr, Scene scene )
	{
		super.draw( state, gr, scene );

		if (drawCircles)
		{
			drawCircle( gr, Color.gray, sprite.position, targetLocal.maximum, false);	
		}
	}

}
