package java.awt;

import java.beans.ConstructorProperties;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedExceptionAction;
import java.util.Hashtable;
import java.util.Properties;
import java.util.StringTokenizer;
import sun.awt.AWTAccessor;
import sun.java2d.Disposer;
import sun.java2d.DisposerRecord;
import sun.security.action.GetPropertyAction;
import sun.util.logging.PlatformLogger;

public class Cursor implements Serializable {
   public static final int DEFAULT_CURSOR = 0;
   public static final int CROSSHAIR_CURSOR = 1;
   public static final int TEXT_CURSOR = 2;
   public static final int WAIT_CURSOR = 3;
   public static final int SW_RESIZE_CURSOR = 4;
   public static final int SE_RESIZE_CURSOR = 5;
   public static final int NW_RESIZE_CURSOR = 6;
   public static final int NE_RESIZE_CURSOR = 7;
   public static final int N_RESIZE_CURSOR = 8;
   public static final int S_RESIZE_CURSOR = 9;
   public static final int W_RESIZE_CURSOR = 10;
   public static final int E_RESIZE_CURSOR = 11;
   public static final int HAND_CURSOR = 12;
   public static final int MOVE_CURSOR = 13;
   /** @deprecated */
   @Deprecated
   protected static Cursor[] predefined = new Cursor[14];
   private static final Cursor[] predefinedPrivate = new Cursor[14];
   static final String[][] cursorProperties = new String[][]{{"AWT.DefaultCursor", "Default Cursor"}, {"AWT.CrosshairCursor", "Crosshair Cursor"}, {"AWT.TextCursor", "Text Cursor"}, {"AWT.WaitCursor", "Wait Cursor"}, {"AWT.SWResizeCursor", "Southwest Resize Cursor"}, {"AWT.SEResizeCursor", "Southeast Resize Cursor"}, {"AWT.NWResizeCursor", "Northwest Resize Cursor"}, {"AWT.NEResizeCursor", "Northeast Resize Cursor"}, {"AWT.NResizeCursor", "North Resize Cursor"}, {"AWT.SResizeCursor", "South Resize Cursor"}, {"AWT.WResizeCursor", "West Resize Cursor"}, {"AWT.EResizeCursor", "East Resize Cursor"}, {"AWT.HandCursor", "Hand Cursor"}, {"AWT.MoveCursor", "Move Cursor"}};
   int type = 0;
   public static final int CUSTOM_CURSOR = -1;
   private static final Hashtable<String, Cursor> systemCustomCursors = new Hashtable(1);
   private static final String systemCustomCursorDirPrefix = initCursorDir();
   private static final String systemCustomCursorPropertiesFile;
   private static Properties systemCustomCursorProperties;
   private static final String CursorDotPrefix = "Cursor.";
   private static final String DotFileSuffix = ".File";
   private static final String DotHotspotSuffix = ".HotSpot";
   private static final String DotNameSuffix = ".Name";
   private static final long serialVersionUID = 8028237497568985504L;
   private static final PlatformLogger log;
   private transient long pData;
   private transient Object anchor = new Object();
   transient Cursor.CursorDisposer disposer;
   protected String name;

