package javax.swing.plaf.basic;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.awt.geom.AffineTransform;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.BoundedRangeModel;
import javax.swing.JComponent;
import javax.swing.JProgressBar;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.ProgressBarUI;
import sun.swing.DefaultLookup;
import sun.swing.SwingUtilities2;

public class BasicProgressBarUI extends ProgressBarUI {
   private int cachedPercent;
   private int cellLength;
   private int cellSpacing;
   private Color selectionForeground;
   private Color selectionBackground;
   private BasicProgressBarUI.Animator animator;
   protected JProgressBar progressBar;
   protected ChangeListener changeListener;
   private BasicProgressBarUI.Handler handler;
   private int animationIndex = 0;
   private int numFrames;
   private int repaintInterval;
   private int cycleTime;
   private static boolean ADJUSTTIMER = true;
   protected Rectangle boxRect;
   private Rectangle nextPaintRect;
   private Rectangle componentInnards;
   private Rectangle oldComponentInnards;
   private double delta = 0.0D;
   private int maxPosition = 0;

   public static ComponentUI createUI(JComponent var0) {
      return new BasicProgressBarUI();
   }

   public void installUI(JComponent var1) {
      this.progressBar = (JProgressBar)var1;
      this.installDefaults();
      this.installListeners();
      if (this.progressBar.isIndeterminate()) {
         this.initIndeterminateValues();
      }

   }

   public void uninstallUI(JComponent var1) {
      if (this.progressBar.isIndeterminate()) {
         this.cleanUpIndeterminateValues();
      }

      this.uninstallDefaults();
      this.uninstallListeners();
      this.progressBar = null;
   }

   protected void installDefaults() {
      LookAndFeel.installProperty(this.progressBar, "opaque", Boolean.TRUE);
      LookAndFeel.installBorder(this.progressBar, "ProgressBar.border");
      LookAndFeel.installColorsAndFont(this.progressBar, "ProgressBar.background", "ProgressBar.foreground", "ProgressBar.font");
      this.cellLength = UIManager.getInt("ProgressBar.cellLength");
      if (this.cellLength == 0) {
         this.cellLength = 1;
      }

      this.cellSpacing = UIManager.getInt("ProgressBar.cellSpacing");
      this.selectionForeground = UIManager.getColor("ProgressBar.selectionForeground");
      this.selectionBackground = UIManager.getColor("ProgressBar.selectionBackground");
   }

   protected void uninstallDefaults() {
      LookAndFeel.uninstallBorder(this.progressBar);
   }

   protected void installListeners() {
      this.changeListener = this.getHandler();
      this.progressBar.addChangeListener(this.changeListener);
      this.progressBar.addPropertyChangeListener(this.getHandler());
   }

   private BasicProgressBarUI.Handler getHandler() {
      if (this.handler == null) {
         this.handler = new BasicProgressBarUI.Handler();
      }

      return this.handler;
   }

   protected void startAnimationTimer() {
      if (this.animator == null) {
         this.animator = new BasicProgressBarUI.Animator();
      }

      this.animator.start(this.getRepaintInterval());
   }

   protected void stopAnimationTimer() {
      if (this.animator != null) {
         this.animator.stop();
      }

   }

   protected void uninstallListeners() {
      this.progressBar.removeChangeListener(this.changeListener);
      this.progressBar.removePropertyChangeListener(this.getHandler());
      this.handler = null;
   }

   public int getBaseline(JComponent var1, int var2, int var3) {
      super.getBaseline(var1, var2, var3);
      if (this.progressBar.isStringPainted() && this.progressBar.getOrientation() == 0) {
         FontMetrics var4 = this.progressBar.getFontMetrics(this.progressBar.getFont());
         Insets var5 = this.progressBar.getInsets();
         int var6 = var5.top;
         var3 = var3 - var5.top - var5.bottom;
         return var6 + (var3 + var4.getAscent() - var4.getLeading() - var4.getDescent()) / 2;
      } else {
         return -1;
      }
   }

