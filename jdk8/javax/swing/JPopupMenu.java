package javax.swing;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Iterator;
import java.util.Vector;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import javax.accessibility.AccessibleSelection;
import javax.accessibility.AccessibleState;
import javax.swing.event.MenuKeyEvent;
import javax.swing.event.MenuKeyListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.plaf.PopupMenuUI;
import javax.swing.plaf.basic.BasicComboPopup;
import sun.awt.SunToolkit;
import sun.security.action.GetPropertyAction;

public class JPopupMenu extends JComponent implements Accessible, MenuElement {
   private static final String uiClassID = "PopupMenuUI";
   private static final Object defaultLWPopupEnabledKey = new StringBuffer("JPopupMenu.defaultLWPopupEnabledKey");
   static boolean popupPostionFixDisabled = false;
   transient Component invoker;
   transient Popup popup;
   transient Frame frame;
   private int desiredLocationX;
   private int desiredLocationY;
   private String label;
   private boolean paintBorder;
   private Insets margin;
   private boolean lightWeightPopup;
   private SingleSelectionModel selectionModel;
   private static final Object classLock;
   private static final boolean TRACE = false;
   private static final boolean VERBOSE = false;
   private static final boolean DEBUG = false;

   public static void setDefaultLightWeightPopupEnabled(boolean var0) {
      SwingUtilities.appContextPut(defaultLWPopupEnabledKey, var0);
   }

   public static boolean getDefaultLightWeightPopupEnabled() {
      Boolean var0 = (Boolean)SwingUtilities.appContextGet(defaultLWPopupEnabledKey);
      if (var0 == null) {
         SwingUtilities.appContextPut(defaultLWPopupEnabledKey, Boolean.TRUE);
         return true;
      } else {
         return var0;
      }
   }

   public JPopupMenu() {
      this((String)null);
   }

   public JPopupMenu(String var1) {
      this.label = null;
      this.paintBorder = true;
      this.margin = null;
      this.lightWeightPopup = true;
      this.label = var1;
      this.lightWeightPopup = getDefaultLightWeightPopupEnabled();
      this.setSelectionModel(new DefaultSingleSelectionModel());
      this.enableEvents(16L);
      this.setFocusTraversalKeysEnabled(false);
      this.updateUI();
   }

   public PopupMenuUI getUI() {
      return (PopupMenuUI)this.ui;
   }

   public void setUI(PopupMenuUI var1) {
      super.setUI(var1);
   }

   public void updateUI() {
      this.setUI((PopupMenuUI)UIManager.getUI(this));
   }

   public String getUIClassID() {
      return "PopupMenuUI";
   }

   protected void processFocusEvent(FocusEvent var1) {
      super.processFocusEvent(var1);
   }

   protected void processKeyEvent(KeyEvent var1) {
      MenuSelectionManager.defaultManager().processKeyEvent(var1);
      if (!var1.isConsumed()) {
         super.processKeyEvent(var1);
      }
   }

   public SingleSelectionModel getSelectionModel() {
      return this.selectionModel;
   }

   public void setSelectionModel(SingleSelectionModel var1) {
      this.selectionModel = var1;
   }

   public JMenuItem add(JMenuItem var1) {
      super.add(var1);
      return var1;
   }

   public JMenuItem add(String var1) {
      return this.add(new JMenuItem(var1));
   }

   public JMenuItem add(Action var1) {
      JMenuItem var2 = this.createActionComponent(var1);
      var2.setAction(var1);
      this.add(var2);
      return var2;
   }

