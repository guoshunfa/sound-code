package javax.imageio;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.io.IOException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;
import javax.imageio.event.IIOReadProgressListener;
import javax.imageio.event.IIOReadUpdateListener;
import javax.imageio.event.IIOReadWarningListener;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.stream.ImageInputStream;

public abstract class ImageReader {
   protected ImageReaderSpi originatingProvider;
   protected Object input = null;
   protected boolean seekForwardOnly = false;
   protected boolean ignoreMetadata = false;
   protected int minIndex = 0;
   protected Locale[] availableLocales = null;
   protected Locale locale = null;
   protected List<IIOReadWarningListener> warningListeners = null;
   protected List<Locale> warningLocales = null;
   protected List<IIOReadProgressListener> progressListeners = null;
   protected List<IIOReadUpdateListener> updateListeners = null;
   private boolean abortFlag = false;

   protected ImageReader(ImageReaderSpi var1) {
      this.originatingProvider = var1;
   }

   public String getFormatName() throws IOException {
      return this.originatingProvider.getFormatNames()[0];
   }

   public ImageReaderSpi getOriginatingProvider() {
      return this.originatingProvider;
   }

   public void setInput(Object var1, boolean var2, boolean var3) {
      if (var1 != null) {
         boolean var4 = false;
         if (this.originatingProvider != null) {
            Class[] var5 = this.originatingProvider.getInputTypes();

            for(int var6 = 0; var6 < var5.length; ++var6) {
               if (var5[var6].isInstance(var1)) {
                  var4 = true;
                  break;
               }
            }
         } else if (var1 instanceof ImageInputStream) {
            var4 = true;
         }

         if (!var4) {
            throw new IllegalArgumentException("Incorrect input type!");
         }

         this.seekForwardOnly = var2;
         this.ignoreMetadata = var3;
         this.minIndex = 0;
      }

      this.input = var1;
   }

   public void setInput(Object var1, boolean var2) {
      this.setInput(var1, var2, false);
   }

   public void setInput(Object var1) {
      this.setInput(var1, false, false);
   }

   public Object getInput() {
      return this.input;
   }

   public boolean isSeekForwardOnly() {
      return this.seekForwardOnly;
   }

   public boolean isIgnoringMetadata() {
      return this.ignoreMetadata;
   }

   public int getMinIndex() {
      return this.minIndex;
   }

   public Locale[] getAvailableLocales() {
      return this.availableLocales == null ? null : (Locale[])((Locale[])this.availableLocales.clone());
   }

   public void setLocale(Locale var1) {
      if (var1 != null) {
         Locale[] var2 = this.getAvailableLocales();
         boolean var3 = false;
         if (var2 != null) {
            for(int var4 = 0; var4 < var2.length; ++var4) {
               if (var1.equals(var2[var4])) {
                  var3 = true;
                  break;
               }
            }
         }

         if (!var3) {
            throw new IllegalArgumentException("Invalid locale!");
         }
      }

      this.locale = var1;
   }

   public Locale getLocale() {
      return this.locale;
   }

   public abstract int getNumImages(boolean var1) throws IOException;

   public abstract int getWidth(int var1) throws IOException;

   public abstract int getHeight(int var1) throws IOException;

   public boolean isRandomAccessEasy(int var1) throws IOException {
      return false;
   }

   public float getAspectRatio(int var1) throws IOException {
      return (float)this.getWidth(var1) / (float)this.getHeight(var1);
   }

   public ImageTypeSpecifier getRawImageType(int var1) throws IOException {
      return (ImageTypeSpecifier)this.getImageTypes(var1).next();
   }

   public abstract Iterator<ImageTypeSpecifier> getImageTypes(int var1) throws IOException;

   public ImageReadParam getDefaultReadParam() {
      return new ImageReadParam();
   }

   public abstract IIOMetadata getStreamMetadata() throws IOException;

   public IIOMetadata getStreamMetadata(String var1, Set<String> var2) throws IOException {
      return this.getMetadata(var1, var2, true, 0);
   }

