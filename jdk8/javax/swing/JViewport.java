package javax.swing;

import java.awt.AlphaComposite;
import java.awt.Component;
import java.awt.Composite;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.peer.ComponentPeer;
import java.beans.Transient;
import java.io.Serializable;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.ViewportUI;

public class JViewport extends JComponent implements Accessible {
   private static final String uiClassID = "ViewportUI";
   static final Object EnableWindowBlit = "EnableWindowBlit";
   protected boolean isViewSizeSet = false;
   protected Point lastPaintPosition = null;
   /** @deprecated */
   @Deprecated
   protected boolean backingStore = false;
   protected transient Image backingStoreImage = null;
   protected boolean scrollUnderway = false;
   private ComponentListener viewListener = null;
   private transient ChangeEvent changeEvent = null;
   public static final int BLIT_SCROLL_MODE = 1;
   public static final int BACKINGSTORE_SCROLL_MODE = 2;
   public static final int SIMPLE_SCROLL_MODE = 0;
   private int scrollMode = 1;
   private transient boolean repaintAll;
   private transient boolean waitingForRepaint;
   private transient Timer repaintTimer;
   private transient boolean inBlitPaint;
   private boolean hasHadValidView;
   private boolean viewChanged;

   public JViewport() {
      this.setLayout(this.createLayoutManager());
      this.setOpaque(true);
      this.updateUI();
      this.setInheritsPopupMenu(true);
   }

   public ViewportUI getUI() {
      return (ViewportUI)this.ui;
   }

   public void setUI(ViewportUI var1) {
      super.setUI(var1);
   }

   public void updateUI() {
      this.setUI((ViewportUI)UIManager.getUI(this));
   }

   public String getUIClassID() {
      return "ViewportUI";
   }

   protected void addImpl(Component var1, Object var2, int var3) {
      this.setView(var1);
   }

   public void remove(Component var1) {
      var1.removeComponentListener(this.viewListener);
      super.remove(var1);
   }

   public void scrollRectToVisible(Rectangle var1) {
      Component var2 = this.getView();
      if (var2 != null) {
         if (!var2.isValid()) {
            this.validateView();
         }

         int var3 = this.positionAdjustment(this.getWidth(), var1.width, var1.x);
         int var4 = this.positionAdjustment(this.getHeight(), var1.height, var1.y);
         if (var3 != 0 || var4 != 0) {
            Point var5 = this.getViewPosition();
            Dimension var6 = var2.getSize();
            int var7 = var5.x;
            int var8 = var5.y;
            Dimension var9 = this.getExtentSize();
            var5.x -= var3;
            var5.y -= var4;
            if (var2.isValid()) {
               if (this.getParent().getComponentOrientation().isLeftToRight()) {
                  if (var5.x + var9.width > var6.width) {
                     var5.x = Math.max(0, var6.width - var9.width);
                  } else if (var5.x < 0) {
                     var5.x = 0;
                  }
               } else if (var9.width > var6.width) {
                  var5.x = var6.width - var9.width;
               } else {
                  var5.x = Math.max(0, Math.min(var6.width - var9.width, var5.x));
               }

               if (var5.y + var9.height > var6.height) {
                  var5.y = Math.max(0, var6.height - var9.height);
               } else if (var5.y < 0) {
                  var5.y = 0;
               }
            }

            if (var5.x != var7 || var5.y != var8) {
               this.setViewPosition(var5);
               this.scrollUnderway = false;
            }
         }

      }
   }

   private void validateView() {
      Container var1 = SwingUtilities.getValidateRoot(this, false);
      if (var1 != null) {
         var1.validate();
         RepaintManager var2 = RepaintManager.currentManager((JComponent)this);
         if (var2 != null) {
            var2.removeInvalidComponent((JComponent)var1);
         }

      }
   }

   private int positionAdjustment(int var1, int var2, int var3) {
      if (var3 >= 0 && var2 + var3 <= var1) {
         return 0;
      } else if (var3 <= 0 && var2 + var3 >= var1) {
         return 0;
      } else if (var3 > 0 && var2 <= var1) {
         return -var3 + var1 - var2;
      } else if (var3 >= 0 && var2 >= var1) {
         return -var3;
      } else if (var3 <= 0 && var2 <= var1) {
         return -var3;
      } else {
         return var3 < 0 && var2 >= var1 ? -var3 + var1 - var2 : 0;
      }
   }

