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
package de.fraunhofer.iosb.ilt.faaast.service.endpoint.opcua.prosys;

import com.prosysopc.ua.StatusException;
import com.prosysopc.ua.nodes.UaMethod;
import com.prosysopc.ua.nodes.UaNode;
import com.prosysopc.ua.nodes.UaValueNode;
import com.prosysopc.ua.nodes.UaVariable;
import com.prosysopc.ua.server.ServiceContext;
import com.prosysopc.ua.server.io.IoManagerListener;
import com.prosysopc.ua.stack.builtintypes.ByteString;
import com.prosysopc.ua.stack.builtintypes.DataValue;
import com.prosysopc.ua.stack.builtintypes.DateTime;
import com.prosysopc.ua.stack.builtintypes.LocalizedText;
import com.prosysopc.ua.stack.builtintypes.NodeId;
import com.prosysopc.ua.stack.builtintypes.UnsignedInteger;
import com.prosysopc.ua.stack.builtintypes.Variant;
import com.prosysopc.ua.stack.core.AccessLevelType;
import com.prosysopc.ua.stack.core.AttributeWriteMask;
import com.prosysopc.ua.stack.core.StatusCodes;
import com.prosysopc.ua.stack.core.TimestampsToReturn;
import com.prosysopc.ua.stack.utils.NumericRange;
import io.adminshell.aas.v3.model.Blob;
import io.adminshell.aas.v3.model.MultiLanguageProperty;
import io.adminshell.aas.v3.model.Property;
import io.adminshell.aas.v3.model.Range;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Class that listens to I/O-Events
 *
 * @author Tino Bischoff
 */
public class AasServiceIoManagerListener implements IoManagerListener {

    private static final Logger logger = LoggerFactory.getLogger(AasServiceIoManagerListener.class);

    private final OpcUaEndpoint endpoint;
    private final AasServiceNodeManager nodeManager;

    /**
     * Creates a new instance of AasServiceIoManagerListener
     *
     * @param ep the associated endpoint
     * @param nodeMan the associated NodeManager
     */
    public AasServiceIoManagerListener(OpcUaEndpoint ep, AasServiceNodeManager nodeMan) {
        endpoint = ep;
        nodeManager = nodeMan;
    }


    /**
     * Notification of a read request for user access level attribute of a node.
     *
     * @param sc The serviceContext of the client connection used to call this
     *            service.
     * @param nodeid The NodeId of node to read.
     * @param uv The node object to read. If the node is not available this may
     *            be null.
     * @return the user access level of the node, or null if the event is not
     *         handled by the listener.
     */
    @Override
    public AccessLevelType onGetUserAccessLevel(ServiceContext sc, NodeId nodeid, UaVariable uv) {
        if (uv == null) {
            throw new IllegalArgumentException("UaVariable is null!");
        }

        return uv.getAccessLevel();
    }


    /**
     * Notification of a read request for user executable attribute of a node.
     *
     * @param sc The serviceContext of the client connection used to call this
     *            service.
     * @param nodeid The NodeId of node to read.
     * @param um The node object to read. If the node is not available this may
     *            be null.
     * @return The user executable attribute of the node, or null if the event
     *         is not handled by the listener
     */
    @Override
    public Boolean onGetUserExecutable(ServiceContext sc, NodeId nodeid, UaMethod um) {
        // Enable execution of all methods that are allowed by default
        return true;
    }


    /**
     * Notification of a read request for user write mask attribute of a node.
     *
     * @param sc The serviceContext of the client connection used to call this
     *            service.
     * @param nodeid The NodeId of node to read.
     * @param uanode The node object to read. If the node is not available this
     *            may be null.
     * @return the user write mask of the node, or null if the event is not
     *         handled by the listener.
     */
    @Override
    public AttributeWriteMask onGetUserWriteMask(ServiceContext sc, NodeId nodeid, UaNode uanode) {
        // Enable writing to everything that is allowed by default
        // The WriteMask defines the writable attributes, except for Value,
        // which is controlled by UserAccessLevel (above)

        // The following would deny write access for anonymous users:
        // if
        // (serviceContext.getSession().getUserIdentity().getType().equals(
        // UserTokenType.Anonymous))
        // return AttributeWriteMask.of();
        return AttributeWriteMask.of(AttributeWriteMask.Fields.values());
    }


