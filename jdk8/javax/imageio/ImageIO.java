package javax.imageio;

import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FilePermission;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import javax.imageio.spi.IIORegistry;
import javax.imageio.spi.ImageInputStreamSpi;
import javax.imageio.spi.ImageOutputStreamSpi;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.spi.ImageReaderWriterSpi;
import javax.imageio.spi.ImageTranscoderSpi;
import javax.imageio.spi.ImageWriterSpi;
import javax.imageio.spi.ServiceRegistry;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;
import sun.awt.AppContext;
import sun.security.action.GetPropertyAction;

public final class ImageIO {
   private static final IIORegistry theRegistry = IIORegistry.getDefaultInstance();
   private static Method readerFormatNamesMethod;
   private static Method readerFileSuffixesMethod;
   private static Method readerMIMETypesMethod;
   private static Method writerFormatNamesMethod;
   private static Method writerFileSuffixesMethod;
   private static Method writerMIMETypesMethod;

   private ImageIO() {
   }

   public static void scanForPlugins() {
      theRegistry.registerApplicationClasspathSpis();
   }

   private static synchronized ImageIO.CacheInfo getCacheInfo() {
      AppContext var0 = AppContext.getAppContext();
      ImageIO.CacheInfo var1 = (ImageIO.CacheInfo)var0.get(ImageIO.CacheInfo.class);
      if (var1 == null) {
         var1 = new ImageIO.CacheInfo();
         var0.put(ImageIO.CacheInfo.class, var1);
      }

      return var1;
   }

   private static String getTempDir() {
      GetPropertyAction var0 = new GetPropertyAction("java.io.tmpdir");
      return (String)AccessController.doPrivileged((PrivilegedAction)var0);
   }

   private static boolean hasCachePermission() {
      Boolean var0 = getCacheInfo().getHasPermission();
      if (var0 != null) {
         return var0;
      } else {
         try {
            SecurityManager var1 = System.getSecurityManager();
            if (var1 != null) {
               File var2 = getCacheDirectory();
               String var3;
               if (var2 != null) {
                  var3 = var2.getPath();
               } else {
                  var3 = getTempDir();
                  if (var3 == null || var3.isEmpty()) {
                     getCacheInfo().setHasPermission(Boolean.FALSE);
                     return false;
                  }
               }

               String var4 = var3;
               if (!var3.endsWith(File.separator)) {
                  var4 = var3 + File.separator;
               }

               var4 = var4 + "*";
               var1.checkPermission(new FilePermission(var4, "read, write, delete"));
            }
         } catch (SecurityException var5) {
            getCacheInfo().setHasPermission(Boolean.FALSE);
            return false;
         }

         getCacheInfo().setHasPermission(Boolean.TRUE);
         return true;
      }
   }

   public static void setUseCache(boolean var0) {
      getCacheInfo().setUseCache(var0);
   }

   public static boolean getUseCache() {
      return getCacheInfo().getUseCache();
   }

   public static void setCacheDirectory(File var0) {
      if (var0 != null && !var0.isDirectory()) {
         throw new IllegalArgumentException("Not a directory!");
      } else {
         getCacheInfo().setCacheDirectory(var0);
         getCacheInfo().setHasPermission((Boolean)null);
      }
   }

   public static File getCacheDirectory() {
      return getCacheInfo().getCacheDirectory();
   }

   public static ImageInputStream createImageInputStream(Object var0) throws IOException {
      if (var0 == null) {
         throw new IllegalArgumentException("input == null!");
      } else {
         Iterator var1;
         try {
            var1 = theRegistry.getServiceProviders(ImageInputStreamSpi.class, true);
         } catch (IllegalArgumentException var6) {
            return null;
         }

         boolean var2 = getUseCache() && hasCachePermission();

         ImageInputStreamSpi var3;
         do {
            if (!var1.hasNext()) {
               return null;
            }

            var3 = (ImageInputStreamSpi)var1.next();
         } while(!var3.getInputClass().isInstance(var0));

         try {
            return var3.createInputStreamInstance(var0, var2, getCacheDirectory());
         } catch (IOException var5) {
            throw new IIOException("Can't create cache file!", var5);
         }
      }
   }

