package com.sun.jndi.ldap;

import java.util.Vector;
import javax.naming.ConfigurationException;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InvalidAttributeIdentifierException;
import javax.naming.directory.InvalidAttributeValueException;

final class LdapSchemaParser {
   private static final boolean debug = false;
   static final String OBJECTCLASSDESC_ATTR_ID = "objectClasses";
   static final String ATTRIBUTEDESC_ATTR_ID = "attributeTypes";
   static final String SYNTAXDESC_ATTR_ID = "ldapSyntaxes";
   static final String MATCHRULEDESC_ATTR_ID = "matchingRules";
   static final String OBJECTCLASS_DEFINITION_NAME = "ClassDefinition";
   private static final String[] CLASS_DEF_ATTRS = new String[]{"objectclass", "ClassDefinition"};
   static final String ATTRIBUTE_DEFINITION_NAME = "AttributeDefinition";
   private static final String[] ATTR_DEF_ATTRS = new String[]{"objectclass", "AttributeDefinition"};
   static final String SYNTAX_DEFINITION_NAME = "SyntaxDefinition";
   private static final String[] SYNTAX_DEF_ATTRS = new String[]{"objectclass", "SyntaxDefinition"};
   static final String MATCHRULE_DEFINITION_NAME = "MatchingRule";
   private static final String[] MATCHRULE_DEF_ATTRS = new String[]{"objectclass", "MatchingRule"};
   private static final char SINGLE_QUOTE = '\'';
   private static final char WHSP = ' ';
   private static final char OID_LIST_BEGIN = '(';
   private static final char OID_LIST_END = ')';
   private static final char OID_SEPARATOR = '$';
   private static final String NUMERICOID_ID = "NUMERICOID";
   private static final String NAME_ID = "NAME";
   private static final String DESC_ID = "DESC";
   private static final String OBSOLETE_ID = "OBSOLETE";
   private static final String SUP_ID = "SUP";
   private static final String PRIVATE_ID = "X-";
   private static final String ABSTRACT_ID = "ABSTRACT";
   private static final String STRUCTURAL_ID = "STRUCTURAL";
   private static final String AUXILARY_ID = "AUXILIARY";
   private static final String MUST_ID = "MUST";
   private static final String MAY_ID = "MAY";
   private static final String EQUALITY_ID = "EQUALITY";
   private static final String ORDERING_ID = "ORDERING";
   private static final String SUBSTR_ID = "SUBSTR";
   private static final String SYNTAX_ID = "SYNTAX";
   private static final String SINGLE_VAL_ID = "SINGLE-VALUE";
   private static final String COLLECTIVE_ID = "COLLECTIVE";
   private static final String NO_USER_MOD_ID = "NO-USER-MODIFICATION";
   private static final String USAGE_ID = "USAGE";
   private static final String SCHEMA_TRUE_VALUE = "true";
   private boolean netscapeBug;

   LdapSchemaParser(boolean var1) {
      this.netscapeBug = var1;
   }

   static final void LDAP2JNDISchema(Attributes var0, LdapSchemaCtx var1) throws NamingException {
      Attribute var2 = null;
      Attribute var3 = null;
      Attribute var4 = null;
      Attribute var5 = null;
      var2 = var0.get("objectClasses");
      if (var2 != null) {
         objectDescs2ClassDefs(var2, var1);
      }

      var3 = var0.get("attributeTypes");
      if (var3 != null) {
         attrDescs2AttrDefs(var3, var1);
      }

      var4 = var0.get("ldapSyntaxes");
      if (var4 != null) {
         syntaxDescs2SyntaxDefs(var4, var1);
      }

      var5 = var0.get("matchingRules");
      if (var5 != null) {
         matchRuleDescs2MatchRuleDefs(var5, var1);
      }

   }

   private static final DirContext objectDescs2ClassDefs(Attribute var0, LdapSchemaCtx var1) throws NamingException {
      BasicAttributes var5 = new BasicAttributes(true);
      var5.put(CLASS_DEF_ATTRS[0], CLASS_DEF_ATTRS[1]);
      LdapSchemaCtx var4 = var1.setup(2, "ClassDefinition", var5);
      NamingEnumeration var2 = var0.getAll();

      while(var2.hasMore()) {
         String var7 = (String)var2.next();

         try {
            Object[] var8 = desc2Def(var7);
            String var6 = (String)var8[0];
            Attributes var3 = (Attributes)var8[1];
            var4.setup(6, var6, var3);
         } catch (NamingException var9) {
         }
      }

      return var4;
   }

