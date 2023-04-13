package cn.holelin.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import static io.netty.buffer.ByteBufUtil.appendPrettyHexDump;
import static io.netty.util.internal.StringUtil.NEWLINE;

@Slf4j
public class TestByteBuf {


    @Test
    void testByteBufCreate() {
        ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer(10);
        // 使用堆内存来创建
        ByteBuf buffer2 = ByteBufAllocator.DEFAULT.heapBuffer(10);
        // 使用直接内存来创建
        ByteBuf buffer3 = ByteBufAllocator.DEFAULT.directBuffer(10);

        buffer.writeInt(1);
        buffer.writeInt(1);
        buffer.writeInt(1);
        log(buffer);
    }

    private static void log(ByteBuf buffer) {
        int length = buffer.readableBytes();
        int rows = length / 16 + (length % 15 == 0 ? 0 : 1) + 4;
        StringBuilder buf = new StringBuilder(rows * 80 * 2)
                .append("read index:").append(buffer.readerIndex())
                .append(" write index:").append(buffer.writerIndex())
                .append(" capacity:").append(buffer.capacity())
                .append(NEWLINE);
        appendPrettyHexDump(buf, buffer);
        System.out.println(buf.toString());
    }
}
