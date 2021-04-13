package nl.kingdev.jmongoodm.mapping.impl;

import nl.kingdev.jmongoodm.mapping.IMapper;
import nl.kingdev.jmongoodm.utils.DocumentUtils;
import nl.kingdev.jmongoodm.utils.NameUtils;
import org.bson.Document;

import java.lang.reflect.Field;

public class PrimitiveMapper implements IMapper {


    @Override
    public void mapFromDocument(Object ownerInstance, Field field, Document document) throws Exception {
        Object value = DocumentUtils.getValue(document, NameUtils.getColumnName(field));
        if (value != null) {
            field.set(ownerInstance, value);
        }
    }

    @Override
    public void mapToDocument(Object ownerInstance, Field field, Document document) throws Exception {
        Object value = field.get(ownerInstance);
        if (value != null) {
            document.append(NameUtils.getColumnName(field), value);
        }
    }
}