   public final void setBorder(Border var1) {
      if (var1 != null) {
         throw new IllegalArgumentException("JViewport.setBorder() not supported");
      }
   }

   public final Insets getInsets() {
      return new Insets(0, 0, 0, 0);
   }

   public final Insets getInsets(Insets var1) {
      var1.left = var1.top = var1.right = var1.bottom = 0;
      return var1;
   }

   private Graphics getBackingStoreGraphics(Graphics var1) {
      Graphics var2 = this.backingStoreImage.getGraphics();
      var2.setColor(var1.getColor());
      var2.setFont(var1.getFont());
      var2.setClip(var1.getClipBounds());
      return var2;
   }

   private void paintViaBackingStore(Graphics var1) {
      Graphics var2 = this.getBackingStoreGraphics(var1);

      try {
         super.paint(var2);
         var1.drawImage(this.backingStoreImage, 0, 0, this);
      } finally {
         var2.dispose();
      }

   }

   private void paintViaBackingStore(Graphics var1, Rectangle var2) {
      Graphics var3 = this.getBackingStoreGraphics(var1);

      try {
         super.paint(var3);
         var1.setClip(var2);
         var1.drawImage(this.backingStoreImage, 0, 0, this);
      } finally {
         var3.dispose();
      }

   }

   public boolean isOptimizedDrawingEnabled() {
      return false;
   }

   protected boolean isPaintingOrigin() {
      return this.scrollMode == 2;
   }

   private Point getViewLocation() {
      Component var1 = this.getView();
      return var1 != null ? var1.getLocation() : new Point(0, 0);
   }

   public void paint(Graphics var1) {
      int var2 = this.getWidth();
      int var3 = this.getHeight();
      if (var2 > 0 && var3 > 0) {
         if (this.inBlitPaint) {
            super.paint(var1);
         } else {
            Rectangle var4;
            if (this.repaintAll) {
               this.repaintAll = false;
               var4 = var1.getClipBounds();
               if (var4.width >= this.getWidth() && var4.height >= this.getHeight()) {
                  if (this.repaintTimer != null) {
                     this.repaintTimer.stop();
                  }

                  this.waitingForRepaint = false;
               } else {
                  this.waitingForRepaint = true;
                  if (this.repaintTimer == null) {
                     this.repaintTimer = this.createRepaintTimer();
                  }

                  this.repaintTimer.stop();
                  this.repaintTimer.start();
               }
            } else if (this.waitingForRepaint) {
               var4 = var1.getClipBounds();
               if (var4.width >= this.getWidth() && var4.height >= this.getHeight()) {
                  this.waitingForRepaint = false;
                  this.repaintTimer.stop();
               }
            }

            if (this.backingStore && !this.isBlitting() && this.getView() != null) {
               var4 = this.getView().getBounds();
               if (!this.isOpaque()) {
                  var1.clipRect(0, 0, var4.width, var4.height);
               }

               if (this.backingStoreImage == null) {
                  this.backingStoreImage = this.createImage(var2, var3);
                  Rectangle var5 = var1.getClipBounds();
                  if (var5.width == var2 && var5.height == var3) {
                     this.paintViaBackingStore(var1);
                  } else {
                     if (!this.isOpaque()) {
                        var1.setClip(0, 0, Math.min(var4.width, var2), Math.min(var4.height, var3));
                     } else {
                        var1.setClip(0, 0, var2, var3);
                     }

                     this.paintViaBackingStore(var1, var5);
                  }
               } else if (this.scrollUnderway && !this.lastPaintPosition.equals(this.getViewLocation())) {
                  Point var21 = new Point();
                  Point var6 = new Point();
                  Dimension var7 = new Dimension();
                  Rectangle var8 = new Rectangle();
                  Point var9 = this.getViewLocation();
                  int var10 = var9.x - this.lastPaintPosition.x;
                  int var11 = var9.y - this.lastPaintPosition.y;
                  boolean var12 = this.computeBlit(var10, var11, var21, var6, var7, var8);
                  if (!var12) {
                     this.paintViaBackingStore(var1);
                  } else {
                     int var13 = var6.x - var21.x;
                     int var14 = var6.y - var21.y;
                     Rectangle var15 = var1.getClipBounds();
                     var1.setClip(0, 0, var2, var3);
                     Graphics var16 = this.getBackingStoreGraphics(var1);

                     try {
                        var16.copyArea(var21.x, var21.y, var7.width, var7.height, var13, var14);
                        var1.setClip(var15.x, var15.y, var15.width, var15.height);
                        Rectangle var17 = var4.intersection(var8);
                        var16.setClip(var17);
                        super.paint(var16);
                        var1.drawImage(this.backingStoreImage, 0, 0, this);
                     } finally {
                        var16.dispose();
                     }
                  }
               } else {
                  this.paintViaBackingStore(var1);
               }

               this.lastPaintPosition = this.getViewLocation();
               this.scrollUnderway = false;
            } else {
               super.paint(var1);
               this.lastPaintPosition = this.getViewLocation();
            }
         }
      }
   }

