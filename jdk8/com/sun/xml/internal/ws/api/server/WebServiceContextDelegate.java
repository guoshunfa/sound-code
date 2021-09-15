package com.sun.xml.internal.ws.api.server;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.message.Packet;
import java.security.Principal;

public interface WebServiceContextDelegate {
   Principal getUserPrincipal(@NotNull Packet var1);

   boolean isUserInRole(@NotNull Packet var1, String var2);

   @NotNull
   String getEPRAddress(@NotNull Packet var1, @NotNull WSEndpoint var2);

   @Nullable
   String getWSDLAddress(@NotNull Packet var1, @NotNull WSEndpoint var2);
}
