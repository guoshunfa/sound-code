package com.sun.jndi.dns;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import javax.naming.CommunicationException;
import javax.naming.InvalidNameException;

public class ResourceRecord {
   static final int TYPE_A = 1;
   static final int TYPE_NS = 2;
   static final int TYPE_CNAME = 5;
   static final int TYPE_SOA = 6;
   static final int TYPE_PTR = 12;
   static final int TYPE_HINFO = 13;
   static final int TYPE_MX = 15;
   static final int TYPE_TXT = 16;
   static final int TYPE_AAAA = 28;
   static final int TYPE_SRV = 33;
   static final int TYPE_NAPTR = 35;
   static final int QTYPE_AXFR = 252;
   static final int QTYPE_STAR = 255;
   static final String[] rrTypeNames = new String[]{null, "A", "NS", null, null, "CNAME", "SOA", null, null, null, null, null, "PTR", "HINFO", null, "MX", "TXT", null, null, null, null, null, null, null, null, null, null, null, "AAAA", null, null, null, null, "SRV", null, "NAPTR"};
   static final int CLASS_INTERNET = 1;
   static final int CLASS_HESIOD = 2;
   static final int QCLASS_STAR = 255;
   static final String[] rrClassNames = new String[]{null, "IN", null, null, "HS"};
   private static final int MAXIMUM_COMPRESSION_REFERENCES = 16;
   byte[] msg;
   int msgLen;
   boolean qSection;
   int offset;
   int rrlen;
   DnsName name;
   int rrtype;
   String rrtypeName;
   int rrclass;
   String rrclassName;
   int ttl = 0;
   int rdlen = 0;
   Object rdata = null;
   private static final boolean debug = false;

   ResourceRecord(byte[] var1, int var2, int var3, boolean var4, boolean var5) throws CommunicationException {
      this.msg = var1;
      this.msgLen = var2;
      this.offset = var3;
      this.qSection = var4;
      this.decode(var5);
   }

   public String toString() {
      String var1 = this.name + " " + this.rrclassName + " " + this.rrtypeName;
      if (!this.qSection) {
         var1 = var1 + " " + this.ttl + " " + (this.rdata != null ? this.rdata : "[n/a]");
      }

      return var1;
   }

   public DnsName getName() {
      return this.name;
   }

   public int size() {
      return this.rrlen;
   }

   public int getType() {
      return this.rrtype;
   }

   public int getRrclass() {
      return this.rrclass;
   }

   public Object getRdata() {
      return this.rdata;
   }

   public static String getTypeName(int var0) {
      return valueToName(var0, rrTypeNames);
   }

   public static int getType(String var0) {
      return nameToValue(var0, rrTypeNames);
   }

   public static String getRrclassName(int var0) {
      return valueToName(var0, rrClassNames);
   }

   public static int getRrclass(String var0) {
      return nameToValue(var0, rrClassNames);
   }

   private static String valueToName(int var0, String[] var1) {
      String var2 = null;
      if (var0 > 0 && var0 < var1.length) {
         var2 = var1[var0];
      } else if (var0 == 255) {
         var2 = "*";
      }

      if (var2 == null) {
         var2 = Integer.toString(var0);
      }

      return var2;
   }

   private static int nameToValue(String var0, String[] var1) {
      if (var0.equals("")) {
         return -1;
      } else if (var0.equals("*")) {
         return 255;
      } else {
         if (Character.isDigit(var0.charAt(0))) {
            try {
               return Integer.parseInt(var0);
            } catch (NumberFormatException var3) {
            }
         }

         for(int var2 = 1; var2 < var1.length; ++var2) {
            if (var1[var2] != null && var0.equalsIgnoreCase(var1[var2])) {
               return var2;
            }
         }

         return -1;
      }
   }

   public static int compareSerialNumbers(long var0, long var2) {
      long var4 = var2 - var0;
      if (var4 == 0L) {
         return 0;
      } else {
         return (var4 <= 0L || var4 > 2147483647L) && (var4 >= 0L || -var4 <= 2147483647L) ? 1 : -1;
      }
   }