   public Component.BaselineResizeBehavior getBaselineResizeBehavior(JComponent var1) {
      super.getBaselineResizeBehavior(var1);
      return this.progressBar.isStringPainted() && this.progressBar.getOrientation() == 0 ? Component.BaselineResizeBehavior.CENTER_OFFSET : Component.BaselineResizeBehavior.OTHER;
   }

   protected Dimension getPreferredInnerHorizontal() {
      Dimension var1 = (Dimension)DefaultLookup.get(this.progressBar, this, "ProgressBar.horizontalSize");
      if (var1 == null) {
         var1 = new Dimension(146, 12);
      }

      return var1;
   }

   protected Dimension getPreferredInnerVertical() {
      Dimension var1 = (Dimension)DefaultLookup.get(this.progressBar, this, "ProgressBar.verticalSize");
      if (var1 == null) {
         var1 = new Dimension(12, 146);
      }

      return var1;
   }

   protected Color getSelectionForeground() {
      return this.selectionForeground;
   }

   protected Color getSelectionBackground() {
      return this.selectionBackground;
   }

   private int getCachedPercent() {
      return this.cachedPercent;
   }

   private void setCachedPercent(int var1) {
      this.cachedPercent = var1;
   }

   protected int getCellLength() {
      return this.progressBar.isStringPainted() ? 1 : this.cellLength;
   }

   protected void setCellLength(int var1) {
      this.cellLength = var1;
   }

   protected int getCellSpacing() {
      return this.progressBar.isStringPainted() ? 0 : this.cellSpacing;
   }

   protected void setCellSpacing(int var1) {
      this.cellSpacing = var1;
   }

   protected int getAmountFull(Insets var1, int var2, int var3) {
      int var4 = 0;
      BoundedRangeModel var5 = this.progressBar.getModel();
      if (var5.getMaximum() - var5.getMinimum() != 0) {
         if (this.progressBar.getOrientation() == 0) {
            var4 = (int)Math.round((double)var2 * this.progressBar.getPercentComplete());
         } else {
            var4 = (int)Math.round((double)var3 * this.progressBar.getPercentComplete());
         }
      }

      return var4;
   }

   public void paint(Graphics var1, JComponent var2) {
      if (this.progressBar.isIndeterminate()) {
         this.paintIndeterminate(var1, var2);
      } else {
         this.paintDeterminate(var1, var2);
      }

   }

   protected Rectangle getBox(Rectangle var1) {
      int var2 = this.getAnimationIndex();
      int var3 = this.numFrames / 2;
      if (this.sizeChanged() || this.delta == 0.0D || (double)this.maxPosition == 0.0D) {
         this.updateSizes();
      }

      var1 = this.getGenericBox(var1);
      if (var1 == null) {
         return null;
      } else if (var3 <= 0) {
         return null;
      } else {
         if (this.progressBar.getOrientation() == 0) {
            if (var2 < var3) {
               var1.x = this.componentInnards.x + (int)Math.round(this.delta * (double)var2);
            } else {
               var1.x = this.maxPosition - (int)Math.round(this.delta * (double)(var2 - var3));
            }
         } else if (var2 < var3) {
            var1.y = this.componentInnards.y + (int)Math.round(this.delta * (double)var2);
         } else {
            var1.y = this.maxPosition - (int)Math.round(this.delta * (double)(var2 - var3));
         }

         return var1;
      }
   }

   private void updateSizes() {
      boolean var1 = false;
      int var2;
      if (this.progressBar.getOrientation() == 0) {
         var2 = this.getBoxLength(this.componentInnards.width, this.componentInnards.height);
         this.maxPosition = this.componentInnards.x + this.componentInnards.width - var2;
      } else {
         var2 = this.getBoxLength(this.componentInnards.height, this.componentInnards.width);
         this.maxPosition = this.componentInnards.y + this.componentInnards.height - var2;
      }

      this.delta = 2.0D * (double)this.maxPosition / (double)this.numFrames;
   }

