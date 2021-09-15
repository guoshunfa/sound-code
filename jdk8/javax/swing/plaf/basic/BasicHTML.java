package javax.swing.plaf.basic;

import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Shape;
import java.io.StringReader;
import java.net.URL;
import javax.swing.JComponent;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.ParagraphView;
import javax.swing.text.Position;
import javax.swing.text.StyleConstants;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.ImageView;
import javax.swing.text.html.StyleSheet;
import sun.swing.SwingUtilities2;

public class BasicHTML {
   private static final String htmlDisable = "html.disable";
   public static final String propertyKey = "html";
   public static final String documentBaseKey = "html.base";
   private static BasicHTML.BasicEditorKit basicHTMLFactory;
   private static ViewFactory basicHTMLViewFactory;
   private static final String styleChanges = "p { margin-top: 0; margin-bottom: 0; margin-left: 0; margin-right: 0 }body { margin-top: 0; margin-bottom: 0; margin-left: 0; margin-right: 0 }";

   public static View createHTMLView(JComponent var0, String var1) {
      BasicHTML.BasicEditorKit var2 = getFactory();
      Document var3 = var2.createDefaultDocument(var0.getFont(), var0.getForeground());
      Object var4 = var0.getClientProperty("html.base");
      if (var4 instanceof URL) {
         ((HTMLDocument)var3).setBase((URL)var4);
      }

      StringReader var5 = new StringReader(var1);

      try {
         var2.read(var5, var3, 0);
      } catch (Throwable var9) {
      }

      ViewFactory var6 = var2.getViewFactory();
      View var7 = var6.create(var3.getDefaultRootElement());
      BasicHTML.Renderer var8 = new BasicHTML.Renderer(var0, var6, var7);
      return var8;
   }

   public static int getHTMLBaseline(View var0, int var1, int var2) {
      if (var1 >= 0 && var2 >= 0) {
         return var0 instanceof BasicHTML.Renderer ? getBaseline(var0.getView(0), var1, var2) : -1;
      } else {
         throw new IllegalArgumentException("Width and height must be >= 0");
      }
   }

   static int getBaseline(JComponent var0, int var1, int var2, int var3, int var4) {
      View var5 = (View)var0.getClientProperty("html");
      if (var5 != null) {
         int var6 = getHTMLBaseline(var5, var3, var4);
         return var6 < 0 ? var6 : var1 + var6;
      } else {
         return var1 + var2;
      }
   }

   static int getBaseline(View var0, int var1, int var2) {
      if (hasParagraph(var0)) {
         var0.setSize((float)var1, (float)var2);
         return getBaseline(var0, new Rectangle(0, 0, var1, var2));
      } else {
         return -1;
      }
   }

   private static int getBaseline(View var0, Shape var1) {
      if (var0.getViewCount() == 0) {
         return -1;
      } else {
         AttributeSet var2 = var0.getElement().getAttributes();
         Object var3 = null;
         if (var2 != null) {
            var3 = var2.getAttribute(StyleConstants.NameAttribute);
         }

         int var4 = 0;
         if (var3 == HTML.Tag.HTML && var0.getViewCount() > 1) {
            ++var4;
         }

         var1 = var0.getChildAllocation(var4, var1);
         if (var1 == null) {
            return -1;
         } else {
            View var5 = var0.getView(var4);
            if (var0 instanceof ParagraphView) {
               Rectangle var6;
               if (var1 instanceof Rectangle) {
                  var6 = (Rectangle)var1;
               } else {
                  var6 = var1.getBounds();
               }

               return var6.y + (int)((float)var6.height * var5.getAlignment(1));
            } else {
               return getBaseline(var5, var1);
            }
         }
      }
   }

   private static boolean hasParagraph(View var0) {
      if (var0 instanceof ParagraphView) {
         return true;
      } else if (var0.getViewCount() == 0) {
         return false;
      } else {
         AttributeSet var1 = var0.getElement().getAttributes();
         Object var2 = null;
         if (var1 != null) {
            var2 = var1.getAttribute(StyleConstants.NameAttribute);
         }

         byte var3 = 0;
         if (var2 == HTML.Tag.HTML && var0.getViewCount() > 1) {
            var3 = 1;
         }

         return hasParagraph(var0.getView(var3));
      }
   }

   public static boolean isHTMLString(String var0) {
      if (var0 != null && var0.length() >= 6 && var0.charAt(0) == '<' && var0.charAt(5) == '>') {
         String var1 = var0.substring(1, 5);
         return var1.equalsIgnoreCase("html");
      } else {
         return false;
      }
   }

   public static void updateRenderer(JComponent var0, String var1) {
      View var2 = null;
      View var3 = (View)var0.getClientProperty("html");
      Boolean var4 = (Boolean)var0.getClientProperty("html.disable");
      if (var4 != Boolean.TRUE && isHTMLString(var1)) {
         var2 = createHTMLView(var0, var1);
      }

      if (var2 != var3 && var3 != null) {
         for(int var5 = 0; var5 < var3.getViewCount(); ++var5) {
            var3.getView(var5).setParent((View)null);
         }
      }

      var0.putClientProperty("html", var2);
   }

