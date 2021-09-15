package javax.swing.text.html;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.BitSet;
import javax.swing.AbstractListModel;
import javax.swing.Box;
import javax.swing.ButtonModel;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultButtonModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentListener;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.ListDataListener;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.ComponentView;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.ElementIterator;
import javax.swing.text.PlainDocument;
import javax.swing.text.StyleConstants;

public class FormView extends ComponentView implements ActionListener {
   /** @deprecated */
   @Deprecated
   public static final String SUBMIT = new String("Submit Query");
   /** @deprecated */
   @Deprecated
   public static final String RESET = new String("Reset");
   static final String PostDataProperty = "javax.swing.JEditorPane.postdata";
   private short maxIsPreferred;

   public FormView(Element var1) {
      super(var1);
   }

   protected Component createComponent() {
      AttributeSet var1 = this.getElement().getAttributes();
      HTML.Tag var2 = (HTML.Tag)var1.getAttribute(StyleConstants.NameAttribute);
      Object var3 = null;
      Object var4 = var1.getAttribute(StyleConstants.ModelAttribute);
      this.removeStaleListenerForModel(var4);
      if (var2 == HTML.Tag.INPUT) {
         var3 = this.createInputComponent(var1, var4);
      } else {
         int var6;
         if (var2 == HTML.Tag.SELECT) {
            if (var4 instanceof OptionListModel) {
               JList var5 = new JList((ListModel)var4);
               var6 = HTML.getIntegerAttributeValue(var1, HTML.Attribute.SIZE, 1);
               var5.setVisibleRowCount(var6);
               var5.setSelectionModel((ListSelectionModel)var4);
               var3 = new JScrollPane(var5);
            } else {
               var3 = new JComboBox((ComboBoxModel)var4);
               this.maxIsPreferred = 3;
            }
         } else if (var2 == HTML.Tag.TEXTAREA) {
            JTextArea var8 = new JTextArea((Document)var4);
            var6 = HTML.getIntegerAttributeValue(var1, HTML.Attribute.ROWS, 1);
            var8.setRows(var6);
            int var7 = HTML.getIntegerAttributeValue(var1, HTML.Attribute.COLS, 20);
            this.maxIsPreferred = 3;
            var8.setColumns(var7);
            var3 = new JScrollPane(var8, 22, 32);
         }
      }

      if (var3 != null) {
         ((JComponent)var3).setAlignmentY(1.0F);
      }

      return (Component)var3;
   }

   private JComponent createInputComponent(AttributeSet var1, Object var2) {
      Object var3 = null;
      String var4 = (String)var1.getAttribute(HTML.Attribute.TYPE);
      String var5;
      JButton var6;
      if (!var4.equals("submit") && !var4.equals("reset")) {
         if (var4.equals("image")) {
            var5 = (String)var1.getAttribute(HTML.Attribute.SRC);

            try {
               URL var7 = ((HTMLDocument)this.getElement().getDocument()).getBase();
               URL var8 = new URL(var7, var5);
               ImageIcon var9 = new ImageIcon(var8);
               var6 = new JButton(var9);
            } catch (MalformedURLException var10) {
               var6 = new JButton(var5);
            }

            if (var2 != null) {
               var6.setModel((ButtonModel)var2);
               var6.addMouseListener(new FormView.MouseEventListener());
            }

            var3 = var6;
            this.maxIsPreferred = 3;
         } else if (var4.equals("checkbox")) {
            var3 = new JCheckBox();
            if (var2 != null) {
               ((JCheckBox)var3).setModel((JToggleButton.ToggleButtonModel)var2);
            }

            this.maxIsPreferred = 3;
         } else if (var4.equals("radio")) {
            var3 = new JRadioButton();
            if (var2 != null) {
               ((JRadioButton)var3).setModel((JToggleButton.ToggleButtonModel)var2);
            }

            this.maxIsPreferred = 3;
         } else if (var4.equals("text")) {
            int var11 = HTML.getIntegerAttributeValue(var1, HTML.Attribute.SIZE, -1);
            JTextField var15;
            if (var11 > 0) {
               var15 = new JTextField();
               var15.setColumns(var11);
            } else {
               var15 = new JTextField();
               var15.setColumns(20);
            }

            var3 = var15;
            if (var2 != null) {
               var15.setDocument((Document)var2);
            }

            var15.addActionListener(this);
            this.maxIsPreferred = 3;
         } else {
            int var17;
            if (var4.equals("password")) {
               JPasswordField var12 = new JPasswordField();
               var3 = var12;
               if (var2 != null) {
                  var12.setDocument((Document)var2);
               }

               var17 = HTML.getIntegerAttributeValue(var1, HTML.Attribute.SIZE, -1);
               var12.setColumns(var17 > 0 ? var17 : 20);
               var12.addActionListener(this);
               this.maxIsPreferred = 3;
            } else if (var4.equals("file")) {
               JTextField var13 = new JTextField();
               if (var2 != null) {
                  var13.setDocument((Document)var2);
               }

               var17 = HTML.getIntegerAttributeValue(var1, HTML.Attribute.SIZE, -1);
               var13.setColumns(var17 > 0 ? var17 : 20);
               JButton var14 = new JButton(UIManager.getString("FormView.browseFileButtonText"));
               Box var16 = Box.createHorizontalBox();
               var16.add(var13);
               var16.add(Box.createHorizontalStrut(5));
               var16.add(var14);
               var14.addActionListener(new FormView.BrowseFileAction(var1, (Document)var2));
               var3 = var16;
               this.maxIsPreferred = 3;
            }
         }
      } else {
         var5 = (String)var1.getAttribute(HTML.Attribute.VALUE);
         if (var5 == null) {
            if (var4.equals("submit")) {
               var5 = UIManager.getString("FormView.submitButtonText");
            } else {
               var5 = UIManager.getString("FormView.resetButtonText");
            }
         }

         var6 = new JButton(var5);
         if (var2 != null) {
            var6.setModel((ButtonModel)var2);
            var6.addActionListener(this);
         }

         var3 = var6;
         this.maxIsPreferred = 3;
      }

      return (JComponent)var3;
   }

