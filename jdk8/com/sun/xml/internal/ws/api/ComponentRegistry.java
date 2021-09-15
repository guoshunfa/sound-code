package com.sun.xml.internal.ws.api;

import com.sun.istack.internal.NotNull;
import java.util.Set;

public interface ComponentRegistry extends Component {
   @NotNull
   Set<Component> getComponents();
}
