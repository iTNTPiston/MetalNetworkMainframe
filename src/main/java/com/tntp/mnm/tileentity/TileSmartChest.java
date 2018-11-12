package com.tntp.mnm.tileentity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.Constants.NBT;

public class TileSmartChest extends TileEntity implements IInventory {

  private ItemStack[] slots;

  public TileSmartChest(int invSlots) {
    slots = new ItemStack[invSlots];
  }

  @Override
  public int getSizeInventory() {
    return slots.length;
  }

  @Override
  public ItemStack getStackInSlot(int i) {
    return slots[i];
  }

  @Override
  public ItemStack decrStackSize(int slot, int size) {
    ItemStack stack = getStackInSlot(slot);
    if (stack == null)
      return null;
    if (stack.stackSize < size) {
      markDirty();
      setInventorySlotContents(slot, null);
      return stack;
    } else {
      markDirty();
      ItemStack s = stack.splitStack(size);
      if (stack.stackSize == 0)
        setInventorySlotContents(slot, null);
      return s;
    }
  }

  @Override
  public ItemStack getStackInSlotOnClosing(int i) {
    return getStackInSlot(i);
  }

  @Override
  public void setInventorySlotContents(int slot, ItemStack stack) {
    slots[slot] = stack;
  }

  @Override
  public String getInventoryName() {
    return "container.ev.smartchest";
  }

  @Override
  public boolean hasCustomInventoryName() {
    return false;
  }

  @Override
  public int getInventoryStackLimit() {
    return 64;
  }

  @Override
  public boolean isUseableByPlayer(EntityPlayer p_70300_1_) {
    return true;
  }

  @Override
  public void openInventory() {

  }

  @Override
  public void closeInventory() {

  }

  @Override
  public boolean isItemValidForSlot(int slot, ItemStack stack) {
    return true;
  }

  public void writeToNBT(NBTTagCompound tag) {
    super.writeToNBT(tag);
    NBTTagList contents = new NBTTagList();
    for (int i = 0; i < slots.length; i++) {
      if (slots[i] != null) {
        NBTTagCompound slot = new NBTTagCompound();
        slot.setByte("Slot", (byte) i);
        slots[i].writeToNBT(slot);
        contents.appendTag(slot);
      }
    }
    tag.setTag("EV|SmartChest", contents);
    tag.setInteger("EV|InvSize", slots.length);
  }

  public void readFromNBT(NBTTagCompound tag) {
    super.readFromNBT(tag);
    slots = new ItemStack[tag.getInteger("EV|InvSize")];
    NBTTagList contents = tag.getTagList("EV|SmartChest", NBT.TAG_COMPOUND);
    for (int i = 0; i < contents.tagCount(); i++) {
      NBTTagCompound slot = contents.getCompoundTagAt(i);
      int s = slot.getByte("Slot") & 255;
      ItemStack stack = ItemStack.loadItemStackFromNBT(slot);
      if (s >= 0 && s <= slots.length)
        slots[s] = stack;
    }
  }

}
