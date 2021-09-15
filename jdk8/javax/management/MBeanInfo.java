package javax.management;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.StreamCorruptedException;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.WeakHashMap;

public class MBeanInfo implements Cloneable, Serializable, DescriptorRead {
   static final long serialVersionUID = -6451021435135161911L;
   private transient Descriptor descriptor;
   private final String description;
   private final String className;
   private final MBeanAttributeInfo[] attributes;
   private final MBeanOperationInfo[] operations;
   private final MBeanConstructorInfo[] constructors;
   private final MBeanNotificationInfo[] notifications;
   private transient int hashCode;
   private final transient boolean arrayGettersSafe;
   private static final Map<Class<?>, Boolean> arrayGettersSafeMap = new WeakHashMap();

   public MBeanInfo(String var1, String var2, MBeanAttributeInfo[] var3, MBeanConstructorInfo[] var4, MBeanOperationInfo[] var5, MBeanNotificationInfo[] var6) throws IllegalArgumentException {
      this(var1, var2, var3, var4, var5, var6, (Descriptor)null);
   }

   public MBeanInfo(String var1, String var2, MBeanAttributeInfo[] var3, MBeanConstructorInfo[] var4, MBeanOperationInfo[] var5, MBeanNotificationInfo[] var6, Descriptor var7) throws IllegalArgumentException {
      this.className = var1;
      this.description = var2;
      if (var3 == null) {
         var3 = MBeanAttributeInfo.NO_ATTRIBUTES;
      }

      this.attributes = var3;
      if (var5 == null) {
         var5 = MBeanOperationInfo.NO_OPERATIONS;
      }

      this.operations = var5;
      if (var4 == null) {
         var4 = MBeanConstructorInfo.NO_CONSTRUCTORS;
      }

      this.constructors = var4;
      if (var6 == null) {
         var6 = MBeanNotificationInfo.NO_NOTIFICATIONS;
      }

      this.notifications = var6;
      if (var7 == null) {
         var7 = ImmutableDescriptor.EMPTY_DESCRIPTOR;
      }

      this.descriptor = (Descriptor)var7;
      this.arrayGettersSafe = arrayGettersSafe(this.getClass(), MBeanInfo.class);
   }

   public Object clone() {
      try {
         return super.clone();
      } catch (CloneNotSupportedException var2) {
         return null;
      }
   }

   public String getClassName() {
      return this.className;
   }

   public String getDescription() {
      return this.description;
   }

   public MBeanAttributeInfo[] getAttributes() {
      MBeanAttributeInfo[] var1 = this.nonNullAttributes();
      return var1.length == 0 ? var1 : (MBeanAttributeInfo[])var1.clone();
   }

   private MBeanAttributeInfo[] fastGetAttributes() {
      return this.arrayGettersSafe ? this.nonNullAttributes() : this.getAttributes();
   }

   private MBeanAttributeInfo[] nonNullAttributes() {
      return this.attributes == null ? MBeanAttributeInfo.NO_ATTRIBUTES : this.attributes;
   }

   public MBeanOperationInfo[] getOperations() {
      MBeanOperationInfo[] var1 = this.nonNullOperations();
      return var1.length == 0 ? var1 : (MBeanOperationInfo[])var1.clone();
   }

   private MBeanOperationInfo[] fastGetOperations() {
      return this.arrayGettersSafe ? this.nonNullOperations() : this.getOperations();
   }

   private MBeanOperationInfo[] nonNullOperations() {
      return this.operations == null ? MBeanOperationInfo.NO_OPERATIONS : this.operations;
   }

   public MBeanConstructorInfo[] getConstructors() {
      MBeanConstructorInfo[] var1 = this.nonNullConstructors();
      return var1.length == 0 ? var1 : (MBeanConstructorInfo[])var1.clone();
   }

   private MBeanConstructorInfo[] fastGetConstructors() {
      return this.arrayGettersSafe ? this.nonNullConstructors() : this.getConstructors();
   }

   private MBeanConstructorInfo[] nonNullConstructors() {
      return this.constructors == null ? MBeanConstructorInfo.NO_CONSTRUCTORS : this.constructors;
   }

   public MBeanNotificationInfo[] getNotifications() {
      MBeanNotificationInfo[] var1 = this.nonNullNotifications();
      return var1.length == 0 ? var1 : (MBeanNotificationInfo[])var1.clone();
   }

   private MBeanNotificationInfo[] fastGetNotifications() {
      return this.arrayGettersSafe ? this.nonNullNotifications() : this.getNotifications();
   }

   private MBeanNotificationInfo[] nonNullNotifications() {
      return this.notifications == null ? MBeanNotificationInfo.NO_NOTIFICATIONS : this.notifications;
   }