   private IIOMetadata getMetadata(String var1, Set var2, boolean var3, int var4) throws IOException {
      if (var1 == null) {
         throw new IllegalArgumentException("formatName == null!");
      } else if (var2 == null) {
         throw new IllegalArgumentException("nodeNames == null!");
      } else {
         IIOMetadata var5 = var3 ? this.getStreamMetadata() : this.getImageMetadata(var4);
         if (var5 != null) {
            if (var5.isStandardMetadataFormatSupported() && var1.equals("javax_imageio_1.0")) {
               return var5;
            }

            String var6 = var5.getNativeMetadataFormatName();
            if (var6 != null && var1.equals(var6)) {
               return var5;
            }

            String[] var7 = var5.getExtraMetadataFormatNames();
            if (var7 != null) {
               for(int var8 = 0; var8 < var7.length; ++var8) {
                  if (var1.equals(var7[var8])) {
                     return var5;
                  }
               }
            }
         }

         return null;
      }
   }

   public abstract IIOMetadata getImageMetadata(int var1) throws IOException;

   public IIOMetadata getImageMetadata(int var1, String var2, Set<String> var3) throws IOException {
      return this.getMetadata(var2, var3, false, var1);
   }

   public BufferedImage read(int var1) throws IOException {
      return this.read(var1, (ImageReadParam)null);
   }

   public abstract BufferedImage read(int var1, ImageReadParam var2) throws IOException;

   public IIOImage readAll(int var1, ImageReadParam var2) throws IOException {
      if (var1 < this.getMinIndex()) {
         throw new IndexOutOfBoundsException("imageIndex < getMinIndex()!");
      } else {
         BufferedImage var3 = this.read(var1, var2);
         ArrayList var4 = null;
         int var5 = this.getNumThumbnails(var1);
         if (var5 > 0) {
            var4 = new ArrayList();

            for(int var6 = 0; var6 < var5; ++var6) {
               var4.add(this.readThumbnail(var1, var6));
            }
         }

         IIOMetadata var7 = this.getImageMetadata(var1);
         return new IIOImage(var3, var4, var7);
      }
   }

   public Iterator<IIOImage> readAll(Iterator<? extends ImageReadParam> var1) throws IOException {
      ArrayList var2 = new ArrayList();
      int var3 = this.getMinIndex();
      this.processSequenceStarted(var3);

      while(true) {
         ImageReadParam var4 = null;
         Object var5;
         if (var1 != null && var1.hasNext()) {
            var5 = var1.next();
            if (var5 != null) {
               if (!(var5 instanceof ImageReadParam)) {
                  throw new IllegalArgumentException("Non-ImageReadParam supplied as part of params!");
               }

               var4 = (ImageReadParam)var5;
            }
         }

         var5 = null;

         BufferedImage var11;
         try {
            var11 = this.read(var3, var4);
         } catch (IndexOutOfBoundsException var10) {
            this.processSequenceComplete();
            return var2.iterator();
         }

         ArrayList var6 = null;
         int var7 = this.getNumThumbnails(var3);
         if (var7 > 0) {
            var6 = new ArrayList();

            for(int var8 = 0; var8 < var7; ++var8) {
               var6.add(this.readThumbnail(var3, var8));
            }
         }

         IIOMetadata var12 = this.getImageMetadata(var3);
         IIOImage var9 = new IIOImage(var11, var6, var12);
         var2.add(var9);
         ++var3;
      }
   }

   public boolean canReadRaster() {
      return false;
   }

   public Raster readRaster(int var1, ImageReadParam var2) throws IOException {
      throw new UnsupportedOperationException("readRaster not supported!");
   }

   public boolean isImageTiled(int var1) throws IOException {
      return false;
   }

   public int getTileWidth(int var1) throws IOException {
      return this.getWidth(var1);
   }

   public int getTileHeight(int var1) throws IOException {
      return this.getHeight(var1);
   }

   public int getTileGridXOffset(int var1) throws IOException {
      return 0;
   }

   public int getTileGridYOffset(int var1) throws IOException {
      return 0;
   }

   public BufferedImage readTile(int var1, int var2, int var3) throws IOException {
      if (var2 == 0 && var3 == 0) {
         return this.read(var1);
      } else {
         throw new IllegalArgumentException("Invalid tile indices");
      }
   }

   public Raster readTileRaster(int var1, int var2, int var3) throws IOException {
      if (!this.canReadRaster()) {
         throw new UnsupportedOperationException("readTileRaster not supported!");
      } else if (var2 == 0 && var3 == 0) {
         return this.readRaster(var1, (ImageReadParam)null);
      } else {
         throw new IllegalArgumentException("Invalid tile indices");
      }
   }

