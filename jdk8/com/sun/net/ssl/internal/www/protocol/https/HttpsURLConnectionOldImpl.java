package com.sun.net.ssl.internal.www.protocol.https;

import com.sun.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.Proxy;
import java.net.URL;
import java.security.Permission;
import java.security.cert.Certificate;
import java.util.List;
import java.util.Map;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.security.cert.X509Certificate;

public class HttpsURLConnectionOldImpl extends HttpsURLConnection {
   private DelegateHttpsURLConnection delegate;

   HttpsURLConnectionOldImpl(URL var1, Handler var2) throws IOException {
      this(var1, (Proxy)null, var2);
   }

   static URL checkURL(URL var0) throws IOException {
      if (var0 != null && var0.toExternalForm().indexOf(10) > -1) {
         throw new MalformedURLException("Illegal character in URL");
      } else {
         return var0;
      }
   }

   HttpsURLConnectionOldImpl(URL var1, Proxy var2, Handler var3) throws IOException {
      super(checkURL(var1));
      this.delegate = new DelegateHttpsURLConnection(this.url, var2, var3, this);
   }

   protected void setNewClient(URL var1) throws IOException {
      this.delegate.setNewClient(var1, false);
   }

   protected void setNewClient(URL var1, boolean var2) throws IOException {
      this.delegate.setNewClient(var1, var2);
   }

   protected void setProxiedClient(URL var1, String var2, int var3) throws IOException {
      this.delegate.setProxiedClient(var1, var2, var3);
   }

   protected void setProxiedClient(URL var1, String var2, int var3, boolean var4) throws IOException {
      this.delegate.setProxiedClient(var1, var2, var3, var4);
   }

   public void connect() throws IOException {
      this.delegate.connect();
   }

   protected boolean isConnected() {
      return this.delegate.isConnected();
   }

   protected void setConnected(boolean var1) {
      this.delegate.setConnected(var1);
   }

   public String getCipherSuite() {
      return this.delegate.getCipherSuite();
   }

   public Certificate[] getLocalCertificates() {
      return this.delegate.getLocalCertificates();
   }

   public Certificate[] getServerCertificates() throws SSLPeerUnverifiedException {
      return this.delegate.getServerCertificates();
   }

   public X509Certificate[] getServerCertificateChain() {
      try {
         return this.delegate.getServerCertificateChain();
      } catch (SSLPeerUnverifiedException var2) {
         return null;
      }
   }

   public synchronized OutputStream getOutputStream() throws IOException {
      return this.delegate.getOutputStream();
   }

   public synchronized InputStream getInputStream() throws IOException {
      return this.delegate.getInputStream();
   }

   public InputStream getErrorStream() {
      return this.delegate.getErrorStream();
   }

   public void disconnect() {
      this.delegate.disconnect();
   }

   public boolean usingProxy() {
      return this.delegate.usingProxy();
   }

   public Map<String, List<String>> getHeaderFields() {
      return this.delegate.getHeaderFields();
   }

   public String getHeaderField(String var1) {
      return this.delegate.getHeaderField(var1);
   }

   public String getHeaderField(int var1) {
      return this.delegate.getHeaderField(var1);
   }

   public String getHeaderFieldKey(int var1) {
      return this.delegate.getHeaderFieldKey(var1);
   }

   public void setRequestProperty(String var1, String var2) {
      this.delegate.setRequestProperty(var1, var2);
   }

   public void addRequestProperty(String var1, String var2) {
      this.delegate.addRequestProperty(var1, var2);
   }

   public int getResponseCode() throws IOException {
      return this.delegate.getResponseCode();
   }

   public String getRequestProperty(String var1) {
      return this.delegate.getRequestProperty(var1);
   }

   public Map<String, List<String>> getRequestProperties() {
      return this.delegate.getRequestProperties();
   }

   public void setInstanceFollowRedirects(boolean var1) {
      this.delegate.setInstanceFollowRedirects(var1);
   }

