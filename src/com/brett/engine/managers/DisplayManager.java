package com.brett.engine.managers;

import java.io.File;
import java.io.IOException;
import java.nio.IntBuffer;

import javax.imageio.ImageIO;

import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.system.MemoryStack;

import com.brett.engine.InputMaster;
import com.brett.engine.shaders.ProjectionMatrix;
import com.brett.engine.tools.ImageToBuffer;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

public class DisplayManager {
	    
	public static int WIDTH = 1280;
	public static int HEIGHT = 720;
	public static int FPS_MAX = 120;

	public static long window;
	
	private static long lastFrameTime;
	private static float delta;

	public static void createDisplay(boolean isUsingFBOs) {
		System.out.println("Hello " + Version.getVersion() + "!");
		
		GLFWErrorCallback.createPrint(System.err).set();
		
		if ( !glfwInit() )
			throw new IllegalStateException("Unable to initialize GLFW");
		
		glfwDefaultWindowHints(); 
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); 
		glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
		
		window = glfwCreateWindow(WIDTH, HEIGHT, "RMS - V0.1A", NULL, NULL);
		if ( window == NULL )
			throw new RuntimeException("Failed to create the GLFW window");
		
		glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
			if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE )
				glfwSetInputMode(window, GLFW_CURSOR, glfwGetInputMode(window, GLFW_CURSOR) == GLFW_CURSOR_DISABLED ? GLFW_CURSOR_NORMAL : GLFW_CURSOR_DISABLED);
			if ( action == GLFW_PRESS )
				InputMaster.keyPressed(key);
			if ( action == GLFW_RELEASE )
				InputMaster.keyReleased(key);
		});
		
		glfwSetMouseButtonCallback(window, (window, button, action, mods) -> {
			if ( action == GLFW_PRESS )
				InputMaster.mousePressed(button);
			if ( action == GLFW_RELEASE )
				InputMaster.mouseReleased(button);
		});
		
		glfwSetWindowSizeCallback(window, (window, x, y) -> {
			DisplayManager.WIDTH = x;
			DisplayManager.HEIGHT = y;
			GL11.glViewport(0, 0, x, y); ProjectionMatrix.updateProjectionMatrix();
		});
		
		try ( MemoryStack stack = stackPush() ) {
			IntBuffer pWidth = stack.mallocInt(1);
			IntBuffer pHeight = stack.mallocInt(1);

			glfwGetWindowSize(window, pWidth, pHeight);

			GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

			glfwSetWindowPos(
				window,
				(vidmode.width() - pWidth.get(0)) / 2,
				(vidmode.height() - pHeight.get(0)) / 2
			);
		}
		
		glfwMakeContextCurrent(window);
		// Enable v-sync
		glfwSwapInterval(1);

		glfwShowWindow(window);
		
		GL.createCapabilities();
		
		glfwWindowHint(GLFW_SAMPLES, 4);
		GL11.glEnable(GL13.GL_MULTISAMPLE);
		
		/*

		ByteBuffer[] list = new ByteBuffer[2];
		try {
			list[0] = ImageToBuffer.convet(ImageIO.read(new File("resources/textures/icon/icon16.png")));
			list[1] = ImageToBuffer.convet(ImageIO.read(new File("resources/textures/icon/icon32.png")));
		} catch (IOException e) {e.printStackTrace();}
		Display.setIcon(list);*/
		ProjectionMatrix.updateProjectionMatrix();
	}

	public static void updateDisplay() {
		
		glfwSwapBuffers(window);
		glfwPollEvents();
		
		long currentFrameTime = getCurrentTime();
		delta = currentFrameTime - lastFrameTime;
		lastFrameTime = currentFrameTime;
	}

	public static void closeDisplay() {
		glfwFreeCallbacks(window);
		glfwDestroyWindow(window);
		
		glfwTerminate();
		glfwSetErrorCallback(null);
	}

	private static long getCurrentTime() {
		return System.currentTimeMillis();
	}

	public static float getFrameTimeMilis() {
		return delta;
	}

	public static float getFrameTimeSeconds() {
		return delta / 1000;
	}
	
	public static void enableCulling() {
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glCullFace(GL11.GL_BACK);
	}
	
	public static void enableTransparentcy() {
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
	}
	
	public static void disableTransparentcy() {
		GL11.glDisable(GL11.GL_BLEND);
	}
	
	public static void disableCulling() {
		GL11.glDisable(GL11.GL_CULL_FACE);
	}
	
}