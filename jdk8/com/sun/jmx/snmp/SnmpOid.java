package com.sun.jmx.snmp;

import java.util.NoSuchElementException;
import java.util.StringTokenizer;

public class SnmpOid extends SnmpValue {
   protected long[] components = null;
   protected int componentCount = 0;
   static final String name = "Object Identifier";
   private static SnmpOidTable meta = null;
   static final long serialVersionUID = 8956237235607885096L;

   public SnmpOid() {
      this.components = new long[15];
      this.componentCount = 0;
   }

   public SnmpOid(long[] var1) {
      this.components = (long[])var1.clone();
      this.componentCount = this.components.length;
   }

   public SnmpOid(long var1) {
      this.components = new long[1];
      this.components[0] = var1;
      this.componentCount = this.components.length;
   }

   public SnmpOid(long var1, long var3, long var5, long var7) {
      this.components = new long[4];
      this.components[0] = var1;
      this.components[1] = var3;
      this.components[2] = var5;
      this.components[3] = var7;
      this.componentCount = this.components.length;
   }

   public SnmpOid(String var1) throws IllegalArgumentException {
      String var2 = var1;
      if (!var1.startsWith(".")) {
         try {
            var2 = this.resolveVarName(var1);
         } catch (SnmpStatusException var7) {
            throw new IllegalArgumentException(var7.getMessage());
         }
      }

      StringTokenizer var3 = new StringTokenizer(var2, ".", false);
      this.componentCount = var3.countTokens();
      if (this.componentCount == 0) {
         this.components = new long[15];
      } else {
         this.components = new long[this.componentCount];

         try {
            for(int var4 = 0; var4 < this.componentCount; ++var4) {
               try {
                  this.components[var4] = Long.parseLong(var3.nextToken());
               } catch (NoSuchElementException var6) {
               }
            }
         } catch (NumberFormatException var8) {
            throw new IllegalArgumentException(var1);
         }
      }

   }

   public int getLength() {
      return this.componentCount;
   }

   public long[] longValue() {
      long[] var1 = new long[this.componentCount];
      System.arraycopy(this.components, 0, var1, 0, this.componentCount);
      return var1;
   }

   public final long[] longValue(boolean var1) {
      return this.longValue();
   }

   public final long getOidArc(int var1) throws SnmpStatusException {
      try {
         return this.components[var1];
      } catch (Exception var3) {
         throw new SnmpStatusException(6);
      }
   }

   public Long toLong() {
      if (this.componentCount != 1) {
         throw new IllegalArgumentException();
      } else {
         return new Long(this.components[0]);
      }
   }

   public Integer toInteger() {
      if (this.componentCount == 1 && this.components[0] <= 2147483647L) {
         return new Integer((int)this.components[0]);
      } else {
         throw new IllegalArgumentException();
      }
   }

   public String toString() {
      String var1 = "";
      if (this.componentCount >= 1) {
         for(int var2 = 0; var2 < this.componentCount - 1; ++var2) {
            var1 = var1 + this.components[var2] + ".";
         }

         var1 = var1 + this.components[this.componentCount - 1];
      }

      return var1;
   }

   public Boolean toBoolean() {
      if (this.componentCount != 1 && this.components[0] != 1L && this.components[0] != 2L) {
         throw new IllegalArgumentException();
      } else {
         return this.components[0] == 1L;
      }
   }

   public Byte[] toByte() {
      Byte[] var1 = new Byte[this.componentCount];

      for(int var2 = 0; var2 < this.componentCount; ++var2) {
         if (this.components[0] > 255L) {
            throw new IllegalArgumentException();
         }

         var1[var2] = new Byte((byte)((int)this.components[var2]));
      }

      return var1;
   }

   public SnmpOid toOid() {
      long[] var1 = new long[this.componentCount];

      for(int var2 = 0; var2 < this.componentCount; ++var2) {
         var1[var2] = this.components[var2];
      }

      return new SnmpOid(var1);
   }

   public static SnmpOid toOid(long[] var0, int var1) throws SnmpStatusException {
      try {
         if (var0[var1] > 2147483647L) {
            throw new SnmpStatusException(2);
         } else {
            int var2 = (int)var0[var1++];
            long[] var3 = new long[var2];

            for(int var4 = 0; var4 < var2; ++var4) {
               var3[var4] = var0[var1 + var4];
            }

            return new SnmpOid(var3);
         }
      } catch (IndexOutOfBoundsException var5) {
         throw new SnmpStatusException(2);
      }
   }

   public static int nextOid(long[] var0, int var1) throws SnmpStatusException {
      try {
         if (var0[var1] > 2147483647L) {
            throw new SnmpStatusException(2);
         } else {
            int var2 = (int)var0[var1++];
            var1 += var2;
            if (var1 <= var0.length) {
               return var1;
            } else {
               throw new SnmpStatusException(2);
            }
         }
      } catch (IndexOutOfBoundsException var3) {
         throw new SnmpStatusException(2);
      }
   }

   public static void appendToOid(SnmpOid var0, SnmpOid var1) {
      var1.append((long)var0.getLength());
      var1.append(var0);
   }

