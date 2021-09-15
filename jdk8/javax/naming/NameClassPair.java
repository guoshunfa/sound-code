package javax.naming;

import java.io.Serializable;

public class NameClassPair implements Serializable {
   private String name;
   private String className;
   private String fullName = null;
   private boolean isRel = true;
   private static final long serialVersionUID = 5620776610160863339L;

   public NameClassPair(String var1, String var2) {
      this.name = var1;
      this.className = var2;
   }

   public NameClassPair(String var1, String var2, boolean var3) {
      this.name = var1;
      this.className = var2;
      this.isRel = var3;
   }

   public String getClassName() {
      return this.className;
   }

   public String getName() {
      return this.name;
   }

   public void setName(String var1) {
      this.name = var1;
   }

   public void setClassName(String var1) {
      this.className = var1;
   }

   public boolean isRelative() {
      return this.isRel;
   }

   public void setRelative(boolean var1) {
      this.isRel = var1;
   }

   public String getNameInNamespace() {
      if (this.fullName == null) {
         throw new UnsupportedOperationException();
      } else {
         return this.fullName;
      }
   }

   public void setNameInNamespace(String var1) {
      this.fullName = var1;
   }

   public String toString() {
      return (this.isRelative() ? "" : "(not relative)") + this.getName() + ": " + this.getClassName();
   }
}
