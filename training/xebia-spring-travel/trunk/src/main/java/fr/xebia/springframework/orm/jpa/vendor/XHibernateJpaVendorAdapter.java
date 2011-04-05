/*
 * Copyright 2008-2010 Xebia and the original author or authors.
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
package fr.xebia.springframework.orm.jpa.vendor;

import java.util.Map;

import org.hibernate.cfg.Environment;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.util.StringUtils;

public class XHibernateJpaVendorAdapter extends HibernateJpaVendorAdapter {

    private String hbm2ddlAuto;

    public String getHbm2ddlAuto() {
        return hbm2ddlAuto;
    }

    public void setHbm2ddlAuto(String hbm2ddlAuto) {
        this.hbm2ddlAuto = hbm2ddlAuto;
    }

    @Override
    public Map<String, Object> getJpaPropertyMap() {
        Map<String, Object> jpaPropertyMap = super.getJpaPropertyMap();
        if (StringUtils.hasLength(getHbm2ddlAuto())) {
            jpaPropertyMap.put(Environment.HBM2DDL_AUTO, getHbm2ddlAuto());
        }
        return jpaPropertyMap;
    }
}
