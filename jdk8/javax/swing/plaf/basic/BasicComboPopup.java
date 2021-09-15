package javax.swing.plaf.basic;

import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import javax.accessibility.AccessibleContext;
import javax.swing.BoxLayout;
import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPopupMenu;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.MenuElement;
import javax.swing.MenuSelectionManager;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import sun.awt.AWTAccessor;

public class BasicComboPopup extends JPopupMenu implements ComboPopup {
   static final ListModel EmptyListModel = new BasicComboPopup.EmptyListModelClass();
   private static Border LIST_BORDER;
   protected JComboBox comboBox;
   protected JList list;
   protected JScrollPane scroller;
   protected boolean valueIsAdjusting = false;
   private BasicComboPopup.Handler handler;
   protected MouseMotionListener mouseMotionListener;
   protected MouseListener mouseListener;
   protected KeyListener keyListener;
   protected ListSelectionListener listSelectionListener;
   protected MouseListener listMouseListener;
   protected MouseMotionListener listMouseMotionListener;
   protected PropertyChangeListener propertyChangeListener;
   protected ListDataListener listDataListener;
   protected ItemListener itemListener;
   private MouseWheelListener scrollerMouseWheelListener;
   protected Timer autoscrollTimer;
   protected boolean hasEntered = false;
   protected boolean isAutoScrolling = false;
   protected int scrollDirection = 0;
   protected static final int SCROLL_UP = 0;
   protected static final int SCROLL_DOWN = 1;

   public void show() {
      this.comboBox.firePopupMenuWillBecomeVisible();
      this.setListSelection(this.comboBox.getSelectedIndex());
      Point var1 = this.getPopupLocation();
      this.show(this.comboBox, var1.x, var1.y);
   }

   public void hide() {
      MenuSelectionManager var1 = MenuSelectionManager.defaultManager();
      MenuElement[] var2 = var1.getSelectedPath();

      for(int var3 = 0; var3 < var2.length; ++var3) {
         if (var2[var3] == this) {
            var1.clearSelectedPath();
            break;
         }
      }

      if (var2.length > 0) {
         this.comboBox.repaint();
      }

   }

   public JList getList() {
      return this.list;
   }

   public MouseListener getMouseListener() {
      if (this.mouseListener == null) {
         this.mouseListener = this.createMouseListener();
      }

      return this.mouseListener;
   }

   public MouseMotionListener getMouseMotionListener() {
      if (this.mouseMotionListener == null) {
         this.mouseMotionListener = this.createMouseMotionListener();
      }

      return this.mouseMotionListener;
   }

   public KeyListener getKeyListener() {
      if (this.keyListener == null) {
         this.keyListener = this.createKeyListener();
      }

      return this.keyListener;
   }

   public void uninstallingUI() {
      if (this.propertyChangeListener != null) {
         this.comboBox.removePropertyChangeListener(this.propertyChangeListener);
      }

      if (this.itemListener != null) {
         this.comboBox.removeItemListener(this.itemListener);
      }

      this.uninstallComboBoxModelListeners(this.comboBox.getModel());
      this.uninstallKeyboardActions();
      this.uninstallListListeners();
      this.uninstallScrollerListeners();
      this.list.setModel(EmptyListModel);
   }

   protected void uninstallComboBoxModelListeners(ComboBoxModel var1) {
      if (var1 != null && this.listDataListener != null) {
         var1.removeListDataListener(this.listDataListener);
      }

   }

   protected void uninstallKeyboardActions() {
   }

   public BasicComboPopup(JComboBox var1) {
      this.setName("ComboPopup.popup");
      this.comboBox = var1;
      this.setLightWeightPopupEnabled(this.comboBox.isLightWeightPopupEnabled());
      this.list = this.createList();
      this.list.setName("ComboBox.list");
      this.configureList();
      this.scroller = this.createScroller();
      this.scroller.setName("ComboBox.scrollPane");
      this.configureScroller();
      this.configurePopup();
      this.installComboBoxListeners();
      this.installKeyboardActions();
   }

