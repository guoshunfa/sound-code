package javax.swing.plaf.basic;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.CellRendererPane;
import javax.swing.ComboBoxEditor;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.KeyStroke;
import javax.swing.ListCellRenderer;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.plaf.ComboBoxUI;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.text.Position;
import sun.awt.AppContext;
import sun.swing.DefaultLookup;
import sun.swing.UIAction;

public class BasicComboBoxUI extends ComboBoxUI {
   protected JComboBox comboBox;
   protected boolean hasFocus = false;
   private boolean isTableCellEditor = false;
   private static final String IS_TABLE_CELL_EDITOR = "JComboBox.isTableCellEditor";
   protected JList listBox;
   protected CellRendererPane currentValuePane = new CellRendererPane();
   protected ComboPopup popup;
   protected Component editor;
   protected JButton arrowButton;
   protected KeyListener keyListener;
   protected FocusListener focusListener;
   protected PropertyChangeListener propertyChangeListener;
   protected ItemListener itemListener;
   protected MouseListener popupMouseListener;
   protected MouseMotionListener popupMouseMotionListener;
   protected KeyListener popupKeyListener;
   protected ListDataListener listDataListener;
   private BasicComboBoxUI.Handler handler;
   private long timeFactor = 1000L;
   private long lastTime = 0L;
   private long time = 0L;
   JComboBox.KeySelectionManager keySelectionManager;
   protected boolean isMinimumSizeDirty = true;
   protected Dimension cachedMinimumSize = new Dimension(0, 0);
   private boolean isDisplaySizeDirty = true;
   private Dimension cachedDisplaySize = new Dimension(0, 0);
   private static final Object COMBO_UI_LIST_CELL_RENDERER_KEY = new StringBuffer("DefaultListCellRendererKey");
   static final StringBuffer HIDE_POPUP_KEY = new StringBuffer("HidePopupKey");
   private boolean sameBaseline;
   protected boolean squareButton = true;
   protected Insets padding;

   private static ListCellRenderer getDefaultListCellRenderer() {
      Object var0 = (ListCellRenderer)AppContext.getAppContext().get(COMBO_UI_LIST_CELL_RENDERER_KEY);
      if (var0 == null) {
         var0 = new DefaultListCellRenderer();
         AppContext.getAppContext().put(COMBO_UI_LIST_CELL_RENDERER_KEY, new DefaultListCellRenderer());
      }

      return (ListCellRenderer)var0;
   }

   static void loadActionMap(LazyActionMap var0) {
      var0.put(new BasicComboBoxUI.Actions("hidePopup"));
      var0.put(new BasicComboBoxUI.Actions("pageDownPassThrough"));
      var0.put(new BasicComboBoxUI.Actions("pageUpPassThrough"));
      var0.put(new BasicComboBoxUI.Actions("homePassThrough"));
      var0.put(new BasicComboBoxUI.Actions("endPassThrough"));
      var0.put(new BasicComboBoxUI.Actions("selectNext"));
      var0.put(new BasicComboBoxUI.Actions("selectNext2"));
      var0.put(new BasicComboBoxUI.Actions("togglePopup"));
      var0.put(new BasicComboBoxUI.Actions("spacePopup"));
      var0.put(new BasicComboBoxUI.Actions("selectPrevious"));
      var0.put(new BasicComboBoxUI.Actions("selectPrevious2"));
      var0.put(new BasicComboBoxUI.Actions("enterPressed"));
   }

   public static ComponentUI createUI(JComponent var0) {
      return new BasicComboBoxUI();
   }

   public void installUI(JComponent var1) {
      this.isMinimumSizeDirty = true;
      this.comboBox = (JComboBox)var1;
      this.installDefaults();
      this.popup = this.createPopup();
      this.listBox = this.popup.getList();
      Boolean var2 = (Boolean)var1.getClientProperty("JComboBox.isTableCellEditor");
      if (var2 != null) {
         this.isTableCellEditor = var2.equals(Boolean.TRUE);
      }

      if (this.comboBox.getRenderer() == null || this.comboBox.getRenderer() instanceof UIResource) {
         this.comboBox.setRenderer(this.createRenderer());
      }

      if (this.comboBox.getEditor() == null || this.comboBox.getEditor() instanceof UIResource) {
         this.comboBox.setEditor(this.createEditor());
      }

      this.installListeners();
      this.installComponents();
      this.comboBox.setLayout(this.createLayoutManager());
      this.comboBox.setRequestFocusEnabled(true);
      this.installKeyboardActions();
      this.comboBox.putClientProperty("doNotCancelPopup", HIDE_POPUP_KEY);
      if (this.keySelectionManager == null || this.keySelectionManager instanceof UIResource) {
         this.keySelectionManager = new BasicComboBoxUI.DefaultKeySelectionManager();
      }

      this.comboBox.setKeySelectionManager(this.keySelectionManager);
   }

   public void uninstallUI(JComponent var1) {
      this.setPopupVisible(this.comboBox, false);
      this.popup.uninstallingUI();
      this.uninstallKeyboardActions();
      this.comboBox.setLayout((LayoutManager)null);
      this.uninstallComponents();
      this.uninstallListeners();
      this.uninstallDefaults();
      if (this.comboBox.getRenderer() == null || this.comboBox.getRenderer() instanceof UIResource) {
         this.comboBox.setRenderer((ListCellRenderer)null);
      }

      ComboBoxEditor var2 = this.comboBox.getEditor();
      if (var2 instanceof UIResource) {
         if (var2.getEditorComponent().hasFocus()) {
            this.comboBox.requestFocusInWindow();
         }

         this.comboBox.setEditor((ComboBoxEditor)null);
      }

      if (this.keySelectionManager instanceof UIResource) {
         this.comboBox.setKeySelectionManager((JComboBox.KeySelectionManager)null);
      }

      this.handler = null;
      this.keyListener = null;
      this.focusListener = null;
      this.listDataListener = null;
      this.propertyChangeListener = null;
      this.popup = null;
      this.listBox = null;
      this.comboBox = null;
   }

