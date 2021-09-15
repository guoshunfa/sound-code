package sun.applet;

import java.awt.Button;
import java.awt.Choice;
import java.awt.Event;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextField;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Properties;
import sun.security.action.GetBooleanAction;
import sun.security.action.GetPropertyAction;

class AppletProps extends Frame {
   TextField proxyHost;
   TextField proxyPort;
   Choice accessMode;
   private static AppletMessageHandler amh = new AppletMessageHandler("appletprops");

   AppletProps() {
      this.setTitle(amh.getMessage("title"));
      Panel var1 = new Panel();
      var1.setLayout(new GridLayout(0, 2));
      var1.add(new Label(amh.getMessage("label.http.server", (Object)"Http proxy server:")));
      var1.add(this.proxyHost = new TextField());
      var1.add(new Label(amh.getMessage("label.http.proxy")));
      var1.add(this.proxyPort = new TextField());
      var1.add(new Label(amh.getMessage("label.class")));
      var1.add(this.accessMode = new Choice());
      this.accessMode.addItem(amh.getMessage("choice.class.item.restricted"));
      this.accessMode.addItem(amh.getMessage("choice.class.item.unrestricted"));
      this.add("Center", var1);
      var1 = new Panel();
      var1.add(new Button(amh.getMessage("button.apply")));
      var1.add(new Button(amh.getMessage("button.reset")));
      var1.add(new Button(amh.getMessage("button.cancel")));
      this.add("South", var1);
      this.move(200, 150);
      this.pack();
      this.reset();
   }

   void reset() {
      AppletSecurity var1 = (AppletSecurity)System.getSecurityManager();
      if (var1 != null) {
         var1.reset();
      }

      String var2 = (String)AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction("http.proxyHost")));
      String var3 = (String)AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction("http.proxyPort")));
      Boolean var4 = (Boolean)AccessController.doPrivileged((PrivilegedAction)(new GetBooleanAction("package.restrict.access.sun")));
      boolean var5 = var4;
      if (var5) {
         this.accessMode.select(amh.getMessage("choice.class.item.restricted"));
      } else {
         this.accessMode.select(amh.getMessage("choice.class.item.unrestricted"));
      }

      if (var2 != null) {
         this.proxyHost.setText(var2);
         this.proxyPort.setText(var3);
      } else {
         this.proxyHost.setText("");
         this.proxyPort.setText("");
      }

   }

   void apply() {
      String var1 = this.proxyHost.getText().trim();
      String var2 = this.proxyPort.getText().trim();
      final Properties var3 = (Properties)AccessController.doPrivileged(new PrivilegedAction() {
         public Object run() {
            return System.getProperties();
         }
      });
      if (var1.length() != 0) {
         int var4 = 0;

         try {
            var4 = Integer.parseInt(var2);
         } catch (NumberFormatException var7) {
         }

         if (var4 <= 0) {
            this.proxyPort.selectAll();
            this.proxyPort.requestFocus();
            (new AppletPropsErrorDialog(this, amh.getMessage("title.invalidproxy"), amh.getMessage("label.invalidproxy"), amh.getMessage("button.ok"))).show();
            return;
         }

         var3.put("http.proxyHost", var1);
         var3.put("http.proxyPort", var2);
      } else {
         var3.put("http.proxyHost", "");
      }

      if (amh.getMessage("choice.class.item.restricted").equals(this.accessMode.getSelectedItem())) {
         var3.put("package.restrict.access.sun", "true");
      } else {
         var3.put("package.restrict.access.sun", "false");
      }

      try {
         this.reset();
         AccessController.doPrivileged(new PrivilegedExceptionAction() {
            public Object run() throws IOException {
               File var1 = Main.theUserPropertiesFile;
               FileOutputStream var2 = new FileOutputStream(var1);
               Properties var3x = new Properties();

               for(int var4 = 0; var4 < Main.avDefaultUserProps.length; ++var4) {
                  String var5 = Main.avDefaultUserProps[var4][0];
                  var3x.setProperty(var5, var3.getProperty(var5));
               }

               var3x.store((OutputStream)var2, AppletProps.amh.getMessage("prop.store"));
               var2.close();
               return null;
            }
         });
         this.hide();
      } catch (PrivilegedActionException var6) {
         System.out.println(amh.getMessage("apply.exception", (Object)var6.getException()));
         var6.printStackTrace();
         this.reset();
      }

   }

   public boolean action(Event var1, Object var2) {
      if (amh.getMessage("button.apply").equals(var2)) {
         this.apply();
         return true;
      } else if (amh.getMessage("button.reset").equals(var2)) {
         this.reset();
         return true;
      } else if (amh.getMessage("button.cancel").equals(var2)) {
         this.reset();
         this.hide();
         return true;
      } else {
         return false;
      }
   }
}
