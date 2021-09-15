package sun.awt.datatransfer;

import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.FlavorMap;
import java.awt.datatransfer.FlavorTable;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ImageObserver;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FilePermission;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Reader;
import java.io.SequenceInputStream;
import java.io.Serializable;
import java.io.StringReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.Stack;
import java.util.TreeMap;
import java.util.TreeSet;
import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriter;
import javax.imageio.spi.ImageWriterSpi;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;
import sun.awt.AppContext;
import sun.awt.ComponentFactory;
import sun.awt.SunToolkit;
import sun.awt.image.ImageRepresentation;
import sun.awt.image.ToolkitImage;
import sun.util.logging.PlatformLogger;

public abstract class DataTransferer {
   public static final DataFlavor plainTextStringFlavor;
   public static final DataFlavor javaTextEncodingFlavor;
   private static final Map textMIMESubtypeCharsetSupport;
   private static String defaultEncoding;
   private static final Set textNatives = Collections.synchronizedSet(new HashSet());
   private static final Map nativeCharsets = Collections.synchronizedMap(new HashMap());
   private static final Map nativeEOLNs = Collections.synchronizedMap(new HashMap());
   private static final Map nativeTerminators = Collections.synchronizedMap(new HashMap());
   private static final String DATA_CONVERTER_KEY = "DATA_CONVERTER_KEY";
   private static DataTransferer transferer;
   private static final PlatformLogger dtLog = PlatformLogger.getLogger("sun.awt.datatransfer.DataTransfer");
   private static final String[] DEPLOYMENT_CACHE_PROPERTIES;
   private static final ArrayList<File> deploymentCacheDirectoryList;

   public static synchronized DataTransferer getInstance() {
      return ((ComponentFactory)Toolkit.getDefaultToolkit()).getDataTransferer();
   }

   public static String canonicalName(String var0) {
      if (var0 == null) {
         return null;
      } else {
         try {
            return Charset.forName(var0).name();
         } catch (IllegalCharsetNameException var2) {
            return var0;
         } catch (UnsupportedCharsetException var3) {
            return var0;
         }
      }
   }

   public static String getTextCharset(DataFlavor var0) {
      if (!isFlavorCharsetTextType(var0)) {
         return null;
      } else {
         String var1 = var0.getParameter("charset");
         return var1 != null ? var1 : getDefaultTextCharset();
      }
   }

   public static String getDefaultTextCharset() {
      return defaultEncoding != null ? defaultEncoding : (defaultEncoding = Charset.defaultCharset().name());
   }

   public static boolean doesSubtypeSupportCharset(DataFlavor var0) {
      if (dtLog.isLoggable(PlatformLogger.Level.FINE) && !"text".equals(var0.getPrimaryType())) {
         dtLog.fine("Assertion (\"text\".equals(flavor.getPrimaryType())) failed");
      }

      String var1 = var0.getSubType();
      if (var1 == null) {
         return false;
      } else {
         Object var2 = textMIMESubtypeCharsetSupport.get(var1);
         if (var2 != null) {
            return var2 == Boolean.TRUE;
         } else {
            boolean var3 = var0.getParameter("charset") != null;
            textMIMESubtypeCharsetSupport.put(var1, var3 ? Boolean.TRUE : Boolean.FALSE);
            return var3;
         }
      }
   }

   public static boolean doesSubtypeSupportCharset(String var0, String var1) {
      Object var2 = textMIMESubtypeCharsetSupport.get(var0);
      if (var2 != null) {
         return var2 == Boolean.TRUE;
      } else {
         boolean var3 = var1 != null;
         textMIMESubtypeCharsetSupport.put(var0, var3 ? Boolean.TRUE : Boolean.FALSE);
         return var3;
      }
   }

   public static boolean isFlavorCharsetTextType(DataFlavor var0) {
      if (DataFlavor.stringFlavor.equals(var0)) {
         return true;
      } else if ("text".equals(var0.getPrimaryType()) && doesSubtypeSupportCharset(var0)) {
         Class var1 = var0.getRepresentationClass();
         if (!var0.isRepresentationClassReader() && !String.class.equals(var1) && !var0.isRepresentationClassCharBuffer() && !char[].class.equals(var1)) {
            if (!var0.isRepresentationClassInputStream() && !var0.isRepresentationClassByteBuffer() && !byte[].class.equals(var1)) {
               return false;
            } else {
               String var2 = var0.getParameter("charset");
               return var2 != null ? isEncodingSupported(var2) : true;
            }
         } else {
            return true;
         }
      } else {
         return false;
      }
   }

   public static boolean isFlavorNoncharsetTextType(DataFlavor var0) {
      if ("text".equals(var0.getPrimaryType()) && !doesSubtypeSupportCharset(var0)) {
         return var0.isRepresentationClassInputStream() || var0.isRepresentationClassByteBuffer() || byte[].class.equals(var0.getRepresentationClass());
      } else {
         return false;
      }
   }

   public static boolean isEncodingSupported(String var0) {
      if (var0 == null) {
         return false;
      } else {
         try {
            return Charset.isSupported(var0);
         } catch (IllegalCharsetNameException var2) {
            return false;
         }
      }
   }

   public static boolean isRemote(Class<?> var0) {
      return DataTransferer.RMI.isRemote(var0);
   }

   public static Set<String> standardEncodings() {
      return DataTransferer.StandardEncodingsHolder.standardEncodings;
   }

   public static FlavorTable adaptFlavorMap(final FlavorMap var0) {
      return var0 instanceof FlavorTable ? (FlavorTable)var0 : new FlavorTable() {
         public Map getNativesForFlavors(DataFlavor[] var1) {
            return var0.getNativesForFlavors(var1);
         }

         public Map getFlavorsForNatives(String[] var1) {
            return var0.getFlavorsForNatives(var1);
         }

         public List getNativesForFlavor(DataFlavor var1) {
            Map var2 = this.getNativesForFlavors(new DataFlavor[]{var1});
            String var3 = (String)var2.get(var1);
            if (var3 != null) {
               ArrayList var4 = new ArrayList(1);
               var4.add(var3);
               return var4;
            } else {
               return Collections.EMPTY_LIST;
            }
         }

         public List getFlavorsForNative(String var1) {
            Map var2 = this.getFlavorsForNatives(new String[]{var1});
            DataFlavor var3 = (DataFlavor)var2.get(var1);
            if (var3 != null) {
               ArrayList var4 = new ArrayList(1);
               var4.add(var3);
               return var4;
            } else {
               return Collections.EMPTY_LIST;
            }
         }
      };
   }

   public abstract String getDefaultUnicodeEncoding();

   public void registerTextFlavorProperties(String var1, String var2, String var3, String var4) {
      Long var5 = this.getFormatForNativeAsLong(var1);
      textNatives.add(var5);
      nativeCharsets.put(var5, var2 != null && var2.length() != 0 ? var2 : getDefaultTextCharset());
      if (var3 != null && var3.length() != 0 && !var3.equals("\n")) {
         nativeEOLNs.put(var5, var3);
      }

      if (var4 != null && var4.length() != 0) {
         Integer var6 = Integer.valueOf(var4);
         if (var6 > 0) {
            nativeTerminators.put(var5, var6);
         }
      }

   }

   protected boolean isTextFormat(long var1) {
      return textNatives.contains(var1);
   }

   protected String getCharsetForTextFormat(Long var1) {
      return (String)nativeCharsets.get(var1);
   }

   public abstract boolean isLocaleDependentTextFormat(long var1);

   public abstract boolean isFileFormat(long var1);

   public abstract boolean isImageFormat(long var1);

   protected boolean isURIListFormat(long var1) {
      return false;
   }

   public SortedMap<Long, DataFlavor> getFormatsForTransferable(Transferable var1, FlavorTable var2) {
      DataFlavor[] var3 = var1.getTransferDataFlavors();
      return (SortedMap)(var3 == null ? new TreeMap() : this.getFormatsForFlavors(var3, var2));
   }

   public SortedMap getFormatsForFlavor(DataFlavor var1, FlavorTable var2) {
      return this.getFormatsForFlavors(new DataFlavor[]{var1}, var2);
   }