   private Rectangle getGenericBox(Rectangle var1) {
      if (var1 == null) {
         var1 = new Rectangle();
      }

      if (this.progressBar.getOrientation() == 0) {
         var1.width = this.getBoxLength(this.componentInnards.width, this.componentInnards.height);
         if (var1.width < 0) {
            var1 = null;
         } else {
            var1.height = this.componentInnards.height;
            var1.y = this.componentInnards.y;
         }
      } else {
         var1.height = this.getBoxLength(this.componentInnards.height, this.componentInnards.width);
         if (var1.height < 0) {
            var1 = null;
         } else {
            var1.width = this.componentInnards.width;
            var1.x = this.componentInnards.x;
         }
      }

      return var1;
   }

   protected int getBoxLength(int var1, int var2) {
      return (int)Math.round((double)var1 / 6.0D);
   }

   protected void paintIndeterminate(Graphics var1, JComponent var2) {
      if (var1 instanceof Graphics2D) {
         Insets var3 = this.progressBar.getInsets();
         int var4 = this.progressBar.getWidth() - (var3.right + var3.left);
         int var5 = this.progressBar.getHeight() - (var3.top + var3.bottom);
         if (var4 > 0 && var5 > 0) {
            Graphics2D var6 = (Graphics2D)var1;
            this.boxRect = this.getBox(this.boxRect);
            if (this.boxRect != null) {
               var6.setColor(this.progressBar.getForeground());
               var6.fillRect(this.boxRect.x, this.boxRect.y, this.boxRect.width, this.boxRect.height);
            }

            if (this.progressBar.isStringPainted()) {
               if (this.progressBar.getOrientation() == 0) {
                  this.paintString(var6, var3.left, var3.top, var4, var5, this.boxRect.x, this.boxRect.width, var3);
               } else {
                  this.paintString(var6, var3.left, var3.top, var4, var5, this.boxRect.y, this.boxRect.height, var3);
               }
            }

         }
      }
   }

   protected void paintDeterminate(Graphics var1, JComponent var2) {
      if (var1 instanceof Graphics2D) {
         Insets var3 = this.progressBar.getInsets();
         int var4 = this.progressBar.getWidth() - (var3.right + var3.left);
         int var5 = this.progressBar.getHeight() - (var3.top + var3.bottom);
         if (var4 > 0 && var5 > 0) {
            int var6 = this.getCellLength();
            int var7 = this.getCellSpacing();
            int var8 = this.getAmountFull(var3, var4, var5);
            Graphics2D var9 = (Graphics2D)var1;
            var9.setColor(this.progressBar.getForeground());
            if (this.progressBar.getOrientation() == 0) {
               if (var7 == 0 && var8 > 0) {
                  var9.setStroke(new BasicStroke((float)var5, 0, 2));
               } else {
                  var9.setStroke(new BasicStroke((float)var5, 0, 2, 0.0F, new float[]{(float)var6, (float)var7}, 0.0F));
               }

               if (BasicGraphicsUtils.isLeftToRight(var2)) {
                  var9.drawLine(var3.left, var5 / 2 + var3.top, var8 + var3.left, var5 / 2 + var3.top);
               } else {
                  var9.drawLine(var4 + var3.left, var5 / 2 + var3.top, var4 + var3.left - var8, var5 / 2 + var3.top);
               }
            } else {
               if (var7 == 0 && var8 > 0) {
                  var9.setStroke(new BasicStroke((float)var4, 0, 2));
               } else {
                  var9.setStroke(new BasicStroke((float)var4, 0, 2, 0.0F, new float[]{(float)var6, (float)var7}, 0.0F));
               }

               var9.drawLine(var4 / 2 + var3.left, var3.top + var5, var4 / 2 + var3.left, var3.top + var5 - var8);
            }

            if (this.progressBar.isStringPainted()) {
               this.paintString(var1, var3.left, var3.top, var4, var5, var8, var3);
            }

         }
      }
   }

