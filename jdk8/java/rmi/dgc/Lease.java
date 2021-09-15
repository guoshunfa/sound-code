package java.rmi.dgc;

import java.io.Serializable;

public final class Lease implements Serializable {
   private VMID vmid;
   private long value;
   private static final long serialVersionUID = -5713411624328831948L;

   public Lease(VMID var1, long var2) {
      this.vmid = var1;
      this.value = var2;
   }

   public VMID getVMID() {
      return this.vmid;
   }

   public long getValue() {
      return this.value;
   }
}
