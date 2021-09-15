package javax.swing.text.html;

import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Toolkit;
import java.awt.image.ImageObserver;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Dictionary;
import javax.swing.GrayFilter;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.GlyphView;
import javax.swing.text.Highlighter;
import javax.swing.text.JTextComponent;
import javax.swing.text.LayeredHighlighter;
import javax.swing.text.Position;
import javax.swing.text.Segment;
import javax.swing.text.StyledDocument;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;

public class ImageView extends View {
   private static boolean sIsInc = false;
   private static int sIncRate = 100;
   private static final String PENDING_IMAGE = "html.pendingImage";
   private static final String MISSING_IMAGE = "html.missingImage";
   private static final String IMAGE_CACHE_PROPERTY = "imageCache";
   private static final int DEFAULT_WIDTH = 38;
   private static final int DEFAULT_HEIGHT = 38;
   private static final int DEFAULT_BORDER = 2;
   private static final int LOADING_FLAG = 1;
   private static final int LINK_FLAG = 2;
   private static final int WIDTH_FLAG = 4;
   private static final int HEIGHT_FLAG = 8;
   private static final int RELOAD_FLAG = 16;
   private static final int RELOAD_IMAGE_FLAG = 32;
   private static final int SYNC_LOAD_FLAG = 64;
   private AttributeSet attr;
   private Image image;
   private Image disabledImage;
   private int width;
   private int height;
   private int state = 48;
   private Container container;
   private Rectangle fBounds = new Rectangle();
   private Color borderColor;
   private short borderSize;
   private short leftInset;
   private short rightInset;
   private short topInset;
   private short bottomInset;
   private ImageObserver imageObserver = new ImageView.ImageHandler();
   private View altView;
   private float vAlign;

   public ImageView(Element var1) {
      super(var1);
   }

   public String getAltText() {
      return (String)this.getElement().getAttributes().getAttribute(HTML.Attribute.ALT);
   }

   public URL getImageURL() {
      String var1 = (String)this.getElement().getAttributes().getAttribute(HTML.Attribute.SRC);
      if (var1 == null) {
         return null;
      } else {
         URL var2 = ((HTMLDocument)this.getDocument()).getBase();

         try {
            URL var3 = new URL(var2, var1);
            return var3;
         } catch (MalformedURLException var4) {
            return null;
         }
      }
   }

   public Icon getNoImageIcon() {
      return (Icon)UIManager.getLookAndFeelDefaults().get("html.missingImage");
   }

   public Icon getLoadingImageIcon() {
      return (Icon)UIManager.getLookAndFeelDefaults().get("html.pendingImage");
   }

   public Image getImage() {
      this.sync();
      return this.image;
   }

   private Image getImage(boolean var1) {
      Image var2 = this.getImage();
      if (!var1) {
         if (this.disabledImage == null) {
            this.disabledImage = GrayFilter.createDisabledImage(var2);
         }

         var2 = this.disabledImage;
      }

      return var2;
   }

   public void setLoadsSynchronously(boolean var1) {
      synchronized(this) {
         if (var1) {
            this.state |= 64;
         } else {
            this.state = (this.state | 64) ^ 64;
         }

      }
   }

   public boolean getLoadsSynchronously() {
      return (this.state & 64) != 0;
   }

   protected StyleSheet getStyleSheet() {
      HTMLDocument var1 = (HTMLDocument)this.getDocument();
      return var1.getStyleSheet();
   }

   public AttributeSet getAttributes() {
      this.sync();
      return this.attr;
   }

   public String getToolTipText(float var1, float var2, Shape var3) {
      return this.getAltText();
   }

