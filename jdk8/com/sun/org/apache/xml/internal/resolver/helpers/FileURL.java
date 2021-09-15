package com.sun.org.apache.xml.internal.resolver.helpers;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

public abstract class FileURL {
   protected FileURL() {
   }

   public static URL makeURL(String pathname) throws MalformedURLException {
      File file = new File(pathname);
      return file.toURI().toURL();
   }
}
