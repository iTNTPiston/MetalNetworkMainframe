package com.tntp.mnm.gui;

import org.lwjgl.opengl.GL11;

import com.tntp.mnm.api.ek.IHeatNode;
import com.tntp.mnm.init.MNMBlocks;
import com.tntp.mnm.init.MNMItems;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public enum GuiTabType {
  HEAT("mnm.gui.label.heat", 1f, 0.7f, 0.5f), HEAT_PIPE("mnm.gui.label.heat_pipe", 1f, 0.8f, 0.7f);
  @SideOnly(Side.CLIENT)
  private static RenderItem itemRender;
  @SideOnly(Side.CLIENT)
  private static FontRenderer fontRenderer;
  @SideOnly(Side.CLIENT)
  private static TextureManager textureManager;

  private String label;
  private float r;
  private float g;
  private float b;

  GuiTabType(String label, float r, float g, float b) {
    this.label = label;
    this.r = r;
    this.g = g;
    this.b = b;
  }

  public ItemStack getIconStack() {
    switch (this) {
    case HEAT:
      return new ItemStack(MNMItems.itemWrench);
    case HEAT_PIPE:
      return new ItemStack(MNMBlocks.blockHeatPipe);

    }
    return null;
  }

  public static boolean isOnTab(int tabLocation, int mx, int my) {
    int tabX = tabLocation > 2 ? 148 : 0;
    int tabY = (tabLocation % 3) * 28;
    return mx >= tabX && mx <= tabX + 28 && my >= tabY && my <= tabY + 28;
  }

  public String getUnlocalizedLabel() {
    return label;
  }

  public int flag() {
    return 1 << ordinal();
  }

  public String getGuiString(ItemStack toolItem, World world, int x, int y, int z) {
    String name = null;
    Block block = world.getBlock(x, y, z);
    TileEntity tile = world.getTileEntity(x, y, z);
    switch (this) {
    case HEAT:
      if (tile instanceof IHeatNode)
        name = "GuiHeat";
      break;
    case HEAT_PIPE:
      if (block == MNMBlocks.blockHeatPipe)
        name = "GuiHeatPipe";
      break;
    }
    return name;
  }

  @SideOnly(Side.CLIENT)
  public void setColorToTab() {
    GL11.glColor4f(r, g, b, 1);
  }

  @SideOnly(Side.CLIENT)
  public void drawTab(GuiContainer gui, int tabLocation) {

    GL11.glColor4f(r, g, b, 1.0F);
    int tabX = tabLocation > 2 ? 148 : 0;
    int tabY = (tabLocation % 3) * 28;
    gui.drawTexturedModalRect(tabX, tabY, 0, 0, 28, 28);
    GL11.glColor4f(1f, 1f, 1f, 1.0F);
    ItemStack icon = getIconStack();
    if (icon != null) {
      itemRender().zLevel = 100.0F;
      RenderHelper.enableGUIStandardItemLighting();
      // GL11.glDisable(GL11.GL_DEPTH_TEST);
      // GL11.glDisable(GL11.GL_BLEND);
      itemRender().renderItemAndEffectIntoGUI(fontRenderer(), textureManager(), icon, 5 + tabX, 5 + tabY);
      itemRender().renderItemOverlayIntoGUI(fontRenderer(), textureManager(), icon, 5 + tabX, 5 + tabY);
      GL11.glEnable(GL11.GL_BLEND);
      RenderHelper.disableStandardItemLighting();
      itemRender().zLevel = 0.0F;
    }
  }

  @SideOnly(Side.CLIENT)
  private static RenderItem itemRender() {
    if (itemRender == null)
      itemRender = new RenderItem();
    return itemRender;
  }

  @SideOnly(Side.CLIENT)
  private static FontRenderer fontRenderer() {
    if (fontRenderer == null)
      fontRenderer = Minecraft.getMinecraft().fontRenderer;
    return fontRenderer;
  }

  @SideOnly(Side.CLIENT)
  private static TextureManager textureManager() {
    if (textureManager == null)
      textureManager = Minecraft.getMinecraft().getTextureManager();
    return textureManager;
  }

}