   protected void setPropertiesFromAttributes() {
      StyleSheet var1 = this.getStyleSheet();
      this.attr = var1.getViewAttributes(this);
      this.borderSize = (short)this.getIntAttr(HTML.Attribute.BORDER, this.isLink() ? 2 : 0);
      this.leftInset = this.rightInset = (short)(this.getIntAttr(HTML.Attribute.HSPACE, 0) + this.borderSize);
      this.topInset = this.bottomInset = (short)(this.getIntAttr(HTML.Attribute.VSPACE, 0) + this.borderSize);
      this.borderColor = ((StyledDocument)this.getDocument()).getForeground(this.getAttributes());
      AttributeSet var2 = this.getElement().getAttributes();
      Object var3 = var2.getAttribute(HTML.Attribute.ALIGN);
      this.vAlign = 1.0F;
      if (var3 != null) {
         String var10 = var3.toString();
         if ("top".equals(var10)) {
            this.vAlign = 0.0F;
         } else if ("middle".equals(var10)) {
            this.vAlign = 0.5F;
         }
      }

      AttributeSet var4 = (AttributeSet)var2.getAttribute(HTML.Tag.A);
      if (var4 != null && var4.isDefined(HTML.Attribute.HREF)) {
         synchronized(this) {
            this.state |= 2;
         }
      } else {
         synchronized(this) {
            this.state = (this.state | 2) ^ 2;
         }
      }

   }

   public void setParent(View var1) {
      View var2 = this.getParent();
      super.setParent(var1);
      this.container = var1 != null ? this.getContainer() : null;
      if (var2 != var1) {
         synchronized(this) {
            this.state |= 16;
         }
      }

   }

   public void changedUpdate(DocumentEvent var1, Shape var2, ViewFactory var3) {
      super.changedUpdate(var1, var2, var3);
      synchronized(this) {
         this.state |= 48;
      }

      this.preferenceChanged((View)null, true, true);
   }

   public void paint(Graphics var1, Shape var2) {
      this.sync();
      Rectangle var3 = var2 instanceof Rectangle ? (Rectangle)var2 : var2.getBounds();
      Rectangle var4 = var1.getClipBounds();
      this.fBounds.setBounds(var3);
      this.paintHighlights(var1, var2);
      this.paintBorder(var1, var3);
      if (var4 != null) {
         var1.clipRect(var3.x + this.leftInset, var3.y + this.topInset, var3.width - this.leftInset - this.rightInset, var3.height - this.topInset - this.bottomInset);
      }

      Container var5 = this.getContainer();
      Image var6 = this.getImage(var5 == null || var5.isEnabled());
      Icon var7;
      if (var6 != null) {
         if (!this.hasPixels(var6)) {
            var7 = this.getLoadingImageIcon();
            if (var7 != null) {
               var7.paintIcon(var5, var1, var3.x + this.leftInset, var3.y + this.topInset);
            }
         } else {
            var1.drawImage(var6, var3.x + this.leftInset, var3.y + this.topInset, this.width, this.height, this.imageObserver);
         }
      } else {
         var7 = this.getNoImageIcon();
         if (var7 != null) {
            var7.paintIcon(var5, var1, var3.x + this.leftInset, var3.y + this.topInset);
         }

         View var8 = this.getAltView();
         if (var8 != null && ((this.state & 4) == 0 || this.width > 38)) {
            Rectangle var9 = new Rectangle(var3.x + this.leftInset + 38, var3.y + this.topInset, var3.width - this.leftInset - this.rightInset - 38, var3.height - this.topInset - this.bottomInset);
            var8.paint(var1, var9);
         }
      }

      if (var4 != null) {
         var1.setClip(var4.x, var4.y, var4.width, var4.height);
      }

   }

   private void paintHighlights(Graphics var1, Shape var2) {
      if (this.container instanceof JTextComponent) {
         JTextComponent var3 = (JTextComponent)this.container;
         Highlighter var4 = var3.getHighlighter();
         if (var4 instanceof LayeredHighlighter) {
            ((LayeredHighlighter)var4).paintLayeredHighlights(var1, this.getStartOffset(), this.getEndOffset(), var2, var3, this);
         }
      }

   }

   private void paintBorder(Graphics var1, Rectangle var2) {
      Color var3 = this.borderColor;
      if ((this.borderSize > 0 || this.image == null) && var3 != null) {
         int var4 = this.leftInset - this.borderSize;
         int var5 = this.topInset - this.borderSize;
         var1.setColor(var3);
         short var6 = this.image == null ? 1 : this.borderSize;

         for(int var7 = 0; var7 < var6; ++var7) {
            var1.drawRect(var2.x + var4 + var7, var2.y + var5 + var7, var2.width - var7 - var7 - var4 - var4 - 1, var2.height - var7 - var7 - var5 - var5 - 1);
         }
      }

   }

