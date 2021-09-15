package sun.awt.image;

import java.awt.image.ImageConsumer;
import java.awt.image.ImageProducer;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

public abstract class InputStreamImageSource implements ImageProducer, ImageFetchable {
   ImageConsumerQueue consumers;
   ImageDecoder decoder;
   ImageDecoder decoders;
   boolean awaitingFetch = false;

   abstract boolean checkSecurity(Object var1, boolean var2);

   int countConsumers(ImageConsumerQueue var1) {
      int var2;
      for(var2 = 0; var1 != null; var1 = var1.next) {
         ++var2;
      }

      return var2;
   }

   synchronized int countConsumers() {
      ImageDecoder var1 = this.decoders;

      int var2;
      for(var2 = this.countConsumers(this.consumers); var1 != null; var1 = var1.next) {
         var2 += this.countConsumers(var1.queue);
      }

      return var2;
   }

   public void addConsumer(ImageConsumer var1) {
      this.addConsumer(var1, false);
   }

   synchronized void printQueue(ImageConsumerQueue var1, String var2) {
      while(var1 != null) {
         System.out.println(var2 + var1);
         var1 = var1.next;
      }

   }

   synchronized void printQueues(String var1) {
      System.out.println(var1 + "[ -----------");
      this.printQueue(this.consumers, "  ");

      for(ImageDecoder var2 = this.decoders; var2 != null; var2 = var2.next) {
         System.out.println("    " + var2);
         this.printQueue(var2.queue, "      ");
      }

      System.out.println("----------- ]" + var1);
   }

   synchronized void addConsumer(ImageConsumer var1, boolean var2) {
      this.checkSecurity((Object)null, false);

      for(ImageDecoder var3 = this.decoders; var3 != null; var3 = var3.next) {
         if (var3.isConsumer(var1)) {
            return;
         }
      }

      ImageConsumerQueue var6;
      for(var6 = this.consumers; var6 != null && var6.consumer != var1; var6 = var6.next) {
      }

      if (var6 == null) {
         var6 = new ImageConsumerQueue(this, var1);
         var6.next = this.consumers;
         this.consumers = var6;
      } else {
         if (!var6.secure) {
            Object var4 = null;
            SecurityManager var5 = System.getSecurityManager();
            if (var5 != null) {
               var4 = var5.getSecurityContext();
            }

            if (var6.securityContext == null) {
               var6.securityContext = var4;
            } else if (!var6.securityContext.equals(var4)) {
               this.errorConsumer(var6, false);
               throw new SecurityException("Applets are trading image data!");
            }
         }

         var6.interested = true;
      }

      if (var2 && this.decoder == null) {
         this.startProduction();
      }

   }

   public synchronized boolean isConsumer(ImageConsumer var1) {
      for(ImageDecoder var2 = this.decoders; var2 != null; var2 = var2.next) {
         if (var2.isConsumer(var1)) {
            return true;
         }
      }

      return ImageConsumerQueue.isConsumer(this.consumers, var1);
   }

   private void errorAllConsumers(ImageConsumerQueue var1, boolean var2) {
      for(; var1 != null; var1 = var1.next) {
         if (var1.interested) {
            this.errorConsumer(var1, var2);
         }
      }

   }

   private void errorConsumer(ImageConsumerQueue var1, boolean var2) {
      var1.consumer.imageComplete(1);
      if (var2 && var1.consumer instanceof ImageRepresentation) {
         ((ImageRepresentation)var1.consumer).image.flush();
      }

      this.removeConsumer(var1.consumer);
   }

   public synchronized void removeConsumer(ImageConsumer var1) {
      for(ImageDecoder var2 = this.decoders; var2 != null; var2 = var2.next) {
         var2.removeConsumer(var1);
      }

      this.consumers = ImageConsumerQueue.removeConsumer(this.consumers, var1, false);
   }

   public void startProduction(ImageConsumer var1) {
      this.addConsumer(var1, true);
   }

   private synchronized void startProduction() {
      if (!this.awaitingFetch) {
         if (ImageFetcher.add(this)) {
            this.awaitingFetch = true;
         } else {
            ImageConsumerQueue var1 = this.consumers;
            this.consumers = null;
            this.errorAllConsumers(var1, false);
         }
      }

   }