    /**
     * Notification of a read request for a node attribute, except for the Value
     * attribute of a variable node (which goes to onReadValue). The
     * notification is sent after the value is read from the node or other data
     * source, depending on the actual IoManager.
     *
     * @param sc The serviceContext of the client connection used to call this
     *            service.
     * @param nodeid The NodeId of node to read.
     * @param uanode The node object to read. If the node is not available this
     *            may be null.
     * @param ui The attribute to read.
     * @param dv The data value being returned.
     * @return true if the request was handled by the listener.
     * @throws StatusException Throw the exception to cancel the request, for
     *             example due to insufficient user rights. Possible result codes:
     *             Bad_NodeIdInvalid, Bad_NodeIdUnknown, Bad_AttributeIdInvalid,
     *             Bad_NotReadable, Bad_UserAccessDenied
     */
    @Override
    public boolean onReadNonValue(ServiceContext sc, NodeId nodeid, UaNode uanode, UnsignedInteger ui, DataValue dv) throws StatusException {
        //        if (uanode != null) {
        //            logger.trace("onReadNonValue: Node BrowseName" + uanode.getBrowseName());
        //        }

        return false;
    }


    /**
     * Notification of a read request for the Value attribute of a Variable
     * node.
     *
     * @param sc The serviceContext of the client connection used to call this
     *            service.
     * @param nodeId The NodeId of node to read.
     * @param uvn The node object to read. If the node is not available this may
     *            be null.
     * @param nr The requested index range for an array value. May be null.
     * @param ttr Which timestamps were requested by the client.
     * @param dt Minimum value of the ServerTimestamp of the value to be read.
     *            If there is no value available that is new enough already available (in
     *            the server cache), the server should attempt to read a new value from the
     *            actual data source, instead of using the cached value. If a new value
     *            cannot be read, the best value available is returned. If minTimestamp ==
     *            DateTime.MAX_VALUE a new value should be read from the source.
     * @param dv The data value to return. Set Value, and for Value attribute
     *            also StatusCode and the Timestamps.
     * @return true if the request was handled by the listener.
     * @throws StatusException Throw the exception to cancel the request, for
     *             example due to insufficient user rights. Possible result codes:
     *             Bad_NodeIdInvalid, Bad_NodeIdUnknown, Bad_IndexRangeInvalid,
     *             Bad_IndexRangeNoData, Bad_NotReadable, Bad_UserAccessDenied
     */
    @Override
    public boolean onReadValue(ServiceContext sc, NodeId nodeId, UaValueNode uvn, NumericRange nr, TimestampsToReturn ttr, DateTime dt, DataValue dv) throws StatusException {
        //        if (logger.isDebugEnabled()) {
        //            logger.debug("onReadValue: nodeId=" + nodeId + (uvn != null ? " node=" + uvn.getBrowseName() : ""));
        //        }

        return false;
    }


    /**
     * Notification of a write request for the value of a single node attribute,
     * except for the Value of a variable node. The method is called before
     * write actually takes place. So you can cancel the write, for example, if
     * the user is not allowed to write to the attribute.
     *
     * @param sc The serviceContext of the client connection used to call this
     *            service.
     * @param nodeid The NodeId of node to write.
     * @param uanode The node object to write. If the node is not available this
     *            may be null.
     * @param ui The attribute to write.
     * @param dv The data value to write.
     * @return rue if the value was written to the source and you do not want
     *         any other operations to continue writing it - including it being written
     *         to the node (if you are using an IoManagerUaNode). If you return false,
     *         other listeners and the IoManager will get called with the write request.
     * @throws StatusException If the write fails. Expected result codes:
     *             Bad_NodeIdInvalid, Bad_NodeIdUnknown, Bad_AttributeIdInvalid,
     *             Bad_IndexRangeInvalid, Bad_IndexRangeNoData, Bad_DataEncodingInvalid,
     *             Bad_DataEncodingUnsupported, Bad_NotWriteable, Bad_UserAccessDenied,
     *             Bad_TypeMismatch
     */
    @Override
    public boolean onWriteNonValue(ServiceContext sc, NodeId nodeid, UaNode uanode, UnsignedInteger ui, DataValue dv) throws StatusException {
        if (uanode != null) {
            logger.trace("onWriteNonValue: Node BrowseName" + uanode.getBrowseName());
        }
        return false;
    }


