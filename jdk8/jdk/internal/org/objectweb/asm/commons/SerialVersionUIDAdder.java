package jdk.internal.org.objectweb.asm.commons;

import java.io.ByteArrayOutputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import jdk.internal.org.objectweb.asm.ClassVisitor;
import jdk.internal.org.objectweb.asm.FieldVisitor;
import jdk.internal.org.objectweb.asm.MethodVisitor;

public class SerialVersionUIDAdder extends ClassVisitor {
   private boolean computeSVUID;
   private boolean hasSVUID;
   private int access;
   private String name;
   private String[] interfaces;
   private Collection<SerialVersionUIDAdder.Item> svuidFields;
   private boolean hasStaticInitializer;
   private Collection<SerialVersionUIDAdder.Item> svuidConstructors;
   private Collection<SerialVersionUIDAdder.Item> svuidMethods;

   public SerialVersionUIDAdder(ClassVisitor var1) {
      this(327680, var1);
      if (this.getClass() != SerialVersionUIDAdder.class) {
         throw new IllegalStateException();
      }
   }

   protected SerialVersionUIDAdder(int var1, ClassVisitor var2) {
      super(var1, var2);
      this.svuidFields = new ArrayList();
      this.svuidConstructors = new ArrayList();
      this.svuidMethods = new ArrayList();
   }

   public void visit(int var1, int var2, String var3, String var4, String var5, String[] var6) {
      this.computeSVUID = (var2 & 512) == 0;
      if (this.computeSVUID) {
         this.name = var3;
         this.access = var2;
         this.interfaces = new String[var6.length];
         System.arraycopy(var6, 0, this.interfaces, 0, var6.length);
      }

      super.visit(var1, var2, var3, var4, var5, var6);
   }

   public MethodVisitor visitMethod(int var1, String var2, String var3, String var4, String[] var5) {
      if (this.computeSVUID) {
         if ("<clinit>".equals(var2)) {
            this.hasStaticInitializer = true;
         }

         int var6 = var1 & 3391;
         if ((var1 & 2) == 0) {
            if ("<init>".equals(var2)) {
               this.svuidConstructors.add(new SerialVersionUIDAdder.Item(var2, var6, var3));
            } else if (!"<clinit>".equals(var2)) {
               this.svuidMethods.add(new SerialVersionUIDAdder.Item(var2, var6, var3));
            }
         }
      }

      return super.visitMethod(var1, var2, var3, var4, var5);
   }

   public FieldVisitor visitField(int var1, String var2, String var3, String var4, Object var5) {
      if (this.computeSVUID) {
         if ("serialVersionUID".equals(var2)) {
            this.computeSVUID = false;
            this.hasSVUID = true;
         }

         if ((var1 & 2) == 0 || (var1 & 136) == 0) {
            int var6 = var1 & 223;
            this.svuidFields.add(new SerialVersionUIDAdder.Item(var2, var6, var3));
         }
      }

      return super.visitField(var1, var2, var3, var4, var5);
   }

   public void visitInnerClass(String var1, String var2, String var3, int var4) {
      if (this.name != null && this.name.equals(var1)) {
         this.access = var4;
      }

      super.visitInnerClass(var1, var2, var3, var4);
   }

   public void visitEnd() {
      if (this.computeSVUID && !this.hasSVUID) {
         try {
            this.addSVUID(this.computeSVUID());
         } catch (Throwable var2) {
            throw new RuntimeException("Error while computing SVUID for " + this.name, var2);
         }
      }

      super.visitEnd();
   }

   public boolean hasSVUID() {
      return this.hasSVUID;
   }

   protected void addSVUID(long var1) {
      FieldVisitor var3 = super.visitField(24, "serialVersionUID", "J", (String)null, var1);
      if (var3 != null) {
         var3.visitEnd();
      }

   }

   protected long computeSVUID() throws IOException {
      DataOutputStream var2 = null;
      long var3 = 0L;

      try {
         ByteArrayOutputStream var1 = new ByteArrayOutputStream();
         var2 = new DataOutputStream(var1);
         var2.writeUTF(this.name.replace('/', '.'));
         var2.writeInt(this.access & 1553);
         Arrays.sort((Object[])this.interfaces);

         for(int var5 = 0; var5 < this.interfaces.length; ++var5) {
            var2.writeUTF(this.interfaces[var5].replace('/', '.'));
         }

         writeItems(this.svuidFields, var2, false);
         if (this.hasStaticInitializer) {
            var2.writeUTF("<clinit>");
            var2.writeInt(8);
            var2.writeUTF("()V");
         }

         writeItems(this.svuidConstructors, var2, true);
         writeItems(this.svuidMethods, var2, true);
         var2.flush();
         byte[] var10 = this.computeSHAdigest(var1.toByteArray());

         for(int var6 = Math.min(var10.length, 8) - 1; var6 >= 0; --var6) {
            var3 = var3 << 8 | (long)(var10[var6] & 255);
         }
      } finally {
         if (var2 != null) {
            var2.close();
         }

      }

      return var3;
   }

   protected byte[] computeSHAdigest(byte[] var1) {
      try {
         return MessageDigest.getInstance("SHA").digest(var1);
      } catch (Exception var3) {
         throw new UnsupportedOperationException(var3.toString());
      }
   }

   private static void writeItems(Collection<SerialVersionUIDAdder.Item> var0, DataOutput var1, boolean var2) throws IOException {
      int var3 = var0.size();
      SerialVersionUIDAdder.Item[] var4 = (SerialVersionUIDAdder.Item[])var0.toArray(new SerialVersionUIDAdder.Item[var3]);
      Arrays.sort((Object[])var4);

      for(int var5 = 0; var5 < var3; ++var5) {
         var1.writeUTF(var4[var5].name);
         var1.writeInt(var4[var5].access);
         var1.writeUTF(var2 ? var4[var5].desc.replace('/', '.') : var4[var5].desc);
      }

   }

   private static class Item implements Comparable<SerialVersionUIDAdder.Item> {
      final String name;
      final int access;
      final String desc;

      Item(String var1, int var2, String var3) {
         this.name = var1;
         this.access = var2;
         this.desc = var3;
      }

      public int compareTo(SerialVersionUIDAdder.Item var1) {
         int var2 = this.name.compareTo(var1.name);
         if (var2 == 0) {
            var2 = this.desc.compareTo(var1.desc);
         }

         return var2;
      }

      public boolean equals(Object var1) {
         if (var1 instanceof SerialVersionUIDAdder.Item) {
            return this.compareTo((SerialVersionUIDAdder.Item)var1) == 0;
         } else {
            return false;
         }
      }

      public int hashCode() {
         return (this.name + this.desc).hashCode();
      }
   }
}
