package java.awt.datatransfer;

import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ref.SoftReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import sun.awt.AppContext;
import sun.awt.datatransfer.DataTransferer;

public final class SystemFlavorMap implements FlavorMap, FlavorTable {
   private static String JavaMIME = "JAVA_DATAFLAVOR:";
   private static final Object FLAVOR_MAP_KEY = new Object();
   private static final String keyValueSeparators = "=: \t\r\n\f";
   private static final String strictKeyValueSeparators = "=:";
   private static final String whiteSpaceChars = " \t\r\n\f";
   private static final String[] UNICODE_TEXT_CLASSES = new String[]{"java.io.Reader", "java.lang.String", "java.nio.CharBuffer", "\"[C\""};
   private static final String[] ENCODED_TEXT_CLASSES = new String[]{"java.io.InputStream", "java.nio.ByteBuffer", "\"[B\""};
   private static final String TEXT_PLAIN_BASE_TYPE = "text/plain";
   private static final String HTML_TEXT_BASE_TYPE = "text/html";
   private final Map<String, LinkedHashSet<DataFlavor>> nativeToFlavor = new HashMap();
   private final Map<DataFlavor, LinkedHashSet<String>> flavorToNative = new HashMap();
   private Map<String, LinkedHashSet<String>> textTypeToNative = new HashMap();
   private boolean isMapInitialized = false;
   private final SystemFlavorMap.SoftCache<DataFlavor, String> nativesForFlavorCache = new SystemFlavorMap.SoftCache();
   private final SystemFlavorMap.SoftCache<String, DataFlavor> flavorsForNativeCache = new SystemFlavorMap.SoftCache();
   private Set<Object> disabledMappingGenerationKeys = new HashSet();
   private static final String[] htmlDocumntTypes = new String[]{"all", "selection", "fragment"};

   private Map<String, LinkedHashSet<DataFlavor>> getNativeToFlavor() {
      if (!this.isMapInitialized) {
         this.initSystemFlavorMap();
      }

      return this.nativeToFlavor;
   }

   private synchronized Map<DataFlavor, LinkedHashSet<String>> getFlavorToNative() {
      if (!this.isMapInitialized) {
         this.initSystemFlavorMap();
      }

      return this.flavorToNative;
   }

   private synchronized Map<String, LinkedHashSet<String>> getTextTypeToNative() {
      if (!this.isMapInitialized) {
         this.initSystemFlavorMap();
         this.textTypeToNative = Collections.unmodifiableMap(this.textTypeToNative);
      }

      return this.textTypeToNative;
   }

   public static FlavorMap getDefaultFlavorMap() {
      AppContext var0 = AppContext.getAppContext();
      Object var1 = (FlavorMap)var0.get(FLAVOR_MAP_KEY);
      if (var1 == null) {
         var1 = new SystemFlavorMap();
         var0.put(FLAVOR_MAP_KEY, var1);
      }

      return (FlavorMap)var1;
   }

   private SystemFlavorMap() {
   }

   private void initSystemFlavorMap() {
      if (!this.isMapInitialized) {
         this.isMapInitialized = true;
         BufferedReader var1 = (BufferedReader)AccessController.doPrivileged(new PrivilegedAction<BufferedReader>() {
            public BufferedReader run() {
               String var1 = System.getProperty("java.home") + File.separator + "lib" + File.separator + "flavormap.properties";

               try {
                  return new BufferedReader(new InputStreamReader((new File(var1)).toURI().toURL().openStream(), "ISO-8859-1"));
               } catch (MalformedURLException var3) {
                  System.err.println("MalformedURLException:" + var3 + " while loading default flavormap.properties file:" + var1);
               } catch (IOException var4) {
                  System.err.println("IOException:" + var4 + " while loading default flavormap.properties file:" + var1);
               }

               return null;
            }
         });
         String var2 = (String)AccessController.doPrivileged(new PrivilegedAction<String>() {
            public String run() {
               return Toolkit.getProperty("AWT.DnD.flavorMapFileURL", (String)null);
            }
         });
         if (var1 != null) {
            try {
               this.parseAndStoreReader(var1);
            } catch (IOException var9) {
               System.err.println("IOException:" + var9 + " while parsing default flavormap.properties file");
            }
         }

         BufferedReader var3 = null;
         if (var2 != null) {
            try {
               var3 = new BufferedReader(new InputStreamReader((new URL(var2)).openStream(), "ISO-8859-1"));
            } catch (MalformedURLException var6) {
               System.err.println("MalformedURLException:" + var6 + " while reading AWT.DnD.flavorMapFileURL:" + var2);
            } catch (IOException var7) {
               System.err.println("IOException:" + var7 + " while reading AWT.DnD.flavorMapFileURL:" + var2);
            } catch (SecurityException var8) {
            }
         }

         if (var3 != null) {
            try {
               this.parseAndStoreReader(var3);
            } catch (IOException var5) {
               System.err.println("IOException:" + var5 + " while parsing AWT.DnD.flavorMapFileURL");
            }
         }

      }
   }

