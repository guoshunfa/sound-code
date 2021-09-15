package java.awt;

import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.peer.ScrollbarPeer;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.EventListener;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import javax.accessibility.AccessibleState;
import javax.accessibility.AccessibleStateSet;
import javax.accessibility.AccessibleValue;

public class Scrollbar extends Component implements Adjustable, Accessible {
   public static final int HORIZONTAL = 0;
   public static final int VERTICAL = 1;
   int value;
   int maximum;
   int minimum;
   int visibleAmount;
   int orientation;
   int lineIncrement;
   int pageIncrement;
   transient boolean isAdjusting;
   transient AdjustmentListener adjustmentListener;
   private static final String base = "scrollbar";
   private static int nameCounter = 0;
   private static final long serialVersionUID = 8451667562882310543L;
   private int scrollbarSerializedDataVersion;

   private static native void initIDs();

   public Scrollbar() throws HeadlessException {
      this(1, 0, 10, 0, 100);
   }

   public Scrollbar(int var1) throws HeadlessException {
      this(var1, 0, 10, 0, 100);
   }

   public Scrollbar(int var1, int var2, int var3, int var4, int var5) throws HeadlessException {
      this.lineIncrement = 1;
      this.pageIncrement = 10;
      this.scrollbarSerializedDataVersion = 1;
      GraphicsEnvironment.checkHeadless();
      switch(var1) {
      case 0:
      case 1:
         this.orientation = var1;
         this.setValues(var2, var3, var4, var5);
         return;
      default:
         throw new IllegalArgumentException("illegal scrollbar orientation");
      }
   }

   String constructComponentName() {
      Class var1 = Scrollbar.class;
      synchronized(Scrollbar.class) {
         return "scrollbar" + nameCounter++;
      }
   }

   public void addNotify() {
      synchronized(this.getTreeLock()) {
         if (this.peer == null) {
            this.peer = this.getToolkit().createScrollbar(this);
         }

         super.addNotify();
      }
   }

   public int getOrientation() {
      return this.orientation;
   }

   public void setOrientation(int var1) {
      synchronized(this.getTreeLock()) {
         if (var1 == this.orientation) {
            return;
         }

         switch(var1) {
         case 0:
         case 1:
            this.orientation = var1;
            if (this.peer != null) {
               this.removeNotify();
               this.addNotify();
               this.invalidate();
            }
            break;
         default:
            throw new IllegalArgumentException("illegal scrollbar orientation");
         }
      }

      if (this.accessibleContext != null) {
         this.accessibleContext.firePropertyChange("AccessibleState", var1 == 1 ? AccessibleState.HORIZONTAL : AccessibleState.VERTICAL, var1 == 1 ? AccessibleState.VERTICAL : AccessibleState.HORIZONTAL);
      }

   }

   public int getValue() {
      return this.value;
   }

   public void setValue(int var1) {
      this.setValues(var1, this.visibleAmount, this.minimum, this.maximum);
   }

   public int getMinimum() {
      return this.minimum;
   }

   public void setMinimum(int var1) {
      this.setValues(this.value, this.visibleAmount, var1, this.maximum);
   }

   public int getMaximum() {
      return this.maximum;
   }

   public void setMaximum(int var1) {
      if (var1 == Integer.MIN_VALUE) {
         var1 = -2147483647;
      }

      if (this.minimum >= var1) {
         this.minimum = var1 - 1;
      }

      this.setValues(this.value, this.visibleAmount, this.minimum, var1);
   }

   public int getVisibleAmount() {
      return this.getVisible();
   }

   /** @deprecated */
   @Deprecated
   public int getVisible() {
      return this.visibleAmount;
   }

   public void setVisibleAmount(int var1) {
      this.setValues(this.value, var1, this.minimum, this.maximum);
   }

   public void setUnitIncrement(int var1) {
      this.setLineIncrement(var1);
   }

   /** @deprecated */
   @Deprecated
   public synchronized void setLineIncrement(int var1) {
      int var2 = var1 < 1 ? 1 : var1;
      if (this.lineIncrement != var2) {
         this.lineIncrement = var2;
         ScrollbarPeer var3 = (ScrollbarPeer)this.peer;
         if (var3 != null) {
            var3.setLineIncrement(this.lineIncrement);
         }

      }
   }

   public int getUnitIncrement() {
      return this.getLineIncrement();
   }

   /** @deprecated */
   @Deprecated
   public int getLineIncrement() {
      return this.lineIncrement;
   }

   public void setBlockIncrement(int var1) {
      this.setPageIncrement(var1);
   }

   /** @deprecated */
   @Deprecated
   public synchronized void setPageIncrement(int var1) {
      int var2 = var1 < 1 ? 1 : var1;
      if (this.pageIncrement != var2) {
         this.pageIncrement = var2;
         ScrollbarPeer var3 = (ScrollbarPeer)this.peer;
         if (var3 != null) {
            var3.setPageIncrement(this.pageIncrement);
         }

      }
   }

   public int getBlockIncrement() {
      return this.getPageIncrement();
   }

   /** @deprecated */
   @Deprecated
   public int getPageIncrement() {
      return this.pageIncrement;
   }

