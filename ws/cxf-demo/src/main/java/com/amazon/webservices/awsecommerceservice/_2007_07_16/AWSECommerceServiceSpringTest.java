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

import org.springframework.test.AbstractDependencyInjectionSpringContextTests;

/**
 * @author <a href="mailto:cyrille.leclerc@pobox.com">Cyrille Le Clerc</a>
 */
public class AWSECommerceServiceSpringTest extends AbstractDependencyInjectionSpringContextTests {

    protected AWSECommerceServicePortType awseCommerceServicePortType;

    protected String[] getConfigLocations() {
        return new String[]{"classpath:com/amazon/webservices/awsecommerceservice/_2007_07_16/beans.xml"};
    }

    public void setAwseCommerceServicePortType(AWSECommerceServicePortType awseCommerceServicePortType) {
        this.awseCommerceServicePortType = awseCommerceServicePortType;
    }

    public void testItemSearch() throws Exception {

        assertNotNull(this.awseCommerceServicePortType);
        for (String beanName : getApplicationContext().getBeanDefinitionNames()) {
            System.out.println(beanName);
        }
        Map<String, Object> requestContext = ((BindingProvider) awseCommerceServicePortType).getRequestContext();

        String endPointUrl = (String) requestContext.get(BindingProvider.ENDPOINT_ADDRESS_PROPERTY);

        assertEquals("endpointUrl", "http://localhost:8080/cxf-demo/services/AWSECommerceService", endPointUrl);

        ItemSearchRequest itemSearchRequest = new ItemSearchRequest();
        itemSearchRequest.setSearchIndex("Books");
        itemSearchRequest.setPower("title");
        itemSearchRequest.setSort("salesrank");

        ItemSearch itemSearch = new ItemSearch();
        itemSearch.setSubscriptionId("0525E2PQ81DD7ZTWTK82");
        itemSearch.setShared(itemSearchRequest);

        ItemSearchResponse itemSearchResponse = awseCommerceServicePortType.itemSearch(itemSearch);
        System.out.println("RESPONSE : " + itemSearchResponse);

    }

}
