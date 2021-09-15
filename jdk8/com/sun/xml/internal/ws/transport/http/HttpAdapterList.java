package com.sun.xml.internal.ws.transport.http;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.api.server.PortAddressResolver;
import com.sun.xml.internal.ws.api.server.WSEndpoint;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.xml.namespace.QName;

public abstract class HttpAdapterList<T extends HttpAdapter> extends AbstractList<T> implements DeploymentDescriptorParser.AdapterFactory<T> {
   private final List<T> adapters = new ArrayList();
   private final Map<HttpAdapterList.PortInfo, String> addressMap = new HashMap();

   public T createAdapter(String name, String urlPattern, WSEndpoint<?> endpoint) {
      T t = this.createHttpAdapter(name, urlPattern, endpoint);
      this.adapters.add(t);
      WSDLPort port = endpoint.getPort();
      if (port != null) {
         HttpAdapterList.PortInfo portInfo = new HttpAdapterList.PortInfo(port.getOwner().getName(), port.getName().getLocalPart(), endpoint.getImplementationClass());
         this.addressMap.put(portInfo, this.getValidPath(urlPattern));
      }

      return t;
   }

   protected abstract T createHttpAdapter(String var1, String var2, WSEndpoint<?> var3);

   private String getValidPath(@NotNull String urlPattern) {
      return urlPattern.endsWith("/*") ? urlPattern.substring(0, urlPattern.length() - 2) : urlPattern;
   }

   public PortAddressResolver createPortAddressResolver(final String baseAddress, final Class<?> endpointImpl) {
      return new PortAddressResolver() {
         public String getAddressFor(@NotNull QName serviceName, @NotNull String portName) {
            String urlPattern = (String)HttpAdapterList.this.addressMap.get(new HttpAdapterList.PortInfo(serviceName, portName, endpointImpl));
            if (urlPattern == null) {
               Iterator var4 = HttpAdapterList.this.addressMap.entrySet().iterator();

               while(var4.hasNext()) {
                  Map.Entry<HttpAdapterList.PortInfo, String> e = (Map.Entry)var4.next();
                  if (serviceName.equals(((HttpAdapterList.PortInfo)e.getKey()).serviceName) && portName.equals(((HttpAdapterList.PortInfo)e.getKey()).portName)) {
                     urlPattern = (String)e.getValue();
                     break;
                  }
               }
            }

            return urlPattern == null ? null : baseAddress + urlPattern;
         }
      };
   }

   public T get(int index) {
      return (HttpAdapter)this.adapters.get(index);
   }

   public int size() {
      return this.adapters.size();
   }

   private static class PortInfo {
      private final QName serviceName;
      private final String portName;
      private final Class<?> implClass;

      PortInfo(@NotNull QName serviceName, @NotNull String portName, Class<?> implClass) {
         this.serviceName = serviceName;
         this.portName = portName;
         this.implClass = implClass;
      }

      public boolean equals(Object portInfo) {
         if (!(portInfo instanceof HttpAdapterList.PortInfo)) {
            return false;
         } else {
            HttpAdapterList.PortInfo that = (HttpAdapterList.PortInfo)portInfo;
            if (this.implClass == null) {
               return this.serviceName.equals(that.serviceName) && this.portName.equals(that.portName) && that.implClass == null;
            } else {
               return this.serviceName.equals(that.serviceName) && this.portName.equals(that.portName) && this.implClass.equals(that.implClass);
            }
         }
      }

      public int hashCode() {
         int retVal = this.serviceName.hashCode() + this.portName.hashCode();
         return this.implClass != null ? retVal + this.implClass.hashCode() : retVal;
      }
   }
}
