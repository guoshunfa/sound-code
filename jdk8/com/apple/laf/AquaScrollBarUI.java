package com.apple.laf;

import apple.laf.JRSUIConstants;
import apple.laf.JRSUIState;
import apple.laf.JRSUIStateFactory;
import apple.laf.JRSUIUtils;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Map;
import javax.swing.BoundedRangeModel;
import javax.swing.JComponent;
import javax.swing.JScrollBar;
import javax.swing.LookAndFeel;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.ScrollBarUI;

public class AquaScrollBarUI extends ScrollBarUI {
   private static final int kInitialDelay = 300;
   private static final int kNormalDelay = 100;
   static final int MIN_ARROW_COLLAPSE_SIZE = 64;
   protected boolean fIsDragging;
   protected Timer fScrollTimer;
   protected AquaScrollBarUI.ScrollListener fScrollListener;
   protected AquaScrollBarUI.TrackListener fTrackListener;
   protected JRSUIConstants.Hit fTrackHighlight;
   protected JRSUIConstants.Hit fMousePart;
   protected JScrollBar fScrollBar;
   protected AquaScrollBarUI.ModelListener fModelListener;
   protected PropertyChangeListener fPropertyChangeListener;
   protected final AquaPainter<JRSUIState.ScrollBarState> painter;
   static final AquaUtils.RecyclableSingleton<Map<JRSUIConstants.Hit, JRSUIConstants.ScrollBarPart>> hitToPressedPartMap = new AquaUtils.RecyclableSingleton<Map<JRSUIConstants.Hit, JRSUIConstants.ScrollBarPart>>() {
      protected Map<JRSUIConstants.Hit, JRSUIConstants.ScrollBarPart> getInstance() {
         HashMap var1 = new HashMap(7);
         var1.put(JRSUIConstants.ScrollBarHit.ARROW_MAX, JRSUIConstants.ScrollBarPart.ARROW_MAX);
         var1.put(JRSUIConstants.ScrollBarHit.ARROW_MIN, JRSUIConstants.ScrollBarPart.ARROW_MIN);
         var1.put(JRSUIConstants.ScrollBarHit.ARROW_MAX_INSIDE, JRSUIConstants.ScrollBarPart.ARROW_MAX_INSIDE);
         var1.put(JRSUIConstants.ScrollBarHit.ARROW_MIN_INSIDE, JRSUIConstants.ScrollBarPart.ARROW_MIN_INSIDE);
         var1.put(JRSUIConstants.ScrollBarHit.TRACK_MAX, JRSUIConstants.ScrollBarPart.TRACK_MAX);
         var1.put(JRSUIConstants.ScrollBarHit.TRACK_MIN, JRSUIConstants.ScrollBarPart.TRACK_MIN);
         var1.put(JRSUIConstants.ScrollBarHit.THUMB, JRSUIConstants.ScrollBarPart.THUMB);
         return var1;
      }
   };

   public static ComponentUI createUI(JComponent var0) {
      return new AquaScrollBarUI();
   }

   public AquaScrollBarUI() {
      this.fTrackHighlight = JRSUIConstants.Hit.NONE;
      this.fMousePart = JRSUIConstants.Hit.NONE;
      this.painter = AquaPainter.create(JRSUIStateFactory.getScrollBar());
   }

   public void installUI(JComponent var1) {
      this.fScrollBar = (JScrollBar)var1;
      this.installListeners();
      this.configureScrollBarColors();
   }

   public void uninstallUI(JComponent var1) {
      this.uninstallListeners();
      this.fScrollBar = null;
   }

   protected void configureScrollBarColors() {
      LookAndFeel.installColors(this.fScrollBar, "ScrollBar.background", "ScrollBar.foreground");
   }

   protected AquaScrollBarUI.TrackListener createTrackListener() {
      return new AquaScrollBarUI.TrackListener();
   }

   protected AquaScrollBarUI.ScrollListener createScrollListener() {
      return new AquaScrollBarUI.ScrollListener();
   }

