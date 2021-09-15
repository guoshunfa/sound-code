package java.awt;

import java.awt.event.ActionEvent;
import java.awt.event.FocusListener;
import java.awt.peer.MenuComponentPeer;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.util.Locale;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleComponent;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import javax.accessibility.AccessibleSelection;
import javax.accessibility.AccessibleStateSet;
import sun.awt.AWTAccessor;
import sun.awt.AppContext;

public abstract class MenuComponent implements Serializable {
   transient MenuComponentPeer peer;
   transient MenuContainer parent;
   transient AppContext appContext;
   volatile Font font;
   private String name;
   private boolean nameExplicitlySet = false;
   boolean newEventsOnly = false;
   private transient volatile AccessControlContext acc = AccessController.getContext();
   static final String actionListenerK = "actionL";
   static final String itemListenerK = "itemL";
   private static final long serialVersionUID = -4536902356223894379L;
   AccessibleContext accessibleContext = null;

   final AccessControlContext getAccessControlContext() {
      if (this.acc == null) {
         throw new SecurityException("MenuComponent is missing AccessControlContext");
      } else {
         return this.acc;
      }
   }

   public MenuComponent() throws HeadlessException {
      GraphicsEnvironment.checkHeadless();
      this.appContext = AppContext.getAppContext();
   }

   String constructComponentName() {
      return null;
   }

   public String getName() {
      if (this.name == null && !this.nameExplicitlySet) {
         synchronized(this) {
            if (this.name == null && !this.nameExplicitlySet) {
               this.name = this.constructComponentName();
            }
         }
      }

      return this.name;
   }

   public void setName(String var1) {
      synchronized(this) {
         this.name = var1;
         this.nameExplicitlySet = true;
      }
   }

   public MenuContainer getParent() {
      return this.getParent_NoClientCode();
   }

   final MenuContainer getParent_NoClientCode() {
      return this.parent;
   }

   /** @deprecated */
   @Deprecated
   public MenuComponentPeer getPeer() {
      return this.peer;
   }

   public Font getFont() {
      Font var1 = this.font;
      if (var1 != null) {
         return var1;
      } else {
         MenuContainer var2 = this.parent;
         return var2 != null ? var2.getFont() : null;
      }
   }

   final Font getFont_NoClientCode() {
      Font var1 = this.font;
      if (var1 != null) {
         return var1;
      } else {
         MenuContainer var2 = this.parent;
         if (var2 != null) {
            if (var2 instanceof Component) {
               var1 = ((Component)var2).getFont_NoClientCode();
            } else if (var2 instanceof MenuComponent) {
               var1 = ((MenuComponent)var2).getFont_NoClientCode();
            }
         }

         return var1;
      }
   }

   public void setFont(Font var1) {
      synchronized(this.getTreeLock()) {
         this.font = var1;
         MenuComponentPeer var3 = this.peer;
         if (var3 != null) {
            var3.setFont(var1);
         }

      }
   }

   public void removeNotify() {
      synchronized(this.getTreeLock()) {
         MenuComponentPeer var2 = this.peer;
         if (var2 != null) {
            Toolkit.getEventQueue().removeSourceEvents(this, true);
            this.peer = null;
            var2.dispose();
         }

      }
   }

   /** @deprecated */
   @Deprecated
   public boolean postEvent(Event var1) {
      MenuContainer var2 = this.parent;
      if (var2 != null) {
         var2.postEvent(var1);
      }

      return false;
   }

   public final void dispatchEvent(AWTEvent var1) {
      this.dispatchEventImpl(var1);
   }

   void dispatchEventImpl(AWTEvent var1) {
      EventQueue.setCurrentEventAndMostRecentTime(var1);
      Toolkit.getDefaultToolkit().notifyAWTEventListeners(var1);
      if (this.newEventsOnly || this.parent != null && this.parent instanceof MenuComponent && ((MenuComponent)this.parent).newEventsOnly) {
         if (this.eventEnabled(var1)) {
            this.processEvent(var1);
         } else if (var1 instanceof ActionEvent && this.parent != null) {
            var1.setSource(this.parent);
            ((MenuComponent)this.parent).dispatchEvent(var1);
         }
      } else {
         Event var2 = var1.convertToOld();
         if (var2 != null) {
            this.postEvent(var2);
         }
      }

   }

   boolean eventEnabled(AWTEvent var1) {
      return false;
   }

   protected void processEvent(AWTEvent var1) {
   }

   protected String paramString() {
      String var1 = this.getName();
      return var1 != null ? var1 : "";
   }

   public String toString() {
      return this.getClass().getName() + "[" + this.paramString() + "]";
   }

   protected final Object getTreeLock() {
      return Component.LOCK;
   }

   private void readObject(ObjectInputStream var1) throws ClassNotFoundException, IOException, HeadlessException {
      GraphicsEnvironment.checkHeadless();
      this.acc = AccessController.getContext();
      var1.defaultReadObject();
      this.appContext = AppContext.getAppContext();
   }

