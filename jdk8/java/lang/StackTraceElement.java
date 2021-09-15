package java.lang;

import java.io.Serializable;
import java.util.Objects;

public final class StackTraceElement implements Serializable {
   private String declaringClass;
   private String methodName;
   private String fileName;
   private int lineNumber;
   private static final long serialVersionUID = 6992337162326171013L;

   public StackTraceElement(String var1, String var2, String var3, int var4) {
      this.declaringClass = (String)Objects.requireNonNull(var1, (String)"Declaring class is null");
      this.methodName = (String)Objects.requireNonNull(var2, (String)"Method name is null");
      this.fileName = var3;
      this.lineNumber = var4;
   }

   public String getFileName() {
      return this.fileName;
   }

   public int getLineNumber() {
      return this.lineNumber;
   }

   public String getClassName() {
      return this.declaringClass;
   }

   public String getMethodName() {
      return this.methodName;
   }

   public boolean isNativeMethod() {
      return this.lineNumber == -2;
   }

   public String toString() {
      return this.getClassName() + "." + this.methodName + (this.isNativeMethod() ? "(Native Method)" : (this.fileName != null && this.lineNumber >= 0 ? "(" + this.fileName + ":" + this.lineNumber + ")" : (this.fileName != null ? "(" + this.fileName + ")" : "(Unknown Source)")));
   }

   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof StackTraceElement)) {
         return false;
      } else {
         StackTraceElement var2 = (StackTraceElement)var1;
         return var2.declaringClass.equals(this.declaringClass) && var2.lineNumber == this.lineNumber && Objects.equals(this.methodName, var2.methodName) && Objects.equals(this.fileName, var2.fileName);
      }
   }

   public int hashCode() {
      int var1 = 31 * this.declaringClass.hashCode() + this.methodName.hashCode();
      var1 = 31 * var1 + Objects.hashCode(this.fileName);
      var1 = 31 * var1 + this.lineNumber;
      return var1;
   }
}
