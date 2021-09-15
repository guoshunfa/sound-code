package sun.awt;

import java.awt.AWTEvent;
import java.awt.AWTException;
import java.awt.Component;
import java.awt.Container;
import java.awt.EventQueue;
import java.awt.Window;
import java.awt.event.InputMethodEvent;
import java.awt.font.TextAttribute;
import java.awt.font.TextHitInfo;
import java.awt.im.InputMethodHighlight;
import java.awt.im.spi.InputMethodContext;
import java.awt.peer.ComponentPeer;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Pattern;
import sun.awt.im.InputMethodAdapter;
import sun.util.logging.PlatformLogger;

public abstract class X11InputMethod extends InputMethodAdapter {
   private static final PlatformLogger log = PlatformLogger.getLogger("sun.awt.X11InputMethod");
   private static final int XIMReverse = 1;
   private static final int XIMUnderline = 2;
   private static final int XIMHighlight = 4;
   private static final int XIMPrimary = 32;
   private static final int XIMSecondary = 64;
   private static final int XIMTertiary = 128;
   private static final int XIMVisibleToForward = 256;
   private static final int XIMVisibleToBackward = 512;
   private static final int XIMVisibleCenter = 1024;
   private static final int XIMVisibleMask = 1792;
   private Locale locale = X11InputMethodDescriptor.getSupportedLocale();
   private static boolean isXIMOpened = false;
   protected Container clientComponentWindow = null;
   private Component awtFocussedComponent = null;
   private Component lastXICFocussedComponent = null;
   private boolean isLastXICActive = false;
   private boolean isLastTemporary = false;
   private boolean isActive = false;
   private boolean isActiveClient = false;
   private static Map[] highlightStyles;
   private boolean disposed = false;
   private boolean needResetXIC = false;
   private WeakReference<Component> needResetXICClient = new WeakReference((Object)null);
   private boolean compositionEnableSupported = true;
   private boolean savedCompositionState = false;
   private String committedText = null;
   private StringBuffer composedText = null;
   private X11InputMethod.IntBuffer rawFeedbacks;
   private transient long pData = 0L;
   private static final int INITIAL_SIZE = 64;

   private static native void initIDs();

   public X11InputMethod() throws AWTException {
      if (!this.initXIM()) {
         throw new AWTException("Cannot open X Input Method");
      }
   }

   protected void finalize() throws Throwable {
      this.dispose();
      super.finalize();
   }

   private synchronized boolean initXIM() {
      if (!isXIMOpened) {
         isXIMOpened = this.openXIM();
      }

      return isXIMOpened;
   }

   protected abstract boolean openXIM();

   protected boolean isDisposed() {
      return this.disposed;
   }

   protected abstract void setXICFocus(ComponentPeer var1, boolean var2, boolean var3);

   public void setInputMethodContext(InputMethodContext var1) {
   }

   public boolean setLocale(Locale var1) {
      if (var1.equals(this.locale)) {
         return true;
      } else {
         return this.locale.equals(Locale.JAPAN) && var1.equals(Locale.JAPANESE) || this.locale.equals(Locale.KOREA) && var1.equals(Locale.KOREAN);
      }
   }

   public Locale getLocale() {
      return this.locale;
   }

   public void setCharacterSubsets(Character.Subset[] var1) {
   }

   public void dispatchEvent(AWTEvent var1) {
   }

   protected final void resetXICifneeded() {
      if (this.needResetXIC && this.haveActiveClient() && this.getClientComponent() != this.needResetXICClient.get()) {
         this.resetXIC();
         this.lastXICFocussedComponent = null;
         this.isLastXICActive = false;
         this.needResetXICClient.clear();
         this.needResetXIC = false;
      }

   }

   private void resetCompositionState() {
      if (this.compositionEnableSupported) {
         try {
            this.setCompositionEnabled(this.savedCompositionState);
         } catch (UnsupportedOperationException var2) {
            this.compositionEnableSupported = false;
         }
      }

   }

