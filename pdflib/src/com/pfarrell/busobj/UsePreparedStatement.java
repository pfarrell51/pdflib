/*
 * UsePreparedStatement.java
 *
 * Created on December 18, 2006, 12:26 PM
 *
 * Copyright (c) 2006, Pat Farrell. All rights reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.pfarrell.busobj;

/**
 * The <code>UsePreparedStatement</code> interface declares {@link com.pfarrell.busobj.PersistentBusinessObject}s
 * that must use a PreparedStatement rather than inline strings for
 * saving and retreival.
 *
 * @author pfarrell
 */
public interface UsePreparedStatement {
      public String getSaveStatement();
      public String getSelectStatement();
}