   public static ImageOutputStream createImageOutputStream(Object var0) throws IOException {
      if (var0 == null) {
         throw new IllegalArgumentException("output == null!");
      } else {
         Iterator var1;
         try {
            var1 = theRegistry.getServiceProviders(ImageOutputStreamSpi.class, true);
         } catch (IllegalArgumentException var6) {
            return null;
         }

         boolean var2 = getUseCache() && hasCachePermission();

         ImageOutputStreamSpi var3;
         do {
            if (!var1.hasNext()) {
               return null;
            }

            var3 = (ImageOutputStreamSpi)var1.next();
         } while(!var3.getOutputClass().isInstance(var0));

         try {
            return var3.createOutputStreamInstance(var0, var2, getCacheDirectory());
         } catch (IOException var5) {
            throw new IIOException("Can't create cache file!", var5);
         }
      }
   }

   private static <S extends ImageReaderWriterSpi> String[] getReaderWriterInfo(Class<S> var0, ImageIO.SpiInfo var1) {
      Iterator var2;
      try {
         var2 = theRegistry.getServiceProviders(var0, true);
      } catch (IllegalArgumentException var5) {
         return new String[0];
      }

      HashSet var3 = new HashSet();

      while(var2.hasNext()) {
         ImageReaderWriterSpi var4 = (ImageReaderWriterSpi)var2.next();
         Collections.addAll(var3, var1.info(var4));
      }

      return (String[])var3.toArray(new String[var3.size()]);
   }

   public static String[] getReaderFormatNames() {
      return getReaderWriterInfo(ImageReaderSpi.class, ImageIO.SpiInfo.FORMAT_NAMES);
   }

   public static String[] getReaderMIMETypes() {
      return getReaderWriterInfo(ImageReaderSpi.class, ImageIO.SpiInfo.MIME_TYPES);
   }

   public static String[] getReaderFileSuffixes() {
      return getReaderWriterInfo(ImageReaderSpi.class, ImageIO.SpiInfo.FILE_SUFFIXES);
   }

   public static Iterator<ImageReader> getImageReaders(Object var0) {
      if (var0 == null) {
         throw new IllegalArgumentException("input == null!");
      } else {
         Iterator var1;
         try {
            var1 = theRegistry.getServiceProviders(ImageReaderSpi.class, new ImageIO.CanDecodeInputFilter(var0), true);
         } catch (IllegalArgumentException var3) {
            return Collections.emptyIterator();
         }

         return new ImageIO.ImageReaderIterator(var1);
      }
   }

   public static Iterator<ImageReader> getImageReadersByFormatName(String var0) {
      if (var0 == null) {
         throw new IllegalArgumentException("formatName == null!");
      } else {
         Iterator var1;
         try {
            var1 = theRegistry.getServiceProviders(ImageReaderSpi.class, new ImageIO.ContainsFilter(readerFormatNamesMethod, var0), true);
         } catch (IllegalArgumentException var3) {
            return Collections.emptyIterator();
         }

         return new ImageIO.ImageReaderIterator(var1);
      }
   }

   public static Iterator<ImageReader> getImageReadersBySuffix(String var0) {
      if (var0 == null) {
         throw new IllegalArgumentException("fileSuffix == null!");
      } else {
         Iterator var1;
         try {
            var1 = theRegistry.getServiceProviders(ImageReaderSpi.class, new ImageIO.ContainsFilter(readerFileSuffixesMethod, var0), true);
         } catch (IllegalArgumentException var3) {
            return Collections.emptyIterator();
         }

         return new ImageIO.ImageReaderIterator(var1);
      }
   }

   public static Iterator<ImageReader> getImageReadersByMIMEType(String var0) {
      if (var0 == null) {
         throw new IllegalArgumentException("MIMEType == null!");
      } else {
         Iterator var1;
         try {
            var1 = theRegistry.getServiceProviders(ImageReaderSpi.class, new ImageIO.ContainsFilter(readerMIMETypesMethod, var0), true);
         } catch (IllegalArgumentException var3) {
            return Collections.emptyIterator();
         }

         return new ImageIO.ImageReaderIterator(var1);
      }
   }

   public static String[] getWriterFormatNames() {
      return getReaderWriterInfo(ImageWriterSpi.class, ImageIO.SpiInfo.FORMAT_NAMES);
   }

