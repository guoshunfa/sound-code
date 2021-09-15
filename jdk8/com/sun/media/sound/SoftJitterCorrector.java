package com.sun.media.sound;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;

public final class SoftJitterCorrector extends AudioInputStream {
   public SoftJitterCorrector(AudioInputStream var1, int var2, int var3) {
      super(new SoftJitterCorrector.JitterStream(var1, var2, var3), var1.getFormat(), var1.getFrameLength());
   }

   private static class JitterStream extends InputStream {
      static int MAX_BUFFER_SIZE = 1048576;
      boolean active = true;
      Thread thread;
      AudioInputStream stream;
      int writepos = 0;
      int readpos = 0;
      byte[][] buffers;
      private final Object buffers_mutex = new Object();
      int w_count = 1000;
      int w_min_tol = 2;
      int w_max_tol = 10;
      int w = 0;
      int w_min = -1;
      int bbuffer_pos = 0;
      int bbuffer_max = 0;
      byte[] bbuffer = null;

      public byte[] nextReadBuffer() {
         int var2;
         synchronized(this.buffers_mutex) {
            if (this.writepos > this.readpos) {
               var2 = this.writepos - this.readpos;
               if (var2 < this.w_min) {
                  this.w_min = var2;
               }

               int var3 = this.readpos++;
               return this.buffers[var3 % this.buffers.length];
            }

            this.w_min = -1;
            this.w = this.w_count - 1;
         }

         while(true) {
            try {
               Thread.sleep(1L);
            } catch (InterruptedException var7) {
               return null;
            }

            synchronized(this.buffers_mutex) {
               if (this.writepos > this.readpos) {
                  this.w = 0;
                  this.w_min = -1;
                  this.w = this.w_count - 1;
                  var2 = this.readpos++;
                  return this.buffers[var2 % this.buffers.length];
               }
            }
         }
      }

      public byte[] nextWriteBuffer() {
         synchronized(this.buffers_mutex) {
            return this.buffers[this.writepos % this.buffers.length];
         }
      }

      public void commit() {
         synchronized(this.buffers_mutex) {
            ++this.writepos;
            if (this.writepos - this.readpos > this.buffers.length) {
               int var2 = this.writepos - this.readpos + 10;
               var2 = Math.max(this.buffers.length * 2, var2);
               this.buffers = new byte[var2][this.buffers[0].length];
            }

         }
      }

      JitterStream(AudioInputStream var1, int var2, int var3) {
         this.w_count = 10 * (var2 / var3);
         if (this.w_count < 100) {
            this.w_count = 100;
         }

         this.buffers = new byte[var2 / var3 + 10][var3];
         this.bbuffer_max = MAX_BUFFER_SIZE / var3;
         this.stream = var1;
         Runnable var4 = new Runnable() {
            public void run() {
               AudioFormat var1 = JitterStream.this.stream.getFormat();
               int var2 = JitterStream.this.buffers[0].length;
               int var3 = var2 / var1.getFrameSize();
               long var4 = (long)((double)var3 * 1.0E9D / (double)var1.getSampleRate());
               long var6 = System.nanoTime();
               long var8 = var6 + var4;
               int var10 = 0;

               while(true) {
                  synchronized(JitterStream.this) {
                     if (!JitterStream.this.active) {
                        return;
                     }
                  }

                  int var11;
                  synchronized(JitterStream.this.buffers) {
                     var11 = JitterStream.this.writepos - JitterStream.this.readpos;
                     if (var10 == 0) {
                        ++JitterStream.this.w;
                        if (JitterStream.this.w_min != Integer.MAX_VALUE && JitterStream.this.w == JitterStream.this.w_count) {
                           var10 = 0;
                           if (JitterStream.this.w_min < JitterStream.this.w_min_tol) {
                              var10 = (JitterStream.this.w_min_tol + JitterStream.this.w_max_tol) / 2 - JitterStream.this.w_min;
                           }

                           if (JitterStream.this.w_min > JitterStream.this.w_max_tol) {
                              var10 = (JitterStream.this.w_min_tol + JitterStream.this.w_max_tol) / 2 - JitterStream.this.w_min;
                           }

                           JitterStream.this.w = 0;
                           JitterStream.this.w_min = Integer.MAX_VALUE;
                        }
                     }
                  }

                  while(var11 > JitterStream.this.bbuffer_max) {
                     synchronized(JitterStream.this.buffers) {
                        var11 = JitterStream.this.writepos - JitterStream.this.readpos;
                     }

                     synchronized(JitterStream.this) {
                        if (!JitterStream.this.active) {
                           break;
                        }
                     }

                     try {
                        Thread.sleep(1L);
                     } catch (InterruptedException var17) {
                     }
                  }

                  if (var10 < 0) {
                     ++var10;
                  } else {
                     byte[] var12 = JitterStream.this.nextWriteBuffer();

                     int var14;
                     try {
                        for(int var13 = 0; var13 != var12.length; var13 += var14) {
                           var14 = JitterStream.this.stream.read(var12, var13, var12.length - var13);
                           if (var14 < 0) {
                              throw new EOFException();
                           }

                           if (var14 == 0) {
                              Thread.yield();
                           }
                        }
                     } catch (IOException var20) {
                     }

                     JitterStream.this.commit();
                  }

                  if (var10 > 0) {
                     --var10;
                     var8 = System.nanoTime() + var4;
                  } else {
                     long var23 = var8 - System.nanoTime();
                     if (var23 > 0L) {
                        try {
                           Thread.sleep(var23 / 1000000L);
                        } catch (InterruptedException var16) {
                        }
                     }

                     var8 += var4;
                  }
               }
            }
         };
         this.thread = new Thread(var4);
         this.thread.setDaemon(true);
         this.thread.setPriority(10);
         this.thread.start();
      }

      public void close() throws IOException {
         synchronized(this) {
            this.active = false;
         }

         try {
            this.thread.join();
         } catch (InterruptedException var3) {
         }

         this.stream.close();
      }

      public int read() throws IOException {
         byte[] var1 = new byte[1];
         return this.read(var1) == -1 ? -1 : var1[0] & 255;
      }

      public void fillBuffer() {
         this.bbuffer = this.nextReadBuffer();
         this.bbuffer_pos = 0;
      }

      public int read(byte[] var1, int var2, int var3) {
         if (this.bbuffer == null) {
            this.fillBuffer();
         }

         int var4 = this.bbuffer.length;
         int var5 = var2 + var3;

         while(true) {
            while(var2 < var5) {
               if (this.available() == 0) {
                  this.fillBuffer();
               } else {
                  byte[] var6 = this.bbuffer;

                  int var7;
                  for(var7 = this.bbuffer_pos; var2 < var5 && var7 < var4; var1[var2++] = var6[var7++]) {
                  }

                  this.bbuffer_pos = var7;
               }
            }

            return var3;
         }
      }

      public int available() {
         return this.bbuffer.length - this.bbuffer_pos;
      }
   }
}
