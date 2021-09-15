package com.apple.laf;

import apple.laf.JRSUIConstants;
import apple.laf.JRSUIState;
import apple.laf.JRSUIStateFactory;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.BoundedRangeModel;
import javax.swing.JComponent;
import javax.swing.JProgressBar;
import javax.swing.LookAndFeel;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.ProgressBarUI;
import sun.swing.SwingUtilities2;

public class AquaProgressBarUI extends ProgressBarUI implements ChangeListener, PropertyChangeListener, AncestorListener, AquaUtilControlSize.Sizeable {
   private static final boolean ADJUSTTIMER = true;
   protected static final AquaUtils.RecyclableSingleton<AquaUtilControlSize.SizeDescriptor> sizeDescriptor = new AquaUtils.RecyclableSingleton<AquaUtilControlSize.SizeDescriptor>() {
      protected AquaUtilControlSize.SizeDescriptor getInstance() {
         return new AquaUtilControlSize.SizeDescriptor(new AquaUtilControlSize.SizeVariant(146, 20)) {
            public AquaUtilControlSize.SizeVariant deriveSmall(AquaUtilControlSize.SizeVariant var1) {
               var1.alterMinSize(0, -6);
               return super.deriveSmall(var1);
            }
         };
      }
   };
   protected JRSUIConstants.Size sizeVariant;
   protected Color selectionForeground;
   private AquaProgressBarUI.Animator animator;
   protected boolean isAnimating;
   protected boolean isCircular;
   protected final AquaPainter<JRSUIState.ValueState> painter;
   protected JProgressBar progressBar;
   private final Rectangle fUpdateArea;
   private final Dimension fLastSize;

   static AquaUtilControlSize.SizeDescriptor getSizeDescriptor() {
      return (AquaUtilControlSize.SizeDescriptor)sizeDescriptor.get();
   }

   public static ComponentUI createUI(JComponent var0) {
      return new AquaProgressBarUI();
   }

   protected AquaProgressBarUI() {
      this.sizeVariant = JRSUIConstants.Size.REGULAR;
      this.painter = AquaPainter.create(JRSUIStateFactory.getProgressBar());
      this.fUpdateArea = new Rectangle(0, 0, 0, 0);
      this.fLastSize = new Dimension(0, 0);
   }

   public void installUI(JComponent var1) {
      this.progressBar = (JProgressBar)var1;
      this.installDefaults();
      this.installListeners();
   }

   public void uninstallUI(JComponent var1) {
      this.uninstallDefaults();
      this.uninstallListeners();
      this.stopAnimationTimer();
      this.progressBar = null;
   }

   protected void installDefaults() {
      this.progressBar.setOpaque(false);
      LookAndFeel.installBorder(this.progressBar, "ProgressBar.border");
      LookAndFeel.installColorsAndFont(this.progressBar, "ProgressBar.background", "ProgressBar.foreground", "ProgressBar.font");
      this.selectionForeground = UIManager.getColor("ProgressBar.selectionForeground");
   }

   protected void uninstallDefaults() {
      LookAndFeel.uninstallBorder(this.progressBar);
   }

   protected void installListeners() {
      this.progressBar.addChangeListener(this);
      this.progressBar.addPropertyChangeListener(this);
      this.progressBar.addAncestorListener(this);
      AquaUtilControlSize.addSizePropertyListener(this.progressBar);
   }

   protected void uninstallListeners() {
      AquaUtilControlSize.removeSizePropertyListener(this.progressBar);
      this.progressBar.removeAncestorListener(this);
      this.progressBar.removePropertyChangeListener(this);
      this.progressBar.removeChangeListener(this);
   }

   public void stateChanged(ChangeEvent var1) {
      this.progressBar.repaint();
   }

   public void propertyChange(PropertyChangeEvent var1) {
      String var2 = var1.getPropertyName();
      if ("indeterminate".equals(var2)) {
         if (!this.progressBar.isIndeterminate()) {
            return;
         }

         this.stopAnimationTimer();
         if (this.progressBar.isDisplayable()) {
            this.startAnimationTimer();
         }
      }

      if ("JProgressBar.style".equals(var2)) {
         this.isCircular = "circular".equalsIgnoreCase(var1.getNewValue() + "");
         this.progressBar.repaint();
      }

   }

   public void ancestorRemoved(AncestorEvent var1) {
      this.stopAnimationTimer();
   }

   public void ancestorAdded(AncestorEvent var1) {
      if (this.progressBar.isIndeterminate()) {
         if (this.progressBar.isDisplayable()) {
            this.startAnimationTimer();
         }

      }
   }

   public void ancestorMoved(AncestorEvent var1) {
   }

