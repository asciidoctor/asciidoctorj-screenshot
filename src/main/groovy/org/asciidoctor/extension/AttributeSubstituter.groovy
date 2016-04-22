/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2016 Stephan Classen, Markus Schlichting
 * Copyright (c) 2014 François-Xavier Thoorens
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.asciidoctor.extension

import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * Block macro to take a screenshot.
 */
trait AttributeSubstituter {

    private final Pattern attributePattern = ~/\{[A-Za-z0-9_][A-Za-z0-9_-]*\}/

    String substituteAttributesInText(String text, Map<String, Object> attributes) {
        Matcher m = checkInputAgainstPattern(text)

        String result = text;
        if (m) {
            for (int i = 0; i < (int) m.size(); i++) {
                result = replaceAttribute(result, m[i].toString(), attributes)
            }
        }

        return result;
    }

    private Matcher checkInputAgainstPattern(String input) {
        if (!(input ==~ attributePattern)) {
            return null
        }

        input =~ attributePattern
    }

    private String replaceAttribute(String text, String attribute, Map<String, Object> documentAttributes) {
        // attribute is on the form '{attribute}'
        String attributeName = attribute.substring(1, attribute.length() - 1)

        if (!documentAttributes.containsKey(attributeName)) {
            throw new IllegalArgumentException("${attributeName} is not set in the document so it cannot be expanded.")
        }

        return text.replace(attribute, documentAttributes.get(attributeName).toString())
    }

}
