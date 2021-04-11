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

package nl.kingdev.jmongoodm.utils;

import nl.kingdev.jmongoodm.annotations.Column;
import nl.kingdev.jmongoodm.annotations.Entity;
import nl.kingdev.jmongoodm.entity.BaseEntity;

import java.lang.reflect.Field;

public class NameUtils {

    /**
     * @param clzz The class of the Entity
     * @return The name of the collection to use
     */
    public static String getEntityCollectionName(Class<? extends BaseEntity> clzz) {
        Entity entityInfo = clzz.getDeclaredAnnotation(Entity.class);

        if (entityInfo == null) {
            System.out.printf("Entity class %s is missing the @Entity annotation!", clzz.getName());
            throw new RuntimeException("Invalid entity class, Missing @Entity annotation");
        }

        if (!entityInfo.value().equals("")) {
            return entityInfo.value();
        }

        return clzz.getSimpleName().toLowerCase().replaceAll("entity", "") + "s";
    }


    public static String getColumnName(Field field) {

        Column column = field.getDeclaredAnnotation(Column.class);

        if (!column.value().equals("")) {
            return column.value();
        }

        return field.getName();
    }

}
