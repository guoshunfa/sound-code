package sun.lwawt.macosx;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.TextComponent;
import java.awt.event.InputMethodEvent;
import java.awt.font.TextAttribute;
import java.awt.font.TextHitInfo;
import java.awt.im.InputMethodHighlight;
import java.awt.im.spi.InputMethodContext;
import java.awt.peer.ComponentPeer;
import java.awt.peer.LightweightPeer;
import java.lang.reflect.InvocationTargetException;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import javax.swing.text.JTextComponent;
import sun.awt.im.InputMethodAdapter;
import sun.lwawt.LWComponentPeer;

public class CInputMethod extends InputMethodAdapter {
   private InputMethodContext fIMContext;
   private Component fAwtFocussedComponent;
   private LWComponentPeer fAwtFocussedComponentPeer;
   private boolean isActive;
   private static Map<TextAttribute, Integer>[] sHighlightStyles;
   private AttributedString fCurrentText = null;
   private String fCurrentTextAsString = null;
   private int fCurrentTextLength = 0;
   private static final int kCaretPosition = 0;
   private static final int kRawText = 1;
   private static final int kSelectedRawText = 2;
   private static final int kConvertedText = 3;
   private static final int kSelectedConvertedText = 4;

   public void setInputMethodContext(InputMethodContext var1) {
      this.fIMContext = var1;
   }

   public boolean setLocale(Locale var1) {
      return this.setLocale(var1, false);
   }

   private boolean setLocale(Locale var1, boolean var2) {
      Object[] var3 = CInputMethodDescriptor.getAvailableLocalesInternal();

      for(int var4 = 0; var4 < var3.length; ++var4) {
         Locale var5 = (Locale)var3[var4];
         if (var1.equals(var5) || var5.equals(Locale.JAPAN) && var1.equals(Locale.JAPANESE) || var5.equals(Locale.KOREA) && var1.equals(Locale.KOREAN)) {
            if (this.isActive) {
               setNativeLocale(var5.toString(), var2);
            }

            return true;
         }
      }

      return false;
   }

   public Locale getLocale() {
      Locale var1 = getNativeLocale();
      if (var1 == null) {
         var1 = Locale.getDefault();
      }

      return var1;
   }

   public void setCharacterSubsets(Character.Subset[] var1) {
   }

   public void setCompositionEnabled(boolean var1) {
      throw new UnsupportedOperationException("Can't adjust composition mode on Mac OS X.");
   }

   public boolean isCompositionEnabled() {
      throw new UnsupportedOperationException("Can't adjust composition mode on Mac OS X.");
   }

   public void dispatchEvent(AWTEvent var1) {
   }

   public void activate() {
      this.isActive = true;
   }

   public void deactivate(boolean var1) {
      this.isActive = false;
   }

   public void hideWindows() {
   }

   long getNativeViewPtr(LWComponentPeer var1) {
      if (var1.getPlatformWindow() instanceof CPlatformWindow) {
         CPlatformWindow var2 = (CPlatformWindow)var1.getPlatformWindow();
         CPlatformView var3 = var2.getContentView();
         return var3.getAWTView();
      } else {
         return 0L;
      }
   }

   public void removeNotify() {
      if (this.fAwtFocussedComponentPeer != null) {
         this.nativeEndComposition(this.getNativeViewPtr(this.fAwtFocussedComponentPeer));
      }

      this.fAwtFocussedComponentPeer = null;
   }

   protected void setAWTFocussedComponent(Component var1) {
      LWComponentPeer var2 = null;
      long var3 = 0L;
      CInputMethod var5 = this;
      if (var1 == null) {
         var2 = this.fAwtFocussedComponentPeer;
         var5 = null;
      } else {
         var2 = this.getNearestNativePeer(var1);
         if (var1.getInputMethodRequests() == null) {
            var5 = null;
         }
      }

      if (var2 != null) {
         var3 = this.getNativeViewPtr(var2);
         this.nativeNotifyPeer(var3, var5);
      }

      this.fAwtFocussedComponent = var1;
      this.fAwtFocussedComponentPeer = this.getNearestNativePeer(var1);
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

      return sHighlightStyles[var1];
   }

   public void endComposition() {
      if (this.fAwtFocussedComponentPeer != null) {
         this.nativeEndComposition(this.getNativeViewPtr(this.fAwtFocussedComponentPeer));
      }

   }

   public void dispose() {
      this.fIMContext = null;
      this.fAwtFocussedComponent = null;
      this.fAwtFocussedComponentPeer = null;
   }

   public Object getControlObject() {
      return null;
   }

