package java.awt;

import java.awt.dnd.DropTarget;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.awt.event.KeyEvent;
import java.awt.peer.ComponentPeer;
import java.awt.peer.ContainerPeer;
import java.awt.peer.LightweightPeer;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamField;
import java.io.OptionalDataException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleComponent;
import javax.accessibility.AccessibleContext;
import javax.swing.JInternalFrame;
import sun.awt.AWTAccessor;
import sun.awt.AppContext;
import sun.awt.CausedFocusEvent;
import sun.awt.PeerEvent;
import sun.awt.SunToolkit;
import sun.java2d.pipe.Region;
import sun.security.action.GetBooleanAction;
import sun.util.logging.PlatformLogger;

public class Container extends Component {
   private static final PlatformLogger log = PlatformLogger.getLogger("java.awt.Container");
   private static final PlatformLogger eventLog = PlatformLogger.getLogger("java.awt.event.Container");
   private static final Component[] EMPTY_ARRAY = new Component[0];
   private java.util.List<Component> component = new ArrayList();
   LayoutManager layoutMgr;
   private LightweightDispatcher dispatcher;
   private transient FocusTraversalPolicy focusTraversalPolicy;
   private boolean focusCycleRoot = false;
   private boolean focusTraversalPolicyProvider;
   private transient Set<Thread> printingThreads;
   private transient boolean printing = false;
   transient ContainerListener containerListener;
   transient int listeningChildren;
   transient int listeningBoundsChildren;
   transient int descendantsCount;
   transient Color preserveBackgroundColor = null;
   private static final long serialVersionUID = 4613797578919906343L;
   static final boolean INCLUDE_SELF = true;
   static final boolean SEARCH_HEAVYWEIGHTS = true;
   private transient int numOfHWComponents = 0;
   private transient int numOfLWComponents = 0;
   private static final PlatformLogger mixingLog = PlatformLogger.getLogger("java.awt.mixing.Container");
   private static final ObjectStreamField[] serialPersistentFields;
   private static final boolean isJavaAwtSmartInvalidate;
   private static boolean descendUnconditionallyWhenValidating;
   transient Component modalComp;
   transient AppContext modalAppContext;
   private int containerSerializedDataVersion = 1;

   private static native void initIDs();

   void initializeFocusTraversalKeys() {
      this.focusTraversalKeys = new Set[4];
   }

   public int getComponentCount() {
      return this.countComponents();
   }

   /** @deprecated */
   @Deprecated
   public int countComponents() {
      return this.component.size();
   }

   public Component getComponent(int var1) {
      try {
         return (Component)this.component.get(var1);
      } catch (IndexOutOfBoundsException var3) {
         throw new ArrayIndexOutOfBoundsException("No such child: " + var1);
      }
   }

   public Component[] getComponents() {
      return this.getComponents_NoClientCode();
   }

   final Component[] getComponents_NoClientCode() {
      return (Component[])this.component.toArray(EMPTY_ARRAY);
   }

   Component[] getComponentsSync() {
      synchronized(this.getTreeLock()) {
         return this.getComponents();
      }
   }

   public Insets getInsets() {
      return this.insets();
   }

   /** @deprecated */
   @Deprecated
   public Insets insets() {
      ComponentPeer var1 = this.peer;
      if (var1 instanceof ContainerPeer) {
         ContainerPeer var2 = (ContainerPeer)var1;
         return (Insets)var2.getInsets().clone();
      } else {
         return new Insets(0, 0, 0, 0);
      }
   }

   public Component add(Component var1) {
      this.addImpl(var1, (Object)null, -1);
      return var1;
   }

   public Component add(String var1, Component var2) {
      this.addImpl(var2, var1, -1);
      return var2;
   }

   public Component add(Component var1, int var2) {
      this.addImpl(var1, (Object)null, var2);
      return var1;
   }

   private void checkAddToSelf(Component var1) {
      if (var1 instanceof Container) {
         for(Container var2 = this; var2 != null; var2 = var2.parent) {
            if (var2 == var1) {
               throw new IllegalArgumentException("adding container's parent to itself");
            }
         }
      }

   }

   private void checkNotAWindow(Component var1) {
      if (var1 instanceof Window) {
         throw new IllegalArgumentException("adding a window to a container");
      }
   }

   private void checkAdding(Component var1, int var2) {
      this.checkTreeLock();
      GraphicsConfiguration var3 = this.getGraphicsConfiguration();
      if (var2 <= this.component.size() && var2 >= 0) {
         if (var1.parent == this && var2 == this.component.size()) {
            throw new IllegalArgumentException("illegal component position " + var2 + " should be less then " + this.component.size());
         } else {
            this.checkAddToSelf(var1);
            this.checkNotAWindow(var1);
            Window var4 = this.getContainingWindow();
            Window var5 = var1.getContainingWindow();
            if (var4 != var5) {
               throw new IllegalArgumentException("component and container should be in the same top-level window");
            } else {
               if (var3 != null) {
                  var1.checkGD(var3.getDevice().getIDstring());
               }

            }
         }
      } else {
         throw new IllegalArgumentException("illegal component position");
      }
   }

   private boolean removeDelicately(Component var1, Container var2, int var3) {
      this.checkTreeLock();
      int var4 = this.getComponentZOrder(var1);
      boolean var5 = isRemoveNotifyNeeded(var1, this, var2);
      if (var5) {
         var1.removeNotify();
      }

      if (var2 != this) {
         if (this.layoutMgr != null) {
            this.layoutMgr.removeLayoutComponent(var1);
         }

         this.adjustListeningChildren(32768L, -var1.numListening(32768L));
         this.adjustListeningChildren(65536L, -var1.numListening(65536L));
         this.adjustDescendants(-var1.countHierarchyMembers());
         var1.parent = null;
         if (var5) {
            var1.setGraphicsConfiguration((GraphicsConfiguration)null);
         }

         this.component.remove(var4);
         this.invalidateIfValid();
      } else {
         this.component.remove(var4);
         this.component.add(var3, var1);
      }

      if (var1.parent == null) {
         if (this.containerListener != null || (this.eventMask & 2L) != 0L || Toolkit.enabledOnToolkit(2L)) {
            ContainerEvent var6 = new ContainerEvent(this, 301, var1);
            this.dispatchEvent(var6);
         }

         var1.createHierarchyEvents(1400, var1, this, 1L, Toolkit.enabledOnToolkit(32768L));
         if (this.peer != null && this.layoutMgr == null && this.isVisible()) {
            this.updateCursorImmediately();
         }
      }

      return var5;
   }

   boolean canContainFocusOwner(Component var1) {
      if (this.isEnabled() && this.isDisplayable() && this.isVisible() && this.isFocusable()) {
         if (this.isFocusCycleRoot()) {
            FocusTraversalPolicy var2 = this.getFocusTraversalPolicy();
            if (var2 instanceof DefaultFocusTraversalPolicy && !((DefaultFocusTraversalPolicy)var2).accept(var1)) {
               return false;
            }
         }

         synchronized(this.getTreeLock()) {
            return this.parent != null ? this.parent.canContainFocusOwner(var1) : true;
         }
      } else {
         return false;
      }
   }

   final boolean hasHeavyweightDescendants() {
      this.checkTreeLock();
      return this.numOfHWComponents > 0;
   }

   final boolean hasLightweightDescendants() {
      this.checkTreeLock();
      return this.numOfLWComponents > 0;
   }

   Container getHeavyweightContainer() {
      this.checkTreeLock();
      return this.peer != null && !(this.peer instanceof LightweightPeer) ? this : this.getNativeContainer();
   }

   private static boolean isRemoveNotifyNeeded(Component var0, Container var1, Container var2) {
      if (var1 == null) {
         return false;
      } else if (var0.peer == null) {
         return false;
      } else if (var2.peer == null) {
         return true;
      } else {
         if (var0.isLightweight()) {
            boolean var3 = var0 instanceof Container;
            if (!var3 || var3 && !((Container)var0).hasHeavyweightDescendants()) {
               return false;
            }
         }

         Container var5 = var1.getHeavyweightContainer();
         Container var4 = var2.getHeavyweightContainer();
         if (var5 != var4) {
            return !var0.peer.isReparentSupported();
         } else {
            return false;
         }
      }
   }

   public void setComponentZOrder(Component var1, int var2) {
      synchronized(this.getTreeLock()) {
         Container var4 = var1.parent;
         int var5 = this.getComponentZOrder(var1);
         if (var4 != this || var2 != var5) {
            this.checkAdding(var1, var2);
            boolean var6 = var4 != null ? var4.removeDelicately(var1, this, var2) : false;
            this.addDelicately(var1, var4, var2);
            if (!var6 && var5 != -1) {
               var1.mixOnZOrderChanging(var5, var2);
            }

         }
      }
   }

