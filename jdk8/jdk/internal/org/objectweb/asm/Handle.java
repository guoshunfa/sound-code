package jdk.internal.org.objectweb.asm;

public final class Handle {
   final int tag;
   final String owner;
   final String name;
   final String desc;

   public Handle(int var1, String var2, String var3, String var4) {
      this.tag = var1;
      this.owner = var2;
      this.name = var3;
      this.desc = var4;
   }

   public int getTag() {
      return this.tag;
   }

   public String getOwner() {
      return this.owner;
   }

   public String getName() {
      return this.name;
   }

   public String getDesc() {
      return this.desc;
   }

   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof Handle)) {
         return false;
      } else {
         Handle var2 = (Handle)var1;
         return this.tag == var2.tag && this.owner.equals(var2.owner) && this.name.equals(var2.name) && this.desc.equals(var2.desc);
      }
   }

   public int hashCode() {
      return this.tag + this.owner.hashCode() * this.name.hashCode() * this.desc.hashCode();
   }

   public String toString() {
      return this.owner + '.' + this.name + this.desc + " (" + this.tag + ')';
   }
}
