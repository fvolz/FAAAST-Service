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
package de.fraunhofer.iosb.ilt.faaast.service.request.handler;

import de.fraunhofer.iosb.ilt.faaast.service.Service;
import de.fraunhofer.iosb.ilt.faaast.service.assetconnection.AssetConnectionManager;
import de.fraunhofer.iosb.ilt.faaast.service.config.CoreConfig;
import de.fraunhofer.iosb.ilt.faaast.service.endpoint.Endpoint;
import de.fraunhofer.iosb.ilt.faaast.service.filestorage.FileStorage;
import de.fraunhofer.iosb.ilt.faaast.service.messagebus.MessageBus;
import de.fraunhofer.iosb.ilt.faaast.service.persistence.Persistence;
import java.util.Objects;


/**
 * Immutable wrapper class containing access to all relevant information of a Service to execute a
 * {@link de.fraunhofer.iosb.ilt.faaast.service.model.api.Request}.
 */
public class DynamicRequestExecutionContext implements RequestExecutionContext {

    private final Endpoint endpoint;
    private final Service service;

    public DynamicRequestExecutionContext(Service service, Endpoint endpoint) {
        this.service = service;
        this.endpoint = endpoint;
    }


    public DynamicRequestExecutionContext(Service service) {
        this(service, null);
    }


    @Override
    public AssetConnectionManager getAssetConnectionManager() {
        return service.getAssetConnectionManager();
    }


    @Override
    public CoreConfig getCoreConfig() {
        return service.getConfig().getCore();
    }


    @Override
    public Endpoint getEndpoint() {
        return endpoint;
    }


    @Override
    public boolean hasEndpoint() {
        return Objects.nonNull(endpoint);
    }


    @Override
    public FileStorage getFileStorage() {
        return service.getFileStorage();
    }


    @Override
    public MessageBus getMessageBus() {
        return service.getMessageBus();
    }


    @Override
    public Persistence<?> getPersistence() {
        return service.getPersistence();
    }


    @Override
    public DynamicRequestExecutionContext withEndpoint(Endpoint endpoint) {
        return new DynamicRequestExecutionContext(service, endpoint);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DynamicRequestExecutionContext that = (DynamicRequestExecutionContext) o;
        return Objects.equals(endpoint, that.endpoint)
                && Objects.equals(service, that.service);
    }


    @Override
    public int hashCode() {
        return Objects.hash(endpoint, service);
    }
}
