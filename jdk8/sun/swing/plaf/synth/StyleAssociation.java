package sun.swing.plaf.synth;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import javax.swing.plaf.synth.SynthStyle;

public class StyleAssociation {
   private SynthStyle _style;
   private Pattern _pattern;
   private Matcher _matcher;
   private int _id;

   public static StyleAssociation createStyleAssociation(String var0, SynthStyle var1) throws PatternSyntaxException {
      return createStyleAssociation(var0, var1, 0);
   }

   public static StyleAssociation createStyleAssociation(String var0, SynthStyle var1, int var2) throws PatternSyntaxException {
      return new StyleAssociation(var0, var1, var2);
   }

   private StyleAssociation(String var1, SynthStyle var2, int var3) throws PatternSyntaxException {
      this._style = var2;
      this._pattern = Pattern.compile(var1);
      this._id = var3;
   }

   public int getID() {
      return this._id;
   }

   public synchronized boolean matches(CharSequence var1) {
      if (this._matcher == null) {
         this._matcher = this._pattern.matcher(var1);
      } else {
         this._matcher.reset(var1);
      }

      return this._matcher.matches();
   }

   public String getText() {
      return this._pattern.pattern();
   }

   public SynthStyle getStyle() {
      return this._style;
   }
}
