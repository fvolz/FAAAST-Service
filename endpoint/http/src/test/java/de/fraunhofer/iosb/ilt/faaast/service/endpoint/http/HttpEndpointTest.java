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
package de.fraunhofer.iosb.ilt.faaast.service.endpoint.http;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.fraunhofer.iosb.ilt.faaast.service.ServiceContext;
import de.fraunhofer.iosb.ilt.faaast.service.config.CoreConfig;
import de.fraunhofer.iosb.ilt.faaast.service.endpoint.http.serialization.HttpJsonDeserializer;
import de.fraunhofer.iosb.ilt.faaast.service.model.AASFull;
import de.fraunhofer.iosb.ilt.faaast.service.model.v3.api.StatusCode;
import de.fraunhofer.iosb.ilt.faaast.service.model.v3.api.response.GetAllAssetAdministrationShellsResponse;
import io.adminshell.aas.v3.model.AssetAdministrationShell;
import java.net.ServerSocket;
import java.util.List;
import java.util.Map;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.util.StringRequestContent;
import org.eclipse.jetty.http.HttpMethod;
import org.eclipse.jetty.http.HttpStatus;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class HttpEndpointTest {

    private static Logger logger = LoggerFactory.getLogger(HttpEndpointTest.class);
    private static final String HOST = "localhost";
    private static int port;
    private static HttpClient client;
    private static HttpEndpoint endpoint;
    private static ServiceContext serviceContext;
    private static HttpJsonDeserializer deserializer;

    @BeforeClass
    public static void init() throws Exception {
        try (ServerSocket serverSocket = new ServerSocket(0)) {
            Assert.assertNotNull(serverSocket);
            Assert.assertTrue(serverSocket.getLocalPort() > 0);
            port = serverSocket.getLocalPort();
        }
        deserializer = new HttpJsonDeserializer();
        HttpEndpointConfig endpointConfig = new HttpEndpointConfig();
        endpointConfig.setPort(port);
        serviceContext = mock(ServiceContext.class);
        endpoint = new HttpEndpoint();
        endpoint.init(CoreConfig.builder()
                .build(),
                endpointConfig,
                serviceContext);
        endpoint.start();
        client = new HttpClient();
        client.start();
    }


    @AfterClass
    public static void cleanUp() {
        if (client != null) {
            try {
                client.stop();
            }
            catch (Exception ex) {
                logger.info("error stopping HTTP client", ex);
            }
        }
        if (endpoint != null) {
            try {
                endpoint.stop();
            }
            catch (Exception ex) {
                logger.info("error stopping HTTP endpoint", ex);
            }
        }
    }


    public ContentResponse execute(HttpMethod method, String path, Map<String, String> parameters) throws Exception {
        return execute(method, path, parameters, null, null);
    }


    public ContentResponse execute(HttpMethod method, String path) throws Exception {
        return execute(method, path, null, null, null);
    }


    public ContentResponse execute(HttpMethod method, String path, Map<String, String> parameters, String body, String contentType) throws Exception {
        Request request = client.newRequest(HOST, port)
                .path(path);
        if (parameters != null) {
            for (Map.Entry<String, String> parameter: parameters.entrySet()) {
                request = request.param(parameter.getKey(), parameter.getValue());
            }
        }
        if (body != null) {
            if (contentType != null) {
                request = request.body(new StringRequestContent(contentType, body));
            }
            else {
                request = request.body(new StringRequestContent(body));
            }
        }
        return request.send();
    }


    @Test
    public void testInvalidUrl() throws Exception {
        ContentResponse response = execute(HttpMethod.GET, "/foo/bar");
        Assert.assertEquals(HttpStatus.BAD_REQUEST_400, response.getStatus());
    }


    @Test
    @Ignore
    public void testGetAllAssetAdministrationShells() throws Exception {
        List<AssetAdministrationShell> expectedPayload = List.of(AASFull.AAS_1);
        when(serviceContext.execute(any())).thenReturn(GetAllAssetAdministrationShellsResponse.builder()
                .statusCode(StatusCode.Success)
                .payload(expectedPayload)
                .build());
        ContentResponse response = execute(HttpMethod.GET, "/shells");
        Assert.assertEquals(HttpStatus.OK_200, response.getStatus());
        // server not returning character encoding
        List<AssetAdministrationShell> actualPayload = deserializer.readList(new String(response.getContent()), AssetAdministrationShell.class);
        Assert.assertEquals(expectedPayload, actualPayload);
    }
}
