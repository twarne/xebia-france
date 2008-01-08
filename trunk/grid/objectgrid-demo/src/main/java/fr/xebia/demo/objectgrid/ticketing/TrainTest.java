/*
 * Copyright 2002-2006 the original author or authors.
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
package fr.xebia.demo.objectgrid.ticketing;

import java.lang.reflect.Field;

import junit.framework.TestCase;

/**
 * @author <a href="mailto:cyrille.leclerc@pobox.com">Cyrille Le Clerc</a>
 */
public class TrainTest extends TestCase {

    public void test() throws Exception {
        System.out.println(Train.class);
        System.out.println("\tDeclared fields");
        for (Field field : Train.class.getDeclaredFields()) {
            System.out.println("\t\t" + field.getName() + " - " + field);
        }
        System.out.println("\tFields");
        for (Field field : Train.class.getFields()) {
            System.out.println("\t\t" + field);
        }
        
        {
            Field field = Train.class.getDeclaredField("id");
            Train train = new Train();
            field.set(train, new Integer(2));
            System.out.println(field);
        }
    }
}