   private boolean getCompositionState() {
      boolean var1 = false;
      if (this.compositionEnableSupported) {
         try {
            var1 = this.isCompositionEnabled();
         } catch (UnsupportedOperationException var3) {
            this.compositionEnableSupported = false;
         }
      }

      return var1;
   }

   public synchronized void activate() {
      this.clientComponentWindow = this.getClientComponentWindow();
      if (this.clientComponentWindow != null) {
         if (this.lastXICFocussedComponent != null && log.isLoggable(PlatformLogger.Level.FINE)) {
            log.fine("XICFocused {0}, AWTFocused {1}", this.lastXICFocussedComponent, this.awtFocussedComponent);
         }

         if (this.pData == 0L) {
            if (!this.createXIC()) {
               return;
            }

            this.disposed = false;
         }

         this.resetXICifneeded();
         ComponentPeer var1 = null;
         ComponentPeer var2 = this.getPeer(this.awtFocussedComponent);
         if (this.lastXICFocussedComponent != null) {
            var1 = this.getPeer(this.lastXICFocussedComponent);
         }

         if (this.isLastTemporary || var1 != var2 || this.isLastXICActive != this.haveActiveClient()) {
            if (var1 != null) {
               this.setXICFocus(var1, false, this.isLastXICActive);
            }

            if (var2 != null) {
               this.setXICFocus(var2, true, this.haveActiveClient());
            }

            this.lastXICFocussedComponent = this.awtFocussedComponent;
            this.isLastXICActive = this.haveActiveClient();
         }

         this.resetCompositionState();
         this.isActive = true;
      }
   }

   protected abstract boolean createXIC();

   public synchronized void deactivate(boolean var1) {
      boolean var2 = this.haveActiveClient();
      this.savedCompositionState = this.getCompositionState();
      if (var1) {
         this.turnoffStatusWindow();
      }

      this.lastXICFocussedComponent = this.awtFocussedComponent;
      this.isLastXICActive = var2;
      this.isLastTemporary = var1;
      this.isActive = false;
   }

   public void disableInputMethod() {
      if (this.lastXICFocussedComponent != null) {
         this.setXICFocus(this.getPeer(this.lastXICFocussedComponent), false, this.isLastXICActive);
         this.lastXICFocussedComponent = null;
         this.isLastXICActive = false;
         this.resetXIC();
         this.needResetXICClient.clear();
         this.needResetXIC = false;
      }

   }

   public void hideWindows() {
   }

   public static Map mapInputMethodHighlight(InputMethodHighlight var0) {
      int var2 = var0.getState();
      int var1;
      if (var2 == 0) {
         var1 = 0;
      } else {
         if (var2 != 1) {
            return null;
         }

         var1 = 2;
      }

      if (var0.isSelected()) {
         ++var1;
      }

      return highlightStyles[var1];
   }

   protected void setAWTFocussedComponent(Component var1) {
      if (var1 != null) {
         if (this.isActive) {
            boolean var2 = this.haveActiveClient();
            this.setXICFocus(this.getPeer(this.awtFocussedComponent), false, var2);
            this.setXICFocus(this.getPeer(var1), true, var2);
         }

         this.awtFocussedComponent = var1;
      }
   }

   protected void stopListening() {
      this.endComposition();
      this.disableInputMethod();
      if (this.needResetXIC) {
         this.resetXIC();
         this.needResetXICClient.clear();
         this.needResetXIC = false;
      }

   }

   private Window getClientComponentWindow() {
      Component var1 = this.getClientComponent();
      Container var2;
      if (var1 instanceof Container) {
         var2 = (Container)var1;
      } else {
         var2 = this.getParent(var1);
      }

      while(var2 != null && !(var2 instanceof Window)) {
         var2 = this.getParent(var2);
      }

      return (Window)var2;
   }

   protected abstract Container getParent(Component var1);

   protected abstract ComponentPeer getPeer(Component var1);

   protected abstract void awtLock();

   protected abstract void awtUnlock();

   private void postInputMethodEvent(int var1, AttributedCharacterIterator var2, int var3, TextHitInfo var4, TextHitInfo var5, long var6) {
      Component var8 = this.getClientComponent();
      if (var8 != null) {
         InputMethodEvent var9 = new InputMethodEvent(var8, var1, var6, var2, var3, var4, var5);
         SunToolkit.postEvent(SunToolkit.targetToAppContext(var8), var9);
      }

   }

