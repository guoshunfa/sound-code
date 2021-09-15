package sun.security.provider;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StreamTokenizer;
import java.io.Writer;
import java.security.GeneralSecurityException;
import java.security.Principal;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.Vector;
import javax.security.auth.x500.X500Principal;
import sun.net.www.ParseUtil;
import sun.security.util.Debug;
import sun.security.util.PropertyExpander;
import sun.security.util.ResourcesMgr;

public class PolicyParser {
   private static final String EXTDIRS_PROPERTY = "java.ext.dirs";
   private static final String OLD_EXTDIRS_EXPANSION = "${java.ext.dirs}";
   static final String EXTDIRS_EXPANSION = "${{java.ext.dirs}}";
   private Vector<PolicyParser.GrantEntry> grantEntries;
   private Map<String, PolicyParser.DomainEntry> domainEntries;
   private static final Debug debug = Debug.getInstance("parser", "\t[Policy Parser]");
   private StreamTokenizer st;
   private int lookahead;
   private boolean expandProp;
   private String keyStoreUrlString;
   private String keyStoreType;
   private String keyStoreProvider;
   private String storePassURL;

   private String expand(String var1) throws PropertyExpander.ExpandException {
      return this.expand(var1, false);
   }

   private String expand(String var1, boolean var2) throws PropertyExpander.ExpandException {
      return !this.expandProp ? var1 : PropertyExpander.expand(var1, var2);
   }

   public PolicyParser() {
      this.expandProp = false;
      this.keyStoreUrlString = null;
      this.keyStoreType = null;
      this.keyStoreProvider = null;
      this.storePassURL = null;
      this.grantEntries = new Vector();
   }

   public PolicyParser(boolean var1) {
      this();
      this.expandProp = var1;
   }

   public void read(Reader var1) throws PolicyParser.ParsingException, IOException {
      if (!(var1 instanceof BufferedReader)) {
         var1 = new BufferedReader((Reader)var1);
      }

      this.st = new StreamTokenizer((Reader)var1);
      this.st.resetSyntax();
      this.st.wordChars(97, 122);
      this.st.wordChars(65, 90);
      this.st.wordChars(46, 46);
      this.st.wordChars(48, 57);
      this.st.wordChars(95, 95);
      this.st.wordChars(36, 36);
      this.st.wordChars(160, 255);
      this.st.whitespaceChars(0, 32);
      this.st.commentChar(47);
      this.st.quoteChar(39);
      this.st.quoteChar(34);
      this.st.lowerCaseMode(false);
      this.st.ordinaryChar(47);
      this.st.slashSlashComments(true);
      this.st.slashStarComments(true);
      this.lookahead = this.st.nextToken();

      for(PolicyParser.GrantEntry var2 = null; this.lookahead != -1; this.match(";")) {
         if (this.peek("grant")) {
            var2 = this.parseGrantEntry();
            if (var2 != null) {
               this.add(var2);
            }
         } else if (this.peek("keystore") && this.keyStoreUrlString == null) {
            this.parseKeyStoreEntry();
         } else if (this.peek("keystorePasswordURL") && this.storePassURL == null) {
            this.parseStorePassURL();
         } else if (var2 == null && this.keyStoreUrlString == null && this.storePassURL == null && this.peek("domain")) {
            if (this.domainEntries == null) {
               this.domainEntries = new TreeMap();
            }

            PolicyParser.DomainEntry var3 = this.parseDomainEntry();
            if (var3 != null) {
               String var4 = var3.getName();
               if (this.domainEntries.containsKey(var4)) {
                  MessageFormat var5 = new MessageFormat(ResourcesMgr.getString("duplicate.keystore.domain.name"));
                  Object[] var6 = new Object[]{var4};
                  throw new PolicyParser.ParsingException(var5.format(var6));
               }

               this.domainEntries.put(var4, var3);
            }
         }
      }

      if (this.keyStoreUrlString == null && this.storePassURL != null) {
         throw new PolicyParser.ParsingException(ResourcesMgr.getString("keystorePasswordURL.can.not.be.specified.without.also.specifying.keystore"));
      }
   }

   public void add(PolicyParser.GrantEntry var1) {
      this.grantEntries.addElement(var1);
   }

