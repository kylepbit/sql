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
 *    Copyright 2020 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License").
 *    You may not use this file except in compliance with the License.
 *    A copy of the License is located at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *    or in the "license" file accompanying this file. This file is distributed
 *    on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 *    express or implied. See the License for the specific language governing
 *    permissions and limitations under the License.
 *
 */

package org.opensearch.sql.sql;

import org.json.JSONObject;
import org.junit.Test;
import org.opensearch.sql.legacy.SQLIntegTestCase;
import org.opensearch.sql.legacy.TestsConstants;

public class PreparedStatementIT extends SQLIntegTestCase {

  @Override
  protected void init() throws Exception {
    loadIndex(Index.ACCOUNT);
  }

  @Test
  public void testPreparedStatement() {
    JSONObject response = new JSONObject(
        executeQuery(String.format("{\n"
            + "  \"query\": \"SELECT state FROM %s WHERE state = ? GROUP BY state\",\n"
            + "  \"parameters\": [\n"
            + "    {\n"
            + "      \"type\": \"string\",\n"
            + "      \"value\": \"WA\"\n"
            + "    }\n"
            + " ]\n"
            + "}", TestsConstants.TEST_INDEX_ACCOUNT), "jdbc"));

    assertFalse(response.getJSONArray("datarows").isEmpty());
  }

  @Override
  protected String makeRequest(String query) {
    // Avoid wrap with "query" again
    return query;
  }

}
