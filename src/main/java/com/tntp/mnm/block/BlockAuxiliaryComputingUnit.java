package com.tntp.mnm.block;

import net.minecraft.block.material.Material;

public class BlockAuxiliaryComputingUnit extends SBlockModelSpecial {

  public BlockAuxiliaryComputingUnit() {
    super(Material.iron, "blockAuxiliaryComputingUnit");
  }

  public boolean renderAsNormalBlock() {
    return false;
  }

  public boolean isOpaqueCube() {
    return false;
  }

}