   protected void firePopupMenuWillBecomeVisible() {
      if (this.scrollerMouseWheelListener != null) {
         this.comboBox.addMouseWheelListener(this.scrollerMouseWheelListener);
      }

      super.firePopupMenuWillBecomeVisible();
   }

   protected void firePopupMenuWillBecomeInvisible() {
      if (this.scrollerMouseWheelListener != null) {
         this.comboBox.removeMouseWheelListener(this.scrollerMouseWheelListener);
      }

      super.firePopupMenuWillBecomeInvisible();
      this.comboBox.firePopupMenuWillBecomeInvisible();
   }

   protected void firePopupMenuCanceled() {
      if (this.scrollerMouseWheelListener != null) {
         this.comboBox.removeMouseWheelListener(this.scrollerMouseWheelListener);
      }

      super.firePopupMenuCanceled();
      this.comboBox.firePopupMenuCanceled();
   }

   protected MouseListener createMouseListener() {
      return this.getHandler();
   }

   protected MouseMotionListener createMouseMotionListener() {
      return this.getHandler();
   }

   protected KeyListener createKeyListener() {
      return null;
   }

   protected ListSelectionListener createListSelectionListener() {
      return null;
   }

   protected ListDataListener createListDataListener() {
      return null;
   }

   protected MouseListener createListMouseListener() {
      return this.getHandler();
   }

   protected MouseMotionListener createListMouseMotionListener() {
      return this.getHandler();
   }

   protected PropertyChangeListener createPropertyChangeListener() {
      return this.getHandler();
   }

   protected ItemListener createItemListener() {
      return this.getHandler();
   }

   private BasicComboPopup.Handler getHandler() {
      if (this.handler == null) {
         this.handler = new BasicComboPopup.Handler();
      }

      return this.handler;
   }

   protected JList createList() {
      return new JList(this.comboBox.getModel()) {
         public void processMouseEvent(MouseEvent var1) {
            if (BasicGraphicsUtils.isMenuShortcutKeyDown(var1)) {
               Toolkit var2 = Toolkit.getDefaultToolkit();
               MouseEvent var3 = new MouseEvent((Component)var1.getSource(), var1.getID(), var1.getWhen(), var1.getModifiers() ^ var2.getMenuShortcutKeyMask(), var1.getX(), var1.getY(), var1.getXOnScreen(), var1.getYOnScreen(), var1.getClickCount(), var1.isPopupTrigger(), 0);
               AWTAccessor.MouseEventAccessor var4 = AWTAccessor.getMouseEventAccessor();
               var4.setCausedByTouchEvent(var3, var4.isCausedByTouchEvent(var1));
               var1 = var3;
            }

            super.processMouseEvent(var1);
         }
      };
   }

   protected void configureList() {
      this.list.setFont(this.comboBox.getFont());
      this.list.setForeground(this.comboBox.getForeground());
      this.list.setBackground(this.comboBox.getBackground());
      this.list.setSelectionForeground(UIManager.getColor("ComboBox.selectionForeground"));
      this.list.setSelectionBackground(UIManager.getColor("ComboBox.selectionBackground"));
      this.list.setBorder((Border)null);
      this.list.setCellRenderer(this.comboBox.getRenderer());
      this.list.setFocusable(false);
      this.list.setSelectionMode(0);
      this.setListSelection(this.comboBox.getSelectedIndex());
      this.installListListeners();
   }

   protected void installListListeners() {
      if ((this.listMouseListener = this.createListMouseListener()) != null) {
         this.list.addMouseListener(this.listMouseListener);
      }

      if ((this.listMouseMotionListener = this.createListMouseMotionListener()) != null) {
         this.list.addMouseMotionListener(this.listMouseMotionListener);
      }

      if ((this.listSelectionListener = this.createListSelectionListener()) != null) {
         this.list.addListSelectionListener(this.listSelectionListener);
      }

   }

