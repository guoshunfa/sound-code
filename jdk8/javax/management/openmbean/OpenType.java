package javax.management.openmbean;

import com.sun.jmx.mbeanserver.GetPropertyAction;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.management.Descriptor;
import javax.management.ImmutableDescriptor;

public abstract class OpenType<T> implements Serializable {
   static final long serialVersionUID = -9195195325186646468L;
   public static final List<String> ALLOWED_CLASSNAMES_LIST = Collections.unmodifiableList(Arrays.asList("java.lang.Void", "java.lang.Boolean", "java.lang.Character", "java.lang.Byte", "java.lang.Short", "java.lang.Integer", "java.lang.Long", "java.lang.Float", "java.lang.Double", "java.lang.String", "java.math.BigDecimal", "java.math.BigInteger", "java.util.Date", "javax.management.ObjectName", CompositeData.class.getName(), TabularData.class.getName()));
   /** @deprecated */
   @Deprecated
   public static final String[] ALLOWED_CLASSNAMES;
   private String className;
   private String description;
   private String typeName;
   private transient boolean isArray = false;
   private transient Descriptor descriptor;

   protected OpenType(String var1, String var2, String var3) throws OpenDataException {
      this.checkClassNameOverride();
      this.typeName = valid("typeName", var2);
      this.description = valid("description", var3);
      this.className = validClassName(var1);
      this.isArray = this.className != null && this.className.startsWith("[");
   }

   OpenType(String var1, String var2, String var3, boolean var4) {
      this.className = valid("className", var1);
      this.typeName = valid("typeName", var2);
      this.description = valid("description", var3);
      this.isArray = var4;
   }

   private void checkClassNameOverride() throws SecurityException {
      if (this.getClass().getClassLoader() != null) {
         if (overridesGetClassName(this.getClass())) {
            GetPropertyAction var1 = new GetPropertyAction("jmx.extend.open.types");
            if (AccessController.doPrivileged((PrivilegedAction)var1) == null) {
               throw new SecurityException("Cannot override getClassName() unless -Djmx.extend.open.types");
            }
         }

      }
   }

   private static boolean overridesGetClassName(final Class<?> var0) {
      return (Boolean)AccessController.doPrivileged(new PrivilegedAction<Boolean>() {
         public Boolean run() {
            try {
               return var0.getMethod("getClassName").getDeclaringClass() != OpenType.class;
            } catch (Exception var2) {
               return true;
            }
         }
      });
   }

   private static String validClassName(String var0) throws OpenDataException {
      var0 = valid("className", var0);

      int var1;
      for(var1 = 0; var0.startsWith("[", var1); ++var1) {
      }

      boolean var3 = false;
      String var2;
      if (var1 > 0) {
         if (var0.startsWith("L", var1) && var0.endsWith(";")) {
            var2 = var0.substring(var1 + 1, var0.length() - 1);
         } else {
            if (var1 != var0.length() - 1) {
               throw new OpenDataException("Argument className=\"" + var0 + "\" is not a valid class name");
            }

            var2 = var0.substring(var1, var0.length());
            var3 = true;
         }
      } else {
         var2 = var0;
      }

      boolean var4 = false;
      if (var3) {
         var4 = ArrayType.isPrimitiveContentType(var2);
      } else {
         var4 = ALLOWED_CLASSNAMES_LIST.contains(var2);
      }

      if (!var4) {
         throw new OpenDataException("Argument className=\"" + var0 + "\" is not one of the allowed Java class names for open data.");
      } else {
         return var0;
      }
   }

   private static String valid(String var0, String var1) {
      if (var1 != null && !(var1 = var1.trim()).equals("")) {
         return var1;
      } else {
         throw new IllegalArgumentException("Argument " + var0 + " cannot be null or empty");
      }
   }

   synchronized Descriptor getDescriptor() {
      if (this.descriptor == null) {
         this.descriptor = new ImmutableDescriptor(new String[]{"openType"}, new Object[]{this});
      }

      return this.descriptor;
   }

   public String getClassName() {
      return this.className;
   }

   String safeGetClassName() {
      return this.className;
   }

   public String getTypeName() {
      return this.typeName;
   }

   public String getDescription() {
      return this.description;
   }

   public boolean isArray() {
      return this.isArray;
   }

   public abstract boolean isValue(Object var1);

   boolean isAssignableFrom(OpenType<?> var1) {
      return this.equals(var1);
   }

   public abstract boolean equals(Object var1);

   public abstract int hashCode();

   public abstract String toString();

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      this.checkClassNameOverride();
      ObjectInputStream.GetField var2 = var1.readFields();

      String var3;
      String var4;
      String var5;
      try {
         var3 = validClassName((String)var2.get("className", (Object)null));
         var4 = valid("description", (String)var2.get("description", (Object)null));
         var5 = valid("typeName", (String)var2.get("typeName", (Object)null));
      } catch (Exception var8) {
         InvalidObjectException var7 = new InvalidObjectException(var8.getMessage());
         var7.initCause(var8);
         throw var7;
      }

      this.className = var3;
      this.description = var4;
      this.typeName = var5;
      this.isArray = this.className.startsWith("[");
   }

   static {
      ALLOWED_CLASSNAMES = (String[])ALLOWED_CLASSNAMES_LIST.toArray(new String[0]);
   }
}
