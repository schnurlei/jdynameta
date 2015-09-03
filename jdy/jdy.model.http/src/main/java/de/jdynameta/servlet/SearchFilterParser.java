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

import java.io.IOException;

import javax.naming.NamingException;
import javax.naming.directory.InvalidSearchFilterException;

/**
 *
 */
public final class SearchFilterParser
{
    private static final boolean dbg = true;

    /*	s. rfc2254
     * 
     * Character       ASCII value    encoded value
     -----------------------------------------
     *               0x2a			\2a
     (               0x28            \28
     )               0x29            \29
     \               0x5c            \5c
     NUL             0x00            \00
     */
    /**
     * First convert filter string into char[]
     *
     * @param filterStr
     * @throws java.io.IOException
     * @throws javax.naming.NamingException
     */
    public static void encodeFilterString(String filterStr) throws IOException, NamingException
    {
        if ((filterStr == null) || (filterStr.equals("")))
        {
            throw new InvalidSearchFilterException("Empty filter");
        }

        char[] filter = filterStr.toCharArray();
        debug("String filter: " + filterStr);

        SearchFilter root = new SearchFilter(SearchFilter.COMPLEX_TYPE.ROOT);
        parseFilter(root, filter, 0, filter.length);
    }

    private static void parseFilter(SearchFilter aParent, char[] filterString, int filterStart, int filterEnd) throws IOException, NamingException
    {
        debug(("encFilter: " + String.valueOf(filterString, filterStart, filterEnd - filterStart)));

        if ((filterEnd - filterStart) <= 0)
        {
            throw new InvalidSearchFilterException("Empty filter");
        }

        int nextOffset;
        int parens, balance;
        boolean escape;

        parens = 0;

        int filtOffset[] = new int[1];

        for (filtOffset[0] = filterStart; filtOffset[0] < filterEnd; filtOffset[0]++)
        {
            switch (filterString[filtOffset[0]])
            {
                case '(':
                    filtOffset[0]++;
                    parens++;
                    switch (filterString[filtOffset[0]])
                    {
                        case '&':
                            SearchFilter andFilter = new SearchFilter(SearchFilter.COMPLEX_TYPE.AND);
                            aParent.addSubFilter(andFilter);
                            parseComplexFilter(andFilter, filterString, filtOffset, filterEnd);
                            parens--;
                            break;

                        case '|':
                            SearchFilter orFilter = new SearchFilter(SearchFilter.COMPLEX_TYPE.OR);
                            aParent.addSubFilter(orFilter);
                            parseComplexFilter(orFilter, filterString, filtOffset, filterEnd);
                            parens--;
                            break;

                        case '!':
                            SearchFilter notFilter = new SearchFilter(SearchFilter.COMPLEX_TYPE.NOT);
                            aParent.addSubFilter(notFilter);
                            parseComplexFilter(notFilter, filterString, filtOffset, filterEnd);
                            parens--;
                            break;

                        default:
                            balance = 1;
                            escape = false;
                            nextOffset = filtOffset[0];
                            while (nextOffset < filterEnd && balance > 0)
                            {
                                if (!escape)
                                {
                                    if (filterString[nextOffset] == '(')
                                    {
                                        balance++;
                                    } else if (filterString[nextOffset] == ')')
                                    {
                                        balance--;
                                    }
                                }
                                if (filterString[nextOffset] == '\\' && !escape)
                                {
                                    escape = true;
                                } else
                                {
                                    escape = false;
                                }
                                if (balance > 0)
                                {
                                    nextOffset++;
                                }
                            }
                            if (balance != 0)
                            {
                                throw new InvalidSearchFilterException("Unbalanced parenthesis");
                            }

                            parseSimpleFilter(aParent, filterString, filtOffset[0], nextOffset);

                            // points to right parens; for loop will increment beyond
                            // parens
                            filtOffset[0] = nextOffset;

                            parens--;
                            break;

                    }
                    break;

                case ')':
                    //
                    // End of sequence
                    //
                    filtOffset[0]++;
                    parens--;
                    break;

                case ' ':
                    filtOffset[0]++;
                    break;

                default: // assume simple type=value filter
                    parseSimpleFilter(aParent, filterString, filtOffset[0], filterEnd);
                    filtOffset[0] = filterEnd; // force break from outer
                    break;
            }
        }

        if (parens > 0)
        {
            throw new InvalidSearchFilterException("Unbalanced parenthesis");
        }
    }

