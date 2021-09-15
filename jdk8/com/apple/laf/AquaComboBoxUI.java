package com.apple.laf;

import apple.laf.JRSUIConstants;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleState;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.ComboBoxEditor;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JRootPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.plaf.ActionMapUIResource;
import javax.swing.plaf.ComboBoxUI;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.ListUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicComboBoxEditor;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.ComboPopup;

public class AquaComboBoxUI extends BasicComboBoxUI implements AquaUtilControlSize.Sizeable {
   static final String POPDOWN_CLIENT_PROPERTY_KEY = "JComboBox.isPopDown";
   static final String ISSQUARE_CLIENT_PROPERTY_KEY = "JComboBox.isSquare";
   private boolean wasOpaque;
   private Action highlightNextAction = new AquaComboBoxUI.ComboBoxAction() {
      public void performComboBoxAction(AquaComboBoxUI var1) {
         int var2 = AquaComboBoxUI.this.listBox.getSelectedIndex();
         if (var2 < AquaComboBoxUI.this.comboBox.getModel().getSize() - 1) {
            AquaComboBoxUI.this.listBox.setSelectedIndex(var2 + 1);
            AquaComboBoxUI.this.listBox.ensureIndexIsVisible(var2 + 1);
         }

         AquaComboBoxUI.this.comboBox.repaint();
      }
   };
   private Action highlightPreviousAction = new AquaComboBoxUI.ComboBoxAction() {
      void performComboBoxAction(AquaComboBoxUI var1) {
         int var2 = AquaComboBoxUI.this.listBox.getSelectedIndex();
         if (var2 > 0) {
            AquaComboBoxUI.this.listBox.setSelectedIndex(var2 - 1);
            AquaComboBoxUI.this.listBox.ensureIndexIsVisible(var2 - 1);
         }

         AquaComboBoxUI.this.comboBox.repaint();
      }
   };
   private Action highlightFirstAction = new AquaComboBoxUI.ComboBoxAction() {
      void performComboBoxAction(AquaComboBoxUI var1) {
         AquaComboBoxUI.this.listBox.setSelectedIndex(0);
         AquaComboBoxUI.this.listBox.ensureIndexIsVisible(0);
      }
   };
   private Action highlightLastAction = new AquaComboBoxUI.ComboBoxAction() {
      void performComboBoxAction(AquaComboBoxUI var1) {
         int var2 = AquaComboBoxUI.this.listBox.getModel().getSize();
         AquaComboBoxUI.this.listBox.setSelectedIndex(var2 - 1);
         AquaComboBoxUI.this.listBox.ensureIndexIsVisible(var2 - 1);
      }
   };
   private Action highlightPageUpAction = new AquaComboBoxUI.ComboBoxAction() {
      void performComboBoxAction(AquaComboBoxUI var1) {
         int var2 = AquaComboBoxUI.this.listBox.getSelectedIndex();
         int var3 = AquaComboBoxUI.this.listBox.getFirstVisibleIndex();
         if (var2 != var3) {
            AquaComboBoxUI.this.listBox.setSelectedIndex(var3);
         } else {
            int var4 = AquaComboBoxUI.this.listBox.getVisibleRect().height / AquaComboBoxUI.this.listBox.getCellBounds(0, 0).height;
            int var5 = var3 - var4;
            if (var5 < 0) {
               var5 = 0;
            }

            AquaComboBoxUI.this.listBox.ensureIndexIsVisible(var5);
            AquaComboBoxUI.this.listBox.setSelectedIndex(var5);
         }
      }
   };
   private Action highlightPageDownAction = new AquaComboBoxUI.ComboBoxAction() {
      void performComboBoxAction(AquaComboBoxUI var1) {
         int var2 = AquaComboBoxUI.this.listBox.getSelectedIndex();
         int var3 = AquaComboBoxUI.this.listBox.getLastVisibleIndex();
         if (var2 != var3) {
            AquaComboBoxUI.this.listBox.setSelectedIndex(var3);
         } else {
            int var4 = AquaComboBoxUI.this.listBox.getVisibleRect().height / AquaComboBoxUI.this.listBox.getCellBounds(0, 0).height;
            int var5 = AquaComboBoxUI.this.listBox.getModel().getSize() - 1;
            int var6 = var3 + var4;
            if (var6 > var5) {
               var6 = var5;
            }

            AquaComboBoxUI.this.listBox.ensureIndexIsVisible(var6);
            AquaComboBoxUI.this.listBox.setSelectedIndex(var6);
         }
      }
   };
   protected static final String IS_TABLE_CELL_EDITOR = "JComboBox.isTableCellEditor";
   private final Action triggerSelectionAction = new AbstractAction() {
      public void actionPerformed(ActionEvent var1) {
         AquaComboBoxUI.triggerSelectionEvent((JComboBox)var1.getSource(), var1);
      }

      public boolean isEnabled() {
         return AquaComboBoxUI.this.comboBox.isPopupVisible() && super.isEnabled();
      }
   };
   private static final Action toggleSelectionAction = new AbstractAction() {
      public void actionPerformed(ActionEvent var1) {
         JComboBox var2 = (JComboBox)var1.getSource();
         if (var2.isEnabled()) {
            if (!var2.isEditable()) {
               AquaComboBoxUI var3 = (AquaComboBoxUI)var2.getUI();
               if (var2.isPopupVisible()) {
                  var2.setSelectedIndex(var3.getPopup().getList().getSelectedIndex());
                  var2.setPopupVisible(false);
               } else {
                  var2.setPopupVisible(true);
               }
            }
         }
      }
   };
   private final Action hideAction = new AbstractAction() {
      public void actionPerformed(ActionEvent var1) {
         JComboBox var2 = (JComboBox)var1.getSource();
         var2.firePopupMenuCanceled();
         var2.setPopupVisible(false);
      }

      public boolean isEnabled() {
         return AquaComboBoxUI.this.comboBox.isPopupVisible() && super.isEnabled();
      }
   };
   static final AquaUtils.RecyclableSingleton<ClientPropertyApplicator<JComboBox, AquaComboBoxUI>> APPLICATOR = new AquaUtils.RecyclableSingleton<ClientPropertyApplicator<JComboBox, AquaComboBoxUI>>() {
      protected ClientPropertyApplicator<JComboBox, AquaComboBoxUI> getInstance() {
         return new ClientPropertyApplicator<JComboBox, AquaComboBoxUI>(new ClientPropertyApplicator.Property[]{new ClientPropertyApplicator.Property<AquaComboBoxUI>("Frame.active") {
            public void applyProperty(AquaComboBoxUI var1, Object var2) {
               if (Boolean.FALSE.equals(var2) && var1.comboBox != null) {
                  var1.comboBox.hidePopup();
               }

               if (var1.listBox != null) {
                  var1.listBox.repaint();
               }

            }
         }, new ClientPropertyApplicator.Property<AquaComboBoxUI>("editable") {
            public void applyProperty(AquaComboBoxUI var1, Object var2) {
               if (var1.comboBox != null) {
                  var1.comboBox.repaint();
               }
            }
         }, new ClientPropertyApplicator.Property<AquaComboBoxUI>("background") {
            public void applyProperty(AquaComboBoxUI var1, Object var2) {
               Color var3 = (Color)var2;
               if (var1.arrowButton != null) {
                  var1.arrowButton.setBackground(var3);
               }

               if (var1.listBox != null) {
                  var1.listBox.setBackground(var3);
               }

            }
         }, new ClientPropertyApplicator.Property<AquaComboBoxUI>("foreground") {
            public void applyProperty(AquaComboBoxUI var1, Object var2) {
               Color var3 = (Color)var2;
               if (var1.arrowButton != null) {
                  var1.arrowButton.setForeground(var3);
               }

               if (var1.listBox != null) {
                  var1.listBox.setForeground(var3);
               }

            }
         }, new ClientPropertyApplicator.Property<AquaComboBoxUI>("JComboBox.isPopDown") {
            public void applyProperty(AquaComboBoxUI var1, Object var2) {
               if (var1.arrowButton instanceof AquaComboBoxButton) {
                  ((AquaComboBoxButton)var1.arrowButton).setIsPopDown(Boolean.TRUE.equals(var2));
               }
            }
         }, new ClientPropertyApplicator.Property<AquaComboBoxUI>("JComboBox.isSquare") {
            public void applyProperty(AquaComboBoxUI var1, Object var2) {
               if (var1.arrowButton instanceof AquaComboBoxButton) {
                  ((AquaComboBoxButton)var1.arrowButton).setIsSquare(Boolean.TRUE.equals(var2));
               }
            }
         }}) {
            public AquaComboBoxUI convertJComponentToTarget(JComboBox var1) {
               ComboBoxUI var2 = var1.getUI();
               return var2 instanceof AquaComboBoxUI ? (AquaComboBoxUI)var2 : null;
            }
         };
      }
   };