   protected void installListeners() {
      this.fTrackListener = this.createTrackListener();
      this.fModelListener = this.createModelListener();
      this.fPropertyChangeListener = this.createPropertyChangeListener();
      this.fScrollBar.addMouseListener(this.fTrackListener);
      this.fScrollBar.addMouseMotionListener(this.fTrackListener);
      this.fScrollBar.getModel().addChangeListener(this.fModelListener);
      this.fScrollBar.addPropertyChangeListener(this.fPropertyChangeListener);
      this.fScrollListener = this.createScrollListener();
      this.fScrollTimer = new Timer(100, this.fScrollListener);
      this.fScrollTimer.setInitialDelay(300);
   }

   protected void uninstallListeners() {
      this.fScrollTimer.stop();
      this.fScrollTimer = null;
      this.fScrollBar.getModel().removeChangeListener(this.fModelListener);
      this.fScrollBar.removeMouseListener(this.fTrackListener);
      this.fScrollBar.removeMouseMotionListener(this.fTrackListener);
      this.fScrollBar.removePropertyChangeListener(this.fPropertyChangeListener);
   }

   protected PropertyChangeListener createPropertyChangeListener() {
      return new AquaScrollBarUI.PropertyChangeHandler();
   }

   protected AquaScrollBarUI.ModelListener createModelListener() {
      return new AquaScrollBarUI.ModelListener();
   }

   protected void syncState(JComponent var1) {
      JRSUIState.ScrollBarState var2 = (JRSUIState.ScrollBarState)this.painter.state;
      var2.set(this.isHorizontal() ? JRSUIConstants.Orientation.HORIZONTAL : JRSUIConstants.Orientation.VERTICAL);
      float var3 = (float)(this.fScrollBar.getMaximum() - this.fScrollBar.getMinimum() - this.fScrollBar.getModel().getExtent());
      if (var3 <= 0.0F) {
         var2.set(JRSUIConstants.NothingToScroll.YES);
      } else {
         JRSUIConstants.ScrollBarPart var4 = this.getPressedPart();
         var2.set(var4);
         var2.set(this.getState(var1, var4));
         var2.set(JRSUIConstants.NothingToScroll.NO);
         var2.setValue((double)((float)(this.fScrollBar.getValue() - this.fScrollBar.getMinimum()) / var3));
         var2.setThumbStart((double)this.getThumbStart());
         var2.setThumbPercent((double)this.getThumbPercent());
         var2.set(this.shouldShowArrows() ? JRSUIConstants.ShowArrows.YES : JRSUIConstants.ShowArrows.NO);
      }
   }

   public void paint(Graphics var1, JComponent var2) {
      this.syncState(var2);
      this.painter.paint(var1, var2, 0, 0, this.fScrollBar.getWidth(), this.fScrollBar.getHeight());
   }

   protected JRSUIConstants.State getState(JComponent var1, JRSUIConstants.ScrollBarPart var2) {
      if (!AquaFocusHandler.isActive(var1)) {
         return JRSUIConstants.State.INACTIVE;
      } else if (!var1.isEnabled()) {
         return JRSUIConstants.State.INACTIVE;
      } else {
         return var2 != JRSUIConstants.ScrollBarPart.NONE ? JRSUIConstants.State.PRESSED : JRSUIConstants.State.ACTIVE;
      }
   }

   protected JRSUIConstants.ScrollBarPart getPressedPart() {
      if (this.fTrackListener.fInArrows && this.fTrackListener.fStillInArrow) {
         JRSUIConstants.ScrollBarPart var1 = (JRSUIConstants.ScrollBarPart)((Map)hitToPressedPartMap.get()).get(this.fMousePart);
         return var1 == null ? JRSUIConstants.ScrollBarPart.NONE : var1;
      } else {
         return JRSUIConstants.ScrollBarPart.NONE;
      }
   }

