package javax.sql.rowset.spi;

import java.io.Writer;
import java.sql.SQLException;
import javax.sql.RowSetWriter;
import javax.sql.rowset.WebRowSet;

public interface XmlWriter extends RowSetWriter {
   void writeXML(WebRowSet var1, Writer var2) throws SQLException;
}
