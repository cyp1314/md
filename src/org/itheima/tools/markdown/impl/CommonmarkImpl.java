package org.itheima.tools.markdown.impl;

import java.util.Collections;
import java.util.Set;

import org.commonmark.node.IndentedCodeBlock;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.NodeRenderer;
import org.commonmark.renderer.html.HtmlNodeRendererContext;
import org.commonmark.renderer.html.HtmlNodeRendererFactory;
import org.commonmark.renderer.html.HtmlRenderer;
import org.commonmark.renderer.html.HtmlWriter;
import org.itheima.tools.markdown.Markdown;


public class CommonmarkImpl
        implements Markdown {
    public String parse(String text) {
        Parser parser = Parser.builder().build();

        HtmlRenderer renderer = HtmlRenderer.builder()
                .nodeRendererFactory(new HtmlNodeRendererFactory() {
                    public NodeRenderer create(HtmlNodeRendererContext context) {
                        return new CommonmarkImpl.IndentedCodeBlockNodeRenderer(context);
                    }
                }).build();

        Node node = parser.parse(text);

        return renderer.render(node);
    }

    private class IndentedCodeBlockNodeRenderer
            implements NodeRenderer {
        private final HtmlWriter html;

        IndentedCodeBlockNodeRenderer(HtmlNodeRendererContext context) {
            this.html = context.getWriter();
        }


        public Set<Class<? extends Node>> getNodeTypes() {
            return (Set) Collections.singleton(IndentedCodeBlock.class);
        }


        public void render(Node node) {
            IndentedCodeBlock codeBlock = (IndentedCodeBlock) node;
            this.html.line();
            this.html.tag("pre");
            String literal = codeBlock.getLiteral();

            System.out.println(literal);

            this.html.text(literal);
            this.html.tag("/pre");
            this.html.line();
        }
    }
}
