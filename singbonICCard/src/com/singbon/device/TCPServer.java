package com.singbon.device;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Arrays;
import java.util.Iterator;
import java.util.UUID;

import com.singbon.util.StringUtil;

/**
 * TCP监听和分发服务
 * 
 * @author 郝威
 * 
 */
public class TCPServer implements Runnable {

	protected Selector selector;

	public void startServer() {
		try {
			selector = Selector.open();

			ServerSocketChannel ssc = ServerSocketChannel.open();
			ssc.socket().bind(new InetSocketAddress(10001)); // port
			ssc.configureBlocking(false);
			ssc.register(selector, SelectionKey.OP_ACCEPT);// register

			while (true) {
				// selector 线程。select() 会阻塞，直到有客户端连接，或者有消息读入
				selector.select();
				Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
				while (iterator.hasNext()) {
					SelectionKey selectionKey = iterator.next();
					iterator.remove(); // 删除此消息
					// 并在当前线程内处理
					handleSelectionKey(selectionKey);
				}
			}
		} catch (Exception e) {
//			e.printStackTrace();
		}
	}

	int i = 0;

	public void handleSelectionKey(SelectionKey selectionKey) throws Exception {
		if (selectionKey.isAcceptable()) {
			ServerSocketChannel ssc = (ServerSocketChannel) selectionKey.channel();
			SocketChannel socketChannel = ssc.accept();
			socketChannel.configureBlocking(false);
			// 立即注册一个 OP_READ 的SelectionKey, 接收客户端的消息
			SelectionKey key = socketChannel.register(selector, SelectionKey.OP_READ);

			String uuid = UUID.randomUUID().toString();
			key.attach(uuid);
			// 打印
			StringUtil.println(key.attachment() + " connect successful ");
		} else if (selectionKey.isReadable()) {
			if (!TerminalManager.SystemRunning)
				return;
			// 有消息进来
			ByteBuffer byteBuffer = ByteBuffer.allocate(273);
			SocketChannel sc = (SocketChannel) selectionKey.channel();

			int len = 0;
			try {
				len = sc.read(byteBuffer);
			} catch (Exception e) {
//				e.printStackTrace();
				String uuid = selectionKey.attachment().toString();
				StringUtil.println(uuid);
				removeSockeckChannel(uuid);
				// 如果read抛出异常，表示连接异常中断，需要关闭 socketChannel
				sc.close();
			}
			// 如果len>0，表示有输入。如果len==0, 表示输入结束。需要关闭 socketChannel
			if (len > 0) {
				byteBuffer.flip();
				byte[] b = byteBuffer.array();
				// for (byte b2 : b) {
				// StringUtil.print(StringUtil.toHexString(b2) + " ");
				// }
				// StringUtil.println("");

				byteBuffer.clear();

				// 处理数据
				try {
					if (b.length < 30)
						return;

					byte[] tmpBuf = null;
					len = StringUtil.hexToInt(28, 29, b);
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
					CardReaderCommandExec.execCommand(selectionKey, tmpBuf);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				// 输入结束，关闭 socketChannel
				String uuid = selectionKey.attachment().toString();
				StringUtil.println(uuid + "connect closed");
				sc.close();
				removeSockeckChannel(uuid);
			}

			Thread.sleep(100);
		}
	}

	private String removeSockeckChannel(String uuid) {
		String sn = null;
		if (TerminalManager.UuidToSNList.containsKey(uuid)) {
			sn = TerminalManager.UuidToSNList.get(uuid);
			TerminalManager.UuidToSNList.remove(uuid);
			TerminalManager.SNToSocketChannelList.remove(sn);
		}
		return sn;
	}

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		new TCPServer().startServer();
	}

	@Override
	public void run() {
		try {
			new TCPServer().startServer();
		} catch (Exception e) {
//			e.printStackTrace();
		}
	}
}