   public static String[] getWriterMIMETypes() {
      return getReaderWriterInfo(ImageWriterSpi.class, ImageIO.SpiInfo.MIME_TYPES);
   }

   public static String[] getWriterFileSuffixes() {
      return getReaderWriterInfo(ImageWriterSpi.class, ImageIO.SpiInfo.FILE_SUFFIXES);
   }

   private static boolean contains(String[] var0, String var1) {
      for(int var2 = 0; var2 < var0.length; ++var2) {
         if (var1.equalsIgnoreCase(var0[var2])) {
            return true;
         }
      }

      return false;
   }

   public static Iterator<ImageWriter> getImageWritersByFormatName(String var0) {
      if (var0 == null) {
         throw new IllegalArgumentException("formatName == null!");
      } else {
         Iterator var1;
         try {
            var1 = theRegistry.getServiceProviders(ImageWriterSpi.class, new ImageIO.ContainsFilter(writerFormatNamesMethod, var0), true);
         } catch (IllegalArgumentException var3) {
            return Collections.emptyIterator();
         }

         return new ImageIO.ImageWriterIterator(var1);
      }
   }

   public static Iterator<ImageWriter> getImageWritersBySuffix(String var0) {
      if (var0 == null) {
         throw new IllegalArgumentException("fileSuffix == null!");
      } else {
         Iterator var1;
         try {
            var1 = theRegistry.getServiceProviders(ImageWriterSpi.class, new ImageIO.ContainsFilter(writerFileSuffixesMethod, var0), true);
         } catch (IllegalArgumentException var3) {
            return Collections.emptyIterator();
         }

         return new ImageIO.ImageWriterIterator(var1);
      }
   }

   public static Iterator<ImageWriter> getImageWritersByMIMEType(String var0) {
      if (var0 == null) {
         throw new IllegalArgumentException("MIMEType == null!");
      } else {
         Iterator var1;
         try {
            var1 = theRegistry.getServiceProviders(ImageWriterSpi.class, new ImageIO.ContainsFilter(writerMIMETypesMethod, var0), true);
         } catch (IllegalArgumentException var3) {
            return Collections.emptyIterator();
         }

         return new ImageIO.ImageWriterIterator(var1);
      }
   }

   public static ImageWriter getImageWriter(ImageReader var0) {
      if (var0 == null) {
         throw new IllegalArgumentException("reader == null!");
      } else {
         ImageReaderSpi var1 = var0.getOriginatingProvider();
         ImageReaderSpi var3;
         if (var1 == null) {
            Iterator var2;
            try {
               var2 = theRegistry.getServiceProviders(ImageReaderSpi.class, false);
            } catch (IllegalArgumentException var8) {
               return null;
            }

            while(var2.hasNext()) {
               var3 = (ImageReaderSpi)var2.next();
               if (var3.isOwnReader(var0)) {
                  var1 = var3;
                  break;
               }
            }

            if (var1 == null) {
               return null;
            }
         }

         String[] var9 = var1.getImageWriterSpiNames();
         if (var9 == null) {
            return null;
         } else {
            var3 = null;

            Class var10;
            try {
               var10 = Class.forName(var9[0], true, ClassLoader.getSystemClassLoader());
            } catch (ClassNotFoundException var7) {
               return null;
            }

            ImageWriterSpi var4 = (ImageWriterSpi)theRegistry.getServiceProviderByClass(var10);
            if (var4 == null) {
               return null;
            } else {
               try {
                  return var4.createWriterInstance();
               } catch (IOException var6) {
                  theRegistry.deregisterServiceProvider(var4, ImageWriterSpi.class);
                  return null;
               }
            }
         }
      }
   }