   private void postInputMethodEvent(int var1, AttributedCharacterIterator var2, int var3, TextHitInfo var4, TextHitInfo var5) {
      this.postInputMethodEvent(var1, var2, var3, var4, var5, EventQueue.getMostRecentEventTime());
   }

   void dispatchCommittedText(String var1, long var2) {
      if (var1 != null) {
         if (this.composedText == null) {
            AttributedString var4 = new AttributedString(var1);
            this.postInputMethodEvent(1100, var4.getIterator(), var1.length(), (TextHitInfo)null, (TextHitInfo)null, var2);
         } else {
            this.committedText = var1;
         }

      }
   }

   private void dispatchCommittedText(String var1) {
      this.dispatchCommittedText(var1, EventQueue.getMostRecentEventTime());
   }

   void dispatchComposedText(String var1, int[] var2, int var3, int var4, int var5, long var6) {
      if (!this.disposed) {
         if (var1 != null || var2 != null || var3 != 0 || var4 != 0 || var5 != 0 || this.composedText != null || this.committedText != null) {
            if (this.composedText == null) {
               this.composedText = new StringBuffer(64);
               this.rawFeedbacks = new X11InputMethod.IntBuffer(64);
            }

            if (var4 > 0) {
               if (var1 == null && var2 != null) {
                  this.rawFeedbacks.replace(var3, var2);
               } else if (var4 == this.composedText.length()) {
                  this.composedText = new StringBuffer(64);
                  this.rawFeedbacks = new X11InputMethod.IntBuffer(64);
               } else if (this.composedText.length() > 0) {
                  if (var3 + var4 < this.composedText.length()) {
                     String var8 = this.composedText.toString().substring(var3 + var4, this.composedText.length());
                     this.composedText.setLength(var3);
                     this.composedText.append(var8);
                  } else {
                     this.composedText.setLength(var3);
                  }

                  this.rawFeedbacks.remove(var3, var4);
               }
            }

            if (var1 != null) {
               this.composedText.insert(var3, var1);
               if (var2 != null) {
                  this.rawFeedbacks.insert(var3, var2);
               }
            }

            if (this.composedText.length() == 0) {
               this.composedText = null;
               this.rawFeedbacks = null;
               if (this.committedText != null) {
                  this.dispatchCommittedText(this.committedText, var6);
                  this.committedText = null;
               } else {
                  this.postInputMethodEvent(1100, (AttributedCharacterIterator)null, 0, (TextHitInfo)null, (TextHitInfo)null, var6);
               }
            } else {
               AttributedString var9;
               int var17;
               if (this.committedText != null) {
                  var17 = this.committedText.length();
                  var9 = new AttributedString(this.committedText + this.composedText);
                  this.committedText = null;
               } else {
                  var17 = 0;
                  var9 = new AttributedString(this.composedText.toString());
               }

               int var12 = 0;
               int var14 = 0;
               TextHitInfo var15 = null;
               this.rawFeedbacks.rewind();
               int var10 = this.rawFeedbacks.getNext();
               this.rawFeedbacks.unget();

               int var11;
               int var13;
               while((var11 = this.rawFeedbacks.getNext()) != -1) {
                  if (var14 == 0) {
                     var14 = var11 & 1792;
                     if (var14 != 0) {
                        int var16 = this.rawFeedbacks.getOffset() - 1;
                        if (var14 == 512) {
                           var15 = TextHitInfo.leading(var16);
                        } else {
                           var15 = TextHitInfo.trailing(var16);
                        }
                     }
                  }

                  var11 &= -1793;
                  if (var10 != var11) {
                     this.rawFeedbacks.unget();
                     var13 = this.rawFeedbacks.getOffset();
                     var9.addAttribute(TextAttribute.INPUT_METHOD_HIGHLIGHT, this.convertVisualFeedbackToHighlight(var10), var17 + var12, var17 + var13);
                     var12 = var13;
                     var10 = var11;
                  }
               }

               var13 = this.rawFeedbacks.getOffset();
               if (var13 >= 0) {
                  var9.addAttribute(TextAttribute.INPUT_METHOD_HIGHLIGHT, this.convertVisualFeedbackToHighlight(var10), var17 + var12, var17 + var13);
               }

               this.postInputMethodEvent(1100, var9.getIterator(), var17, TextHitInfo.leading(var5), var15, var6);
            }
         }
      }
   }