    /**
     * Notification of a write request for the Value attribute of a Variable
     * node. The method is called before write actually takes place. So you can
     * cancel the write, for example, if the user is not allowed to write to the
     * value.
     *
     * @param sc The serviceContext of the client connection used to call this
     *            service.
     * @param nodeId The NodeId of node to write.
     * @param uvn The node object to write. If the node is not available this
     *            may be null.
     * @param indexRange The index range to set for an array value. May be null.
     * @param dv The data value to write.
     * @return true if the value was written to the source and you do not want
     *         any other operations to continue writing it - including it being written
     *         to the node (if you are using an IoManagerUaNode). If you return false,
     *         other listeners and the IoManager will get called with the write request.
     * @throws StatusException If the write fails. Expected result codes:
     *             Bad_NodeIdInvalid, Bad_NodeIdUnknown, Bad_AttributeIdInvalid,
     *             Bad_IndexRangeInvalid, Bad_IndexRangeNoData, Bad_DataEncodingInvalid,
     *             Bad_DataEncodingUnsupported, Bad_NotWriteable, Bad_UserAccessDenied,
     *             Bad_OutOfRange, Bad_TypeMismatch, Bad_WriteNotSupported
     */
    @Override
    public boolean onWriteValue(ServiceContext sc, NodeId nodeId, UaValueNode uvn, NumericRange indexRange, DataValue dv) throws StatusException {
        logger.info(
                "onWriteValue: nodeId=" + nodeId + (uvn != null ? " node=" + uvn.getBrowseName() : "") + (indexRange != null ? " indexRange=" + indexRange : "") + " value=" + dv);

        try {
            if (endpoint == null) {
                logger.warn("onWriteValue: no Endpoint available");
            }
            else if (dv.getStatusCode().isNotGood()) {
                logger.warn("onWriteValue: StatusCode not good");
            }
            else {
                //String oldValue = "";
                //if (uvn.getValue().getStatusCode().isGood()) {
                //    oldValue = uvn.getValue().getValue().getValue().toString();
                //}

                boolean rv;
                SubmodelElementData data = nodeManager.getAasData(nodeId);
                if (data != null) {
                    if (data.getType() == null) {
                        logger.warn("onWriteValue: Node " + nodeId + ": unkown type");
                        rv = false;
                    }
                    else
                        switch (data.getType()) {
                            case PROPERTY_VALUE: {
                                Property aasProp = (Property) data.getSubmodelElement();
                                String newValue = dv.getValue().getValue().toString();
                                aasProp.setValue(newValue);
                                rv = endpoint.writeValue(aasProp, data.getSubmodel());
                                break;
                            }
                            case RANGE_MIN: {
                                Range aasRange = (Range) data.getSubmodelElement();
                                String newValue = dv.getValue().getValue().toString();
                                aasRange.setMin(newValue);
                                rv = endpoint.writeValue(aasRange, data.getSubmodel());
                                break;
                            }
                            case RANGE_MAX: {
                                Range aasRange = (Range) data.getSubmodelElement();
                                String newValue = dv.getValue().getValue().toString();
                                aasRange.setMax(newValue);
                                rv = endpoint.writeValue(aasRange, data.getSubmodel());
                                break;
                            }
                            case BLOB_VALUE: {
                                Blob aasBlob = (Blob) data.getSubmodelElement();
                                ByteString bs = (ByteString) dv.getValue().getValue();
                                aasBlob.setValue(ByteString.asByteArray(bs));
                                rv = endpoint.writeValue(aasBlob, data.getSubmodel());
                                break;
                            }
                            case MULTI_LANGUAGE_VALUE: {
                                MultiLanguageProperty aasMultiProp = (MultiLanguageProperty) data.getSubmodelElement();
                                //String newValue = dv.getValue().getValue().toString();
                                Variant variant = dv.getValue();
                                if (variant.isArray()) {
                                    aasMultiProp.setValues(ValueConverter.getLangStringSetFromLocalizedText((LocalizedText[]) variant.getValue()));
                                }

                                rv = endpoint.writeValue(aasMultiProp, data.getSubmodel());
                                break;
                            }
                            default:
                                logger.warn("onWriteValue: Node " + nodeId + ": unkown type");
                                rv = false;
                                break;
                        }
                }
                else {
                    logger.warn("onWriteValue: Node " + nodeId + ": SubmodelElementData not found");
                    rv = false;
                }

                if (rv) {
                    logger.debug("onWriteValue: NodeId " + nodeId.toString() + " written successfully");
                }
                else {
                    logger.info("onWriteValue: NodeId " + nodeId.toString() + " write failed");
                    throw new StatusException(StatusCodes.Bad_InternalError);
                }
            }
        }
        catch (Throwable ex) {
            logger.error("onWriteValue Exception", ex);
            throw new StatusException(ex.getMessage(), StatusCodes.Bad_UnexpectedError);
        }

        // We return true here. So, the value is not written to the node here. 
        // The node is written in the callback from the MessageBus.
        return true;
    }

}
