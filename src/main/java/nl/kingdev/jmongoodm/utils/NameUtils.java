package nl.kingdev.jmongoodm.utils;

import nl.kingdev.jmongoodm.annotations.Column;
import nl.kingdev.jmongoodm.annotations.Embed;
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

    public static String getEmbedName(Field field) {
        Embed embed = field.getDeclaredAnnotation(Embed.class);

        if (!embed.value().equals("")) {
            return embed.value();
        }

        return field.getName();
    }

}
