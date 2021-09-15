package javax.swing.text.html.parser;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.Reader;
import java.io.Serializable;
import java.security.AccessController;
import java.security.PrivilegedAction;
import javax.swing.text.html.HTMLEditorKit;
import sun.awt.AppContext;

public class ParserDelegator extends HTMLEditorKit.Parser implements Serializable {
   private static final Object DTD_KEY = new Object();

   protected static void setDefaultDTD() {
      getDefaultDTD();
   }

   private static synchronized DTD getDefaultDTD() {
      AppContext var0 = AppContext.getAppContext();
      DTD var1 = (DTD)var0.get(DTD_KEY);
      if (var1 == null) {
         DTD var2 = null;
         String var3 = "html32";

         try {
            var2 = DTD.getDTD(var3);
         } catch (IOException var5) {
            System.out.println("Throw an exception: could not get default dtd: " + var3);
         }

         var1 = createDTD(var2, var3);
         var0.put(DTD_KEY, var1);
      }

      return var1;
   }

   protected static DTD createDTD(DTD var0, String var1) {
      InputStream var2 = null;
      boolean var3 = true;

      try {
         String var4 = var1 + ".bdtd";
         var2 = getResourceAsStream(var4);
         if (var2 != null) {
            var0.read(new DataInputStream(new BufferedInputStream(var2)));
            DTD.putDTDHash(var1, var0);
         }
      } catch (Exception var5) {
         System.out.println((Object)var5);
      }

      return var0;
   }

   public ParserDelegator() {
      setDefaultDTD();
   }

   public void parse(Reader var1, HTMLEditorKit.ParserCallback var2, boolean var3) throws IOException {
      (new DocumentParser(getDefaultDTD())).parse(var1, var2, var3);
   }

   static InputStream getResourceAsStream(final String var0) {
      return (InputStream)AccessController.doPrivileged(new PrivilegedAction<InputStream>() {
         public InputStream run() {
            return ParserDelegator.class.getResourceAsStream(var0);
         }
      });
   }

   private void readObject(ObjectInputStream var1) throws ClassNotFoundException, IOException {
      var1.defaultReadObject();
      setDefaultDTD();
   }
}
