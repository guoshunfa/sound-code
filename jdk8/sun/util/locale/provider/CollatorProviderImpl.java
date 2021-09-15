package sun.util.locale.provider;

import java.text.Collator;
import java.text.ParseException;
import java.text.RuleBasedCollator;
import java.text.spi.CollatorProvider;
import java.util.Locale;
import java.util.Set;

public class CollatorProviderImpl extends CollatorProvider implements AvailableLanguageTags {
   private final LocaleProviderAdapter.Type type;
   private final Set<String> langtags;

   public CollatorProviderImpl(LocaleProviderAdapter.Type var1, Set<String> var2) {
      this.type = var1;
      this.langtags = var2;
   }

   public Locale[] getAvailableLocales() {
      return LocaleProviderAdapter.toLocaleArray(this.langtags);
   }

   public boolean isSupportedLocale(Locale var1) {
      return LocaleProviderAdapter.isSupportedLocale(var1, this.type, this.langtags);
   }

   public Collator getInstance(Locale var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         RuleBasedCollator var2 = null;
         String var3 = LocaleProviderAdapter.forType(this.type).getLocaleResources(var1).getCollationData();

         try {
            var2 = new RuleBasedCollator("='\u200b'=\u200c=\u200d=\u200e=\u200f=\u0000 =\u0001 =\u0002 =\u0003 =\u0004=\u0005 =\u0006 =\u0007 =\b ='\t'='\u000b' =\u000e=\u000f ='\u0010' =\u0011 =\u0012 =\u0013=\u0014 =\u0015 =\u0016 =\u0017 =\u0018=\u0019 =\u001a =\u001b =\u001c =\u001d=\u001e =\u001f =\u007f=\u0080 =\u0081 =\u0082 =\u0083 =\u0084 =\u0085=\u0086 =\u0087 =\u0088 =\u0089 =\u008a =\u008b=\u008c =\u008d =\u008e =\u008f =\u0090 =\u0091=\u0092 =\u0093 =\u0094 =\u0095 =\u0096 =\u0097=\u0098 =\u0099 =\u009a =\u009b =\u009c =\u009d=\u009e =\u009f;' ';' ';' ';' ';' ';' ';' ';' ';' ';' ';' ';' ';' ';'　';'\ufeff';'\r' ;'\t' ;'\n';'\f';'\u000b';́;̀;̆;̂;̌;̊;̍;̈;̋;̃;̇;̄;̷;̧;̨;̣;̲;̅;̉;̎;̏;̐;̑;̒;̓;̔;̕;̖;̗;̘;̙;̚;̛;̜;̝;̞;̟;̠;̡;̢;̤;̥;̦;̩;̪;̫;̬;̭;̮;̯;̰;̱;̳;̴;̵;̶;̸;̹;̺;̻;̼;̽;̾;̿;͂;̈́;ͅ;͠;͡;҃;҄;҅;҆;⃐;⃑;⃒;⃓;⃔;⃕;⃖;⃗;⃘;⃙;⃚;⃛;⃜;⃝;⃞;⃟;⃠;⃡,'-';\u00ad;‐;‑;‒;–;—;―;−<'_'<¯<','<';'<':'<'!'<¡<'?'<¿<'/'<'.'<´<'`'<'^'<¨<'~'<·<¸<'''<'\"'<«<»<'('<')'<'['<']'<'{'<'}'<§<¶<©<®<'@'<¤<฿<¢<₡<₢<'$'<₫<€<₣<₤<₥<₦<₧<£<₨<₪<₩<¥<'*'<'\\'<'&'<'#'<'%'<'+'<±<÷<×<'<'<'='<'>'<¬<'|'<¦<°<µ<0<1<2<3<4<5<6<7<8<9<¼<½<¾<a,A<b,B<c,C<d,D<ð,Ð<e,E<f,F<g,G<h,H<i,I<j,J<k,K<l,L<m,M<n,N<o,O<p,P<q,Q<r,R<s, S & SS,ß<t,T& TH, Þ &TH, þ <u,U<v,V<w,W<x,X<y,Y<z,Z&AE,Æ&AE,æ&OE,Œ&OE,œ" + var3);
         } catch (ParseException var7) {
            try {
               var2 = new RuleBasedCollator("='\u200b'=\u200c=\u200d=\u200e=\u200f=\u0000 =\u0001 =\u0002 =\u0003 =\u0004=\u0005 =\u0006 =\u0007 =\b ='\t'='\u000b' =\u000e=\u000f ='\u0010' =\u0011 =\u0012 =\u0013=\u0014 =\u0015 =\u0016 =\u0017 =\u0018=\u0019 =\u001a =\u001b =\u001c =\u001d=\u001e =\u001f =\u007f=\u0080 =\u0081 =\u0082 =\u0083 =\u0084 =\u0085=\u0086 =\u0087 =\u0088 =\u0089 =\u008a =\u008b=\u008c =\u008d =\u008e =\u008f =\u0090 =\u0091=\u0092 =\u0093 =\u0094 =\u0095 =\u0096 =\u0097=\u0098 =\u0099 =\u009a =\u009b =\u009c =\u009d=\u009e =\u009f;' ';' ';' ';' ';' ';' ';' ';' ';' ';' ';' ';' ';' ';'　';'\ufeff';'\r' ;'\t' ;'\n';'\f';'\u000b';́;̀;̆;̂;̌;̊;̍;̈;̋;̃;̇;̄;̷;̧;̨;̣;̲;̅;̉;̎;̏;̐;̑;̒;̓;̔;̕;̖;̗;̘;̙;̚;̛;̜;̝;̞;̟;̠;̡;̢;̤;̥;̦;̩;̪;̫;̬;̭;̮;̯;̰;̱;̳;̴;̵;̶;̸;̹;̺;̻;̼;̽;̾;̿;͂;̈́;ͅ;͠;͡;҃;҄;҅;҆;⃐;⃑;⃒;⃓;⃔;⃕;⃖;⃗;⃘;⃙;⃚;⃛;⃜;⃝;⃞;⃟;⃠;⃡,'-';\u00ad;‐;‑;‒;–;—;―;−<'_'<¯<','<';'<':'<'!'<¡<'?'<¿<'/'<'.'<´<'`'<'^'<¨<'~'<·<¸<'''<'\"'<«<»<'('<')'<'['<']'<'{'<'}'<§<¶<©<®<'@'<¤<฿<¢<₡<₢<'$'<₫<€<₣<₤<₥<₦<₧<£<₨<₪<₩<¥<'*'<'\\'<'&'<'#'<'%'<'+'<±<÷<×<'<'<'='<'>'<¬<'|'<¦<°<µ<0<1<2<3<4<5<6<7<8<9<¼<½<¾<a,A<b,B<c,C<d,D<ð,Ð<e,E<f,F<g,G<h,H<i,I<j,J<k,K<l,L<m,M<n,N<o,O<p,P<q,Q<r,R<s, S & SS,ß<t,T& TH, Þ &TH, þ <u,U<v,V<w,W<x,X<y,Y<z,Z&AE,Æ&AE,æ&OE,Œ&OE,œ");
            } catch (ParseException var6) {
               throw new InternalError(var6);
            }
         }

         var2.setDecomposition(0);
         return (Collator)var2.clone();
      }
   }

   public Set<String> getAvailableLanguageTags() {
      return this.langtags;
   }
}