   protected void installDefaults() {
      LookAndFeel.installColorsAndFont(this.comboBox, "ComboBox.background", "ComboBox.foreground", "ComboBox.font");
      LookAndFeel.installBorder(this.comboBox, "ComboBox.border");
      LookAndFeel.installProperty(this.comboBox, "opaque", Boolean.TRUE);
      Long var1 = (Long)UIManager.get("ComboBox.timeFactor");
      this.timeFactor = var1 == null ? 1000L : var1;
      Boolean var2 = (Boolean)UIManager.get("ComboBox.squareButton");
      this.squareButton = var2 == null ? true : var2;
      this.padding = UIManager.getInsets("ComboBox.padding");
   }

   protected void installListeners() {
      if ((this.itemListener = this.createItemListener()) != null) {
         this.comboBox.addItemListener(this.itemListener);
      }

      if ((this.propertyChangeListener = this.createPropertyChangeListener()) != null) {
         this.comboBox.addPropertyChangeListener(this.propertyChangeListener);
      }

      if ((this.keyListener = this.createKeyListener()) != null) {
         this.comboBox.addKeyListener(this.keyListener);
      }

      if ((this.focusListener = this.createFocusListener()) != null) {
         this.comboBox.addFocusListener(this.focusListener);
      }

      if ((this.popupMouseListener = this.popup.getMouseListener()) != null) {
         this.comboBox.addMouseListener(this.popupMouseListener);
      }

      if ((this.popupMouseMotionListener = this.popup.getMouseMotionListener()) != null) {
         this.comboBox.addMouseMotionListener(this.popupMouseMotionListener);
      }

      if ((this.popupKeyListener = this.popup.getKeyListener()) != null) {
         this.comboBox.addKeyListener(this.popupKeyListener);
      }

      if (this.comboBox.getModel() != null && (this.listDataListener = this.createListDataListener()) != null) {
         this.comboBox.getModel().addListDataListener(this.listDataListener);
      }

   }

   protected void uninstallDefaults() {
      LookAndFeel.installColorsAndFont(this.comboBox, "ComboBox.background", "ComboBox.foreground", "ComboBox.font");
      LookAndFeel.uninstallBorder(this.comboBox);
   }

   protected void uninstallListeners() {
      if (this.keyListener != null) {
         this.comboBox.removeKeyListener(this.keyListener);
      }

      if (this.itemListener != null) {
         this.comboBox.removeItemListener(this.itemListener);
      }

      if (this.propertyChangeListener != null) {
         this.comboBox.removePropertyChangeListener(this.propertyChangeListener);
      }

      if (this.focusListener != null) {
         this.comboBox.removeFocusListener(this.focusListener);
      }

      if (this.popupMouseListener != null) {
         this.comboBox.removeMouseListener(this.popupMouseListener);
      }

      if (this.popupMouseMotionListener != null) {
         this.comboBox.removeMouseMotionListener(this.popupMouseMotionListener);
      }

      if (this.popupKeyListener != null) {
         this.comboBox.removeKeyListener(this.popupKeyListener);
      }

      if (this.comboBox.getModel() != null && this.listDataListener != null) {
         this.comboBox.getModel().removeListDataListener(this.listDataListener);
      }

   }

   protected ComboPopup createPopup() {
      return new BasicComboPopup(this.comboBox);
   }

   protected KeyListener createKeyListener() {
      return this.getHandler();
   }

   protected FocusListener createFocusListener() {
      return this.getHandler();
   }

   protected ListDataListener createListDataListener() {
      return this.getHandler();
   }

   protected ItemListener createItemListener() {
      return null;
   }

   protected PropertyChangeListener createPropertyChangeListener() {
      return this.getHandler();
   }

   protected LayoutManager createLayoutManager() {
      return this.getHandler();
   }

   protected ListCellRenderer createRenderer() {
      return new BasicComboBoxRenderer.UIResource();
   }

   protected ComboBoxEditor createEditor() {
      return new BasicComboBoxEditor.UIResource();
   }

   private BasicComboBoxUI.Handler getHandler() {
      if (this.handler == null) {
         this.handler = new BasicComboBoxUI.Handler();
      }

      return this.handler;
   }

   private void updateToolTipTextForChildren() {
      Component[] var1 = this.comboBox.getComponents();

      for(int var2 = 0; var2 < var1.length; ++var2) {
         if (var1[var2] instanceof JComponent) {
            ((JComponent)var1[var2]).setToolTipText(this.comboBox.getToolTipText());
         }
      }

   }

   protected void installComponents() {
      this.arrowButton = this.createArrowButton();
      if (this.arrowButton != null) {
         this.comboBox.add(this.arrowButton);
         this.configureArrowButton();
      }

      if (this.comboBox.isEditable()) {
         this.addEditor();
      }

      this.comboBox.add(this.currentValuePane);
   }

   protected void uninstallComponents() {
      if (this.arrowButton != null) {
         this.unconfigureArrowButton();
      }

      if (this.editor != null) {
         this.unconfigureEditor();
      }

      this.comboBox.removeAll();
      this.arrowButton = null;
   }

   public void addEditor() {
      this.removeEditor();
      this.editor = this.comboBox.getEditor().getEditorComponent();
      if (this.editor != null) {
         this.configureEditor();
         this.comboBox.add(this.editor);
         if (this.comboBox.isFocusOwner()) {
            this.editor.requestFocusInWindow();
         }
      }

   }

   public void removeEditor() {
      if (this.editor != null) {
         this.unconfigureEditor();
         this.comboBox.remove(this.editor);
         this.editor = null;
      }

   }

