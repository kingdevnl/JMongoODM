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

package nl.kingdev.jmongoodm.mapping.impl;

import nl.kingdev.jmongoodm.mapping.IMapper;
import nl.kingdev.jmongoodm.utils.DocumentUtils;
import nl.kingdev.jmongoodm.utils.NameUtils;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.lang.reflect.Field;

public class ObjectIdMapper implements IMapper {
    @Override
    public void mapFromDocument(Object ownerInstance, Field field, Document document) throws Exception {
        ObjectId objectId = (ObjectId) DocumentUtils.getValue(document, NameUtils.getColumnName(field));
        field.set(ownerInstance, objectId);
    }

    @Override
    public void mapToDocument(Object ownerInstance, Field field, Document document) throws Exception {
        String name = NameUtils.getColumnName(field);
        if (name.equals("_id")) return;
        ObjectId objectId = (ObjectId) field.get(ownerInstance);
        if (objectId != null) {
            document.append(name, objectId.toString());
        }
    }
}
