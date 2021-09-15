package com.sun.jmx.remote.internal;

import java.io.IOException;
import java.rmi.MarshalledObject;

public interface Unmarshal {
   Object get(MarshalledObject<?> var1) throws IOException, ClassNotFoundException;
}
