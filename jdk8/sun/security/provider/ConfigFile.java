package sun.security.provider;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StreamTokenizer;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.security.Security;
import java.security.URIParameter;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.security.auth.AuthPermission;
import javax.security.auth.login.AppConfigurationEntry;
import javax.security.auth.login.Configuration;
import javax.security.auth.login.ConfigurationSpi;
import sun.security.util.Debug;
import sun.security.util.PropertyExpander;
import sun.security.util.ResourcesMgr;

public final class ConfigFile extends Configuration {
   private final ConfigFile.Spi spi = new ConfigFile.Spi();

   public AppConfigurationEntry[] getAppConfigurationEntry(String var1) {
      return this.spi.engineGetAppConfigurationEntry(var1);
   }

   public synchronized void refresh() {
      this.spi.engineRefresh();
   }

   public static final class Spi extends ConfigurationSpi {
      private URL url;
      private boolean expandProp = true;
      private Map<String, List<AppConfigurationEntry>> configuration;
      private int linenum;
      private StreamTokenizer st;
      private int lookahead;
      private static Debug debugConfig = Debug.getInstance("configfile");
      private static Debug debugParser = Debug.getInstance("configparser");

      public Spi() {
         try {
            this.init();
         } catch (IOException var2) {
            throw new SecurityException(var2);
         }
      }

      public Spi(URI var1) {
         try {
            this.url = var1.toURL();
            this.init();
         } catch (IOException var3) {
            throw new SecurityException(var3);
         }
      }

      public Spi(final Configuration.Parameters var1) throws IOException {
         try {
            AccessController.doPrivileged(new PrivilegedExceptionAction<Void>() {
               public Void run() throws IOException {
                  if (var1 == null) {
                     Spi.this.init();
                  } else {
                     if (!(var1 instanceof URIParameter)) {
                        throw new IllegalArgumentException("Unrecognized parameter: " + var1);
                     }

                     URIParameter var1x = (URIParameter)var1;
                     Spi.this.url = var1x.getURI().toURL();
                     Spi.this.init();
                  }

                  return null;
               }
            });
         } catch (PrivilegedActionException var3) {
            throw (IOException)var3.getException();
         }
      }

      private void init() throws IOException {
         boolean var1 = false;
         String var2 = Security.getProperty("policy.expandProperties");
         if (var2 == null) {
            var2 = System.getProperty("policy.expandProperties");
         }

         if ("false".equals(var2)) {
            this.expandProp = false;
         }

         HashMap var3 = new HashMap();
         if (this.url != null) {
            if (debugConfig != null) {
               debugConfig.println("reading " + this.url);
            }

            this.init(this.url, var3);
            this.configuration = var3;
         } else {
            String var4 = Security.getProperty("policy.allowSystemProperty");
            if ("true".equalsIgnoreCase(var4)) {
               String var5 = System.getProperty("java.security.auth.login.config");
               if (var5 != null) {
                  boolean var6 = false;
                  if (var5.startsWith("=")) {
                     var6 = true;
                     var5 = var5.substring(1);
                  }

                  try {
                     var5 = PropertyExpander.expand(var5);
                  } catch (PropertyExpander.ExpandException var10) {
                     throw this.ioException("Unable.to.properly.expand.config", var5);
                  }

                  URL var7 = null;

                  try {
                     var7 = new URL(var5);
                  } catch (MalformedURLException var12) {
                     File var9 = new File(var5);
                     if (!var9.exists()) {
                        throw this.ioException("extra.config.No.such.file.or.directory.", var5);
                     }

                     var7 = var9.toURI().toURL();
                  }

                  if (debugConfig != null) {
                     debugConfig.println("reading " + var7);
                  }

                  this.init(var7, var3);
                  var1 = true;
                  if (var6) {
                     if (debugConfig != null) {
                        debugConfig.println("overriding other policies!");
                     }

                     this.configuration = var3;
                     return;
                  }
               }
            }

            int var13;
            String var14;
            for(var13 = 1; (var14 = Security.getProperty("login.config.url." + var13)) != null; ++var13) {
               try {
                  var14 = PropertyExpander.expand(var14).replace(File.separatorChar, '/');
                  if (debugConfig != null) {
                     debugConfig.println("\tReading config: " + var14);
                  }

                  this.init(new URL(var14), var3);
                  var1 = true;
               } catch (PropertyExpander.ExpandException var11) {
                  throw this.ioException("Unable.to.properly.expand.config", var14);
               }
            }

            if (!var1 && var13 == 1 && var14 == null) {
               if (debugConfig != null) {
                  debugConfig.println("\tReading Policy from ~/.java.login.config");
               }

               var14 = System.getProperty("user.home");
               String var15 = var14 + File.separatorChar + ".java.login.config";
               if ((new File(var15)).exists()) {
                  this.init((new File(var15)).toURI().toURL(), var3);
               }
            }

            this.configuration = var3;
         }
      }