   private LWComponentPeer getNearestNativePeer(Component var1) {
      if (var1 == null) {
         return null;
      } else {
         ComponentPeer var2 = ((Component)var1).getPeer();
         if (var2 == null) {
            return null;
         } else {
            do {
               if (!(var2 instanceof LightweightPeer)) {
                  if (var2 instanceof LWComponentPeer) {
                     return (LWComponentPeer)var2;
                  }

                  return null;
               }

               var1 = ((Component)var1).getParent();
               if (var1 == null) {
                  return null;
               }

               var2 = ((Component)var1).getPeer();
            } while(var2 != null);

            return null;
         }
      }
   }

   private synchronized void insertText(String var1) {
      AttributedString var2 = new AttributedString(var1);
      var2.addAttribute(AttributedCharacterIterator.Attribute.LANGUAGE, this.getLocale(), 0, var1.length());
      TextHitInfo var3 = TextHitInfo.afterOffset(var1.length() - 1);
      InputMethodEvent var4 = new InputMethodEvent(this.fAwtFocussedComponent, 1100, var2.getIterator(), var1.length(), var3, var3);
      LWCToolkit.postEvent(LWCToolkit.targetToAppContext(this.fAwtFocussedComponent), var4);
      this.fCurrentText = null;
      this.fCurrentTextAsString = null;
      this.fCurrentTextLength = 0;
   }

   private void startIMUpdate(String var1) {
      this.fCurrentTextAsString = new String(var1);
      this.fCurrentText = new AttributedString(this.fCurrentTextAsString);
      this.fCurrentTextLength = var1.length();
   }

   private void addAttribute(boolean var1, boolean var2, int var3, int var4) {
      int var6 = var3 + var4;
      byte var7 = 1;
      if (var1 && var2) {
         var7 = 1;
      } else if (!var1 && var2) {
         var7 = 1;
      } else if (var1 && !var2) {
         var7 = 4;
      } else if (!var1 && !var2) {
         var7 = 3;
      }

      InputMethodHighlight var8;
      switch(var7) {
      case 1:
      default:
         var8 = InputMethodHighlight.UNSELECTED_RAW_TEXT_HIGHLIGHT;
         break;
      case 2:
         var8 = InputMethodHighlight.SELECTED_RAW_TEXT_HIGHLIGHT;
         break;
      case 3:
         var8 = InputMethodHighlight.UNSELECTED_CONVERTED_TEXT_HIGHLIGHT;
         break;
      case 4:
         var8 = InputMethodHighlight.SELECTED_CONVERTED_TEXT_HIGHLIGHT;
      }

      this.fCurrentText.addAttribute(TextAttribute.INPUT_METHOD_HIGHLIGHT, var8, var3, var6);
   }

   private void selectPreviousGlyph() {
      if (this.fIMContext != null) {
         try {
            LWCToolkit.invokeLater(new Runnable() {
               public void run() {
                  int var1 = CInputMethod.this.fIMContext.getInsertPositionOffset();
                  if (var1 >= 1) {
                     if (CInputMethod.this.fAwtFocussedComponent instanceof JTextComponent) {
                        ((JTextComponent)CInputMethod.this.fAwtFocussedComponent).select(var1 - 1, var1);
                     } else if (CInputMethod.this.fAwtFocussedComponent instanceof TextComponent) {
                        ((TextComponent)CInputMethod.this.fAwtFocussedComponent).select(var1 - 1, var1);
                     }
                  }
               }
            }, this.fAwtFocussedComponent);
         } catch (Exception var2) {
            var2.printStackTrace();
         }

      }
   }

   private void selectNextGlyph() {
      if (this.fIMContext != null && this.fAwtFocussedComponent instanceof JTextComponent) {
         try {
            LWCToolkit.invokeLater(new Runnable() {
               public void run() {
                  int var1 = CInputMethod.this.fIMContext.getInsertPositionOffset();
                  if (var1 >= 0) {
                     ((JTextComponent)CInputMethod.this.fAwtFocussedComponent).select(var1, var1 + 1);
                  }
               }
            }, this.fAwtFocussedComponent);
         } catch (Exception var2) {
            var2.printStackTrace();
         }

      }
   }

   private void dispatchText(int var1, int var2, boolean var3) {
      if (this.fCurrentText != null) {
         TextHitInfo var4 = var2 == 0 ? TextHitInfo.beforeOffset(var1) : null;
         TextHitInfo var5 = TextHitInfo.beforeOffset(0);
         InputMethodEvent var6 = new InputMethodEvent(this.fAwtFocussedComponent, 1100, this.fCurrentText.getIterator(), 0, var4, var5);
         LWCToolkit.postEvent(LWCToolkit.targetToAppContext(this.fAwtFocussedComponent), var6);
         if (var3) {
            this.selectNextGlyph();
         }

      }
   }