   public void replace(PolicyParser.GrantEntry var1, PolicyParser.GrantEntry var2) {
      this.grantEntries.setElementAt(var2, this.grantEntries.indexOf(var1));
   }

   public boolean remove(PolicyParser.GrantEntry var1) {
      return this.grantEntries.removeElement(var1);
   }

   public String getKeyStoreUrl() {
      try {
         return this.keyStoreUrlString != null && this.keyStoreUrlString.length() != 0 ? this.expand(this.keyStoreUrlString, true).replace(File.separatorChar, '/') : null;
      } catch (PropertyExpander.ExpandException var2) {
         if (debug != null) {
            debug.println(var2.toString());
         }

         return null;
      }
   }

   public void setKeyStoreUrl(String var1) {
      this.keyStoreUrlString = var1;
   }

   public String getKeyStoreType() {
      return this.keyStoreType;
   }

   public void setKeyStoreType(String var1) {
      this.keyStoreType = var1;
   }

   public String getKeyStoreProvider() {
      return this.keyStoreProvider;
   }

   public void setKeyStoreProvider(String var1) {
      this.keyStoreProvider = var1;
   }

   public String getStorePassURL() {
      try {
         return this.storePassURL != null && this.storePassURL.length() != 0 ? this.expand(this.storePassURL, true).replace(File.separatorChar, '/') : null;
      } catch (PropertyExpander.ExpandException var2) {
         if (debug != null) {
            debug.println(var2.toString());
         }

         return null;
      }
   }

   public void setStorePassURL(String var1) {
      this.storePassURL = var1;
   }

   public Enumeration<PolicyParser.GrantEntry> grantElements() {
      return this.grantEntries.elements();
   }

   public Collection<PolicyParser.DomainEntry> getDomainEntries() {
      return this.domainEntries.values();
   }

   public void write(Writer var1) {
      PrintWriter var2 = new PrintWriter(new BufferedWriter(var1));
      Enumeration var3 = this.grantElements();
      var2.println("/* AUTOMATICALLY GENERATED ON " + new Date() + "*/");
      var2.println("/* DO NOT EDIT */");
      var2.println();
      if (this.keyStoreUrlString != null) {
         this.writeKeyStoreEntry(var2);
      }

      if (this.storePassURL != null) {
         this.writeStorePassURL(var2);
      }

      while(var3.hasMoreElements()) {
         PolicyParser.GrantEntry var4 = (PolicyParser.GrantEntry)var3.nextElement();
         var4.write(var2);
         var2.println();
      }

      var2.flush();
   }

   private void parseKeyStoreEntry() throws PolicyParser.ParsingException, IOException {
      this.match("keystore");
      this.keyStoreUrlString = this.match("quoted string");
      if (this.peek(",")) {
         this.match(",");
         if (this.peek("\"")) {
            this.keyStoreType = this.match("quoted string");
            if (this.peek(",")) {
               this.match(",");
               if (this.peek("\"")) {
                  this.keyStoreProvider = this.match("quoted string");
               } else {
                  throw new PolicyParser.ParsingException(this.st.lineno(), ResourcesMgr.getString("expected.keystore.provider"));
               }
            }
         } else {
            throw new PolicyParser.ParsingException(this.st.lineno(), ResourcesMgr.getString("expected.keystore.type"));
         }
      }
   }

   private void parseStorePassURL() throws PolicyParser.ParsingException, IOException {
      this.match("keyStorePasswordURL");
      this.storePassURL = this.match("quoted string");
   }

   private void writeKeyStoreEntry(PrintWriter var1) {
      var1.print("keystore \"");
      var1.print(this.keyStoreUrlString);
      var1.print('"');
      if (this.keyStoreType != null && this.keyStoreType.length() > 0) {
         var1.print(", \"" + this.keyStoreType + "\"");
      }

      if (this.keyStoreProvider != null && this.keyStoreProvider.length() > 0) {
         var1.print(", \"" + this.keyStoreProvider + "\"");
      }

      var1.println(";");
      var1.println();
   }

   private void writeStorePassURL(PrintWriter var1) {
      var1.print("keystorePasswordURL \"");
      var1.print(this.storePassURL);
      var1.print('"');
      var1.println(";");
      var1.println();
   }

