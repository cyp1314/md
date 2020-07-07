package org.itheima.tools.markdown.impl;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringEscapeUtils;
import org.itheima.tools.markdown.Markdown;
import org.parboiled.common.StringUtils;
import org.pegdown.LinkRenderer;
import org.pegdown.PegDownProcessor;
import org.pegdown.Printer;
import org.pegdown.ToHtmlSerializer;
import org.pegdown.VerbatimSerializer;
import org.pegdown.ast.CodeNode;
import org.pegdown.ast.Node;
import org.pegdown.ast.RootNode;
import org.pegdown.ast.VerbatimNode;
import org.pegdown.ast.Visitor;
import org.pegdown.plugins.PegDownPlugins;
import org.pegdown.plugins.ToHtmlSerializerPlugin;


public class PegdownImpl
        implements Markdown {
    private PegDownProcessor processor;

    public PegdownImpl() {
        PegDownPlugins plugins = (new PegDownPlugins.Builder()).withHtmlSerializer(new ToHtmlSerializerPlugin[]{new CustomToHtmlSerializerPlugin(null)
        }).build();

        this.processor = new PegDownProcessor(65535);
    }


    public String parse(String text) {
        RootNode node = this.processor.parseMarkdown(text.toCharArray());

        List<ToHtmlSerializerPlugin> serializePlugins = Arrays.asList(new ToHtmlSerializerPlugin[]{new CustomToHtmlSerializerPlugin(null)});

        String finalHtml = (new CustomToHtmlSerializer(new LinkRenderer(), serializePlugins)).toHtml(node);
        return finalHtml;
    }

    private static class CustomToHtmlSerializer
            extends ToHtmlSerializer {
        public CustomToHtmlSerializer(LinkRenderer linkRenderer) {
            super(linkRenderer);
        }


        public CustomToHtmlSerializer(LinkRenderer linkRenderer, List<ToHtmlSerializerPlugin> plugins) {
            super(linkRenderer, plugins);
        }


        public CustomToHtmlSerializer(LinkRenderer linkRenderer, Map<String, VerbatimSerializer> verbatimSerializers, List<ToHtmlSerializerPlugin> plugins) {
            super(linkRenderer, verbatimSerializers, plugins);
        }


        public CustomToHtmlSerializer(LinkRenderer linkRenderer, Map<String, VerbatimSerializer> verbatimSerializers) {
            super(linkRenderer, verbatimSerializers);
        }


        public void visit(CodeNode node) {
            String preTag = "pre";
            String codeTag = "code";
            boolean needPre = false;

            String text = node.getText();
            while (text.charAt(0) == '\n' || text.charAt(0) == '\r') {
                text = text.substring(1);
            }


            needPre = text.contains("\n");
            String escapeHtml = StringEscapeUtils.escapeHtml(text);
            if (needPre) {
                this.printer.print('<').print(preTag).print('>');
            }

            this.printer.print('<').print(codeTag).print('>');

            this.printer.print(escapeHtml);
            this.printer.print('<').print('/').print(codeTag).print('>');

            if (needPre)
                this.printer.print('<').print('/').print(preTag).print('>');
        }
    }

    private static class CustomToHtmlSerializerPlugin implements ToHtmlSerializerPlugin {
        private CustomToHtmlSerializerPlugin(Object o) {
        }

        public boolean visit(Node node, Visitor visitor, Printer printer) {
            System.out.println(node);
            System.out.println(visitor);

            return true;
        }
    }

    private static class CustomVerbatimSerializer
            implements VerbatimSerializer {
        public void serialize(VerbatimNode node, Printer printer) {
            printer.println().print("<pre><code");
            if (!StringUtils.isEmpty(node.getType())) {
                printAttribute(printer, "class", node.getType());
            }
            printer.print(">");
            String text = node.getText();

            while (text.charAt(0) == '\n') {
                printer.print("<br/>");
                text = text.substring(1);
            }

            String all = text.replaceAll("\n", "<br/>").replaceAll("\\t", "&nbsp;&nbsp;&nbsp;&nbsp;").replaceAll(" ",
                    "&nbsp;");
            System.out.println(all);

            printer.printEncoded(all);
            printer.print("</code></pre>");
        }


        private void printAttribute(Printer printer, String name, String value) {
            printer.print(' ').print(name).print('=').print('"').print(value).print('"');
        }
    }
}
