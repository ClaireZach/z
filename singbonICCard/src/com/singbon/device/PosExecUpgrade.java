package com.singbon.device;

import java.net.InetSocketAddress;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;

import com.singbon.entity.Device;
import com.singbon.util.StringUtil;

/**
 * 处理UDP升级命令
 * 
 * @author 郝威
 * 
 */
public class PosExecUpgrade implements Runnable {

	private byte[] b;
	@SuppressWarnings("rawtypes")
	private Map map;
	private Device device;
	private InetSocketAddress inetSocketAddress;

	@SuppressWarnings("rawtypes")
	public PosExecUpgrade(Device device, byte[] b, Map map, InetSocketAddress inetSocketAddress) {
		this.device = device;
		this.b = b;
		this.map = map;
		this.inetSocketAddress = inetSocketAddress;
	}

	@SuppressWarnings("unchecked")
	public void run() {
		List<String> upgradeList = TerminalManager.FilenameToUpgradeStrsList.get(device.getUpgradeType());
		int upgradeCount = upgradeList.size();
		int index = Integer.parseInt(StringUtil.getHexStrFromBytes(b.length - 6, b.length - 3, b), 16);
		int size = 300;
		// 如果升级完毕发送返回应用程序命令
		if ((float) index / size + (float) index % size >= upgradeCount) {
			String sendBufStr = StringUtil.hexLeftPad(PosFrame.Sys04, 2) + StringUtil.hexLeftPad(PosSubFrameSys04.UpgradeEnd, 2) + "0000" + "0000" + "0000";
			String bufLen = StringUtil.hexLeftPad(2 + sendBufStr.length() / 2, 4);
			sendBufStr = device.getSn() + StringUtil.hexLeftPad(device.getDeviceNum(), 8) + CommandDevice.NoSubDeviceNum + DeviceType.Main + DeviceType.getDeviceTypeFrame(device) + bufLen
					+ sendBufStr;
			byte[] sendBuf = StringUtil.strTobytes(sendBufStr);
			try {
				TerminalManager.sendToPos(inetSocketAddress, sendBuf);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			// 发送升级包
			String upgradeStr = upgradeList.get(index / size);
			String sendBufStr = StringUtil.hexLeftPad(PosFrame.Sys04, 2) + StringUtil.hexLeftPad(PosSubFrameSys04.UpgradeAppend, 2) + "0000" + "0000" + StringUtil.hexLeftPad(index, 8) + upgradeStr
					+ "0000";
			String bufLen = StringUtil.hexLeftPad(2 + sendBufStr.length() / 2, 4);
			sendBufStr = device.getSn() + StringUtil.hexLeftPad(device.getDeviceNum(), 8) + CommandDevice.NoSubDeviceNum + DeviceType.Main + DeviceType.getDeviceTypeFrame(device) + bufLen
					+ sendBufStr;
			byte[] sendBuf = StringUtil.strTobytes(sendBufStr);
			try {
				TerminalManager.sendToPos(inetSocketAddress, sendBuf);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		// 向监控平台发送升级进度
		map.put("type", "schedule");
		map.remove("time");
		map.remove("from");
		float s = (float) index / 3 / upgradeCount + (float) index % size;
		s = s > 100 ? 100 : s;
		DecimalFormat df = new DecimalFormat("0.00");
		map.put("s", df.format(s));
		// 向监控平台发送命令
		TerminalManager.sendToMonitor(map, device.getCompanyId());
	}
}