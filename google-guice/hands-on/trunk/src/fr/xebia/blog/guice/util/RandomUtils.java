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
package fr.xebia.blog.guice.util;

/**
 * Utils for Provides
 * 
 * @author <a href="mailto:pabs.agro@gmail.com">Pablo Lopez</a>
 */
public class RandomUtils {

	/**
	 * Randomizer
	 * 
	 * @return a random Boolean
	 */
	public static Boolean randomBoolean() {
		if (Math.random() > .5) {
			return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}
}