   private PolicyParser.GrantEntry parseGrantEntry() throws PolicyParser.ParsingException, IOException {
      PolicyParser.GrantEntry var1 = new PolicyParser.GrantEntry();
      LinkedList var2 = null;
      boolean var3 = false;
      this.match("grant");

      while(true) {
         int var13;
         while(!this.peek("{")) {
            if (this.peekAndMatch("Codebase")) {
               if (var1.codeBase != null) {
                  throw new PolicyParser.ParsingException(this.st.lineno(), ResourcesMgr.getString("multiple.Codebase.expressions"));
               }

               var1.codeBase = this.match("quoted string");
               this.peekAndMatch(",");
            } else if (this.peekAndMatch("SignedBy")) {
               if (var1.signedBy != null) {
                  throw new PolicyParser.ParsingException(this.st.lineno(), ResourcesMgr.getString("multiple.SignedBy.expressions"));
               }

               var1.signedBy = this.match("quoted string");
               StringTokenizer var11 = new StringTokenizer(var1.signedBy, ",", true);
               int var16 = 0;
               var13 = 0;

               while(var11.hasMoreTokens()) {
                  String var7 = var11.nextToken().trim();
                  if (var7.equals(",")) {
                     ++var13;
                  } else if (var7.length() > 0) {
                     ++var16;
                  }
               }

               if (var16 <= var13) {
                  throw new PolicyParser.ParsingException(this.st.lineno(), ResourcesMgr.getString("SignedBy.has.empty.alias"));
               }

               this.peekAndMatch(",");
            } else {
               if (!this.peekAndMatch("Principal")) {
                  throw new PolicyParser.ParsingException(this.st.lineno(), ResourcesMgr.getString("expected.codeBase.or.SignedBy.or.Principal"));
               }

               if (var2 == null) {
                  var2 = new LinkedList();
               }

               String var4;
               String var5;
               if (this.peek("\"")) {
                  var4 = "PolicyParser.REPLACE_NAME";
                  var5 = this.match("principal type");
               } else {
                  if (this.peek("*")) {
                     this.match("*");
                     var4 = "WILDCARD_PRINCIPAL_CLASS";
                  } else {
                     var4 = this.match("principal type");
                  }

                  if (this.peek("*")) {
                     this.match("*");
                     var5 = "WILDCARD_PRINCIPAL_NAME";
                  } else {
                     var5 = this.match("quoted string");
                  }

                  if (var4.equals("WILDCARD_PRINCIPAL_CLASS") && !var5.equals("WILDCARD_PRINCIPAL_NAME")) {
                     if (debug != null) {
                        debug.println("disallowing principal that has WILDCARD class but no WILDCARD name");
                     }

                     throw new PolicyParser.ParsingException(this.st.lineno(), ResourcesMgr.getString("can.not.specify.Principal.with.a.wildcard.class.without.a.wildcard.name"));
                  }
               }

               try {
                  var5 = this.expand(var5);
                  if (var4.equals("javax.security.auth.x500.X500Principal") && !var5.equals("WILDCARD_PRINCIPAL_NAME")) {
                     X500Principal var6 = new X500Principal((new X500Principal(var5)).toString());
                     var5 = var6.getName();
                  }

                  var2.add(new PolicyParser.PrincipalEntry(var4, var5));
               } catch (PropertyExpander.ExpandException var10) {
                  if (debug != null) {
                     debug.println("principal name expansion failed: " + var5);
                  }

                  var3 = true;
               }

               this.peekAndMatch(",");
            }
         }

         if (var2 != null) {
            var1.principals = var2;
         }

         this.match("{");

         for(; !this.peek("}"); this.match(";")) {
            if (!this.peek("Permission")) {
               throw new PolicyParser.ParsingException(this.st.lineno(), ResourcesMgr.getString("expected.permission.entry"));
            }

            try {
               PolicyParser.PermissionEntry var12 = this.parsePermissionEntry();
               var1.add(var12);
            } catch (PropertyExpander.ExpandException var9) {
               if (debug != null) {
                  debug.println(var9.toString());
               }

               this.skipEntry();
            }
         }

         this.match("}");

         try {
            if (var1.signedBy != null) {
               var1.signedBy = this.expand(var1.signedBy);
            }

            if (var1.codeBase != null) {
               if (var1.codeBase.equals("${java.ext.dirs}")) {
                  var1.codeBase = "${{java.ext.dirs}}";
               }

               int var14;
               if ((var14 = var1.codeBase.indexOf("${{java.ext.dirs}}")) < 0) {
                  var1.codeBase = this.expand(var1.codeBase, true).replace(File.separatorChar, '/');
               } else {
                  String[] var17 = parseExtDirs(var1.codeBase, var14);
                  if (var17 != null && var17.length > 0) {
                     for(var13 = 0; var13 < var17.length; ++var13) {
                        PolicyParser.GrantEntry var15 = (PolicyParser.GrantEntry)var1.clone();
                        var15.codeBase = var17[var13];
                        this.add(var15);
                        if (debug != null) {
                           debug.println("creating policy entry for expanded java.ext.dirs path:\n\t\t" + var17[var13]);
                        }
                     }
                  }

                  var3 = true;
               }
            }
         } catch (PropertyExpander.ExpandException var8) {
            if (debug != null) {
               debug.println(var8.toString());
            }

            return null;
         }

         return var3 ? null : var1;
      }
   }

