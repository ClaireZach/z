package com.singbon.device;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;

import org.springframework.util.StringUtils;

import com.singbon.entity.Device;
import com.singbon.util.JdbcUtil;
import com.singbon.util.StringUtil;

/**
 * 处理补助请求
 * 
 * @author 郝威
 * 
 */
public class PosExecSubsidyRequest implements Runnable {

	private byte[] b;
	private Device device;

	public PosExecSubsidyRequest(Device device, byte[] b) {
		this.device = device;
		this.b = b;
	}

	// 处理补助请求
	@SuppressWarnings("rawtypes")
	public void run() {
		long userId = Long.parseLong(StringUtil.getHexStrFromBytes(36, 39, b), 16);
		long cardNO = Long.parseLong(StringUtil.getHexStrFromBytes(40, 43, b), 16);
		int cardTypeId = Integer.parseInt(StringUtil.getHexStrFromBytes(44, 44, b), 16);
		int subsidyVersion = Integer.parseInt(StringUtil.getHexStrFromBytes(45, 46, b), 16);
		String sql = String.format(
				"select s.subsidyVersion,s.subsidyFare from user u left join subsidy s on u.userId=s.userId where u.companyId=%s and u.userId=%s and u.status=241 and s.subsidyStatus=1 and s.subsidyVersion>%s",
				device.getCompanyId(), userId, subsidyVersion);
		List<Map> list = JdbcUtil.baseDAO.selectListBySql(sql);
		if (list.size() == 0)
			return;
		// 补助帐号（4 字节）+补助卡号（4 字节）+卡种类（1 字节参数
		// 卡或者消费卡）+补助版本号（2 字节）+补助金额（4 字节）
		String sendBufStr = "00000000" + StringUtil.hexLeftPad(userId, 8) + StringUtil.hexLeftPad(cardNO, 8) + StringUtil.hexLeftPad(cardTypeId, 2);
		Map m = list.get(0);
		if (StringUtils.isEmpty(m.get("subsidyVersion")) || StringUtils.isEmpty(m.get("subsidyFare")))
			return;

		int subsidyFare = (int) (StringUtil.objToFloat(m.get("subsidyFare")) * 100);
		sendBufStr = "0601" + sendBufStr + StringUtil.hexLeftPad(subsidyFare, 8) + StringUtil.hexLeftPad(StringUtil.objToInt(m.get("subsidyVersion")), 4) + "0000";
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