package sun.lwawt.macosx;

import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import sun.awt.datatransfer.DataTransferer;
import sun.awt.datatransfer.ToolkitThreadBlockedHandler;

public class CDataTransferer extends DataTransferer {
   private static final Map<String, Long> predefinedClipboardNameMap;
   private static final Map<Long, String> predefinedClipboardFormatMap;
   private static final String[] predefinedClipboardNames = new String[]{"", "STRING", "FILE_NAME", "TIFF", "RICH_TEXT", "HTML", "PDF", "URL", "PNG", "JFIF", "XPICT"};
   public static final int CF_UNSUPPORTED = 0;
   public static final int CF_STRING = 1;
   public static final int CF_FILE = 2;
   public static final int CF_TIFF = 3;
   public static final int CF_RICH_TEXT = 4;
   public static final int CF_HTML = 5;
   public static final int CF_PDF = 6;
   public static final int CF_URL = 7;
   public static final int CF_PNG = 8;
   public static final int CF_JPEG = 9;
   public static final int CF_XPICT = 10;
   private static CDataTransferer fTransferer;
   private final ToolkitThreadBlockedHandler handler = new CToolkitThreadBlockedHandler();

   private CDataTransferer() {
   }

   static synchronized CDataTransferer getInstanceImpl() {
      if (fTransferer == null) {
         fTransferer = new CDataTransferer();
      }

      return fTransferer;
   }

   public String getDefaultUnicodeEncoding() {
      return "utf-16le";
   }

   public boolean isLocaleDependentTextFormat(long var1) {
      return var1 == 1L;
   }

   public boolean isFileFormat(long var1) {
      return var1 == 2L;
   }

   public boolean isImageFormat(long var1) {
      int var3 = (int)var1;
      switch(var3) {
      case 3:
      case 6:
      case 8:
      case 9:
         return true;
      case 4:
      case 5:
      case 7:
      default:
         return false;
      }
   }

   public Object translateBytes(byte[] var1, DataFlavor var2, long var3, Transferable var5) throws IOException {
      String[] var6;
      if (var3 == 7L && URL.class.equals(var2.getRepresentationClass())) {
         var6 = this.dragQueryFile(var1);
         return var6 != null && var6.length != 0 ? new URL(var6[0]) : null;
      } else {
         if (this.isUriListFlavor(var2)) {
            var6 = this.dragQueryFile(var1);
            if (var6 == null) {
               return null;
            }

            String var7 = System.getProperty("line.separator");
            StringBuilder var8 = new StringBuilder();
            if (var6.length > 0) {
               for(int var9 = 0; var9 < var6.length; ++var9) {
                  var8.append(var6[var9]);
                  var8.append(var7);
               }
            }

            var1 = var8.toString().getBytes();
            var3 = 1L;
         } else if (var3 == 1L) {
            var1 = Normalizer.normalize(new String(var1, "UTF8"), Normalizer.Form.NFC).getBytes("UTF8");
         }

         return super.translateBytes(var1, var2, var3, var5);
      }
   }

   protected synchronized Long getFormatForNativeAsLong(String var1) {
      Long var2 = (Long)predefinedClipboardNameMap.get(var1);
      if (var2 == null) {
         if (GraphicsEnvironment.getLocalGraphicsEnvironment().isHeadlessInstance()) {
            return -1L;
         }

         var2 = this.registerFormatWithPasteboard(var1);
         predefinedClipboardNameMap.put(var1, var2);
         predefinedClipboardFormatMap.put(var2, var1);
      }

      return var2;
   }

   private native long registerFormatWithPasteboard(String var1);

   private native String formatForIndex(long var1);

   protected String getNativeForFormat(long var1) {
      String var3 = null;
      if (var1 >= 0L && var1 < (long)predefinedClipboardNames.length) {
         var3 = predefinedClipboardNames[(int)var1];
      } else {
         Long var4 = var1;
         var3 = (String)predefinedClipboardFormatMap.get(var4);
         if (var3 == null) {
            var3 = this.formatForIndex(var1);
            if (var3 != null) {
               predefinedClipboardNameMap.put(var3, var4);
               predefinedClipboardFormatMap.put(var4, var3);
            }
         }
      }

      if (var3 == null) {
         var3 = predefinedClipboardNames[0];
      }

      return var3;
   }

   public ToolkitThreadBlockedHandler getToolkitThreadBlockedHandler() {
      return this.handler;
   }

   protected byte[] imageToPlatformBytes(Image var1, long var2) {
      return CImage.getCreator().getPlatformImageBytes(var1);
   }

   private static native String[] nativeDragQueryFile(byte[] var0);

   protected String[] dragQueryFile(byte[] var1) {
      if (var1 == null) {
         return null;
      } else {
         return (new String(var1)).startsWith("Unsupported type") ? null : nativeDragQueryFile(var1);
      }
   }

   protected Image platformImageBytesToImage(byte[] var1, long var2) throws IOException {
      return CImage.getCreator().createImageFromPlatformImageBytes(var1);
   }

   protected ByteArrayOutputStream convertFileListToBytes(ArrayList<String> var1) throws IOException {
      ByteArrayOutputStream var2 = new ByteArrayOutputStream();
      Iterator var3 = var1.iterator();

      while(var3.hasNext()) {
         String var4 = (String)var3.next();
         byte[] var5 = var4.getBytes();
         var2.write(var5, 0, var5.length);
         var2.write(0);
      }

      return var2;
   }

   protected boolean isURIListFormat(long var1) {
      String var3 = this.getNativeForFormat(var1);
      if (var3 == null) {
         return false;
      } else {
         try {
            DataFlavor var4 = new DataFlavor(var3);
            if (this.isUriListFlavor(var4)) {
               return true;
            }
         } catch (Exception var5) {
         }

         return false;
      }
   }

   private boolean isUriListFlavor(DataFlavor var1) {
      return var1.getPrimaryType().equals("text") && var1.getSubType().equals("uri-list");
   }

   static {
      HashMap var0 = new HashMap(predefinedClipboardNames.length, 1.0F);
      HashMap var1 = new HashMap(predefinedClipboardNames.length, 1.0F);

      for(int var2 = 1; var2 < predefinedClipboardNames.length; ++var2) {
         var0.put(predefinedClipboardNames[var2], (long)var2);
         var1.put((long)var2, predefinedClipboardNames[var2]);
      }

      predefinedClipboardNameMap = Collections.synchronizedMap(var0);
      predefinedClipboardFormatMap = Collections.synchronizedMap(var1);
   }
}
