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
package de.fraunhofer.iosb.ilt.faaast.service.model.api.response;

import de.fraunhofer.iosb.ilt.faaast.service.model.api.BaseResponseWithPayload;
import io.adminshell.aas.v3.model.Submodel;
import java.util.List;


/**
 * Chapter 6.3.4
 */
public class GetAllSubmodelsBySemanticIdResponse extends BaseResponseWithPayload<List<Submodel>> {
    public static GetAllSubmodelsBySemanticIdResponse.Builder builder() {
        return new GetAllSubmodelsBySemanticIdResponse.Builder();
    }

    public static class Builder extends AbstractBuilder<GetAllSubmodelsBySemanticIdResponse, GetAllSubmodelsBySemanticIdResponse.Builder> {

        @Override
        protected GetAllSubmodelsBySemanticIdResponse.Builder getSelf() {
            return this;
        }


        public GetAllSubmodelsBySemanticIdResponse.Builder payload(List<Submodel> value) {
            getBuildingInstance().setPayload(value);
            return getSelf();
        }


        @Override
        protected GetAllSubmodelsBySemanticIdResponse newBuildingInstance() {
            return new GetAllSubmodelsBySemanticIdResponse();
        }
    }
}