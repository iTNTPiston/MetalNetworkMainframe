package com.tntp.mnm.init;

import com.tntp.mnm.model.PipeRenderer;
import com.tntp.mnm.model.SimpleObjRenderer;
import com.tntp.mnm.model.WaveObjRenderer;

import cpw.mods.fml.client.registry.RenderingRegistry;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.obj.Vertex;
import net.minecraftforge.client.model.obj.WavefrontObject;

public class MNMRender {
  public static void loadRenderers() {
    SimpleObjRenderer.id = RenderingRegistry.getNextAvailableRenderId();

    SimpleObjRenderer simple = new SimpleObjRenderer();

    int i = simple.registerWaveObj(getWaveObjRenderer("MNM_CP"));
    simple.bindWaveObj(MNMBlocks.blockCentralProcessor, 0, i);

    i = simple.registerWaveObj(getWaveObjRenderer("MNM_ACU"));
    simple.bindWaveObj(MNMBlocks.blockAuxiliaryComputingUnit, 0, i);

    WavefrontObject heatPipe = getWaveObj("MNM_HP");
    WavefrontObject heatPipeExt = getWaveObj("MNM_HP_EXT");
    ResourceLocation heatPipeTex = getTexture("MNM_HP");
    PipeRenderer heatPipeRender = new PipeRenderer(heatPipe, heatPipeExt, heatPipeTex);
    i = simple.registerWaveObj(heatPipeRender);
    simple.bindWaveObj(MNMBlocks.blockHeatPipe, 0, i);

    RenderingRegistry.registerBlockHandler(simple);

  }

  public static WaveObjRenderer getWaveObjRenderer(String name) {
    return new WaveObjRenderer(getWaveObj(name), getTexture(name));
  }

  public static WavefrontObject getWaveObj(String name) {
    WavefrontObject obj = new WavefrontObject(MNMResources.getResource("models/" + name + ".obj"));
    for (Vertex v : obj.vertices) {
      v.x = v.x / 16f;
      v.y = v.y / 16f;
      v.z = v.z / 16f;
    }
    return obj;
  }

  public static ResourceLocation getTexture(String name) {
    return MNMResources.getResource("models/" + name + ".png");
  }
}
