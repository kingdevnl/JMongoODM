package nl.kingdev.jmongoorm.entity;

import com.mongodb.client.MongoCollection;
import nl.kingdev.jmongoorm.JMongoORM;
import nl.kingdev.jmongoorm.annotations.ObjectID;
import nl.kingdev.jmongoorm.mapper.EntityMapper;
import nl.kingdev.jmongoorm.query.Query;
import nl.kingdev.jmongoorm.utils.NameUtils;
import org.bson.BsonObjectId;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class BaseEntity {


    @ObjectID()
    public ObjectId objectID;

    public BaseEntity() {
    }


    public static <T extends BaseEntity> T findOne(Query query, Class<? extends BaseEntity> type) {
        String collectionName = NameUtils.getEntityCollectionName(type);
        MongoCollection<Document> collection = JMongoORM.getDatabase().getCollection(collectionName);
        Document first = collection.find(query.getQueryDocument()).first();

        if (first != null) {
            EntityMapper<T> mapper = new EntityMapper<>(first, type);
            return mapper.mapToEntity();
        }

        return null;
    }


    public static <T extends BaseEntity> List<T> find(Query query, Class<? extends BaseEntity> type) {
        List<T> entities = new ArrayList<>();

        String collectionName = NameUtils.getEntityCollectionName(type);
        MongoCollection<Document> collection = JMongoORM.getDatabase().getCollection(collectionName);

        for (Document document : collection.find(query.getQueryDocument())) {

            EntityMapper<T> mapper = new EntityMapper<>(document, type);
            entities.add(mapper.mapToEntity());
        }

        return entities;
    }

    public <T extends BaseEntity> T save() {
        return save(getClass());
    }

    public <T extends BaseEntity> T save(Class<? extends BaseEntity> type) {
        String collectionName = NameUtils.getEntityCollectionName(type);
        MongoCollection<Document> collection = JMongoORM.getDatabase().getCollection(collectionName);

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