   public static ComponentUI createUI(JComponent var0) {
      return new AquaComboBoxUI();
   }

   public void installUI(JComponent var1) {
      super.installUI(var1);
      LookAndFeel.installProperty(var1, "opaque", Boolean.FALSE);
      this.wasOpaque = var1.isOpaque();
      var1.setOpaque(false);
   }

   public void uninstallUI(JComponent var1) {
      var1.setOpaque(this.wasOpaque);
      super.uninstallUI(var1);
   }

   protected void installListeners() {
      super.installListeners();
      AquaUtilControlSize.addSizePropertyListener(this.comboBox);
   }

   protected void uninstallListeners() {
      AquaUtilControlSize.removeSizePropertyListener(this.comboBox);
      super.uninstallListeners();
   }

   protected void installComponents() {
      super.installComponents();
      getApplicator().attachAndApplyClientProperties(this.comboBox);
   }

   protected void uninstallComponents() {
      getApplicator().removeFrom(this.comboBox);
      super.uninstallComponents();
   }

   protected ItemListener createItemListener() {
      return new ItemListener() {
         long lastBlink = 0L;

         public void itemStateChanged(ItemEvent var1) {
            if (var1.getStateChange() == 1) {
               if (AquaComboBoxUI.this.popup.isVisible()) {
                  long var2 = System.currentTimeMillis();
                  if (var2 - 1000L >= this.lastBlink) {
                     this.lastBlink = var2;
                     JList var4 = AquaComboBoxUI.this.popup.getList();
                     ListUI var5 = var4.getUI();
                     if (var5 instanceof AquaListUI) {
                        final AquaListUI var6 = (AquaListUI)var5;
                        final int var7 = AquaComboBoxUI.this.comboBox.getSelectedIndex();
                        ListModel var8 = var4.getModel();
                        if (var8 != null) {
                           final Object var9 = var8.getElementAt(var7);
                           AquaUtils.blinkMenu(new AquaUtils.Selectable() {
                              public void paintSelected(boolean var1) {
                                 var6.repaintCell(var9, var7, var1);
                              }
                           });
                        }
                     }
                  }
               }
            }
         }
      };
   }

