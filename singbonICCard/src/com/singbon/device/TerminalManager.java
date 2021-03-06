package com.singbon.device;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.comet4j.core.CometContext;
import org.comet4j.core.CometEngine;
import org.comet4j.core.util.JSONUtil;

import com.singbon.entity.Device;
import com.singbon.entity.Meal;
import com.singbon.util.StringUtil;

/**
 * 终端设备通信监控管理
 * 
 * @author 郝威
 * 
 */
public class TerminalManager {

	/**
	 * 系统是否启动
	 */
	public static boolean SystemRunning = false;

	/**
	 * 服务器推送连接引擎
	 */
	public static CometEngine EngineInstance = null;

	/**
	 * netty
	 */
	public static ChannelHandlerContext ctx = null;

	/**
	 * UUID到SN序列号映射列表
	 */
	public static Map<String, String> UuidToSNList = new HashMap<>();

	/**
	 * SN序列号到设备映射列表
	 */
	public static Map<String, Device> SNToDeviceList = new HashMap<>();

	/**
	 * 中转通信器ID到SN列表，中转设备用
	 */
	public static Map<Integer, String> TransferIdToSNList = new HashMap<>();

	/**
	 * SN序列号到套接字通道映射列表
	 */
	public static Map<String, SocketChannel> SNToSocketChannelList = new HashMap<>();

	/**
	 * SN序列号到InetSocketAddress映射列表
	 */
	public static Map<String, InetSocketAddress> SNToInetSocketAddressList = new HashMap<>();

	/**
	 * 公司ID到采集监控命令多线程映射列表
	 */
	public static Map<Integer, List<Thread>> CompanyIdToMonitorThreadList = new HashMap<>();

	/**
	 * SN序列号到发送命令映射列表
	 */
	public static Map<String, ArrayList<SendCommand>> SNToSendCommandList = new HashMap<>();

	/**
	 * 公司ID到采集监控工作模式映射列表
	 */
	public static Map<Integer, Integer> CompanyIdToMonitorModeList = new HashMap<>();

	/**
	 * 公司ID到餐别限次映射列表(消费记录、监控用)
	 */
	public static Map<Integer, List<Meal>> CompanyIdToMealList = new HashMap<>();

	/**
	 * 公司ID到最后一个批次ID映射列表(自动下载黑名单用)
	 */
	public static Map<Integer, Integer> CompanyIdToLastBatchIdList = new HashMap<>();

	/**
	 * 公司ID到最后一个增量黑名单映射列表(自动下载黑名单用)
	 */
	public static Map<Integer, Long> CompanyIdToLastBlackNumList = new HashMap<>();

	/**
	 * 公司ID到监控是否允许映射列表(自动下载用)
	 */
	public static Map<Integer, Boolean> CompanyIdToMonitorRunningList = new HashMap<>();

	/**
	 * 文件名到升级文件映射列表
	 */
	public static Map<String, List<String>> FilenameToUpgradeStrsList = new HashMap<>();
	
	/**
	 * 文件名到升级文件总包数映射列表
	 */
	public static Map<String, Integer> FilenameToUpgradeSizeList = new HashMap<>();

	// ///////////////////////////////////////////////////////////////////////////////////////////////////////////
	// //////////////////////////////监控用
	// ///////////////////////////////////////////////////////////////////////////////////////////////////////////
	// 发送命令锁
	public static Object sendCommandObject = new Object();

	/**
	 * 注册通道
	 * 
	 * @param sn
	 */
	public static void registChannel(String channelId) {
		if (!CometContext.getInstance().getAppModules().contains(channelId)) {
			CometContext.getInstance().registChannel(channelId);
		}
	}

	/**
	 * 获取通道
	 * 
	 * @param sn
	 * @return
	 */
	public static SocketChannel getSocketChannel(String sn) {
		return SNToSocketChannelList.get(sn);
	}