   private void parseAndStoreReader(BufferedReader var1) throws IOException {
      while(true) {
         String var2 = var1.readLine();
         if (var2 == null) {
            return;
         }

         if (var2.length() > 0) {
            char var3 = var2.charAt(0);
            if (var3 != '#' && var3 != '!') {
               int var6;
               while(this.continueLine(var2)) {
                  String var4 = var1.readLine();
                  if (var4 == null) {
                     var4 = "";
                  }

                  String var5 = var2.substring(0, var2.length() - 1);

                  for(var6 = 0; var6 < var4.length() && " \t\r\n\f".indexOf(var4.charAt(var6)) != -1; ++var6) {
                  }

                  var4 = var4.substring(var6, var4.length());
                  var2 = var5 + var4;
               }

               int var17 = var2.length();

               int var18;
               for(var18 = 0; var18 < var17 && " \t\r\n\f".indexOf(var2.charAt(var18)) != -1; ++var18) {
               }

               if (var18 != var17) {
                  for(var6 = var18; var6 < var17; ++var6) {
                     char var7 = var2.charAt(var6);
                     if (var7 == '\\') {
                        ++var6;
                     } else if ("=: \t\r\n\f".indexOf(var7) != -1) {
                        break;
                     }
                  }

                  int var19;
                  for(var19 = var6; var19 < var17 && " \t\r\n\f".indexOf(var2.charAt(var19)) != -1; ++var19) {
                  }

                  if (var19 < var17 && "=:".indexOf(var2.charAt(var19)) != -1) {
                     ++var19;
                  }

                  while(var19 < var17 && " \t\r\n\f".indexOf(var2.charAt(var19)) != -1) {
                     ++var19;
                  }

                  String var8 = var2.substring(var18, var6);
                  String var9 = var6 < var17 ? var2.substring(var19, var17) : "";
                  var8 = this.loadConvert(var8);
                  var9 = this.loadConvert(var9);

                  try {
                     MimeType var10 = new MimeType(var9);
                     if ("text".equals(var10.getPrimaryType())) {
                        String var11 = var10.getParameter("charset");
                        if (DataTransferer.doesSubtypeSupportCharset(var10.getSubType(), var11)) {
                           DataTransferer var12 = DataTransferer.getInstance();
                           if (var12 != null) {
                              var12.registerTextFlavorProperties(var8, var11, var10.getParameter("eoln"), var10.getParameter("terminators"));
                           }
                        }

                        var10.removeParameter("charset");
                        var10.removeParameter("class");
                        var10.removeParameter("eoln");
                        var10.removeParameter("terminators");
                        var9 = var10.toString();
                     }
                  } catch (MimeTypeParseException var16) {
                     var16.printStackTrace();
                     continue;
                  }

                  DataFlavor var20;
                  try {
                     var20 = new DataFlavor(var9);
                  } catch (Exception var15) {
                     try {
                        var20 = new DataFlavor(var9, (String)null);
                     } catch (Exception var14) {
                        var14.printStackTrace();
                        continue;
                     }
                  }

                  LinkedHashSet var21 = new LinkedHashSet();
                  var21.add(var20);
                  if ("text".equals(var20.getPrimaryType())) {
                     var21.addAll(convertMimeTypeToDataFlavors(var9));
                     this.store(var20.mimeType.getBaseType(), var8, this.getTextTypeToNative());
                  }

                  Iterator var22 = var21.iterator();

                  while(var22.hasNext()) {
                     DataFlavor var13 = (DataFlavor)var22.next();
                     this.store(var13, var8, this.getFlavorToNative());
                     this.store(var8, var13, this.getNativeToFlavor());
                  }
               }
            }
         }
      }
   }

