package javax.swing;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.HeadlessException;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Vector;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.plaf.OptionPaneUI;

public class JOptionPane extends JComponent implements Accessible {
   private static final String uiClassID = "OptionPaneUI";
   public static final Object UNINITIALIZED_VALUE = "uninitializedValue";
   public static final int DEFAULT_OPTION = -1;
   public static final int YES_NO_OPTION = 0;
   public static final int YES_NO_CANCEL_OPTION = 1;
   public static final int OK_CANCEL_OPTION = 2;
   public static final int YES_OPTION = 0;
   public static final int NO_OPTION = 1;
   public static final int CANCEL_OPTION = 2;
   public static final int OK_OPTION = 0;
   public static final int CLOSED_OPTION = -1;
   public static final int ERROR_MESSAGE = 0;
   public static final int INFORMATION_MESSAGE = 1;
   public static final int WARNING_MESSAGE = 2;
   public static final int QUESTION_MESSAGE = 3;
   public static final int PLAIN_MESSAGE = -1;
   public static final String ICON_PROPERTY = "icon";
   public static final String MESSAGE_PROPERTY = "message";
   public static final String VALUE_PROPERTY = "value";
   public static final String OPTIONS_PROPERTY = "options";
   public static final String INITIAL_VALUE_PROPERTY = "initialValue";
   public static final String MESSAGE_TYPE_PROPERTY = "messageType";
   public static final String OPTION_TYPE_PROPERTY = "optionType";
   public static final String SELECTION_VALUES_PROPERTY = "selectionValues";
   public static final String INITIAL_SELECTION_VALUE_PROPERTY = "initialSelectionValue";
   public static final String INPUT_VALUE_PROPERTY = "inputValue";
   public static final String WANTS_INPUT_PROPERTY = "wantsInput";
   protected transient Icon icon;
   protected transient Object message;
   protected transient Object[] options;
   protected transient Object initialValue;
   protected int messageType;
   protected int optionType;
   protected transient Object value;
   protected transient Object[] selectionValues;
   protected transient Object inputValue;
   protected transient Object initialSelectionValue;
   protected boolean wantsInput;
   private static final Object sharedFrameKey = JOptionPane.class;

   public static String showInputDialog(Object var0) throws HeadlessException {
      return showInputDialog((Component)null, var0);
   }

   public static String showInputDialog(Object var0, Object var1) {
      return showInputDialog((Component)null, var0, var1);
   }

   public static String showInputDialog(Component var0, Object var1) throws HeadlessException {
      return showInputDialog(var0, var1, UIManager.getString("OptionPane.inputDialogTitle", (Component)var0), 3);
   }

   public static String showInputDialog(Component var0, Object var1, Object var2) {
      return (String)showInputDialog(var0, var1, UIManager.getString("OptionPane.inputDialogTitle", (Component)var0), 3, (Icon)null, (Object[])null, var2);
   }

   public static String showInputDialog(Component var0, Object var1, String var2, int var3) throws HeadlessException {
      return (String)showInputDialog(var0, var1, var2, var3, (Icon)null, (Object[])null, (Object)null);
   }

   public static Object showInputDialog(Component var0, Object var1, String var2, int var3, Icon var4, Object[] var5, Object var6) throws HeadlessException {
      JOptionPane var7 = new JOptionPane(var1, var3, 2, var4, (Object[])null, (Object)null);
      var7.setWantsInput(true);
      var7.setSelectionValues(var5);
      var7.setInitialSelectionValue(var6);
      var7.setComponentOrientation(((Component)(var0 == null ? getRootFrame() : var0)).getComponentOrientation());
      int var8 = styleFromMessageType(var3);
      JDialog var9 = var7.createDialog(var0, var2, var8);
      var7.selectInitialValue();
      var9.show();
      var9.dispose();
      Object var10 = var7.getInputValue();
      return var10 == UNINITIALIZED_VALUE ? null : var10;
   }