   private void reparentTraverse(ContainerPeer var1, Container var2) {
      this.checkTreeLock();

      for(int var3 = 0; var3 < var2.getComponentCount(); ++var3) {
         Component var4 = var2.getComponent(var3);
         if (var4.isLightweight()) {
            if (var4 instanceof Container) {
               this.reparentTraverse(var1, (Container)var4);
            }
         } else {
            var4.getPeer().reparent(var1);
         }
      }

   }

   private void reparentChild(Component var1) {
      this.checkTreeLock();
      if (var1 != null) {
         if (var1.isLightweight()) {
            if (var1 instanceof Container) {
               this.reparentTraverse((ContainerPeer)this.getPeer(), (Container)var1);
            }
         } else {
            var1.getPeer().reparent((ContainerPeer)this.getPeer());
         }

      }
   }

   private void addDelicately(Component var1, Container var2, int var3) {
      this.checkTreeLock();
      if (var2 != this) {
         if (var3 == -1) {
            this.component.add(var1);
         } else {
            this.component.add(var3, var1);
         }

         var1.parent = this;
         var1.setGraphicsConfiguration(this.getGraphicsConfiguration());
         this.adjustListeningChildren(32768L, var1.numListening(32768L));
         this.adjustListeningChildren(65536L, var1.numListening(65536L));
         this.adjustDescendants(var1.countHierarchyMembers());
      } else if (var3 < this.component.size()) {
         this.component.set(var3, var1);
      }

      this.invalidateIfValid();
      if (this.peer != null) {
         if (var1.peer == null) {
            var1.addNotify();
         } else {
            Container var4 = this.getHeavyweightContainer();
            Container var5 = var2.getHeavyweightContainer();
            if (var5 != var4) {
               var4.reparentChild(var1);
            }

            var1.updateZOrder();
            if (!var1.isLightweight() && this.isLightweight()) {
               var1.relocateComponent();
            }
         }
      }

      if (var2 != this) {
         if (this.layoutMgr != null) {
            if (this.layoutMgr instanceof LayoutManager2) {
               ((LayoutManager2)this.layoutMgr).addLayoutComponent(var1, (Object)null);
            } else {
               this.layoutMgr.addLayoutComponent((String)null, var1);
            }
         }

         if (this.containerListener != null || (this.eventMask & 2L) != 0L || Toolkit.enabledOnToolkit(2L)) {
            ContainerEvent var6 = new ContainerEvent(this, 300, var1);
            this.dispatchEvent(var6);
         }

         var1.createHierarchyEvents(1400, var1, this, 1L, Toolkit.enabledOnToolkit(32768L));
         if (var1.isFocusOwner() && !var1.canBeFocusOwnerRecursively()) {
            var1.transferFocus();
         } else if (var1 instanceof Container) {
            Component var7 = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
            if (var7 != null && this.isParentOf(var7) && !var7.canBeFocusOwnerRecursively()) {
               var7.transferFocus();
            }
         }
      } else {
         var1.createHierarchyEvents(1400, var1, this, 1400L, Toolkit.enabledOnToolkit(32768L));
      }

      if (this.peer != null && this.layoutMgr == null && this.isVisible()) {
         this.updateCursorImmediately();
      }

   }

   public int getComponentZOrder(Component var1) {
      if (var1 == null) {
         return -1;
      } else {
         synchronized(this.getTreeLock()) {
            return var1.parent != this ? -1 : this.component.indexOf(var1);
         }
      }
   }

   public void add(Component var1, Object var2) {
      this.addImpl(var1, var2, -1);
   }

   public void add(Component var1, Object var2, int var3) {
      this.addImpl(var1, var2, var3);
   }

   protected void addImpl(Component var1, Object var2, int var3) {
      synchronized(this.getTreeLock()) {
         GraphicsConfiguration var5 = this.getGraphicsConfiguration();
         if (var3 <= this.component.size() && (var3 >= 0 || var3 == -1)) {
            this.checkAddToSelf(var1);
            this.checkNotAWindow(var1);
            if (var1.parent != null) {
               var1.parent.remove(var1);
               if (var3 > this.component.size()) {
                  throw new IllegalArgumentException("illegal component position");
               }
            }

            if (var5 != null) {
               var1.checkGD(var5.getDevice().getIDstring());
            }

            if (var3 == -1) {
               this.component.add(var1);
            } else {
               this.component.add(var3, var1);
            }

            var1.parent = this;
            var1.setGraphicsConfiguration(var5);
            this.adjustListeningChildren(32768L, var1.numListening(32768L));
            this.adjustListeningChildren(65536L, var1.numListening(65536L));
            this.adjustDescendants(var1.countHierarchyMembers());
            this.invalidateIfValid();
            if (this.peer != null) {
               var1.addNotify();
            }

            if (this.layoutMgr != null) {
               if (this.layoutMgr instanceof LayoutManager2) {
                  ((LayoutManager2)this.layoutMgr).addLayoutComponent(var1, var2);
               } else if (var2 instanceof String) {
                  this.layoutMgr.addLayoutComponent((String)var2, var1);
               }
            }

            if (this.containerListener != null || (this.eventMask & 2L) != 0L || Toolkit.enabledOnToolkit(2L)) {
               ContainerEvent var6 = new ContainerEvent(this, 300, var1);
               this.dispatchEvent(var6);
            }

            var1.createHierarchyEvents(1400, var1, this, 1L, Toolkit.enabledOnToolkit(32768L));
            if (this.peer != null && this.layoutMgr == null && this.isVisible()) {
               this.updateCursorImmediately();
            }

         } else {
            throw new IllegalArgumentException("illegal component position");
         }
      }
   }

   boolean updateGraphicsData(GraphicsConfiguration var1) {
      this.checkTreeLock();
      boolean var2 = super.updateGraphicsData(var1);
      Iterator var3 = this.component.iterator();

      while(var3.hasNext()) {
         Component var4 = (Component)var3.next();
         if (var4 != null) {
            var2 |= var4.updateGraphicsData(var1);
         }
      }

      return var2;
   }

   void checkGD(String var1) {
      Iterator var2 = this.component.iterator();

      while(var2.hasNext()) {
         Component var3 = (Component)var2.next();
         if (var3 != null) {
            var3.checkGD(var1);
         }
      }

   }

   public void remove(int var1) {
      synchronized(this.getTreeLock()) {
         if (var1 >= 0 && var1 < this.component.size()) {
            Component var3 = (Component)this.component.get(var1);
            if (this.peer != null) {
               var3.removeNotify();
            }

            if (this.layoutMgr != null) {
               this.layoutMgr.removeLayoutComponent(var3);
            }

            this.adjustListeningChildren(32768L, -var3.numListening(32768L));
            this.adjustListeningChildren(65536L, -var3.numListening(65536L));
            this.adjustDescendants(-var3.countHierarchyMembers());
            var3.parent = null;
            this.component.remove(var1);
            var3.setGraphicsConfiguration((GraphicsConfiguration)null);
            this.invalidateIfValid();
            if (this.containerListener != null || (this.eventMask & 2L) != 0L || Toolkit.enabledOnToolkit(2L)) {
               ContainerEvent var4 = new ContainerEvent(this, 301, var3);
               this.dispatchEvent(var4);
            }

            var3.createHierarchyEvents(1400, var3, this, 1L, Toolkit.enabledOnToolkit(32768L));
            if (this.peer != null && this.layoutMgr == null && this.isVisible()) {
               this.updateCursorImmediately();
            }

         } else {
            throw new ArrayIndexOutOfBoundsException(var1);
         }
      }
   }

   public void remove(Component var1) {
      synchronized(this.getTreeLock()) {
         if (var1.parent == this) {
            int var3 = this.component.indexOf(var1);
            if (var3 >= 0) {
               this.remove(var3);
            }
         }

      }
   }

   public void removeAll() {
      synchronized(this.getTreeLock()) {
         this.adjustListeningChildren(32768L, -this.listeningChildren);
         this.adjustListeningChildren(65536L, -this.listeningBoundsChildren);
         this.adjustDescendants(-this.descendantsCount);

         Component var2;
         for(; !this.component.isEmpty(); var2.createHierarchyEvents(1400, var2, this, 1L, Toolkit.enabledOnToolkit(32768L))) {
            var2 = (Component)this.component.remove(this.component.size() - 1);
            if (this.peer != null) {
               var2.removeNotify();
            }

            if (this.layoutMgr != null) {
               this.layoutMgr.removeLayoutComponent(var2);
            }

            var2.parent = null;
            var2.setGraphicsConfiguration((GraphicsConfiguration)null);
            if (this.containerListener != null || (this.eventMask & 2L) != 0L || Toolkit.enabledOnToolkit(2L)) {
               ContainerEvent var3 = new ContainerEvent(this, 301, var2);
               this.dispatchEvent(var3);
            }
         }

         if (this.peer != null && this.layoutMgr == null && this.isVisible()) {
            this.updateCursorImmediately();
         }

         this.invalidateIfValid();
      }
   }