   Point adjustPopupLocationToFitScreen(int var1, int var2) {
      Point var3 = new Point(var1, var2);
      if (!popupPostionFixDisabled && !GraphicsEnvironment.isHeadless()) {
         GraphicsConfiguration var5 = this.getCurrentGraphicsConfiguration(var3);
         Toolkit var6 = Toolkit.getDefaultToolkit();
         Rectangle var4;
         if (var5 != null) {
            var4 = var5.getBounds();
         } else {
            var4 = new Rectangle(var6.getScreenSize());
         }

         Dimension var7 = this.getPreferredSize();
         long var8 = (long)var3.x + (long)var7.width;
         long var10 = (long)var3.y + (long)var7.height;
         int var12 = var4.width;
         int var13 = var4.height;
         if (!canPopupOverlapTaskBar()) {
            Insets var14 = var6.getScreenInsets(var5);
            var4.x += var14.left;
            var4.y += var14.top;
            var12 -= var14.left + var14.right;
            var13 -= var14.top + var14.bottom;
         }

         int var16 = var4.x + var12;
         int var15 = var4.y + var13;
         if (var8 > (long)var16) {
            var3.x = var16 - var7.width;
         }

         if (var10 > (long)var15) {
            var3.y = var15 - var7.height;
         }

         if (var3.x < var4.x) {
            var3.x = var4.x;
         }

         if (var3.y < var4.y) {
            var3.y = var4.y;
         }

         return var3;
      } else {
         return var3;
      }
   }

   private GraphicsConfiguration getCurrentGraphicsConfiguration(Point var1) {
      GraphicsConfiguration var2 = null;
      GraphicsEnvironment var3 = GraphicsEnvironment.getLocalGraphicsEnvironment();
      GraphicsDevice[] var4 = var3.getScreenDevices();

      for(int var5 = 0; var5 < var4.length; ++var5) {
         if (var4[var5].getType() == 0) {
            GraphicsConfiguration var6 = var4[var5].getDefaultConfiguration();
            if (var6.getBounds().contains(var1)) {
               var2 = var6;
               break;
            }
         }
      }

      if (var2 == null && this.getInvoker() != null) {
         var2 = this.getInvoker().getGraphicsConfiguration();
      }

      return var2;
   }

   static boolean canPopupOverlapTaskBar() {
      boolean var0 = true;
      Toolkit var1 = Toolkit.getDefaultToolkit();
      if (var1 instanceof SunToolkit) {
         var0 = ((SunToolkit)var1).canPopupOverlapTaskBar();
      }

      return var0;
   }

   protected JMenuItem createActionComponent(Action var1) {
      JMenuItem var2 = new JMenuItem() {
         protected PropertyChangeListener createActionPropertyChangeListener(Action var1) {
            PropertyChangeListener var2 = JPopupMenu.this.createActionChangeListener(this);
            if (var2 == null) {
               var2 = super.createActionPropertyChangeListener(var1);
            }

            return var2;
         }
      };
      var2.setHorizontalTextPosition(11);
      var2.setVerticalTextPosition(0);
      return var2;
   }

   protected PropertyChangeListener createActionChangeListener(JMenuItem var1) {
      return var1.createActionPropertyChangeListener0(var1.getAction());
   }

   public void remove(int var1) {
      if (var1 < 0) {
         throw new IllegalArgumentException("index less than zero.");
      } else if (var1 > this.getComponentCount() - 1) {
         throw new IllegalArgumentException("index greater than the number of items.");
      } else {
         super.remove(var1);
      }
   }

   public void setLightWeightPopupEnabled(boolean var1) {
      this.lightWeightPopup = var1;
   }

   public boolean isLightWeightPopupEnabled() {
      return this.lightWeightPopup;
   }

   public String getLabel() {
      return this.label;
   }

   public void setLabel(String var1) {
      String var2 = this.label;
      this.label = var1;
      this.firePropertyChange("label", var2, var1);
      if (this.accessibleContext != null) {
         this.accessibleContext.firePropertyChange("AccessibleVisibleData", var2, var1);
      }

      this.invalidate();
      this.repaint();
   }

   public void addSeparator() {
      this.add((Component)(new JPopupMenu.Separator()));
   }

   public void insert(Action var1, int var2) {
      JMenuItem var3 = this.createActionComponent(var1);
      var3.setAction(var1);
      this.insert((Component)var3, var2);
   }

   public void insert(Component var1, int var2) {
      if (var2 < 0) {
         throw new IllegalArgumentException("index less than zero.");
      } else {
         int var3 = this.getComponentCount();
         Vector var4 = new Vector();

         for(int var5 = var2; var5 < var3; ++var5) {
            var4.addElement(this.getComponent(var2));
            this.remove(var2);
         }

         this.add((Component)var1);
         Iterator var7 = var4.iterator();

         while(var7.hasNext()) {
            Component var6 = (Component)var7.next();
            this.add((Component)var6);
         }

      }
   }

