package sun.lwawt.macosx;

import java.awt.Component;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.concurrent.Callable;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleEditableText;
import javax.accessibility.AccessibleText;
import javax.swing.text.Element;
import javax.swing.text.JTextComponent;

class CAccessibleText {
   static AccessibleEditableText getAccessibleEditableText(final Accessible var0, Component var1) {
      return var0 == null ? null : (AccessibleEditableText)CAccessibility.invokeAndWait(new Callable<AccessibleEditableText>() {
         public AccessibleEditableText call() throws Exception {
            AccessibleContext var1 = var0.getAccessibleContext();
            return var1 == null ? null : var1.getAccessibleEditableText();
         }
      }, var1);
   }

   static String getSelectedText(final Accessible var0, Component var1) {
      return var0 == null ? null : (String)CAccessibility.invokeAndWait(new Callable<String>() {
         public String call() throws Exception {
            AccessibleContext var1 = var0.getAccessibleContext();
            if (var1 == null) {
               return null;
            } else {
               AccessibleText var2 = var1.getAccessibleText();
               return var2 == null ? null : var2.getSelectedText();
            }
         }
      }, var1);
   }

   static void setSelectedText(final Accessible var0, Component var1, final String var2) {
      if (var0 != null) {
         CAccessibility.invokeLater(new Runnable() {
            public void run() {
               AccessibleContext var1 = var0.getAccessibleContext();
               if (var1 != null) {
                  AccessibleEditableText var2x = var1.getAccessibleEditableText();
                  if (var2x != null) {
                     int var3 = var2x.getSelectionStart();
                     int var4 = var2x.getSelectionEnd();
                     var2x.replaceText(var3, var4, var2);
                  }
               }
            }
         }, var1);
      }
   }

   static void setSelectedTextRange(final Accessible var0, Component var1, final int var2, final int var3) {
      if (var0 != null) {
         CAccessibility.invokeLater(new Runnable() {
            public void run() {
               AccessibleContext var1 = var0.getAccessibleContext();
               if (var1 != null) {
                  AccessibleEditableText var2x = var1.getAccessibleEditableText();
                  if (var2x != null) {
                     boolean var3x = var2 >= 0 && var3 >= var2 && var3 <= var2x.getCharCount();
                     if (var3x) {
                        var2x.selectText(var2, var3);
                     }
                  }
               }
            }
         }, var1);
      }
   }

   static String getTextRange(final AccessibleEditableText var0, final int var1, final int var2, Component var3) {
      return var0 == null ? null : (String)CAccessibility.invokeAndWait(new Callable<String>() {
         public String call() throws Exception {
            return var0.getTextRange(var1, var2);
         }
      }, var3);
   }

   static int getCharacterIndexAtPosition(final Accessible var0, Component var1, final int var2, final int var3) {
      return var0 == null ? 0 : (Integer)CAccessibility.invokeAndWait(new Callable<Integer>() {
         public Integer call() throws Exception {
            AccessibleContext var1 = var0.getAccessibleContext();
            if (var1 == null) {
               return null;
            } else {
               AccessibleText var2x = var1.getAccessibleText();
               if (var2x == null) {
                  return null;
               } else {
                  Point var3x = var1.getAccessibleComponent().getLocationOnScreen();
                  int var4 = var2 - (int)var3x.getX();
                  int var5 = var3 - (int)var3x.getY();
                  return var2x.getIndexAtPoint(new Point(var4, var5));
               }
            }
         }
      }, var1);
   }

   static int[] getSelectedTextRange(final Accessible var0, Component var1) {
      return var0 == null ? new int[2] : (int[])CAccessibility.invokeAndWait(new Callable<int[]>() {
         public int[] call() {
            AccessibleContext var1 = var0.getAccessibleContext();
            if (var1 == null) {
               return new int[2];
            } else {
               AccessibleText var2 = var1.getAccessibleText();
               if (var2 == null) {
                  return new int[2];
               } else {
                  int[] var3 = new int[]{var2.getSelectionStart(), var2.getSelectionEnd()};
                  return var3;
               }
            }
         }
      }, var1);
   }

   static int[] getVisibleCharacterRange(final Accessible var0, Component var1) {
      return var0 == null ? null : (int[])CAccessibility.invokeAndWait(new Callable<int[]>() {
         public int[] call() {
            return CAccessibleText.getVisibleCharacterRange(var0);
         }
      }, var1);
   }

   static int getLineNumberForIndex(final Accessible var0, Component var1, final int var2) {
      return var0 == null ? 0 : (Integer)CAccessibility.invokeAndWait(new Callable<Integer>() {
         public Integer call() {
            return CAccessibleText.getLineNumberForIndex(var0, var2);
         }
      }, var1);
   }

   static int getLineNumberForInsertionPoint(final Accessible var0, Component var1) {
      return var0 == null ? 0 : (Integer)CAccessibility.invokeAndWait(new Callable<Integer>() {
         public Integer call() {
            return CAccessibleText.getLineNumberForInsertionPoint(var0);
         }
      }, var1);
   }

   static int[] getRangeForLine(final Accessible var0, Component var1, final int var2) {
      return var0 == null ? null : (int[])CAccessibility.invokeAndWait(new Callable<int[]>() {
         public int[] call() {
            return CAccessibleText.getRangeForLine(var0, var2);
         }
      }, var1);
   }