   public void paint(Graphics var1, JComponent var2) {
      this.revalidateAnimationTimers();
      ((JRSUIState.ValueState)this.painter.state).set(this.getState(var2));
      ((JRSUIState.ValueState)this.painter.state).set(this.isHorizontal() ? JRSUIConstants.Orientation.HORIZONTAL : JRSUIConstants.Orientation.VERTICAL);
      ((JRSUIState.ValueState)this.painter.state).set(this.isAnimating ? JRSUIConstants.Animating.YES : JRSUIConstants.Animating.NO);
      if (this.progressBar.isIndeterminate()) {
         if (this.isCircular) {
            ((JRSUIState.ValueState)this.painter.state).set(JRSUIConstants.Widget.PROGRESS_SPINNER);
            this.painter.paint(var1, var2, 2, 2, 16, 16);
         } else {
            ((JRSUIState.ValueState)this.painter.state).set(JRSUIConstants.Widget.PROGRESS_INDETERMINATE_BAR);
            this.paint(var1);
         }
      } else {
         ((JRSUIState.ValueState)this.painter.state).set(JRSUIConstants.Widget.PROGRESS_BAR);
         ((JRSUIState.ValueState)this.painter.state).setValue(checkValue(this.progressBar.getPercentComplete()));
         this.paint(var1);
      }
   }

   static double checkValue(double var0) {
      return Double.isNaN(var0) ? 0.0D : var0;
   }

   protected void paint(Graphics var1) {
      Insets var2 = this.progressBar.getInsets();
      int var3 = this.progressBar.getWidth() - (var2.right + var2.left);
      int var4 = this.progressBar.getHeight() - (var2.bottom + var2.top);
      this.painter.paint(var1, this.progressBar, var2.left, var2.top, var3, var4);
      if (this.progressBar.isStringPainted() && !this.progressBar.isIndeterminate()) {
         this.paintString(var1, var2.left, var2.top, var3, var4);
      }

   }

   protected JRSUIConstants.State getState(JComponent var1) {
      if (!var1.isEnabled()) {
         return JRSUIConstants.State.INACTIVE;
      } else {
         return !AquaFocusHandler.isActive(var1) ? JRSUIConstants.State.INACTIVE : JRSUIConstants.State.ACTIVE;
      }
   }

   protected void paintString(Graphics var1, int var2, int var3, int var4, int var5) {
      if (var1 instanceof Graphics2D) {
         Graphics2D var6 = (Graphics2D)var1;
         String var7 = this.progressBar.getString();
         var6.setFont(this.progressBar.getFont());
         Point var8 = this.getStringPlacement(var6, var7, var2, var3, var4, var5);
         Rectangle var9 = var6.getClipBounds();
         if (this.isHorizontal()) {
            var6.setColor(this.selectionForeground);
            SwingUtilities2.drawString(this.progressBar, var6, (String)var7, var8.x, var8.y);
         } else {
            AffineTransform var10 = var6.getTransform();
            var6.transform(AffineTransform.getRotateInstance(-1.5707963267948966D, 0.0D, 0.0D));
            var6.translate(-this.progressBar.getHeight(), 0);
            var6.setColor(this.selectionForeground);
            SwingUtilities2.drawString(this.progressBar, var6, (String)var7, var8.x, var8.y);
            var6.setTransform(var10);
         }

         var6.setClip(var9);
      }
   }

   protected Point getStringPlacement(Graphics var1, String var2, int var3, int var4, int var5, int var6) {
      FontMetrics var7 = this.progressBar.getFontMetrics(this.progressBar.getFont());
      int var8 = var7.stringWidth(var2);
      if (!this.isHorizontal()) {
         int var9 = var6;
         var6 = var5;
         var5 = var9;
         int var10 = var3;
         var3 = var4;
         var4 = var10;
      }

      return new Point(var3 + Math.round((float)(var5 / 2 - var8 / 2)), var4 + (var6 + var7.getAscent() - var7.getLeading() - var7.getDescent()) / 2 - 1);
   }

   static Dimension getCircularPreferredSize() {
      return new Dimension(20, 20);
   }

   public Dimension getPreferredSize(JComponent var1) {
      if (this.isCircular) {
         return getCircularPreferredSize();
      } else {
         FontMetrics var2 = this.progressBar.getFontMetrics(this.progressBar.getFont());
         Dimension var3 = this.isHorizontal() ? this.getPreferredHorizontalSize(var2) : this.getPreferredVerticalSize(var2);
         Insets var4 = this.progressBar.getInsets();
         var3.width += var4.left + var4.right;
         var3.height += var4.top + var4.bottom;
         return var3;
      }
   }

   protected Dimension getPreferredHorizontalSize(FontMetrics var1) {
      AquaUtilControlSize.SizeVariant var2 = getSizeDescriptor().get(this.sizeVariant);
      Dimension var3 = new Dimension(var2.w, var2.h);
      if (!this.progressBar.isStringPainted()) {
         return var3;
      } else {
         String var4 = this.progressBar.getString();
         int var5 = var1.stringWidth(var4);
         if (var5 > var3.width) {
            var3.width = var5;
         }

         int var6 = var1.getHeight() + var1.getDescent();
         if (var6 > var3.height) {
            var3.height = var6;
         }

         return var3;
      }
   }

