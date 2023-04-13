package cn.holelin.netty;


import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import static cn.holelin.netty.websocket.utils.ByteBufferUtil.debugAll;


public class TestByteBuffer {

    @Test
    public void testFileChannel() {
        // FileChannel
        // 获取方式1. 输入输出流 2. RandomAccessFile
        try (final FileChannel channel = new FileInputStream("data.txt").getChannel()) {
            // 准备缓冲区
            final ByteBuffer buffer = ByteBuffer.allocate(10);

            // 循环获取
            while (true) {
                buffer.clear();
                // 从Channel中读取数据,向buffer写入
                int len = channel.read(buffer);
                if (len == -1) {
                    break;
                }

                // 打印buffer的内容
                // 切换至读模式
                buffer.flip();
                while (buffer.hasRemaining()) {
                    final byte b = buffer.get();
                    System.out.print((char) b);
                }
                buffer.clear();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testWriteInByteBuffer() {
        final ByteBuffer buffer = ByteBuffer.allocate(10);
        buffer.put((byte) 0x61);
        debugAll(buffer);
        buffer.put(new byte[]{0x62, 0x63, 0x64});
        debugAll(buffer);
        // 在写模式下直接获取,是获取不到数据的
//        System.out.println(buffer.get());

        // 切换到读模式
        System.out.println("切换到读模式");
        buffer.flip();
        System.out.println(buffer.get());
        debugAll(buffer);

        buffer.compact();
        debugAll(buffer);
        buffer.put((byte) 0x61);
        debugAll(buffer);
    }

    @Test
    public void testAllocate() {
        System.out.println(ByteBuffer.allocate(16).getClass());
        System.out.println(ByteBuffer.allocateDirect(16).getClass());
    }

    @Test
    public void testReadFromByteBuffer() {
        final ByteBuffer buffer = ByteBuffer.allocate(10);
        buffer.put(new byte[]{'a', 'b', 'c', 'd'});
        buffer.flip();
        buffer.get(new byte[4]);
        debugAll(buffer);

        // rewind
        buffer.rewind();
        System.out.println((char) buffer.get());

        // mark & reset
        // mark 做一个标记,记录position位置
        // reset是将position重置mark的位置
        System.out.println((char) buffer.get());
        buffer.mark();
        System.out.println((char) buffer.get());
        System.out.println((char) buffer.get());
        buffer.reset();
        System.out.println((char) buffer.get());
        System.out.println((char) buffer.get());
    }

    @Test
    public void testByteBufferToString() {
        // 字符串==>byte[]==>byteBuffer
        final ByteBuffer buffer = ByteBuffer.allocate(16);
        final byte[] bytes = "holelin".getBytes();
        buffer.put(bytes);
        debugAll(buffer);

        // Charset并切换到读模式
        final ByteBuffer newBuffer = StandardCharsets.UTF_8.encode("holelin");
        debugAll(newBuffer);


        // wrap并切换到读模式
        final ByteBuffer wrapBuffer = ByteBuffer.wrap("holelin".getBytes());
        debugAll(wrapBuffer);

        // bytebuffer转字符串
        final CharBuffer decode = StandardCharsets.UTF_8.decode(newBuffer);
        System.out.println(decode.toString());
    }
}