      private void init(URL var1, Map<String, List<AppConfigurationEntry>> var2) throws IOException {
         try {
            InputStreamReader var3 = new InputStreamReader(this.getInputStream(var1), "UTF-8");
            Throwable var4 = null;

            try {
               this.readConfig(var3, var2);
            } catch (Throwable var14) {
               var4 = var14;
               throw var14;
            } finally {
               if (var3 != null) {
                  if (var4 != null) {
                     try {
                        var3.close();
                     } catch (Throwable var13) {
                        var4.addSuppressed(var13);
                     }
                  } else {
                     var3.close();
                  }
               }

            }

         } catch (FileNotFoundException var16) {
            if (debugConfig != null) {
               debugConfig.println(var16.toString());
            }

            throw new IOException(ResourcesMgr.getString("Configuration.Error.No.such.file.or.directory", "sun.security.util.AuthResources"));
         }
      }

      public AppConfigurationEntry[] engineGetAppConfigurationEntry(String var1) {
         List var2 = null;
         synchronized(this.configuration) {
            var2 = (List)this.configuration.get(var1);
         }

         if (var2 != null && var2.size() != 0) {
            AppConfigurationEntry[] var3 = new AppConfigurationEntry[var2.size()];
            Iterator var4 = var2.iterator();

            for(int var5 = 0; var4.hasNext(); ++var5) {
               AppConfigurationEntry var6 = (AppConfigurationEntry)var4.next();
               var3[var5] = new AppConfigurationEntry(var6.getLoginModuleName(), var6.getControlFlag(), var6.getOptions());
            }

            return var3;
         } else {
            return null;
         }
      }

      public synchronized void engineRefresh() {
         SecurityManager var1 = System.getSecurityManager();
         if (var1 != null) {
            var1.checkPermission(new AuthPermission("refreshLoginConfiguration"));
         }

         AccessController.doPrivileged(new PrivilegedAction<Void>() {
            public Void run() {
               try {
                  Spi.this.init();
                  return null;
               } catch (IOException var2) {
                  throw new SecurityException(var2.getLocalizedMessage(), var2);
               }
            }
         });
      }

      private void readConfig(Reader var1, Map<String, List<AppConfigurationEntry>> var2) throws IOException {
         this.linenum = 1;
         if (!(var1 instanceof BufferedReader)) {
            var1 = new BufferedReader((Reader)var1);
         }

         this.st = new StreamTokenizer((Reader)var1);
         this.st.quoteChar(34);
         this.st.wordChars(36, 36);
         this.st.wordChars(95, 95);
         this.st.wordChars(45, 45);
         this.st.wordChars(42, 42);
         this.st.lowerCaseMode(false);
         this.st.slashSlashComments(true);
         this.st.slashStarComments(true);
         this.st.eolIsSignificant(true);
         this.lookahead = this.nextToken();

         while(this.lookahead != -1) {
            this.parseLoginEntry(var2);
         }

      }

      private void parseLoginEntry(Map<String, List<AppConfigurationEntry>> var1) throws IOException {
         LinkedList var2 = new LinkedList();
         String var3 = this.st.sval;
         this.lookahead = this.nextToken();
         if (debugParser != null) {
            debugParser.println("\tReading next config entry: " + var3);
         }

         this.match("{");

         String var4;
         AppConfigurationEntry.LoginModuleControlFlag var5;
         HashMap var7;
         for(; !this.peek("}"); var2.add(new AppConfigurationEntry(var4, var5, var7))) {
            var4 = this.match("module class name");
            String var6 = this.match("controlFlag").toUpperCase(Locale.ENGLISH);
            byte var8 = -1;
            switch(var6.hashCode()) {
            case -848090850:
               if (var6.equals("SUFFICIENT")) {
                  var8 = 2;
               }
               break;
            case -810754599:
               if (var6.equals("REQUISITE")) {
                  var8 = 1;
               }
               break;
            case 389487519:
               if (var6.equals("REQUIRED")) {
                  var8 = 0;
               }
               break;
            case 703609696:
               if (var6.equals("OPTIONAL")) {
                  var8 = 3;
               }
            }

            switch(var8) {
            case 0:
               var5 = AppConfigurationEntry.LoginModuleControlFlag.REQUIRED;
               break;
            case 1:
               var5 = AppConfigurationEntry.LoginModuleControlFlag.REQUISITE;
               break;
            case 2:
               var5 = AppConfigurationEntry.LoginModuleControlFlag.SUFFICIENT;
               break;
            case 3:
               var5 = AppConfigurationEntry.LoginModuleControlFlag.OPTIONAL;
               break;
            default:
               throw this.ioException("Configuration.Error.Invalid.control.flag.flag", var6);
            }

            var7 = new HashMap();

            while(!this.peek(";")) {
               String var11 = this.match("option key");
               this.match("=");

               try {
                  var7.put(var11, this.expand(this.match("option value")));
               } catch (PropertyExpander.ExpandException var10) {
                  throw new IOException(var10.getLocalizedMessage());
               }
            }

            this.lookahead = this.nextToken();
            if (debugParser != null) {
               debugParser.println("\t\t" + var4 + ", " + var6);
               Iterator var12 = var7.keySet().iterator();

               while(var12.hasNext()) {
                  String var9 = (String)var12.next();
                  debugParser.println("\t\t\t" + var9 + "=" + (String)var7.get(var9));
               }
            }
         }

         this.match("}");
         this.match(";");
         if (var1.containsKey(var3)) {
            throw this.ioException("Configuration.Error.Can.not.specify.multiple.entries.for.appName", var3);
         } else {
            var1.put(var3, var2);
         }
      }

