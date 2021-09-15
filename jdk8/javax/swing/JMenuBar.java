package javax.swing;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.beans.Transient;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Vector;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import javax.accessibility.AccessibleSelection;
import javax.accessibility.AccessibleStateSet;
import javax.swing.plaf.MenuBarUI;

public class JMenuBar extends JComponent implements Accessible, MenuElement {
   private static final String uiClassID = "MenuBarUI";
   private transient SingleSelectionModel selectionModel;
   private boolean paintBorder = true;
   private Insets margin = null;
   private static final boolean TRACE = false;
   private static final boolean VERBOSE = false;
   private static final boolean DEBUG = false;

   public JMenuBar() {
      this.setFocusTraversalKeysEnabled(false);
      this.setSelectionModel(new DefaultSingleSelectionModel());
      this.updateUI();
   }

   public MenuBarUI getUI() {
      return (MenuBarUI)this.ui;
   }

   public void setUI(MenuBarUI var1) {
      super.setUI(var1);
   }

   public void updateUI() {
      this.setUI((MenuBarUI)UIManager.getUI(this));
   }

   public String getUIClassID() {
      return "MenuBarUI";
   }

   public SingleSelectionModel getSelectionModel() {
      return this.selectionModel;
   }

   public void setSelectionModel(SingleSelectionModel var1) {
      SingleSelectionModel var2 = this.selectionModel;
      this.selectionModel = var1;
      this.firePropertyChange("selectionModel", var2, this.selectionModel);
   }

   public JMenu add(JMenu var1) {
      super.add(var1);
      return var1;
   }

   public JMenu getMenu(int var1) {
      Component var2 = this.getComponentAtIndex(var1);
      return var2 instanceof JMenu ? (JMenu)var2 : null;
   }

   public int getMenuCount() {
      return this.getComponentCount();
   }

   public void setHelpMenu(JMenu var1) {
      throw new Error("setHelpMenu() not yet implemented.");
   }

   @Transient
   public JMenu getHelpMenu() {
      throw new Error("getHelpMenu() not yet implemented.");
   }

