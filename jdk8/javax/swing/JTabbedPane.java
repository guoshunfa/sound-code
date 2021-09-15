package javax.swing;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.beans.Transient;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleComponent;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleIcon;
import javax.accessibility.AccessibleRole;
import javax.accessibility.AccessibleSelection;
import javax.accessibility.AccessibleState;
import javax.accessibility.AccessibleStateSet;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.TabbedPaneUI;
import javax.swing.plaf.UIResource;
import sun.swing.SwingUtilities2;

public class JTabbedPane extends JComponent implements Serializable, Accessible, SwingConstants {
   public static final int WRAP_TAB_LAYOUT = 0;
   public static final int SCROLL_TAB_LAYOUT = 1;
   private static final String uiClassID = "TabbedPaneUI";
   protected int tabPlacement;
   private int tabLayoutPolicy;
   protected SingleSelectionModel model;
   private boolean haveRegistered;
   protected ChangeListener changeListener;
   private final List<JTabbedPane.Page> pages;
   private Component visComp;
   protected transient ChangeEvent changeEvent;

   public JTabbedPane() {
      this(1, 0);
   }

   public JTabbedPane(int var1) {
      this(var1, 0);
   }

   public JTabbedPane(int var1, int var2) {
      this.tabPlacement = 1;
      this.changeListener = null;
      this.visComp = null;
      this.changeEvent = null;
      this.setTabPlacement(var1);
      this.setTabLayoutPolicy(var2);
      this.pages = new ArrayList(1);
      this.setModel(new DefaultSingleSelectionModel());
      this.updateUI();
   }

   public TabbedPaneUI getUI() {
      return (TabbedPaneUI)this.ui;
   }

   public void setUI(TabbedPaneUI var1) {
      super.setUI(var1);

      for(int var2 = 0; var2 < this.getTabCount(); ++var2) {
         Icon var3 = ((JTabbedPane.Page)this.pages.get(var2)).disabledIcon;
         if (var3 instanceof UIResource) {
            this.setDisabledIconAt(var2, (Icon)null);
         }
      }

   }

   public void updateUI() {
      this.setUI((TabbedPaneUI)UIManager.getUI(this));
   }

   public String getUIClassID() {
      return "TabbedPaneUI";
   }

   protected ChangeListener createChangeListener() {
      return new JTabbedPane.ModelListener();
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
      int var1 = this.getSelectedIndex();
      if (var1 < 0) {
         if (this.visComp != null && this.visComp.isVisible()) {
            this.visComp.setVisible(false);
         }

         this.visComp = null;
      } else {
         Component var2 = this.getComponentAt(var1);
         if (var2 != null && var2 != this.visComp) {
            boolean var3 = false;
            if (this.visComp != null) {
               var3 = SwingUtilities.findFocusOwner(this.visComp) != null;
               if (this.visComp.isVisible()) {
                  this.visComp.setVisible(false);
               }
            }

            if (!var2.isVisible()) {
               var2.setVisible(true);
            }

            if (var3) {
               SwingUtilities2.tabbedPaneChangeFocusTo(var2);
            }

            this.visComp = var2;
         }
      }

      Object[] var4 = this.listenerList.getListenerList();

      for(int var5 = var4.length - 2; var5 >= 0; var5 -= 2) {
         if (var4[var5] == ChangeListener.class) {
            if (this.changeEvent == null) {
               this.changeEvent = new ChangeEvent(this);
            }

            ((ChangeListener)var4[var5 + 1]).stateChanged(this.changeEvent);
         }
      }

   }

   public SingleSelectionModel getModel() {
      return this.model;
   }

   public void setModel(SingleSelectionModel var1) {
      SingleSelectionModel var2 = this.getModel();
      if (var2 != null) {
         var2.removeChangeListener(this.changeListener);
         this.changeListener = null;
      }

      this.model = var1;
      if (var1 != null) {
         this.changeListener = this.createChangeListener();
         var1.addChangeListener(this.changeListener);
      }

      this.firePropertyChange("model", var2, var1);
      this.repaint();
   }

   public int getTabPlacement() {
      return this.tabPlacement;
   }

