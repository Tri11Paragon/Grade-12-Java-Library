package com.brett.tools;

import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import com.brett.renderer.world.Chunk;
import com.brett.world.VoxelWorld;
import com.brett.world.blocks.Block;
import com.brett.world.cameras.Camera;
import com.tester.Main;

/**
*
* @author brett
* @date Feb. 19, 2020
*/

public class MouseBlockPicker {

	
	// http://antongerdelan.net/opengl/raycasting.html
	
	// amount of times the binary search can run
	private static final int RECURSION_COUNT = 200;
	private static final float RAY_RANGE = 6;

	private Vector3f currentRay = new Vector3f();

	private Matrix4f projectionMatrix;
	private Matrix4f viewMatrix;
	private Camera camera;
	
	private VoxelWorld world;

	public MouseBlockPicker(Camera cam, Matrix4f projection, VoxelWorld terrain) {
		camera = cam;
		projectionMatrix = projection;
		viewMatrix = Maths.createViewMatrix(camera);
		this.world = terrain;
	}
	
	public Vector3f getCurrentTerrainPoint() {
		if (intersectionInRange(0, RAY_RANGE, currentRay)) {
			///System.out.println("SEACHING");
			return binarySearch(0, 0, RAY_RANGE, currentRay);
		}else
			return null;
	}
	
	public int getCurrentBlockPoint() {
		if (intersectionInRange(0, RAY_RANGE, currentRay)) {
			Vector3f pos = accountForFloatErrors(binarySearch(0, 0, RAY_RANGE, currentRay));
			Chunk c = getTerrain(pos.x, pos.z);
			if (c == null)
				return 0;
			return c.getBlock((int)(pos.x)%16, (int)pos.y, (int)(pos.z)%16);
		}else
			return 0;
	}
	
	@Deprecated
	Vector3f z = new Vector3f(0,0,0);
	/**
	 * Old please use setCurrentBlockPointNEW
	 * this one has issues.
	 */
	@Deprecated
	public Vector3f setCurrectBlockPoint(int block) {
		if (intersectionInRange(0, RAY_RANGE, currentRay)) {
			Vector3f pos = accountForFloatErrors(binarySearch(0, 0, RAY_RANGE, currentRay));
			try {
				Chunk c = getTerrain(pos.x, pos.z);
				if (c == null)
					return pos;
				Main.hitent.setPosition(pos);
				System.out.println(pos);
				System.out.println((int)(pos.x) + " " + (int)pos.y + " " + (int)(pos.z));
				System.out.println((int)(pos.x)%16 + " " + (int)pos.y + " " + (int)(pos.z)%16);
				// TODO: REMOVE THIS
				Block.blocks.get(c.getBlock((int)(pos.x)%16, (int)pos.y, (int)(pos.z)%16)).playBreakSound((int)(pos.x), (int)pos.y, (int)(pos.z));
				c.setBlock((int)(pos.x)%16, (int)pos.y, (int)(pos.z)%16, block);
				c.remesh();
				return pos;
			} catch (Exception e) {}
		}
		return z;
	}
	
	public void setCurrentBlockPointNEW(int block) {
		Vector3f pos = camera.getPosition();
		System.out.println(currentRay.x + " " + currentRay.y + " " + currentRay.z);
		Vector3f currentRay = biasVector(this.currentRay);
		for (float i = currentRay.x; i < currentRay.x*RAY_RANGE; i++) {
			for (float j = currentRay.y; j < currentRay.y*RAY_RANGE; j++) {
				for (float k = currentRay.z; k < currentRay.z*RAY_RANGE; k++) {
					Vector3f posadj = new Vector3f(pos.x + i, pos.y + j, pos.z + k);
					System.out.println(posadj.x + " " + posadj.z);
					Chunk c = getTerrain(posadj.x, posadj.z);
					if (c == null)
						continue;
					Main.hitent.setPosition(posadj);
					System.out.println(c.getBlock((int)(posadj.x)%16, (int)posadj.y, (int)(posadj.z)%16));
					if (c.getBlock((int)(posadj.x)%16, (int)posadj.y, (int)(posadj.z)%16) == 0)
						continue;
					c.setBlock((int)(posadj.x)%16, (int)posadj.y, (int)(posadj.z)%16, block);
					c.remesh();
					System.out.println(posadj);
					System.out.println((int)(posadj.x) + " " + (int)posadj.y + " " + (int)(posadj.z));
					System.out.println((int)(posadj.x)%16 + " " + (int)posadj.y + " " + (int)(posadj.z)%16);
					
				}
			}
		}
	}

