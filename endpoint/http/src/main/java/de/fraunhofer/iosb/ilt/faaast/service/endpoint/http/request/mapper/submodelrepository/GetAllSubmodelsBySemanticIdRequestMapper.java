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
package de.fraunhofer.iosb.ilt.faaast.service.endpoint.http.request.mapper.submodelrepository;

import de.fraunhofer.iosb.ilt.faaast.service.ServiceContext;
import de.fraunhofer.iosb.ilt.faaast.service.dataformat.DeserializationException;
import de.fraunhofer.iosb.ilt.faaast.service.endpoint.http.model.HttpMethod;
import de.fraunhofer.iosb.ilt.faaast.service.endpoint.http.model.HttpRequest;
import de.fraunhofer.iosb.ilt.faaast.service.endpoint.http.request.mapper.AbstractRequestMapperWithOutputModifierAndPaging;
import de.fraunhofer.iosb.ilt.faaast.service.endpoint.http.request.mapper.QueryParameters;
import de.fraunhofer.iosb.ilt.faaast.service.model.api.modifier.OutputModifier;
import de.fraunhofer.iosb.ilt.faaast.service.model.api.paging.PagingInfo;
import de.fraunhofer.iosb.ilt.faaast.service.model.api.request.submodelrepository.GetAllSubmodelsBySemanticIdRequest;
import de.fraunhofer.iosb.ilt.faaast.service.model.api.response.submodelrepository.GetAllSubmodelsBySemanticIdResponse;
import de.fraunhofer.iosb.ilt.faaast.service.model.exception.InvalidRequestException;
import java.util.Map;
import org.eclipse.digitaltwin.aas4j.v3.model.Reference;


/**
 * class to map HTTP-GET-Request path: submodels.
 */
public class GetAllSubmodelsBySemanticIdRequestMapper
        extends AbstractRequestMapperWithOutputModifierAndPaging<GetAllSubmodelsBySemanticIdRequest, GetAllSubmodelsBySemanticIdResponse> {

    private static final String PATTERN = "submodels";

    public GetAllSubmodelsBySemanticIdRequestMapper(ServiceContext serviceContext) {
        super(serviceContext, HttpMethod.GET, PATTERN);
    }


    @Override
    public boolean matchesUrl(HttpRequest httpRequest) {
        return super.matchesUrl(httpRequest) && httpRequest.hasQueryParameter(QueryParameters.SEMANTIC_ID);
    }


    @Override
    public GetAllSubmodelsBySemanticIdRequest doParse(HttpRequest httpRequest, Map<String, String> urlParameters, OutputModifier outputModifier, PagingInfo pagingInfo)
            throws InvalidRequestException {
        try {
            return GetAllSubmodelsBySemanticIdRequest.builder()
                    .semanticId(deserializer.read(
                            getParameterBase64UrlEncoded(httpRequest.getQueryParameters(), QueryParameters.SEMANTIC_ID),
                            Reference.class))
                    .build();
        }
        catch (DeserializationException e) {
            throw new InvalidRequestException(
                    String.format(
                            "error deserializing %s (value: %s)",
                            QueryParameters.SEMANTIC_ID,
                            httpRequest.getQueryParameter(QueryParameters.SEMANTIC_ID)),
                    e);
        }
    }
}
