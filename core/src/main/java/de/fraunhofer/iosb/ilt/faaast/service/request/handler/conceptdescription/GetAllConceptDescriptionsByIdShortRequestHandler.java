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
package de.fraunhofer.iosb.ilt.faaast.service.request.handler.conceptdescription;

import de.fraunhofer.iosb.ilt.faaast.service.exception.MessageBusException;
import de.fraunhofer.iosb.ilt.faaast.service.model.api.paging.Page;
import de.fraunhofer.iosb.ilt.faaast.service.model.api.request.conceptdescription.GetAllConceptDescriptionsByIdShortRequest;
import de.fraunhofer.iosb.ilt.faaast.service.model.api.response.conceptdescription.GetAllConceptDescriptionsByIdShortResponse;
import de.fraunhofer.iosb.ilt.faaast.service.model.exception.PersistenceException;
import de.fraunhofer.iosb.ilt.faaast.service.model.messagebus.event.access.ElementReadEventMessage;
import de.fraunhofer.iosb.ilt.faaast.service.persistence.ConceptDescriptionSearchCriteria;
import de.fraunhofer.iosb.ilt.faaast.service.request.handler.AbstractRequestHandler;
import de.fraunhofer.iosb.ilt.faaast.service.request.handler.RequestExecutionContext;
import de.fraunhofer.iosb.ilt.faaast.service.util.LambdaExceptionHelper;
import java.util.Objects;
import org.eclipse.digitaltwin.aas4j.v3.model.ConceptDescription;


/**
 * Class to handle a
 * {@link de.fraunhofer.iosb.ilt.faaast.service.model.api.request.conceptdescription.GetAllConceptDescriptionsByIdShortRequest}
 * in the service and to send the corresponding response
 * {@link de.fraunhofer.iosb.ilt.faaast.service.model.api.response.conceptdescription.GetAllConceptDescriptionsByIdShortResponse}.
 * Is responsible for communication with the persistence and sends the corresponding events to the message bus.
 */
public class GetAllConceptDescriptionsByIdShortRequestHandler
        extends AbstractRequestHandler<GetAllConceptDescriptionsByIdShortRequest, GetAllConceptDescriptionsByIdShortResponse> {

    @Override
    public GetAllConceptDescriptionsByIdShortResponse process(GetAllConceptDescriptionsByIdShortRequest request, RequestExecutionContext context)
            throws MessageBusException, PersistenceException {
        Page<ConceptDescription> page = context.getPersistence().findConceptDescriptions(
                ConceptDescriptionSearchCriteria.builder()
                        .idShort(request.getIdShort())
                        .build(),
                request.getOutputModifier(),
                request.getPagingInfo());
        if (!request.isInternal() && Objects.nonNull(page.getContent())) {
            page.getContent().forEach(LambdaExceptionHelper.rethrowConsumer(
                    x -> context.getMessageBus().publish(ElementReadEventMessage.builder()
                            .element(x)
                            .value(x)
                            .build())));
        }
        return GetAllConceptDescriptionsByIdShortResponse.builder()
                .payload(page)
                .success()
                .build();
    }

}
