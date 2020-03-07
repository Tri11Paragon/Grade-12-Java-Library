package com.brett.world.cameras;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector3f;

import com.brett.DisplayManager;
import com.brett.tools.TerrainArray;
import com.brett.world.VoxelWorld;
import com.brett.world.terrain.Terrain;

public class CreativeFirstPersonCamera extends Camera {
	
	private VoxelWorld world;
	
	public CreativeFirstPersonCamera(Vector3f pos) {
		this.position = pos;
	}
	
	public void assignWorld(VoxelWorld world) {
		this.world = world;
	}
	
	private float speed = 40f;
	private float turnSpeed = 5.0f;
	private float moveAtX = 0;
	private float moveAtY = 0;
	private float moveatZ = 0;
	
	@Override
	public void move() {
		if (!Mouse.isGrabbed())
			return;
		if (Keyboard.isKeyDown(Keyboard.KEY_LMENU)) {
			speed = 5f;
			if (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL))
				speed=1f;
		} else
			speed = 40f;
		
		if (Keyboard.isKeyDown(Keyboard.KEY_W))
			moveAtX = -speed * DisplayManager.getFrameTimeSeconds();
		
		else if (Keyboard.isKeyDown(Keyboard.KEY_S))
			moveAtX = speed * DisplayManager.getFrameTimeSeconds();
		else
			moveAtX = 0;
			
		if (Keyboard.isKeyDown(Keyboard.KEY_A))
			moveatZ = speed * DisplayManager.getFrameTimeSeconds();
		else
		if (Keyboard.isKeyDown(Keyboard.KEY_D))
			moveatZ = -speed * DisplayManager.getFrameTimeSeconds();
		else 
			moveatZ = 0;

		if (Keyboard.isKeyDown(Keyboard.KEY_SPACE))
			moveAtY = speed * DisplayManager.getFrameTimeSeconds();
		else
			moveAtY = 0;
			
		if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT))
			moveAtY = -speed * DisplayManager.getFrameTimeSeconds();
		
		float speed = 30f;
		
		if (Mouse.isGrabbed()) {
			pitch += -Mouse.getDY() * turnSpeed * DisplayManager.getFrameTimeSeconds();
			yaw += Mouse.getDX() * turnSpeed * DisplayManager.getFrameTimeSeconds();
		}
		
		if (Keyboard.isKeyDown(Keyboard.KEY_LEFT))
			yaw += -speed * turnSpeed * DisplayManager.getFrameTimeSeconds();
		if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT))
			yaw += speed * turnSpeed * DisplayManager.getFrameTimeSeconds();
		if (Keyboard.isKeyDown(Keyboard.KEY_UP))
			pitch += -speed * turnSpeed * DisplayManager.getFrameTimeSeconds();
		if (Keyboard.isKeyDown(Keyboard.KEY_DOWN))
			pitch += speed * turnSpeed * DisplayManager.getFrameTimeSeconds();
		
		float dx = (float) (-((moveAtX) * Math.sin(Math.toRadians(yaw)))) + (float)-((moveatZ) * Math.cos(Math.toRadians(yaw)));
		float dy = (float) (moveAtX * Math.sin(Math.toRadians(roll))) + moveAtY;
		float dz = (float) ((moveAtX) * Math.cos(Math.toRadians(yaw))) + (float) -((moveatZ) * Math.sin(Math.toRadians(yaw)));
		
		position.x += dx;
		position.y += dy;
		position.z += dz;
		
		float posx = position.x;
		float posy = position.y;
		float posz = position.z;
		
		//System.out.println(world.chunk.getBlock((int)posx, (int)posy, (int)posz));
		
		float cond = 1.0f;
		// really basic collision.
		if (world.chunk.getBlock((int)posx, (int)posy, (int)posz) != 0) {
			position.x -= dx*cond;
			position.y -= dy*cond;
			position.z -= dz*cond;
		}
		
	}
	
	public void checkCollision(TerrainArray terrains) {
		Terrain terrain = terrains.get(this.position);
		float offset = 1;
		if (terrain == null) {
			if (this.position.y < offset)
				this.position.y = offset;
			return;
		}
		float terrainHeight = terrain.getHeightOfTerrain(getPosition().x, getPosition().z);
		
		if (getPosition().y < terrainHeight + offset) {
			getPosition().y = terrainHeight + offset;
		}
	}
	
}