   private PolicyParser.PermissionEntry parsePermissionEntry() throws PolicyParser.ParsingException, IOException, PropertyExpander.ExpandException {
      PolicyParser.PermissionEntry var1 = new PolicyParser.PermissionEntry();
      this.match("Permission");
      var1.permission = this.match("permission type");
      if (this.peek("\"")) {
         var1.name = this.expand(this.match("quoted string"));
      }

      if (!this.peek(",")) {
         return var1;
      } else {
         this.match(",");
         if (this.peek("\"")) {
            var1.action = this.expand(this.match("quoted string"));
            if (!this.peek(",")) {
               return var1;
            }

            this.match(",");
         }

         if (this.peekAndMatch("SignedBy")) {
            var1.signedBy = this.expand(this.match("quoted string"));
         }

         return var1;
      }
   }

   private PolicyParser.DomainEntry parseDomainEntry() throws PolicyParser.ParsingException, IOException {
      boolean var1 = false;
      String var3 = null;
      Object var4 = new HashMap();
      this.match("domain");

      for(var3 = this.match("domain name"); !this.peek("{"); var4 = this.parseProperties("{")) {
      }

      this.match("{");
      PolicyParser.DomainEntry var2 = new PolicyParser.DomainEntry(var3, (Map)var4);

      while(!this.peek("}")) {
         this.match("keystore");
         var3 = this.match("keystore name");
         if (!this.peek("}")) {
            var4 = this.parseProperties(";");
         }

         this.match(";");
         var2.add(new PolicyParser.KeyStoreEntry(var3, (Map)var4));
      }

      this.match("}");
      return var1 ? null : var2;
   }

   private Map<String, String> parseProperties(String var1) throws PolicyParser.ParsingException, IOException {
      HashMap var2;
      String var3;
      String var4;
      for(var2 = new HashMap(); !this.peek(var1); var2.put(var3.toLowerCase(Locale.ENGLISH), var4)) {
         var3 = this.match("property name");
         this.match("=");

         try {
            var4 = this.expand(this.match("quoted string"));
         } catch (PropertyExpander.ExpandException var6) {
            throw new IOException(var6.getLocalizedMessage());
         }
      }

      return var2;
   }

   static String[] parseExtDirs(String var0, int var1) {
      String var2 = System.getProperty("java.ext.dirs");
      String var3 = var1 > 0 ? var0.substring(0, var1) : "file:";
      int var4 = var1 + "${{java.ext.dirs}}".length();
      String var5 = var4 < var0.length() ? var0.substring(var4) : (String)null;
      String[] var6 = null;
      if (var2 != null) {
         StringTokenizer var8 = new StringTokenizer(var2, File.pathSeparator);
         int var9 = var8.countTokens();
         var6 = new String[var9];

         for(int var10 = 0; var10 < var9; ++var10) {
            File var11 = new File(var8.nextToken());
            var6[var10] = ParseUtil.encodePath(var11.getAbsolutePath());
            if (!var6[var10].startsWith("/")) {
               var6[var10] = "/" + var6[var10];
            }

            String var7 = var5 == null ? (var6[var10].endsWith("/") ? "*" : "/*") : var5;
            var6[var10] = var3 + var6[var10] + var7;
         }
      }

      return var6;
   }

