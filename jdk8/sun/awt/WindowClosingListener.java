package sun.awt;

import java.awt.event.WindowEvent;

public interface WindowClosingListener {
   RuntimeException windowClosingNotify(WindowEvent var1);

   RuntimeException windowClosingDelivered(WindowEvent var1);
}
