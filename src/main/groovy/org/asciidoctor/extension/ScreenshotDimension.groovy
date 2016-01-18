/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2016 Stephan Classen
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

/**
 * ...
 */
class ScreenshotDimension {

    private static final Map<String, String> dimensions = [
            "FRAME_IPHONE4"    : "336x504",
            "FRAME_IPHONE5"    : "336x596",
            "FRAME_IPHONE6"    : "336x596",
            "FRAME_IPHONE6PLUS": "360x640",
            "FRAME_SAMSUNG_S4" : "370x657",
            "FRAME_IMAC"       : "1200x675",
            "FRAME_BROWSER"    : "800x500"
    ].withDefault { key -> key }

    int width
    int height

    ScreenshotDimension(int width, int height) {
        this.width = width
        this.height = height
    }

    ScreenshotDimension(String dim) {
        String[] d = dimensions[dim].split('x')

        if (d.size() != 2) {
            throw new IllegalArgumentException('Dimension must be in the format {width}x{height}')
        }

        try {
            width = d[0] as int
            height = d[1] as int
        } catch (Exception ignored) {
            throw new IllegalArgumentException('Dimension must be in the format {width}x{height}')
        }
    }
}
