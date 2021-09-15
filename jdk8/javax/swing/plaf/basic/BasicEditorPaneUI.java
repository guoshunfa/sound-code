package javax.swing.plaf.basic;

import java.awt.Color;
import java.awt.Font;
import java.beans.PropertyChangeEvent;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import javax.swing.plaf.ActionMapUIResource;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.text.Document;
import javax.swing.text.EditorKit;
import javax.swing.text.JTextComponent;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.StyleSheet;
import sun.swing.SwingUtilities2;

public class BasicEditorPaneUI extends BasicTextUI {
   private static final String FONT_ATTRIBUTE_KEY = "FONT_ATTRIBUTE_KEY";

   public static ComponentUI createUI(JComponent var0) {
      return new BasicEditorPaneUI();
   }

   protected String getPropertyPrefix() {
      return "EditorPane";
   }

   public void installUI(JComponent var1) {
      super.installUI(var1);
      this.updateDisplayProperties(var1.getFont(), var1.getForeground());
   }

   public void uninstallUI(JComponent var1) {
      this.cleanDisplayProperties();
      super.uninstallUI(var1);
   }

   public EditorKit getEditorKit(JTextComponent var1) {
      JEditorPane var2 = (JEditorPane)this.getComponent();
      return var2.getEditorKit();
   }

   ActionMap getActionMap() {
      ActionMapUIResource var1 = new ActionMapUIResource();
      var1.put("requestFocus", new BasicTextUI.FocusAction());
      EditorKit var2 = this.getEditorKit(this.getComponent());
      if (var2 != null) {
         Action[] var3 = var2.getActions();
         if (var3 != null) {
            this.addActions(var1, var3);
         }
      }

      var1.put(TransferHandler.getCutAction().getValue("Name"), TransferHandler.getCutAction());
      var1.put(TransferHandler.getCopyAction().getValue("Name"), TransferHandler.getCopyAction());
      var1.put(TransferHandler.getPasteAction().getValue("Name"), TransferHandler.getPasteAction());
      return var1;
   }

   protected void propertyChange(PropertyChangeEvent var1) {
      super.propertyChange(var1);
      String var2 = var1.getPropertyName();
      Object var4;
      if ("editorKit".equals(var2)) {
         ActionMap var3 = SwingUtilities.getUIActionMap(this.getComponent());
         if (var3 != null) {
            var4 = var1.getOldValue();
            if (var4 instanceof EditorKit) {
               Action[] var5 = ((EditorKit)var4).getActions();
               if (var5 != null) {
                  this.removeActions(var3, var5);
               }
            }

            Object var8 = var1.getNewValue();
            if (var8 instanceof EditorKit) {
               Action[] var6 = ((EditorKit)var8).getActions();
               if (var6 != null) {
                  this.addActions(var3, var6);
               }
            }
         }

         this.updateFocusTraversalKeys();
      } else if ("editable".equals(var2)) {
         this.updateFocusTraversalKeys();
      } else if ("foreground".equals(var2) || "font".equals(var2) || "document".equals(var2) || "JEditorPane.w3cLengthUnits".equals(var2) || "JEditorPane.honorDisplayProperties".equals(var2)) {
         JTextComponent var7 = this.getComponent();
         this.updateDisplayProperties(var7.getFont(), var7.getForeground());
         if ("JEditorPane.w3cLengthUnits".equals(var2) || "JEditorPane.honorDisplayProperties".equals(var2)) {
            this.modelChanged();
         }

         if ("foreground".equals(var2)) {
            var4 = var7.getClientProperty("JEditorPane.honorDisplayProperties");
            boolean var9 = false;
            if (var4 instanceof Boolean) {
               var9 = (Boolean)var4;
            }

            if (var9) {
               this.modelChanged();
            }
         }
      }

   }