   int numListening(long var1) {
      int var3 = super.numListening(var1);
      int var4;
      Iterator var5;
      Component var6;
      if (var1 == 32768L) {
         if (eventLog.isLoggable(PlatformLogger.Level.FINE)) {
            var4 = 0;

            for(var5 = this.component.iterator(); var5.hasNext(); var4 += var6.numListening(var1)) {
               var6 = (Component)var5.next();
            }

            if (this.listeningChildren != var4) {
               eventLog.fine("Assertion (listeningChildren == sum) failed");
            }
         }

         return this.listeningChildren + var3;
      } else if (var1 != 65536L) {
         if (eventLog.isLoggable(PlatformLogger.Level.FINE)) {
            eventLog.fine("This code must never be reached");
         }

         return var3;
      } else {
         if (eventLog.isLoggable(PlatformLogger.Level.FINE)) {
            var4 = 0;

            for(var5 = this.component.iterator(); var5.hasNext(); var4 += var6.numListening(var1)) {
               var6 = (Component)var5.next();
            }

            if (this.listeningBoundsChildren != var4) {
               eventLog.fine("Assertion (listeningBoundsChildren == sum) failed");
            }
         }

         return this.listeningBoundsChildren + var3;
      }
   }

   void adjustListeningChildren(long var1, int var3) {
      if (eventLog.isLoggable(PlatformLogger.Level.FINE)) {
         boolean var4 = var1 == 32768L || var1 == 65536L || var1 == 98304L;
         if (!var4) {
            eventLog.fine("Assertion failed");
         }
      }

      if (var3 != 0) {
         if ((var1 & 32768L) != 0L) {
            this.listeningChildren += var3;
         }

         if ((var1 & 65536L) != 0L) {
            this.listeningBoundsChildren += var3;
         }

         this.adjustListeningChildrenOnParent(var1, var3);
      }
   }

   void adjustDescendants(int var1) {
      if (var1 != 0) {
         this.descendantsCount += var1;
         this.adjustDecendantsOnParent(var1);
      }
   }

   void adjustDecendantsOnParent(int var1) {
      if (this.parent != null) {
         this.parent.adjustDescendants(var1);
      }

   }

   int countHierarchyMembers() {
      if (log.isLoggable(PlatformLogger.Level.FINE)) {
         int var1 = 0;

         Component var3;
         for(Iterator var2 = this.component.iterator(); var2.hasNext(); var1 += var3.countHierarchyMembers()) {
            var3 = (Component)var2.next();
         }

         if (this.descendantsCount != var1) {
            log.fine("Assertion (descendantsCount == sum) failed");
         }
      }

      return this.descendantsCount + 1;
   }

   private int getListenersCount(int var1, boolean var2) {
      this.checkTreeLock();
      if (var2) {
         return this.descendantsCount;
      } else {
         switch(var1) {
         case 1400:
            return this.listeningChildren;
         case 1401:
         case 1402:
            return this.listeningBoundsChildren;
         default:
            return 0;
         }
      }
   }

   final int createHierarchyEvents(int var1, Component var2, Container var3, long var4, boolean var6) {
      this.checkTreeLock();
      int var7 = this.getListenersCount(var1, var6);
      int var8 = var7;

      for(int var9 = 0; var8 > 0; ++var9) {
         var8 -= ((Component)this.component.get(var9)).createHierarchyEvents(var1, var2, var3, var4, var6);
      }

      return var7 + super.createHierarchyEvents(var1, var2, var3, var4, var6);
   }

   final void createChildHierarchyEvents(int var1, long var2, boolean var4) {
      this.checkTreeLock();
      if (!this.component.isEmpty()) {
         int var5 = this.getListenersCount(var1, var4);
         int var6 = var5;

         for(int var7 = 0; var6 > 0; ++var7) {
            var6 -= ((Component)this.component.get(var7)).createHierarchyEvents(var1, this, this.parent, var2, var4);
         }

      }
   }

   public LayoutManager getLayout() {
      return this.layoutMgr;
   }

   public void setLayout(LayoutManager var1) {
      this.layoutMgr = var1;
      this.invalidateIfValid();
   }

   public void doLayout() {
      this.layout();
   }

   /** @deprecated */
   @Deprecated
   public void layout() {
      LayoutManager var1 = this.layoutMgr;
      if (var1 != null) {
         var1.layoutContainer(this);
      }

   }

   public boolean isValidateRoot() {
      return false;
   }

   void invalidateParent() {
      if (!isJavaAwtSmartInvalidate || !this.isValidateRoot()) {
         super.invalidateParent();
      }

   }

   public void invalidate() {
      LayoutManager var1 = this.layoutMgr;
      if (var1 instanceof LayoutManager2) {
         LayoutManager2 var2 = (LayoutManager2)var1;
         var2.invalidateLayout(this);
      }

      super.invalidate();
   }

   public void validate() {
      boolean var1 = false;
      synchronized(this.getTreeLock()) {
         if ((!this.isValid() || descendUnconditionallyWhenValidating) && this.peer != null) {
            ContainerPeer var3 = null;
            if (this.peer instanceof ContainerPeer) {
               var3 = (ContainerPeer)this.peer;
            }

            if (var3 != null) {
               var3.beginValidate();
            }

            this.validateTree();
            if (var3 != null) {
               var3.endValidate();
               if (!descendUnconditionallyWhenValidating) {
                  var1 = this.isVisible();
               }
            }
         }
      }

      if (var1) {
         this.updateCursorImmediately();
      }

   }

   final void validateUnconditionally() {
      boolean var1 = false;
      synchronized(this.getTreeLock()) {
         descendUnconditionallyWhenValidating = true;
         this.validate();
         if (this.peer instanceof ContainerPeer) {
            var1 = this.isVisible();
         }

         descendUnconditionallyWhenValidating = false;
      }

      if (var1) {
         this.updateCursorImmediately();
      }

   }

   protected void validateTree() {
      this.checkTreeLock();
      if (!this.isValid() || descendUnconditionallyWhenValidating) {
         if (this.peer instanceof ContainerPeer) {
            ((ContainerPeer)this.peer).beginLayout();
         }

         if (!this.isValid()) {
            this.doLayout();
         }

         for(int var1 = 0; var1 < this.component.size(); ++var1) {
            Component var2 = (Component)this.component.get(var1);
            if (!(var2 instanceof Container) || var2 instanceof Window || var2.isValid() && !descendUnconditionallyWhenValidating) {
               var2.validate();
            } else {
               ((Container)var2).validateTree();
            }
         }

         if (this.peer instanceof ContainerPeer) {
            ((ContainerPeer)this.peer).endLayout();
         }
      }

      super.validate();
   }

   void invalidateTree() {
      synchronized(this.getTreeLock()) {
         for(int var2 = 0; var2 < this.component.size(); ++var2) {
            Component var3 = (Component)this.component.get(var2);
            if (var3 instanceof Container) {
               ((Container)var3).invalidateTree();
            } else {
               var3.invalidateIfValid();
            }
         }

         this.invalidateIfValid();
      }
   }

   public void setFont(Font var1) {
      boolean var2 = false;
      Font var3 = this.getFont();
      super.setFont(var1);
      Font var4 = this.getFont();
      if (var4 != var3 && (var3 == null || !var3.equals(var4))) {
         this.invalidateTree();
      }

   }

   public Dimension getPreferredSize() {
      return this.preferredSize();
   }

   /** @deprecated */
   @Deprecated
   public Dimension preferredSize() {
      Dimension var1 = this.prefSize;
      if (var1 == null || !this.isPreferredSizeSet() && !this.isValid()) {
         synchronized(this.getTreeLock()) {
            this.prefSize = this.layoutMgr != null ? this.layoutMgr.preferredLayoutSize(this) : super.preferredSize();
            var1 = this.prefSize;
         }
      }

      return var1 != null ? new Dimension(var1) : var1;
   }

   public Dimension getMinimumSize() {
      return this.minimumSize();
   }

   /** @deprecated */
   @Deprecated
   public Dimension minimumSize() {
      Dimension var1 = this.minSize;
      if (var1 == null || !this.isMinimumSizeSet() && !this.isValid()) {
         synchronized(this.getTreeLock()) {
            this.minSize = this.layoutMgr != null ? this.layoutMgr.minimumLayoutSize(this) : super.minimumSize();
            var1 = this.minSize;
         }
      }

      return var1 != null ? new Dimension(var1) : var1;
   }

