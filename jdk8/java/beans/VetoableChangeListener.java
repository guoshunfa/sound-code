package java.beans;

import java.util.EventListener;

public interface VetoableChangeListener extends EventListener {
   void vetoableChange(PropertyChangeEvent var1) throws PropertyVetoException;
}