   protected boolean shouldShowArrows() {
      return 64 < (this.isHorizontal() ? this.fScrollBar.getWidth() : this.fScrollBar.getHeight());
   }

   public void layoutContainer(Container var1) {
      this.fScrollBar.repaint();
      this.fScrollBar.revalidate();
   }

   protected Rectangle getTrackBounds() {
      return new Rectangle(0, 0, this.fScrollBar.getWidth(), this.fScrollBar.getHeight());
   }

   protected Rectangle getDragBounds() {
      return new Rectangle(0, 0, this.fScrollBar.getWidth(), this.fScrollBar.getHeight());
   }

   protected void startTimer(boolean var1) {
      this.fScrollTimer.setInitialDelay(var1 ? 300 : 100);
      this.fScrollTimer.start();
   }

   protected void scrollByBlock(int var1) {
      synchronized(this.fScrollBar) {
         int var3 = this.fScrollBar.getValue();
         int var4 = this.fScrollBar.getBlockIncrement(var1);
         int var5 = var4 * (var1 > 0 ? 1 : -1);
         this.fScrollBar.setValue(var3 + var5);
         this.fTrackHighlight = var1 > 0 ? JRSUIConstants.ScrollBarHit.TRACK_MAX : JRSUIConstants.ScrollBarHit.TRACK_MIN;
         this.fScrollBar.repaint();
         this.fScrollListener.setDirection(var1);
         this.fScrollListener.setScrollByBlock(true);
      }
   }

   protected void scrollByUnit(int var1) {
      synchronized(this.fScrollBar) {
         int var3 = this.fScrollBar.getUnitIncrement(var1);
         if (var1 <= 0) {
            var3 = -var3;
         }

         this.fScrollBar.setValue(var3 + this.fScrollBar.getValue());
         this.fScrollBar.repaint();
         this.fScrollListener.setDirection(var1);
         this.fScrollListener.setScrollByBlock(false);
      }
   }

   protected JRSUIConstants.Hit getPartHit(int var1, int var2) {
      this.syncState(this.fScrollBar);
      return JRSUIUtils.HitDetection.getHitForPoint(this.painter.getControl(), 0.0D, 0.0D, (double)this.fScrollBar.getWidth(), (double)this.fScrollBar.getHeight(), (double)var1, (double)var2);
   }

   float getThumbStart() {
      int var1 = this.fScrollBar.getMaximum();
      int var2 = this.fScrollBar.getMinimum();
      int var3 = var1 - var2;
      return var3 <= 0 ? 0.0F : (float)(this.fScrollBar.getValue() - this.fScrollBar.getMinimum()) / (float)var3;
   }

   float getThumbPercent() {
      int var1 = this.fScrollBar.getVisibleAmount();
      int var2 = this.fScrollBar.getMaximum();
      int var3 = this.fScrollBar.getMinimum();
      int var4 = var2 - var3;
      return var4 <= 0 ? 0.0F : (float)var1 / (float)var4;
   }

   public Dimension getPreferredSize(JComponent var1) {
      return this.isHorizontal() ? new Dimension(96, 15) : new Dimension(15, 96);
   }

   public Dimension getMinimumSize(JComponent var1) {
      return this.isHorizontal() ? new Dimension(54, 15) : new Dimension(15, 54);
   }

   public Dimension getMaximumSize(JComponent var1) {
      return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
   }

   boolean isHorizontal() {
      return this.fScrollBar.getOrientation() == 0;
   }

