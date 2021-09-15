package javax.swing;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Insets;
import java.awt.ItemSelectable;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.Transient;
import java.io.Serializable;
import java.text.BreakIterator;
import java.util.Enumeration;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleAction;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleExtendedComponent;
import javax.accessibility.AccessibleIcon;
import javax.accessibility.AccessibleKeyBinding;
import javax.accessibility.AccessibleRelation;
import javax.accessibility.AccessibleRelationSet;
import javax.accessibility.AccessibleState;
import javax.accessibility.AccessibleStateSet;
import javax.accessibility.AccessibleText;
import javax.accessibility.AccessibleValue;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.ButtonUI;
import javax.swing.plaf.UIResource;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.Position;
import javax.swing.text.StyledDocument;
import javax.swing.text.View;

public abstract class AbstractButton extends JComponent implements ItemSelectable, SwingConstants {
   public static final String MODEL_CHANGED_PROPERTY = "model";
   public static final String TEXT_CHANGED_PROPERTY = "text";
   public static final String MNEMONIC_CHANGED_PROPERTY = "mnemonic";
   public static final String MARGIN_CHANGED_PROPERTY = "margin";
   public static final String VERTICAL_ALIGNMENT_CHANGED_PROPERTY = "verticalAlignment";
   public static final String HORIZONTAL_ALIGNMENT_CHANGED_PROPERTY = "horizontalAlignment";
   public static final String VERTICAL_TEXT_POSITION_CHANGED_PROPERTY = "verticalTextPosition";
   public static final String HORIZONTAL_TEXT_POSITION_CHANGED_PROPERTY = "horizontalTextPosition";
   public static final String BORDER_PAINTED_CHANGED_PROPERTY = "borderPainted";
   public static final String FOCUS_PAINTED_CHANGED_PROPERTY = "focusPainted";
   public static final String ROLLOVER_ENABLED_CHANGED_PROPERTY = "rolloverEnabled";
   public static final String CONTENT_AREA_FILLED_CHANGED_PROPERTY = "contentAreaFilled";
   public static final String ICON_CHANGED_PROPERTY = "icon";
   public static final String PRESSED_ICON_CHANGED_PROPERTY = "pressedIcon";
   public static final String SELECTED_ICON_CHANGED_PROPERTY = "selectedIcon";
   public static final String ROLLOVER_ICON_CHANGED_PROPERTY = "rolloverIcon";
   public static final String ROLLOVER_SELECTED_ICON_CHANGED_PROPERTY = "rolloverSelectedIcon";
   public static final String DISABLED_ICON_CHANGED_PROPERTY = "disabledIcon";
   public static final String DISABLED_SELECTED_ICON_CHANGED_PROPERTY = "disabledSelectedIcon";
   protected ButtonModel model = null;
   private String text = "";
   private Insets margin = null;
   private Insets defaultMargin = null;
   private Icon defaultIcon = null;
   private Icon pressedIcon = null;
   private Icon disabledIcon = null;
   private Icon selectedIcon = null;
   private Icon disabledSelectedIcon = null;
   private Icon rolloverIcon = null;
   private Icon rolloverSelectedIcon = null;
   private boolean paintBorder = true;
   private boolean paintFocus = true;
   private boolean rolloverEnabled = false;
   private boolean contentAreaFilled = true;
   private int verticalAlignment = 0;
   private int horizontalAlignment = 0;
   private int verticalTextPosition = 0;
   private int horizontalTextPosition = 11;
   private int iconTextGap = 4;
   private int mnemonic;
   private int mnemonicIndex = -1;
   private long multiClickThreshhold = 0L;
   private boolean borderPaintedSet = false;
   private boolean rolloverEnabledSet = false;
   private boolean iconTextGapSet = false;
   private boolean contentAreaFilledSet = false;
   private boolean setLayout = false;
   boolean defaultCapable = true;
   private AbstractButton.Handler handler;
   protected ChangeListener changeListener = null;
   protected ActionListener actionListener = null;
   protected ItemListener itemListener = null;
   protected transient ChangeEvent changeEvent;
   private boolean hideActionText = false;
   private Action action;
   private PropertyChangeListener actionPropertyChangeListener;

   public void setHideActionText(boolean var1) {
      if (var1 != this.hideActionText) {
         this.hideActionText = var1;
         if (this.getAction() != null) {
            this.setTextFromAction(this.getAction(), false);
         }

         this.firePropertyChange("hideActionText", !var1, var1);
      }

   }

   public boolean getHideActionText() {
      return this.hideActionText;
   }

   public String getText() {
      return this.text;
   }

   public void setText(String var1) {
      String var2 = this.text;
      this.text = var1;
      this.firePropertyChange("text", var2, var1);
      this.updateDisplayedMnemonicIndex(var1, this.getMnemonic());
      if (this.accessibleContext != null) {
         this.accessibleContext.firePropertyChange("AccessibleVisibleData", var2, var1);
      }

      if (var1 == null || var2 == null || !var1.equals(var2)) {
         this.revalidate();
         this.repaint();
      }

   }

   public boolean isSelected() {
      return this.model.isSelected();
   }

   public void setSelected(boolean var1) {
      boolean var2 = this.isSelected();
      this.model.setSelected(var1);
   }

   public void doClick() {
      this.doClick(68);
   }

   public void doClick(int var1) {
      Dimension var2 = this.getSize();
      this.model.setArmed(true);
      this.model.setPressed(true);
      this.paintImmediately(new Rectangle(0, 0, var2.width, var2.height));

      try {
         Thread.currentThread();
         Thread.sleep((long)var1);
      } catch (InterruptedException var4) {
      }

      this.model.setPressed(false);
      this.model.setArmed(false);
   }

