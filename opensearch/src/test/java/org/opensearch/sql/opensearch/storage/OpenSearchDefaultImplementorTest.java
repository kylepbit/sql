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
 *
 *    Copyright 2020 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License").
 *    You may not use this file except in compliance with the License.
 *    A copy of the License is located at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    or in the "license" file accompanying this file. This file is distributed
 *    on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 *    express or implied. See the License for the specific language governing
 *    permissions and limitations under the License.
 *
 */

package org.opensearch.sql.opensearch.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.opensearch.sql.planner.logical.LogicalPlanDSL.relation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class OpenSearchDefaultImplementorTest {

  @Mock
  OpenSearchIndexScan indexScan;

  /**
   * For test coverage.
   */
  @Test
  public void visitInvalidTypeShouldThrowException() {
    final OpenSearchIndex.OpenSearchDefaultImplementor implementor =
        new OpenSearchIndex.OpenSearchDefaultImplementor(indexScan);

    final IllegalStateException exception =
        assertThrows(IllegalStateException.class, () -> implementor.visitNode(relation("index"),
            indexScan));
    ;
    assertEquals(
        "unexpected plan node type "
            + "class org.opensearch.sql.planner.logical.LogicalRelation",
        exception.getMessage());
  }
}
