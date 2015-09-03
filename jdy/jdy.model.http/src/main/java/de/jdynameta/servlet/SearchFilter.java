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
package de.jdynameta.servlet;

import java.util.ArrayList;

public class SearchFilter
{
    public enum COMPLEX_TYPE
    {
        AND, OR, NOT, ROOT
    }

    enum OPERATOR
    {
        EQUAL, GREATER_EQ, LESS_EQ
    }

    private final COMPLEX_TYPE complexType;
    private OPERATOR operator;
    private boolean isSubString;
    private String value;
    private final ArrayList<SearchFilter> filterList;

    /**
     *
     * @param aComplexType
     */
    public SearchFilter(COMPLEX_TYPE aComplexType)
    {
        this.complexType = aComplexType;
        this.filterList = new ArrayList<>();
    }

    /**
     *
     * @return
     */
    public COMPLEX_TYPE getComplexType()
    {
        return complexType;
    }

    public void addSubFilter(SearchFilter aSubFilter)
    {
        this.filterList.add(aSubFilter);
    }

}