   public boolean getInstanceFollowRedirects() {
      return this.delegate.getInstanceFollowRedirects();
   }

   public void setRequestMethod(String var1) throws ProtocolException {
      this.delegate.setRequestMethod(var1);
   }

   public String getRequestMethod() {
      return this.delegate.getRequestMethod();
   }

   public String getResponseMessage() throws IOException {
      return this.delegate.getResponseMessage();
   }

   public long getHeaderFieldDate(String var1, long var2) {
      return this.delegate.getHeaderFieldDate(var1, var2);
   }

   public Permission getPermission() throws IOException {
      return this.delegate.getPermission();
   }

   public URL getURL() {
      return this.delegate.getURL();
   }

   public int getContentLength() {
      return this.delegate.getContentLength();
   }

   public long getContentLengthLong() {
      return this.delegate.getContentLengthLong();
   }

   public String getContentType() {
      return this.delegate.getContentType();
   }

   public String getContentEncoding() {
      return this.delegate.getContentEncoding();
   }

   public long getExpiration() {
      return this.delegate.getExpiration();
   }

   public long getDate() {
      return this.delegate.getDate();
   }

   public long getLastModified() {
      return this.delegate.getLastModified();
   }

   public int getHeaderFieldInt(String var1, int var2) {
      return this.delegate.getHeaderFieldInt(var1, var2);
   }

   public long getHeaderFieldLong(String var1, long var2) {
      return this.delegate.getHeaderFieldLong(var1, var2);
   }

   public Object getContent() throws IOException {
      return this.delegate.getContent();
   }

   public Object getContent(Class[] var1) throws IOException {
      return this.delegate.getContent(var1);
   }

   public String toString() {
      return this.delegate.toString();
   }

   public void setDoInput(boolean var1) {
      this.delegate.setDoInput(var1);
   }

   public boolean getDoInput() {
      return this.delegate.getDoInput();
   }

   public void setDoOutput(boolean var1) {
      this.delegate.setDoOutput(var1);
   }

   public boolean getDoOutput() {
      return this.delegate.getDoOutput();
   }

   public void setAllowUserInteraction(boolean var1) {
      this.delegate.setAllowUserInteraction(var1);
   }

   public boolean getAllowUserInteraction() {
      return this.delegate.getAllowUserInteraction();
   }

   public void setUseCaches(boolean var1) {
      this.delegate.setUseCaches(var1);
   }

   public boolean getUseCaches() {
      return this.delegate.getUseCaches();
   }

   public void setIfModifiedSince(long var1) {
      this.delegate.setIfModifiedSince(var1);
   }

   public long getIfModifiedSince() {
      return this.delegate.getIfModifiedSince();
   }

   public boolean getDefaultUseCaches() {
      return this.delegate.getDefaultUseCaches();
   }

   public void setDefaultUseCaches(boolean var1) {
      this.delegate.setDefaultUseCaches(var1);
   }

   protected void finalize() throws Throwable {
      this.delegate.dispose();
   }

   public boolean equals(Object var1) {
      return this.delegate.equals(var1);
   }

   public int hashCode() {
      return this.delegate.hashCode();
   }

   public void setConnectTimeout(int var1) {
      this.delegate.setConnectTimeout(var1);
   }

   public int getConnectTimeout() {
      return this.delegate.getConnectTimeout();
   }

   public void setReadTimeout(int var1) {
      this.delegate.setReadTimeout(var1);
   }

   public int getReadTimeout() {
      return this.delegate.getReadTimeout();
   }

   public void setFixedLengthStreamingMode(int var1) {
      this.delegate.setFixedLengthStreamingMode(var1);
   }

   public void setFixedLengthStreamingMode(long var1) {
      this.delegate.setFixedLengthStreamingMode(var1);
   }

   public void setChunkedStreamingMode(int var1) {
      this.delegate.setChunkedStreamingMode(var1);
   }
}