   public void addPopupMenuListener(PopupMenuListener var1) {
      this.listenerList.add(PopupMenuListener.class, var1);
   }

   public void removePopupMenuListener(PopupMenuListener var1) {
      this.listenerList.remove(PopupMenuListener.class, var1);
   }

   public PopupMenuListener[] getPopupMenuListeners() {
      return (PopupMenuListener[])this.listenerList.getListeners(PopupMenuListener.class);
   }

   public void addMenuKeyListener(MenuKeyListener var1) {
      this.listenerList.add(MenuKeyListener.class, var1);
   }

   public void removeMenuKeyListener(MenuKeyListener var1) {
      this.listenerList.remove(MenuKeyListener.class, var1);
   }

   public MenuKeyListener[] getMenuKeyListeners() {
      return (MenuKeyListener[])this.listenerList.getListeners(MenuKeyListener.class);
   }

   protected void firePopupMenuWillBecomeVisible() {
      Object[] var1 = this.listenerList.getListenerList();
      PopupMenuEvent var2 = null;

      for(int var3 = var1.length - 2; var3 >= 0; var3 -= 2) {
         if (var1[var3] == PopupMenuListener.class) {
            if (var2 == null) {
               var2 = new PopupMenuEvent(this);
            }

            ((PopupMenuListener)var1[var3 + 1]).popupMenuWillBecomeVisible(var2);
         }
      }

   }

   protected void firePopupMenuWillBecomeInvisible() {
      Object[] var1 = this.listenerList.getListenerList();
      PopupMenuEvent var2 = null;

      for(int var3 = var1.length - 2; var3 >= 0; var3 -= 2) {
         if (var1[var3] == PopupMenuListener.class) {
            if (var2 == null) {
               var2 = new PopupMenuEvent(this);
            }

            ((PopupMenuListener)var1[var3 + 1]).popupMenuWillBecomeInvisible(var2);
         }
      }

   }

   protected void firePopupMenuCanceled() {
      Object[] var1 = this.listenerList.getListenerList();
      PopupMenuEvent var2 = null;

      for(int var3 = var1.length - 2; var3 >= 0; var3 -= 2) {
         if (var1[var3] == PopupMenuListener.class) {
            if (var2 == null) {
               var2 = new PopupMenuEvent(this);
            }

            ((PopupMenuListener)var1[var3 + 1]).popupMenuCanceled(var2);
         }
      }

   }

   boolean alwaysOnTop() {
      return true;
   }

   public void pack() {
      if (this.popup != null) {
         Dimension var1 = this.getPreferredSize();
         if (var1 != null && var1.width == this.getWidth() && var1.height == this.getHeight()) {
            this.validate();
         } else {
            this.showPopup();
         }
      }

   }

   public void setVisible(boolean var1) {
      if (var1 != this.isVisible()) {
         if (!var1) {
            Boolean var2 = (Boolean)this.getClientProperty("JPopupMenu.firePopupMenuCanceled");
            if (var2 != null && var2 == Boolean.TRUE) {
               this.putClientProperty("JPopupMenu.firePopupMenuCanceled", Boolean.FALSE);
               this.firePopupMenuCanceled();
            }

            this.getSelectionModel().clearSelection();
         } else if (this.isPopupMenu()) {
            MenuElement[] var3 = new MenuElement[]{this};
            MenuSelectionManager.defaultManager().setSelectedPath(var3);
         }

         if (var1) {
            this.firePopupMenuWillBecomeVisible();
            this.showPopup();
            this.firePropertyChange("visible", Boolean.FALSE, Boolean.TRUE);
         } else if (this.popup != null) {
            this.firePopupMenuWillBecomeInvisible();
            this.popup.hide();
            this.popup = null;
            this.firePropertyChange("visible", Boolean.TRUE, Boolean.FALSE);
            if (this.isPopupMenu()) {
               MenuSelectionManager.defaultManager().clearSelectedPath();
            }
         }

      }
   }

