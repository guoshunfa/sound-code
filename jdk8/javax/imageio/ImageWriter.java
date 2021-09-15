package javax.imageio;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.io.IOException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import javax.imageio.event.IIOWriteProgressListener;
import javax.imageio.event.IIOWriteWarningListener;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.spi.ImageWriterSpi;

public abstract class ImageWriter implements ImageTranscoder {
   protected ImageWriterSpi originatingProvider = null;
   protected Object output = null;
   protected Locale[] availableLocales = null;
   protected Locale locale = null;
   protected List<IIOWriteWarningListener> warningListeners = null;
   protected List<Locale> warningLocales = null;
   protected List<IIOWriteProgressListener> progressListeners = null;
   private boolean abortFlag = false;

   protected ImageWriter(ImageWriterSpi var1) {
      this.originatingProvider = var1;
   }

   public ImageWriterSpi getOriginatingProvider() {
      return this.originatingProvider;
   }

   public void setOutput(Object var1) {
      if (var1 != null) {
         ImageWriterSpi var2 = this.getOriginatingProvider();
         if (var2 != null) {
            Class[] var3 = var2.getOutputTypes();
            boolean var4 = false;

            for(int var5 = 0; var5 < var3.length; ++var5) {
               if (var3[var5].isInstance(var1)) {
                  var4 = true;
                  break;
               }
            }

            if (!var4) {
               throw new IllegalArgumentException("Illegal output type!");
            }
         }
      }

      this.output = var1;
   }