   public void paint(Graphics var1, JComponent var2) {
   }

   protected ListCellRenderer createRenderer() {
      return new AquaComboBoxRenderer(this.comboBox);
   }

   protected ComboPopup createPopup() {
      return new AquaComboBoxPopup(this.comboBox);
   }

   protected JButton createArrowButton() {
      return new AquaComboBoxButton(this, this.comboBox, this.currentValuePane, this.listBox);
   }

   protected ComboBoxEditor createEditor() {
      return new AquaComboBoxUI.AquaComboBoxEditor();
   }

   protected FocusListener createFocusListener() {
      return new BasicComboBoxUI.FocusHandler() {
         public void focusGained(FocusEvent var1) {
            super.focusGained(var1);
            if (AquaComboBoxUI.this.arrowButton != null) {
               AquaComboBoxUI.this.arrowButton.repaint();
            }

         }

         public void focusLost(FocusEvent var1) {
            AquaComboBoxUI.this.hasFocus = false;
            if (!var1.isTemporary()) {
               AquaComboBoxUI.this.setPopupVisible(AquaComboBoxUI.this.comboBox, false);
            }

            AquaComboBoxUI.this.comboBox.repaint();
            AccessibleContext var2 = AquaComboBoxUI.this.comboBox.getAccessibleContext();
            if (var2 != null) {
               var2.firePropertyChange("AccessibleState", AccessibleState.FOCUSED, (Object)null);
            }

            if (AquaComboBoxUI.this.arrowButton != null) {
               AquaComboBoxUI.this.arrowButton.repaint();
            }

         }
      };
   }

