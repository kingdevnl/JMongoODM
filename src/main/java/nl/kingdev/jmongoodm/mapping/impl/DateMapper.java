package nl.kingdev.jmongoodm.mapping.impl;

import nl.kingdev.jmongoodm.mapping.IMapper;
import nl.kingdev.jmongoodm.utils.DocumentUtils;
import nl.kingdev.jmongoodm.utils.NameUtils;
import org.bson.Document;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateMapper implements IMapper {

    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");


    @Override
    public void mapFromDocument(Object ownerInstance, Field field, Document document) throws Exception {
        field.set(ownerInstance, simpleDateFormat.parse((String) DocumentUtils.getValue(document, NameUtils.getColumnName(field))));
    }

    @Override
    public void mapToDocument(Object ownerInstance, Field field, Document document) throws Exception {
        Date date = (Date) field.get(ownerInstance);
        document.append(NameUtils.getColumnName(field), simpleDateFormat.format(date));
    }
}