   private boolean peekAndMatch(String var1) throws PolicyParser.ParsingException, IOException {
      if (this.peek(var1)) {
         this.match(var1);
         return true;
      } else {
         return false;
      }
   }

   private boolean peek(String var1) {
      boolean var2 = false;
      switch(this.lookahead) {
      case -3:
         if (var1.equalsIgnoreCase(this.st.sval)) {
            var2 = true;
         }
         break;
      case 34:
         if (var1.equalsIgnoreCase("\"")) {
            var2 = true;
         }
         break;
      case 42:
         if (var1.equalsIgnoreCase("*")) {
            var2 = true;
         }
         break;
      case 44:
         if (var1.equalsIgnoreCase(",")) {
            var2 = true;
         }
         break;
      case 59:
         if (var1.equalsIgnoreCase(";")) {
            var2 = true;
         }
         break;
      case 123:
         if (var1.equalsIgnoreCase("{")) {
            var2 = true;
         }
         break;
      case 125:
         if (var1.equalsIgnoreCase("}")) {
            var2 = true;
         }
      }

      return var2;
   }

   private String match(String var1) throws PolicyParser.ParsingException, IOException {
      String var2 = null;
      switch(this.lookahead) {
      case -3:
         if (var1.equalsIgnoreCase(this.st.sval)) {
            this.lookahead = this.st.nextToken();
         } else if (var1.equalsIgnoreCase("permission type")) {
            var2 = this.st.sval;
            this.lookahead = this.st.nextToken();
         } else if (var1.equalsIgnoreCase("principal type")) {
            var2 = this.st.sval;
            this.lookahead = this.st.nextToken();
         } else {
            if (!var1.equalsIgnoreCase("domain name") && !var1.equalsIgnoreCase("keystore name") && !var1.equalsIgnoreCase("property name")) {
               throw new PolicyParser.ParsingException(this.st.lineno(), var1, this.st.sval);
            }

            var2 = this.st.sval;
            this.lookahead = this.st.nextToken();
         }
         break;
      case -2:
         throw new PolicyParser.ParsingException(this.st.lineno(), var1, ResourcesMgr.getString("number.") + String.valueOf(this.st.nval));
      case -1:
         MessageFormat var3 = new MessageFormat(ResourcesMgr.getString("expected.expect.read.end.of.file."));
         Object[] var4 = new Object[]{var1};
         throw new PolicyParser.ParsingException(var3.format(var4));
      case 34:
         if (var1.equalsIgnoreCase("quoted string")) {
            var2 = this.st.sval;
            this.lookahead = this.st.nextToken();
         } else if (var1.equalsIgnoreCase("permission type")) {
            var2 = this.st.sval;
            this.lookahead = this.st.nextToken();
         } else {
            if (!var1.equalsIgnoreCase("principal type")) {
               throw new PolicyParser.ParsingException(this.st.lineno(), var1, this.st.sval);
            }

            var2 = this.st.sval;
            this.lookahead = this.st.nextToken();
         }
         break;
      case 42:
         if (!var1.equalsIgnoreCase("*")) {
            throw new PolicyParser.ParsingException(this.st.lineno(), var1, "*");
         }

         this.lookahead = this.st.nextToken();
         break;
      case 44:
         if (!var1.equalsIgnoreCase(",")) {
            throw new PolicyParser.ParsingException(this.st.lineno(), var1, ",");
         }

         this.lookahead = this.st.nextToken();
         break;
      case 59:
         if (!var1.equalsIgnoreCase(";")) {
            throw new PolicyParser.ParsingException(this.st.lineno(), var1, ";");
         }

         this.lookahead = this.st.nextToken();
         break;
      case 61:
         if (!var1.equalsIgnoreCase("=")) {
            throw new PolicyParser.ParsingException(this.st.lineno(), var1, "=");
         }

         this.lookahead = this.st.nextToken();
         break;
      case 123:
         if (!var1.equalsIgnoreCase("{")) {
            throw new PolicyParser.ParsingException(this.st.lineno(), var1, "{");
         }

         this.lookahead = this.st.nextToken();
         break;
      case 125:
         if (!var1.equalsIgnoreCase("}")) {
            throw new PolicyParser.ParsingException(this.st.lineno(), var1, "}");
         }

         this.lookahead = this.st.nextToken();
         break;
      default:
         throw new PolicyParser.ParsingException(this.st.lineno(), var1, new String(new char[]{(char)this.lookahead}));
      }

      return var2;
   }

