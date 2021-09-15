package com.sun.media.sound;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import javax.sound.sampled.AudioFormat;

public abstract class AudioFloatConverter {
   private AudioFormat format;

   public static AudioFloatConverter getConverter(AudioFormat var0) {
      Object var1 = null;
      if (var0.getFrameSize() == 0) {
         return null;
      } else if (var0.getFrameSize() != (var0.getSampleSizeInBits() + 7) / 8 * var0.getChannels()) {
         return null;
      } else {
         if (var0.getEncoding().equals(AudioFormat.Encoding.PCM_SIGNED)) {
            if (var0.isBigEndian()) {
               if (var0.getSampleSizeInBits() <= 8) {
                  var1 = new AudioFloatConverter.AudioFloatConversion8S();
               } else if (var0.getSampleSizeInBits() > 8 && var0.getSampleSizeInBits() <= 16) {
                  var1 = new AudioFloatConverter.AudioFloatConversion16SB();
               } else if (var0.getSampleSizeInBits() > 16 && var0.getSampleSizeInBits() <= 24) {
                  var1 = new AudioFloatConverter.AudioFloatConversion24SB();
               } else if (var0.getSampleSizeInBits() > 24 && var0.getSampleSizeInBits() <= 32) {
                  var1 = new AudioFloatConverter.AudioFloatConversion32SB();
               } else if (var0.getSampleSizeInBits() > 32) {
                  var1 = new AudioFloatConverter.AudioFloatConversion32xSB((var0.getSampleSizeInBits() + 7) / 8 - 4);
               }
            } else if (var0.getSampleSizeInBits() <= 8) {
               var1 = new AudioFloatConverter.AudioFloatConversion8S();
            } else if (var0.getSampleSizeInBits() > 8 && var0.getSampleSizeInBits() <= 16) {
               var1 = new AudioFloatConverter.AudioFloatConversion16SL();
            } else if (var0.getSampleSizeInBits() > 16 && var0.getSampleSizeInBits() <= 24) {
               var1 = new AudioFloatConverter.AudioFloatConversion24SL();
            } else if (var0.getSampleSizeInBits() > 24 && var0.getSampleSizeInBits() <= 32) {
               var1 = new AudioFloatConverter.AudioFloatConversion32SL();
            } else if (var0.getSampleSizeInBits() > 32) {
               var1 = new AudioFloatConverter.AudioFloatConversion32xSL((var0.getSampleSizeInBits() + 7) / 8 - 4);
            }
         } else if (var0.getEncoding().equals(AudioFormat.Encoding.PCM_UNSIGNED)) {
            if (var0.isBigEndian()) {
               if (var0.getSampleSizeInBits() <= 8) {
                  var1 = new AudioFloatConverter.AudioFloatConversion8U();
               } else if (var0.getSampleSizeInBits() > 8 && var0.getSampleSizeInBits() <= 16) {
                  var1 = new AudioFloatConverter.AudioFloatConversion16UB();
               } else if (var0.getSampleSizeInBits() > 16 && var0.getSampleSizeInBits() <= 24) {
                  var1 = new AudioFloatConverter.AudioFloatConversion24UB();
               } else if (var0.getSampleSizeInBits() > 24 && var0.getSampleSizeInBits() <= 32) {
                  var1 = new AudioFloatConverter.AudioFloatConversion32UB();
               } else if (var0.getSampleSizeInBits() > 32) {
                  var1 = new AudioFloatConverter.AudioFloatConversion32xUB((var0.getSampleSizeInBits() + 7) / 8 - 4);
               }
            } else if (var0.getSampleSizeInBits() <= 8) {
               var1 = new AudioFloatConverter.AudioFloatConversion8U();
            } else if (var0.getSampleSizeInBits() > 8 && var0.getSampleSizeInBits() <= 16) {
               var1 = new AudioFloatConverter.AudioFloatConversion16UL();
            } else if (var0.getSampleSizeInBits() > 16 && var0.getSampleSizeInBits() <= 24) {
               var1 = new AudioFloatConverter.AudioFloatConversion24UL();
            } else if (var0.getSampleSizeInBits() > 24 && var0.getSampleSizeInBits() <= 32) {
               var1 = new AudioFloatConverter.AudioFloatConversion32UL();
            } else if (var0.getSampleSizeInBits() > 32) {
               var1 = new AudioFloatConverter.AudioFloatConversion32xUL((var0.getSampleSizeInBits() + 7) / 8 - 4);
            }
         } else if (var0.getEncoding().equals(AudioFormat.Encoding.PCM_FLOAT)) {
            if (var0.getSampleSizeInBits() == 32) {
               if (var0.isBigEndian()) {
                  var1 = new AudioFloatConverter.AudioFloatConversion32B();
               } else {
                  var1 = new AudioFloatConverter.AudioFloatConversion32L();
               }
            } else if (var0.getSampleSizeInBits() == 64) {
               if (var0.isBigEndian()) {
                  var1 = new AudioFloatConverter.AudioFloatConversion64B();
               } else {
                  var1 = new AudioFloatConverter.AudioFloatConversion64L();
               }
            }
         }

         if ((var0.getEncoding().equals(AudioFormat.Encoding.PCM_SIGNED) || var0.getEncoding().equals(AudioFormat.Encoding.PCM_UNSIGNED)) && var0.getSampleSizeInBits() % 8 != 0) {
            var1 = new AudioFloatConverter.AudioFloatLSBFilter((AudioFloatConverter)var1, var0);
         }

         if (var1 != null) {
            ((AudioFloatConverter)var1).format = var0;
         }

         return (AudioFloatConverter)var1;
      }
   }