   public SortedMap<Long, DataFlavor> getFormatsForFlavors(DataFlavor[] var1, FlavorTable var2) {
      HashMap var3 = new HashMap(var1.length);
      HashMap var4 = new HashMap(var1.length);
      HashMap var5 = new HashMap(var1.length);
      HashMap var6 = new HashMap(var1.length);
      int var7 = 0;

      label48:
      for(int var8 = var1.length - 1; var8 >= 0; --var8) {
         DataFlavor var9 = var1[var8];
         if (var9 != null && (var9.isFlavorTextType() || var9.isFlavorJavaFileListType() || DataFlavor.imageFlavor.equals(var9) || var9.isRepresentationClassSerializable() || var9.isRepresentationClassInputStream() || var9.isRepresentationClassRemote())) {
            List var10 = var2.getNativesForFlavor(var9);
            var7 += var10.size();
            Iterator var11 = var10.iterator();

            while(true) {
               Long var12;
               Integer var13;
               do {
                  if (!var11.hasNext()) {
                     var7 += var10.size();
                     continue label48;
                  }

                  var12 = this.getFormatForNativeAsLong((String)var11.next());
                  var13 = var7--;
                  var3.put(var12, var9);
                  var5.put(var12, var13);
               } while((!"text".equals(var9.getPrimaryType()) || !"plain".equals(var9.getSubType())) && !var9.equals(DataFlavor.stringFlavor));

               var4.put(var12, var9);
               var6.put(var12, var13);
            }
         }
      }

      var3.putAll(var4);
      var5.putAll(var6);
      DataTransferer.IndexOrderComparator var14 = new DataTransferer.IndexOrderComparator(var5, false);
      TreeMap var15 = new TreeMap(var14);
      var15.putAll(var3);
      return var15;
   }

   public long[] getFormatsForTransferableAsArray(Transferable var1, FlavorTable var2) {
      return keysToLongArray(this.getFormatsForTransferable(var1, var2));
   }

   public long[] getFormatsForFlavorAsArray(DataFlavor var1, FlavorTable var2) {
      return keysToLongArray(this.getFormatsForFlavor(var1, var2));
   }

   public long[] getFormatsForFlavorsAsArray(DataFlavor[] var1, FlavorTable var2) {
      return keysToLongArray(this.getFormatsForFlavors(var1, var2));
   }

   public Map getFlavorsForFormat(long var1, FlavorTable var3) {
      return this.getFlavorsForFormats(new long[]{var1}, var3);
   }

   public Map getFlavorsForFormats(long[] var1, FlavorTable var2) {
      HashMap var3 = new HashMap(var1.length);
      HashSet var4 = new HashSet(var1.length);
      HashSet var5 = new HashSet(var1.length);

      label60:
      for(int var6 = 0; var6 < var1.length; ++var6) {
         long var7 = var1[var6];
         String var9 = this.getNativeForFormat(var7);
         List var10 = var2.getFlavorsForNative(var9);
         Iterator var11 = var10.iterator();

         while(true) {
            DataFlavor var12;
            do {
               if (!var11.hasNext()) {
                  continue label60;
               }

               var12 = (DataFlavor)var11.next();
            } while(!var12.isFlavorTextType() && !var12.isFlavorJavaFileListType() && !DataFlavor.imageFlavor.equals(var12) && !var12.isRepresentationClassSerializable() && !var12.isRepresentationClassInputStream() && !var12.isRepresentationClassRemote());

            Long var13 = var7;
            Object var14 = createMapping(var13, var12);
            var3.put(var12, var13);
            var4.add(var14);
            var5.add(var12);
         }
      }

      Iterator var15 = var5.iterator();

      while(true) {
         while(var15.hasNext()) {
            DataFlavor var16 = (DataFlavor)var15.next();
            List var8 = var2.getNativesForFlavor(var16);
            Iterator var17 = var8.iterator();

            while(var17.hasNext()) {
               Long var18 = this.getFormatForNativeAsLong((String)var17.next());
               Object var19 = createMapping(var18, var16);
               if (var4.contains(var19)) {
                  var3.put(var16, var18);
                  break;
               }
            }
         }

         return var3;
      }
   }

   public Set getFlavorsForFormatsAsSet(long[] var1, FlavorTable var2) {
      HashSet var3 = new HashSet(var1.length);

      label40:
      for(int var4 = 0; var4 < var1.length; ++var4) {
         String var5 = this.getNativeForFormat(var1[var4]);
         List var6 = var2.getFlavorsForNative(var5);
         Iterator var7 = var6.iterator();

         while(true) {
            DataFlavor var8;
            do {
               if (!var7.hasNext()) {
                  continue label40;
               }

               var8 = (DataFlavor)var7.next();
            } while(!var8.isFlavorTextType() && !var8.isFlavorJavaFileListType() && !DataFlavor.imageFlavor.equals(var8) && !var8.isRepresentationClassSerializable() && !var8.isRepresentationClassInputStream() && !var8.isRepresentationClassRemote());

            var3.add(var8);
         }
      }

      return var3;
   }

   public DataFlavor[] getFlavorsForFormatAsArray(long var1, FlavorTable var3) {
      return this.getFlavorsForFormatsAsArray(new long[]{var1}, var3);
   }

   public DataFlavor[] getFlavorsForFormatsAsArray(long[] var1, FlavorTable var2) {
      return setToSortedDataFlavorArray(this.getFlavorsForFormatsAsSet(var1, var2));
   }

   private static Object createMapping(Object var0, Object var1) {
      return Arrays.asList(var0, var1);
   }

   protected abstract Long getFormatForNativeAsLong(String var1);

   protected abstract String getNativeForFormat(long var1);

   private String getBestCharsetForTextFormat(Long var1, Transferable var2) throws IOException {
      String var3 = null;
      if (var2 != null && this.isLocaleDependentTextFormat(var1) && var2.isDataFlavorSupported(javaTextEncodingFlavor)) {
         try {
            var3 = new String((byte[])((byte[])var2.getTransferData(javaTextEncodingFlavor)), "UTF-8");
         } catch (UnsupportedFlavorException var5) {
         }
      } else {
         var3 = this.getCharsetForTextFormat(var1);
      }

      if (var3 == null) {
         var3 = getDefaultTextCharset();
      }

      return var3;
   }

   private byte[] translateTransferableString(String var1, long var2) throws IOException {
      Long var4 = var2;
      String var5 = this.getBestCharsetForTextFormat(var4, (Transferable)null);
      String var6 = (String)nativeEOLNs.get(var4);
      int var9;
      if (var6 != null) {
         int var7 = var1.length();
         StringBuffer var8 = new StringBuffer(var7 * 2);

         for(var9 = 0; var9 < var7; ++var9) {
            if (var1.startsWith(var6, var9)) {
               var8.append(var6);
               var9 += var6.length() - 1;
            } else {
               char var10 = var1.charAt(var9);
               if (var10 == '\n') {
                  var8.append(var6);
               } else {
                  var8.append(var10);
               }
            }
         }

         var1 = var8.toString();
      }

      byte[] var12 = var1.getBytes(var5);
      Integer var13 = (Integer)nativeTerminators.get(var4);
      if (var13 != null) {
         var9 = var13;
         byte[] var14 = new byte[var12.length + var9];
         System.arraycopy(var12, 0, var14, 0, var12.length);

         for(int var11 = var12.length; var11 < var14.length; ++var11) {
            var14[var11] = 0;
         }

         var12 = var14;
      }

      return var12;
   }

   private String translateBytesToString(byte[] var1, long var2, Transferable var4) throws IOException {
      Long var5 = var2;
      String var6 = this.getBestCharsetForTextFormat(var5, var4);
      String var7 = (String)nativeEOLNs.get(var5);
      Integer var8 = (Integer)nativeTerminators.get(var5);
      int var9;
      if (var8 != null) {
         int var10 = var8;

         label64:
         for(var9 = 0; var9 < var1.length - var10 + 1; var9 += var10) {
            int var11 = var9;

            while(true) {
               if (var11 >= var9 + var10) {
                  break label64;
               }

               if (var1[var11] != 0) {
                  break;
               }

               ++var11;
            }
         }
      } else {
         var9 = var1.length;
      }

      String var18 = new String(var1, 0, var9, var6);
      if (var7 != null) {
         char[] var19 = var18.toCharArray();
         char[] var12 = var7.toCharArray();
         var18 = null;
         int var13 = 0;
         int var15 = 0;

         while(true) {
            while(var15 < var19.length) {
               if (var15 + var12.length > var19.length) {
                  var19[var13++] = var19[var15++];
               } else {
                  boolean var14 = true;
                  int var16 = 0;

                  for(int var17 = var15; var16 < var12.length; ++var17) {
                     if (var12[var16] != var19[var17]) {
                        var14 = false;
                        break;
                     }

                     ++var16;
                  }

                  if (var14) {
                     var19[var13++] = '\n';
                     var15 += var12.length;
                  } else {
                     var19[var13++] = var19[var15++];
                  }
               }
            }

            var18 = new String(var19, 0, var13);
            break;
         }
      }

      return var18;
   }

