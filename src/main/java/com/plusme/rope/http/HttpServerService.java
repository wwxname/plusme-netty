package com.plusme.rope.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.handler.stream.ChunkedFile;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

import javax.activation.MimetypesFileTypeMap;
import java.io.*;
import java.net.URLDecoder;
import java.util.regex.Pattern;

/**
 * @author plusme
 * @create 2019-12-01 9:26
 */
@Slf4j
public class HttpServerService {
    private final String url;

    HttpServerService(String url) {
        this.url = url;
    }


    private static final Pattern INSECURE_URI = Pattern.compile(".*[<>&\"].*");

    private static void sendListing(ChannelHandlerContext ctx, File dir) {
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
        response.headers().set("content-type", "text/html;charset=UTF-8");
        StringBuilder buf = new StringBuilder();
        String dirPath = dir.getPath();
        buf.append("<!DOCTYPE html>\r\n");
        buf.append("<html><head><title>");
        buf.append(dirPath);
        buf.append("dir :");
        buf.append("</title></head><body>\r\n");

        buf.append("<h3>");
        buf.append(dirPath).append("dir :");
        buf.append("</h3>\r\n");
        buf.append("<ul>");
        buf.append("<li>link:<a href=\"../\">..</a></li>\r\n");
        for (File f : dir.listFiles()) {
            buf.append("<li>link:<a href=\"");
            buf.append(f.getName());
            buf.append("\">");
            buf.append(f.getName());
            buf.append("</a></li>\r\n");
        }
        buf.append("</ul></body></html>\r\n");
        ByteBuf buffer = Unpooled.copiedBuffer(buf, CharsetUtil.UTF_8);
        response.content().writeBytes(buffer);
        buffer.release();
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);

    }

    private static void sendRedirect(ChannelHandlerContext ct, String newUri) {
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.FOUND);
        response.headers().set("location", newUri);
        ct.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    private static void sendError(ChannelHandlerContext ctx, HttpResponseStatus status) {
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status, Unpooled
                .copiedBuffer("Failure:" + status + "\r\n", CharsetUtil.UTF_8));
        ctx.writeAndFlush(response).addListener(
                ChannelFutureListener.CLOSE
        );
    }

    private String sanitizeUri(String uri) throws UnsupportedEncodingException {
        uri = URLDecoder.decode(uri, "utf-8");
        uri.replace('/', File.separatorChar);
        return System.getProperty("user.dir") + File.separator + uri;
    }

    public void execute(ChannelHandlerContext ctx, FullHttpRequest request) throws IOException {
        if (!request.getDecoderResult().isSuccess()) {
            sendError(ctx, HttpResponseStatus.BAD_REQUEST);
            return;
        }
        if (request.getMethod() != HttpMethod.GET) {
            sendError(ctx, HttpResponseStatus.METHOD_NOT_ALLOWED);
            return;
        }
        String uri = request.getUri();
        String path = sanitizeUri(uri);
        File file = new File(path);
        if (!file.exists()) {
            sendError(ctx, HttpResponseStatus.NOT_FOUND);
            return;
        }
        if (file.isDirectory()) {
            if (uri.endsWith("/")) {
                sendListing(ctx, file);
            } else {
                sendRedirect(ctx, uri + '/');
            }
            return;
        }
        if (!file.isFile()) {
            sendError(ctx, HttpResponseStatus.FORBIDDEN);
            return;
        }

        RandomAccessFile randomAccessFile = null;
        try {
            randomAccessFile = new RandomAccessFile(file, "r");
        } catch (FileNotFoundException e) {
            sendError(ctx, HttpResponseStatus.NOT_FOUND);
            return;
        }
        long fileLen = randomAccessFile.length();
        HttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
        response.headers().set("content-length", fileLen);
        MimetypesFileTypeMap map = new MimetypesFileTypeMap();
        response.headers().set("content-type", map.getContentType(file.getPath()));
        if ("keep-alive".equals(request.headers().get("Connection"))) {
            response.headers().set("Connection", HttpHeaders.Values.KEEP_ALIVE);
        }
        ctx.write(response);
        ChannelFuture sendFuture;
        sendFuture = ctx.write(new ChunkedFile(randomAccessFile, 0, fileLen, 8192)
                , ctx.newProgressivePromise());
        sendFuture.addListener(new ChannelProgressiveFutureListener() {

            @Override
            public void operationComplete(ChannelProgressiveFuture future) throws Exception {
                log.info("Transfer complete.");
            }

            @Override
            public void operationProgressed(ChannelProgressiveFuture future, long progress, long total) throws Exception {

            }
        });
        ChannelFuture lastContentFuture = ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
        if (!"keep-alive".equals(request.headers().get("Connection"))) {
            lastContentFuture.addListener(ChannelFutureListener.CLOSE);
        }
    }
}
