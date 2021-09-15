package com.sun.corba.se.impl.orbutil.graph;

import java.util.Set;

public interface Graph extends Set {
   NodeData getNodeData(Node var1);

   Set getRoots();
}
