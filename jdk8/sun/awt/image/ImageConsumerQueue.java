package sun.awt.image;

import java.awt.image.ImageConsumer;

class ImageConsumerQueue {
   ImageConsumerQueue next;
   ImageConsumer consumer;
   boolean interested;
   Object securityContext;
   boolean secure;

   static ImageConsumerQueue removeConsumer(ImageConsumerQueue var0, ImageConsumer var1, boolean var2) {
      ImageConsumerQueue var3 = null;

      for(ImageConsumerQueue var4 = var0; var4 != null; var4 = var4.next) {
         if (var4.consumer == var1) {
            if (var3 == null) {
               var0 = var4.next;
            } else {
               var3.next = var4.next;
            }

            var4.interested = var2;
            break;
         }

         var3 = var4;
      }

      return var0;
   }

   static boolean isConsumer(ImageConsumerQueue var0, ImageConsumer var1) {
      for(ImageConsumerQueue var2 = var0; var2 != null; var2 = var2.next) {
         if (var2.consumer == var1) {
            return true;
         }
      }

      return false;
   }

   ImageConsumerQueue(InputStreamImageSource var1, ImageConsumer var2) {
      this.consumer = var2;
      this.interested = true;
      if (var2 instanceof ImageRepresentation) {
         ImageRepresentation var3 = (ImageRepresentation)var2;
         if (var3.image.source != var1) {
            throw new SecurityException("ImageRep added to wrong image source");
         }

         this.secure = true;
      } else {
         SecurityManager var4 = System.getSecurityManager();
         if (var4 != null) {
            this.securityContext = var4.getSecurityContext();
         } else {
            this.securityContext = null;
         }
      }

   }

   public String toString() {
      return "[" + this.consumer + ", " + (this.interested ? "" : "not ") + "interested" + (this.securityContext != null ? ", " + this.securityContext : "") + "]";
   }
}