   public static void showMessageDialog(Component var0, Object var1) throws HeadlessException {
      showMessageDialog(var0, var1, UIManager.getString("OptionPane.messageDialogTitle", (Component)var0), 1);
   }

   public static void showMessageDialog(Component var0, Object var1, String var2, int var3) throws HeadlessException {
      showMessageDialog(var0, var1, var2, var3, (Icon)null);
   }

   public static void showMessageDialog(Component var0, Object var1, String var2, int var3, Icon var4) throws HeadlessException {
      showOptionDialog(var0, var1, var2, -1, var3, var4, (Object[])null, (Object)null);
   }

   public static int showConfirmDialog(Component var0, Object var1) throws HeadlessException {
      return showConfirmDialog(var0, var1, UIManager.getString("OptionPane.titleText"), 1);
   }

   public static int showConfirmDialog(Component var0, Object var1, String var2, int var3) throws HeadlessException {
      return showConfirmDialog(var0, var1, var2, var3, 3);
   }

   public static int showConfirmDialog(Component var0, Object var1, String var2, int var3, int var4) throws HeadlessException {
      return showConfirmDialog(var0, var1, var2, var3, var4, (Icon)null);
   }

   public static int showConfirmDialog(Component var0, Object var1, String var2, int var3, int var4, Icon var5) throws HeadlessException {
      return showOptionDialog(var0, var1, var2, var3, var4, var5, (Object[])null, (Object)null);
   }

   public static int showOptionDialog(Component var0, Object var1, String var2, int var3, int var4, Icon var5, Object[] var6, Object var7) throws HeadlessException {
      JOptionPane var8 = new JOptionPane(var1, var4, var3, var5, var6, var7);
      var8.setInitialValue(var7);
      var8.setComponentOrientation(((Component)(var0 == null ? getRootFrame() : var0)).getComponentOrientation());
      int var9 = styleFromMessageType(var4);
      JDialog var10 = var8.createDialog(var0, var2, var9);
      var8.selectInitialValue();
      var10.show();
      var10.dispose();
      Object var11 = var8.getValue();
      if (var11 == null) {
         return -1;
      } else if (var6 == null) {
         return var11 instanceof Integer ? (Integer)var11 : -1;
      } else {
         int var12 = 0;

         for(int var13 = var6.length; var12 < var13; ++var12) {
            if (var6[var12].equals(var11)) {
               return var12;
            }
         }

         return -1;
      }
   }

   public JDialog createDialog(Component var1, String var2) throws HeadlessException {
      int var3 = styleFromMessageType(this.getMessageType());
      return this.createDialog(var1, var2, var3);
   }

   public JDialog createDialog(String var1) throws HeadlessException {
      int var2 = styleFromMessageType(this.getMessageType());
      JDialog var3 = new JDialog((Dialog)null, var1, true);
      this.initDialog(var3, var2, (Component)null);
      return var3;
   }

   private JDialog createDialog(Component var1, String var2, int var3) throws HeadlessException {
      Window var5 = getWindowForComponent(var1);
      JDialog var4;
      if (var5 instanceof Frame) {
         var4 = new JDialog((Frame)var5, var2, true);
      } else {
         var4 = new JDialog((Dialog)var5, var2, true);
      }

      if (var5 instanceof SwingUtilities.SharedOwnerFrame) {
         WindowListener var6 = SwingUtilities.getSharedOwnerFrameShutdownListener();
         var4.addWindowListener(var6);
      }

      this.initDialog(var4, var3, var1);
      return var4;
   }