   public void setMargin(Insets var1) {
      if (var1 instanceof UIResource) {
         this.defaultMargin = var1;
      } else if (this.margin instanceof UIResource) {
         this.defaultMargin = this.margin;
      }

      if (var1 == null && this.defaultMargin != null) {
         var1 = this.defaultMargin;
      }

      Insets var2 = this.margin;
      this.margin = var1;
      this.firePropertyChange("margin", var2, var1);
      if (var2 == null || !var2.equals(var1)) {
         this.revalidate();
         this.repaint();
      }

   }

   public Insets getMargin() {
      return this.margin == null ? null : (Insets)this.margin.clone();
   }

   public Icon getIcon() {
      return this.defaultIcon;
   }

   public void setIcon(Icon var1) {
      Icon var2 = this.defaultIcon;
      this.defaultIcon = var1;
      if (var1 != var2 && this.disabledIcon instanceof UIResource) {
         this.disabledIcon = null;
      }

      this.firePropertyChange("icon", var2, var1);
      if (this.accessibleContext != null) {
         this.accessibleContext.firePropertyChange("AccessibleVisibleData", var2, var1);
      }

      if (var1 != var2) {
         if (var1 == null || var2 == null || var1.getIconWidth() != var2.getIconWidth() || var1.getIconHeight() != var2.getIconHeight()) {
            this.revalidate();
         }

         this.repaint();
      }

   }

   public Icon getPressedIcon() {
      return this.pressedIcon;
   }

   public void setPressedIcon(Icon var1) {
      Icon var2 = this.pressedIcon;
      this.pressedIcon = var1;
      this.firePropertyChange("pressedIcon", var2, var1);
      if (this.accessibleContext != null) {
         this.accessibleContext.firePropertyChange("AccessibleVisibleData", var2, var1);
      }

      if (var1 != var2 && this.getModel().isPressed()) {
         this.repaint();
      }

   }

   public Icon getSelectedIcon() {
      return this.selectedIcon;
   }

   public void setSelectedIcon(Icon var1) {
      Icon var2 = this.selectedIcon;
      this.selectedIcon = var1;
      if (var1 != var2 && this.disabledSelectedIcon instanceof UIResource) {
         this.disabledSelectedIcon = null;
      }

      this.firePropertyChange("selectedIcon", var2, var1);
      if (this.accessibleContext != null) {
         this.accessibleContext.firePropertyChange("AccessibleVisibleData", var2, var1);
      }

      if (var1 != var2 && this.isSelected()) {
         this.repaint();
      }

   }

   public Icon getRolloverIcon() {
      return this.rolloverIcon;
   }

   public void setRolloverIcon(Icon var1) {
      Icon var2 = this.rolloverIcon;
      this.rolloverIcon = var1;
      this.firePropertyChange("rolloverIcon", var2, var1);
      if (this.accessibleContext != null) {
         this.accessibleContext.firePropertyChange("AccessibleVisibleData", var2, var1);
      }

      this.setRolloverEnabled(true);
      if (var1 != var2) {
         this.repaint();
      }

   }

   public Icon getRolloverSelectedIcon() {
      return this.rolloverSelectedIcon;
   }

   public void setRolloverSelectedIcon(Icon var1) {
      Icon var2 = this.rolloverSelectedIcon;
      this.rolloverSelectedIcon = var1;
      this.firePropertyChange("rolloverSelectedIcon", var2, var1);
      if (this.accessibleContext != null) {
         this.accessibleContext.firePropertyChange("AccessibleVisibleData", var2, var1);
      }

      this.setRolloverEnabled(true);
      if (var1 != var2 && this.isSelected()) {
         this.repaint();
      }

   }

   @Transient
   public Icon getDisabledIcon() {
      if (this.disabledIcon == null) {
         this.disabledIcon = UIManager.getLookAndFeel().getDisabledIcon(this, this.getIcon());
         if (this.disabledIcon != null) {
            this.firePropertyChange("disabledIcon", (Object)null, this.disabledIcon);
         }
      }

      return this.disabledIcon;
   }

   public void setDisabledIcon(Icon var1) {
      Icon var2 = this.disabledIcon;
      this.disabledIcon = var1;
      this.firePropertyChange("disabledIcon", var2, var1);
      if (this.accessibleContext != null) {
         this.accessibleContext.firePropertyChange("AccessibleVisibleData", var2, var1);
      }

      if (var1 != var2 && !this.isEnabled()) {
         this.repaint();
      }

   }

   public Icon getDisabledSelectedIcon() {
      if (this.disabledSelectedIcon == null) {
         if (this.selectedIcon == null) {
            return this.getDisabledIcon();
         }

         this.disabledSelectedIcon = UIManager.getLookAndFeel().getDisabledSelectedIcon(this, this.getSelectedIcon());
      }

      return this.disabledSelectedIcon;
   }

   public void setDisabledSelectedIcon(Icon var1) {
      Icon var2 = this.disabledSelectedIcon;
      this.disabledSelectedIcon = var1;
      this.firePropertyChange("disabledSelectedIcon", var2, var1);
      if (this.accessibleContext != null) {
         this.accessibleContext.firePropertyChange("AccessibleVisibleData", var2, var1);
      }

      if (var1 != var2) {
         if (var1 == null || var2 == null || var1.getIconWidth() != var2.getIconWidth() || var1.getIconHeight() != var2.getIconHeight()) {
            this.revalidate();
         }

         if (!this.isEnabled() && this.isSelected()) {
            this.repaint();
         }
      }

   }

   public int getVerticalAlignment() {
      return this.verticalAlignment;
   }

   public void setVerticalAlignment(int var1) {
      if (var1 != this.verticalAlignment) {
         int var2 = this.verticalAlignment;
         this.verticalAlignment = this.checkVerticalKey(var1, "verticalAlignment");
         this.firePropertyChange("verticalAlignment", var2, this.verticalAlignment);
         this.repaint();
      }
   }

   public int getHorizontalAlignment() {
      return this.horizontalAlignment;
   }

