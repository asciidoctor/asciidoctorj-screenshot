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

import spock.lang.Specification
import spock.lang.Unroll

/**
 * Unit test for {@link ScreenshotDimension}.
 */
class ScreenshotDimensionTest extends Specification {

    @Unroll
    def "screenshotDimension #dim should have width #w and height #h"() {
        given:
          def d = new ScreenshotDimension(dim)

        expect:
          d.width == w
          d.height == h

        where:
          dim       || w | h
          '100x200' || 100 | 200
          '200x200' || 200 | 200
          '300x200' || 300 | 200
          '200x100' || 200 | 100
          '200x100' || 200 | 100
          'browser' || 800 | 600
          'iphone5' || 320 | 568
          'nexus5'  || 360 | 640
    }

    @Unroll
    def "should throw #ex for invalid input '#dim'"() {
        given:
          def e = null
          try {
              new ScreenshotDimension(dim)
          } catch (any) {
              e = any
          }

        expect:
          e.class == ex

        where:
          dim           | ex
          null          | NullPointerException
          ''            | IllegalArgumentException
          'blubb'       | IllegalArgumentException
          'faxen'       | IllegalArgumentException
          '100x'        | IllegalArgumentException
          '100xasdf'    | IllegalArgumentException
          '100x200x300' | IllegalArgumentException
    }
}