   protected void paintString(Graphics var1, int var2, int var3, int var4, int var5, int var6, Insets var7) {
      if (this.progressBar.getOrientation() == 0) {
         if (BasicGraphicsUtils.isLeftToRight(this.progressBar)) {
            if (this.progressBar.isIndeterminate()) {
               this.boxRect = this.getBox(this.boxRect);
               this.paintString(var1, var2, var3, var4, var5, this.boxRect.x, this.boxRect.width, var7);
            } else {
               this.paintString(var1, var2, var3, var4, var5, var2, var6, var7);
            }
         } else {
            this.paintString(var1, var2, var3, var4, var5, var2 + var4 - var6, var6, var7);
         }
      } else if (this.progressBar.isIndeterminate()) {
         this.boxRect = this.getBox(this.boxRect);
         this.paintString(var1, var2, var3, var4, var5, this.boxRect.y, this.boxRect.height, var7);
      } else {
         this.paintString(var1, var2, var3, var4, var5, var3 + var5 - var6, var6, var7);
      }

   }

   private void paintString(Graphics var1, int var2, int var3, int var4, int var5, int var6, int var7, Insets var8) {
      if (var1 instanceof Graphics2D) {
         Graphics2D var9 = (Graphics2D)var1;
         String var10 = this.progressBar.getString();
         var9.setFont(this.progressBar.getFont());
         Point var11 = this.getStringPlacement(var9, var10, var2, var3, var4, var5);
         Rectangle var12 = var9.getClipBounds();
         if (this.progressBar.getOrientation() == 0) {
            var9.setColor(this.getSelectionBackground());
            SwingUtilities2.drawString(this.progressBar, var9, (String)var10, var11.x, var11.y);
            var9.setColor(this.getSelectionForeground());
            var9.clipRect(var6, var3, var7, var5);
            SwingUtilities2.drawString(this.progressBar, var9, (String)var10, var11.x, var11.y);
         } else {
            var9.setColor(this.getSelectionBackground());
            AffineTransform var13 = AffineTransform.getRotateInstance(1.5707963267948966D);
            var9.setFont(this.progressBar.getFont().deriveFont(var13));
            var11 = this.getStringPlacement(var9, var10, var2, var3, var4, var5);
            SwingUtilities2.drawString(this.progressBar, var9, (String)var10, var11.x, var11.y);
            var9.setColor(this.getSelectionForeground());
            var9.clipRect(var2, var6, var4, var7);
            SwingUtilities2.drawString(this.progressBar, var9, (String)var10, var11.x, var11.y);
         }

         var9.setClip(var12);
      }
   }

   protected Point getStringPlacement(Graphics var1, String var2, int var3, int var4, int var5, int var6) {
      FontMetrics var7 = SwingUtilities2.getFontMetrics(this.progressBar, var1, this.progressBar.getFont());
      int var8 = SwingUtilities2.stringWidth(this.progressBar, var7, var2);
      return this.progressBar.getOrientation() == 0 ? new Point(var3 + Math.round((float)(var5 / 2 - var8 / 2)), var4 + (var6 + var7.getAscent() - var7.getLeading() - var7.getDescent()) / 2) : new Point(var3 + (var5 - var7.getAscent() + var7.getLeading() + var7.getDescent()) / 2, var4 + Math.round((float)(var6 / 2 - var8 / 2)));
   }

   public Dimension getPreferredSize(JComponent var1) {
      Insets var3 = this.progressBar.getInsets();
      FontMetrics var4 = this.progressBar.getFontMetrics(this.progressBar.getFont());
      Dimension var2;
      String var5;
      int var6;
      int var7;
      if (this.progressBar.getOrientation() == 0) {
         var2 = new Dimension(this.getPreferredInnerHorizontal());
         if (this.progressBar.isStringPainted()) {
            var5 = this.progressBar.getString();
            var6 = SwingUtilities2.stringWidth(this.progressBar, var4, var5);
            if (var6 > var2.width) {
               var2.width = var6;
            }

            var7 = var4.getHeight() + var4.getDescent();
            if (var7 > var2.height) {
               var2.height = var7;
            }
         }
      } else {
         var2 = new Dimension(this.getPreferredInnerVertical());
         if (this.progressBar.isStringPainted()) {
            var5 = this.progressBar.getString();
            var6 = var4.getHeight() + var4.getDescent();
            if (var6 > var2.width) {
               var2.width = var6;
            }

            var7 = SwingUtilities2.stringWidth(this.progressBar, var4, var5);
            if (var7 > var2.height) {
               var2.height = var7;
            }
         }
      }

      var2.width += var3.left + var3.right;
      var2.height += var3.top + var3.bottom;
      return var2;
   }