   /** @deprecated */
   @Deprecated
   public Component getComponentAtIndex(int var1) {
      return var1 >= 0 && var1 < this.getComponentCount() ? this.getComponent(var1) : null;
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

   public void setSelected(Component var1) {
      SingleSelectionModel var2 = this.getSelectionModel();
      int var3 = this.getComponentIndex(var1);
      var2.setSelectedIndex(var3);
   }

   public boolean isSelected() {
      return this.selectionModel.isSelected();
   }

   public boolean isBorderPainted() {
      return this.paintBorder;
   }

   public void setBorderPainted(boolean var1) {
      boolean var2 = this.paintBorder;
      this.paintBorder = var1;
      this.firePropertyChange("borderPainted", var2, this.paintBorder);
      if (var1 != var2) {
         this.revalidate();
         this.repaint();
      }

   }

   protected void paintBorder(Graphics var1) {
      if (this.isBorderPainted()) {
         super.paintBorder(var1);
      }

   }

   public void setMargin(Insets var1) {
      Insets var2 = this.margin;
      this.margin = var1;
      this.firePropertyChange("margin", var2, var1);
      if (var2 == null || !var2.equals(var1)) {
         this.revalidate();
         this.repaint();
      }

   }

   public Insets getMargin() {
      return this.margin == null ? new Insets(0, 0, 0, 0) : this.margin;
   }

   public void processMouseEvent(MouseEvent var1, MenuElement[] var2, MenuSelectionManager var3) {
   }

   public void processKeyEvent(KeyEvent var1, MenuElement[] var2, MenuSelectionManager var3) {
   }

   public void menuSelectionChanged(boolean var1) {
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

   protected String paramString() {
      String var1 = this.paintBorder ? "true" : "false";
      String var2 = this.margin != null ? this.margin.toString() : "";
      return super.paramString() + ",margin=" + var2 + ",paintBorder=" + var1;
   }

   public AccessibleContext getAccessibleContext() {
      if (this.accessibleContext == null) {
         this.accessibleContext = new JMenuBar.AccessibleJMenuBar();
      }

      return this.accessibleContext;
   }

   protected boolean processKeyBinding(KeyStroke var1, KeyEvent var2, int var3, boolean var4) {
      boolean var5 = super.processKeyBinding(var1, var2, var3, var4);
      if (!var5) {
         MenuElement[] var6 = this.getSubElements();
         MenuElement[] var7 = var6;
         int var8 = var6.length;

         for(int var9 = 0; var9 < var8; ++var9) {
            MenuElement var10 = var7[var9];
            if (processBindingForKeyStrokeRecursive(var10, var1, var2, var3, var4)) {
               return true;
            }
         }
      }

      return var5;
   }

   static boolean processBindingForKeyStrokeRecursive(MenuElement var0, KeyStroke var1, KeyEvent var2, int var3, boolean var4) {
      if (var0 == null) {
         return false;
      } else {
         Component var5 = var0.getComponent();
         if ((var5.isVisible() || var5 instanceof JPopupMenu) && var5.isEnabled()) {
            if (var5 != null && var5 instanceof JComponent && ((JComponent)var5).processKeyBinding(var1, var2, var3, var4)) {
               return true;
            } else {
               MenuElement[] var6 = var0.getSubElements();
               MenuElement[] var7 = var6;
               int var8 = var6.length;

               for(int var9 = 0; var9 < var8; ++var9) {
                  MenuElement var10 = var7[var9];
                  if (processBindingForKeyStrokeRecursive(var10, var1, var2, var3, var4)) {
                     return true;
                  }
               }

               return false;
            }
         } else {
            return false;
         }
      }
   }

   public void addNotify() {
      super.addNotify();
      KeyboardManager.getCurrentManager().registerMenuBar(this);
   }

   public void removeNotify() {
      super.removeNotify();
      KeyboardManager.getCurrentManager().unregisterMenuBar(this);
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.defaultWriteObject();
      if (this.getUIClassID().equals("MenuBarUI")) {
         byte var2 = JComponent.getWriteObjCounter(this);
         --var2;
         JComponent.setWriteObjCounter(this, var2);
         if (var2 == 0 && this.ui != null) {
            this.ui.installUI(this);
         }
      }

      Object[] var4 = new Object[4];
      byte var3 = 0;
      if (this.selectionModel instanceof Serializable) {
         int var5 = var3 + 1;
         var4[var3] = "selectionModel";
         var4[var5++] = this.selectionModel;
      }

      var1.writeObject(var4);
   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      Object[] var2 = (Object[])((Object[])var1.readObject());

      for(int var3 = 0; var3 < var2.length && var2[var3] != null; var3 += 2) {
         if (var2[var3].equals("selectionModel")) {
            this.selectionModel = (SingleSelectionModel)var2[var3 + 1];
         }
      }

   }

   protected class AccessibleJMenuBar extends JComponent.AccessibleJComponent implements AccessibleSelection {
      protected AccessibleJMenuBar() {
         super();
      }

      public AccessibleStateSet getAccessibleStateSet() {
         AccessibleStateSet var1 = super.getAccessibleStateSet();
         return var1;
      }

      public AccessibleRole getAccessibleRole() {
         return AccessibleRole.MENU_BAR;
      }

      public AccessibleSelection getAccessibleSelection() {
         return this;
      }

      public int getAccessibleSelectionCount() {
         return JMenuBar.this.isSelected() ? 1 : 0;
      }

      public Accessible getAccessibleSelection(int var1) {
         if (JMenuBar.this.isSelected()) {
            if (var1 != 0) {
               return null;
            }

            int var2 = JMenuBar.this.getSelectionModel().getSelectedIndex();
            if (JMenuBar.this.getComponentAtIndex(var2) instanceof Accessible) {
               return (Accessible)JMenuBar.this.getComponentAtIndex(var2);
            }
         }

         return null;
      }

      public boolean isAccessibleChildSelected(int var1) {
         return var1 == JMenuBar.this.getSelectionModel().getSelectedIndex();
      }

      public void addAccessibleSelection(int var1) {
         int var2 = JMenuBar.this.getSelectionModel().getSelectedIndex();
         if (var1 != var2) {
            JMenu var3;
            if (var2 >= 0 && var2 < JMenuBar.this.getMenuCount()) {
               var3 = JMenuBar.this.getMenu(var2);
               if (var3 != null) {
                  MenuSelectionManager.defaultManager().setSelectedPath((MenuElement[])null);
               }
            }

            JMenuBar.this.getSelectionModel().setSelectedIndex(var1);
            var3 = JMenuBar.this.getMenu(var1);
            if (var3 != null) {
               MenuElement[] var4 = new MenuElement[]{JMenuBar.this, var3, var3.getPopupMenu()};
               MenuSelectionManager.defaultManager().setSelectedPath(var4);
            }

         }
      }

      public void removeAccessibleSelection(int var1) {
         if (var1 >= 0 && var1 < JMenuBar.this.getMenuCount()) {
            JMenu var2 = JMenuBar.this.getMenu(var1);
            if (var2 != null) {
               MenuSelectionManager.defaultManager().setSelectedPath((MenuElement[])null);
            }

            JMenuBar.this.getSelectionModel().setSelectedIndex(-1);
         }

      }

      public void clearAccessibleSelection() {
         int var1 = JMenuBar.this.getSelectionModel().getSelectedIndex();
         if (var1 >= 0 && var1 < JMenuBar.this.getMenuCount()) {
            JMenu var2 = JMenuBar.this.getMenu(var1);
            if (var2 != null) {
               MenuSelectionManager.defaultManager().setSelectedPath((MenuElement[])null);
            }
         }

         JMenuBar.this.getSelectionModel().setSelectedIndex(-1);
      }

      public void selectAllAccessibleSelection() {
      }
   }
}