   private void skipEntry() throws PolicyParser.ParsingException, IOException {
      while(this.lookahead != 59) {
         switch(this.lookahead) {
         case -2:
            throw new PolicyParser.ParsingException(this.st.lineno(), ";", ResourcesMgr.getString("number.") + String.valueOf(this.st.nval));
         case -1:
            throw new PolicyParser.ParsingException(ResourcesMgr.getString("expected.read.end.of.file."));
         default:
            this.lookahead = this.st.nextToken();
         }
      }

   }

   public static void main(String[] var0) throws Exception {
      FileReader var1 = new FileReader(var0[0]);
      Throwable var2 = null;

      try {
         FileWriter var3 = new FileWriter(var0[1]);
         Throwable var4 = null;

         try {
            PolicyParser var5 = new PolicyParser(true);
            var5.read(var1);
            var5.write(var3);
         } catch (Throwable var27) {
            var4 = var27;
            throw var27;
         } finally {
            if (var3 != null) {
               if (var4 != null) {
                  try {
                     var3.close();
                  } catch (Throwable var26) {
                     var4.addSuppressed(var26);
                  }
               } else {
                  var3.close();
               }
            }

         }
      } catch (Throwable var29) {
         var2 = var29;
         throw var29;
      } finally {
         if (var1 != null) {
            if (var2 != null) {
               try {
                  var1.close();
               } catch (Throwable var25) {
                  var2.addSuppressed(var25);
               }
            } else {
               var1.close();
            }
         }

      }

   }

   public static class ParsingException extends GeneralSecurityException {
      private static final long serialVersionUID = -4330692689482574072L;
      private String i18nMessage;

      public ParsingException(String var1) {
         super(var1);
         this.i18nMessage = var1;
      }

      public ParsingException(int var1, String var2) {
         super("line " + var1 + ": " + var2);
         MessageFormat var3 = new MessageFormat(ResourcesMgr.getString("line.number.msg"));
         Object[] var4 = new Object[]{new Integer(var1), var2};
         this.i18nMessage = var3.format(var4);
      }

      public ParsingException(int var1, String var2, String var3) {
         super("line " + var1 + ": expected [" + var2 + "], found [" + var3 + "]");
         MessageFormat var4 = new MessageFormat(ResourcesMgr.getString("line.number.expected.expect.found.actual."));
         Object[] var5 = new Object[]{new Integer(var1), var2, var3};
         this.i18nMessage = var4.format(var5);
      }

      public String getLocalizedMessage() {
         return this.i18nMessage;
      }
   }

   static class KeyStoreEntry {
      private final String name;
      private final Map<String, String> properties;

      KeyStoreEntry(String var1, Map<String, String> var2) {
         this.name = var1;
         this.properties = var2;
      }

      String getName() {
         return this.name;
      }

      Map<String, String> getProperties() {
         return this.properties;
      }

      public String toString() {
         StringBuilder var1 = (new StringBuilder("\n    keystore ")).append(this.name);
         if (this.properties != null) {
            Iterator var2 = this.properties.entrySet().iterator();

            while(var2.hasNext()) {
               Map.Entry var3 = (Map.Entry)var2.next();
               var1.append("\n        ").append((String)var3.getKey()).append('=').append((String)var3.getValue());
            }
         }

         var1.append(";");
         return var1.toString();
      }
   }

   static class DomainEntry {
      private final String name;
      private final Map<String, String> properties;
      private final Map<String, PolicyParser.KeyStoreEntry> entries;

      DomainEntry(String var1, Map<String, String> var2) {
         this.name = var1;
         this.properties = var2;
         this.entries = new HashMap();
      }

      String getName() {
         return this.name;
      }

      Map<String, String> getProperties() {
         return this.properties;
      }

      Collection<PolicyParser.KeyStoreEntry> getEntries() {
         return this.entries.values();
      }