   public byte[] translateTransferable(Transferable var1, DataFlavor var2, long var3) throws IOException {
      Object var5;
      boolean var6;
      try {
         var5 = var1.getTransferData(var2);
         if (var5 == null) {
            return null;
         }

         if (var2.equals(DataFlavor.plainTextFlavor) && !(var5 instanceof InputStream)) {
            var5 = var1.getTransferData(DataFlavor.stringFlavor);
            if (var5 == null) {
               return null;
            }

            var6 = true;
         } else {
            var6 = false;
         }
      } catch (UnsupportedFlavorException var148) {
         throw new IOException(var148.getMessage());
      }

      String var7;
      if (!var6 && (!String.class.equals(var2.getRepresentationClass()) || !isFlavorCharsetTextType(var2) || !this.isTextFormat(var3))) {
         Throwable var9;
         if (var2.isRepresentationClassReader()) {
            if (isFlavorCharsetTextType(var2) && this.isTextFormat(var3)) {
               StringBuffer var162 = new StringBuffer();
               Reader var159 = (Reader)var5;
               var9 = null;

               try {
                  int var165;
                  try {
                     while((var165 = var159.read()) != -1) {
                        var162.append((char)var165);
                     }
                  } catch (Throwable var140) {
                     var9 = var140;
                     throw var140;
                  }
               } finally {
                  if (var159 != null) {
                     if (var9 != null) {
                        try {
                           var159.close();
                        } catch (Throwable var132) {
                           var9.addSuppressed(var132);
                        }
                     } else {
                        var159.close();
                     }
                  }

               }

               return this.translateTransferableString(var162.toString(), var3);
            } else {
               throw new IOException("cannot transfer non-text data as Reader");
            }
         } else {
            int var155;
            if (var2.isRepresentationClassCharBuffer()) {
               if (isFlavorCharsetTextType(var2) && this.isTextFormat(var3)) {
                  CharBuffer var160 = (CharBuffer)var5;
                  var155 = var160.remaining();
                  char[] var169 = new char[var155];
                  var160.get(var169, 0, var155);
                  return this.translateTransferableString(new String(var169), var3);
               } else {
                  throw new IOException("cannot transfer non-text data as CharBuffer");
               }
            } else if (char[].class.equals(var2.getRepresentationClass())) {
               if (isFlavorCharsetTextType(var2) && this.isTextFormat(var3)) {
                  return this.translateTransferableString(new String((char[])((char[])var5)), var3);
               } else {
                  throw new IOException("cannot transfer non-text data as char array");
               }
            } else if (var2.isRepresentationClassByteBuffer()) {
               ByteBuffer var157 = (ByteBuffer)var5;
               var155 = var157.remaining();
               byte[] var167 = new byte[var155];
               var157.get(var167, 0, var155);
               if (isFlavorCharsetTextType(var2) && this.isTextFormat(var3)) {
                  String var164 = getTextCharset(var2);
                  return this.translateTransferableString(new String(var167, var164), var3);
               } else {
                  return var167;
               }
            } else {
               byte[] var149;
               String var151;
               if (byte[].class.equals(var2.getRepresentationClass())) {
                  var149 = (byte[])((byte[])var5);
                  if (isFlavorCharsetTextType(var2) && this.isTextFormat(var3)) {
                     var151 = getTextCharset(var2);
                     return this.translateTransferableString(new String(var149, var151), var3);
                  } else {
                     return var149;
                  }
               } else if (DataFlavor.imageFlavor.equals(var2)) {
                  if (!this.isImageFormat(var3)) {
                     throw new IOException("Data translation failed: not an image format");
                  } else {
                     Image var154 = (Image)var5;
                     byte[] var153 = this.imageToPlatformBytes(var154, var3);
                     if (var153 == null) {
                        throw new IOException("Data translation failed: cannot convert java image to native format");
                     } else {
                        return var153;
                     }
                  }
               } else {
                  var7 = null;
                  if (this.isFileFormat(var3)) {
                     if (!DataFlavor.javaFileListFlavor.equals(var2)) {
                        throw new IOException("data translation failed");
                     }

                     List var152 = (List)var5;
                     ProtectionDomain var166 = getUserProtectionDomain(var1);
                     ArrayList var163 = this.castToFiles(var152, var166);
                     ByteArrayOutputStream var172 = this.convertFileListToBytes(var163);
                     Throwable var12 = null;

                     try {
                        var149 = var172.toByteArray();
                     } catch (Throwable var138) {
                        var12 = var138;
                        throw var138;
                     } finally {
                        if (var172 != null) {
                           if (var12 != null) {
                              try {
                                 var172.close();
                              } catch (Throwable var131) {
                                 var12.addSuppressed(var131);
                              }
                           } else {
                              var172.close();
                           }
                        }

                     }
                  } else {
                     byte[] var14;
                     if (this.isURIListFormat(var3)) {
                        if (!DataFlavor.javaFileListFlavor.equals(var2)) {
                           throw new IOException("data translation failed");
                        }

                        var151 = this.getNativeForFormat(var3);
                        String var156 = null;
                        if (var151 != null) {
                           try {
                              var156 = (new DataFlavor(var151)).getParameter("charset");
                           } catch (ClassNotFoundException var137) {
                              throw new IOException(var137);
                           }
                        }

                        if (var156 == null) {
                           var156 = "UTF-8";
                        }

                        List var161 = (List)var5;
                        ProtectionDomain var170 = getUserProtectionDomain(var1);
                        ArrayList var174 = this.castToFiles(var161, var170);
                        ArrayList var175 = new ArrayList(var174.size());
                        Iterator var176 = var174.iterator();

                        while(var176.hasNext()) {
                           String var177 = (String)var176.next();
                           URI var16 = (new File(var177)).toURI();

                           try {
                              var175.add((new URI(var16.getScheme(), "", var16.getPath(), var16.getFragment())).toString());
                           } catch (URISyntaxException var136) {
                              throw new IOException(var136);
                           }
                        }

                        var14 = "\r\n".getBytes(var156);
                        ByteArrayOutputStream var178 = new ByteArrayOutputStream();
                        Throwable var179 = null;

                        try {
                           for(int var17 = 0; var17 < var175.size(); ++var17) {
                              byte[] var18 = ((String)var175.get(var17)).getBytes(var156);
                              var178.write(var18, 0, var18.length);
                              var178.write(var14, 0, var14.length);
                           }

                           var149 = var178.toByteArray();
                        } catch (Throwable var142) {
                           var179 = var142;
                           throw var142;
                        } finally {
                           if (var178 != null) {
                              if (var179 != null) {
                                 try {
                                    var178.close();
                                 } catch (Throwable var135) {
                                    var179.addSuppressed(var135);
                                 }
                              } else {
                                 var178.close();
                              }
                           }

                        }
                     } else if (!var2.isRepresentationClassInputStream()) {
                        if (var2.isRepresentationClassRemote()) {
                           Object var150 = DataTransferer.RMI.newMarshalledObject(var5);
                           var149 = convertObjectToBytes(var150);
                        } else {
                           if (!var2.isRepresentationClassSerializable()) {
                              throw new IOException("data translation failed");
                           }

                           var149 = convertObjectToBytes(var5);
                        }
                     } else {
                        if (!(var5 instanceof InputStream)) {
                           return new byte[0];
                        }

                        ByteArrayOutputStream var8 = new ByteArrayOutputStream();
                        var9 = null;

                        byte[] var173;
                        try {
                           InputStream var10 = (InputStream)var5;
                           Throwable var11 = null;

                           try {
                              boolean var171 = false;
                              int var13 = var10.available();
                              var14 = new byte[var13 > 8192 ? var13 : 8192];

                              do {
                                 int var15;
                                 if (!(var171 = (var15 = var10.read(var14, 0, var14.length)) == -1)) {
                                    var8.write(var14, 0, var15);
                                 }
                              } while(!var171);
                           } catch (Throwable var144) {
                              var11 = var144;
                              throw var144;
                           } finally {
                              if (var10 != null) {
                                 if (var11 != null) {
                                    try {
                                       var10.close();
                                    } catch (Throwable var134) {
                                       var11.addSuppressed(var134);
                                    }
                                 } else {
                                    var10.close();
                                 }
                              }

                           }

                           if (!isFlavorCharsetTextType(var2) || !this.isTextFormat(var3)) {
                              var149 = var8.toByteArray();
                              return var149;
                           }

                           byte[] var158 = var8.toByteArray();
                           String var168 = getTextCharset(var2);
                           var173 = this.translateTransferableString(new String(var158, var168), var3);
                        } catch (Throwable var146) {
                           var9 = var146;
                           throw var146;
                        } finally {
                           if (var8 != null) {
                              if (var9 != null) {
                                 try {
                                    var8.close();
                                 } catch (Throwable var133) {
                                    var9.addSuppressed(var133);
                                 }
                              } else {
                                 var8.close();
                              }
                           }

                        }

                        return var173;
                     }
                  }

                  return var149;
               }
            }
         }
      } else {
         var7 = this.removeSuspectedData(var2, var1, (String)var5);
         return this.translateTransferableString(var7, var3);
      }
   }

