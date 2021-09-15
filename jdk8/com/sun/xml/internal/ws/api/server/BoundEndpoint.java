package com.sun.xml.internal.ws.api.server;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.Component;
import java.net.URI;

public interface BoundEndpoint extends Component {
   @NotNull
   WSEndpoint getEndpoint();

   @NotNull
   URI getAddress();

   @NotNull
   URI getAddress(String var1);
}
