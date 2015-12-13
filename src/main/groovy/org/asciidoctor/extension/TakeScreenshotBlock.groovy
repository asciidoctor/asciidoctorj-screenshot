package org.asciidoctor.extension

import geb.Browser
import org.asciidoctor.ast.AbstractBlock
import org.jruby.RubySymbol

import javax.imageio.ImageIO
import java.awt.image.BufferedImage

class TakeScreenshotBlock extends BlockProcessor implements BrowserRisizer {

    TakeScreenshotBlock(String name, Map<String, Object> config) {
        super(name, [contexts: [':paragraph'], content_model: ':simple'])
    }

    String generateId() {
        def alphabet = (('A'..'Z') + ('0'..'9')).join()
        def stringId
        new Random().with {
            stringId = (1..32).collect { alphabet[nextInt(36)] }.join()
        }
        stringId
    }

    def process(AbstractBlock block, Reader reader, Map<String, Object> attributes) {
        final Map<String, Object> globalAttributes = block.document.attributes
        final Map<String, Object> globalOptions = block.document.options

        def maxHeight = 600
        def maxWidth = 800

        String dimension = attributes['dimension']
        if (dimension) {
            ScreenshotDimension dim = resizBrowserWindow(dimension)
            maxHeight = dim.height
            maxWidth = dim.width
        }

        def cutElement

        def name = attributes['name']
        if (!name) {
            name = generateId()
        }

        Browser.drive {
            if (attributes['url']) {
                go attributes['url']
                waitFor(1) { true }
            }
            report name
            if (attributes['selector']) {
                cutElement = $(attributes['selector'])
            }
        }

        def buildDir = getFromOptions(globalOptions, 'to_dir')
        String screenshotsDir = "${buildDir}/${globalAttributes['screenshot-dir-name']}/"
        if (cutElement) {
            cropScreenshot(new File(screenshotsDir + name + ".png"), cutElement, maxWidth, maxHeight)
        } else {
            cropScreenshot(new File(screenshotsDir + name + ".png"), maxWidth, maxHeight)
        }

        def alt = attributes['dimension']
        createBlock(block, "image", "", [
                target: "${screenshotsDir}/${name}.png" as String,
                title : reader.lines().join(" // "),
                alt   : alt
        ], [:])
    }

    // required, since keys in options are RubySymbol and not String...
    private def getFromOptions(Map options, String key) {
        RubySymbol keySym = RubySymbol.newSymbol(rubyRuntime, key)
        return options[keySym]
    }

    private def cropScreenshot(imageFile, element, int maxWidth, int maxHeight) {
        BufferedImage img = ImageIO.read(imageFile)
        int width = Math.min(element.width, maxWidth)
        int height = Math.min(element.height, maxHeight)

        BufferedImage result = img.getSubimage(element.x, element.y, width, height)
        ImageIO.write(result, "png", imageFile)
    }

    private def cropScreenshot(File imageFile, int maxWidth, int maxHeight) {
        BufferedImage img = ImageIO.read(imageFile)
        int width = Math.min(img.width, maxWidth)
        int height = Math.min(img.height, maxHeight)

        BufferedImage result = img.getSubimage(0, 0, width, height)
        ImageIO.write(result, "png", imageFile)
    }
}