   public void reshape(int var1, int var2, int var3, int var4) {
      boolean var5 = this.getWidth() != var3 || this.getHeight() != var4;
      if (var5) {
         this.backingStoreImage = null;
      }

      super.reshape(var1, var2, var3, var4);
      if (var5 || this.viewChanged) {
         this.viewChanged = false;
         this.fireStateChanged();
      }

   }

   public void setScrollMode(int var1) {
      this.scrollMode = var1;
      this.backingStore = var1 == 2;
   }

   public int getScrollMode() {
      return this.scrollMode;
   }

   /** @deprecated */
   @Deprecated
   public boolean isBackingStoreEnabled() {
      return this.scrollMode == 2;
   }

   /** @deprecated */
   @Deprecated
   public void setBackingStoreEnabled(boolean var1) {
      if (var1) {
         this.setScrollMode(2);
      } else {
         this.setScrollMode(1);
      }

   }

   private boolean isBlitting() {
      Component var1 = this.getView();
      return this.scrollMode == 1 && var1 instanceof JComponent && var1.isOpaque();
   }

   public Component getView() {
      return this.getComponentCount() > 0 ? this.getComponent(0) : null;
   }

   public void setView(Component var1) {
      int var2 = this.getComponentCount();

      for(int var3 = var2 - 1; var3 >= 0; --var3) {
         this.remove(this.getComponent(var3));
      }

      this.isViewSizeSet = false;
      if (var1 != null) {
         super.addImpl(var1, (Object)null, -1);
         this.viewListener = this.createViewListener();
         var1.addComponentListener(this.viewListener);
      }

      if (this.hasHadValidView) {
         this.fireStateChanged();
      } else if (var1 != null) {
         this.hasHadValidView = true;
      }

      this.viewChanged = true;
      this.revalidate();
      this.repaint();
   }

   public Dimension getViewSize() {
      Component var1 = this.getView();
      if (var1 == null) {
         return new Dimension(0, 0);
      } else {
         return this.isViewSizeSet ? var1.getSize() : var1.getPreferredSize();
      }
   }

   public void setViewSize(Dimension var1) {
      Component var2 = this.getView();
      if (var2 != null) {
         Dimension var3 = var2.getSize();
         if (!var1.equals(var3)) {
            this.scrollUnderway = false;
            var2.setSize(var1);
            this.isViewSizeSet = true;
            this.fireStateChanged();
         }
      }

   }

   public Point getViewPosition() {
      Component var1 = this.getView();
      if (var1 != null) {
         Point var2 = var1.getLocation();
         var2.x = -var2.x;
         var2.y = -var2.y;
         return var2;
      } else {
         return new Point(0, 0);
      }
   }

