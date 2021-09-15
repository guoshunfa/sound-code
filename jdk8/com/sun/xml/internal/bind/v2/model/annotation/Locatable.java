package com.sun.xml.internal.bind.v2.model.annotation;

import com.sun.xml.internal.bind.v2.runtime.Location;

public interface Locatable {
   Locatable getUpstream();

   Location getLocation();
}
