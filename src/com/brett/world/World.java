package com.brett.world;

import java.util.ArrayList;
import java.util.List;

import com.brett.Main;
import com.brett.engine.data.collision.AxisAlignedBB;
import com.brett.engine.managers.ThreadPool;
import com.brett.world.block.Block;
import com.brett.world.chunks.Chunk;
import com.brett.world.chunks.Noise;
import com.brett.world.chunks.data.ByteBlockStorage;
import com.brett.world.chunks.data.NdHashMap;
import com.brett.world.chunks.data.RenderMode;
import com.brett.world.chunks.data.ShortBlockStorage;

/**
* @author Brett
* @date Jun. 28, 2020
*/

public class World {
	
	public static World world;
	
	public volatile NdHashMap<Integer, Chunk> chunks = new NdHashMap<Integer, Chunk>();
	public volatile NdHashMap<Integer, Chunk> ungeneratedChunks = new NdHashMap<Integer, Chunk>();
	public volatile List<NdHashMap<Integer, Chunk>> maps = null;
	public volatile List<Thread> generatorThreads = new ArrayList<Thread>();
	public int threads = 1;
	
	public World() {
		threads = ThreadPool.reserveQuarterThreads() + ThreadPool.reserveQuarterThreads();
		World.world = this;
		Thread th = new Thread(() ->  {
			
			for (int o = 0; o < threads; o++) {
				//int threadIDLocal = i;
				Thread dth = new Thread(() -> {
					//int threadID = threadIDLocal;
					
					while (Main.isOpen) {
						try {
							if (maps != null && maps.size() > 0) {
								NdHashMap<Integer, Chunk> ourMap = null;
								try {
									ourMap = maps.get(0);
									maps.remove(0);
								} catch (Exception e) {}
								if (ourMap != null) {
									ourMap.iterate((NdHashMap<Integer, Chunk> dt, Integer k1, Integer k2, Integer k3, Chunk v1)->{
										ShortBlockStorage blks = v1.blocks;
										int cxw = k1 * 16;
										int cyw = k2 * 16;
										int czw = k3 * 16;
										for (int i = 0; i < 16; i++) {
											for (int k = 0; k < 16; k++) {
												int wx = cxw + i;
												int wz = czw + k;
												double nfxz = 0;
												if (cyw > -120)
													nfxz = (Noise.noise.nNoise(wx/32d, 56.43958205810, wz/32d, 16, 4d)) * 64 + 64;
												for (int j = 0; j < 16; j++) {
													int wy = cyw + j;
													
													if (wy > nfxz) {
														
													} else {
													
														double nf = (Noise.noise.noise(wx/96.593, wy/173.593, wz/96.593) * Noise.noise.noise(wx/16.493, wy/32.293, wz/16.493)) 
																+ Noise.noise.noise(wx/256.793, wy/256.593, wz/156.793);
														if (nf > -0.2) {
															if (wy < nfxz && wy > nfxz-1)
																blks.setWorld(wx, wy, wz, Block.GRASS);
															else if (wy < nfxz-1 && wy > (nfxz - 4))
																blks.setWorld(wx, wy, wz, Block.DIRT);
															else if (wy > -120)
																blks.setWorld(wx, wy, wz, Block.STONE);
															else
																blks.setWorld(wx, wy, wz, Block.BASALT);
														}
													}
												}
											}
										}
										v1.meshChunk();
										chunks.set(k1, k2, k3, v1);
									});
									ungeneratedChunks.clear(ourMap);
								}
							}
							Thread.sleep(100);
						} catch (Exception e) {e.printStackTrace();}
					}
					
				});
				dth.start();
				generatorThreads.add(dth);
			}
			
			while (Main.isOpen) {
				try {
					//NdHashMap<Integer, Chunk> hmcp = ungeneratedChunks.clone();
					
					if (maps == null || maps.size() == 0) {
						maps = ungeneratedChunks.split(threads);
					}
					
					//chunks.iterate((NdHashMap<Integer, Chunk> dt, Integer k1, Integer k2, Integer k3, Chunk v1) -> {
					//	if (dt.get(k1, k2, k3).chunkInfo != 0) {
							//dt.get(k1, k2, k3).meshChunk();
					//	}
					//});
					
					Thread.sleep(100);
				} catch (Exception e) {System.err.println(e.getCause());}
			}
		});
		th.start();
		generatorThreads.add(th);
	}
	
