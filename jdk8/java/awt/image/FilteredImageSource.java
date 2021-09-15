package java.awt.image;

import java.util.Hashtable;

public class FilteredImageSource implements ImageProducer {
   ImageProducer src;
   ImageFilter filter;
   private Hashtable proxies;

   public FilteredImageSource(ImageProducer var1, ImageFilter var2) {
      this.src = var1;
      this.filter = var2;
   }

   public synchronized void addConsumer(ImageConsumer var1) {
      if (this.proxies == null) {
         this.proxies = new Hashtable();
      }

      if (!this.proxies.containsKey(var1)) {
         ImageFilter var2 = this.filter.getFilterInstance(var1);
         this.proxies.put(var1, var2);
         this.src.addConsumer(var2);
      }

   }

   public synchronized boolean isConsumer(ImageConsumer var1) {
      return this.proxies != null && this.proxies.containsKey(var1);
   }

   public synchronized void removeConsumer(ImageConsumer var1) {
      if (this.proxies != null) {
         ImageFilter var2 = (ImageFilter)this.proxies.get(var1);
         if (var2 != null) {
            this.src.removeConsumer(var2);
            this.proxies.remove(var1);
            if (this.proxies.isEmpty()) {
               this.proxies = null;
            }
         }
      }

   }

   public void startProduction(ImageConsumer var1) {
      if (this.proxies == null) {
         this.proxies = new Hashtable();
      }

      ImageFilter var2 = (ImageFilter)this.proxies.get(var1);
      if (var2 == null) {
         var2 = this.filter.getFilterInstance(var1);
         this.proxies.put(var1, var2);
      }

      this.src.startProduction(var2);
   }

   public void requestTopDownLeftRightResend(ImageConsumer var1) {
      if (this.proxies != null) {
         ImageFilter var2 = (ImageFilter)this.proxies.get(var1);
         if (var2 != null) {
            var2.resendTopDownLeftRight(this.src);
         }
      }

   }
}
