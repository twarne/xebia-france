/*
 * Copyright 2007 Xebia and the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.amazon.webservices.awsecommerceservice._2007_07_16;

import java.util.Map;

import javax.xml.ws.BindingProvider;

import junit.framework.TestCase;

/**
 * @author <a href="mailto:cyrille.leclerc@pobox.com">Cyrille Le Clerc</a>
 */
public class AWSECommerceServiceTest extends TestCase {

    public void testItemSearch() throws Exception {

        String endPointUrl = "http://localhost:9080/jaxws-ri-demo/services/AWSECommerceService";
        AWSECommerceServicePortType awseCommerceServicePortType = new AWSECommerceService().getAWSECommerceServicePort();

        Map<String, Object> requestContext = ((BindingProvider) awseCommerceServicePortType).getRequestContext();

        String previousEndPointUrl = (String) requestContext.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endPointUrl);

        System.out.println("endPointUrl=" + endPointUrl + ", previousEndPointUrl=" + previousEndPointUrl);

    }
}
