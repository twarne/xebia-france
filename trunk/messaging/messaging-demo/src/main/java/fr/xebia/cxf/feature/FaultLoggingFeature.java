/*
 * Copyright 2002-2008 the original author or authors.
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
package fr.xebia.cxf.feature;

import org.apache.cxf.Bus;
import org.apache.cxf.feature.AbstractFeature;
import org.apache.cxf.interceptor.InterceptorProvider;

import fr.xebia.cxf.interceptor.FaultLoggingOutInterceptor;

/**
 * This class is used to control message faults logging. 
 * By attaching this feature to an endpoint, you
 * enable faults logging. If this feature is present, an endpoint will log 
 * outgoing exceptions.
 * <pre>
 * <![CDATA[
    <jaxws:endpoint ...>
      <jaxws:features>
       <bean class="fr.xebia.cxf.interceptor.FaultLoggingOutInterceptor"/>
      </jaxws:features>
    </jaxws:endpoint>
  ]]>
  </pre>
 *  
 * @author <a href="mailto:cyrille.leclerc@pobox.com">Cyrille Le Clerc</a>
 */
public class FaultLoggingFeature extends AbstractFeature {
    
    private final static FaultLoggingOutInterceptor OUT = new FaultLoggingOutInterceptor();
    
    @Override
    protected void initializeProvider(InterceptorProvider provider, Bus bus) {
        super.initializeProvider(provider, bus);
        provider.getOutFaultInterceptors().add(OUT);        
    }
}
