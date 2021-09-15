package javax.swing.plaf.synth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.PatternSyntaxException;
import javax.swing.JComponent;
import javax.swing.plaf.FontUIResource;
import sun.swing.BakedArrayList;
import sun.swing.plaf.synth.DefaultSynthStyle;
import sun.swing.plaf.synth.StyleAssociation;

class DefaultSynthStyleFactory extends SynthStyleFactory {
   public static final int NAME = 0;
   public static final int REGION = 1;
   private List<StyleAssociation> _styles = new ArrayList();
   private BakedArrayList _tmpList = new BakedArrayList(5);
   private Map<BakedArrayList, SynthStyle> _resolvedStyles = new HashMap();
   private SynthStyle _defaultStyle;

   public synchronized void addStyle(DefaultSynthStyle var1, String var2, int var3) throws PatternSyntaxException {
      if (var2 == null) {
         var2 = ".*";
      }

      if (var3 == 0) {
         this._styles.add(StyleAssociation.createStyleAssociation(var2, var1, var3));
      } else if (var3 == 1) {
         this._styles.add(StyleAssociation.createStyleAssociation(var2.toLowerCase(), var1, var3));
      }

   }

   public synchronized SynthStyle getStyle(JComponent var1, Region var2) {
      BakedArrayList var3 = this._tmpList;
      var3.clear();
      this.getMatchingStyles(var3, var1, var2);
      if (var3.size() == 0) {
         return this.getDefaultStyle();
      } else {
         var3.cacheHashCode();
         SynthStyle var4 = this.getCachedStyle(var3);
         if (var4 == null) {
            var4 = this.mergeStyles(var3);
            if (var4 != null) {
               this.cacheStyle(var3, var4);
            }
         }

         return var4;
      }
   }

   private SynthStyle getDefaultStyle() {
      if (this._defaultStyle == null) {
         this._defaultStyle = new DefaultSynthStyle();
         ((DefaultSynthStyle)this._defaultStyle).setFont(new FontUIResource("Dialog", 0, 12));
      }

      return this._defaultStyle;
   }

   private void getMatchingStyles(List var1, JComponent var2, Region var3) {
      String var4 = var3.getLowerCaseName();
      String var5 = var2.getName();
      if (var5 == null) {
         var5 = "";
      }

      for(int var6 = this._styles.size() - 1; var6 >= 0; --var6) {
         StyleAssociation var7 = (StyleAssociation)this._styles.get(var6);
         String var8;
         if (var7.getID() == 0) {
            var8 = var5;
         } else {
            var8 = var4;
         }

         if (var7.matches(var8) && var1.indexOf(var7.getStyle()) == -1) {
            var1.add(var7.getStyle());
         }
      }

   }

   private void cacheStyle(List var1, SynthStyle var2) {
      BakedArrayList var3 = new BakedArrayList(var1);
      this._resolvedStyles.put(var3, var2);
   }

   private SynthStyle getCachedStyle(List var1) {
      return var1.size() == 0 ? null : (SynthStyle)this._resolvedStyles.get(var1);
   }

   private SynthStyle mergeStyles(List var1) {
      int var2 = var1.size();
      if (var2 == 0) {
         return null;
      } else if (var2 == 1) {
         return (SynthStyle)((DefaultSynthStyle)var1.get(0)).clone();
      } else {
         DefaultSynthStyle var3 = (DefaultSynthStyle)var1.get(var2 - 1);
         var3 = (DefaultSynthStyle)var3.clone();

         for(int var4 = var2 - 2; var4 >= 0; --var4) {
            var3 = ((DefaultSynthStyle)var1.get(var4)).addTo(var3);
         }

         return var3;
      }
   }
}