      void add(PolicyParser.KeyStoreEntry var1) throws PolicyParser.ParsingException {
         String var2 = var1.getName();
         if (!this.entries.containsKey(var2)) {
            this.entries.put(var2, var1);
         } else {
            MessageFormat var3 = new MessageFormat(ResourcesMgr.getString("duplicate.keystore.name"));
            Object[] var4 = new Object[]{var2};
            throw new PolicyParser.ParsingException(var3.format(var4));
         }
      }

      public String toString() {
         StringBuilder var1 = (new StringBuilder("\ndomain ")).append(this.name);
         Iterator var2;
         if (this.properties != null) {
            var2 = this.properties.entrySet().iterator();

            while(var2.hasNext()) {
               Map.Entry var3 = (Map.Entry)var2.next();
               var1.append("\n        ").append((String)var3.getKey()).append('=').append((String)var3.getValue());
            }
         }

         var1.append(" {\n");
         if (this.entries != null) {
            var2 = this.entries.values().iterator();

            while(var2.hasNext()) {
               PolicyParser.KeyStoreEntry var4 = (PolicyParser.KeyStoreEntry)var2.next();
               var1.append((Object)var4).append("\n");
            }
         }

         var1.append("}");
         return var1.toString();
      }
   }

   public static class PermissionEntry {
      public String permission;
      public String name;
      public String action;
      public String signedBy;

      public PermissionEntry() {
      }

      public PermissionEntry(String var1, String var2, String var3) {
         this.permission = var1;
         this.name = var2;
         this.action = var3;
      }

      public int hashCode() {
         int var1 = this.permission.hashCode();
         if (this.name != null) {
            var1 ^= this.name.hashCode();
         }

         if (this.action != null) {
            var1 ^= this.action.hashCode();
         }

         return var1;
      }

      public boolean equals(Object var1) {
         if (var1 == this) {
            return true;
         } else if (!(var1 instanceof PolicyParser.PermissionEntry)) {
            return false;
         } else {
            PolicyParser.PermissionEntry var2 = (PolicyParser.PermissionEntry)var1;
            if (this.permission == null) {
               if (var2.permission != null) {
                  return false;
               }
            } else if (!this.permission.equals(var2.permission)) {
               return false;
            }

            if (this.name == null) {
               if (var2.name != null) {
                  return false;
               }
            } else if (!this.name.equals(var2.name)) {
               return false;
            }

            if (this.action == null) {
               if (var2.action != null) {
                  return false;
               }
            } else if (!this.action.equals(var2.action)) {
               return false;
            }

            if (this.signedBy == null) {
               if (var2.signedBy != null) {
                  return false;
               }
            } else if (!this.signedBy.equals(var2.signedBy)) {
               return false;
            }

            return true;
         }
      }

      public void write(PrintWriter var1) {
         var1.print("permission ");
         var1.print(this.permission);
         if (this.name != null) {
            var1.print(" \"");
            var1.print(this.name.replaceAll("\\\\", "\\\\\\\\").replaceAll("\\\"", "\\\\\\\""));
            var1.print('"');
         }

         if (this.action != null) {
            var1.print(", \"");
            var1.print(this.action);
            var1.print('"');
         }

         if (this.signedBy != null) {
            var1.print(", signedBy \"");
            var1.print(this.signedBy);
            var1.print('"');
         }

         var1.println(";");
      }
   }

   public static class PrincipalEntry implements Principal {
      public static final String WILDCARD_CLASS = "WILDCARD_PRINCIPAL_CLASS";
      public static final String WILDCARD_NAME = "WILDCARD_PRINCIPAL_NAME";
      public static final String REPLACE_NAME = "PolicyParser.REPLACE_NAME";
      String principalClass;
      String principalName;

      public PrincipalEntry(String var1, String var2) {
         if (var1 != null && var2 != null) {
            this.principalClass = var1;
            this.principalName = var2;
         } else {
            throw new NullPointerException(ResourcesMgr.getString("null.principalClass.or.principalName"));
         }
      }

      boolean isWildcardName() {
         return this.principalName.equals("WILDCARD_PRINCIPAL_NAME");
      }

      boolean isWildcardClass() {
         return this.principalClass.equals("WILDCARD_PRINCIPAL_CLASS");
      }

