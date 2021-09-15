package javax.swing.text.html;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Shape;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.ComponentView;
import javax.swing.text.Document;
import javax.swing.text.EditorKit;
import javax.swing.text.Element;
import javax.swing.text.JTextComponent;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;
import sun.swing.text.html.FrameEditorPaneTag;

class FrameView extends ComponentView implements HyperlinkListener {
   JEditorPane htmlPane;
   JScrollPane scroller;
   boolean editable;
   float width;
   float height;
   URL src;
   private boolean createdComponent;

   public FrameView(Element var1) {
      super(var1);
   }

   protected Component createComponent() {
      Element var1 = this.getElement();
      AttributeSet var2 = var1.getAttributes();
      String var3 = (String)var2.getAttribute(HTML.Attribute.SRC);
      if (var3 != null && !var3.equals("")) {
         try {
            URL var4 = ((HTMLDocument)var1.getDocument()).getBase();
            this.src = new URL(var4, var3);
            this.htmlPane = new FrameView.FrameEditorPane();
            this.htmlPane.addHyperlinkListener(this);
            JEditorPane var5 = this.getHostPane();
            boolean var6 = true;
            if (var5 != null) {
               this.htmlPane.setEditable(var5.isEditable());
               String var7 = (String)var5.getClientProperty("charset");
               if (var7 != null) {
                  this.htmlPane.putClientProperty("charset", var7);
               }

               HTMLEditorKit var8 = (HTMLEditorKit)var5.getEditorKit();
               if (var8 != null) {
                  var6 = var8.isAutoFormSubmission();
               }
            }

            this.htmlPane.setPage(this.src);
            HTMLEditorKit var11 = (HTMLEditorKit)this.htmlPane.getEditorKit();
            if (var11 != null) {
               var11.setAutoFormSubmission(var6);
            }

            Document var12 = this.htmlPane.getDocument();
            if (var12 instanceof HTMLDocument) {
               ((HTMLDocument)var12).setFrameDocumentState(true);
            }

            this.setMargin();
            this.createScrollPane();
            this.setBorder();
         } catch (MalformedURLException var9) {
            var9.printStackTrace();
         } catch (IOException var10) {
            var10.printStackTrace();
         }
      }

      this.createdComponent = true;
      return this.scroller;
   }

   JEditorPane getHostPane() {
      Container var1;
      for(var1 = this.getContainer(); var1 != null && !(var1 instanceof JEditorPane); var1 = var1.getParent()) {
      }

      return (JEditorPane)var1;
   }

   public void setParent(View var1) {
      if (var1 != null) {
         JTextComponent var2 = (JTextComponent)var1.getContainer();
         this.editable = var2.isEditable();
      }

      super.setParent(var1);
   }

   public void paint(Graphics var1, Shape var2) {
      Container var3 = this.getContainer();
      if (var3 != null && this.htmlPane != null && this.htmlPane.isEditable() != ((JTextComponent)var3).isEditable()) {
         this.editable = ((JTextComponent)var3).isEditable();
         this.htmlPane.setEditable(this.editable);
      }

      super.paint(var1, var2);
   }

   private void setMargin() {
      boolean var1 = false;
      Insets var2 = this.htmlPane.getMargin();
      boolean var4 = false;
      AttributeSet var5 = this.getElement().getAttributes();
      String var6 = (String)var5.getAttribute(HTML.Attribute.MARGINWIDTH);
      Insets var3;
      if (var2 != null) {
         var3 = new Insets(var2.top, var2.left, var2.right, var2.bottom);
      } else {
         var3 = new Insets(0, 0, 0, 0);
      }

      int var7;
      if (var6 != null) {
         var7 = Integer.parseInt(var6);
         if (var7 > 0) {
            var3.left = var7;
            var3.right = var7;
            var4 = true;
         }
      }

      var6 = (String)var5.getAttribute(HTML.Attribute.MARGINHEIGHT);
      if (var6 != null) {
         var7 = Integer.parseInt(var6);
         if (var7 > 0) {
            var3.top = var7;
            var3.bottom = var7;
            var4 = true;
         }
      }

      if (var4) {
         this.htmlPane.setMargin(var3);
      }

   }

   private void setBorder() {
      AttributeSet var1 = this.getElement().getAttributes();
      String var2 = (String)var1.getAttribute(HTML.Attribute.FRAMEBORDER);
      if (var2 != null && (var2.equals("no") || var2.equals("0"))) {
         this.scroller.setBorder((Border)null);
      }

   }

   private void createScrollPane() {
      AttributeSet var1 = this.getElement().getAttributes();
      String var2 = (String)var1.getAttribute(HTML.Attribute.SCROLLING);
      if (var2 == null) {
         var2 = "auto";
      }

      if (!var2.equals("no")) {
         if (var2.equals("yes")) {
            this.scroller = new JScrollPane(22, 32);
         } else {
            this.scroller = new JScrollPane();
         }
      } else {
         this.scroller = new JScrollPane(21, 31);
      }

      JViewport var3 = this.scroller.getViewport();
      var3.add(this.htmlPane);
      var3.setBackingStoreEnabled(true);
      this.scroller.setMinimumSize(new Dimension(5, 5));
      this.scroller.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
   }