   public Dimension getMinimumSize(JComponent var1) {
      Dimension var2 = this.getPreferredSize(this.progressBar);
      if (this.progressBar.getOrientation() == 0) {
         var2.width = 10;
      } else {
         var2.height = 10;
      }

      return var2;
   }

   public Dimension getMaximumSize(JComponent var1) {
      Dimension var2 = this.getPreferredSize(this.progressBar);
      if (this.progressBar.getOrientation() == 0) {
         var2.width = 32767;
      } else {
         var2.height = 32767;
      }

      return var2;
   }

   protected int getAnimationIndex() {
      return this.animationIndex;
   }

   protected final int getFrameCount() {
      return this.numFrames;
   }

   protected void setAnimationIndex(int var1) {
      if (this.animationIndex != var1) {
         if (this.sizeChanged()) {
            this.animationIndex = var1;
            this.maxPosition = 0;
            this.delta = 0.0D;
            this.progressBar.repaint();
         } else {
            this.nextPaintRect = this.getBox(this.nextPaintRect);
            this.animationIndex = var1;
            if (this.nextPaintRect != null) {
               this.boxRect = this.getBox(this.boxRect);
               if (this.boxRect != null) {
                  this.nextPaintRect.add(this.boxRect);
               }
            }

            if (this.nextPaintRect != null) {
               this.progressBar.repaint(this.nextPaintRect);
            } else {
               this.progressBar.repaint();
            }

         }
      }
   }

   private boolean sizeChanged() {
      if (this.oldComponentInnards != null && this.componentInnards != null) {
         this.oldComponentInnards.setRect(this.componentInnards);
         this.componentInnards = SwingUtilities.calculateInnerArea(this.progressBar, this.componentInnards);
         return !this.oldComponentInnards.equals(this.componentInnards);
      } else {
         return true;
      }
   }

   protected void incrementAnimationIndex() {
      int var1 = this.getAnimationIndex() + 1;
      if (var1 < this.numFrames) {
         this.setAnimationIndex(var1);
      } else {
         this.setAnimationIndex(0);
      }

   }

   private int getRepaintInterval() {
      return this.repaintInterval;
   }

   private int initRepaintInterval() {
      this.repaintInterval = DefaultLookup.getInt(this.progressBar, this, "ProgressBar.repaintInterval", 50);
      return this.repaintInterval;
   }

   private int getCycleTime() {
      return this.cycleTime;
   }

   private int initCycleTime() {
      this.cycleTime = DefaultLookup.getInt(this.progressBar, this, "ProgressBar.cycleTime", 3000);
      return this.cycleTime;
   }

   private void initIndeterminateDefaults() {
      this.initRepaintInterval();
      this.initCycleTime();
      if (this.repaintInterval <= 0) {
         this.repaintInterval = 100;
      }

      if (this.repaintInterval > this.cycleTime) {
         this.cycleTime = this.repaintInterval * 20;
      } else {
         int var1 = (int)Math.ceil((double)this.cycleTime / ((double)this.repaintInterval * 2.0D));
         this.cycleTime = this.repaintInterval * var1 * 2;
      }

   }

   private void initIndeterminateValues() {
      this.initIndeterminateDefaults();
      this.numFrames = this.cycleTime / this.repaintInterval;
      this.initAnimationIndex();
      this.boxRect = new Rectangle();
      this.nextPaintRect = new Rectangle();
      this.componentInnards = new Rectangle();
      this.oldComponentInnards = new Rectangle();
      this.progressBar.addHierarchyListener(this.getHandler());
      if (this.progressBar.isDisplayable()) {
         this.startAnimationTimer();
      }

   }