   private static final DirContext attrDescs2AttrDefs(Attribute var0, LdapSchemaCtx var1) throws NamingException {
      BasicAttributes var5 = new BasicAttributes(true);
      var5.put(ATTR_DEF_ATTRS[0], ATTR_DEF_ATTRS[1]);
      LdapSchemaCtx var4 = var1.setup(3, "AttributeDefinition", var5);
      NamingEnumeration var2 = var0.getAll();

      while(var2.hasMore()) {
         String var7 = (String)var2.next();

         try {
            Object[] var8 = desc2Def(var7);
            String var6 = (String)var8[0];
            Attributes var3 = (Attributes)var8[1];
            var4.setup(7, var6, var3);
         } catch (NamingException var9) {
         }
      }

      return var4;
   }

   private static final DirContext syntaxDescs2SyntaxDefs(Attribute var0, LdapSchemaCtx var1) throws NamingException {
      BasicAttributes var5 = new BasicAttributes(true);
      var5.put(SYNTAX_DEF_ATTRS[0], SYNTAX_DEF_ATTRS[1]);
      LdapSchemaCtx var4 = var1.setup(4, "SyntaxDefinition", var5);
      NamingEnumeration var2 = var0.getAll();

      while(var2.hasMore()) {
         String var7 = (String)var2.next();

         try {
            Object[] var8 = desc2Def(var7);
            String var6 = (String)var8[0];
            Attributes var3 = (Attributes)var8[1];
            var4.setup(8, var6, var3);
         } catch (NamingException var9) {
         }
      }

      return var4;
   }

   private static final DirContext matchRuleDescs2MatchRuleDefs(Attribute var0, LdapSchemaCtx var1) throws NamingException {
      BasicAttributes var5 = new BasicAttributes(true);
      var5.put(MATCHRULE_DEF_ATTRS[0], MATCHRULE_DEF_ATTRS[1]);
      LdapSchemaCtx var4 = var1.setup(5, "MatchingRule", var5);
      NamingEnumeration var2 = var0.getAll();

      while(var2.hasMore()) {
         String var7 = (String)var2.next();

         try {
            Object[] var8 = desc2Def(var7);
            String var6 = (String)var8[0];
            Attributes var3 = (Attributes)var8[1];
            var4.setup(9, var6, var3);
         } catch (NamingException var9) {
         }
      }

      return var4;
   }

   private static final Object[] desc2Def(String var0) throws NamingException {
      BasicAttributes var1 = new BasicAttributes(true);
      Attribute var2 = null;
      int[] var3 = new int[]{1};
      boolean var4 = true;
      var2 = readNumericOID(var0, var3);
      String var5 = (String)var2.get(0);
      var1.put(var2);
      skipWhitespace(var0, var3);

      while(var4) {
         var2 = readNextTag(var0, var3);
         var1.put(var2);
         if (var2.getID().equals("NAME")) {
            var5 = (String)var2.get(0);
         }

         skipWhitespace(var0, var3);
         if (var3[0] >= var0.length() - 1) {
            var4 = false;
         }
      }

      return new Object[]{var5, var1};
   }

   private static final int findTrailingWhitespace(String var0, int var1) {
      for(int var2 = var1; var2 > 0; --var2) {
         if (var0.charAt(var2) != ' ') {
            return var2 + 1;
         }
      }

      return 0;
   }

   private static final void skipWhitespace(String var0, int[] var1) {
      for(int var2 = var1[0]; var2 < var0.length(); ++var2) {
         if (var0.charAt(var2) != ' ') {
            var1[0] = var2;
            return;
         }
      }

   }

   private static final Attribute readNumericOID(String var0, int[] var1) throws NamingException {
      String var4 = null;
      skipWhitespace(var0, var1);
      int var2 = var1[0];
      int var3 = var0.indexOf(32, var2);
      if (var3 != -1 && var3 - var2 >= 1) {
         var4 = var0.substring(var2, var3);
         var1[0] += var4.length();
         return new BasicAttribute("NUMERICOID", var4);
      } else {
         throw new InvalidAttributeValueException("no numericoid found: " + var0);
      }
   }

