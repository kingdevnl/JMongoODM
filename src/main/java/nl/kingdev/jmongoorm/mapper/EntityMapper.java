package nl.kingdev.jmongoorm.mapper;

import nl.kingdev.jmongoorm.annotations.Column;
import nl.kingdev.jmongoorm.annotations.Embed;
import nl.kingdev.jmongoorm.annotations.ObjectID;
import nl.kingdev.jmongoorm.entity.BaseEntity;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.lang.reflect.Field;
import java.util.*;

public class EntityMapper<T extends BaseEntity> {

    private Document rootDocument;
    private Class<? extends BaseEntity> clzz;

    public EntityMapper(Document document, Class<? extends BaseEntity> clzz) {
        this.rootDocument = document;
        this.clzz = clzz;
    }

    private boolean isBuiltinType(Class<?> type) {
        return type == String.class || type == boolean.class || type == byte.class || type == char.class || type == short.class
                || type == int.class || type == long.class || type == float.class || type == double.class || type == Date.class;
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

    private String getColumnName(Field field) {

        Column column = field.getDeclaredAnnotation(Column.class);

        if (!column.value().equals("")) {
            return column.value();
        }

        return field.getName();
    }

    public String getEmbedName(Field field) {
        Embed embed = field.getDeclaredAnnotation(Embed.class);

        if (!embed.value().equals("")) {
            return embed.value();
        }

        return field.getName();
    }

    private void mapField(Object instance, Field field, Document document) {


        try {
            //Check if it's a Column and a Basic type.
            if (field.isAnnotationPresent(Column.class) && isBuiltinType(field.getType())) {

                //Get the name, Then the value
                Object value = getValue(document, getColumnName(field));
                if (value != null) {
                    field.setAccessible(true);
                    field.set(instance, value);
                }
            }
            //Check if it's a embed
            else if (field.isAnnotationPresent(Embed.class)) {
                String embedName = getEmbedName(field);
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
            } else if (field.isAnnotationPresent(nl.kingdev.jmongoorm.annotations.List.class) && field.isAnnotationPresent(Column.class)) {

                field.setAccessible(true);
                nl.kingdev.jmongoorm.annotations.List listInfo = field.getDeclaredAnnotation(nl.kingdev.jmongoorm.annotations.List.class);
                List<Object> list = (List<Object>) field.get(instance);
                if (list == null) {
                    list = new ArrayList<>();
                    field.set(instance, list);
                    System.out.println("new list");
                }

                if (isBuiltinType(listInfo.value())) {
                    list.addAll((Collection<?>) document.get(getColumnName(field)));

                } else {
                    List<Document> documentList = (List<Document>) document.get(getColumnName(field));
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
        try {
            if (field.isAnnotationPresent(Column.class) && isBuiltinType(field.getType())) {
                Object value = field.get(instance);
                if (value != null) {
                    document.append(getColumnName(field), value);
                }
            } else if (field.isAnnotationPresent(Embed.class)) {
                String embedName = getEmbedName(field);
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
            } else if (field.isAnnotationPresent(nl.kingdev.jmongoorm.annotations.List.class) && field.getType().isAssignableFrom(List.class)) {
                List<Object> list = (List<Object>) field.get(instance);
                nl.kingdev.jmongoorm.annotations.List listInfo = field.getDeclaredAnnotation(nl.kingdev.jmongoorm.annotations.List.class);

                if (list != null) {


                    if (isBuiltinType(listInfo.value())) {
                        document.append(getColumnName(field), list);
                    } else {
                        List<Document> documents = new ArrayList<>();
                        for (Object element : list) {
                            Document doc = new Document();
                            for (Field elementField : element.getClass().getDeclaredFields()) {
                                saveField(element, elementField, doc);
                            }
                            documents.add(doc);
                        }
                        rootDocument.append(getColumnName(field), documents);

                    }
                } else {
                    document.put(getColumnName(field), null);
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Document mapToDocument(T entity) {
        List<Field> fields = new ArrayList<>();
        fields.addAll(Arrays.asList(entity.getClass().getSuperclass().getDeclaredFields()));
        fields.addAll(Arrays.asList(entity.getClass().getDeclaredFields()));

        fields.forEach(field -> saveField(entity, field, rootDocument));

        return rootDocument;
    }
}