   Point getScrollToHereStartPoint(int var1, int var2) {
      Rectangle var3 = this.getDragBounds();
      this.syncState(this.fScrollBar);
      double[] var4 = new double[4];
      JRSUIUtils.ScrollBar.getPartBounds(var4, this.painter.getControl(), 0.0D, 0.0D, (double)this.fScrollBar.getWidth(), (double)this.fScrollBar.getHeight(), JRSUIConstants.ScrollBarPart.THUMB);
      Rectangle var5 = new Rectangle((int)var4[0], (int)var4[1], (int)var4[2], (int)var4[3]);
      Point var6 = new Point(var1, var2);
      int var7;
      int var8;
      if (this.isHorizontal()) {
         var7 = var5.width / 2;
         var8 = var3.x + var3.width;
         if (var1 + var7 > var8) {
            var6.x = var5.x + var5.width - var8 - var1 - 1;
         } else if (var1 - var7 < var3.x) {
            var6.x = var5.x + var1 - var3.x;
         } else {
            var6.x = var5.x + var7;
         }

         var6.y = (var5.y + var5.height) / 2;
         return var6;
      } else {
         var7 = var5.height / 2;
         var8 = var3.y + var3.height;
         if (var2 + var7 > var8) {
            var6.y = var5.y + var5.height - var8 - var2 - 1;
         } else if (var2 - var7 < var3.y) {
            var6.y = var5.y + var2 - var3.y;
         } else {
            var6.y = var5.y + var7;
         }

         var6.x = (var5.x + var5.width) / 2;
         return var6;
      }
   }

   static class HitUtil {
      static boolean isIncrement(JRSUIConstants.Hit var0) {
         return var0 == JRSUIConstants.ScrollBarHit.ARROW_MAX || var0 == JRSUIConstants.ScrollBarHit.ARROW_MAX_INSIDE;
      }

      static boolean isDecrement(JRSUIConstants.Hit var0) {
         return var0 == JRSUIConstants.ScrollBarHit.ARROW_MIN || var0 == JRSUIConstants.ScrollBarHit.ARROW_MIN_INSIDE;
      }

      static boolean isArrow(JRSUIConstants.Hit var0) {
         return isIncrement(var0) || isDecrement(var0);
      }

      static boolean isTrack(JRSUIConstants.Hit var0) {
         return var0 == JRSUIConstants.ScrollBarHit.TRACK_MAX || var0 == JRSUIConstants.ScrollBarHit.TRACK_MIN;
      }
   }

   protected class ScrollListener implements ActionListener {
      boolean fUseBlockIncrement;
      int fDirection = 1;

      void setDirection(int var1) {
         this.fDirection = var1;
      }

      void setScrollByBlock(boolean var1) {
         this.fUseBlockIncrement = var1;
      }

      public void actionPerformed(ActionEvent var1) {
         if (this.fUseBlockIncrement) {
            JRSUIConstants.Hit var2 = AquaScrollBarUI.this.getPartHit(AquaScrollBarUI.this.fTrackListener.fCurrentMouseX, AquaScrollBarUI.this.fTrackListener.fCurrentMouseY);
            if (var2 == JRSUIConstants.ScrollBarHit.TRACK_MIN || var2 == JRSUIConstants.ScrollBarHit.TRACK_MAX) {
               int var3 = var2 == JRSUIConstants.ScrollBarHit.TRACK_MAX ? 1 : -1;
               if (this.fDirection != var3) {
                  this.fDirection = var3;
               }
            }

            AquaScrollBarUI.this.scrollByBlock(this.fDirection);
            var2 = AquaScrollBarUI.this.getPartHit(AquaScrollBarUI.this.fTrackListener.fCurrentMouseX, AquaScrollBarUI.this.fTrackListener.fCurrentMouseY);
            if (var2 == JRSUIConstants.ScrollBarHit.THUMB) {
               ((Timer)var1.getSource()).stop();
            }
         } else {
            AquaScrollBarUI.this.scrollByUnit(this.fDirection);
         }

         if (this.fDirection > 0 && AquaScrollBarUI.this.fScrollBar.getValue() + AquaScrollBarUI.this.fScrollBar.getVisibleAmount() >= AquaScrollBarUI.this.fScrollBar.getMaximum()) {
            ((Timer)var1.getSource()).stop();
         } else if (this.fDirection < 0 && AquaScrollBarUI.this.fScrollBar.getValue() <= AquaScrollBarUI.this.fScrollBar.getMinimum()) {
            ((Timer)var1.getSource()).stop();
         }

      }
   }