   private boolean continueLine(String var1) {
      int var2 = 0;

      for(int var3 = var1.length() - 1; var3 >= 0 && var1.charAt(var3--) == '\\'; ++var2) {
      }

      return var2 % 2 == 1;
   }

   private String loadConvert(String var1) {
      int var3 = var1.length();
      StringBuilder var4 = new StringBuilder(var3);
      int var5 = 0;

      while(true) {
         while(true) {
            while(var5 < var3) {
               char var2 = var1.charAt(var5++);
               if (var2 == '\\') {
                  var2 = var1.charAt(var5++);
                  if (var2 == 'u') {
                     int var6 = 0;

                     for(int var7 = 0; var7 < 4; ++var7) {
                        var2 = var1.charAt(var5++);
                        switch(var2) {
                        case '0':
                        case '1':
                        case '2':
                        case '3':
                        case '4':
                        case '5':
                        case '6':
                        case '7':
                        case '8':
                        case '9':
                           var6 = (var6 << 4) + var2 - 48;
                           break;
                        case ':':
                        case ';':
                        case '<':
                        case '=':
                        case '>':
                        case '?':
                        case '@':
                        case 'G':
                        case 'H':
                        case 'I':
                        case 'J':
                        case 'K':
                        case 'L':
                        case 'M':
                        case 'N':
                        case 'O':
                        case 'P':
                        case 'Q':
                        case 'R':
                        case 'S':
                        case 'T':
                        case 'U':
                        case 'V':
                        case 'W':
                        case 'X':
                        case 'Y':
                        case 'Z':
                        case '[':
                        case '\\':
                        case ']':
                        case '^':
                        case '_':
                        case '`':
                        default:
                           throw new IllegalArgumentException("Malformed \\uxxxx encoding.");
                        case 'A':
                        case 'B':
                        case 'C':
                        case 'D':
                        case 'E':
                        case 'F':
                           var6 = (var6 << 4) + 10 + var2 - 65;
                           break;
                        case 'a':
                        case 'b':
                        case 'c':
                        case 'd':
                        case 'e':
                        case 'f':
                           var6 = (var6 << 4) + 10 + var2 - 97;
                        }
                     }

                     var4.append((char)var6);
                  } else {
                     if (var2 == 't') {
                        var2 = '\t';
                     } else if (var2 == 'r') {
                        var2 = '\r';
                     } else if (var2 == 'n') {
                        var2 = '\n';
                     } else if (var2 == 'f') {
                        var2 = '\f';
                     }

                     var4.append(var2);
                  }
               } else {
                  var4.append(var2);
               }
            }

            return var4.toString();
         }
      }
   }

   private <H, L> void store(H var1, L var2, Map<H, LinkedHashSet<L>> var3) {
      LinkedHashSet var4 = (LinkedHashSet)var3.get(var1);
      if (var4 == null) {
         var4 = new LinkedHashSet(1);
         var3.put(var1, var4);
      }

      if (!var4.contains(var2)) {
         var4.add(var2);
      }

   }

   private LinkedHashSet<DataFlavor> nativeToFlavorLookup(String var1) {
      LinkedHashSet var2 = (LinkedHashSet)this.getNativeToFlavor().get(var1);
      if (var1 != null && !this.disabledMappingGenerationKeys.contains(var1)) {
         DataTransferer var3 = DataTransferer.getInstance();
         if (var3 != null) {
            LinkedHashSet var4 = var3.getPlatformMappingsForNative(var1);
            if (!var4.isEmpty()) {
               if (var2 != null) {
                  var4.addAll(var2);
               }

               var2 = var4;
            }
         }
      }

      if (var2 == null && isJavaMIMEType(var1)) {
         String var7 = decodeJavaMIMEType(var1);
         DataFlavor var8 = null;

         try {
            var8 = new DataFlavor(var7);
         } catch (Exception var6) {
            System.err.println("Exception \"" + var6.getClass().getName() + ": " + var6.getMessage() + "\"while constructing DataFlavor for: " + var7);
         }

         if (var8 != null) {
            var2 = new LinkedHashSet(1);
            this.getNativeToFlavor().put(var1, var2);
            var2.add(var8);
            this.flavorsForNativeCache.remove(var1);
            LinkedHashSet var5 = (LinkedHashSet)this.getFlavorToNative().get(var8);
            if (var5 == null) {
               var5 = new LinkedHashSet(1);
               this.getFlavorToNative().put(var8, var5);
            }

            var5.add(var1);
            this.nativesForFlavorCache.remove(var8);
         }
      }

      return var2 != null ? var2 : new LinkedHashSet(0);
   }

