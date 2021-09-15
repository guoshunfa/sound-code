package javax.swing.text.html;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.text.AttributeSet;
import javax.swing.text.ComponentView;
import javax.swing.text.Element;

class IsindexView extends ComponentView implements ActionListener {
   JTextField textField;

   public IsindexView(Element var1) {
      super(var1);
   }

   public Component createComponent() {
      AttributeSet var1 = this.getElement().getAttributes();
      JPanel var2 = new JPanel(new BorderLayout());
      var2.setBackground((Color)null);
      String var3 = (String)var1.getAttribute(HTML.Attribute.PROMPT);
      if (var3 == null) {
         var3 = UIManager.getString("IsindexView.prompt");
      }

      JLabel var4 = new JLabel(var3);
      this.textField = new JTextField();
      this.textField.addActionListener(this);
      var2.add(var4, "West");
      var2.add(this.textField, "Center");
      var2.setAlignmentY(1.0F);
      var2.setOpaque(false);
      return var2;
   }

   public void actionPerformed(ActionEvent var1) {
      String var2 = this.textField.getText();
      if (var2 != null) {
         var2 = URLEncoder.encode(var2);
      }

      AttributeSet var3 = this.getElement().getAttributes();
      HTMLDocument var4 = (HTMLDocument)this.getElement().getDocument();
      String var5 = (String)var3.getAttribute(HTML.Attribute.ACTION);
      if (var5 == null) {
         var5 = var4.getBase().toString();
      }

      try {
         URL var6 = new URL(var5 + "?" + var2);
         JEditorPane var7 = (JEditorPane)this.getContainer();
         var7.setPage(var6);
      } catch (MalformedURLException var8) {
      } catch (IOException var9) {
      }

   }
}
