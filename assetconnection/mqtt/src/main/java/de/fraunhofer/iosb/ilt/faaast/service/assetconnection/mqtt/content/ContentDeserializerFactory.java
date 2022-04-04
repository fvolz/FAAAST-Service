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
package de.fraunhofer.iosb.ilt.faaast.service.assetconnection.mqtt.content;

/**
 * Factory class for {@link ContentDeserializer} based on {@link ContentFormat}.
 */
public class ContentDeserializerFactory {

    private ContentDeserializerFactory() {}


    /**
     * Instantiate {@link ContentDeserializer} based on provided
     * {@link ContentFormat}.
     *
     * @param format content format to use
     * @return instance of {@link ContentDeserializer} able to deserialize given
     *         format
     * @throws IllegalArgumentException if provided format is not supported
     */
    public static ContentDeserializer create(ContentFormat format) {
        if (format == ContentFormat.JSON) {
            return new JsonContentDeserializer();
        }
        throw new IllegalArgumentException(String.format("unsupported content format (%s)", format));
    }
}
