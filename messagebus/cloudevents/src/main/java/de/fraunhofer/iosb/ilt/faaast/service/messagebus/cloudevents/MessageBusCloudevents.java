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
package de.fraunhofer.iosb.ilt.faaast.service.messagebus.cloudevents;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.fraunhofer.iosb.ilt.faaast.service.ServiceContext;
import de.fraunhofer.iosb.ilt.faaast.service.config.CoreConfig;
import de.fraunhofer.iosb.ilt.faaast.service.dataformat.json.JsonEventDeserializer;
import de.fraunhofer.iosb.ilt.faaast.service.dataformat.json.JsonEventSerializer;
import de.fraunhofer.iosb.ilt.faaast.service.exception.ConfigurationInitializationException;
import de.fraunhofer.iosb.ilt.faaast.service.exception.MessageBusException;
import de.fraunhofer.iosb.ilt.faaast.service.messagebus.MessageBus;
import de.fraunhofer.iosb.ilt.faaast.service.model.messagebus.EventMessage;
import de.fraunhofer.iosb.ilt.faaast.service.model.messagebus.SubscriptionId;
import de.fraunhofer.iosb.ilt.faaast.service.model.messagebus.SubscriptionInfo;
import de.fraunhofer.iosb.ilt.faaast.service.model.messagebus.event.change.ElementCreateEventMessage;
import de.fraunhofer.iosb.ilt.faaast.service.model.messagebus.event.change.ValueChangeEventMessage;
import de.fraunhofer.iosb.ilt.faaast.service.model.value.PropertyValue;
import de.fraunhofer.iosb.ilt.faaast.service.util.Ensure;
import io.cloudevents.CloudEvent;
import io.cloudevents.core.builder.CloudEventBuilder;
import io.cloudevents.jackson.JsonFormat;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ScanResult;
import java.lang.reflect.Modifier;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


/**
 * MessageBusCloudevents: Implements the external MessageBus interface and publishes/dispatchesEventMessages.
 */
public class MessageBusCloudevents implements MessageBus<MessageBusCloudeventsConfig> {

    private final Map<SubscriptionId, SubscriptionInfo> subscriptions;
    private final JsonEventSerializer serializer;
    private final JsonEventDeserializer deserializer;
    private MessageBusCloudeventsConfig config;
    private PahoClient client;
    private ObjectMapper objectMapper;

    public MessageBusCloudevents() {
        subscriptions = new ConcurrentHashMap<>();
        serializer = new JsonEventSerializer();
        deserializer = new JsonEventDeserializer();
    }


    @Override
    public MessageBusCloudeventsConfig asConfig() {
        return config;
    }


    @Override
    public void init(CoreConfig coreConfig, MessageBusCloudeventsConfig config, ServiceContext serviceContext) throws ConfigurationInitializationException {
        this.config = config;
        client = new PahoClient(config);
        this.objectMapper = new ObjectMapper();
        objectMapper.registerModule(JsonFormat.getCloudEventJacksonModule());
    }


    @Override
    public void publish(EventMessage message) throws MessageBusException {
        try {
            CloudEvent cloudMessage = createCloudevent(message);
            if (cloudMessage != null) {
                client.publish(config.getTopicPrefix(), objectMapper.writeValueAsString(cloudMessage));
            }
        }
        catch (Exception e) {
            throw new MessageBusException(String.format("Error publishing event via Cloudevents MQTT message bus for message type {s}", message.getClass()), e);
        }
    }


