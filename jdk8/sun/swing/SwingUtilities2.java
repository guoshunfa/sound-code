package sun.swing;

import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FocusTraversalPolicy;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.PrintGraphics;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Toolkit;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextAttribute;
import java.awt.font.TextHitInfo;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.print.PrinterGraphics;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.text.BreakIterator;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.event.TreeModelEvent;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.swing.text.DefaultCaret;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import javax.swing.text.JTextComponent;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import sun.awt.AppContext;
import sun.awt.SunToolkit;
import sun.font.FontDesignMetrics;
import sun.font.FontUtilities;
import sun.java2d.SunGraphicsEnvironment;
import sun.print.ProxyPrintGraphics;
import sun.security.util.SecurityConstants;

public class SwingUtilities2 {
   public static final Object LAF_STATE_KEY = new StringBuffer("LookAndFeel State");
   public static final Object MENU_SELECTION_MANAGER_LISTENER_KEY = new StringBuffer("MenuSelectionManager listener key");
   private static SwingUtilities2.LSBCacheEntry[] fontCache = new SwingUtilities2.LSBCacheEntry[6];
   private static final int CACHE_SIZE = 6;
   private static int nextIndex;
   private static SwingUtilities2.LSBCacheEntry searchKey;
   private static final int MIN_CHAR_INDEX = 87;
   private static final int MAX_CHAR_INDEX = 88;
   public static final FontRenderContext DEFAULT_FRC = new FontRenderContext((AffineTransform)null, false, false);
   public static final Object AA_TEXT_PROPERTY_KEY = new StringBuffer("AATextInfoPropertyKey");
   public static final String IMPLIED_CR = "CR";
   private static final StringBuilder SKIP_CLICK_COUNT = new StringBuilder("skipClickCount");
   public static final Object COMPONENT_UI_PROPERTY_KEY = new StringBuffer("ComponentUIPropertyKey");
   public static final StringUIClientPropertyKey BASICMENUITEMUI_MAX_TEXT_OFFSET = new StringUIClientPropertyKey("maxTextOffset");
   private static Field inputEvent_CanAccessSystemClipboard_Field = null;
   private static final String UntrustedClipboardAccess = "UNTRUSTED_CLIPBOARD_ACCESS_KEY";
   private static final int CHAR_BUFFER_SIZE = 100;
   private static final Object charsBufferLock = new Object();
   private static char[] charsBuffer = new char[100];

   private static int syncCharsBuffer(String var0) {
      int var1 = var0.length();
      if (charsBuffer != null && charsBuffer.length >= var1) {
         var0.getChars(0, var1, charsBuffer, 0);
      } else {
         charsBuffer = var0.toCharArray();
      }

      return var1;
   }

   public static final boolean isComplexLayout(char[] var0, int var1, int var2) {
      return FontUtilities.isComplexText(var0, var1, var2);
   }

   public static SwingUtilities2.AATextInfo drawTextAntialiased(JComponent var0) {
      return var0 != null ? (SwingUtilities2.AATextInfo)var0.getClientProperty(AA_TEXT_PROPERTY_KEY) : null;
   }

   public static int getLeftSideBearing(JComponent var0, FontMetrics var1, String var2) {
      return var2 != null && var2.length() != 0 ? getLeftSideBearing(var0, var1, var2.charAt(0)) : 0;
   }

   public static int getLeftSideBearing(JComponent var0, FontMetrics var1, char var2) {
      if (var2 < 'X' && var2 >= 'W') {
         Object var4 = null;
         FontRenderContext var5 = getFontRenderContext(var0, var1);
         Font var6 = var1.getFont();
         Class var7 = SwingUtilities2.class;
         synchronized(SwingUtilities2.class) {
            SwingUtilities2.LSBCacheEntry var8 = null;
            if (searchKey == null) {
               searchKey = new SwingUtilities2.LSBCacheEntry(var5, var6);
            } else {
               searchKey.reset(var5, var6);
            }

            SwingUtilities2.LSBCacheEntry[] var9 = fontCache;
            int var10 = var9.length;

            for(int var11 = 0; var11 < var10; ++var11) {
               SwingUtilities2.LSBCacheEntry var12 = var9[var11];
               if (searchKey.equals(var12)) {
                  var8 = var12;
                  break;
               }
            }

            if (var8 == null) {
               var8 = searchKey;
               fontCache[nextIndex] = searchKey;
               searchKey = null;
               nextIndex = (nextIndex + 1) % 6;
            }

            return var8.getLeftSideBearing(var2);
         }
      } else {
         return 0;
      }
   }

   public static FontMetrics getFontMetrics(JComponent var0, Graphics var1) {
      return getFontMetrics(var0, var1, var1.getFont());
   }

   public static FontMetrics getFontMetrics(JComponent var0, Graphics var1, Font var2) {
      return var0 != null ? var0.getFontMetrics(var2) : Toolkit.getDefaultToolkit().getFontMetrics(var2);
   }

