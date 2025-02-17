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
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   or in the "license" file accompanying this file. This file is distributed
 *   on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 *   express or implied. See the License for the specific language governing
 *   permissions and limitations under the License.
 */

package org.opensearch.sql.correctness.tests;

import static java.util.Arrays.asList;
import static java.util.Collections.singleton;
import static org.junit.Assert.fail;

import org.json.JSONObject;
import org.junit.Test;
import org.opensearch.sql.correctness.report.ErrorTestCase;
import org.opensearch.sql.correctness.report.FailedTestCase;
import org.opensearch.sql.correctness.report.SuccessTestCase;
import org.opensearch.sql.correctness.report.TestReport;
import org.opensearch.sql.correctness.runner.resultset.DBResult;
import org.opensearch.sql.correctness.runner.resultset.Row;
import org.opensearch.sql.correctness.runner.resultset.Type;

/**
 * Test for {@link TestReport}
 */
public class TestReportTest {

  private TestReport report = new TestReport();

  @Test
  public void testSuccessReport() {
    report.addTestCase(new SuccessTestCase(1, "SELECT * FROM accounts"));
    JSONObject actual = new JSONObject(report);
    JSONObject expected = new JSONObject(
        "{" +
            "  \"summary\": {" +
            "    \"total\": 1," +
            "    \"success\": 1," +
            "    \"failure\": 0" +
            "  }," +
            "  \"tests\": [" +
            "    {" +
            "      \"id\": 1," +
            "      \"result\": 'Success'," +
            "      \"sql\": \"SELECT * FROM accounts\"," +
            "    }" +
            "  ]" +
            "}"
    );

    if (!actual.similar(expected)) {
      fail("Actual JSON is different from expected: " + actual.toString(2));
    }
  }

  @Test
  public void testFailedReport() {
    report.addTestCase(new FailedTestCase(1, "SELECT * FROM accounts", asList(
        new DBResult("OpenSearch", singleton(new Type("firstName", "text")),
            singleton(new Row(asList("hello")))),
        new DBResult("H2", singleton(new Type("firstName", "text")),
            singleton(new Row(asList("world"))))),
        "[SQLITE_ERROR] SQL error or missing database;"
    ));
    JSONObject actual = new JSONObject(report);
    JSONObject expected = new JSONObject(
        "{" +
            "  \"summary\": {" +
            "    \"total\": 1," +
            "    \"success\": 0," +
            "    \"failure\": 1" +
            "  }," +
            "  \"tests\": [" +
            "    {" +
            "      \"id\": 1," +
            "      \"result\": 'Failed'," +
            "      \"sql\": \"SELECT * FROM accounts\"," +
            "      \"explain\": \"Data row at [0] is different: this=[Row(values=[world])], other=[Row(values=[hello])]\"," +
            "      \"errors\": \"[SQLITE_ERROR] SQL error or missing database;\"," +
            "      \"resultSets\": [" +
            "        {" +
            "          \"database\": \"H2\"," +
            "          \"schema\": [" +
            "            {" +
            "              \"name\": \"firstName\"," +
            "              \"type\": \"text\"" +
            "            }" +
            "          ]," +
            "          \"dataRows\": [[\"world\"]]" +
            "        }," +
            "        {" +
            "          \"database\": \"OpenSearch\"," +
            "          \"schema\": [" +
            "            {" +
            "              \"name\": \"firstName\"," +
            "              \"type\": \"text\"" +
            "            }" +
            "          ]," +
            "          \"dataRows\": [[\"hello\"]]" +
            "        }" +
            "      ]" +
            "    }" +
            "  ]" +
            "}"
    );

    if (!actual.similar(expected)) {
      fail("Actual JSON is different from expected: " + actual.toString(2));
    }
  }

  @Test
  public void testErrorReport() {
    report.addTestCase(new ErrorTestCase(1, "SELECT * FROM", "Missing table name in query"));
    JSONObject actual = new JSONObject(report);
    JSONObject expected = new JSONObject(
        "{" +
            "  \"summary\": {" +
            "    \"total\": 1," +
            "    \"success\": 0," +
            "    \"failure\": 1" +
            "  }," +
            "  \"tests\": [" +
            "    {" +
            "      \"id\": 1," +
            "      \"result\": 'Failed'," +
            "      \"sql\": \"SELECT * FROM\"," +
            "      \"reason\": \"Missing table name in query\"," +
            "    }" +
            "  ]" +
            "}"
    );

    if (!actual.similar(expected)) {
      fail("Actual JSON is different from expected: " + actual.toString(2));
    }
  }

}