   public float getPreferredSpan(int var1) {
      this.sync();
      if (var1 == 0 && (this.state & 4) == 4) {
         this.getPreferredSpanFromAltView(var1);
         return (float)(this.width + this.leftInset + this.rightInset);
      } else if (var1 == 1 && (this.state & 8) == 8) {
         this.getPreferredSpanFromAltView(var1);
         return (float)(this.height + this.topInset + this.bottomInset);
      } else {
         Image var2 = this.getImage();
         if (var2 != null) {
            switch(var1) {
            case 0:
               return (float)(this.width + this.leftInset + this.rightInset);
            case 1:
               return (float)(this.height + this.topInset + this.bottomInset);
            default:
               throw new IllegalArgumentException("Invalid axis: " + var1);
            }
         } else {
            View var3 = this.getAltView();
            float var4 = 0.0F;
            if (var3 != null) {
               var4 = var3.getPreferredSpan(var1);
            }

            switch(var1) {
            case 0:
               return var4 + (float)(this.width + this.leftInset + this.rightInset);
            case 1:
               return var4 + (float)(this.height + this.topInset + this.bottomInset);
            default:
               throw new IllegalArgumentException("Invalid axis: " + var1);
            }
         }
      }
   }

   public float getAlignment(int var1) {
      switch(var1) {
      case 1:
         return this.vAlign;
      default:
         return super.getAlignment(var1);
      }
   }

   public Shape modelToView(int var1, Shape var2, Position.Bias var3) throws BadLocationException {
      int var4 = this.getStartOffset();
      int var5 = this.getEndOffset();
      if (var1 >= var4 && var1 <= var5) {
         Rectangle var6 = var2.getBounds();
         if (var1 == var5) {
            var6.x += var6.width;
         }

         var6.width = 0;
         return var6;
      } else {
         return null;
      }
   }

   public int viewToModel(float var1, float var2, Shape var3, Position.Bias[] var4) {
      Rectangle var5 = (Rectangle)var3;
      if (var1 < (float)(var5.x + var5.width)) {
         var4[0] = Position.Bias.Forward;
         return this.getStartOffset();
      } else {
         var4[0] = Position.Bias.Backward;
         return this.getEndOffset();
      }
   }

   public void setSize(float var1, float var2) {
      this.sync();
      if (this.getImage() == null) {
         View var3 = this.getAltView();
         if (var3 != null) {
            var3.setSize(Math.max(0.0F, var1 - (float)(38 + this.leftInset + this.rightInset)), Math.max(0.0F, var2 - (float)(this.topInset + this.bottomInset)));
         }
      }

   }

   private boolean isLink() {
      return (this.state & 2) == 2;
   }

   private boolean hasPixels(Image var1) {
      return var1 != null && var1.getHeight(this.imageObserver) > 0 && var1.getWidth(this.imageObserver) > 0;
   }

   private float getPreferredSpanFromAltView(int var1) {
      if (this.getImage() == null) {
         View var2 = this.getAltView();
         if (var2 != null) {
            return var2.getPreferredSpan(var1);
         }
      }

      return 0.0F;
   }

   private void repaint(long var1) {
      if (this.container != null && this.fBounds != null) {
         this.container.repaint(var1, this.fBounds.x, this.fBounds.y, this.fBounds.width, this.fBounds.height);
      }

   }

   private int getIntAttr(HTML.Attribute var1, int var2) {
      AttributeSet var3 = this.getElement().getAttributes();
      if (var3.isDefined(var1)) {
         String var5 = (String)var3.getAttribute(var1);
         int var4;
         if (var5 == null) {
            var4 = var2;
         } else {
            try {
               var4 = Math.max(0, Integer.parseInt(var5));
            } catch (NumberFormatException var7) {
               var4 = var2;
            }
         }

         return var4;
      } else {
         return var2;
      }
   }

