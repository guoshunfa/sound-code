package java.awt;

import java.awt.event.ActionEvent;
import java.awt.event.AdjustmentEvent;
import java.awt.event.InputEvent;
import java.awt.event.ItemEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.peer.ComponentPeer;
import java.awt.peer.LightweightPeer;
import java.lang.reflect.Field;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.EventObject;
import sun.awt.AWTAccessor;
import sun.util.logging.PlatformLogger;

public abstract class AWTEvent extends EventObject {
   private static final PlatformLogger log = PlatformLogger.getLogger("java.awt.AWTEvent");
   private byte[] bdata;
   protected int id;
   protected boolean consumed;
   private transient volatile AccessControlContext acc;
   transient boolean focusManagerIsDispatching;
   transient boolean isPosted;
   private transient boolean isSystemGenerated;
   public static final long COMPONENT_EVENT_MASK = 1L;
   public static final long CONTAINER_EVENT_MASK = 2L;
   public static final long FOCUS_EVENT_MASK = 4L;
   public static final long KEY_EVENT_MASK = 8L;
   public static final long MOUSE_EVENT_MASK = 16L;
   public static final long MOUSE_MOTION_EVENT_MASK = 32L;
   public static final long WINDOW_EVENT_MASK = 64L;
   public static final long ACTION_EVENT_MASK = 128L;
   public static final long ADJUSTMENT_EVENT_MASK = 256L;
   public static final long ITEM_EVENT_MASK = 512L;
   public static final long TEXT_EVENT_MASK = 1024L;
   public static final long INPUT_METHOD_EVENT_MASK = 2048L;
   static final long INPUT_METHODS_ENABLED_MASK = 4096L;
   public static final long PAINT_EVENT_MASK = 8192L;
   public static final long INVOCATION_EVENT_MASK = 16384L;
   public static final long HIERARCHY_EVENT_MASK = 32768L;
   public static final long HIERARCHY_BOUNDS_EVENT_MASK = 65536L;
   public static final long MOUSE_WHEEL_EVENT_MASK = 131072L;
   public static final long WINDOW_STATE_EVENT_MASK = 262144L;
   public static final long WINDOW_FOCUS_EVENT_MASK = 524288L;
   public static final int RESERVED_ID_MAX = 1999;
   private static Field inputEvent_CanAccessSystemClipboard_Field = null;
   private static final long serialVersionUID = -1825314779160409405L;

   final AccessControlContext getAccessControlContext() {
      if (this.acc == null) {
         throw new SecurityException("AWTEvent is missing AccessControlContext");
      } else {
         return this.acc;
      }
   }

   private static synchronized Field get_InputEvent_CanAccessSystemClipboard() {
      if (inputEvent_CanAccessSystemClipboard_Field == null) {
         inputEvent_CanAccessSystemClipboard_Field = (Field)AccessController.doPrivileged(new PrivilegedAction<Field>() {
            public Field run() {
               Field var1 = null;

               try {
                  var1 = InputEvent.class.getDeclaredField("canAccessSystemClipboard");
                  var1.setAccessible(true);
                  return var1;
               } catch (SecurityException var3) {
                  if (AWTEvent.log.isLoggable(PlatformLogger.Level.FINE)) {
                     AWTEvent.log.fine("AWTEvent.get_InputEvent_CanAccessSystemClipboard() got SecurityException ", (Throwable)var3);
                  }
               } catch (NoSuchFieldException var4) {
                  if (AWTEvent.log.isLoggable(PlatformLogger.Level.FINE)) {
                     AWTEvent.log.fine("AWTEvent.get_InputEvent_CanAccessSystemClipboard() got NoSuchFieldException ", (Throwable)var4);
                  }
               }

               return null;
            }
         });
      }

      return inputEvent_CanAccessSystemClipboard_Field;
   }

   private static native void initIDs();

   public AWTEvent(Event var1) {
      this(var1.target, var1.id);
   }

   public AWTEvent(Object var1, int var2) {
      super(var1);
      this.consumed = false;
      this.acc = AccessController.getContext();
      this.focusManagerIsDispatching = false;
      this.id = var2;
      switch(var2) {
      case 601:
      case 701:
      case 900:
      case 1001:
         this.consumed = true;
      default:
      }
   }

   public void setSource(Object var1) {
      if (this.source != var1) {
         Object var2 = null;
         if (var1 instanceof Component) {
            for(var2 = (Component)var1; var2 != null && ((Component)var2).peer != null && ((Component)var2).peer instanceof LightweightPeer; var2 = ((Component)var2).parent) {
            }
         }

         synchronized(this) {
            this.source = var1;
            if (var2 != null) {
               ComponentPeer var4 = ((Component)var2).peer;
               if (var4 != null) {
                  this.nativeSetSource(var4);
               }
            }

         }
      }
   }

   private native void nativeSetSource(ComponentPeer var1);

   public int getID() {
      return this.id;
   }

   public String toString() {
      String var1 = null;
      if (this.source instanceof Component) {
         var1 = ((Component)this.source).getName();
      } else if (this.source instanceof MenuComponent) {
         var1 = ((MenuComponent)this.source).getName();
      }

      return this.getClass().getName() + "[" + this.paramString() + "] on " + (var1 != null ? var1 : this.source);
   }