   private void decode(boolean var1) throws CommunicationException {
      int var2 = this.offset;
      this.name = new DnsName();
      var2 = this.decodeName(var2, this.name);
      this.rrtype = this.getUShort(var2);
      this.rrtypeName = this.rrtype < rrTypeNames.length ? rrTypeNames[this.rrtype] : null;
      if (this.rrtypeName == null) {
         this.rrtypeName = Integer.toString(this.rrtype);
      }

      var2 += 2;
      this.rrclass = this.getUShort(var2);
      this.rrclassName = this.rrclass < rrClassNames.length ? rrClassNames[this.rrclass] : null;
      if (this.rrclassName == null) {
         this.rrclassName = Integer.toString(this.rrclass);
      }

      var2 += 2;
      if (!this.qSection) {
         this.ttl = this.getInt(var2);
         var2 += 4;
         this.rdlen = this.getUShort(var2);
         var2 += 2;
         this.rdata = !var1 && this.rrtype != 6 ? null : this.decodeRdata(var2);
         if (this.rdata instanceof DnsName) {
            this.rdata = this.rdata.toString();
         }

         var2 += this.rdlen;
      }

      this.rrlen = var2 - this.offset;
      this.msg = null;
   }

   private int getUByte(int var1) {
      return this.msg[var1] & 255;
   }

   private int getUShort(int var1) {
      return (this.msg[var1] & 255) << 8 | this.msg[var1 + 1] & 255;
   }

   private int getInt(int var1) {
      return this.getUShort(var1) << 16 | this.getUShort(var1 + 2);
   }

   private long getUInt(int var1) {
      return (long)this.getInt(var1) & 4294967295L;
   }

   private DnsName decodeName(int var1) throws CommunicationException {
      DnsName var2 = new DnsName();
      this.decodeName(var1, var2);
      return var2;
   }

   private int decodeName(int var1, DnsName var2) throws CommunicationException {
      int var3 = -1;
      int var4 = 0;

      try {
         while(true) {
            if (var4 > 16) {
               throw new IOException("Too many compression references");
            }

            int var5 = this.msg[var1] & 255;
            if (var5 == 0) {
               ++var1;
               var2.add(0, "");
               break;
            }

            if (var5 <= 63) {
               ++var1;
               var2.add(0, new String(this.msg, var1, var5, StandardCharsets.ISO_8859_1));
               var1 += var5;
            } else {
               if ((var5 & 192) != 192) {
                  throw new IOException("Invalid label type: " + var5);
               }

               ++var4;
               if (var3 == -1) {
                  var3 = var1 + 2;
               }

               var1 = this.getUShort(var1) & 16383;
            }
         }
      } catch (InvalidNameException | IOException var7) {
         CommunicationException var6 = new CommunicationException("DNS error: malformed packet");
         var6.initCause(var7);
         throw var6;
      }

      if (var3 == -1) {
         var3 = var1;
      }

      return var3;
   }

   private Object decodeRdata(int var1) throws CommunicationException {
      if (this.rrclass == 1) {
         switch(this.rrtype) {
         case 1:
            return this.decodeA(var1);
         case 2:
         case 5:
         case 12:
            return this.decodeName(var1);
         case 3:
         case 4:
         case 7:
         case 8:
         case 9:
         case 10:
         case 11:
         case 14:
         case 17:
         case 18:
         case 19:
         case 20:
         case 21:
         case 22:
         case 23:
         case 24:
         case 25:
         case 26:
         case 27:
         case 29:
         case 30:
         case 31:
         case 32:
         case 34:
         default:
            break;
         case 6:
            return this.decodeSoa(var1);
         case 13:
            return this.decodeHinfo(var1);
         case 15:
            return this.decodeMx(var1);
         case 16:
            return this.decodeTxt(var1);
         case 28:
            return this.decodeAAAA(var1);
         case 33:
            return this.decodeSrv(var1);
         case 35:
            return this.decodeNaptr(var1);
         }
      }

      byte[] var2 = new byte[this.rdlen];
      System.arraycopy(this.msg, var1, var2, 0, this.rdlen);
      return var2;
   }

   private String decodeMx(int var1) throws CommunicationException {
      int var2 = this.getUShort(var1);
      var1 += 2;
      DnsName var3 = this.decodeName(var1);
      return var2 + " " + var3;
   }

   private String decodeSoa(int var1) throws CommunicationException {
      DnsName var2 = new DnsName();
      var1 = this.decodeName(var1, var2);
      DnsName var3 = new DnsName();
      var1 = this.decodeName(var1, var3);
      long var4 = this.getUInt(var1);
      var1 += 4;
      long var6 = this.getUInt(var1);
      var1 += 4;
      long var8 = this.getUInt(var1);
      var1 += 4;
      long var10 = this.getUInt(var1);
      var1 += 4;
      long var12 = this.getUInt(var1);
      var1 += 4;
      return var2 + " " + var3 + " " + var4 + " " + var6 + " " + var8 + " " + var10 + " " + var12;
   }

