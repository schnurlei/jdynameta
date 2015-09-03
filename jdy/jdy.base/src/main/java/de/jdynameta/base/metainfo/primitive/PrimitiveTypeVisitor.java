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
package de.jdynameta.base.metainfo.primitive;

import java.math.BigDecimal;
import java.util.Date;

import de.jdynameta.base.value.JdyPersistentException;

/**
 * @author Rainer
 *
 * @version 17.07.2002
 */
public interface PrimitiveTypeVisitor
{
    public void handleValue(Long aValue, LongType aType) throws de.jdynameta.base.value.JdyPersistentException;

    public void handleValue(Boolean aValue, BooleanType aType) throws JdyPersistentException;

    public void handleValue(Double aValue, FloatType aType) throws JdyPersistentException;

    public void handleValue(Date aValue, TimeStampType aType) throws JdyPersistentException;

    public void handleValue(BigDecimal aValue, CurrencyType aType) throws JdyPersistentException;

    public void handleValue(String aValue, TextType aType) throws JdyPersistentException;

    public void handleValue(String aValue, VarCharType aType) throws JdyPersistentException;

    public void handleValue(BlobByteArrayHolder aValue, BlobType aType) throws JdyPersistentException;

}
