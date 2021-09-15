package javax.swing.plaf.synth;

import java.awt.Graphics;
import javax.swing.JComponent;

public interface SynthUI extends SynthConstants {
   SynthContext getContext(JComponent var1);

   void paintBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6);
}
