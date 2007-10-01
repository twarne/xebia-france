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

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.endpoint.ConduitSelector;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.transport.Conduit;

import junit.framework.TestCase;

/**
 * @author <a href="mailto:cyrille.leclerc@pobox.com">Cyrille Le Clerc</a>
 */
public class AWSECommerceServiceTest extends TestCase {

    public void testItemSearch() throws Exception {

        String endPointUrl = "http://localhost:8080/cxf-demo/services/AWSECommerceService";
        AWSECommerceServicePortType awseCommerceServicePortType = new AWSECommerceService().getAWSECommerceServicePort();
        Map<String, Object> requestContext = ((BindingProvider) awseCommerceServicePortType).getRequestContext();

        String previousEndPointUrl = (String) requestContext.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endPointUrl);

        System.out.println("endPointUrl=" + endPointUrl + ", previousEndPointUrl=" + previousEndPointUrl);

        ItemSearchRequest itemSearchRequest = new ItemSearchRequest();
        itemSearchRequest.setSearchIndex("Books");
        itemSearchRequest.setPower("title");
        itemSearchRequest.setSort("salesrank");

        ItemSearch itemSearch = new ItemSearch();
        itemSearch.setSubscriptionId("0525E2PQ81DD7ZTWTK82");
        itemSearch.setShared(itemSearchRequest);

        /*
         * org.apache.cxf.interceptor.Fault: No conduit initiator was found for the namespace http://schemas.xmlsoap.org/soap/http.
         * 
         * java.lang.NoClassDefFoundError: com.sun.org.apache.xerces.internal.dom.ElementNSImpl at
         * java.lang.ClassLoader.defineClassImpl(Native Method) at java.lang.ClassLoader.defineClass(ClassLoader.java:228) at
         * java.security.SecureClassLoader.defineClass(SecureClassLoader.java:148) at
         * java.net.URLClassLoader.defineClass(URLClassLoader.java:557) at java.net.URLClassLoader.access$400(URLClassLoader.java:120) at
         * java.net.URLClassLoader$ClassFinder.run(URLClassLoader.java:962) at
         * java.security.AccessController.doPrivileged(AccessController.java:275) at
         * java.net.URLClassLoader.findClass(URLClassLoader.java:488) at java.lang.ClassLoader.loadClass(ClassLoader.java:607) at
         * sun.misc.Launcher$AppClassLoader.loadClass(Launcher.java:327) at java.lang.ClassLoader.loadClass(ClassLoader.java:573) at
         * java.lang.ClassLoader.defineClassImpl(Native Method) at java.lang.ClassLoader.defineClass(ClassLoader.java:228) at
         * java.security.SecureClassLoader.defineClass(SecureClassLoader.java:148) at
         * java.net.URLClassLoader.defineClass(URLClassLoader.java:557) at java.net.URLClassLoader.access$400(URLClassLoader.java:120) at
         * java.net.URLClassLoader$ClassFinder.run(URLClassLoader.java:962) at
         * java.security.AccessController.doPrivileged(AccessController.java:275) at
         * java.net.URLClassLoader.findClass(URLClassLoader.java:488) at java.lang.ClassLoader.loadClass(ClassLoader.java:607) at
         * sun.misc.Launcher$AppClassLoader.loadClass(Launcher.java:327) at java.lang.ClassLoader.loadClass(ClassLoader.java:573) at
         * java.lang.ClassLoader.defineClassImpl(Native Method) at java.lang.ClassLoader.defineClass(ClassLoader.java:228) at
         * java.security.SecureClassLoader.defineClass(SecureClassLoader.java:148) at
         * java.net.URLClassLoader.defineClass(URLClassLoader.java:557) at java.net.URLClassLoader.access$400(URLClassLoader.java:120) at
         * java.net.URLClassLoader$ClassFinder.run(URLClassLoader.java:962) at
         * java.security.AccessController.doPrivileged(AccessController.java:275) at
         * java.net.URLClassLoader.findClass(URLClassLoader.java:488) at java.lang.ClassLoader.loadClass(ClassLoader.java:607) at
         * sun.misc.Launcher$AppClassLoader.loadClass(Launcher.java:327) at java.lang.ClassLoader.loadClass(ClassLoader.java:573) at
         * com.sun.xml.messaging.saaj.soap.ver1_1.SOAPFactory1_1Impl.createFault(SOAPFactory1_1Impl.java:62) at
         * org.apache.cxf.jaxws.JaxWsClientProxy.invoke(JaxWsClientProxy.java:150) at $Proxy24.itemSearch(Unknown Source) at
         * com.amazon.webservices.awsecommerceservice._2007_07_16.AWSECommerceServiceTest.testItemSearch(AWSECommerceServiceTest.java:53) at
         * sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method) at
         * sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:64) at
         * sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43) at
         * java.lang.reflect.Method.invoke(Method.java:615) at junit.framework.TestCase.runTest(TestCase.java:154) at
         * junit.framework.TestCase.runBare(TestCase.java:127) at junit.framework.TestResult$1.protect(TestResult.java:106) at
         * junit.framework.TestResult.runProtected(TestResult.java:124) at junit.framework.TestResult.run(TestResult.java:109) at
         * junit.framework.TestCase.run(TestCase.java:118) at junit.framework.TestSuite.runTest(TestSuite.java:208) at
         * junit.framework.TestSuite.run(TestSuite.java:203) at
         * org.eclipse.jdt.internal.junit.runner.junit3.JUnit3TestReference.run(JUnit3TestReference.java:130) at
         * org.eclipse.jdt.internal.junit.runner.TestExecution.run(TestExecution.java:38) at
         * org.eclipse.jdt.internal.junit.runner.RemoteTestRunner.runTests(RemoteTestRunner.java:460) at
         * org.eclipse.jdt.internal.junit.runner.RemoteTestRunner.runTests(RemoteTestRunner.java:673) at
         * org.eclipse.jdt.internal.junit.runner.RemoteTestRunner.run(RemoteTestRunner.java:386) at
         * org.eclipse.jdt.internal.junit.runner.RemoteTestRunner.main(RemoteTestRunner.java:196)
         * 
         */
        ItemSearchResponse itemSearchResponse = awseCommerceServicePortType.itemSearch(itemSearch);
        System.out.println("Response: " + itemSearchResponse);

    }
}