   public final AudioFormat getFormat() {
      return this.format;
   }

   public abstract float[] toFloatArray(byte[] var1, int var2, float[] var3, int var4, int var5);

   public final float[] toFloatArray(byte[] var1, float[] var2, int var3, int var4) {
      return this.toFloatArray(var1, 0, var2, var3, var4);
   }

   public final float[] toFloatArray(byte[] var1, int var2, float[] var3, int var4) {
      return this.toFloatArray(var1, var2, var3, 0, var4);
   }

   public final float[] toFloatArray(byte[] var1, float[] var2, int var3) {
      return this.toFloatArray(var1, 0, var2, 0, var3);
   }

   public final float[] toFloatArray(byte[] var1, float[] var2) {
      return this.toFloatArray(var1, 0, var2, 0, var2.length);
   }

   public abstract byte[] toByteArray(float[] var1, int var2, int var3, byte[] var4, int var5);

   public final byte[] toByteArray(float[] var1, int var2, byte[] var3, int var4) {
      return this.toByteArray(var1, 0, var2, var3, var4);
   }

   public final byte[] toByteArray(float[] var1, int var2, int var3, byte[] var4) {
      return this.toByteArray(var1, var2, var3, var4, 0);
   }

   public final byte[] toByteArray(float[] var1, int var2, byte[] var3) {
      return this.toByteArray(var1, 0, var2, var3, 0);
   }

   public final byte[] toByteArray(float[] var1, byte[] var2) {
      return this.toByteArray(var1, 0, var1.length, var2, 0);
   }

   private static class AudioFloatConversion32xUB extends AudioFloatConverter {
      final int xbytes;

      AudioFloatConversion32xUB(int var1) {
         this.xbytes = var1;
      }

      public float[] toFloatArray(byte[] var1, int var2, float[] var3, int var4, int var5) {
         int var6 = var2;
         int var7 = var4;

         for(int var8 = 0; var8 < var5; ++var8) {
            int var9 = (var1[var6++] & 255) << 24 | (var1[var6++] & 255) << 16 | (var1[var6++] & 255) << 8 | var1[var6++] & 255;
            var6 += this.xbytes;
            var9 -= Integer.MAX_VALUE;
            var3[var7++] = (float)var9 * 4.656613E-10F;
         }

         return var3;
      }

      public byte[] toByteArray(float[] var1, int var2, int var3, byte[] var4, int var5) {
         int var6 = var2;
         int var7 = var5;

         for(int var8 = 0; var8 < var3; ++var8) {
            int var9 = (int)((double)var1[var6++] * 2.147483647E9D);
            var9 += Integer.MAX_VALUE;
            var4[var7++] = (byte)(var9 >>> 24);
            var4[var7++] = (byte)(var9 >>> 16);
            var4[var7++] = (byte)(var9 >>> 8);
            var4[var7++] = (byte)var9;

            for(int var10 = 0; var10 < this.xbytes; ++var10) {
               var4[var7++] = 0;
            }
         }

         return var4;
      }
   }

   private static class AudioFloatConversion32xUL extends AudioFloatConverter {
      final int xbytes;

      AudioFloatConversion32xUL(int var1) {
         this.xbytes = var1;
      }

      public float[] toFloatArray(byte[] var1, int var2, float[] var3, int var4, int var5) {
         int var6 = var2;
         int var7 = var4;

         for(int var8 = 0; var8 < var5; ++var8) {
            var6 += this.xbytes;
            int var9 = var1[var6++] & 255 | (var1[var6++] & 255) << 8 | (var1[var6++] & 255) << 16 | (var1[var6++] & 255) << 24;
            var9 -= Integer.MAX_VALUE;
            var3[var7++] = (float)var9 * 4.656613E-10F;
         }

         return var3;
      }