   static int[] getRangeForIndex(final Accessible var0, Component var1, final int var2) {
      return var0 == null ? new int[2] : (int[])CAccessibility.invokeAndWait(new Callable<int[]>() {
         public int[] call() {
            AccessibleContext var1 = var0.getAccessibleContext();
            if (var1 == null) {
               return new int[2];
            } else {
               AccessibleEditableText var2x = var1.getAccessibleEditableText();
               if (var2x == null) {
                  return new int[2];
               } else {
                  int var3 = var2x.getCharCount();
                  if (var2 >= var3) {
                     return new int[2];
                  } else {
                     String var4 = var2x.getAtIndex(2, var2);
                     int var5 = var4.length();
                     String var6 = var2x.getTextRange(0, var3 - 1);
                     int var7 = -1;

                     for(int var8 = 0; var7 == -1 && var8 < var5; ++var8) {
                        if (var6.regionMatches(true, var2 - var8, var4, 0, var5)) {
                           var7 = var2 - var8;
                        }

                        if (var6.regionMatches(true, var2 + var8, var4, 0, var5)) {
                           var7 = var2 + var8;
                        }
                     }

                     int[] var9 = new int[]{var7, var7 + var5};
                     return var9;
                  }
               }
            }
         }
      }, var1);
   }

   static double[] getBoundsForRange(final Accessible var0, Component var1, final int var2, final int var3) {
      final double[] var4 = new double[4];
      return var0 == null ? var4 : (double[])CAccessibility.invokeAndWait(new Callable<double[]>() {
         public double[] call() throws Exception {
            AccessibleContext var1 = var0.getAccessibleContext();
            if (var1 == null) {
               return var4;
            } else {
               AccessibleText var2x = var1.getAccessibleText();
               if (var2x == null) {
                  var1.getAccessibleName();
                  var1.getAccessibleEditableText();
                  return var4;
               } else {
                  Rectangle var3x = var2x.getCharacterBounds(var2);
                  Rectangle var4x = var2x.getCharacterBounds(var2 + var3 - 1);
                  if (var4x != null && var3x != null) {
                     Rectangle2D var5 = var3x.createUnion(var4x);
                     if (var5.isEmpty()) {
                        return var4;
                     } else {
                        double var6 = var5.getX();
                        double var8 = var5.getY();
                        Point var10 = var1.getAccessibleComponent().getLocationOnScreen();
                        if (var10 == null) {
                           return var4;
                        } else {
                           double var11 = var10.getX() + var6;
                           double var13 = var10.getY() + var8;
                           var4[0] = var11;
                           var4[1] = var13;
                           var4[2] = var5.getWidth();
                           var4[3] = var5.getHeight();
                           return var4;
                        }
                     }
                  } else {
                     return var4;
                  }
               }
            }
         }
      }, var1);
   }

   static String getStringForRange(final Accessible var0, Component var1, final int var2, final int var3) {
      return var0 == null ? null : (String)CAccessibility.invokeAndWait(new Callable<String>() {
         public String call() throws Exception {
            AccessibleContext var1 = var0.getAccessibleContext();
            if (var1 == null) {
               return null;
            } else {
               AccessibleEditableText var2x = var1.getAccessibleEditableText();
               return var2x == null ? null : var2x.getTextRange(var2, var2 + var3);
            }
         }
      }, var1);
   }

   static int[] getVisibleCharacterRange(Accessible var0) {
      Accessible var1 = CAccessible.getSwingAccessible(var0);
      if (!(var1 instanceof JTextComponent)) {
         return null;
      } else {
         JTextComponent var2 = (JTextComponent)var1;
         Rectangle var3 = var2.getVisibleRect();
         Point var4 = new Point(var3.x, var3.y);
         Point var5 = new Point(var3.x + var3.width, var3.y);
         Point var6 = new Point(var3.x, var3.y + var3.height);
         Point var7 = new Point(var3.x + var3.width, var3.y + var3.height);
         int var8 = Math.min(var2.viewToModel(var4), var2.viewToModel(var5));
         int var9 = Math.max(var2.viewToModel(var6), var2.viewToModel(var7));
         if (var8 < 0) {
            var8 = 0;
         }

         if (var9 < 0) {
            var9 = 0;
         }

         return new int[]{var8, var9};
      }
   }

   static int getLineNumberForIndex(Accessible var0, int var1) {
      Accessible var2 = CAccessible.getSwingAccessible(var0);
      if (!(var2 instanceof JTextComponent)) {
         return -1;
      } else {
         JTextComponent var3 = (JTextComponent)var2;
         Element var4 = var3.getDocument().getDefaultRootElement();
         if (var1 == -1) {
            var1 = var3.getCaretPosition();
         }

         return var4.getElementIndex(var1);
      }
   }

   static int getLineNumberForInsertionPoint(Accessible var0) {
      return getLineNumberForIndex(var0, -1);
   }

   static int[] getRangeForLine(Accessible var0, int var1) {
      Accessible var2 = CAccessible.getSwingAccessible(var0);
      if (!(var2 instanceof JTextComponent)) {
         return null;
      } else {
         JTextComponent var3 = (JTextComponent)var2;
         Element var4 = var3.getDocument().getDefaultRootElement();
         Element var5 = var4.getElement(var1);
         return var5 == null ? null : new int[]{var5.getStartOffset(), var5.getEndOffset()};
      }
   }
}