	/**
	 * 关闭连接通道
	 * 
	 * @param socketChannel
	 * @throws IOException
	 */
	public static void closeSocketChannel(String sn) throws IOException {
		SocketChannel socketChannel = TerminalManager.SNToSocketChannelList.get(sn);
		socketChannel.close();
		TerminalManager.SNToSocketChannelList.remove(sn);
	}

	/**
	 * 先获取卡信息后操作
	 * 
	 * @param socketChannel
	 * @param device
	 * @param commandCode
	 * @param sectionBlocks
	 * @throws IOException
	 */
	public static void getCardInfo(SocketChannel socketChannel, Device device, byte commandCode, List<Integer> sectionBlocks) throws IOException {
		String deviceNum = StringUtil.hexLeftPad(device.getDeviceNum(), 8);
		String commandCodeStr = "0000" + StringUtil.hexLeftPad(commandCode, 4);
		String sendBufStr = CardReaderFrame.ReadCard + commandCodeStr + CardReaderFrame.NoValidateCardSN + CardReaderFrame.NoCardSN;
		for (int i : sectionBlocks) {
			int section = i / 10;
			int block = i % 10;
			String sectionStr = StringUtil.hexLeftPad(section, 2);
			String blockStr = StringUtil.hexLeftPad(block, 2);
			sendBufStr += sectionStr + blockStr + StringUtil.strLeftPad("", 34);
		}
		sendBufStr += "0000";
		String bufLen = StringUtil.hexLeftPad(2 + sendBufStr.length() / 2, 4);
		sendBufStr = device.getSn() + deviceNum + CommandDevice.NoSubDeviceNum + "0000" + "0808" + bufLen + sendBufStr;
		byte[] sendBuf = StringUtil.strTobytes(sendBufStr);
		CRC16.generate(sendBuf);
		ByteBuffer byteBuffer = ByteBuffer.wrap(sendBuf);
		socketChannel.write(byteBuffer);
	}

	/**
	 * 发送命令到读卡机
	 * 
	 * @param socketChannel
	 * @param b
	 * @throws IOException
	 */
	public static void sendToCardReader(SocketChannel socketChannel, byte[] sendBuf) throws Exception {
		CRC16.generate(sendBuf);
		StringUtil.print(StringUtil.toHexString(sendBuf[sendBuf.length - 2]) + " ");
		StringUtil.print(StringUtil.toHexString(sendBuf[sendBuf.length - 1]) + " ");
		ByteBuffer byteBuffer = ByteBuffer.wrap(sendBuf);
		socketChannel.write(byteBuffer);
	}

	/**
	 * 发送命令到消费机， 改方法负责crc校验
	 * 
	 * @param socketChannel
	 * @param b
	 * @throws IOException
	 */
	public static void sendToPos(InetSocketAddress inetSocketAddress, byte[] b) throws Exception {
		CRC16.generate(b);
		StringUtil.print(StringUtil.toHexString(b[b.length - 2]) + " ");
		StringUtil.print(StringUtil.toHexString(b[b.length - 1]) + " ");
		ByteBuf buf = Unpooled.copiedBuffer(b);
		DatagramPacket datagramPacket = new DatagramPacket(buf, inetSocketAddress);
		TerminalManager.ctx.writeAndFlush(datagramPacket);
	}

	/**
	 * 发送消息到卡操作
	 * 
	 * @param map
	 * @param sn
	 * @throws IOException
	 */
	@SuppressWarnings("rawtypes")
	public static void sendToCardManager(Map map, String sn) {
		String msg = JSONUtil.convertToJson(map);
		TerminalManager.EngineInstance.sendToAll("c" + sn, msg);
	}

	/**
	 * 发送消息到监控平台
	 * 
	 * @param map
	 * @param companyId
	 * @throws IOException
	 */
	@SuppressWarnings("rawtypes")
	public static void sendToMonitor(Map map, Integer companyId) {
		String msg = JSONUtil.convertToJson(map);
		TerminalManager.EngineInstance.sendToAll("Co" + companyId, msg);
	}
}
