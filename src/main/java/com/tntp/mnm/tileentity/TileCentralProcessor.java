package com.tntp.mnm.tileentity;

import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

import com.tntp.mnm.api.db.IQuery;
import com.tntp.mnm.api.db.Mainframe;
import com.tntp.mnm.api.db.QueryExecuter;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

/**
 * controller
 * 
 * @author iTNTPiston
 *
 */
public class TileCentralProcessor extends STile {
  /*
   * A TileCentral Processor is associated with a Mainframe object
   */
  private Mainframe mainframe;
  private PriorityQueue<QueryExecuter> queue;
  private int executionLeft;
  /**
   * task per tick, depends on CPU tier and ACUs
   */
  private int executionPerTick;
  /**
   * maxmium tasks it can store (depends on RAM)
   */
  private int maxQueueSize;

  private int scanCD;
  private int scanTotal = 200;

  public TileCentralProcessor() {
    mainframe = new Mainframe(this);
    queue = new PriorityQueue<QueryExecuter>();
    executionPerTick = 1;
    executionLeft = 0;
    maxQueueSize = 10;
  }

  public void updateEntity() {
    super.updateEntity();
    if (worldObj != null && !worldObj.isRemote) {
      // init mainframe
      if (mainframe.getWorld() == null) {
        mainframe.setWorld(worldObj);
      }
      if (scanCD <= 0) {
        // mandatory scan
        mainframe.scan();
        scanCD = scanTotal;
      } else {
        scanCD--;
      }

      // execution power check
      //
      if (executionLeft < executionPerTick) {
        executionLeft += executionPerTick;
      }
      // execution loop
      while (executionLeft > 0) {
        if (queue.isEmpty())
          break;// no more task
        System.out.println("Executing Query");
        QueryExecuter qe = queue.poll();
        if (qe.execute(mainframe))// only if it is executed successfully
          executionLeft--;
      }

      mainframe.setNeedScan();
    }
  }

  public boolean addQuery(QueryExecuter query) {
    System.out.println("CPU received Query");
    if (queue.size() < maxQueueSize) {
      System.out.println("Query Queued");
      queue.add(query);
      return true;
    }
    return false;
  }

  @Override
  public void writeToNBT(NBTTagCompound tag) {
    super.writeToNBT(tag);
    NBTTagCompound mf = new NBTTagCompound();
    mainframe.writeToNBT(mf);
    tag.setTag("mainframe", mf);
  }

  @Override
  public void readFromNBT(NBTTagCompound tag) {
    super.readFromNBT(tag);
    NBTTagCompound mf = (NBTTagCompound) tag.getTag("mainframe");
    mainframe = new Mainframe(this);
    mainframe.readFromNBT(mf);
  }
}
