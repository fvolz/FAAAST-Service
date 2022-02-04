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
package de.fraunhofer.iosb.ilt.faaast.service.model.v3.valuedata.values;

import java.util.Objects;


public abstract class TypedValue<T> {

    protected T value;

    protected TypedValue(T value) {
        this.value = value;
    }


    protected TypedValue() {}


    public abstract String asString();


    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final TypedValue other = (TypedValue) obj;
        return Objects.equals(value, other.value);
    }


    public abstract void fromString(String value) throws ValueFormatException;


    public abstract Datatype getDataType();


    public T getValue() {
        return value;
    }


    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
