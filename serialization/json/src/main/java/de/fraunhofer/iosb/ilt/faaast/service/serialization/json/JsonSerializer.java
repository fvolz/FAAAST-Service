/*
 * Copyright (c) 2021 Fraunhofer IOSB, eine rechtlich nicht selbstaendige
 * Einrichtung der Fraunhofer-Gesellschaft zur Foerderung der angewandten
 * Forschung e.V.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.fraunhofer.iosb.ilt.faaast.service.serialization.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;
import de.fraunhofer.iosb.ilt.faaast.service.model.v3.api.Content;
import de.fraunhofer.iosb.ilt.faaast.service.model.v3.api.OutputModifier;
import de.fraunhofer.iosb.ilt.faaast.service.model.v3.valuedata.ElementValue;
import de.fraunhofer.iosb.ilt.faaast.service.serialization.core.SerializationException;
import de.fraunhofer.iosb.ilt.faaast.service.serialization.core.Serializer;
import de.fraunhofer.iosb.ilt.faaast.service.serialization.json.serializer.ModifierAwareSerializer;
import java.util.function.Consumer;


public class JsonSerializer implements Serializer {

    private final ValueOnlyJsonSerializer valueOnlySerializer;
    private final SerializerWrapper wrapper;

    public JsonSerializer() {
        this.wrapper = new SerializerWrapper();
        this.valueOnlySerializer = new ValueOnlyJsonSerializer();
    }


    public JsonSerializer(Consumer<JsonMapper> modifier) {
        this.wrapper = new SerializerWrapper(modifier);
        this.valueOnlySerializer = new ValueOnlyJsonSerializer();
    }


    @Override
    public String write(Object obj, OutputModifier modifier) throws SerializationException {
        if ((modifier != null && modifier.getContent() == Content.Value)
                || (obj != null && ElementValue.class.isAssignableFrom(obj.getClass()))) {
            return valueOnlySerializer.write(obj, modifier.getLevel(), modifier.getExtend());
        }
        try {
            return wrapper.getMapper().writer()
                    .withAttribute(ModifierAwareSerializer.LEVEL, modifier)
                    .writeValueAsString(obj);
        }
        catch (JsonProcessingException ex) {
            throw new SerializationException("serialization failed", ex);
        }
    }

}
