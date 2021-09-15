package javax.swing.text.rtf;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.StyledDocument;
import javax.swing.text.StyledEditorKit;

public class RTFEditorKit extends StyledEditorKit {
   public String getContentType() {
      return "text/rtf";
   }

   public void read(InputStream var1, Document var2, int var3) throws IOException, BadLocationException {
      if (var2 instanceof StyledDocument) {
         RTFReader var4 = new RTFReader((StyledDocument)var2);
         var4.readFromStream(var1);
         var4.close();
      } else {
         super.read(var1, var2, var3);
      }

   }

   public void write(OutputStream var1, Document var2, int var3, int var4) throws IOException, BadLocationException {
      RTFGenerator.writeDocument(var2, var1);
   }

   public void read(Reader var1, Document var2, int var3) throws IOException, BadLocationException {
      if (var2 instanceof StyledDocument) {
         RTFReader var4 = new RTFReader((StyledDocument)var2);
         var4.readFromReader(var1);
         var4.close();
      } else {
         super.read(var1, var2, var3);
      }

   }

   public void write(Writer var1, Document var2, int var3, int var4) throws IOException, BadLocationException {
      throw new IOException("RTF is an 8-bit format");
   }
}
