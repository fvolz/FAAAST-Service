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
package de.fraunhofer.iosb.ilt.faaast.service.endpoint.http.request.mapper.conceptdescription;

import de.fraunhofer.iosb.ilt.faaast.service.ServiceContext;
import de.fraunhofer.iosb.ilt.faaast.service.dataformat.DeserializationException;
import de.fraunhofer.iosb.ilt.faaast.service.endpoint.http.model.HttpMethod;
import de.fraunhofer.iosb.ilt.faaast.service.endpoint.http.model.HttpRequest;
import de.fraunhofer.iosb.ilt.faaast.service.endpoint.http.request.mapper.AbstractRequestMapperWithOutputModifierAndPaging;
import de.fraunhofer.iosb.ilt.faaast.service.endpoint.http.request.mapper.QueryParameters;
import de.fraunhofer.iosb.ilt.faaast.service.model.api.modifier.OutputModifier;
import de.fraunhofer.iosb.ilt.faaast.service.model.api.paging.PagingInfo;
import de.fraunhofer.iosb.ilt.faaast.service.model.api.request.conceptdescription.GetAllConceptDescriptionsByDataSpecificationReferenceRequest;
import de.fraunhofer.iosb.ilt.faaast.service.model.api.response.conceptdescription.GetAllConceptDescriptionsByDataSpecificationReferenceResponse;
import de.fraunhofer.iosb.ilt.faaast.service.model.exception.InvalidRequestException;
import java.util.Map;
import org.eclipse.digitaltwin.aas4j.v3.model.Reference;


/**
 * class to map HTTP-GET-Request path: concept-descriptions.
 */
public class GetAllConceptDescriptionsByDataSpecificationReferenceRequestMapper
        extends
        AbstractRequestMapperWithOutputModifierAndPaging<GetAllConceptDescriptionsByDataSpecificationReferenceRequest, GetAllConceptDescriptionsByDataSpecificationReferenceResponse> {

    private static final String PATTERN = "concept-descriptions";

    public GetAllConceptDescriptionsByDataSpecificationReferenceRequestMapper(ServiceContext serviceContext) {
        super(serviceContext, HttpMethod.GET, PATTERN);
    }


    @Override
    public boolean matchesUrl(HttpRequest httpRequest) {
        return super.matchesUrl(httpRequest) && httpRequest.hasQueryParameter(QueryParameters.DATA_SPECIFICATION_REF);
    }


    @Override
    public GetAllConceptDescriptionsByDataSpecificationReferenceRequest doParse(HttpRequest httpRequest, Map<String, String> urlParameters, OutputModifier outputModifier,
                                                                                PagingInfo pagingInfo)
            throws InvalidRequestException {
        try {
            return GetAllConceptDescriptionsByDataSpecificationReferenceRequest.builder()
                    .dataSpecification(deserializer.read(
                            getParameterBase64UrlEncoded(httpRequest.getQueryParameters(), QueryParameters.DATA_SPECIFICATION_REF),
                            Reference.class))
                    .build();
        }
        catch (DeserializationException e) {
            throw new InvalidRequestException(
                    String.format("error deserializing %s (value: %s)", QueryParameters.DATA_SPECIFICATION_REF,
                            httpRequest.getQueryParameter(QueryParameters.DATA_SPECIFICATION_REF)),
                    e);
        }
    }
}