   protected Dimension getPreferredVerticalSize(FontMetrics var1) {
      AquaUtilControlSize.SizeVariant var2 = getSizeDescriptor().get(this.sizeVariant);
      Dimension var3 = new Dimension(var2.h, var2.w);
      if (!this.progressBar.isStringPainted()) {
         return var3;
      } else {
         String var4 = this.progressBar.getString();
         int var5 = var1.getHeight() + var1.getDescent();
         if (var5 > var3.width) {
            var3.width = var5;
         }

         int var6 = var1.stringWidth(var4);
         if (var6 > var3.height) {
            var3.height = var6;
         }

         return var3;
      }
   }

   public Dimension getMinimumSize(JComponent var1) {
      if (this.isCircular) {
         return getCircularPreferredSize();
      } else {
         Dimension var2 = this.getPreferredSize(this.progressBar);
         if (this.isHorizontal()) {
            var2.width = 10;
         } else {
            var2.height = 10;
         }

         return var2;
      }
   }

   public Dimension getMaximumSize(JComponent var1) {
      if (this.isCircular) {
         return getCircularPreferredSize();
      } else {
         Dimension var2 = this.getPreferredSize(this.progressBar);
         if (this.isHorizontal()) {
            var2.width = 32767;
         } else {
            var2.height = 32767;
         }

         return var2;
      }
   }

   public void applySizeFor(JComponent var1, JRSUIConstants.Size var2) {
      ((JRSUIState.ValueState)this.painter.state).set(this.sizeVariant = var2 == JRSUIConstants.Size.MINI ? JRSUIConstants.Size.SMALL : this.sizeVariant);
   }

   protected void startAnimationTimer() {
      if (this.animator == null) {
         this.animator = new AquaProgressBarUI.Animator();
      }

      this.animator.start();
      this.isAnimating = true;
   }

   protected void stopAnimationTimer() {
      if (this.animator != null) {
         this.animator.stop();
      }

      this.isAnimating = false;
   }

   protected Rectangle getRepaintRect() {
      int var1 = this.progressBar.getHeight();
      int var2 = this.progressBar.getWidth();
      if (this.isCircular) {
         return new Rectangle(20, 20);
      } else if (this.fLastSize.height == var1 && this.fLastSize.width == var2) {
         return this.fUpdateArea;
      } else {
         int var3 = 0;
         int var4 = 0;
         this.fLastSize.height = var1;
         this.fLastSize.width = var2;
         int var5 = this.getMaxProgressBarHeight();
         int var6;
         if (this.isHorizontal()) {
            var6 = var1 - var5;
            var4 += var6 / 2;
            var1 = var5;
         } else {
            var6 = var2 - var5;
            var3 += var6 / 2;
            var2 = var5;
         }

         this.fUpdateArea.setBounds(var3, var4, var2, var1);
         return this.fUpdateArea;
      }
   }

   protected int getMaxProgressBarHeight() {
      return getSizeDescriptor().get(this.sizeVariant).h;
   }

   protected boolean isHorizontal() {
      return this.progressBar.getOrientation() == 0;
   }

   protected void revalidateAnimationTimers() {
      if (!this.progressBar.isIndeterminate()) {
         if (!this.isAnimating) {
            this.startAnimationTimer();
         } else {
            BoundedRangeModel var1 = this.progressBar.getModel();
            double var2 = (double)var1.getValue();
            if (var2 == (double)var1.getMaximum() || var2 == (double)var1.getMinimum()) {
               this.stopAnimationTimer();
            }

         }
      }
   }

   protected void repaint() {
      Rectangle var1 = this.getRepaintRect();
      if (var1 == null) {
         this.progressBar.repaint();
      } else {
         this.progressBar.repaint(var1);
      }
   }

   protected class Animator implements ActionListener {
      private static final int MINIMUM_DELAY = 5;
      private Timer timer;
      private long previousDelay;
      private long lastCall;
      private int repaintInterval = UIManager.getInt("ProgressBar.repaintInterval");

      public Animator() {
         if (this.repaintInterval <= 0) {
            this.repaintInterval = 100;
         }

      }

      protected void start() {
         this.previousDelay = (long)this.repaintInterval;
         this.lastCall = 0L;
         if (this.timer == null) {
            this.timer = new Timer(this.repaintInterval, this);
         } else {
            this.timer.setDelay(this.repaintInterval);
         }

         this.timer.setRepeats(false);
         this.timer.setCoalesce(false);
         this.timer.start();
      }

      protected void stop() {
         this.timer.stop();
      }

      public void actionPerformed(ActionEvent var1) {
         long var2 = System.currentTimeMillis();
         if (this.lastCall > 0L) {
            int var4 = (int)(this.previousDelay - var2 + this.lastCall + (long)this.repaintInterval);
            if (var4 < 5) {
               var4 = 5;
            }

            this.timer.setInitialDelay(var4);
            this.previousDelay = (long)var4;
         }

         this.timer.start();
         this.lastCall = var2;
         AquaProgressBarUI.this.repaint();
      }
   }
}