   void flushText() {
      String var1 = this.committedText != null ? this.committedText : "";
      if (this.composedText != null) {
         var1 = var1 + this.composedText.toString();
      }

      if (!var1.equals("")) {
         AttributedString var2 = new AttributedString(var1);
         this.postInputMethodEvent(1100, var2.getIterator(), var1.length(), (TextHitInfo)null, (TextHitInfo)null, EventQueue.getMostRecentEventTime());
         this.composedText = null;
         this.committedText = null;
      }

   }

   protected synchronized void disposeImpl() {
      this.disposeXIC();
      this.awtLock();
      this.composedText = null;
      this.committedText = null;
      this.rawFeedbacks = null;
      this.awtUnlock();
      this.awtFocussedComponent = null;
      this.lastXICFocussedComponent = null;
   }

   public final void dispose() {
      boolean var1 = false;
      if (!this.disposed) {
         synchronized(this) {
            if (!this.disposed) {
               var1 = true;
               this.disposed = true;
            }
         }
      }

      if (var1) {
         this.disposeImpl();
      }

   }

   public Object getControlObject() {
      return null;
   }

   public synchronized void removeNotify() {
      this.dispose();
   }

   public void setCompositionEnabled(boolean var1) {
      if (this.setCompositionEnabledNative(var1)) {
         this.savedCompositionState = var1;
      }

   }

   public boolean isCompositionEnabled() {
      return this.isCompositionEnabledNative();
   }

   public void endComposition() {
      if (!this.disposed) {
         this.savedCompositionState = this.getCompositionState();
         boolean var1 = this.haveActiveClient();
         if (var1 && this.composedText == null && this.committedText == null) {
            this.needResetXIC = true;
            this.needResetXICClient = new WeakReference(this.getClientComponent());
         } else {
            String var2 = this.resetXIC();
            if (var1) {
               this.needResetXIC = false;
            }

            this.awtLock();
            this.composedText = null;
            this.postInputMethodEvent(1100, (AttributedCharacterIterator)null, 0, (TextHitInfo)null, (TextHitInfo)null);
            if (var2 != null && var2.length() > 0) {
               this.dispatchCommittedText(var2);
            }

            this.awtUnlock();
            if (this.savedCompositionState) {
               this.resetCompositionState();
            }

         }
      }
   }

   public String getNativeInputMethodInfo() {
      String var1 = System.getenv("XMODIFIERS");
      String var2 = null;
      if (var1 != null) {
         int var3 = var1.indexOf("@im=");
         if (var3 != -1) {
            var2 = var1.substring(var3 + 4);
         }
      } else if (System.getProperty("os.name").startsWith("SunOS")) {
         File var10 = new File(System.getProperty("user.home") + "/.dtprofile");
         String var4 = null;

         try {
            BufferedReader var5 = new BufferedReader(new FileReader(var10));
            String var6 = null;

            label51:
            while(true) {
               while(true) {
                  do {
                     if (var4 != null || (var6 = var5.readLine()) == null) {
                        var5.close();
                        break label51;
                     }
                  } while(!var6.contains("atok") && !var6.contains("wnn"));

                  StringTokenizer var7 = new StringTokenizer(var6);

                  while(var7.hasMoreTokens()) {
                     String var8 = var7.nextToken();
                     if (Pattern.matches("atok.*setup", var8) || Pattern.matches("wnn.*setup", var8)) {
                        var4 = var8.substring(0, var8.indexOf("setup"));
                        break;
                     }
                  }
               }
            }
         } catch (IOException var9) {
            var9.printStackTrace();
         }

         var2 = "htt " + var4;
      }

      return var2;
   }

