package com.brett.renderer.font;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import com.brett.renderer.font.fontRendering.TextMaster;

public class GUIText {

	protected String textString;
	private float fontSize;

	private int textMeshVao;
	private int vertexCount;
	private Vector3f colour = new Vector3f(0f, 0f, 0f);

	protected Vector2f position;
	private float lineMaxSize;
	private int numberOfLines;
	private float height = 0;
	
	public float getHeight() {
		return height;
	}

	public void setHeight(float height) {
		this.height = height;
	}

	private int maxNumberOfLines = Integer.MAX_VALUE;

	private FontType font;

	private boolean atMax = false;
	public boolean isAtMax() {
		return atMax;
	}

	public void setAtMax(boolean atMax) {
		this.atMax = atMax;
	}

	private boolean centerText = false;

	public GUIText(String text, float fontSize, FontType font, Vector2f position, float maxLineLength, boolean centered) {
		this.textString = text;
		this.fontSize = fontSize;
		this.font = font;
		this.position = position;
		this.lineMaxSize = maxLineLength;
		this.centerText = centered;
	}
	
	public GUIText(String text, float fontSize, FontType font, Vector2f position, float maxLineLength, boolean centered, int maxNumberOfLines) {
		this.textString = text;
		this.fontSize = fontSize;
		this.font = font;
		this.position = position;
		this.lineMaxSize = maxLineLength;
		this.centerText = centered;
		this.maxNumberOfLines = maxNumberOfLines;
		//TextMaster.loadText(this);
	}
	
	public void setText(String test) {
		this.textString = test;
	}
	
	public String getText() {
		return this.textString;
	}
	
	public void remove() {
		TextMaster.removeText(this);
	}

	public FontType getFont() {
		return font;
	}

	public void setColour(float r, float g, float b) {
		colour.set(r, g, b);
	}

	public Vector3f getColour() {
		return colour;
	}
	
	public int getMaxNumberOfLines() {
		return maxNumberOfLines;
	}

	public int getNumberOfLines() {
		return numberOfLines;
	}

	public Vector2f getPosition() {
		return position;
	}

	public int getMesh() {
		return textMeshVao;
	}

	public void setMeshInfo(int vao, int verticesCount) {
		this.textMeshVao = vao;
		this.vertexCount = verticesCount;
	}

	public int getVertexCount() {
		return this.vertexCount;
	}

	protected float getFontSize() {
		return fontSize;
	}

	protected GUIText setNumberOfLines(int number) {
		this.numberOfLines = number;
		return this;
	}

	protected boolean isCentered() {
		return centerText;
	}

	protected float getMaxLineSize() {
		return lineMaxSize;
	}

	protected String getTextString() {
		return textString;
	}

}
