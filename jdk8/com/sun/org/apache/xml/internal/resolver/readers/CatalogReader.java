package com.sun.org.apache.xml.internal.resolver.readers;

import com.sun.org.apache.xml.internal.resolver.Catalog;
import com.sun.org.apache.xml.internal.resolver.CatalogException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;

public interface CatalogReader {
   void readCatalog(Catalog var1, String var2) throws MalformedURLException, IOException, CatalogException;

   void readCatalog(Catalog var1, InputStream var2) throws IOException, CatalogException;
}
