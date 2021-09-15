package com.sun.xml.internal.ws.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class Version {
   public final String BUILD_ID;
   public final String BUILD_VERSION;
   public final String MAJOR_VERSION;
   public final String SVN_REVISION;
   public static final Version RUNTIME_VERSION = create(Version.class.getResourceAsStream("version.properties"));

   private Version(String buildId, String buildVersion, String majorVersion, String svnRev) {
      this.BUILD_ID = this.fixNull(buildId);
      this.BUILD_VERSION = this.fixNull(buildVersion);
      this.MAJOR_VERSION = this.fixNull(majorVersion);
      this.SVN_REVISION = this.fixNull(svnRev);
   }

   public static Version create(InputStream is) {
      Properties props = new Properties();

      try {
         props.load(is);
      } catch (IOException var3) {
      } catch (Exception var4) {
      }

      return new Version(props.getProperty("build-id"), props.getProperty("build-version"), props.getProperty("major-version"), props.getProperty("svn-revision"));
   }

   private String fixNull(String v) {
      return v == null ? "unknown" : v;
   }

   public String toString() {
      return this.BUILD_VERSION + " svn-revision#" + this.SVN_REVISION;
   }
}