   public static int stringWidth(JComponent var0, FontMetrics var1, String var2) {
      if (var2 != null && !var2.equals("")) {
         boolean var3 = var0 != null && var0.getClientProperty(TextAttribute.NUMERIC_SHAPING) != null;
         if (var3) {
            synchronized(charsBufferLock) {
               int var5 = syncCharsBuffer(var2);
               var3 = isComplexLayout(charsBuffer, 0, var5);
            }
         }

         if (var3) {
            TextLayout var4 = createTextLayout(var0, var2, var1.getFont(), var1.getFontRenderContext());
            return (int)var4.getAdvance();
         } else {
            return var1.stringWidth(var2);
         }
      } else {
         return 0;
      }
   }

   public static String clipStringIfNecessary(JComponent var0, FontMetrics var1, String var2, int var3) {
      if (var2 != null && !var2.equals("")) {
         int var4 = stringWidth(var0, var1, var2);
         return var4 > var3 ? clipString(var0, var1, var2, var3) : var2;
      } else {
         return "";
      }
   }

   public static String clipString(JComponent var0, FontMetrics var1, String var2, int var3) {
      String var4 = "...";
      var3 -= stringWidth(var0, var1, var4);
      if (var3 <= 0) {
         return var4;
      } else {
         boolean var5;
         synchronized(charsBufferLock) {
            int var7 = syncCharsBuffer(var2);
            var5 = isComplexLayout(charsBuffer, 0, var7);
            if (!var5) {
               int var8 = 0;

               for(int var9 = 0; var9 < var7; ++var9) {
                  var8 += var1.charWidth(charsBuffer[var9]);
                  if (var8 > var3) {
                     var2 = var2.substring(0, var9);
                     break;
                  }
               }
            }
         }

         if (var5) {
            AttributedString var6 = new AttributedString(var2);
            if (var0 != null) {
               var6.addAttribute(TextAttribute.NUMERIC_SHAPING, var0.getClientProperty(TextAttribute.NUMERIC_SHAPING));
            }

            LineBreakMeasurer var12 = new LineBreakMeasurer(var6.getIterator(), BreakIterator.getCharacterInstance(), getFontRenderContext(var0, var1));
            var2 = var2.substring(0, var12.nextOffset((float)var3));
         }

         return var2 + var4;
      }
   }

   public static void drawString(JComponent var0, Graphics var1, String var2, int var3, int var4) {
      if (var2 != null && var2.length() > 0) {
         TextLayout var8;
         if (isPrinting(var1)) {
            Graphics2D var5 = getGraphics2D(var1);
            if (var5 != null) {
               String var13 = trimTrailingSpaces(var2);
               if (!var13.isEmpty()) {
                  float var14 = (float)var5.getFont().getStringBounds(var13, DEFAULT_FRC).getWidth();
                  var8 = createTextLayout(var0, var2, var5.getFont(), var5.getFontRenderContext());
                  var8 = var8.getJustifiedLayout(var14);
                  Color var17 = var5.getColor();
                  if (var17 instanceof PrintColorUIResource) {
                     var5.setColor(((PrintColorUIResource)var17).getPrintColor());
                  }

                  var8.draw(var5, (float)var3, (float)var4);
                  var5.setColor(var17);
               }

               return;
            }
         }

         if (var1 instanceof Graphics2D) {
            SwingUtilities2.AATextInfo var12 = drawTextAntialiased(var0);
            Graphics2D var6 = (Graphics2D)var1;
            boolean var7 = var0 != null && var0.getClientProperty(TextAttribute.NUMERIC_SHAPING) != null;
            if (var7) {
               synchronized(charsBufferLock) {
                  int var9 = syncCharsBuffer(var2);
                  var7 = isComplexLayout(charsBuffer, 0, var9);
               }
            }

            if (var12 != null) {
               Object var15 = null;
               Object var16 = var6.getRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING);
               if (var12.aaHint != var16) {
                  var6.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, var12.aaHint);
               } else {
                  var16 = null;
               }

               if (var12.lcdContrastHint != null) {
                  var15 = var6.getRenderingHint(RenderingHints.KEY_TEXT_LCD_CONTRAST);
                  if (var12.lcdContrastHint.equals(var15)) {
                     var15 = null;
                  } else {
                     var6.setRenderingHint(RenderingHints.KEY_TEXT_LCD_CONTRAST, var12.lcdContrastHint);
                  }
               }

               if (var7) {
                  TextLayout var10 = createTextLayout(var0, var2, var6.getFont(), var6.getFontRenderContext());
                  var10.draw(var6, (float)var3, (float)var4);
               } else {
                  var1.drawString(var2, var3, var4);
               }

               if (var16 != null) {
                  var6.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, var16);
               }

               if (var15 != null) {
                  var6.setRenderingHint(RenderingHints.KEY_TEXT_LCD_CONTRAST, var15);
               }

               return;
            }

