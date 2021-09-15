package javax.management;

import com.sun.jmx.mbeanserver.Util;
import java.io.InvalidObjectException;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public class ImmutableDescriptor implements Descriptor {
   private static final long serialVersionUID = 8853308591080540165L;
   private final String[] names;
   private final Object[] values;
   private transient int hashCode;
   public static final ImmutableDescriptor EMPTY_DESCRIPTOR = new ImmutableDescriptor(new String[0]);

   public ImmutableDescriptor(String[] var1, Object[] var2) {
      this((Map)makeMap(var1, var2));
   }

   public ImmutableDescriptor(String... var1) {
      this((Map)makeMap(var1));
   }

   public ImmutableDescriptor(Map<String, ?> var1) {
      this.hashCode = -1;
      if (var1 == null) {
         throw new IllegalArgumentException("Null Map");
      } else {
         TreeMap var2 = new TreeMap(String.CASE_INSENSITIVE_ORDER);
         Iterator var3 = var1.entrySet().iterator();

         while(var3.hasNext()) {
            Map.Entry var4 = (Map.Entry)var3.next();
            String var5 = (String)var4.getKey();
            if (var5 == null || var5.equals("")) {
               throw new IllegalArgumentException("Empty or null field name");
            }

            if (var2.containsKey(var5)) {
               throw new IllegalArgumentException("Duplicate name: " + var5);
            }

            var2.put(var5, var4.getValue());
         }

         int var6 = var2.size();
         this.names = (String[])var2.keySet().toArray(new String[var6]);
         this.values = var2.values().toArray(new Object[var6]);
      }
   }

   private Object readResolve() throws InvalidObjectException {
      boolean var1 = false;
      if (this.names == null || this.values == null || this.names.length != this.values.length) {
         var1 = true;
      }

      if (!var1) {
         if (this.names.length == 0 && this.getClass() == ImmutableDescriptor.class) {
            return EMPTY_DESCRIPTOR;
         }

         Comparator var2 = String.CASE_INSENSITIVE_ORDER;
         String var3 = "";

         for(int var4 = 0; var4 < this.names.length; ++var4) {
            if (this.names[var4] == null || var2.compare(var3, this.names[var4]) >= 0) {
               var1 = true;
               break;
            }

            var3 = this.names[var4];
         }
      }

      if (var1) {
         throw new InvalidObjectException("Bad names or values");
      } else {
         return this;
      }
   }

   private static SortedMap<String, ?> makeMap(String[] var0, Object[] var1) {
      if (var0 != null && var1 != null) {
         if (var0.length != var1.length) {
            throw new IllegalArgumentException("Different size arrays");
         } else {
            TreeMap var2 = new TreeMap(String.CASE_INSENSITIVE_ORDER);

            for(int var3 = 0; var3 < var0.length; ++var3) {
               String var4 = var0[var3];
               if (var4 == null || var4.equals("")) {
                  throw new IllegalArgumentException("Empty or null field name");
               }

               Object var5 = var2.put(var4, var1[var3]);
               if (var5 != null) {
                  throw new IllegalArgumentException("Duplicate field name: " + var4);
               }
            }

            return var2;
         }
      } else {
         throw new IllegalArgumentException("Null array parameter");
      }
   }

   private static SortedMap<String, ?> makeMap(String[] var0) {
      if (var0 == null) {
         throw new IllegalArgumentException("Null fields parameter");
      } else {
         String[] var1 = new String[var0.length];
         String[] var2 = new String[var0.length];

         for(int var3 = 0; var3 < var0.length; ++var3) {
            String var4 = var0[var3];
            int var5 = var4.indexOf(61);
            if (var5 < 0) {
               throw new IllegalArgumentException("Missing = character: " + var4);
            }

            var1[var3] = var4.substring(0, var5);
            var2[var3] = var4.substring(var5 + 1);
         }

         return makeMap(var1, var2);
      }
   }

   public static ImmutableDescriptor union(Descriptor... var0) {
      int var1 = findNonEmpty(var0, 0);
      if (var1 < 0) {
         return EMPTY_DESCRIPTOR;
      } else if (var0[var1] instanceof ImmutableDescriptor && findNonEmpty(var0, var1 + 1) < 0) {
         return (ImmutableDescriptor)var0[var1];
      } else {
         TreeMap var2 = new TreeMap(String.CASE_INSENSITIVE_ORDER);
         ImmutableDescriptor var3 = EMPTY_DESCRIPTOR;
         Descriptor[] var4 = var0;
         int var5 = var0.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            Descriptor var7 = var4[var6];
            if (var7 != null) {
               String[] var8;
               if (var7 instanceof ImmutableDescriptor) {
                  ImmutableDescriptor var9 = (ImmutableDescriptor)var7;
                  var8 = var9.names;
                  if (var9.getClass() == ImmutableDescriptor.class && var8.length > var3.names.length) {
                     var3 = var9;
                  }
               } else {
                  var8 = var7.getFieldNames();
               }

               String[] var17 = var8;
               int var10 = var8.length;

               for(int var11 = 0; var11 < var10; ++var11) {
                  String var12 = var17[var11];
                  Object var13 = var7.getFieldValue(var12);
                  Object var14 = var2.put(var12, var13);
                  if (var14 != null) {
                     boolean var15;
                     if (var14.getClass().isArray()) {
                        var15 = Arrays.deepEquals(new Object[]{var14}, new Object[]{var13});
                     } else {
                        var15 = var14.equals(var13);
                     }

                     if (!var15) {
                        String var16 = "Inconsistent values for descriptor field " + var12 + ": " + var14 + " :: " + var13;
                        throw new IllegalArgumentException(var16);
                     }
                  }
               }
            }
         }

         if (var3.names.length == var2.size()) {
            return var3;
         } else {
            return new ImmutableDescriptor(var2);
         }
      }
   }

   private static boolean isEmpty(Descriptor var0) {
      if (var0 == null) {
         return true;
      } else if (var0 instanceof ImmutableDescriptor) {
         return ((ImmutableDescriptor)var0).names.length == 0;
      } else {
         return var0.getFieldNames().length == 0;
      }
   }

   private static int findNonEmpty(Descriptor[] var0, int var1) {
      for(int var2 = var1; var2 < var0.length; ++var2) {
         if (!isEmpty(var0[var2])) {
            return var2;
         }
      }

      return -1;
   }

   private int fieldIndex(String var1) {
      return Arrays.binarySearch(this.names, var1, String.CASE_INSENSITIVE_ORDER);
   }

   public final Object getFieldValue(String var1) {
      checkIllegalFieldName(var1);
      int var2 = this.fieldIndex(var1);
      if (var2 < 0) {
         return null;
      } else {
         Object var3 = this.values[var2];
         if (var3 != null && var3.getClass().isArray()) {
            if (var3 instanceof Object[]) {
               return ((Object[])((Object[])var3)).clone();
            } else {
               int var4 = Array.getLength(var3);
               Object var5 = Array.newInstance(var3.getClass().getComponentType(), var4);
               System.arraycopy(var3, 0, var5, 0, var4);
               return var5;
            }
         } else {
            return var3;
         }
      }
   }

   public final String[] getFields() {
      String[] var1 = new String[this.names.length];

      for(int var2 = 0; var2 < var1.length; ++var2) {
         Object var3 = this.values[var2];
         if (var3 == null) {
            var3 = "";
         } else if (!(var3 instanceof String)) {
            var3 = "(" + var3 + ")";
         }

         var1[var2] = this.names[var2] + "=" + var3;
      }

      return var1;
   }

   public final Object[] getFieldValues(String... var1) {
      if (var1 == null) {
         return (Object[])this.values.clone();
      } else {
         Object[] var2 = new Object[var1.length];

         for(int var3 = 0; var3 < var1.length; ++var3) {
            String var4 = var1[var3];
            if (var4 != null && !var4.equals("")) {
               var2[var3] = this.getFieldValue(var4);
            }
         }

         return var2;
      }
   }

   public final String[] getFieldNames() {
      return (String[])this.names.clone();
   }

   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof Descriptor)) {
         return false;
      } else {
         String[] var2;
         if (var1 instanceof ImmutableDescriptor) {
            var2 = ((ImmutableDescriptor)var1).names;
         } else {
            var2 = ((Descriptor)var1).getFieldNames();
            Arrays.sort(var2, String.CASE_INSENSITIVE_ORDER);
         }

         if (this.names.length != var2.length) {
            return false;
         } else {
            for(int var3 = 0; var3 < this.names.length; ++var3) {
               if (!this.names[var3].equalsIgnoreCase(var2[var3])) {
                  return false;
               }
            }

            Object[] var4;
            if (var1 instanceof ImmutableDescriptor) {
               var4 = ((ImmutableDescriptor)var1).values;
            } else {
               var4 = ((Descriptor)var1).getFieldValues(var2);
            }

            return Arrays.deepEquals(this.values, var4);
         }
      }
   }

   public int hashCode() {
      if (this.hashCode == -1) {
         this.hashCode = Util.hashCode(this.names, this.values);
      }

      return this.hashCode;
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder("{");

      for(int var2 = 0; var2 < this.names.length; ++var2) {
         if (var2 > 0) {
            var1.append(", ");
         }

         var1.append(this.names[var2]).append("=");
         Object var3 = this.values[var2];
         if (var3 != null && var3.getClass().isArray()) {
            String var4 = Arrays.deepToString(new Object[]{var3});
            var4 = var4.substring(1, var4.length() - 1);
            var3 = var4;
         }

         var1.append(String.valueOf(var3));
      }

      return var1.append("}").toString();
   }

   public boolean isValid() {
      return true;
   }

   public Descriptor clone() {
      return this;
   }

   public final void setFields(String[] var1, Object[] var2) throws RuntimeOperationsException {
      if (var1 == null || var2 == null) {
         illegal("Null argument");
      }

      if (var1.length != var2.length) {
         illegal("Different array sizes");
      }

      int var3;
      for(var3 = 0; var3 < var1.length; ++var3) {
         checkIllegalFieldName(var1[var3]);
      }

      for(var3 = 0; var3 < var1.length; ++var3) {
         this.setField(var1[var3], var2[var3]);
      }

   }

   public final void setField(String var1, Object var2) throws RuntimeOperationsException {
      checkIllegalFieldName(var1);
      int var3 = this.fieldIndex(var1);
      if (var3 < 0) {
         unsupported();
      }

      Object var4 = this.values[var3];
      if (var4 == null) {
         if (var2 == null) {
            return;
         }
      } else if (var4.equals(var2)) {
         return;
      }

      unsupported();
   }

   public final void removeField(String var1) {
      if (var1 != null && this.fieldIndex(var1) >= 0) {
         unsupported();
      }

   }

   static Descriptor nonNullDescriptor(Descriptor var0) {
      return (Descriptor)(var0 == null ? EMPTY_DESCRIPTOR : var0);
   }

   private static void checkIllegalFieldName(String var0) {
      if (var0 == null || var0.equals("")) {
         illegal("Null or empty field name");
      }

   }

   private static void unsupported() {
      UnsupportedOperationException var0 = new UnsupportedOperationException("Descriptor is read-only");
      throw new RuntimeOperationsException(var0);
   }

   private static void illegal(String var0) {
      IllegalArgumentException var1 = new IllegalArgumentException(var0);
      throw new RuntimeOperationsException(var1);
   }
}
