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
package fr.xebia.demo.hibernate.search.model;

/**
 * Model constants used for database mapping.
 * 
 * @author <a href="cheubes@xebia.fr">Christophe Heubès</a>
 */
public class Constants {

    /**
     * The default sequence allocation size.
     */
    public static final int DEFAULT_SEQUENCE_ALLOCATION_SIZE = 10;

    /**
     * The document table name.
     */
    public static final String DOCUMENT_TABLE_NAME = "SPL_DOCUMENT";

    /**
     * The document sequence.
     */
    public static final String DOCUMENT_SEQUENCE = "SEQ_SPL_DOCUMENT";

    /**
     * The document sequence name.
     */
    public static final String DOCUMENT_SEQUENCE_NAME = "documentSequence";

    /**
     * The author table name.
     */
    public static final String AUTHOR_TABLE_NAME = "SPL_AUTHOR";

    /**
     * The author sequence.
     */
    public static final String AUTHOR_SEQUENCE = "SEQ_SPL_AUTHOR";

    /**
     * The author sequence name.
     */
    public static final String AUTHOR_SEQUENCE_NAME = "authorSequence";

}