   private String decodeSrv(int var1) throws CommunicationException {
      int var2 = this.getUShort(var1);
      var1 += 2;
      int var3 = this.getUShort(var1);
      var1 += 2;
      int var4 = this.getUShort(var1);
      var1 += 2;
      DnsName var5 = this.decodeName(var1);
      return var2 + " " + var3 + " " + var4 + " " + var5;
   }

   private String decodeNaptr(int var1) throws CommunicationException {
      int var2 = this.getUShort(var1);
      var1 += 2;
      int var3 = this.getUShort(var1);
      var1 += 2;
      StringBuffer var4 = new StringBuffer();
      var1 += this.decodeCharString(var1, var4);
      StringBuffer var5 = new StringBuffer();
      var1 += this.decodeCharString(var1, var5);
      StringBuffer var6 = new StringBuffer(this.rdlen);
      var1 += this.decodeCharString(var1, var6);
      DnsName var7 = this.decodeName(var1);
      return var2 + " " + var3 + " " + var4 + " " + var5 + " " + var6 + " " + var7;
   }

   private String decodeTxt(int var1) {
      StringBuffer var2 = new StringBuffer(this.rdlen);
      int var3 = var1 + this.rdlen;

      while(var1 < var3) {
         var1 += this.decodeCharString(var1, var2);
         if (var1 < var3) {
            var2.append(' ');
         }
      }

      return var2.toString();
   }

   private String decodeHinfo(int var1) {
      StringBuffer var2 = new StringBuffer(this.rdlen);
      var1 += this.decodeCharString(var1, var2);
      var2.append(' ');
      int var10000 = var1 + this.decodeCharString(var1, var2);
      return var2.toString();
   }

   private int decodeCharString(int var1, StringBuffer var2) {
      int var3 = var2.length();
      int var4 = this.getUByte(var1++);
      boolean var5 = var4 == 0;

      for(int var6 = 0; var6 < var4; ++var6) {
         int var7 = this.getUByte(var1++);
         var5 |= var7 == 32;
         if (var7 == 92 || var7 == 34) {
            var5 = true;
            var2.append('\\');
         }

         var2.append((char)var7);
      }

      if (var5) {
         var2.insert(var3, '"');
         var2.append('"');
      }

      return var4 + 1;
   }

   private String decodeA(int var1) {
      return (this.msg[var1] & 255) + "." + (this.msg[var1 + 1] & 255) + "." + (this.msg[var1 + 2] & 255) + "." + (this.msg[var1 + 3] & 255);
   }

   private String decodeAAAA(int var1) {
      int[] var2 = new int[8];

      int var3;
      for(var3 = 0; var3 < 8; ++var3) {
         var2[var3] = this.getUShort(var1);
         var1 += 2;
      }

      var3 = -1;
      int var4 = 0;
      int var5 = -1;
      int var6 = 0;

      for(int var7 = 0; var7 < 8; ++var7) {
         if (var2[var7] == 0) {
            if (var3 == -1) {
               var3 = var7;
               var4 = 1;
            } else {
               ++var4;
               if (var4 >= 2 && var4 > var6) {
                  var5 = var3;
                  var6 = var4;
               }
            }
         } else {
            var3 = -1;
         }
      }

      if (var5 == 0) {
         if (var6 == 6 || var6 == 7 && var2[7] > 1) {
            return "::" + this.decodeA(var1 - 4);
         }

         if (var6 == 5 && var2[5] == 65535) {
            return "::ffff:" + this.decodeA(var1 - 4);
         }
      }

      boolean var10 = var5 != -1;
      StringBuffer var8 = new StringBuffer(40);
      if (var5 == 0) {
         var8.append(':');
      }

      for(int var9 = 0; var9 < 8; ++var9) {
         if (var10 && var9 >= var5 && var9 < var5 + var6) {
            if (var10 && var9 == var5) {
               var8.append(':');
            }
         } else {
            var8.append(Integer.toHexString(var2[var9]));
            if (var9 < 7) {
               var8.append(':');
            }
         }
      }

      return var8.toString();
   }

   private static void dprint(String var0) {
   }
}
