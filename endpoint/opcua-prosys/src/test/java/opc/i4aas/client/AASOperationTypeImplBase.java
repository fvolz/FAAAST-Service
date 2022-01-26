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
package opc.i4aas.client;

import com.prosysopc.ua.MethodArgumentTransformer;
import com.prosysopc.ua.MethodCallStatusException;
import com.prosysopc.ua.ServiceException;
import com.prosysopc.ua.TypeDefinitionId;
import com.prosysopc.ua.client.AddressSpace;
import com.prosysopc.ua.nodes.Mandatory;
import com.prosysopc.ua.nodes.UaMethod;
import com.prosysopc.ua.stack.builtintypes.LocalizedText;
import com.prosysopc.ua.stack.builtintypes.NodeId;
import com.prosysopc.ua.stack.builtintypes.QualifiedName;
import com.prosysopc.ua.stack.builtintypes.Variant;
import com.prosysopc.ua.stack.transport.AsyncResult;
import opc.i4aas.AASOperationType;


/**
 * Generated on 2022-01-26 16:50:24
 */
@TypeDefinitionId("nsu=http://opcfoundation.org/UA/I4AAS/V3/;i=1015")
public abstract class AASOperationTypeImplBase extends AASSubmodelElementTypeImpl implements AASOperationType {
    protected AASOperationTypeImplBase(AddressSpace addressSpace, NodeId nodeId,
            QualifiedName browseName, LocalizedText displayName) {
        super(addressSpace, nodeId, browseName, displayName);
    }


    @Mandatory
    @Override
    public UaMethod getOperationNode() {
        QualifiedName browseName = getQualifiedName("http://opcfoundation.org/UA/I4AAS/V3/", "Operation");
        return (UaMethod) getComponent(browseName);
    }


    @Override
    public void operation() throws MethodCallStatusException, ServiceException {
        NodeId methodId = getComponentId(getQualifiedName("http://opcfoundation.org/UA/I4AAS/V3/", "Operation"));
        call(methodId);
    }


    public AsyncResult<Void> operationAsync() {
        NodeId methodId = getComponentId(getQualifiedName("http://opcfoundation.org/UA/I4AAS/V3/", "Operation"));
        return callAsync(methodId, new MethodArgumentTransformer<Void>() {
            @Override
            public Void fromVariantArray(Variant[] values) {
                return null;
            }
        });
    }
}
