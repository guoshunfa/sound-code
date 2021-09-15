package javax.swing.plaf.synth;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JScrollBar;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicScrollBarUI;

public class SynthScrollBarUI extends BasicScrollBarUI implements PropertyChangeListener, SynthUI {
   private SynthStyle style;
   private SynthStyle thumbStyle;
   private SynthStyle trackStyle;
   private boolean validMinimumThumbSize;

   public static ComponentUI createUI(JComponent var0) {
      return new SynthScrollBarUI();
   }

   protected void installDefaults() {
      super.installDefaults();
      this.trackHighlight = 0;
      if (this.scrollbar.getLayout() == null || this.scrollbar.getLayout() instanceof UIResource) {
         this.scrollbar.setLayout(this);
      }

      this.configureScrollBarColors();
      this.updateStyle(this.scrollbar);
   }

   protected void configureScrollBarColors() {
   }

   private void updateStyle(JScrollBar var1) {
      SynthStyle var2 = this.style;
      SynthContext var3 = this.getContext(var1, 1);
      this.style = SynthLookAndFeel.updateStyle(var3, this);
      if (this.style != var2) {
         this.scrollBarWidth = this.style.getInt(var3, "ScrollBar.thumbHeight", 14);
         this.minimumThumbSize = (Dimension)this.style.get(var3, "ScrollBar.minimumThumbSize");
         if (this.minimumThumbSize == null) {
            this.minimumThumbSize = new Dimension();
            this.validMinimumThumbSize = false;
         } else {
            this.validMinimumThumbSize = true;
         }

         this.maximumThumbSize = (Dimension)this.style.get(var3, "ScrollBar.maximumThumbSize");
         if (this.maximumThumbSize == null) {
            this.maximumThumbSize = new Dimension(4096, 4097);
         }

         this.incrGap = this.style.getInt(var3, "ScrollBar.incrementButtonGap", 0);
         this.decrGap = this.style.getInt(var3, "ScrollBar.decrementButtonGap", 0);
         String var4 = (String)this.scrollbar.getClientProperty("JComponent.sizeVariant");
         if (var4 != null) {
            if ("large".equals(var4)) {
               this.scrollBarWidth = (int)((double)this.scrollBarWidth * 1.15D);
               this.incrGap = (int)((double)this.incrGap * 1.15D);
               this.decrGap = (int)((double)this.decrGap * 1.15D);
            } else if ("small".equals(var4)) {
               this.scrollBarWidth = (int)((double)this.scrollBarWidth * 0.857D);
               this.incrGap = (int)((double)this.incrGap * 0.857D);
               this.decrGap = (int)((double)this.decrGap * 0.857D);
            } else if ("mini".equals(var4)) {
               this.scrollBarWidth = (int)((double)this.scrollBarWidth * 0.714D);
               this.incrGap = (int)((double)this.incrGap * 0.714D);
               this.decrGap = (int)((double)this.decrGap * 0.714D);
            }
         }

         if (var2 != null) {
            this.uninstallKeyboardActions();
            this.installKeyboardActions();
         }
      }

      var3.dispose();
      var3 = this.getContext(var1, Region.SCROLL_BAR_TRACK, 1);
      this.trackStyle = SynthLookAndFeel.updateStyle(var3, this);
      var3.dispose();
      var3 = this.getContext(var1, Region.SCROLL_BAR_THUMB, 1);
      this.thumbStyle = SynthLookAndFeel.updateStyle(var3, this);
      var3.dispose();
   }

   protected void installListeners() {
      super.installListeners();
      this.scrollbar.addPropertyChangeListener(this);
   }

   protected void uninstallListeners() {
      super.uninstallListeners();
      this.scrollbar.removePropertyChangeListener(this);
   }

   protected void uninstallDefaults() {
      SynthContext var1 = this.getContext(this.scrollbar, 1);
      this.style.uninstallDefaults(var1);
      var1.dispose();
      this.style = null;
      var1 = this.getContext(this.scrollbar, Region.SCROLL_BAR_TRACK, 1);
      this.trackStyle.uninstallDefaults(var1);
      var1.dispose();
      this.trackStyle = null;
      var1 = this.getContext(this.scrollbar, Region.SCROLL_BAR_THUMB, 1);
      this.thumbStyle.uninstallDefaults(var1);
      var1.dispose();
      this.thumbStyle = null;
      super.uninstallDefaults();
   }

   public SynthContext getContext(JComponent var1) {
      return this.getContext(var1, SynthLookAndFeel.getComponentState(var1));
   }

   private SynthContext getContext(JComponent var1, int var2) {
      return SynthContext.getContext(var1, this.style, var2);
   }

   private SynthContext getContext(JComponent var1, Region var2) {
      return this.getContext(var1, var2, this.getComponentState(var1, var2));
   }

   private SynthContext getContext(JComponent var1, Region var2, int var3) {
      SynthStyle var4 = this.trackStyle;
      if (var2 == Region.SCROLL_BAR_THUMB) {
         var4 = this.thumbStyle;
      }

      return SynthContext.getContext(var1, var2, var4, var3);
   }

   private int getComponentState(JComponent var1, Region var2) {
      return var2 == Region.SCROLL_BAR_THUMB && this.isThumbRollover() && var1.isEnabled() ? 2 : SynthLookAndFeel.getComponentState(var1);
   }

   public boolean getSupportsAbsolutePositioning() {
      SynthContext var1 = this.getContext(this.scrollbar);
      boolean var2 = this.style.getBoolean(var1, "ScrollBar.allowsAbsolutePositioning", false);
      var1.dispose();
      return var2;
   }