   public RenderedImage readAsRenderedImage(int var1, ImageReadParam var2) throws IOException {
      return this.read(var1, var2);
   }

   public boolean readerSupportsThumbnails() {
      return false;
   }

   public boolean hasThumbnails(int var1) throws IOException {
      return this.getNumThumbnails(var1) > 0;
   }

   public int getNumThumbnails(int var1) throws IOException {
      return 0;
   }

   public int getThumbnailWidth(int var1, int var2) throws IOException {
      return this.readThumbnail(var1, var2).getWidth();
   }

   public int getThumbnailHeight(int var1, int var2) throws IOException {
      return this.readThumbnail(var1, var2).getHeight();
   }

   public BufferedImage readThumbnail(int var1, int var2) throws IOException {
      throw new UnsupportedOperationException("Thumbnails not supported!");
   }

   public synchronized void abort() {
      this.abortFlag = true;
   }

   protected synchronized boolean abortRequested() {
      return this.abortFlag;
   }

   protected synchronized void clearAbortRequest() {
      this.abortFlag = false;
   }

   static List addToList(List var0, Object var1) {
      if (var0 == null) {
         var0 = new ArrayList();
      }

      ((List)var0).add(var1);
      return (List)var0;
   }

   static List removeFromList(List var0, Object var1) {
      if (var0 == null) {
         return var0;
      } else {
         var0.remove(var1);
         if (var0.size() == 0) {
            var0 = null;
         }

         return var0;
      }
   }

   public void addIIOReadWarningListener(IIOReadWarningListener var1) {
      if (var1 != null) {
         this.warningListeners = addToList(this.warningListeners, var1);
         this.warningLocales = addToList(this.warningLocales, this.getLocale());
      }
   }

   public void removeIIOReadWarningListener(IIOReadWarningListener var1) {
      if (var1 != null && this.warningListeners != null) {
         int var2 = this.warningListeners.indexOf(var1);
         if (var2 != -1) {
            this.warningListeners.remove(var2);
            this.warningLocales.remove(var2);
            if (this.warningListeners.size() == 0) {
               this.warningListeners = null;
               this.warningLocales = null;
            }
         }

      }
   }

   public void removeAllIIOReadWarningListeners() {
      this.warningListeners = null;
      this.warningLocales = null;
   }

   public void addIIOReadProgressListener(IIOReadProgressListener var1) {
      if (var1 != null) {
         this.progressListeners = addToList(this.progressListeners, var1);
      }
   }

   public void removeIIOReadProgressListener(IIOReadProgressListener var1) {
      if (var1 != null && this.progressListeners != null) {
         this.progressListeners = removeFromList(this.progressListeners, var1);
      }
   }

   public void removeAllIIOReadProgressListeners() {
      this.progressListeners = null;
   }

   public void addIIOReadUpdateListener(IIOReadUpdateListener var1) {
      if (var1 != null) {
         this.updateListeners = addToList(this.updateListeners, var1);
      }
   }

   public void removeIIOReadUpdateListener(IIOReadUpdateListener var1) {
      if (var1 != null && this.updateListeners != null) {
         this.updateListeners = removeFromList(this.updateListeners, var1);
      }
   }

   public void removeAllIIOReadUpdateListeners() {
      this.updateListeners = null;
   }

   protected void processSequenceStarted(int var1) {
      if (this.progressListeners != null) {
         int var2 = this.progressListeners.size();

         for(int var3 = 0; var3 < var2; ++var3) {
            IIOReadProgressListener var4 = (IIOReadProgressListener)this.progressListeners.get(var3);
            var4.sequenceStarted(this, var1);
         }

      }
   }

   protected void processSequenceComplete() {
      if (this.progressListeners != null) {
         int var1 = this.progressListeners.size();

         for(int var2 = 0; var2 < var1; ++var2) {
            IIOReadProgressListener var3 = (IIOReadProgressListener)this.progressListeners.get(var2);
            var3.sequenceComplete(this);
         }

      }
   }

   protected void processImageStarted(int var1) {
      if (this.progressListeners != null) {
         int var2 = this.progressListeners.size();

         for(int var3 = 0; var3 < var2; ++var3) {
            IIOReadProgressListener var4 = (IIOReadProgressListener)this.progressListeners.get(var3);
            var4.imageStarted(this, var1);
         }

      }
   }