   protected void installKeyboardActions() {
      super.installKeyboardActions();
      ActionMapUIResource var1 = new ActionMapUIResource();
      var1.put("aquaSelectNext", this.highlightNextAction);
      var1.put("aquaSelectPrevious", this.highlightPreviousAction);
      var1.put("enterPressed", this.triggerSelectionAction);
      var1.put("aquaSpacePressed", toggleSelectionAction);
      var1.put("aquaSelectHome", this.highlightFirstAction);
      var1.put("aquaSelectEnd", this.highlightLastAction);
      var1.put("aquaSelectPageUp", this.highlightPageUpAction);
      var1.put("aquaSelectPageDown", this.highlightPageDownAction);
      var1.put("aquaHidePopup", this.hideAction);
      SwingUtilities.replaceUIActionMap(this.comboBox, var1);
   }

   public ComboPopup getPopup() {
      return this.popup;
   }

   protected LayoutManager createLayoutManager() {
      return new AquaComboBoxUI.AquaComboBoxLayoutManager();
   }

   protected static boolean isTableCellEditor(JComponent var0) {
      return Boolean.TRUE.equals(var0.getClientProperty("JComboBox.isTableCellEditor"));
   }

   protected static boolean isPopdown(JComboBox var0) {
      return var0.isEditable() || Boolean.TRUE.equals(var0.getClientProperty("JComboBox.isPopDown"));
   }

   protected static void triggerSelectionEvent(JComboBox var0, ActionEvent var1) {
      if (var0.isEnabled()) {
         AquaComboBoxUI var2 = (AquaComboBoxUI)var0.getUI();
         if (var2.getPopup().getList().getSelectedIndex() < 0) {
            var0.setPopupVisible(false);
         }

         if (isTableCellEditor(var0)) {
            var0.setSelectedIndex(var2.getPopup().getList().getSelectedIndex());
         } else if (var0.isPopupVisible()) {
            var0.setSelectedIndex(var2.getPopup().getList().getSelectedIndex());
            var0.setPopupVisible(false);
         } else {
            JRootPane var3 = SwingUtilities.getRootPane(var0);
            if (var3 != null) {
               InputMap var4 = var3.getInputMap(2);
               ActionMap var5 = var3.getActionMap();
               if (var4 != null && var5 != null) {
                  Object var6 = var4.get(KeyStroke.getKeyStroke(10, 0));
                  if (var6 != null) {
                     Action var7 = var5.get(var6);
                     if (var7 != null) {
                        var7.actionPerformed(new ActionEvent(var3, var1.getID(), var1.getActionCommand(), var1.getWhen(), var1.getModifiers()));
                     }
                  }
               }
            }
         }
      }
   }