   private static final Attribute readNextTag(String var0, int[] var1) throws NamingException {
      BasicAttribute var2 = null;
      String var3 = null;
      String[] var4 = null;
      skipWhitespace(var0, var1);
      int var5 = var0.indexOf(32, var1[0]);
      if (var5 < 0) {
         var3 = var0.substring(var1[0], var0.length() - 1);
      } else {
         var3 = var0.substring(var1[0], var5);
      }

      var4 = readTag(var3, var0, var1);
      if (var4.length < 0) {
         throw new InvalidAttributeValueException("no values for attribute \"" + var3 + "\"");
      } else {
         var2 = new BasicAttribute(var3, var4[0]);

         for(int var6 = 1; var6 < var4.length; ++var6) {
            var2.add(var4[var6]);
         }

         return var2;
      }
   }

   private static final String[] readTag(String var0, String var1, int[] var2) throws NamingException {
      var2[0] += var0.length();
      skipWhitespace(var1, var2);
      if (var0.equals("NAME")) {
         return readQDescrs(var1, var2);
      } else if (var0.equals("DESC")) {
         return readQDString(var1, var2);
      } else if (!var0.equals("EQUALITY") && !var0.equals("ORDERING") && !var0.equals("SUBSTR") && !var0.equals("SYNTAX")) {
         if (!var0.equals("OBSOLETE") && !var0.equals("ABSTRACT") && !var0.equals("STRUCTURAL") && !var0.equals("AUXILIARY") && !var0.equals("SINGLE-VALUE") && !var0.equals("COLLECTIVE") && !var0.equals("NO-USER-MODIFICATION")) {
            return !var0.equals("SUP") && !var0.equals("MUST") && !var0.equals("MAY") && !var0.equals("USAGE") ? readQDStrings(var1, var2) : readOIDs(var1, var2);
         } else {
            return new String[]{"true"};
         }
      } else {
         return readWOID(var1, var2);
      }
   }

   private static final String[] readQDString(String var0, int[] var1) throws NamingException {
      int var2 = var0.indexOf(39, var1[0]) + 1;
      int var3 = var0.indexOf(39, var2);
      if (var2 != -1 && var3 != -1 && var2 != var3) {
         if (var0.charAt(var2 - 1) != '\'') {
            throw new InvalidAttributeIdentifierException("qdstring has no end mark: " + var0);
         } else {
            var1[0] = var3 + 1;
            return new String[]{var0.substring(var2, var3)};
         }
      } else {
         throw new InvalidAttributeIdentifierException("malformed QDString: " + var0);
      }
   }

   private static final String[] readQDStrings(String var0, int[] var1) throws NamingException {
      return readQDescrs(var0, var1);
   }

   private static final String[] readQDescrs(String var0, int[] var1) throws NamingException {
      skipWhitespace(var0, var1);
      switch(var0.charAt(var1[0])) {
      case '\'':
         return readQDString(var0, var1);
      case '(':
         return readQDescrList(var0, var1);
      default:
         throw new InvalidAttributeValueException("unexpected oids string: " + var0);
      }
   }

   private static final String[] readQDescrList(String var0, int[] var1) throws NamingException {
      Vector var4 = new Vector(5);
      int var10002 = var1[0]++;
      skipWhitespace(var0, var1);
      int var2 = var1[0];
      int var3 = var0.indexOf(41, var2);
      if (var3 == -1) {
         throw new InvalidAttributeValueException("oidlist has no end mark: " + var0);
      } else {
         String[] var5;
         while(var2 < var3) {
            var5 = readQDString(var0, var1);
            var4.addElement(var5[0]);
            skipWhitespace(var0, var1);
            var2 = var1[0];
         }

         var1[0] = var3 + 1;
         var5 = new String[var4.size()];

         for(int var6 = 0; var6 < var5.length; ++var6) {
            var5[var6] = (String)var4.elementAt(var6);
         }

         return var5;
      }
   }

   private static final String[] readWOID(String var0, int[] var1) throws NamingException {
      skipWhitespace(var0, var1);
      if (var0.charAt(var1[0]) == '\'') {
         return readQDString(var0, var1);
      } else {
         int var2 = var1[0];
         int var3 = var0.indexOf(32, var2);
         if (var3 != -1 && var2 != var3) {
            var1[0] = var3 + 1;
            return new String[]{var0.substring(var2, var3)};
         } else {
            throw new InvalidAttributeIdentifierException("malformed OID: " + var0);
         }
      }
   }

