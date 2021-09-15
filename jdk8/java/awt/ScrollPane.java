package java.awt;

import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.MouseWheelEvent;
import java.awt.peer.ScrollPanePeer;
import java.beans.ConstructorProperties;
import java.beans.Transient;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import sun.awt.ScrollPaneWheelScroller;
import sun.awt.SunToolkit;

public class ScrollPane extends Container implements Accessible {
   public static final int SCROLLBARS_AS_NEEDED = 0;
   public static final int SCROLLBARS_ALWAYS = 1;
   public static final int SCROLLBARS_NEVER = 2;
   private int scrollbarDisplayPolicy;
   private ScrollPaneAdjustable vAdjustable;
   private ScrollPaneAdjustable hAdjustable;
   private static final String base = "scrollpane";
   private static int nameCounter;
   private static final boolean defaultWheelScroll = true;
   private boolean wheelScrollingEnabled;
   private static final long serialVersionUID = 7956609840827222915L;

   private static native void initIDs();

   public ScrollPane() throws HeadlessException {
      this(0);
   }

   @ConstructorProperties({"scrollbarDisplayPolicy"})
   public ScrollPane(int var1) throws HeadlessException {
      this.wheelScrollingEnabled = true;
      GraphicsEnvironment.checkHeadless();
      this.layoutMgr = null;
      this.width = 100;
      this.height = 100;
      switch(var1) {
      case 0:
      case 1:
      case 2:
         this.scrollbarDisplayPolicy = var1;
         this.vAdjustable = new ScrollPaneAdjustable(this, new ScrollPane.PeerFixer(this), 1);
         this.hAdjustable = new ScrollPaneAdjustable(this, new ScrollPane.PeerFixer(this), 0);
         this.setWheelScrollingEnabled(true);
         return;
      default:
         throw new IllegalArgumentException("illegal scrollbar display policy");
      }
   }

   String constructComponentName() {
      Class var1 = ScrollPane.class;
      synchronized(ScrollPane.class) {
         return "scrollpane" + nameCounter++;
      }
   }

   private void addToPanel(Component var1, Object var2, int var3) {
      Panel var4 = new Panel();
      var4.setLayout(new BorderLayout());
      var4.add(var1);
      super.addImpl(var4, var2, var3);
      this.validate();
   }

   protected final void addImpl(Component var1, Object var2, int var3) {
      synchronized(this.getTreeLock()) {
         if (this.getComponentCount() > 0) {
            this.remove(0);
         }

         if (var3 > 0) {
            throw new IllegalArgumentException("position greater than 0");
         } else {
            if (!SunToolkit.isLightweightOrUnknown(var1)) {
               super.addImpl(var1, var2, var3);
            } else {
               this.addToPanel(var1, var2, var3);
            }

         }
      }
   }

   public int getScrollbarDisplayPolicy() {
      return this.scrollbarDisplayPolicy;
   }

   public Dimension getViewportSize() {
      Insets var1 = this.getInsets();
      return new Dimension(this.width - var1.right - var1.left, this.height - var1.top - var1.bottom);
   }

   public int getHScrollbarHeight() {
      int var1 = 0;
      if (this.scrollbarDisplayPolicy != 2) {
         ScrollPanePeer var2 = (ScrollPanePeer)this.peer;
         if (var2 != null) {
            var1 = var2.getHScrollbarHeight();
         }
      }

      return var1;
   }

   public int getVScrollbarWidth() {
      int var1 = 0;
      if (this.scrollbarDisplayPolicy != 2) {
         ScrollPanePeer var2 = (ScrollPanePeer)this.peer;
         if (var2 != null) {
            var1 = var2.getVScrollbarWidth();
         }
      }

      return var1;
   }

   public Adjustable getVAdjustable() {
      return this.vAdjustable;
   }

   public Adjustable getHAdjustable() {
      return this.hAdjustable;
   }

   public void setScrollPosition(int var1, int var2) {
      synchronized(this.getTreeLock()) {
         if (this.getComponentCount() == 0) {
            throw new NullPointerException("child is null");
         } else {
            this.hAdjustable.setValue(var1);
            this.vAdjustable.setValue(var2);
         }
      }
   }

   public void setScrollPosition(Point var1) {
      this.setScrollPosition(var1.x, var1.y);
   }

   @Transient
   public Point getScrollPosition() {
      synchronized(this.getTreeLock()) {
         if (this.getComponentCount() == 0) {
            throw new NullPointerException("child is null");
         } else {
            return new Point(this.hAdjustable.getValue(), this.vAdjustable.getValue());
         }
      }
   }

   public final void setLayout(LayoutManager var1) {
      throw new AWTError("ScrollPane controls layout");
   }

   public void doLayout() {
      this.layout();
   }

   Dimension calculateChildSize() {
      Dimension var1 = this.getSize();
      Insets var2 = this.getInsets();
      int var3 = var1.width - var2.left * 2;
      int var4 = var1.height - var2.top * 2;
      Component var7 = this.getComponent(0);
      Dimension var8 = new Dimension(var7.getPreferredSize());
      boolean var5;
      boolean var6;
      if (this.scrollbarDisplayPolicy == 0) {
         var5 = var8.height > var4;
         var6 = var8.width > var3;
      } else if (this.scrollbarDisplayPolicy == 1) {
         var6 = true;
         var5 = true;
      } else {
         var6 = false;
         var5 = false;
      }

      int var9 = this.getVScrollbarWidth();
      int var10 = this.getHScrollbarHeight();
      if (var5) {
         var3 -= var9;
      }

      if (var6) {
         var4 -= var10;
      }

      if (var8.width < var3) {
         var8.width = var3;
      }

      if (var8.height < var4) {
         var8.height = var4;
      }

      return var8;
   }

