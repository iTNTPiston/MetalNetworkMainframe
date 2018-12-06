package com.tntp.mnm.tileentity;

import com.tntp.mnm.api.ek.IHeatSink;
import com.tntp.mnm.api.ek.IHeatSource;
import com.tntp.mnm.gui.conf.ITileConfig;
import com.tntp.mnm.init.MNMBlocks;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class TileHeatDistributor extends STileHeatNodeInventory implements IHeatSource, IHeatSink, ITileConfig {
  private static final int MAX = 1000;
  private boolean[] isOut;

  public TileHeatDistributor() {
    super(6);
    isOut = new boolean[6];
    this.setMaxEK(MAX);
  }

  @Override
  public boolean isSinkSide(int side) {
    return !isOut[side];
  }

  @Override
  public boolean isSourceSide(int side) {
    return isOut[side];
  }

  public void setSide(int side, boolean in) {
    if (side >= 0 && side < 6) {
      isOut[side] = !in;
      markDirty();
      if (worldObj != null)
        this.worldObj.markBlockRangeForRenderUpdate(xCoord, yCoord, zCoord, xCoord, yCoord, zCoord);
    }
  }

  @Override
  public void workAsSource() {
    int totalcfg = 0;
    for (int i = 0; i < getSizeInventory(); i++) {
      ItemStack stack = getStackInSlot(i);
      if (stack != null && stack.getItem() == Item.getItemFromBlock(MNMBlocks.heatPipe)) {
        totalcfg += stack.stackSize;
      }
    }
    if (totalcfg == 0)
      return;
    if (getEK() < totalcfg)
      return;
    super.workAsSource();
  }

  @Override
  public boolean transferToSink(IHeatSink sink, int sourceSide) {
    ItemStack stack = getStackInSlot(sourceSide);
    if (stack == null || stack.getItem() != Item.getItemFromBlock(MNMBlocks.heatPipe))
      return false;
    int transfer = Math.min(stack.stackSize, sink.getMaxEK() - sink.getEK());
    if (transfer > 0) {
      setEK(getEK() - transfer);
      sink.setEK(sink.getEK() + transfer);
      return true;
    } else {
      return false;
    }
  }

  @Override
  public void writeToNBT(NBTTagCompound tag) {
    super.writeToNBT(tag);
    byte[] config = new byte[6];
    for (int i = 0; i < 6; i++) {
      config[i] = (byte) (isOut[i] ? 1 : 0);
    }
    tag.setByteArray("heat_config", config);
  }

  @Override
  public void readFromNBT(NBTTagCompound tag) {
    super.readFromNBT(tag);
    isOut = new boolean[6];
    byte[] config = tag.getByteArray("heat_config");
    for (int i = 0; i < 6; i++) {
      isOut[i] = config[i] == 1;
    }
  }

  @Override
  public String getConfigGui() {
    return "GuiConfigHeatDistributor";
  }

}