   private static final String[] readOIDs(String var0, int[] var1) throws NamingException {
      skipWhitespace(var0, var1);
      if (var0.charAt(var1[0]) != '(') {
         return readWOID(var0, var1);
      } else {
         String var5 = null;
         Vector var6 = new Vector(5);
         int var10002 = var1[0]++;
         skipWhitespace(var0, var1);
         int var2 = var1[0];
         int var4 = var0.indexOf(41, var2);
         int var3 = var0.indexOf(36, var2);
         if (var4 == -1) {
            throw new InvalidAttributeValueException("oidlist has no end mark: " + var0);
         } else {
            if (var3 == -1 || var4 < var3) {
               var3 = var4;
            }

            int var7;
            while(var3 < var4 && var3 > 0) {
               var7 = findTrailingWhitespace(var0, var3 - 1);
               var5 = var0.substring(var2, var7);
               var6.addElement(var5);
               var1[0] = var3 + 1;
               skipWhitespace(var0, var1);
               var2 = var1[0];
               var3 = var0.indexOf(36, var2);
            }

            var7 = findTrailingWhitespace(var0, var4 - 1);
            var5 = var0.substring(var2, var7);
            var6.addElement(var5);
            var1[0] = var4 + 1;
            String[] var8 = new String[var6.size()];

            for(int var9 = 0; var9 < var8.length; ++var9) {
               var8[var9] = (String)var6.elementAt(var9);
            }

            return var8;
         }
      }
   }

   private final String classDef2ObjectDesc(Attributes var1) throws NamingException {
      StringBuffer var2 = new StringBuffer("( ");
      Attribute var3 = null;
      byte var4 = 0;
      var3 = var1.get("NUMERICOID");
      if (var3 != null) {
         var2.append(this.writeNumericOID(var3));
         int var7 = var4 + 1;
         var3 = var1.get("NAME");
         if (var3 != null) {
            var2.append(this.writeQDescrs(var3));
            ++var7;
         }

         var3 = var1.get("DESC");
         if (var3 != null) {
            var2.append(this.writeQDString(var3));
            ++var7;
         }

         var3 = var1.get("OBSOLETE");
         if (var3 != null) {
            var2.append(this.writeBoolean(var3));
            ++var7;
         }

         var3 = var1.get("SUP");
         if (var3 != null) {
            var2.append(this.writeOIDs(var3));
            ++var7;
         }

         var3 = var1.get("ABSTRACT");
         if (var3 != null) {
            var2.append(this.writeBoolean(var3));
            ++var7;
         }

         var3 = var1.get("STRUCTURAL");
         if (var3 != null) {
            var2.append(this.writeBoolean(var3));
            ++var7;
         }

         var3 = var1.get("AUXILIARY");
         if (var3 != null) {
            var2.append(this.writeBoolean(var3));
            ++var7;
         }

         var3 = var1.get("MUST");
         if (var3 != null) {
            var2.append(this.writeOIDs(var3));
            ++var7;
         }

         var3 = var1.get("MAY");
         if (var3 != null) {
            var2.append(this.writeOIDs(var3));
            ++var7;
         }

         if (var7 < var1.size()) {
            String var5 = null;
            NamingEnumeration var6 = var1.getAll();

            while(var6.hasMoreElements()) {
               var3 = (Attribute)var6.next();
               var5 = var3.getID();
               if (!var5.equals("NUMERICOID") && !var5.equals("NAME") && !var5.equals("SUP") && !var5.equals("MAY") && !var5.equals("MUST") && !var5.equals("STRUCTURAL") && !var5.equals("DESC") && !var5.equals("AUXILIARY") && !var5.equals("ABSTRACT") && !var5.equals("OBSOLETE")) {
                  var2.append(this.writeQDStrings(var3));
               }
            }
         }

         var2.append(")");
         return var2.toString();
      } else {
         throw new ConfigurationException("Class definition doesn'thave a numeric OID");
      }
   }