   /** @deprecated */
   @Deprecated
   public void layout() {
      if (this.getComponentCount() != 0) {
         Component var1 = this.getComponent(0);
         Point var2 = this.getScrollPosition();
         Dimension var3 = this.calculateChildSize();
         Dimension var4 = this.getViewportSize();
         var1.reshape(-var2.x, -var2.y, var3.width, var3.height);
         ScrollPanePeer var5 = (ScrollPanePeer)this.peer;
         if (var5 != null) {
            var5.childResized(var3.width, var3.height);
         }

         var4 = this.getViewportSize();
         this.hAdjustable.setSpan(0, var3.width, var4.width);
         this.vAdjustable.setSpan(0, var3.height, var4.height);
      }
   }

   public void printComponents(Graphics var1) {
      if (this.getComponentCount() != 0) {
         Component var2 = this.getComponent(0);
         Point var3 = var2.getLocation();
         Dimension var4 = this.getViewportSize();
         Insets var5 = this.getInsets();
         Graphics var6 = var1.create();

         try {
            var6.clipRect(var5.left, var5.top, var4.width, var4.height);
            var6.translate(var3.x, var3.y);
            var2.printAll(var6);
         } finally {
            var6.dispose();
         }

      }
   }

   public void addNotify() {
      synchronized(this.getTreeLock()) {
         int var2 = 0;
         int var3 = 0;
         if (this.getComponentCount() > 0) {
            var2 = this.vAdjustable.getValue();
            var3 = this.hAdjustable.getValue();
            this.vAdjustable.setValue(0);
            this.hAdjustable.setValue(0);
         }

         if (this.peer == null) {
            this.peer = this.getToolkit().createScrollPane(this);
         }

         super.addNotify();
         if (this.getComponentCount() > 0) {
            this.vAdjustable.setValue(var2);
            this.hAdjustable.setValue(var3);
         }

      }
   }

   public String paramString() {
      String var1;
      switch(this.scrollbarDisplayPolicy) {
      case 0:
         var1 = "as-needed";
         break;
      case 1:
         var1 = "always";
         break;
      case 2:
         var1 = "never";
         break;
      default:
         var1 = "invalid display policy";
      }

      Point var2 = this.getComponentCount() > 0 ? this.getScrollPosition() : new Point(0, 0);
      Insets var3 = this.getInsets();
      return super.paramString() + ",ScrollPosition=(" + var2.x + "," + var2.y + "),Insets=(" + var3.top + "," + var3.left + "," + var3.bottom + "," + var3.right + "),ScrollbarDisplayPolicy=" + var1 + ",wheelScrollingEnabled=" + this.isWheelScrollingEnabled();
   }

   void autoProcessMouseWheel(MouseWheelEvent var1) {
      this.processMouseWheelEvent(var1);
   }

   protected void processMouseWheelEvent(MouseWheelEvent var1) {
      if (this.isWheelScrollingEnabled()) {
         ScrollPaneWheelScroller.handleWheelScrolling(this, var1);
         var1.consume();
      }

      super.processMouseWheelEvent(var1);
   }

   protected boolean eventTypeEnabled(int var1) {
      return var1 == 507 && this.isWheelScrollingEnabled() ? true : super.eventTypeEnabled(var1);
   }

   public void setWheelScrollingEnabled(boolean var1) {
      this.wheelScrollingEnabled = var1;
   }

   public boolean isWheelScrollingEnabled() {
      return this.wheelScrollingEnabled;
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.defaultWriteObject();
   }

   private void readObject(ObjectInputStream var1) throws ClassNotFoundException, IOException, HeadlessException {
      GraphicsEnvironment.checkHeadless();
      ObjectInputStream.GetField var2 = var1.readFields();
      this.scrollbarDisplayPolicy = var2.get("scrollbarDisplayPolicy", (int)0);
      this.hAdjustable = (ScrollPaneAdjustable)var2.get("hAdjustable", (Object)null);
      this.vAdjustable = (ScrollPaneAdjustable)var2.get("vAdjustable", (Object)null);
      this.wheelScrollingEnabled = var2.get("wheelScrollingEnabled", true);
   }

   public AccessibleContext getAccessibleContext() {
      if (this.accessibleContext == null) {
         this.accessibleContext = new ScrollPane.AccessibleAWTScrollPane();
      }

      return this.accessibleContext;
   }

   static {
      Toolkit.loadLibraries();
      if (!GraphicsEnvironment.isHeadless()) {
         initIDs();
      }

      nameCounter = 0;
   }

   protected class AccessibleAWTScrollPane extends Container.AccessibleAWTContainer {
      private static final long serialVersionUID = 6100703663886637L;

      protected AccessibleAWTScrollPane() {
         super();
      }

      public AccessibleRole getAccessibleRole() {
         return AccessibleRole.SCROLL_PANE;
      }
   }

   class PeerFixer implements AdjustmentListener, Serializable {
      private static final long serialVersionUID = 1043664721353696630L;
      private ScrollPane scroller;

      PeerFixer(ScrollPane var2) {
         this.scroller = var2;
      }

      public void adjustmentValueChanged(AdjustmentEvent var1) {
         Adjustable var2 = var1.getAdjustable();
         int var3 = var1.getValue();
         ScrollPanePeer var4 = (ScrollPanePeer)this.scroller.peer;
         if (var4 != null) {
            var4.setValue(var2, var3);
         }

         Component var5 = this.scroller.getComponent(0);
         switch(var2.getOrientation()) {
         case 0:
            var5.move(-var3, var5.getLocation().y);
            break;
         case 1:
            var5.move(var5.getLocation().x, -var3);
            break;
         default:
            throw new IllegalArgumentException("Illegal adjustable orientation");
         }

      }
   }
}