   private LinkedHashSet<String> flavorToNativeLookup(DataFlavor var1, boolean var2) {
      LinkedHashSet var3 = (LinkedHashSet)this.getFlavorToNative().get(var1);
      LinkedHashSet var5;
      if (var1 != null && !this.disabledMappingGenerationKeys.contains(var1)) {
         DataTransferer var4 = DataTransferer.getInstance();
         if (var4 != null) {
            var5 = var4.getPlatformMappingsForFlavor(var1);
            if (!var5.isEmpty()) {
               if (var3 != null) {
                  var5.addAll(var3);
               }

               var3 = var5;
            }
         }
      }

      if (var3 == null) {
         if (var2) {
            String var6 = encodeDataFlavor(var1);
            var3 = new LinkedHashSet(1);
            this.getFlavorToNative().put(var1, var3);
            var3.add(var6);
            var5 = (LinkedHashSet)this.getNativeToFlavor().get(var6);
            if (var5 == null) {
               var5 = new LinkedHashSet(1);
               this.getNativeToFlavor().put(var6, var5);
            }

            var5.add(var1);
            this.nativesForFlavorCache.remove(var1);
            this.flavorsForNativeCache.remove(var6);
         } else {
            var3 = new LinkedHashSet(0);
         }
      }

      return new LinkedHashSet(var3);
   }

   public synchronized List<String> getNativesForFlavor(DataFlavor var1) {
      LinkedHashSet var2 = this.nativesForFlavorCache.check(var1);
      if (var2 != null) {
         return new ArrayList(var2);
      } else {
         if (var1 == null) {
            var2 = new LinkedHashSet(this.getNativeToFlavor().keySet());
         } else if (this.disabledMappingGenerationKeys.contains(var1)) {
            var2 = this.flavorToNativeLookup(var1, false);
         } else if (DataTransferer.isFlavorCharsetTextType(var1)) {
            var2 = new LinkedHashSet(0);
            LinkedHashSet var3;
            if ("text".equals(var1.getPrimaryType())) {
               var3 = (LinkedHashSet)this.getTextTypeToNative().get(var1.mimeType.getBaseType());
               if (var3 != null) {
                  var2.addAll(var3);
               }
            }

            var3 = (LinkedHashSet)this.getTextTypeToNative().get("text/plain");
            if (var3 != null) {
               var2.addAll(var3);
            }

            if (var2.isEmpty()) {
               var2 = this.flavorToNativeLookup(var1, true);
            } else {
               var2.addAll(this.flavorToNativeLookup(var1, false));
            }
         } else if (DataTransferer.isFlavorNoncharsetTextType(var1)) {
            var2 = (LinkedHashSet)this.getTextTypeToNative().get(var1.mimeType.getBaseType());
            if (var2 != null && !var2.isEmpty()) {
               var2.addAll(this.flavorToNativeLookup(var1, false));
            } else {
               var2 = this.flavorToNativeLookup(var1, true);
            }
         } else {
            var2 = this.flavorToNativeLookup(var1, true);
         }

         this.nativesForFlavorCache.put(var1, var2);
         return new ArrayList(var2);
      }
   }

   public synchronized List<DataFlavor> getFlavorsForNative(String var1) {
      LinkedHashSet var2 = this.flavorsForNativeCache.check(var1);
      if (var2 != null) {
         return new ArrayList(var2);
      } else {
         var2 = new LinkedHashSet();
         if (var1 == null) {
            Iterator var3 = this.getNativesForFlavor((DataFlavor)null).iterator();

            while(var3.hasNext()) {
               String var4 = (String)var3.next();
               var2.addAll(this.getFlavorsForNative(var4));
            }
         } else {
            LinkedHashSet var8 = this.nativeToFlavorLookup(var1);
            if (this.disabledMappingGenerationKeys.contains(var1)) {
               return new ArrayList(var8);
            }

            LinkedHashSet var9 = this.nativeToFlavorLookup(var1);
            Iterator var5 = var9.iterator();

            while(var5.hasNext()) {
               DataFlavor var6 = (DataFlavor)var5.next();
               var2.add(var6);
               if ("text".equals(var6.getPrimaryType())) {
                  String var7 = var6.mimeType.getBaseType();
                  var2.addAll(convertMimeTypeToDataFlavors(var7));
               }
            }
         }

         this.flavorsForNativeCache.put(var1, var2);
         return new ArrayList(var2);
      }
   }