   public void setValues(int var1, int var2, int var3, int var4) {
      int var5;
      synchronized(this) {
         if (var3 == Integer.MAX_VALUE) {
            var3 = 2147483646;
         }

         if (var4 <= var3) {
            var4 = var3 + 1;
         }

         long var7 = (long)var4 - (long)var3;
         if (var7 > 2147483647L) {
            var7 = 2147483647L;
            var4 = var3 + (int)var7;
         }

         if (var2 > (int)var7) {
            var2 = (int)var7;
         }

         if (var2 < 1) {
            var2 = 1;
         }

         if (var1 < var3) {
            var1 = var3;
         }

         if (var1 > var4 - var2) {
            var1 = var4 - var2;
         }

         var5 = this.value;
         this.value = var1;
         this.visibleAmount = var2;
         this.minimum = var3;
         this.maximum = var4;
         ScrollbarPeer var9 = (ScrollbarPeer)this.peer;
         if (var9 != null) {
            var9.setValues(var1, this.visibleAmount, var3, var4);
         }
      }

      if (var5 != var1 && this.accessibleContext != null) {
         this.accessibleContext.firePropertyChange("AccessibleValue", var5, var1);
      }

   }

   public boolean getValueIsAdjusting() {
      return this.isAdjusting;
   }

   public void setValueIsAdjusting(boolean var1) {
      boolean var2;
      synchronized(this) {
         var2 = this.isAdjusting;
         this.isAdjusting = var1;
      }

      if (var2 != var1 && this.accessibleContext != null) {
         this.accessibleContext.firePropertyChange("AccessibleState", var2 ? AccessibleState.BUSY : null, var1 ? AccessibleState.BUSY : null);
      }

   }

   public synchronized void addAdjustmentListener(AdjustmentListener var1) {
      if (var1 != null) {
         this.adjustmentListener = AWTEventMulticaster.add(this.adjustmentListener, var1);
         this.newEventsOnly = true;
      }
   }

   public synchronized void removeAdjustmentListener(AdjustmentListener var1) {
      if (var1 != null) {
         this.adjustmentListener = AWTEventMulticaster.remove(this.adjustmentListener, var1);
      }
   }

   public synchronized AdjustmentListener[] getAdjustmentListeners() {
      return (AdjustmentListener[])this.getListeners(AdjustmentListener.class);
   }

   public <T extends EventListener> T[] getListeners(Class<T> var1) {
      AdjustmentListener var2 = null;
      if (var1 == AdjustmentListener.class) {
         var2 = this.adjustmentListener;
         return AWTEventMulticaster.getListeners(var2, var1);
      } else {
         return super.getListeners(var1);
      }
   }

   boolean eventEnabled(AWTEvent var1) {
      if (var1.id == 601) {
         return (this.eventMask & 256L) != 0L || this.adjustmentListener != null;
      } else {
         return super.eventEnabled(var1);
      }
   }

   protected void processEvent(AWTEvent var1) {
      if (var1 instanceof AdjustmentEvent) {
         this.processAdjustmentEvent((AdjustmentEvent)var1);
      } else {
         super.processEvent(var1);
      }
   }

   protected void processAdjustmentEvent(AdjustmentEvent var1) {
      AdjustmentListener var2 = this.adjustmentListener;
      if (var2 != null) {
         var2.adjustmentValueChanged(var1);
      }

   }

   protected String paramString() {
      return super.paramString() + ",val=" + this.value + ",vis=" + this.visibleAmount + ",min=" + this.minimum + ",max=" + this.maximum + (this.orientation == 1 ? ",vert" : ",horz") + ",isAdjusting=" + this.isAdjusting;
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.defaultWriteObject();
      AWTEventMulticaster.save(var1, "adjustmentL", this.adjustmentListener);
      var1.writeObject((Object)null);
   }

   private void readObject(ObjectInputStream var1) throws ClassNotFoundException, IOException, HeadlessException {
      GraphicsEnvironment.checkHeadless();
      var1.defaultReadObject();

      Object var2;
      while(null != (var2 = var1.readObject())) {
         String var3 = ((String)var2).intern();
         if ("adjustmentL" == var3) {
            this.addAdjustmentListener((AdjustmentListener)((AdjustmentListener)var1.readObject()));
         } else {
            var1.readObject();
         }
      }

   }

   public AccessibleContext getAccessibleContext() {
      if (this.accessibleContext == null) {
         this.accessibleContext = new Scrollbar.AccessibleAWTScrollBar();
      }

      return this.accessibleContext;
   }

   static {
      Toolkit.loadLibraries();
      if (!GraphicsEnvironment.isHeadless()) {
         initIDs();
      }

   }

   protected class AccessibleAWTScrollBar extends Component.AccessibleAWTComponent implements AccessibleValue {
      private static final long serialVersionUID = -344337268523697807L;

      protected AccessibleAWTScrollBar() {
         super();
      }

      public AccessibleStateSet getAccessibleStateSet() {
         AccessibleStateSet var1 = super.getAccessibleStateSet();
         if (Scrollbar.this.getValueIsAdjusting()) {
            var1.add(AccessibleState.BUSY);
         }

         if (Scrollbar.this.getOrientation() == 1) {
            var1.add(AccessibleState.VERTICAL);
         } else {
            var1.add(AccessibleState.HORIZONTAL);
         }

         return var1;
      }

      public AccessibleRole getAccessibleRole() {
         return AccessibleRole.SCROLL_BAR;
      }

      public AccessibleValue getAccessibleValue() {
         return this;
      }

      public Number getCurrentAccessibleValue() {
         return Scrollbar.this.getValue();
      }

      public boolean setCurrentAccessibleValue(Number var1) {
         if (var1 instanceof Integer) {
            Scrollbar.this.setValue(var1.intValue());
            return true;
         } else {
            return false;
         }
      }

      public Number getMinimumAccessibleValue() {
         return Scrollbar.this.getMinimum();
      }

      public Number getMaximumAccessibleValue() {
         return Scrollbar.this.getMaximum();
      }
   }
}