   private InputMethodHighlight convertVisualFeedbackToHighlight(int var1) {
      InputMethodHighlight var2;
      switch(var1) {
      case 1:
         var2 = InputMethodHighlight.SELECTED_CONVERTED_TEXT_HIGHLIGHT;
         break;
      case 2:
         var2 = InputMethodHighlight.UNSELECTED_CONVERTED_TEXT_HIGHLIGHT;
         break;
      case 4:
         var2 = InputMethodHighlight.SELECTED_RAW_TEXT_HIGHLIGHT;
         break;
      case 32:
         var2 = InputMethodHighlight.UNSELECTED_CONVERTED_TEXT_HIGHLIGHT;
         break;
      case 64:
         var2 = InputMethodHighlight.SELECTED_CONVERTED_TEXT_HIGHLIGHT;
         break;
      case 128:
         var2 = InputMethodHighlight.SELECTED_RAW_TEXT_HIGHLIGHT;
         break;
      default:
         var2 = InputMethodHighlight.SELECTED_RAW_TEXT_HIGHLIGHT;
      }

      return var2;
   }

   protected native String resetXIC();

   private native void disposeXIC();

   private native boolean setCompositionEnabledNative(boolean var1);

   private native boolean isCompositionEnabledNative();

   private native void turnoffStatusWindow();

   static {
      Map[] var0 = new Map[4];
      HashMap var1 = new HashMap(1);
      var1.put(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD);
      var0[0] = Collections.unmodifiableMap(var1);
      var1 = new HashMap(1);
      var1.put(TextAttribute.SWAP_COLORS, TextAttribute.SWAP_COLORS_ON);
      var0[1] = Collections.unmodifiableMap(var1);
      var1 = new HashMap(1);
      var1.put(TextAttribute.INPUT_METHOD_UNDERLINE, TextAttribute.UNDERLINE_LOW_ONE_PIXEL);
      var0[2] = Collections.unmodifiableMap(var1);
      var1 = new HashMap(1);
      var1.put(TextAttribute.SWAP_COLORS, TextAttribute.SWAP_COLORS_ON);
      var0[3] = Collections.unmodifiableMap(var1);
      highlightStyles = var0;
      initIDs();
   }

   private final class IntBuffer {
      private int[] intArray;
      private int size;
      private int index;

      IntBuffer(int var2) {
         this.intArray = new int[var2];
         this.size = 0;
         this.index = 0;
      }

      void insert(int var1, int[] var2) {
         int var3 = this.size + var2.length;
         if (this.intArray.length < var3) {
            int[] var4 = new int[var3 * 2];
            System.arraycopy(this.intArray, 0, var4, 0, this.size);
            this.intArray = var4;
         }

         System.arraycopy(this.intArray, var1, this.intArray, var1 + var2.length, this.size - var1);
         System.arraycopy(var2, 0, this.intArray, var1, var2.length);
         this.size += var2.length;
         if (this.index > var1) {
            this.index = var1;
         }

      }

      void remove(int var1, int var2) {
         if (var1 + var2 != this.size) {
            System.arraycopy(this.intArray, var1 + var2, this.intArray, var1, this.size - var1 - var2);
         }

         this.size -= var2;
         if (this.index > var1) {
            this.index = var1;
         }

      }

      void replace(int var1, int[] var2) {
         System.arraycopy(var2, 0, this.intArray, var1, var2.length);
      }

      void removeAll() {
         this.size = 0;
         this.index = 0;
      }

      void rewind() {
         this.index = 0;
      }

      int getNext() {
         return this.index == this.size ? -1 : this.intArray[this.index++];
      }

      void unget() {
         if (this.index != 0) {
            --this.index;
         }

      }

      int getOffset() {
         return this.index;
      }

      public String toString() {
         StringBuffer var1 = new StringBuffer();
         int var2 = 0;

         while(var2 < this.size) {
            var1.append(this.intArray[var2++]);
            if (var2 < this.size) {
               var1.append(",");
            }
         }

         return var1.toString();
      }
   }
}