   public void setTabPlacement(int var1) {
      if (var1 != 1 && var1 != 2 && var1 != 3 && var1 != 4) {
         throw new IllegalArgumentException("illegal tab placement: must be TOP, BOTTOM, LEFT, or RIGHT");
      } else {
         if (this.tabPlacement != var1) {
            int var2 = this.tabPlacement;
            this.tabPlacement = var1;
            this.firePropertyChange("tabPlacement", var2, var1);
            this.revalidate();
            this.repaint();
         }

      }
   }

   public int getTabLayoutPolicy() {
      return this.tabLayoutPolicy;
   }

   public void setTabLayoutPolicy(int var1) {
      if (var1 != 0 && var1 != 1) {
         throw new IllegalArgumentException("illegal tab layout policy: must be WRAP_TAB_LAYOUT or SCROLL_TAB_LAYOUT");
      } else {
         if (this.tabLayoutPolicy != var1) {
            int var2 = this.tabLayoutPolicy;
            this.tabLayoutPolicy = var1;
            this.firePropertyChange("tabLayoutPolicy", var2, var1);
            this.revalidate();
            this.repaint();
         }

      }
   }

   @Transient
   public int getSelectedIndex() {
      return this.model.getSelectedIndex();
   }

   public void setSelectedIndex(int var1) {
      if (var1 != -1) {
         this.checkIndex(var1);
      }

      this.setSelectedIndexImpl(var1, true);
   }

   private void setSelectedIndexImpl(int var1, boolean var2) {
      int var3 = this.model.getSelectedIndex();
      JTabbedPane.Page var4 = null;
      JTabbedPane.Page var5 = null;
      String var6 = null;
      var2 = var2 && var3 != var1;
      if (var2) {
         if (this.accessibleContext != null) {
            var6 = this.accessibleContext.getAccessibleName();
         }

         if (var3 >= 0) {
            var4 = (JTabbedPane.Page)this.pages.get(var3);
         }

         if (var1 >= 0) {
            var5 = (JTabbedPane.Page)this.pages.get(var1);
         }
      }

      this.model.setSelectedIndex(var1);
      if (var2) {
         this.changeAccessibleSelection(var4, var6, var5);
      }

   }

   private void changeAccessibleSelection(JTabbedPane.Page var1, String var2, JTabbedPane.Page var3) {
      if (this.accessibleContext != null) {
         if (var1 != null) {
            var1.firePropertyChange("AccessibleState", AccessibleState.SELECTED, (Object)null);
         }

         if (var3 != null) {
            var3.firePropertyChange("AccessibleState", (Object)null, AccessibleState.SELECTED);
         }

         this.accessibleContext.firePropertyChange("AccessibleName", var2, this.accessibleContext.getAccessibleName());
      }
   }

   @Transient
   public Component getSelectedComponent() {
      int var1 = this.getSelectedIndex();
      return var1 == -1 ? null : this.getComponentAt(var1);
   }

   public void setSelectedComponent(Component var1) {
      int var2 = this.indexOfComponent(var1);
      if (var2 != -1) {
         this.setSelectedIndex(var2);
      } else {
         throw new IllegalArgumentException("component not found in tabbed pane");
      }
   }

   public void insertTab(String var1, Icon var2, Component var3, String var4, int var5) {
      int var6 = var5;
      int var7 = this.indexOfComponent(var3);
      if (var3 != null && var7 != -1) {
         this.removeTabAt(var7);
         if (var5 > var7) {
            var6 = var5 - 1;
         }
      }

      int var8 = this.getSelectedIndex();
      this.pages.add(var6, new JTabbedPane.Page(this, var1 != null ? var1 : "", var2, (Icon)null, var3, var4));
      if (var3 != null) {
         this.addImpl(var3, (Object)null, -1);
         var3.setVisible(false);
      } else {
         this.firePropertyChange("indexForNullComponent", -1, var5);
      }

      if (this.pages.size() == 1) {
         this.setSelectedIndex(0);
      }

      if (var8 >= var6) {
         this.setSelectedIndexImpl(var8 + 1, false);
      }

      if (!this.haveRegistered && var4 != null) {
         ToolTipManager.sharedInstance().registerComponent(this);
         this.haveRegistered = true;
      }

      if (this.accessibleContext != null) {
         this.accessibleContext.firePropertyChange("AccessibleVisibleData", (Object)null, var3);
      }

      this.revalidate();
      this.repaint();
   }