   public static ImageReader getImageReader(ImageWriter var0) {
      if (var0 == null) {
         throw new IllegalArgumentException("writer == null!");
      } else {
         ImageWriterSpi var1 = var0.getOriginatingProvider();
         ImageWriterSpi var3;
         if (var1 == null) {
            Iterator var2;
            try {
               var2 = theRegistry.getServiceProviders(ImageWriterSpi.class, false);
            } catch (IllegalArgumentException var8) {
               return null;
            }

            while(var2.hasNext()) {
               var3 = (ImageWriterSpi)var2.next();
               if (var3.isOwnWriter(var0)) {
                  var1 = var3;
                  break;
               }
            }

            if (var1 == null) {
               return null;
            }
         }

         String[] var9 = var1.getImageReaderSpiNames();
         if (var9 == null) {
            return null;
         } else {
            var3 = null;

            Class var10;
            try {
               var10 = Class.forName(var9[0], true, ClassLoader.getSystemClassLoader());
            } catch (ClassNotFoundException var7) {
               return null;
            }

            ImageReaderSpi var4 = (ImageReaderSpi)theRegistry.getServiceProviderByClass(var10);
            if (var4 == null) {
               return null;
            } else {
               try {
                  return var4.createReaderInstance();
               } catch (IOException var6) {
                  theRegistry.deregisterServiceProvider(var4, ImageReaderSpi.class);
                  return null;
               }
            }
         }
      }
   }

   public static Iterator<ImageWriter> getImageWriters(ImageTypeSpecifier var0, String var1) {
      if (var0 == null) {
         throw new IllegalArgumentException("type == null!");
      } else if (var1 == null) {
         throw new IllegalArgumentException("formatName == null!");
      } else {
         Iterator var2;
         try {
            var2 = theRegistry.getServiceProviders(ImageWriterSpi.class, new ImageIO.CanEncodeImageAndFormatFilter(var0, var1), true);
         } catch (IllegalArgumentException var4) {
            return Collections.emptyIterator();
         }

         return new ImageIO.ImageWriterIterator(var2);
      }
   }

   public static Iterator<ImageTranscoder> getImageTranscoders(ImageReader var0, ImageWriter var1) {
      if (var0 == null) {
         throw new IllegalArgumentException("reader == null!");
      } else if (var1 == null) {
         throw new IllegalArgumentException("writer == null!");
      } else {
         ImageReaderSpi var2 = var0.getOriginatingProvider();
         ImageWriterSpi var3 = var1.getOriginatingProvider();
         ImageIO.TranscoderFilter var4 = new ImageIO.TranscoderFilter(var2, var3);

         Iterator var5;
         try {
            var5 = theRegistry.getServiceProviders(ImageTranscoderSpi.class, var4, true);
         } catch (IllegalArgumentException var7) {
            return Collections.emptyIterator();
         }

         return new ImageIO.ImageTranscoderIterator(var5);
      }
   }

   public static BufferedImage read(File var0) throws IOException {
      if (var0 == null) {
         throw new IllegalArgumentException("input == null!");
      } else if (!var0.canRead()) {
         throw new IIOException("Can't read input file!");
      } else {
         ImageInputStream var1 = createImageInputStream(var0);
         if (var1 == null) {
            throw new IIOException("Can't create an ImageInputStream!");
         } else {
            BufferedImage var2 = read(var1);
            if (var2 == null) {
               var1.close();
            }

            return var2;
         }
      }
   }

   public static BufferedImage read(InputStream var0) throws IOException {
      if (var0 == null) {
         throw new IllegalArgumentException("input == null!");
      } else {
         ImageInputStream var1 = createImageInputStream(var0);
         BufferedImage var2 = read(var1);
         if (var2 == null) {
            var1.close();
         }

         return var2;
      }
   }

   public static BufferedImage read(URL var0) throws IOException {
      if (var0 == null) {
         throw new IllegalArgumentException("input == null!");
      } else {
         InputStream var1 = null;

         try {
            var1 = var0.openStream();
         } catch (IOException var7) {
            throw new IIOException("Can't get input stream from URL!", var7);
         }

         ImageInputStream var2 = createImageInputStream(var1);

         BufferedImage var3;
         try {
            var3 = read(var2);
            if (var3 == null) {
               var2.close();
            }
         } finally {
            var1.close();
         }

         return var3;
      }
   }

   public static BufferedImage read(ImageInputStream var0) throws IOException {
      if (var0 == null) {
         throw new IllegalArgumentException("stream == null!");
      } else {
         Iterator var1 = getImageReaders(var0);
         if (!var1.hasNext()) {
            return null;
         } else {
            ImageReader var2 = (ImageReader)var1.next();
            ImageReadParam var3 = var2.getDefaultReadParam();
            var2.setInput(var0, true, true);

            BufferedImage var4;
            try {
               var4 = var2.read(0, var3);
            } finally {
               var2.dispose();
               var0.close();
            }

            return var4;
         }
      }
   }