   public void setHorizontalAlignment(int var1) {
      if (var1 != this.horizontalAlignment) {
         int var2 = this.horizontalAlignment;
         this.horizontalAlignment = this.checkHorizontalKey(var1, "horizontalAlignment");
         this.firePropertyChange("horizontalAlignment", var2, this.horizontalAlignment);
         this.repaint();
      }
   }

   public int getVerticalTextPosition() {
      return this.verticalTextPosition;
   }

   public void setVerticalTextPosition(int var1) {
      if (var1 != this.verticalTextPosition) {
         int var2 = this.verticalTextPosition;
         this.verticalTextPosition = this.checkVerticalKey(var1, "verticalTextPosition");
         this.firePropertyChange("verticalTextPosition", var2, this.verticalTextPosition);
         this.revalidate();
         this.repaint();
      }
   }

   public int getHorizontalTextPosition() {
      return this.horizontalTextPosition;
   }

   public void setHorizontalTextPosition(int var1) {
      if (var1 != this.horizontalTextPosition) {
         int var2 = this.horizontalTextPosition;
         this.horizontalTextPosition = this.checkHorizontalKey(var1, "horizontalTextPosition");
         this.firePropertyChange("horizontalTextPosition", var2, this.horizontalTextPosition);
         this.revalidate();
         this.repaint();
      }
   }

   public int getIconTextGap() {
      return this.iconTextGap;
   }

   public void setIconTextGap(int var1) {
      int var2 = this.iconTextGap;
      this.iconTextGap = var1;
      this.iconTextGapSet = true;
      this.firePropertyChange("iconTextGap", var2, var1);
      if (var1 != var2) {
         this.revalidate();
         this.repaint();
      }

   }

   protected int checkHorizontalKey(int var1, String var2) {
      if (var1 != 2 && var1 != 0 && var1 != 4 && var1 != 10 && var1 != 11) {
         throw new IllegalArgumentException(var2);
      } else {
         return var1;
      }
   }

   protected int checkVerticalKey(int var1, String var2) {
      if (var1 != 1 && var1 != 0 && var1 != 3) {
         throw new IllegalArgumentException(var2);
      } else {
         return var1;
      }
   }

   public void removeNotify() {
      super.removeNotify();
      if (this.isRolloverEnabled()) {
         this.getModel().setRollover(false);
      }

   }

   public void setActionCommand(String var1) {
      this.getModel().setActionCommand(var1);
   }

   public String getActionCommand() {
      String var1 = this.getModel().getActionCommand();
      if (var1 == null) {
         var1 = this.getText();
      }

      return var1;
   }

   public void setAction(Action var1) {
      Action var2 = this.getAction();
      if (this.action == null || !this.action.equals(var1)) {
         this.action = var1;
         if (var2 != null) {
            this.removeActionListener(var2);
            var2.removePropertyChangeListener(this.actionPropertyChangeListener);
            this.actionPropertyChangeListener = null;
         }

         this.configurePropertiesFromAction(this.action);
         if (this.action != null) {
            if (!this.isListener(ActionListener.class, this.action)) {
               this.addActionListener(this.action);
            }

            this.actionPropertyChangeListener = this.createActionPropertyChangeListener(this.action);
            this.action.addPropertyChangeListener(this.actionPropertyChangeListener);
         }

         this.firePropertyChange("action", var2, this.action);
      }

   }

   private boolean isListener(Class var1, ActionListener var2) {
      boolean var3 = false;
      Object[] var4 = this.listenerList.getListenerList();

      for(int var5 = var4.length - 2; var5 >= 0; var5 -= 2) {
         if (var4[var5] == var1 && var4[var5 + 1] == var2) {
            var3 = true;
         }
      }

      return var3;
   }

   public Action getAction() {
      return this.action;
   }

   protected void configurePropertiesFromAction(Action var1) {
      this.setMnemonicFromAction(var1);
      this.setTextFromAction(var1, false);
      AbstractAction.setToolTipTextFromAction(this, var1);
      this.setIconFromAction(var1);
      this.setActionCommandFromAction(var1);
      AbstractAction.setEnabledFromAction(this, var1);
      if (AbstractAction.hasSelectedKey(var1) && this.shouldUpdateSelectedStateFromAction()) {
         this.setSelectedFromAction(var1);
      }

      this.setDisplayedMnemonicIndexFromAction(var1, false);
   }

   void clientPropertyChanged(Object var1, Object var2, Object var3) {
      if (var1 == "hideActionText") {
         boolean var4 = var3 instanceof Boolean ? (Boolean)var3 : false;
         if (this.getHideActionText() != var4) {
            this.setHideActionText(var4);
         }
      }

   }

   boolean shouldUpdateSelectedStateFromAction() {
      return false;
   }

   protected void actionPropertyChanged(Action var1, String var2) {
      if (var2 == "Name") {
         this.setTextFromAction(var1, true);
      } else if (var2 == "enabled") {
         AbstractAction.setEnabledFromAction(this, var1);
      } else if (var2 == "ShortDescription") {
         AbstractAction.setToolTipTextFromAction(this, var1);
      } else if (var2 == "SmallIcon") {
         this.smallIconChanged(var1);
      } else if (var2 == "MnemonicKey") {
         this.setMnemonicFromAction(var1);
      } else if (var2 == "ActionCommandKey") {
         this.setActionCommandFromAction(var1);
      } else if (var2 == "SwingSelectedKey" && AbstractAction.hasSelectedKey(var1) && this.shouldUpdateSelectedStateFromAction()) {
         this.setSelectedFromAction(var1);
      } else if (var2 == "SwingDisplayedMnemonicIndexKey") {
         this.setDisplayedMnemonicIndexFromAction(var1, true);
      } else if (var2 == "SwingLargeIconKey") {
         this.largeIconChanged(var1);
      }

   }

   private void setDisplayedMnemonicIndexFromAction(Action var1, boolean var2) {
      Integer var3 = var1 == null ? null : (Integer)var1.getValue("SwingDisplayedMnemonicIndexKey");
      if (var2 || var3 != null) {
         int var4;
         if (var3 == null) {
            var4 = -1;
         } else {
            var4 = var3;
            String var5 = this.getText();
            if (var5 == null || var4 >= var5.length()) {
               var4 = -1;
            }
         }

         this.setDisplayedMnemonicIndex(var4);
      }

   }