   protected class TrackListener extends MouseAdapter implements MouseMotionListener {
      protected transient int fCurrentMouseX;
      protected transient int fCurrentMouseY;
      protected transient boolean fInArrows;
      protected transient boolean fStillInArrow = false;
      protected transient boolean fStillInTrack = false;
      protected transient int fFirstMouseX;
      protected transient int fFirstMouseY;
      protected transient int fFirstValue;

      public void mouseReleased(MouseEvent var1) {
         if (AquaScrollBarUI.this.fScrollBar.isEnabled()) {
            if (this.fInArrows) {
               this.mouseReleasedInArrows(var1);
            } else {
               this.mouseReleasedInTrack(var1);
            }

            this.fInArrows = false;
            this.fStillInArrow = false;
            this.fStillInTrack = false;
            AquaScrollBarUI.this.fScrollBar.repaint();
            AquaScrollBarUI.this.fScrollBar.revalidate();
         }
      }

      public void mousePressed(MouseEvent var1) {
         if (AquaScrollBarUI.this.fScrollBar.isEnabled()) {
            JRSUIConstants.Hit var2 = AquaScrollBarUI.this.getPartHit(var1.getX(), var1.getY());
            this.fInArrows = AquaScrollBarUI.HitUtil.isArrow(var2);
            if (this.fInArrows) {
               this.mousePressedInArrows(var1, var2);
            } else if (var2 == JRSUIConstants.Hit.NONE) {
               AquaScrollBarUI.this.fTrackHighlight = JRSUIConstants.Hit.NONE;
            } else {
               this.mousePressedInTrack(var1, var2);
            }

         }
      }

      public void mouseDragged(MouseEvent var1) {
         if (AquaScrollBarUI.this.fScrollBar.isEnabled()) {
            if (this.fInArrows) {
               this.mouseDraggedInArrows(var1);
            } else if (AquaScrollBarUI.this.fIsDragging) {
               this.mouseDraggedInTrack(var1);
            } else {
               JRSUIConstants.Hit var2 = AquaScrollBarUI.this.getPartHit(this.fCurrentMouseX, this.fCurrentMouseY);
               if (!AquaScrollBarUI.HitUtil.isTrack(var2)) {
                  this.fStillInTrack = false;
               }

               this.fCurrentMouseX = var1.getX();
               this.fCurrentMouseY = var1.getY();
               JRSUIConstants.Hit var3 = AquaScrollBarUI.this.getPartHit(var1.getX(), var1.getY());
               boolean var4 = AquaScrollBarUI.HitUtil.isTrack(var3);
               if (var4 == this.fStillInTrack) {
                  return;
               }

               this.fStillInTrack = var4;
               if (!this.fStillInTrack) {
                  AquaScrollBarUI.this.fScrollTimer.stop();
               } else {
                  AquaScrollBarUI.this.fScrollListener.actionPerformed(new ActionEvent(AquaScrollBarUI.this.fScrollTimer, 0, ""));
                  AquaScrollBarUI.this.startTimer(false);
               }
            }

         }
      }

      int getValueFromOffset(int var1, int var2, int var3) {
         boolean var4 = AquaScrollBarUI.this.isHorizontal();
         int var5 = var4 ? var1 : var2;
         int var6 = AquaScrollBarUI.this.fScrollBar.getVisibleAmount();
         int var7 = AquaScrollBarUI.this.fScrollBar.getMaximum();
         int var8 = AquaScrollBarUI.this.fScrollBar.getMinimum();
         int var9 = var7 - var8;
         AquaScrollBarUI.this.syncState(AquaScrollBarUI.this.fScrollBar);
         double var10 = JRSUIUtils.ScrollBar.getNativeOffsetChange(AquaScrollBarUI.this.painter.getControl(), 0.0D, 0.0D, (double)AquaScrollBarUI.this.fScrollBar.getWidth(), (double)AquaScrollBarUI.this.fScrollBar.getHeight(), var5, var6, var9);
         int var12 = var9 - var6;
         int var13 = (int)(var10 * (double)var12);
         int var14 = var3 + var13;
         var14 = Math.max(var8, var14);
         var14 = Math.min(var7 - var6, var14);
         return var14;
      }

