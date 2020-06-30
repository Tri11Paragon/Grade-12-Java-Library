package com.brett.world.block;

import java.util.HashMap;

import com.brett.world.chunks.data.RenderMode;

/**
* @author Brett
* @date Jun. 28, 2020
*/

public class Block {
	
	public static final HashMap<Short, Block> blocks = new HashMap<Short, Block>();
	public static final HashMap<Block, Short> inverseBlocks = new HashMap<Block, Short>();
	
	public static final short AIR = 0;
	public static final short STONE = 1;
	public static final short DIRT = 2;
	public static final short GRASS = 3;
	
	public int textureTop, textureBottom, textureLeft, textureRight, textureFront, textureFront2, textureBack;
	public short id;
	
	public RenderMode renderMode;
	
	public Block(short id, int texture) {
		this.id = id;
		this.textureTop = texture;
		this.textureBottom = texture;
		this.textureFront = texture;
		this.textureFront2 = texture;
		this.textureBack = texture;
		this.textureLeft = texture;
		this.textureRight = texture;
	}
	
	public int getTextureTop() {
		return textureTop;
	}

	public Block setTextureTop(int textureTop) {
		this.textureTop = textureTop;
		return this;
	}

	public int getTextureBottom() {
		return textureBottom;
	}

	public Block setTextureBottom(int textureBottom) {
		this.textureBottom = textureBottom;
		return this;
	}

	public int getTextureLeft() {
		return textureLeft;
	}

	public Block setTextureLeft(int textureLeft) {
		this.textureLeft = textureLeft;
		return this;
	}

	public int getTextureRight() {
		return textureRight;
	}

	public Block setTextureRight(int textureRight) {
		this.textureRight = textureRight;
		return this;
	}

	public int getTextureFront() {
		return textureFront;
	}

	public Block setTextureFront(int textureFront) {
		this.textureFront = textureFront;
		return this;
	}

	public int getTextureFront2() {
		return textureFront2;
	}

	public Block setTextureFront2(int textureFront2) {
		this.textureFront2 = textureFront2;
		return this;
	}

	public int getTextureBack() {
		return textureBack;
	}

	public Block setTextureBack(int textureBack) {
		this.textureBack = textureBack;
		return this;
	}

	public Block setRenderMode(RenderMode mode) {
		this.renderMode = mode;
		return this;
	}
	
	public RenderMode getRenderMode() {
		return renderMode;
	}
	
}