   private final String attrDef2AttrDesc(Attributes var1) throws NamingException {
      StringBuffer var2 = new StringBuffer("( ");
      Attribute var3 = null;
      byte var4 = 0;
      var3 = var1.get("NUMERICOID");
      if (var3 == null) {
         throw new ConfigurationException("Attribute type doesn'thave a numeric OID");
      } else {
         var2.append(this.writeNumericOID(var3));
         int var7 = var4 + 1;
         var3 = var1.get("NAME");
         if (var3 != null) {
            var2.append(this.writeQDescrs(var3));
            ++var7;
         }

         var3 = var1.get("DESC");
         if (var3 != null) {
            var2.append(this.writeQDString(var3));
            ++var7;
         }

         var3 = var1.get("OBSOLETE");
         if (var3 != null) {
            var2.append(this.writeBoolean(var3));
            ++var7;
         }

         var3 = var1.get("SUP");
         if (var3 != null) {
            var2.append(this.writeWOID(var3));
            ++var7;
         }

         var3 = var1.get("EQUALITY");
         if (var3 != null) {
            var2.append(this.writeWOID(var3));
            ++var7;
         }

         var3 = var1.get("ORDERING");
         if (var3 != null) {
            var2.append(this.writeWOID(var3));
            ++var7;
         }

         var3 = var1.get("SUBSTR");
         if (var3 != null) {
            var2.append(this.writeWOID(var3));
            ++var7;
         }

         var3 = var1.get("SYNTAX");
         if (var3 != null) {
            var2.append(this.writeWOID(var3));
            ++var7;
         }

         var3 = var1.get("SINGLE-VALUE");
         if (var3 != null) {
            var2.append(this.writeBoolean(var3));
            ++var7;
         }

         var3 = var1.get("COLLECTIVE");
         if (var3 != null) {
            var2.append(this.writeBoolean(var3));
            ++var7;
         }

         var3 = var1.get("NO-USER-MODIFICATION");
         if (var3 != null) {
            var2.append(this.writeBoolean(var3));
            ++var7;
         }

         var3 = var1.get("USAGE");
         if (var3 != null) {
            var2.append(this.writeQDString(var3));
            ++var7;
         }

         if (var7 < var1.size()) {
            String var5 = null;
            NamingEnumeration var6 = var1.getAll();

            while(var6.hasMoreElements()) {
               var3 = (Attribute)var6.next();
               var5 = var3.getID();
               if (!var5.equals("NUMERICOID") && !var5.equals("NAME") && !var5.equals("SYNTAX") && !var5.equals("DESC") && !var5.equals("SINGLE-VALUE") && !var5.equals("EQUALITY") && !var5.equals("ORDERING") && !var5.equals("SUBSTR") && !var5.equals("NO-USER-MODIFICATION") && !var5.equals("USAGE") && !var5.equals("SUP") && !var5.equals("COLLECTIVE") && !var5.equals("OBSOLETE")) {
                  var2.append(this.writeQDStrings(var3));
               }
            }
         }

         var2.append(")");
         return var2.toString();
      }
   }

   private final String syntaxDef2SyntaxDesc(Attributes var1) throws NamingException {
      StringBuffer var2 = new StringBuffer("( ");
      Attribute var3 = null;
      byte var4 = 0;
      var3 = var1.get("NUMERICOID");
      if (var3 != null) {
         var2.append(this.writeNumericOID(var3));
         int var7 = var4 + 1;
         var3 = var1.get("DESC");
         if (var3 != null) {
            var2.append(this.writeQDString(var3));
            ++var7;
         }

         if (var7 < var1.size()) {
            String var5 = null;
            NamingEnumeration var6 = var1.getAll();

            while(var6.hasMoreElements()) {
               var3 = (Attribute)var6.next();
               var5 = var3.getID();
               if (!var5.equals("NUMERICOID") && !var5.equals("DESC")) {
                  var2.append(this.writeQDStrings(var3));
               }
            }
         }

         var2.append(")");
         return var2.toString();
      } else {
         throw new ConfigurationException("Attribute type doesn'thave a numeric OID");
      }
   }

