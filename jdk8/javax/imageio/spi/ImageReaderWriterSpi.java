package javax.imageio.spi;

import java.lang.reflect.Method;
import javax.imageio.metadata.IIOMetadataFormat;
import javax.imageio.metadata.IIOMetadataFormatImpl;

public abstract class ImageReaderWriterSpi extends IIOServiceProvider {
   protected String[] names = null;
   protected String[] suffixes = null;
   protected String[] MIMETypes = null;
   protected String pluginClassName = null;
   protected boolean supportsStandardStreamMetadataFormat = false;
   protected String nativeStreamMetadataFormatName = null;
   protected String nativeStreamMetadataFormatClassName = null;
   protected String[] extraStreamMetadataFormatNames = null;
   protected String[] extraStreamMetadataFormatClassNames = null;
   protected boolean supportsStandardImageMetadataFormat = false;
   protected String nativeImageMetadataFormatName = null;
   protected String nativeImageMetadataFormatClassName = null;
   protected String[] extraImageMetadataFormatNames = null;
   protected String[] extraImageMetadataFormatClassNames = null;

   public ImageReaderWriterSpi(String var1, String var2, String[] var3, String[] var4, String[] var5, String var6, boolean var7, String var8, String var9, String[] var10, String[] var11, boolean var12, String var13, String var14, String[] var15, String[] var16) {
      super(var1, var2);
      if (var3 == null) {
         throw new IllegalArgumentException("names == null!");
      } else if (var3.length == 0) {
         throw new IllegalArgumentException("names.length == 0!");
      } else if (var6 == null) {
         throw new IllegalArgumentException("pluginClassName == null!");
      } else {
         this.names = (String[])((String[])var3.clone());
         if (var4 != null && var4.length > 0) {
            this.suffixes = (String[])((String[])var4.clone());
         }

         if (var5 != null && var5.length > 0) {
            this.MIMETypes = (String[])((String[])var5.clone());
         }

         this.pluginClassName = var6;
         this.supportsStandardStreamMetadataFormat = var7;
         this.nativeStreamMetadataFormatName = var8;
         this.nativeStreamMetadataFormatClassName = var9;
         if (var10 != null && var10.length > 0) {
            this.extraStreamMetadataFormatNames = (String[])((String[])var10.clone());
         }

         if (var11 != null && var11.length > 0) {
            this.extraStreamMetadataFormatClassNames = (String[])((String[])var11.clone());
         }

         this.supportsStandardImageMetadataFormat = var12;
         this.nativeImageMetadataFormatName = var13;
         this.nativeImageMetadataFormatClassName = var14;
         if (var15 != null && var15.length > 0) {
            this.extraImageMetadataFormatNames = (String[])((String[])var15.clone());
         }

         if (var16 != null && var16.length > 0) {
            this.extraImageMetadataFormatClassNames = (String[])((String[])var16.clone());
         }

      }
   }

   public ImageReaderWriterSpi() {
   }

   public String[] getFormatNames() {
      return (String[])((String[])this.names.clone());
   }

   public String[] getFileSuffixes() {
      return this.suffixes == null ? null : (String[])((String[])this.suffixes.clone());
   }

   public String[] getMIMETypes() {
      return this.MIMETypes == null ? null : (String[])((String[])this.MIMETypes.clone());
   }

   public String getPluginClassName() {
      return this.pluginClassName;
   }

   public boolean isStandardStreamMetadataFormatSupported() {
      return this.supportsStandardStreamMetadataFormat;
   }

   public String getNativeStreamMetadataFormatName() {
      return this.nativeStreamMetadataFormatName;
   }

   public String[] getExtraStreamMetadataFormatNames() {
      return this.extraStreamMetadataFormatNames == null ? null : (String[])((String[])this.extraStreamMetadataFormatNames.clone());
   }

   public boolean isStandardImageMetadataFormatSupported() {
      return this.supportsStandardImageMetadataFormat;
   }

   public String getNativeImageMetadataFormatName() {
      return this.nativeImageMetadataFormatName;
   }

   public String[] getExtraImageMetadataFormatNames() {
      return this.extraImageMetadataFormatNames == null ? null : (String[])((String[])this.extraImageMetadataFormatNames.clone());
   }

   public IIOMetadataFormat getStreamMetadataFormat(String var1) {
      return this.getMetadataFormat(var1, this.supportsStandardStreamMetadataFormat, this.nativeStreamMetadataFormatName, this.nativeStreamMetadataFormatClassName, this.extraStreamMetadataFormatNames, this.extraStreamMetadataFormatClassNames);
   }

   public IIOMetadataFormat getImageMetadataFormat(String var1) {
      return this.getMetadataFormat(var1, this.supportsStandardImageMetadataFormat, this.nativeImageMetadataFormatName, this.nativeImageMetadataFormatClassName, this.extraImageMetadataFormatNames, this.extraImageMetadataFormatClassNames);
   }

   private IIOMetadataFormat getMetadataFormat(String var1, boolean var2, String var3, String var4, String[] var5, String[] var6) {
      if (var1 == null) {
         throw new IllegalArgumentException("formatName == null!");
      } else if (var2 && var1.equals("javax_imageio_1.0")) {
         return IIOMetadataFormatImpl.getStandardFormatInstance();
      } else {
         String var7 = null;
         if (var1.equals(var3)) {
            var7 = var4;
         } else if (var5 != null) {
            for(int var8 = 0; var8 < var5.length; ++var8) {
               if (var1.equals(var5[var8])) {
                  var7 = var6[var8];
                  break;
               }
            }
         }

         if (var7 == null) {
            throw new IllegalArgumentException("Unsupported format name");
         } else {
            try {
               Class var11 = Class.forName(var7, true, ClassLoader.getSystemClassLoader());
               Method var12 = var11.getMethod("getInstance");
               return (IIOMetadataFormat)var12.invoke((Object)null);
            } catch (Exception var10) {
               IllegalStateException var9 = new IllegalStateException("Can't obtain format");
               var9.initCause(var10);
               throw var9;
            }
         }
      }
   }
}