      void mousePressedInArrows(MouseEvent var1, JRSUIConstants.Hit var2) {
         int var3 = AquaScrollBarUI.HitUtil.isIncrement(var2) ? 1 : -1;
         this.fStillInArrow = true;
         AquaScrollBarUI.this.scrollByUnit(var3);
         AquaScrollBarUI.this.fScrollTimer.stop();
         AquaScrollBarUI.this.fScrollListener.setDirection(var3);
         AquaScrollBarUI.this.fScrollListener.setScrollByBlock(false);
         AquaScrollBarUI.this.fMousePart = var2;
         AquaScrollBarUI.this.startTimer(true);
      }

      void mouseReleasedInArrows(MouseEvent var1) {
         AquaScrollBarUI.this.fScrollTimer.stop();
         AquaScrollBarUI.this.fMousePart = JRSUIConstants.Hit.NONE;
         AquaScrollBarUI.this.fScrollBar.setValueIsAdjusting(false);
      }

      void mouseDraggedInArrows(MouseEvent var1) {
         JRSUIConstants.Hit var2 = AquaScrollBarUI.this.getPartHit(var1.getX(), var1.getY());
         if (AquaScrollBarUI.this.fMousePart != var2 || !this.fStillInArrow) {
            if (AquaScrollBarUI.this.fMousePart != var2 && !AquaScrollBarUI.HitUtil.isArrow(var2)) {
               AquaScrollBarUI.this.fScrollTimer.stop();
               this.fStillInArrow = false;
               AquaScrollBarUI.this.fScrollBar.repaint();
            } else {
               AquaScrollBarUI.this.fMousePart = var2;
               AquaScrollBarUI.this.fScrollListener.setDirection(AquaScrollBarUI.HitUtil.isIncrement(var2) ? 1 : -1);
               this.fStillInArrow = true;
               AquaScrollBarUI.this.fScrollListener.actionPerformed(new ActionEvent(AquaScrollBarUI.this.fScrollTimer, 0, ""));
               AquaScrollBarUI.this.startTimer(false);
            }

            AquaScrollBarUI.this.fScrollBar.repaint();
         }
      }

      void mouseReleasedInTrack(MouseEvent var1) {
         if (AquaScrollBarUI.this.fTrackHighlight != JRSUIConstants.Hit.NONE) {
            AquaScrollBarUI.this.fScrollBar.repaint();
         }

         AquaScrollBarUI.this.fTrackHighlight = JRSUIConstants.Hit.NONE;
         AquaScrollBarUI.this.fIsDragging = false;
         AquaScrollBarUI.this.fScrollTimer.stop();
         AquaScrollBarUI.this.fScrollBar.setValueIsAdjusting(false);
      }