   private static byte[] convertObjectToBytes(Object var0) throws IOException {
      ByteArrayOutputStream var1 = new ByteArrayOutputStream();
      Throwable var2 = null;

      Object var5;
      try {
         ObjectOutputStream var3 = new ObjectOutputStream(var1);
         Throwable var4 = null;

         try {
            var3.writeObject(var0);
            var5 = var1.toByteArray();
         } catch (Throwable var28) {
            var5 = var28;
            var4 = var28;
            throw var28;
         } finally {
            if (var3 != null) {
               if (var4 != null) {
                  try {
                     var3.close();
                  } catch (Throwable var27) {
                     var4.addSuppressed(var27);
                  }
               } else {
                  var3.close();
               }
            }

         }
      } catch (Throwable var30) {
         var2 = var30;
         throw var30;
      } finally {
         if (var1 != null) {
            if (var2 != null) {
               try {
                  var1.close();
               } catch (Throwable var26) {
                  var2.addSuppressed(var26);
               }
            } else {
               var1.close();
            }
         }

      }

      return (byte[])var5;
   }

   protected abstract ByteArrayOutputStream convertFileListToBytes(ArrayList<String> var1) throws IOException;

   private String removeSuspectedData(DataFlavor var1, Transferable var2, final String var3) throws IOException {
      if (null != System.getSecurityManager() && var1.isMimeTypeEqual("text/uri-list")) {
         String var4 = "";
         final ProtectionDomain var5 = getUserProtectionDomain(var2);

         try {
            var4 = (String)AccessController.doPrivileged(new PrivilegedExceptionAction() {
               public Object run() {
                  StringBuffer var1 = new StringBuffer(var3.length());
                  String[] var2 = var3.split("(\\s)+");
                  String[] var3x = var2;
                  int var4 = var2.length;

                  for(int var5x = 0; var5x < var4; ++var5x) {
                     String var6 = var3x[var5x];
                     File var7 = new File(var6);
                     if (var7.exists() && !DataTransferer.isFileInWebstartedCache(var7) && !DataTransferer.this.isForbiddenToRead(var7, var5)) {
                        if (0 != var1.length()) {
                           var1.append("\\r\\n");
                        }

                        var1.append(var6);
                     }
                  }

                  return var1.toString();
               }
            });
            return var4;
         } catch (PrivilegedActionException var7) {
            throw new IOException(var7.getMessage(), var7);
         }
      } else {
         return var3;
      }
   }

   private static ProtectionDomain getUserProtectionDomain(Transferable var0) {
      return var0.getClass().getProtectionDomain();
   }

   private boolean isForbiddenToRead(File var1, ProtectionDomain var2) {
      if (null == var2) {
         return false;
      } else {
         try {
            FilePermission var3 = new FilePermission(var1.getCanonicalPath(), "read, delete");
            if (var2.implies(var3)) {
               return false;
            }
         } catch (IOException var4) {
         }

         return true;
      }
   }

   private ArrayList<String> castToFiles(final List var1, final ProtectionDomain var2) throws IOException {
      final ArrayList var3 = new ArrayList();

      try {
         AccessController.doPrivileged(new PrivilegedExceptionAction() {
            public Object run() throws IOException {
               Iterator var1x = var1.iterator();

               while(true) {
                  File var3x;
                  do {
                     do {
                        if (!var1x.hasNext()) {
                           return null;
                        }

                        Object var2x = var1x.next();
                        var3x = DataTransferer.this.castToFile(var2x);
                     } while(var3x == null);
                  } while(null != System.getSecurityManager() && (DataTransferer.isFileInWebstartedCache(var3x) || DataTransferer.this.isForbiddenToRead(var3x, var2)));

                  var3.add(var3x.getCanonicalPath());
               }
            }
         });
         return var3;
      } catch (PrivilegedActionException var5) {
         throw new IOException(var5.getMessage());
      }
   }

   private File castToFile(Object var1) throws IOException {
      String var2 = null;
      if (var1 instanceof File) {
         var2 = ((File)var1).getCanonicalPath();
      } else {
         if (!(var1 instanceof String)) {
            return null;
         }

         var2 = (String)var1;
      }

      return new File(var2);
   }

   private static boolean isFileInWebstartedCache(File var0) {
      if (deploymentCacheDirectoryList.isEmpty()) {
         String[] var1 = DEPLOYMENT_CACHE_PROPERTIES;
         int var2 = var1.length;

         for(int var3 = 0; var3 < var2; ++var3) {
            String var4 = var1[var3];
            String var5 = System.getProperty(var4);
            if (var5 != null) {
               try {
                  File var6 = (new File(var5)).getCanonicalFile();
                  if (var6 != null) {
                     deploymentCacheDirectoryList.add(var6);
                  }
               } catch (IOException var7) {
               }
            }
         }
      }

      Iterator var8 = deploymentCacheDirectoryList.iterator();

      while(var8.hasNext()) {
         File var9 = (File)var8.next();

         for(File var10 = var0; var10 != null; var10 = var10.getParentFile()) {
            if (var10.equals(var9)) {
               return true;
            }
         }
      }

      return false;
   }

   public Object translateBytes(byte[] var1, DataFlavor var2, long var3, Transferable var5) throws IOException {
      Object var6 = null;
      if (this.isFileFormat(var3)) {
         if (!DataFlavor.javaFileListFlavor.equals(var2)) {
            throw new IOException("data translation failed");
         }

         String[] var197 = this.dragQueryFile(var1);
         if (var197 == null) {
            return null;
         }

         File[] var201 = new File[var197.length];

         for(int var199 = 0; var199 < var197.length; ++var199) {
            var201[var199] = new File(var197[var199]);
         }

         var6 = Arrays.asList(var201);
      } else {
         ByteArrayInputStream var195;
         Throwable var8;
         Throwable var10;
         if (this.isURIListFormat(var3) && DataFlavor.javaFileListFlavor.equals(var2)) {
            var195 = new ByteArrayInputStream(var1);
            var8 = null;

            try {
               URI[] var198 = this.dragQueryURIs(var195, var3, var5);
               if (var198 == null) {
                  var10 = null;
                  return var10;
               }

               ArrayList var200 = new ArrayList();
               URI[] var11 = var198;
               int var12 = var198.length;

               for(int var13 = 0; var13 < var12; ++var13) {
                  URI var14 = var11[var13];

                  try {
                     var200.add(new File(var14));
                  } catch (IllegalArgumentException var185) {
                  }
               }

               var6 = var200;
            } catch (Throwable var193) {
               var8 = var193;
               throw var193;
            } finally {
               if (var195 != null) {
                  if (var8 != null) {
                     try {
                        var195.close();
                     } catch (Throwable var178) {
                        var8.addSuppressed(var178);
                     }
                  } else {
                     var195.close();
                  }
               }

            }
         } else if (String.class.equals(var2.getRepresentationClass()) && isFlavorCharsetTextType(var2) && this.isTextFormat(var3)) {
            var6 = this.translateBytesToString(var1, var3, var5);
         } else if (var2.isRepresentationClassReader()) {
            var195 = new ByteArrayInputStream(var1);
            var8 = null;

            try {
               var6 = this.translateStream(var195, var2, var3, var5);
            } catch (Throwable var184) {
               var8 = var184;
               throw var184;
            } finally {
               if (var195 != null) {
                  if (var8 != null) {
                     try {
                        var195.close();
                     } catch (Throwable var176) {
                        var8.addSuppressed(var176);
                     }
                  } else {
                     var195.close();
                  }
               }

            }
         } else if (var2.isRepresentationClassCharBuffer()) {
            if (!isFlavorCharsetTextType(var2) || !this.isTextFormat(var3)) {
               throw new IOException("cannot transfer non-text data as CharBuffer");
            }

            CharBuffer var196 = CharBuffer.wrap((CharSequence)this.translateBytesToString(var1, var3, var5));
            var6 = this.constructFlavoredObject(var196, var2, CharBuffer.class);
         } else if (char[].class.equals(var2.getRepresentationClass())) {
            if (!isFlavorCharsetTextType(var2) || !this.isTextFormat(var3)) {
               throw new IOException("cannot transfer non-text data as char array");
            }

            var6 = this.translateBytesToString(var1, var3, var5).toCharArray();
         } else if (var2.isRepresentationClassByteBuffer()) {
            if (isFlavorCharsetTextType(var2) && this.isTextFormat(var3)) {
               var1 = this.translateBytesToString(var1, var3, var5).getBytes(getTextCharset(var2));
            }

            ByteBuffer var7 = ByteBuffer.wrap(var1);
            var6 = this.constructFlavoredObject(var7, var2, ByteBuffer.class);
         } else if (byte[].class.equals(var2.getRepresentationClass())) {
            if (isFlavorCharsetTextType(var2) && this.isTextFormat(var3)) {
               var6 = this.translateBytesToString(var1, var3, var5).getBytes(getTextCharset(var2));
            } else {
               var6 = var1;
            }
         } else if (var2.isRepresentationClassInputStream()) {
            var195 = new ByteArrayInputStream(var1);
            var8 = null;

            try {
               var6 = this.translateStream(var195, var2, var3, var5);
            } catch (Throwable var183) {
               var8 = var183;
               throw var183;
            } finally {
               if (var195 != null) {
                  if (var8 != null) {
                     try {
                        var195.close();
                     } catch (Throwable var177) {
                        var8.addSuppressed(var177);
                     }
                  } else {
                     var195.close();
                  }
               }

            }
         } else if (var2.isRepresentationClassRemote()) {
            try {
               var195 = new ByteArrayInputStream(var1);
               var8 = null;

               try {
                  ObjectInputStream var9 = new ObjectInputStream(var195);
                  var10 = null;

                  try {
                     var6 = DataTransferer.RMI.getMarshalledObject(var9.readObject());
                  } catch (Throwable var182) {
                     var10 = var182;
                     throw var182;
                  } finally {
                     if (var9 != null) {
                        if (var10 != null) {
                           try {
                              var9.close();
                           } catch (Throwable var180) {
                              var10.addSuppressed(var180);
                           }
                        } else {
                           var9.close();
                        }
                     }

                  }
               } catch (Throwable var190) {
                  var8 = var190;
                  throw var190;
               } finally {
                  if (var195 != null) {
                     if (var8 != null) {
                        try {
                           var195.close();
                        } catch (Throwable var179) {
                           var8.addSuppressed(var179);
                        }
                     } else {
                        var195.close();
                     }
                  }

               }
            } catch (Exception var192) {
               throw new IOException(var192.getMessage());
            }
         } else if (var2.isRepresentationClassSerializable()) {
            var195 = new ByteArrayInputStream(var1);
            var8 = null;

            try {
               var6 = this.translateStream(var195, var2, var3, var5);
            } catch (Throwable var181) {
               var8 = var181;
               throw var181;
            } finally {
               if (var195 != null) {
                  if (var8 != null) {
                     try {
                        var195.close();
                     } catch (Throwable var175) {
                        var8.addSuppressed(var175);
                     }
                  } else {
                     var195.close();
                  }
               }

            }
         } else if (DataFlavor.imageFlavor.equals(var2)) {
            if (!this.isImageFormat(var3)) {
               throw new IOException("data translation failed");
            }

            var6 = this.platformImageBytesToImage(var1, var3);
         }
      }

      if (var6 == null) {
         throw new IOException("data translation failed");
      } else {
         return var6;
      }
   }