      public byte[] toByteArray(float[] var1, int var2, int var3, byte[] var4, int var5) {
         int var6 = var2;
         int var7 = var5;

         for(int var8 = 0; var8 < var3; ++var8) {
            int var9 = (int)(var1[var6++] * 2.14748365E9F);
            var9 += Integer.MAX_VALUE;

            for(int var10 = 0; var10 < this.xbytes; ++var10) {
               var4[var7++] = 0;
            }

            var4[var7++] = (byte)var9;
            var4[var7++] = (byte)(var9 >>> 8);
            var4[var7++] = (byte)(var9 >>> 16);
            var4[var7++] = (byte)(var9 >>> 24);
         }

         return var4;
      }
   }

   private static class AudioFloatConversion32xSB extends AudioFloatConverter {
      final int xbytes;

      AudioFloatConversion32xSB(int var1) {
         this.xbytes = var1;
      }

      public float[] toFloatArray(byte[] var1, int var2, float[] var3, int var4, int var5) {
         int var6 = var2;
         int var7 = var4;

         for(int var8 = 0; var8 < var5; ++var8) {
            int var9 = (var1[var6++] & 255) << 24 | (var1[var6++] & 255) << 16 | (var1[var6++] & 255) << 8 | var1[var6++] & 255;
            var6 += this.xbytes;
            var3[var7++] = (float)var9 * 4.656613E-10F;
         }

         return var3;
      }

      public byte[] toByteArray(float[] var1, int var2, int var3, byte[] var4, int var5) {
         int var6 = var2;
         int var7 = var5;

         for(int var8 = 0; var8 < var3; ++var8) {
            int var9 = (int)(var1[var6++] * 2.14748365E9F);
            var4[var7++] = (byte)(var9 >>> 24);
            var4[var7++] = (byte)(var9 >>> 16);
            var4[var7++] = (byte)(var9 >>> 8);
            var4[var7++] = (byte)var9;

            for(int var10 = 0; var10 < this.xbytes; ++var10) {
               var4[var7++] = 0;
            }
         }

         return var4;
      }
   }

   private static class AudioFloatConversion32xSL extends AudioFloatConverter {
      final int xbytes;

      AudioFloatConversion32xSL(int var1) {
         this.xbytes = var1;
      }

      public float[] toFloatArray(byte[] var1, int var2, float[] var3, int var4, int var5) {
         int var6 = var2;
         int var7 = var4;

         for(int var8 = 0; var8 < var5; ++var8) {
            var6 += this.xbytes;
            int var9 = var1[var6++] & 255 | (var1[var6++] & 255) << 8 | (var1[var6++] & 255) << 16 | (var1[var6++] & 255) << 24;
            var3[var7++] = (float)var9 * 4.656613E-10F;
         }

         return var3;
      }

      public byte[] toByteArray(float[] var1, int var2, int var3, byte[] var4, int var5) {
         int var6 = var2;
         int var7 = var5;

         for(int var8 = 0; var8 < var3; ++var8) {
            int var9 = (int)(var1[var6++] * 2.14748365E9F);

            for(int var10 = 0; var10 < this.xbytes; ++var10) {
               var4[var7++] = 0;
            }

            var4[var7++] = (byte)var9;
            var4[var7++] = (byte)(var9 >>> 8);
            var4[var7++] = (byte)(var9 >>> 16);
            var4[var7++] = (byte)(var9 >>> 24);
         }

         return var4;
      }
   }

   private static class AudioFloatConversion32UB extends AudioFloatConverter {
      private AudioFloatConversion32UB() {
      }

      public float[] toFloatArray(byte[] var1, int var2, float[] var3, int var4, int var5) {
         int var6 = var2;
         int var7 = var4;

         for(int var8 = 0; var8 < var5; ++var8) {
            int var9 = (var1[var6++] & 255) << 24 | (var1[var6++] & 255) << 16 | (var1[var6++] & 255) << 8 | var1[var6++] & 255;
            var9 -= Integer.MAX_VALUE;
            var3[var7++] = (float)var9 * 4.656613E-10F;
         }

         return var3;
      }

      public byte[] toByteArray(float[] var1, int var2, int var3, byte[] var4, int var5) {
         int var6 = var2;
         int var7 = var5;

         for(int var8 = 0; var8 < var3; ++var8) {
            int var9 = (int)(var1[var6++] * 2.14748365E9F);
            var9 += Integer.MAX_VALUE;
            var4[var7++] = (byte)(var9 >>> 24);
            var4[var7++] = (byte)(var9 >>> 16);
            var4[var7++] = (byte)(var9 >>> 8);
            var4[var7++] = (byte)var9;
         }

         return var4;
      }

      // $FF: synthetic method
      AudioFloatConversion32UB(Object var1) {
         this();
      }
   }

   private static class AudioFloatConversion32UL extends AudioFloatConverter {
      private AudioFloatConversion32UL() {
      }

      public float[] toFloatArray(byte[] var1, int var2, float[] var3, int var4, int var5) {
         int var6 = var2;
         int var7 = var4;

         for(int var8 = 0; var8 < var5; ++var8) {
            int var9 = var1[var6++] & 255 | (var1[var6++] & 255) << 8 | (var1[var6++] & 255) << 16 | (var1[var6++] & 255) << 24;
            var9 -= Integer.MAX_VALUE;
            var3[var7++] = (float)var9 * 4.656613E-10F;
         }

         return var3;
      }