    private CloudEvent createCloudevent(EventMessage message) throws URISyntaxException, JsonProcessingException {
        if (message.getClass().isAssignableFrom(ElementCreateEventMessage.class)) {
            if (message.getElement().toString().contains("ASSET_ADMINISTRATION_SHELL")) {
                return CloudEventBuilder.v1()
                        .withType("org.factory-x.events.v1." + "ElementCreatedEvent")
                        .withSource(new URI("uri:aas:shells/" +
                                Base64.getEncoder().encodeToString(message.getElement().getKeys().get(0).getValue().getBytes())))
                        .withId(UUID.randomUUID().toString())
                        .withTime(OffsetDateTime.now())
                        .withDataContentType("application/json")
                        .withDataSchema(new URI("https://api.swaggerhub.com/domains/Plattform_i40/Part1-MetaModel-Schemas/V3.1.0#/components/schemas/AssetAdministrationShell"))
                        .withData(objectMapper.writeValueAsString(((ElementCreateEventMessage) message).getValue()).getBytes())
                        .build();
            }
            else {
                return CloudEventBuilder.v1()
                        .withType("org.factory-x.events.v1." + "ElementCreatedEvent")
                        .withSource(new URI("uri:submodels/" +
                                Base64.getEncoder().encodeToString(message.getElement().getKeys().get(0).getValue().getBytes())))
                        .withId(UUID.randomUUID().toString())
                        .withTime(OffsetDateTime.now())
                        .withDataContentType("application/json")
                        .withDataSchema(new URI("https://api.swaggerhub.com/domains/Plattform_i40/Part1-MetaModel-Schemas/V3.1.0#/components/schemas/Submodel"))
                        .withData(objectMapper.writeValueAsString(((ElementCreateEventMessage) message).getValue()).getBytes())
                        .build();
            }
        }
        else if (message.getClass().isAssignableFrom(ValueChangeEventMessage.class)) {
            return CloudEventBuilder.v1()
                    .withType("org.factory-x.events.v1." + "ValueChangedEvent")
                    .withSource(new URI("uri:submodels/" +
                            Base64.getEncoder().encodeToString(message.getElement().getKeys().get(0).getValue().getBytes())
                            + "/submodel-elements/" + message.getElement().getKeys().get(1).getValue()))
                    .withId(UUID.randomUUID().toString())
                    .withTime(OffsetDateTime.now())
                    .withDataContentType("application/json")
                    .withDataSchema(new URI("https://api.swaggerhub.com/domains/Plattform_i40/Part1-MetaModel-Schemas/V3.1.0#/components/schemas/Property"))
                    .withData(
                            objectMapper.writeValueAsString(((PropertyValue) ((ValueChangeEventMessage) message)
                                    .getNewValue()).getValue().getValue()).getBytes())
                    .build();
        }
        else {
            return null;
        }

    }


    @Override
    public void start() throws MessageBusException {
        client.start();
    }


    @Override
    public void stop() {
        client.stop();
    }


    @Override
    public SubscriptionId subscribe(SubscriptionInfo subscriptionInfo) {
        Ensure.requireNonNull(subscriptionInfo, "subscriptionInfo must be non-null");
        subscriptionInfo.getSubscribedEvents()
                .forEach(x -> determineEvents((Class<? extends EventMessage>) x).stream()
                        .forEach(e -> client.subscribe(config.getTopicPrefix() + e.getSimpleName(), (t, message) -> {
                            EventMessage event = deserializer.read(message.toString(), e);
                            if (subscriptionInfo.getFilter().test(event.getElement())) {
                                subscriptionInfo.getHandler().accept(event);
                            }
                        })));

        SubscriptionId subscriptionId = new SubscriptionId();
        subscriptions.put(subscriptionId, subscriptionInfo);
        return subscriptionId;
    }


    private List<Class<EventMessage>> determineEvents(Class<? extends EventMessage> messageType) {
        try (ScanResult scanResult = new ClassGraph().acceptPackages("de.fraunhofer.iosb.ilt.faaast.service.model.messagebus.event")
                .enableClassInfo().scan()) {
            if (Modifier.isAbstract(messageType.getModifiers())) {
                return scanResult
                        .getSubclasses(messageType.getName())
                        .filter(x -> !x.isAbstract())
                        .loadClasses(EventMessage.class);
            }
            else {
                List<Class<EventMessage>> list = new ArrayList<>();
                list.add((Class<EventMessage>) messageType);
                return list;
            }
        }
    }


    @Override
    public void unsubscribe(SubscriptionId id) {
        SubscriptionInfo info = subscriptions.get(id);
        Ensure.requireNonNull(info.getSubscribedEvents(), "subscriptionInfo must be non-null");
        subscriptions.get(id).getSubscribedEvents().stream().forEach(a -> //find all events for given abstract or event
        determineEvents((Class<? extends EventMessage>) a).stream().forEach(e -> //unsubscribe from all events
        client.unsubscribe(config.getTopicPrefix() + e.getSimpleName())));
        subscriptions.remove(id);
    }
}