   public void setViewPosition(Point var1) {
      Component var2 = this.getView();
      if (var2 != null) {
         int var5 = var1.x;
         int var6 = var1.y;
         int var3;
         int var4;
         if (var2 instanceof JComponent) {
            JComponent var7 = (JComponent)var2;
            var3 = var7.getX();
            var4 = var7.getY();
         } else {
            Rectangle var17 = var2.getBounds();
            var3 = var17.x;
            var4 = var17.y;
         }

         int var18 = -var5;
         int var8 = -var6;
         if (var3 != var18 || var4 != var8) {
            if (!this.waitingForRepaint && this.isBlitting() && this.canUseWindowBlitter()) {
               RepaintManager var9 = RepaintManager.currentManager((JComponent)this);
               JComponent var10 = (JComponent)var2;
               Rectangle var11 = var9.getDirtyRegion(var10);
               if (var11 != null && var11.contains(var10.getVisibleRect())) {
                  var2.setLocation(var18, var8);
                  this.repaintAll = false;
               } else {
                  var9.beginPaint();

                  try {
                     Graphics var12 = JComponent.safelyGetGraphics(this);
                     this.flushViewDirtyRegion(var12, var11);
                     var2.setLocation(var18, var8);
                     Rectangle var13 = new Rectangle(0, 0, this.getWidth(), Math.min(this.getHeight(), var10.getHeight()));
                     var12.setClip(var13);
                     this.repaintAll = this.windowBlitPaint(var12) && this.needsRepaintAfterBlit();
                     var12.dispose();
                     var9.notifyRepaintPerformed(this, var13.x, var13.y, var13.width, var13.height);
                     var9.markCompletelyClean((JComponent)this.getParent());
                     var9.markCompletelyClean(this);
                     var9.markCompletelyClean(var10);
                  } finally {
                     var9.endPaint();
                  }
               }
            } else {
               this.scrollUnderway = true;
               var2.setLocation(var18, var8);
               this.repaintAll = false;
            }

            this.revalidate();
            this.fireStateChanged();
         }

      }
   }

   public Rectangle getViewRect() {
      return new Rectangle(this.getViewPosition(), this.getExtentSize());
   }

   protected boolean computeBlit(int var1, int var2, Point var3, Point var4, Dimension var5, Rectangle var6) {
      int var7 = Math.abs(var1);
      int var8 = Math.abs(var2);
      Dimension var9 = this.getExtentSize();
      if (var1 == 0 && var2 != 0 && var8 < var9.height) {
         if (var2 < 0) {
            var3.y = -var2;
            var4.y = 0;
            var6.y = var9.height + var2;
         } else {
            var3.y = 0;
            var4.y = var2;
            var6.y = 0;
         }

         var6.x = var3.x = var4.x = 0;
         var5.width = var9.width;
         var5.height = var9.height - var8;
         var6.width = var9.width;
         var6.height = var8;
         return true;
      } else if (var2 == 0 && var1 != 0 && var7 < var9.width) {
         if (var1 < 0) {
            var3.x = -var1;
            var4.x = 0;
            var6.x = var9.width + var1;
         } else {
            var3.x = 0;
            var4.x = var1;
            var6.x = 0;
         }

         var6.y = var3.y = var4.y = 0;
         var5.width = var9.width - var7;
         var5.height = var9.height;
         var6.width = var7;
         var6.height = var9.height;
         return true;
      } else {
         return false;
      }
   }

   @Transient
   public Dimension getExtentSize() {
      return this.getSize();
   }

   public Dimension toViewCoordinates(Dimension var1) {
      return new Dimension(var1);
   }

   public Point toViewCoordinates(Point var1) {
      return new Point(var1);
   }

   public void setExtentSize(Dimension var1) {
      Dimension var2 = this.getExtentSize();
      if (!var1.equals(var2)) {
         this.setSize(var1);
         this.fireStateChanged();
      }

   }

   protected JViewport.ViewListener createViewListener() {
      return new JViewport.ViewListener();
   }

   protected LayoutManager createLayoutManager() {
      return ViewportLayout.SHARED_INSTANCE;
   }

   public void addChangeListener(ChangeListener var1) {
      this.listenerList.add(ChangeListener.class, var1);
   }

   public void removeChangeListener(ChangeListener var1) {
      this.listenerList.remove(ChangeListener.class, var1);
   }

   public ChangeListener[] getChangeListeners() {
      return (ChangeListener[])this.listenerList.getListeners(ChangeListener.class);
   }