      public byte[] toByteArray(float[] var1, int var2, int var3, byte[] var4, int var5) {
         int var6 = var2;
         int var7 = var5;

         for(int var8 = 0; var8 < var3; ++var8) {
            int var9 = (int)(var1[var6++] * 2.14748365E9F);
            var9 += Integer.MAX_VALUE;
            var4[var7++] = (byte)var9;
            var4[var7++] = (byte)(var9 >>> 8);
            var4[var7++] = (byte)(var9 >>> 16);
            var4[var7++] = (byte)(var9 >>> 24);
         }

         return var4;
      }

      // $FF: synthetic method
      AudioFloatConversion32UL(Object var1) {
         this();
      }
   }

   private static class AudioFloatConversion32SB extends AudioFloatConverter {
      private AudioFloatConversion32SB() {
      }

      public float[] toFloatArray(byte[] var1, int var2, float[] var3, int var4, int var5) {
         int var6 = var2;
         int var7 = var4;

         for(int var8 = 0; var8 < var5; ++var8) {
            int var9 = (var1[var6++] & 255) << 24 | (var1[var6++] & 255) << 16 | (var1[var6++] & 255) << 8 | var1[var6++] & 255;
            var3[var7++] = (float)var9 * 4.656613E-10F;
         }

         return var3;
      }

      public byte[] toByteArray(float[] var1, int var2, int var3, byte[] var4, int var5) {
         int var6 = var2;
         int var7 = var5;

         for(int var8 = 0; var8 < var3; ++var8) {
            int var9 = (int)(var1[var6++] * 2.14748365E9F);
            var4[var7++] = (byte)(var9 >>> 24);
            var4[var7++] = (byte)(var9 >>> 16);
            var4[var7++] = (byte)(var9 >>> 8);
            var4[var7++] = (byte)var9;
         }

         return var4;
      }

      // $FF: synthetic method
      AudioFloatConversion32SB(Object var1) {
         this();
      }
   }

   private static class AudioFloatConversion32SL extends AudioFloatConverter {
      private AudioFloatConversion32SL() {
      }

      public float[] toFloatArray(byte[] var1, int var2, float[] var3, int var4, int var5) {
         int var6 = var2;
         int var7 = var4;

         for(int var8 = 0; var8 < var5; ++var8) {
            int var9 = var1[var6++] & 255 | (var1[var6++] & 255) << 8 | (var1[var6++] & 255) << 16 | (var1[var6++] & 255) << 24;
            var3[var7++] = (float)var9 * 4.656613E-10F;
         }

         return var3;
      }

      public byte[] toByteArray(float[] var1, int var2, int var3, byte[] var4, int var5) {
         int var6 = var2;
         int var7 = var5;

         for(int var8 = 0; var8 < var3; ++var8) {
            int var9 = (int)(var1[var6++] * 2.14748365E9F);
            var4[var7++] = (byte)var9;
            var4[var7++] = (byte)(var9 >>> 8);
            var4[var7++] = (byte)(var9 >>> 16);
            var4[var7++] = (byte)(var9 >>> 24);
         }

         return var4;
      }

      // $FF: synthetic method
      AudioFloatConversion32SL(Object var1) {
         this();
      }
   }

   private static class AudioFloatConversion24UB extends AudioFloatConverter {
      private AudioFloatConversion24UB() {
      }

      public float[] toFloatArray(byte[] var1, int var2, float[] var3, int var4, int var5) {
         int var6 = var2;
         int var7 = var4;

         for(int var8 = 0; var8 < var5; ++var8) {
            int var9 = (var1[var6++] & 255) << 16 | (var1[var6++] & 255) << 8 | var1[var6++] & 255;
            var9 -= 8388607;
            var3[var7++] = (float)var9 * 1.192093E-7F;
         }

         return var3;
      }

      public byte[] toByteArray(float[] var1, int var2, int var3, byte[] var4, int var5) {
         int var6 = var2;
         int var7 = var5;

         for(int var8 = 0; var8 < var3; ++var8) {
            int var9 = (int)(var1[var6++] * 8388607.0F);
            var9 += 8388607;
            var4[var7++] = (byte)(var9 >>> 16);
            var4[var7++] = (byte)(var9 >>> 8);
            var4[var7++] = (byte)var9;
         }

         return var4;
      }

      // $FF: synthetic method
      AudioFloatConversion24UB(Object var1) {
         this();
      }
   }

   private static class AudioFloatConversion24UL extends AudioFloatConverter {
      private AudioFloatConversion24UL() {
      }

