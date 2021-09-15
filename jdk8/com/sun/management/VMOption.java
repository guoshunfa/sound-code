package com.sun.management;

import javax.management.openmbean.CompositeData;
import jdk.Exported;
import sun.management.VMOptionCompositeData;

@Exported
public class VMOption {
   private String name;
   private String value;
   private boolean writeable;
   private VMOption.Origin origin;

   public VMOption(String var1, String var2, boolean var3, VMOption.Origin var4) {
      this.name = var1;
      this.value = var2;
      this.writeable = var3;
      this.origin = var4;
   }

   private VMOption(CompositeData var1) {
      VMOptionCompositeData.validateCompositeData(var1);
      this.name = VMOptionCompositeData.getName(var1);
      this.value = VMOptionCompositeData.getValue(var1);
      this.writeable = VMOptionCompositeData.isWriteable(var1);
      this.origin = VMOptionCompositeData.getOrigin(var1);
   }

   public String getName() {
      return this.name;
   }

   public String getValue() {
      return this.value;
   }

   public VMOption.Origin getOrigin() {
      return this.origin;
   }

   public boolean isWriteable() {
      return this.writeable;
   }

   public String toString() {
      return "VM option: " + this.getName() + " value: " + this.value + "  origin: " + this.origin + " " + (this.writeable ? "(read-write)" : "(read-only)");
   }

   public static VMOption from(CompositeData var0) {
      if (var0 == null) {
         return null;
      } else {
         return var0 instanceof VMOptionCompositeData ? ((VMOptionCompositeData)var0).getVMOption() : new VMOption(var0);
      }
   }

   @Exported
   public static enum Origin {
      DEFAULT,
      VM_CREATION,
      ENVIRON_VAR,
      CONFIG_FILE,
      MANAGEMENT,
      ERGONOMIC,
      OTHER;
   }
}
