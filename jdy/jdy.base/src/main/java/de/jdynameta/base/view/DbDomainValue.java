/**
 *
 * Copyright 2011 (C) Rainer Schneider,Roggenburg <schnurlei@googlemail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package de.jdynameta.base.view;

import java.io.Serializable;

/**
 * Map a DB Value to an text representation
 *
 * @author rs
 * @param <TDbValue>
 *
 */
public interface DbDomainValue<TDbValue extends Object> extends Serializable
{
    public TDbValue getDbValue();

    public String getRepresentation();
}