   public void addTab(String var1, Icon var2, Component var3, String var4) {
      this.insertTab(var1, var2, var3, var4, this.pages.size());
   }

   public void addTab(String var1, Icon var2, Component var3) {
      this.insertTab(var1, var2, var3, (String)null, this.pages.size());
   }

   public void addTab(String var1, Component var2) {
      this.insertTab(var1, (Icon)null, var2, (String)null, this.pages.size());
   }

   public Component add(Component var1) {
      if (!(var1 instanceof UIResource)) {
         this.addTab(var1.getName(), var1);
      } else {
         super.add(var1);
      }

      return var1;
   }

   public Component add(String var1, Component var2) {
      if (!(var2 instanceof UIResource)) {
         this.addTab(var1, var2);
      } else {
         super.add(var1, var2);
      }

      return var2;
   }

   public Component add(Component var1, int var2) {
      if (!(var1 instanceof UIResource)) {
         this.insertTab(var1.getName(), (Icon)null, var1, (String)null, var2 == -1 ? this.getTabCount() : var2);
      } else {
         super.add(var1, var2);
      }

      return var1;
   }

   public void add(Component var1, Object var2) {
      if (!(var1 instanceof UIResource)) {
         if (var2 instanceof String) {
            this.addTab((String)var2, var1);
         } else if (var2 instanceof Icon) {
            this.addTab((String)null, (Icon)var2, var1);
         } else {
            this.add(var1);
         }
      } else {
         super.add(var1, var2);
      }

   }

   public void add(Component var1, Object var2, int var3) {
      if (!(var1 instanceof UIResource)) {
         Icon var4 = var2 instanceof Icon ? (Icon)var2 : null;
         String var5 = var2 instanceof String ? (String)var2 : null;
         this.insertTab(var5, var4, var1, (String)null, var3 == -1 ? this.getTabCount() : var3);
      } else {
         super.add(var1, var2, var3);
      }

   }

   public void removeTabAt(int var1) {
      this.checkIndex(var1);
      Component var2 = this.getComponentAt(var1);
      boolean var3 = false;
      int var4 = this.getSelectedIndex();
      String var5 = null;
      if (var2 == this.visComp) {
         var3 = SwingUtilities.findFocusOwner(this.visComp) != null;
         this.visComp = null;
      }

      if (this.accessibleContext != null) {
         if (var1 == var4) {
            ((JTabbedPane.Page)this.pages.get(var1)).firePropertyChange("AccessibleState", AccessibleState.SELECTED, (Object)null);
            var5 = this.accessibleContext.getAccessibleName();
         }

         this.accessibleContext.firePropertyChange("AccessibleVisibleData", var2, (Object)null);
      }

      this.setTabComponentAt(var1, (Component)null);
      this.pages.remove(var1);
      this.putClientProperty("__index_to_remove__", var1);
      if (var4 > var1) {
         this.setSelectedIndexImpl(var4 - 1, false);
      } else if (var4 >= this.getTabCount()) {
         this.setSelectedIndexImpl(var4 - 1, false);
         JTabbedPane.Page var6 = var4 != 0 ? (JTabbedPane.Page)this.pages.get(var4 - 1) : null;
         this.changeAccessibleSelection((JTabbedPane.Page)null, var5, var6);
      } else if (var1 == var4) {
         this.fireStateChanged();
         this.changeAccessibleSelection((JTabbedPane.Page)null, var5, (JTabbedPane.Page)this.pages.get(var1));
      }

      if (var2 != null) {
         Component[] var8 = this.getComponents();
         int var7 = var8.length;

         while(true) {
            --var7;
            if (var7 < 0) {
               break;
            }

            if (var8[var7] == var2) {
               super.remove(var7);
               var2.setVisible(true);
               break;
            }
         }
      }

      if (var3) {
         SwingUtilities2.tabbedPaneChangeFocusTo(this.getSelectedComponent());
      }

      this.revalidate();
      this.repaint();
   }

   public void remove(Component var1) {
      int var2 = this.indexOfComponent(var1);
      if (var2 != -1) {
         this.removeTabAt(var2);
      } else {
         Component[] var3 = this.getComponents();

         for(int var4 = 0; var4 < var3.length; ++var4) {
            if (var1 == var3[var4]) {
               super.remove(var4);
               break;
            }
         }
      }

   }