    /**
     * convert character 'c' that represents a hexadecimal digit to an integer.
     * if 'c' is not a hexidecimal digit [0-9A-Fa-f], -1 is returned. otherwise
     * the converted value is returned.
     */
    private static int hexchar2int(char c)
    {
        if (c >= '0' && c <= '9')
        {
            return (c - '0');
        }
        if (c >= 'A' && c <= 'F')
        {
            return (c - 'A' + 10);
        }
        if (c >= 'a' && c <= 'f')
        {
            return (c - 'a' + 10);
        }
        return (-1);
    }

    private static String unescapeFilterValue(char[] orig, int start, int end) throws NamingException
    {
        boolean escape = false;
        boolean escStart = false;
        int excapedVal = 0;
        char ch;

        debug(("unescape: " + String.valueOf(orig, start, end - start)));

        StringBuilder answer = new StringBuilder();

        for (int i = start; i < end; i++)
        {
            ch = orig[i];
            if (escape)
            {
                // check whether it is a hex char
                int hexVal = 0;
                if ((hexVal = hexchar2int(ch)) < 0)
                {

                    // escaping already started but we can't find 2nd hex
                    throw new InvalidSearchFilterException(
                            "invalid escape sequence: \\" + ch);
                } else
                {
                    if (escStart)
                    {
                        excapedVal = (excapedVal << 4) + hexVal;
                        answer.append((char) excapedVal);
                        escape = false;
                        escStart = false;
                    } else
                    {
                        excapedVal = hexVal;
                        escStart = true;
                    }
                }
            } else if (ch != '\\')
            {
                answer.append(ch);
                escape = false;
            } else
            {
                escape = true;
            }
        }

        return answer.toString();
    }

    private static int indexOf(char[] str, char ch, int start, int end)
    {
        for (int i = start; i < end; i++)
        {
            if (str[i] == ch)
            {
                return i;
            }
        }
        return -1;
    }

    private static int findUnescaped(char[] str, char ch, int start, int end)
    {
        while (start < end)
        {
            int where = indexOf(str, ch, start, end);

            /*
             * Count the immediate preceding '\' to find out if this is an
             * escaped '*'. This is a made-up way for parsing an escaped '*' in
             * v2. This is how the other leading SDK vendors interpret v2. For
             * v3 we fallback to the way we parse "\*" in v2. It's not legal in
             * v3 to use "\*" to escape '*'; the right way is to use "\2a"
             * instead.
             */
            int backSlashPos;
            int backSlashCnt = 0;
            for (backSlashPos = where - 1; ((backSlashPos >= start) && (str[backSlashPos] == '\\')); backSlashPos--, backSlashCnt++)
				;

            // if at start of string, or not there at all, or if not escaped
            if (where == start || where == -1 || ((backSlashCnt % 2) == 0))
            {
                return where;
            }

            // start search after escaped star
            start = where + 1;
        }
        return -1;
    }

    private static void parseSimpleFilter(SearchFilter aParent, char[] filterString, int filtStart, int filtEnd) throws IOException, NamingException
    {
        debug(("encSimpleFilter: " + String.valueOf(filterString, filtStart, filtEnd - filtStart)));

        int valueStart, valueEnd, typeStart, typeEnd;

        int eq;
        if ((eq = indexOf(filterString, '=', filtStart, filtEnd)) == -1)
        {
            throw new InvalidSearchFilterException("Missing 'equals'");
        }

        valueStart = eq + 1; // value starts after equal sign
        valueEnd = filtEnd;
        typeStart = filtStart; // beginning of string

        SearchFilter.OPERATOR operator = SearchFilter.OPERATOR.EQUAL;

        switch (filterString[eq - 1])
        {
            case '<':
                operator = SearchFilter.OPERATOR.LESS_EQ;
                typeEnd = eq - 1;
                break;
            case '>':
                operator = SearchFilter.OPERATOR.GREATER_EQ;
                typeEnd = eq - 1;
                break;
            case '~': // LDAP_FILTER_APPROX
                throw new InvalidSearchFilterException("Filter operator ~= not used");
//			typeEnd = eq - 1;
//			break;
            case ':': // LDAP_FILTER_EXT
                throw new InvalidSearchFilterException("Filter operator := not used");
//			typeEnd = eq - 1;
//			break;
            default:
                typeEnd = eq;
                if (findUnescaped(filterString, '*', valueStart, valueEnd) == -1)
                {
                    operator = SearchFilter.OPERATOR.EQUAL;
                } else if (filterString[valueStart] == '*'
                        && valueStart == (valueEnd - 1))
                {
                    // LDAP_FILTER_PRESENT
                    throw new InvalidSearchFilterException("Filter operator =* not used");
                } else
                {
                    parseSubstringFilter(filterString, typeStart, typeEnd, valueStart, valueEnd);
                    return;
                }
                break;
        }
        String valueString = unescapeFilterValue(filterString, valueStart, valueEnd);

        debug("type: " + operator);
        debug("value: " + valueString);
    }