   public final synchronized SnmpValue duplicate() {
      return (SnmpValue)this.clone();
   }

   public Object clone() {
      try {
         SnmpOid var1 = (SnmpOid)super.clone();
         var1.components = new long[this.componentCount];
         System.arraycopy(this.components, 0, var1.components, 0, this.componentCount);
         return var1;
      } catch (CloneNotSupportedException var2) {
         throw new InternalError();
      }
   }

   public void insert(long var1) {
      this.enlargeIfNeeded(1);

      for(int var3 = this.componentCount - 1; var3 >= 0; --var3) {
         this.components[var3 + 1] = this.components[var3];
      }

      this.components[0] = var1;
      ++this.componentCount;
   }

   public void insert(int var1) {
      this.insert((long)var1);
   }

   public void append(SnmpOid var1) {
      this.enlargeIfNeeded(var1.componentCount);

      for(int var2 = 0; var2 < var1.componentCount; ++var2) {
         this.components[this.componentCount + var2] = var1.components[var2];
      }

      this.componentCount += var1.componentCount;
   }

   public void append(long var1) {
      this.enlargeIfNeeded(1);
      this.components[this.componentCount] = var1;
      ++this.componentCount;
   }

   public void addToOid(String var1) throws SnmpStatusException {
      SnmpOid var2 = new SnmpOid(var1);
      this.append(var2);
   }

   public void addToOid(long[] var1) throws SnmpStatusException {
      SnmpOid var2 = new SnmpOid(var1);
      this.append(var2);
   }

   public boolean isValid() {
      return this.componentCount >= 2 && 0L <= this.components[0] && this.components[0] < 3L && 0L <= this.components[1] && this.components[1] < 40L;
   }

   public boolean equals(Object var1) {
      boolean var2 = false;
      if (var1 instanceof SnmpOid) {
         SnmpOid var3 = (SnmpOid)var1;
         if (var3.componentCount == this.componentCount) {
            int var4 = 0;

            for(long[] var5 = var3.components; var4 < this.componentCount && this.components[var4] == var5[var4]; ++var4) {
            }

            var2 = var4 == this.componentCount;
         }
      }

      return var2;
   }

   public int hashCode() {
      long var1 = 0L;

      for(int var3 = 0; var3 < this.componentCount; ++var3) {
         var1 = var1 * 31L + this.components[var3];
      }

      return (int)var1;
   }

   public int compareTo(SnmpOid var1) {
      boolean var2 = false;
      boolean var3 = false;
      int var4 = Math.min(this.componentCount, var1.componentCount);
      long[] var5 = var1.components;

      int var7;
      for(var7 = 0; var7 < var4 && this.components[var7] == var5[var7]; ++var7) {
      }

      int var6;
      if (var7 == this.componentCount && var7 == var1.componentCount) {
         var6 = 0;
      } else if (var7 == this.componentCount) {
         var6 = -1;
      } else if (var7 == var1.componentCount) {
         var6 = 1;
      } else {
         var6 = this.components[var7] < var5[var7] ? -1 : 1;
      }

      return var6;
   }

   public String resolveVarName(String var1) throws SnmpStatusException {
      int var2 = var1.indexOf(46);

      try {
         return this.handleLong(var1, var2);
      } catch (NumberFormatException var5) {
         SnmpOidTable var3 = getSnmpOidTable();
         if (var3 == null) {
            throw new SnmpStatusException(2);
         } else {
            SnmpOidRecord var4;
            if (var2 <= 0) {
               var4 = var3.resolveVarName(var1);
               return var4.getOid();
            } else {
               var4 = var3.resolveVarName(var1.substring(0, var2));
               return var4.getOid() + var1.substring(var2);
            }
         }
      }
   }

   public String getTypeName() {
      return "Object Identifier";
   }

   public static SnmpOidTable getSnmpOidTable() {
      return meta;
   }

   public static void setSnmpOidTable(SnmpOidTable var0) {
      SecurityManager var1 = System.getSecurityManager();
      if (var1 != null) {
         var1.checkPermission(new SnmpPermission("setSnmpOidTable"));
      }

      meta = var0;
   }

   public String toOctetString() {
      return new String(this.tobyte());
   }

   private byte[] tobyte() {
      byte[] var1 = new byte[this.componentCount];

      for(int var2 = 0; var2 < this.componentCount; ++var2) {
         if (this.components[0] > 255L) {
            throw new IllegalArgumentException();
         }

         var1[var2] = (byte)((int)this.components[var2]);
      }

      return var1;
   }

   private void enlargeIfNeeded(int var1) {
      int var2;
      for(var2 = this.components.length; this.componentCount + var1 > var2; var2 *= 2) {
      }

      if (var2 > this.components.length) {
         long[] var3 = new long[var2];

         for(int var4 = 0; var4 < this.components.length; ++var4) {
            var3[var4] = this.components[var4];
         }

         this.components = var3;
      }

   }

   private String handleLong(String var1, int var2) throws NumberFormatException, SnmpStatusException {
      String var3;
      if (var2 > 0) {
         var3 = var1.substring(0, var2);
      } else {
         var3 = var1;
      }

      Long.parseLong(var3);
      return var1;
   }
}
