package java.awt.event;

import java.awt.Component;
import java.awt.GraphicsEnvironment;
import java.awt.IllegalComponentStateException;
import java.awt.Point;
import java.awt.Toolkit;
import java.io.IOException;
import java.io.ObjectInputStream;
import sun.awt.AWTAccessor;
import sun.awt.SunToolkit;

public class MouseEvent extends InputEvent {
   public static final int MOUSE_FIRST = 500;
   public static final int MOUSE_LAST = 507;
   public static final int MOUSE_CLICKED = 500;
   public static final int MOUSE_PRESSED = 501;
   public static final int MOUSE_RELEASED = 502;
   public static final int MOUSE_MOVED = 503;
   public static final int MOUSE_ENTERED = 504;
   public static final int MOUSE_EXITED = 505;
   public static final int MOUSE_DRAGGED = 506;
   public static final int MOUSE_WHEEL = 507;
   public static final int NOBUTTON = 0;
   public static final int BUTTON1 = 1;
   public static final int BUTTON2 = 2;
   public static final int BUTTON3 = 3;
   int x;
   int y;
   private int xAbs;
   private int yAbs;
   int clickCount;
   private boolean causedByTouchEvent;
   int button;
   boolean popupTrigger;
   private static final long serialVersionUID = -991214153494842848L;
   private static int cachedNumberOfButtons;
   private transient boolean shouldExcludeButtonFromExtModifiers;

   private static native void initIDs();

   public Point getLocationOnScreen() {
      return new Point(this.xAbs, this.yAbs);
   }

   public int getXOnScreen() {
      return this.xAbs;
   }

   public int getYOnScreen() {
      return this.yAbs;
   }

   public MouseEvent(Component var1, int var2, long var3, int var5, int var6, int var7, int var8, boolean var9, int var10) {
      this(var1, var2, var3, var5, var6, var7, 0, 0, var8, var9, var10);
      new Point(0, 0);

      try {
         Point var11 = var1.getLocationOnScreen();
         this.xAbs = var11.x + var6;
         this.yAbs = var11.y + var7;
      } catch (IllegalComponentStateException var13) {
         this.xAbs = 0;
         this.yAbs = 0;
      }

   }

   public MouseEvent(Component var1, int var2, long var3, int var5, int var6, int var7, int var8, boolean var9) {
      this(var1, var2, var3, var5, var6, var7, var8, var9, 0);
   }

   public int getModifiersEx() {
      int var1 = this.modifiers;
      if (this.shouldExcludeButtonFromExtModifiers) {
         var1 &= ~InputEvent.getMaskForButton(this.getButton());
      }

      return var1 & -64;
   }

   public MouseEvent(Component var1, int var2, long var3, int var5, int var6, int var7, int var8, int var9, int var10, boolean var11, int var12) {
      super(var1, var2, var3, var5);
      this.popupTrigger = false;
      this.shouldExcludeButtonFromExtModifiers = false;
      this.x = var6;
      this.y = var7;
      this.xAbs = var8;
      this.yAbs = var9;
      this.clickCount = var10;
      this.popupTrigger = var11;
      if (var12 < 0) {
         throw new IllegalArgumentException("Invalid button value :" + var12);
      } else {
         if (var12 > 3) {
            if (!Toolkit.getDefaultToolkit().areExtraMouseButtonsEnabled()) {
               throw new IllegalArgumentException("Extra mouse events are disabled " + var12);
            }

            if (var12 > cachedNumberOfButtons) {
               throw new IllegalArgumentException("Nonexistent button " + var12);
            }

            if (this.getModifiersEx() != 0 && (var2 == 502 || var2 == 500)) {
               this.shouldExcludeButtonFromExtModifiers = true;
            }
         }

         this.button = var12;
         if (this.getModifiers() != 0 && this.getModifiersEx() == 0) {
            this.setNewModifiers();
         } else if (this.getModifiers() == 0 && (this.getModifiersEx() != 0 || var12 != 0) && var12 <= 3) {
            this.setOldModifiers();
         }

      }
   }

   public int getX() {
      return this.x;
   }

   public int getY() {
      return this.y;
   }

   public Point getPoint() {
      int var1;
      int var2;
      synchronized(this) {
         var1 = this.x;
         var2 = this.y;
      }

      return new Point(var1, var2);
   }

   public synchronized void translatePoint(int var1, int var2) {
      this.x += var1;
      this.y += var2;
   }

   public int getClickCount() {
      return this.clickCount;
   }

   public int getButton() {
      return this.button;
   }

   public boolean isPopupTrigger() {
      return this.popupTrigger;
   }

   public static String getMouseModifiersText(int var0) {
      StringBuilder var1 = new StringBuilder();
      if ((var0 & 8) != 0) {
         var1.append(Toolkit.getProperty("AWT.alt", "Alt"));
         var1.append("+");
      }

      if ((var0 & 4) != 0) {
         var1.append(Toolkit.getProperty("AWT.meta", "Meta"));
         var1.append("+");
      }

      if ((var0 & 2) != 0) {
         var1.append(Toolkit.getProperty("AWT.control", "Ctrl"));
         var1.append("+");
      }

      if ((var0 & 1) != 0) {
         var1.append(Toolkit.getProperty("AWT.shift", "Shift"));
         var1.append("+");
      }

      if ((var0 & 32) != 0) {
         var1.append(Toolkit.getProperty("AWT.altGraph", "Alt Graph"));
         var1.append("+");
      }

      if ((var0 & 16) != 0) {
         var1.append(Toolkit.getProperty("AWT.button1", "Button1"));
         var1.append("+");
      }

      if ((var0 & 8) != 0) {
         var1.append(Toolkit.getProperty("AWT.button2", "Button2"));
         var1.append("+");
      }

      if ((var0 & 4) != 0) {
         var1.append(Toolkit.getProperty("AWT.button3", "Button3"));
         var1.append("+");
      }

      for(int var3 = 1; var3 <= cachedNumberOfButtons; ++var3) {
         int var2 = InputEvent.getMaskForButton(var3);
         if ((var0 & var2) != 0 && var1.indexOf(Toolkit.getProperty("AWT.button" + var3, "Button" + var3)) == -1) {
            var1.append(Toolkit.getProperty("AWT.button" + var3, "Button" + var3));
            var1.append("+");
         }
      }

      if (var1.length() > 0) {
         var1.setLength(var1.length() - 1);
      }

      return var1.toString();
   }