   protected void processImageProgress(float var1) {
      if (this.progressListeners != null) {
         int var2 = this.progressListeners.size();

         for(int var3 = 0; var3 < var2; ++var3) {
            IIOReadProgressListener var4 = (IIOReadProgressListener)this.progressListeners.get(var3);
            var4.imageProgress(this, var1);
         }

      }
   }

   protected void processImageComplete() {
      if (this.progressListeners != null) {
         int var1 = this.progressListeners.size();

         for(int var2 = 0; var2 < var1; ++var2) {
            IIOReadProgressListener var3 = (IIOReadProgressListener)this.progressListeners.get(var2);
            var3.imageComplete(this);
         }

      }
   }

   protected void processThumbnailStarted(int var1, int var2) {
      if (this.progressListeners != null) {
         int var3 = this.progressListeners.size();

         for(int var4 = 0; var4 < var3; ++var4) {
            IIOReadProgressListener var5 = (IIOReadProgressListener)this.progressListeners.get(var4);
            var5.thumbnailStarted(this, var1, var2);
         }

      }
   }

   protected void processThumbnailProgress(float var1) {
      if (this.progressListeners != null) {
         int var2 = this.progressListeners.size();

         for(int var3 = 0; var3 < var2; ++var3) {
            IIOReadProgressListener var4 = (IIOReadProgressListener)this.progressListeners.get(var3);
            var4.thumbnailProgress(this, var1);
         }

      }
   }

   protected void processThumbnailComplete() {
      if (this.progressListeners != null) {
         int var1 = this.progressListeners.size();

         for(int var2 = 0; var2 < var1; ++var2) {
            IIOReadProgressListener var3 = (IIOReadProgressListener)this.progressListeners.get(var2);
            var3.thumbnailComplete(this);
         }

      }
   }

   protected void processReadAborted() {
      if (this.progressListeners != null) {
         int var1 = this.progressListeners.size();

         for(int var2 = 0; var2 < var1; ++var2) {
            IIOReadProgressListener var3 = (IIOReadProgressListener)this.progressListeners.get(var2);
            var3.readAborted(this);
         }

      }
   }

   protected void processPassStarted(BufferedImage var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, int[] var9) {
      if (this.updateListeners != null) {
         int var10 = this.updateListeners.size();

         for(int var11 = 0; var11 < var10; ++var11) {
            IIOReadUpdateListener var12 = (IIOReadUpdateListener)this.updateListeners.get(var11);
            var12.passStarted(this, var1, var2, var3, var4, var5, var6, var7, var8, var9);
         }

      }
   }

   protected void processImageUpdate(BufferedImage var1, int var2, int var3, int var4, int var5, int var6, int var7, int[] var8) {
      if (this.updateListeners != null) {
         int var9 = this.updateListeners.size();

         for(int var10 = 0; var10 < var9; ++var10) {
            IIOReadUpdateListener var11 = (IIOReadUpdateListener)this.updateListeners.get(var10);
            var11.imageUpdate(this, var1, var2, var3, var4, var5, var6, var7, var8);
         }

      }
   }

   protected void processPassComplete(BufferedImage var1) {
      if (this.updateListeners != null) {
         int var2 = this.updateListeners.size();

         for(int var3 = 0; var3 < var2; ++var3) {
            IIOReadUpdateListener var4 = (IIOReadUpdateListener)this.updateListeners.get(var3);
            var4.passComplete(this, var1);
         }

      }
   }

   protected void processThumbnailPassStarted(BufferedImage var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, int[] var9) {
      if (this.updateListeners != null) {
         int var10 = this.updateListeners.size();

         for(int var11 = 0; var11 < var10; ++var11) {
            IIOReadUpdateListener var12 = (IIOReadUpdateListener)this.updateListeners.get(var11);
            var12.thumbnailPassStarted(this, var1, var2, var3, var4, var5, var6, var7, var8, var9);
         }

      }
   }

   protected void processThumbnailUpdate(BufferedImage var1, int var2, int var3, int var4, int var5, int var6, int var7, int[] var8) {
      if (this.updateListeners != null) {
         int var9 = this.updateListeners.size();

         for(int var10 = 0; var10 < var9; ++var10) {
            IIOReadUpdateListener var11 = (IIOReadUpdateListener)this.updateListeners.get(var10);
            var11.thumbnailUpdate(this, var1, var2, var3, var4, var5, var6, var7, var8);
         }

      }
   }