   private void showPopup() {
      Popup var1 = this.popup;
      if (var1 != null) {
         var1.hide();
      }

      PopupFactory var2 = PopupFactory.getSharedInstance();
      if (this.isLightWeightPopupEnabled()) {
         var2.setPopupType(0);
      } else {
         var2.setPopupType(2);
      }

      Point var3 = this.adjustPopupLocationToFitScreen(this.desiredLocationX, this.desiredLocationY);
      this.desiredLocationX = var3.x;
      this.desiredLocationY = var3.y;
      Popup var4 = this.getUI().getPopup(this, this.desiredLocationX, this.desiredLocationY);
      var2.setPopupType(0);
      this.popup = var4;
      var4.show();
   }

   public boolean isVisible() {
      return this.popup != null;
   }

   public void setLocation(int var1, int var2) {
      int var3 = this.desiredLocationX;
      int var4 = this.desiredLocationY;
      this.desiredLocationX = var1;
      this.desiredLocationY = var2;
      if (this.popup != null && (var1 != var3 || var2 != var4)) {
         this.showPopup();
      }

   }

   private boolean isPopupMenu() {
      return this.invoker != null && !(this.invoker instanceof JMenu);
   }

   public Component getInvoker() {
      return this.invoker;
   }

   public void setInvoker(Component var1) {
      Component var2 = this.invoker;
      this.invoker = var1;
      if (var2 != this.invoker && this.ui != null) {
         this.ui.uninstallUI(this);
         this.ui.installUI(this);
      }

      this.invalidate();
   }

   public void show(Component var1, int var2, int var3) {
      this.setInvoker(var1);
      Frame var4 = getFrame(var1);
      if (var4 != this.frame && var4 != null) {
         this.frame = var4;
         if (this.popup != null) {
            this.setVisible(false);
         }
      }

      if (var1 != null) {
         Point var5 = var1.getLocationOnScreen();
         long var6 = (long)var5.x + (long)var2;
         long var8 = (long)var5.y + (long)var3;
         if (var6 > 2147483647L) {
            var6 = 2147483647L;
         }

         if (var6 < -2147483648L) {
            var6 = -2147483648L;
         }

         if (var8 > 2147483647L) {
            var8 = 2147483647L;
         }

         if (var8 < -2147483648L) {
            var8 = -2147483648L;
         }

         this.setLocation((int)var6, (int)var8);
      } else {
         this.setLocation(var2, var3);
      }

      this.setVisible(true);
   }

   JPopupMenu getRootPopupMenu() {
      JPopupMenu var1;
      for(var1 = this; var1 != null && !var1.isPopupMenu() && var1.getInvoker() != null && var1.getInvoker().getParent() != null && var1.getInvoker().getParent() instanceof JPopupMenu; var1 = (JPopupMenu)var1.getInvoker().getParent()) {
      }

      return var1;
   }

   /** @deprecated */
   @Deprecated
   public Component getComponentAtIndex(int var1) {
      return this.getComponent(var1);
   }

   public int getComponentIndex(Component var1) {
      int var2 = this.getComponentCount();
      Component[] var3 = this.getComponents();

      for(int var4 = 0; var4 < var2; ++var4) {
         Component var5 = var3[var4];
         if (var5 == var1) {
            return var4;
         }
      }

      return -1;
   }

   public void setPopupSize(Dimension var1) {
      Dimension var2 = this.getPreferredSize();
      this.setPreferredSize(var1);
      if (this.popup != null) {
         Dimension var3 = this.getPreferredSize();
         if (!var2.equals(var3)) {
            this.showPopup();
         }
      }

   }

   public void setPopupSize(int var1, int var2) {
      this.setPopupSize(new Dimension(var1, var2));
   }

   public void setSelected(Component var1) {
      SingleSelectionModel var2 = this.getSelectionModel();
      int var3 = this.getComponentIndex(var1);
      var2.setSelectedIndex(var3);
   }

   public boolean isBorderPainted() {
      return this.paintBorder;
   }

   public void setBorderPainted(boolean var1) {
      this.paintBorder = var1;
      this.repaint();
   }

   protected void paintBorder(Graphics var1) {
      if (this.isBorderPainted()) {
         super.paintBorder(var1);
      }

   }

   public Insets getMargin() {
      return this.margin == null ? new Insets(0, 0, 0, 0) : this.margin;
   }

