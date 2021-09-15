package java.util.jar;

import java.io.DataOutputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Manifest implements Cloneable {
   private Attributes attr = new Attributes();
   private Map<String, Attributes> entries = new HashMap();

   public Manifest() {
   }

   public Manifest(InputStream var1) throws IOException {
      this.read(var1);
   }

   public Manifest(Manifest var1) {
      this.attr.putAll(var1.getMainAttributes());
      this.entries.putAll(var1.getEntries());
   }

   public Attributes getMainAttributes() {
      return this.attr;
   }

   public Map<String, Attributes> getEntries() {
      return this.entries;
   }

   public Attributes getAttributes(String var1) {
      return (Attributes)this.getEntries().get(var1);
   }

   public void clear() {
      this.attr.clear();
      this.entries.clear();
   }

   public void write(OutputStream var1) throws IOException {
      DataOutputStream var2 = new DataOutputStream(var1);
      this.attr.writeMain(var2);
      Iterator var3 = this.entries.entrySet().iterator();

      while(var3.hasNext()) {
         Map.Entry var4 = (Map.Entry)var3.next();
         StringBuffer var5 = new StringBuffer("Name: ");
         String var6 = (String)var4.getKey();
         if (var6 != null) {
            byte[] var7 = var6.getBytes("UTF8");
            var6 = new String(var7, 0, 0, var7.length);
         }

         var5.append(var6);
         var5.append("\r\n");
         make72Safe(var5);
         var2.writeBytes(var5.toString());
         ((Attributes)var4.getValue()).write(var2);
      }

      var2.flush();
   }

   static void make72Safe(StringBuffer var0) {
      int var1 = var0.length();
      if (var1 > 72) {
         for(int var2 = 70; var2 < var1 - 2; var1 += 3) {
            var0.insert(var2, "\r\n ");
            var2 += 72;
         }
      }

   }

   public void read(InputStream var1) throws IOException {
      Manifest.FastInputStream var2 = new Manifest.FastInputStream(var1);
      byte[] var3 = new byte[512];
      this.attr.read(var2, var3);
      int var4 = 0;
      int var5 = 0;
      int var6 = 2;
      String var8 = null;
      boolean var9 = true;
      byte[] var10 = null;

      while(true) {
         while(true) {
            int var7;
            do {
               if ((var7 = var2.readLine(var3)) == -1) {
                  return;
               }

               --var7;
               if (var3[var7] != 10) {
                  throw new IOException("manifest line too long");
               }

               if (var7 > 0 && var3[var7 - 1] == 13) {
                  --var7;
               }
            } while(var7 == 0 && var9);

            var9 = false;
            if (var8 == null) {
               var8 = this.parseName(var3, var7);
               if (var8 == null) {
                  throw new IOException("invalid manifest format");
               }

               if (var2.peek() == 32) {
                  var10 = new byte[var7 - 6];
                  System.arraycopy(var3, 6, var10, 0, var7 - 6);
                  continue;
               }
            } else {
               byte[] var11 = new byte[var10.length + var7 - 1];
               System.arraycopy(var10, 0, var11, 0, var10.length);
               System.arraycopy(var3, 1, var11, var10.length, var7 - 1);
               if (var2.peek() == 32) {
                  var10 = var11;
                  continue;
               }

               var8 = new String(var11, 0, var11.length, "UTF8");
               var10 = null;
            }

            Attributes var12 = this.getAttributes(var8);
            if (var12 == null) {
               var12 = new Attributes(var6);
               this.entries.put(var8, var12);
            }

            var12.read(var2, var3);
            ++var4;
            var5 += var12.size();
            var6 = Math.max(2, var5 / var4);
            var8 = null;
            var9 = true;
         }
      }
   }

   private String parseName(byte[] var1, int var2) {
      if (this.toLower(var1[0]) == 110 && this.toLower(var1[1]) == 97 && this.toLower(var1[2]) == 109 && this.toLower(var1[3]) == 101 && var1[4] == 58 && var1[5] == 32) {
         try {
            return new String(var1, 6, var2 - 6, "UTF8");
         } catch (Exception var4) {
         }
      }

      return null;
   }

   private int toLower(int var1) {
      return var1 >= 65 && var1 <= 90 ? 97 + (var1 - 65) : var1;
   }

   public boolean equals(Object var1) {
      if (!(var1 instanceof Manifest)) {
         return false;
      } else {
         Manifest var2 = (Manifest)var1;
         return this.attr.equals(var2.getMainAttributes()) && this.entries.equals(var2.getEntries());
      }
   }

   public int hashCode() {
      return this.attr.hashCode() + this.entries.hashCode();
   }

   public Object clone() {
      return new Manifest(this);
   }

   static class FastInputStream extends FilterInputStream {
      private byte[] buf;
      private int count;
      private int pos;

      FastInputStream(InputStream var1) {
         this(var1, 8192);
      }

      FastInputStream(InputStream var1, int var2) {
         super(var1);
         this.count = 0;
         this.pos = 0;
         this.buf = new byte[var2];
      }

      public int read() throws IOException {
         if (this.pos >= this.count) {
            this.fill();
            if (this.pos >= this.count) {
               return -1;
            }
         }

         return Byte.toUnsignedInt(this.buf[this.pos++]);
      }

      public int read(byte[] var1, int var2, int var3) throws IOException {
         int var4 = this.count - this.pos;
         if (var4 <= 0) {
            if (var3 >= this.buf.length) {
               return this.in.read(var1, var2, var3);
            }

            this.fill();
            var4 = this.count - this.pos;
            if (var4 <= 0) {
               return -1;
            }
         }

         if (var3 > var4) {
            var3 = var4;
         }

         System.arraycopy(this.buf, this.pos, var1, var2, var3);
         this.pos += var3;
         return var3;
      }

      public int readLine(byte[] var1, int var2, int var3) throws IOException {
         byte[] var4 = this.buf;
         int var5 = 0;

         while(var5 < var3) {
            int var6 = this.count - this.pos;
            if (var6 <= 0) {
               this.fill();
               var6 = this.count - this.pos;
               if (var6 <= 0) {
                  return -1;
               }
            }

            int var7 = var3 - var5;
            if (var7 > var6) {
               var7 = var6;
            }

            int var8 = this.pos;
            int var9 = var8 + var7;

            while(var8 < var9 && var4[var8++] != 10) {
            }

            var7 = var8 - this.pos;
            System.arraycopy(var4, this.pos, var1, var2, var7);
            var2 += var7;
            var5 += var7;
            this.pos = var8;
            if (var4[var8 - 1] == 10) {
               break;
            }
         }

         return var5;
      }

      public byte peek() throws IOException {
         if (this.pos == this.count) {
            this.fill();
         }

         return this.pos == this.count ? -1 : this.buf[this.pos];
      }

      public int readLine(byte[] var1) throws IOException {
         return this.readLine(var1, 0, var1.length);
      }

      public long skip(long var1) throws IOException {
         if (var1 <= 0L) {
            return 0L;
         } else {
            long var3 = (long)(this.count - this.pos);
            if (var3 <= 0L) {
               return this.in.skip(var1);
            } else {
               if (var1 > var3) {
                  var1 = var3;
               }

               this.pos = (int)((long)this.pos + var1);
               return var1;
            }
         }
      }

      public int available() throws IOException {
         return this.count - this.pos + this.in.available();
      }

      public void close() throws IOException {
         if (this.in != null) {
            this.in.close();
            this.in = null;
            this.buf = null;
         }

      }

      private void fill() throws IOException {
         this.count = this.pos = 0;
         int var1 = this.in.read(this.buf, 0, this.buf.length);
         if (var1 > 0) {
            this.count = var1;
         }

      }
   }
}