   private void initDialog(final JDialog var1, int var2, Component var3) {
      var1.setComponentOrientation(this.getComponentOrientation());
      Container var4 = var1.getContentPane();
      var4.setLayout(new BorderLayout());
      var4.add((Component)this, (Object)"Center");
      var1.setResizable(false);
      if (JDialog.isDefaultLookAndFeelDecorated()) {
         boolean var5 = UIManager.getLookAndFeel().getSupportsWindowDecorations();
         if (var5) {
            var1.setUndecorated(true);
            this.getRootPane().setWindowDecorationStyle(var2);
         }
      }

      var1.pack();
      var1.setLocationRelativeTo(var3);
      final PropertyChangeListener var7 = new PropertyChangeListener() {
         public void propertyChange(PropertyChangeEvent var1x) {
            if (var1.isVisible() && var1x.getSource() == JOptionPane.this && var1x.getPropertyName().equals("value") && var1x.getNewValue() != null && var1x.getNewValue() != JOptionPane.UNINITIALIZED_VALUE) {
               var1.setVisible(false);
            }

         }
      };
      WindowAdapter var6 = new WindowAdapter() {
         private boolean gotFocus = false;

         public void windowClosing(WindowEvent var1x) {
            JOptionPane.this.setValue((Object)null);
         }

         public void windowClosed(WindowEvent var1x) {
            JOptionPane.this.removePropertyChangeListener(var7);
            var1.getContentPane().removeAll();
         }

         public void windowGainedFocus(WindowEvent var1x) {
            if (!this.gotFocus) {
               JOptionPane.this.selectInitialValue();
               this.gotFocus = true;
            }

         }
      };
      var1.addWindowListener(var6);
      var1.addWindowFocusListener(var6);
      var1.addComponentListener(new ComponentAdapter() {
         public void componentShown(ComponentEvent var1) {
            JOptionPane.this.setValue(JOptionPane.UNINITIALIZED_VALUE);
         }
      });
      this.addPropertyChangeListener(var7);
   }

   public static void showInternalMessageDialog(Component var0, Object var1) {
      showInternalMessageDialog(var0, var1, UIManager.getString("OptionPane.messageDialogTitle", (Component)var0), 1);
   }

   public static void showInternalMessageDialog(Component var0, Object var1, String var2, int var3) {
      showInternalMessageDialog(var0, var1, var2, var3, (Icon)null);
   }

   public static void showInternalMessageDialog(Component var0, Object var1, String var2, int var3, Icon var4) {
      showInternalOptionDialog(var0, var1, var2, -1, var3, var4, (Object[])null, (Object)null);
   }

   public static int showInternalConfirmDialog(Component var0, Object var1) {
      return showInternalConfirmDialog(var0, var1, UIManager.getString("OptionPane.titleText"), 1);
   }

   public static int showInternalConfirmDialog(Component var0, Object var1, String var2, int var3) {
      return showInternalConfirmDialog(var0, var1, var2, var3, 3);
   }

   public static int showInternalConfirmDialog(Component var0, Object var1, String var2, int var3, int var4) {
      return showInternalConfirmDialog(var0, var1, var2, var3, var4, (Icon)null);
   }

   public static int showInternalConfirmDialog(Component var0, Object var1, String var2, int var3, int var4, Icon var5) {
      return showInternalOptionDialog(var0, var1, var2, var3, var4, var5, (Object[])null, (Object)null);
   }