   private void cleanUpIndeterminateValues() {
      if (this.progressBar.isDisplayable()) {
         this.stopAnimationTimer();
      }

      this.cycleTime = this.repaintInterval = 0;
      this.numFrames = this.animationIndex = 0;
      this.maxPosition = 0;
      this.delta = 0.0D;
      this.boxRect = this.nextPaintRect = null;
      this.componentInnards = this.oldComponentInnards = null;
      this.progressBar.removeHierarchyListener(this.getHandler());
   }

   private void initAnimationIndex() {
      if (this.progressBar.getOrientation() == 0 && BasicGraphicsUtils.isLeftToRight(this.progressBar)) {
         this.setAnimationIndex(0);
      } else {
         this.setAnimationIndex(this.numFrames / 2);
      }

   }

   private class Handler implements ChangeListener, PropertyChangeListener, HierarchyListener {
      private Handler() {
      }

      public void stateChanged(ChangeEvent var1) {
         BoundedRangeModel var2 = BasicProgressBarUI.this.progressBar.getModel();
         int var3 = var2.getMaximum() - var2.getMinimum();
         int var5 = BasicProgressBarUI.this.getCachedPercent();
         int var4;
         if (var3 > 0) {
            var4 = (int)(100L * (long)var2.getValue() / (long)var3);
         } else {
            var4 = 0;
         }

         if (var4 != var5) {
            BasicProgressBarUI.this.setCachedPercent(var4);
            BasicProgressBarUI.this.progressBar.repaint();
         }

      }

      public void propertyChange(PropertyChangeEvent var1) {
         String var2 = var1.getPropertyName();
         if ("indeterminate" == var2) {
            if (BasicProgressBarUI.this.progressBar.isIndeterminate()) {
               BasicProgressBarUI.this.initIndeterminateValues();
            } else {
               BasicProgressBarUI.this.cleanUpIndeterminateValues();
            }

            BasicProgressBarUI.this.progressBar.repaint();
         }

      }

      public void hierarchyChanged(HierarchyEvent var1) {
         if ((var1.getChangeFlags() & 2L) != 0L && BasicProgressBarUI.this.progressBar.isIndeterminate()) {
            if (BasicProgressBarUI.this.progressBar.isDisplayable()) {
               BasicProgressBarUI.this.startAnimationTimer();
            } else {
               BasicProgressBarUI.this.stopAnimationTimer();
            }
         }

      }

      // $FF: synthetic method
      Handler(Object var2) {
         this();
      }
   }

   public class ChangeHandler implements ChangeListener {
      public void stateChanged(ChangeEvent var1) {
         BasicProgressBarUI.this.getHandler().stateChanged(var1);
      }
   }

   private class Animator implements ActionListener {
      private Timer timer;
      private long previousDelay;
      private int interval;
      private long lastCall;
      private int MINIMUM_DELAY;

      private Animator() {
         this.MINIMUM_DELAY = 5;
      }

      private void start(int var1) {
         this.previousDelay = (long)var1;
         this.lastCall = 0L;
         if (this.timer == null) {
            this.timer = new Timer(var1, this);
         } else {
            this.timer.setDelay(var1);
         }

         if (BasicProgressBarUI.ADJUSTTIMER) {
            this.timer.setRepeats(false);
            this.timer.setCoalesce(false);
         }

         this.timer.start();
      }

      private void stop() {
         this.timer.stop();
      }

      public void actionPerformed(ActionEvent var1) {
         if (BasicProgressBarUI.ADJUSTTIMER) {
            long var2 = System.currentTimeMillis();
            if (this.lastCall > 0L) {
               int var4 = (int)(this.previousDelay - var2 + this.lastCall + (long)BasicProgressBarUI.this.getRepaintInterval());
               if (var4 < this.MINIMUM_DELAY) {
                  var4 = this.MINIMUM_DELAY;
               }

               this.timer.setInitialDelay(var4);
               this.previousDelay = (long)var4;
            }

            this.timer.start();
            this.lastCall = var2;
         }

         BasicProgressBarUI.this.incrementAnimationIndex();
      }

      // $FF: synthetic method
      Animator(Object var2) {
         this();
      }
   }
}
