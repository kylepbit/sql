from __future__ import unicode_literals

"""
SPDX-License-Identifier: Apache-2.0

The OpenSearch Contributors require contributions made to
this file be licensed under the Apache-2.0 license or a
compatible open source license.

Modifications Copyright OpenSearch Contributors. See
GitHub history for details.
"""
"""
Copyright 2020 Amazon.com, Inc. or its affiliates. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License").
You may not use this file except in compliance with the License.
A copy of the License is located at

    http://www.apache.org/licenses/LICENSE-2.0

or in the "license" file accompanying this file. This file is distributed
on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
express or implied. See the License for the specific language governing
permissions and limitations under the License.
"""

from prompt_toolkit.enums import DEFAULT_BUFFER
from prompt_toolkit.filters import Condition
from prompt_toolkit.application import get_app


def opensearch_is_multiline(opensearchsql_cli):
    """Return function that returns boolean to enable/unable multiline mode."""

    @Condition
    def cond():
        doc = get_app().layout.get_buffer_by_name(DEFAULT_BUFFER).document

        if not opensearchsql_cli.multi_line:
            return False
        if opensearchsql_cli.multiline_mode == "safe":
            return True
        else:
            return not _multiline_exception(doc.text)

    return cond


def _is_complete(sql):
    # A complete command is an sql statement that ends with a semicolon
    return sql.endswith(";")


def _multiline_exception(text):
    text = text.strip()
    return _is_complete(text)