   protected void configureEditor() {
      this.editor.setEnabled(this.comboBox.isEnabled());
      this.editor.setFocusable(this.comboBox.isFocusable());
      this.editor.setFont(this.comboBox.getFont());
      if (this.focusListener != null) {
         this.editor.addFocusListener(this.focusListener);
      }

      this.editor.addFocusListener(this.getHandler());
      this.comboBox.getEditor().addActionListener(this.getHandler());
      if (this.editor instanceof JComponent) {
         ((JComponent)this.editor).putClientProperty("doNotCancelPopup", HIDE_POPUP_KEY);
         ((JComponent)this.editor).setInheritsPopupMenu(true);
      }

      this.comboBox.configureEditor(this.comboBox.getEditor(), this.comboBox.getSelectedItem());
      this.editor.addPropertyChangeListener(this.propertyChangeListener);
   }

   protected void unconfigureEditor() {
      if (this.focusListener != null) {
         this.editor.removeFocusListener(this.focusListener);
      }

      this.editor.removePropertyChangeListener(this.propertyChangeListener);
      this.editor.removeFocusListener(this.getHandler());
      this.comboBox.getEditor().removeActionListener(this.getHandler());
   }

   public void configureArrowButton() {
      if (this.arrowButton != null) {
         this.arrowButton.setEnabled(this.comboBox.isEnabled());
         this.arrowButton.setFocusable(this.comboBox.isFocusable());
         this.arrowButton.setRequestFocusEnabled(false);
         this.arrowButton.addMouseListener(this.popup.getMouseListener());
         this.arrowButton.addMouseMotionListener(this.popup.getMouseMotionListener());
         this.arrowButton.resetKeyboardActions();
         this.arrowButton.putClientProperty("doNotCancelPopup", HIDE_POPUP_KEY);
         this.arrowButton.setInheritsPopupMenu(true);
      }

   }

   public void unconfigureArrowButton() {
      if (this.arrowButton != null) {
         this.arrowButton.removeMouseListener(this.popup.getMouseListener());
         this.arrowButton.removeMouseMotionListener(this.popup.getMouseMotionListener());
      }

   }

   protected JButton createArrowButton() {
      BasicArrowButton var1 = new BasicArrowButton(5, UIManager.getColor("ComboBox.buttonBackground"), UIManager.getColor("ComboBox.buttonShadow"), UIManager.getColor("ComboBox.buttonDarkShadow"), UIManager.getColor("ComboBox.buttonHighlight"));
      var1.setName("ComboBox.arrowButton");
      return var1;
   }

   public boolean isPopupVisible(JComboBox var1) {
      return this.popup.isVisible();
   }

   public void setPopupVisible(JComboBox var1, boolean var2) {
      if (var2) {
         this.popup.show();
      } else {
         this.popup.hide();
      }

   }

   public boolean isFocusTraversable(JComboBox var1) {
      return !this.comboBox.isEditable();
   }

   public void paint(Graphics var1, JComponent var2) {
      this.hasFocus = this.comboBox.hasFocus();
      if (!this.comboBox.isEditable()) {
         Rectangle var3 = this.rectangleForCurrentValue();
         this.paintCurrentValueBackground(var1, var3, this.hasFocus);
         this.paintCurrentValue(var1, var3, this.hasFocus);
      }

   }

   public Dimension getPreferredSize(JComponent var1) {
      return this.getMinimumSize(var1);
   }

   public Dimension getMinimumSize(JComponent var1) {
      if (!this.isMinimumSizeDirty) {
         return new Dimension(this.cachedMinimumSize);
      } else {
         Dimension var2 = this.getDisplaySize();
         Insets var3 = this.getInsets();
         int var4 = var2.height;
         int var5 = this.squareButton ? var4 : this.arrowButton.getPreferredSize().width;
         var2.height += var3.top + var3.bottom;
         var2.width += var3.left + var3.right + var5;
         this.cachedMinimumSize.setSize(var2.width, var2.height);
         this.isMinimumSizeDirty = false;
         return new Dimension(var2);
      }
   }

   public Dimension getMaximumSize(JComponent var1) {
      return new Dimension(32767, 32767);
   }

   public int getBaseline(JComponent var1, int var2, int var3) {
      super.getBaseline(var1, var2, var3);
      int var4 = -1;
      this.getDisplaySize();
      if (this.sameBaseline) {
         Insets var5 = var1.getInsets();
         var3 = var3 - var5.top - var5.bottom;
         if (this.comboBox.isEditable()) {
            var4 = this.editor.getBaseline(var2, var3);
         } else {
            Object var6 = this.comboBox.getRenderer();
            if (var6 == null) {
               var6 = new DefaultListCellRenderer();
            }

            Object var7 = null;
            Object var8 = this.comboBox.getPrototypeDisplayValue();
            if (var8 != null) {
               var7 = var8;
            } else if (this.comboBox.getModel().getSize() > 0) {
               var7 = this.comboBox.getModel().getElementAt(0);
            }

            Component var9 = ((ListCellRenderer)var6).getListCellRendererComponent(this.listBox, var7, -1, false, false);
            if (var9 instanceof JLabel) {
               JLabel var10 = (JLabel)var9;
               String var11 = var10.getText();
               if (var11 == null || var11.isEmpty()) {
                  var10.setText(" ");
               }
            }

            if (var9 instanceof JComponent) {
               var9.setFont(this.comboBox.getFont());
            }

            var4 = var9.getBaseline(var2, var3);
         }

         if (var4 > 0) {
            var4 += var5.top;
         }
      }

      return var4;
   }

   public Component.BaselineResizeBehavior getBaselineResizeBehavior(JComponent var1) {
      super.getBaselineResizeBehavior(var1);
      this.getDisplaySize();
      if (this.comboBox.isEditable()) {
         return this.editor.getBaselineResizeBehavior();
      } else {
         if (this.sameBaseline) {
            Object var2 = this.comboBox.getRenderer();
            if (var2 == null) {
               var2 = new DefaultListCellRenderer();
            }

            Object var3 = null;
            Object var4 = this.comboBox.getPrototypeDisplayValue();
            if (var4 != null) {
               var3 = var4;
            } else if (this.comboBox.getModel().getSize() > 0) {
               var3 = this.comboBox.getModel().getElementAt(0);
            }

            if (var3 != null) {
               Component var5 = ((ListCellRenderer)var2).getListCellRendererComponent(this.listBox, var3, -1, false, false);
               return var5.getBaselineResizeBehavior();
            }
         }

         return Component.BaselineResizeBehavior.OTHER;
      }
   }