   public void remove(int var1) {
      this.removeTabAt(var1);
   }

   public void removeAll() {
      this.setSelectedIndexImpl(-1, true);
      int var1 = this.getTabCount();

      while(var1-- > 0) {
         this.removeTabAt(var1);
      }

   }

   public int getTabCount() {
      return this.pages.size();
   }

   public int getTabRunCount() {
      return this.ui != null ? ((TabbedPaneUI)this.ui).getTabRunCount(this) : 0;
   }

   public String getTitleAt(int var1) {
      return ((JTabbedPane.Page)this.pages.get(var1)).title;
   }

   public Icon getIconAt(int var1) {
      return ((JTabbedPane.Page)this.pages.get(var1)).icon;
   }

   public Icon getDisabledIconAt(int var1) {
      JTabbedPane.Page var2 = (JTabbedPane.Page)this.pages.get(var1);
      if (var2.disabledIcon == null) {
         var2.disabledIcon = UIManager.getLookAndFeel().getDisabledIcon(this, var2.icon);
      }

      return var2.disabledIcon;
   }

   public String getToolTipTextAt(int var1) {
      return ((JTabbedPane.Page)this.pages.get(var1)).tip;
   }

   public Color getBackgroundAt(int var1) {
      return ((JTabbedPane.Page)this.pages.get(var1)).getBackground();
   }

   public Color getForegroundAt(int var1) {
      return ((JTabbedPane.Page)this.pages.get(var1)).getForeground();
   }

   public boolean isEnabledAt(int var1) {
      return ((JTabbedPane.Page)this.pages.get(var1)).isEnabled();
   }

   public Component getComponentAt(int var1) {
      return ((JTabbedPane.Page)this.pages.get(var1)).component;
   }

   public int getMnemonicAt(int var1) {
      this.checkIndex(var1);
      JTabbedPane.Page var2 = (JTabbedPane.Page)this.pages.get(var1);
      return var2.getMnemonic();
   }

   public int getDisplayedMnemonicIndexAt(int var1) {
      this.checkIndex(var1);
      JTabbedPane.Page var2 = (JTabbedPane.Page)this.pages.get(var1);
      return var2.getDisplayedMnemonicIndex();
   }

   public Rectangle getBoundsAt(int var1) {
      this.checkIndex(var1);
      return this.ui != null ? ((TabbedPaneUI)this.ui).getTabBounds(this, var1) : null;
   }

   public void setTitleAt(int var1, String var2) {
      JTabbedPane.Page var3 = (JTabbedPane.Page)this.pages.get(var1);
      String var4 = var3.title;
      var3.title = var2;
      if (var4 != var2) {
         this.firePropertyChange("indexForTitle", -1, var1);
      }

      var3.updateDisplayedMnemonicIndex();
      if (var4 != var2 && this.accessibleContext != null) {
         this.accessibleContext.firePropertyChange("AccessibleVisibleData", var4, var2);
      }

      if (var2 == null || var4 == null || !var2.equals(var4)) {
         this.revalidate();
         this.repaint();
      }

   }

   public void setIconAt(int var1, Icon var2) {
      JTabbedPane.Page var3 = (JTabbedPane.Page)this.pages.get(var1);
      Icon var4 = var3.icon;
      if (var2 != var4) {
         var3.icon = var2;
         if (var3.disabledIcon instanceof UIResource) {
            var3.disabledIcon = null;
         }

         if (this.accessibleContext != null) {
            this.accessibleContext.firePropertyChange("AccessibleVisibleData", var4, var2);
         }

         this.revalidate();
         this.repaint();
      }

   }

   public void setDisabledIconAt(int var1, Icon var2) {
      Icon var3 = ((JTabbedPane.Page)this.pages.get(var1)).disabledIcon;
      ((JTabbedPane.Page)this.pages.get(var1)).disabledIcon = var2;
      if (var2 != var3 && !this.isEnabledAt(var1)) {
         this.revalidate();
         this.repaint();
      }

   }

