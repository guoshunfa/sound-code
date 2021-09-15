package sun.awt.image;

import java.awt.image.ColorModel;
import java.awt.image.ImageConsumer;
import java.io.IOException;
import java.io.InputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Hashtable;

public abstract class ImageDecoder {
   InputStreamImageSource source;
   InputStream input;
   Thread feeder;
   protected boolean aborted;
   protected boolean finished;
   ImageConsumerQueue queue;
   ImageDecoder next;

   public ImageDecoder(InputStreamImageSource var1, InputStream var2) {
      this.source = var1;
      this.input = var2;
      this.feeder = Thread.currentThread();
   }

   public boolean isConsumer(ImageConsumer var1) {
      return ImageConsumerQueue.isConsumer(this.queue, var1);
   }

   public void removeConsumer(ImageConsumer var1) {
      this.queue = ImageConsumerQueue.removeConsumer(this.queue, var1, false);
      if (!this.finished && this.queue == null) {
         this.abort();
      }

   }

   protected ImageConsumerQueue nextConsumer(ImageConsumerQueue var1) {
      synchronized(this.source) {
         if (this.aborted) {
            return null;
         } else {
            for(var1 = var1 == null ? this.queue : var1.next; var1 != null; var1 = var1.next) {
               if (var1.interested) {
                  return var1;
               }
            }

            return null;
         }
      }
   }

   protected int setDimensions(int var1, int var2) {
      ImageConsumerQueue var3 = null;

      int var4;
      for(var4 = 0; (var3 = this.nextConsumer(var3)) != null; ++var4) {
         var3.consumer.setDimensions(var1, var2);
      }

      return var4;
   }

   protected int setProperties(Hashtable var1) {
      ImageConsumerQueue var2 = null;

      int var3;
      for(var3 = 0; (var2 = this.nextConsumer(var2)) != null; ++var3) {
         var2.consumer.setProperties(var1);
      }

      return var3;
   }

   protected int setColorModel(ColorModel var1) {
      ImageConsumerQueue var2 = null;

      int var3;
      for(var3 = 0; (var2 = this.nextConsumer(var2)) != null; ++var3) {
         var2.consumer.setColorModel(var1);
      }

      return var3;
   }

   protected int setHints(int var1) {
      ImageConsumerQueue var2 = null;

      int var3;
      for(var3 = 0; (var2 = this.nextConsumer(var2)) != null; ++var3) {
         var2.consumer.setHints(var1);
      }

      return var3;
   }

   protected void headerComplete() {
      this.feeder.setPriority(3);
   }

   protected int setPixels(int var1, int var2, int var3, int var4, ColorModel var5, byte[] var6, int var7, int var8) {
      this.source.latchConsumers(this);
      ImageConsumerQueue var9 = null;

      int var10;
      for(var10 = 0; (var9 = this.nextConsumer(var9)) != null; ++var10) {
         var9.consumer.setPixels(var1, var2, var3, var4, var5, var6, var7, var8);
      }

      return var10;
   }

   protected int setPixels(int var1, int var2, int var3, int var4, ColorModel var5, int[] var6, int var7, int var8) {
      this.source.latchConsumers(this);
      ImageConsumerQueue var9 = null;

      int var10;
      for(var10 = 0; (var9 = this.nextConsumer(var9)) != null; ++var10) {
         var9.consumer.setPixels(var1, var2, var3, var4, var5, var6, var7, var8);
      }

      return var10;
   }

   protected int imageComplete(int var1, boolean var2) {
      this.source.latchConsumers(this);
      if (var2) {
         this.finished = true;
         this.source.doneDecoding(this);
      }

      ImageConsumerQueue var3 = null;

      int var4;
      for(var4 = 0; (var3 = this.nextConsumer(var3)) != null; ++var4) {
         var3.consumer.imageComplete(var1);
      }

      return var4;
   }

   public abstract void produceImage() throws IOException, ImageFormatException;

   public void abort() {
      this.aborted = true;
      this.source.doneDecoding(this);
      this.close();
      AccessController.doPrivileged(new PrivilegedAction() {
         public Object run() {
            ImageDecoder.this.feeder.interrupt();
            return null;
         }
      });
   }

   public synchronized void close() {
      if (this.input != null) {
         try {
            this.input.close();
         } catch (IOException var2) {
         }
      }

   }
}