   public int getAccessibleChildrenCount(JComponent var1) {
      return this.comboBox.isEditable() ? 2 : 1;
   }

   public Accessible getAccessibleChild(JComponent var1, int var2) {
      AccessibleContext var3;
      switch(var2) {
      case 0:
         if (this.popup instanceof Accessible) {
            var3 = ((Accessible)this.popup).getAccessibleContext();
            var3.setAccessibleParent(this.comboBox);
            return (Accessible)this.popup;
         }
         break;
      case 1:
         if (this.comboBox.isEditable() && this.editor instanceof Accessible) {
            var3 = ((Accessible)this.editor).getAccessibleContext();
            var3.setAccessibleParent(this.comboBox);
            return (Accessible)this.editor;
         }
      }

      return null;
   }

   protected boolean isNavigationKey(int var1) {
      return var1 == 38 || var1 == 40 || var1 == 224 || var1 == 225;
   }

   private boolean isNavigationKey(int var1, int var2) {
      InputMap var3 = this.comboBox.getInputMap(1);
      KeyStroke var4 = KeyStroke.getKeyStroke(var1, var2);
      return var3 != null && var3.get(var4) != null;
   }

   protected void selectNextPossibleValue() {
      int var1;
      if (this.comboBox.isPopupVisible()) {
         var1 = this.listBox.getSelectedIndex();
      } else {
         var1 = this.comboBox.getSelectedIndex();
      }

      if (var1 < this.comboBox.getModel().getSize() - 1) {
         this.listBox.setSelectedIndex(var1 + 1);
         this.listBox.ensureIndexIsVisible(var1 + 1);
         if (!this.isTableCellEditor && (!UIManager.getBoolean("ComboBox.noActionOnKeyNavigation") || !this.comboBox.isPopupVisible())) {
            this.comboBox.setSelectedIndex(var1 + 1);
         }

         this.comboBox.repaint();
      }

   }

   protected void selectPreviousPossibleValue() {
      int var1;
      if (this.comboBox.isPopupVisible()) {
         var1 = this.listBox.getSelectedIndex();
      } else {
         var1 = this.comboBox.getSelectedIndex();
      }

      if (var1 > 0) {
         this.listBox.setSelectedIndex(var1 - 1);
         this.listBox.ensureIndexIsVisible(var1 - 1);
         if (!this.isTableCellEditor && (!UIManager.getBoolean("ComboBox.noActionOnKeyNavigation") || !this.comboBox.isPopupVisible())) {
            this.comboBox.setSelectedIndex(var1 - 1);
         }

         this.comboBox.repaint();
      }

   }

   protected void toggleOpenClose() {
      this.setPopupVisible(this.comboBox, !this.isPopupVisible(this.comboBox));
   }

   protected Rectangle rectangleForCurrentValue() {
      int var1 = this.comboBox.getWidth();
      int var2 = this.comboBox.getHeight();
      Insets var3 = this.getInsets();
      int var4 = var2 - (var3.top + var3.bottom);
      if (this.arrowButton != null) {
         var4 = this.arrowButton.getWidth();
      }

      return BasicGraphicsUtils.isLeftToRight(this.comboBox) ? new Rectangle(var3.left, var3.top, var1 - (var3.left + var3.right + var4), var2 - (var3.top + var3.bottom)) : new Rectangle(var3.left + var4, var3.top, var1 - (var3.left + var3.right + var4), var2 - (var3.top + var3.bottom));
   }

   protected Insets getInsets() {
      return this.comboBox.getInsets();
   }

   public void paintCurrentValue(Graphics var1, Rectangle var2, boolean var3) {
      ListCellRenderer var4 = this.comboBox.getRenderer();
      Component var5;
      if (var3 && !this.isPopupVisible(this.comboBox)) {
         var5 = var4.getListCellRendererComponent(this.listBox, this.comboBox.getSelectedItem(), -1, true, false);
      } else {
         var5 = var4.getListCellRendererComponent(this.listBox, this.comboBox.getSelectedItem(), -1, false, false);
         var5.setBackground(UIManager.getColor("ComboBox.background"));
      }

      var5.setFont(this.comboBox.getFont());
      if (var3 && !this.isPopupVisible(this.comboBox)) {
         var5.setForeground(this.listBox.getSelectionForeground());
         var5.setBackground(this.listBox.getSelectionBackground());
      } else if (this.comboBox.isEnabled()) {
         var5.setForeground(this.comboBox.getForeground());
         var5.setBackground(this.comboBox.getBackground());
      } else {
         var5.setForeground(DefaultLookup.getColor(this.comboBox, this, "ComboBox.disabledForeground", (Color)null));
         var5.setBackground(DefaultLookup.getColor(this.comboBox, this, "ComboBox.disabledBackground", (Color)null));
      }

      boolean var6 = false;
      if (var5 instanceof JPanel) {
         var6 = true;
      }

      int var7 = var2.x;
      int var8 = var2.y;
      int var9 = var2.width;
      int var10 = var2.height;
      if (this.padding != null) {
         var7 = var2.x + this.padding.left;
         var8 = var2.y + this.padding.top;
         var9 = var2.width - (this.padding.left + this.padding.right);
         var10 = var2.height - (this.padding.top + this.padding.bottom);
      }

      this.currentValuePane.paintComponent(var1, var5, this.comboBox, var7, var8, var9, var10, var6);
   }

   public void paintCurrentValueBackground(Graphics var1, Rectangle var2, boolean var3) {
      Color var4 = var1.getColor();
      if (this.comboBox.isEnabled()) {
         var1.setColor(DefaultLookup.getColor(this.comboBox, this, "ComboBox.background", (Color)null));
      } else {
         var1.setColor(DefaultLookup.getColor(this.comboBox, this, "ComboBox.disabledBackground", (Color)null));
      }

      var1.fillRect(var2.x, var2.y, var2.width, var2.height);
      var1.setColor(var4);
   }