      void mousePressedInTrack(MouseEvent var1, JRSUIConstants.Hit var2) {
         AquaScrollBarUI.this.fScrollBar.setValueIsAdjusting(true);
         boolean var3 = var2 != JRSUIConstants.ScrollBarHit.THUMB && JRSUIUtils.ScrollBar.useScrollToClick();
         if (var1.isAltDown()) {
            var3 = !var3;
         }

         if (var3) {
            Point var7 = AquaScrollBarUI.this.getScrollToHereStartPoint(var1.getX(), var1.getY());
            this.fFirstMouseX = var7.x;
            this.fFirstMouseY = var7.y;
            this.fFirstValue = AquaScrollBarUI.this.fScrollBar.getValue();
            this.moveToMouse(var1);
            AquaScrollBarUI.this.fTrackHighlight = JRSUIConstants.ScrollBarHit.THUMB;
            AquaScrollBarUI.this.fIsDragging = true;
         } else {
            this.fCurrentMouseX = var1.getX();
            this.fCurrentMouseY = var1.getY();
            boolean var4 = false;
            byte var6;
            if (var2 == JRSUIConstants.ScrollBarHit.TRACK_MIN) {
               AquaScrollBarUI.this.fTrackHighlight = JRSUIConstants.ScrollBarHit.TRACK_MIN;
               var6 = -1;
            } else {
               if (var2 != JRSUIConstants.ScrollBarHit.TRACK_MAX) {
                  this.fFirstValue = AquaScrollBarUI.this.fScrollBar.getValue();
                  this.fFirstMouseX = this.fCurrentMouseX;
                  this.fFirstMouseY = this.fCurrentMouseY;
                  AquaScrollBarUI.this.fTrackHighlight = JRSUIConstants.ScrollBarHit.THUMB;
                  AquaScrollBarUI.this.fIsDragging = true;
                  return;
               }

               AquaScrollBarUI.this.fTrackHighlight = JRSUIConstants.ScrollBarHit.TRACK_MAX;
               var6 = 1;
            }

            AquaScrollBarUI.this.fIsDragging = false;
            this.fStillInTrack = true;
            AquaScrollBarUI.this.scrollByBlock(var6);
            JRSUIConstants.Hit var5 = AquaScrollBarUI.this.getPartHit(this.fCurrentMouseX, this.fCurrentMouseY);
            if (var5 == JRSUIConstants.ScrollBarHit.TRACK_MIN || var5 == JRSUIConstants.ScrollBarHit.TRACK_MAX) {
               AquaScrollBarUI.this.fScrollTimer.stop();
               AquaScrollBarUI.this.fScrollListener.setDirection(var5 == JRSUIConstants.ScrollBarHit.TRACK_MAX ? 1 : -1);
               AquaScrollBarUI.this.fScrollListener.setScrollByBlock(true);
               AquaScrollBarUI.this.startTimer(true);
            }

         }
      }

      void mouseDraggedInTrack(MouseEvent var1) {
         this.moveToMouse(var1);
      }

      void moveToMouse(MouseEvent var1) {
         this.fCurrentMouseX = var1.getX();
         this.fCurrentMouseY = var1.getY();
         int var2 = AquaScrollBarUI.this.fScrollBar.getValue();
         int var3 = this.getValueFromOffset(this.fCurrentMouseX - this.fFirstMouseX, this.fCurrentMouseY - this.fFirstMouseY, this.fFirstValue);
         if (var3 != var2) {
            AquaScrollBarUI.this.fScrollBar.setValue(var3);
            Rectangle var4 = AquaScrollBarUI.this.getTrackBounds();
            AquaScrollBarUI.this.fScrollBar.repaint(var4.x, var4.y, var4.width, var4.height);
         }
      }
   }

   protected class ModelListener implements ChangeListener {
      public void stateChanged(ChangeEvent var1) {
         AquaScrollBarUI.this.layoutContainer(AquaScrollBarUI.this.fScrollBar);
      }
   }

   protected class PropertyChangeHandler implements PropertyChangeListener {
      public void propertyChange(PropertyChangeEvent var1) {
         String var2 = var1.getPropertyName();
         if ("model".equals(var2)) {
            BoundedRangeModel var3 = (BoundedRangeModel)var1.getOldValue();
            BoundedRangeModel var4 = (BoundedRangeModel)var1.getNewValue();
            var3.removeChangeListener(AquaScrollBarUI.this.fModelListener);
            var4.addChangeListener(AquaScrollBarUI.this.fModelListener);
            AquaScrollBarUI.this.fScrollBar.repaint();
            AquaScrollBarUI.this.fScrollBar.revalidate();
         } else if ("Frame.active".equals(var2)) {
            AquaScrollBarUI.this.fScrollBar.repaint();
         }

      }
   }
}