   public Descriptor getDescriptor() {
      return (Descriptor)ImmutableDescriptor.nonNullDescriptor(this.descriptor).clone();
   }

   public String toString() {
      return this.getClass().getName() + "[description=" + this.getDescription() + ", attributes=" + Arrays.asList(this.fastGetAttributes()) + ", constructors=" + Arrays.asList(this.fastGetConstructors()) + ", operations=" + Arrays.asList(this.fastGetOperations()) + ", notifications=" + Arrays.asList(this.fastGetNotifications()) + ", descriptor=" + this.getDescriptor() + "]";
   }

   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof MBeanInfo)) {
         return false;
      } else {
         MBeanInfo var2 = (MBeanInfo)var1;
         if (isEqual(this.getClassName(), var2.getClassName()) && isEqual(this.getDescription(), var2.getDescription()) && this.getDescriptor().equals(var2.getDescriptor())) {
            return Arrays.equals((Object[])var2.fastGetAttributes(), (Object[])this.fastGetAttributes()) && Arrays.equals((Object[])var2.fastGetOperations(), (Object[])this.fastGetOperations()) && Arrays.equals((Object[])var2.fastGetConstructors(), (Object[])this.fastGetConstructors()) && Arrays.equals((Object[])var2.fastGetNotifications(), (Object[])this.fastGetNotifications());
         } else {
            return false;
         }
      }
   }

   public int hashCode() {
      if (this.hashCode != 0) {
         return this.hashCode;
      } else {
         this.hashCode = Objects.hash(this.getClassName(), this.getDescriptor()) ^ Arrays.hashCode((Object[])this.fastGetAttributes()) ^ Arrays.hashCode((Object[])this.fastGetOperations()) ^ Arrays.hashCode((Object[])this.fastGetConstructors()) ^ Arrays.hashCode((Object[])this.fastGetNotifications());
         return this.hashCode;
      }
   }

   static boolean arrayGettersSafe(Class<?> var0, Class<?> var1) {
      if (var0 == var1) {
         return true;
      } else {
         synchronized(arrayGettersSafeMap) {
            Boolean var3 = (Boolean)arrayGettersSafeMap.get(var0);
            if (var3 == null) {
               try {
                  MBeanInfo.ArrayGettersSafeAction var4 = new MBeanInfo.ArrayGettersSafeAction(var0, var1);
                  var3 = (Boolean)AccessController.doPrivileged((PrivilegedAction)var4);
               } catch (Exception var6) {
                  var3 = false;
               }

               arrayGettersSafeMap.put(var0, var3);
            }

            return var3;
         }
      }
   }

   private static boolean isEqual(String var0, String var1) {
      boolean var2;
      if (var0 == null) {
         var2 = var1 == null;
      } else {
         var2 = var0.equals(var1);
      }

      return var2;
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.defaultWriteObject();
      if (this.descriptor.getClass() == ImmutableDescriptor.class) {
         var1.write(1);
         String[] var2 = this.descriptor.getFieldNames();
         var1.writeObject(var2);
         var1.writeObject(this.descriptor.getFieldValues(var2));
      } else {
         var1.write(0);
         var1.writeObject(this.descriptor);
      }

   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      switch(var1.read()) {
      case -1:
         this.descriptor = ImmutableDescriptor.EMPTY_DESCRIPTOR;
         break;
      case 0:
         this.descriptor = (Descriptor)var1.readObject();
         if (this.descriptor == null) {
            this.descriptor = ImmutableDescriptor.EMPTY_DESCRIPTOR;
         }
         break;
      case 1:
         String[] var2 = (String[])((String[])var1.readObject());
         Object[] var3 = (Object[])((Object[])var1.readObject());
         this.descriptor = var2.length == 0 ? ImmutableDescriptor.EMPTY_DESCRIPTOR : new ImmutableDescriptor(var2, var3);
         break;
      default:
         throw new StreamCorruptedException("Got unexpected byte.");
      }

   }

   private static class ArrayGettersSafeAction implements PrivilegedAction<Boolean> {
      private final Class<?> subclass;
      private final Class<?> immutableClass;

      ArrayGettersSafeAction(Class<?> var1, Class<?> var2) {
         this.subclass = var1;
         this.immutableClass = var2;
      }

      public Boolean run() {
         Method[] var1 = this.immutableClass.getMethods();

         for(int var2 = 0; var2 < var1.length; ++var2) {
            Method var3 = var1[var2];
            String var4 = var3.getName();
            if (var4.startsWith("get") && var3.getParameterTypes().length == 0 && var3.getReturnType().isArray()) {
               try {
                  Method var5 = this.subclass.getMethod(var4);
                  if (!var5.equals(var3)) {
                     return false;
                  }
               } catch (NoSuchMethodException var6) {
                  return false;
               }
            }
         }

         return true;
      }
   }
}
