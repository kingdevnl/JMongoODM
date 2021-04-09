package nl.kingdev.jmongoorm.utils;

import nl.kingdev.jmongoorm.annotations.Entity;
import nl.kingdev.jmongoorm.entity.BaseEntity;

public class NameUtils {

    /**
     * @param clzz The class of the Entity
     * @return The name of the collection to use
     */
    public static String getEntityCollectionName(Class<? extends BaseEntity> clzz) {
        Entity entityInfo = clzz.getDeclaredAnnotation(Entity.class);

        if (entityInfo == null) {
            System.out.printf("Entity class %s is missing the @Entity annotation!", clzz.getName());
            return null;
        }

        if (!entityInfo.value().equals("")) {
            return entityInfo.value();
        }

        return clzz.getSimpleName().toLowerCase().replaceAll("entity", "") + "s";
    }


}