   void repaintCurrentValue() {
      Rectangle var1 = this.rectangleForCurrentValue();
      this.comboBox.repaint(var1.x, var1.y, var1.width, var1.height);
   }

   protected Dimension getDefaultSize() {
      Dimension var1 = this.getSizeForComponent(getDefaultListCellRenderer().getListCellRendererComponent(this.listBox, " ", -1, false, false));
      return new Dimension(var1.width, var1.height);
   }

   protected Dimension getDisplaySize() {
      if (!this.isDisplaySizeDirty) {
         return new Dimension(this.cachedDisplaySize);
      } else {
         Dimension var1 = new Dimension();
         Object var2 = this.comboBox.getRenderer();
         if (var2 == null) {
            var2 = new DefaultListCellRenderer();
         }

         this.sameBaseline = true;
         Object var3 = this.comboBox.getPrototypeDisplayValue();
         if (var3 != null) {
            var1 = this.getSizeForComponent(((ListCellRenderer)var2).getListCellRendererComponent(this.listBox, var3, -1, false, false));
         } else {
            ComboBoxModel var4 = this.comboBox.getModel();
            int var5 = var4.getSize();
            int var6 = -1;
            if (var5 > 0) {
               for(int var9 = 0; var9 < var5; ++var9) {
                  Object var10 = var4.getElementAt(var9);
                  Component var11 = ((ListCellRenderer)var2).getListCellRendererComponent(this.listBox, var10, -1, false, false);
                  Dimension var7 = this.getSizeForComponent(var11);
                  if (this.sameBaseline && var10 != null && (!(var10 instanceof String) || !"".equals(var10))) {
                     int var12 = var11.getBaseline(var7.width, var7.height);
                     if (var12 == -1) {
                        this.sameBaseline = false;
                     } else if (var6 == -1) {
                        var6 = var12;
                     } else if (var6 != var12) {
                        this.sameBaseline = false;
                     }
                  }

                  var1.width = Math.max(var1.width, var7.width);
                  var1.height = Math.max(var1.height, var7.height);
               }
            } else {
               var1 = this.getDefaultSize();
               if (this.comboBox.isEditable()) {
                  var1.width = 100;
               }
            }
         }

         if (this.comboBox.isEditable()) {
            Dimension var13 = this.editor.getPreferredSize();
            var1.width = Math.max(var1.width, var13.width);
            var1.height = Math.max(var1.height, var13.height);
         }

         if (this.padding != null) {
            var1.width += this.padding.left + this.padding.right;
            var1.height += this.padding.top + this.padding.bottom;
         }

         this.cachedDisplaySize.setSize(var1.width, var1.height);
         this.isDisplaySizeDirty = false;
         return var1;
      }
   }

   protected Dimension getSizeForComponent(Component var1) {
      this.currentValuePane.add(var1);
      var1.setFont(this.comboBox.getFont());
      Dimension var2 = var1.getPreferredSize();
      this.currentValuePane.remove(var1);
      return var2;
   }

   protected void installKeyboardActions() {
      InputMap var1 = this.getInputMap(1);
      SwingUtilities.replaceUIInputMap(this.comboBox, 1, var1);
      LazyActionMap.installLazyActionMap(this.comboBox, BasicComboBoxUI.class, "ComboBox.actionMap");
   }

   InputMap getInputMap(int var1) {
      return var1 == 1 ? (InputMap)DefaultLookup.get(this.comboBox, this, "ComboBox.ancestorInputMap") : null;
   }

   boolean isTableCellEditor() {
      return this.isTableCellEditor;
   }

   protected void uninstallKeyboardActions() {
      SwingUtilities.replaceUIInputMap(this.comboBox, 1, (InputMap)null);
      SwingUtilities.replaceUIActionMap(this.comboBox, (ActionMap)null);
   }

   class DefaultKeySelectionManager implements JComboBox.KeySelectionManager, UIResource {
      private String prefix = "";
      private String typedString = "";

      public int selectionForKey(char var1, ComboBoxModel var2) {
         if (BasicComboBoxUI.this.lastTime == 0L) {
            this.prefix = "";
            this.typedString = "";
         }

         boolean var3 = true;
         int var4 = BasicComboBoxUI.this.comboBox.getSelectedIndex();
         if (BasicComboBoxUI.this.time - BasicComboBoxUI.this.lastTime < BasicComboBoxUI.this.timeFactor) {
            this.typedString = this.typedString + var1;
            if (this.prefix.length() == 1 && var1 == this.prefix.charAt(0)) {
               ++var4;
            } else {
               this.prefix = this.typedString;
            }
         } else {
            ++var4;
            this.typedString = "" + var1;
            this.prefix = this.typedString;
         }

         BasicComboBoxUI.this.lastTime = BasicComboBoxUI.this.time;
         if (var4 < 0 || var4 >= var2.getSize()) {
            var3 = false;
            var4 = 0;
         }

         int var5 = BasicComboBoxUI.this.listBox.getNextMatch(this.prefix, var4, Position.Bias.Forward);
         if (var5 < 0 && var3) {
            var5 = BasicComboBoxUI.this.listBox.getNextMatch(this.prefix, 0, Position.Bias.Forward);
         }

         return var5;
      }
   }

   private class Handler implements ActionListener, FocusListener, KeyListener, LayoutManager, ListDataListener, PropertyChangeListener {
      private Handler() {
      }