   private void sync() {
      int var1 = this.state;
      if ((var1 & 32) != 0) {
         this.refreshImage();
      }

      var1 = this.state;
      if ((var1 & 16) != 0) {
         synchronized(this) {
            this.state = (this.state | 16) ^ 16;
         }

         this.setPropertiesFromAttributes();
      }

   }

   private void refreshImage() {
      synchronized(this) {
         this.state = (this.state | 1 | 32 | 4 | 8) ^ 44;
         this.image = null;
         this.width = this.height = 0;
      }

      boolean var11 = false;

      try {
         var11 = true;
         this.loadImage();
         this.updateImageSize();
         var11 = false;
      } finally {
         if (var11) {
            synchronized(this) {
               this.state = (this.state | 1) ^ 1;
            }
         }
      }

      synchronized(this) {
         this.state = (this.state | 1) ^ 1;
      }
   }

   private void loadImage() {
      URL var1 = this.getImageURL();
      Image var2 = null;
      if (var1 != null) {
         Dictionary var3 = (Dictionary)this.getDocument().getProperty("imageCache");
         if (var3 != null) {
            var2 = (Image)var3.get(var1);
         } else {
            var2 = Toolkit.getDefaultToolkit().createImage(var1);
            if (var2 != null && this.getLoadsSynchronously()) {
               ImageIcon var4 = new ImageIcon();
               var4.setImage(var2);
            }
         }
      }

      this.image = var2;
   }

   private void updateImageSize() {
      boolean var1 = false;
      boolean var2 = false;
      int var3 = 0;
      Image var4 = this.getImage();
      if (var4 != null) {
         Element var5 = this.getElement();
         AttributeSet var6 = var5.getAttributes();
         int var11 = this.getIntAttr(HTML.Attribute.WIDTH, -1);
         if (var11 > 0) {
            var3 |= 4;
         }

         int var12 = this.getIntAttr(HTML.Attribute.HEIGHT, -1);
         if (var12 > 0) {
            var3 |= 8;
         }

         if (var11 <= 0) {
            var11 = var4.getWidth(this.imageObserver);
            if (var11 <= 0) {
               var11 = 38;
            }
         }

         if (var12 <= 0) {
            var12 = var4.getHeight(this.imageObserver);
            if (var12 <= 0) {
               var12 = 38;
            }
         }

         if ((var3 & 12) != 0) {
            Toolkit.getDefaultToolkit().prepareImage(var4, var11, var12, this.imageObserver);
         } else {
            Toolkit.getDefaultToolkit().prepareImage(var4, -1, -1, this.imageObserver);
         }

         boolean var7 = false;
         synchronized(this) {
            if (this.image == null) {
               var7 = true;
               if ((var3 & 4) == 4) {
                  this.width = var11;
               }

               if ((var3 & 8) == 8) {
                  this.height = var12;
               }
            } else {
               if ((var3 & 4) == 4 || this.width == 0) {
                  this.width = var11;
               }

               if ((var3 & 8) == 8 || this.height == 0) {
                  this.height = var12;
               }
            }

            this.state |= var3;
            this.state = (this.state | 1) ^ 1;
         }

         if (var7) {
            this.updateAltTextView();
         }
      } else {
         this.width = this.height = 38;
         this.updateAltTextView();
      }

   }

   private void updateAltTextView() {
      String var1 = this.getAltText();
      if (var1 != null) {
         ImageView.ImageLabelView var2 = new ImageView.ImageLabelView(this.getElement(), var1);
         synchronized(this) {
            this.altView = var2;
         }
      }

   }

   private View getAltView() {
      View var1;
      synchronized(this) {
         var1 = this.altView;
      }

      if (var1 != null && var1.getParent() == null) {
         var1.setParent(this.getParent());
      }

      return var1;
   }

   private void safePreferenceChanged() {
      if (SwingUtilities.isEventDispatchThread()) {
         Document var1 = this.getDocument();
         if (var1 instanceof AbstractDocument) {
            ((AbstractDocument)var1).readLock();
         }

         this.preferenceChanged((View)null, true, true);
         if (var1 instanceof AbstractDocument) {
            ((AbstractDocument)var1).readUnlock();
         }
      } else {
         SwingUtilities.invokeLater(new Runnable() {
            public void run() {
               ImageView.this.safePreferenceChanged();
            }
         });
      }

   }