    private static void parseSubstringFilter(char[] filterString, int typeStart, int typeEnd, int valueStart, int valueEnd) throws IOException, NamingException
    {
        debug(("parseSubstringFilter: " + String.valueOf(filterString, typeStart, typeEnd - typeStart)));

        int index;
        int previndex = valueStart;
        while ((index = findUnescaped(filterString, '*', previndex, valueEnd)) != -1)
        {
            if (previndex == valueStart)
            {
                if (previndex < index)
                {
                    debug("initial: " + previndex + "," + index);
                }
            } else
            {
                if (previndex < index)
                {
                    debug("any: " + previndex + "," + index);
                }
            }
            previndex = index + 1;
        }
        if (previndex < valueEnd)
        {
            debug("final: " + previndex + "," + valueEnd);
        }
    }

    private static void parseComplexFilter(SearchFilter parent, char[] filterString, int filtOffset[], int filtEnd) throws IOException, NamingException
    {

        //
        // We have a complex filter of type "&(type=val)(type=val)"
        // with filtOffset[0] pointing to the &
        //
        debug(("parseComplexFilter: " + String.valueOf(filterString, filtOffset[0], filtEnd - filtOffset[0])));
        debug(" type: " + parent.getComplexType());

        filtOffset[0]++;

        int[] parens = findRightParen(filterString, filtOffset, filtEnd);
        parseFilterList(parent, filterString, parens[0], parens[1]);
    }

    //
    // filter at filtOffset[0] - 1 points to a (. Find ) that matches it
    // and return substring between the parens. Adjust filtOffset[0] to
    // point to char after right paren
    //
    private static int[] findRightParen(char[] filter, int filtOffset[], int end)
            throws IOException, NamingException
    {

        int balance = 1;
        boolean escape = false;
        int nextOffset = filtOffset[0];

        while (nextOffset < end && balance > 0)
        {
            if (!escape)
            {
                if (filter[nextOffset] == '(')
                {
                    balance++;
                } else if (filter[nextOffset] == ')')
                {
                    balance--;
                }
            }
            if (filter[nextOffset] == '\\' && !escape)
            {
                escape = true;
            } else
            {
                escape = false;
            }
            if (balance > 0)
            {
                nextOffset++;
            }
        }
        if (balance != 0)
        {
            throw new InvalidSearchFilterException("Unbalanced parenthesis");
        }

        // String tmp = filter.substring(filtOffset[0], nextOffset);
        int[] tmp = new int[]
        {
            filtOffset[0], nextOffset
        };

        filtOffset[0] = nextOffset + 1;

        return tmp;

    }

    //
    // Encode filter list of type "(filter1)(filter2)..."
    //
    private static void parseFilterList(SearchFilter parent, char[] filterString, int start, int end) throws IOException, NamingException
    {

        debug(("parseComplexFilter: " + String.valueOf(filterString, start, end - start)));

        int filtOffset[] = new int[1];

        for (filtOffset[0] = start; filtOffset[0] < end; filtOffset[0]++)
        {
            if (Character.isSpaceChar((char) filterString[filtOffset[0]]))
            {
                continue;
            }

            if (filterString[filtOffset[0]] == '(')
            {
                continue;
            }

            int[] parens = findRightParen(filterString, filtOffset, end);

            // add enclosing parens
            int len = parens[1] - parens[0];
            char[] newfilter = new char[len + 2];
            System.arraycopy(filterString, parens[0], newfilter, 1, len);
            newfilter[0] = (byte) '(';
            newfilter[len + 1] = (byte) ')';
            parseFilter(parent, newfilter, 0, newfilter.length);
        }
    }

    private static void debug(String aDebugString)
    {
        if (dbg)
        {
            System.err.println(aDebugString);
        }
    }

}
