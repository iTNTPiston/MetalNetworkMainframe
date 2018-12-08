package com.tntp.mnm.tileentity;

import net.minecraft.tileentity.TileEntity;

/**
 * Super class of (hopefully) all tileentities in MNM
 * 
 * @author iTNTPiston
 *
 */
public class STile extends TileEntity {
  public boolean isValidInWorld() {
    return this.hasWorldObj() && this.worldObj.getTileEntity(xCoord, yCoord, zCoord) == this;
  }
}