   private void setMnemonicFromAction(Action var1) {
      Integer var2 = var1 == null ? null : (Integer)var1.getValue("MnemonicKey");
      this.setMnemonic(var2 == null ? 0 : var2);
   }

   private void setTextFromAction(Action var1, boolean var2) {
      boolean var3 = this.getHideActionText();
      if (!var2) {
         this.setText(var1 != null && !var3 ? (String)var1.getValue("Name") : null);
      } else if (!var3) {
         this.setText((String)var1.getValue("Name"));
      }

   }

   void setIconFromAction(Action var1) {
      Icon var2 = null;
      if (var1 != null) {
         var2 = (Icon)var1.getValue("SwingLargeIconKey");
         if (var2 == null) {
            var2 = (Icon)var1.getValue("SmallIcon");
         }
      }

      this.setIcon(var2);
   }

   void smallIconChanged(Action var1) {
      if (var1.getValue("SwingLargeIconKey") == null) {
         this.setIconFromAction(var1);
      }

   }

   void largeIconChanged(Action var1) {
      this.setIconFromAction(var1);
   }

   private void setActionCommandFromAction(Action var1) {
      this.setActionCommand(var1 != null ? (String)var1.getValue("ActionCommandKey") : null);
   }

   private void setSelectedFromAction(Action var1) {
      boolean var2 = false;
      if (var1 != null) {
         var2 = AbstractAction.isSelected(var1);
      }

      if (var2 != this.isSelected()) {
         this.setSelected(var2);
         if (!var2 && this.isSelected() && this.getModel() instanceof DefaultButtonModel) {
            ButtonGroup var3 = ((DefaultButtonModel)this.getModel()).getGroup();
            if (var3 != null) {
               var3.clearSelection();
            }
         }
      }

   }

   protected PropertyChangeListener createActionPropertyChangeListener(Action var1) {
      return this.createActionPropertyChangeListener0(var1);
   }

   PropertyChangeListener createActionPropertyChangeListener0(Action var1) {
      return new AbstractButton.ButtonActionPropertyChangeListener(this, var1);
   }

   public boolean isBorderPainted() {
      return this.paintBorder;
   }