   protected void processThumbnailPassComplete(BufferedImage var1) {
      if (this.updateListeners != null) {
         int var2 = this.updateListeners.size();

         for(int var3 = 0; var3 < var2; ++var3) {
            IIOReadUpdateListener var4 = (IIOReadUpdateListener)this.updateListeners.get(var3);
            var4.thumbnailPassComplete(this, var1);
         }

      }
   }

   protected void processWarningOccurred(String var1) {
      if (this.warningListeners != null) {
         if (var1 == null) {
            throw new IllegalArgumentException("warning == null!");
         } else {
            int var2 = this.warningListeners.size();

            for(int var3 = 0; var3 < var2; ++var3) {
               IIOReadWarningListener var4 = (IIOReadWarningListener)this.warningListeners.get(var3);
               var4.warningOccurred(this, var1);
            }

         }
      }
   }

   protected void processWarningOccurred(String var1, String var2) {
      if (this.warningListeners != null) {
         if (var1 == null) {
            throw new IllegalArgumentException("baseName == null!");
         } else if (var2 == null) {
            throw new IllegalArgumentException("keyword == null!");
         } else {
            int var3 = this.warningListeners.size();

            for(int var4 = 0; var4 < var3; ++var4) {
               IIOReadWarningListener var5 = (IIOReadWarningListener)this.warningListeners.get(var4);
               Locale var6 = (Locale)this.warningLocales.get(var4);
               if (var6 == null) {
                  var6 = Locale.getDefault();
               }

               ClassLoader var7 = (ClassLoader)AccessController.doPrivileged(new PrivilegedAction() {
                  public Object run() {
                     return Thread.currentThread().getContextClassLoader();
                  }
               });
               ResourceBundle var8 = null;

               try {
                  var8 = ResourceBundle.getBundle(var1, var6, var7);
               } catch (MissingResourceException var14) {
                  try {
                     var8 = ResourceBundle.getBundle(var1, var6);
                  } catch (MissingResourceException var13) {
                     throw new IllegalArgumentException("Bundle not found!");
                  }
               }

               String var9 = null;

               try {
                  var9 = var8.getString(var2);
               } catch (ClassCastException var11) {
                  throw new IllegalArgumentException("Resource is not a String!");
               } catch (MissingResourceException var12) {
                  throw new IllegalArgumentException("Resource is missing!");
               }

               var5.warningOccurred(this, var9);
            }

         }
      }
   }

   public void reset() {
      this.setInput((Object)null, false, false);
      this.setLocale((Locale)null);
      this.removeAllIIOReadUpdateListeners();
      this.removeAllIIOReadProgressListeners();
      this.removeAllIIOReadWarningListeners();
      this.clearAbortRequest();
   }

   public void dispose() {
   }

   protected static Rectangle getSourceRegion(ImageReadParam var0, int var1, int var2) {
      Rectangle var3 = new Rectangle(0, 0, var1, var2);
      if (var0 != null) {
         Rectangle var4 = var0.getSourceRegion();
         if (var4 != null) {
            var3 = var3.intersection(var4);
         }

         int var5 = var0.getSubsamplingXOffset();
         int var6 = var0.getSubsamplingYOffset();
         var3.x += var5;
         var3.y += var6;
         var3.width -= var5;
         var3.height -= var6;
      }

      return var3;
   }