   public void applySizeFor(JComponent var1, JRSUIConstants.Size var2) {
      if (this.arrowButton != null) {
         Border var3 = this.arrowButton.getBorder();
         if (var3 instanceof AquaButtonBorder) {
            AquaButtonBorder var4 = (AquaButtonBorder)var3;
            this.arrowButton.setBorder(var4.deriveBorderForSize(var2));
         }
      }
   }

   public Dimension getMinimumSize(JComponent var1) {
      if (!this.isMinimumSizeDirty) {
         return new Dimension(this.cachedMinimumSize);
      } else {
         boolean var2 = this.comboBox.isEditable();
         Dimension var3;
         Insets var5;
         if (!var2 && this.arrowButton != null && this.arrowButton instanceof AquaComboBoxButton) {
            AquaComboBoxButton var7 = (AquaComboBoxButton)this.arrowButton;
            var5 = var7.getInsets();
            Insets var6 = new Insets(0, 5, 0, 25);
            var3 = this.getDisplaySize();
            var3.width += var6.left + var6.right;
            var3.width += var5.left + var5.right;
            var3.width += var5.right + 10;
            var3.height += var6.top + var6.bottom;
            var3.height += var5.top + var5.bottom;
            var3.height = Math.max(27, var3.height);
         } else if (var2 && this.arrowButton != null && this.editor != null) {
            var3 = super.getMinimumSize(var1);
            Insets var4 = this.arrowButton.getMargin();
            var3.height += var4.top + var4.bottom;
         } else {
            var3 = super.getMinimumSize(var1);
         }

         Border var8 = var1.getBorder();
         if (var8 != null) {
            var5 = var8.getBorderInsets(var1);
            var3.height += var5.top + var5.bottom;
            var3.width += var5.left + var5.right;
         }

         this.cachedMinimumSize.setSize(var3.width, var3.height);
         this.isMinimumSizeDirty = false;
         return new Dimension(this.cachedMinimumSize);
      }
   }

   static ClientPropertyApplicator<JComboBox, AquaComboBoxUI> getApplicator() {
      return (ClientPropertyApplicator)APPLICATOR.get();
   }

   class AquaComboBoxLayoutManager extends BasicComboBoxUI.ComboBoxLayoutManager {
      AquaComboBoxLayoutManager() {
         super();
      }

      public void layoutContainer(Container var1) {
         int var3;
         int var4;
         if (AquaComboBoxUI.this.arrowButton != null && !AquaComboBoxUI.this.comboBox.isEditable()) {
            Insets var9 = AquaComboBoxUI.this.comboBox.getInsets();
            var3 = AquaComboBoxUI.this.comboBox.getWidth();
            var4 = AquaComboBoxUI.this.comboBox.getHeight();
            AquaComboBoxUI.this.arrowButton.setBounds(var9.left, var9.top, var3 - (var9.left + var9.right), var4 - (var9.top + var9.bottom));
         } else {
            JComboBox var2 = (JComboBox)var1;
            var3 = var2.getWidth();
            var4 = var2.getHeight();
            Insets var5 = AquaComboBoxUI.this.getInsets();
            int var6 = var4 - (var5.top + var5.bottom);
            if (AquaComboBoxUI.this.arrowButton != null) {
               AquaComboBoxUI.this.arrowButton.setBounds(var3 - (var5.right + 20), var5.top, 20, var6);
            }

            if (AquaComboBoxUI.this.editor != null) {
               Rectangle var8 = AquaComboBoxUI.this.rectangleForCurrentValue();
               var8.width += 4;
               ++var8.height;
               AquaComboBoxUI.this.editor.setBounds(var8);
            }

         }
      }
   }

   private abstract class ComboBoxAction extends AbstractAction {
      private ComboBoxAction() {
      }

