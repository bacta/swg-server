
package org.magnos.steer.behavior;

import com.gameprogblog.engine.*;
import com.gameprogblog.engine.input.GameInput;
import org.magnos.steer.vec.Vec2;

import java.awt.*;
import java.awt.event.KeyEvent;


public class SteerDriveExample extends SteerBasicExample
{

	public static void main( String[] args )
	{
		Game game = new SteerDriveExample( DEFAULT_WIDTH, DEFAULT_HEIGHT );
		GameLoop loop = new GameLoopVariable( 0.1f );
		GameScreen screen = new GameScreen( DEFAULT_WIDTH, DEFAULT_HEIGHT, true, loop, game );
		screen.setBackground( Color.black );
		GameScreen.showWindow( screen, "SteerDriveExample" );
	}

	private SteerDrive<Vec2> drive;

	public SteerDriveExample( int w, int h )
	{
		super( w, h );
	}
	
	@Override
	public void start( Scene scene )
	{
		newSprite( Color.blue, 15, 300,  
			drive = new SteerDrive<Vec2>( 1000, 600, 600, 100, true, new Vec2(0, 1000), new Vec2(0, -1000) )
		);
	}

	@Override
	public void input( GameInput input )
	{
		super.input( input );
		
		drive.thrusting = input.keyDown[ KeyEvent.VK_UP ];
		drive.braking = input.keyDown[ KeyEvent.VK_DOWN ];
		drive.turn[0] = input.keyDown[ KeyEvent.VK_RIGHT ];
		drive.turn[1] = input.keyDown[ KeyEvent.VK_LEFT ];
	}
	
}
