package com.sun.java.browser.net;

public interface ProxyInfo {
   String getHost();

   int getPort();

   boolean isSocks();
}