   public String paramString() {
      return "";
   }

   protected void consume() {
      switch(this.id) {
      case 401:
      case 402:
      case 501:
      case 502:
      case 503:
      case 504:
      case 505:
      case 506:
      case 507:
      case 1100:
      case 1101:
         this.consumed = true;
      default:
      }
   }

   protected boolean isConsumed() {
      return this.consumed;
   }

   Event convertToOld() {
      Object var1 = this.getSource();
      int var2 = this.id;
      switch(this.id) {
      case 100:
         if (var1 instanceof Frame || var1 instanceof Dialog) {
            Point var13 = ((Component)var1).getLocation();
            return new Event(var1, 0L, 205, var13.x, var13.y, 0, 0);
         }
      default:
         return null;
      case 201:
      case 203:
      case 204:
         return new Event(var1, var2, (Object)null);
      case 401:
      case 402:
         KeyEvent var3 = (KeyEvent)this;
         if (var3.isActionKey()) {
            var2 = this.id == 401 ? 403 : 404;
         }

         int var4 = var3.getKeyCode();
         if (var4 != 16 && var4 != 17 && var4 != 18) {
            return new Event(var1, var3.getWhen(), var2, 0, 0, Event.getOldEventKey(var3), var3.getModifiers() & -17);
         }

         return null;
      case 501:
      case 502:
      case 503:
      case 504:
      case 505:
      case 506:
         MouseEvent var5 = (MouseEvent)this;
         Event var6 = new Event(var1, var5.getWhen(), var2, var5.getX(), var5.getY(), 0, var5.getModifiers() & -17);
         var6.clickCount = var5.getClickCount();
         return var6;
      case 601:
         AdjustmentEvent var11 = (AdjustmentEvent)this;
         short var12;
         switch(var11.getAdjustmentType()) {
         case 1:
            var12 = 602;
            break;
         case 2:
            var12 = 601;
            break;
         case 3:
            var12 = 603;
            break;
         case 4:
            var12 = 604;
            break;
         case 5:
            if (var11.getValueIsAdjusting()) {
               var12 = 605;
            } else {
               var12 = 607;
            }
            break;
         default:
            return null;
         }

         return new Event(var1, var12, var11.getValue());
      case 701:
         ItemEvent var9 = (ItemEvent)this;
         Object var10;
         if (var1 instanceof List) {
            var2 = var9.getStateChange() == 1 ? 701 : 702;
            var10 = var9.getItem();
         } else {
            var2 = 1001;
            if (var1 instanceof Choice) {
               var10 = var9.getItem();
            } else {
               var10 = var9.getStateChange() == 1;
            }
         }

         return new Event(var1, var2, var10);
      case 1001:
         ActionEvent var7 = (ActionEvent)this;
         String var8;
         if (var1 instanceof Button) {
            var8 = ((Button)var1).getLabel();
         } else if (var1 instanceof MenuItem) {
            var8 = ((MenuItem)var1).getLabel();
         } else {
            var8 = var7.getActionCommand();
         }

         return new Event(var1, 0L, var2, 0, 0, 0, var7.getModifiers(), var8);
      case 1004:
         return new Event(var1, 1004, (Object)null);
      case 1005:
         return new Event(var1, 1005, (Object)null);
      }
   }

   void copyPrivateDataInto(AWTEvent var1) {
      var1.bdata = this.bdata;
      if (this instanceof InputEvent && var1 instanceof InputEvent) {
         Field var2 = get_InputEvent_CanAccessSystemClipboard();
         if (var2 != null) {
            try {
               boolean var3 = var2.getBoolean(this);
               var2.setBoolean(var1, var3);
            } catch (IllegalAccessException var4) {
               if (log.isLoggable(PlatformLogger.Level.FINE)) {
                  log.fine("AWTEvent.copyPrivateDataInto() got IllegalAccessException ", (Throwable)var4);
               }
            }
         }
      }

      var1.isSystemGenerated = this.isSystemGenerated;
   }

   void dispatched() {
      if (this instanceof InputEvent) {
         Field var1 = get_InputEvent_CanAccessSystemClipboard();
         if (var1 != null) {
            try {
               var1.setBoolean(this, false);
            } catch (IllegalAccessException var3) {
               if (log.isLoggable(PlatformLogger.Level.FINE)) {
                  log.fine("AWTEvent.dispatched() got IllegalAccessException ", (Throwable)var3);
               }
            }
         }
      }

   }

   static {
      Toolkit.loadLibraries();
      if (!GraphicsEnvironment.isHeadless()) {
         initIDs();
      }

      AWTAccessor.setAWTEventAccessor(new AWTAccessor.AWTEventAccessor() {
         public void setPosted(AWTEvent var1) {
            var1.isPosted = true;
         }

         public void setSystemGenerated(AWTEvent var1) {
            var1.isSystemGenerated = true;
         }

         public boolean isSystemGenerated(AWTEvent var1) {
            return var1.isSystemGenerated;
         }

         public AccessControlContext getAccessControlContext(AWTEvent var1) {
            return var1.getAccessControlContext();
         }

         public byte[] getBData(AWTEvent var1) {
            return var1.bdata;
         }

         public void setBData(AWTEvent var1, byte[] var2) {
            var1.bdata = var2;
         }
      });
   }
}