   private class ImageLabelView extends InlineView {
      private Segment segment;
      private Color fg;

      ImageLabelView(Element var2, String var3) {
         super(var2);
         this.reset(var3);
      }

      public void reset(String var1) {
         this.segment = new Segment(var1.toCharArray(), 0, var1.length());
      }

      public void paint(Graphics var1, Shape var2) {
         GlyphView.GlyphPainter var3 = this.getGlyphPainter();
         if (var3 != null) {
            var1.setColor(this.getForeground());
            var3.paint(this, var1, var2, this.getStartOffset(), this.getEndOffset());
         }

      }

      public Segment getText(int var1, int var2) {
         if (var1 >= 0 && var2 <= this.segment.array.length) {
            this.segment.offset = var1;
            this.segment.count = var2 - var1;
            return this.segment;
         } else {
            throw new RuntimeException("ImageLabelView: Stale view");
         }
      }

      public int getStartOffset() {
         return 0;
      }

      public int getEndOffset() {
         return this.segment.array.length;
      }

      public View breakView(int var1, int var2, float var3, float var4) {
         return this;
      }

      public Color getForeground() {
         View var1;
         if (this.fg == null && (var1 = this.getParent()) != null) {
            Document var2 = this.getDocument();
            AttributeSet var3 = var1.getAttributes();
            if (var3 != null && var2 instanceof StyledDocument) {
               this.fg = ((StyledDocument)var2).getForeground(var3);
            }
         }

         return this.fg;
      }
   }

   private class ImageHandler implements ImageObserver {
      private ImageHandler() {
      }

      public boolean imageUpdate(Image var1, int var2, int var3, int var4, int var5, int var6) {
         if ((var1 == ImageView.this.image || var1 == ImageView.this.disabledImage) && ImageView.this.image != null && ImageView.this.getParent() != null) {
            if ((var2 & 192) != 0) {
               ImageView.this.repaint(0L);
               synchronized(ImageView.this) {
                  if (ImageView.this.image == var1) {
                     ImageView.this.image = null;
                     if ((ImageView.this.state & 4) != 4) {
                        ImageView.this.width = 38;
                     }

                     if ((ImageView.this.state & 8) != 8) {
                        ImageView.this.height = 38;
                     }
                  } else {
                     ImageView.this.disabledImage = null;
                  }

                  if ((ImageView.this.state & 1) == 1) {
                     return false;
                  }
               }

               ImageView.this.updateAltTextView();
               ImageView.this.safePreferenceChanged();
               return false;
            } else {
               if (ImageView.this.image == var1) {
                  short var7 = 0;
                  if ((var2 & 2) != 0 && !ImageView.this.getElement().getAttributes().isDefined(HTML.Attribute.HEIGHT)) {
                     var7 = (short)(var7 | 1);
                  }

                  if ((var2 & 1) != 0 && !ImageView.this.getElement().getAttributes().isDefined(HTML.Attribute.WIDTH)) {
                     var7 = (short)(var7 | 2);
                  }

                  synchronized(ImageView.this) {
                     if ((var7 & 1) == 1 && (ImageView.this.state & 4) == 0) {
                        ImageView.this.width = var5;
                     }

                     if ((var7 & 2) == 2 && (ImageView.this.state & 8) == 0) {
                        ImageView.this.height = var6;
                     }

                     if ((ImageView.this.state & 1) == 1) {
                        return true;
                     }
                  }

                  if (var7 != 0) {
                     ImageView.this.safePreferenceChanged();
                     return true;
                  }
               }

               if ((var2 & 48) != 0) {
                  ImageView.this.repaint(0L);
               } else if ((var2 & 8) != 0 && ImageView.sIsInc) {
                  ImageView.this.repaint((long)ImageView.sIncRate);
               }

               return (var2 & 32) == 0;
            }
         } else {
            return false;
         }
      }

      // $FF: synthetic method
      ImageHandler(Object var2) {
         this();
      }
   }
}