   public void setToolTipTextAt(int var1, String var2) {
      String var3 = ((JTabbedPane.Page)this.pages.get(var1)).tip;
      ((JTabbedPane.Page)this.pages.get(var1)).tip = var2;
      if (var3 != var2 && this.accessibleContext != null) {
         this.accessibleContext.firePropertyChange("AccessibleVisibleData", var3, var2);
      }

      if (!this.haveRegistered && var2 != null) {
         ToolTipManager.sharedInstance().registerComponent(this);
         this.haveRegistered = true;
      }

   }

   public void setBackgroundAt(int var1, Color var2) {
      Color var3 = ((JTabbedPane.Page)this.pages.get(var1)).background;
      ((JTabbedPane.Page)this.pages.get(var1)).setBackground(var2);
      if (var2 == null || var3 == null || !var2.equals(var3)) {
         Rectangle var4 = this.getBoundsAt(var1);
         if (var4 != null) {
            this.repaint(var4);
         }
      }

   }

   public void setForegroundAt(int var1, Color var2) {
      Color var3 = ((JTabbedPane.Page)this.pages.get(var1)).foreground;
      ((JTabbedPane.Page)this.pages.get(var1)).setForeground(var2);
      if (var2 == null || var3 == null || !var2.equals(var3)) {
         Rectangle var4 = this.getBoundsAt(var1);
         if (var4 != null) {
            this.repaint(var4);
         }
      }

   }

   public void setEnabledAt(int var1, boolean var2) {
      boolean var3 = ((JTabbedPane.Page)this.pages.get(var1)).isEnabled();
      ((JTabbedPane.Page)this.pages.get(var1)).setEnabled(var2);
      if (var2 != var3) {
         this.revalidate();
         this.repaint();
      }

   }

   public void setComponentAt(int var1, Component var2) {
      JTabbedPane.Page var3 = (JTabbedPane.Page)this.pages.get(var1);
      if (var2 != var3.component) {
         boolean var4 = false;
         if (var3.component != null) {
            var4 = SwingUtilities.findFocusOwner(var3.component) != null;
            synchronized(this.getTreeLock()) {
               int var6 = this.getComponentCount();
               Component[] var7 = this.getComponents();

               for(int var8 = 0; var8 < var6; ++var8) {
                  if (var7[var8] == var3.component) {
                     super.remove(var8);
                  }
               }
            }
         }

         var3.component = var2;
         boolean var5 = this.getSelectedIndex() == var1;
         if (var5) {
            this.visComp = var2;
         }

         if (var2 != null) {
            var2.setVisible(var5);
            this.addImpl(var2, (Object)null, -1);
            if (var4) {
               SwingUtilities2.tabbedPaneChangeFocusTo(var2);
            }
         } else {
            this.repaint();
         }

         this.revalidate();
      }

   }

   public void setDisplayedMnemonicIndexAt(int var1, int var2) {
      this.checkIndex(var1);
      JTabbedPane.Page var3 = (JTabbedPane.Page)this.pages.get(var1);
      var3.setDisplayedMnemonicIndex(var2);
   }

   public void setMnemonicAt(int var1, int var2) {
      this.checkIndex(var1);
      JTabbedPane.Page var3 = (JTabbedPane.Page)this.pages.get(var1);
      var3.setMnemonic(var2);
      this.firePropertyChange("mnemonicAt", (Object)null, (Object)null);
   }

   public int indexOfTab(String var1) {
      for(int var2 = 0; var2 < this.getTabCount(); ++var2) {
         if (this.getTitleAt(var2).equals(var1 == null ? "" : var1)) {
            return var2;
         }
      }

      return -1;
   }

   public int indexOfTab(Icon var1) {
      for(int var2 = 0; var2 < this.getTabCount(); ++var2) {
         Icon var3 = this.getIconAt(var2);
         if (var3 != null && var3.equals(var1) || var3 == null && var3 == var1) {
            return var2;
         }
      }

      return -1;
   }

   public int indexOfComponent(Component var1) {
      for(int var2 = 0; var2 < this.getTabCount(); ++var2) {
         Component var3 = this.getComponentAt(var2);
         if (var3 != null && var3.equals(var1) || var3 == null && var3 == var1) {
            return var2;
         }
      }

      return -1;
   }

