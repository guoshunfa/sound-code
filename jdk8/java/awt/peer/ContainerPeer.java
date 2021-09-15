package java.awt.peer;

import java.awt.Insets;

public interface ContainerPeer extends ComponentPeer {
   Insets getInsets();

   void beginValidate();

   void endValidate();

   void beginLayout();

   void endLayout();
}