   private synchronized void stopProduction() {
      if (this.awaitingFetch) {
         ImageFetcher.remove(this);
         this.awaitingFetch = false;
      }

   }

   public void requestTopDownLeftRightResend(ImageConsumer var1) {
   }

   protected abstract ImageDecoder getDecoder();

   protected ImageDecoder decoderForType(InputStream var1, String var2) {
      return null;
   }

   protected ImageDecoder getDecoder(InputStream var1) {
      if (!((InputStream)var1).markSupported()) {
         var1 = new BufferedInputStream((InputStream)var1);
      }

      try {
         ((InputStream)var1).mark(8);
         int var2 = ((InputStream)var1).read();
         int var3 = ((InputStream)var1).read();
         int var4 = ((InputStream)var1).read();
         int var5 = ((InputStream)var1).read();
         int var6 = ((InputStream)var1).read();
         int var7 = ((InputStream)var1).read();
         int var8 = ((InputStream)var1).read();
         int var9 = ((InputStream)var1).read();
         ((InputStream)var1).reset();
         ((InputStream)var1).mark(-1);
         if (var2 == 71 && var3 == 73 && var4 == 70 && var5 == 56) {
            return new GifImageDecoder(this, (InputStream)var1);
         }

         if (var2 == 255 && var3 == 216 && var4 == 255) {
            return new JPEGImageDecoder(this, (InputStream)var1);
         }

         if (var2 == 35 && var3 == 100 && var4 == 101 && var5 == 102) {
            return new XbmImageDecoder(this, (InputStream)var1);
         }

         if (var2 == 137 && var3 == 80 && var4 == 78 && var5 == 71 && var6 == 13 && var7 == 10 && var8 == 26 && var9 == 10) {
            return new PNGImageDecoder(this, (InputStream)var1);
         }
      } catch (IOException var10) {
      }

      return null;
   }

   public void doFetch() {
      synchronized(this) {
         if (this.consumers == null) {
            this.awaitingFetch = false;
            return;
         }
      }

      ImageDecoder var1 = this.getDecoder();
      if (var1 == null) {
         this.badDecoder();
      } else {
         this.setDecoder(var1);

         try {
            var1.produceImage();
         } catch (IOException var8) {
            var8.printStackTrace();
         } catch (ImageFormatException var9) {
            var9.printStackTrace();
         } finally {
            this.removeDecoder(var1);
            if (!Thread.currentThread().isInterrupted() && Thread.currentThread().isAlive()) {
               this.errorAllConsumers(var1.queue, false);
            } else {
               this.errorAllConsumers(var1.queue, true);
            }

         }
      }

   }

   private void badDecoder() {
      ImageConsumerQueue var1;
      synchronized(this) {
         var1 = this.consumers;
         this.consumers = null;
         this.awaitingFetch = false;
      }

      this.errorAllConsumers(var1, false);
   }

   private void setDecoder(ImageDecoder var1) {
      ImageConsumerQueue var2;
      synchronized(this) {
         var1.next = this.decoders;
         this.decoders = var1;
         this.decoder = var1;
         var2 = this.consumers;
         var1.queue = var2;
         this.consumers = null;
         this.awaitingFetch = false;
      }

      for(; var2 != null; var2 = var2.next) {
         if (var2.interested && !this.checkSecurity(var2.securityContext, true)) {
            this.errorConsumer(var2, false);
         }
      }

   }

   private synchronized void removeDecoder(ImageDecoder var1) {
      this.doneDecoding(var1);
      ImageDecoder var2 = null;

      for(ImageDecoder var3 = this.decoders; var3 != null; var3 = var3.next) {
         if (var3 == var1) {
            if (var2 == null) {
               this.decoders = var3.next;
            } else {
               var2.next = var3.next;
            }
            break;
         }

         var2 = var3;
      }

   }

   synchronized void doneDecoding(ImageDecoder var1) {
      if (this.decoder == var1) {
         this.decoder = null;
         if (this.consumers != null) {
            this.startProduction();
         }
      }

   }

   void latchConsumers(ImageDecoder var1) {
      this.doneDecoding(var1);
   }

   synchronized void flush() {
      this.decoder = null;
   }
}
