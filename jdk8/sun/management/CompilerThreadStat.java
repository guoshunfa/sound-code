package sun.management;

import java.io.Serializable;

public class CompilerThreadStat implements Serializable {
   private String name;
   private long taskCount;
   private long compileTime;
   private MethodInfo lastMethod;
   private static final long serialVersionUID = 6992337162326171013L;

   CompilerThreadStat(String var1, long var2, long var4, MethodInfo var6) {
      this.name = var1;
      this.taskCount = var2;
      this.compileTime = var4;
      this.lastMethod = var6;
   }

   public String getName() {
      return this.name;
   }

   public long getCompileTaskCount() {
      return this.taskCount;
   }

   public long getCompileTime() {
      return this.compileTime;
   }

   public MethodInfo getLastCompiledMethodInfo() {
      return this.lastMethod;
   }

   public String toString() {
      return this.getName() + " compileTasks = " + this.getCompileTaskCount() + " compileTime = " + this.getCompileTime();
   }
}