   private synchronized void unmarkText() {
      if (this.fCurrentText != null) {
         TextHitInfo var1 = TextHitInfo.afterOffset(this.fCurrentTextLength);
         InputMethodEvent var3 = new InputMethodEvent(this.fAwtFocussedComponent, 1100, this.fCurrentText.getIterator(), this.fCurrentTextLength, var1, var1);
         LWCToolkit.postEvent(LWCToolkit.targetToAppContext(this.fAwtFocussedComponent), var3);
         this.fCurrentText = null;
         this.fCurrentTextAsString = null;
         this.fCurrentTextLength = 0;
      }
   }

   private synchronized boolean hasMarkedText() {
      return this.fCurrentText != null;
   }

   private synchronized String attributedSubstringFromRange(final int var1, final int var2) {
      final String[] var3 = new String[1];

      try {
         LWCToolkit.invokeAndWait(new Runnable() {
            public void run() {
               synchronized(var3) {
                  int var2x = var1;
                  int var3x = var2;
                  if (var2x + var3x > CInputMethod.this.fIMContext.getCommittedTextLength() + CInputMethod.this.fCurrentTextLength) {
                     var3x = CInputMethod.this.fIMContext.getCommittedTextLength() - var2x;
                  }

                  AttributedCharacterIterator var4 = null;
                  if (CInputMethod.this.fCurrentText == null) {
                     var4 = CInputMethod.this.fIMContext.getCommittedText(var2x, var2x + var3x, (AttributedCharacterIterator.Attribute[])null);
                  } else {
                     int var5 = CInputMethod.this.fIMContext.getInsertPositionOffset();
                     if (var2x < var5) {
                        var4 = CInputMethod.this.fIMContext.getCommittedText(var2x, var2x + var3x, (AttributedCharacterIterator.Attribute[])null);
                     } else if (var2x >= var5 && var2x < var5 + CInputMethod.this.fCurrentTextLength) {
                        var4 = CInputMethod.this.fCurrentText.getIterator((AttributedCharacterIterator.Attribute[])null, var2x - var5, var2x - var5 + var3x);
                     } else {
                        var4 = CInputMethod.this.fIMContext.getCommittedText(var2x - CInputMethod.this.fCurrentTextLength, var2x - CInputMethod.this.fCurrentTextLength + var3x, (AttributedCharacterIterator.Attribute[])null);
                     }
                  }

                  char[] var10 = new char[var4.getEndIndex() - var4.getBeginIndex()];
                  char var6 = var4.first();

                  for(int var7 = 0; var6 != '\uffff'; var6 = var4.next()) {
                     var10[var7++] = var6;
                  }

                  var3[0] = new String(var10);
               }
            }
         }, this.fAwtFocussedComponent);
      } catch (InvocationTargetException var7) {
         var7.printStackTrace();
      }

      synchronized(var3) {
         return var3[0];
      }
   }

   private synchronized int[] selectedRange() {
      final int[] var1 = new int[2];

      try {
         LWCToolkit.invokeAndWait(new Runnable() {
            public void run() {
               synchronized(var1) {
                  AttributedCharacterIterator var2 = CInputMethod.this.fIMContext.getSelectedText((AttributedCharacterIterator.Attribute[])null);
                  if (var2 == null) {
                     var1[0] = CInputMethod.this.fIMContext.getInsertPositionOffset();
                     var1[1] = 0;
                  } else {
                     int var3;
                     if (CInputMethod.this.fAwtFocussedComponent instanceof JTextComponent) {
                        JTextComponent var4 = (JTextComponent)CInputMethod.this.fAwtFocussedComponent;
                        var3 = var4.getSelectionStart();
                     } else if (CInputMethod.this.fAwtFocussedComponent instanceof TextComponent) {
                        TextComponent var7 = (TextComponent)CInputMethod.this.fAwtFocussedComponent;
                        var3 = var7.getSelectionStart();
                     } else {
                        var3 = CInputMethod.this.fIMContext.getInsertPositionOffset() - (var2.getEndIndex() - var2.getBeginIndex());
                        if (var3 < 0) {
                           var3 = CInputMethod.this.fIMContext.getInsertPositionOffset() + (var2.getEndIndex() - var2.getBeginIndex());
                        }
                     }

                     var1[0] = var3;
                     var1[1] = var2.getEndIndex() - var2.getBeginIndex();
                  }
               }
            }
         }, this.fAwtFocussedComponent);
      } catch (InvocationTargetException var5) {
         var5.printStackTrace();
      }

      synchronized(var1) {
         return var1;
      }
   }

