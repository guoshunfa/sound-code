package sun.applet;

import java.awt.Button;
import java.awt.Frame;
import java.awt.Panel;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

final class TextFrame extends Frame {
   private static AppletMessageHandler amh = new AppletMessageHandler("textframe");

   TextFrame(int var1, int var2, String var3, String var4) {
      this.setTitle(var3);
      TextArea var5 = new TextArea(20, 60);
      var5.setText(var4);
      var5.setEditable(false);
      this.add("Center", var5);
      Panel var6 = new Panel();
      this.add("South", var6);
      Button var7 = new Button(amh.getMessage("button.dismiss", (Object)"Dismiss"));
      var6.add(var7);

      class ActionEventListener implements ActionListener {
         public void actionPerformed(ActionEvent var1) {
            TextFrame.this.dispose();
         }
      }

      var7.addActionListener(new ActionEventListener());
      this.pack();
      this.move(var1, var2);
      this.setVisible(true);
      WindowAdapter var8 = new WindowAdapter() {
         public void windowClosing(WindowEvent var1) {
            TextFrame.this.dispose();
         }
      };
      this.addWindowListener(var8);
   }
}