   void uninstallListListeners() {
      if (this.listMouseListener != null) {
         this.list.removeMouseListener(this.listMouseListener);
         this.listMouseListener = null;
      }

      if (this.listMouseMotionListener != null) {
         this.list.removeMouseMotionListener(this.listMouseMotionListener);
         this.listMouseMotionListener = null;
      }

      if (this.listSelectionListener != null) {
         this.list.removeListSelectionListener(this.listSelectionListener);
         this.listSelectionListener = null;
      }

      this.handler = null;
   }

   protected JScrollPane createScroller() {
      JScrollPane var1 = new JScrollPane(this.list, 20, 31);
      var1.setHorizontalScrollBar((JScrollBar)null);
      return var1;
   }

   protected void configureScroller() {
      this.scroller.setFocusable(false);
      this.scroller.getVerticalScrollBar().setFocusable(false);
      this.scroller.setBorder((Border)null);
      this.installScrollerListeners();
   }

   protected void configurePopup() {
      this.setLayout(new BoxLayout(this, 1));
      this.setBorderPainted(true);
      this.setBorder(LIST_BORDER);
      this.setOpaque(false);
      this.add(this.scroller);
      this.setDoubleBuffered(true);
      this.setFocusable(false);
   }

   private void installScrollerListeners() {
      this.scrollerMouseWheelListener = this.getHandler();
      if (this.scrollerMouseWheelListener != null) {
         this.scroller.addMouseWheelListener(this.scrollerMouseWheelListener);
      }

   }

   private void uninstallScrollerListeners() {
      if (this.scrollerMouseWheelListener != null) {
         this.scroller.removeMouseWheelListener(this.scrollerMouseWheelListener);
         this.scrollerMouseWheelListener = null;
      }

   }

   protected void installComboBoxListeners() {
      if ((this.propertyChangeListener = this.createPropertyChangeListener()) != null) {
         this.comboBox.addPropertyChangeListener(this.propertyChangeListener);
      }

      if ((this.itemListener = this.createItemListener()) != null) {
         this.comboBox.addItemListener(this.itemListener);
      }

      this.installComboBoxModelListeners(this.comboBox.getModel());
   }

   protected void installComboBoxModelListeners(ComboBoxModel var1) {
      if (var1 != null && (this.listDataListener = this.createListDataListener()) != null) {
         var1.addListDataListener(this.listDataListener);
      }

   }

   protected void installKeyboardActions() {
   }

   public boolean isFocusTraversable() {
      return false;
   }

   protected void startAutoScrolling(int var1) {
      if (this.isAutoScrolling) {
         this.autoscrollTimer.stop();
      }

      this.isAutoScrolling = true;
      if (var1 == 0) {
         this.scrollDirection = 0;
         Point var2 = SwingUtilities.convertPoint(this.scroller, new Point(1, 1), this.list);
         int var3 = this.list.locationToIndex(var2);
         this.list.setSelectedIndex(var3);
         this.autoscrollTimer = new Timer(100, new BasicComboPopup.AutoScrollActionHandler(0));
      } else if (var1 == 1) {
         this.scrollDirection = 1;
         Dimension var5 = this.scroller.getSize();
         Point var6 = SwingUtilities.convertPoint(this.scroller, new Point(1, var5.height - 1 - 2), this.list);
         int var4 = this.list.locationToIndex(var6);
         this.list.setSelectedIndex(var4);
         this.autoscrollTimer = new Timer(100, new BasicComboPopup.AutoScrollActionHandler(1));
      }

      this.autoscrollTimer.start();
   }

   protected void stopAutoScrolling() {
      this.isAutoScrolling = false;
      if (this.autoscrollTimer != null) {
         this.autoscrollTimer.stop();
         this.autoscrollTimer = null;
      }

   }

   protected void autoScrollUp() {
      int var1 = this.list.getSelectedIndex();
      if (var1 > 0) {
         this.list.setSelectedIndex(var1 - 1);
         this.list.ensureIndexIsVisible(var1 - 1);
      }

   }