      public float[] toFloatArray(byte[] var1, int var2, float[] var3, int var4, int var5) {
         int var6 = var2;
         int var7 = var4;

         for(int var8 = 0; var8 < var5; ++var8) {
            int var9 = var1[var6++] & 255 | (var1[var6++] & 255) << 8 | (var1[var6++] & 255) << 16;
            var9 -= 8388607;
            var3[var7++] = (float)var9 * 1.192093E-7F;
         }

         return var3;
      }

      public byte[] toByteArray(float[] var1, int var2, int var3, byte[] var4, int var5) {
         int var6 = var2;
         int var7 = var5;

         for(int var8 = 0; var8 < var3; ++var8) {
            int var9 = (int)(var1[var6++] * 8388607.0F);
            var9 += 8388607;
            var4[var7++] = (byte)var9;
            var4[var7++] = (byte)(var9 >>> 8);
            var4[var7++] = (byte)(var9 >>> 16);
         }

         return var4;
      }

      // $FF: synthetic method
      AudioFloatConversion24UL(Object var1) {
         this();
      }
   }

   private static class AudioFloatConversion24SB extends AudioFloatConverter {
      private AudioFloatConversion24SB() {
      }

      public float[] toFloatArray(byte[] var1, int var2, float[] var3, int var4, int var5) {
         int var6 = var2;
         int var7 = var4;

         for(int var8 = 0; var8 < var5; ++var8) {
            int var9 = (var1[var6++] & 255) << 16 | (var1[var6++] & 255) << 8 | var1[var6++] & 255;
            if (var9 > 8388607) {
               var9 -= 16777216;
            }

            var3[var7++] = (float)var9 * 1.192093E-7F;
         }

         return var3;
      }

      public byte[] toByteArray(float[] var1, int var2, int var3, byte[] var4, int var5) {
         int var6 = var2;
         int var7 = var5;

         for(int var8 = 0; var8 < var3; ++var8) {
            int var9 = (int)(var1[var6++] * 8388607.0F);
            if (var9 < 0) {
               var9 += 16777216;
            }

            var4[var7++] = (byte)(var9 >>> 16);
            var4[var7++] = (byte)(var9 >>> 8);
            var4[var7++] = (byte)var9;
         }

         return var4;
      }

      // $FF: synthetic method
      AudioFloatConversion24SB(Object var1) {
         this();
      }
   }

   private static class AudioFloatConversion24SL extends AudioFloatConverter {
      private AudioFloatConversion24SL() {
      }

      public float[] toFloatArray(byte[] var1, int var2, float[] var3, int var4, int var5) {
         int var6 = var2;
         int var7 = var4;

         for(int var8 = 0; var8 < var5; ++var8) {
            int var9 = var1[var6++] & 255 | (var1[var6++] & 255) << 8 | (var1[var6++] & 255) << 16;
            if (var9 > 8388607) {
               var9 -= 16777216;
            }

            var3[var7++] = (float)var9 * 1.192093E-7F;
         }

         return var3;
      }

      public byte[] toByteArray(float[] var1, int var2, int var3, byte[] var4, int var5) {
         int var6 = var2;
         int var7 = var5;

         for(int var8 = 0; var8 < var3; ++var8) {
            int var9 = (int)(var1[var6++] * 8388607.0F);
            if (var9 < 0) {
               var9 += 16777216;
            }

            var4[var7++] = (byte)var9;
            var4[var7++] = (byte)(var9 >>> 8);
            var4[var7++] = (byte)(var9 >>> 16);
         }

         return var4;
      }

      // $FF: synthetic method
      AudioFloatConversion24SL(Object var1) {
         this();
      }
   }

   private static class AudioFloatConversion16UB extends AudioFloatConverter {
      private AudioFloatConversion16UB() {
      }

      public float[] toFloatArray(byte[] var1, int var2, float[] var3, int var4, int var5) {
         int var6 = var2;
         int var7 = var4;

         for(int var8 = 0; var8 < var5; ++var8) {
            int var9 = (var1[var6++] & 255) << 8 | var1[var6++] & 255;
            var3[var7++] = (float)(var9 - 32767) * 3.051851E-5F;
         }

         return var3;
      }

      public byte[] toByteArray(float[] var1, int var2, int var3, byte[] var4, int var5) {
         int var6 = var2;
         int var7 = var5;

         for(int var8 = 0; var8 < var3; ++var8) {
            int var9 = 32767 + (int)((double)var1[var6++] * 32767.0D);
            var4[var7++] = (byte)(var9 >>> 8);
            var4[var7++] = (byte)var9;
         }

         return var4;
      }

      // $FF: synthetic method
      AudioFloatConversion16UB(Object var1) {
         this();
      }
   }

   private static class AudioFloatConversion16UL extends AudioFloatConverter {
      private AudioFloatConversion16UL() {
      }