   boolean isSubPopupMenu(JPopupMenu var1) {
      int var2 = this.getComponentCount();
      Component[] var3 = this.getComponents();

      for(int var4 = 0; var4 < var2; ++var4) {
         Component var5 = var3[var4];
         if (var5 instanceof JMenu) {
            JMenu var6 = (JMenu)var5;
            JPopupMenu var7 = var6.getPopupMenu();
            if (var7 == var1) {
               return true;
            }

            if (var7.isSubPopupMenu(var1)) {
               return true;
            }
         }
      }

      return false;
   }

   private static Frame getFrame(Component var0) {
      Object var1;
      for(var1 = var0; !(var1 instanceof Frame) && var1 != null; var1 = ((Component)var1).getParent()) {
      }

      return (Frame)var1;
   }

   protected String paramString() {
      String var1 = this.label != null ? this.label : "";
      String var2 = this.paintBorder ? "true" : "false";
      String var3 = this.margin != null ? this.margin.toString() : "";
      String var4 = this.isLightWeightPopupEnabled() ? "true" : "false";
      return super.paramString() + ",desiredLocationX=" + this.desiredLocationX + ",desiredLocationY=" + this.desiredLocationY + ",label=" + var1 + ",lightWeightPopupEnabled=" + var4 + ",margin=" + var3 + ",paintBorder=" + var2;
   }

   public AccessibleContext getAccessibleContext() {
      if (this.accessibleContext == null) {
         this.accessibleContext = new JPopupMenu.AccessibleJPopupMenu();
      }

      return this.accessibleContext;
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      Vector var2 = new Vector();
      var1.defaultWriteObject();
      if (this.invoker != null && this.invoker instanceof Serializable) {
         var2.addElement("invoker");
         var2.addElement(this.invoker);
      }

      if (this.popup != null && this.popup instanceof Serializable) {
         var2.addElement("popup");
         var2.addElement(this.popup);
      }

      var1.writeObject(var2);
      if (this.getUIClassID().equals("PopupMenuUI")) {
         byte var3 = JComponent.getWriteObjCounter(this);
         --var3;
         JComponent.setWriteObjCounter(this, var3);
         if (var3 == 0 && this.ui != null) {
            this.ui.installUI(this);
         }
      }

   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      Vector var2 = (Vector)var1.readObject();
      int var3 = 0;
      int var4 = var2.size();
      if (var3 < var4 && var2.elementAt(var3).equals("invoker")) {
         ++var3;
         this.invoker = (Component)var2.elementAt(var3);
         ++var3;
      }

      if (var3 < var4 && var2.elementAt(var3).equals("popup")) {
         ++var3;
         this.popup = (Popup)var2.elementAt(var3);
         ++var3;
      }

   }

   public void processMouseEvent(MouseEvent var1, MenuElement[] var2, MenuSelectionManager var3) {
   }

   public void processKeyEvent(KeyEvent var1, MenuElement[] var2, MenuSelectionManager var3) {
      MenuKeyEvent var4 = new MenuKeyEvent(var1.getComponent(), var1.getID(), var1.getWhen(), var1.getModifiers(), var1.getKeyCode(), var1.getKeyChar(), var2, var3);
      this.processMenuKeyEvent(var4);
      if (var4.isConsumed()) {
         var1.consume();
      }

   }

   private void processMenuKeyEvent(MenuKeyEvent var1) {
      switch(var1.getID()) {
      case 400:
         this.fireMenuKeyTyped(var1);
         break;
      case 401:
         this.fireMenuKeyPressed(var1);
         break;
      case 402:
         this.fireMenuKeyReleased(var1);
      }

   }

   private void fireMenuKeyPressed(MenuKeyEvent var1) {
      Object[] var2 = this.listenerList.getListenerList();

      for(int var3 = var2.length - 2; var3 >= 0; var3 -= 2) {
         if (var2[var3] == MenuKeyListener.class) {
            ((MenuKeyListener)var2[var3 + 1]).menuKeyPressed(var1);
         }
      }

   }