   JEditorPane getOutermostJEditorPane() {
      View var1 = this.getParent();

      FrameSetView var2;
      for(var2 = null; var1 != null; var1 = var1.getParent()) {
         if (var1 instanceof FrameSetView) {
            var2 = (FrameSetView)var1;
         }
      }

      if (var2 != null) {
         return (JEditorPane)var2.getContainer();
      } else {
         return null;
      }
   }

   private boolean inNestedFrameSet() {
      FrameSetView var1 = (FrameSetView)this.getParent();
      return var1.getParent() instanceof FrameSetView;
   }

   public void hyperlinkUpdate(HyperlinkEvent var1) {
      JEditorPane var2 = this.getOutermostJEditorPane();
      if (var2 != null) {
         if (!(var1 instanceof HTMLFrameHyperlinkEvent)) {
            var2.fireHyperlinkUpdate(var1);
         } else {
            HTMLFrameHyperlinkEvent var3 = (HTMLFrameHyperlinkEvent)var1;
            if (var3.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
               String var4 = var3.getTarget();
               String var5 = var4;
               if (var4.equals("_parent") && !this.inNestedFrameSet()) {
                  var4 = "_top";
               }

               if (var1 instanceof FormSubmitEvent) {
                  HTMLEditorKit var6 = (HTMLEditorKit)var2.getEditorKit();
                  if (var6 != null && var6.isAutoFormSubmission()) {
                     if (var4.equals("_top")) {
                        try {
                           this.movePostData(var2, var5);
                           var2.setPage(var3.getURL());
                        } catch (IOException var8) {
                        }
                     } else {
                        HTMLDocument var7 = (HTMLDocument)var2.getDocument();
                        var7.processHTMLFrameHyperlinkEvent(var3);
                     }
                  } else {
                     var2.fireHyperlinkUpdate(var1);
                  }

                  return;
               }

               if (var4.equals("_top")) {
                  try {
                     var2.setPage(var3.getURL());
                  } catch (IOException var9) {
                  }
               }

               if (!var2.isEditable()) {
                  var2.fireHyperlinkUpdate(new HTMLFrameHyperlinkEvent(var2, var3.getEventType(), var3.getURL(), var3.getDescription(), this.getElement(), var3.getInputEvent(), var4));
               }
            }

         }
      }
   }

   public void changedUpdate(DocumentEvent var1, Shape var2, ViewFactory var3) {
      Element var4 = this.getElement();
      AttributeSet var5 = var4.getAttributes();
      URL var6 = this.src;
      String var7 = (String)var5.getAttribute(HTML.Attribute.SRC);
      URL var8 = ((HTMLDocument)var4.getDocument()).getBase();

      try {
         if (!this.createdComponent) {
            return;
         }

         Object var9 = this.movePostData(this.htmlPane, (String)null);
         this.src = new URL(var8, var7);
         if (var6.equals(this.src) && this.src.getRef() == null && var9 == null) {
            return;
         }

         this.htmlPane.setPage(this.src);
         Document var10 = this.htmlPane.getDocument();
         if (var10 instanceof HTMLDocument) {
            ((HTMLDocument)var10).setFrameDocumentState(true);
         }
      } catch (MalformedURLException var11) {
      } catch (IOException var12) {
      }

   }

   private Object movePostData(JEditorPane var1, String var2) {
      Object var3 = null;
      JEditorPane var4 = this.getOutermostJEditorPane();
      if (var4 != null) {
         if (var2 == null) {
            var2 = (String)this.getElement().getAttributes().getAttribute(HTML.Attribute.NAME);
         }

         if (var2 != null) {
            String var5 = "javax.swing.JEditorPane.postdata." + var2;
            Document var6 = var4.getDocument();
            var3 = var6.getProperty(var5);
            if (var3 != null) {
               var1.getDocument().putProperty("javax.swing.JEditorPane.postdata", var3);
               var6.putProperty(var5, (Object)null);
            }
         }
      }

      return var3;
   }

   public float getMinimumSpan(int var1) {
      return 5.0F;
   }

   public float getMaximumSpan(int var1) {
      return 2.14748365E9F;
   }

   class FrameEditorPane extends JEditorPane implements FrameEditorPaneTag {
      public EditorKit getEditorKitForContentType(String var1) {
         EditorKit var2 = super.getEditorKitForContentType(var1);
         JEditorPane var3 = null;
         if ((var3 = FrameView.this.getOutermostJEditorPane()) != null) {
            EditorKit var4 = var3.getEditorKitForContentType(var1);
            if (!var2.getClass().equals(var4.getClass())) {
               var2 = (EditorKit)var4.clone();
               this.setEditorKitForContentType(var1, var2);
            }
         }

         return var2;
      }

      FrameView getFrameView() {
         return FrameView.this;
      }
   }
}
