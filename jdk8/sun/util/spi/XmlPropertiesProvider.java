package sun.util.spi;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;

public abstract class XmlPropertiesProvider {
   protected XmlPropertiesProvider() {
   }

   public abstract void load(Properties var1, InputStream var2) throws IOException, InvalidPropertiesFormatException;

   public abstract void store(Properties var1, OutputStream var2, String var3, String var4) throws IOException;
}