	public Vector3f getCurrentRay() {
		return currentRay;
	}

	public void update() {
		viewMatrix = Maths.createViewMatrix(camera);
		currentRay = calculateMouseRay();
	}

	private Vector3f calculateMouseRay() {
		float mouseX = Display.getWidth()/2;
		float mouseY = Display.getHeight()/2;
		Vector2f normalizedCoords = getNormalisedDeviceCoordinates(mouseX, mouseY);
		Vector4f clipCoords = new Vector4f(normalizedCoords.x, normalizedCoords.y, -1.0f, 1.0f);
		Vector4f eyeCoords = toEyeCoords(clipCoords);
		Vector3f worldRay = toWorldCoords(eyeCoords);
		return worldRay;
	}

	private Vector3f toWorldCoords(Vector4f eyeCoords) {
		Matrix4f invertedView = Matrix4f.invert(viewMatrix, null);
		Vector4f rayWorld = Matrix4f.transform(invertedView, eyeCoords, null);
		Vector3f mouseRay = new Vector3f(rayWorld.x, rayWorld.y, rayWorld.z);
		mouseRay.normalise();
		return mouseRay;
	}

	private Vector4f toEyeCoords(Vector4f clipCoords) {
		Matrix4f invertedProjection = Matrix4f.invert(projectionMatrix, null);
		Vector4f eyeCoords = Matrix4f.transform(invertedProjection, clipCoords, null);
		return new Vector4f(eyeCoords.x, eyeCoords.y, -1f, 0f);
	}

	private Vector2f getNormalisedDeviceCoordinates(float mouseX, float mouseY) {
		float x = (2.0f * mouseX) / Display.getWidth() - 1f;
		float y = (2.0f * mouseY) / Display.getHeight() - 1f;
		return new Vector2f(x, y);
	}
	
	// terrain stuff beyond this point
	//**********************************************************
	
	private Vector3f getPointOnRay(Vector3f ray, float distance) {
		Vector3f camPos = camera.getPosition();
		Vector3f start = new Vector3f(camPos.x, camPos.y, camPos.z);
		Vector3f scaledRay = new Vector3f(ray.x * distance, ray.y * distance, ray.z * distance);
		return Vector3f.add(start, scaledRay, null);
	}
	
	private Vector3f binarySearch(int count, float start, float finish, Vector3f ray) {
		float half = start + ((finish - start) / 2f);
		if (count >= RECURSION_COUNT) {
			Vector3f endPoint = getPointOnRay(ray, half);
			Chunk terrain = getTerrain(endPoint.getX(), endPoint.getZ());
			if (terrain != null) {
				return endPoint;
			} else {
				return null;
			}
		}
		if (intersectionInRange(start, half, ray)) {
			return binarySearch(count + 1, start, half, ray);
		} else {
			return binarySearch(count + 1, half, finish, ray);
		}
	}

	private boolean intersectionInRange(float start, float finish, Vector3f ray) {
		Vector3f startPoint = getPointOnRay(ray, start);
		Vector3f endPoint = getPointOnRay(ray, finish);
		if (!isUnderGround(startPoint) && isUnderGround(endPoint)) {
			return true;
		} else {
			return false;
		}
	}

	private boolean isUnderGround(Vector3f testPoint) {
		Chunk terrain = getTerrain(testPoint.getX(), testPoint.getZ());
		float height = 0;
		if (terrain != null) {
			height = terrain.getHeight((int)testPoint.getX() % 16, (int)testPoint.getZ() % 16) - 0.0001f;
		}
		if (testPoint.y < height) {
			return true;
		} else {
			return false;
		}
	}

	private Chunk getTerrain(float worldX, float worldZ) {
		return world.chunk.getChunk((int) (worldX/Chunk.x), (int) (worldZ/Chunk.z));
	}
	
	private Vector3f biasVector(Vector3f v) {
		v.x += 1;
		v.y += 1;
		v.z += 1;
		return v;
	}
	
	private Vector3f accountForFloatErrors(Vector3f x) {
		if (x == null)
			return x;
		x.x = Math.round(x.x);
		x.y = Math.round(x.y);
		x.z = Math.round(x.z);
		return x;
	}
	
	
}