      public void propertyChange(PropertyChangeEvent var1) {
         String var2 = var1.getPropertyName();
         if (var1.getSource() == BasicComboBoxUI.this.editor) {
            if ("border".equals(var2)) {
               BasicComboBoxUI.this.isMinimumSizeDirty = true;
               BasicComboBoxUI.this.isDisplaySizeDirty = true;
               BasicComboBoxUI.this.comboBox.revalidate();
            }
         } else {
            JComboBox var3 = (JComboBox)var1.getSource();
            if (var2 == "model") {
               ComboBoxModel var4 = (ComboBoxModel)var1.getNewValue();
               ComboBoxModel var5 = (ComboBoxModel)var1.getOldValue();
               if (var5 != null && BasicComboBoxUI.this.listDataListener != null) {
                  var5.removeListDataListener(BasicComboBoxUI.this.listDataListener);
               }

               if (var4 != null && BasicComboBoxUI.this.listDataListener != null) {
                  var4.addListDataListener(BasicComboBoxUI.this.listDataListener);
               }

               if (BasicComboBoxUI.this.editor != null) {
                  var3.configureEditor(var3.getEditor(), var3.getSelectedItem());
               }

               BasicComboBoxUI.this.isMinimumSizeDirty = true;
               BasicComboBoxUI.this.isDisplaySizeDirty = true;
               var3.revalidate();
               var3.repaint();
            } else if (var2 == "editor" && var3.isEditable()) {
               BasicComboBoxUI.this.addEditor();
               var3.revalidate();
            } else if (var2 == "editable") {
               if (var3.isEditable()) {
                  var3.setRequestFocusEnabled(false);
                  BasicComboBoxUI.this.addEditor();
               } else {
                  var3.setRequestFocusEnabled(true);
                  BasicComboBoxUI.this.removeEditor();
               }

               BasicComboBoxUI.this.updateToolTipTextForChildren();
               var3.revalidate();
            } else {
               boolean var6;
               if (var2 == "enabled") {
                  var6 = var3.isEnabled();
                  if (BasicComboBoxUI.this.editor != null) {
                     BasicComboBoxUI.this.editor.setEnabled(var6);
                  }

                  if (BasicComboBoxUI.this.arrowButton != null) {
                     BasicComboBoxUI.this.arrowButton.setEnabled(var6);
                  }

                  var3.repaint();
               } else if (var2 == "focusable") {
                  var6 = var3.isFocusable();
                  if (BasicComboBoxUI.this.editor != null) {
                     BasicComboBoxUI.this.editor.setFocusable(var6);
                  }

                  if (BasicComboBoxUI.this.arrowButton != null) {
                     BasicComboBoxUI.this.arrowButton.setFocusable(var6);
                  }

                  var3.repaint();
               } else if (var2 == "maximumRowCount") {
                  if (BasicComboBoxUI.this.isPopupVisible(var3)) {
                     BasicComboBoxUI.this.setPopupVisible(var3, false);
                     BasicComboBoxUI.this.setPopupVisible(var3, true);
                  }
               } else if (var2 == "font") {
                  BasicComboBoxUI.this.listBox.setFont(var3.getFont());
                  if (BasicComboBoxUI.this.editor != null) {
                     BasicComboBoxUI.this.editor.setFont(var3.getFont());
                  }

                  BasicComboBoxUI.this.isMinimumSizeDirty = true;
                  BasicComboBoxUI.this.isDisplaySizeDirty = true;
                  var3.validate();
               } else if (var2 == "ToolTipText") {
                  BasicComboBoxUI.this.updateToolTipTextForChildren();
               } else if (var2 == "JComboBox.isTableCellEditor") {
                  Boolean var7 = (Boolean)var1.getNewValue();
                  BasicComboBoxUI.this.isTableCellEditor = var7.equals(Boolean.TRUE);
               } else if (var2 == "prototypeDisplayValue") {
                  BasicComboBoxUI.this.isMinimumSizeDirty = true;
                  BasicComboBoxUI.this.isDisplaySizeDirty = true;
                  var3.revalidate();
               } else if (var2 == "renderer") {
                  BasicComboBoxUI.this.isMinimumSizeDirty = true;
                  BasicComboBoxUI.this.isDisplaySizeDirty = true;
                  var3.revalidate();
               }
            }
         }

      }

      public void keyPressed(KeyEvent var1) {
         if (BasicComboBoxUI.this.isNavigationKey(var1.getKeyCode(), var1.getModifiers())) {
            BasicComboBoxUI.this.lastTime = 0L;
         } else if (BasicComboBoxUI.this.comboBox.isEnabled() && BasicComboBoxUI.this.comboBox.getModel().getSize() != 0 && this.isTypeAheadKey(var1) && var1.getKeyChar() != '\uffff') {
            BasicComboBoxUI.this.time = var1.getWhen();
            if (BasicComboBoxUI.this.comboBox.selectWithKeyChar(var1.getKeyChar())) {
               var1.consume();
            }
         }

      }

      public void keyTyped(KeyEvent var1) {
      }

      public void keyReleased(KeyEvent var1) {
      }

      private boolean isTypeAheadKey(KeyEvent var1) {
         return !var1.isAltDown() && !BasicGraphicsUtils.isMenuShortcutKeyDown(var1);
      }

      public void focusGained(FocusEvent var1) {
         ComboBoxEditor var2 = BasicComboBoxUI.this.comboBox.getEditor();
         if (var2 == null || var1.getSource() != var2.getEditorComponent()) {
            BasicComboBoxUI.this.hasFocus = true;
            BasicComboBoxUI.this.comboBox.repaint();
            if (BasicComboBoxUI.this.comboBox.isEditable() && BasicComboBoxUI.this.editor != null) {
               BasicComboBoxUI.this.editor.requestFocus();
            }

         }
      }

      public void focusLost(FocusEvent var1) {
         ComboBoxEditor var2 = BasicComboBoxUI.this.comboBox.getEditor();
         if (var2 != null && var1.getSource() == var2.getEditorComponent()) {
            Object var3 = var2.getItem();
            Object var4 = BasicComboBoxUI.this.comboBox.getSelectedItem();
            if (!var1.isTemporary() && var3 != null && !var3.equals(var4 == null ? "" : var4)) {
               BasicComboBoxUI.this.comboBox.actionPerformed(new ActionEvent(var2, 0, "", EventQueue.getMostRecentEventTime(), 0));
            }
         }

         BasicComboBoxUI.this.hasFocus = false;
         if (!var1.isTemporary()) {
            BasicComboBoxUI.this.setPopupVisible(BasicComboBoxUI.this.comboBox, false);
         }

         BasicComboBoxUI.this.comboBox.repaint();
      }

