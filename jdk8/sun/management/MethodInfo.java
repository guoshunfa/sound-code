package sun.management;

import java.io.Serializable;

public class MethodInfo implements Serializable {
   private String name;
   private long type;
   private int compileSize;
   private static final long serialVersionUID = 6992337162326171013L;

   MethodInfo(String var1, long var2, int var4) {
      this.name = var1;
      this.type = var2;
      this.compileSize = var4;
   }

   public String getName() {
      return this.name;
   }

   public long getType() {
      return this.type;
   }

   public int getCompileSize() {
      return this.compileSize;
   }

   public String toString() {
      return this.getName() + " type = " + this.getType() + " compileSize = " + this.getCompileSize();
   }
}
