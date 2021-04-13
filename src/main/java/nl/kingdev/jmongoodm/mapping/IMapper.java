package nl.kingdev.jmongoodm.mapping;

import org.bson.Document;
import java.lang.reflect.Field;


public interface IMapper {

     void mapFromDocument(Object ownerInstance, Field field, Document document) throws Exception;
     void mapToDocument(Object ownerInstance, Field field, Document document) throws Exception;

}
