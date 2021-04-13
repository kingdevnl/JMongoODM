package nl.kingdev.jmongoodm.utils;

import org.bson.Document;

public class DocumentUtils {
    public static Object getValue(Document document, String name) {

        if (name.contains(".")) {
            String[] split = name.split("\\.");
            Document doc = document;
            for (int i = 0; i < split.length - 1; i++) {
                doc = (Document) doc.get(split[i]);
            }
            return doc.get(split[split.length - 1]);
        }

        return document.get(name);
    }
}
