package javax.imageio.spi;

import com.sun.imageio.plugins.bmp.BMPImageReaderSpi;
import com.sun.imageio.plugins.bmp.BMPImageWriterSpi;
import com.sun.imageio.plugins.gif.GIFImageReaderSpi;
import com.sun.imageio.plugins.gif.GIFImageWriterSpi;
import com.sun.imageio.plugins.jpeg.JPEGImageReaderSpi;
import com.sun.imageio.plugins.jpeg.JPEGImageWriterSpi;
import com.sun.imageio.plugins.png.PNGImageReaderSpi;
import com.sun.imageio.plugins.png.PNGImageWriterSpi;
import com.sun.imageio.plugins.wbmp.WBMPImageReaderSpi;
import com.sun.imageio.plugins.wbmp.WBMPImageWriterSpi;
import com.sun.imageio.spi.FileImageInputStreamSpi;
import com.sun.imageio.spi.FileImageOutputStreamSpi;
import com.sun.imageio.spi.InputStreamImageInputStreamSpi;
import com.sun.imageio.spi.OutputStreamImageOutputStreamSpi;
import com.sun.imageio.spi.RAFImageInputStreamSpi;
import com.sun.imageio.spi.RAFImageOutputStreamSpi;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Iterator;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;
import java.util.Vector;
import sun.awt.AppContext;

public final class IIORegistry extends ServiceRegistry {
   private static final Vector initialCategories = new Vector(5);

   private IIORegistry() {
      super(initialCategories.iterator());
      this.registerStandardSpis();
      this.registerApplicationClasspathSpis();
   }

   public static IIORegistry getDefaultInstance() {
      AppContext var0 = AppContext.getAppContext();
      IIORegistry var1 = (IIORegistry)var0.get(IIORegistry.class);
      if (var1 == null) {
         var1 = new IIORegistry();
         var0.put(IIORegistry.class, var1);
      }

      return var1;
   }

   private void registerStandardSpis() {
      this.registerServiceProvider(new GIFImageReaderSpi());
      this.registerServiceProvider(new GIFImageWriterSpi());
      this.registerServiceProvider(new BMPImageReaderSpi());
      this.registerServiceProvider(new BMPImageWriterSpi());
      this.registerServiceProvider(new WBMPImageReaderSpi());
      this.registerServiceProvider(new WBMPImageWriterSpi());
      this.registerServiceProvider(new PNGImageReaderSpi());
      this.registerServiceProvider(new PNGImageWriterSpi());
      this.registerServiceProvider(new JPEGImageReaderSpi());
      this.registerServiceProvider(new JPEGImageWriterSpi());
      this.registerServiceProvider(new FileImageInputStreamSpi());
      this.registerServiceProvider(new FileImageOutputStreamSpi());
      this.registerServiceProvider(new InputStreamImageInputStreamSpi());
      this.registerServiceProvider(new OutputStreamImageOutputStreamSpi());
      this.registerServiceProvider(new RAFImageInputStreamSpi());
      this.registerServiceProvider(new RAFImageOutputStreamSpi());
      this.registerInstalledProviders();
   }

   public void registerApplicationClasspathSpis() {
      ClassLoader var1 = Thread.currentThread().getContextClassLoader();
      Iterator var2 = this.getCategories();

      while(var2.hasNext()) {
         Class var3 = (Class)var2.next();
         Iterator var4 = ServiceLoader.load(var3, var1).iterator();

         while(var4.hasNext()) {
            try {
               IIOServiceProvider var5 = (IIOServiceProvider)var4.next();
               this.registerServiceProvider(var5);
            } catch (ServiceConfigurationError var6) {
               if (System.getSecurityManager() == null) {
                  throw var6;
               }

               var6.printStackTrace();
            }
         }
      }

   }

   private void registerInstalledProviders() {
      PrivilegedAction var1 = new PrivilegedAction() {
         public Object run() {
            Iterator var1 = IIORegistry.this.getCategories();

            while(var1.hasNext()) {
               Class var2 = (Class)var1.next();
               Iterator var3 = ServiceLoader.loadInstalled(var2).iterator();

               while(var3.hasNext()) {
                  IIOServiceProvider var4 = (IIOServiceProvider)var3.next();
                  IIORegistry.this.registerServiceProvider(var4);
               }
            }

            return this;
         }
      };
      AccessController.doPrivileged(var1);
   }

   static {
      initialCategories.add(ImageReaderSpi.class);
      initialCategories.add(ImageWriterSpi.class);
      initialCategories.add(ImageTranscoderSpi.class);
      initialCategories.add(ImageInputStreamSpi.class);
      initialCategories.add(ImageOutputStreamSpi.class);
   }
}
