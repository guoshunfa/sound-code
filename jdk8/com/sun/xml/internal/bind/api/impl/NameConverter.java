package com.sun.xml.internal.bind.api.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import javax.lang.model.SourceVersion;

public interface NameConverter {
   NameConverter standard = new NameConverter.Standard();
   NameConverter jaxrpcCompatible = new NameConverter.Standard() {
      protected boolean isPunct(char c) {
         return c == '.' || c == '-' || c == ';' || c == 183 || c == 903 || c == 1757 || c == 1758;
      }

      protected boolean isLetter(char c) {
         return super.isLetter(c) || c == '_';
      }

      protected int classify(char c0) {
         return c0 == '_' ? 2 : super.classify(c0);
      }
   };
   NameConverter smart = new NameConverter.Standard() {
      public String toConstantName(String token) {
         String name = super.toConstantName(token);
         return !SourceVersion.isKeyword(name) ? name : '_' + name;
      }
   };

   String toClassName(String var1);

   String toInterfaceName(String var1);

   String toPropertyName(String var1);

   String toConstantName(String var1);

   String toVariableName(String var1);

   String toPackageName(String var1);

   public static class Standard extends NameUtil implements NameConverter {
      public String toClassName(String s) {
         return this.toMixedCaseName(this.toWordList(s), true);
      }

      public String toVariableName(String s) {
         return this.toMixedCaseName(this.toWordList(s), false);
      }

      public String toInterfaceName(String token) {
         return this.toClassName(token);
      }

      public String toPropertyName(String s) {
         String prop = this.toClassName(s);
         if (prop.equals("Class")) {
            prop = "Clazz";
         }

         return prop;
      }

      public String toConstantName(String token) {
         return super.toConstantName(token);
      }

      public String toPackageName(String nsUri) {
         int idx = nsUri.indexOf(58);
         String scheme = "";
         if (idx >= 0) {
            scheme = nsUri.substring(0, idx);
            if (scheme.equalsIgnoreCase("http") || scheme.equalsIgnoreCase("urn")) {
               nsUri = nsUri.substring(idx + 1);
            }
         }

         ArrayList<String> tokens = tokenize(nsUri, "/: ");
         if (tokens.size() == 0) {
            return null;
         } else {
            String domain;
            if (tokens.size() > 1) {
               domain = (String)tokens.get(tokens.size() - 1);
               idx = domain.lastIndexOf(46);
               if (idx > 0) {
                  domain = domain.substring(0, idx);
                  tokens.set(tokens.size() - 1, domain);
               }
            }

            domain = (String)tokens.get(0);
            idx = domain.indexOf(58);
            if (idx >= 0) {
               domain = domain.substring(0, idx);
            }

            ArrayList<String> r = reverse(tokenize(domain, scheme.equals("urn") ? ".-" : "."));
            if (((String)r.get(r.size() - 1)).equalsIgnoreCase("www")) {
               r.remove(r.size() - 1);
            }

            tokens.addAll(1, r);
            tokens.remove(0);

            for(int i = 0; i < tokens.size(); ++i) {
               String token = (String)tokens.get(i);
               token = removeIllegalIdentifierChars(token);
               if (SourceVersion.isKeyword(token.toLowerCase())) {
                  token = '_' + token;
               }

               tokens.set(i, token.toLowerCase());
            }

            return combine(tokens, '.');
         }
      }

      private static String removeIllegalIdentifierChars(String token) {
         StringBuilder newToken = new StringBuilder(token.length() + 1);

         for(int i = 0; i < token.length(); ++i) {
            char c = token.charAt(i);
            if (i == 0 && !Character.isJavaIdentifierStart(c)) {
               newToken.append('_');
            }

            if (!Character.isJavaIdentifierPart(c)) {
               newToken.append('_');
            } else {
               newToken.append(c);
            }
         }

         return newToken.toString();
      }

      private static ArrayList<String> tokenize(String str, String sep) {
         StringTokenizer tokens = new StringTokenizer(str, sep);
         ArrayList r = new ArrayList();

         while(tokens.hasMoreTokens()) {
            r.add(tokens.nextToken());
         }

         return r;
      }

      private static <T> ArrayList<T> reverse(List<T> a) {
         ArrayList<T> r = new ArrayList();

         for(int i = a.size() - 1; i >= 0; --i) {
            r.add(a.get(i));
         }

         return r;
      }

      private static String combine(List r, char sep) {
         StringBuilder buf = new StringBuilder(r.get(0).toString());

         for(int i = 1; i < r.size(); ++i) {
            buf.append(sep);
            buf.append(r.get(i));
         }

         return buf.toString();
      }
   }
}
