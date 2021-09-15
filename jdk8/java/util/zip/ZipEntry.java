package java.util.zip;

import java.nio.file.attribute.FileTime;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class ZipEntry implements ZipConstants, Cloneable {
   String name;
   long xdostime = -1L;
   FileTime mtime;
   FileTime atime;
   FileTime ctime;
   long crc = -1L;
   long size = -1L;
   long csize = -1L;
   int method = -1;
   int flag = 0;
   byte[] extra;
   String comment;
   public static final int STORED = 0;
   public static final int DEFLATED = 8;
   static final long DOSTIME_BEFORE_1980 = 2162688L;
   private static final long UPPER_DOSTIME_BOUND = 4036608000000L;

   public ZipEntry(String var1) {
      Objects.requireNonNull(var1, (String)"name");
      if (var1.length() > 65535) {
         throw new IllegalArgumentException("entry name too long");
      } else {
         this.name = var1;
      }
   }

   public ZipEntry(ZipEntry var1) {
      Objects.requireNonNull(var1, (String)"entry");
      this.name = var1.name;
      this.xdostime = var1.xdostime;
      this.mtime = var1.mtime;
      this.atime = var1.atime;
      this.ctime = var1.ctime;
      this.crc = var1.crc;
      this.size = var1.size;
      this.csize = var1.csize;
      this.method = var1.method;
      this.flag = var1.flag;
      this.extra = var1.extra;
      this.comment = var1.comment;
   }

   ZipEntry() {
   }

   public String getName() {
      return this.name;
   }

   public void setTime(long var1) {
      this.xdostime = ZipUtils.javaToExtendedDosTime(var1);
      if (this.xdostime != 2162688L && var1 <= 4036608000000L) {
         this.mtime = null;
      } else {
         this.mtime = FileTime.from(var1, TimeUnit.MILLISECONDS);
      }

   }

   public long getTime() {
      if (this.mtime != null) {
         return this.mtime.toMillis();
      } else {
         return this.xdostime != -1L ? ZipUtils.extendedDosToJavaTime(this.xdostime) : -1L;
      }
   }

   public ZipEntry setLastModifiedTime(FileTime var1) {
      this.mtime = (FileTime)Objects.requireNonNull(var1, (String)"lastModifiedTime");
      this.xdostime = ZipUtils.javaToExtendedDosTime(var1.to(TimeUnit.MILLISECONDS));
      return this;
   }

   public FileTime getLastModifiedTime() {
      if (this.mtime != null) {
         return this.mtime;
      } else {
         return this.xdostime == -1L ? null : FileTime.from(this.getTime(), TimeUnit.MILLISECONDS);
      }
   }

   public ZipEntry setLastAccessTime(FileTime var1) {
      this.atime = (FileTime)Objects.requireNonNull(var1, (String)"lastAccessTime");
      return this;
   }

   public FileTime getLastAccessTime() {
      return this.atime;
   }

   public ZipEntry setCreationTime(FileTime var1) {
      this.ctime = (FileTime)Objects.requireNonNull(var1, (String)"creationTime");
      return this;
   }

   public FileTime getCreationTime() {
      return this.ctime;
   }

   public void setSize(long var1) {
      if (var1 < 0L) {
         throw new IllegalArgumentException("invalid entry size");
      } else {
         this.size = var1;
      }
   }

   public long getSize() {
      return this.size;
   }

   public long getCompressedSize() {
      return this.csize;
   }

   public void setCompressedSize(long var1) {
      this.csize = var1;
   }

   public void setCrc(long var1) {
      if (var1 >= 0L && var1 <= 4294967295L) {
         this.crc = var1;
      } else {
         throw new IllegalArgumentException("invalid entry crc-32");
      }
   }

   public long getCrc() {
      return this.crc;
   }

   public void setMethod(int var1) {
      if (var1 != 0 && var1 != 8) {
         throw new IllegalArgumentException("invalid compression method");
      } else {
         this.method = var1;
      }
   }

   public int getMethod() {
      return this.method;
   }

   public void setExtra(byte[] var1) {
      this.setExtra0(var1, false);
   }

   void setExtra0(byte[] var1, boolean var2) {
      if (var1 != null) {
         if (var1.length > 65535) {
            throw new IllegalArgumentException("invalid extra field length");
         }

         int var3 = 0;

         int var6;
         for(int var4 = var1.length; var3 + 4 < var4; var3 += var6) {
            int var5 = ZipUtils.get16(var1, var3);
            var6 = ZipUtils.get16(var1, var3 + 2);
            var3 += 4;
            if (var3 + var6 > var4) {
               break;
            }

            switch(var5) {
            case 1:
               if (var2 && var6 >= 16) {
                  this.size = ZipUtils.get64(var1, var3);
                  this.csize = ZipUtils.get64(var1, var3 + 8);
               }
               break;
            case 10:
               if (var6 >= 32) {
                  int var7 = var3 + 4;
                  if (ZipUtils.get16(var1, var7) == 1 && ZipUtils.get16(var1, var7 + 2) == 24) {
                     this.mtime = ZipUtils.winTimeToFileTime(ZipUtils.get64(var1, var7 + 4));
                     this.atime = ZipUtils.winTimeToFileTime(ZipUtils.get64(var1, var7 + 12));
                     this.ctime = ZipUtils.winTimeToFileTime(ZipUtils.get64(var1, var7 + 20));
                  }
               }
               break;
            case 21589:
               int var8 = Byte.toUnsignedInt(var1[var3]);
               int var9 = 1;
               if ((var8 & 1) != 0 && var9 + 4 <= var6) {
                  this.mtime = ZipUtils.unixTimeToFileTime(ZipUtils.get32(var1, var3 + var9));
                  var9 += 4;
               }

               if ((var8 & 2) != 0 && var9 + 4 <= var6) {
                  this.atime = ZipUtils.unixTimeToFileTime(ZipUtils.get32(var1, var3 + var9));
                  var9 += 4;
               }

               if ((var8 & 4) != 0 && var9 + 4 <= var6) {
                  this.ctime = ZipUtils.unixTimeToFileTime(ZipUtils.get32(var1, var3 + var9));
                  var9 += 4;
               }
            }
         }
      }

      this.extra = var1;
   }

   public byte[] getExtra() {
      return this.extra;
   }

   public void setComment(String var1) {
      this.comment = var1;
   }

   public String getComment() {
      return this.comment;
   }

   public boolean isDirectory() {
      return this.name.endsWith("/");
   }

   public String toString() {
      return this.getName();
   }

   public int hashCode() {
      return this.name.hashCode();
   }

   public Object clone() {
      try {
         ZipEntry var1 = (ZipEntry)super.clone();
         var1.extra = this.extra == null ? null : (byte[])this.extra.clone();
         return var1;
      } catch (CloneNotSupportedException var2) {
         throw new InternalError(var2);
      }
   }
}
