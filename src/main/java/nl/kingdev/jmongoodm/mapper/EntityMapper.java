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

import nl.kingdev.jmongoodm.JMongoODM;
import nl.kingdev.jmongoodm.annotations.Column;
import nl.kingdev.jmongoodm.annotations.Embed;
import nl.kingdev.jmongoodm.annotations.ObjectID;
import nl.kingdev.jmongoodm.annotations.Reference;
import nl.kingdev.jmongoodm.entity.BaseEntity;
import nl.kingdev.jmongoodm.mapping.IMapper;
import nl.kingdev.jmongoodm.query.Query;
import nl.kingdev.jmongoodm.utils.DocumentUtils;
import nl.kingdev.jmongoodm.utils.NameUtils;
import nl.kingdev.jmongoodm.utils.TypeUtils;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EntityMapper<T extends BaseEntity> {

    private Document rootDocument;
    private Class<? extends BaseEntity> clzz;

    public EntityMapper(Document document, Class<? extends BaseEntity> clzz) {
        this.rootDocument = document;
        this.clzz = clzz;
    }


    private void loadList(Object instance, Field field, Document document) throws Exception {

        field.setAccessible(true);
        nl.kingdev.jmongoodm.annotations.List listInfo = field.getDeclaredAnnotation(nl.kingdev.jmongoodm.annotations.List.class);


        //Get the list
        List<Object> list = (List<Object>) field.get(instance);
        if (list == null) {
            list = new ArrayList<>();
            field.set(instance, list);
        }

        //If it's a simple type, just add them
        if (TypeUtils.isPrimitive(listInfo.value())) {
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
    }

    private void mapField(Object instance, Field field, Document document) {

        try {
            field.setAccessible(true);
            //Map the simple types
            if (field.isAnnotationPresent(Column.class) && TypeUtils.isPrimitive(field.getType())) {
                IMapper mapper = JMongoODM.getMappingRegistry().getMapperByType(field.getType());
                mapper.mapFromDocument(instance, field, document);
                return;
            }

            //Embeds
            if (field.isAnnotationPresent(Embed.class)) {
                Document childDocument = (Document) document.get(NameUtils.getColumnName(field));
                if (childDocument != null) {
                    Class<?> embedType = field.getType();

                    Object embedInstance = field.get(instance);
                    if (embedInstance == null) {
                        embedInstance = newInstance(embedType);
                        field.set(instance, embedInstance);
                    }

                    for (Field embedField : embedType.getDeclaredFields()) {
                        mapField(embedInstance, embedField, childDocument);
                    }
                }
                return;
            }

            //Lists
            if (field.isAnnotationPresent(nl.kingdev.jmongoodm.annotations.List.class)) {
                loadList(instance, field, document);
                return;
            }

            //References
            if (field.isAnnotationPresent(Reference.class)) {
                Reference referenceInfo = field.getDeclaredAnnotation(Reference.class);
                if (field.getType().isAssignableFrom(BaseEntity.class)) {
                    ObjectId id = (ObjectId) DocumentUtils.getValue(document, NameUtils.getColumnName(field));
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
                return;
            }


            if (field.isAnnotationPresent(ObjectID.class)) {
                IMapper mapper = JMongoODM.getMappingRegistry().getMapperByType(field.getType());
                mapper.mapFromDocument(instance, field, document);
                return;
            }


            //All other types
            IMapper mapper = JMongoODM.getMappingRegistry().getMapperByType(field.getType());
            mapper.mapFromDocument(instance, field, document);

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


    //TODO: Find a better way to handle lists
    private void saveList(Object instance, Field field, Document document) throws Exception {
        List<Object> objectList = (List<Object>) field.get(instance);
        nl.kingdev.jmongoodm.annotations.List listInfo = field.getDeclaredAnnotation(nl.kingdev.jmongoodm.annotations.List.class);

        if (objectList != null) {
            if (TypeUtils.isPrimitive(listInfo.value())) {
                document.append(NameUtils.getColumnName(field), objectList);
            } else if (Enum.class.isAssignableFrom(listInfo.value())) {
                List<String> values = new ArrayList<>();
                for (Object e : objectList) {
                    values.add(e.toString());
                }
                document.append(NameUtils.getColumnName(field), values);
            } else {
                List<Document> documents = new ArrayList<>();
                for (Object element : objectList) {
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


    private void saveField(Object instance, Field field, Document document) {

        try {
            if (field.isAnnotationPresent(Column.class)) {
                field.setAccessible(true);

                //Primitives
                if (TypeUtils.isPrimitive(field.getType())) {
                    JMongoODM.getMappingRegistry().getMapperByType(field.getType()).mapToDocument(instance, field, document);
                    return;
                }

                //Embeds
                if (field.isAnnotationPresent(Embed.class)) {
                    String name = NameUtils.getColumnName(field);
                    Object embedInstance = field.get(instance);

                    if(embedInstance != null) {
                        Document childDocument = (Document) document.get(name);
                        if (childDocument == null) {
                            childDocument = new Document();
                            document.append(name, childDocument);
                        }
                        for (Field f : embedInstance.getClass().getDeclaredFields()) {
                            saveField(embedInstance, f, childDocument);
                        }
                    } else {
                        System.out.println("INSTANCE "+field.getName() + " IS NULL!");
                    }

                    return;
                }


                //Lists
                if (field.isAnnotationPresent(nl.kingdev.jmongoodm.annotations.List.class) && List.class.isAssignableFrom(field.getType())) {
                    saveList(instance, field, document);
                    return;
                }

                //References
                if (field.isAnnotationPresent(Reference.class)) {
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

                    return;
                }

                //All other types
                IMapper mapper = JMongoODM.getMappingRegistry().getMapperByType(field.getType());
                mapper.mapToDocument(instance, field, document);
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

    private Object newInstance(Class<?> m) throws Exception {
        Constructor<?> c = m.getDeclaredConstructor();

        boolean canAccess = !Modifier.isPrivate(c.getModifiers());

        if (!canAccess) {
            c.setAccessible(true);
        }
        Object instance = c.newInstance();

        c.setAccessible(canAccess);

        return instance;
    }
}