   protected void autoScrollDown() {
      int var1 = this.list.getSelectedIndex();
      int var2 = this.list.getModel().getSize() - 1;
      if (var1 < var2) {
         this.list.setSelectedIndex(var1 + 1);
         this.list.ensureIndexIsVisible(var1 + 1);
      }

   }

   public AccessibleContext getAccessibleContext() {
      AccessibleContext var1 = super.getAccessibleContext();
      var1.setAccessibleParent(this.comboBox);
      return var1;
   }

   protected void delegateFocus(MouseEvent var1) {
      if (this.comboBox.isEditable()) {
         Component var2 = this.comboBox.getEditor().getEditorComponent();
         if (!(var2 instanceof JComponent) || ((JComponent)var2).isRequestFocusEnabled()) {
            var2.requestFocus();
         }
      } else if (this.comboBox.isRequestFocusEnabled()) {
         this.comboBox.requestFocus();
      }

   }

   protected void togglePopup() {
      if (this.isVisible()) {
         this.hide();
      } else {
         this.show();
      }

   }

   private void setListSelection(int var1) {
      if (var1 == -1) {
         this.list.clearSelection();
      } else {
         this.list.setSelectedIndex(var1);
         this.list.ensureIndexIsVisible(var1);
      }

   }

   protected MouseEvent convertMouseEvent(MouseEvent var1) {
      Point var2 = SwingUtilities.convertPoint((Component)var1.getSource(), var1.getPoint(), this.list);
      MouseEvent var3 = new MouseEvent((Component)var1.getSource(), var1.getID(), var1.getWhen(), var1.getModifiers(), var2.x, var2.y, var1.getXOnScreen(), var1.getYOnScreen(), var1.getClickCount(), var1.isPopupTrigger(), 0);
      AWTAccessor.MouseEventAccessor var4 = AWTAccessor.getMouseEventAccessor();
      var4.setCausedByTouchEvent(var3, var4.isCausedByTouchEvent(var1));
      return var3;
   }

   protected int getPopupHeightForRowCount(int var1) {
      int var2 = Math.min(var1, this.comboBox.getItemCount());
      int var3 = 0;
      ListCellRenderer var4 = this.list.getCellRenderer();
      Object var5 = null;

      for(int var6 = 0; var6 < var2; ++var6) {
         var5 = this.list.getModel().getElementAt(var6);
         Component var7 = var4.getListCellRendererComponent(this.list, var5, var6, false, false);
         var3 += var7.getPreferredSize().height;
      }

      if (var3 == 0) {
         var3 = this.comboBox.getHeight();
      }

      Border var8 = this.scroller.getViewportBorder();
      Insets var9;
      if (var8 != null) {
         var9 = var8.getBorderInsets((Component)null);
         var3 += var9.top + var9.bottom;
      }

      var8 = this.scroller.getBorder();
      if (var8 != null) {
         var9 = var8.getBorderInsets((Component)null);
         var3 += var9.top + var9.bottom;
      }

      return var3;
   }

   protected Rectangle computePopupBounds(int var1, int var2, int var3, int var4) {
      Toolkit var5 = Toolkit.getDefaultToolkit();
      GraphicsConfiguration var7 = this.comboBox.getGraphicsConfiguration();
      Point var8 = new Point();
      SwingUtilities.convertPointFromScreen(var8, this.comboBox);
      Rectangle var6;
      if (var7 != null) {
         Insets var9 = var5.getScreenInsets(var7);
         var6 = var7.getBounds();
         var6.width -= var9.left + var9.right;
         var6.height -= var9.top + var9.bottom;
         var6.x += var8.x + var9.left;
         var6.y += var8.y + var9.top;
      } else {
         var6 = new Rectangle(var8, var5.getScreenSize());
      }

      Rectangle var10 = new Rectangle(var1, var2, var3, var4);
      if (var2 + var4 > var6.y + var6.height && var4 < var6.height) {
         var10.y = -var10.height;
      }

      return var10;
   }

