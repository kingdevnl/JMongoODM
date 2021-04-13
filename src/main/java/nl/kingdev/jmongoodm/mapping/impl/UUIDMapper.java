package nl.kingdev.jmongoodm.mapping.impl;

import nl.kingdev.jmongoodm.mapping.IMapper;
import nl.kingdev.jmongoodm.utils.DocumentUtils;
import nl.kingdev.jmongoodm.utils.NameUtils;
import org.bson.Document;

import java.lang.reflect.Field;
import java.util.UUID;

public class UUIDMapper implements IMapper {
    @Override
    public void mapFromDocument(Object ownerInstance, Field field, Document document) throws Exception {
        field.set(ownerInstance, UUID.fromString((String) DocumentUtils.getValue(document, NameUtils.getColumnName(field))));
    }

    @Override
    public void mapToDocument(Object ownerInstance, Field field, Document document) throws Exception {
        UUID uuid = (UUID) field.get(ownerInstance);
        if (uuid != null) {
            document.append(NameUtils.getColumnName(field), uuid.toString());
        }
    }
}
