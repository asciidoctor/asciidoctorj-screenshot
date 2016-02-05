package org.asciidoctor.extension

import javax.imageio.ImageIO
import java.awt.image.BufferedImage

/**
 * ...
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