   public int indexAtLocation(int var1, int var2) {
      return this.ui != null ? ((TabbedPaneUI)this.ui).tabForCoordinate(this, var1, var2) : -1;
   }

   public String getToolTipText(MouseEvent var1) {
      if (this.ui != null) {
         int var2 = ((TabbedPaneUI)this.ui).tabForCoordinate(this, var1.getX(), var1.getY());
         if (var2 != -1) {
            return ((JTabbedPane.Page)this.pages.get(var2)).tip;
         }
      }

      return super.getToolTipText(var1);
   }

   private void checkIndex(int var1) {
      if (var1 < 0 || var1 >= this.pages.size()) {
         throw new IndexOutOfBoundsException("Index: " + var1 + ", Tab count: " + this.pages.size());
      }
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.defaultWriteObject();
      if (this.getUIClassID().equals("TabbedPaneUI")) {
         byte var2 = JComponent.getWriteObjCounter(this);
         --var2;
         JComponent.setWriteObjCounter(this, var2);
         if (var2 == 0 && this.ui != null) {
            this.ui.installUI(this);
         }
      }

   }

   void compWriteObjectNotify() {
      super.compWriteObjectNotify();
      if (this.getToolTipText() == null && this.haveRegistered) {
         ToolTipManager.sharedInstance().unregisterComponent(this);
      }

   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      if (this.ui != null && this.getUIClassID().equals("TabbedPaneUI")) {
         this.ui.installUI(this);
      }

      if (this.getToolTipText() == null && this.haveRegistered) {
         ToolTipManager.sharedInstance().registerComponent(this);
      }

   }

   protected String paramString() {
      String var1;
      if (this.tabPlacement == 1) {
         var1 = "TOP";
      } else if (this.tabPlacement == 3) {
         var1 = "BOTTOM";
      } else if (this.tabPlacement == 2) {
         var1 = "LEFT";
      } else if (this.tabPlacement == 4) {
         var1 = "RIGHT";
      } else {
         var1 = "";
      }

      String var2 = this.haveRegistered ? "true" : "false";
      return super.paramString() + ",haveRegistered=" + var2 + ",tabPlacement=" + var1;
   }

   public AccessibleContext getAccessibleContext() {
      if (this.accessibleContext == null) {
         this.accessibleContext = new JTabbedPane.AccessibleJTabbedPane();
         int var1 = this.getTabCount();

         for(int var2 = 0; var2 < var1; ++var2) {
            ((JTabbedPane.Page)this.pages.get(var2)).initAccessibleContext();
         }
      }

      return this.accessibleContext;
   }

   public void setTabComponentAt(int var1, Component var2) {
      if (var2 != null && this.indexOfComponent(var2) != -1) {
         throw new IllegalArgumentException("Component is already added to this JTabbedPane");
      } else {
         Component var3 = this.getTabComponentAt(var1);
         if (var2 != var3) {
            int var4 = this.indexOfTabComponent(var2);
            if (var4 != -1) {
               this.setTabComponentAt(var4, (Component)null);
            }

            ((JTabbedPane.Page)this.pages.get(var1)).tabComponent = var2;
            this.firePropertyChange("indexForTabComponent", -1, var1);
         }

      }
   }

   public Component getTabComponentAt(int var1) {
      return ((JTabbedPane.Page)this.pages.get(var1)).tabComponent;
   }

   public int indexOfTabComponent(Component var1) {
      for(int var2 = 0; var2 < this.getTabCount(); ++var2) {
         Component var3 = this.getTabComponentAt(var2);
         if (var3 == var1) {
            return var2;
         }
      }

      return -1;
   }

   private class Page extends AccessibleContext implements Serializable, Accessible, AccessibleComponent {
      String title;
      Color background;
      Color foreground;
      Icon icon;
      Icon disabledIcon;
      JTabbedPane parent;
      Component component;
      String tip;
      boolean enabled = true;
      boolean needsUIUpdate;
      int mnemonic = -1;
      int mnemonicIndex = -1;
      Component tabComponent;

      Page(JTabbedPane var2, String var3, Icon var4, Icon var5, Component var6, String var7) {
         this.title = var3;
         this.icon = var4;
         this.disabledIcon = var5;
         this.parent = var2;
         this.setAccessibleParent(var2);
         this.component = var6;
         this.tip = var7;
         this.initAccessibleContext();
      }

