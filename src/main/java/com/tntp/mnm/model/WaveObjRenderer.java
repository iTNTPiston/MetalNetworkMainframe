package com.tntp.mnm.model;

import org.lwjgl.opengl.GL11;

import com.tntp.mnm.util.DirUtil;
import com.tntp.mnm.util.RenderUtil;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.model.obj.Face;
import net.minecraftforge.client.model.obj.GroupObject;
import net.minecraftforge.client.model.obj.TextureCoordinate;
import net.minecraftforge.client.model.obj.Vertex;
import net.minecraftforge.client.model.obj.WavefrontObject;
import net.minecraftforge.common.util.ForgeDirection;

@SideOnly(Side.CLIENT)
public class WaveObjRenderer {
  protected WavefrontObject obj;
  protected ResourceLocation texture;

  protected float rotation;
  protected int rotationAxis;
  private boolean metaRotation;

  public WaveObjRenderer(WavefrontObject obj, ResourceLocation texture) {
    this.obj = obj;
    this.texture = texture;
    rotationAxis = -1;
  }

  public void render() {
    bindTexture();
    obj.renderAll();
  }

  public void bindTexture() {
    Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
  }

  public void tessellate(RenderBlocks renderBlock, Tessellator tes, Block block, int x, int y, int z, IIcon icon,
      int blockMetadata) {
    float metaRotate = 0;
    if (metaRotation) {
      switch (blockMetadata) {
      case 2:
        metaRotate = (float) (Math.PI / 2);
        break;
      case 3:
        metaRotate = (float) -Math.PI / 2;
        break;
      case 4:
        metaRotate = (float) Math.PI;
        break;
      }
    }
    Vertex vRot = null;
    Vertex vRot2 = null;
    for (GroupObject go : obj.groupObjects) {
      for (Face f : go.faces) {
        Vertex n = f.faceNormal;
        vRot = RenderUtil.rotate(n, rotation, rotationAxis, vRot);
        if (metaRotation) {
          vRot = RenderUtil.rotate(vRot, metaRotate, DirUtil.UP_PY, vRot2);
        }
        tes.setNormal(vRot.x, vRot.y, vRot.z);
        int normal = RenderUtil.determineFace(vRot);

        RenderUtil.tessellateWithAmbientOcclusion(renderBlock, tes, block, x, y, z, normal, f.vertices,
            f.textureCoordinates, rotation, rotationAxis, metaRotate, icon);

      }
    }
  }

  public void setRotation(float rad, int axis) {
    rotation = rad;
    rotationAxis = axis;
  }

  public void clearRotation() {
    rotationAxis = -1;
    rotation = 0;
  }

  public void setRotationFor(int side) {
    switch (side) {
    case 1:
      setRotation((float) Math.PI, DirUtil.EAST_PX);
      break;
    case 2:
      setRotation((float) Math.PI / 2, DirUtil.EAST_PX);
      break;
    case 3:
      setRotation((float) -Math.PI / 2, DirUtil.EAST_PX);
      break;
    case 4:
      setRotation((float) -Math.PI / 2, DirUtil.SOUTH_PZ);
      break;
    case 5:
      setRotation((float) Math.PI / 2, DirUtil.SOUTH_PZ);
      break;

    }
  }

  public void rotateGLFor(int side) {
    switch (side) {
    case 1:// up,x-axis 180
      GL11.glRotatef(180, 1, 0, 0);
      break;
    case 2:// -z
      GL11.glRotatef(90, 1, 0, 0);
      break;
    case 3:// +z
      GL11.glRotatef(90, -1, 0, 0);
      break;
    case 4:// -x
      GL11.glRotatef(90, 0, 0, 1);
      break;
    case 5:// +x
      GL11.glRotatef(90, 0, 0, -1);
      break;
    }
  }

  public void setRotationOnlyYFor(int side) {
    switch (side) {
    case 2:
      setRotation((float) Math.PI / 2, DirUtil.UP_PY);
      break;
    case 3:
      setRotation((float) -Math.PI / 2, DirUtil.UP_PY);
      break;
    case 4:
      setRotation((float) Math.PI, DirUtil.UP_PY);
      break;
    case 5:
      setRotation(0, DirUtil.UP_PY);
      break;

    }
  }

  public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {
    render();
  }

  public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId,
      RenderBlocks renderer) {
    Tessellator tes = Tessellator.instance;
    tes.addTranslation(x + 0.5f, y + 0.5f, z + 0.5f);

    int meta = world.getBlockMetadata(x, y, z);
    IIcon icon = renderer.hasOverrideBlockTexture() ? renderer.overrideBlockTexture : block.getIcon(0, meta);
    tessellate(renderer, tes, block, x, y, z, icon, meta);
    tes.addTranslation(-x - 0.5f, -y - 0.5f, -z - 0.5f);
    return true;
  }

  public void enableMetaRotation() {
    metaRotation = true;
  }
}
