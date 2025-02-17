/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 *
 * Modifications Copyright OpenSearch Contributors. See
 * GitHub history for details.
 */

/*
 *   Copyright 2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License").
 *   You may not use this file except in compliance with the License.
 *   A copy of the License is located at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *   or in the "license" file accompanying this file. This file is distributed
 *   on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 *   express or implied. See the License for the specific language governing
 *   permissions and limitations under the License.
 */

package org.opensearch.sql.legacy.unittest.executor.format;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;

import com.google.common.collect.ImmutableMap;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import org.opensearch.sql.legacy.executor.csv.CSVResult;
import org.opensearch.sql.legacy.executor.csv.CSVResultsExtractor;
import org.opensearch.sql.legacy.executor.csv.CsvExtractorException;
import org.opensearch.sql.legacy.expression.domain.BindingTuple;

public class CSVResultsExtractorTest {
    private final CSVResultsExtractor csvResultsExtractor = new CSVResultsExtractor(false, false, false);

    @Test
    public void extractResultsFromBindingTupleListShouldPass() throws CsvExtractorException {
        CSVResult csvResult = csv(Arrays.asList(BindingTuple.from(ImmutableMap.of("age", 31, "gender", "m")),
                                                BindingTuple.from(ImmutableMap.of("age", 31, "gender", "f")),
                                                BindingTuple.from(ImmutableMap.of("age", 39, "gender", "m")),
                                                BindingTuple.from(ImmutableMap.of("age", 39, "gender", "f"))),
                                  Arrays.asList("age", "gender"));

        assertThat(csvResult.getHeaders(), contains("age", "gender"));
        assertThat(csvResult.getLines(), contains("31,m", "31,f", "39,m", "39,f"));
    }

    private CSVResult csv(List<BindingTuple> bindingTupleList, List<String> fieldNames) throws CsvExtractorException {
        return csvResultsExtractor.extractResults(bindingTupleList, false, ",", fieldNames);
    }
}
