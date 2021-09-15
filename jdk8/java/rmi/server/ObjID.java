package java.rmi.server;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.SecureRandom;
import java.util.concurrent.atomic.AtomicLong;
import sun.security.action.GetPropertyAction;

public final class ObjID implements Serializable {
   public static final int REGISTRY_ID = 0;
   public static final int ACTIVATOR_ID = 1;
   public static final int DGC_ID = 2;
   private static final long serialVersionUID = -6386392263968365220L;
   private static final AtomicLong nextObjNum = new AtomicLong(0L);
   private static final UID mySpace = new UID();
   private static final SecureRandom secureRandom = new SecureRandom();
   private final long objNum;
   private final UID space;

   public ObjID() {
      if (useRandomIDs()) {
         this.space = new UID();
         this.objNum = secureRandom.nextLong();
      } else {
         this.space = mySpace;
         this.objNum = nextObjNum.getAndIncrement();
      }

   }

   public ObjID(int var1) {
      this.space = new UID((short)0);
      this.objNum = (long)var1;
   }

   private ObjID(long var1, UID var3) {
      this.objNum = var1;
      this.space = var3;
   }

   public void write(ObjectOutput var1) throws IOException {
      var1.writeLong(this.objNum);
      this.space.write(var1);
   }

   public static ObjID read(ObjectInput var0) throws IOException {
      long var1 = var0.readLong();
      UID var3 = UID.read(var0);
      return new ObjID(var1, var3);
   }

   public int hashCode() {
      return (int)this.objNum;
   }

   public boolean equals(Object var1) {
      if (!(var1 instanceof ObjID)) {
         return false;
      } else {
         ObjID var2 = (ObjID)var1;
         return this.objNum == var2.objNum && this.space.equals(var2.space);
      }
   }

   public String toString() {
      return "[" + (this.space.equals(mySpace) ? "" : this.space + ", ") + this.objNum + "]";
   }

   private static boolean useRandomIDs() {
      String var0 = (String)AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction("java.rmi.server.randomIDs")));
      return var0 == null ? true : Boolean.parseBoolean(var0);
   }
}
