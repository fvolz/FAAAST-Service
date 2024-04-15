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
package de.fraunhofer.iosb.ilt.faaast.service.assetconnection;

/**
 * Default config for AssetOperationProvider implementations. Implement this interface to create specialized
 * configuration class for concrete AssetOperationProvider implementations, e.g. containing connection-specific
 * properties.
 */
public interface AssetOperationProviderConfig extends AssetProviderConfig {

    public ArgumentValidationMode getInputValidationMode();


    public void setInputValidationMode(ArgumentValidationMode mode);


    public ArgumentValidationMode getInoutputValidationMode();


    public void setInoutputValidationMode(ArgumentValidationMode mode);


    public ArgumentValidationMode getOutputValidationMode();


    public void setOutputValidationMode(ArgumentValidationMode mode);
}
