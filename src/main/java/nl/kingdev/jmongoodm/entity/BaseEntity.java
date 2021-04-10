package nl.kingdev.jmongoodm.entity;

import com.mongodb.client.MongoCollection;
import nl.kingdev.jmongoodm.JMongoODM;
import nl.kingdev.jmongoodm.annotations.ObjectID;
import nl.kingdev.jmongoodm.mapper.EntityMapper;
import nl.kingdev.jmongoodm.query.Query;
import nl.kingdev.jmongoodm.utils.NameUtils;
import org.bson.BsonObjectId;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public abstract class BaseEntity {


    @ObjectID()
    public ObjectId objectID;

    public BaseEntity() {
    }

    /**
     * Find one entity
     *
     * @param query Query to execute
     * @param type  Type of the entity (Class)
     * @param <T>   Generic type
     * @return Entity found
     */
    public static <T extends BaseEntity> T findOne(Query query, Class<? extends BaseEntity> type) {
        String collectionName = NameUtils.getEntityCollectionName(type);
        MongoCollection<Document> collection = JMongoODM.getDatabase().getCollection(collectionName);

        Document first = collection.find(query.getQueryDocument()).first();

        if (first != null) {
            EntityMapper<T> mapper = new EntityMapper<>(first, type);
            return mapper.mapToEntity();
        }

        return null;
    }

    /**
     * Find multiple entities
     *
     * @param query Query to execute
     * @param type  Type of the entity (Class)
     * @param <T>   Generic type
     * @return Entities found
     */
    public static <T extends BaseEntity> List<T> find(Query query, Class<? extends BaseEntity> type) {
        List<T> entities = new ArrayList<>();

        String collectionName = NameUtils.getEntityCollectionName(type);
        MongoCollection<Document> collection = JMongoODM.getDatabase().getCollection(collectionName);

        for (Document document : collection.find(query.getQueryDocument())) {

            EntityMapper<T> mapper = new EntityMapper<>(document, type);
            entities.add(mapper.mapToEntity());
        }

        return entities;
    }

    /**
     * Delete one entity
     *
     * @param query Query to execute
     * @param type  Type of the entity (Class)
     * @param <T>   Generic type
     * @return How many where deleted
     */
    public static <T extends BaseEntity> long deleteOne(Query query, Class<T> type) {
        return JMongoODM.getDatabase().getCollection(NameUtils.getEntityCollectionName(type))
                .deleteOne((query.getQueryDocument())).getDeletedCount();
    }

    /**
     * Delete multiple entity
     *
     * @param query Query to execute
     * @param type  Type of the entity (Class)
     * @param <T>   Generic type
     * @return How many where deleted
     */
    public static <T extends BaseEntity> long delete(Query query, Class<T> type) {
        return JMongoODM.getDatabase().getCollection(NameUtils.getEntityCollectionName(type))
                .deleteMany((query.getQueryDocument())).getDeletedCount();
    }

    /**
     * Delete one entity
     *
     * @param entity  Type of the entity (Class)
     * @param <T>   Generic type
     * @return How many where deleted
     */
    public static <T extends BaseEntity> long deleteOne(T entity) {
        return JMongoODM.getDatabase().getCollection(NameUtils.getEntityCollectionName(entity.getClass()))
                .deleteOne(new Document("_id", entity.objectID)).getDeletedCount();
    }

    /**
     * Save the entity
     *
     * @param <T> Generic type
     * @return entity
     */
    public <T extends BaseEntity> T save() {
        return save(getClass());
    }

    /**
     * Save the entity
     *
     * @param <T> Generic type
     * @return entity
     */
    public <T extends BaseEntity> T save(Class<? extends BaseEntity> type) {
        String collectionName = NameUtils.getEntityCollectionName(type);
        MongoCollection<Document> collection = JMongoODM.getDatabase().getCollection(collectionName);

        EntityMapper<T> mapper = new EntityMapper<>(new Document(), type);

        Document document = mapper.mapToDocument((T) this);


        if (objectID != null) {
            collection.updateOne(new Document("_id", objectID), new Document("$set", document));
        } else {
            BsonObjectId bsonObjectId = collection.insertOne(document).getInsertedId().asObjectId();
            this.objectID = bsonObjectId.getValue();
        }


        return (T) this;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BaseEntity that = (BaseEntity) o;
        return Objects.equals(objectID, that.objectID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(objectID);
    }
}


