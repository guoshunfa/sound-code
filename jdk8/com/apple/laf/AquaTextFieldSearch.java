package com.apple.laf;

import apple.laf.JRSUIConstants;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.plaf.TextUI;
import javax.swing.text.JTextComponent;

public class AquaTextFieldSearch {
   private static final String VARIANT_KEY = "JTextField.variant";
   private static final String SEARCH_VARIANT_VALUE = "search";
   private static final String FIND_POPUP_KEY = "JTextField.Search.FindPopup";
   private static final String FIND_ACTION_KEY = "JTextField.Search.FindAction";
   private static final String CANCEL_ACTION_KEY = "JTextField.Search.CancelAction";
   private static final String PROMPT_KEY = "JTextField.Search.Prompt";
   private static final AquaTextFieldSearch.SearchFieldPropertyListener SEARCH_FIELD_PROPERTY_LISTENER = new AquaTextFieldSearch.SearchFieldPropertyListener();
   protected static final AquaUtils.RecyclableSingleton<AquaTextFieldSearch.SearchFieldBorder> instance = new AquaUtils.RecyclableSingletonFromDefaultConstructor(AquaTextFieldSearch.SearchFieldBorder.class);

   protected static void installSearchFieldListener(JTextComponent var0) {
      var0.addPropertyChangeListener(SEARCH_FIELD_PROPERTY_LISTENER);
   }

   protected static void uninstallSearchFieldListener(JTextComponent var0) {
      var0.removePropertyChangeListener(SEARCH_FIELD_PROPERTY_LISTENER);
   }

   protected static boolean wantsToBeASearchField(JTextComponent var0) {
      return "search".equals(var0.getClientProperty("JTextField.variant"));
   }

   protected static boolean hasPopupMenu(JTextComponent var0) {
      return var0.getClientProperty("JTextField.Search.FindPopup") instanceof JPopupMenu;
   }

   public static AquaTextFieldSearch.SearchFieldBorder getSearchTextFieldBorder() {
      return (AquaTextFieldSearch.SearchFieldBorder)instance.get();
   }

   protected static void installSearchField(JTextComponent var0) {
      AquaTextFieldSearch.SearchFieldBorder var1 = getSearchTextFieldBorder();
      var0.setBorder(var1);
      var0.setLayout(var1.getCustomLayout());
      var0.add(getFindButton(var0), "West");
      var0.add(getCancelButton(var0), "East");
      var0.add(getPromptLabel(var0), "Center");
      TextUI var2 = var0.getUI();
      if (var2 instanceof AquaTextFieldUI) {
         ((AquaTextFieldUI)var2).setPaintingDelegate(var1);
      }

   }

   protected static void uninstallSearchField(JTextComponent var0) {
      var0.setBorder(UIManager.getBorder("TextField.border"));
      var0.removeAll();
      TextUI var1 = var0.getUI();
      if (var1 instanceof AquaTextFieldUI) {
         ((AquaTextFieldUI)var1).setPaintingDelegate((AquaUtils.JComponentPainter)null);
      }

   }

   protected static AquaIcon.DynamicallySizingJRSUIIcon getFindIcon(JTextComponent var0) {
      return var0.getClientProperty("JTextField.Search.FindPopup") == null ? new AquaIcon.DynamicallySizingJRSUIIcon(new AquaUtilControlSize.SizeDescriptor((new AquaUtilControlSize.SizeVariant(25, 22)).alterMargins(0, 4, 0, -5))) {
         public void initJRSUIState() {
            this.painter.state.set(JRSUIConstants.Widget.BUTTON_SEARCH_FIELD_FIND);
         }
      } : new AquaIcon.DynamicallySizingJRSUIIcon(new AquaUtilControlSize.SizeDescriptor((new AquaUtilControlSize.SizeVariant(25, 22)).alterMargins(0, 4, 0, 2))) {
         public void initJRSUIState() {
            this.painter.state.set(JRSUIConstants.Widget.BUTTON_SEARCH_FIELD_FIND);
         }
      };
   }

   protected static AquaIcon.DynamicallySizingJRSUIIcon getCancelIcon() {
      return new AquaIcon.DynamicallySizingJRSUIIcon(new AquaUtilControlSize.SizeDescriptor((new AquaUtilControlSize.SizeVariant(22, 22)).alterMargins(0, 0, 0, 4))) {
         public void initJRSUIState() {
            this.painter.state.set(JRSUIConstants.Widget.BUTTON_SEARCH_FIELD_CANCEL);
         }
      };
   }

