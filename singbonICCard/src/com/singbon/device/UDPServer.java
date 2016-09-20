package com.singbon.device;

import java.util.Arrays;

import com.singbon.util.StringUtil;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;

/**
 * 
 * UDP监听和分发服务
 * 
 * @author 郝威
 * 
 */
public class UDPServer implements Runnable {

	public void startServer() {

		EventLoopGroup group = new NioEventLoopGroup();
		Bootstrap b = new Bootstrap();
		b.group(group).channel(NioDatagramChannel.class).option(ChannelOption.SO_BROADCAST, true).handler(new UDPSeverHandler());

		try {
			b.bind(10002).sync().channel().closeFuture().await();
		} catch (InterruptedException e) {
			e.printStackTrace();
			group.shutdownGracefully();
		}
	}

	public static void main(String[] args) {
		new UDPServer().startServer();
	}

	@Override
	public void run() {
		try {
			new UDPServer().startServer();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

class UDPSeverHandler extends SimpleChannelInboundHandler<DatagramPacket> {

	@Override
	public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
		TerminalManager.ctx = ctx;
		super.channelRegistered(ctx);
	}

	protected void messageReceived(ChannelHandlerContext ctx, DatagramPacket packet) throws Exception {
		if (!TerminalManager.SystemRunning)
			return;

		ByteBuf buf = (ByteBuf) packet.copy().content();
		byte[] b = new byte[buf.readableBytes()];
		buf.readBytes(b);

		// 处理数据
		try {
			if (b.length < 30)
				return;

			byte[] tmpBuf = null;
			int len = StringUtil.hexToInt(28, 29, b);
			if (b.length < 28 + len) {
				return;
			} else if (b.length > 28 + len) {
				tmpBuf = Arrays.copyOf(b, 28 + len);
			} else {
				tmpBuf = b;
			}
			// 校验
			if (!CRC16.compareCRC16(tmpBuf)) {
				return;
			}
			PosExecCommandDispatch.execCommand(packet.sender(), tmpBuf);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}