package com.singbon.device;

import java.net.InetSocketAddress;
import java.util.Map;

import com.singbon.entity.Device;
import com.singbon.util.JdbcUtil;
import com.singbon.util.StringUtil;

/**
 * 处理在线充值请求
 * 
 * @author 郝威
 * 
 */
public class PosExecChargeRequest implements Runnable {

	private byte[] b;
	private Device device;

	public PosExecChargeRequest(Device device, byte[] b) {
		this.device = device;
		this.b = b;
	}

	// 处理在线充值请求
	@SuppressWarnings("rawtypes")
	public void run() {
		long userId = Long.parseLong(StringUtil.getHexStrFromBytes(36, 39, b), 16);
		int version = Integer.parseInt(StringUtil.getHexStrFromBytes(40, 41, b), 16);
		String sql = String.format("select opFare,version from chargeRecord where userId=%s and status=0 and version>%s limit 0,1", userId, version);
		Map m = JdbcUtil.baseDAO.selectBySql(sql);
		if (m == null)
			return;

		// 帐号（4 字节）+版本号（2 字节）
		String sendBufStr = "00000000" + StringUtil.hexLeftPad(userId, 8) + StringUtil.hexLeftPad(StringUtil.objToInt(m.get("version")), 4)
				+ StringUtil.hexLeftPad(StringUtil.objToInt(m.get("opFare")), 8);

		sendBufStr = "DE02" + sendBufStr + "0000";
		String bufLen = StringUtil.hexLeftPad(2 + sendBufStr.length() / 2, 4);
		sendBufStr = device.getSn() + StringUtil.hexLeftPad(device.getDeviceNum(), 8) + CommandDevice.NoSubDeviceNum + DeviceType.Main + DeviceType.getDeviceTypeFrame(device) + bufLen + sendBufStr;
		byte[] sendBuf = StringUtil.strTobytes(sendBufStr);

		String sn = device.getSn();
		if (device.getTransferId() != null && device.getTransferId() != 0) {
			sn = TerminalManager.TransferIdToSNList.get(device.getTransferId());
		}

		InetSocketAddress inetSocketAddress = TerminalManager.SNToInetSocketAddressList.get(sn);
		try {
			TerminalManager.sendToPos(inetSocketAddress, sendBuf);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}