package java.awt;

import java.io.Serializable;

class FocusManager implements Serializable {
   Container focusRoot;
   Component focusOwner;
   static final long serialVersionUID = 2491878825643557906L;
}
