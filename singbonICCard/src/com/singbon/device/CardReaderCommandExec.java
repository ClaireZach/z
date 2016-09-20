package com.singbon.device;

import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.comet4j.core.util.JSONUtil;

import com.singbon.entity.Device;
import com.singbon.util.StringUtil;

/**
 * 处理读卡机回复命令
 * 
 * @author 郝威
 * 
 */
public class CardReaderCommandExec {

	/**
	 * 分发命令
	 * 
	 * @param selectionKey
	 * @param b
	 *            26 27指令 00 获取机器号序列号，01读卡，
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void execCommand(SelectionKey selectionKey, byte[] b) {
		if (b == null)
			return;
		String sn = StringUtil.getHexStrFromBytes(0, 15, b).toUpperCase();
		Device device = TerminalManager.SNToDeviceList.get(sn);
		if (device == null || device.getDeviceType() != 8)
			return;
		// 帧
		byte[] frameByte = Arrays.copyOfRange(b, 30, 34);
		// 命令码
		int commandCode = StringUtil.byteToInt(b[36]) * 256 + StringUtil.byteToInt(b[37]);
		// 状态码 1读写成功、2寻卡失败、3卡校验失败、4物理卡号不匹配、5读写卡失败
		byte cardStatus = 0;
		// 物理卡号
		String cardSN = null;
		if (frameByte[0] == 0x03 && frameByte[1] == (byte) 0xcd) {
			cardStatus = b[45];
			cardSN = StringUtil.getHexStrFromBytes(39, 42, b);
		}

		// 是否升级模式
		boolean upgrade = false;

		Map map = new HashMap();
		map.put("sn", sn);
		// 心跳包
		if (frameByte[0] == 0x03 && frameByte[1] == 0x08) {
			heart(selectionKey, b, sn, device, map);
			// 下载读卡机参数，命令码为0是手动下载为1是自动校时不做页面处理
		} else if ((frameByte[1] == 0x04 || frameByte[1] == 0x07) && commandCode == 0) {
			upgrade = paramAndUpgrade(selectionKey, b, sn, device, frameByte, upgrade, map);
			// 写卡回复
		} else if (Arrays.equals(frameByte, new byte[] { 0x03, (byte) 0xcd, 0x01, 0x01 })) {
			writeCardReply(commandCode, cardStatus, map);
			// 读卡回复
		} else if (Arrays.equals(frameByte, new byte[] { 0x03, (byte) 0xcd, 0x00, 0x01 })) {
			readCardReply(b, commandCode, cardStatus, cardSN, map);
		}
		if (upgrade) {
			String msg = JSONUtil.convertToJson(map);
			TerminalManager.EngineInstance.sendToAll("CR" + device.getCompanyId(), msg);
			return;
		}
		if (map.size() > 0) {
			TerminalManager.sendToCardManager(map, sn);
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static boolean paramAndUpgrade(SelectionKey selectionKey, byte[] b, String sn, Device device, byte[] frameByte, boolean upgrade, Map map) {
		// 单位名称
		if (frameByte[2] == 0x08) {
			map.put("'f1'", CardReaderResultCommandCode.CompanyName);
			// 密码
		} else if (frameByte[2] == 0x03) {
			map.put("'f1'", CardReaderResultCommandCode.CardReaderPwd);
			// 校时
		} else if (frameByte[2] == 0x01) {
			map.put("'f1'", CardReaderResultCommandCode.SysTime);
		} // 进入底层回复
		else if (frameByte[2] == 0x0D) {
			map.put("'f1'", CardReaderResultCommandCode.UpgradeUpdateDone);
			upgrade = true;
			// 返回应用程序回复
		} else if (frameByte[2] == 0x0C) {
			map.put("'f1'", CardReaderResultCommandCode.UpgradeEndDone);
			upgrade = true;
			// 升级包回复
		} else if (frameByte[2] == 0x0B) {
			List<String> upgradeList = TerminalManager.FilenameToUpgradeStrsList.get(device.getUpgradeType());
			int upgradeCount = upgradeList.size();
			int index = Integer.parseInt(StringUtil.getHexStrFromBytes(b.length - 6, b.length - 3, b), 16);
			SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
			TerminalManager.SNToSocketChannelList.put(sn, (SocketChannel) selectionKey.channel());

			int size = 300;
			// 如果升级完毕发送返回应用程序命令
			if ((float) index / size + (float) index % size >= upgradeCount) {
				String sendBufStr = CardReaderFrame.UpgradEnd + "0000" + "0000" + "0000";
				String bufLen = StringUtil.hexLeftPad(2 + sendBufStr.length() / 2, 4);
				sendBufStr = device.getSn() + StringUtil.hexLeftPad(device.getDeviceNum(), 8) + CommandDevice.NoSubDeviceNum + DeviceType.Main + DeviceType.CardReader + bufLen + sendBufStr;
				byte[] sendBuf = StringUtil.strTobytes(sendBufStr);
				try {
					TerminalManager.sendToCardReader(socketChannel, sendBuf);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				// 发送升级包
				String upgradeStr = upgradeList.get(index / size);
				String sendBufStr = CardReaderFrame.UpgradeAppend + "0000" + "0000" + StringUtil.hexLeftPad(index, 8) + upgradeStr + "0000";
				String bufLen = StringUtil.hexLeftPad(2 + sendBufStr.length() / 2, 4);
				sendBufStr = device.getSn() + StringUtil.hexLeftPad(device.getDeviceNum(), 8) + CommandDevice.NoSubDeviceNum + DeviceType.Main + DeviceType.CardReader + bufLen + sendBufStr;
				byte[] sendBuf = StringUtil.strTobytes(sendBufStr);
				try {
					TerminalManager.sendToCardReader(socketChannel, sendBuf);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			upgrade = true;
			map.put("'f1'", CardReaderResultCommandCode.UpgradeAppendDone);
			float s = (float) index / 3 / upgradeCount + (float) index % size;
			s = s > 100 ? 100 : s;
			DecimalFormat df = new DecimalFormat("0.00");
			map.put("s", df.format(s));
		}
		return upgrade;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static void readCardReply(byte[] b, int commandCode, byte cardStatus, String cardSN, Map map) {
		int baseLen = 43;
		// 发送单个发卡命令
		if (commandCode == CardReaderCommandCode.SingleCard) {
			map.put("'f1'", CardReaderResultCommandCode.SingleCardCmd);
			map.put("'r'", cardStatus);
			map.put("'cardSN'", cardSN);
		}
		// 发送信息发卡命令
		else if (commandCode == CardReaderCommandCode.InfoCard) {
			map.put("'f1'", CardReaderResultCommandCode.InfoCardCmd);
			map.put("'r'", cardStatus);
			map.put("'cardSN'", cardSN);
		}
		// 发送补卡命令
		else if (commandCode == CardReaderCommandCode.RemakeCard) {
			map.put("'f1'", CardReaderResultCommandCode.RemakeCardCmd);
			map.put("'r'", cardStatus);
			map.put("'cardSN'", cardSN);
		}
		// 命令：解挂、注销、读卡
		else if (commandCode == CardReaderCommandCode.Unloss || commandCode == CardReaderCommandCode.CardOff || commandCode == CardReaderCommandCode.ReadCard) {
			if (commandCode == CardReaderCommandCode.Unloss) {
				map.put("'f1'", CardReaderResultCommandCode.UnlossCmd);
				map.put("'cardInfoStr'", StringUtil.getHexStrFromBytes(baseLen, baseLen + 19 - 1, b));
			} else if (commandCode == CardReaderCommandCode.CardOff) {
				map.put("'f1'", CardReaderResultCommandCode.CardOffCmd);
			} else if (commandCode == CardReaderCommandCode.ReadCard) {
				map.put("'f1'", CardReaderResultCommandCode.ReadCardCmd);
			}

			map.put("'r'", cardStatus);
			map.put("'cardSN'", cardSN);

			int base0 = baseLen + 3;
			try {
				map.put("'userId'", Long.parseLong(StringUtil.getHexStrFromBytes(base0, base0 + 3, b), 16));
			} catch (Exception e) {
				map.put("userId", "未知");
			}
			try {
				map.put("'cardNO'", Long.parseLong(StringUtil.getHexStrFromBytes(base0 + 4, base0 + 7, b), 16));
			} catch (Exception e) {
				map.put("cardNO", "未知");
			}
			try {
				map.put("'endDate'", StringUtil.dateFromHexStr(StringUtil.getHexStrFromBytes(base0 + 10, base0 + 11, b)));
			} catch (Exception e) {
				map.put("endDate", "未知");
			}
			try {
				int status = Integer.parseInt(StringUtil.getHexStrFromBytes(base0 + 12, base0 + 12, b), 16);
				String statusDesc = "正常";
				if (status == 241) {
					statusDesc = "正常";
				} else if (status == 243) {
					statusDesc = "挂失";
				} else if (status == 244) {
					statusDesc = "注销";
				} else {
					statusDesc = "异常";
				}

				map.put("'status'", status);
				map.put("'statusDesc'", statusDesc);
			} catch (Exception e) {
				map.put("status", "未知");
				map.put("statusDesc", "未知");
			}

			int base2 = baseLen + 19 + 3;
			try {
				map.put("'cardSeq'", Integer.parseInt(StringUtil.getHexStrFromBytes(base2 + 4, base2 + 4, b), 16));
			} catch (Exception e) {
				map.put("cardSeq", "未知");
			}
			try {
				map.put("'cardTypeId'", Integer.parseInt(StringUtil.getHexStrFromBytes(base2 + 5, base2 + 5, b), 16));
			} catch (Exception e) {
				map.put("cardTypeId", "未知");
			}
			try {
				map.put("'totalFare'", (float) StringUtil.hexToLong(base2 + 10, base2 + 13, b) / 100);
			} catch (Exception e) {
				map.put("totalFare", "未知");
			}

			int consume0 = baseLen + 19 * 2 + 3;
			try {
				map.put("'opCount'", Integer.parseInt(StringUtil.getHexStrFromBytes(consume0, consume0 + 1, b), 16));
			} catch (Exception e) {
				map.put("opCount", "未知");
			}
			try {
				map.put("'oddFare'", (float) StringUtil.hexToLong(consume0 + 3, consume0 + 5, b) / 100);
			} catch (Exception e) {
				map.put("oddFare", "未知");
			}

			int subsidy0 = baseLen + 19 * 3 + 3;
			try {
				map.put("'subsidyOpCount'", Integer.parseInt(StringUtil.getHexStrFromBytes(subsidy0, subsidy0 + 1, b), 16));
			} catch (Exception e) {
				map.put("subsidyOpCount", "未知");
			}
			try {
				map.put("'subsidyOddFare'", (float) StringUtil.hexToLong(subsidy0 + 2, subsidy0 + 5, b) / 100);
			} catch (Exception e) {
				map.put("subsidyOddFare", "未知");
			}
			try {
				map.put("'subsidyVersion'", Integer.parseInt(StringUtil.getHexStrFromBytes(subsidy0 + 8, subsidy0 + 9, b), 16));
			} catch (Exception e) {
				map.put("subsidyVersion", "未知");
			}
		}
		// 存取款读取卡信息命令
		else if (commandCode == CardReaderCommandCode.ReadCardOddFare) {
			map.put("'f1'", CardReaderResultCommandCode.ReadCardOddFareCmd);
			map.put("'r'", cardStatus);

			int base0 = baseLen + 3;
			map.put("'userId'", Long.parseLong(StringUtil.getHexStrFromBytes(base0, base0 + 3, b), 16));
			map.put("'cardNO'", Long.parseLong(StringUtil.getHexStrFromBytes(base0 + 4, base0 + 7, b), 16));
			map.put("'cardSN'", cardSN);

			int status = Integer.parseInt(StringUtil.getHexStrFromBytes(base0 + 12, base0 + 12, b), 16);
			String statusDesc = "正常";
			if (status == 241) {
				statusDesc = "正常";
			} else if (status == 242) {
				statusDesc = "未开户或注销";
			} else if (status == 243) {
				statusDesc = "挂失";
			} else {
				statusDesc = "异常";
			}

			map.put("'status'", status);
			map.put("'statusDesc'", statusDesc);

			int consume0 = baseLen + 19 * 2 + 3;
			map.put("'opCount'", Integer.parseInt(StringUtil.getHexStrFromBytes(consume0, consume0 + 1, b), 16));
			map.put("'oddFare'", (float) StringUtil.hexToLong(consume0 + 2, consume0 + 5, b) / 100);
			map.put("'cardInfoStr'", StringUtil.getHexStrFromBytes(baseLen + 19, b.length - 20, b));

			int subsidy0 = baseLen + 19 * 3 + 3;
			map.put("'subsidyOpCount'", Integer.parseInt(StringUtil.getHexStrFromBytes(subsidy0, subsidy0 + 1, b), 16));
			map.put("'subsidyOddFare'", (float) StringUtil.hexToLong(subsidy0 + 2, subsidy0 + 5, b) / 100);
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static void writeCardReply(int commandCode, byte cardStatus, Map map) {
		// 单个发卡完成
		if (commandCode == CardReaderCommandCode.SingleCard) {
			map.put("'f1'", CardReaderResultCommandCode.SingleCardDone);
			map.put("'r'", cardStatus);
		}
		// 信息发卡完成
		else if (commandCode == CardReaderCommandCode.InfoCard) {
			map.put("'f1'", CardReaderResultCommandCode.InfoCardDone);
			map.put("'r'", cardStatus);
		}
		// 解挂完成
		else if (commandCode == CardReaderCommandCode.Unloss) {
			map.put("'f1'", CardReaderResultCommandCode.UnlossDone);
			map.put("'r'", cardStatus);
		}
		// 卡注销完成
		else if (commandCode == CardReaderCommandCode.CardOff) {
			map.put("'f1'", CardReaderResultCommandCode.CardOffDone);
			map.put("'r'", cardStatus);
		}
		// 补卡完成
		else if (commandCode == CardReaderCommandCode.RemakeCard) {
			map.put("'f1'", CardReaderResultCommandCode.RemakeCardDone);
			map.put("'r'", cardStatus);
		}
		// 按库修正完成
		else if (commandCode == CardReaderCommandCode.UpdateByInfo) {
			map.put("'f1'", CardReaderResultCommandCode.UpdateByInfoDone);
			map.put("'r'", cardStatus);
		}
		// 存取款完成
		else if (commandCode == CardReaderCommandCode.Charge) {
			map.put("'f1'", CardReaderResultCommandCode.ChargeDone);
			map.put("'r'", cardStatus);
		}
		// 制功能卡完成
		else if (commandCode == CardReaderCommandCode.MakeFuncCard) {
			map.put("'f1'", CardReaderResultCommandCode.MakeFuncCardDone);
			map.put("'r'", cardStatus);
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static void heart(SelectionKey selectionKey, byte[] b, String sn, Device device, Map map) {
		byte frame = CardReaderResultCommandCode.HeartStatus;
		TerminalManager.UuidToSNList.put(selectionKey.attachment().toString(), sn);
		SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
		TerminalManager.SNToSocketChannelList.put(sn, (SocketChannel) selectionKey.channel());
		map.put("'f1'", frame);
		String msg = JSONUtil.convertToJson(map);
		TerminalManager.EngineInstance.sendToAll("CR" + device.getCompanyId(), msg);

		// 相差半分钟校时
		Calendar c1 = Calendar.getInstance();
		c1.set(StringUtil.objToInt("20" + StringUtil.getHexStrFromBytes(36, 36, b)), StringUtil.objToInt(StringUtil.getHexStrFromBytes(37, 37, b)),
				StringUtil.objToInt(StringUtil.getHexStrFromBytes(38, 38, b)), StringUtil.objToInt(StringUtil.getHexStrFromBytes(39, 39, b)),
				StringUtil.objToInt(StringUtil.getHexStrFromBytes(40, 40, b)), StringUtil.objToInt(StringUtil.getHexStrFromBytes(41, 41, b)));
		c1.add(Calendar.MONTH, -1);
		Calendar c2 = Calendar.getInstance();
		c2.setTime(new Date());

		if (Math.abs(c1.getTimeInMillis() - c2.getTimeInMillis()) > 30000) {
			String sendBufStr = CardReaderFrame.SysTime + "00000001" + StringUtil.timeToHexStr() + "0000";
			String bufLen = StringUtil.hexLeftPad(2 + sendBufStr.length() / 2, 4);
			sendBufStr = StringUtil.getHexStrFromBytes(0, 19, b) + CommandDevice.NoSubDeviceNum + DeviceType.Main + DeviceType.CardReader + bufLen + sendBufStr;
			byte[] sendBuf = StringUtil.strTobytes(sendBufStr);
			try {
				TerminalManager.sendToCardReader(socketChannel, sendBuf);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