      public void contentsChanged(ListDataEvent var1) {
         if (var1.getIndex0() != -1 || var1.getIndex1() != -1) {
            BasicComboBoxUI.this.isMinimumSizeDirty = true;
            BasicComboBoxUI.this.comboBox.revalidate();
         }

         if (BasicComboBoxUI.this.comboBox.isEditable() && BasicComboBoxUI.this.editor != null) {
            BasicComboBoxUI.this.comboBox.configureEditor(BasicComboBoxUI.this.comboBox.getEditor(), BasicComboBoxUI.this.comboBox.getSelectedItem());
         }

         BasicComboBoxUI.this.isDisplaySizeDirty = true;
         BasicComboBoxUI.this.comboBox.repaint();
      }

      public void intervalAdded(ListDataEvent var1) {
         this.contentsChanged(var1);
      }

      public void intervalRemoved(ListDataEvent var1) {
         this.contentsChanged(var1);
      }

      public void addLayoutComponent(String var1, Component var2) {
      }

      public void removeLayoutComponent(Component var1) {
      }

      public Dimension preferredLayoutSize(Container var1) {
         return var1.getPreferredSize();
      }

      public Dimension minimumLayoutSize(Container var1) {
         return var1.getMinimumSize();
      }

      public void layoutContainer(Container var1) {
         JComboBox var2 = (JComboBox)var1;
         int var3 = var2.getWidth();
         int var4 = var2.getHeight();
         Insets var5 = BasicComboBoxUI.this.getInsets();
         int var6 = var4 - (var5.top + var5.bottom);
         int var7 = var6;
         if (BasicComboBoxUI.this.arrowButton != null) {
            Insets var8 = BasicComboBoxUI.this.arrowButton.getInsets();
            var7 = BasicComboBoxUI.this.squareButton ? var6 : BasicComboBoxUI.this.arrowButton.getPreferredSize().width + var8.left + var8.right;
         }

         if (BasicComboBoxUI.this.arrowButton != null) {
            if (BasicGraphicsUtils.isLeftToRight(var2)) {
               BasicComboBoxUI.this.arrowButton.setBounds(var3 - (var5.right + var7), var5.top, var7, var6);
            } else {
               BasicComboBoxUI.this.arrowButton.setBounds(var5.left, var5.top, var7, var6);
            }
         }

         if (BasicComboBoxUI.this.editor != null) {
            Rectangle var9 = BasicComboBoxUI.this.rectangleForCurrentValue();
            BasicComboBoxUI.this.editor.setBounds(var9);
         }

      }

      public void actionPerformed(ActionEvent var1) {
         Object var2 = BasicComboBoxUI.this.comboBox.getEditor().getItem();
         if (var2 != null) {
            if (!BasicComboBoxUI.this.comboBox.isPopupVisible() && !var2.equals(BasicComboBoxUI.this.comboBox.getSelectedItem())) {
               BasicComboBoxUI.this.comboBox.setSelectedItem(BasicComboBoxUI.this.comboBox.getEditor().getItem());
            }

            ActionMap var3 = BasicComboBoxUI.this.comboBox.getActionMap();
            if (var3 != null) {
               Action var4 = var3.get("enterPressed");
               if (var4 != null) {
                  var4.actionPerformed(new ActionEvent(BasicComboBoxUI.this.comboBox, var1.getID(), var1.getActionCommand(), var1.getModifiers()));
               }
            }
         }

      }

      // $FF: synthetic method
      Handler(Object var2) {
         this();
      }
   }

   private static class Actions extends UIAction {
      private static final String HIDE = "hidePopup";
      private static final String DOWN = "selectNext";
      private static final String DOWN_2 = "selectNext2";
      private static final String TOGGLE = "togglePopup";
      private static final String TOGGLE_2 = "spacePopup";
      private static final String UP = "selectPrevious";
      private static final String UP_2 = "selectPrevious2";
      private static final String ENTER = "enterPressed";
      private static final String PAGE_DOWN = "pageDownPassThrough";
      private static final String PAGE_UP = "pageUpPassThrough";
      private static final String HOME = "homePassThrough";
      private static final String END = "endPassThrough";

      Actions(String var1) {
         super(var1);
      }