   private void removeStaleListenerForModel(Object var1) {
      String var3;
      int var6;
      if (var1 instanceof DefaultButtonModel) {
         DefaultButtonModel var2 = (DefaultButtonModel)var1;
         var3 = "javax.swing.AbstractButton$Handler";
         ActionListener[] var4 = var2.getActionListeners();
         int var5 = var4.length;

         for(var6 = 0; var6 < var5; ++var6) {
            ActionListener var7 = var4[var6];
            if (var3.equals(var7.getClass().getName())) {
               var2.removeActionListener(var7);
            }
         }

         ChangeListener[] var11 = var2.getChangeListeners();
         var5 = var11.length;

         for(var6 = 0; var6 < var5; ++var6) {
            ChangeListener var17 = var11[var6];
            if (var3.equals(var17.getClass().getName())) {
               var2.removeChangeListener(var17);
            }
         }

         ItemListener[] var12 = var2.getItemListeners();
         var5 = var12.length;

         for(var6 = 0; var6 < var5; ++var6) {
            ItemListener var18 = var12[var6];
            if (var3.equals(var18.getClass().getName())) {
               var2.removeItemListener(var18);
            }
         }
      } else {
         int var19;
         if (var1 instanceof AbstractListModel) {
            AbstractListModel var9 = (AbstractListModel)var1;
            var3 = "javax.swing.plaf.basic.BasicListUI$Handler";
            String var13 = "javax.swing.plaf.basic.BasicComboBoxUI$Handler";
            ListDataListener[] var15 = var9.getListDataListeners();
            var6 = var15.length;

            for(var19 = 0; var19 < var6; ++var19) {
               ListDataListener var8 = var15[var19];
               if (var3.equals(var8.getClass().getName()) || var13.equals(var8.getClass().getName())) {
                  var9.removeListDataListener(var8);
               }
            }
         } else if (var1 instanceof AbstractDocument) {
            String var10 = "javax.swing.plaf.basic.BasicTextUI$UpdateHandler";
            var3 = "javax.swing.text.DefaultCaret$Handler";
            AbstractDocument var14 = (AbstractDocument)var1;
            DocumentListener[] var16 = var14.getDocumentListeners();
            var6 = var16.length;

            for(var19 = 0; var19 < var6; ++var19) {
               DocumentListener var20 = var16[var19];
               if (var10.equals(var20.getClass().getName()) || var3.equals(var20.getClass().getName())) {
                  var14.removeDocumentListener(var20);
               }
            }
         }
      }

   }

   public float getMaximumSpan(int var1) {
      switch(var1) {
      case 0:
         if ((this.maxIsPreferred & 1) == 1) {
            super.getMaximumSpan(var1);
            return this.getPreferredSpan(var1);
         }

         return super.getMaximumSpan(var1);
      case 1:
         if ((this.maxIsPreferred & 2) == 2) {
            super.getMaximumSpan(var1);
            return this.getPreferredSpan(var1);
         }

         return super.getMaximumSpan(var1);
      default:
         return super.getMaximumSpan(var1);
      }
   }

