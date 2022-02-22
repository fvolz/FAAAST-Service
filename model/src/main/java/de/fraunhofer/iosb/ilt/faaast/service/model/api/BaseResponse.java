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
package de.fraunhofer.iosb.ilt.faaast.service.model.api;

import io.adminshell.aas.v3.model.builder.ExtendableBuilder;
import java.util.Objects;

/**
 * Abstract base class for protocol-agnostic responses.
 */
public abstract class BaseResponse implements Response {

    private StatusCode statusCode;

    public BaseResponse() {
        this.statusCode = StatusCode.ServerInternalError;
    }

    public StatusCode getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(StatusCode statusCode) {
        this.statusCode = statusCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        BaseResponse that = (BaseResponse) o;
        return statusCode == that.statusCode;
    }

    @Override
    public int hashCode() {
        return Objects.hash(statusCode);
    }

    public static abstract class AbstractBuilder<T extends BaseResponse, B extends AbstractBuilder<T, B>> extends ExtendableBuilder<T, B> {

        public B statusCode(StatusCode value) {
            getBuildingInstance().setStatusCode(value);
            return getSelf();
        }
    }
}
