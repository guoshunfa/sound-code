package javax.naming;

import java.util.Hashtable;

public interface Context {
   String INITIAL_CONTEXT_FACTORY = "java.naming.factory.initial";
   String OBJECT_FACTORIES = "java.naming.factory.object";
   String STATE_FACTORIES = "java.naming.factory.state";
   String URL_PKG_PREFIXES = "java.naming.factory.url.pkgs";
   String PROVIDER_URL = "java.naming.provider.url";
   String DNS_URL = "java.naming.dns.url";
   String AUTHORITATIVE = "java.naming.authoritative";
   String BATCHSIZE = "java.naming.batchsize";
   String REFERRAL = "java.naming.referral";
   String SECURITY_PROTOCOL = "java.naming.security.protocol";
   String SECURITY_AUTHENTICATION = "java.naming.security.authentication";
   String SECURITY_PRINCIPAL = "java.naming.security.principal";
   String SECURITY_CREDENTIALS = "java.naming.security.credentials";
   String LANGUAGE = "java.naming.language";
   String APPLET = "java.naming.applet";

   Object lookup(Name var1) throws NamingException;

   Object lookup(String var1) throws NamingException;

   void bind(Name var1, Object var2) throws NamingException;

   void bind(String var1, Object var2) throws NamingException;

   void rebind(Name var1, Object var2) throws NamingException;

   void rebind(String var1, Object var2) throws NamingException;

   void unbind(Name var1) throws NamingException;

   void unbind(String var1) throws NamingException;

   void rename(Name var1, Name var2) throws NamingException;

   void rename(String var1, String var2) throws NamingException;

   NamingEnumeration<NameClassPair> list(Name var1) throws NamingException;

   NamingEnumeration<NameClassPair> list(String var1) throws NamingException;

   NamingEnumeration<Binding> listBindings(Name var1) throws NamingException;

   NamingEnumeration<Binding> listBindings(String var1) throws NamingException;

   void destroySubcontext(Name var1) throws NamingException;

   void destroySubcontext(String var1) throws NamingException;

   Context createSubcontext(Name var1) throws NamingException;

   Context createSubcontext(String var1) throws NamingException;

   Object lookupLink(Name var1) throws NamingException;

   Object lookupLink(String var1) throws NamingException;

   NameParser getNameParser(Name var1) throws NamingException;

   NameParser getNameParser(String var1) throws NamingException;

   Name composeName(Name var1, Name var2) throws NamingException;

   String composeName(String var1, String var2) throws NamingException;

   Object addToEnvironment(String var1, Object var2) throws NamingException;

   Object removeFromEnvironment(String var1) throws NamingException;

   Hashtable<?, ?> getEnvironment() throws NamingException;

   void close() throws NamingException;

   String getNameInNamespace() throws NamingException;
}
