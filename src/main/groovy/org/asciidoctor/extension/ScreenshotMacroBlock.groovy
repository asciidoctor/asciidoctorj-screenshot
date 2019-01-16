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

import org.asciidoctor.ast.AbstractBlock
import org.jruby.RubyHash
import org.jruby.RubySymbol

import static org.jruby.RubySymbol.newSymbol

/**
 * Block macro to take a screenshot.
 */
class ScreenshotMacroBlock extends BlockMacroProcessor implements BrowserResizer, AttributeSubstituter {

    ScreenshotMacroBlock(String name, RubyHash config) {
        super(name, [contexts: [':paragraph'], content_model: ':attributes', pos_attrs: ['name', 'frame']])
    }

    def process(AbstractBlock parent, String target, Map<String, Object> attributes) {
        final String url = substituteAttributesInText(target, parent.document.attributes)
        attributes.put('url', url)

        final String screenshotDirName = attribute(parent, 'screenshot-dir-name', 'screenshots')
        final File screenshotDir = getScreenshotDir(parent, screenshotDirName)
        final File screenshotFile = new ScreenshotTaker(screenshotDir, attributes).takeScreenshot()

        createBlock(parent, "image", "", [
                target: screenshotDirName + '/' + screenshotFile.name,
                title : attributes['title'],
                alt : nameWithoutEnding(screenshotFile)
        ], [:])
    }

    private String attribute(AbstractBlock block, String attributeName, String defaultValue) {
        String value = block.getAttr(attributeName)

        if (!value || value.isAllWhitespace()) {
            value = defaultValue
        }

        value
    }

    private File getScreenshotDir(AbstractBlock block, String screenshotDirName) {
        final String imagesDirName = attribute(block, 'imagesdir', '')

        if (new File(imagesDirName).isAbsolute()) {
            return new File(imagesDirName + '/' + screenshotDirName)
        } else {
            final File buildDir = buildDir(block)
            return new File(buildDir, imagesDirName + '/' + screenshotDirName)
        }
    }

    private File buildDir(AbstractBlock block) {
        Map<RubySymbol, Object> globalOptions = block.document.options

        String toDir = globalOptions[newSymbol(rubyRuntime, 'to_dir')]
        String destDir = globalOptions[newSymbol(rubyRuntime, 'destination_dir')]
        String buildDir = toDir ? toDir : destDir
        new File(buildDir)
    }

    private String nameWithoutEnding(File file) {
        String name = file.name
        def positionOfLasPeriod = name.lastIndexOf('.')
        if (positionOfLasPeriod > 0) {
            name = name.substring(0, positionOfLasPeriod)
        }
        name
    }
}
