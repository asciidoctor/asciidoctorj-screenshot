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

import geb.Browser
import org.asciidoctor.ast.AbstractBlock
import org.jruby.RubySymbol

import static org.jruby.RubySymbol.newSymbol

/**
 * Block macro to take a screenshot.
 */
class ScreenshotMacroBlock extends BlockMacroProcessor implements DriverSelector, AttributeSubstituter {

    private final Browser browser

    ScreenshotMacroBlock(String name, Browser browser) {
        super(name, [contexts: [':paragraph'], content_model: ':attributes', pos_attrs: ['name', 'frame']])
        this.browser = browser
    }

    @Override
    def process(AbstractBlock parent, String target, Map<String, Object> attributes) {
        setGebDriver(browser, parent.document.attributes, attributes)

        attributes.put('url', substituteAttributesInText(target, parent.document.attributes))
        final File buildDir = buildDir(parent)
        final String screenshotDirName = screenshotDirName(parent)
        final File screenshotDir = new File(buildDir, screenshotDirName)

        final File screenshotFile = new ScreenshotTaker(browser, screenshotDir, attributes).takeScreenshot()

        createBlock(parent, "image", "", [
                target: screenshotDirName + '/' + screenshotFile.name,
                title : attributes['title'],
        ], [:])
    }

    private File buildDir(AbstractBlock block) {
        Map<RubySymbol, Object> globalOptions = block.document.options

        String toDir = globalOptions[newSymbol(rubyRuntime, 'to_dir')]
        String destDir = globalOptions[newSymbol(rubyRuntime, 'destination_dir')]
        String buildDir = toDir ? toDir : destDir
        new File(buildDir)
    }

    private String screenshotDirName(AbstractBlock block) {
        Map<String, Object> globalAttributes = block.document.attributes

        String screenshotDirName = globalAttributes['screenshot-dir-name']

        if (!screenshotDirName || screenshotDirName.isAllWhitespace()) {
            screenshotDirName = 'screenshots'
        }
        screenshotDirName
    }
}