   public Object getOutput() {
      return this.output;
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

   public ImageWriteParam getDefaultWriteParam() {
      return new ImageWriteParam(this.getLocale());
   }

   public abstract IIOMetadata getDefaultStreamMetadata(ImageWriteParam var1);

   public abstract IIOMetadata getDefaultImageMetadata(ImageTypeSpecifier var1, ImageWriteParam var2);

   public abstract IIOMetadata convertStreamMetadata(IIOMetadata var1, ImageWriteParam var2);

   public abstract IIOMetadata convertImageMetadata(IIOMetadata var1, ImageTypeSpecifier var2, ImageWriteParam var3);

   public int getNumThumbnailsSupported(ImageTypeSpecifier var1, ImageWriteParam var2, IIOMetadata var3, IIOMetadata var4) {
      return 0;
   }

   public Dimension[] getPreferredThumbnailSizes(ImageTypeSpecifier var1, ImageWriteParam var2, IIOMetadata var3, IIOMetadata var4) {
      return null;
   }

   public boolean canWriteRasters() {
      return false;
   }

   public abstract void write(IIOMetadata var1, IIOImage var2, ImageWriteParam var3) throws IOException;

   public void write(IIOImage var1) throws IOException {
      this.write((IIOMetadata)null, var1, (ImageWriteParam)null);
   }

   public void write(RenderedImage var1) throws IOException {
      this.write((IIOMetadata)null, new IIOImage(var1, (List)null, (IIOMetadata)null), (ImageWriteParam)null);
   }

   private void unsupported() {
      if (this.getOutput() == null) {
         throw new IllegalStateException("getOutput() == null!");
      } else {
         throw new UnsupportedOperationException("Unsupported write variant!");
      }
   }

   public boolean canWriteSequence() {
      return false;
   }

   public void prepareWriteSequence(IIOMetadata var1) throws IOException {
      this.unsupported();
   }

   public void writeToSequence(IIOImage var1, ImageWriteParam var2) throws IOException {
      this.unsupported();
   }

   public void endWriteSequence() throws IOException {
      this.unsupported();
   }

   public boolean canReplaceStreamMetadata() throws IOException {
      if (this.getOutput() == null) {
         throw new IllegalStateException("getOutput() == null!");
      } else {
         return false;
      }
   }

   public void replaceStreamMetadata(IIOMetadata var1) throws IOException {
      this.unsupported();
   }

   public boolean canReplaceImageMetadata(int var1) throws IOException {
      if (this.getOutput() == null) {
         throw new IllegalStateException("getOutput() == null!");
      } else {
         return false;
      }
   }

   public void replaceImageMetadata(int var1, IIOMetadata var2) throws IOException {
      this.unsupported();
   }

   public boolean canInsertImage(int var1) throws IOException {
      if (this.getOutput() == null) {
         throw new IllegalStateException("getOutput() == null!");
      } else {
         return false;
      }
   }

   public void writeInsert(int var1, IIOImage var2, ImageWriteParam var3) throws IOException {
      this.unsupported();
   }

   public boolean canRemoveImage(int var1) throws IOException {
      if (this.getOutput() == null) {
         throw new IllegalStateException("getOutput() == null!");
      } else {
         return false;
      }
   }

   public void removeImage(int var1) throws IOException {
      this.unsupported();
   }

   public boolean canWriteEmpty() throws IOException {
      if (this.getOutput() == null) {
         throw new IllegalStateException("getOutput() == null!");
      } else {
         return false;
      }
   }

   public void prepareWriteEmpty(IIOMetadata var1, ImageTypeSpecifier var2, int var3, int var4, IIOMetadata var5, List<? extends BufferedImage> var6, ImageWriteParam var7) throws IOException {
      this.unsupported();
   }

   public void endWriteEmpty() throws IOException {
      if (this.getOutput() == null) {
         throw new IllegalStateException("getOutput() == null!");
      } else {
         throw new IllegalStateException("No call to prepareWriteEmpty!");
      }
   }

   public boolean canInsertEmpty(int var1) throws IOException {
      if (this.getOutput() == null) {
         throw new IllegalStateException("getOutput() == null!");
      } else {
         return false;
      }
   }

   public void prepareInsertEmpty(int var1, ImageTypeSpecifier var2, int var3, int var4, IIOMetadata var5, List<? extends BufferedImage> var6, ImageWriteParam var7) throws IOException {
      this.unsupported();
   }

   public void endInsertEmpty() throws IOException {
      this.unsupported();
   }

   public boolean canReplacePixels(int var1) throws IOException {
      if (this.getOutput() == null) {
         throw new IllegalStateException("getOutput() == null!");
      } else {
         return false;
      }
   }

   public void prepareReplacePixels(int var1, Rectangle var2) throws IOException {
      this.unsupported();
   }

   public void replacePixels(RenderedImage var1, ImageWriteParam var2) throws IOException {
      this.unsupported();
   }

   public void replacePixels(Raster var1, ImageWriteParam var2) throws IOException {
      this.unsupported();
   }

   public void endReplacePixels() throws IOException {
      this.unsupported();
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

   public void addIIOWriteWarningListener(IIOWriteWarningListener var1) {
      if (var1 != null) {
         this.warningListeners = ImageReader.addToList(this.warningListeners, var1);
         this.warningLocales = ImageReader.addToList(this.warningLocales, this.getLocale());
      }
   }

   public void removeIIOWriteWarningListener(IIOWriteWarningListener var1) {
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

   public void removeAllIIOWriteWarningListeners() {
      this.warningListeners = null;
      this.warningLocales = null;
   }

   public void addIIOWriteProgressListener(IIOWriteProgressListener var1) {
      if (var1 != null) {
         this.progressListeners = ImageReader.addToList(this.progressListeners, var1);
      }
   }

   public void removeIIOWriteProgressListener(IIOWriteProgressListener var1) {
      if (var1 != null && this.progressListeners != null) {
         this.progressListeners = ImageReader.removeFromList(this.progressListeners, var1);
      }
   }

   public void removeAllIIOWriteProgressListeners() {
      this.progressListeners = null;
   }

   protected void processImageStarted(int var1) {
      if (this.progressListeners != null) {
         int var2 = this.progressListeners.size();

         for(int var3 = 0; var3 < var2; ++var3) {
            IIOWriteProgressListener var4 = (IIOWriteProgressListener)this.progressListeners.get(var3);
            var4.imageStarted(this, var1);
         }

      }
   }

   protected void processImageProgress(float var1) {
      if (this.progressListeners != null) {
         int var2 = this.progressListeners.size();

         for(int var3 = 0; var3 < var2; ++var3) {
            IIOWriteProgressListener var4 = (IIOWriteProgressListener)this.progressListeners.get(var3);
            var4.imageProgress(this, var1);
         }

      }
   }

   protected void processImageComplete() {
      if (this.progressListeners != null) {
         int var1 = this.progressListeners.size();

         for(int var2 = 0; var2 < var1; ++var2) {
            IIOWriteProgressListener var3 = (IIOWriteProgressListener)this.progressListeners.get(var2);
            var3.imageComplete(this);
         }

      }
   }

   protected void processThumbnailStarted(int var1, int var2) {
      if (this.progressListeners != null) {
         int var3 = this.progressListeners.size();

         for(int var4 = 0; var4 < var3; ++var4) {
            IIOWriteProgressListener var5 = (IIOWriteProgressListener)this.progressListeners.get(var4);
            var5.thumbnailStarted(this, var1, var2);
         }

      }
   }

   protected void processThumbnailProgress(float var1) {
      if (this.progressListeners != null) {
         int var2 = this.progressListeners.size();

         for(int var3 = 0; var3 < var2; ++var3) {
            IIOWriteProgressListener var4 = (IIOWriteProgressListener)this.progressListeners.get(var3);
            var4.thumbnailProgress(this, var1);
         }

      }
   }

   protected void processThumbnailComplete() {
      if (this.progressListeners != null) {
         int var1 = this.progressListeners.size();

         for(int var2 = 0; var2 < var1; ++var2) {
            IIOWriteProgressListener var3 = (IIOWriteProgressListener)this.progressListeners.get(var2);
            var3.thumbnailComplete(this);
         }

      }
   }

   protected void processWriteAborted() {
      if (this.progressListeners != null) {
         int var1 = this.progressListeners.size();

         for(int var2 = 0; var2 < var1; ++var2) {
            IIOWriteProgressListener var3 = (IIOWriteProgressListener)this.progressListeners.get(var2);
            var3.writeAborted(this);
         }

      }
   }

   protected void processWarningOccurred(int var1, String var2) {
      if (this.warningListeners != null) {
         if (var2 == null) {
            throw new IllegalArgumentException("warning == null!");
         } else {
            int var3 = this.warningListeners.size();

            for(int var4 = 0; var4 < var3; ++var4) {
               IIOWriteWarningListener var5 = (IIOWriteWarningListener)this.warningListeners.get(var4);
               var5.warningOccurred(this, var1, var2);
            }

         }
      }
   }

   protected void processWarningOccurred(int var1, String var2, String var3) {
      if (this.warningListeners != null) {
         if (var2 == null) {
            throw new IllegalArgumentException("baseName == null!");
         } else if (var3 == null) {
            throw new IllegalArgumentException("keyword == null!");
         } else {
            int var4 = this.warningListeners.size();

            for(int var5 = 0; var5 < var4; ++var5) {
               IIOWriteWarningListener var6 = (IIOWriteWarningListener)this.warningListeners.get(var5);
               Locale var7 = (Locale)this.warningLocales.get(var5);
               if (var7 == null) {
                  var7 = Locale.getDefault();
               }

               ClassLoader var8 = (ClassLoader)AccessController.doPrivileged(new PrivilegedAction() {
                  public Object run() {
                     return Thread.currentThread().getContextClassLoader();
                  }
               });
               ResourceBundle var9 = null;

               try {
                  var9 = ResourceBundle.getBundle(var2, var7, var8);
               } catch (MissingResourceException var15) {
                  try {
                     var9 = ResourceBundle.getBundle(var2, var7);
                  } catch (MissingResourceException var14) {
                     throw new IllegalArgumentException("Bundle not found!");
                  }
               }

               String var10 = null;

               try {
                  var10 = var9.getString(var3);
               } catch (ClassCastException var12) {
                  throw new IllegalArgumentException("Resource is not a String!");
               } catch (MissingResourceException var13) {
                  throw new IllegalArgumentException("Resource is missing!");
               }

               var6.warningOccurred(this, var1, var10);
            }

         }
      }
   }

   public void reset() {
      this.setOutput((Object)null);
      this.setLocale((Locale)null);
      this.removeAllIIOWriteWarningListeners();
      this.removeAllIIOWriteProgressListeners();
      this.clearAbortRequest();
   }

   public void dispose() {
   }
}