	/**
	 * queue a chunk for generation in chunk space.
	 */
	public synchronized void queueChunk(int x, int y, int z) {
		if (ungeneratedChunks.containsKey(x, y, z))
			return;
		Chunk c = new Chunk(this, new ShortBlockStorage(), new ByteBlockStorage(), new ByteBlockStorage(), x, y, z);
		ungeneratedChunks.set(x, y, z, c);
	}
	
	/**
	 * sets a block in block space.
	 */
	public void setBlock(int x, int y, int z, short id) {
		int cx = x >> 4;
		int cy = y >> 4;
		int cz = z >> 4;
		Chunk c	= getChunk(cx, cy, cz);
		if (c == null) {
			if (ungeneratedChunks.containsKey(cx, cy, cz)) {
				c = ungeneratedChunks.get(cx, cy, cz);
			} else {
				c = new Chunk(this, new ShortBlockStorage(), new ByteBlockStorage(), new ByteBlockStorage(), cx, cy, cz);
				ungeneratedChunks.set(cx, cy, cz, c);
			}
		}
		if (c != null)
			c.blocks.setWorld(x, y, z, id);
	}
	
	public RenderMode getRenderMode(int x, int y, int z) {
		Chunk c = getChunkWorld(x, y, z);
		if (c == null)
			return GameRegistry.getBlock((short)0).getRenderMode();
		short block = c.blocks.getWorld(x, y, z);
		return GameRegistry.getBlock(block).getRenderMode();
	}
	
	public RenderMode getRenderModeNull(int x, int y, int z) {
		Chunk c = getChunkWorld(x, y, z);
		if (c == null)
			return null;
		short block = c.blocks.getWorld(x, y, z);
		Block b = Block.blocks.get(block);
		if (b == null)
			return null;
		return b.getRenderMode();
	}
	
	public short getBlock(int x, int y, int z) {
		Chunk c = getChunkWorld(x, y, z);
		if (c == null)
			return 0;
		return c.blocks.getWorld(x, y, z);
	}
	
	public Block getBlockB(int x, int y, int z) {
		Chunk c = getChunkWorld(x, y, z);
		if (c == null)
			return GameRegistry.getBlock((short) 0);
		return GameRegistry.getBlock(c.blocks.getWorld(x, y, z));
	}
	
	public List<AxisAlignedBB> getBoundsInRange(int nx, int ny, int nz, int px, int py, int pz){
		ArrayList<AxisAlignedBB> lis = new ArrayList<AxisAlignedBB>();
		
		for (int i = nx; i <= px; i++) {
			for (int j = ny; j <= py; j++) {
				for (int k = nz; k <= pz; k++) {
					AxisAlignedBB bb = getBlockB(i, j, k).bbox;
					if (bb != null)
						lis.add(bb.translate(i, j, k));
				}
			}
		}
		
		return lis;
	}
	
	/*
	 * gets the chunk in world pos
	 */
	public Chunk getChunkWorld(int wx, int wy, int wz) {
		return chunks.get(wx >> 4, wy >> 4, wz >> 4);
	}
	
	/**
	 * gets the chunk in chunk pos
	 */
	public Chunk getChunk(int x, int y, int z) {
		return chunks.get(x, y, z);
	}
	
	public void setChunk(int x, int y, int z, Chunk c) {
		chunks.set(x, y, z, c);
	}
	
	public void setChunk(Chunk c) {
		chunks.set(c.x_pos, c.y_pos, c.y_pos, c);
	}
	
	public void setChunkWorld(int x, int y, int z, Chunk c) {
		chunks.set(x >> 4, y >> 4, z >> 4, c);
	}
	
	public void save() {
		
	}
	
}