   public static boolean write(RenderedImage var0, String var1, ImageOutputStream var2) throws IOException {
      if (var0 == null) {
         throw new IllegalArgumentException("im == null!");
      } else if (var1 == null) {
         throw new IllegalArgumentException("formatName == null!");
      } else if (var2 == null) {
         throw new IllegalArgumentException("output == null!");
      } else {
         return doWrite(var0, getWriter(var0, var1), var2);
      }
   }

   public static boolean write(RenderedImage var0, String var1, File var2) throws IOException {
      if (var2 == null) {
         throw new IllegalArgumentException("output == null!");
      } else {
         ImageOutputStream var3 = null;
         ImageWriter var4 = getWriter(var0, var1);
         if (var4 == null) {
            return false;
         } else {
            try {
               var2.delete();
               var3 = createImageOutputStream(var2);
            } catch (IOException var10) {
               throw new IIOException("Can't create output stream!", var10);
            }

            boolean var5;
            try {
               var5 = doWrite(var0, var4, var3);
            } finally {
               var3.close();
            }

            return var5;
         }
      }
   }

   public static boolean write(RenderedImage var0, String var1, OutputStream var2) throws IOException {
      if (var2 == null) {
         throw new IllegalArgumentException("output == null!");
      } else {
         ImageOutputStream var3 = null;

         try {
            var3 = createImageOutputStream(var2);
         } catch (IOException var9) {
            throw new IIOException("Can't create output stream!", var9);
         }

         boolean var4;
         try {
            var4 = doWrite(var0, getWriter(var0, var1), var3);
         } finally {
            var3.close();
         }

         return var4;
      }
   }

   private static ImageWriter getWriter(RenderedImage var0, String var1) {
      ImageTypeSpecifier var2 = ImageTypeSpecifier.createFromRenderedImage(var0);
      Iterator var3 = getImageWriters(var2, var1);
      return var3.hasNext() ? (ImageWriter)var3.next() : null;
   }

   private static boolean doWrite(RenderedImage var0, ImageWriter var1, ImageOutputStream var2) throws IOException {
      if (var1 == null) {
         return false;
      } else {
         var1.setOutput(var2);

         try {
            var1.write(var0);
         } finally {
            var1.dispose();
            var2.flush();
         }

         return true;
      }
   }

   static {
      try {
         readerFormatNamesMethod = ImageReaderSpi.class.getMethod("getFormatNames");
         readerFileSuffixesMethod = ImageReaderSpi.class.getMethod("getFileSuffixes");
         readerMIMETypesMethod = ImageReaderSpi.class.getMethod("getMIMETypes");
         writerFormatNamesMethod = ImageWriterSpi.class.getMethod("getFormatNames");
         writerFileSuffixesMethod = ImageWriterSpi.class.getMethod("getFileSuffixes");
         writerMIMETypesMethod = ImageWriterSpi.class.getMethod("getMIMETypes");
      } catch (NoSuchMethodException var1) {
         var1.printStackTrace();
      }

   }

   static class TranscoderFilter implements ServiceRegistry.Filter {
      String readerSpiName;
      String writerSpiName;

      public TranscoderFilter(ImageReaderSpi var1, ImageWriterSpi var2) {
         this.readerSpiName = var1.getClass().getName();
         this.writerSpiName = var2.getClass().getName();
      }

      public boolean filter(Object var1) {
         ImageTranscoderSpi var2 = (ImageTranscoderSpi)var1;
         String var3 = var2.getReaderServiceProviderName();
         String var4 = var2.getWriterServiceProviderName();
         return var3.equals(this.readerSpiName) && var4.equals(this.writerSpiName);
      }
   }

   static class ImageTranscoderIterator implements Iterator<ImageTranscoder> {
      public Iterator iter;

      public ImageTranscoderIterator(Iterator var1) {
         this.iter = var1;
      }

      public boolean hasNext() {
         return this.iter.hasNext();
      }

      public ImageTranscoder next() {
         ImageTranscoderSpi var1 = null;
         var1 = (ImageTranscoderSpi)this.iter.next();
         return var1.createTranscoderInstance();
      }

      public void remove() {
         throw new UnsupportedOperationException();
      }
   }

