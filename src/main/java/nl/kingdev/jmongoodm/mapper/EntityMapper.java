/*
 * MIT License
 *
 * Copyright (c) 2021 kingdevnl
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package nl.kingdev.jmongoodm.mapper;

import nl.kingdev.jmongoodm.annotations.Column;
import nl.kingdev.jmongoodm.annotations.Embed;
import nl.kingdev.jmongoodm.annotations.ObjectID;
import nl.kingdev.jmongoodm.annotations.Reference;
import nl.kingdev.jmongoodm.entity.BaseEntity;
import nl.kingdev.jmongoodm.query.Query;
import nl.kingdev.jmongoodm.utils.NameUtils;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
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
                String embedName = NameUtils.getColumnName(field);
                Document embeddedDocument = (Document) document.get(embedName);
                if (embeddedDocument != null) {
                    field.setAccessible(true);

                    Class<?> embedType = field.getType();

                    Object embedInstance = field.get(instance);

                    if (embedInstance == null) {
                        embedInstance = newInstance(embedType);
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
                            Object elementInstance = newInstance(listInfo.value());
                            for (Field elementField : listInfo.value().getDeclaredFields()) {
                                mapField(elementInstance, elementField, doc);
                            }
                            list.add(elementInstance);
                        }
                    }
                }
            } else if (field.isAnnotationPresent(Reference.class)) {
                Reference referenceInfo = field.getDeclaredAnnotation(Reference.class);
                if (field.getType().isAssignableFrom(BaseEntity.class)) {
                    ObjectId id = (ObjectId) getValue(document, NameUtils.getColumnName(field));
                    BaseEntity entity = BaseEntity.findOne(new Query().field("_id")._equals(id), referenceInfo.value());
                    if (entity != null) {
                        field.set(instance, entity);
                    }
                } else if (List.class.isAssignableFrom(field.getType())) {

                    List<Object> list = (List<Object>) field.get(instance);
                    if (list == null) {
                        list = new ArrayList<>();
                        field.set(instance, list);
                    }
                    List<ObjectId> ids = (List<ObjectId>) document.get(NameUtils.getColumnName(field));
                    for (ObjectId id : ids) {
                        list.addAll(BaseEntity.find(new Query().field("_id")._equals(id), referenceInfo.value()));
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
            T entity = (T) newInstance(clzz);
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
                    document.append(NameUtils.getColumnName(field), value);
                }
            }
            if (field.isAnnotationPresent(Embed.class)) {
                String embedName = NameUtils.getColumnName(field);
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

            } else if (field.isAnnotationPresent(Reference.class)) {

                if (BaseEntity.class.isAssignableFrom(field.getType())) {
                    Object value = field.get(instance);
                    if (value instanceof BaseEntity) {
                        document.append(NameUtils.getColumnName(field), ((BaseEntity) value).objectID);
                    }
                } else if (List.class.isAssignableFrom(field.getType())) {
                    List<BaseEntity> entities = (List<BaseEntity>) field.get(instance);
                    List<ObjectId> ids = new ArrayList<>();
                    entities.forEach(e -> ids.add(e.objectID));
                    document.append(NameUtils.getColumnName(field), ids);
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

    private Object newInstance(Class<?> m) throws Exception {
        Constructor<?> c = m.getDeclaredConstructor();

        boolean canAccess = !Modifier.isPrivate(c.getModifiers());

        if(!canAccess) {
            c.setAccessible(true);
        }
        Object instance = c.newInstance();

        c.setAccessible(canAccess);

        return instance;
    }
}
