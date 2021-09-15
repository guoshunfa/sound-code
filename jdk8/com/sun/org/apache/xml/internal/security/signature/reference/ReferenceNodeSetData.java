package com.sun.org.apache.xml.internal.security.signature.reference;

import java.util.Iterator;
import org.w3c.dom.Node;

public interface ReferenceNodeSetData extends ReferenceData {
   Iterator<Node> iterator();
}