      boolean isReplaceName() {
         return this.principalClass.equals("PolicyParser.REPLACE_NAME");
      }

      public String getPrincipalClass() {
         return this.principalClass;
      }

      public String getPrincipalName() {
         return this.principalName;
      }

      public String getDisplayClass() {
         if (this.isWildcardClass()) {
            return "*";
         } else {
            return this.isReplaceName() ? "" : this.principalClass;
         }
      }

      public String getDisplayName() {
         return this.getDisplayName(false);
      }

      public String getDisplayName(boolean var1) {
         if (this.isWildcardName()) {
            return "*";
         } else {
            return var1 ? "\"" + this.principalName + "\"" : this.principalName;
         }
      }

      public String getName() {
         return this.principalName;
      }

      public String toString() {
         return !this.isReplaceName() ? this.getDisplayClass() + "/" + this.getDisplayName() : this.getDisplayName();
      }

      public boolean equals(Object var1) {
         if (this == var1) {
            return true;
         } else if (!(var1 instanceof PolicyParser.PrincipalEntry)) {
            return false;
         } else {
            PolicyParser.PrincipalEntry var2 = (PolicyParser.PrincipalEntry)var1;
            return this.principalClass.equals(var2.principalClass) && this.principalName.equals(var2.principalName);
         }
      }

      public int hashCode() {
         return this.principalClass.hashCode();
      }

      public void write(PrintWriter var1) {
         var1.print("principal " + this.getDisplayClass() + " " + this.getDisplayName(true));
      }
   }

   public static class GrantEntry {
      public String signedBy;
      public String codeBase;
      public LinkedList<PolicyParser.PrincipalEntry> principals;
      public Vector<PolicyParser.PermissionEntry> permissionEntries;

      public GrantEntry() {
         this.principals = new LinkedList();
         this.permissionEntries = new Vector();
      }

      public GrantEntry(String var1, String var2) {
         this.codeBase = var2;
         this.signedBy = var1;
         this.principals = new LinkedList();
         this.permissionEntries = new Vector();
      }

      public void add(PolicyParser.PermissionEntry var1) {
         this.permissionEntries.addElement(var1);
      }

      public boolean remove(PolicyParser.PrincipalEntry var1) {
         return this.principals.remove(var1);
      }

      public boolean remove(PolicyParser.PermissionEntry var1) {
         return this.permissionEntries.removeElement(var1);
      }

      public boolean contains(PolicyParser.PrincipalEntry var1) {
         return this.principals.contains(var1);
      }

      public boolean contains(PolicyParser.PermissionEntry var1) {
         return this.permissionEntries.contains(var1);
      }

      public Enumeration<PolicyParser.PermissionEntry> permissionElements() {
         return this.permissionEntries.elements();
      }

      public void write(PrintWriter var1) {
         var1.print("grant");
         if (this.signedBy != null) {
            var1.print(" signedBy \"");
            var1.print(this.signedBy);
            var1.print('"');
            if (this.codeBase != null) {
               var1.print(", ");
            }
         }

         if (this.codeBase != null) {
            var1.print(" codeBase \"");
            var1.print(this.codeBase);
            var1.print('"');
            if (this.principals != null && this.principals.size() > 0) {
               var1.print(",\n");
            }
         }

         if (this.principals != null && this.principals.size() > 0) {
            Iterator var2 = this.principals.iterator();

            while(var2.hasNext()) {
               var1.print("      ");
               PolicyParser.PrincipalEntry var3 = (PolicyParser.PrincipalEntry)var2.next();
               var3.write(var1);
               if (var2.hasNext()) {
                  var1.print(",\n");
               }
            }
         }

         var1.println(" {");
         Enumeration var4 = this.permissionEntries.elements();

         while(var4.hasMoreElements()) {
            PolicyParser.PermissionEntry var5 = (PolicyParser.PermissionEntry)var4.nextElement();
            var1.write("  ");
            var5.write(var1);
         }

         var1.println("};");
      }

      public Object clone() {
         PolicyParser.GrantEntry var1 = new PolicyParser.GrantEntry();
         var1.codeBase = this.codeBase;
         var1.signedBy = this.signedBy;
         var1.principals = new LinkedList(this.principals);
         var1.permissionEntries = new Vector(this.permissionEntries);
         return var1;
      }
   }
}