   public void actionPerformed(ActionEvent var1) {
      Element var2 = this.getElement();
      StringBuilder var3 = new StringBuilder();
      HTMLDocument var4 = (HTMLDocument)this.getDocument();
      AttributeSet var5 = var2.getAttributes();
      String var6 = (String)var5.getAttribute(HTML.Attribute.TYPE);
      if (var6.equals("submit")) {
         this.getFormData(var3);
         this.submitData(var3.toString());
      } else if (var6.equals("reset")) {
         this.resetForm();
      } else if (var6.equals("text") || var6.equals("password")) {
         if (this.isLastTextOrPasswordField()) {
            this.getFormData(var3);
            this.submitData(var3.toString());
         } else {
            this.getComponent().transferFocus();
         }
      }

   }

   protected void submitData(String var1) {
      Element var2 = this.getFormElement();
      AttributeSet var3 = var2.getAttributes();
      HTMLDocument var4 = (HTMLDocument)var2.getDocument();
      URL var5 = var4.getBase();
      String var6 = (String)var3.getAttribute(HTML.Attribute.TARGET);
      if (var6 == null) {
         var6 = "_self";
      }

      String var7 = (String)var3.getAttribute(HTML.Attribute.METHOD);
      if (var7 == null) {
         var7 = "GET";
      }

      var7 = var7.toLowerCase();
      boolean var8 = var7.equals("post");
      if (var8) {
         this.storePostData(var4, var6, var1);
      }

      String var9 = (String)var3.getAttribute(HTML.Attribute.ACTION);

      final URL var10;
      try {
         var10 = var9 == null ? new URL(var5.getProtocol(), var5.getHost(), var5.getPort(), var5.getFile()) : new URL(var5, var9);
         if (!var8) {
            String var11 = var1.toString();
            var10 = new URL(var10 + "?" + var11);
         }
      } catch (MalformedURLException var16) {
         var10 = null;
      }

      final JEditorPane var17 = (JEditorPane)this.getContainer();
      HTMLEditorKit var12 = (HTMLEditorKit)var17.getEditorKit();
      final FormSubmitEvent var13 = null;
      if (!var12.isAutoFormSubmission() || var4.isFrameDocument()) {
         FormSubmitEvent.MethodType var14 = var8 ? FormSubmitEvent.MethodType.POST : FormSubmitEvent.MethodType.GET;
         var13 = new FormSubmitEvent(this, HyperlinkEvent.EventType.ACTIVATED, var10, var2, var6, var14, var1);
      }

      SwingUtilities.invokeLater(new Runnable() {
         public void run() {
            if (var13 != null) {
               var17.fireHyperlinkUpdate(var13);
            } else {
               try {
                  var17.setPage(var10);
               } catch (IOException var2) {
                  UIManager.getLookAndFeel().provideErrorFeedback(var17);
               }
            }

         }
      });
   }

   private void storePostData(HTMLDocument var1, String var2, String var3) {
      Object var4 = var1;
      String var5 = "javax.swing.JEditorPane.postdata";
      if (var1.isFrameDocument()) {
         FrameView.FrameEditorPane var6 = (FrameView.FrameEditorPane)this.getContainer();
         FrameView var7 = var6.getFrameView();
         JEditorPane var8 = var7.getOutermostJEditorPane();
         if (var8 != null) {
            var4 = var8.getDocument();
            var5 = var5 + "." + var2;
         }
      }

      ((Document)var4).putProperty(var5, var3);
   }

   protected void imageSubmit(String var1) {
      StringBuilder var2 = new StringBuilder();
      Element var3 = this.getElement();
      HTMLDocument var4 = (HTMLDocument)var3.getDocument();
      this.getFormData(var2);
      if (var2.length() > 0) {
         var2.append('&');
      }

      var2.append(var1);
      this.submitData(var2.toString());
   }

   private String getImageData(Point var1) {
      String var2 = var1.x + ":" + var1.y;
      int var3 = var2.indexOf(58);
      String var4 = var2.substring(0, var3);
      ++var3;
      String var5 = var2.substring(var3);
      String var6 = (String)this.getElement().getAttributes().getAttribute(HTML.Attribute.NAME);
      String var7;
      if (var6 != null && !var6.equals("")) {
         var6 = URLEncoder.encode(var6);
         var7 = var6 + ".x=" + var4 + "&" + var6 + ".y=" + var5;
      } else {
         var7 = "x=" + var4 + "&y=" + var5;
      }

      return var7;
   }