   private final String matchRuleDef2MatchRuleDesc(Attributes var1) throws NamingException {
      StringBuffer var2 = new StringBuffer("( ");
      Attribute var3 = null;
      byte var4 = 0;
      var3 = var1.get("NUMERICOID");
      if (var3 == null) {
         throw new ConfigurationException("Attribute type doesn'thave a numeric OID");
      } else {
         var2.append(this.writeNumericOID(var3));
         int var7 = var4 + 1;
         var3 = var1.get("NAME");
         if (var3 != null) {
            var2.append(this.writeQDescrs(var3));
            ++var7;
         }

         var3 = var1.get("DESC");
         if (var3 != null) {
            var2.append(this.writeQDString(var3));
            ++var7;
         }

         var3 = var1.get("OBSOLETE");
         if (var3 != null) {
            var2.append(this.writeBoolean(var3));
            ++var7;
         }

         var3 = var1.get("SYNTAX");
         if (var3 == null) {
            throw new ConfigurationException("Attribute type doesn'thave a syntax OID");
         } else {
            var2.append(this.writeWOID(var3));
            ++var7;
            if (var7 < var1.size()) {
               String var5 = null;
               NamingEnumeration var6 = var1.getAll();

               while(var6.hasMoreElements()) {
                  var3 = (Attribute)var6.next();
                  var5 = var3.getID();
                  if (!var5.equals("NUMERICOID") && !var5.equals("NAME") && !var5.equals("SYNTAX") && !var5.equals("DESC") && !var5.equals("OBSOLETE")) {
                     var2.append(this.writeQDStrings(var3));
                  }
               }
            }

            var2.append(")");
            return var2.toString();
         }
      }
   }

   private final String writeNumericOID(Attribute var1) throws NamingException {
      if (var1.size() != 1) {
         throw new InvalidAttributeValueException("A class definition must have exactly one numeric OID");
      } else {
         return (String)((String)var1.get()) + ' ';
      }
   }

   private final String writeWOID(Attribute var1) throws NamingException {
      return this.netscapeBug ? this.writeQDString(var1) : var1.getID() + ' ' + var1.get() + ' ';
   }

   private final String writeQDString(Attribute var1) throws NamingException {
      if (var1.size() != 1) {
         throw new InvalidAttributeValueException(var1.getID() + " must have exactly one value");
      } else {
         return var1.getID() + ' ' + '\'' + var1.get() + '\'' + ' ';
      }
   }

   private final String writeQDStrings(Attribute var1) throws NamingException {
      return this.writeQDescrs(var1);
   }

   private final String writeQDescrs(Attribute var1) throws NamingException {
      switch(var1.size()) {
      case 0:
         throw new InvalidAttributeValueException(var1.getID() + "has no values");
      case 1:
         return this.writeQDString(var1);
      default:
         StringBuffer var2 = new StringBuffer(var1.getID());
         var2.append(' ');
         var2.append('(');
         NamingEnumeration var3 = var1.getAll();

         while(var3.hasMore()) {
            var2.append(' ');
            var2.append('\'');
            var2.append((String)var3.next());
            var2.append('\'');
            var2.append(' ');
         }

         var2.append(')');
         var2.append(' ');
         return var2.toString();
      }
   }

   private final String writeOIDs(Attribute var1) throws NamingException {
      switch(var1.size()) {
      case 0:
         throw new InvalidAttributeValueException(var1.getID() + "has no values");
      case 1:
         if (!this.netscapeBug) {
            return this.writeWOID(var1);
         }
      default:
         StringBuffer var2 = new StringBuffer(var1.getID());
         var2.append(' ');
         var2.append('(');
         NamingEnumeration var3 = var1.getAll();
         var2.append(' ');
         var2.append(var3.next());

         while(var3.hasMore()) {
            var2.append(' ');
            var2.append('$');
            var2.append(' ');
            var2.append((String)var3.next());
         }

         var2.append(' ');
         var2.append(')');
         var2.append(' ');
         return var2.toString();
      }
   }

   private final String writeBoolean(Attribute var1) throws NamingException {
      return var1.getID() + ' ';
   }

   final Attribute stringifyObjDesc(Attributes var1) throws NamingException {
      BasicAttribute var2 = new BasicAttribute("objectClasses");
      var2.add(this.classDef2ObjectDesc(var1));
      return var2;
   }

   final Attribute stringifyAttrDesc(Attributes var1) throws NamingException {
      BasicAttribute var2 = new BasicAttribute("attributeTypes");
      var2.add(this.attrDef2AttrDesc(var1));
      return var2;
   }

   final Attribute stringifySyntaxDesc(Attributes var1) throws NamingException {
      BasicAttribute var2 = new BasicAttribute("ldapSyntaxes");
      var2.add(this.syntaxDef2SyntaxDesc(var1));
      return var2;
   }

   final Attribute stringifyMatchRuleDesc(Attributes var1) throws NamingException {
      BasicAttribute var2 = new BasicAttribute("matchingRules");
      var2.add(this.matchRuleDef2MatchRuleDesc(var1));
      return var2;
   }
}
