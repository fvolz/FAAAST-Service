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

import de.fraunhofer.iosb.ilt.faaast.service.model.api.response.AbstractResponse;
import org.eclipse.digitaltwin.aas4j.v3.model.MessageTypeEnum;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultResult;


/**
 * Response class representing internal error.
 */
public class InternalErrorResponse extends AbstractResponse {

    public InternalErrorResponse(String message) {
        this.statusCode = StatusCode.SERVER_INTERNAL_ERROR;
        this.result = new DefaultResult.Builder()
                .messages(Message.builder()
                        .messageType(MessageTypeEnum.EXCEPTION)
                        .text(message)
                        .build())
                .build();
    }


    public InternalErrorResponse() {
        this("Internal Server Error");
    }
}