   private Element getFormElement() {
      for(Element var1 = this.getElement(); var1 != null; var1 = var1.getParentElement()) {
         if (var1.getAttributes().getAttribute(StyleConstants.NameAttribute) == HTML.Tag.FORM) {
            return var1;
         }
      }

      return null;
   }

   private void getFormData(StringBuilder var1) {
      Element var2 = this.getFormElement();
      if (var2 != null) {
         ElementIterator var3 = new ElementIterator(var2);

         while(true) {
            Element var4;
            String var5;
            do {
               do {
                  do {
                     if ((var4 = var3.next()) == null) {
                        return;
                     }
                  } while(!this.isControl(var4));

                  var5 = (String)var4.getAttributes().getAttribute(HTML.Attribute.TYPE);
               } while(var5 != null && var5.equals("submit") && var4 != this.getElement());
            } while(var5 != null && var5.equals("image"));

            this.loadElementDataIntoBuffer(var4, var1);
         }
      }
   }

   private void loadElementDataIntoBuffer(Element var1, StringBuilder var2) {
      AttributeSet var3 = var1.getAttributes();
      String var4 = (String)var3.getAttribute(HTML.Attribute.NAME);
      if (var4 != null) {
         String var5 = null;
         HTML.Tag var6 = (HTML.Tag)var1.getAttributes().getAttribute(StyleConstants.NameAttribute);
         if (var6 == HTML.Tag.INPUT) {
            var5 = this.getInputElementData(var3);
         } else if (var6 == HTML.Tag.TEXTAREA) {
            var5 = this.getTextAreaData(var3);
         } else if (var6 == HTML.Tag.SELECT) {
            this.loadSelectData(var3, var2);
         }

         if (var4 != null && var5 != null) {
            this.appendBuffer(var2, var4, var5);
         }

      }
   }

   private String getInputElementData(AttributeSet var1) {
      Object var2 = var1.getAttribute(StyleConstants.ModelAttribute);
      String var3 = (String)var1.getAttribute(HTML.Attribute.TYPE);
      String var4 = null;
      Document var5;
      if (!var3.equals("text") && !var3.equals("password")) {
         if (!var3.equals("submit") && !var3.equals("hidden")) {
            if (!var3.equals("radio") && !var3.equals("checkbox")) {
               if (var3.equals("file")) {
                  var5 = (Document)var2;

                  String var6;
                  try {
                     var6 = var5.getText(0, var5.getLength());
                  } catch (BadLocationException var8) {
                     var6 = null;
                  }

                  if (var6 != null && var6.length() > 0) {
                     var4 = var6;
                  }
               }
            } else {
               ButtonModel var10 = (ButtonModel)var2;
               if (var10.isSelected()) {
                  var4 = (String)var1.getAttribute(HTML.Attribute.VALUE);
                  if (var4 == null) {
                     var4 = "on";
                  }
               }
            }
         } else {
            var4 = (String)var1.getAttribute(HTML.Attribute.VALUE);
            if (var4 == null) {
               var4 = "";
            }
         }
      } else {
         var5 = (Document)var2;

         try {
            var4 = var5.getText(0, var5.getLength());
         } catch (BadLocationException var9) {
            var4 = null;
         }
      }

      return var4;
   }

   private String getTextAreaData(AttributeSet var1) {
      Document var2 = (Document)var1.getAttribute(StyleConstants.ModelAttribute);

      try {
         return var2.getText(0, var2.getLength());
      } catch (BadLocationException var4) {
         return null;
      }
   }

   private void loadSelectData(AttributeSet var1, StringBuilder var2) {
      String var3 = (String)var1.getAttribute(HTML.Attribute.NAME);
      if (var3 != null) {
         Object var4 = var1.getAttribute(StyleConstants.ModelAttribute);
         if (var4 instanceof OptionListModel) {
            OptionListModel var5 = (OptionListModel)var4;

            for(int var6 = 0; var6 < var5.getSize(); ++var6) {
               if (var5.isSelectedIndex(var6)) {
                  Option var7 = (Option)var5.getElementAt(var6);
                  this.appendBuffer(var2, var3, var7.getValue());
               }
            }
         } else if (var4 instanceof ComboBoxModel) {
            ComboBoxModel var8 = (ComboBoxModel)var4;
            Option var9 = (Option)var8.getSelectedItem();
            if (var9 != null) {
               this.appendBuffer(var2, var3, var9.getValue());
            }
         }

      }
   }