            if (var7) {
               var8 = createTextLayout(var0, var2, var6.getFont(), var6.getFontRenderContext());
               var8.draw(var6, (float)var3, (float)var4);
               return;
            }
         }

         var1.drawString(var2, var3, var4);
      }
   }

   public static void drawStringUnderlineCharAt(JComponent var0, Graphics var1, String var2, int var3, int var4, int var5) {
      if (var2 != null && var2.length() > 0) {
         drawString(var0, var1, var2, var4, var5);
         int var6 = var2.length();
         if (var3 >= 0 && var3 < var6) {
            byte var8 = 1;
            int var9 = 0;
            int var10 = 0;
            boolean var11 = isPrinting(var1);
            boolean var12 = var11;
            if (!var11) {
               synchronized(charsBufferLock) {
                  syncCharsBuffer(var2);
                  var12 = isComplexLayout(charsBuffer, 0, var6);
               }
            }

            if (!var12) {
               FontMetrics var13 = var1.getFontMetrics();
               var9 = var4 + stringWidth(var0, var13, var2.substring(0, var3));
               var10 = var13.charWidth(var2.charAt(var3));
            } else {
               Graphics2D var20 = getGraphics2D(var1);
               if (var20 != null) {
                  TextLayout var14 = createTextLayout(var0, var2, var20.getFont(), var20.getFontRenderContext());
                  if (var11) {
                     float var15 = (float)var20.getFont().getStringBounds(var2, DEFAULT_FRC).getWidth();
                     var14 = var14.getJustifiedLayout(var15);
                  }

                  TextHitInfo var21 = TextHitInfo.leading(var3);
                  TextHitInfo var16 = TextHitInfo.trailing(var3);
                  Shape var17 = var14.getVisualHighlightShape(var21, var16);
                  Rectangle var18 = var17.getBounds();
                  var9 = var4 + var18.x;
                  var10 = var18.width;
               }
            }

            var1.fillRect(var9, var5 + 1, var10, var8);
         }

      }
   }

   public static int loc2IndexFileList(JList var0, Point var1) {
      int var2 = var0.locationToIndex(var1);
      if (var2 != -1) {
         Object var3 = var0.getClientProperty("List.isFileList");
         if (var3 instanceof Boolean && (Boolean)var3 && !pointIsInActualBounds(var0, var2, var1)) {
            var2 = -1;
         }
      }

      return var2;
   }

   private static boolean pointIsInActualBounds(JList var0, int var1, Point var2) {
      ListCellRenderer var3 = var0.getCellRenderer();
      ListModel var4 = var0.getModel();
      Object var5 = var4.getElementAt(var1);
      Component var6 = var3.getListCellRendererComponent(var0, var5, var1, false, false);
      Dimension var7 = var6.getPreferredSize();
      Rectangle var8 = var0.getCellBounds(var1, var1);
      if (!var6.getComponentOrientation().isLeftToRight()) {
         var8.x += var8.width - var7.width;
      }

      var8.width = var7.width;
      return var8.contains(var2);
   }

   public static boolean pointOutsidePrefSize(JTable var0, int var1, int var2, Point var3) {
      if (var0.convertColumnIndexToModel(var2) == 0 && var1 != -1) {
         TableCellRenderer var4 = var0.getCellRenderer(var1, var2);
         Object var5 = var0.getValueAt(var1, var2);
         Component var6 = var4.getTableCellRendererComponent(var0, var5, false, false, var1, var2);
         Dimension var7 = var6.getPreferredSize();
         Rectangle var8 = var0.getCellRect(var1, var2, false);
         var8.width = var7.width;
         var8.height = var7.height;

         assert var3.x >= var8.x && var3.y >= var8.y;

         return var3.x > var8.x + var8.width || var3.y > var8.y + var8.height;
      } else {
         return true;
      }
   }

   public static void setLeadAnchorWithoutSelection(ListSelectionModel var0, int var1, int var2) {
      if (var2 == -1) {
         var2 = var1;
      }

      if (var1 == -1) {
         var0.setAnchorSelectionIndex(-1);
         var0.setLeadSelectionIndex(-1);
      } else {
         if (var0.isSelectedIndex(var1)) {
            var0.addSelectionInterval(var1, var1);
         } else {
            var0.removeSelectionInterval(var1, var1);
         }

         var0.setAnchorSelectionIndex(var2);
      }

   }

   public static boolean shouldIgnore(MouseEvent var0, JComponent var1) {
      return var1 == null || !var1.isEnabled() || !SwingUtilities.isLeftMouseButton(var0) || var0.isConsumed();
   }

   public static void adjustFocus(JComponent var0) {
      if (!var0.hasFocus() && var0.isRequestFocusEnabled()) {
         var0.requestFocus();
      }

   }

   public static int drawChars(JComponent var0, Graphics var1, char[] var2, int var3, int var4, int var5, int var6) {
      if (var4 <= 0) {
         return var5;
      } else {
         int var7 = var5 + getFontMetrics(var0, var1).charsWidth(var2, var3, var4);
         if (isPrinting(var1)) {
            Graphics2D var8 = getGraphics2D(var1);
            if (var8 != null) {
               FontRenderContext var9 = var8.getFontRenderContext();
               FontRenderContext var10 = getFontRenderContext(var0);
               if (var10 != null && !isFontRenderContextPrintCompatible(var9, var10)) {
                  String var19 = new String(var2, var3, var4);
                  TextLayout var12 = new TextLayout(var19, var8.getFont(), var9);
                  String var13 = trimTrailingSpaces(var19);
                  if (!var13.isEmpty()) {
                     float var14 = (float)var8.getFont().getStringBounds(var13, var10).getWidth();
                     var12 = var12.getJustifiedLayout(var14);
                     Color var15 = var8.getColor();
                     if (var15 instanceof PrintColorUIResource) {
                        var8.setColor(((PrintColorUIResource)var15).getPrintColor());
                     }

                     var12.draw(var8, (float)var5, (float)var6);
                     var8.setColor(var15);
                  }

                  return var7;
               }
            }
         }

         SwingUtilities2.AATextInfo var16 = drawTextAntialiased(var0);
         if (var16 != null && var1 instanceof Graphics2D) {
            Graphics2D var17 = (Graphics2D)var1;
            Object var18 = null;
            Object var11 = var17.getRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING);
            if (var16.aaHint != null && var16.aaHint != var11) {
               var17.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, var16.aaHint);
            } else {
               var11 = null;
            }

            if (var16.lcdContrastHint != null) {
               var18 = var17.getRenderingHint(RenderingHints.KEY_TEXT_LCD_CONTRAST);
               if (var16.lcdContrastHint.equals(var18)) {
                  var18 = null;
               } else {
                  var17.setRenderingHint(RenderingHints.KEY_TEXT_LCD_CONTRAST, var16.lcdContrastHint);
               }
            }

            var1.drawChars(var2, var3, var4, var5, var6);
            if (var11 != null) {
               var17.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, var11);
            }

            if (var18 != null) {
               var17.setRenderingHint(RenderingHints.KEY_TEXT_LCD_CONTRAST, var18);
            }
         } else {
            var1.drawChars(var2, var3, var4, var5, var6);
         }

         return var7;
      }
   }

   public static float drawString(JComponent var0, Graphics var1, AttributedCharacterIterator var2, int var3, int var4) {
      boolean var6 = isPrinting(var1);
      Color var7 = var1.getColor();
      if (var6 && var7 instanceof PrintColorUIResource) {
         var1.setColor(((PrintColorUIResource)var7).getPrintColor());
      }

      Graphics2D var8 = getGraphics2D(var1);
      float var5;
      if (var8 == null) {
         var1.drawString(var2, var3, var4);
         var5 = (float)var3;
      } else {
         FontRenderContext var9;
         if (var6) {
            var9 = getFontRenderContext(var0);
            if (var9.isAntiAliased() || var9.usesFractionalMetrics()) {
               var9 = new FontRenderContext(var9.getTransform(), false, false);
            }
         } else if ((var9 = getFRCProperty(var0)) == null) {
            var9 = var8.getFontRenderContext();
         }

         TextLayout var10;
         if (var6) {
            FontRenderContext var11 = var8.getFontRenderContext();
            if (!isFontRenderContextPrintCompatible(var9, var11)) {
               var10 = new TextLayout(var2, var11);
               AttributedCharacterIterator var12 = getTrimmedTrailingSpacesIterator(var2);
               if (var12 != null) {
                  float var13 = (new TextLayout(var12, var9)).getAdvance();
                  var10 = var10.getJustifiedLayout(var13);
               }
            } else {
               var10 = new TextLayout(var2, var9);
            }
         } else {
            var10 = new TextLayout(var2, var9);
         }

         var10.draw(var8, (float)var3, (float)var4);
         var5 = var10.getAdvance();
      }

      if (var6) {
         var1.setColor(var7);
      }

      return var5;
   }

   public static void drawVLine(Graphics var0, int var1, int var2, int var3) {
      if (var3 < var2) {
         int var4 = var3;
         var3 = var2;
         var2 = var4;
      }

      var0.fillRect(var1, var2, 1, var3 - var2 + 1);
   }

   public static void drawHLine(Graphics var0, int var1, int var2, int var3) {
      if (var2 < var1) {
         int var4 = var2;
         var2 = var1;
         var1 = var4;
      }

      var0.fillRect(var1, var3, var2 - var1 + 1, 1);
   }

   public static void drawRect(Graphics var0, int var1, int var2, int var3, int var4) {
      if (var3 >= 0 && var4 >= 0) {
         if (var4 != 0 && var3 != 0) {
            var0.fillRect(var1, var2, var3, 1);
            var0.fillRect(var1 + var3, var2, 1, var4);
            var0.fillRect(var1 + 1, var2 + var4, var3, 1);
            var0.fillRect(var1, var2 + 1, 1, var4);
         } else {
            var0.fillRect(var1, var2, var3 + 1, var4 + 1);
         }

      }
   }

   private static TextLayout createTextLayout(JComponent var0, String var1, Font var2, FontRenderContext var3) {
      Object var4 = var0 == null ? null : var0.getClientProperty(TextAttribute.NUMERIC_SHAPING);
      if (var4 == null) {
         return new TextLayout(var1, var2, var3);
      } else {
         HashMap var5 = new HashMap();
         var5.put(TextAttribute.FONT, var2);
         var5.put(TextAttribute.NUMERIC_SHAPING, var4);
         return new TextLayout(var1, var5, var3);
      }
   }

   private static boolean isFontRenderContextPrintCompatible(FontRenderContext var0, FontRenderContext var1) {
      if (var0 == var1) {
         return true;
      } else if (var0 != null && var1 != null) {
         if (var0.getFractionalMetricsHint() != var1.getFractionalMetricsHint()) {
            return false;
         } else if (!var0.isTransformed() && !var1.isTransformed()) {
            return true;
         } else {
            double[] var2 = new double[4];
            double[] var3 = new double[4];
            var0.getTransform().getMatrix(var2);
            var1.getTransform().getMatrix(var3);
            return var2[0] == var3[0] && var2[1] == var3[1] && var2[2] == var3[2] && var2[3] == var3[3];
         }
      } else {
         return false;
      }
   }

   public static Graphics2D getGraphics2D(Graphics var0) {
      if (var0 instanceof Graphics2D) {
         return (Graphics2D)var0;
      } else {
         return var0 instanceof ProxyPrintGraphics ? (Graphics2D)((Graphics2D)((ProxyPrintGraphics)var0).getGraphics()) : null;
      }
   }

   public static FontRenderContext getFontRenderContext(Component var0) {
      assert var0 != null;

      return var0 == null ? DEFAULT_FRC : var0.getFontMetrics(var0.getFont()).getFontRenderContext();
   }

   private static FontRenderContext getFontRenderContext(Component var0, FontMetrics var1) {
      assert var1 != null || var0 != null;

      return var1 != null ? var1.getFontRenderContext() : getFontRenderContext(var0);
   }

   public static FontMetrics getFontMetrics(JComponent var0, Font var1) {
      FontRenderContext var2 = getFRCProperty(var0);
      if (var2 == null) {
         var2 = DEFAULT_FRC;
      }

      return FontDesignMetrics.getMetrics(var1, var2);
   }

   private static FontRenderContext getFRCProperty(JComponent var0) {
      if (var0 != null) {
         SwingUtilities2.AATextInfo var1 = (SwingUtilities2.AATextInfo)var0.getClientProperty(AA_TEXT_PROPERTY_KEY);
         if (var1 != null) {
            return var1.frc;
         }
      }

      return null;
   }

   static boolean isPrinting(Graphics var0) {
      return var0 instanceof PrinterGraphics || var0 instanceof PrintGraphics;
   }

   private static String trimTrailingSpaces(String var0) {
      int var1;
      for(var1 = var0.length() - 1; var1 >= 0 && Character.isWhitespace(var0.charAt(var1)); --var1) {
      }

      return var0.substring(0, var1 + 1);
   }

   private static AttributedCharacterIterator getTrimmedTrailingSpacesIterator(AttributedCharacterIterator var0) {
      int var1 = var0.getIndex();

      char var2;
      for(var2 = var0.last(); var2 != '\uffff' && Character.isWhitespace(var2); var2 = var0.previous()) {
      }

      if (var2 != '\uffff') {
         int var3 = var0.getIndex();
         if (var3 == var0.getEndIndex() - 1) {
            var0.setIndex(var1);
            return var0;
         } else {
            AttributedString var4 = new AttributedString(var0, var0.getBeginIndex(), var3 + 1);
            return var4.getIterator();
         }
      } else {
         return null;
      }
   }

   public static boolean useSelectedTextColor(Highlighter.Highlight var0, JTextComponent var1) {
      Highlighter.HighlightPainter var2 = var0.getPainter();
      String var3 = var2.getClass().getName();
      if (var3.indexOf("javax.swing.text.DefaultHighlighter") != 0 && var3.indexOf("com.sun.java.swing.plaf.windows.WindowsTextUI") != 0) {
         return false;
      } else {
         try {
            DefaultHighlighter.DefaultHighlightPainter var4 = (DefaultHighlighter.DefaultHighlightPainter)var2;
            return var4.getColor() == null || var4.getColor().equals(var1.getSelectionColor());
         } catch (ClassCastException var5) {
            return false;
         }
      }
   }

   public static boolean canAccessSystemClipboard() {
      boolean var0 = false;
      if (!GraphicsEnvironment.isHeadless()) {
         SecurityManager var1 = System.getSecurityManager();
         if (var1 == null) {
            var0 = true;
         } else {
            try {
               var1.checkPermission(SecurityConstants.AWT.ACCESS_CLIPBOARD_PERMISSION);
               var0 = true;
            } catch (SecurityException var3) {
            }

            if (var0 && !isTrustedContext()) {
               var0 = canCurrentEventAccessSystemClipboard(true);
            }
         }
      }

      return var0;
   }

   public static boolean canCurrentEventAccessSystemClipboard() {
      return isTrustedContext() || canCurrentEventAccessSystemClipboard(false);
   }

   public static boolean canEventAccessSystemClipboard(AWTEvent var0) {
      return isTrustedContext() || canEventAccessSystemClipboard(var0, false);
   }

   private static synchronized boolean inputEvent_canAccessSystemClipboard(InputEvent var0) {
      if (inputEvent_CanAccessSystemClipboard_Field == null) {
         inputEvent_CanAccessSystemClipboard_Field = (Field)AccessController.doPrivileged(new PrivilegedAction<Field>() {
            public Field run() {
               try {
                  Field var1 = InputEvent.class.getDeclaredField("canAccessSystemClipboard");
                  var1.setAccessible(true);
                  return var1;
               } catch (SecurityException var2) {
               } catch (NoSuchFieldException var3) {
               }

               return null;
            }
         });
      }

      if (inputEvent_CanAccessSystemClipboard_Field == null) {
         return false;
      } else {
         boolean var1 = false;

         try {
            var1 = inputEvent_CanAccessSystemClipboard_Field.getBoolean(var0);
         } catch (IllegalAccessException var3) {
         }

         return var1;
      }
   }

   private static boolean isAccessClipboardGesture(InputEvent var0) {
      boolean var1 = false;
      if (var0 instanceof KeyEvent) {
         KeyEvent var2 = (KeyEvent)var0;
         int var3 = var2.getKeyCode();
         int var4 = var2.getModifiers();
         switch(var3) {
         case 67:
         case 86:
         case 88:
            var1 = var4 == 2;
            break;
         case 127:
            var1 = var4 == 1;
            break;
         case 155:
            var1 = var4 == 2 || var4 == 1;
            break;
         case 65485:
         case 65487:
         case 65489:
            var1 = true;
         }
      }

      return var1;
   }

   private static boolean canEventAccessSystemClipboard(AWTEvent var0, boolean var1) {
      if (!EventQueue.isDispatchThread()) {
         return true;
      } else {
         return !(var0 instanceof InputEvent) || var1 && !isAccessClipboardGesture((InputEvent)var0) ? false : inputEvent_canAccessSystemClipboard((InputEvent)var0);
      }
   }

   public static void checkAccess(int var0) {
      if (System.getSecurityManager() != null && !Modifier.isPublic(var0)) {
         throw new SecurityException("Resource is not accessible");
      }
   }

   private static boolean canCurrentEventAccessSystemClipboard(boolean var0) {
      AWTEvent var1 = EventQueue.getCurrentEvent();
      return canEventAccessSystemClipboard(var1, var0);
   }

   private static boolean isTrustedContext() {
      return System.getSecurityManager() == null || AppContext.getAppContext().get("UNTRUSTED_CLIPBOARD_ACCESS_KEY") == null;
   }

   public static String displayPropertiesToCSS(Font var0, Color var1) {
      StringBuffer var2 = new StringBuffer("body {");
      if (var0 != null) {
         var2.append(" font-family: ");
         var2.append(var0.getFamily());
         var2.append(" ; ");
         var2.append(" font-size: ");
         var2.append(var0.getSize());
         var2.append("pt ;");
         if (var0.isBold()) {
            var2.append(" font-weight: 700 ; ");
         }

         if (var0.isItalic()) {
            var2.append(" font-style: italic ; ");
         }
      }

      if (var1 != null) {
         var2.append(" color: #");
         if (var1.getRed() < 16) {
            var2.append('0');
         }

         var2.append(Integer.toHexString(var1.getRed()));
         if (var1.getGreen() < 16) {
            var2.append('0');
         }

         var2.append(Integer.toHexString(var1.getGreen()));
         if (var1.getBlue() < 16) {
            var2.append('0');
         }

         var2.append(Integer.toHexString(var1.getBlue()));
         var2.append(" ; ");
      }

      var2.append(" }");
      return var2.toString();
   }

   public static Object makeIcon(final Class<?> var0, final Class<?> var1, final String var2) {
      return new UIDefaults.LazyValue() {
         public Object createValue(UIDefaults var1x) {
            byte[] var2x = (byte[])AccessController.doPrivileged(new PrivilegedAction<byte[]>() {
               public byte[] run() {
                  try {
                     InputStream var1x = null;

                     for(Class var2x = var0; var2x != null; var2x = var2x.getSuperclass()) {
                        var1x = var2x.getResourceAsStream(var2);
                        if (var1x != null || var2x == var1) {
                           break;
                        }
                     }

                     if (var1x == null) {
                        return null;
                     } else {
                        BufferedInputStream var3 = new BufferedInputStream(var1x);
                        ByteArrayOutputStream var4 = new ByteArrayOutputStream(1024);
                        byte[] var5 = new byte[1024];

                        int var6;
                        while((var6 = var3.read(var5)) > 0) {
                           var4.write(var5, 0, var6);
                        }

                        var3.close();
                        var4.flush();
                        return var4.toByteArray();
                     }
                  } catch (IOException var7) {
                     System.err.println(var7.toString());
                     return null;
                  }
               }
            });
            if (var2x == null) {
               return null;
            } else if (var2x.length == 0) {
               System.err.println("warning: " + var2 + " is zero-length");
               return null;
            } else {
               return new ImageIconUIResource(var2x);
            }
         }
      };
   }

   public static boolean isLocalDisplay() {
      GraphicsEnvironment var1 = GraphicsEnvironment.getLocalGraphicsEnvironment();
      boolean var0;
      if (var1 instanceof SunGraphicsEnvironment) {
         var0 = ((SunGraphicsEnvironment)var1).isDisplayLocal();
      } else {
         var0 = true;
      }

      return var0;
   }

   public static int getUIDefaultsInt(Object var0) {
      return getUIDefaultsInt(var0, 0);
   }

   public static int getUIDefaultsInt(Object var0, Locale var1) {
      return getUIDefaultsInt(var0, var1, 0);
   }

   public static int getUIDefaultsInt(Object var0, int var1) {
      return getUIDefaultsInt(var0, (Locale)null, var1);
   }

   public static int getUIDefaultsInt(Object var0, Locale var1, int var2) {
      Object var3 = UIManager.get(var0, var1);
      if (var3 instanceof Integer) {
         return (Integer)var3;
      } else {
         if (var3 instanceof String) {
            try {
               return Integer.parseInt((String)var3);
            } catch (NumberFormatException var5) {
            }
         }

         return var2;
      }
   }

   public static Component compositeRequestFocus(Component var0) {
      if (var0 instanceof Container) {
         Container var1 = (Container)var0;
         if (var1.isFocusCycleRoot()) {
            FocusTraversalPolicy var2 = var1.getFocusTraversalPolicy();
            Component var3 = var2.getDefaultComponent(var1);
            if (var3 != null) {
               var3.requestFocus();
               return var3;
            }
         }

         Container var5 = var1.getFocusCycleRootAncestor();
         if (var5 != null) {
            FocusTraversalPolicy var6 = var5.getFocusTraversalPolicy();
            Component var4 = var6.getComponentAfter(var5, var1);
            if (var4 != null && SwingUtilities.isDescendingFrom(var4, var1)) {
               var4.requestFocus();
               return var4;
            }
         }
      }

      if (var0.isFocusable()) {
         var0.requestFocus();
         return var0;
      } else {
         return null;
      }
   }

   public static boolean tabbedPaneChangeFocusTo(Component var0) {
      if (var0 != null) {
         if (var0.isFocusTraversable()) {
            compositeRequestFocus(var0);
            return true;
         }

         if (var0 instanceof JComponent && ((JComponent)var0).requestDefaultFocus()) {
            return true;
         }
      }

      return false;
   }

   public static <V> Future<V> submit(Callable<V> var0) {
      if (var0 == null) {
         throw new NullPointerException();
      } else {
         FutureTask var1 = new FutureTask(var0);
         execute(var1);
         return var1;
      }
   }

   public static <V> Future<V> submit(Runnable var0, V var1) {
      if (var0 == null) {
         throw new NullPointerException();
      } else {
         FutureTask var2 = new FutureTask(var0, var1);
         execute(var2);
         return var2;
      }
   }

   private static void execute(Runnable var0) {
      SwingUtilities.invokeLater(var0);
   }

   public static void setSkipClickCount(Component var0, int var1) {
      if (var0 instanceof JTextComponent && ((JTextComponent)var0).getCaret() instanceof DefaultCaret) {
         ((JTextComponent)var0).putClientProperty(SKIP_CLICK_COUNT, var1);
      }

   }

   public static int getAdjustedClickCount(JTextComponent var0, MouseEvent var1) {
      int var2 = var1.getClickCount();
      if (var2 == 1) {
         var0.putClientProperty(SKIP_CLICK_COUNT, (Object)null);
      } else {
         Integer var3 = (Integer)var0.getClientProperty(SKIP_CLICK_COUNT);
         if (var3 != null) {
            return var2 - var3;
         }
      }

      return var2;
   }

   private static SwingUtilities2.Section liesIn(Rectangle var0, Point var1, boolean var2, boolean var3, boolean var4) {
      int var5;
      int var6;
      int var7;
      boolean var8;
      if (var2) {
         var5 = var0.x;
         var6 = var1.x;
         var7 = var0.width;
         var8 = var3;
      } else {
         var5 = var0.y;
         var6 = var1.y;
         var7 = var0.height;
         var8 = true;
      }

      int var9;
      if (var4) {
         var9 = var7 >= 30 ? 10 : var7 / 3;
         if (var6 < var5 + var9) {
            return var8 ? SwingUtilities2.Section.LEADING : SwingUtilities2.Section.TRAILING;
         } else if (var6 >= var5 + var7 - var9) {
            return var8 ? SwingUtilities2.Section.TRAILING : SwingUtilities2.Section.LEADING;
         } else {
            return SwingUtilities2.Section.MIDDLE;
         }
      } else {
         var9 = var5 + var7 / 2;
         if (var8) {
            return var6 >= var9 ? SwingUtilities2.Section.TRAILING : SwingUtilities2.Section.LEADING;
         } else {
            return var6 < var9 ? SwingUtilities2.Section.TRAILING : SwingUtilities2.Section.LEADING;
         }
      }
   }

   public static SwingUtilities2.Section liesInHorizontal(Rectangle var0, Point var1, boolean var2, boolean var3) {
      return liesIn(var0, var1, true, var2, var3);
   }

   public static SwingUtilities2.Section liesInVertical(Rectangle var0, Point var1, boolean var2) {
      return liesIn(var0, var1, false, false, var2);
   }

   public static int convertColumnIndexToModel(TableColumnModel var0, int var1) {
      return var1 < 0 ? var1 : var0.getColumn(var1).getModelIndex();
   }

   public static int convertColumnIndexToView(TableColumnModel var0, int var1) {
      if (var1 < 0) {
         return var1;
      } else {
         for(int var2 = 0; var2 < var0.getColumnCount(); ++var2) {
            if (var0.getColumn(var2).getModelIndex() == var1) {
               return var2;
            }
         }

         return -1;
      }
   }

   public static int getSystemMnemonicKeyMask() {
      Toolkit var0 = Toolkit.getDefaultToolkit();
      return var0 instanceof SunToolkit ? ((SunToolkit)var0).getFocusAcceleratorKeyMask() : 8;
   }

   public static TreePath getTreePath(TreeModelEvent var0, TreeModel var1) {
      TreePath var2 = var0.getTreePath();
      if (var2 == null && var1 != null) {
         Object var3 = var1.getRoot();
         if (var3 != null) {
            var2 = new TreePath(var3);
         }
      }

      return var2;
   }

   public interface RepaintListener {
      void repaintPerformed(JComponent var1, int var2, int var3, int var4, int var5);
   }

   public static enum Section {
      LEADING,
      MIDDLE,
      TRAILING;
   }

   private static class LSBCacheEntry {
      private static final byte UNSET = 127;
      private static final char[] oneChar = new char[1];
      private byte[] lsbCache = new byte[1];
      private Font font;
      private FontRenderContext frc;

      public LSBCacheEntry(FontRenderContext var1, Font var2) {
         this.reset(var1, var2);
      }

      public void reset(FontRenderContext var1, Font var2) {
         this.font = var2;
         this.frc = var1;

         for(int var3 = this.lsbCache.length - 1; var3 >= 0; --var3) {
            this.lsbCache[var3] = 127;
         }

      }

      public int getLeftSideBearing(char var1) {
         int var2 = var1 - 87;

         assert var2 >= 0 && var2 < 1;

         byte var3 = this.lsbCache[var2];
         if (var3 == 127) {
            oneChar[0] = var1;
            GlyphVector var4 = this.font.createGlyphVector(this.frc, oneChar);
            var3 = (byte)var4.getGlyphPixelBounds(0, this.frc, 0.0F, 0.0F).x;
            if (var3 < 0) {
               Object var5 = this.frc.getAntiAliasingHint();
               if (var5 == RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB || var5 == RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HBGR) {
                  ++var3;
               }
            }

            this.lsbCache[var2] = var3;
         }

         return var3;
      }

      public boolean equals(Object var1) {
         if (var1 == this) {
            return true;
         } else if (!(var1 instanceof SwingUtilities2.LSBCacheEntry)) {
            return false;
         } else {
            SwingUtilities2.LSBCacheEntry var2 = (SwingUtilities2.LSBCacheEntry)var1;
            return this.font.equals(var2.font) && this.frc.equals(var2.frc);
         }
      }

      public int hashCode() {
         int var1 = 17;
         if (this.font != null) {
            var1 = 37 * var1 + this.font.hashCode();
         }

         if (this.frc != null) {
            var1 = 37 * var1 + this.frc.hashCode();
         }

         return var1;
      }
   }

   public static class AATextInfo {
      Object aaHint;
      Integer lcdContrastHint;
      FontRenderContext frc;

      private static SwingUtilities2.AATextInfo getAATextInfoFromMap(Map var0) {
         Object var1 = var0.get(RenderingHints.KEY_TEXT_ANTIALIASING);
         Object var2 = var0.get(RenderingHints.KEY_TEXT_LCD_CONTRAST);
         return var1 != null && var1 != RenderingHints.VALUE_TEXT_ANTIALIAS_OFF && var1 != RenderingHints.VALUE_TEXT_ANTIALIAS_DEFAULT ? new SwingUtilities2.AATextInfo(var1, (Integer)var2) : null;
      }

      public static SwingUtilities2.AATextInfo getAATextInfo(boolean var0) {
         SunToolkit.setAAFontSettingsCondition(var0);
         Toolkit var1 = Toolkit.getDefaultToolkit();
         Object var2 = var1.getDesktopProperty("awt.font.desktophints");
         return var2 instanceof Map ? getAATextInfoFromMap((Map)var2) : null;
      }

      public AATextInfo(Object var1, Integer var2) {
         if (var1 == null) {
            throw new InternalError("null not allowed here");
         } else if (var1 != RenderingHints.VALUE_TEXT_ANTIALIAS_OFF && var1 != RenderingHints.VALUE_TEXT_ANTIALIAS_DEFAULT) {
            this.aaHint = var1;
            this.lcdContrastHint = var2;
            this.frc = new FontRenderContext((AffineTransform)null, var1, RenderingHints.VALUE_FRACTIONALMETRICS_DEFAULT);
         } else {
            throw new InternalError("AA must be on");
         }
      }
   }
}