      public float[] toFloatArray(byte[] var1, int var2, float[] var3, int var4, int var5) {
         int var6 = var2;
         int var7 = var4;

         for(int var8 = 0; var8 < var5; ++var8) {
            int var9 = var1[var6++] & 255 | (var1[var6++] & 255) << 8;
            var3[var7++] = (float)(var9 - 32767) * 3.051851E-5F;
         }

         return var3;
      }

      public byte[] toByteArray(float[] var1, int var2, int var3, byte[] var4, int var5) {
         int var6 = var2;
         int var7 = var5;

         for(int var8 = 0; var8 < var3; ++var8) {
            int var9 = 32767 + (int)((double)var1[var6++] * 32767.0D);
            var4[var7++] = (byte)var9;
            var4[var7++] = (byte)(var9 >>> 8);
         }

         return var4;
      }

      // $FF: synthetic method
      AudioFloatConversion16UL(Object var1) {
         this();
      }
   }

   private static class AudioFloatConversion16SB extends AudioFloatConverter {
      private AudioFloatConversion16SB() {
      }

      public float[] toFloatArray(byte[] var1, int var2, float[] var3, int var4, int var5) {
         int var6 = var2;
         int var7 = var4;

         for(int var8 = 0; var8 < var5; ++var8) {
            var3[var7++] = (float)((short)(var1[var6++] << 8 | var1[var6++] & 255)) * 3.051851E-5F;
         }

         return var3;
      }

      public byte[] toByteArray(float[] var1, int var2, int var3, byte[] var4, int var5) {
         int var6 = var2;
         int var7 = var5;

         for(int var8 = 0; var8 < var3; ++var8) {
            int var9 = (int)((double)var1[var6++] * 32767.0D);
            var4[var7++] = (byte)(var9 >>> 8);
            var4[var7++] = (byte)var9;
         }

         return var4;
      }

      // $FF: synthetic method
      AudioFloatConversion16SB(Object var1) {
         this();
      }
   }

   private static class AudioFloatConversion16SL extends AudioFloatConverter {
      private AudioFloatConversion16SL() {
      }

      public float[] toFloatArray(byte[] var1, int var2, float[] var3, int var4, int var5) {
         int var6 = var2;
         int var7 = var4 + var5;

         for(int var8 = var4; var8 < var7; ++var8) {
            var3[var8] = (float)((short)(var1[var6++] & 255 | var1[var6++] << 8)) * 3.051851E-5F;
         }

         return var3;
      }

      public byte[] toByteArray(float[] var1, int var2, int var3, byte[] var4, int var5) {
         int var6 = var5;
         int var7 = var2 + var3;

         for(int var8 = var2; var8 < var7; ++var8) {
            int var9 = (int)((double)var1[var8] * 32767.0D);
            var4[var6++] = (byte)var9;
            var4[var6++] = (byte)(var9 >>> 8);
         }

         return var4;
      }

      // $FF: synthetic method
      AudioFloatConversion16SL(Object var1) {
         this();
      }
   }

   private static class AudioFloatConversion8U extends AudioFloatConverter {
      private AudioFloatConversion8U() {
      }

      public float[] toFloatArray(byte[] var1, int var2, float[] var3, int var4, int var5) {
         int var6 = var2;
         int var7 = var4;

         for(int var8 = 0; var8 < var5; ++var8) {
            var3[var7++] = (float)((var1[var6++] & 255) - 127) * 0.007874016F;
         }

         return var3;
      }

      public byte[] toByteArray(float[] var1, int var2, int var3, byte[] var4, int var5) {
         int var6 = var2;
         int var7 = var5;

         for(int var8 = 0; var8 < var3; ++var8) {
            var4[var7++] = (byte)((int)(127.0F + var1[var6++] * 127.0F));
         }

         return var4;
      }

      // $FF: synthetic method
      AudioFloatConversion8U(Object var1) {
         this();
      }
   }

   private static class AudioFloatConversion8S extends AudioFloatConverter {
      private AudioFloatConversion8S() {
      }

      public float[] toFloatArray(byte[] var1, int var2, float[] var3, int var4, int var5) {
         int var6 = var2;
         int var7 = var4;

         for(int var8 = 0; var8 < var5; ++var8) {
            var3[var7++] = (float)var1[var6++] * 0.007874016F;
         }

         return var3;
      }

      public byte[] toByteArray(float[] var1, int var2, int var3, byte[] var4, int var5) {
         int var6 = var2;
         int var7 = var5;

         for(int var8 = 0; var8 < var3; ++var8) {
            var4[var7++] = (byte)((int)(var1[var6++] * 127.0F));
         }

         return var4;
      }

      // $FF: synthetic method
      AudioFloatConversion8S(Object var1) {
         this();
      }
   }

   private static class AudioFloatConversion32B extends AudioFloatConverter {
      ByteBuffer bytebuffer;
      FloatBuffer floatbuffer;

      private AudioFloatConversion32B() {
         this.bytebuffer = null;
         this.floatbuffer = null;
      }

