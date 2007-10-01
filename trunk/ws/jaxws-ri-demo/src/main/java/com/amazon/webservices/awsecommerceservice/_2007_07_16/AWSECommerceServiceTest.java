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
import javax.xml.ws.Holder;

import junit.framework.TestCase;

/**
 * @author <a href="mailto:cyrille.leclerc@pobox.com">Cyrille Le Clerc</a>
 */
public class AWSECommerceServiceTest extends TestCase {

    public void testItemSearch() throws Exception {

        String endPointUrl = "http://localhost:8080/jaxws-ri-demo/services/AWSECommerceService";
        AWSECommerceServicePortType awseCommerceServicePortType = new AWSECommerceService().getAWSECommerceServicePort();
        Map<String, Object> requestContext = ((BindingProvider) awseCommerceServicePortType).getRequestContext();

        String previousEndPointUrl = (String) requestContext.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endPointUrl);

        System.out.println("endPointUrl=" + endPointUrl + ", previousEndPointUrl=" + previousEndPointUrl);

        ItemSearchRequest shared = new ItemSearchRequest();
        shared.setSearchIndex("Books");
        shared.setPower("title");
        shared.setSort("salesrank");

        ItemSearch itemSearch = new ItemSearch();
        itemSearch.setSubscriptionId("0525E2PQ81DD7ZTWTK82");
        itemSearch.setShared(shared);

        String marketplaceDomain = "marketplaceDomain";
        String awsAccessKeyId = "awsAccessKeyId";
        String subscriptionId = "subscriptionId";
        String associateTag = "associateTag";
        String xmlEscaping = "xmlEscaping";
        String validate = "validate";
        java.util.List<ItemSearchRequest> itemsSearchRequests = null;
        Holder<OperationRequest> operationRequest = null;
        Holder<java.util.List<Items>> items = new Holder<java.util.List<Items>>();
        awseCommerceServicePortType.itemSearch(marketplaceDomain, awsAccessKeyId, subscriptionId, associateTag, xmlEscaping, validate,
                shared, itemsSearchRequests, operationRequest, items);
        System.out.println("Response: " + items.value);

    }
}
