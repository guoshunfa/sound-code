package com.sun.xml.internal.ws.assembler;

public class MetroConfigNameImpl implements MetroConfigName {
   private final String defaultFileName;
   private final String appFileName;

   public MetroConfigNameImpl(String defaultFileName, String appFileName) {
      this.defaultFileName = defaultFileName;
      this.appFileName = appFileName;
   }

   public String getDefaultFileName() {
      return this.defaultFileName;
   }

   public String getAppFileName() {
      return this.appFileName;
   }
}