   void removeActions(ActionMap var1, Action[] var2) {
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Action var5 = var2[var4];
         var1.remove(var5.getValue("Name"));
      }

   }

   void addActions(ActionMap var1, Action[] var2) {
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Action var5 = var2[var4];
         var1.put(var5.getValue("Name"), var5);
      }

   }

   void updateDisplayProperties(Font var1, Color var2) {
      JTextComponent var3 = this.getComponent();
      Object var4 = var3.getClientProperty("JEditorPane.honorDisplayProperties");
      boolean var5 = false;
      Object var6 = var3.getClientProperty("JEditorPane.w3cLengthUnits");
      boolean var7 = false;
      if (var4 instanceof Boolean) {
         var5 = (Boolean)var4;
      }

      if (var6 instanceof Boolean) {
         var7 = (Boolean)var6;
      }

      Document var8;
      if (!(this instanceof BasicTextPaneUI) && !var5) {
         this.cleanDisplayProperties();
      } else {
         var8 = this.getComponent().getDocument();
         if (var8 instanceof StyledDocument) {
            if (var8 instanceof HTMLDocument && var5) {
               this.updateCSS(var1, var2);
            } else {
               this.updateStyle(var1, var2);
            }
         }
      }

      StyleSheet var9;
      if (var7) {
         var8 = this.getComponent().getDocument();
         if (var8 instanceof HTMLDocument) {
            var9 = ((HTMLDocument)var8).getStyleSheet();
            var9.addRule("W3C_LENGTH_UNITS_ENABLE");
         }
      } else {
         var8 = this.getComponent().getDocument();
         if (var8 instanceof HTMLDocument) {
            var9 = ((HTMLDocument)var8).getStyleSheet();
            var9.addRule("W3C_LENGTH_UNITS_DISABLE");
         }
      }

   }

   void cleanDisplayProperties() {
      Document var1 = this.getComponent().getDocument();
      if (var1 instanceof HTMLDocument) {
         StyleSheet var2 = ((HTMLDocument)var1).getStyleSheet();
         StyleSheet[] var3 = var2.getStyleSheets();
         if (var3 != null) {
            StyleSheet[] var4 = var3;
            int var5 = var3.length;

            for(int var6 = 0; var6 < var5; ++var6) {
               StyleSheet var7 = var4[var6];
               if (var7 instanceof BasicEditorPaneUI.StyleSheetUIResource) {
                  var2.removeStyleSheet(var7);
                  var2.addRule("BASE_SIZE_DISABLE");
                  break;
               }
            }
         }

         Style var8 = ((StyledDocument)var1).getStyle("default");
         if (var8.getAttribute("FONT_ATTRIBUTE_KEY") != null) {
            var8.removeAttribute("FONT_ATTRIBUTE_KEY");
         }
      }

   }

   private void updateCSS(Font var1, Color var2) {
      JTextComponent var3 = this.getComponent();
      Document var4 = var3.getDocument();
      if (var4 instanceof HTMLDocument) {
         BasicEditorPaneUI.StyleSheetUIResource var5 = new BasicEditorPaneUI.StyleSheetUIResource();
         StyleSheet var6 = ((HTMLDocument)var4).getStyleSheet();
         StyleSheet[] var7 = var6.getStyleSheets();
         if (var7 != null) {
            StyleSheet[] var8 = var7;
            int var9 = var7.length;

            for(int var10 = 0; var10 < var9; ++var10) {
               StyleSheet var11 = var8[var10];
               if (var11 instanceof BasicEditorPaneUI.StyleSheetUIResource) {
                  var6.removeStyleSheet(var11);
               }
            }
         }

         String var12 = SwingUtilities2.displayPropertiesToCSS(var1, var2);
         var5.addRule(var12);
         var6.addStyleSheet(var5);
         var6.addRule("BASE_SIZE " + var3.getFont().getSize());
         Style var13 = ((StyledDocument)var4).getStyle("default");
         if (!var1.equals(var13.getAttribute("FONT_ATTRIBUTE_KEY"))) {
            var13.addAttribute("FONT_ATTRIBUTE_KEY", var1);
         }
      }

   }

   private void updateStyle(Font var1, Color var2) {
      this.updateFont(var1);
      this.updateForeground(var2);
   }

   private void updateForeground(Color var1) {
      StyledDocument var2 = (StyledDocument)this.getComponent().getDocument();
      Style var3 = var2.getStyle("default");
      if (var3 != null) {
         if (var1 == null) {
            if (var3.getAttribute(StyleConstants.Foreground) != null) {
               var3.removeAttribute(StyleConstants.Foreground);
            }
         } else if (!var1.equals(StyleConstants.getForeground(var3))) {
            StyleConstants.setForeground(var3, var1);
         }

      }
   }

   private void updateFont(Font var1) {
      StyledDocument var2 = (StyledDocument)this.getComponent().getDocument();
      Style var3 = var2.getStyle("default");
      if (var3 != null) {
         String var4 = (String)var3.getAttribute(StyleConstants.FontFamily);
         Integer var5 = (Integer)var3.getAttribute(StyleConstants.FontSize);
         Boolean var6 = (Boolean)var3.getAttribute(StyleConstants.Bold);
         Boolean var7 = (Boolean)var3.getAttribute(StyleConstants.Italic);
         Font var8 = (Font)var3.getAttribute("FONT_ATTRIBUTE_KEY");
         if (var1 == null) {
            if (var4 != null) {
               var3.removeAttribute(StyleConstants.FontFamily);
            }

            if (var5 != null) {
               var3.removeAttribute(StyleConstants.FontSize);
            }

            if (var6 != null) {
               var3.removeAttribute(StyleConstants.Bold);
            }

            if (var7 != null) {
               var3.removeAttribute(StyleConstants.Italic);
            }

            if (var8 != null) {
               var3.removeAttribute("FONT_ATTRIBUTE_KEY");
            }
         } else {
            if (!var1.getName().equals(var4)) {
               StyleConstants.setFontFamily(var3, var1.getName());
            }

            if (var5 == null || var5 != var1.getSize()) {
               StyleConstants.setFontSize(var3, var1.getSize());
            }

            if (var6 == null || var6 != var1.isBold()) {
               StyleConstants.setBold(var3, var1.isBold());
            }

            if (var7 == null || var7 != var1.isItalic()) {
               StyleConstants.setItalic(var3, var1.isItalic());
            }

            if (!var1.equals(var8)) {
               var3.addAttribute("FONT_ATTRIBUTE_KEY", var1);
            }
         }

      }
   }

   static class StyleSheetUIResource extends StyleSheet implements UIResource {
   }
}