   public String paramString() {
      StringBuilder var1 = new StringBuilder(80);
      switch(this.id) {
      case 500:
         var1.append("MOUSE_CLICKED");
         break;
      case 501:
         var1.append("MOUSE_PRESSED");
         break;
      case 502:
         var1.append("MOUSE_RELEASED");
         break;
      case 503:
         var1.append("MOUSE_MOVED");
         break;
      case 504:
         var1.append("MOUSE_ENTERED");
         break;
      case 505:
         var1.append("MOUSE_EXITED");
         break;
      case 506:
         var1.append("MOUSE_DRAGGED");
         break;
      case 507:
         var1.append("MOUSE_WHEEL");
         break;
      default:
         var1.append("unknown type");
      }

      var1.append(",(").append(this.x).append(",").append(this.y).append(")");
      var1.append(",absolute(").append(this.xAbs).append(",").append(this.yAbs).append(")");
      if (this.id != 506 && this.id != 503) {
         var1.append(",button=").append(this.getButton());
      }

      if (this.getModifiers() != 0) {
         var1.append(",modifiers=").append(getMouseModifiersText(this.modifiers));
      }

      if (this.getModifiersEx() != 0) {
         var1.append(",extModifiers=").append(getModifiersExText(this.getModifiersEx()));
      }

      var1.append(",clickCount=").append(this.clickCount);
      return var1.toString();
   }

   private void setNewModifiers() {
      if ((this.modifiers & 16) != 0) {
         this.modifiers |= 1024;
      }

      if ((this.modifiers & 8) != 0) {
         this.modifiers |= 2048;
      }

      if ((this.modifiers & 4) != 0) {
         this.modifiers |= 4096;
      }

      if (this.id == 501 || this.id == 502 || this.id == 500) {
         if ((this.modifiers & 16) != 0) {
            this.button = 1;
            this.modifiers &= -13;
            if (this.id != 501) {
               this.modifiers &= -1025;
            }
         } else if ((this.modifiers & 8) != 0) {
            this.button = 2;
            this.modifiers &= -21;
            if (this.id != 501) {
               this.modifiers &= -2049;
            }
         } else if ((this.modifiers & 4) != 0) {
            this.button = 3;
            this.modifiers &= -25;
            if (this.id != 501) {
               this.modifiers &= -4097;
            }
         }
      }

      if ((this.modifiers & 8) != 0) {
         this.modifiers |= 512;
      }

      if ((this.modifiers & 4) != 0) {
         this.modifiers |= 256;
      }

      if ((this.modifiers & 1) != 0) {
         this.modifiers |= 64;
      }

      if ((this.modifiers & 2) != 0) {
         this.modifiers |= 128;
      }

      if ((this.modifiers & 32) != 0) {
         this.modifiers |= 8192;
      }

   }

   private void setOldModifiers() {
      if (this.id != 501 && this.id != 502 && this.id != 500) {
         if ((this.modifiers & 1024) != 0) {
            this.modifiers |= 16;
         }

         if ((this.modifiers & 2048) != 0) {
            this.modifiers |= 8;
         }

         if ((this.modifiers & 4096) != 0) {
            this.modifiers |= 4;
         }
      } else {
         switch(this.button) {
         case 1:
            this.modifiers |= 16;
            break;
         case 2:
            this.modifiers |= 8;
            break;
         case 3:
            this.modifiers |= 4;
         }
      }

      if ((this.modifiers & 512) != 0) {
         this.modifiers |= 8;
      }

      if ((this.modifiers & 256) != 0) {
         this.modifiers |= 4;
      }

      if ((this.modifiers & 64) != 0) {
         this.modifiers |= 1;
      }

      if ((this.modifiers & 128) != 0) {
         this.modifiers |= 2;
      }

      if ((this.modifiers & 8192) != 0) {
         this.modifiers |= 32;
      }

   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      if (this.getModifiers() != 0 && this.getModifiersEx() == 0) {
         this.setNewModifiers();
      }

   }

   static {
      NativeLibLoader.loadLibraries();
      if (!GraphicsEnvironment.isHeadless()) {
         initIDs();
      }

      Toolkit var0 = Toolkit.getDefaultToolkit();
      if (var0 instanceof SunToolkit) {
         cachedNumberOfButtons = ((SunToolkit)var0).getNumberOfButtons();
      } else {
         cachedNumberOfButtons = 3;
      }

      AWTAccessor.setMouseEventAccessor(new AWTAccessor.MouseEventAccessor() {
         public boolean isCausedByTouchEvent(MouseEvent var1) {
            return var1.causedByTouchEvent;
         }

         public void setCausedByTouchEvent(MouseEvent var1, boolean var2) {
            var1.causedByTouchEvent = var2;
         }
      });
   }
}
