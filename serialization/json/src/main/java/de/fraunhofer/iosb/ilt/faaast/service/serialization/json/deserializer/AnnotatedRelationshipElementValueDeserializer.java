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
package de.fraunhofer.iosb.ilt.faaast.service.serialization.json.deserializer;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import de.fraunhofer.iosb.ilt.faaast.service.model.v3.valuedata.AnnotatedRelationshipElementValue;
import de.fraunhofer.iosb.ilt.faaast.service.model.v3.valuedata.DataElementValue;
import de.fraunhofer.iosb.ilt.faaast.service.model.v3.valuedata.ReferenceElementValue;
import de.fraunhofer.iosb.ilt.faaast.service.serialization.json.JsonFieldNames;
import java.io.IOException;


public class AnnotatedRelationshipElementValueDeserializer extends ContextAwareElementValueDeserializer<AnnotatedRelationshipElementValue> {

    public AnnotatedRelationshipElementValueDeserializer() {
        this(null);
    }


    public AnnotatedRelationshipElementValueDeserializer(Class<AnnotatedRelationshipElementValue> type) {
        super(type);
    }


    @Override
    public AnnotatedRelationshipElementValue deserialize(JsonParser parser, DeserializationContext context) throws IOException, JacksonException {
        AnnotatedRelationshipElementValue.Builder builder = new AnnotatedRelationshipElementValue.Builder();
        JsonNode node = parser.readValueAsTree();
        if (node.has(JsonFieldNames.ANNOTATED_RELATIONSHIP_ELEMENT_VALUE_FIRST)) {
            builder.first(context.readTreeAsValue(node.get(JsonFieldNames.ANNOTATED_RELATIONSHIP_ELEMENT_VALUE_FIRST), ReferenceElementValue.class).getKeys());
        }
        if (node.has(JsonFieldNames.ANNOTATED_RELATIONSHIP_ELEMENT_VALUE_SECOND)) {
            builder.second(context.readTreeAsValue(node.get(JsonFieldNames.ANNOTATED_RELATIONSHIP_ELEMENT_VALUE_SECOND), ReferenceElementValue.class).getKeys());
        }
        if (node.has(JsonFieldNames.ANNOTATED_RELATIONSHIP_ELEMENT_VALUE_ANNOTATION)) {
            builder.annotations(deserializeChildren(node.get(JsonFieldNames.ANNOTATED_RELATIONSHIP_ELEMENT_VALUE_ANNOTATION), context, DataElementValue.class));
        }
        return builder.build();
    }

}
