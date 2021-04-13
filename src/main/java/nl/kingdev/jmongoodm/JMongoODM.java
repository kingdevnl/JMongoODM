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

package nl.kingdev.jmongoodm;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import lombok.Data;
import lombok.Getter;
import nl.kingdev.jmongoodm.mapping.MappingRegistry;
import nl.kingdev.jmongoodm.mapping.impl.DateMapper;
import nl.kingdev.jmongoodm.mapping.impl.EnumMapper;
import nl.kingdev.jmongoodm.mapping.impl.ObjectIdMapper;
import nl.kingdev.jmongoodm.mapping.impl.UUIDMapper;
import org.bson.types.ObjectId;

import java.util.Date;
import java.util.UUID;

public class JMongoODM {


    @Getter
    private static MongoClient mongoClient;
    @Getter
    private static MongoDatabase database;


    @Getter
    private static MappingRegistry mappingRegistry;


    public static void init(MongoClient client, String dbName) {
        mongoClient = client;
        JMongoODM.database = client.getDatabase(dbName);
        mappingRegistry = new MappingRegistry();

        mappingRegistry.registerMapper(ObjectId.class, new ObjectIdMapper());
        mappingRegistry.registerMapper(UUID.class, new UUIDMapper());
        mappingRegistry.registerMapper(Enum.class, new EnumMapper());
        mappingRegistry.registerMapper(Date.class, new DateMapper());
    }


    public static void close() {
        mongoClient.close();
    }


}
