package ibf2022.batch1.project.server.utils;

import org.bson.Document;
import org.bson.types.Binary;


public class MongoUtils {

    public static Document toDocument(String fileName, byte[] pdfByte) {
        return new Document()
                .append("bill", fileName)
                .append("pdfDocumentInByte", new Binary(pdfByte));
    }

    public static byte[] getPdfByte(Document document) {
        Binary binaryObject = (Binary) document.get("pdfDocumentInByte");
        return binaryObject.getData();
    }
}
