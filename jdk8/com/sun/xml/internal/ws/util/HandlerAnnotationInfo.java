package com.sun.xml.internal.ws.util;

import java.util.List;
import java.util.Set;
import javax.xml.ws.handler.Handler;

public class HandlerAnnotationInfo {
   private List<Handler> handlers;
   private Set<String> roles;

   public List<Handler> getHandlers() {
      return this.handlers;
   }

   public void setHandlers(List<Handler> handlers) {
      this.handlers = handlers;
   }

   public Set<String> getRoles() {
      return this.roles;
   }

   public void setRoles(Set<String> roles) {
      this.roles = roles;
   }
}