   public Dimension getMaximumSize() {
      Dimension var1 = this.maxSize;
      if (var1 == null || !this.isMaximumSizeSet() && !this.isValid()) {
         synchronized(this.getTreeLock()) {
            if (this.layoutMgr instanceof LayoutManager2) {
               LayoutManager2 var3 = (LayoutManager2)this.layoutMgr;
               this.maxSize = var3.maximumLayoutSize(this);
            } else {
               this.maxSize = super.getMaximumSize();
            }

            var1 = this.maxSize;
         }
      }

      return var1 != null ? new Dimension(var1) : var1;
   }

   public float getAlignmentX() {
      float var1;
      if (this.layoutMgr instanceof LayoutManager2) {
         synchronized(this.getTreeLock()) {
            LayoutManager2 var3 = (LayoutManager2)this.layoutMgr;
            var1 = var3.getLayoutAlignmentX(this);
         }
      } else {
         var1 = super.getAlignmentX();
      }

      return var1;
   }

   public float getAlignmentY() {
      float var1;
      if (this.layoutMgr instanceof LayoutManager2) {
         synchronized(this.getTreeLock()) {
            LayoutManager2 var3 = (LayoutManager2)this.layoutMgr;
            var1 = var3.getLayoutAlignmentY(this);
         }
      } else {
         var1 = super.getAlignmentY();
      }

      return var1;
   }

   public void paint(Graphics var1) {
      if (this.isShowing()) {
         synchronized(this.getObjectLock()) {
            if (this.printing && this.printingThreads.contains(Thread.currentThread())) {
               return;
            }
         }

         GraphicsCallback.PaintCallback.getInstance().runComponents(this.getComponentsSync(), var1, 2);
      }

   }

   public void update(Graphics var1) {
      if (this.isShowing()) {
         if (!(this.peer instanceof LightweightPeer)) {
            var1.clearRect(0, 0, this.width, this.height);
         }

         this.paint(var1);
      }

   }

   public void print(Graphics var1) {
      if (this.isShowing()) {
         Thread var2 = Thread.currentThread();
         boolean var13 = false;

         try {
            var13 = true;
            synchronized(this.getObjectLock()) {
               if (this.printingThreads == null) {
                  this.printingThreads = new HashSet();
               }

               this.printingThreads.add(var2);
               this.printing = true;
            }

            super.print(var1);
            var13 = false;
         } finally {
            if (var13) {
               synchronized(this.getObjectLock()) {
                  this.printingThreads.remove(var2);
                  this.printing = !this.printingThreads.isEmpty();
               }
            }
         }

         synchronized(this.getObjectLock()) {
            this.printingThreads.remove(var2);
            this.printing = !this.printingThreads.isEmpty();
         }

         GraphicsCallback.PrintCallback.getInstance().runComponents(this.getComponentsSync(), var1, 2);
      }

   }

   public void paintComponents(Graphics var1) {
      if (this.isShowing()) {
         GraphicsCallback.PaintAllCallback.getInstance().runComponents(this.getComponentsSync(), var1, 4);
      }

   }

   void lightweightPaint(Graphics var1) {
      super.lightweightPaint(var1);
      this.paintHeavyweightComponents(var1);
   }

   void paintHeavyweightComponents(Graphics var1) {
      if (this.isShowing()) {
         GraphicsCallback.PaintHeavyweightComponentsCallback.getInstance().runComponents(this.getComponentsSync(), var1, 3);
      }

   }

   public void printComponents(Graphics var1) {
      if (this.isShowing()) {
         GraphicsCallback.PrintAllCallback.getInstance().runComponents(this.getComponentsSync(), var1, 4);
      }

   }

   void lightweightPrint(Graphics var1) {
      super.lightweightPrint(var1);
      this.printHeavyweightComponents(var1);
   }

   void printHeavyweightComponents(Graphics var1) {
      if (this.isShowing()) {
         GraphicsCallback.PrintHeavyweightComponentsCallback.getInstance().runComponents(this.getComponentsSync(), var1, 3);
      }

   }

   public synchronized void addContainerListener(ContainerListener var1) {
      if (var1 != null) {
         this.containerListener = AWTEventMulticaster.add(this.containerListener, var1);
         this.newEventsOnly = true;
      }
   }

   public synchronized void removeContainerListener(ContainerListener var1) {
      if (var1 != null) {
         this.containerListener = AWTEventMulticaster.remove(this.containerListener, var1);
      }
   }

   public synchronized ContainerListener[] getContainerListeners() {
      return (ContainerListener[])this.getListeners(ContainerListener.class);
   }

   public <T extends EventListener> T[] getListeners(Class<T> var1) {
      ContainerListener var2 = null;
      if (var1 == ContainerListener.class) {
         var2 = this.containerListener;
         return AWTEventMulticaster.getListeners(var2, var1);
      } else {
         return super.getListeners(var1);
      }
   }

   boolean eventEnabled(AWTEvent var1) {
      int var2 = var1.getID();
      if (var2 != 300 && var2 != 301) {
         return super.eventEnabled(var1);
      } else {
         return (this.eventMask & 2L) != 0L || this.containerListener != null;
      }
   }

   protected void processEvent(AWTEvent var1) {
      if (var1 instanceof ContainerEvent) {
         this.processContainerEvent((ContainerEvent)var1);
      } else {
         super.processEvent(var1);
      }
   }

   protected void processContainerEvent(ContainerEvent var1) {
      ContainerListener var2 = this.containerListener;
      if (var2 != null) {
         switch(var1.getID()) {
         case 300:
            var2.componentAdded(var1);
            break;
         case 301:
            var2.componentRemoved(var1);
         }
      }

   }

   void dispatchEventImpl(AWTEvent var1) {
      if (this.dispatcher != null && this.dispatcher.dispatchEvent(var1)) {
         var1.consume();
         if (this.peer != null) {
            this.peer.handleEvent(var1);
         }

      } else {
         super.dispatchEventImpl(var1);
         synchronized(this.getTreeLock()) {
            switch(var1.getID()) {
            case 100:
               this.createChildHierarchyEvents(1401, 0L, Toolkit.enabledOnToolkit(65536L));
               break;
            case 101:
               this.createChildHierarchyEvents(1402, 0L, Toolkit.enabledOnToolkit(65536L));
            }

         }
      }
   }

   void dispatchEventToSelf(AWTEvent var1) {
      super.dispatchEventImpl(var1);
   }

   Component getMouseEventTarget(int var1, int var2, boolean var3) {
      return this.getMouseEventTarget(var1, var2, var3, Container.MouseEventTargetFilter.FILTER, false);
   }

   Component getDropTargetEventTarget(int var1, int var2, boolean var3) {
      return this.getMouseEventTarget(var1, var2, var3, Container.DropTargetEventTargetFilter.FILTER, true);
   }

   private Component getMouseEventTarget(int var1, int var2, boolean var3, Container.EventTargetFilter var4, boolean var5) {
      Component var6 = null;
      if (var5) {
         var6 = this.getMouseEventTargetImpl(var1, var2, var3, var4, true, var5);
      }

      if (var6 == null || var6 == this) {
         var6 = this.getMouseEventTargetImpl(var1, var2, var3, var4, false, var5);
      }

      return var6;
   }

   private Component getMouseEventTargetImpl(int var1, int var2, boolean var3, Container.EventTargetFilter var4, boolean var5, boolean var6) {
      synchronized(this.getTreeLock()) {
         for(int var8 = 0; var8 < this.component.size(); ++var8) {
            Component var9 = (Component)this.component.get(var8);
            if (var9 != null && var9.visible && (!var5 && var9.peer instanceof LightweightPeer || var5 && !(var9.peer instanceof LightweightPeer)) && var9.contains(var1 - var9.x, var2 - var9.y)) {
               if (var9 instanceof Container) {
                  Container var10 = (Container)var9;
                  Component var11 = var10.getMouseEventTarget(var1 - var10.x, var2 - var10.y, var3, var4, var6);
                  if (var11 != null) {
                     return var11;
                  }
               } else if (var4.accept(var9)) {
                  return var9;
               }
            }
         }

         boolean var14 = this.peer instanceof LightweightPeer || var3;
         boolean var15 = this.contains(var1, var2);
         if (var15 && var14 && var4.accept(this)) {
            return this;
         } else {
            return null;
         }
      }
   }

   void proxyEnableEvents(long var1) {
      if (this.peer instanceof LightweightPeer) {
         if (this.parent != null) {
            this.parent.proxyEnableEvents(var1);
         }
      } else if (this.dispatcher != null) {
         this.dispatcher.enableEvents(var1);
      }

   }