   static BasicHTML.BasicEditorKit getFactory() {
      if (basicHTMLFactory == null) {
         basicHTMLViewFactory = new BasicHTML.BasicHTMLViewFactory();
         basicHTMLFactory = new BasicHTML.BasicEditorKit();
      }

      return basicHTMLFactory;
   }

   static class Renderer extends View {
      private int width;
      private View view;
      private ViewFactory factory;
      private JComponent host;

      Renderer(JComponent var1, ViewFactory var2, View var3) {
         super((Element)null);
         this.host = var1;
         this.factory = var2;
         this.view = var3;
         this.view.setParent(this);
         this.setSize(this.view.getPreferredSpan(0), this.view.getPreferredSpan(1));
      }

      public AttributeSet getAttributes() {
         return null;
      }

      public float getPreferredSpan(int var1) {
         return var1 == 0 ? (float)this.width : this.view.getPreferredSpan(var1);
      }

      public float getMinimumSpan(int var1) {
         return this.view.getMinimumSpan(var1);
      }

      public float getMaximumSpan(int var1) {
         return 2.14748365E9F;
      }

      public void preferenceChanged(View var1, boolean var2, boolean var3) {
         this.host.revalidate();
         this.host.repaint();
      }

      public float getAlignment(int var1) {
         return this.view.getAlignment(var1);
      }

      public void paint(Graphics var1, Shape var2) {
         Rectangle var3 = var2.getBounds();
         this.view.setSize((float)var3.width, (float)var3.height);
         this.view.paint(var1, var2);
      }

      public void setParent(View var1) {
         throw new Error("Can't set parent on root view");
      }

      public int getViewCount() {
         return 1;
      }

      public View getView(int var1) {
         return this.view;
      }

      public Shape modelToView(int var1, Shape var2, Position.Bias var3) throws BadLocationException {
         return this.view.modelToView(var1, var2, var3);
      }

      public Shape modelToView(int var1, Position.Bias var2, int var3, Position.Bias var4, Shape var5) throws BadLocationException {
         return this.view.modelToView(var1, var2, var3, var4, var5);
      }

      public int viewToModel(float var1, float var2, Shape var3, Position.Bias[] var4) {
         return this.view.viewToModel(var1, var2, var3, var4);
      }

      public Document getDocument() {
         return this.view.getDocument();
      }

      public int getStartOffset() {
         return this.view.getStartOffset();
      }

      public int getEndOffset() {
         return this.view.getEndOffset();
      }

      public Element getElement() {
         return this.view.getElement();
      }

      public void setSize(float var1, float var2) {
         this.width = (int)var1;
         this.view.setSize(var1, var2);
      }

      public Container getContainer() {
         return this.host;
      }

      public ViewFactory getViewFactory() {
         return this.factory;
      }
   }

   static class BasicDocument extends HTMLDocument {
      BasicDocument(StyleSheet var1, Font var2, Color var3) {
         super(var1);
         this.setPreservesUnknownTags(false);
         this.setFontAndColor(var2, var3);
      }

      private void setFontAndColor(Font var1, Color var2) {
         this.getStyleSheet().addRule(SwingUtilities2.displayPropertiesToCSS(var1, var2));
      }
   }

   static class BasicHTMLViewFactory extends HTMLEditorKit.HTMLFactory {
      public View create(Element var1) {
         View var2 = super.create(var1);
         if (var2 instanceof ImageView) {
            ((ImageView)var2).setLoadsSynchronously(true);
         }

         return var2;
      }
   }

   static class BasicEditorKit extends HTMLEditorKit {
      private static StyleSheet defaultStyles;

      public StyleSheet getStyleSheet() {
         if (defaultStyles == null) {
            defaultStyles = new StyleSheet();
            StringReader var1 = new StringReader("p { margin-top: 0; margin-bottom: 0; margin-left: 0; margin-right: 0 }body { margin-top: 0; margin-bottom: 0; margin-left: 0; margin-right: 0 }");

            try {
               defaultStyles.loadRules(var1, (URL)null);
            } catch (Throwable var3) {
            }

            var1.close();
            defaultStyles.addStyleSheet(super.getStyleSheet());
         }

         return defaultStyles;
      }

      public Document createDefaultDocument(Font var1, Color var2) {
         StyleSheet var3 = this.getStyleSheet();
         StyleSheet var4 = new StyleSheet();
         var4.addStyleSheet(var3);
         BasicHTML.BasicDocument var5 = new BasicHTML.BasicDocument(var4, var1, var2);
         var5.setAsynchronousLoadPriority(Integer.MAX_VALUE);
         var5.setPreservesUnknownTags(false);
         return var5;
      }

      public ViewFactory getViewFactory() {
         return BasicHTML.basicHTMLViewFactory;
      }
   }
}
