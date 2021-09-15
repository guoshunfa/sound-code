package jdk.internal.util.xml;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;
import sun.util.spi.XmlPropertiesProvider;

public class BasicXmlPropertiesProvider extends XmlPropertiesProvider {
   public void load(Properties var1, InputStream var2) throws IOException, InvalidPropertiesFormatException {
      PropertiesDefaultHandler var3 = new PropertiesDefaultHandler();
      var3.load(var1, var2);
   }

   public void store(Properties var1, OutputStream var2, String var3, String var4) throws IOException {
      PropertiesDefaultHandler var5 = new PropertiesDefaultHandler();
      var5.store(var1, var2, var3, var4);
   }
}