      private String match(String var1) throws IOException {
         String var2 = null;
         switch(this.lookahead) {
         case -3:
         case 34:
            if (!var1.equalsIgnoreCase("module class name") && !var1.equalsIgnoreCase("controlFlag") && !var1.equalsIgnoreCase("option key") && !var1.equalsIgnoreCase("option value")) {
               throw this.ioException("Configuration.Error.Line.line.expected.expect.found.value.", new Integer(this.linenum), var1, this.st.sval);
            }

            var2 = this.st.sval;
            this.lookahead = this.nextToken();
            break;
         case -1:
            throw this.ioException("Configuration.Error.expected.expect.read.end.of.file.", var1);
         case 59:
            if (!var1.equalsIgnoreCase(";")) {
               throw this.ioException("Configuration.Error.Line.line.expected.expect.", new Integer(this.linenum), var1, this.st.sval);
            }

            this.lookahead = this.nextToken();
            break;
         case 61:
            if (!var1.equalsIgnoreCase("=")) {
               throw this.ioException("Configuration.Error.Line.line.expected.expect.", new Integer(this.linenum), var1, this.st.sval);
            }

            this.lookahead = this.nextToken();
            break;
         case 123:
            if (!var1.equalsIgnoreCase("{")) {
               throw this.ioException("Configuration.Error.Line.line.expected.expect.", new Integer(this.linenum), var1, this.st.sval);
            }

            this.lookahead = this.nextToken();
            break;
         case 125:
            if (!var1.equalsIgnoreCase("}")) {
               throw this.ioException("Configuration.Error.Line.line.expected.expect.", new Integer(this.linenum), var1, this.st.sval);
            }

            this.lookahead = this.nextToken();
            break;
         default:
            throw this.ioException("Configuration.Error.Line.line.expected.expect.found.value.", new Integer(this.linenum), var1, this.st.sval);
         }

         return var2;
      }

      private boolean peek(String var1) {
         switch(this.lookahead) {
         case 44:
            return var1.equalsIgnoreCase(",");
         case 59:
            return var1.equalsIgnoreCase(";");
         case 123:
            return var1.equalsIgnoreCase("{");
         case 125:
            return var1.equalsIgnoreCase("}");
         default:
            return false;
         }
      }

      private int nextToken() throws IOException {
         int var1;
         while((var1 = this.st.nextToken()) == 10) {
            ++this.linenum;
         }

         return var1;
      }

      private InputStream getInputStream(URL var1) throws IOException {
         if ("file".equalsIgnoreCase(var1.getProtocol())) {
            try {
               return var1.openStream();
            } catch (Exception var4) {
               String var3 = var1.getPath();
               if (var1.getHost().length() > 0) {
                  var3 = "//" + var1.getHost() + var3;
               }

               if (debugConfig != null) {
                  debugConfig.println("cannot read " + var1 + ", try " + var3);
               }

               return new FileInputStream(var3);
            }
         } else {
            return var1.openStream();
         }
      }

      private String expand(String var1) throws PropertyExpander.ExpandException, IOException {
         if (var1.isEmpty()) {
            return var1;
         } else if (!this.expandProp) {
            return var1;
         } else {
            String var2 = PropertyExpander.expand(var1);
            if (var2 != null && var2.length() != 0) {
               return var2;
            } else {
               throw this.ioException("Configuration.Error.Line.line.system.property.value.expanded.to.empty.value", new Integer(this.linenum), var1);
            }
         }
      }

      private IOException ioException(String var1, Object... var2) {
         MessageFormat var3 = new MessageFormat(ResourcesMgr.getString(var1, "sun.security.util.AuthResources"));
         return new IOException(var3.format(var2));
      }
   }
}
