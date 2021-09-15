package java.awt.event;

import java.awt.Component;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.util.Arrays;
import sun.awt.AWTAccessor;
import sun.security.util.SecurityConstants;
import sun.util.logging.PlatformLogger;

public abstract class InputEvent extends ComponentEvent {
   private static final PlatformLogger logger = PlatformLogger.getLogger("java.awt.event.InputEvent");
   public static final int SHIFT_MASK = 1;
   public static final int CTRL_MASK = 2;
   public static final int META_MASK = 4;
   public static final int ALT_MASK = 8;
   public static final int ALT_GRAPH_MASK = 32;
   public static final int BUTTON1_MASK = 16;
   public static final int BUTTON2_MASK = 8;
   public static final int BUTTON3_MASK = 4;
   public static final int SHIFT_DOWN_MASK = 64;
   public static final int CTRL_DOWN_MASK = 128;
   public static final int META_DOWN_MASK = 256;
   public static final int ALT_DOWN_MASK = 512;
   public static final int BUTTON1_DOWN_MASK = 1024;
   public static final int BUTTON2_DOWN_MASK = 2048;
   public static final int BUTTON3_DOWN_MASK = 4096;
   public static final int ALT_GRAPH_DOWN_MASK = 8192;
   private static final int[] BUTTON_DOWN_MASK = new int[]{1024, 2048, 4096, 16384, 32768, 65536, 131072, 262144, 524288, 1048576, 2097152, 4194304, 8388608, 16777216, 33554432, 67108864, 134217728, 268435456, 536870912, 1073741824};
   static final int FIRST_HIGH_BIT = Integer.MIN_VALUE;
   static final int JDK_1_3_MODIFIERS = 63;
   static final int HIGH_MODIFIERS = Integer.MIN_VALUE;
   long when;
   int modifiers;
   private transient boolean canAccessSystemClipboard;
   static final long serialVersionUID = -2482525981698309786L;

   private static int[] getButtonDownMasks() {
      return Arrays.copyOf(BUTTON_DOWN_MASK, BUTTON_DOWN_MASK.length);
   }

   public static int getMaskForButton(int var0) {
      if (var0 > 0 && var0 <= BUTTON_DOWN_MASK.length) {
         return BUTTON_DOWN_MASK[var0 - 1];
      } else {
         throw new IllegalArgumentException("button doesn't exist " + var0);
      }
   }

   private static native void initIDs();

   InputEvent(Component var1, int var2, long var3, int var5) {
      super(var1, var2);
      this.when = var3;
      this.modifiers = var5;
      this.canAccessSystemClipboard = this.canAccessSystemClipboard();
   }

   private boolean canAccessSystemClipboard() {
      boolean var1 = false;
      if (!GraphicsEnvironment.isHeadless()) {
         SecurityManager var2 = System.getSecurityManager();
         if (var2 != null) {
            try {
               var2.checkPermission(SecurityConstants.AWT.ACCESS_CLIPBOARD_PERMISSION);
               var1 = true;
            } catch (SecurityException var4) {
               if (logger.isLoggable(PlatformLogger.Level.FINE)) {
                  logger.fine("InputEvent.canAccessSystemClipboard() got SecurityException ", (Throwable)var4);
               }
            }
         } else {
            var1 = true;
         }
      }

      return var1;
   }

   public boolean isShiftDown() {
      return (this.modifiers & 1) != 0;
   }

   public boolean isControlDown() {
      return (this.modifiers & 2) != 0;
   }

   public boolean isMetaDown() {
      return (this.modifiers & 4) != 0;
   }

   public boolean isAltDown() {
      return (this.modifiers & 8) != 0;
   }

   public boolean isAltGraphDown() {
      return (this.modifiers & 32) != 0;
   }

   public long getWhen() {
      return this.when;
   }

   public int getModifiers() {
      return this.modifiers & -2147483585;
   }

   public int getModifiersEx() {
      return this.modifiers & -64;
   }

   public void consume() {
      this.consumed = true;
   }

   public boolean isConsumed() {
      return this.consumed;
   }

   public static String getModifiersExText(int var0) {
      StringBuilder var1 = new StringBuilder();
      if ((var0 & 256) != 0) {
         var1.append(Toolkit.getProperty("AWT.meta", "Meta"));
         var1.append("+");
      }

      if ((var0 & 128) != 0) {
         var1.append(Toolkit.getProperty("AWT.control", "Ctrl"));
         var1.append("+");
      }

      if ((var0 & 512) != 0) {
         var1.append(Toolkit.getProperty("AWT.alt", "Alt"));
         var1.append("+");
      }

      if ((var0 & 64) != 0) {
         var1.append(Toolkit.getProperty("AWT.shift", "Shift"));
         var1.append("+");
      }

      if ((var0 & 8192) != 0) {
         var1.append(Toolkit.getProperty("AWT.altGraph", "Alt Graph"));
         var1.append("+");
      }

      int var2 = 1;
      int[] var3 = BUTTON_DOWN_MASK;
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         int var6 = var3[var5];
         if ((var0 & var6) != 0) {
            var1.append(Toolkit.getProperty("AWT.button" + var2, "Button" + var2));
            var1.append("+");
         }

         ++var2;
      }

      if (var1.length() > 0) {
         var1.setLength(var1.length() - 1);
      }

      return var1.toString();
   }

   static {
      NativeLibLoader.loadLibraries();
      if (!GraphicsEnvironment.isHeadless()) {
         initIDs();
      }

      AWTAccessor.setInputEventAccessor(new AWTAccessor.InputEventAccessor() {
         public int[] getButtonDownMasks() {
            return InputEvent.getButtonDownMasks();
         }
      });
   }
}