   public static int showInternalOptionDialog(Component var0, Object var1, String var2, int var3, int var4, Icon var5, Object[] var6, Object var7) {
      JOptionPane var8 = new JOptionPane(var1, var4, var3, var5, var6, var7);
      var8.putClientProperty(ClientPropertyKey.PopupFactory_FORCE_HEAVYWEIGHT_POPUP, Boolean.TRUE);
      Component var9 = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
      var8.setInitialValue(var7);
      JInternalFrame var10 = var8.createInternalFrame(var0, var2);
      var8.selectInitialValue();
      var10.setVisible(true);
      if (var10.isVisible() && !var10.isShowing()) {
         for(Container var11 = var10.getParent(); var11 != null; var11 = var11.getParent()) {
            if (!var11.isVisible()) {
               var11.setVisible(true);
            }
         }
      }

      try {
         Method var18 = (Method)AccessController.doPrivileged((PrivilegedAction)(new JOptionPane.ModalPrivilegedAction(Container.class, "startLWModal")));
         if (var18 != null) {
            var18.invoke(var10, (Object[])null);
         }
      } catch (IllegalAccessException var15) {
      } catch (IllegalArgumentException var16) {
      } catch (InvocationTargetException var17) {
      }

      if (var0 instanceof JInternalFrame) {
         try {
            ((JInternalFrame)var0).setSelected(true);
         } catch (PropertyVetoException var14) {
         }
      }

      Object var19 = var8.getValue();
      if (var9 != null && var9.isShowing()) {
         var9.requestFocus();
      }

      if (var19 == null) {
         return -1;
      } else if (var6 == null) {
         return var19 instanceof Integer ? (Integer)var19 : -1;
      } else {
         int var12 = 0;

         for(int var13 = var6.length; var12 < var13; ++var12) {
            if (var6[var12].equals(var19)) {
               return var12;
            }
         }

         return -1;
      }
   }

   public static String showInternalInputDialog(Component var0, Object var1) {
      return showInternalInputDialog(var0, var1, UIManager.getString("OptionPane.inputDialogTitle", (Component)var0), 3);
   }

   public static String showInternalInputDialog(Component var0, Object var1, String var2, int var3) {
      return (String)showInternalInputDialog(var0, var1, var2, var3, (Icon)null, (Object[])null, (Object)null);
   }

   public static Object showInternalInputDialog(Component var0, Object var1, String var2, int var3, Icon var4, Object[] var5, Object var6) {
      JOptionPane var7 = new JOptionPane(var1, var3, 2, var4, (Object[])null, (Object)null);
      var7.putClientProperty(ClientPropertyKey.PopupFactory_FORCE_HEAVYWEIGHT_POPUP, Boolean.TRUE);
      Component var8 = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
      var7.setWantsInput(true);
      var7.setSelectionValues(var5);
      var7.setInitialSelectionValue(var6);
      JInternalFrame var9 = var7.createInternalFrame(var0, var2);
      var7.selectInitialValue();
      var9.setVisible(true);
      if (var9.isVisible() && !var9.isShowing()) {
         for(Container var10 = var9.getParent(); var10 != null; var10 = var10.getParent()) {
            if (!var10.isVisible()) {
               var10.setVisible(true);
            }
         }
      }

      try {
         Method var15 = (Method)AccessController.doPrivileged((PrivilegedAction)(new JOptionPane.ModalPrivilegedAction(Container.class, "startLWModal")));
         if (var15 != null) {
            var15.invoke(var9, (Object[])null);
         }
      } catch (IllegalAccessException var12) {
      } catch (IllegalArgumentException var13) {
      } catch (InvocationTargetException var14) {
      }

      if (var0 instanceof JInternalFrame) {
         try {
            ((JInternalFrame)var0).setSelected(true);
         } catch (PropertyVetoException var11) {
         }
      }

      if (var8 != null && var8.isShowing()) {
         var8.requestFocus();
      }

      Object var16 = var7.getInputValue();
      return var16 == UNINITIALIZED_VALUE ? null : var16;
   }

