package com.tntp.mnm.tileentity;

import java.util.List;

import com.tntp.mnm.gui.cont.ITileDataCont;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;

public class TileStructureModule extends STilePOB implements ITileDataCont {

  public TileStructureModule(int size) {
    super(size);
    // TODO Auto-generated constructor stub
  }

  @Override
  public String getContainerGui() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void addContainerSlots(List<Slot> slots) {
    // TODO Auto-generated method stub

  }

  @Override
  public boolean canReadData() {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public boolean isUseableByPlayer(EntityPlayer player) {
    return super.isUseableByPlayer(player) && canReadData();
  }

}
