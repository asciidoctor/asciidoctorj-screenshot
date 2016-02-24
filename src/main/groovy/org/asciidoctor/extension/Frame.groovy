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

import javax.imageio.ImageIO
import java.awt.image.BufferedImage

/**
 * Enum listing all currently available frames.
 */
enum Frame {

    BROWSER('800x600', 'browser.png', 6, 71),
    IPHONE5('320x568', 'iphone-5.png', 34, 118),
    NEXUS5('360x640', 'nexus-5.png', 23, 68);

    static Map<String, String> dimensions() {
        Map<String, String> result = [:]
        values().each {
            result.put(it.name(), it.dim)
        }
        result
    }

    private final String dim
    private final String imageName

    final int xOffset
    final int yOffset
    
    private Frame(String dimension, String imageName, int xOffset, int yOffset) {
        this.dim = dimension
        this.imageName = imageName
        this.xOffset = xOffset
        this.yOffset = yOffset
    }
    
    BufferedImage getFrameImage() {
        ImageIO.read(getClass().classLoader.getResourceAsStream('frames/' + imageName))
    }

}
