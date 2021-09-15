package javax.swing.plaf.basic;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Locale;
import javax.swing.ActionMap;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.OptionPaneUI;
import sun.security.action.GetPropertyAction;
import sun.swing.DefaultLookup;
import sun.swing.UIAction;

public class BasicOptionPaneUI extends OptionPaneUI {
   public static final int MinimumWidth = 262;
   public static final int MinimumHeight = 90;
   private static String newline = (String)AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction("line.separator")));
   protected JOptionPane optionPane;
   protected Dimension minimumSize;
   protected JComponent inputComponent;
   protected Component initialFocusComponent;
   protected boolean hasCustomComponents;
   protected PropertyChangeListener propertyChangeListener;
   private BasicOptionPaneUI.Handler handler;

   static void loadActionMap(LazyActionMap var0) {
      var0.put(new BasicOptionPaneUI.Actions("close"));
      BasicLookAndFeel.installAudioActionMap(var0);
   }

   public static ComponentUI createUI(JComponent var0) {
      return new BasicOptionPaneUI();
   }

   public void installUI(JComponent var1) {
      this.optionPane = (JOptionPane)var1;
      this.installDefaults();
      this.optionPane.setLayout(this.createLayoutManager());
      this.installComponents();
      this.installListeners();
      this.installKeyboardActions();
   }

   public void uninstallUI(JComponent var1) {
      this.uninstallComponents();
      this.optionPane.setLayout((LayoutManager)null);
      this.uninstallKeyboardActions();
      this.uninstallListeners();
      this.uninstallDefaults();
      this.optionPane = null;
   }

   protected void installDefaults() {
      LookAndFeel.installColorsAndFont(this.optionPane, "OptionPane.background", "OptionPane.foreground", "OptionPane.font");
      LookAndFeel.installBorder(this.optionPane, "OptionPane.border");
      this.minimumSize = UIManager.getDimension("OptionPane.minimumSize");
      LookAndFeel.installProperty(this.optionPane, "opaque", Boolean.TRUE);
   }

   protected void uninstallDefaults() {
      LookAndFeel.uninstallBorder(this.optionPane);
   }

   protected void installComponents() {
      this.optionPane.add(this.createMessageArea());
      Container var1 = this.createSeparator();
      if (var1 != null) {
         this.optionPane.add(var1);
      }

      this.optionPane.add(this.createButtonArea());
      this.optionPane.applyComponentOrientation(this.optionPane.getComponentOrientation());
   }

   protected void uninstallComponents() {
      this.hasCustomComponents = false;
      this.inputComponent = null;
      this.initialFocusComponent = null;
      this.optionPane.removeAll();
   }

   protected LayoutManager createLayoutManager() {
      return new BoxLayout(this.optionPane, 1);
   }

   protected void installListeners() {
      if ((this.propertyChangeListener = this.createPropertyChangeListener()) != null) {
         this.optionPane.addPropertyChangeListener(this.propertyChangeListener);
      }

   }

   protected void uninstallListeners() {
      if (this.propertyChangeListener != null) {
         this.optionPane.removePropertyChangeListener(this.propertyChangeListener);
         this.propertyChangeListener = null;
      }

      this.handler = null;
   }

   protected PropertyChangeListener createPropertyChangeListener() {
      return this.getHandler();
   }

   private BasicOptionPaneUI.Handler getHandler() {
      if (this.handler == null) {
         this.handler = new BasicOptionPaneUI.Handler();
      }

      return this.handler;
   }

   protected void installKeyboardActions() {
      InputMap var1 = this.getInputMap(2);
      SwingUtilities.replaceUIInputMap(this.optionPane, 2, var1);
      LazyActionMap.installLazyActionMap(this.optionPane, BasicOptionPaneUI.class, "OptionPane.actionMap");
   }

   protected void uninstallKeyboardActions() {
      SwingUtilities.replaceUIInputMap(this.optionPane, 2, (InputMap)null);
      SwingUtilities.replaceUIActionMap(this.optionPane, (ActionMap)null);
   }

   InputMap getInputMap(int var1) {
      if (var1 == 2) {
         Object[] var2 = (Object[])((Object[])DefaultLookup.get(this.optionPane, this, "OptionPane.windowBindings"));
         if (var2 != null) {
            return LookAndFeel.makeComponentInputMap(this.optionPane, var2);
         }
      }

      return null;
   }

   public Dimension getMinimumOptionPaneSize() {
      return this.minimumSize == null ? new Dimension(262, 90) : new Dimension(this.minimumSize.width, this.minimumSize.height);
   }

   public Dimension getPreferredSize(JComponent var1) {
      if (var1 == this.optionPane) {
         Dimension var2 = this.getMinimumOptionPaneSize();
         LayoutManager var3 = var1.getLayout();
         if (var3 != null) {
            Dimension var4 = var3.preferredLayoutSize(var1);
            return var2 != null ? new Dimension(Math.max(var4.width, var2.width), Math.max(var4.height, var2.height)) : var4;
         } else {
            return var2;
         }
      } else {
         return null;
      }
   }

   protected Container createMessageArea() {
      JPanel var1 = new JPanel();
      Border var2 = (Border)DefaultLookup.get(this.optionPane, this, "OptionPane.messageAreaBorder");
      if (var2 != null) {
         var1.setBorder(var2);
      }

      var1.setLayout(new BorderLayout());
      JPanel var3 = new JPanel(new GridBagLayout());
      JPanel var4 = new JPanel(new BorderLayout());
      var3.setName("OptionPane.body");
      var4.setName("OptionPane.realBody");
      if (this.getIcon() != null) {
         JPanel var5 = new JPanel();
         var5.setName("OptionPane.separator");
         var5.setPreferredSize(new Dimension(15, 1));
         var4.add((Component)var5, (Object)"Before");
      }

      var4.add((Component)var3, (Object)"Center");
      GridBagConstraints var6 = new GridBagConstraints();
      var6.gridx = var6.gridy = 0;
      var6.gridwidth = 0;
      var6.gridheight = 1;
      var6.anchor = DefaultLookup.getInt(this.optionPane, this, "OptionPane.messageAnchor", 10);
      var6.insets = new Insets(0, 0, 3, 0);
      this.addMessageComponents(var3, var6, this.getMessage(), this.getMaxCharactersPerLineCount(), false);
      var1.add(var4, "Center");
      this.addIcon(var1);
      return var1;
   }

   protected void addMessageComponents(Container var1, GridBagConstraints var2, Object var3, int var4, boolean var5) {
      if (var3 != null) {
         if (var3 instanceof Component) {
            if (!(var3 instanceof JScrollPane) && !(var3 instanceof JPanel)) {
               var2.fill = 2;
            } else {
               var2.fill = 1;
               var2.weighty = 1.0D;
            }

            var2.weightx = 1.0D;
            var1.add((Component)((Component)var3), (Object)var2);
            var2.weightx = 0.0D;
            var2.weighty = 0.0D;
            var2.fill = 0;
            ++var2.gridy;
            if (!var5) {
               this.hasCustomComponents = true;
            }
         } else {
            int var8;
            int var9;
            if (var3 instanceof Object[]) {
               Object[] var6 = (Object[])((Object[])var3);
               Object[] var7 = var6;
               var8 = var6.length;

               for(var9 = 0; var9 < var8; ++var9) {
                  Object var10 = var7[var9];
                  this.addMessageComponents(var1, var2, var10, var4, false);
               }
            } else if (var3 instanceof Icon) {
               JLabel var11 = new JLabel((Icon)var3, 0);
               this.configureMessageLabel(var11);
               this.addMessageComponents(var1, var2, var11, var4, true);
            } else {
               String var12 = var3.toString();
               int var13 = var12.length();
               if (var13 <= 0) {
                  return;
               }

               var9 = 0;
               if ((var8 = var12.indexOf(newline)) >= 0) {
                  var9 = newline.length();
               } else if ((var8 = var12.indexOf("\r\n")) >= 0) {
                  var9 = 2;
               } else if ((var8 = var12.indexOf(10)) >= 0) {
                  var9 = 1;
               }

               if (var8 >= 0) {
                  if (var8 == 0) {
                     JPanel var14 = new JPanel() {
                        public Dimension getPreferredSize() {
                           Font var1 = this.getFont();
                           return var1 != null ? new Dimension(1, var1.getSize() + 2) : new Dimension(0, 0);
                        }
                     };
                     var14.setName("OptionPane.break");
                     this.addMessageComponents(var1, var2, var14, var4, true);
                  } else {
                     this.addMessageComponents(var1, var2, var12.substring(0, var8), var4, false);
                  }

                  this.addMessageComponents(var1, var2, var12.substring(var8 + var9), var4, false);
               } else if (var13 > var4) {
                  Box var15 = Box.createVerticalBox();
                  var15.setName("OptionPane.verticalBox");
                  this.burstStringInto(var15, var12, var4);
                  this.addMessageComponents(var1, var2, var15, var4, true);
               } else {
                  JLabel var16 = new JLabel(var12, 10);
                  var16.setName("OptionPane.label");
                  this.configureMessageLabel(var16);
                  this.addMessageComponents(var1, var2, var16, var4, true);
               }
            }
         }

      }
   }

   protected Object getMessage() {
      this.inputComponent = null;
      if (this.optionPane != null) {
         if (!this.optionPane.getWantsInput()) {
            return this.optionPane.getMessage();
         } else {
            Object var1 = this.optionPane.getMessage();
            Object[] var2 = this.optionPane.getSelectionValues();
            Object var3 = this.optionPane.getInitialSelectionValue();
            Object var4;
            if (var2 != null) {
               if (var2.length < 20) {
                  JComboBox var5 = new JComboBox();
                  var5.setName("OptionPane.comboBox");
                  int var6 = 0;

                  for(int var7 = var2.length; var6 < var7; ++var6) {
                     var5.addItem(var2[var6]);
                  }

                  if (var3 != null) {
                     var5.setSelectedItem(var3);
                  }

                  this.inputComponent = var5;
                  var4 = var5;
               } else {
                  JList var8 = new JList(var2);
                  JScrollPane var11 = new JScrollPane(var8);
                  var11.setName("OptionPane.scrollPane");
                  var8.setName("OptionPane.list");
                  var8.setVisibleRowCount(10);
                  var8.setSelectionMode(0);
                  if (var3 != null) {
                     var8.setSelectedValue(var3, true);
                  }

                  var8.addMouseListener(this.getHandler());
                  var4 = var11;
                  this.inputComponent = var8;
               }
            } else {
               BasicOptionPaneUI.MultiplexingTextField var9 = new BasicOptionPaneUI.MultiplexingTextField(20);
               var9.setName("OptionPane.textField");
               var9.setKeyStrokes(new KeyStroke[]{KeyStroke.getKeyStroke("ENTER")});
               if (var3 != null) {
                  String var12 = var3.toString();
                  var9.setText(var12);
                  var9.setSelectionStart(0);
                  var9.setSelectionEnd(var12.length());
               }

               var9.addActionListener(this.getHandler());
               var4 = this.inputComponent = var9;
            }

            Object[] var10;
            if (var1 == null) {
               var10 = new Object[]{var4};
            } else {
               var10 = new Object[]{var1, var4};
            }

            return var10;
         }
      } else {
         return null;
      }
   }

   protected void addIcon(Container var1) {
      Icon var2 = this.getIcon();
      if (var2 != null) {
         JLabel var3 = new JLabel(var2);
         var3.setName("OptionPane.iconLabel");
         var3.setVerticalAlignment(1);
         var1.add((Component)var3, (Object)"Before");
      }

   }

   protected Icon getIcon() {
      Icon var1 = this.optionPane == null ? null : this.optionPane.getIcon();
      if (var1 == null && this.optionPane != null) {
         var1 = this.getIconForType(this.optionPane.getMessageType());
      }

      return var1;
   }

   protected Icon getIconForType(int var1) {
      if (var1 >= 0 && var1 <= 3) {
         String var2 = null;
         switch(var1) {
         case 0:
            var2 = "OptionPane.errorIcon";
            break;
         case 1:
            var2 = "OptionPane.informationIcon";
            break;
         case 2:
            var2 = "OptionPane.warningIcon";
            break;
         case 3:
            var2 = "OptionPane.questionIcon";
         }

         return var2 != null ? (Icon)DefaultLookup.get(this.optionPane, this, var2) : null;
      } else {
         return null;
      }
   }

   protected int getMaxCharactersPerLineCount() {
      return this.optionPane.getMaxCharactersPerLineCount();
   }

   protected void burstStringInto(Container var1, String var2, int var3) {
      int var4 = var2.length();
      if (var4 > 0) {
         if (var4 > var3) {
            int var5 = var2.lastIndexOf(32, var3);
            if (var5 <= 0) {
               var5 = var2.indexOf(32, var3);
            }

            if (var5 > 0 && var5 < var4) {
               this.burstStringInto(var1, var2.substring(0, var5), var3);
               this.burstStringInto(var1, var2.substring(var5 + 1), var3);
               return;
            }
         }

         JLabel var6 = new JLabel(var2, 2);
         var6.setName("OptionPane.label");
         this.configureMessageLabel(var6);
         var1.add(var6);
      }
   }

   protected Container createSeparator() {
      return null;
   }

   protected Container createButtonArea() {
      JPanel var1 = new JPanel();
      Border var2 = (Border)DefaultLookup.get(this.optionPane, this, "OptionPane.buttonAreaBorder");
      var1.setName("OptionPane.buttonArea");
      if (var2 != null) {
         var1.setBorder(var2);
      }

      var1.setLayout(new BasicOptionPaneUI.ButtonAreaLayout(DefaultLookup.getBoolean(this.optionPane, this, "OptionPane.sameSizeButtons", true), DefaultLookup.getInt(this.optionPane, this, "OptionPane.buttonPadding", 6), DefaultLookup.getInt(this.optionPane, this, "OptionPane.buttonOrientation", 0), DefaultLookup.getBoolean(this.optionPane, this, "OptionPane.isYesLast", false)));
      this.addButtonComponents(var1, this.getButtons(), this.getInitialValueIndex());
      return var1;
   }

   protected void addButtonComponents(Container var1, Object[] var2, int var3) {
      if (var2 != null && var2.length > 0) {
         boolean var4 = this.getSizeButtonsToSameWidth();
         boolean var5 = true;
         int var6 = var2.length;
         JButton[] var7 = null;
         int var8 = 0;
         if (var4) {
            var7 = new JButton[var6];
         }

         for(int var9 = 0; var9 < var6; ++var9) {
            Object var10 = var2[var9];
            Object var11;
            JButton var12;
            if (var10 instanceof Component) {
               var5 = false;
               var11 = (Component)var10;
               var1.add((Component)var11);
               this.hasCustomComponents = true;
            } else {
               if (var10 instanceof BasicOptionPaneUI.ButtonFactory) {
                  var12 = ((BasicOptionPaneUI.ButtonFactory)var10).createButton();
               } else if (var10 instanceof Icon) {
                  var12 = new JButton((Icon)var10);
               } else {
                  var12 = new JButton(var10.toString());
               }

               var12.setName("OptionPane.button");
               var12.setMultiClickThreshhold((long)DefaultLookup.getInt(this.optionPane, this, "OptionPane.buttonClickThreshhold", 0));
               this.configureButton(var12);
               var1.add(var12);
               ActionListener var13 = this.createButtonActionListener(var9);
               if (var13 != null) {
                  var12.addActionListener(var13);
               }

               var11 = var12;
            }

            if (var4 && var5 && var11 instanceof JButton) {
               var7[var9] = (JButton)var11;
               var8 = Math.max(var8, ((Component)var11).getMinimumSize().width);
            }

            if (var9 == var3) {
               this.initialFocusComponent = (Component)var11;
               if (this.initialFocusComponent instanceof JButton) {
                  var12 = (JButton)this.initialFocusComponent;
                  var12.addHierarchyListener(new HierarchyListener() {
                     public void hierarchyChanged(HierarchyEvent var1) {
                        if ((var1.getChangeFlags() & 1L) != 0L) {
                           JButton var2 = (JButton)var1.getComponent();
                           JRootPane var3 = SwingUtilities.getRootPane(var2);
                           if (var3 != null) {
                              var3.setDefaultButton(var2);
                           }
                        }

                     }
                  });
               }
            }
         }

         ((BasicOptionPaneUI.ButtonAreaLayout)var1.getLayout()).setSyncAllWidths(var4 && var5);
         if (DefaultLookup.getBoolean(this.optionPane, this, "OptionPane.setButtonMargin", true) && var4 && var5) {
            int var15 = var6 <= 2 ? 8 : 4;

            for(int var16 = 0; var16 < var6; ++var16) {
               JButton var14 = var7[var16];
               var14.setMargin(new Insets(2, var15, 2, var15));
            }
         }
      }

   }

   protected ActionListener createButtonActionListener(int var1) {
      return new BasicOptionPaneUI.ButtonActionListener(var1);
   }

   protected Object[] getButtons() {
      if (this.optionPane != null) {
         Object[] var1 = this.optionPane.getOptions();
         if (var1 == null) {
            int var3 = this.optionPane.getOptionType();
            Locale var4 = this.optionPane.getLocale();
            int var5 = DefaultLookup.getInt(this.optionPane, this, "OptionPane.buttonMinimumWidth", -1);
            BasicOptionPaneUI.ButtonFactory[] var2;
            if (var3 == 0) {
               var2 = new BasicOptionPaneUI.ButtonFactory[]{new BasicOptionPaneUI.ButtonFactory(UIManager.getString("OptionPane.yesButtonText", (Locale)var4), this.getMnemonic("OptionPane.yesButtonMnemonic", var4), (Icon)DefaultLookup.get(this.optionPane, this, "OptionPane.yesIcon"), var5), new BasicOptionPaneUI.ButtonFactory(UIManager.getString("OptionPane.noButtonText", (Locale)var4), this.getMnemonic("OptionPane.noButtonMnemonic", var4), (Icon)DefaultLookup.get(this.optionPane, this, "OptionPane.noIcon"), var5)};
            } else if (var3 == 1) {
               var2 = new BasicOptionPaneUI.ButtonFactory[]{new BasicOptionPaneUI.ButtonFactory(UIManager.getString("OptionPane.yesButtonText", (Locale)var4), this.getMnemonic("OptionPane.yesButtonMnemonic", var4), (Icon)DefaultLookup.get(this.optionPane, this, "OptionPane.yesIcon"), var5), new BasicOptionPaneUI.ButtonFactory(UIManager.getString("OptionPane.noButtonText", (Locale)var4), this.getMnemonic("OptionPane.noButtonMnemonic", var4), (Icon)DefaultLookup.get(this.optionPane, this, "OptionPane.noIcon"), var5), new BasicOptionPaneUI.ButtonFactory(UIManager.getString("OptionPane.cancelButtonText", (Locale)var4), this.getMnemonic("OptionPane.cancelButtonMnemonic", var4), (Icon)DefaultLookup.get(this.optionPane, this, "OptionPane.cancelIcon"), var5)};
            } else if (var3 == 2) {
               var2 = new BasicOptionPaneUI.ButtonFactory[]{new BasicOptionPaneUI.ButtonFactory(UIManager.getString("OptionPane.okButtonText", (Locale)var4), this.getMnemonic("OptionPane.okButtonMnemonic", var4), (Icon)DefaultLookup.get(this.optionPane, this, "OptionPane.okIcon"), var5), new BasicOptionPaneUI.ButtonFactory(UIManager.getString("OptionPane.cancelButtonText", (Locale)var4), this.getMnemonic("OptionPane.cancelButtonMnemonic", var4), (Icon)DefaultLookup.get(this.optionPane, this, "OptionPane.cancelIcon"), var5)};
            } else {
               var2 = new BasicOptionPaneUI.ButtonFactory[]{new BasicOptionPaneUI.ButtonFactory(UIManager.getString("OptionPane.okButtonText", (Locale)var4), this.getMnemonic("OptionPane.okButtonMnemonic", var4), (Icon)DefaultLookup.get(this.optionPane, this, "OptionPane.okIcon"), var5)};
            }

            return var2;
         } else {
            return var1;
         }
      } else {
         return null;
      }
   }

   private int getMnemonic(String var1, Locale var2) {
      String var3 = (String)UIManager.get(var1, var2);
      if (var3 == null) {
         return 0;
      } else {
         try {
            return Integer.parseInt(var3);
         } catch (NumberFormatException var5) {
            return 0;
         }
      }
   }

   protected boolean getSizeButtonsToSameWidth() {
      return true;
   }

   protected int getInitialValueIndex() {
      if (this.optionPane != null) {
         Object var1 = this.optionPane.getInitialValue();
         Object[] var2 = this.optionPane.getOptions();
         if (var2 == null) {
            return 0;
         }

         if (var1 != null) {
            for(int var3 = var2.length - 1; var3 >= 0; --var3) {
               if (var2[var3].equals(var1)) {
                  return var3;
               }
            }
         }
      }

      return -1;
   }

   protected void resetInputValue() {
      if (this.inputComponent != null && this.inputComponent instanceof JTextField) {
         this.optionPane.setInputValue(((JTextField)this.inputComponent).getText());
      } else if (this.inputComponent != null && this.inputComponent instanceof JComboBox) {
         this.optionPane.setInputValue(((JComboBox)this.inputComponent).getSelectedItem());
      } else if (this.inputComponent != null) {
         this.optionPane.setInputValue(((JList)this.inputComponent).getSelectedValue());
      }

   }

   public void selectInitialValue(JOptionPane var1) {
      if (this.inputComponent != null) {
         this.inputComponent.requestFocus();
      } else {
         if (this.initialFocusComponent != null) {
            this.initialFocusComponent.requestFocus();
         }

         if (this.initialFocusComponent instanceof JButton) {
            JRootPane var2 = SwingUtilities.getRootPane(this.initialFocusComponent);
            if (var2 != null) {
               var2.setDefaultButton((JButton)this.initialFocusComponent);
            }
         }
      }

   }

   public boolean containsCustomComponents(JOptionPane var1) {
      return this.hasCustomComponents;
   }

   private void configureMessageLabel(JLabel var1) {
      Color var2 = (Color)DefaultLookup.get(this.optionPane, this, "OptionPane.messageForeground");
      if (var2 != null) {
         var1.setForeground(var2);
      }

      Font var3 = (Font)DefaultLookup.get(this.optionPane, this, "OptionPane.messageFont");
      if (var3 != null) {
         var1.setFont(var3);
      }

   }

   private void configureButton(JButton var1) {
      Font var2 = (Font)DefaultLookup.get(this.optionPane, this, "OptionPane.buttonFont");
      if (var2 != null) {
         var1.setFont(var2);
      }

   }

   static {
      if (newline == null) {
         newline = "\n";
      }

   }

   private static class ButtonFactory {
      private String text;
      private int mnemonic;
      private Icon icon;
      private int minimumWidth = -1;

      ButtonFactory(String var1, int var2, Icon var3, int var4) {
         this.text = var1;
         this.mnemonic = var2;
         this.icon = var3;
         this.minimumWidth = var4;
      }

      JButton createButton() {
         Object var1;
         if (this.minimumWidth > 0) {
            var1 = new BasicOptionPaneUI.ButtonFactory.ConstrainedButton(this.text, this.minimumWidth);
         } else {
            var1 = new JButton(this.text);
         }

         if (this.icon != null) {
            ((JButton)var1).setIcon(this.icon);
         }

         if (this.mnemonic != 0) {
            ((JButton)var1).setMnemonic(this.mnemonic);
         }

         return (JButton)var1;
      }

      private static class ConstrainedButton extends JButton {
         int minimumWidth;

         ConstrainedButton(String var1, int var2) {
            super(var1);
            this.minimumWidth = var2;
         }

         public Dimension getMinimumSize() {
            Dimension var1 = super.getMinimumSize();
            var1.width = Math.max(var1.width, this.minimumWidth);
            return var1;
         }

         public Dimension getPreferredSize() {
            Dimension var1 = super.getPreferredSize();
            var1.width = Math.max(var1.width, this.minimumWidth);
            return var1;
         }
      }
   }

   private static class Actions extends UIAction {
      private static final String CLOSE = "close";

      Actions(String var1) {
         super(var1);
      }

      public void actionPerformed(ActionEvent var1) {
         if (this.getName() == "close") {
            JOptionPane var2 = (JOptionPane)var1.getSource();
            var2.setValue(-1);
         }

      }
   }

   private static class MultiplexingTextField extends JTextField {
      private KeyStroke[] strokes;

      MultiplexingTextField(int var1) {
         super(var1);
      }

      void setKeyStrokes(KeyStroke[] var1) {
         this.strokes = var1;
      }

      protected boolean processKeyBinding(KeyStroke var1, KeyEvent var2, int var3, boolean var4) {
         boolean var5 = super.processKeyBinding(var1, var2, var3, var4);
         if (var5 && var3 != 2) {
            for(int var6 = this.strokes.length - 1; var6 >= 0; --var6) {
               if (this.strokes[var6].equals(var1)) {
                  return false;
               }
            }
         }

         return var5;
      }
   }

   private class Handler implements ActionListener, MouseListener, PropertyChangeListener {
      private Handler() {
      }

      public void actionPerformed(ActionEvent var1) {
         BasicOptionPaneUI.this.optionPane.setInputValue(((JTextField)var1.getSource()).getText());
      }

      public void mouseClicked(MouseEvent var1) {
      }

      public void mouseReleased(MouseEvent var1) {
      }

      public void mouseEntered(MouseEvent var1) {
      }

      public void mouseExited(MouseEvent var1) {
      }

      public void mousePressed(MouseEvent var1) {
         if (var1.getClickCount() == 2) {
            JList var2 = (JList)var1.getSource();
            int var3 = var2.locationToIndex(var1.getPoint());
            BasicOptionPaneUI.this.optionPane.setInputValue(var2.getModel().getElementAt(var3));
            BasicOptionPaneUI.this.optionPane.setValue(0);
         }

      }

      public void propertyChange(PropertyChangeEvent var1) {
         if (var1.getSource() == BasicOptionPaneUI.this.optionPane) {
            if ("ancestor" == var1.getPropertyName()) {
               JOptionPane var2 = (JOptionPane)var1.getSource();
               boolean var3;
               if (var1.getOldValue() == null) {
                  var3 = true;
               } else {
                  var3 = false;
               }

               switch(var2.getMessageType()) {
               case -1:
                  if (var3) {
                     BasicLookAndFeel.playSound(BasicOptionPaneUI.this.optionPane, "OptionPane.informationSound");
                  }
                  break;
               case 0:
                  if (var3) {
                     BasicLookAndFeel.playSound(BasicOptionPaneUI.this.optionPane, "OptionPane.errorSound");
                  }
                  break;
               case 1:
                  if (var3) {
                     BasicLookAndFeel.playSound(BasicOptionPaneUI.this.optionPane, "OptionPane.informationSound");
                  }
                  break;
               case 2:
                  if (var3) {
                     BasicLookAndFeel.playSound(BasicOptionPaneUI.this.optionPane, "OptionPane.warningSound");
                  }
                  break;
               case 3:
                  if (var3) {
                     BasicLookAndFeel.playSound(BasicOptionPaneUI.this.optionPane, "OptionPane.questionSound");
                  }
                  break;
               default:
                  System.err.println("Undefined JOptionPane type: " + var2.getMessageType());
               }
            }

            String var5 = var1.getPropertyName();
            if (var5 != "options" && var5 != "initialValue" && var5 != "icon" && var5 != "messageType" && var5 != "optionType" && var5 != "message" && var5 != "selectionValues" && var5 != "initialSelectionValue" && var5 != "wantsInput") {
               if (var5 == "componentOrientation") {
                  ComponentOrientation var6 = (ComponentOrientation)var1.getNewValue();
                  JOptionPane var4 = (JOptionPane)var1.getSource();
                  if (var6 != var1.getOldValue()) {
                     var4.applyComponentOrientation(var6);
                  }
               }
            } else {
               BasicOptionPaneUI.this.uninstallComponents();
               BasicOptionPaneUI.this.installComponents();
               BasicOptionPaneUI.this.optionPane.validate();
            }
         }

      }

      // $FF: synthetic method
      Handler(Object var2) {
         this();
      }
   }

   public class ButtonActionListener implements ActionListener {
      protected int buttonIndex;

      public ButtonActionListener(int var2) {
         this.buttonIndex = var2;
      }

      public void actionPerformed(ActionEvent var1) {
         if (BasicOptionPaneUI.this.optionPane != null) {
            int var2 = BasicOptionPaneUI.this.optionPane.getOptionType();
            Object[] var3 = BasicOptionPaneUI.this.optionPane.getOptions();
            if (BasicOptionPaneUI.this.inputComponent != null && (var3 != null || var2 == -1 || (var2 == 0 || var2 == 1 || var2 == 2) && this.buttonIndex == 0)) {
               BasicOptionPaneUI.this.resetInputValue();
            }

            if (var3 == null) {
               if (var2 == 2 && this.buttonIndex == 1) {
                  BasicOptionPaneUI.this.optionPane.setValue(2);
               } else {
                  BasicOptionPaneUI.this.optionPane.setValue(this.buttonIndex);
               }
            } else {
               BasicOptionPaneUI.this.optionPane.setValue(var3[this.buttonIndex]);
            }
         }

      }
   }

   public class PropertyChangeHandler implements PropertyChangeListener {
      public void propertyChange(PropertyChangeEvent var1) {
         BasicOptionPaneUI.this.getHandler().propertyChange(var1);
      }
   }

   public static class ButtonAreaLayout implements LayoutManager {
      protected boolean syncAllWidths;
      protected int padding;
      protected boolean centersChildren;
      private int orientation;
      private boolean reverseButtons;
      private boolean useOrientation;

      public ButtonAreaLayout(boolean var1, int var2) {
         this.syncAllWidths = var1;
         this.padding = var2;
         this.centersChildren = true;
         this.useOrientation = false;
      }

      ButtonAreaLayout(boolean var1, int var2, int var3, boolean var4) {
         this(var1, var2);
         this.useOrientation = true;
         this.orientation = var3;
         this.reverseButtons = var4;
      }

      public void setSyncAllWidths(boolean var1) {
         this.syncAllWidths = var1;
      }

      public boolean getSyncAllWidths() {
         return this.syncAllWidths;
      }

      public void setPadding(int var1) {
         this.padding = var1;
      }

      public int getPadding() {
         return this.padding;
      }

      public void setCentersChildren(boolean var1) {
         this.centersChildren = var1;
         this.useOrientation = false;
      }

      public boolean getCentersChildren() {
         return this.centersChildren;
      }

      private int getOrientation(Container var1) {
         if (!this.useOrientation) {
            return 0;
         } else if (var1.getComponentOrientation().isLeftToRight()) {
            return this.orientation;
         } else {
            switch(this.orientation) {
            case 0:
               return 0;
            case 1:
            case 3:
            default:
               return 2;
            case 2:
               return 4;
            case 4:
               return 2;
            }
         }
      }

      public void addLayoutComponent(String var1, Component var2) {
      }

      public void layoutContainer(Container var1) {
         Component[] var2 = var1.getComponents();
         if (var2 != null && var2.length > 0) {
            int var3 = var2.length;
            Insets var4 = var1.getInsets();
            int var5 = 0;
            int var6 = 0;
            int var7 = 0;
            int var8 = 0;
            int var9 = 0;
            boolean var10 = var1.getComponentOrientation().isLeftToRight();
            boolean var11 = var10 ? this.reverseButtons : !this.reverseButtons;

            int var12;
            for(var12 = 0; var12 < var3; ++var12) {
               Dimension var13 = var2[var12].getPreferredSize();
               var5 = Math.max(var5, var13.width);
               var6 = Math.max(var6, var13.height);
               var7 += var13.width;
            }

            if (this.getSyncAllWidths()) {
               var7 = var5 * var3;
            }

            var7 += (var3 - 1) * this.padding;
            switch(this.getOrientation(var1)) {
            case 0:
               if (!this.getCentersChildren() && var3 >= 2) {
                  var8 = var4.left;
                  if (this.getSyncAllWidths()) {
                     var9 = (var1.getWidth() - var4.left - var4.right - var7) / (var3 - 1) + var5;
                  } else {
                     var9 = (var1.getWidth() - var4.left - var4.right - var7) / (var3 - 1);
                  }
               } else {
                  var8 = (var1.getWidth() - var7) / 2;
               }
            case 1:
            case 3:
            default:
               break;
            case 2:
               var8 = var4.left;
               break;
            case 4:
               var8 = var1.getWidth() - var4.right - var7;
            }

            for(var12 = 0; var12 < var3; ++var12) {
               int var15 = var11 ? var3 - var12 - 1 : var12;
               Dimension var14 = var2[var15].getPreferredSize();
               if (this.getSyncAllWidths()) {
                  var2[var15].setBounds(var8, var4.top, var5, var6);
               } else {
                  var2[var15].setBounds(var8, var4.top, var14.width, var14.height);
               }

               if (var9 != 0) {
                  var8 += var9;
               } else {
                  var8 += var2[var15].getWidth() + this.padding;
               }
            }
         }

      }

      public Dimension minimumLayoutSize(Container var1) {
         if (var1 != null) {
            Component[] var2 = var1.getComponents();
            if (var2 != null && var2.length > 0) {
               int var4 = var2.length;
               int var5 = 0;
               Insets var6 = var1.getInsets();
               int var7 = var6.top + var6.bottom;
               int var8 = var6.left + var6.right;
               Dimension var3;
               int var9;
               int var10;
               if (this.syncAllWidths) {
                  var9 = 0;

                  for(var10 = 0; var10 < var4; ++var10) {
                     var3 = var2[var10].getPreferredSize();
                     var5 = Math.max(var5, var3.height);
                     var9 = Math.max(var9, var3.width);
                  }

                  return new Dimension(var8 + var9 * var4 + (var4 - 1) * this.padding, var7 + var5);
               }

               var9 = 0;

               for(var10 = 0; var10 < var4; ++var10) {
                  var3 = var2[var10].getPreferredSize();
                  var5 = Math.max(var5, var3.height);
                  var9 += var3.width;
               }

               var9 += (var4 - 1) * this.padding;
               return new Dimension(var8 + var9, var7 + var5);
            }
         }

         return new Dimension(0, 0);
      }

      public Dimension preferredLayoutSize(Container var1) {
         return this.minimumLayoutSize(var1);
      }

      public void removeLayoutComponent(Component var1) {
      }
   }
}
