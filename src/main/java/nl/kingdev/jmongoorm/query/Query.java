package nl.kingdev.jmongoorm.query;

import lombok.Getter;
import org.bson.Document;

import java.util.Queue;

public class Query {

    private Document queryDocument = null;


    private String currentFieldName = "";

    public Query field(String fieldName) {
        this.currentFieldName = fieldName;
        return this;
    }

    public Query _equals(Object value) {
        if(queryDocument == null) {
            queryDocument = new Document(currentFieldName, value);
            return this;
        } else {
            queryDocument.append(currentFieldName, value);
        }
        return this;
    }

    public Query _greaterThen(Number value) {
        if(queryDocument == null) {
            queryDocument = new Document(currentFieldName, value);
        }

        Document doc = new Document();
        doc.append("$gt", value);
        this.queryDocument.append(currentFieldName, doc);
        return this;
    }
    public Query _greaterThenEquals(Number value) {
        if(queryDocument == null) {
            queryDocument = new Document(currentFieldName, value);
        }

        Document doc = new Document();
        doc.append("$gte", value);
        this.queryDocument.append(currentFieldName, doc);
        return this;
    }
    public Query _lessThen(Number value) {
        if(queryDocument == null) {
            queryDocument = new Document(currentFieldName, value);
        }

        Document doc = new Document();
        doc.append("$lt", value);
        this.queryDocument.append(currentFieldName, doc);
        return this;
    }

    public Query _lessThenEquals(Number value) {
        if(queryDocument == null) {
            queryDocument = new Document(currentFieldName, value);
        }

        Document doc = new Document();
        doc.append("$lte", value);
        this.queryDocument.append(currentFieldName, doc);
        return this;
    }

    public Document getQueryDocument() {
        if(queryDocument == null) {
            queryDocument = new Document();
        }
        return queryDocument;
    }
}
