package com.sun.org.glassfish.external.probe.provider;

public enum PluginPoint {
   SERVER("server", "server"),
   APPLICATIONS("applications", "server/applications");

   String name;
   String path;

   private PluginPoint(String lname, String lpath) {
      this.name = lname;
      this.path = lpath;
   }

   public String getName() {
      return this.name;
   }

   public String getPath() {
      return this.path;
   }
}
