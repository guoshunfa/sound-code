package com.sun.xml.internal.ws.transport.http;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Set;

public interface ResourceLoader {
   URL getResource(String var1) throws MalformedURLException;

   URL getCatalogFile() throws MalformedURLException;

   Set<String> getResourcePaths(String var1);
}