   protected void fireStateChanged() {
      Object[] var1 = this.listenerList.getListenerList();

      for(int var2 = var1.length - 2; var2 >= 0; var2 -= 2) {
         if (var1[var2] == ChangeListener.class) {
            if (this.changeEvent == null) {
               this.changeEvent = new ChangeEvent(this);
            }

            ((ChangeListener)var1[var2 + 1]).stateChanged(this.changeEvent);
         }
      }

   }

   public void repaint(long var1, int var3, int var4, int var5, int var6) {
      Container var7 = this.getParent();
      if (var7 != null) {
         var7.repaint(var1, var3 + this.getX(), var4 + this.getY(), var5, var6);
      } else {
         super.repaint(var1, var3, var4, var5, var6);
      }

   }

   protected String paramString() {
      String var1 = this.isViewSizeSet ? "true" : "false";
      String var2 = this.lastPaintPosition != null ? this.lastPaintPosition.toString() : "";
      String var3 = this.scrollUnderway ? "true" : "false";
      return super.paramString() + ",isViewSizeSet=" + var1 + ",lastPaintPosition=" + var2 + ",scrollUnderway=" + var3;
   }

   protected void firePropertyChange(String var1, Object var2, Object var3) {
      super.firePropertyChange(var1, var2, var3);
      if (var1.equals(EnableWindowBlit)) {
         if (var3 != null) {
            this.setScrollMode(1);
         } else {
            this.setScrollMode(0);
         }
      }

   }

   private boolean needsRepaintAfterBlit() {
      Container var1;
      for(var1 = this.getParent(); var1 != null && var1.isLightweight(); var1 = var1.getParent()) {
      }

      if (var1 != null) {
         ComponentPeer var2 = var1.getPeer();
         if (var2 != null && var2.canDetermineObscurity() && !var2.isObscured()) {
            return false;
         }
      }

      return true;
   }

   private Timer createRepaintTimer() {
      Timer var1 = new Timer(300, new ActionListener() {
         public void actionPerformed(ActionEvent var1) {
            if (JViewport.this.waitingForRepaint) {
               JViewport.this.repaint();
            }

         }
      });
      var1.setRepeats(false);
      return var1;
   }

   private void flushViewDirtyRegion(Graphics var1, Rectangle var2) {
      JComponent var3 = (JComponent)this.getView();
      if (var2 != null && var2.width > 0 && var2.height > 0) {
         var2.x += var3.getX();
         var2.y += var3.getY();
         Rectangle var4 = var1.getClipBounds();
         if (var4 == null) {
            var1.setClip(0, 0, this.getWidth(), this.getHeight());
         }

         var1.clipRect(var2.x, var2.y, var2.width, var2.height);
         var4 = var1.getClipBounds();
         if (var4.width > 0 && var4.height > 0) {
            this.paintView(var1);
         }
      }

   }

   private boolean windowBlitPaint(Graphics var1) {
      int var2 = this.getWidth();
      int var3 = this.getHeight();
      if (var2 != 0 && var3 != 0) {
         RepaintManager var5 = RepaintManager.currentManager((JComponent)this);
         JComponent var6 = (JComponent)this.getView();
         boolean var4;
         if (this.lastPaintPosition != null && !this.lastPaintPosition.equals(this.getViewLocation())) {
            Point var7 = new Point();
            Point var8 = new Point();
            Dimension var9 = new Dimension();
            Rectangle var10 = new Rectangle();
            Point var11 = this.getViewLocation();
            int var12 = var11.x - this.lastPaintPosition.x;
            int var13 = var11.y - this.lastPaintPosition.y;
            boolean var14 = this.computeBlit(var12, var13, var7, var8, var9, var10);
            if (!var14) {
               this.paintView(var1);
               var4 = false;
            } else {
               Rectangle var15 = var6.getBounds().intersection(var10);
               var15.x -= var6.getX();
               var15.y -= var6.getY();
               this.blitDoubleBuffered(var6, var1, var15.x, var15.y, var15.width, var15.height, var7.x, var7.y, var8.x, var8.y, var9.width, var9.height);
               var4 = true;
            }
         } else {
            this.paintView(var1);
            var4 = false;
         }

         this.lastPaintPosition = this.getViewLocation();
         return var4;
      } else {
         return false;
      }
   }

