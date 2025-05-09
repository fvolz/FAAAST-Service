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
package de.fraunhofer.iosb.ilt.faaast.service.model.value.primitive;

import de.fraunhofer.iosb.ilt.faaast.service.model.value.Datatype;
import java.time.LocalTime;
import java.time.OffsetTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;


/**
 * A time value.
 */
public class TimeValue extends AbstractDateTimeValue<OffsetTime> {

    public TimeValue() {
        super();
    }


    public TimeValue(OffsetTime value) {
        super(value);
    }


    @Override
    public Datatype getDataType() {
        return Datatype.TIME;
    }


    @Override
    protected DateTimeFormatter getFormatBase() {
        return DateTimeFormatter.ISO_LOCAL_TIME;
    }


    @Override
    protected OffsetTime parseLocal(String value) throws DateTimeParseException {
        LocalTime local = LocalTime.parse(value);
        return OffsetTime.of(local, OffsetTime.now(ZoneId.systemDefault()).getOffset());
    }


    @Override
    protected OffsetTime parseOffset(String value) throws DateTimeParseException {
        return OffsetTime.parse(value);
    }

}
