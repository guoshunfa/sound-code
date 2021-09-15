package com.sun.corba.se.impl.ior;

import com.sun.corba.se.spi.presentation.rmi.StubAdapter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import org.omg.CORBA.ORB;
import org.omg.CORBA.Object;
import org.omg.CORBA.portable.Delegate;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import sun.corba.SharedSecrets;

public class StubIORImpl {
   private int hashCode;
   private byte[] typeData;
   private int[] profileTags;
   private byte[][] profileData;

   public StubIORImpl() {
      this.hashCode = 0;
      this.typeData = null;
      this.profileTags = null;
      this.profileData = (byte[][])null;
   }

   public String getRepositoryId() {
      return this.typeData == null ? null : new String(this.typeData);
   }

   public StubIORImpl(Object var1) {
      OutputStream var2 = StubAdapter.getORB(var1).create_output_stream();
      var2.write_Object(var1);
      InputStream var3 = var2.create_input_stream();
      int var4 = var3.read_long();
      this.typeData = new byte[var4];
      var3.read_octet_array(this.typeData, 0, var4);
      int var5 = var3.read_long();
      this.profileTags = new int[var5];
      this.profileData = new byte[var5][];

      for(int var6 = 0; var6 < var5; ++var6) {
         this.profileTags[var6] = var3.read_long();
         this.profileData[var6] = new byte[var3.read_long()];
         var3.read_octet_array(this.profileData[var6], 0, this.profileData[var6].length);
      }

   }

   public Delegate getDelegate(ORB var1) {
      OutputStream var2 = var1.create_output_stream();
      var2.write_long(this.typeData.length);
      var2.write_octet_array(this.typeData, 0, this.typeData.length);
      var2.write_long(this.profileTags.length);

      for(int var3 = 0; var3 < this.profileTags.length; ++var3) {
         var2.write_long(this.profileTags[var3]);
         var2.write_long(this.profileData[var3].length);
         var2.write_octet_array(this.profileData[var3], 0, this.profileData[var3].length);
      }

      InputStream var5 = var2.create_input_stream();
      Object var4 = var5.read_Object();
      return StubAdapter.getDelegate(var4);
   }

   public void doRead(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      int var2 = var1.readInt();
      SharedSecrets.getJavaOISAccess().checkArray(var1, byte[].class, var2);
      this.typeData = new byte[var2];
      var1.readFully(this.typeData);
      int var3 = var1.readInt();
      SharedSecrets.getJavaOISAccess().checkArray(var1, int[].class, var3);
      SharedSecrets.getJavaOISAccess().checkArray(var1, byte[].class, var3);
      this.profileTags = new int[var3];
      this.profileData = new byte[var3][];

      for(int var4 = 0; var4 < var3; ++var4) {
         this.profileTags[var4] = var1.readInt();
         int var5 = var1.readInt();
         SharedSecrets.getJavaOISAccess().checkArray(var1, byte[].class, var5);
         this.profileData[var4] = new byte[var5];
         var1.readFully(this.profileData[var4]);
      }

   }

   public void doWrite(ObjectOutputStream var1) throws IOException {
      var1.writeInt(this.typeData.length);
      var1.write(this.typeData);
      var1.writeInt(this.profileTags.length);

      for(int var2 = 0; var2 < this.profileTags.length; ++var2) {
         var1.writeInt(this.profileTags[var2]);
         var1.writeInt(this.profileData[var2].length);
         var1.write(this.profileData[var2]);
      }

   }

   public synchronized int hashCode() {
      if (this.hashCode == 0) {
         int var1;
         for(var1 = 0; var1 < this.typeData.length; ++var1) {
            this.hashCode = this.hashCode * 37 + this.typeData[var1];
         }

         for(var1 = 0; var1 < this.profileTags.length; ++var1) {
            this.hashCode = this.hashCode * 37 + this.profileTags[var1];

            for(int var2 = 0; var2 < this.profileData[var1].length; ++var2) {
               this.hashCode = this.hashCode * 37 + this.profileData[var1][var2];
            }
         }
      }

      return this.hashCode;
   }

   private boolean equalArrays(int[] var1, int[] var2) {
      if (var1.length != var2.length) {
         return false;
      } else {
         for(int var3 = 0; var3 < var1.length; ++var3) {
            if (var1[var3] != var2[var3]) {
               return false;
            }
         }

         return true;
      }
   }

   private boolean equalArrays(byte[] var1, byte[] var2) {
      if (var1.length != var2.length) {
         return false;
      } else {
         for(int var3 = 0; var3 < var1.length; ++var3) {
            if (var1[var3] != var2[var3]) {
               return false;
            }
         }

         return true;
      }
   }

   private boolean equalArrays(byte[][] var1, byte[][] var2) {
      if (var1.length != var2.length) {
         return false;
      } else {
         for(int var3 = 0; var3 < var1.length; ++var3) {
            if (!this.equalArrays(var1[var3], var2[var3])) {
               return false;
            }
         }

         return true;
      }
   }

   public boolean equals(java.lang.Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof StubIORImpl)) {
         return false;
      } else {
         StubIORImpl var2 = (StubIORImpl)var1;
         if (var2.hashCode() != this.hashCode()) {
            return false;
         } else {
            return this.equalArrays(this.typeData, var2.typeData) && this.equalArrays(this.profileTags, var2.profileTags) && this.equalArrays(this.profileData, var2.profileData);
         }
      }
   }

   private void appendByteArray(StringBuffer var1, byte[] var2) {
      for(int var3 = 0; var3 < var2.length; ++var3) {
         var1.append(Integer.toHexString(var2[var3]));
      }

   }

   public String toString() {
      StringBuffer var1 = new StringBuffer();
      var1.append("SimpleIORImpl[");
      String var2 = new String(this.typeData);
      var1.append(var2);

      for(int var3 = 0; var3 < this.profileTags.length; ++var3) {
         var1.append(",(");
         var1.append(this.profileTags[var3]);
         var1.append(")");
         this.appendByteArray(var1, this.profileData[var3]);
      }

      var1.append("]");
      return var1.toString();
   }
}
