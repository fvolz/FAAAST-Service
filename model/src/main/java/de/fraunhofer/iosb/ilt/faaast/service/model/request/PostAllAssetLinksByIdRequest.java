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
package de.fraunhofer.iosb.ilt.faaast.service.model.request;

import de.fraunhofer.iosb.ilt.faaast.service.model.api.BaseRequest;
import de.fraunhofer.iosb.ilt.faaast.service.model.api.response.PostAllAssetLinksByIdResponse;
import io.adminshell.aas.v3.model.IdentifierKeyValuePair;
import io.adminshell.aas.v3.model.builder.ExtendableBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


/**
 * Chapter 7.2.4
 */
public class PostAllAssetLinksByIdRequest extends BaseRequest<PostAllAssetLinksByIdResponse> {
    private String aasIdentifier;
    private List<IdentifierKeyValuePair> assetLinks;

    public PostAllAssetLinksByIdRequest() {
        this.assetLinks = new ArrayList<>();
    }


    public String getAasIdentifier() {
        return aasIdentifier;
    }


    public void setAasIdentifier(String aasIdentifier) {
        this.aasIdentifier = aasIdentifier;
    }


    public List<IdentifierKeyValuePair> getAssetLinks() {
        return assetLinks;
    }


    public void setAssetLinks(List<IdentifierKeyValuePair> assetLinks) {
        this.assetLinks = assetLinks;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        PostAllAssetLinksByIdRequest that = (PostAllAssetLinksByIdRequest) o;
        return Objects.equals(aasIdentifier, that.aasIdentifier)
                && Objects.equals(assetLinks, that.assetLinks);
    }


    @Override
    public int hashCode() {
        return Objects.hash(aasIdentifier, assetLinks);
    }


    public static Builder builder() {
        return new Builder();
    }

    public static abstract class AbstractBuilder<T extends PostAllAssetLinksByIdRequest, B extends AbstractBuilder<T, B>> extends ExtendableBuilder<T, B> {
        public B aasIdentifier(String value) {
            getBuildingInstance().setAasIdentifier(value);
            return getSelf();
        }


        public B assetLink(IdentifierKeyValuePair value) {
            getBuildingInstance().getAssetLinks().add(value);
            return getSelf();
        }


        public B assetLinks(List<IdentifierKeyValuePair> value) {
            getBuildingInstance().setAssetLinks(value);
            return getSelf();
        }
    }

    public static class Builder extends AbstractBuilder<PostAllAssetLinksByIdRequest, Builder> {

        @Override
        protected Builder getSelf() {
            return this;
        }


        @Override
        protected PostAllAssetLinksByIdRequest newBuildingInstance() {
            return new PostAllAssetLinksByIdRequest();
        }
    }
}