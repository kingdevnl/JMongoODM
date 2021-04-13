package nl.kingdev.jmongoodm.mapping;

import nl.kingdev.jmongoodm.mapping.impl.PrimitiveMapper;

import java.util.HashMap;
import java.util.Map;

import static nl.kingdev.jmongoodm.utils.TypeUtils.isPrimitive;

public class MappingRegistry {

    /*
        Simple registry for Mapping classes
     */
    private Map<Class<?>, IMapper> registry = new HashMap<>();


    public MappingRegistry() {

    }

    /**
     * Register a Mapper
     *
     * @param clzz   type to map
     * @param mapper The mapper
     */
    public void registerMapper(Class<?> clzz, IMapper mapper) {
        if (registry.containsKey(clzz)) {
            registry.replace(clzz, mapper);
            return;
        }
        registry.put(clzz, mapper);
    }


    /**
     * Get a mapper by a type
     *
     * @param type the type to get
     * @return Mapper
     */
    public IMapper getMapperByType(Class<?> type) {
        IMapper mapper = registry.get(type);
        if (mapper == null) {
            if (isPrimitive(type)) {
                return new PrimitiveMapper();
            }
            if (Enum.class.isAssignableFrom(type)) {
                return getMapperByType(Enum.class);
            }
            System.err.println("Failed to find mapper for type: " + type);
            throw new RuntimeException("Failed to find mapper for type: " + type);
        }
        return mapper;
    }


}