   public JInternalFrame createInternalFrame(Component var1, String var2) {
      Object var3 = getDesktopPaneForComponent(var1);
      if (var3 == null && (var1 == null || (var3 = var1.getParent()) == null)) {
         throw new RuntimeException("JOptionPane: parentComponent does not have a valid parent");
      } else {
         final JInternalFrame var4 = new JInternalFrame(var2, false, true, false, false);
         var4.putClientProperty("JInternalFrame.frameType", "optionDialog");
         var4.putClientProperty("JInternalFrame.messageType", this.getMessageType());
         var4.addInternalFrameListener(new InternalFrameAdapter() {
            public void internalFrameClosing(InternalFrameEvent var1) {
               if (JOptionPane.this.getValue() == JOptionPane.UNINITIALIZED_VALUE) {
                  JOptionPane.this.setValue((Object)null);
               }

            }
         });
         this.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent var1) {
               if (var4.isVisible() && var1.getSource() == JOptionPane.this && var1.getPropertyName().equals("value")) {
                  try {
                     Method var2 = (Method)AccessController.doPrivileged((PrivilegedAction)(new JOptionPane.ModalPrivilegedAction(Container.class, "stopLWModal")));
                     if (var2 != null) {
                        var2.invoke(var4, (Object[])null);
                     }
                  } catch (IllegalAccessException var4x) {
                  } catch (IllegalArgumentException var5) {
                  } catch (InvocationTargetException var6) {
                  }

                  try {
                     var4.setClosed(true);
                  } catch (PropertyVetoException var3) {
                  }

                  var4.setVisible(false);
               }

            }
         });
         var4.getContentPane().add((Component)this, (Object)"Center");
         if (var3 instanceof JDesktopPane) {
            ((Container)var3).add((Component)var4, (Object)JLayeredPane.MODAL_LAYER);
         } else {
            ((Container)var3).add((Component)var4, (Object)"Center");
         }

         Dimension var5 = var4.getPreferredSize();
         Dimension var6 = ((Container)var3).getSize();
         Dimension var7 = var1.getSize();
         var4.setBounds((var6.width - var5.width) / 2, (var6.height - var5.height) / 2, var5.width, var5.height);
         Point var8 = SwingUtilities.convertPoint(var1, 0, 0, (Component)var3);
         int var9 = (var7.width - var5.width) / 2 + var8.x;
         int var10 = (var7.height - var5.height) / 2 + var8.y;
         int var11 = var9 + var5.width - var6.width;
         int var12 = var10 + var5.height - var6.height;
         var9 = Math.max(var11 > 0 ? var9 - var11 : var9, 0);
         var10 = Math.max(var12 > 0 ? var10 - var12 : var10, 0);
         var4.setBounds(var9, var10, var5.width, var5.height);
         ((Container)var3).validate();

         try {
            var4.setSelected(true);
         } catch (PropertyVetoException var14) {
         }

         return var4;
      }
   }

   public static Frame getFrameForComponent(Component var0) throws HeadlessException {
      if (var0 == null) {
         return getRootFrame();
      } else {
         return var0 instanceof Frame ? (Frame)var0 : getFrameForComponent(var0.getParent());
      }
   }

   static Window getWindowForComponent(Component var0) throws HeadlessException {
      if (var0 == null) {
         return getRootFrame();
      } else {
         return !(var0 instanceof Frame) && !(var0 instanceof Dialog) ? getWindowForComponent(var0.getParent()) : (Window)var0;
      }
   }

   public static JDesktopPane getDesktopPaneForComponent(Component var0) {
      if (var0 == null) {
         return null;
      } else {
         return var0 instanceof JDesktopPane ? (JDesktopPane)var0 : getDesktopPaneForComponent(var0.getParent());
      }
   }

   public static void setRootFrame(Frame var0) {
      if (var0 != null) {
         SwingUtilities.appContextPut(sharedFrameKey, var0);
      } else {
         SwingUtilities.appContextRemove(sharedFrameKey);
      }

   }

   public static Frame getRootFrame() throws HeadlessException {
      Frame var0 = (Frame)SwingUtilities.appContextGet(sharedFrameKey);
      if (var0 == null) {
         var0 = SwingUtilities.getSharedOwnerFrame();
         SwingUtilities.appContextPut(sharedFrameKey, var0);
      }

      return var0;
   }

   public JOptionPane() {
      this("JOptionPane message");
   }

   public JOptionPane(Object var1) {
      this(var1, -1);
   }

   public JOptionPane(Object var1, int var2) {
      this(var1, var2, -1);
   }

   public JOptionPane(Object var1, int var2, int var3) {
      this(var1, var2, var3, (Icon)null);
   }

   public JOptionPane(Object var1, int var2, int var3, Icon var4) {
      this(var1, var2, var3, var4, (Object[])null);
   }

   public JOptionPane(Object var1, int var2, int var3, Icon var4, Object[] var5) {
      this(var1, var2, var3, var4, var5, (Object)null);
   }

   public JOptionPane(Object var1, int var2, int var3, Icon var4, Object[] var5, Object var6) {
      this.message = var1;
      this.options = var5;
      this.initialValue = var6;
      this.icon = var4;
      this.setMessageType(var2);
      this.setOptionType(var3);
      this.value = UNINITIALIZED_VALUE;
      this.inputValue = UNINITIALIZED_VALUE;
      this.updateUI();
   }

   public void setUI(OptionPaneUI var1) {
      if (this.ui != var1) {
         super.setUI(var1);
         this.invalidate();
      }

   }

   public OptionPaneUI getUI() {
      return (OptionPaneUI)this.ui;
   }

   public void updateUI() {
      this.setUI((OptionPaneUI)UIManager.getUI(this));
   }

   public String getUIClassID() {
      return "OptionPaneUI";
   }

   public void setMessage(Object var1) {
      Object var2 = this.message;
      this.message = var1;
      this.firePropertyChange("message", var2, this.message);
   }

   public Object getMessage() {
      return this.message;
   }

   public void setIcon(Icon var1) {
      Icon var2 = this.icon;
      this.icon = var1;
      this.firePropertyChange("icon", var2, this.icon);
   }

   public Icon getIcon() {
      return this.icon;
   }

   public void setValue(Object var1) {
      Object var2 = this.value;
      this.value = var1;
      this.firePropertyChange("value", var2, this.value);
   }

   public Object getValue() {
      return this.value;
   }

   public void setOptions(Object[] var1) {
      Object[] var2 = this.options;
      this.options = var1;
      this.firePropertyChange("options", var2, this.options);
   }

   public Object[] getOptions() {
      if (this.options != null) {
         int var1 = this.options.length;
         Object[] var2 = new Object[var1];
         System.arraycopy(this.options, 0, var2, 0, var1);
         return var2;
      } else {
         return this.options;
      }
   }

   public void setInitialValue(Object var1) {
      Object var2 = this.initialValue;
      this.initialValue = var1;
      this.firePropertyChange("initialValue", var2, this.initialValue);
   }

   public Object getInitialValue() {
      return this.initialValue;
   }

   public void setMessageType(int var1) {
      if (var1 != 0 && var1 != 1 && var1 != 2 && var1 != 3 && var1 != -1) {
         throw new RuntimeException("JOptionPane: type must be one of JOptionPane.ERROR_MESSAGE, JOptionPane.INFORMATION_MESSAGE, JOptionPane.WARNING_MESSAGE, JOptionPane.QUESTION_MESSAGE or JOptionPane.PLAIN_MESSAGE");
      } else {
         int var2 = this.messageType;
         this.messageType = var1;
         this.firePropertyChange("messageType", var2, this.messageType);
      }
   }

   public int getMessageType() {
      return this.messageType;
   }

   public void setOptionType(int var1) {
      if (var1 != -1 && var1 != 0 && var1 != 1 && var1 != 2) {
         throw new RuntimeException("JOptionPane: option type must be one of JOptionPane.DEFAULT_OPTION, JOptionPane.YES_NO_OPTION, JOptionPane.YES_NO_CANCEL_OPTION or JOptionPane.OK_CANCEL_OPTION");
      } else {
         int var2 = this.optionType;
         this.optionType = var1;
         this.firePropertyChange("optionType", var2, this.optionType);
      }
   }

   public int getOptionType() {
      return this.optionType;
   }

   public void setSelectionValues(Object[] var1) {
      Object[] var2 = this.selectionValues;
      this.selectionValues = var1;
      this.firePropertyChange("selectionValues", var2, var1);
      if (this.selectionValues != null) {
         this.setWantsInput(true);
      }

   }

   public Object[] getSelectionValues() {
      return this.selectionValues;
   }

   public void setInitialSelectionValue(Object var1) {
      Object var2 = this.initialSelectionValue;
      this.initialSelectionValue = var1;
      this.firePropertyChange("initialSelectionValue", var2, var1);
   }

   public Object getInitialSelectionValue() {
      return this.initialSelectionValue;
   }

   public void setInputValue(Object var1) {
      Object var2 = this.inputValue;
      this.inputValue = var1;
      this.firePropertyChange("inputValue", var2, var1);
   }

   public Object getInputValue() {
      return this.inputValue;
   }

   public int getMaxCharactersPerLineCount() {
      return Integer.MAX_VALUE;
   }

   public void setWantsInput(boolean var1) {
      boolean var2 = this.wantsInput;
      this.wantsInput = var1;
      this.firePropertyChange("wantsInput", var2, var1);
   }

   public boolean getWantsInput() {
      return this.wantsInput;
   }

   public void selectInitialValue() {
      OptionPaneUI var1 = this.getUI();
      if (var1 != null) {
         var1.selectInitialValue(this);
      }

   }

   private static int styleFromMessageType(int var0) {
      switch(var0) {
      case -1:
      default:
         return 2;
      case 0:
         return 4;
      case 1:
         return 3;
      case 2:
         return 8;
      case 3:
         return 7;
      }
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      Vector var2 = new Vector();
      var1.defaultWriteObject();
      if (this.icon != null && this.icon instanceof Serializable) {
         var2.addElement("icon");
         var2.addElement(this.icon);
      }

      if (this.message != null && this.message instanceof Serializable) {
         var2.addElement("message");
         var2.addElement(this.message);
      }

      int var4;
      int var5;
      if (this.options != null) {
         Vector var3 = new Vector();
         var4 = 0;

         for(var5 = this.options.length; var4 < var5; ++var4) {
            if (this.options[var4] instanceof Serializable) {
               var3.addElement(this.options[var4]);
            }
         }

         if (var3.size() > 0) {
            var4 = var3.size();
            Object[] var7 = new Object[var4];
            var3.copyInto(var7);
            var2.addElement("options");
            var2.addElement(var7);
         }
      }

      if (this.initialValue != null && this.initialValue instanceof Serializable) {
         var2.addElement("initialValue");
         var2.addElement(this.initialValue);
      }

      if (this.value != null && this.value instanceof Serializable) {
         var2.addElement("value");
         var2.addElement(this.value);
      }

      if (this.selectionValues != null) {
         boolean var6 = true;
         var4 = 0;

         for(var5 = this.selectionValues.length; var4 < var5; ++var4) {
            if (this.selectionValues[var4] != null && !(this.selectionValues[var4] instanceof Serializable)) {
               var6 = false;
               break;
            }
         }

         if (var6) {
            var2.addElement("selectionValues");
            var2.addElement(this.selectionValues);
         }
      }

      if (this.inputValue != null && this.inputValue instanceof Serializable) {
         var2.addElement("inputValue");
         var2.addElement(this.inputValue);
      }

      if (this.initialSelectionValue != null && this.initialSelectionValue instanceof Serializable) {
         var2.addElement("initialSelectionValue");
         var2.addElement(this.initialSelectionValue);
      }

      var1.writeObject(var2);
   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      Vector var2 = (Vector)var1.readObject();
      int var3 = 0;
      int var4 = var2.size();
      if (var3 < var4 && var2.elementAt(var3).equals("icon")) {
         ++var3;
         this.icon = (Icon)var2.elementAt(var3);
         ++var3;
      }

      if (var3 < var4 && var2.elementAt(var3).equals("message")) {
         ++var3;
         this.message = var2.elementAt(var3);
         ++var3;
      }

      if (var3 < var4 && var2.elementAt(var3).equals("options")) {
         ++var3;
         this.options = (Object[])((Object[])var2.elementAt(var3));
         ++var3;
      }

      if (var3 < var4 && var2.elementAt(var3).equals("initialValue")) {
         ++var3;
         this.initialValue = var2.elementAt(var3);
         ++var3;
      }

      if (var3 < var4 && var2.elementAt(var3).equals("value")) {
         ++var3;
         this.value = var2.elementAt(var3);
         ++var3;
      }

      if (var3 < var4 && var2.elementAt(var3).equals("selectionValues")) {
         ++var3;
         this.selectionValues = (Object[])((Object[])var2.elementAt(var3));
         ++var3;
      }

      if (var3 < var4 && var2.elementAt(var3).equals("inputValue")) {
         ++var3;
         this.inputValue = var2.elementAt(var3);
         ++var3;
      }

      if (var3 < var4 && var2.elementAt(var3).equals("initialSelectionValue")) {
         ++var3;
         this.initialSelectionValue = var2.elementAt(var3);
         ++var3;
      }

      if (this.getUIClassID().equals("OptionPaneUI")) {
         byte var5 = JComponent.getWriteObjCounter(this);
         --var5;
         JComponent.setWriteObjCounter(this, var5);
         if (var5 == 0 && this.ui != null) {
            this.ui.installUI(this);
         }
      }

   }

   protected String paramString() {
      String var1 = this.icon != null ? this.icon.toString() : "";
      String var2 = this.initialValue != null ? this.initialValue.toString() : "";
      String var3 = this.message != null ? this.message.toString() : "";
      String var4;
      if (this.messageType == 0) {
         var4 = "ERROR_MESSAGE";
      } else if (this.messageType == 1) {
         var4 = "INFORMATION_MESSAGE";
      } else if (this.messageType == 2) {
         var4 = "WARNING_MESSAGE";
      } else if (this.messageType == 3) {
         var4 = "QUESTION_MESSAGE";
      } else if (this.messageType == -1) {
         var4 = "PLAIN_MESSAGE";
      } else {
         var4 = "";
      }

      String var5;
      if (this.optionType == -1) {
         var5 = "DEFAULT_OPTION";
      } else if (this.optionType == 0) {
         var5 = "YES_NO_OPTION";
      } else if (this.optionType == 1) {
         var5 = "YES_NO_CANCEL_OPTION";
      } else if (this.optionType == 2) {
         var5 = "OK_CANCEL_OPTION";
      } else {
         var5 = "";
      }

      String var6 = this.wantsInput ? "true" : "false";
      return super.paramString() + ",icon=" + var1 + ",initialValue=" + var2 + ",message=" + var3 + ",messageType=" + var4 + ",optionType=" + var5 + ",wantsInput=" + var6;
   }

   public AccessibleContext getAccessibleContext() {
      if (this.accessibleContext == null) {
         this.accessibleContext = new JOptionPane.AccessibleJOptionPane();
      }

      return this.accessibleContext;
   }

   protected class AccessibleJOptionPane extends JComponent.AccessibleJComponent {
      protected AccessibleJOptionPane() {
         super();
      }

      public AccessibleRole getAccessibleRole() {
         switch(JOptionPane.this.messageType) {
         case 0:
         case 1:
         case 2:
            return AccessibleRole.ALERT;
         default:
            return AccessibleRole.OPTION_PANE;
         }
      }
   }

   private static class ModalPrivilegedAction implements PrivilegedAction<Method> {
      private Class<?> clazz;
      private String methodName;

      public ModalPrivilegedAction(Class<?> var1, String var2) {
         this.clazz = var1;
         this.methodName = var2;
      }

      public Method run() {
         Method var1 = null;

         try {
            var1 = this.clazz.getDeclaredMethod(this.methodName, (Class[])null);
         } catch (NoSuchMethodException var3) {
         }

         if (var1 != null) {
            var1.setAccessible(true);
         }

         return var1;
      }
   }
}
