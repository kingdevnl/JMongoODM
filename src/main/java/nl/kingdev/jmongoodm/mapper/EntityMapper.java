package nl.kingdev.jmongoodm.mapper;

import nl.kingdev.jmongoodm.annotations.Column;
import nl.kingdev.jmongoodm.annotations.Embed;
import nl.kingdev.jmongoodm.annotations.ObjectID;
import nl.kingdev.jmongoodm.entity.BaseEntity;
import nl.kingdev.jmongoodm.utils.NameUtils;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.*;

public class EntityMapper<T extends BaseEntity> {

    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
    private Document rootDocument;
    private Class<? extends BaseEntity> clzz;

    public EntityMapper(Document document, Class<? extends BaseEntity> clzz) {
        this.rootDocument = document;
        this.clzz = clzz;
    }

    private boolean isBuiltinType(Class<?> type) {
        return type == String.class || type == boolean.class || type == byte.class || type == char.class || type == short.class
                || type == int.class || type == long.class || type == float.class || type == double.class;
    }

    private Object getValue(Document document, String name) {

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


    private void mapField(Object instance, Field field, Document document) {


        try {

            //Check if it's a embed
            if (field.isAnnotationPresent(Embed.class)) {
                String embedName = NameUtils.getEmbedName(field);
                Document embeddedDocument = (Document) document.get(embedName);
                if (embeddedDocument != null) {
                    field.setAccessible(true);

                    Class<?> embedType = field.getType();

                    Object embedInstance = field.get(instance);

                    if (embedInstance == null) {
                        embedInstance = embedType.getDeclaredConstructor().newInstance();
                        field.set(instance, embedInstance);
                    }

                    for (Field embedField : embedType.getDeclaredFields()) {
                        mapField(embedInstance, embedField, embeddedDocument);
                    }
                }
            } else if (field.isAnnotationPresent(ObjectID.class)) {
                ObjectId objectId = (ObjectId) rootDocument.get("_id");
                field.setAccessible(true);
                field.set(instance, objectId);
            } else if (field.isAnnotationPresent(nl.kingdev.jmongoodm.annotations.List.class) && field.isAnnotationPresent(Column.class)) {

                field.setAccessible(true);
                nl.kingdev.jmongoodm.annotations.List listInfo = field.getDeclaredAnnotation(nl.kingdev.jmongoodm.annotations.List.class);

                //Get the list
                List<Object> list = (List<Object>) field.get(instance);
                if (list == null) {
                    list = new ArrayList<>();
                    field.set(instance, list);
                }

                //If it's a simple type, just add them
                if (isBuiltinType(listInfo.value())) {
                    List<Object> objects = (List<Object>) document.get(NameUtils.getColumnName(field));
                    if (objects != null) {
                        list.addAll(objects);
                    }
                } else if (Enum.class.isAssignableFrom(listInfo.value())) {
                    List<String> values = (List<String>) document.get(NameUtils.getColumnName(field));

                    List<Object> enums = new ArrayList<>();

                    values.forEach(s -> {
                        enums.add(Enum.valueOf((Class<? extends Enum>) listInfo.value(), s));
                    });


                    field.set(instance, enums);

                } else {
                    //Map the custom type to documents
                    List<Document> documentList = (List<Document>) document.get(NameUtils.getColumnName(field));
                    if (documentList != null) {
                        for (Document doc : documentList) {
                            Object elementInstance = listInfo.value().getDeclaredConstructor().newInstance();
                            for (Field elementField : listInfo.value().getDeclaredFields()) {
                                mapField(elementInstance, elementField, doc);
                            }
                            list.add(elementInstance);
                        }
                    }
                }
            }
            //Check if it's a Column and a Basic type.
            else if (field.isAnnotationPresent(Column.class)) {
                field.setAccessible(true);

                //Get the name, Then the value
                Object value = null;


                if (isBuiltinType(field.getType())) {
                    value = getValue(document, NameUtils.getColumnName(field));
                } else if (UUID.class.isAssignableFrom(field.getType())) {
                    field.set(instance, UUID.fromString((String) getValue(document, NameUtils.getColumnName(field))));
                } else if (Date.class.isAssignableFrom(field.getType())) {
                    field.set(instance, simpleDateFormat.parse((String) getValue(document, NameUtils.getColumnName(field))));
                } else if (Enum.class.isAssignableFrom(field.getType())) {
                    String enumValue = (String) getValue(document, NameUtils.getColumnName(field));
                    field.set(instance, Enum.valueOf((Class<? extends Enum>) field.getType(), enumValue));
                }

                if (value != null) {
                    field.set(instance, value);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public T mapToEntity() {

        try {
            T entity = (T) clzz.getDeclaredConstructor().newInstance();
            List<Field> fields = new ArrayList<>();
            fields.addAll(Arrays.asList(entity.getClass().getSuperclass().getDeclaredFields()));
            fields.addAll(Arrays.asList(entity.getClass().getDeclaredFields()));

            fields.forEach(field -> mapField(entity, field, rootDocument));

            return entity;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    private void saveField(Object instance, Field field, Document document) {

        boolean canAccess = field.canAccess(instance);
        if (!canAccess) {
            field.setAccessible(true);
        }
        try {
            if (field.isAnnotationPresent(Column.class)) {
                Object value = null;

                if (isBuiltinType(field.getType())) {
                    value = field.get(instance);
                } else if (Enum.class.isAssignableFrom(field.getType())) {
                    value = field.get(instance).toString();
                } else if (UUID.class.isAssignableFrom(field.getType())) {
                    value = field.get(instance).toString();
                } else if (Date.class.isAssignableFrom(field.getType())) {
                    value = simpleDateFormat.format((Date) field.get(instance));
                }

                if (value != null) {
                    document.append(NameUtils.getColumnName(field), value.toString());
                }
            }
            if (field.isAnnotationPresent(Embed.class)) {
                String embedName = NameUtils.getEmbedName(field);
                Object embedInstance = field.get(instance);
                Document embeddedDocument = (Document) document.get(embedName);
                if (embeddedDocument == null) {
                    embeddedDocument = new Document();
                    document.append(embedName, embeddedDocument);
                }
                if (embedInstance != null) {
                    for (Field embedField : embedInstance.getClass().getDeclaredFields()) {
                        saveField(embedInstance, embedField, embeddedDocument);
                    }
                }
            } else if (field.isAnnotationPresent(nl.kingdev.jmongoodm.annotations.List.class) && List.class.isAssignableFrom(field.getType())) {

                List<Object> list = (List<Object>) field.get(instance);
                nl.kingdev.jmongoodm.annotations.List listInfo = field.getDeclaredAnnotation(nl.kingdev.jmongoodm.annotations.List.class);
                if (list != null) {
                    if (isBuiltinType(listInfo.value())) {
                        document.append(NameUtils.getColumnName(field), list);
                    } else if (Enum.class.isAssignableFrom(listInfo.value())) {
                        List<String> values = new ArrayList<>();
                        for (Object e : list) {
                            values.add(e.toString());
                        }
                        document.append(NameUtils.getColumnName(field), values);
                    } else {
                        List<Document> documents = new ArrayList<>();
                        for (Object element : list) {
                            Document doc = new Document();
                            for (Field elementField : element.getClass().getDeclaredFields()) {
                                saveField(element, elementField, doc);
                            }
                            documents.add(doc);
                        }
                        rootDocument.append(NameUtils.getColumnName(field), documents);

                    }
                } else {
                    document.put(NameUtils.getColumnName(field), null);
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        field.setAccessible(canAccess);
    }

    public Document mapToDocument(T entity) {
        List<Field> fields = new ArrayList<>();
        fields.addAll(Arrays.asList(entity.getClass().getSuperclass().getDeclaredFields()));
        fields.addAll(Arrays.asList(entity.getClass().getDeclaredFields()));

        fields.forEach(field -> saveField(entity, field, rootDocument));

        return rootDocument;
    }
}
