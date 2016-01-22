package org.asciidoctor.extension

import javax.imageio.ImageIO
import java.awt.image.BufferedImage

/**
 * ...
 */
enum Frame {

    IPHONE4('336x504', 'iphone4.png', 5, 5),
    IPHONE5('336x596', 'iphone5.png', 5, 5),
    IPHONE6('336x596', 'iphone6.png', 5, 5),
    IPHONE6PLUS('360x640', 'iphone6plus.png', 5, 5),
    SAMSUNG_S4('370x657', 'samsungS4.png', 5, 5),
    IMAC('1200x675', 'imac.png', 5, 5),
    BROWSER('800x500', 'browser.png', 5, 5);

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
    
    private Frame(String dimension, String imageName, int x, int y) {
        this.dim = dimension
        this.imageName = imageName
        this.xOffset = x
        this.yOffset = y
    }
    
    BufferedImage getFrameImage() {
        ImageIO.read(getClass().classLoader.getResourceAsStream('frames/' + imageName))
    }

}