   public Object translateStream(InputStream var1, DataFlavor var2, long var3, Transferable var5) throws IOException {
      Object var6 = null;
      if (this.isURIListFormat(var3) && DataFlavor.javaFileListFlavor.equals(var2)) {
         URI[] var49 = this.dragQueryURIs(var1, var3, var5);
         if (var49 == null) {
            return null;
         }

         ArrayList var52 = new ArrayList();
         URI[] var50 = var49;
         int var10 = var49.length;

         for(int var11 = 0; var11 < var10; ++var11) {
            URI var12 = var50[var11];

            try {
               var52.add(new File(var12));
            } catch (IllegalArgumentException var43) {
            }
         }

         var6 = var52;
      } else {
         if (String.class.equals(var2.getRepresentationClass()) && isFlavorCharsetTextType(var2) && this.isTextFormat(var3)) {
            return this.translateBytesToString(inputStreamToByteArray(var1), var3, var5);
         }

         if (DataFlavor.plainTextFlavor.equals(var2)) {
            var6 = new StringReader(this.translateBytesToString(inputStreamToByteArray(var1), var3, var5));
         } else if (var2.isRepresentationClassInputStream()) {
            var6 = this.translateStreamToInputStream(var1, var2, var3, var5);
         } else if (var2.isRepresentationClassReader()) {
            if (!isFlavorCharsetTextType(var2) || !this.isTextFormat(var3)) {
               throw new IOException("cannot transfer non-text data as Reader");
            }

            InputStream var48 = (InputStream)this.translateStreamToInputStream(var1, DataFlavor.plainTextFlavor, var3, var5);
            String var51 = getTextCharset(DataFlavor.plainTextFlavor);
            InputStreamReader var9 = new InputStreamReader(var48, var51);
            var6 = this.constructFlavoredObject(var9, var2, Reader.class);
         } else if (byte[].class.equals(var2.getRepresentationClass())) {
            if (isFlavorCharsetTextType(var2) && this.isTextFormat(var3)) {
               var6 = this.translateBytesToString(inputStreamToByteArray(var1), var3, var5).getBytes(getTextCharset(var2));
            } else {
               var6 = inputStreamToByteArray(var1);
            }
         } else {
            ObjectInputStream var7;
            Throwable var8;
            if (var2.isRepresentationClassRemote()) {
               try {
                  var7 = new ObjectInputStream(var1);
                  var8 = null;

                  try {
                     var6 = DataTransferer.RMI.getMarshalledObject(var7.readObject());
                  } catch (Throwable var42) {
                     var8 = var42;
                     throw var42;
                  } finally {
                     if (var7 != null) {
                        if (var8 != null) {
                           try {
                              var7.close();
                           } catch (Throwable var39) {
                              var8.addSuppressed(var39);
                           }
                        } else {
                           var7.close();
                        }
                     }

                  }
               } catch (Exception var47) {
                  throw new IOException(var47.getMessage());
               }
            } else if (var2.isRepresentationClassSerializable()) {
               try {
                  var7 = new ObjectInputStream(var1);
                  var8 = null;

                  try {
                     var6 = var7.readObject();
                  } catch (Throwable var41) {
                     var8 = var41;
                     throw var41;
                  } finally {
                     if (var7 != null) {
                        if (var8 != null) {
                           try {
                              var7.close();
                           } catch (Throwable var40) {
                              var8.addSuppressed(var40);
                           }
                        } else {
                           var7.close();
                        }
                     }

                  }
               } catch (Exception var45) {
                  throw new IOException(var45.getMessage());
               }
            } else if (DataFlavor.imageFlavor.equals(var2)) {
               if (!this.isImageFormat(var3)) {
                  throw new IOException("data translation failed");
               }

               var6 = this.platformImageBytesToImage(inputStreamToByteArray(var1), var3);
            }
         }
      }

      if (var6 == null) {
         throw new IOException("data translation failed");
      } else {
         return var6;
      }
   }

   private Object translateStreamToInputStream(InputStream var1, DataFlavor var2, long var3, Transferable var5) throws IOException {
      if (isFlavorCharsetTextType(var2) && this.isTextFormat(var3)) {
         var1 = new DataTransferer.ReencodingInputStream((InputStream)var1, var3, getTextCharset(var2), var5);
      }

      return this.constructFlavoredObject(var1, var2, InputStream.class);
   }

   private Object constructFlavoredObject(Object var1, DataFlavor var2, Class var3) throws IOException {
      final Class var4 = var2.getRepresentationClass();
      if (var3.equals(var4)) {
         return var1;
      } else {
         Constructor[] var5 = null;

         try {
            var5 = (Constructor[])((Constructor[])AccessController.doPrivileged(new PrivilegedAction() {
               public Object run() {
                  return var4.getConstructors();
               }
            }));
         } catch (SecurityException var10) {
            throw new IOException(var10.getMessage());
         }

         Constructor var6 = null;

         for(int var7 = 0; var7 < var5.length; ++var7) {
            if (Modifier.isPublic(var5[var7].getModifiers())) {
               Class[] var8 = var5[var7].getParameterTypes();
               if (var8 != null && var8.length == 1 && var3.equals(var8[0])) {
                  var6 = var5[var7];
                  break;
               }
            }
         }

         if (var6 == null) {
            throw new IOException("can't find <init>(L" + var3 + ";)V for class: " + var4.getName());
         } else {
            try {
               return var6.newInstance(var1);
            } catch (Exception var9) {
               throw new IOException(var9.getMessage());
            }
         }
      }
   }

   protected abstract String[] dragQueryFile(byte[] var1);

   protected URI[] dragQueryURIs(InputStream var1, long var2, Transferable var4) throws IOException {
      throw new IOException(new UnsupportedOperationException("not implemented on this platform"));
   }

   protected abstract Image platformImageBytesToImage(byte[] var1, long var2) throws IOException;

   protected Image standardImageBytesToImage(byte[] var1, String var2) throws IOException {
      Iterator var3 = ImageIO.getImageReadersByMIMEType(var2);
      if (!var3.hasNext()) {
         throw new IOException("No registered service provider can decode  an image from " + var2);
      } else {
         IOException var4 = null;

         while(var3.hasNext()) {
            ImageReader var5 = (ImageReader)var3.next();

            try {
               ByteArrayInputStream var6 = new ByteArrayInputStream(var1);
               Throwable var7 = null;

               BufferedImage var11;
               try {
                  ImageInputStream var8 = ImageIO.createImageInputStream(var6);

                  try {
                     ImageReadParam var9 = var5.getDefaultReadParam();
                     var5.setInput(var8, true, true);
                     BufferedImage var10 = var5.read(var5.getMinIndex(), var9);
                     if (var10 == null) {
                        continue;
                     }

                     var11 = var10;
                  } finally {
                     var8.close();
                     var5.dispose();
                  }
               } catch (Throwable var30) {
                  var7 = var30;
                  throw var30;
               } finally {
                  if (var6 != null) {
                     if (var7 != null) {
                        try {
                           var6.close();
                        } catch (Throwable var28) {
                           var7.addSuppressed(var28);
                        }
                     } else {
                        var6.close();
                     }
                  }

               }

               return var11;
            } catch (IOException var32) {
               var4 = var32;
            }
         }

         if (var4 == null) {
            var4 = new IOException("Registered service providers failed to decode an image from " + var2);
         }

         throw var4;
      }
   }

