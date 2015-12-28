package org.asciidoctor.extension

import geb.Browser
import org.asciidoctor.ast.AbstractBlock
import org.jruby.RubyHash
import org.openqa.selenium.Dimension

class DriveBrowserBlock extends BlockProcessor implements BrowserResizer {

    DriveBrowserBlock(String name, RubyHash config) {
        super(name, [contexts: [':paragraph'], content_model: ':simple'])
    }

    def process(AbstractBlock block, Reader reader, Map<String, Object> attributes) {
        final String dimension = attributes['dimension']
        if (dimension) {
            resizeBrowserWindow(dimension)
        }

        def binding = new Binding()
        binding.setVariable("Browser", Browser)
        binding.setVariable("Dimension", Dimension)

        def shell = new GroovyShell(binding)
        shell.evaluate("Browser.drive{" + reader.lines().join("\n") + "}")
        createBlock(block, "paragraph", "", [:], [:])
    }
}
