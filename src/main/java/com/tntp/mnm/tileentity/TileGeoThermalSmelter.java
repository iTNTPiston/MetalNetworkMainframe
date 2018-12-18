package com.tntp.mnm.tileentity;

import com.tntp.mnm.api.ek.IHeatSink;
import com.tntp.mnm.gui.process.ITileProcess;
import com.tntp.mnm.gui.structure.ITileStructure;
import com.tntp.mnm.init.MNMBlocks;
import com.tntp.mnm.util.ItemUtil;
import com.tntp.mnm.util.RandomUtil;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;

public class TileGeoThermalSmelter extends STileHeatNodeInventory implements IHeatSink, ITileStructure, ITileProcess {
  private static final int MAX = 1000, NORMAL_RATE = 20, BOOST_RATE = 40, NORMAL_TIME = 400, BOOST_TIME = 240;
  private int totalProgress;
  private int currentProgress;
  private boolean boosted;
  private boolean formed;

  public TileGeoThermalSmelter() {
    super(8);
    setEK(0);
    setMaxEK(MAX);
  }

  public void updateEntity() {
    super.updateEntity();
    if (worldObj != null) {
      if (!worldObj.isRemote) {
        int nextProgress = 0;
        int isWorking = 0;
        if (formed) {
          if (hasEnoughEK()) {
            if (canContinueSmelting()) {
              totalProgress = getTotal();
              nextProgress = currentProgress + 1;
              setEK(getEK() - getRate());
              isWorking = 8;
            }
          }
        }

        int meta = (worldObj.getBlockMetadata(xCoord, yCoord, zCoord) & 7) + isWorking;
        worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, meta, 2);
        if (currentProgress != nextProgress) {
          currentProgress = nextProgress;
          markDirty();
        }
        if (currentProgress == totalProgress) {
          currentProgress = 0;
          smeltAll();
        }
      } else {
        if ((worldObj.getBlockMetadata(xCoord, yCoord, zCoord) & 8) == 8)
          if (worldObj.getBlock(xCoord, yCoord + 1, zCoord) == MNMBlocks.chimney)
            if (worldObj.getBlock(xCoord, yCoord + 2, zCoord) == MNMBlocks.heat_pipe)
              if (worldObj.getBlockMetadata(xCoord, yCoord + 2, zCoord) == 1)
                if (worldObj.getBlock(xCoord, yCoord + 3, zCoord).isAir(worldObj, xCoord, yCoord + 3, zCoord)) {
                  // spawn particles
                  double x = xCoord + RandomUtil.RAND.nextFloat() * 0.6 + 0.2;
                  double z = zCoord + RandomUtil.RAND.nextFloat() * 0.6 + 0.2;
                  double y = yCoord + 3 + RandomUtil.RAND.nextFloat() * 0.5;
                  double vx = RandomUtil.RAND.nextFloat() * 0.1 - 0.05;
                  double vz = RandomUtil.RAND.nextFloat() * 0.1 - 0.05;
                  double vy = RandomUtil.RAND.nextFloat() * 0.1;
                  worldObj.spawnParticle("smoke", x, y, z, vx, vy, vz);
                }

      }
    }

  }

  @Override
  public boolean isSinkSide(int side) {
    if (worldObj != null) {
      return (worldObj.getBlockMetadata(xCoord, yCoord, zCoord) & 7) == (side ^ 1);
    }
    return false;
  }

  @Override
  public void rescan() {
    super.rescan();
    formed = false;
    boosted = false;
    int meta = worldObj.getBlockMetadata(xCoord, yCoord, zCoord);
    meta &= 7;
    ForgeDirection back = ForgeDirection.getOrientation(meta ^ 1);
    ForgeDirection side = ForgeDirection.getOrientation(7 - meta);
    Block mid = worldObj.getBlock(xCoord + back.offsetX, yCoord, zCoord + back.offsetZ);
    if (mid != MNMBlocks.heat_pipe)
      return;
    Block back2 = worldObj.getBlock(xCoord + back.offsetX * 2, yCoord, zCoord + back.offsetZ * 2);
    if (back2 != MNMBlocks.firewall)
      return;
    int offX = Math.abs(side.offsetX);
    int offZ = Math.abs(side.offsetZ);// one of these is 0

    for (int xx = xCoord - offX; xx <= xCoord + offX; xx += 2 * offX) {
      for (int zz = zCoord - offZ; zz <= zCoord + offZ; zz += 2 * offZ) {
        for (int i = 0; i < 2; i++) {
          if (worldObj.getBlock(xx + back.offsetX * i, yCoord, zz + back.offsetZ * i) != MNMBlocks.firewall)
            return;
        }
        if (offZ == 0)
          break;
      }
      if (offX == 0)
        break;
    }
    formed = true;
    int yy = yCoord + 1;
    for (int xx = xCoord - offX; xx <= xCoord + offX; xx += offX) {
      for (int zz = zCoord - offZ; zz <= zCoord + offZ; zz += offZ) {
        for (int i = 0; i < 2; i++) {
          Block type = (xx == xCoord && zz == zCoord && i == 0) ? MNMBlocks.chimney : MNMBlocks.firewall;
          if (worldObj.getBlock(xx + back.offsetX * i, yy, zz + back.offsetZ * i) != type)
            return;
        }
        if (offZ == 0)
          break;
      }
      if (offX == 0)
        break;
    }
    if (worldObj.getBlock(xCoord, yCoord + 2, zCoord) == MNMBlocks.heat_pipe) {
      if (worldObj.getBlockMetadata(xCoord, yCoord + 2, zCoord) == 1) {
        if (worldObj.getBlock(xCoord, yCoord + 3, zCoord).isAir(worldObj, xCoord, yCoord + 3, zCoord))
          boosted = true;// must be up-down direction & not obstructed
      }
    }
  }

  private boolean hasEnoughEK() {
    return getEK() >= getRate();
  }

  private int getTotal() {
    return boosted ? BOOST_TIME : NORMAL_TIME;
  }

  private int getRate() {
    return boosted ? BOOST_RATE : NORMAL_RATE;
  }

  private boolean canContinueSmelting() {
    for (int i = 0; i < 4; i++) {
      if (canSmelt(i))
        return true;
    }
    return false;
  }

  private boolean canSmelt(int slot) {
    ItemStack input = getStackInSlot(slot);
    if (input == null)
      return false;
    ItemStack product = FurnaceRecipes.smelting().getSmeltingResult(input);
    if (product == null)
      return false;
    ItemStack output = getStackInSlot(slot + 4);
    if (output == null)
      return true;
    if (!ItemUtil.areItemAndTagEqual(output, product))
      return false;
    int resultStackSize = output.stackSize + product.stackSize;
    return resultStackSize <= this.getInventoryStackLimit() && resultStackSize <= product.getMaxStackSize();
  }

  private void smeltAll() {
    for (int i = 0; i < 4; i++) {
      smelt(i);
    }
  }

  private void smelt(int slot) {
    if (canSmelt(slot)) {
      ItemStack input = getStackInSlot(slot);
      ItemStack product = FurnaceRecipes.smelting().getSmeltingResult(input);
      ItemStack output = getStackInSlot(slot + 4);
      if (output == null) {
        output = product.copy();
      } else if (ItemUtil.areItemAndTagEqual(output, product)) {
        output.stackSize += product.stackSize;
      }
      this.setInventorySlotContents(slot + 4, output);
      input.stackSize--;
      if (input.stackSize == 0)
        input = null;
      this.setInventorySlotContents(slot, input);
    }
  }

  public void setTotalProgress(int p) {
    totalProgress = p;
  }

  public void setCurrentProgress(int p) {
    currentProgress = p;
  }

  public int getCurrentProgress() {
    return currentProgress;
  }

  public int getTotalProgress() {
    return totalProgress;
  }

  public boolean isFormed() {
    return formed;
  }

  public boolean isBoosted() {
    return boosted;
  }

  public void setFormed(boolean b) {
    formed = b;
  }

  public void setBoosted(boolean b) {
    boosted = b;
  }

  @Override
  public void writeToNBT(NBTTagCompound tag) {
    super.writeToNBT(tag);
    tag.setInteger("current_progress", currentProgress);
    tag.setInteger("total_progress", totalProgress);
    tag.setBoolean("boosted", boosted);
    tag.setBoolean("formed", formed);
  }

  @Override
  public void readFromNBT(NBTTagCompound tag) {
    super.readFromNBT(tag);
    currentProgress = tag.getInteger("current_progress");
    totalProgress = tag.getInteger("total_progress");
    boosted = tag.getBoolean("boosted");
    formed = tag.getBoolean("formed");
  }

  @Override
  public String getStructureGui() {
    return "GuiStructureGeoThermalSmelter";
  }

  @Override
  public String getProcessGui() {
    return "GuiProcessGeoThermalSmelter";
  }

}
