package javax.management.loading;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.Set;
import javax.management.ServiceNotFoundException;

public interface MLetMBean {
   Set<Object> getMBeansFromURL(String var1) throws ServiceNotFoundException;

   Set<Object> getMBeansFromURL(URL var1) throws ServiceNotFoundException;

   void addURL(URL var1);

   void addURL(String var1) throws ServiceNotFoundException;

   URL[] getURLs();

   URL getResource(String var1);

   InputStream getResourceAsStream(String var1);

   Enumeration<URL> getResources(String var1) throws IOException;

   String getLibraryDirectory();

   void setLibraryDirectory(String var1);
}
