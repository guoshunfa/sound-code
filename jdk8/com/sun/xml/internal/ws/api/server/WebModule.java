package com.sun.xml.internal.ws.api.server;

import com.sun.istack.internal.NotNull;

public abstract class WebModule extends Module {
   @NotNull
   public abstract String getContextPath();
}