   static class ImageWriterIterator implements Iterator<ImageWriter> {
      public Iterator iter;

      public ImageWriterIterator(Iterator var1) {
         this.iter = var1;
      }

      public boolean hasNext() {
         return this.iter.hasNext();
      }

      public ImageWriter next() {
         ImageWriterSpi var1 = null;

         try {
            var1 = (ImageWriterSpi)this.iter.next();
            return var1.createWriterInstance();
         } catch (IOException var3) {
            ImageIO.theRegistry.deregisterServiceProvider(var1, ImageWriterSpi.class);
            return null;
         }
      }

      public void remove() {
         throw new UnsupportedOperationException();
      }
   }

   static class ContainsFilter implements ServiceRegistry.Filter {
      Method method;
      String name;

      public ContainsFilter(Method var1, String var2) {
         this.method = var1;
         this.name = var2;
      }

      public boolean filter(Object var1) {
         try {
            return ImageIO.contains((String[])((String[])this.method.invoke(var1)), this.name);
         } catch (Exception var3) {
            return false;
         }
      }
   }

   static class CanEncodeImageAndFormatFilter implements ServiceRegistry.Filter {
      ImageTypeSpecifier type;
      String formatName;

      public CanEncodeImageAndFormatFilter(ImageTypeSpecifier var1, String var2) {
         this.type = var1;
         this.formatName = var2;
      }

      public boolean filter(Object var1) {
         ImageWriterSpi var2 = (ImageWriterSpi)var1;
         return Arrays.asList(var2.getFormatNames()).contains(this.formatName) && var2.canEncodeImage(this.type);
      }
   }

   static class CanDecodeInputFilter implements ServiceRegistry.Filter {
      Object input;

      public CanDecodeInputFilter(Object var1) {
         this.input = var1;
      }

      public boolean filter(Object var1) {
         try {
            ImageReaderSpi var2 = (ImageReaderSpi)var1;
            ImageInputStream var3 = null;
            if (this.input instanceof ImageInputStream) {
               var3 = (ImageInputStream)this.input;
            }

            boolean var4 = false;
            if (var3 != null) {
               var3.mark();
            }

            var4 = var2.canDecodeInput(this.input);
            if (var3 != null) {
               var3.reset();
            }

            return var4;
         } catch (IOException var5) {
            return false;
         }
      }
   }

   static class ImageReaderIterator implements Iterator<ImageReader> {
      public Iterator iter;

      public ImageReaderIterator(Iterator var1) {
         this.iter = var1;
      }

      public boolean hasNext() {
         return this.iter.hasNext();
      }

      public ImageReader next() {
         ImageReaderSpi var1 = null;

         try {
            var1 = (ImageReaderSpi)this.iter.next();
            return var1.createReaderInstance();
         } catch (IOException var3) {
            ImageIO.theRegistry.deregisterServiceProvider(var1, ImageReaderSpi.class);
            return null;
         }
      }

      public void remove() {
         throw new UnsupportedOperationException();
      }
   }

   private static enum SpiInfo {
      FORMAT_NAMES {
         String[] info(ImageReaderWriterSpi var1) {
            return var1.getFormatNames();
         }
      },
      MIME_TYPES {
         String[] info(ImageReaderWriterSpi var1) {
            return var1.getMIMETypes();
         }
      },
      FILE_SUFFIXES {
         String[] info(ImageReaderWriterSpi var1) {
            return var1.getFileSuffixes();
         }
      };

      private SpiInfo() {
      }

      abstract String[] info(ImageReaderWriterSpi var1);

      // $FF: synthetic method
      SpiInfo(Object var3) {
         this();
      }
   }

   static class CacheInfo {
      boolean useCache = true;
      File cacheDirectory = null;
      Boolean hasPermission = null;

      public CacheInfo() {
      }

      public boolean getUseCache() {
         return this.useCache;
      }

      public void setUseCache(boolean var1) {
         this.useCache = var1;
      }

      public File getCacheDirectory() {
         return this.cacheDirectory;
      }

      public void setCacheDirectory(File var1) {
         this.cacheDirectory = var1;
      }

      public Boolean getHasPermission() {
         return this.hasPermission;
      }

      public void setHasPermission(Boolean var1) {
         this.hasPermission = var1;
      }
   }
}