   public void setBorderPainted(boolean var1) {
      boolean var2 = this.paintBorder;
      this.paintBorder = var1;
      this.borderPaintedSet = true;
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

   public boolean isFocusPainted() {
      return this.paintFocus;
   }

   public void setFocusPainted(boolean var1) {
      boolean var2 = this.paintFocus;
      this.paintFocus = var1;
      this.firePropertyChange("focusPainted", var2, this.paintFocus);
      if (var1 != var2 && this.isFocusOwner()) {
         this.revalidate();
         this.repaint();
      }

   }

   public boolean isContentAreaFilled() {
      return this.contentAreaFilled;
   }

   public void setContentAreaFilled(boolean var1) {
      boolean var2 = this.contentAreaFilled;
      this.contentAreaFilled = var1;
      this.contentAreaFilledSet = true;
      this.firePropertyChange("contentAreaFilled", var2, this.contentAreaFilled);
      if (var1 != var2) {
         this.repaint();
      }

   }

   public boolean isRolloverEnabled() {
      return this.rolloverEnabled;
   }

   public void setRolloverEnabled(boolean var1) {
      boolean var2 = this.rolloverEnabled;
      this.rolloverEnabled = var1;
      this.rolloverEnabledSet = true;
      this.firePropertyChange("rolloverEnabled", var2, this.rolloverEnabled);
      if (var1 != var2) {
         this.repaint();
      }

   }

   public int getMnemonic() {
      return this.mnemonic;
   }

   public void setMnemonic(int var1) {
      int var2 = this.getMnemonic();
      this.model.setMnemonic(var1);
      this.updateMnemonicProperties();
   }

   public void setMnemonic(char var1) {
      int var2 = var1;
      if (var1 >= 'a' && var1 <= 'z') {
         var2 = var1 - 32;
      }

      this.setMnemonic(var2);
   }

   public void setDisplayedMnemonicIndex(int var1) throws IllegalArgumentException {
      int var2 = this.mnemonicIndex;
      if (var1 == -1) {
         this.mnemonicIndex = -1;
      } else {
         String var3 = this.getText();
         int var4 = var3 == null ? 0 : var3.length();
         if (var1 < -1 || var1 >= var4) {
            throw new IllegalArgumentException("index == " + var1);
         }
      }

      this.mnemonicIndex = var1;
      this.firePropertyChange("displayedMnemonicIndex", var2, var1);
      if (var1 != var2) {
         this.revalidate();
         this.repaint();
      }

   }

   public int getDisplayedMnemonicIndex() {
      return this.mnemonicIndex;
   }

   private void updateDisplayedMnemonicIndex(String var1, int var2) {
      this.setDisplayedMnemonicIndex(SwingUtilities.findDisplayedMnemonicIndex(var1, var2));
   }

   private void updateMnemonicProperties() {
      int var1 = this.model.getMnemonic();
      if (this.mnemonic != var1) {
         int var2 = this.mnemonic;
         this.mnemonic = var1;
         this.firePropertyChange("mnemonic", var2, this.mnemonic);
         this.updateDisplayedMnemonicIndex(this.getText(), this.mnemonic);
         this.revalidate();
         this.repaint();
      }

   }

   public void setMultiClickThreshhold(long var1) {
      if (var1 < 0L) {
         throw new IllegalArgumentException("threshhold must be >= 0");
      } else {
         this.multiClickThreshhold = var1;
      }
   }

   public long getMultiClickThreshhold() {
      return this.multiClickThreshhold;
   }

   public ButtonModel getModel() {
      return this.model;
   }

   public void setModel(ButtonModel var1) {
      ButtonModel var2 = this.getModel();
      if (var2 != null) {
         var2.removeChangeListener(this.changeListener);
         var2.removeActionListener(this.actionListener);
         var2.removeItemListener(this.itemListener);
         this.changeListener = null;
         this.actionListener = null;
         this.itemListener = null;
      }

      this.model = var1;
      if (var1 != null) {
         this.changeListener = this.createChangeListener();
         this.actionListener = this.createActionListener();
         this.itemListener = this.createItemListener();
         var1.addChangeListener(this.changeListener);
         var1.addActionListener(this.actionListener);
         var1.addItemListener(this.itemListener);
         this.updateMnemonicProperties();
         super.setEnabled(var1.isEnabled());
      } else {
         this.mnemonic = 0;
      }

      this.updateDisplayedMnemonicIndex(this.getText(), this.mnemonic);
      this.firePropertyChange("model", var2, var1);
      if (var1 != var2) {
         this.revalidate();
         this.repaint();
      }

   }

   public ButtonUI getUI() {
      return (ButtonUI)this.ui;
   }

   public void setUI(ButtonUI var1) {
      super.setUI(var1);
      if (this.disabledIcon instanceof UIResource) {
         this.setDisabledIcon((Icon)null);
      }

      if (this.disabledSelectedIcon instanceof UIResource) {
         this.setDisabledSelectedIcon((Icon)null);
      }

   }

   public void updateUI() {
   }

   protected void addImpl(Component var1, Object var2, int var3) {
      if (!this.setLayout) {
         this.setLayout(new OverlayLayout(this));
      }

      super.addImpl(var1, var2, var3);
   }

   public void setLayout(LayoutManager var1) {
      this.setLayout = true;
      super.setLayout(var1);
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

   public void addActionListener(ActionListener var1) {
      this.listenerList.add(ActionListener.class, var1);
   }

   public void removeActionListener(ActionListener var1) {
      if (var1 != null && this.getAction() == var1) {
         this.setAction((Action)null);
      } else {
         this.listenerList.remove(ActionListener.class, var1);
      }

   }

   public ActionListener[] getActionListeners() {
      return (ActionListener[])this.listenerList.getListeners(ActionListener.class);
   }

   protected ChangeListener createChangeListener() {
      return this.getHandler();
   }

   protected void fireActionPerformed(ActionEvent var1) {
      Object[] var2 = this.listenerList.getListenerList();
      ActionEvent var3 = null;

      for(int var4 = var2.length - 2; var4 >= 0; var4 -= 2) {
         if (var2[var4] == ActionListener.class) {
            if (var3 == null) {
               String var5 = var1.getActionCommand();
               if (var5 == null) {
                  var5 = this.getActionCommand();
               }

               var3 = new ActionEvent(this, 1001, var5, var1.getWhen(), var1.getModifiers());
            }

            ((ActionListener)var2[var4 + 1]).actionPerformed(var3);
         }
      }

   }

   protected void fireItemStateChanged(ItemEvent var1) {
      Object[] var2 = this.listenerList.getListenerList();
      ItemEvent var3 = null;

      for(int var4 = var2.length - 2; var4 >= 0; var4 -= 2) {
         if (var2[var4] == ItemListener.class) {
            if (var3 == null) {
               var3 = new ItemEvent(this, 701, this, var1.getStateChange());
            }

            ((ItemListener)var2[var4 + 1]).itemStateChanged(var3);
         }
      }

      if (this.accessibleContext != null) {
         if (var1.getStateChange() == 1) {
            this.accessibleContext.firePropertyChange("AccessibleState", (Object)null, AccessibleState.SELECTED);
            this.accessibleContext.firePropertyChange("AccessibleValue", 0, 1);
         } else {
            this.accessibleContext.firePropertyChange("AccessibleState", AccessibleState.SELECTED, (Object)null);
            this.accessibleContext.firePropertyChange("AccessibleValue", 1, 0);
         }
      }

   }

   protected ActionListener createActionListener() {
      return this.getHandler();
   }

   protected ItemListener createItemListener() {
      return this.getHandler();
   }

   public void setEnabled(boolean var1) {
      if (!var1 && this.model.isRollover()) {
         this.model.setRollover(false);
      }

      super.setEnabled(var1);
      this.model.setEnabled(var1);
   }

   /** @deprecated */
   @Deprecated
   public String getLabel() {
      return this.getText();
   }

   /** @deprecated */
   @Deprecated
   public void setLabel(String var1) {
      this.setText(var1);
   }

   public void addItemListener(ItemListener var1) {
      this.listenerList.add(ItemListener.class, var1);
   }

   public void removeItemListener(ItemListener var1) {
      this.listenerList.remove(ItemListener.class, var1);
   }

   public ItemListener[] getItemListeners() {
      return (ItemListener[])this.listenerList.getListeners(ItemListener.class);
   }

   public Object[] getSelectedObjects() {
      if (!this.isSelected()) {
         return null;
      } else {
         Object[] var1 = new Object[]{this.getText()};
         return var1;
      }
   }

   protected void init(String var1, Icon var2) {
      if (var1 != null) {
         this.setText(var1);
      }

      if (var2 != null) {
         this.setIcon(var2);
      }

      this.updateUI();
      this.setAlignmentX(0.0F);
      this.setAlignmentY(0.5F);
   }

   public boolean imageUpdate(Image var1, int var2, int var3, int var4, int var5, int var6) {
      Icon var7 = null;
      if (!this.model.isEnabled()) {
         if (this.model.isSelected()) {
            var7 = this.getDisabledSelectedIcon();
         } else {
            var7 = this.getDisabledIcon();
         }
      } else if (this.model.isPressed() && this.model.isArmed()) {
         var7 = this.getPressedIcon();
      } else if (this.isRolloverEnabled() && this.model.isRollover()) {
         if (this.model.isSelected()) {
            var7 = this.getRolloverSelectedIcon();
         } else {
            var7 = this.getRolloverIcon();
         }
      } else if (this.model.isSelected()) {
         var7 = this.getSelectedIcon();
      }

      if (var7 == null) {
         var7 = this.getIcon();
      }

      return var7 != null && SwingUtilities.doesIconReferenceImage(var7, var1) ? super.imageUpdate(var1, var2, var3, var4, var5, var6) : false;
   }

   void setUIProperty(String var1, Object var2) {
      if (var1 == "borderPainted") {
         if (!this.borderPaintedSet) {
            this.setBorderPainted((Boolean)var2);
            this.borderPaintedSet = false;
         }
      } else if (var1 == "rolloverEnabled") {
         if (!this.rolloverEnabledSet) {
            this.setRolloverEnabled((Boolean)var2);
            this.rolloverEnabledSet = false;
         }
      } else if (var1 == "iconTextGap") {
         if (!this.iconTextGapSet) {
            this.setIconTextGap(((Number)var2).intValue());
            this.iconTextGapSet = false;
         }
      } else if (var1 == "contentAreaFilled") {
         if (!this.contentAreaFilledSet) {
            this.setContentAreaFilled((Boolean)var2);
            this.contentAreaFilledSet = false;
         }
      } else {
         super.setUIProperty(var1, var2);
      }

   }

   protected String paramString() {
      String var1 = this.defaultIcon != null && this.defaultIcon != this ? this.defaultIcon.toString() : "";
      String var2 = this.pressedIcon != null && this.pressedIcon != this ? this.pressedIcon.toString() : "";
      String var3 = this.disabledIcon != null && this.disabledIcon != this ? this.disabledIcon.toString() : "";
      String var4 = this.selectedIcon != null && this.selectedIcon != this ? this.selectedIcon.toString() : "";
      String var5 = this.disabledSelectedIcon != null && this.disabledSelectedIcon != this ? this.disabledSelectedIcon.toString() : "";
      String var6 = this.rolloverIcon != null && this.rolloverIcon != this ? this.rolloverIcon.toString() : "";
      String var7 = this.rolloverSelectedIcon != null && this.rolloverSelectedIcon != this ? this.rolloverSelectedIcon.toString() : "";
      String var8 = this.paintBorder ? "true" : "false";
      String var9 = this.paintFocus ? "true" : "false";
      String var10 = this.rolloverEnabled ? "true" : "false";
      return super.paramString() + ",defaultIcon=" + var1 + ",disabledIcon=" + var3 + ",disabledSelectedIcon=" + var5 + ",margin=" + this.margin + ",paintBorder=" + var8 + ",paintFocus=" + var9 + ",pressedIcon=" + var2 + ",rolloverEnabled=" + var10 + ",rolloverIcon=" + var6 + ",rolloverSelectedIcon=" + var7 + ",selectedIcon=" + var4 + ",text=" + this.text;
   }

   private AbstractButton.Handler getHandler() {
      if (this.handler == null) {
         this.handler = new AbstractButton.Handler();
      }

      return this.handler;
   }

   protected abstract class AccessibleAbstractButton extends JComponent.AccessibleJComponent implements AccessibleAction, AccessibleValue, AccessibleText, AccessibleExtendedComponent {
      protected AccessibleAbstractButton() {
         super();
      }

      public String getAccessibleName() {
         String var1 = this.accessibleName;
         if (var1 == null) {
            var1 = (String)AbstractButton.this.getClientProperty("AccessibleName");
         }

         if (var1 == null) {
            var1 = AbstractButton.this.getText();
         }

         if (var1 == null) {
            var1 = super.getAccessibleName();
         }

         return var1;
      }

      public AccessibleIcon[] getAccessibleIcon() {
         Icon var1 = AbstractButton.this.getIcon();
         if (var1 instanceof Accessible) {
            AccessibleContext var2 = ((Accessible)var1).getAccessibleContext();
            if (var2 != null && var2 instanceof AccessibleIcon) {
               return new AccessibleIcon[]{(AccessibleIcon)var2};
            }
         }

         return null;
      }

      public AccessibleStateSet getAccessibleStateSet() {
         AccessibleStateSet var1 = super.getAccessibleStateSet();
         if (AbstractButton.this.getModel().isArmed()) {
            var1.add(AccessibleState.ARMED);
         }

         if (AbstractButton.this.isFocusOwner()) {
            var1.add(AccessibleState.FOCUSED);
         }

         if (AbstractButton.this.getModel().isPressed()) {
            var1.add(AccessibleState.PRESSED);
         }

         if (AbstractButton.this.isSelected()) {
            var1.add(AccessibleState.CHECKED);
         }

         return var1;
      }

      public AccessibleRelationSet getAccessibleRelationSet() {
         AccessibleRelationSet var1 = super.getAccessibleRelationSet();
         if (!var1.contains(AccessibleRelation.MEMBER_OF)) {
            ButtonModel var2 = AbstractButton.this.getModel();
            if (var2 != null && var2 instanceof DefaultButtonModel) {
               ButtonGroup var3 = ((DefaultButtonModel)var2).getGroup();
               if (var3 != null) {
                  int var4 = var3.getButtonCount();
                  Object[] var5 = new Object[var4];
                  Enumeration var6 = var3.getElements();

                  for(int var7 = 0; var7 < var4; ++var7) {
                     if (var6.hasMoreElements()) {
                        var5[var7] = var6.nextElement();
                     }
                  }

                  AccessibleRelation var8 = new AccessibleRelation(AccessibleRelation.MEMBER_OF);
                  var8.setTarget(var5);
                  var1.add(var8);
               }
            }
         }

         return var1;
      }

      public AccessibleAction getAccessibleAction() {
         return this;
      }

      public AccessibleValue getAccessibleValue() {
         return this;
      }

      public int getAccessibleActionCount() {
         return 1;
      }

      public String getAccessibleActionDescription(int var1) {
         return var1 == 0 ? UIManager.getString("AbstractButton.clickText") : null;
      }

      public boolean doAccessibleAction(int var1) {
         if (var1 == 0) {
            AbstractButton.this.doClick();
            return true;
         } else {
            return false;
         }
      }

      public Number getCurrentAccessibleValue() {
         return AbstractButton.this.isSelected() ? 1 : 0;
      }

      public boolean setCurrentAccessibleValue(Number var1) {
         if (var1 == null) {
            return false;
         } else {
            int var2 = var1.intValue();
            if (var2 == 0) {
               AbstractButton.this.setSelected(false);
            } else {
               AbstractButton.this.setSelected(true);
            }

            return true;
         }
      }

      public Number getMinimumAccessibleValue() {
         return 0;
      }

      public Number getMaximumAccessibleValue() {
         return 1;
      }

      public AccessibleText getAccessibleText() {
         View var1 = (View)AbstractButton.this.getClientProperty("html");
         return var1 != null ? this : null;
      }

      public int getIndexAtPoint(Point var1) {
         View var2 = (View)AbstractButton.this.getClientProperty("html");
         if (var2 != null) {
            Rectangle var3 = this.getTextRectangle();
            if (var3 == null) {
               return -1;
            } else {
               Rectangle2D.Float var4 = new Rectangle2D.Float((float)var3.x, (float)var3.y, (float)var3.width, (float)var3.height);
               Position.Bias[] var5 = new Position.Bias[1];
               return var2.viewToModel((float)var1.x, (float)var1.y, var4, var5);
            }
         } else {
            return -1;
         }
      }

      public Rectangle getCharacterBounds(int var1) {
         View var2 = (View)AbstractButton.this.getClientProperty("html");
         if (var2 != null) {
            Rectangle var3 = this.getTextRectangle();
            if (var3 == null) {
               return null;
            } else {
               Rectangle2D.Float var4 = new Rectangle2D.Float((float)var3.x, (float)var3.y, (float)var3.width, (float)var3.height);

               try {
                  Shape var5 = var2.modelToView(var1, var4, Position.Bias.Forward);
                  return var5.getBounds();
               } catch (BadLocationException var6) {
                  return null;
               }
            }
         } else {
            return null;
         }
      }

      public int getCharCount() {
         View var1 = (View)AbstractButton.this.getClientProperty("html");
         if (var1 != null) {
            Document var2 = var1.getDocument();
            if (var2 instanceof StyledDocument) {
               StyledDocument var3 = (StyledDocument)var2;
               return var3.getLength();
            }
         }

         return AbstractButton.this.accessibleContext.getAccessibleName().length();
      }

      public int getCaretPosition() {
         return -1;
      }

      public String getAtIndex(int var1, int var2) {
         if (var2 >= 0 && var2 < this.getCharCount()) {
            String var3;
            BreakIterator var4;
            int var5;
            switch(var1) {
            case 1:
               try {
                  return this.getText(var2, 1);
               } catch (BadLocationException var8) {
                  return null;
               }
            case 2:
               try {
                  var3 = this.getText(0, this.getCharCount());
                  var4 = BreakIterator.getWordInstance(this.getLocale());
                  var4.setText(var3);
                  var5 = var4.following(var2);
                  return var3.substring(var4.previous(), var5);
               } catch (BadLocationException var7) {
                  return null;
               }
            case 3:
               try {
                  var3 = this.getText(0, this.getCharCount());
                  var4 = BreakIterator.getSentenceInstance(this.getLocale());
                  var4.setText(var3);
                  var5 = var4.following(var2);
                  return var3.substring(var4.previous(), var5);
               } catch (BadLocationException var6) {
                  return null;
               }
            default:
               return null;
            }
         } else {
            return null;
         }
      }

      public String getAfterIndex(int var1, int var2) {
         if (var2 >= 0 && var2 < this.getCharCount()) {
            String var3;
            BreakIterator var4;
            int var5;
            int var6;
            switch(var1) {
            case 1:
               if (var2 + 1 >= this.getCharCount()) {
                  return null;
               } else {
                  try {
                     return this.getText(var2 + 1, 1);
                  } catch (BadLocationException var9) {
                     return null;
                  }
               }
            case 2:
               try {
                  var3 = this.getText(0, this.getCharCount());
                  var4 = BreakIterator.getWordInstance(this.getLocale());
                  var4.setText(var3);
                  var5 = var4.following(var2);
                  if (var5 != -1 && var5 < var3.length()) {
                     var6 = var4.following(var5);
                     if (var6 != -1 && var6 < var3.length()) {
                        return var3.substring(var5, var6);
                     }

                     return null;
                  }

                  return null;
               } catch (BadLocationException var8) {
                  return null;
               }
            case 3:
               try {
                  var3 = this.getText(0, this.getCharCount());
                  var4 = BreakIterator.getSentenceInstance(this.getLocale());
                  var4.setText(var3);
                  var5 = var4.following(var2);
                  if (var5 != -1 && var5 <= var3.length()) {
                     var6 = var4.following(var5);
                     if (var6 != -1 && var6 <= var3.length()) {
                        return var3.substring(var5, var6);
                     }

                     return null;
                  }

                  return null;
               } catch (BadLocationException var7) {
                  return null;
               }
            default:
               return null;
            }
         } else {
            return null;
         }
      }

      public String getBeforeIndex(int var1, int var2) {
         if (var2 >= 0 && var2 <= this.getCharCount() - 1) {
            String var3;
            BreakIterator var4;
            int var5;
            int var6;
            switch(var1) {
            case 1:
               if (var2 == 0) {
                  return null;
               } else {
                  try {
                     return this.getText(var2 - 1, 1);
                  } catch (BadLocationException var9) {
                     return null;
                  }
               }
            case 2:
               try {
                  var3 = this.getText(0, this.getCharCount());
                  var4 = BreakIterator.getWordInstance(this.getLocale());
                  var4.setText(var3);
                  var4.following(var2);
                  var5 = var4.previous();
                  var6 = var4.previous();
                  if (var6 == -1) {
                     return null;
                  }

                  return var3.substring(var6, var5);
               } catch (BadLocationException var8) {
                  return null;
               }
            case 3:
               try {
                  var3 = this.getText(0, this.getCharCount());
                  var4 = BreakIterator.getSentenceInstance(this.getLocale());
                  var4.setText(var3);
                  var4.following(var2);
                  var5 = var4.previous();
                  var6 = var4.previous();
                  if (var6 == -1) {
                     return null;
                  }

                  return var3.substring(var6, var5);
               } catch (BadLocationException var7) {
                  return null;
               }
            default:
               return null;
            }
         } else {
            return null;
         }
      }

      public AttributeSet getCharacterAttribute(int var1) {
         View var2 = (View)AbstractButton.this.getClientProperty("html");
         if (var2 != null) {
            Document var3 = var2.getDocument();
            if (var3 instanceof StyledDocument) {
               StyledDocument var4 = (StyledDocument)var3;
               Element var5 = var4.getCharacterElement(var1);
               if (var5 != null) {
                  return var5.getAttributes();
               }
            }
         }

         return null;
      }

      public int getSelectionStart() {
         return -1;
      }

      public int getSelectionEnd() {
         return -1;
      }

      public String getSelectedText() {
         return null;
      }

      private String getText(int var1, int var2) throws BadLocationException {
         View var3 = (View)AbstractButton.this.getClientProperty("html");
         if (var3 != null) {
            Document var4 = var3.getDocument();
            if (var4 instanceof StyledDocument) {
               StyledDocument var5 = (StyledDocument)var4;
               return var5.getText(var1, var2);
            }
         }

         return null;
      }

      private Rectangle getTextRectangle() {
         String var1 = AbstractButton.this.getText();
         Icon var2 = AbstractButton.this.isEnabled() ? AbstractButton.this.getIcon() : AbstractButton.this.getDisabledIcon();
         if (var2 == null && var1 == null) {
            return null;
         } else {
            Rectangle var3 = new Rectangle();
            Rectangle var4 = new Rectangle();
            Rectangle var5 = new Rectangle();
            Insets var6 = new Insets(0, 0, 0, 0);
            var6 = AbstractButton.this.getInsets(var6);
            var5.x = var6.left;
            var5.y = var6.top;
            var5.width = AbstractButton.this.getWidth() - (var6.left + var6.right);
            var5.height = AbstractButton.this.getHeight() - (var6.top + var6.bottom);
            SwingUtilities.layoutCompoundLabel(AbstractButton.this, this.getFontMetrics(this.getFont()), var1, var2, AbstractButton.this.getVerticalAlignment(), AbstractButton.this.getHorizontalAlignment(), AbstractButton.this.getVerticalTextPosition(), AbstractButton.this.getHorizontalTextPosition(), var5, var3, var4, 0);
            return var4;
         }
      }

      AccessibleExtendedComponent getAccessibleExtendedComponent() {
         return this;
      }

      public String getToolTipText() {
         return AbstractButton.this.getToolTipText();
      }

      public String getTitledBorderText() {
         return super.getTitledBorderText();
      }

      public AccessibleKeyBinding getAccessibleKeyBinding() {
         int var1 = AbstractButton.this.getMnemonic();
         return var1 == 0 ? null : new AbstractButton.AccessibleAbstractButton.ButtonKeyBinding(var1);
      }

      class ButtonKeyBinding implements AccessibleKeyBinding {
         int mnemonic;

         ButtonKeyBinding(int var2) {
            this.mnemonic = var2;
         }

         public int getAccessibleKeyBindingCount() {
            return 1;
         }

         public Object getAccessibleKeyBinding(int var1) {
            if (var1 != 0) {
               throw new IllegalArgumentException();
            } else {
               return KeyStroke.getKeyStroke(this.mnemonic, 0);
            }
         }
      }
   }

   class Handler implements ActionListener, ChangeListener, ItemListener, Serializable {
      public void stateChanged(ChangeEvent var1) {
         Object var2 = var1.getSource();
         AbstractButton.this.updateMnemonicProperties();
         if (AbstractButton.this.isEnabled() != AbstractButton.this.model.isEnabled()) {
            AbstractButton.this.setEnabled(AbstractButton.this.model.isEnabled());
         }

         AbstractButton.this.fireStateChanged();
         AbstractButton.this.repaint();
      }

      public void actionPerformed(ActionEvent var1) {
         AbstractButton.this.fireActionPerformed(var1);
      }

      public void itemStateChanged(ItemEvent var1) {
         AbstractButton.this.fireItemStateChanged(var1);
         if (AbstractButton.this.shouldUpdateSelectedStateFromAction()) {
            Action var2 = AbstractButton.this.getAction();
            if (var2 != null && AbstractAction.hasSelectedKey(var2)) {
               boolean var3 = AbstractButton.this.isSelected();
               boolean var4 = AbstractAction.isSelected(var2);
               if (var4 != var3) {
                  var2.putValue("SwingSelectedKey", var3);
               }
            }
         }

      }
   }

   protected class ButtonChangeListener implements ChangeListener, Serializable {
      ButtonChangeListener() {
      }

      public void stateChanged(ChangeEvent var1) {
         AbstractButton.this.getHandler().stateChanged(var1);
      }
   }

   private static class ButtonActionPropertyChangeListener extends ActionPropertyChangeListener<AbstractButton> {
      ButtonActionPropertyChangeListener(AbstractButton var1, Action var2) {
         super(var1, var2);
      }

      protected void actionPropertyChanged(AbstractButton var1, Action var2, PropertyChangeEvent var3) {
         if (AbstractAction.shouldReconfigure(var3)) {
            var1.configurePropertiesFromAction(var2);
         } else {
            var1.actionPropertyChanged(var2, var3.getPropertyName());
         }

      }
   }
}
