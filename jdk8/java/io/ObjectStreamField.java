package java.io;

import java.lang.reflect.Field;
import sun.reflect.CallerSensitive;
import sun.reflect.Reflection;
import sun.reflect.misc.ReflectUtil;

public class ObjectStreamField implements Comparable<Object> {
   private final String name;
   private final String signature;
   private final Class<?> type;
   private final boolean unshared;
   private final Field field;
   private int offset;

   public ObjectStreamField(String var1, Class<?> var2) {
      this(var1, var2, false);
   }

   public ObjectStreamField(String var1, Class<?> var2, boolean var3) {
      this.offset = 0;
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         this.name = var1;
         this.type = var2;
         this.unshared = var3;
         this.signature = getClassSignature(var2).intern();
         this.field = null;
      }
   }

   ObjectStreamField(String var1, String var2, boolean var3) {
      this.offset = 0;
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         this.name = var1;
         this.signature = var2.intern();
         this.unshared = var3;
         this.field = null;
         switch(var2.charAt(0)) {
         case 'B':
            this.type = Byte.TYPE;
            break;
         case 'C':
            this.type = Character.TYPE;
            break;
         case 'D':
            this.type = Double.TYPE;
            break;
         case 'E':
         case 'G':
         case 'H':
         case 'K':
         case 'M':
         case 'N':
         case 'O':
         case 'P':
         case 'Q':
         case 'R':
         case 'T':
         case 'U':
         case 'V':
         case 'W':
         case 'X':
         case 'Y':
         default:
            throw new IllegalArgumentException("illegal signature");
         case 'F':
            this.type = Float.TYPE;
            break;
         case 'I':
            this.type = Integer.TYPE;
            break;
         case 'J':
            this.type = Long.TYPE;
            break;
         case 'L':
         case '[':
            this.type = Object.class;
            break;
         case 'S':
            this.type = Short.TYPE;
            break;
         case 'Z':
            this.type = Boolean.TYPE;
         }

      }
   }

   ObjectStreamField(Field var1, boolean var2, boolean var3) {
      this.offset = 0;
      this.field = var1;
      this.unshared = var2;
      this.name = var1.getName();
      Class var4 = var1.getType();
      this.type = !var3 && !var4.isPrimitive() ? Object.class : var4;
      this.signature = getClassSignature(var4).intern();
   }

   public String getName() {
      return this.name;
   }

   @CallerSensitive
   public Class<?> getType() {
      if (System.getSecurityManager() != null) {
         Class var1 = Reflection.getCallerClass();
         if (ReflectUtil.needsPackageAccessCheck(var1.getClassLoader(), this.type.getClassLoader())) {
            ReflectUtil.checkPackageAccess(this.type);
         }
      }

      return this.type;
   }

   public char getTypeCode() {
      return this.signature.charAt(0);
   }

   public String getTypeString() {
      return this.isPrimitive() ? null : this.signature;
   }

   public int getOffset() {
      return this.offset;
   }

   protected void setOffset(int var1) {
      this.offset = var1;
   }

   public boolean isPrimitive() {
      char var1 = this.signature.charAt(0);
      return var1 != 'L' && var1 != '[';
   }

   public boolean isUnshared() {
      return this.unshared;
   }

   public int compareTo(Object var1) {
      ObjectStreamField var2 = (ObjectStreamField)var1;
      boolean var3 = this.isPrimitive();
      if (var3 != var2.isPrimitive()) {
         return var3 ? -1 : 1;
      } else {
         return this.name.compareTo(var2.name);
      }
   }

   public String toString() {
      return this.signature + ' ' + this.name;
   }

   Field getField() {
      return this.field;
   }

   String getSignature() {
      return this.signature;
   }

   private static String getClassSignature(Class<?> var0) {
      StringBuilder var1;
      for(var1 = new StringBuilder(); var0.isArray(); var0 = var0.getComponentType()) {
         var1.append('[');
      }

      if (var0.isPrimitive()) {
         if (var0 == Integer.TYPE) {
            var1.append('I');
         } else if (var0 == Byte.TYPE) {
            var1.append('B');
         } else if (var0 == Long.TYPE) {
            var1.append('J');
         } else if (var0 == Float.TYPE) {
            var1.append('F');
         } else if (var0 == Double.TYPE) {
            var1.append('D');
         } else if (var0 == Short.TYPE) {
            var1.append('S');
         } else if (var0 == Character.TYPE) {
            var1.append('C');
         } else if (var0 == Boolean.TYPE) {
            var1.append('Z');
         } else {
            if (var0 != Void.TYPE) {
               throw new InternalError();
            }

            var1.append('V');
         }
      } else {
         var1.append('L' + var0.getName().replace('.', '/') + ';');
      }

      return var1.toString();
   }
}