   private void fireMenuKeyReleased(MenuKeyEvent var1) {
      Object[] var2 = this.listenerList.getListenerList();

      for(int var3 = var2.length - 2; var3 >= 0; var3 -= 2) {
         if (var2[var3] == MenuKeyListener.class) {
            ((MenuKeyListener)var2[var3 + 1]).menuKeyReleased(var1);
         }
      }

   }

   private void fireMenuKeyTyped(MenuKeyEvent var1) {
      Object[] var2 = this.listenerList.getListenerList();

      for(int var3 = var2.length - 2; var3 >= 0; var3 -= 2) {
         if (var2[var3] == MenuKeyListener.class) {
            ((MenuKeyListener)var2[var3 + 1]).menuKeyTyped(var1);
         }
      }

   }

   public void menuSelectionChanged(boolean var1) {
      if (this.invoker instanceof JMenu) {
         JMenu var2 = (JMenu)this.invoker;
         if (var1) {
            var2.setPopupMenuVisible(true);
         } else {
            var2.setPopupMenuVisible(false);
         }
      }

      if (this.isPopupMenu() && !var1) {
         this.setVisible(false);
      }

   }

   public MenuElement[] getSubElements() {
      Vector var2 = new Vector();
      int var3 = this.getComponentCount();

      int var4;
      for(var4 = 0; var4 < var3; ++var4) {
         Component var5 = this.getComponent(var4);
         if (var5 instanceof MenuElement) {
            var2.addElement((MenuElement)var5);
         }
      }

      MenuElement[] var1 = new MenuElement[var2.size()];
      var4 = 0;

      for(var3 = var2.size(); var4 < var3; ++var4) {
         var1[var4] = (MenuElement)var2.elementAt(var4);
      }

      return var1;
   }

   public Component getComponent() {
      return this;
   }

   public boolean isPopupTrigger(MouseEvent var1) {
      return this.getUI().isPopupTrigger(var1);
   }

   static {
      popupPostionFixDisabled = ((String)AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction("javax.swing.adjustPopupLocationToFit", "")))).equals("false");
      classLock = new Object();
   }

   public static class Separator extends JSeparator {
      public Separator() {
         super(0);
      }

      public String getUIClassID() {
         return "PopupMenuSeparatorUI";
      }
   }

   protected class AccessibleJPopupMenu extends JComponent.AccessibleJComponent implements PropertyChangeListener {
      protected AccessibleJPopupMenu() {
         super();
         JPopupMenu.this.addPropertyChangeListener(this);
      }

      public AccessibleRole getAccessibleRole() {
         return AccessibleRole.POPUP_MENU;
      }

      public void propertyChange(PropertyChangeEvent var1) {
         String var2 = var1.getPropertyName();
         if (var2 == "visible") {
            if (var1.getOldValue() == Boolean.FALSE && var1.getNewValue() == Boolean.TRUE) {
               this.handlePopupIsVisibleEvent(true);
            } else if (var1.getOldValue() == Boolean.TRUE && var1.getNewValue() == Boolean.FALSE) {
               this.handlePopupIsVisibleEvent(false);
            }
         }

      }

      private void handlePopupIsVisibleEvent(boolean var1) {
         if (var1) {
            this.firePropertyChange("AccessibleState", (Object)null, AccessibleState.VISIBLE);
            this.fireActiveDescendant();
         } else {
            this.firePropertyChange("AccessibleState", AccessibleState.VISIBLE, (Object)null);
         }

      }

      private void fireActiveDescendant() {
         if (JPopupMenu.this instanceof BasicComboPopup) {
            JList var1 = ((BasicComboPopup)JPopupMenu.this).getList();
            if (var1 == null) {
               return;
            }

            AccessibleContext var2 = var1.getAccessibleContext();
            AccessibleSelection var3 = var2.getAccessibleSelection();
            if (var3 == null) {
               return;
            }

            Accessible var4 = var3.getAccessibleSelection(0);
            if (var4 == null) {
               return;
            }

            AccessibleContext var5 = var4.getAccessibleContext();
            if (var5 != null && JPopupMenu.this.invoker != null) {
               AccessibleContext var6 = JPopupMenu.this.invoker.getAccessibleContext();
               if (var6 != null) {
                  var6.firePropertyChange("AccessibleActiveDescendant", (Object)null, var5);
               }
            }
         }

      }
   }
}