   private synchronized int[] markedRange() {
      if (this.fCurrentText == null) {
         return null;
      } else {
         final int[] var1 = new int[2];

         try {
            LWCToolkit.invokeAndWait(new Runnable() {
               public void run() {
                  synchronized(var1) {
                     var1[0] = CInputMethod.this.fIMContext.getInsertPositionOffset();
                  }
               }
            }, this.fAwtFocussedComponent);
         } catch (InvocationTargetException var5) {
            var5.printStackTrace();
         }

         var1[1] = this.fCurrentTextLength;
         synchronized(var1) {
            return var1;
         }
      }
   }

   private synchronized int[] firstRectForCharacterRange(final int var1) {
      final int[] var2 = new int[4];

      try {
         LWCToolkit.invokeAndWait(new Runnable() {
            public void run() {
               synchronized(var2) {
                  int var2x = CInputMethod.this.fIMContext.getInsertPositionOffset();
                  int var3 = var1 - var2x;
                  if (var3 < 0) {
                     var3 = 0;
                  }

                  Rectangle var4 = CInputMethod.this.fIMContext.getTextLocation(TextHitInfo.beforeOffset(var3));
                  var2[0] = var4.x;
                  var2[1] = var4.y;
                  var2[2] = var4.width;
                  var2[3] = var4.height;
                  if (var3 > 0 && CInputMethod.this.fAwtFocussedComponent instanceof JTextComponent) {
                     Rectangle var5 = CInputMethod.this.fIMContext.getTextLocation(TextHitInfo.beforeOffset(0));
                     if (var4.equals(var5)) {
                        String var6 = CInputMethod.this.fCurrentTextAsString.substring(0, var3);
                        Graphics var7 = CInputMethod.this.fAwtFocussedComponent.getGraphics();
                        int var8 = var7.getFontMetrics().stringWidth(var6);
                        int[] var10000 = var2;
                        var10000[0] += var8;
                        var7.dispose();
                     }
                  }

               }
            }
         }, this.fAwtFocussedComponent);
      } catch (InvocationTargetException var6) {
         var6.printStackTrace();
      }

      synchronized(var2) {
         return var2;
      }
   }

   private synchronized int characterIndexForPoint(final int var1, final int var2) {
      final TextHitInfo[] var3 = new TextHitInfo[1];
      final int[] var4 = new int[1];

      try {
         LWCToolkit.invokeAndWait(new Runnable() {
            public void run() {
               synchronized(var3) {
                  var3[0] = CInputMethod.this.fIMContext.getLocationOffset(var1, var2);
                  var4[0] = CInputMethod.this.fIMContext.getInsertPositionOffset();
               }
            }
         }, this.fAwtFocussedComponent);
      } catch (InvocationTargetException var6) {
         var6.printStackTrace();
      }

      if (var3[0] == null) {
         return var4[0];
      } else {
         int var5 = var3[0].getCharIndex() + var4[0];
         if (var3[0].getCharIndex() == this.fCurrentTextLength) {
            --var5;
         }

         return var5;
      }
   }

   public void disableInputMethod() {
   }

   public String getNativeInputMethodInfo() {
      return nativeGetCurrentInputMethodInfo();
   }

   private native void nativeNotifyPeer(long var1, CInputMethod var3);

   private native void nativeEndComposition(long var1);

   private native void nativeHandleEvent(LWComponentPeer var1, AWTEvent var2);

   static native Locale getNativeLocale();

   static native boolean setNativeLocale(String var0, boolean var1);

   static native String nativeGetCurrentInputMethodInfo();

   static native void nativeInit();

   static {
      Map[] var0 = new Map[4];
      HashMap var1 = new HashMap(1);
      var1.put(TextAttribute.INPUT_METHOD_UNDERLINE, TextAttribute.UNDERLINE_LOW_GRAY);
      var0[0] = Collections.unmodifiableMap(var1);
      var1 = new HashMap(1);
      var1.put(TextAttribute.INPUT_METHOD_UNDERLINE, TextAttribute.UNDERLINE_LOW_GRAY);
      var0[1] = Collections.unmodifiableMap(var1);
      var1 = new HashMap(1);
      var1.put(TextAttribute.INPUT_METHOD_UNDERLINE, TextAttribute.UNDERLINE_LOW_ONE_PIXEL);
      var0[2] = Collections.unmodifiableMap(var1);
      var1 = new HashMap(1);
      var1.put(TextAttribute.INPUT_METHOD_UNDERLINE, TextAttribute.UNDERLINE_LOW_TWO_PIXEL);
      var0[3] = Collections.unmodifiableMap(var1);
      sHighlightStyles = var0;
      nativeInit();
   }
}
