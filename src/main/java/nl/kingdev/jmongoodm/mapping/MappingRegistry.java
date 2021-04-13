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