   private static String initCursorDir() {
      String var0 = (String)AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction("java.home")));
      return var0 + File.separator + "lib" + File.separator + "images" + File.separator + "cursors" + File.separator;
   }

   private static native void initIDs();

   private void setPData(long var1) {
      this.pData = var1;
      if (!GraphicsEnvironment.isHeadless()) {
         if (this.disposer == null) {
            this.disposer = new Cursor.CursorDisposer(var1);
            if (this.anchor == null) {
               this.anchor = new Object();
            }

            Disposer.addRecord(this.anchor, this.disposer);
         } else {
            this.disposer.pData = var1;
         }

      }
   }

   public static Cursor getPredefinedCursor(int var0) {
      if (var0 >= 0 && var0 <= 13) {
         Cursor var1 = predefinedPrivate[var0];
         if (var1 == null) {
            predefinedPrivate[var0] = var1 = new Cursor(var0);
         }

         if (predefined[var0] == null) {
            predefined[var0] = var1;
         }

         return var1;
      } else {
         throw new IllegalArgumentException("illegal cursor type");
      }
   }

   public static Cursor getSystemCustomCursor(String var0) throws AWTException, HeadlessException {
      GraphicsEnvironment.checkHeadless();
      Cursor var1 = (Cursor)systemCustomCursors.get(var0);
      if (var1 == null) {
         synchronized(systemCustomCursors) {
            if (systemCustomCursorProperties == null) {
               loadSystemCustomCursorProperties();
            }
         }

         String var2 = "Cursor." + var0;
         String var3 = var2 + ".File";
         if (!systemCustomCursorProperties.containsKey(var3)) {
            if (log.isLoggable(PlatformLogger.Level.FINER)) {
               log.finer("Cursor.getSystemCustomCursor(" + var0 + ") returned null");
            }

            return null;
         }

         final String var4 = systemCustomCursorProperties.getProperty(var3);
         final String var5 = systemCustomCursorProperties.getProperty(var2 + ".Name");
         if (var5 == null) {
            var5 = var0;
         }

         String var6 = systemCustomCursorProperties.getProperty(var2 + ".HotSpot");
         if (var6 == null) {
            throw new AWTException("no hotspot property defined for cursor: " + var0);
         }

         StringTokenizer var7 = new StringTokenizer(var6, ",");
         if (var7.countTokens() != 2) {
            throw new AWTException("failed to parse hotspot property for cursor: " + var0);
         }

         boolean var8 = false;
         boolean var9 = false;

         final int var16;
         final int var17;
         try {
            var16 = Integer.parseInt(var7.nextToken());
            var17 = Integer.parseInt(var7.nextToken());
         } catch (NumberFormatException var14) {
            throw new AWTException("failed to parse hotspot property for cursor: " + var0);
         }

         try {
            var1 = (Cursor)AccessController.doPrivileged(new PrivilegedExceptionAction<Cursor>() {
               public Cursor run() throws Exception {
                  Toolkit var1 = Toolkit.getDefaultToolkit();
                  Image var2 = var1.getImage(Cursor.systemCustomCursorDirPrefix + var4);
                  return var1.createCustomCursor(var2, new Point(var16, var17), var5);
               }
            });
         } catch (Exception var13) {
            throw new AWTException("Exception: " + var13.getClass() + " " + var13.getMessage() + " occurred while creating cursor " + var0);
         }

         if (var1 == null) {
            if (log.isLoggable(PlatformLogger.Level.FINER)) {
               log.finer("Cursor.getSystemCustomCursor(" + var0 + ") returned null");
            }
         } else {
            systemCustomCursors.put(var0, var1);
         }
      }

      return var1;
   }

   public static Cursor getDefaultCursor() {
      return getPredefinedCursor(0);
   }

   @ConstructorProperties({"type"})
   public Cursor(int var1) {
      if (var1 >= 0 && var1 <= 13) {
         this.type = var1;
         this.name = Toolkit.getProperty(cursorProperties[var1][0], cursorProperties[var1][1]);
      } else {
         throw new IllegalArgumentException("illegal cursor type");
      }
   }

   protected Cursor(String var1) {
      this.type = -1;
      this.name = var1;
   }

   public int getType() {
      return this.type;
   }

   public String getName() {
      return this.name;
   }

   public String toString() {
      return this.getClass().getName() + "[" + this.getName() + "]";
   }

   private static void loadSystemCustomCursorProperties() throws AWTException {
      synchronized(systemCustomCursors) {
         systemCustomCursorProperties = new Properties();

         try {
            AccessController.doPrivileged(new PrivilegedExceptionAction<Object>() {
               public Object run() throws Exception {
                  FileInputStream var1 = null;

                  try {
                     var1 = new FileInputStream(Cursor.systemCustomCursorPropertiesFile);
                     Cursor.systemCustomCursorProperties.load((InputStream)var1);
                  } finally {
                     if (var1 != null) {
                        var1.close();
                     }

                  }

                  return null;
               }
            });
         } catch (Exception var3) {
            systemCustomCursorProperties = null;
            throw new AWTException("Exception: " + var3.getClass() + " " + var3.getMessage() + " occurred while loading: " + systemCustomCursorPropertiesFile);
         }

      }
   }

   private static native void finalizeImpl(long var0);

   static {
      systemCustomCursorPropertiesFile = systemCustomCursorDirPrefix + "cursors.properties";
      systemCustomCursorProperties = null;
      log = PlatformLogger.getLogger("java.awt.Cursor");
      Toolkit.loadLibraries();
      if (!GraphicsEnvironment.isHeadless()) {
         initIDs();
      }

      AWTAccessor.setCursorAccessor(new AWTAccessor.CursorAccessor() {
         public long getPData(Cursor var1) {
            return var1.pData;
         }

         public void setPData(Cursor var1, long var2) {
            var1.pData = var2;
         }

         public int getType(Cursor var1) {
            return var1.type;
         }
      });
   }

   static class CursorDisposer implements DisposerRecord {
      volatile long pData;

      public CursorDisposer(long var1) {
         this.pData = var1;
      }

      public void dispose() {
         if (this.pData != 0L) {
            Cursor.finalizeImpl(this.pData);
         }

      }
   }
}