   /** @deprecated */
   @Deprecated
   public void deliverEvent(Event var1) {
      Component var2 = this.getComponentAt(var1.x, var1.y);
      if (var2 != null && var2 != this) {
         var1.translate(-var2.x, -var2.y);
         var2.deliverEvent(var1);
      } else {
         this.postEvent(var1);
      }

   }

   public Component getComponentAt(int var1, int var2) {
      return this.locate(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   public Component locate(int var1, int var2) {
      if (!this.contains(var1, var2)) {
         return null;
      } else {
         Component var3 = null;
         synchronized(this.getTreeLock()) {
            Iterator var5 = this.component.iterator();

            while(var5.hasNext()) {
               Component var6 = (Component)var5.next();
               if (var6.contains(var1 - var6.x, var2 - var6.y)) {
                  if (!var6.isLightweight()) {
                     return var6;
                  }

                  if (var3 == null) {
                     var3 = var6;
                  }
               }
            }

            return (Component)(var3 != null ? var3 : this);
         }
      }
   }

   public Component getComponentAt(Point var1) {
      return this.getComponentAt(var1.x, var1.y);
   }

   public Point getMousePosition(boolean var1) throws HeadlessException {
      if (GraphicsEnvironment.isHeadless()) {
         throw new HeadlessException();
      } else {
         PointerInfo var2 = (PointerInfo)AccessController.doPrivileged(new PrivilegedAction<PointerInfo>() {
            public PointerInfo run() {
               return MouseInfo.getPointerInfo();
            }
         });
         synchronized(this.getTreeLock()) {
            Component var4 = this.findUnderMouseInWindow(var2);
            return this.isSameOrAncestorOf(var4, var1) ? this.pointRelativeToComponent(var2.getLocation()) : null;
         }
      }
   }

   boolean isSameOrAncestorOf(Component var1, boolean var2) {
      return this == var1 || var2 && this.isParentOf(var1);
   }

   public Component findComponentAt(int var1, int var2) {
      return this.findComponentAt(var1, var2, true);
   }

   final Component findComponentAt(int var1, int var2, boolean var3) {
      synchronized(this.getTreeLock()) {
         return this.isRecursivelyVisible() ? this.findComponentAtImpl(var1, var2, var3) : null;
      }
   }

   final Component findComponentAtImpl(int var1, int var2, boolean var3) {
      if (!this.contains(var1, var2) || !this.visible || !var3 && !this.enabled) {
         return null;
      } else {
         Component var4 = null;
         Iterator var5 = this.component.iterator();

         while(var5.hasNext()) {
            Component var6 = (Component)var5.next();
            int var7 = var1 - var6.x;
            int var8 = var2 - var6.y;
            if (var6.contains(var7, var8)) {
               if (!var6.isLightweight()) {
                  Component var9 = getChildAt(var6, var7, var8, var3);
                  if (var9 != null) {
                     return var9;
                  }
               } else if (var4 == null) {
                  var4 = getChildAt(var6, var7, var8, var3);
               }
            }
         }

         return (Component)(var4 != null ? var4 : this);
      }
   }

   private static Component getChildAt(Component var0, int var1, int var2, boolean var3) {
      if (var0 instanceof Container) {
         var0 = ((Container)var0).findComponentAtImpl(var1, var2, var3);
      } else {
         var0 = var0.getComponentAt(var1, var2);
      }

      return var0 == null || !var0.visible || !var3 && !var0.enabled ? null : var0;
   }

   public Component findComponentAt(Point var1) {
      return this.findComponentAt(var1.x, var1.y);
   }

   public void addNotify() {
      synchronized(this.getTreeLock()) {
         super.addNotify();
         if (!(this.peer instanceof LightweightPeer)) {
            this.dispatcher = new LightweightDispatcher(this);
         }

         for(int var2 = 0; var2 < this.component.size(); ++var2) {
            ((Component)this.component.get(var2)).addNotify();
         }

      }
   }

   public void removeNotify() {
      synchronized(this.getTreeLock()) {
         for(int var2 = this.component.size() - 1; var2 >= 0; --var2) {
            Component var3 = (Component)this.component.get(var2);
            if (var3 != null) {
               var3.setAutoFocusTransferOnDisposal(false);
               var3.removeNotify();
               var3.setAutoFocusTransferOnDisposal(true);
            }
         }

         if (this.containsFocus() && KeyboardFocusManager.isAutoFocusTransferEnabledFor(this) && !this.transferFocus(false)) {
            this.transferFocusBackward(true);
         }

         if (this.dispatcher != null) {
            this.dispatcher.dispose();
            this.dispatcher = null;
         }

         super.removeNotify();
      }
   }

   public boolean isAncestorOf(Component var1) {
      Container var2;
      if (var1 != null && (var2 = var1.getParent()) != null) {
         while(var2 != null) {
            if (var2 == this) {
               return true;
            }

            var2 = var2.getParent();
         }

         return false;
      } else {
         return false;
      }
   }

   private void startLWModal() {
      this.modalAppContext = AppContext.getAppContext();
      long var1 = Toolkit.getEventQueue().getMostRecentKeyEventTime();
      Component var3 = Component.isInstanceOf(this, "javax.swing.JInternalFrame") ? ((JInternalFrame)((JInternalFrame)this)).getMostRecentFocusOwner() : null;
      if (var3 != null) {
         KeyboardFocusManager.getCurrentKeyboardFocusManager().enqueueKeyEvents(var1, var3);
      }

      final Container var4;
      synchronized(this.getTreeLock()) {
         var4 = this.getHeavyweightContainer();
         if (var4.modalComp != null) {
            this.modalComp = var4.modalComp;
            var4.modalComp = this;
            return;
         }

         var4.modalComp = this;
      }

      Runnable var5 = new Runnable() {
         public void run() {
            EventDispatchThread var1 = (EventDispatchThread)Thread.currentThread();
            var1.pumpEventsForHierarchy(new Conditional() {
               public boolean evaluate() {
                  return Container.this.windowClosingException == null && var4.modalComp != null;
               }
            }, Container.this);
         }
      };
      if (EventQueue.isDispatchThread()) {
         SequencedEvent var6 = KeyboardFocusManager.getCurrentKeyboardFocusManager().getCurrentSequencedEvent();
         if (var6 != null) {
            var6.dispose();
         }

         var5.run();
      } else {
         synchronized(this.getTreeLock()) {
            Toolkit.getEventQueue().postEvent(new PeerEvent(this, var5, 1L));

            while(this.windowClosingException == null && var4.modalComp != null) {
               try {
                  this.getTreeLock().wait();
               } catch (InterruptedException var9) {
                  break;
               }
            }
         }
      }

      if (this.windowClosingException != null) {
         this.windowClosingException.fillInStackTrace();
         throw this.windowClosingException;
      } else {
         if (var3 != null) {
            KeyboardFocusManager.getCurrentKeyboardFocusManager().dequeueKeyEvents(var1, var3);
         }

      }
   }

   private void stopLWModal() {
      synchronized(this.getTreeLock()) {
         if (this.modalAppContext != null) {
            Container var2 = this.getHeavyweightContainer();
            if (var2 != null) {
               if (this.modalComp != null) {
                  var2.modalComp = this.modalComp;
                  this.modalComp = null;
                  return;
               }

               var2.modalComp = null;
            }

            SunToolkit.postEvent(this.modalAppContext, new PeerEvent(this, new Container.WakingRunnable(), 1L));
         }

         EventQueue.invokeLater(new Container.WakingRunnable());
         this.getTreeLock().notifyAll();
      }
   }

   protected String paramString() {
      String var1 = super.paramString();
      LayoutManager var2 = this.layoutMgr;
      if (var2 != null) {
         var1 = var1 + ",layout=" + var2.getClass().getName();
      }

      return var1;
   }

   public void list(PrintStream var1, int var2) {
      super.list(var1, var2);
      synchronized(this.getTreeLock()) {
         for(int var4 = 0; var4 < this.component.size(); ++var4) {
            Component var5 = (Component)this.component.get(var4);
            if (var5 != null) {
               var5.list(var1, var2 + 1);
            }
         }

      }
   }

   public void list(PrintWriter var1, int var2) {
      super.list(var1, var2);
      synchronized(this.getTreeLock()) {
         for(int var4 = 0; var4 < this.component.size(); ++var4) {
            Component var5 = (Component)this.component.get(var4);
            if (var5 != null) {
               var5.list(var1, var2 + 1);
            }
         }

      }
   }

   public void setFocusTraversalKeys(int var1, Set<? extends AWTKeyStroke> var2) {
      if (var1 >= 0 && var1 < 4) {
         this.setFocusTraversalKeys_NoIDCheck(var1, var2);
      } else {
         throw new IllegalArgumentException("invalid focus traversal key identifier");
      }
   }

   public Set<AWTKeyStroke> getFocusTraversalKeys(int var1) {
      if (var1 >= 0 && var1 < 4) {
         return this.getFocusTraversalKeys_NoIDCheck(var1);
      } else {
         throw new IllegalArgumentException("invalid focus traversal key identifier");
      }
   }

   public boolean areFocusTraversalKeysSet(int var1) {
      if (var1 >= 0 && var1 < 4) {
         return this.focusTraversalKeys != null && this.focusTraversalKeys[var1] != null;
      } else {
         throw new IllegalArgumentException("invalid focus traversal key identifier");
      }
   }

   public boolean isFocusCycleRoot(Container var1) {
      return this.isFocusCycleRoot() && var1 == this ? true : super.isFocusCycleRoot(var1);
   }

   private Container findTraversalRoot() {
      Container var1 = KeyboardFocusManager.getCurrentKeyboardFocusManager().getCurrentFocusCycleRoot();
      Container var2;
      if (var1 == this) {
         var2 = this;
      } else {
         var2 = this.getFocusCycleRootAncestor();
         if (var2 == null) {
            var2 = this;
         }
      }

      if (var2 != var1) {
         KeyboardFocusManager.getCurrentKeyboardFocusManager().setGlobalCurrentFocusCycleRootPriv(var2);
      }

      return var2;
   }

   final boolean containsFocus() {
      Component var1 = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
      return this.isParentOf(var1);
   }

   private boolean isParentOf(Component var1) {
      synchronized(this.getTreeLock()) {
         while(var1 != null && var1 != this && !(var1 instanceof Window)) {
            var1 = ((Component)var1).getParent();
         }

         return var1 == this;
      }
   }

   void clearMostRecentFocusOwnerOnHide() {
      boolean var1 = false;
      Window var2 = null;
      synchronized(this.getTreeLock()) {
         var2 = this.getContainingWindow();
         if (var2 != null) {
            Component var4 = KeyboardFocusManager.getMostRecentFocusOwner(var2);
            var1 = var4 == this || this.isParentOf(var4);
            Class var5 = KeyboardFocusManager.class;
            synchronized(KeyboardFocusManager.class) {
               Component var6 = var2.getTemporaryLostComponent();
               if (this.isParentOf(var6) || var6 == this) {
                  var2.setTemporaryLostComponent((Component)null);
               }
            }
         }
      }

      if (var1) {
         KeyboardFocusManager.setMostRecentFocusOwner(var2, (Component)null);
      }

   }

   void clearCurrentFocusCycleRootOnHide() {
      KeyboardFocusManager var1 = KeyboardFocusManager.getCurrentKeyboardFocusManager();
      Container var2 = var1.getCurrentFocusCycleRoot();
      if (var2 == this || this.isParentOf(var2)) {
         var1.setGlobalCurrentFocusCycleRootPriv((Container)null);
      }

   }

   final Container getTraversalRoot() {
      return this.isFocusCycleRoot() ? this.findTraversalRoot() : super.getTraversalRoot();
   }

   public void setFocusTraversalPolicy(FocusTraversalPolicy var1) {
      FocusTraversalPolicy var2;
      synchronized(this) {
         var2 = this.focusTraversalPolicy;
         this.focusTraversalPolicy = var1;
      }

      this.firePropertyChange("focusTraversalPolicy", var2, var1);
   }

   public FocusTraversalPolicy getFocusTraversalPolicy() {
      if (!this.isFocusTraversalPolicyProvider() && !this.isFocusCycleRoot()) {
         return null;
      } else {
         FocusTraversalPolicy var1 = this.focusTraversalPolicy;
         if (var1 != null) {
            return var1;
         } else {
            Container var2 = this.getFocusCycleRootAncestor();
            return var2 != null ? var2.getFocusTraversalPolicy() : KeyboardFocusManager.getCurrentKeyboardFocusManager().getDefaultFocusTraversalPolicy();
         }
      }
   }

   public boolean isFocusTraversalPolicySet() {
      return this.focusTraversalPolicy != null;
   }

   public void setFocusCycleRoot(boolean var1) {
      boolean var2;
      synchronized(this) {
         var2 = this.focusCycleRoot;
         this.focusCycleRoot = var1;
      }

      this.firePropertyChange("focusCycleRoot", var2, var1);
   }

   public boolean isFocusCycleRoot() {
      return this.focusCycleRoot;
   }

   public final void setFocusTraversalPolicyProvider(boolean var1) {
      boolean var2;
      synchronized(this) {
         var2 = this.focusTraversalPolicyProvider;
         this.focusTraversalPolicyProvider = var1;
      }

      this.firePropertyChange("focusTraversalPolicyProvider", var2, var1);
   }

   public final boolean isFocusTraversalPolicyProvider() {
      return this.focusTraversalPolicyProvider;
   }

   public void transferFocusDownCycle() {
      if (this.isFocusCycleRoot()) {
         KeyboardFocusManager.getCurrentKeyboardFocusManager().setGlobalCurrentFocusCycleRootPriv(this);
         Component var1 = this.getFocusTraversalPolicy().getDefaultComponent(this);
         if (var1 != null) {
            var1.requestFocus(CausedFocusEvent.Cause.TRAVERSAL_DOWN);
         }
      }

   }

   void preProcessKeyEvent(KeyEvent var1) {
      Container var2 = this.parent;
      if (var2 != null) {
         var2.preProcessKeyEvent(var1);
      }

   }

   void postProcessKeyEvent(KeyEvent var1) {
      Container var2 = this.parent;
      if (var2 != null) {
         var2.postProcessKeyEvent(var1);
      }

   }

   boolean postsOldMouseEvents() {
      return true;
   }

   public void applyComponentOrientation(ComponentOrientation var1) {
      super.applyComponentOrientation(var1);
      synchronized(this.getTreeLock()) {
         for(int var3 = 0; var3 < this.component.size(); ++var3) {
            Component var4 = (Component)this.component.get(var3);
            var4.applyComponentOrientation(var1);
         }

      }
   }

   public void addPropertyChangeListener(PropertyChangeListener var1) {
      super.addPropertyChangeListener(var1);
   }

   public void addPropertyChangeListener(String var1, PropertyChangeListener var2) {
      super.addPropertyChangeListener(var1, var2);
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      ObjectOutputStream.PutField var2 = var1.putFields();
      var2.put("ncomponents", this.component.size());
      var2.put("component", this.component.toArray(EMPTY_ARRAY));
      var2.put("layoutMgr", this.layoutMgr);
      var2.put("dispatcher", this.dispatcher);
      var2.put("maxSize", this.maxSize);
      var2.put("focusCycleRoot", this.focusCycleRoot);
      var2.put("containerSerializedDataVersion", this.containerSerializedDataVersion);
      var2.put("focusTraversalPolicyProvider", this.focusTraversalPolicyProvider);
      var1.writeFields();
      AWTEventMulticaster.save(var1, "containerL", this.containerListener);
      var1.writeObject((Object)null);
      if (this.focusTraversalPolicy instanceof Serializable) {
         var1.writeObject(this.focusTraversalPolicy);
      } else {
         var1.writeObject((Object)null);
      }

   }

   private void readObject(ObjectInputStream var1) throws ClassNotFoundException, IOException {
      ObjectInputStream.GetField var2 = var1.readFields();
      Component[] var3 = (Component[])((Component[])var2.get("component", (Object)null));
      if (var3 == null) {
         var3 = EMPTY_ARRAY;
      }

      int var4 = Integer.valueOf(var2.get("ncomponents", (int)0));
      if (var4 >= 0 && var4 <= var3.length) {
         this.component = new ArrayList(var4);

         for(int var5 = 0; var5 < var4; ++var5) {
            this.component.add(var3[var5]);
         }

         this.layoutMgr = (LayoutManager)var2.get("layoutMgr", (Object)null);
         this.dispatcher = (LightweightDispatcher)var2.get("dispatcher", (Object)null);
         if (this.maxSize == null) {
            this.maxSize = (Dimension)var2.get("maxSize", (Object)null);
         }

         this.focusCycleRoot = var2.get("focusCycleRoot", false);
         this.containerSerializedDataVersion = var2.get("containerSerializedDataVersion", (int)1);
         this.focusTraversalPolicyProvider = var2.get("focusTraversalPolicyProvider", false);
         java.util.List var9 = this.component;
         Iterator var6 = var9.iterator();

         while(var6.hasNext()) {
            Component var7 = (Component)var6.next();
            var7.parent = this;
            this.adjustListeningChildren(32768L, var7.numListening(32768L));
            this.adjustListeningChildren(65536L, var7.numListening(65536L));
            this.adjustDescendants(var7.countHierarchyMembers());
         }

         Object var10;
         while(null != (var10 = var1.readObject())) {
            String var11 = ((String)var10).intern();
            if ("containerL" == var11) {
               this.addContainerListener((ContainerListener)((ContainerListener)var1.readObject()));
            } else {
               var1.readObject();
            }
         }

         try {
            Object var12 = var1.readObject();
            if (var12 instanceof FocusTraversalPolicy) {
               this.focusTraversalPolicy = (FocusTraversalPolicy)var12;
            }
         } catch (OptionalDataException var8) {
            if (!var8.eof) {
               throw var8;
            }
         }

      } else {
         throw new InvalidObjectException("Incorrect number of components");
      }
   }

   Accessible getAccessibleAt(Point var1) {
      synchronized(this.getTreeLock()) {
         if (this instanceof Accessible) {
            Accessible var12 = (Accessible)this;
            AccessibleContext var13 = var12.getAccessibleContext();
            if (var13 != null) {
               int var16 = var13.getAccessibleChildrenCount();

               for(int var8 = 0; var8 < var16; ++var8) {
                  var12 = var13.getAccessibleChild(var8);
                  if (var12 != null) {
                     var13 = var12.getAccessibleContext();
                     if (var13 != null) {
                        AccessibleComponent var14 = var13.getAccessibleComponent();
                        if (var14 != null && var14.isShowing()) {
                           Point var15 = var14.getLocation();
                           Point var9 = new Point(var1.x - var15.x, var1.y - var15.y);
                           if (var14.contains(var9)) {
                              return var12;
                           }
                        }
                     }
                  }
               }
            }

            return (Accessible)this;
         } else {
            Object var3 = this;
            if (!this.contains(var1.x, var1.y)) {
               var3 = null;
            } else {
               int var4 = this.getComponentCount();

               for(int var5 = 0; var5 < var4; ++var5) {
                  Component var6 = this.getComponent(var5);
                  if (var6 != null && var6.isShowing()) {
                     Point var7 = var6.getLocation();
                     if (var6.contains(var1.x - var7.x, var1.y - var7.y)) {
                        var3 = var6;
                     }
                  }
               }
            }

            return var3 instanceof Accessible ? (Accessible)var3 : null;
         }
      }
   }

   int getAccessibleChildrenCount() {
      synchronized(this.getTreeLock()) {
         int var2 = 0;
         Component[] var3 = this.getComponents();

         for(int var4 = 0; var4 < var3.length; ++var4) {
            if (var3[var4] instanceof Accessible) {
               ++var2;
            }
         }

         return var2;
      }
   }

   Accessible getAccessibleChild(int var1) {
      synchronized(this.getTreeLock()) {
         Component[] var3 = this.getComponents();
         int var4 = 0;

         for(int var5 = 0; var5 < var3.length; ++var5) {
            if (var3[var5] instanceof Accessible) {
               if (var4 == var1) {
                  return (Accessible)var3[var5];
               }

               ++var4;
            }
         }

         return null;
      }
   }

   final void increaseComponentCount(Component var1) {
      synchronized(this.getTreeLock()) {
         if (!var1.isDisplayable()) {
            throw new IllegalStateException("Peer does not exist while invoking the increaseComponentCount() method");
         } else {
            int var3 = 0;
            int var4 = 0;
            if (var1 instanceof Container) {
               var4 = ((Container)var1).numOfLWComponents;
               var3 = ((Container)var1).numOfHWComponents;
            }

            if (var1.isLightweight()) {
               ++var4;
            } else {
               ++var3;
            }

            for(Container var5 = this; var5 != null; var5 = var5.getContainer()) {
               var5.numOfLWComponents += var4;
               var5.numOfHWComponents += var3;
            }

         }
      }
   }

   final void decreaseComponentCount(Component var1) {
      synchronized(this.getTreeLock()) {
         if (!var1.isDisplayable()) {
            throw new IllegalStateException("Peer does not exist while invoking the decreaseComponentCount() method");
         } else {
            int var3 = 0;
            int var4 = 0;
            if (var1 instanceof Container) {
               var4 = ((Container)var1).numOfLWComponents;
               var3 = ((Container)var1).numOfHWComponents;
            }

            if (var1.isLightweight()) {
               ++var4;
            } else {
               ++var3;
            }

            for(Container var5 = this; var5 != null; var5 = var5.getContainer()) {
               var5.numOfLWComponents -= var4;
               var5.numOfHWComponents -= var3;
            }

         }
      }
   }

   private int getTopmostComponentIndex() {
      this.checkTreeLock();
      return this.getComponentCount() > 0 ? 0 : -1;
   }

   private int getBottommostComponentIndex() {
      this.checkTreeLock();
      return this.getComponentCount() > 0 ? this.getComponentCount() - 1 : -1;
   }

   final Region getOpaqueShape() {
      this.checkTreeLock();
      if (this.isLightweight() && this.isNonOpaqueForMixing() && this.hasLightweightDescendants()) {
         Region var1 = Region.EMPTY_REGION;

         for(int var2 = 0; var2 < this.getComponentCount(); ++var2) {
            Component var3 = this.getComponent(var2);
            if (var3.isLightweight() && var3.isShowing()) {
               var1 = var1.getUnion(var3.getOpaqueShape());
            }
         }

         return var1.getIntersection(this.getNormalShape());
      } else {
         return super.getOpaqueShape();
      }
   }

   final void recursiveSubtractAndApplyShape(Region var1) {
      this.recursiveSubtractAndApplyShape(var1, this.getTopmostComponentIndex(), this.getBottommostComponentIndex());
   }

   final void recursiveSubtractAndApplyShape(Region var1, int var2) {
      this.recursiveSubtractAndApplyShape(var1, var2, this.getBottommostComponentIndex());
   }

   final void recursiveSubtractAndApplyShape(Region var1, int var2, int var3) {
      this.checkTreeLock();
      if (mixingLog.isLoggable(PlatformLogger.Level.FINE)) {
         mixingLog.fine("this = " + this + "; shape=" + var1 + "; fromZ=" + var2 + "; toZ=" + var3);
      }

      if (var2 != -1) {
         if (!var1.isEmpty()) {
            if (this.getLayout() == null || this.isValid()) {
               for(int var4 = var2; var4 <= var3; ++var4) {
                  Component var5 = this.getComponent(var4);
                  if (!var5.isLightweight()) {
                     var5.subtractAndApplyShape(var1);
                  } else if (var5 instanceof Container && ((Container)var5).hasHeavyweightDescendants() && var5.isShowing()) {
                     ((Container)var5).recursiveSubtractAndApplyShape(var1);
                  }
               }

            }
         }
      }
   }

   final void recursiveApplyCurrentShape() {
      this.recursiveApplyCurrentShape(this.getTopmostComponentIndex(), this.getBottommostComponentIndex());
   }

   final void recursiveApplyCurrentShape(int var1) {
      this.recursiveApplyCurrentShape(var1, this.getBottommostComponentIndex());
   }

   final void recursiveApplyCurrentShape(int var1, int var2) {
      this.checkTreeLock();
      if (mixingLog.isLoggable(PlatformLogger.Level.FINE)) {
         mixingLog.fine("this = " + this + "; fromZ=" + var1 + "; toZ=" + var2);
      }

      if (var1 != -1) {
         if (this.getLayout() == null || this.isValid()) {
            for(int var3 = var1; var3 <= var2; ++var3) {
               Component var4 = this.getComponent(var3);
               if (!var4.isLightweight()) {
                  var4.applyCurrentShape();
               }

               if (var4 instanceof Container && ((Container)var4).hasHeavyweightDescendants()) {
                  ((Container)var4).recursiveApplyCurrentShape();
               }
            }

         }
      }
   }

   private void recursiveShowHeavyweightChildren() {
      if (this.hasHeavyweightDescendants() && this.isVisible()) {
         for(int var1 = 0; var1 < this.getComponentCount(); ++var1) {
            Component var2 = this.getComponent(var1);
            if (var2.isLightweight()) {
               if (var2 instanceof Container) {
                  ((Container)var2).recursiveShowHeavyweightChildren();
               }
            } else if (var2.isVisible()) {
               ComponentPeer var3 = var2.getPeer();
               if (var3 != null) {
                  var3.setVisible(true);
               }
            }
         }

      }
   }

   private void recursiveHideHeavyweightChildren() {
      if (this.hasHeavyweightDescendants()) {
         for(int var1 = 0; var1 < this.getComponentCount(); ++var1) {
            Component var2 = this.getComponent(var1);
            if (var2.isLightweight()) {
               if (var2 instanceof Container) {
                  ((Container)var2).recursiveHideHeavyweightChildren();
               }
            } else if (var2.isVisible()) {
               ComponentPeer var3 = var2.getPeer();
               if (var3 != null) {
                  var3.setVisible(false);
               }
            }
         }

      }
   }

   private void recursiveRelocateHeavyweightChildren(Point var1) {
      for(int var2 = 0; var2 < this.getComponentCount(); ++var2) {
         Component var3 = this.getComponent(var2);
         if (var3.isLightweight()) {
            if (var3 instanceof Container && ((Container)var3).hasHeavyweightDescendants()) {
               Point var4 = new Point(var1);
               var4.translate(var3.getX(), var3.getY());
               ((Container)var3).recursiveRelocateHeavyweightChildren(var4);
            }
         } else {
            ComponentPeer var5 = var3.getPeer();
            if (var5 != null) {
               var5.setBounds(var1.x + var3.getX(), var1.y + var3.getY(), var3.getWidth(), var3.getHeight(), 1);
            }
         }
      }

   }

   final boolean isRecursivelyVisibleUpToHeavyweightContainer() {
      if (!this.isLightweight()) {
         return true;
      } else {
         for(Container var1 = this; var1 != null && var1.isLightweight(); var1 = var1.getContainer()) {
            if (!var1.isVisible()) {
               return false;
            }
         }

         return true;
      }
   }

   void mixOnShowing() {
      synchronized(this.getTreeLock()) {
         if (mixingLog.isLoggable(PlatformLogger.Level.FINE)) {
            mixingLog.fine("this = " + this);
         }

         boolean var2 = this.isLightweight();
         if (var2 && this.isRecursivelyVisibleUpToHeavyweightContainer()) {
            this.recursiveShowHeavyweightChildren();
         }

         if (this.isMixingNeeded()) {
            if (!var2 || var2 && this.hasHeavyweightDescendants()) {
               this.recursiveApplyCurrentShape();
            }

            super.mixOnShowing();
         }
      }
   }

   void mixOnHiding(boolean var1) {
      synchronized(this.getTreeLock()) {
         if (mixingLog.isLoggable(PlatformLogger.Level.FINE)) {
            mixingLog.fine("this = " + this + "; isLightweight=" + var1);
         }

         if (var1) {
            this.recursiveHideHeavyweightChildren();
         }

         super.mixOnHiding(var1);
      }
   }

   void mixOnReshaping() {
      synchronized(this.getTreeLock()) {
         if (mixingLog.isLoggable(PlatformLogger.Level.FINE)) {
            mixingLog.fine("this = " + this);
         }

         boolean var2 = this.isMixingNeeded();
         if (this.isLightweight() && this.hasHeavyweightDescendants()) {
            Point var3 = new Point(this.getX(), this.getY());

            for(Container var4 = this.getContainer(); var4 != null && var4.isLightweight(); var4 = var4.getContainer()) {
               var3.translate(var4.getX(), var4.getY());
            }

            this.recursiveRelocateHeavyweightChildren(var3);
            if (!var2) {
               return;
            }

            this.recursiveApplyCurrentShape();
         }

         if (var2) {
            super.mixOnReshaping();
         }
      }
   }

   void mixOnZOrderChanging(int var1, int var2) {
      synchronized(this.getTreeLock()) {
         if (mixingLog.isLoggable(PlatformLogger.Level.FINE)) {
            mixingLog.fine("this = " + this + "; oldZ=" + var1 + "; newZ=" + var2);
         }

         if (this.isMixingNeeded()) {
            boolean var4 = var2 < var1;
            if (var4 && this.isLightweight() && this.hasHeavyweightDescendants()) {
               this.recursiveApplyCurrentShape();
            }

            super.mixOnZOrderChanging(var1, var2);
         }
      }
   }

   void mixOnValidating() {
      synchronized(this.getTreeLock()) {
         if (mixingLog.isLoggable(PlatformLogger.Level.FINE)) {
            mixingLog.fine("this = " + this);
         }

         if (this.isMixingNeeded()) {
            if (this.hasHeavyweightDescendants()) {
               this.recursiveApplyCurrentShape();
            }

            if (this.isLightweight() && this.isNonOpaqueForMixing()) {
               this.subtractAndApplyShapeBelowMe();
            }

            super.mixOnValidating();
         }
      }
   }

   static {
      serialPersistentFields = new ObjectStreamField[]{new ObjectStreamField("ncomponents", Integer.TYPE), new ObjectStreamField("component", Component[].class), new ObjectStreamField("layoutMgr", LayoutManager.class), new ObjectStreamField("dispatcher", LightweightDispatcher.class), new ObjectStreamField("maxSize", Dimension.class), new ObjectStreamField("focusCycleRoot", Boolean.TYPE), new ObjectStreamField("containerSerializedDataVersion", Integer.TYPE), new ObjectStreamField("focusTraversalPolicyProvider", Boolean.TYPE)};
      Toolkit.loadLibraries();
      if (!GraphicsEnvironment.isHeadless()) {
         initIDs();
      }

      AWTAccessor.setContainerAccessor(new AWTAccessor.ContainerAccessor() {
         public void validateUnconditionally(Container var1) {
            var1.validateUnconditionally();
         }

         public Component findComponentAt(Container var1, int var2, int var3, boolean var4) {
            return var1.findComponentAt(var2, var3, var4);
         }
      });
      isJavaAwtSmartInvalidate = (Boolean)AccessController.doPrivileged((PrivilegedAction)(new GetBooleanAction("java.awt.smartInvalidate")));
      descendUnconditionallyWhenValidating = false;
   }

   protected class AccessibleAWTContainer extends Component.AccessibleAWTComponent {
      private static final long serialVersionUID = 5081320404842566097L;
      private transient volatile int propertyListenersCount = 0;
      protected ContainerListener accessibleContainerHandler = null;

      protected AccessibleAWTContainer() {
         super();
      }

      public int getAccessibleChildrenCount() {
         return Container.this.getAccessibleChildrenCount();
      }

      public Accessible getAccessibleChild(int var1) {
         return Container.this.getAccessibleChild(var1);
      }

      public Accessible getAccessibleAt(Point var1) {
         return Container.this.getAccessibleAt(var1);
      }

      public void addPropertyChangeListener(PropertyChangeListener var1) {
         if (this.accessibleContainerHandler == null) {
            this.accessibleContainerHandler = new Container.AccessibleAWTContainer.AccessibleContainerHandler();
         }

         if (this.propertyListenersCount++ == 0) {
            Container.this.addContainerListener(this.accessibleContainerHandler);
         }

         super.addPropertyChangeListener(var1);
      }

      public void removePropertyChangeListener(PropertyChangeListener var1) {
         if (--this.propertyListenersCount == 0) {
            Container.this.removeContainerListener(this.accessibleContainerHandler);
         }

         super.removePropertyChangeListener(var1);
      }

      protected class AccessibleContainerHandler implements ContainerListener {
         public void componentAdded(ContainerEvent var1) {
            Component var2 = var1.getChild();
            if (var2 != null && var2 instanceof Accessible) {
               AccessibleAWTContainer.this.firePropertyChange("AccessibleChild", (Object)null, ((Accessible)var2).getAccessibleContext());
            }

         }

         public void componentRemoved(ContainerEvent var1) {
            Component var2 = var1.getChild();
            if (var2 != null && var2 instanceof Accessible) {
               AccessibleAWTContainer.this.firePropertyChange("AccessibleChild", ((Accessible)var2).getAccessibleContext(), (Object)null);
            }

         }
      }
   }

   static final class WakingRunnable implements Runnable {
      public void run() {
      }
   }

   static class DropTargetEventTargetFilter implements Container.EventTargetFilter {
      static final Container.EventTargetFilter FILTER = new Container.DropTargetEventTargetFilter();

      private DropTargetEventTargetFilter() {
      }

      public boolean accept(Component var1) {
         DropTarget var2 = var1.getDropTarget();
         return var2 != null && var2.isActive();
      }
   }

   static class MouseEventTargetFilter implements Container.EventTargetFilter {
      static final Container.EventTargetFilter FILTER = new Container.MouseEventTargetFilter();

      private MouseEventTargetFilter() {
      }

      public boolean accept(Component var1) {
         return (var1.eventMask & 32L) != 0L || (var1.eventMask & 16L) != 0L || (var1.eventMask & 131072L) != 0L || var1.mouseListener != null || var1.mouseMotionListener != null || var1.mouseWheelListener != null;
      }
   }

   interface EventTargetFilter {
      boolean accept(Component var1);
   }
}
