package com.plusme.rope.model;

import com.google.protobuf.InvalidProtocolBufferException;
import io.netty.buffer.*;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import org.junit.Test;

import java.nio.charset.Charset;

/**
 * @author plusme
 * @create 2019-11-30 13:54
 */
public class ModelTest {


    private static final ByteBuf buf = Unpooled.buffer(1024);


    private static void handleArray(byte[] array, int offset, int len) {
        System.err.print("输出为：");
        for (int i = offset; i <= (offset - 1) + len; i++) {
            System.err.print(array[i] + " ");
        }
        System.err.println("");
    }

    public static void main(String[] args) throws InvalidProtocolBufferException {
        ByteBuf buf = (UnpooledHeapByteBuf) Unpooled.buffer(1024);
        buf.writeInt(34);
        if (buf.hasArray()) {
            byte[] arr = buf.array();
            int offset = buf.arrayOffset() + buf.readerIndex();
            int len = buf.readableBytes();
            handleArray(arr, offset, len);
        }
    }

    @Test
    public void testCopy() {
        ByteBuf buf = Unpooled.buffer(1024);
        System.err.println(buf.writableBytes());
        buf.writeBytes("wwx中国".getBytes());
        System.err.println(buf.writableBytes());

        while (buf.isReadable()) {
            System.err.print(buf.readByte() + " ");
        }
    }

    @Test
    public void byteProcessor() {
        ByteBuf buf = ModelTest.buf;
        buf.writeBytes("wwx\r".getBytes());
        int r_offset = buf.forEachByte(ByteBufProcessor.FIND_CR);
        System.err.println(r_offset);
    }

    @Test
    public void byteSlice() {
        Charset utf8 = Charset.forName("UTF-8");
        //创建一个用于保存给定字符串的字节的 ByteBuf
        ByteBuf buf = Unpooled.copiedBuffer("Netty in Action rocks!", utf8);
        //创建该 ByteBuf 从索引 0 开始到索引 15 结束的一个新切片
        ByteBuf sliced = buf.slice(0, 15);
        //将打印“Netty in Action”
        System.out.println(sliced.toString(utf8));
        //更新索引 0 处的字节
        buf.setByte(0, (byte) 'J');
        //将会成功，因为数据是共享的，对其中一个所做的更改对另外一个也是可见的
        //assert buf.getByte(0) == sliced.getByte(0);
        System.err.println(sliced.toString(utf8));
        System.err.println(buf.toString(utf8));

    }

    public void byteCopy() {
        Charset utf8 = Charset.forName("UTF-8");
        //创建 ByteBuf 以保存所提供的字符串的字节
        ByteBuf buf = Unpooled.copiedBuffer("Netty in Action rocks!", utf8);
        //创建该 ByteBuf 从索引 0 开始到索引 15 结束的分段的副本
        ByteBuf copy = buf.copy(0, 15);
        //将打印“Netty in Action”
        System.out.println(copy.toString(utf8));
        //更新索引 0 处的字节
        buf.setByte(0, (byte) 'J');
        //将会成功，因为数据不是共享的
        assert buf.getByte(0) != copy.getByte(0);
    }

    @Test
    public void byteBufWriteRead() {
        Charset utf8 = Charset.forName("UTF-8");
        //创建一个新的 ByteBuf 以保存给定字符串的字节
        ByteBuf buf = Unpooled.copiedBuffer("Netty in Action rocks!", utf8);
        System.err.println(buf.readerIndex());
        //打印第一个字符'N'
        System.err.println((char) buf.readByte());
        //存储当前的readerIndex
        int readerIndex = buf.readerIndex();
        System.err.println(readerIndex);
        //存储当前的writerIndex
        int writerIndex = buf.writerIndex();
        System.err.println(writerIndex);
        //将字符 '?'追加到缓冲区
        buf.writeByte((byte) '?');
        buf.readInt();
        System.err.println(buf.toString(utf8));
        assert readerIndex == buf.readerIndex();
        //将会成功，因为 writeByte()方法移动了 writerIndex
        assert writerIndex != buf.writerIndex();
    }

    private static final ByteBuf BYTE_BUF_FROM_SOMEWHERE = Unpooled.buffer(1024);
    private static final Channel CHANNEL_FROM_SOMEWHERE = null;//new NioSocketChannel(null);
    private static final ChannelHandlerContext CHANNEL_HANDLER_CONTEXT_FROM_SOMEWHERE = null;


    @Test
    public void testPool() {

//        boolean f = PooledByteBufAllocator.DEFAULT.isDirectBufferPooled();
        ByteBuf buf = PooledByteBufAllocator.DEFAULT.buffer(1024);
        System.err.println(buf.isDirect());
        buf.writeInt(123124);
        System.err.println(buf.readInt());

    }

    @Test
    public void testBufHolder() {
        System.err.println(System.getProperty("line.separator"));
    }

    /**
     * 代码清单 5-14 获取一个到 ByteBufAllocator 的引用
     */
    public static void obtainingByteBufAllocatorReference() {
        Channel channel = CHANNEL_FROM_SOMEWHERE; //get reference form somewhere
        //从 Channel 获取一个到ByteBufAllocator 的引用
        ByteBufAllocator allocator = channel.alloc();
        //...
        ChannelHandlerContext ctx = CHANNEL_HANDLER_CONTEXT_FROM_SOMEWHERE; //get reference form somewhere
        //从 ChannelHandlerContext 获取一个到 ByteBufAllocator 的引用
        ByteBufAllocator allocator2 = ctx.alloc();
        //...
    }

    /**
     * 代码清单 5-15 引用计数
     */
    public static void referenceCounting() {
        Channel channel = CHANNEL_FROM_SOMEWHERE; //get reference form somewhere
        //从 Channel 获取ByteBufAllocator
        ByteBufAllocator allocator = channel.alloc();
        //...
        //从 ByteBufAllocator分配一个 ByteBuf
        ByteBuf buffer = allocator.directBuffer();
        //检查引用计数是否为预期的 1
        assert buffer.refCnt() == 1;
        //...
    }

    /**
     * 代码清单 5-16 释放引用计数的对象
     */
    public static void releaseReferenceCountedObject() {
        ByteBuf buffer = BYTE_BUF_FROM_SOMEWHERE; //get reference form somewhere
        //减少到该对象的活动引用。当减少到 0 时，该对象被释放，并且该方法返回 true
        boolean released = buffer.release();
        //...
    }

}