   private Point getPopupLocation() {
      Dimension var1 = this.comboBox.getSize();
      Insets var2 = this.getInsets();
      var1.setSize(var1.width - (var2.right + var2.left), this.getPopupHeightForRowCount(this.comboBox.getMaximumRowCount()));
      Rectangle var3 = this.computePopupBounds(0, this.comboBox.getBounds().height, var1.width, var1.height);
      Dimension var4 = var3.getSize();
      Point var5 = var3.getLocation();
      this.scroller.setMaximumSize(var4);
      this.scroller.setPreferredSize(var4);
      this.scroller.setMinimumSize(var4);
      this.list.revalidate();
      return var5;
   }

   protected void updateListBoxSelectionForEvent(MouseEvent var1, boolean var2) {
      Point var3 = var1.getPoint();
      if (this.list != null) {
         int var4 = this.list.locationToIndex(var3);
         if (var4 == -1) {
            if (var3.y < 0) {
               var4 = 0;
            } else {
               var4 = this.comboBox.getModel().getSize() - 1;
            }
         }

         if (this.list.getSelectedIndex() != var4) {
            this.list.setSelectedIndex(var4);
            if (var2) {
               this.list.ensureIndexIsVisible(var4);
            }
         }

      }
   }

   static {
      LIST_BORDER = new LineBorder(Color.BLACK, 1);
   }

   private class Handler implements ItemListener, MouseListener, MouseMotionListener, MouseWheelListener, PropertyChangeListener, Serializable {
      private Handler() {
      }

      public void mouseClicked(MouseEvent var1) {
      }

      public void mousePressed(MouseEvent var1) {
         if (var1.getSource() != BasicComboPopup.this.list) {
            if (SwingUtilities.isLeftMouseButton(var1) && BasicComboPopup.this.comboBox.isEnabled()) {
               if (BasicComboPopup.this.comboBox.isEditable()) {
                  Component var2 = BasicComboPopup.this.comboBox.getEditor().getEditorComponent();
                  if (!(var2 instanceof JComponent) || ((JComponent)var2).isRequestFocusEnabled()) {
                     var2.requestFocus();
                  }
               } else if (BasicComboPopup.this.comboBox.isRequestFocusEnabled()) {
                  BasicComboPopup.this.comboBox.requestFocus();
               }

               BasicComboPopup.this.togglePopup();
            }
         }
      }

      public void mouseReleased(MouseEvent var1) {
         if (var1.getSource() == BasicComboPopup.this.list) {
            if (BasicComboPopup.this.list.getModel().getSize() > 0) {
               if (BasicComboPopup.this.comboBox.getSelectedIndex() == BasicComboPopup.this.list.getSelectedIndex()) {
                  BasicComboPopup.this.comboBox.getEditor().setItem(BasicComboPopup.this.list.getSelectedValue());
               }

               BasicComboPopup.this.comboBox.setSelectedIndex(BasicComboPopup.this.list.getSelectedIndex());
            }

            BasicComboPopup.this.comboBox.setPopupVisible(false);
            if (BasicComboPopup.this.comboBox.isEditable() && BasicComboPopup.this.comboBox.getEditor() != null) {
               BasicComboPopup.this.comboBox.configureEditor(BasicComboPopup.this.comboBox.getEditor(), BasicComboPopup.this.comboBox.getSelectedItem());
            }

         } else {
            Component var2 = (Component)var1.getSource();
            Dimension var3 = var2.getSize();
            Rectangle var4 = new Rectangle(0, 0, var3.width - 1, var3.height - 1);
            if (!var4.contains(var1.getPoint())) {
               MouseEvent var5 = BasicComboPopup.this.convertMouseEvent(var1);
               Point var6 = var5.getPoint();
               Rectangle var7 = new Rectangle();
               BasicComboPopup.this.list.computeVisibleRect(var7);
               if (var7.contains(var6)) {
                  if (BasicComboPopup.this.comboBox.getSelectedIndex() == BasicComboPopup.this.list.getSelectedIndex()) {
                     BasicComboPopup.this.comboBox.getEditor().setItem(BasicComboPopup.this.list.getSelectedValue());
                  }

                  BasicComboPopup.this.comboBox.setSelectedIndex(BasicComboPopup.this.list.getSelectedIndex());
               }

               BasicComboPopup.this.comboBox.setPopupVisible(false);
            }

            BasicComboPopup.this.hasEntered = false;
            BasicComboPopup.this.stopAutoScrolling();
         }
      }

