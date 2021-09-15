package com.sun.media.sound;

public final class ModelIdentifier {
   private String object = null;
   private String variable = null;
   private int instance = 0;

   public ModelIdentifier(String var1) {
      this.object = var1;
   }

   public ModelIdentifier(String var1, int var2) {
      this.object = var1;
      this.instance = var2;
   }

   public ModelIdentifier(String var1, String var2) {
      this.object = var1;
      this.variable = var2;
   }

   public ModelIdentifier(String var1, String var2, int var3) {
      this.object = var1;
      this.variable = var2;
      this.instance = var3;
   }

   public int getInstance() {
      return this.instance;
   }

   public void setInstance(int var1) {
      this.instance = var1;
   }

   public String getObject() {
      return this.object;
   }

   public void setObject(String var1) {
      this.object = var1;
   }

   public String getVariable() {
      return this.variable;
   }

   public void setVariable(String var1) {
      this.variable = var1;
   }

   public int hashCode() {
      int var1 = this.instance;
      if (this.object != null) {
         var1 |= this.object.hashCode();
      }

      if (this.variable != null) {
         var1 |= this.variable.hashCode();
      }

      return var1;
   }

   public boolean equals(Object var1) {
      if (!(var1 instanceof ModelIdentifier)) {
         return false;
      } else {
         ModelIdentifier var2 = (ModelIdentifier)var1;
         if (this.object == null != (var2.object == null)) {
            return false;
         } else if (this.variable == null != (var2.variable == null)) {
            return false;
         } else if (var2.getInstance() != this.getInstance()) {
            return false;
         } else if (this.object != null && !this.object.equals(var2.object)) {
            return false;
         } else {
            return this.variable == null || this.variable.equals(var2.variable);
         }
      }
   }

   public String toString() {
      return this.variable == null ? this.object + "[" + this.instance + "]" : this.object + "[" + this.instance + "]." + this.variable;
   }
}