      public void actionPerformed(ActionEvent var1) {
         if (AquaComboBoxUI.this.comboBox.isEnabled() && AquaComboBoxUI.this.comboBox.isShowing()) {
            if (AquaComboBoxUI.this.comboBox.isPopupVisible()) {
               AquaComboBoxUI var2 = (AquaComboBoxUI)AquaComboBoxUI.this.comboBox.getUI();
               this.performComboBoxAction(var2);
            } else {
               AquaComboBoxUI.this.comboBox.setPopupVisible(true);
            }

         }
      }

      abstract void performComboBoxAction(AquaComboBoxUI var1);

      // $FF: synthetic method
      ComboBoxAction(Object var2) {
         this();
      }
   }

   class AquaCustomComboTextField extends JTextField {
      public AquaCustomComboTextField() {
         InputMap var2 = this.getInputMap();
         var2.put(KeyStroke.getKeyStroke("DOWN"), AquaComboBoxUI.this.highlightNextAction);
         var2.put(KeyStroke.getKeyStroke("KP_DOWN"), AquaComboBoxUI.this.highlightNextAction);
         var2.put(KeyStroke.getKeyStroke("UP"), AquaComboBoxUI.this.highlightPreviousAction);
         var2.put(KeyStroke.getKeyStroke("KP_UP"), AquaComboBoxUI.this.highlightPreviousAction);
         var2.put(KeyStroke.getKeyStroke("HOME"), AquaComboBoxUI.this.highlightFirstAction);
         var2.put(KeyStroke.getKeyStroke("END"), AquaComboBoxUI.this.highlightLastAction);
         var2.put(KeyStroke.getKeyStroke("PAGE_UP"), AquaComboBoxUI.this.highlightPageUpAction);
         var2.put(KeyStroke.getKeyStroke("PAGE_DOWN"), AquaComboBoxUI.this.highlightPageDownAction);
         final Action var3 = this.getActionMap().get("notify-field-accept");
         var2.put(KeyStroke.getKeyStroke("ENTER"), new AbstractAction() {
            public void actionPerformed(ActionEvent var1) {
               if (AquaComboBoxUI.this.popup.isVisible()) {
                  AquaComboBoxUI.triggerSelectionEvent(AquaComboBoxUI.this.comboBox, var1);
                  if (AquaComboBoxUI.this.editor instanceof AquaComboBoxUI.AquaCustomComboTextField) {
                     ((AquaComboBoxUI.AquaCustomComboTextField)AquaComboBoxUI.this.editor).selectAll();
                  }
               } else {
                  var3.actionPerformed(var1);
               }

            }
         });
      }

      public void setText(String var1) {
         if (!this.getText().equals(var1)) {
            super.setText(var1);
         }
      }
   }

   final class AquaComboBoxEditor extends BasicComboBoxEditor implements UIResource, DocumentListener {
      AquaComboBoxEditor() {
         this.editor = AquaComboBoxUI.this.new AquaCustomComboTextField();
         this.editor.addFocusListener(this);
         this.editor.getDocument().addDocumentListener(this);
      }

      public void changedUpdate(DocumentEvent var1) {
         this.editorTextChanged();
      }

      public void insertUpdate(DocumentEvent var1) {
         this.editorTextChanged();
      }

      public void removeUpdate(DocumentEvent var1) {
         this.editorTextChanged();
      }

      private void editorTextChanged() {
         if (AquaComboBoxUI.this.popup.isVisible()) {
            String var1 = this.editor.getText();
            ListModel var2 = AquaComboBoxUI.this.listBox.getModel();
            int var3 = var2.getSize();

            for(int var4 = 0; var4 < var3; ++var4) {
               Object var5 = var2.getElementAt(var4);
               if (var5 != null) {
                  String var6 = var5.toString();
                  if (var6 != null && var6.equals(var1)) {
                     AquaComboBoxUI.this.popup.getList().setSelectedIndex(var4);
                     return;
                  }
               }
            }

            AquaComboBoxUI.this.popup.getList().clearSelection();
         }
      }
   }
}