   protected static JRSUIConstants.State getState(JButton var0) {
      if (!AquaFocusHandler.isActive(var0)) {
         return JRSUIConstants.State.INACTIVE;
      } else {
         return var0.getModel().isPressed() ? JRSUIConstants.State.PRESSED : JRSUIConstants.State.ACTIVE;
      }
   }

   protected static JButton createButton(final JTextComponent var0, final AquaIcon.DynamicallySizingJRSUIIcon var1) {
      final JButton var2 = new JButton();
      Insets var3 = var1.sizeVariant.margins;
      var2.setBorder(BorderFactory.createEmptyBorder(var3.top, var3.left, var3.bottom, var3.right));
      var2.setIcon(var1);
      var2.setBorderPainted(false);
      var2.setFocusable(false);
      var2.setCursor(new Cursor(0));
      var2.addChangeListener(new ChangeListener() {
         public void stateChanged(ChangeEvent var1x) {
            var1.painter.state.set(AquaTextFieldSearch.getState(var2));
         }
      });
      var2.addMouseListener(new MouseAdapter() {
         public void mousePressed(MouseEvent var1) {
            var0.requestFocusInWindow();
         }
      });
      return var2;
   }

   protected static JButton getFindButton(final JTextComponent var0) {
      AquaIcon.DynamicallySizingJRSUIIcon var1 = getFindIcon(var0);
      final JButton var2 = createButton(var0, var1);
      var2.setName("find");
      final Object var3 = var0.getClientProperty("JTextField.Search.FindPopup");
      if (var3 instanceof JPopupMenu) {
         var1.painter.state.set(JRSUIConstants.Variant.MENU_GLYPH);
         var2.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent var1) {
               ((JPopupMenu)var3).show(var2, 8, var2.getHeight() - 2);
               var0.requestFocusInWindow();
               var0.repaint();
            }
         });
      }

      Object var4 = var0.getClientProperty("JTextField.Search.FindAction");
      if (var4 instanceof ActionListener) {
         var2.addActionListener((ActionListener)var4);
      }

      return var2;
   }

   private static Component getPromptLabel(final JTextComponent var0) {
      final JLabel var1 = new JLabel();
      var1.setForeground(UIManager.getColor("TextField.inactiveForeground"));
      var0.getDocument().addDocumentListener(new DocumentListener() {
         public void changedUpdate(DocumentEvent var1x) {
            AquaTextFieldSearch.updatePromptLabel(var1, var0);
         }

         public void insertUpdate(DocumentEvent var1x) {
            AquaTextFieldSearch.updatePromptLabel(var1, var0);
         }

         public void removeUpdate(DocumentEvent var1x) {
            AquaTextFieldSearch.updatePromptLabel(var1, var0);
         }
      });
      var0.addFocusListener(new FocusAdapter() {
         public void focusGained(FocusEvent var1x) {
            AquaTextFieldSearch.updatePromptLabel(var1, var0);
         }

         public void focusLost(FocusEvent var1x) {
            AquaTextFieldSearch.updatePromptLabel(var1, var0);
         }
      });
      updatePromptLabel(var1, var0);
      return var1;
   }

   static void updatePromptLabel(final JLabel var0, final JTextComponent var1) {
      if (SwingUtilities.isEventDispatchThread()) {
         updatePromptLabelOnEDT(var0, var1);
      } else {
         SwingUtilities.invokeLater(new Runnable() {
            public void run() {
               AquaTextFieldSearch.updatePromptLabelOnEDT(var0, var1);
            }
         });
      }

   }

   static void updatePromptLabelOnEDT(JLabel var0, JTextComponent var1) {
      String var2 = " ";
      if (!var1.hasFocus() && "".equals(var1.getText())) {
         Object var3 = var1.getClientProperty("JTextField.Search.Prompt");
         if (var3 != null) {
            var2 = var3.toString();
         }
      }

      var0.setText(var2);
   }

   protected static JButton getCancelButton(final JTextComponent var0) {
      final JButton var1 = createButton(var0, getCancelIcon());
      var1.setName("cancel");
      Object var2 = var0.getClientProperty("JTextField.Search.CancelAction");
      if (var2 instanceof ActionListener) {
         var1.addActionListener((ActionListener)var2);
      }

      var1.addActionListener(new AbstractAction("cancel") {
         public void actionPerformed(ActionEvent var1) {
            var0.setText("");
         }
      });
      var0.getDocument().addDocumentListener(new DocumentListener() {
         public void changedUpdate(DocumentEvent var1x) {
            AquaTextFieldSearch.updateCancelIcon(var1, var0);
         }

         public void insertUpdate(DocumentEvent var1x) {
            AquaTextFieldSearch.updateCancelIcon(var1, var0);
         }

         public void removeUpdate(DocumentEvent var1x) {
            AquaTextFieldSearch.updateCancelIcon(var1, var0);
         }
      });
      updateCancelIcon(var1, var0);
      return var1;
   }

   static void updateCancelIcon(final JButton var0, final JTextComponent var1) {
      if (SwingUtilities.isEventDispatchThread()) {
         updateCancelIconOnEDT(var0, var1);
      } else {
         SwingUtilities.invokeLater(new Runnable() {
            public void run() {
               AquaTextFieldSearch.updateCancelIconOnEDT(var0, var1);
            }
         });
      }

   }

   static void updateCancelIconOnEDT(JButton var0, JTextComponent var1) {
      var0.setVisible(!"".equals(var1.getText()));
   }

   static class SearchFieldBorder extends AquaTextFieldBorder implements AquaUtils.JComponentPainter {
      protected boolean reallyPaintBorder;
      protected boolean doingLayout;

      public SearchFieldBorder() {
         super(new AquaUtilControlSize.SizeDescriptor((new AquaUtilControlSize.SizeVariant()).alterMargins(6, 31, 6, 24).alterInsets(3, 3, 3, 3)));
         this.painter.state.set(JRSUIConstants.Widget.FRAME_TEXT_FIELD_ROUND);
      }

      public SearchFieldBorder(AquaTextFieldSearch.SearchFieldBorder var1) {
         super((AquaTextFieldBorder)var1);
      }

      public void paint(JComponent var1, Graphics var2, int var3, int var4, int var5, int var6) {
         this.reallyPaintBorder = true;
         this.paintBorder(var1, var2, var3, var4, var5, var6);
         this.reallyPaintBorder = false;
      }

      public void paintBorder(Component var1, Graphics var2, int var3, int var4, int var5, int var6) {
         if (this.reallyPaintBorder) {
            super.paintBorder(var1, var2, var3, var4 - var6 % 2, var5, var6);
         }
      }

      public Insets getBorderInsets(Component var1) {
         if (this.doingLayout) {
            return new Insets(0, 0, 0, 0);
         } else {
            return !AquaTextFieldSearch.hasPopupMenu((JTextComponent)var1) ? new Insets(this.sizeVariant.margins.top, this.sizeVariant.margins.left - 7, this.sizeVariant.margins.bottom, this.sizeVariant.margins.right) : this.sizeVariant.margins;
         }
      }

      protected LayoutManager getCustomLayout() {
         return new BorderLayout(0, 0) {
            public void layoutContainer(Container var1) {
               SearchFieldBorder.this.doingLayout = true;
               super.layoutContainer(var1);
               SearchFieldBorder.this.doingLayout = false;
            }
         };
      }
   }

   static class SearchFieldPropertyListener implements PropertyChangeListener {
      public void propertyChange(PropertyChangeEvent var1) {
         Object var2 = var1.getSource();
         if (var2 instanceof JTextComponent) {
            String var3 = var1.getPropertyName();
            if ("JTextField.variant".equals(var3) || "JTextField.Search.FindPopup".equals(var3) || "JTextField.Search.FindAction".equals(var3) || "JTextField.Search.CancelAction".equals(var3) || "JTextField.Search.Prompt".equals(var3)) {
               JTextComponent var4 = (JTextComponent)var2;
               if (AquaTextFieldSearch.wantsToBeASearchField(var4)) {
                  AquaTextFieldSearch.uninstallSearchField(var4);
                  AquaTextFieldSearch.installSearchField(var4);
               } else {
                  AquaTextFieldSearch.uninstallSearchField(var4);
               }

            }
         }
      }
   }
}