   protected abstract byte[] imageToPlatformBytes(Image var1, long var2) throws IOException;

   protected byte[] imageToStandardBytes(Image var1, String var2) throws IOException {
      IOException var3 = null;
      Iterator var4 = ImageIO.getImageWritersByMIMEType(var2);
      if (!var4.hasNext()) {
         throw new IOException("No registered service provider can encode  an image to " + var2);
      } else {
         if (var1 instanceof RenderedImage) {
            try {
               return this.imageToStandardBytesImpl((RenderedImage)var1, var2);
            } catch (IOException var17) {
               var3 = var17;
            }
         }

         boolean var5 = false;
         boolean var6 = false;
         int var18;
         int var19;
         if (var1 instanceof ToolkitImage) {
            ImageRepresentation var7 = ((ToolkitImage)var1).getImageRep();
            var7.reconstruct(32);
            var18 = var7.getWidth();
            var19 = var7.getHeight();
         } else {
            var18 = var1.getWidth((ImageObserver)null);
            var19 = var1.getHeight((ImageObserver)null);
         }

         ColorModel var20 = ColorModel.getRGBdefault();
         WritableRaster var8 = var20.createCompatibleWritableRaster(var18, var19);
         BufferedImage var9 = new BufferedImage(var20, var8, var20.isAlphaPremultiplied(), (Hashtable)null);
         Graphics var10 = var9.getGraphics();

         try {
            var10.drawImage(var1, 0, 0, var18, var19, (ImageObserver)null);
         } finally {
            var10.dispose();
         }

         try {
            return this.imageToStandardBytesImpl(var9, var2);
         } catch (IOException var16) {
            if (var3 != null) {
               throw var3;
            } else {
               throw var16;
            }
         }
      }
   }

   protected byte[] imageToStandardBytesImpl(RenderedImage var1, String var2) throws IOException {
      Iterator var3 = ImageIO.getImageWritersByMIMEType(var2);
      ImageTypeSpecifier var4 = new ImageTypeSpecifier(var1);
      ByteArrayOutputStream var5 = new ByteArrayOutputStream();
      IOException var6 = null;

      ImageWriter var7;
      while(true) {
         ImageWriterSpi var8;
         do {
            if (!var3.hasNext()) {
               var5.close();
               if (var6 == null) {
                  var6 = new IOException("Registered service providers failed to encode " + var1 + " to " + var2);
               }

               throw var6;
            }

            var7 = (ImageWriter)var3.next();
            var8 = var7.getOriginatingProvider();
         } while(!var8.canEncodeImage(var4));

         try {
            ImageOutputStream var9 = ImageIO.createImageOutputStream(var5);

            try {
               var7.setOutput(var9);
               var7.write(var1);
               var9.flush();
               break;
            } finally {
               var9.close();
            }
         } catch (IOException var14) {
            var7.dispose();
            var5.reset();
            var6 = var14;
         }
      }

      var7.dispose();
      var5.close();
      return var5.toByteArray();
   }

   private Object concatData(Object var1, Object var2) {
      Object var3 = null;
      Object var4 = null;
      if (var1 instanceof byte[]) {
         byte[] var5 = (byte[])((byte[])var1);
         if (var2 instanceof byte[]) {
            byte[] var6 = (byte[])((byte[])var2);
            byte[] var7 = new byte[var5.length + var6.length];
            System.arraycopy(var5, 0, var7, 0, var5.length);
            System.arraycopy(var6, 0, var7, var5.length, var6.length);
            return var7;
         }

         var3 = new ByteArrayInputStream(var5);
         var4 = (InputStream)var2;
      } else {
         var3 = (InputStream)var1;
         if (var2 instanceof byte[]) {
            var4 = new ByteArrayInputStream((byte[])((byte[])var2));
         } else {
            var4 = (InputStream)var2;
         }
      }

      return new SequenceInputStream((InputStream)var3, (InputStream)var4);
   }

   public byte[] convertData(Object var1, final Transferable var2, final long var3, final Map var5, boolean var6) throws IOException {
      byte[] var7 = null;
      if (var6) {
         try {
            final Stack var8 = new Stack();
            Runnable var9 = new Runnable() {
               private boolean done = false;

               public void run() {
                  if (!this.done) {
                     byte[] var1 = null;

                     try {
                        DataFlavor var2x = (DataFlavor)var5.get(var3);
                        if (var2x != null) {
                           var1 = DataTransferer.this.translateTransferable(var2, var2x, var3);
                        }
                     } catch (Exception var7) {
                        var7.printStackTrace();
                        var1 = null;
                     }

                     try {
                        DataTransferer.this.getToolkitThreadBlockedHandler().lock();
                        var8.push(var1);
                        DataTransferer.this.getToolkitThreadBlockedHandler().exit();
                     } finally {
                        DataTransferer.this.getToolkitThreadBlockedHandler().unlock();
                        this.done = true;
                     }

                  }
               }
            };
            AppContext var10 = SunToolkit.targetToAppContext(var1);
            this.getToolkitThreadBlockedHandler().lock();
            if (var10 != null) {
               var10.put("DATA_CONVERTER_KEY", var9);
            }

            SunToolkit.executeOnEventHandlerThread(var1, var9);

            while(var8.empty()) {
               this.getToolkitThreadBlockedHandler().enter();
            }

            if (var10 != null) {
               var10.remove("DATA_CONVERTER_KEY");
            }

            var7 = (byte[])((byte[])var8.pop());
         } finally {
            this.getToolkitThreadBlockedHandler().unlock();
         }
      } else {
         DataFlavor var14 = (DataFlavor)var5.get(var3);
         if (var14 != null) {
            var7 = this.translateTransferable(var2, var14, var3);
         }
      }

      return var7;
   }

   public void processDataConversionRequests() {
      if (EventQueue.isDispatchThread()) {
         AppContext var1 = AppContext.getAppContext();
         this.getToolkitThreadBlockedHandler().lock();

         try {
            Runnable var2 = (Runnable)var1.get("DATA_CONVERTER_KEY");
            if (var2 != null) {
               var2.run();
               var1.remove("DATA_CONVERTER_KEY");
            }
         } finally {
            this.getToolkitThreadBlockedHandler().unlock();
         }
      }

   }

   public abstract ToolkitThreadBlockedHandler getToolkitThreadBlockedHandler();

   public static long[] keysToLongArray(SortedMap var0) {
      Set var1 = var0.keySet();
      long[] var2 = new long[var1.size()];
      int var3 = 0;

      for(Iterator var4 = var1.iterator(); var4.hasNext(); ++var3) {
         var2[var3] = (Long)var4.next();
      }

      return var2;
   }

   public static DataFlavor[] setToSortedDataFlavorArray(Set var0) {
      DataFlavor[] var1 = new DataFlavor[var0.size()];
      var0.toArray(var1);
      DataTransferer.DataFlavorComparator var2 = new DataTransferer.DataFlavorComparator(false);
      Arrays.sort(var1, var2);
      return var1;
   }

   protected static byte[] inputStreamToByteArray(InputStream var0) throws IOException {
      ByteArrayOutputStream var1 = new ByteArrayOutputStream();
      Throwable var2 = null;

      try {
         boolean var3 = false;
         byte[] var4 = new byte[8192];

         int var16;
         while((var16 = var0.read(var4)) != -1) {
            var1.write(var4, 0, var16);
         }

         byte[] var5 = var1.toByteArray();
         return var5;
      } catch (Throwable var14) {
         var2 = var14;
         throw var14;
      } finally {
         if (var1 != null) {
            if (var2 != null) {
               try {
                  var1.close();
               } catch (Throwable var13) {
                  var2.addSuppressed(var13);
               }
            } else {
               var1.close();
            }
         }

      }
   }

   public LinkedHashSet<DataFlavor> getPlatformMappingsForNative(String var1) {
      return new LinkedHashSet();
   }

   public LinkedHashSet<String> getPlatformMappingsForFlavor(DataFlavor var1) {
      return new LinkedHashSet();
   }