      public float[] toFloatArray(byte[] var1, int var2, float[] var3, int var4, int var5) {
         int var6 = var5 * 4;
         if (this.bytebuffer == null || this.bytebuffer.capacity() < var6) {
            this.bytebuffer = ByteBuffer.allocate(var6).order(ByteOrder.BIG_ENDIAN);
            this.floatbuffer = this.bytebuffer.asFloatBuffer();
         }

         this.bytebuffer.position(0);
         this.floatbuffer.position(0);
         this.bytebuffer.put(var1, var2, var6);
         this.floatbuffer.get(var3, var4, var5);
         return var3;
      }

      public byte[] toByteArray(float[] var1, int var2, int var3, byte[] var4, int var5) {
         int var6 = var3 * 4;
         if (this.bytebuffer == null || this.bytebuffer.capacity() < var6) {
            this.bytebuffer = ByteBuffer.allocate(var6).order(ByteOrder.BIG_ENDIAN);
            this.floatbuffer = this.bytebuffer.asFloatBuffer();
         }

         this.floatbuffer.position(0);
         this.bytebuffer.position(0);
         this.floatbuffer.put(var1, var2, var3);
         this.bytebuffer.get(var4, var5, var6);
         return var4;
      }

      // $FF: synthetic method
      AudioFloatConversion32B(Object var1) {
         this();
      }
   }

   private static class AudioFloatConversion32L extends AudioFloatConverter {
      ByteBuffer bytebuffer;
      FloatBuffer floatbuffer;

      private AudioFloatConversion32L() {
         this.bytebuffer = null;
         this.floatbuffer = null;
      }

      public float[] toFloatArray(byte[] var1, int var2, float[] var3, int var4, int var5) {
         int var6 = var5 * 4;
         if (this.bytebuffer == null || this.bytebuffer.capacity() < var6) {
            this.bytebuffer = ByteBuffer.allocate(var6).order(ByteOrder.LITTLE_ENDIAN);
            this.floatbuffer = this.bytebuffer.asFloatBuffer();
         }

         this.bytebuffer.position(0);
         this.floatbuffer.position(0);
         this.bytebuffer.put(var1, var2, var6);
         this.floatbuffer.get(var3, var4, var5);
         return var3;
      }

      public byte[] toByteArray(float[] var1, int var2, int var3, byte[] var4, int var5) {
         int var6 = var3 * 4;
         if (this.bytebuffer == null || this.bytebuffer.capacity() < var6) {
            this.bytebuffer = ByteBuffer.allocate(var6).order(ByteOrder.LITTLE_ENDIAN);
            this.floatbuffer = this.bytebuffer.asFloatBuffer();
         }

         this.floatbuffer.position(0);
         this.bytebuffer.position(0);
         this.floatbuffer.put(var1, var2, var3);
         this.bytebuffer.get(var4, var5, var6);
         return var4;
      }

      // $FF: synthetic method
      AudioFloatConversion32L(Object var1) {
         this();
      }
   }

   private static class AudioFloatConversion64B extends AudioFloatConverter {
      ByteBuffer bytebuffer;
      DoubleBuffer floatbuffer;
      double[] double_buff;

      private AudioFloatConversion64B() {
         this.bytebuffer = null;
         this.floatbuffer = null;
         this.double_buff = null;
      }

      public float[] toFloatArray(byte[] var1, int var2, float[] var3, int var4, int var5) {
         int var6 = var5 * 8;
         if (this.bytebuffer == null || this.bytebuffer.capacity() < var6) {
            this.bytebuffer = ByteBuffer.allocate(var6).order(ByteOrder.BIG_ENDIAN);
            this.floatbuffer = this.bytebuffer.asDoubleBuffer();
         }

         this.bytebuffer.position(0);
         this.floatbuffer.position(0);
         this.bytebuffer.put(var1, var2, var6);
         if (this.double_buff == null || this.double_buff.length < var5 + var4) {
            this.double_buff = new double[var5 + var4];
         }

         this.floatbuffer.get(this.double_buff, var4, var5);
         int var7 = var4 + var5;

         for(int var8 = var4; var8 < var7; ++var8) {
            var3[var8] = (float)this.double_buff[var8];
         }

         return var3;
      }

      public byte[] toByteArray(float[] var1, int var2, int var3, byte[] var4, int var5) {
         int var6 = var3 * 8;
         if (this.bytebuffer == null || this.bytebuffer.capacity() < var6) {
            this.bytebuffer = ByteBuffer.allocate(var6).order(ByteOrder.BIG_ENDIAN);
            this.floatbuffer = this.bytebuffer.asDoubleBuffer();
         }

         this.floatbuffer.position(0);
         this.bytebuffer.position(0);
         if (this.double_buff == null || this.double_buff.length < var2 + var3) {
            this.double_buff = new double[var2 + var3];
         }

         int var7 = var2 + var3;

         for(int var8 = var2; var8 < var7; ++var8) {
            this.double_buff[var8] = (double)var1[var8];
         }

         this.floatbuffer.put(this.double_buff, var2, var3);
         this.bytebuffer.get(var4, var5, var6);
         return var4;
      }

