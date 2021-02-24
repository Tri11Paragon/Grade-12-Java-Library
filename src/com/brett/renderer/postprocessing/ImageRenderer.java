package com.brett.renderer.postprocessing;

import org.lwjgl.opengl.GL11;

import com.brett.renderer.Fbo;

/**
 * @author brett
 * These are old classes and im not currently using them
 * please ignore the post processing class (2020-6-11)
 *
 */
public class ImageRenderer {

	private Fbo fbo;

	public ImageRenderer(int width, int height) {
		this.fbo = new Fbo(width, height, Fbo.NONE);
	}

	public ImageRenderer() {}

	public void renderQuad() {
		if (fbo != null) {
			fbo.bindFrameBuffer();
		}
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
		GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, 4);
		if (fbo != null) {
			fbo.unbindFrameBuffer();
		}
	}

	public int getOutputTexture() {
		return fbo.getColourTexture();
	}

	public void cleanUp() {
		if (fbo != null) {
			fbo.cleanUp();
		}
	}

}