      void initAccessibleContext() {
         if (JTabbedPane.this.accessibleContext != null && this.component instanceof Accessible) {
            AccessibleContext var1 = this.component.getAccessibleContext();
            if (var1 != null) {
               var1.setAccessibleParent(this);
            }
         }

      }

      void setMnemonic(int var1) {
         this.mnemonic = var1;
         this.updateDisplayedMnemonicIndex();
      }

      int getMnemonic() {
         return this.mnemonic;
      }

      void setDisplayedMnemonicIndex(int var1) {
         if (this.mnemonicIndex != var1) {
            if (var1 != -1 && (this.title == null || var1 < 0 || var1 >= this.title.length())) {
               throw new IllegalArgumentException("Invalid mnemonic index: " + var1);
            }

            this.mnemonicIndex = var1;
            JTabbedPane.this.firePropertyChange("displayedMnemonicIndexAt", (Object)null, (Object)null);
         }

      }

      int getDisplayedMnemonicIndex() {
         return this.mnemonicIndex;
      }

      void updateDisplayedMnemonicIndex() {
         this.setDisplayedMnemonicIndex(SwingUtilities.findDisplayedMnemonicIndex(this.title, this.mnemonic));
      }

      public AccessibleContext getAccessibleContext() {
         return this;
      }

      public String getAccessibleName() {
         if (this.accessibleName != null) {
            return this.accessibleName;
         } else {
            return this.title != null ? this.title : null;
         }
      }

      public String getAccessibleDescription() {
         if (this.accessibleDescription != null) {
            return this.accessibleDescription;
         } else {
            return this.tip != null ? this.tip : null;
         }
      }

      public AccessibleRole getAccessibleRole() {
         return AccessibleRole.PAGE_TAB;
      }

      public AccessibleStateSet getAccessibleStateSet() {
         AccessibleStateSet var1 = this.parent.getAccessibleContext().getAccessibleStateSet();
         var1.add(AccessibleState.SELECTABLE);
         int var2 = this.parent.indexOfTab(this.title);
         if (var2 == this.parent.getSelectedIndex()) {
            var1.add(AccessibleState.SELECTED);
         }

         return var1;
      }

      public int getAccessibleIndexInParent() {
         return this.parent.indexOfTab(this.title);
      }

      public int getAccessibleChildrenCount() {
         return this.component instanceof Accessible ? 1 : 0;
      }

      public Accessible getAccessibleChild(int var1) {
         return this.component instanceof Accessible ? (Accessible)this.component : null;
      }

      public Locale getLocale() {
         return this.parent.getLocale();
      }

      public AccessibleComponent getAccessibleComponent() {
         return this;
      }

      public Color getBackground() {
         return this.background != null ? this.background : this.parent.getBackground();
      }

      public void setBackground(Color var1) {
         this.background = var1;
      }

      public Color getForeground() {
         return this.foreground != null ? this.foreground : this.parent.getForeground();
      }

      public void setForeground(Color var1) {
         this.foreground = var1;
      }

      public Cursor getCursor() {
         return this.parent.getCursor();
      }

      public void setCursor(Cursor var1) {
         this.parent.setCursor(var1);
      }

      public Font getFont() {
         return this.parent.getFont();
      }

      public void setFont(Font var1) {
         this.parent.setFont(var1);
      }

      public FontMetrics getFontMetrics(Font var1) {
         return this.parent.getFontMetrics(var1);
      }

      public boolean isEnabled() {
         return this.enabled;
      }

      public void setEnabled(boolean var1) {
         this.enabled = var1;
      }

      public boolean isVisible() {
         return this.parent.isVisible();
      }

      public void setVisible(boolean var1) {
         this.parent.setVisible(var1);
      }

      public boolean isShowing() {
         return this.parent.isShowing();
      }

      public boolean contains(Point var1) {
         Rectangle var2 = this.getBounds();
         return var2.contains(var1);
      }

      public Point getLocationOnScreen() {
         Point var1 = this.parent.getLocationOnScreen();
         Point var2 = this.getLocation();
         var2.translate(var1.x, var1.y);
         return var2;
      }