   private void blitDoubleBuffered(JComponent var1, Graphics var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9, int var10, int var11, int var12) {
      RepaintManager var13 = RepaintManager.currentManager((JComponent)this);
      int var14 = var9 - var7;
      int var15 = var10 - var8;
      Composite var16 = null;
      if (var2 instanceof Graphics2D) {
         Graphics2D var17 = (Graphics2D)var2;
         var16 = var17.getComposite();
         var17.setComposite(AlphaComposite.Src);
      }

      var13.copyArea(this, var2, var7, var8, var11, var12, var14, var15, false);
      if (var16 != null) {
         ((Graphics2D)var2).setComposite(var16);
      }

      int var19 = var1.getX();
      int var18 = var1.getY();
      var2.translate(var19, var18);
      var2.setClip(var3, var4, var5, var6);
      var1.paintForceDoubleBuffered(var2);
      var2.translate(-var19, -var18);
   }

   private void paintView(Graphics var1) {
      Rectangle var2 = var1.getClipBounds();
      JComponent var3 = (JComponent)this.getView();
      if (var3.getWidth() >= this.getWidth()) {
         int var4 = var3.getX();
         int var5 = var3.getY();
         var1.translate(var4, var5);
         var1.setClip(var2.x - var4, var2.y - var5, var2.width, var2.height);
         var3.paintForceDoubleBuffered(var1);
         var1.translate(-var4, -var5);
         var1.setClip(var2.x, var2.y, var2.width, var2.height);
      } else {
         try {
            this.inBlitPaint = true;
            this.paintForceDoubleBuffered(var1);
         } finally {
            this.inBlitPaint = false;
         }
      }

   }

   private boolean canUseWindowBlitter() {
      if (this.isShowing() && (this.getParent() instanceof JComponent || this.getView() instanceof JComponent)) {
         if (this.isPainting()) {
            return false;
         } else {
            Rectangle var1 = RepaintManager.currentManager((JComponent)this).getDirtyRegion((JComponent)this.getParent());
            if (var1 != null && var1.width > 0 && var1.height > 0) {
               return false;
            } else {
               Rectangle var2 = new Rectangle(0, 0, this.getWidth(), this.getHeight());
               Rectangle var3 = new Rectangle();
               Rectangle var4 = null;
               Object var6 = null;

               Object var5;
               for(var5 = this; var5 != null && isLightweightComponent((Component)var5); var5 = ((Container)var5).getParent()) {
                  int var7 = ((Container)var5).getX();
                  int var8 = ((Container)var5).getY();
                  int var9 = ((Container)var5).getWidth();
                  int var10 = ((Container)var5).getHeight();
                  var3.setBounds(var2);
                  SwingUtilities.computeIntersection(0, 0, var9, var10, var2);
                  if (!var2.equals(var3)) {
                     return false;
                  }

                  if (var6 != null && var5 instanceof JComponent && !((JComponent)var5).isOptimizedDrawingEnabled()) {
                     Component[] var11 = ((Container)var5).getComponents();
                     int var12 = 0;

                     for(int var13 = var11.length - 1; var13 >= 0; --var13) {
                        if (var11[var13] == var6) {
                           var12 = var13 - 1;
                           break;
                        }
                     }

                     while(var12 >= 0) {
                        var4 = var11[var12].getBounds(var4);
                        if (var4.intersects(var2)) {
                           return false;
                        }

                        --var12;
                     }
                  }

                  var2.x += var7;
                  var2.y += var8;
                  var6 = var5;
               }

               return var5 != null;
            }
         }
      } else {
         return false;
      }
   }

   public AccessibleContext getAccessibleContext() {
      if (this.accessibleContext == null) {
         this.accessibleContext = new JViewport.AccessibleJViewport();
      }

      return this.accessibleContext;
   }

   protected class AccessibleJViewport extends JComponent.AccessibleJComponent {
      protected AccessibleJViewport() {
         super();
      }

      public AccessibleRole getAccessibleRole() {
         return AccessibleRole.VIEWPORT;
      }
   }

   protected class ViewListener extends ComponentAdapter implements Serializable {
      public void componentResized(ComponentEvent var1) {
         JViewport.this.fireStateChanged();
         JViewport.this.revalidate();
      }
   }
}