   private void appendBuffer(StringBuilder var1, String var2, String var3) {
      if (var1.length() > 0) {
         var1.append('&');
      }

      String var4 = URLEncoder.encode(var2);
      var1.append(var4);
      var1.append('=');
      String var5 = URLEncoder.encode(var3);
      var1.append(var5);
   }

   private boolean isControl(Element var1) {
      return var1.isLeaf();
   }

   boolean isLastTextOrPasswordField() {
      Element var1 = this.getFormElement();
      Element var2 = this.getElement();
      if (var1 != null) {
         ElementIterator var3 = new ElementIterator(var1);
         boolean var5 = false;

         Element var4;
         while((var4 = var3.next()) != null) {
            if (var4 == var2) {
               var5 = true;
            } else if (var5 && this.isControl(var4)) {
               AttributeSet var6 = var4.getAttributes();
               if (HTMLDocument.matchNameAttribute(var6, HTML.Tag.INPUT)) {
                  String var7 = (String)var6.getAttribute(HTML.Attribute.TYPE);
                  if ("text".equals(var7) || "password".equals(var7)) {
                     return false;
                  }
               }
            }
         }
      }

      return true;
   }

   void resetForm() {
      Element var1 = this.getFormElement();
      if (var1 != null) {
         ElementIterator var2 = new ElementIterator(var1);

         while(true) {
            while(true) {
               Element var3;
               do {
                  if ((var3 = var2.next()) == null) {
                     return;
                  }
               } while(!this.isControl(var3));

               AttributeSet var4 = var3.getAttributes();
               Object var5 = var4.getAttribute(StyleConstants.ModelAttribute);
               if (var5 instanceof TextAreaDocument) {
                  TextAreaDocument var14 = (TextAreaDocument)var5;
                  var14.reset();
               } else if (var5 instanceof PlainDocument) {
                  try {
                     PlainDocument var13 = (PlainDocument)var5;
                     var13.remove(0, var13.getLength());
                     if (HTMLDocument.matchNameAttribute(var4, HTML.Tag.INPUT)) {
                        String var17 = (String)var4.getAttribute(HTML.Attribute.VALUE);
                        if (var17 != null) {
                           var13.insertString(0, var17, (AttributeSet)null);
                        }
                     }
                  } catch (BadLocationException var10) {
                  }
               } else if (!(var5 instanceof OptionListModel)) {
                  if (var5 instanceof OptionComboBoxModel) {
                     OptionComboBoxModel var11 = (OptionComboBoxModel)var5;
                     Option var15 = var11.getInitialSelection();
                     if (var15 != null) {
                        var11.setSelectedItem(var15);
                     }
                  } else if (var5 instanceof JToggleButton.ToggleButtonModel) {
                     boolean var12 = (String)var4.getAttribute(HTML.Attribute.CHECKED) != null;
                     JToggleButton.ToggleButtonModel var16 = (JToggleButton.ToggleButtonModel)var5;
                     var16.setSelected(var12);
                  }
               } else {
                  OptionListModel var6 = (OptionListModel)var5;
                  int var7 = var6.getSize();

                  for(int var8 = 0; var8 < var7; ++var8) {
                     var6.removeIndexInterval(var8, var8);
                  }

                  BitSet var18 = var6.getInitialSelection();

                  for(int var9 = 0; var9 < var18.size(); ++var9) {
                     if (var18.get(var9)) {
                        var6.addSelectionInterval(var9, var9);
                     }
                  }
               }
            }
         }
      }
   }

   private class BrowseFileAction implements ActionListener {
      private AttributeSet attrs;
      private Document model;

      BrowseFileAction(AttributeSet var2, Document var3) {
         this.attrs = var2;
         this.model = var3;
      }

      public void actionPerformed(ActionEvent var1) {
         JFileChooser var2 = new JFileChooser();
         var2.setMultiSelectionEnabled(false);
         if (var2.showOpenDialog(FormView.this.getContainer()) == 0) {
            File var3 = var2.getSelectedFile();
            if (var3 != null) {
               try {
                  if (this.model.getLength() > 0) {
                     this.model.remove(0, this.model.getLength());
                  }

                  this.model.insertString(0, var3.getPath(), (AttributeSet)null);
               } catch (BadLocationException var5) {
               }
            }
         }

      }
   }

   protected class MouseEventListener extends MouseAdapter {
      public void mouseReleased(MouseEvent var1) {
         String var2 = FormView.this.getImageData(var1.getPoint());
         FormView.this.imageSubmit(var2);
      }
   }
}