      public void mouseEntered(MouseEvent var1) {
      }

      public void mouseExited(MouseEvent var1) {
      }

      public void mouseMoved(MouseEvent var1) {
         if (var1.getSource() == BasicComboPopup.this.list) {
            Point var2 = var1.getPoint();
            Rectangle var3 = new Rectangle();
            BasicComboPopup.this.list.computeVisibleRect(var3);
            if (var3.contains(var2)) {
               BasicComboPopup.this.updateListBoxSelectionForEvent(var1, false);
            }
         }

      }

      public void mouseDragged(MouseEvent var1) {
         if (var1.getSource() != BasicComboPopup.this.list) {
            if (BasicComboPopup.this.isVisible()) {
               MouseEvent var2 = BasicComboPopup.this.convertMouseEvent(var1);
               Rectangle var3 = new Rectangle();
               BasicComboPopup.this.list.computeVisibleRect(var3);
               if (var2.getPoint().y >= var3.y) {
                  int var10001 = var3.y + var3.height;
                  if (var2.getPoint().y <= var10001 - 1) {
                     BasicComboPopup.this.hasEntered = true;
                     if (BasicComboPopup.this.isAutoScrolling) {
                        BasicComboPopup.this.stopAutoScrolling();
                     }

                     Point var5 = var2.getPoint();
                     if (var3.contains(var5)) {
                        BasicComboPopup.this.updateListBoxSelectionForEvent(var2, false);
                     }

                     return;
                  }
               }

               if (BasicComboPopup.this.hasEntered) {
                  int var4 = var2.getPoint().y < var3.y ? 0 : 1;
                  if (BasicComboPopup.this.isAutoScrolling && BasicComboPopup.this.scrollDirection != var4) {
                     BasicComboPopup.this.stopAutoScrolling();
                     BasicComboPopup.this.startAutoScrolling(var4);
                  } else if (!BasicComboPopup.this.isAutoScrolling) {
                     BasicComboPopup.this.startAutoScrolling(var4);
                  }
               } else if (var1.getPoint().y < 0) {
                  BasicComboPopup.this.hasEntered = true;
                  BasicComboPopup.this.startAutoScrolling(0);
               }
            }

         }
      }

      public void propertyChange(PropertyChangeEvent var1) {
         JComboBox var2 = (JComboBox)var1.getSource();
         String var3 = var1.getPropertyName();
         if (var3 == "model") {
            ComboBoxModel var4 = (ComboBoxModel)var1.getOldValue();
            ComboBoxModel var5 = (ComboBoxModel)var1.getNewValue();
            BasicComboPopup.this.uninstallComboBoxModelListeners(var4);
            BasicComboPopup.this.installComboBoxModelListeners(var5);
            BasicComboPopup.this.list.setModel(var5);
            if (BasicComboPopup.this.isVisible()) {
               BasicComboPopup.this.hide();
            }
         } else if (var3 == "renderer") {
            BasicComboPopup.this.list.setCellRenderer(var2.getRenderer());
            if (BasicComboPopup.this.isVisible()) {
               BasicComboPopup.this.hide();
            }
         } else if (var3 == "componentOrientation") {
            ComponentOrientation var7 = (ComponentOrientation)var1.getNewValue();
            JList var6 = BasicComboPopup.this.getList();
            if (var6 != null && var6.getComponentOrientation() != var7) {
               var6.setComponentOrientation(var7);
            }

            if (BasicComboPopup.this.scroller != null && BasicComboPopup.this.scroller.getComponentOrientation() != var7) {
               BasicComboPopup.this.scroller.setComponentOrientation(var7);
            }

            if (var7 != BasicComboPopup.this.getComponentOrientation()) {
               BasicComboPopup.this.setComponentOrientation(var7);
            }
         } else if (var3 == "lightWeightPopupEnabled") {
            BasicComboPopup.this.setLightWeightPopupEnabled(var2.isLightWeightPopupEnabled());
         }

      }

