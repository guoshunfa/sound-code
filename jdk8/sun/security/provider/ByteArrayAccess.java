package sun.security.provider;

import java.nio.ByteOrder;
import java.security.AccessController;
import java.security.PrivilegedAction;
import sun.misc.Unsafe;
import sun.security.action.GetPropertyAction;

final class ByteArrayAccess {
   private static final Unsafe unsafe = Unsafe.getUnsafe();
   private static final boolean littleEndianUnaligned;
   private static final boolean bigEndian;
   private static final int byteArrayOfs;

   private ByteArrayAccess() {
   }

   private static boolean unaligned() {
      String var0 = (String)AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction("os.arch", "")));
      return var0.equals("i386") || var0.equals("x86") || var0.equals("amd64") || var0.equals("x86_64") || var0.equals("ppc64") || var0.equals("ppc64le");
   }

   static void b2iLittle(byte[] var0, int var1, int[] var2, int var3, int var4) {
      if (var1 >= 0 && var0.length - var1 >= var4 && var3 >= 0 && var2.length - var3 >= var4 / 4) {
         if (littleEndianUnaligned) {
            var1 += byteArrayOfs;

            for(var4 += var1; var1 < var4; var1 += 4) {
               var2[var3++] = unsafe.getInt(var0, (long)var1);
            }
         } else if (bigEndian && (var1 & 3) == 0) {
            var1 += byteArrayOfs;

            for(var4 += var1; var1 < var4; var1 += 4) {
               var2[var3++] = Integer.reverseBytes(unsafe.getInt(var0, (long)var1));
            }
         } else {
            for(var4 += var1; var1 < var4; var1 += 4) {
               var2[var3++] = var0[var1] & 255 | (var0[var1 + 1] & 255) << 8 | (var0[var1 + 2] & 255) << 16 | var0[var1 + 3] << 24;
            }
         }

      } else {
         throw new ArrayIndexOutOfBoundsException();
      }
   }

   static void b2iLittle64(byte[] var0, int var1, int[] var2) {
      if (var1 >= 0 && var0.length - var1 >= 64 && var2.length >= 16) {
         if (littleEndianUnaligned) {
            var1 += byteArrayOfs;
            var2[0] = unsafe.getInt(var0, (long)var1);
            var2[1] = unsafe.getInt(var0, (long)(var1 + 4));
            var2[2] = unsafe.getInt(var0, (long)(var1 + 8));
            var2[3] = unsafe.getInt(var0, (long)(var1 + 12));
            var2[4] = unsafe.getInt(var0, (long)(var1 + 16));
            var2[5] = unsafe.getInt(var0, (long)(var1 + 20));
            var2[6] = unsafe.getInt(var0, (long)(var1 + 24));
            var2[7] = unsafe.getInt(var0, (long)(var1 + 28));
            var2[8] = unsafe.getInt(var0, (long)(var1 + 32));
            var2[9] = unsafe.getInt(var0, (long)(var1 + 36));
            var2[10] = unsafe.getInt(var0, (long)(var1 + 40));
            var2[11] = unsafe.getInt(var0, (long)(var1 + 44));
            var2[12] = unsafe.getInt(var0, (long)(var1 + 48));
            var2[13] = unsafe.getInt(var0, (long)(var1 + 52));
            var2[14] = unsafe.getInt(var0, (long)(var1 + 56));
            var2[15] = unsafe.getInt(var0, (long)(var1 + 60));
         } else if (bigEndian && (var1 & 3) == 0) {
            var1 += byteArrayOfs;
            var2[0] = Integer.reverseBytes(unsafe.getInt(var0, (long)var1));
            var2[1] = Integer.reverseBytes(unsafe.getInt(var0, (long)(var1 + 4)));
            var2[2] = Integer.reverseBytes(unsafe.getInt(var0, (long)(var1 + 8)));
            var2[3] = Integer.reverseBytes(unsafe.getInt(var0, (long)(var1 + 12)));
            var2[4] = Integer.reverseBytes(unsafe.getInt(var0, (long)(var1 + 16)));
            var2[5] = Integer.reverseBytes(unsafe.getInt(var0, (long)(var1 + 20)));
            var2[6] = Integer.reverseBytes(unsafe.getInt(var0, (long)(var1 + 24)));
            var2[7] = Integer.reverseBytes(unsafe.getInt(var0, (long)(var1 + 28)));
            var2[8] = Integer.reverseBytes(unsafe.getInt(var0, (long)(var1 + 32)));
            var2[9] = Integer.reverseBytes(unsafe.getInt(var0, (long)(var1 + 36)));
            var2[10] = Integer.reverseBytes(unsafe.getInt(var0, (long)(var1 + 40)));
            var2[11] = Integer.reverseBytes(unsafe.getInt(var0, (long)(var1 + 44)));
            var2[12] = Integer.reverseBytes(unsafe.getInt(var0, (long)(var1 + 48)));
            var2[13] = Integer.reverseBytes(unsafe.getInt(var0, (long)(var1 + 52)));
            var2[14] = Integer.reverseBytes(unsafe.getInt(var0, (long)(var1 + 56)));
            var2[15] = Integer.reverseBytes(unsafe.getInt(var0, (long)(var1 + 60)));
         } else {
            b2iLittle(var0, var1, var2, 0, 64);
         }

      } else {
         throw new ArrayIndexOutOfBoundsException();
      }
   }

   static void i2bLittle(int[] var0, int var1, byte[] var2, int var3, int var4) {
      if (var1 >= 0 && var0.length - var1 >= var4 / 4 && var3 >= 0 && var2.length - var3 >= var4) {
         if (littleEndianUnaligned) {
            var3 += byteArrayOfs;

            for(var4 += var3; var3 < var4; var3 += 4) {
               unsafe.putInt(var2, (long)var3, var0[var1++]);
            }
         } else {
            int var5;
            if (bigEndian && (var3 & 3) == 0) {
               var3 += byteArrayOfs;

               for(var4 += var3; var3 < var4; var3 += 4) {
                  unsafe.putInt(var2, (long)var3, Integer.reverseBytes(var0[var1++]));
               }
            } else {
               for(var4 += var3; var3 < var4; var2[var3++] = (byte)(var5 >> 24)) {
                  var5 = var0[var1++];
                  var2[var3++] = (byte)var5;
                  var2[var3++] = (byte)(var5 >> 8);
                  var2[var3++] = (byte)(var5 >> 16);
               }
            }
         }

      } else {
         throw new ArrayIndexOutOfBoundsException();
      }
   }

   static void i2bLittle4(int var0, byte[] var1, int var2) {
      if (var2 >= 0 && var1.length - var2 >= 4) {
         if (littleEndianUnaligned) {
            unsafe.putInt(var1, (long)(byteArrayOfs + var2), var0);
         } else if (bigEndian && (var2 & 3) == 0) {
            unsafe.putInt(var1, (long)(byteArrayOfs + var2), Integer.reverseBytes(var0));
         } else {
            var1[var2] = (byte)var0;
            var1[var2 + 1] = (byte)(var0 >> 8);
            var1[var2 + 2] = (byte)(var0 >> 16);
            var1[var2 + 3] = (byte)(var0 >> 24);
         }

      } else {
         throw new ArrayIndexOutOfBoundsException();
      }
   }

   static void b2iBig(byte[] var0, int var1, int[] var2, int var3, int var4) {
      if (var1 >= 0 && var0.length - var1 >= var4 && var3 >= 0 && var2.length - var3 >= var4 / 4) {
         if (littleEndianUnaligned) {
            var1 += byteArrayOfs;

            for(var4 += var1; var1 < var4; var1 += 4) {
               var2[var3++] = Integer.reverseBytes(unsafe.getInt(var0, (long)var1));
            }
         } else if (bigEndian && (var1 & 3) == 0) {
            var1 += byteArrayOfs;

            for(var4 += var1; var1 < var4; var1 += 4) {
               var2[var3++] = unsafe.getInt(var0, (long)var1);
            }
         } else {
            for(var4 += var1; var1 < var4; var1 += 4) {
               var2[var3++] = var0[var1 + 3] & 255 | (var0[var1 + 2] & 255) << 8 | (var0[var1 + 1] & 255) << 16 | var0[var1] << 24;
            }
         }

      } else {
         throw new ArrayIndexOutOfBoundsException();
      }
   }

   static void b2iBig64(byte[] var0, int var1, int[] var2) {
      if (var1 >= 0 && var0.length - var1 >= 64 && var2.length >= 16) {
         if (littleEndianUnaligned) {
            var1 += byteArrayOfs;
            var2[0] = Integer.reverseBytes(unsafe.getInt(var0, (long)var1));
            var2[1] = Integer.reverseBytes(unsafe.getInt(var0, (long)(var1 + 4)));
            var2[2] = Integer.reverseBytes(unsafe.getInt(var0, (long)(var1 + 8)));
            var2[3] = Integer.reverseBytes(unsafe.getInt(var0, (long)(var1 + 12)));
            var2[4] = Integer.reverseBytes(unsafe.getInt(var0, (long)(var1 + 16)));
            var2[5] = Integer.reverseBytes(unsafe.getInt(var0, (long)(var1 + 20)));
            var2[6] = Integer.reverseBytes(unsafe.getInt(var0, (long)(var1 + 24)));
            var2[7] = Integer.reverseBytes(unsafe.getInt(var0, (long)(var1 + 28)));
            var2[8] = Integer.reverseBytes(unsafe.getInt(var0, (long)(var1 + 32)));
            var2[9] = Integer.reverseBytes(unsafe.getInt(var0, (long)(var1 + 36)));
            var2[10] = Integer.reverseBytes(unsafe.getInt(var0, (long)(var1 + 40)));
            var2[11] = Integer.reverseBytes(unsafe.getInt(var0, (long)(var1 + 44)));
            var2[12] = Integer.reverseBytes(unsafe.getInt(var0, (long)(var1 + 48)));
            var2[13] = Integer.reverseBytes(unsafe.getInt(var0, (long)(var1 + 52)));
            var2[14] = Integer.reverseBytes(unsafe.getInt(var0, (long)(var1 + 56)));
            var2[15] = Integer.reverseBytes(unsafe.getInt(var0, (long)(var1 + 60)));
         } else if (bigEndian && (var1 & 3) == 0) {
            var1 += byteArrayOfs;
            var2[0] = unsafe.getInt(var0, (long)var1);
            var2[1] = unsafe.getInt(var0, (long)(var1 + 4));
            var2[2] = unsafe.getInt(var0, (long)(var1 + 8));
            var2[3] = unsafe.getInt(var0, (long)(var1 + 12));
            var2[4] = unsafe.getInt(var0, (long)(var1 + 16));
            var2[5] = unsafe.getInt(var0, (long)(var1 + 20));
            var2[6] = unsafe.getInt(var0, (long)(var1 + 24));
            var2[7] = unsafe.getInt(var0, (long)(var1 + 28));
            var2[8] = unsafe.getInt(var0, (long)(var1 + 32));
            var2[9] = unsafe.getInt(var0, (long)(var1 + 36));
            var2[10] = unsafe.getInt(var0, (long)(var1 + 40));
            var2[11] = unsafe.getInt(var0, (long)(var1 + 44));
            var2[12] = unsafe.getInt(var0, (long)(var1 + 48));
            var2[13] = unsafe.getInt(var0, (long)(var1 + 52));
            var2[14] = unsafe.getInt(var0, (long)(var1 + 56));
            var2[15] = unsafe.getInt(var0, (long)(var1 + 60));
         } else {
            b2iBig(var0, var1, var2, 0, 64);
         }

      } else {
         throw new ArrayIndexOutOfBoundsException();
      }
   }

   static void i2bBig(int[] var0, int var1, byte[] var2, int var3, int var4) {
      if (var1 >= 0 && var0.length - var1 >= var4 / 4 && var3 >= 0 && var2.length - var3 >= var4) {
         if (littleEndianUnaligned) {
            var3 += byteArrayOfs;

            for(var4 += var3; var3 < var4; var3 += 4) {
               unsafe.putInt(var2, (long)var3, Integer.reverseBytes(var0[var1++]));
            }
         } else {
            int var5;
            if (bigEndian && (var3 & 3) == 0) {
               var3 += byteArrayOfs;

               for(var4 += var3; var3 < var4; var3 += 4) {
                  unsafe.putInt(var2, (long)var3, var0[var1++]);
               }
            } else {
               for(var4 += var3; var3 < var4; var2[var3++] = (byte)var5) {
                  var5 = var0[var1++];
                  var2[var3++] = (byte)(var5 >> 24);
                  var2[var3++] = (byte)(var5 >> 16);
                  var2[var3++] = (byte)(var5 >> 8);
               }
            }
         }

      } else {
         throw new ArrayIndexOutOfBoundsException();
      }
   }

   static void i2bBig4(int var0, byte[] var1, int var2) {
      if (var2 >= 0 && var1.length - var2 >= 4) {
         if (littleEndianUnaligned) {
            unsafe.putInt(var1, (long)(byteArrayOfs + var2), Integer.reverseBytes(var0));
         } else if (bigEndian && (var2 & 3) == 0) {
            unsafe.putInt(var1, (long)(byteArrayOfs + var2), var0);
         } else {
            var1[var2] = (byte)(var0 >> 24);
            var1[var2 + 1] = (byte)(var0 >> 16);
            var1[var2 + 2] = (byte)(var0 >> 8);
            var1[var2 + 3] = (byte)var0;
         }

      } else {
         throw new ArrayIndexOutOfBoundsException();
      }
   }

   static void b2lBig(byte[] var0, int var1, long[] var2, int var3, int var4) {
      if (var1 >= 0 && var0.length - var1 >= var4 && var3 >= 0 && var2.length - var3 >= var4 / 8) {
         if (littleEndianUnaligned) {
            var1 += byteArrayOfs;

            for(var4 += var1; var1 < var4; var1 += 8) {
               var2[var3++] = Long.reverseBytes(unsafe.getLong(var0, (long)var1));
            }
         } else if (bigEndian && (var1 & 3) == 0) {
            var1 += byteArrayOfs;

            for(var4 += var1; var1 < var4; var1 += 8) {
               var2[var3++] = (long)unsafe.getInt(var0, (long)var1) << 32 | (long)unsafe.getInt(var0, (long)(var1 + 4)) & 4294967295L;
            }
         } else {
            for(var4 += var1; var1 < var4; var1 += 4) {
               int var5 = var0[var1 + 3] & 255 | (var0[var1 + 2] & 255) << 8 | (var0[var1 + 1] & 255) << 16 | var0[var1] << 24;
               var1 += 4;
               int var6 = var0[var1 + 3] & 255 | (var0[var1 + 2] & 255) << 8 | (var0[var1 + 1] & 255) << 16 | var0[var1] << 24;
               var2[var3++] = (long)var5 << 32 | (long)var6 & 4294967295L;
            }
         }

      } else {
         throw new ArrayIndexOutOfBoundsException();
      }
   }

   static void b2lBig128(byte[] var0, int var1, long[] var2) {
      if (var1 >= 0 && var0.length - var1 >= 128 && var2.length >= 16) {
         if (littleEndianUnaligned) {
            var1 += byteArrayOfs;
            var2[0] = Long.reverseBytes(unsafe.getLong(var0, (long)var1));
            var2[1] = Long.reverseBytes(unsafe.getLong(var0, (long)(var1 + 8)));
            var2[2] = Long.reverseBytes(unsafe.getLong(var0, (long)(var1 + 16)));
            var2[3] = Long.reverseBytes(unsafe.getLong(var0, (long)(var1 + 24)));
            var2[4] = Long.reverseBytes(unsafe.getLong(var0, (long)(var1 + 32)));
            var2[5] = Long.reverseBytes(unsafe.getLong(var0, (long)(var1 + 40)));
            var2[6] = Long.reverseBytes(unsafe.getLong(var0, (long)(var1 + 48)));
            var2[7] = Long.reverseBytes(unsafe.getLong(var0, (long)(var1 + 56)));
            var2[8] = Long.reverseBytes(unsafe.getLong(var0, (long)(var1 + 64)));
            var2[9] = Long.reverseBytes(unsafe.getLong(var0, (long)(var1 + 72)));
            var2[10] = Long.reverseBytes(unsafe.getLong(var0, (long)(var1 + 80)));
            var2[11] = Long.reverseBytes(unsafe.getLong(var0, (long)(var1 + 88)));
            var2[12] = Long.reverseBytes(unsafe.getLong(var0, (long)(var1 + 96)));
            var2[13] = Long.reverseBytes(unsafe.getLong(var0, (long)(var1 + 104)));
            var2[14] = Long.reverseBytes(unsafe.getLong(var0, (long)(var1 + 112)));
            var2[15] = Long.reverseBytes(unsafe.getLong(var0, (long)(var1 + 120)));
         } else {
            b2lBig(var0, var1, var2, 0, 128);
         }

      } else {
         throw new ArrayIndexOutOfBoundsException();
      }
   }

   static void l2bBig(long[] var0, int var1, byte[] var2, int var3, int var4) {
      if (var1 >= 0 && var0.length - var1 >= var4 / 8 && var3 >= 0 && var2.length - var3 >= var4) {
         long var5;
         for(var4 += var3; var3 < var4; var2[var3++] = (byte)((int)var5)) {
            var5 = var0[var1++];
            var2[var3++] = (byte)((int)(var5 >> 56));
            var2[var3++] = (byte)((int)(var5 >> 48));
            var2[var3++] = (byte)((int)(var5 >> 40));
            var2[var3++] = (byte)((int)(var5 >> 32));
            var2[var3++] = (byte)((int)(var5 >> 24));
            var2[var3++] = (byte)((int)(var5 >> 16));
            var2[var3++] = (byte)((int)(var5 >> 8));
         }

      } else {
         throw new ArrayIndexOutOfBoundsException();
      }
   }

   static {
      byteArrayOfs = unsafe.arrayBaseOffset(byte[].class);
      boolean var0 = unsafe.arrayIndexScale(byte[].class) == 1 && unsafe.arrayIndexScale(int[].class) == 4 && unsafe.arrayIndexScale(long[].class) == 8 && (byteArrayOfs & 3) == 0;
      ByteOrder var1 = ByteOrder.nativeOrder();
      littleEndianUnaligned = var0 && unaligned() && var1 == ByteOrder.LITTLE_ENDIAN;
      bigEndian = var0 && var1 == ByteOrder.BIG_ENDIAN;
   }
}
