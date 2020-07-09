package com.brett.world.tools;

import org.joml.Vector3f;
import org.joml.Vector3i;

import com.brett.engine.cameras.ICamera;
import com.brett.world.GameRegistry;
import com.brett.world.World;
import com.brett.world.block.Block;

/**
* @author Brett
* @date Jul. 8, 2020
*/

public class MouseBlockPicker {
	
	// same some GC by reusing memory.
	private static Vector3f cur = new Vector3f();
	private static Vector3f pos = new Vector3f();
	private static Vector3i worldpos = new Vector3i();
	private static int xoff, yoff, zoff;
	
	/**
	 * @param world
	 * @param camera
	 * @param range range of the ray
	 * @param replacement the block to replace the closest non air block with this, set to -1 if you don't wish to remove
	 * @return the block at the closest non air pos
	 */
	public static Block getBlockMine(World world, ICamera camera, float range, short replacement) {
		cur.x = RayCasting.currentRay.x * range;
		cur.y = RayCasting.currentRay.y * range;
		cur.z = RayCasting.currentRay.z * range;
		pos.set(RayCasting.currentRay);
		float xStep = cur.x / 12;
		float yStep = cur.y / 12;
		float zStep = cur.z / 12;
		for (int i = 0; i < 12; i++) {
			worldpos.x = (int) ((pos.x += xStep) + camera.getPosition().x);
			worldpos.y = (int) ((pos.y += yStep) + camera.getPosition().y);
			worldpos.z = (int) ((pos.z += zStep) + camera.getPosition().z);
			// terrible solution to this problem
			if (worldpos.x < 1)
				xoff = -1;
			else
				zoff = 0;
			if (worldpos.y < 1)
				yoff = -1;
			else
				yoff = 0;
			if (worldpos.z < 1)
				zoff = -1;
			else
				zoff = 0;
			Block block = GameRegistry.getBlock(world.getBlock(worldpos.x + xoff, worldpos.y, worldpos.z + zoff));
			if (block.id == Block.AIR)
				continue;
			if (replacement > -1)
				world.setBlock(worldpos.x + xoff, worldpos.y + yoff, worldpos.z + zoff, replacement);
			return block;
		}
		return GameRegistry.getBlock(Block.AIR);
	}
	
}