      public void itemStateChanged(ItemEvent var1) {
         if (var1.getStateChange() == 1) {
            JComboBox var2 = (JComboBox)var1.getSource();
            BasicComboPopup.this.setListSelection(var2.getSelectedIndex());
         } else {
            BasicComboPopup.this.setListSelection(-1);
         }

      }

      public void mouseWheelMoved(MouseWheelEvent var1) {
         var1.consume();
      }

      // $FF: synthetic method
      Handler(Object var2) {
         this();
      }
   }

   private class AutoScrollActionHandler implements ActionListener {
      private int direction;

      AutoScrollActionHandler(int var2) {
         this.direction = var2;
      }

      public void actionPerformed(ActionEvent var1) {
         if (this.direction == 0) {
            BasicComboPopup.this.autoScrollUp();
         } else {
            BasicComboPopup.this.autoScrollDown();
         }

      }
   }

   protected class PropertyChangeHandler implements PropertyChangeListener {
      public void propertyChange(PropertyChangeEvent var1) {
         BasicComboPopup.this.getHandler().propertyChange(var1);
      }
   }

   protected class ItemHandler implements ItemListener {
      public void itemStateChanged(ItemEvent var1) {
         BasicComboPopup.this.getHandler().itemStateChanged(var1);
      }
   }

   protected class ListMouseMotionHandler extends MouseMotionAdapter {
      public void mouseMoved(MouseEvent var1) {
         BasicComboPopup.this.getHandler().mouseMoved(var1);
      }
   }

   protected class ListMouseHandler extends MouseAdapter {
      public void mousePressed(MouseEvent var1) {
      }

      public void mouseReleased(MouseEvent var1) {
         BasicComboPopup.this.getHandler().mouseReleased(var1);
      }
   }

   public class ListDataHandler implements ListDataListener {
      public void contentsChanged(ListDataEvent var1) {
      }

      public void intervalAdded(ListDataEvent var1) {
      }

      public void intervalRemoved(ListDataEvent var1) {
      }
   }

   protected class ListSelectionHandler implements ListSelectionListener {
      public void valueChanged(ListSelectionEvent var1) {
      }
   }

   public class InvocationKeyHandler extends KeyAdapter {
      public void keyReleased(KeyEvent var1) {
      }
   }

   protected class InvocationMouseMotionHandler extends MouseMotionAdapter {
      public void mouseDragged(MouseEvent var1) {
         BasicComboPopup.this.getHandler().mouseDragged(var1);
      }
   }

   protected class InvocationMouseHandler extends MouseAdapter {
      public void mousePressed(MouseEvent var1) {
         BasicComboPopup.this.getHandler().mousePressed(var1);
      }

      public void mouseReleased(MouseEvent var1) {
         BasicComboPopup.this.getHandler().mouseReleased(var1);
      }
   }

   private static class EmptyListModelClass implements ListModel<Object>, Serializable {
      private EmptyListModelClass() {
      }

      public int getSize() {
         return 0;
      }

      public Object getElementAt(int var1) {
         return null;
      }

      public void addListDataListener(ListDataListener var1) {
      }

      public void removeListDataListener(ListDataListener var1) {
      }

      // $FF: synthetic method
      EmptyListModelClass(Object var1) {
         this();
      }
   }
}
