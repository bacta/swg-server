package com.gameprogblog.engine.core;

import com.gameprogblog.engine.GameState;
import com.gameprogblog.engine.Scene;

import java.awt.*;


public interface Entity
{
	public void update( GameState state, Scene scene );
	public void draw( GameState state, Graphics2D gr, Scene scene );
	public boolean isExpired();
	public void expire();
	public void onExpire();
}
