package com.brett.voxel.world.items;

import com.brett.renderer.datatypes.ModelTexture;

/**
*
* @author brett
* @date Apr. 16, 2020
*/

public class ItemTinPickaxe extends ItemTool {

	public ItemTinPickaxe(short id, ModelTexture texture) {
		super(id, texture);
		super.setMiningLevel(2);
		super.setMiningSpeed(0.48f);
		super.setMaxStackSize(1);
		super.setToolType(ItemTool.TOOL_PICKAXE);
	}

}