   private static Set<DataFlavor> convertMimeTypeToDataFlavors(String var0) {
      LinkedHashSet var1 = new LinkedHashSet();
      String var2 = null;

      try {
         MimeType var3 = new MimeType(var0);
         var2 = var3.getSubType();
      } catch (MimeTypeParseException var18) {
      }

      int var4;
      int var5;
      String var6;
      String[] var19;
      if (DataTransferer.doesSubtypeSupportCharset(var2, (String)null)) {
         if ("text/plain".equals(var0)) {
            var1.add(DataFlavor.stringFlavor);
         }

         var19 = UNICODE_TEXT_CLASSES;
         var4 = var19.length;

         for(var5 = 0; var5 < var4; ++var5) {
            var6 = var19[var5];
            String var7 = var0 + ";charset=Unicode;class=" + var6;
            LinkedHashSet var8 = handleHtmlMimeTypes(var0, var7);

            DataFlavor var11;
            for(Iterator var9 = var8.iterator(); var9.hasNext(); var1.add(var11)) {
               String var10 = (String)var9.next();
               var11 = null;

               try {
                  var11 = new DataFlavor(var10);
               } catch (ClassNotFoundException var17) {
               }
            }
         }

         Iterator var20 = DataTransferer.standardEncodings().iterator();

         while(var20.hasNext()) {
            String var21 = (String)var20.next();
            String[] var22 = ENCODED_TEXT_CLASSES;
            int var23 = var22.length;

            for(int var24 = 0; var24 < var23; ++var24) {
               String var26 = var22[var24];
               String var27 = var0 + ";charset=" + var21 + ";class=" + var26;
               LinkedHashSet var28 = handleHtmlMimeTypes(var0, var27);

               DataFlavor var13;
               for(Iterator var29 = var28.iterator(); var29.hasNext(); var1.add(var13)) {
                  String var12 = (String)var29.next();
                  var13 = null;

                  try {
                     var13 = new DataFlavor(var12);
                     if (var13.equals(DataFlavor.plainTextFlavor)) {
                        var13 = DataFlavor.plainTextFlavor;
                     }
                  } catch (ClassNotFoundException var16) {
                  }
               }
            }
         }

         if ("text/plain".equals(var0)) {
            var1.add(DataFlavor.plainTextFlavor);
         }
      } else {
         var19 = ENCODED_TEXT_CLASSES;
         var4 = var19.length;

         for(var5 = 0; var5 < var4; ++var5) {
            var6 = var19[var5];
            DataFlavor var25 = null;

            try {
               var25 = new DataFlavor(var0 + ";class=" + var6);
            } catch (ClassNotFoundException var15) {
            }

            var1.add(var25);
         }
      }

      return var1;
   }

   private static LinkedHashSet<String> handleHtmlMimeTypes(String var0, String var1) {
      LinkedHashSet var2 = new LinkedHashSet();
      if ("text/html".equals(var0)) {
         String[] var3 = htmlDocumntTypes;
         int var4 = var3.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            String var6 = var3[var5];
            var2.add(var1 + ";document=" + var6);
         }
      } else {
         var2.add(var1);
      }

