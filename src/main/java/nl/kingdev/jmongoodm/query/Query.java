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

package nl.kingdev.jmongoodm.query;

import org.bson.Document;

import java.util.List;

public class Query {

    private Document queryDocument = null;
    private String currentFieldName = "";

    public Query() {
    }

    public static Query rawQuery(Document document) {
        Query query = new Query();
        query.queryDocument = document;
        return query;
    }

    public Query field(String fieldName) {
        this.currentFieldName = fieldName;
        return this;
    }

    public Query root() {
        this.currentFieldName = "";
        return this;
    }

    public Query _equals(Object value) {
        if (queryDocument == null) {
            queryDocument = new Document(currentFieldName, value);
            return this;
        } else {
            queryDocument.append(currentFieldName, value);
        }
        return this;
    }

    public Query _notEquals(Object value) {
        if (queryDocument == null) {
            queryDocument = new Document(currentFieldName, value);
        }
        Document doc = new Document();
        doc.append("$ne", value);
        return this;
    }

    public Query _greaterThen(Number value) {
        if (queryDocument == null) {
            queryDocument = new Document(currentFieldName, value);
        }

        Document doc = new Document();
        doc.append("$gt", value);
        this.queryDocument.append(currentFieldName, doc);
        return this;
    }

    public Query _greaterThenEquals(Number value) {
        if (queryDocument == null) {
            queryDocument = new Document(currentFieldName, value);
        }

        Document doc = new Document();
        doc.append("$gte", value);
        this.queryDocument.append(currentFieldName, doc);
        return this;
    }

    public Query _lessThen(Number value) {
        if (queryDocument == null) {
            queryDocument = new Document(currentFieldName, value);
        }

        Document doc = new Document();
        doc.append("$lt", value);
        this.queryDocument.append(currentFieldName, doc);
        return this;
    }

    public Query _lessThenEquals(Number value) {
        if (queryDocument == null) {
            queryDocument = new Document(currentFieldName, value);
        }

        Document doc = new Document();
        doc.append("$lte", value);
        this.queryDocument.append(currentFieldName, doc);
        return this;
    }

    public Query _in(List<Object> array) {
        if (queryDocument == null) {
            queryDocument = new Document();
        }

        queryDocument.append("$in", array);
        return this;
    }

    public Query _notIn(List<Object> array) {
        if (queryDocument == null) {
            queryDocument = new Document();
        }
        queryDocument.append("$nin", array);
        return this;
    }


    public Document getQueryDocument() {
        if (queryDocument == null) {
            queryDocument = new Document();
        }
        return queryDocument;
    }

}