   private static native void initIDs();

   public AccessibleContext getAccessibleContext() {
      return this.accessibleContext;
   }

   int getAccessibleIndexInParent() {
      MenuContainer var1 = this.parent;
      if (!(var1 instanceof MenuComponent)) {
         return -1;
      } else {
         MenuComponent var2 = (MenuComponent)var1;
         return var2.getAccessibleChildIndex(this);
      }
   }

   int getAccessibleChildIndex(MenuComponent var1) {
      return -1;
   }

   AccessibleStateSet getAccessibleStateSet() {
      AccessibleStateSet var1 = new AccessibleStateSet();
      return var1;
   }

   static {
      Toolkit.loadLibraries();
      if (!GraphicsEnvironment.isHeadless()) {
         initIDs();
      }

      AWTAccessor.setMenuComponentAccessor(new AWTAccessor.MenuComponentAccessor() {
         public AppContext getAppContext(MenuComponent var1) {
            return var1.appContext;
         }

         public void setAppContext(MenuComponent var1, AppContext var2) {
            var1.appContext = var2;
         }

         public MenuContainer getParent(MenuComponent var1) {
            return var1.parent;
         }

         public Font getFont_NoClientCode(MenuComponent var1) {
            return var1.getFont_NoClientCode();
         }

         public <T extends MenuComponentPeer> T getPeer(MenuComponent var1) {
            return var1.peer;
         }
      });
   }

   protected abstract class AccessibleAWTMenuComponent extends AccessibleContext implements Serializable, AccessibleComponent, AccessibleSelection {
      private static final long serialVersionUID = -4269533416223798698L;

      public AccessibleSelection getAccessibleSelection() {
         return this;
      }

      public String getAccessibleName() {
         return this.accessibleName;
      }

      public String getAccessibleDescription() {
         return this.accessibleDescription;
      }

      public AccessibleRole getAccessibleRole() {
         return AccessibleRole.AWT_COMPONENT;
      }

      public AccessibleStateSet getAccessibleStateSet() {
         return MenuComponent.this.getAccessibleStateSet();
      }

      public Accessible getAccessibleParent() {
         if (this.accessibleParent != null) {
            return this.accessibleParent;
         } else {
            MenuContainer var1 = MenuComponent.this.getParent();
            return var1 instanceof Accessible ? (Accessible)var1 : null;
         }
      }

      public int getAccessibleIndexInParent() {
         return MenuComponent.this.getAccessibleIndexInParent();
      }

      public int getAccessibleChildrenCount() {
         return 0;
      }

      public Accessible getAccessibleChild(int var1) {
         return null;
      }

      public Locale getLocale() {
         MenuContainer var1 = MenuComponent.this.getParent();
         return var1 instanceof Component ? ((Component)var1).getLocale() : Locale.getDefault();
      }

      public AccessibleComponent getAccessibleComponent() {
         return this;
      }

      public Color getBackground() {
         return null;
      }

      public void setBackground(Color var1) {
      }

      public Color getForeground() {
         return null;
      }

      public void setForeground(Color var1) {
      }

      public Cursor getCursor() {
         return null;
      }

      public void setCursor(Cursor var1) {
      }

      public Font getFont() {
         return MenuComponent.this.getFont();
      }

      public void setFont(Font var1) {
         MenuComponent.this.setFont(var1);
      }

      public FontMetrics getFontMetrics(Font var1) {
         return null;
      }

      public boolean isEnabled() {
         return true;
      }

      public void setEnabled(boolean var1) {
      }

      public boolean isVisible() {
         return true;
      }

      public void setVisible(boolean var1) {
      }

      public boolean isShowing() {
         return true;
      }

      public boolean contains(Point var1) {
         return false;
      }

      public Point getLocationOnScreen() {
         return null;
      }

      public Point getLocation() {
         return null;
      }

      public void setLocation(Point var1) {
      }

      public Rectangle getBounds() {
         return null;
      }

      public void setBounds(Rectangle var1) {
      }

      public Dimension getSize() {
         return null;
      }

      public void setSize(Dimension var1) {
      }

      public Accessible getAccessibleAt(Point var1) {
         return null;
      }

      public boolean isFocusTraversable() {
         return true;
      }

      public void requestFocus() {
      }

      public void addFocusListener(FocusListener var1) {
      }

      public void removeFocusListener(FocusListener var1) {
      }

      public int getAccessibleSelectionCount() {
         return 0;
      }

      public Accessible getAccessibleSelection(int var1) {
         return null;
      }

      public boolean isAccessibleChildSelected(int var1) {
         return false;
      }

      public void addAccessibleSelection(int var1) {
      }

      public void removeAccessibleSelection(int var1) {
      }

      public void clearAccessibleSelection() {
      }

      public void selectAllAccessibleSelection() {
      }
   }
}