   static {
      DataFlavor var0 = null;

      try {
         var0 = new DataFlavor("text/plain;charset=Unicode;class=java.lang.String");
      } catch (ClassNotFoundException var4) {
      }

      plainTextStringFlavor = var0;
      DataFlavor var1 = null;

      try {
         var1 = new DataFlavor("application/x-java-text-encoding;class=\"[B\"");
      } catch (ClassNotFoundException var3) {
      }

      javaTextEncodingFlavor = var1;
      HashMap var2 = new HashMap(17);
      var2.put("sgml", Boolean.TRUE);
      var2.put("xml", Boolean.TRUE);
      var2.put("html", Boolean.TRUE);
      var2.put("enriched", Boolean.TRUE);
      var2.put("richtext", Boolean.TRUE);
      var2.put("uri-list", Boolean.TRUE);
      var2.put("directory", Boolean.TRUE);
      var2.put("css", Boolean.TRUE);
      var2.put("calendar", Boolean.TRUE);
      var2.put("plain", Boolean.TRUE);
      var2.put("rtf", Boolean.FALSE);
      var2.put("tab-separated-values", Boolean.FALSE);
      var2.put("t140", Boolean.FALSE);
      var2.put("rfc822-headers", Boolean.FALSE);
      var2.put("parityfec", Boolean.FALSE);
      textMIMESubtypeCharsetSupport = Collections.synchronizedMap(var2);
      DEPLOYMENT_CACHE_PROPERTIES = new String[]{"deployment.system.cachedir", "deployment.user.cachedir", "deployment.javaws.cachedir", "deployment.javapi.cachedir"};
      deploymentCacheDirectoryList = new ArrayList();
   }

   private static class RMI {
      private static final Class<?> remoteClass = getClass("java.rmi.Remote");
      private static final Class<?> marshallObjectClass = getClass("java.rmi.MarshalledObject");
      private static final Constructor<?> marshallCtor;
      private static final Method marshallGet;

      private static Class<?> getClass(String var0) {
         try {
            return Class.forName(var0, true, (ClassLoader)null);
         } catch (ClassNotFoundException var2) {
            return null;
         }
      }

      private static Constructor<?> getConstructor(Class<?> var0, Class<?>... var1) {
         try {
            return var0 == null ? null : var0.getDeclaredConstructor(var1);
         } catch (NoSuchMethodException var3) {
            throw new AssertionError(var3);
         }
      }

      private static Method getMethod(Class<?> var0, String var1, Class<?>... var2) {
         try {
            return var0 == null ? null : var0.getMethod(var1, var2);
         } catch (NoSuchMethodException var4) {
            throw new AssertionError(var4);
         }
      }

      static boolean isRemote(Class<?> var0) {
         return remoteClass == null ? null : remoteClass.isAssignableFrom(var0);
      }

      static Class<?> remoteClass() {
         return remoteClass;
      }

      static Object newMarshalledObject(Object var0) throws IOException {
         try {
            return marshallCtor.newInstance(var0);
         } catch (InstantiationException var3) {
            throw new AssertionError(var3);
         } catch (IllegalAccessException var4) {
            throw new AssertionError(var4);
         } catch (InvocationTargetException var5) {
            Throwable var2 = var5.getCause();
            if (var2 instanceof IOException) {
               throw (IOException)var2;
            } else {
               throw new AssertionError(var5);
            }
         }
      }

      static Object getMarshalledObject(Object var0) throws IOException, ClassNotFoundException {
         try {
            return marshallGet.invoke(var0);
         } catch (IllegalAccessException var3) {
            throw new AssertionError(var3);
         } catch (InvocationTargetException var4) {
            Throwable var2 = var4.getCause();
            if (var2 instanceof IOException) {
               throw (IOException)var2;
            } else if (var2 instanceof ClassNotFoundException) {
               throw (ClassNotFoundException)var2;
            } else {
               throw new AssertionError(var4);
            }
         }
      }

      static {
         marshallCtor = getConstructor(marshallObjectClass, Object.class);
         marshallGet = getMethod(marshallObjectClass, "get");
      }
   }

   public static class IndexOrderComparator extends DataTransferer.IndexedComparator {
      private final Map indexMap;
      private static final Integer FALLBACK_INDEX = Integer.MIN_VALUE;

      public IndexOrderComparator(Map var1) {
         super(true);
         this.indexMap = var1;
      }

      public IndexOrderComparator(Map var1, boolean var2) {
         super(var2);
         this.indexMap = var1;
      }

      public int compare(Object var1, Object var2) {
         return !this.order ? -compareIndices(this.indexMap, var1, var2, FALLBACK_INDEX) : compareIndices(this.indexMap, var1, var2, FALLBACK_INDEX);
      }
   }

   public static class DataFlavorComparator extends DataTransferer.IndexedComparator {
      private final DataTransferer.CharsetComparator charsetComparator;
      private static final Map exactTypes;
      private static final Map primaryTypes;
      private static final Map nonTextRepresentations;
      private static final Map textTypes;
      private static final Map decodedTextRepresentations;
      private static final Map encodedTextRepresentations;
      private static final Integer UNKNOWN_OBJECT_LOSES = Integer.MIN_VALUE;
      private static final Integer UNKNOWN_OBJECT_WINS = Integer.MAX_VALUE;
      private static final Long UNKNOWN_OBJECT_LOSES_L = Long.MIN_VALUE;
      private static final Long UNKNOWN_OBJECT_WINS_L = Long.MAX_VALUE;

      public DataFlavorComparator() {
         this(true);
      }

      public DataFlavorComparator(boolean var1) {
         super(var1);
         this.charsetComparator = new DataTransferer.CharsetComparator(var1);
      }

      public int compare(Object var1, Object var2) {
         DataFlavor var3 = null;
         DataFlavor var4 = null;
         if (this.order) {
            var3 = (DataFlavor)var1;
            var4 = (DataFlavor)var2;
         } else {
            var3 = (DataFlavor)var2;
            var4 = (DataFlavor)var1;
         }

         if (var3.equals(var4)) {
            return 0;
         } else {
            boolean var5 = false;
            String var6 = var3.getPrimaryType();
            String var7 = var3.getSubType();
            String var8 = var6 + "/" + var7;
            Class var9 = var3.getRepresentationClass();
            String var10 = var4.getPrimaryType();
            String var11 = var4.getSubType();
            String var12 = var10 + "/" + var11;
            Class var13 = var4.getRepresentationClass();
            int var14;
            if (var3.isFlavorTextType() && var4.isFlavorTextType()) {
               var14 = compareIndices(textTypes, var8, var12, UNKNOWN_OBJECT_LOSES);
               if (var14 != 0) {
                  return var14;
               }

               if (DataTransferer.doesSubtypeSupportCharset(var3)) {
                  var14 = compareIndices(decodedTextRepresentations, var9, var13, UNKNOWN_OBJECT_LOSES);
                  if (var14 != 0) {
                     return var14;
                  }

                  var14 = this.charsetComparator.compareCharsets(DataTransferer.getTextCharset(var3), DataTransferer.getTextCharset(var4));
                  if (var14 != 0) {
                     return var14;
                  }
               }

               var14 = compareIndices(encodedTextRepresentations, var9, var13, UNKNOWN_OBJECT_LOSES);
               if (var14 != 0) {
                  return var14;
               }
            } else {
               if (var3.isFlavorTextType()) {
                  return 1;
               }

               if (var4.isFlavorTextType()) {
                  return -1;
               }

               var14 = compareIndices(primaryTypes, var6, var10, UNKNOWN_OBJECT_LOSES);
               if (var14 != 0) {
                  return var14;
               }

               var14 = compareIndices(exactTypes, var8, var12, UNKNOWN_OBJECT_WINS);
               if (var14 != 0) {
                  return var14;
               }

               var14 = compareIndices(nonTextRepresentations, var9, var13, UNKNOWN_OBJECT_LOSES);
               if (var14 != 0) {
                  return var14;
               }
            }

            return var3.getMimeType().compareTo(var4.getMimeType());
         }
      }

      static {
         HashMap var0 = new HashMap(4, 1.0F);
         var0.put("application/x-java-file-list", 0);
         var0.put("application/x-java-serialized-object", 1);
         var0.put("application/x-java-jvm-local-objectref", 2);
         var0.put("application/x-java-remote-object", 3);
         exactTypes = Collections.unmodifiableMap(var0);
         var0 = new HashMap(1, 1.0F);
         var0.put("application", 0);
         primaryTypes = Collections.unmodifiableMap(var0);
         var0 = new HashMap(3, 1.0F);
         var0.put(InputStream.class, 0);
         var0.put(Serializable.class, 1);
         Class var1 = DataTransferer.RMI.remoteClass();
         if (var1 != null) {
            var0.put(var1, 2);
         }

         nonTextRepresentations = Collections.unmodifiableMap(var0);
         var0 = new HashMap(16, 1.0F);
         var0.put("text/plain", 0);
         var0.put("application/x-java-serialized-object", 1);
         var0.put("text/calendar", 2);
         var0.put("text/css", 3);
         var0.put("text/directory", 4);
         var0.put("text/parityfec", 5);
         var0.put("text/rfc822-headers", 6);
         var0.put("text/t140", 7);
         var0.put("text/tab-separated-values", 8);
         var0.put("text/uri-list", 9);
         var0.put("text/richtext", 10);
         var0.put("text/enriched", 11);
         var0.put("text/rtf", 12);
         var0.put("text/html", 13);
         var0.put("text/xml", 14);
         var0.put("text/sgml", 15);
         textTypes = Collections.unmodifiableMap(var0);
         var0 = new HashMap(4, 1.0F);
         var0.put(char[].class, 0);
         var0.put(CharBuffer.class, 1);
         var0.put(String.class, 2);
         var0.put(Reader.class, 3);
         decodedTextRepresentations = Collections.unmodifiableMap(var0);
         var0 = new HashMap(3, 1.0F);
         var0.put(byte[].class, 0);
         var0.put(ByteBuffer.class, 1);
         var0.put(InputStream.class, 2);
         encodedTextRepresentations = Collections.unmodifiableMap(var0);
      }
   }

