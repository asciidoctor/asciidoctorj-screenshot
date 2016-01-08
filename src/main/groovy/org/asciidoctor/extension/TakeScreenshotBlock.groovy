package org.asciidoctor.extension

import org.asciidoctor.ast.AbstractBlock
import org.jruby.RubyHash
import org.jruby.RubySymbol

import static org.jruby.RubySymbol.newSymbol

class TakeScreenshotBlock extends BlockProcessor implements BrowserResizer {

    TakeScreenshotBlock(String name, RubyHash config) {
        super(name, [contexts: [':paragraph'], content_model: ':simple'])
    }

    def process(AbstractBlock block, Reader reader, Map<String, Object> attributes) {
        final File screenshotsFile = new ScreenshotTaker(screenshotDir(block), attributes).takeScreenshot()

        // this is a hack to display frames around images if a special dimension is selected
        final String alt = attributes['dimension']

        createBlock(block, "image", "", [
                target: screenshotsFile.absolutePath,
                title : reader.lines().join(" // "),
                alt   : alt
        ], [:])
    }

    private File screenshotDir(AbstractBlock block) {
        Map<String, Object> globalAttributes = block.document.attributes
        Map<RubySymbol, Object> globalOptions = block.document.options

        String buildDir = globalOptions[newSymbol(rubyRuntime, 'to_dir')]
        String screenshotDirName = globalAttributes['screenshot-dir-name']

        new File("${buildDir}/${screenshotDirName}/")
    }
}