      public Point getLocation() {
         Rectangle var1 = this.getBounds();
         return new Point(var1.x, var1.y);
      }

      public void setLocation(Point var1) {
      }

      public Rectangle getBounds() {
         return this.parent.getUI().getTabBounds(this.parent, this.parent.indexOfTab(this.title));
      }

      public void setBounds(Rectangle var1) {
      }

      public Dimension getSize() {
         Rectangle var1 = this.getBounds();
         return new Dimension(var1.width, var1.height);
      }

      public void setSize(Dimension var1) {
      }

      public Accessible getAccessibleAt(Point var1) {
         return this.component instanceof Accessible ? (Accessible)this.component : null;
      }

      public boolean isFocusTraversable() {
         return false;
      }

      public void requestFocus() {
      }

      public void addFocusListener(FocusListener var1) {
      }

      public void removeFocusListener(FocusListener var1) {
      }

      public AccessibleIcon[] getAccessibleIcon() {
         AccessibleIcon var1 = null;
         AccessibleContext var2;
         if (this.enabled && this.icon instanceof ImageIcon) {
            var2 = ((ImageIcon)this.icon).getAccessibleContext();
            var1 = (AccessibleIcon)var2;
         } else if (!this.enabled && this.disabledIcon instanceof ImageIcon) {
            var2 = ((ImageIcon)this.disabledIcon).getAccessibleContext();
            var1 = (AccessibleIcon)var2;
         }

         if (var1 != null) {
            AccessibleIcon[] var3 = new AccessibleIcon[]{var1};
            return var3;
         } else {
            return null;
         }
      }
   }

   protected class AccessibleJTabbedPane extends JComponent.AccessibleJComponent implements AccessibleSelection, ChangeListener {
      public String getAccessibleName() {
         if (this.accessibleName != null) {
            return this.accessibleName;
         } else {
            String var1 = (String)JTabbedPane.this.getClientProperty("AccessibleName");
            if (var1 != null) {
               return var1;
            } else {
               int var2 = JTabbedPane.this.getSelectedIndex();
               return var2 >= 0 ? ((JTabbedPane.Page)JTabbedPane.this.pages.get(var2)).getAccessibleName() : super.getAccessibleName();
            }
         }
      }

      public AccessibleJTabbedPane() {
         super();
         JTabbedPane.this.model.addChangeListener(this);
      }

      public void stateChanged(ChangeEvent var1) {
         Object var2 = var1.getSource();
         this.firePropertyChange("AccessibleSelection", (Object)null, var2);
      }

      public AccessibleRole getAccessibleRole() {
         return AccessibleRole.PAGE_TAB_LIST;
      }

      public int getAccessibleChildrenCount() {
         return JTabbedPane.this.getTabCount();
      }

      public Accessible getAccessibleChild(int var1) {
         return var1 >= 0 && var1 < JTabbedPane.this.getTabCount() ? (Accessible)JTabbedPane.this.pages.get(var1) : null;
      }

      public AccessibleSelection getAccessibleSelection() {
         return this;
      }

      public Accessible getAccessibleAt(Point var1) {
         int var2 = ((TabbedPaneUI)JTabbedPane.this.ui).tabForCoordinate(JTabbedPane.this, var1.x, var1.y);
         if (var2 == -1) {
            var2 = JTabbedPane.this.getSelectedIndex();
         }

         return this.getAccessibleChild(var2);
      }

      public int getAccessibleSelectionCount() {
         return 1;
      }

      public Accessible getAccessibleSelection(int var1) {
         int var2 = JTabbedPane.this.getSelectedIndex();
         return var2 == -1 ? null : (Accessible)JTabbedPane.this.pages.get(var2);
      }

      public boolean isAccessibleChildSelected(int var1) {
         return var1 == JTabbedPane.this.getSelectedIndex();
      }

      public void addAccessibleSelection(int var1) {
         JTabbedPane.this.setSelectedIndex(var1);
      }

      public void removeAccessibleSelection(int var1) {
      }

      public void clearAccessibleSelection() {
      }

      public void selectAllAccessibleSelection() {
      }
   }

   protected class ModelListener implements ChangeListener, Serializable {
      public void stateChanged(ChangeEvent var1) {
         JTabbedPane.this.fireStateChanged();
      }
   }
}
