/*
 * Copyright 2008-2009 Xebia and the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package fr.xebia.exercice.servloc;

import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Repository
class ServLocPortfolioRepository {

    private Map<Integer, Portfolio> portfolios = new HashMap<Integer, Portfolio>() {{
        put(1, new Portfolio(new Action(1), new Option(2, 1000, new Date(), new Action(3))));
    }};

    public Portfolio load(Integer idPortfolio) {
        return new Portfolio(new Action(1), new Option(2, 1000, new Date(), new Action(3)));

        // ne marche pas. Une histoire d'ordre entre la création de ce bean et le weaving des classes
//        return portfolios.get(idPortfolio);
    }
}
