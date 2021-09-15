package com.sun.org.apache.xml.internal.security;

import com.sun.org.apache.xml.internal.security.algorithms.JCEMapper;
import com.sun.org.apache.xml.internal.security.algorithms.SignatureAlgorithm;
import com.sun.org.apache.xml.internal.security.c14n.Canonicalizer;
import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import com.sun.org.apache.xml.internal.security.keys.keyresolver.KeyResolver;
import com.sun.org.apache.xml.internal.security.transforms.Transform;
import com.sun.org.apache.xml.internal.security.utils.ElementProxy;
import com.sun.org.apache.xml.internal.security.utils.I18n;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import com.sun.org.apache.xml.internal.security.utils.resolver.ResourceResolver;
import java.io.InputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class Init {
   public static final String CONF_NS = "http://www.xmlsecurity.org/NS/#configuration";
   private static Logger log = Logger.getLogger(Init.class.getName());
   private static boolean alreadyInitialized = false;

   public static final synchronized boolean isInitialized() {
      return alreadyInitialized;
   }

   public static synchronized void init() {
      if (!alreadyInitialized) {
         InputStream var0 = (InputStream)AccessController.doPrivileged(new PrivilegedAction<InputStream>() {
            public InputStream run() {
               String var1 = System.getProperty("com.sun.org.apache.xml.internal.security.resource.config");
               return var1 == null ? null : this.getClass().getResourceAsStream(var1);
            }
         });
         if (var0 == null) {
            dynamicInit();
         } else {
            fileInit(var0);
         }

         alreadyInitialized = true;
      }
   }

   private static void dynamicInit() {
      I18n.init("en", "US");
      if (log.isLoggable(Level.FINE)) {
         log.log(Level.FINE, "Registering default algorithms");
      }

      try {
         AccessController.doPrivileged(new PrivilegedExceptionAction<Void>() {
            public Void run() throws XMLSecurityException {
               ElementProxy.registerDefaultPrefixes();
               Transform.registerDefaultAlgorithms();
               SignatureAlgorithm.registerDefaultAlgorithms();
               JCEMapper.registerDefaultAlgorithms();
               Canonicalizer.registerDefaultAlgorithms();
               ResourceResolver.registerDefaultResolvers();
               KeyResolver.registerDefaultResolvers();
               return null;
            }
         });
      } catch (PrivilegedActionException var2) {
         XMLSecurityException var1 = (XMLSecurityException)var2.getException();
         log.log(Level.SEVERE, (String)var1.getMessage(), (Throwable)var1);
         var1.printStackTrace();
      }

   }

   private static void fileInit(InputStream var0) {
      try {
         DocumentBuilderFactory var1 = DocumentBuilderFactory.newInstance();
         var1.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", Boolean.TRUE);
         var1.setNamespaceAware(true);
         var1.setValidating(false);
         DocumentBuilder var2 = var1.newDocumentBuilder();
         Document var3 = var2.parse(var0);

         Node var4;
         for(var4 = var3.getFirstChild(); var4 != null && !"Configuration".equals(var4.getLocalName()); var4 = var4.getNextSibling()) {
         }

         if (var4 == null) {
            log.log(Level.SEVERE, "Error in reading configuration file - Configuration element not found");
            return;
         }

         for(Node var5 = var4.getFirstChild(); var5 != null; var5 = var5.getNextSibling()) {
            if (1 == var5.getNodeType()) {
               String var6 = var5.getLocalName();
               String var10;
               String var11;
               if (var6.equals("ResourceBundles")) {
                  Element var7 = (Element)var5;
                  Attr var8 = var7.getAttributeNode("defaultLanguageCode");
                  Attr var9 = var7.getAttributeNode("defaultCountryCode");
                  var10 = var8 == null ? null : var8.getNodeValue();
                  var11 = var9 == null ? null : var9.getNodeValue();
                  I18n.init(var10, var11);
               }

               Object[] var12;
               Element[] var19;
               int var21;
               String var22;
               if (var6.equals("CanonicalizationMethods")) {
                  var19 = XMLUtils.selectNodes(var5.getFirstChild(), "http://www.xmlsecurity.org/NS/#configuration", "CanonicalizationMethod");

                  for(var21 = 0; var21 < var19.length; ++var21) {
                     var22 = var19[var21].getAttributeNS((String)null, "URI");
                     var10 = var19[var21].getAttributeNS((String)null, "JAVACLASS");

                     try {
                        Canonicalizer.register(var22, var10);
                        if (log.isLoggable(Level.FINE)) {
                           log.log(Level.FINE, "Canonicalizer.register(" + var22 + ", " + var10 + ")");
                        }
                     } catch (ClassNotFoundException var17) {
                        var12 = new Object[]{var22, var10};
                        log.log(Level.SEVERE, I18n.translate("algorithm.classDoesNotExist", var12));
                     }
                  }
               }

               if (var6.equals("TransformAlgorithms")) {
                  var19 = XMLUtils.selectNodes(var5.getFirstChild(), "http://www.xmlsecurity.org/NS/#configuration", "TransformAlgorithm");

                  for(var21 = 0; var21 < var19.length; ++var21) {
                     var22 = var19[var21].getAttributeNS((String)null, "URI");
                     var10 = var19[var21].getAttributeNS((String)null, "JAVACLASS");

                     try {
                        Transform.register(var22, var10);
                        if (log.isLoggable(Level.FINE)) {
                           log.log(Level.FINE, "Transform.register(" + var22 + ", " + var10 + ")");
                        }
                     } catch (ClassNotFoundException var15) {
                        var12 = new Object[]{var22, var10};
                        log.log(Level.SEVERE, I18n.translate("algorithm.classDoesNotExist", var12));
                     } catch (NoClassDefFoundError var16) {
                        log.log(Level.WARNING, "Not able to found dependencies for algorithm, I'll keep working.");
                     }
                  }
               }

               int var24;
               if ("JCEAlgorithmMappings".equals(var6)) {
                  Node var20 = ((Element)var5).getElementsByTagName("Algorithms").item(0);
                  if (var20 != null) {
                     Element[] var23 = XMLUtils.selectNodes(var20.getFirstChild(), "http://www.xmlsecurity.org/NS/#configuration", "Algorithm");

                     for(var24 = 0; var24 < var23.length; ++var24) {
                        Element var25 = var23[var24];
                        var11 = var25.getAttribute("URI");
                        JCEMapper.register(var11, new JCEMapper.Algorithm(var25));
                     }
                  }
               }

               if (var6.equals("SignatureAlgorithms")) {
                  var19 = XMLUtils.selectNodes(var5.getFirstChild(), "http://www.xmlsecurity.org/NS/#configuration", "SignatureAlgorithm");

                  for(var21 = 0; var21 < var19.length; ++var21) {
                     var22 = var19[var21].getAttributeNS((String)null, "URI");
                     var10 = var19[var21].getAttributeNS((String)null, "JAVACLASS");

                     try {
                        SignatureAlgorithm.register(var22, var10);
                        if (log.isLoggable(Level.FINE)) {
                           log.log(Level.FINE, "SignatureAlgorithm.register(" + var22 + ", " + var10 + ")");
                        }
                     } catch (ClassNotFoundException var14) {
                        var12 = new Object[]{var22, var10};
                        log.log(Level.SEVERE, I18n.translate("algorithm.classDoesNotExist", var12));
                     }
                  }
               }

               if (var6.equals("ResourceResolvers")) {
                  var19 = XMLUtils.selectNodes(var5.getFirstChild(), "http://www.xmlsecurity.org/NS/#configuration", "Resolver");

                  for(var21 = 0; var21 < var19.length; ++var21) {
                     var22 = var19[var21].getAttributeNS((String)null, "JAVACLASS");
                     var10 = var19[var21].getAttributeNS((String)null, "DESCRIPTION");
                     if (var10 != null && var10.length() > 0) {
                        if (log.isLoggable(Level.FINE)) {
                           log.log(Level.FINE, "Register Resolver: " + var22 + ": " + var10);
                        }
                     } else if (log.isLoggable(Level.FINE)) {
                        log.log(Level.FINE, "Register Resolver: " + var22 + ": For unknown purposes");
                     }

                     try {
                        ResourceResolver.register(var22);
                     } catch (Throwable var13) {
                        log.log(Level.WARNING, "Cannot register:" + var22 + " perhaps some needed jars are not installed", var13);
                     }
                  }
               }

               if (var6.equals("KeyResolver")) {
                  var19 = XMLUtils.selectNodes(var5.getFirstChild(), "http://www.xmlsecurity.org/NS/#configuration", "Resolver");
                  ArrayList var26 = new ArrayList(var19.length);

                  for(var24 = 0; var24 < var19.length; ++var24) {
                     var10 = var19[var24].getAttributeNS((String)null, "JAVACLASS");
                     var11 = var19[var24].getAttributeNS((String)null, "DESCRIPTION");
                     if (var11 != null && var11.length() > 0) {
                        if (log.isLoggable(Level.FINE)) {
                           log.log(Level.FINE, "Register Resolver: " + var10 + ": " + var11);
                        }
                     } else if (log.isLoggable(Level.FINE)) {
                        log.log(Level.FINE, "Register Resolver: " + var10 + ": For unknown purposes");
                     }

                     var26.add(var10);
                  }

                  KeyResolver.registerClassNames(var26);
               }

               if (var6.equals("PrefixMappings")) {
                  if (log.isLoggable(Level.FINE)) {
                     log.log(Level.FINE, "Now I try to bind prefixes:");
                  }

                  var19 = XMLUtils.selectNodes(var5.getFirstChild(), "http://www.xmlsecurity.org/NS/#configuration", "PrefixMapping");

                  for(var21 = 0; var21 < var19.length; ++var21) {
                     var22 = var19[var21].getAttributeNS((String)null, "namespace");
                     var10 = var19[var21].getAttributeNS((String)null, "prefix");
                     if (log.isLoggable(Level.FINE)) {
                        log.log(Level.FINE, "Now I try to bind " + var10 + " to " + var22);
                     }

                     ElementProxy.setDefaultPrefix(var22, var10);
                  }
               }
            }
         }
      } catch (Exception var18) {
         log.log(Level.SEVERE, (String)"Bad: ", (Throwable)var18);
         var18.printStackTrace();
      }

   }
}