   public static class CharsetComparator extends DataTransferer.IndexedComparator {
      private static final Map charsets;
      private static String defaultEncoding;
      private static final Integer DEFAULT_CHARSET_INDEX = 2;
      private static final Integer OTHER_CHARSET_INDEX = 1;
      private static final Integer WORST_CHARSET_INDEX = 0;
      private static final Integer UNSUPPORTED_CHARSET_INDEX = Integer.MIN_VALUE;
      private static final String UNSUPPORTED_CHARSET = "UNSUPPORTED";

      public CharsetComparator() {
         this(true);
      }

      public CharsetComparator(boolean var1) {
         super(var1);
      }

      public int compare(Object var1, Object var2) {
         String var3 = null;
         String var4 = null;
         if (this.order) {
            var3 = (String)var1;
            var4 = (String)var2;
         } else {
            var3 = (String)var2;
            var4 = (String)var1;
         }

         return this.compareCharsets(var3, var4);
      }

      protected int compareCharsets(String var1, String var2) {
         var1 = getEncoding(var1);
         var2 = getEncoding(var2);
         int var3 = compareIndices(charsets, var1, var2, OTHER_CHARSET_INDEX);
         return var3 == 0 ? var2.compareTo(var1) : var3;
      }

      protected static String getEncoding(String var0) {
         if (var0 == null) {
            return null;
         } else if (!DataTransferer.isEncodingSupported(var0)) {
            return "UNSUPPORTED";
         } else {
            String var1 = DataTransferer.canonicalName(var0);
            return charsets.containsKey(var1) ? var1 : var0;
         }
      }

      static {
         HashMap var0 = new HashMap(8, 1.0F);
         var0.put(DataTransferer.canonicalName("UTF-16LE"), 4);
         var0.put(DataTransferer.canonicalName("UTF-16BE"), 5);
         var0.put(DataTransferer.canonicalName("UTF-8"), 6);
         var0.put(DataTransferer.canonicalName("UTF-16"), 7);
         var0.put(DataTransferer.canonicalName("US-ASCII"), WORST_CHARSET_INDEX);
         String var1 = DataTransferer.canonicalName(DataTransferer.getDefaultTextCharset());
         if (var0.get(defaultEncoding) == null) {
            var0.put(defaultEncoding, DEFAULT_CHARSET_INDEX);
         }

         var0.put("UNSUPPORTED", UNSUPPORTED_CHARSET_INDEX);
         charsets = Collections.unmodifiableMap(var0);
      }
   }

   public abstract static class IndexedComparator implements Comparator {
      public static final boolean SELECT_BEST = true;
      public static final boolean SELECT_WORST = false;
      protected final boolean order;

      public IndexedComparator() {
         this(true);
      }

      public IndexedComparator(boolean var1) {
         this.order = var1;
      }

      protected static int compareIndices(Map var0, Object var1, Object var2, Integer var3) {
         Integer var4 = (Integer)var0.get(var1);
         Integer var5 = (Integer)var0.get(var2);
         if (var4 == null) {
            var4 = var3;
         }

         if (var5 == null) {
            var5 = var3;
         }

         return var4.compareTo(var5);
      }

      protected static int compareLongs(Map var0, Object var1, Object var2, Long var3) {
         Long var4 = (Long)var0.get(var1);
         Long var5 = (Long)var0.get(var2);
         if (var4 == null) {
            var4 = var3;
         }

         if (var5 == null) {
            var5 = var3;
         }

         return var4.compareTo(var5);
      }
   }

   public class ReencodingInputStream extends InputStream {
      protected BufferedReader wrapped;
      protected final char[] in = new char[2];
      protected byte[] out;
      protected CharsetEncoder encoder;
      protected CharBuffer inBuf;
      protected ByteBuffer outBuf;
      protected char[] eoln;
      protected int numTerminators;
      protected boolean eos;
      protected int index;
      protected int limit;

      public ReencodingInputStream(InputStream var2, long var3, String var5, Transferable var6) throws IOException {
         Long var7 = var3;
         String var8 = null;
         if (DataTransferer.this.isLocaleDependentTextFormat(var3) && var6 != null && var6.isDataFlavorSupported(DataTransferer.javaTextEncodingFlavor)) {
            try {
               var8 = new String((byte[])((byte[])var6.getTransferData(DataTransferer.javaTextEncodingFlavor)), "UTF-8");
            } catch (UnsupportedFlavorException var14) {
            }
         } else {
            var8 = DataTransferer.this.getCharsetForTextFormat(var7);
         }

         if (var8 == null) {
            var8 = DataTransferer.getDefaultTextCharset();
         }

         this.wrapped = new BufferedReader(new InputStreamReader(var2, var8));
         if (var5 == null) {
            throw new NullPointerException("null target encoding");
         } else {
            try {
               this.encoder = Charset.forName(var5).newEncoder();
               this.out = new byte[(int)((double)(this.encoder.maxBytesPerChar() * 2.0F) + 0.5D)];
               this.inBuf = CharBuffer.wrap(this.in);
               this.outBuf = ByteBuffer.wrap(this.out);
            } catch (IllegalCharsetNameException var11) {
               throw new IOException(var11.toString());
            } catch (UnsupportedCharsetException var12) {
               throw new IOException(var12.toString());
            } catch (UnsupportedOperationException var13) {
               throw new IOException(var13.toString());
            }

            String var9 = (String)DataTransferer.nativeEOLNs.get(var7);
            if (var9 != null) {
               this.eoln = var9.toCharArray();
            }

            Integer var10 = (Integer)DataTransferer.nativeTerminators.get(var7);
            if (var10 != null) {
               this.numTerminators = var10;
            }

         }
      }

      private int readChar() throws IOException {
         int var1 = this.wrapped.read();
         if (var1 == -1) {
            this.eos = true;
            return -1;
         } else if (this.numTerminators > 0 && var1 == 0) {
            this.eos = true;
            return -1;
         } else {
            if (this.eoln != null && this.matchCharArray(this.eoln, var1)) {
               var1 = 10;
            }

            return var1;
         }
      }

      public int read() throws IOException {
         if (this.eos) {
            return -1;
         } else if (this.index >= this.limit) {
            int var1 = this.readChar();
            if (var1 == -1) {
               return -1;
            } else {
               this.in[0] = (char)var1;
               this.in[1] = 0;
               this.inBuf.limit(1);
               if (Character.isHighSurrogate((char)var1)) {
                  var1 = this.readChar();
                  if (var1 != -1) {
                     this.in[1] = (char)var1;
                     this.inBuf.limit(2);
                  }
               }

               this.inBuf.rewind();
               this.outBuf.limit(this.out.length).rewind();
               this.encoder.encode(this.inBuf, this.outBuf, false);
               this.outBuf.flip();
               this.limit = this.outBuf.limit();
               this.index = 0;
               return this.read();
            }
         } else {
            return this.out[this.index++] & 255;
         }
      }

      public int available() throws IOException {
         return this.eos ? 0 : this.limit - this.index;
      }

      public void close() throws IOException {
         this.wrapped.close();
      }

      private boolean matchCharArray(char[] var1, int var2) throws IOException {
         this.wrapped.mark(var1.length);
         int var3 = 0;
         if ((char)var2 == var1[0]) {
            for(var3 = 1; var3 < var1.length; ++var3) {
               var2 = this.wrapped.read();
               if (var2 == -1 || (char)var2 != var1[var3]) {
                  break;
               }
            }
         }

         if (var3 == var1.length) {
            return true;
         } else {
            this.wrapped.reset();
            return false;
         }
      }
   }

   private static class StandardEncodingsHolder {
      private static final SortedSet<String> standardEncodings = load();

      private static SortedSet<String> load() {
         DataTransferer.CharsetComparator var0 = new DataTransferer.CharsetComparator(false);
         TreeSet var1 = new TreeSet(var0);
         var1.add("US-ASCII");
         var1.add("ISO-8859-1");
         var1.add("UTF-8");
         var1.add("UTF-16BE");
         var1.add("UTF-16LE");
         var1.add("UTF-16");
         var1.add(DataTransferer.getDefaultTextCharset());
         return Collections.unmodifiableSortedSet(var1);
      }
   }
}