      return var2;
   }

   public synchronized Map<DataFlavor, String> getNativesForFlavors(DataFlavor[] var1) {
      if (var1 == null) {
         List var2 = this.getFlavorsForNative((String)null);
         var1 = new DataFlavor[var2.size()];
         var2.toArray(var1);
      }

      HashMap var9 = new HashMap(var1.length, 1.0F);
      DataFlavor[] var3 = var1;
      int var4 = var1.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         DataFlavor var6 = var3[var5];
         List var7 = this.getNativesForFlavor(var6);
         String var8 = var7.isEmpty() ? null : (String)var7.get(0);
         var9.put(var6, var8);
      }

      return var9;
   }

   public synchronized Map<String, DataFlavor> getFlavorsForNatives(String[] var1) {
      if (var1 == null) {
         List var2 = this.getNativesForFlavor((DataFlavor)null);
         var1 = new String[var2.size()];
         var2.toArray(var1);
      }

      HashMap var9 = new HashMap(var1.length, 1.0F);
      String[] var3 = var1;
      int var4 = var1.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         String var6 = var3[var5];
         List var7 = this.getFlavorsForNative(var6);
         DataFlavor var8 = var7.isEmpty() ? null : (DataFlavor)var7.get(0);
         var9.put(var6, var8);
      }

      return var9;
   }

   public synchronized void addUnencodedNativeForFlavor(DataFlavor var1, String var2) {
      Objects.requireNonNull(var2, (String)"Null native not permitted");
      Objects.requireNonNull(var1, (String)"Null flavor not permitted");
      LinkedHashSet var3 = (LinkedHashSet)this.getFlavorToNative().get(var1);
      if (var3 == null) {
         var3 = new LinkedHashSet(1);
         this.getFlavorToNative().put(var1, var3);
      }

      var3.add(var2);
      this.nativesForFlavorCache.remove(var1);
   }

   public synchronized void setNativesForFlavor(DataFlavor var1, String[] var2) {
      Objects.requireNonNull(var2, (String)"Null natives not permitted");
      Objects.requireNonNull(var1, (String)"Null flavors not permitted");
      this.getFlavorToNative().remove(var1);
      String[] var3 = var2;
      int var4 = var2.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         String var6 = var3[var5];
         this.addUnencodedNativeForFlavor(var1, var6);
      }

      this.disabledMappingGenerationKeys.add(var1);
      this.nativesForFlavorCache.remove(var1);
   }

   public synchronized void addFlavorForUnencodedNative(String var1, DataFlavor var2) {
      Objects.requireNonNull(var1, (String)"Null native not permitted");
      Objects.requireNonNull(var2, (String)"Null flavor not permitted");
      LinkedHashSet var3 = (LinkedHashSet)this.getNativeToFlavor().get(var1);
      if (var3 == null) {
         var3 = new LinkedHashSet(1);
         this.getNativeToFlavor().put(var1, var3);
      }

      var3.add(var2);
      this.flavorsForNativeCache.remove(var1);
   }

   public synchronized void setFlavorsForNative(String var1, DataFlavor[] var2) {
      Objects.requireNonNull(var1, (String)"Null native not permitted");
      Objects.requireNonNull(var2, (String)"Null flavors not permitted");
      this.getNativeToFlavor().remove(var1);
      DataFlavor[] var3 = var2;
      int var4 = var2.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         DataFlavor var6 = var3[var5];
         this.addFlavorForUnencodedNative(var1, var6);
      }

      this.disabledMappingGenerationKeys.add(var1);
      this.flavorsForNativeCache.remove(var1);
   }

   public static String encodeJavaMIMEType(String var0) {
      return var0 != null ? JavaMIME + var0 : null;
   }

   public static String encodeDataFlavor(DataFlavor var0) {
      return var0 != null ? encodeJavaMIMEType(var0.getMimeType()) : null;
   }

   public static boolean isJavaMIMEType(String var0) {
      return var0 != null && var0.startsWith(JavaMIME, 0);
   }

   public static String decodeJavaMIMEType(String var0) {
      return isJavaMIMEType(var0) ? var0.substring(JavaMIME.length(), var0.length()).trim() : null;
   }

   public static DataFlavor decodeDataFlavor(String var0) throws ClassNotFoundException {
      String var1 = decodeJavaMIMEType(var0);
      return var1 != null ? new DataFlavor(var1) : null;
   }

   private static final class SoftCache<K, V> {
      Map<K, SoftReference<LinkedHashSet<V>>> cache;

      private SoftCache() {
      }

      public void put(K var1, LinkedHashSet<V> var2) {
         if (this.cache == null) {
            this.cache = new HashMap(1);
         }

         this.cache.put(var1, new SoftReference(var2));
      }

      public void remove(K var1) {
         if (this.cache != null) {
            this.cache.remove((Object)null);
            this.cache.remove(var1);
         }
      }

      public LinkedHashSet<V> check(K var1) {
         if (this.cache == null) {
            return null;
         } else {
            SoftReference var2 = (SoftReference)this.cache.get(var1);
            return var2 != null ? (LinkedHashSet)var2.get() : null;
         }
      }

      // $FF: synthetic method
      SoftCache(Object var1) {
         this();
      }
   }
}