   protected static void computeRegions(ImageReadParam var0, int var1, int var2, BufferedImage var3, Rectangle var4, Rectangle var5) {
      if (var4 == null) {
         throw new IllegalArgumentException("srcRegion == null!");
      } else if (var5 == null) {
         throw new IllegalArgumentException("destRegion == null!");
      } else {
         var4.setBounds(0, 0, var1, var2);
         var5.setBounds(0, 0, var1, var2);
         int var6 = 1;
         int var7 = 1;
         boolean var8 = false;
         boolean var9 = false;
         if (var0 != null) {
            Rectangle var10 = var0.getSourceRegion();
            if (var10 != null) {
               var4.setBounds(var4.intersection(var10));
            }

            var6 = var0.getSourceXSubsampling();
            var7 = var0.getSourceYSubsampling();
            int var15 = var0.getSubsamplingXOffset();
            int var16 = var0.getSubsamplingYOffset();
            var4.translate(var15, var16);
            var4.width -= var15;
            var4.height -= var16;
            var5.setLocation(var0.getDestinationOffset());
         }

         int var17;
         if (var5.x < 0) {
            var17 = -var5.x * var6;
            var4.x += var17;
            var4.width -= var17;
            var5.x = 0;
         }

         if (var5.y < 0) {
            var17 = -var5.y * var7;
            var4.y += var17;
            var4.height -= var17;
            var5.y = 0;
         }

         var17 = (var4.width + var6 - 1) / var6;
         int var11 = (var4.height + var7 - 1) / var7;
         var5.width = var17;
         var5.height = var11;
         if (var3 != null) {
            Rectangle var12 = new Rectangle(0, 0, var3.getWidth(), var3.getHeight());
            var5.setBounds(var5.intersection(var12));
            if (var5.isEmpty()) {
               throw new IllegalArgumentException("Empty destination region!");
            }

            int var13 = var5.x + var17 - var3.getWidth();
            if (var13 > 0) {
               var4.width -= var13 * var6;
            }

            int var14 = var5.y + var11 - var3.getHeight();
            if (var14 > 0) {
               var4.height -= var14 * var7;
            }
         }

         if (var4.isEmpty() || var5.isEmpty()) {
            throw new IllegalArgumentException("Empty region!");
         }
      }
   }

   protected static void checkReadParamBandSettings(ImageReadParam var0, int var1, int var2) {
      int[] var3 = null;
      int[] var4 = null;
      if (var0 != null) {
         var3 = var0.getSourceBands();
         var4 = var0.getDestinationBands();
      }

      int var5 = var3 == null ? var1 : var3.length;
      int var6 = var4 == null ? var2 : var4.length;
      if (var5 != var6) {
         throw new IllegalArgumentException("ImageReadParam num source & dest bands differ!");
      } else {
         int var7;
         if (var3 != null) {
            for(var7 = 0; var7 < var3.length; ++var7) {
               if (var3[var7] >= var1) {
                  throw new IllegalArgumentException("ImageReadParam source bands contains a value >= the number of source bands!");
               }
            }
         }

         if (var4 != null) {
            for(var7 = 0; var7 < var4.length; ++var7) {
               if (var4[var7] >= var2) {
                  throw new IllegalArgumentException("ImageReadParam dest bands contains a value >= the number of dest bands!");
               }
            }
         }

      }
   }

   protected static BufferedImage getDestination(ImageReadParam var0, Iterator<ImageTypeSpecifier> var1, int var2, int var3) throws IIOException {
      if (var1 != null && var1.hasNext()) {
         if ((long)var2 * (long)var3 > 2147483647L) {
            throw new IllegalArgumentException("width*height > Integer.MAX_VALUE!");
         } else {
            BufferedImage var4 = null;
            ImageTypeSpecifier var5 = null;
            if (var0 != null) {
               var4 = var0.getDestination();
               if (var4 != null) {
                  return var4;
               }

               var5 = var0.getDestinationType();
            }

            if (var5 == null) {
               Object var6 = var1.next();
               if (!(var6 instanceof ImageTypeSpecifier)) {
                  throw new IllegalArgumentException("Non-ImageTypeSpecifier retrieved from imageTypes!");
               }

               var5 = (ImageTypeSpecifier)var6;
            } else {
               boolean var10 = false;

               while(var1.hasNext()) {
                  ImageTypeSpecifier var7 = (ImageTypeSpecifier)var1.next();
                  if (var7.equals(var5)) {
                     var10 = true;
                     break;
                  }
               }

               if (!var10) {
                  throw new IIOException("Destination type from ImageReadParam does not match!");
               }
            }

            Rectangle var11 = new Rectangle(0, 0, 0, 0);
            Rectangle var12 = new Rectangle(0, 0, 0, 0);
            computeRegions(var0, var2, var3, (BufferedImage)null, var11, var12);
            int var8 = var12.x + var12.width;
            int var9 = var12.y + var12.height;
            return var5.createBufferedImage(var8, var9);
         }
      } else {
         throw new IllegalArgumentException("imageTypes null or empty!");
      }
   }
}
