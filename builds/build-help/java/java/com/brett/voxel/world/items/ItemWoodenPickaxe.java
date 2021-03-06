package com.brett.voxel.world.items;

import com.brett.datatypes.Texture;

/**
*
* @author brett
* @date Apr. 16, 2020
*/

public class ItemWoodenPickaxe extends ItemTool {

	public ItemWoodenPickaxe(short id, Texture texture) {
		super(id, texture);
		super.setMiningLevel(1);
		super.setMiningSpeed(0.3f);
		super.setMaxStackSize(1);
		super.setToolType(ItemTool.TOOL_PICKAXE);
	}
	
}
