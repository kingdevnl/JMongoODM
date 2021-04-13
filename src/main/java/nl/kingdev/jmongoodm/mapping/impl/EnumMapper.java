package nl.kingdev.jmongoodm.mapping.impl;

import nl.kingdev.jmongoodm.mapping.IMapper;
import nl.kingdev.jmongoodm.utils.NameUtils;
import org.bson.Document;

import java.lang.reflect.Field;

import static nl.kingdev.jmongoodm.utils.DocumentUtils.getValue;

public class EnumMapper implements IMapper {


    @Override
    public void mapFromDocument(Object ownerInstance, Field field, Document document) throws Exception {
        String enumValue = (String) getValue(document, NameUtils.getColumnName(field));
        field.set(ownerInstance, Enum.valueOf((Class<? extends Enum>) field.getType(), enumValue));
    }

    @Override
    public void mapToDocument(Object ownerInstance, Field field, Document document) throws Exception {
        document.append(NameUtils.getColumnName(field), field.get(ownerInstance).toString());
    }
}