      public void actionPerformed(ActionEvent var1) {
         String var2 = this.getName();
         JComboBox var3 = (JComboBox)var1.getSource();
         BasicComboBoxUI var4 = (BasicComboBoxUI)BasicLookAndFeel.getUIOfType(var3.getUI(), BasicComboBoxUI.class);
         if (var2 == "hidePopup") {
            var3.firePopupMenuCanceled();
            var3.setPopupVisible(false);
         } else if (var2 != "pageDownPassThrough" && var2 != "pageUpPassThrough" && var2 != "homePassThrough" && var2 != "endPassThrough") {
            if (var2 == "selectNext") {
               if (var3.isShowing()) {
                  if (var3.isPopupVisible()) {
                     if (var4 != null) {
                        var4.selectNextPossibleValue();
                     }
                  } else {
                     var3.setPopupVisible(true);
                  }
               }
            } else if (var2 == "selectNext2") {
               if (var3.isShowing()) {
                  if ((var3.isEditable() || var4 != null && var4.isTableCellEditor()) && !var3.isPopupVisible()) {
                     var3.setPopupVisible(true);
                  } else if (var4 != null) {
                     var4.selectNextPossibleValue();
                  }
               }
            } else if (var2 != "togglePopup" && var2 != "spacePopup") {
               if (var2 == "selectPrevious") {
                  if (var4 != null) {
                     if (var4.isPopupVisible(var3)) {
                        var4.selectPreviousPossibleValue();
                     } else if (DefaultLookup.getBoolean(var3, var4, "ComboBox.showPopupOnNavigation", false)) {
                        var4.setPopupVisible(var3, true);
                     }
                  }
               } else if (var2 == "selectPrevious2") {
                  if (var3.isShowing() && var4 != null) {
                     if (var3.isEditable() && !var3.isPopupVisible()) {
                        var3.setPopupVisible(true);
                     } else {
                        var4.selectPreviousPossibleValue();
                     }
                  }
               } else if (var2 == "enterPressed") {
                  if (var3.isPopupVisible()) {
                     if (UIManager.getBoolean("ComboBox.noActionOnKeyNavigation")) {
                        Object var10 = var4.popup.getList().getSelectedValue();
                        if (var10 != null) {
                           var3.getEditor().setItem(var10);
                           var3.setSelectedItem(var10);
                        }

                        var3.setPopupVisible(false);
                     } else {
                        boolean var11 = UIManager.getBoolean("ComboBox.isEnterSelectablePopup");
                        if (!var3.isEditable() || var11 || var4.isTableCellEditor) {
                           Object var6 = var4.popup.getList().getSelectedValue();
                           if (var6 != null) {
                              var3.getEditor().setItem(var6);
                              var3.setSelectedItem(var6);
                           }
                        }

                        var3.setPopupVisible(false);
                     }
                  } else {
                     if (var4.isTableCellEditor && !var3.isEditable()) {
                        var3.setSelectedItem(var3.getSelectedItem());
                     }

                     JRootPane var12 = SwingUtilities.getRootPane(var3);
                     if (var12 != null) {
                        InputMap var13 = var12.getInputMap(2);
                        ActionMap var7 = var12.getActionMap();
                        if (var13 != null && var7 != null) {
                           Object var8 = var13.get(KeyStroke.getKeyStroke(10, 0));
                           if (var8 != null) {
                              Action var9 = var7.get(var8);
                              if (var9 != null) {
                                 var9.actionPerformed(new ActionEvent(var12, var1.getID(), var1.getActionCommand(), var1.getWhen(), var1.getModifiers()));
                              }
                           }
                        }
                     }
                  }
               }
            } else if (var4 != null && (var2 == "togglePopup" || !var3.isEditable())) {
               if (var4.isTableCellEditor()) {
                  var3.setSelectedIndex(var4.popup.getList().getSelectedIndex());
               } else {
                  var3.setPopupVisible(!var3.isPopupVisible());
               }
            }
         } else {
            int var5 = this.getNextIndex(var3, var2);
            if (var5 >= 0 && var5 < var3.getItemCount()) {
               if (UIManager.getBoolean("ComboBox.noActionOnKeyNavigation") && var3.isPopupVisible()) {
                  var4.listBox.setSelectedIndex(var5);
                  var4.listBox.ensureIndexIsVisible(var5);
                  var3.repaint();
               } else {
                  var3.setSelectedIndex(var5);
               }
            }
         }

      }

      private int getNextIndex(JComboBox var1, String var2) {
         int var3 = var1.getMaximumRowCount();
         int var4 = var1.getSelectedIndex();
         if (UIManager.getBoolean("ComboBox.noActionOnKeyNavigation") && var1.getUI() instanceof BasicComboBoxUI) {
            var4 = ((BasicComboBoxUI)var1.getUI()).listBox.getSelectedIndex();
         }

         int var5;
         if (var2 == "pageUpPassThrough") {
            var5 = var4 - var3;
            return var5 < 0 ? 0 : var5;
         } else if (var2 == "pageDownPassThrough") {
            var5 = var4 + var3;
            int var6 = var1.getItemCount();
            return var5 < var6 ? var5 : var6 - 1;
         } else if (var2 == "homePassThrough") {
            return 0;
         } else {
            return var2 == "endPassThrough" ? var1.getItemCount() - 1 : var1.getSelectedIndex();
         }
      }

      public boolean isEnabled(Object var1) {
         if (this.getName() != "hidePopup") {
            return true;
         } else {
            return var1 != null && ((JComboBox)var1).isPopupVisible();
         }
      }
   }

   public class ComboBoxLayoutManager implements LayoutManager {
      public void addLayoutComponent(String var1, Component var2) {
      }

      public void removeLayoutComponent(Component var1) {
      }

      public Dimension preferredLayoutSize(Container var1) {
         return BasicComboBoxUI.this.getHandler().preferredLayoutSize(var1);
      }

      public Dimension minimumLayoutSize(Container var1) {
         return BasicComboBoxUI.this.getHandler().minimumLayoutSize(var1);
      }

      public void layoutContainer(Container var1) {
         BasicComboBoxUI.this.getHandler().layoutContainer(var1);
      }
   }

   public class PropertyChangeHandler implements PropertyChangeListener {
      public void propertyChange(PropertyChangeEvent var1) {
         BasicComboBoxUI.this.getHandler().propertyChange(var1);
      }
   }

   public class ItemHandler implements ItemListener {
      public void itemStateChanged(ItemEvent var1) {
      }
   }

   public class ListDataHandler implements ListDataListener {
      public void contentsChanged(ListDataEvent var1) {
         BasicComboBoxUI.this.getHandler().contentsChanged(var1);
      }

      public void intervalAdded(ListDataEvent var1) {
         BasicComboBoxUI.this.getHandler().intervalAdded(var1);
      }

      public void intervalRemoved(ListDataEvent var1) {
         BasicComboBoxUI.this.getHandler().intervalRemoved(var1);
      }
   }

   public class FocusHandler implements FocusListener {
      public void focusGained(FocusEvent var1) {
         BasicComboBoxUI.this.getHandler().focusGained(var1);
      }

      public void focusLost(FocusEvent var1) {
         BasicComboBoxUI.this.getHandler().focusLost(var1);
      }
   }

   public class KeyHandler extends KeyAdapter {
      public void keyPressed(KeyEvent var1) {
         BasicComboBoxUI.this.getHandler().keyPressed(var1);
      }
   }
}
