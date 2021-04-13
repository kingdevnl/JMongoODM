package nl.kingdev.jmongoodm.mapping.impl;

import nl.kingdev.jmongoodm.mapping.IMapper;
import nl.kingdev.jmongoodm.utils.DocumentUtils;
import nl.kingdev.jmongoodm.utils.NameUtils;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.lang.reflect.Field;

public class ObjectIdMapper implements IMapper {
    @Override
    public void mapFromDocument(Object ownerInstance, Field field, Document document) throws Exception {
        ObjectId objectId = (ObjectId) DocumentUtils.getValue(document, NameUtils.getColumnName(field));
        field.set(ownerInstance, objectId);
    }

    @Override
    public void mapToDocument(Object ownerInstance, Field field, Document document) throws Exception {
        String name = NameUtils.getColumnName(field);
        if (name.equals("_id")) return;
        ObjectId objectId = (ObjectId) field.get(ownerInstance);
        if (objectId != null) {
            document.append(name, objectId.toString());
        }
    }
}