      // $FF: synthetic method
      AudioFloatConversion64B(Object var1) {
         this();
      }
   }

   private static class AudioFloatConversion64L extends AudioFloatConverter {
      ByteBuffer bytebuffer;
      DoubleBuffer floatbuffer;
      double[] double_buff;

      private AudioFloatConversion64L() {
         this.bytebuffer = null;
         this.floatbuffer = null;
         this.double_buff = null;
      }

      public float[] toFloatArray(byte[] var1, int var2, float[] var3, int var4, int var5) {
         int var6 = var5 * 8;
         if (this.bytebuffer == null || this.bytebuffer.capacity() < var6) {
            this.bytebuffer = ByteBuffer.allocate(var6).order(ByteOrder.LITTLE_ENDIAN);
            this.floatbuffer = this.bytebuffer.asDoubleBuffer();
         }

         this.bytebuffer.position(0);
         this.floatbuffer.position(0);
         this.bytebuffer.put(var1, var2, var6);
         if (this.double_buff == null || this.double_buff.length < var5 + var4) {
            this.double_buff = new double[var5 + var4];
         }

         this.floatbuffer.get(this.double_buff, var4, var5);
         int var7 = var4 + var5;

         for(int var8 = var4; var8 < var7; ++var8) {
            var3[var8] = (float)this.double_buff[var8];
         }

         return var3;
      }

      public byte[] toByteArray(float[] var1, int var2, int var3, byte[] var4, int var5) {
         int var6 = var3 * 8;
         if (this.bytebuffer == null || this.bytebuffer.capacity() < var6) {
            this.bytebuffer = ByteBuffer.allocate(var6).order(ByteOrder.LITTLE_ENDIAN);
            this.floatbuffer = this.bytebuffer.asDoubleBuffer();
         }

         this.floatbuffer.position(0);
         this.bytebuffer.position(0);
         if (this.double_buff == null || this.double_buff.length < var2 + var3) {
            this.double_buff = new double[var2 + var3];
         }

         int var7 = var2 + var3;

         for(int var8 = var2; var8 < var7; ++var8) {
            this.double_buff[var8] = (double)var1[var8];
         }

         this.floatbuffer.put(this.double_buff, var2, var3);
         this.bytebuffer.get(var4, var5, var6);
         return var4;
      }

      // $FF: synthetic method
      AudioFloatConversion64L(Object var1) {
         this();
      }
   }

   private static class AudioFloatLSBFilter extends AudioFloatConverter {
      private final AudioFloatConverter converter;
      private final int offset;
      private final int stepsize;
      private final byte mask;
      private byte[] mask_buffer;

      AudioFloatLSBFilter(AudioFloatConverter var1, AudioFormat var2) {
         int var3 = var2.getSampleSizeInBits();
         boolean var4 = var2.isBigEndian();
         this.converter = var1;
         this.stepsize = (var3 + 7) / 8;
         this.offset = var4 ? this.stepsize - 1 : 0;
         int var5 = var3 % 8;
         if (var5 == 0) {
            this.mask = 0;
         } else if (var5 == 1) {
            this.mask = -128;
         } else if (var5 == 2) {
            this.mask = -64;
         } else if (var5 == 3) {
            this.mask = -32;
         } else if (var5 == 4) {
            this.mask = -16;
         } else if (var5 == 5) {
            this.mask = -8;
         } else if (var5 == 6) {
            this.mask = -4;
         } else if (var5 == 7) {
            this.mask = -2;
         } else {
            this.mask = -1;
         }

      }

      public byte[] toByteArray(float[] var1, int var2, int var3, byte[] var4, int var5) {
         byte[] var6 = this.converter.toByteArray(var1, var2, var3, var4, var5);
         int var7 = var3 * this.stepsize;

         for(int var8 = var5 + this.offset; var8 < var7; var8 += this.stepsize) {
            var4[var8] &= this.mask;
         }

         return var6;
      }

      public float[] toFloatArray(byte[] var1, int var2, float[] var3, int var4, int var5) {
         if (this.mask_buffer == null || this.mask_buffer.length < var1.length) {
            this.mask_buffer = new byte[var1.length];
         }

         System.arraycopy(var1, 0, this.mask_buffer, 0, var1.length);
         int var6 = var5 * this.stepsize;

         for(int var7 = var2 + this.offset; var7 < var6; var7 += this.stepsize) {
            this.mask_buffer[var7] &= this.mask;
         }

         float[] var8 = this.converter.toFloatArray(this.mask_buffer, var2, var3, var4, var5);
         return var8;
      }
   }
}