   public void update(Graphics var1, JComponent var2) {
      SynthContext var3 = this.getContext(var2);
      SynthLookAndFeel.update(var3, var1);
      var3.getPainter().paintScrollBarBackground(var3, var1, 0, 0, var2.getWidth(), var2.getHeight(), this.scrollbar.getOrientation());
      this.paint(var3, var1);
      var3.dispose();
   }

   public void paint(Graphics var1, JComponent var2) {
      SynthContext var3 = this.getContext(var2);
      this.paint(var3, var1);
      var3.dispose();
   }

   protected void paint(SynthContext var1, Graphics var2) {
      SynthContext var3 = this.getContext(this.scrollbar, Region.SCROLL_BAR_TRACK);
      this.paintTrack(var3, var2, this.getTrackBounds());
      var3.dispose();
      var3 = this.getContext(this.scrollbar, Region.SCROLL_BAR_THUMB);
      this.paintThumb(var3, var2, this.getThumbBounds());
      var3.dispose();
   }

   public void paintBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      var1.getPainter().paintScrollBarBorder(var1, var2, var3, var4, var5, var6, this.scrollbar.getOrientation());
   }

   protected void paintTrack(SynthContext var1, Graphics var2, Rectangle var3) {
      SynthLookAndFeel.updateSubregion(var1, var2, var3);
      var1.getPainter().paintScrollBarTrackBackground(var1, var2, var3.x, var3.y, var3.width, var3.height, this.scrollbar.getOrientation());
      var1.getPainter().paintScrollBarTrackBorder(var1, var2, var3.x, var3.y, var3.width, var3.height, this.scrollbar.getOrientation());
   }

   protected void paintThumb(SynthContext var1, Graphics var2, Rectangle var3) {
      SynthLookAndFeel.updateSubregion(var1, var2, var3);
      int var4 = this.scrollbar.getOrientation();
      var1.getPainter().paintScrollBarThumbBackground(var1, var2, var3.x, var3.y, var3.width, var3.height, var4);
      var1.getPainter().paintScrollBarThumbBorder(var1, var2, var3.x, var3.y, var3.width, var3.height, var4);
   }

   public Dimension getPreferredSize(JComponent var1) {
      Insets var2 = var1.getInsets();
      return this.scrollbar.getOrientation() == 1 ? new Dimension(this.scrollBarWidth + var2.left + var2.right, 48) : new Dimension(48, this.scrollBarWidth + var2.top + var2.bottom);
   }

   protected Dimension getMinimumThumbSize() {
      if (!this.validMinimumThumbSize) {
         if (this.scrollbar.getOrientation() == 1) {
            this.minimumThumbSize.width = this.scrollBarWidth;
            this.minimumThumbSize.height = 7;
         } else {
            this.minimumThumbSize.width = 7;
            this.minimumThumbSize.height = this.scrollBarWidth;
         }
      }

      return this.minimumThumbSize;
   }

   protected JButton createDecreaseButton(int var1) {
      SynthArrowButton var2 = new SynthArrowButton(var1) {
         public boolean contains(int var1, int var2) {
            if (SynthScrollBarUI.this.decrGap < 0) {
               int var3 = this.getWidth();
               int var4 = this.getHeight();
               if (SynthScrollBarUI.this.scrollbar.getOrientation() == 1) {
                  var4 += SynthScrollBarUI.this.decrGap;
               } else {
                  var3 += SynthScrollBarUI.this.decrGap;
               }

               return var1 >= 0 && var1 < var3 && var2 >= 0 && var2 < var4;
            } else {
               return super.contains(var1, var2);
            }
         }
      };
      var2.setName("ScrollBar.button");
      return var2;
   }

   protected JButton createIncreaseButton(int var1) {
      SynthArrowButton var2 = new SynthArrowButton(var1) {
         public boolean contains(int var1, int var2) {
            if (SynthScrollBarUI.this.incrGap < 0) {
               int var3 = this.getWidth();
               int var4 = this.getHeight();
               if (SynthScrollBarUI.this.scrollbar.getOrientation() == 1) {
                  var4 += SynthScrollBarUI.this.incrGap;
                  var2 += SynthScrollBarUI.this.incrGap;
               } else {
                  var3 += SynthScrollBarUI.this.incrGap;
                  var1 += SynthScrollBarUI.this.incrGap;
               }

               return var1 >= 0 && var1 < var3 && var2 >= 0 && var2 < var4;
            } else {
               return super.contains(var1, var2);
            }
         }
      };
      var2.setName("ScrollBar.button");
      return var2;
   }

   protected void setThumbRollover(boolean var1) {
      if (this.isThumbRollover() != var1) {
         this.scrollbar.repaint(this.getThumbBounds());
         super.setThumbRollover(var1);
      }

   }

   private void updateButtonDirections() {
      int var1 = this.scrollbar.getOrientation();
      if (this.scrollbar.getComponentOrientation().isLeftToRight()) {
         ((SynthArrowButton)this.incrButton).setDirection(var1 == 0 ? 3 : 5);
         ((SynthArrowButton)this.decrButton).setDirection(var1 == 0 ? 7 : 1);
      } else {
         ((SynthArrowButton)this.incrButton).setDirection(var1 == 0 ? 7 : 5);
         ((SynthArrowButton)this.decrButton).setDirection(var1 == 0 ? 3 : 1);
      }

   }

   public void propertyChange(PropertyChangeEvent var1) {
      String var2 = var1.getPropertyName();
      if (SynthLookAndFeel.shouldUpdateStyle(var1)) {
         this.updateStyle((JScrollBar)var1.getSource());
      }

      if ("orientation" == var2) {
         this.updateButtonDirections();
      } else if ("componentOrientation" == var2) {
         this.updateButtonDirections();
      }

   }
}
