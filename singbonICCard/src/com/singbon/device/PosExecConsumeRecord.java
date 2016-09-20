package com.singbon.device;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.singbon.entity.Company;
import com.singbon.entity.ConsumeRecord;
import com.singbon.entity.Device;
import com.singbon.entity.EntranceTime;
import com.singbon.entity.Meal;
import com.singbon.entity.OpenIdsAndMobiles;
import com.singbon.util.JdbcUtil;
import com.singbon.util.StringUtil;

/**
 * 处理消费记录
 * 
 * @author 郝威
 * 
 */
public class PosExecConsumeRecord implements Runnable {

	private byte[] b;
	@SuppressWarnings("rawtypes")
	private Map map;
	private Device device;
	private InetSocketAddress inetSocketAddress;

	@SuppressWarnings("rawtypes")
	public PosExecConsumeRecord(Device device, byte[] b, Map map, InetSocketAddress inetSocketAddress) {
		this.device = device;
		this.b = b;
		this.map = map;
		this.inetSocketAddress = inetSocketAddress;
	}

	// 分解消费记录
	@SuppressWarnings({ "unchecked" })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public void run() {
		// 大帧：1为普通8为菜单
		int frame = b[30];
		ConsumeRecord record = new ConsumeRecord();
		int baseIndex = 30;

		record.setCompanyId(device.getCompanyId());

		int consumeType = (int) b[31];
		if (device.getDeviceType() == 3) {
			consumeType += 100;
		}
		record.setConsumeType(consumeType);
		record.setConsumeTypeDes(ConsumeType.getTypeDes(consumeType));

		record.setUserId(Long.parseLong(StringUtil.getHexStrFromBytes(baseIndex + 6, baseIndex + 9, b), 16));
		record.setCardNO(Long.parseLong(StringUtil.getHexStrFromBytes(baseIndex + 10, baseIndex + 13, b), 16));

		record.setCardSeq(StringUtil.hexToInt(baseIndex + 14, baseIndex + 14, b));
		record.setDeviceId(device.getId());
		record.setDeviceName(device.getDeviceName());

		record.setSumFare(StringUtil.hexToLong(baseIndex + 15, baseIndex + 18, b));
		record.setOddFare(StringUtil.hexToLong(baseIndex + 19, baseIndex + 22, b));
		record.setDiscountFare(StringUtil.hexToInt(baseIndex + 23, baseIndex + 26, b));
		record.setSubsidyOddFare(StringUtil.hexToLong(baseIndex + 27, baseIndex + 30, b));
		record.setOpFare(StringUtil.hexToInt(baseIndex + 31, baseIndex + 34, b));
		record.setOpCount(StringUtil.hexToInt(baseIndex + 35, baseIndex + 36, b));
		record.setSubsidyOpCount(StringUtil.hexToInt(baseIndex + 37, baseIndex + 38, b));
		record.setSubsidyOpFare(StringUtil.hexToInt(baseIndex + 39, baseIndex + 42, b));
		Calendar c = Calendar.getInstance();
		c.set(Calendar.YEAR, 2000 + StringUtil.byteToBCDInt(b[baseIndex + 43]));
		c.set(Calendar.MONTH, StringUtil.byteToBCDInt(b[baseIndex + 44]) - 1);
		c.set(Calendar.DAY_OF_MONTH, StringUtil.byteToBCDInt(b[baseIndex + 45]));
		c.set(Calendar.HOUR_OF_DAY, StringUtil.byteToBCDInt(b[baseIndex + 46]));
		c.set(Calendar.MINUTE, StringUtil.byteToBCDInt(b[baseIndex + 47]));
		c.set(Calendar.SECOND, StringUtil.byteToBCDInt(b[baseIndex + 48]));

		String opTime = StringUtil.dateFormat(c.getTime(), "yyyy-MM-dd HH:mm:ss");
		record.setOpTime(opTime);
		boolean isAttendance = device.getEntranceType() != null && device.getEntranceType() > 0;
		// 进出门状态
		int entranceStatus = -1;
		if (isAttendance) {
			if (device.getEntranceType() != 3) {
				entranceStatus = device.getEntranceType();
			} else {
				List<EntranceTime> list = (List<EntranceTime>) JdbcUtil.entranceTimeDAO.selectListByCompanyId(device.getCompanyId());
				String opEntranceTime = StringUtil.dateFormat(c.getTime(), "HH:mm");
				if (list != null) {
					for (EntranceTime entranceTime : list) {
						if (entranceTime.getBeginTime().compareTo(opEntranceTime) <= 0 && entranceTime.getEndTime().compareTo(opEntranceTime) >= 0) {
							entranceStatus = entranceTime.getStatus();
							break;
						}
					}
				}
			}
			record.setIsAttendance(1);
			record.setAttendanceStatus(entranceStatus);
		} else {
			record.setIsAttendance(0);
		}

		int recordNO = StringUtil.hexToInt(baseIndex + 49, baseIndex + 50, b);
		record.setRecordNO(recordNO);

		int addIndex = 0;
		// 订餐消费记录
		if (frame == 8) {
			record.setCookbookCode(StringUtil.hexToInt(baseIndex + 52, baseIndex + 53, b));
			record.setCookbookNum(StringUtil.hexToInt(baseIndex + 60, baseIndex + 61, b));
			addIndex = 11;
		}

		record.setRecordCount(StringUtil.hexToInt(baseIndex + addIndex + 52, baseIndex + addIndex + 53, b));
		record.setSubsidyAuth((b[baseIndex + addIndex + 60] >> 1) & 0x1);
		record.setCardSN(StringUtil.getHexStrFromBytes(baseIndex + addIndex + 67, baseIndex + addIndex + 70, b).toUpperCase());

		List<Meal> mealList = TerminalManager.CompanyIdToMealList.get(device.getCompanyId());
		int mealId = 0;
		String opMealTime = StringUtil.dateFormat(c.getTime(), "HH:mm:ss");
		if (mealList != null) {
			for (Meal m : mealList) {
				if (m.getBeginTime().compareTo(opMealTime) <= 0 && m.getEndTime().compareTo(opMealTime) >= 0) {
					mealId = m.getId();
					record.setMealName(m.getMealName());
				}
			}
		}
		record.setMealId(mealId);

		try {
			JdbcUtil.consumeRecordDAO.insert(record);
			int result = record.getResult();

			if (result == 1 || result == 2) {
				// 回复记录号
				String buf = StringUtil.getHexStrFromBytes(0, 27, b) + "000d0101" + StringUtil.strLeftPad("", 8) + StringUtil.hexLeftPad(recordNO, 4) + "000000";
				b = StringUtil.strTobytes(buf);
				try {
					TerminalManager.sendToPos(inetSocketAddress, b);
				} catch (Exception e) {
					e.printStackTrace();
				}

				// 发送门禁提醒
				if (result == 1 && isAttendance) {
					if (entranceStatus > -1) {
						Company company = (Company) JdbcUtil.companyDAO.selectByObject(device.getCompanyId());
						if (company.getAllowEntranceMessage() != null && company.getAllowEntranceMessage() == 1) {
							OpenIdsAndMobiles openIdsAndMobiles = new OpenIdsAndMobiles();
							openIdsAndMobiles.setCompanyId(device.getCompanyId());
							openIdsAndMobiles.setUserId(record.getUserId());
							openIdsAndMobiles.setMessageType(0);
							JdbcUtil.baseDAO.selectOpenIdsAndMobiles(openIdsAndMobiles);
							int messageResult = openIdsAndMobiles.getResult();
							if (messageResult == 10) {
								try {
									// 微信
									if (!StringUtils.isEmpty(openIdsAndMobiles.getOpenIds())) {
										String[] openIds = openIdsAndMobiles.getOpenIds().split(",");
										String template_id = entranceStatus == 1 ? "D5k-i177yXxbtOna8MuCq12hdEk8EYCkjto-HyBgVh4" : "YeRWyc3StmzlkttP3yMnIrGm78f1GLdzyrrEB0ezFHY";

										for (String openId : openIds) {
											SocketChannel socketChannel = SocketChannel.open(new InetSocketAddress("127.0.0.1", 10003));
											socketChannel.configureBlocking(false);
											StringBuffer sb = new StringBuffer();
											sb.append("{");
											sb.append("\"touser\":\"" + openId + "\",");
											sb.append("\"template_id\":\"" + template_id + "\",");
											sb.append("\"data\":{");
											sb.append("\"keyword1\":{\"value\":\"" + record.getUsername() + "\",\"color\":\"#173177\"},");
											sb.append("\"keyword2\":{\"value\":\"" + opTime + "\",\"color\":\"#173177\"}");
											sb.append("}");
											sb.append("}");
											String json = sb.toString();

											ByteBuffer writeBuffer = ByteBuffer.wrap(json.getBytes("UTF-8"));
											socketChannel.write(writeBuffer);
											socketChannel.close();
										}
									}
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						}
						if (company.getAllowEntranceMessage() != null && company.getAllowEntranceMessage() == 1) {
							OpenIdsAndMobiles openIdsAndMobiles = new OpenIdsAndMobiles();
							openIdsAndMobiles.setCompanyId(device.getCompanyId());
							openIdsAndMobiles.setUserId(record.getUserId());
							openIdsAndMobiles.setMessageType(1);
							JdbcUtil.baseDAO.selectOpenIdsAndMobiles(openIdsAndMobiles);
							int messageResult = openIdsAndMobiles.getResult();
							if (messageResult == 10) {
								try {
									// 短信
									if (!StringUtils.isEmpty(openIdsAndMobiles.getMobiles())) {
										String[] mobiles = openIdsAndMobiles.getMobiles().split(",");
										String status = entranceStatus == 1 ? "入" : "离";
										for (String mobile : mobiles) {
											mobile = "18538128695".equals(mobile) ? "18736035837" : mobile;
											String msg = String.format("尊敬的%s家长，您的学生已%s校。%s", record.getUsername(), status, opTime);
											StringUtil.sendMobileShortMessage(mobile, msg);
										}
									}
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						}
					}
				}
				// 发送微信在线充值提醒
				if (result == 1 && (record.getConsumeType() == 17 || record.getConsumeType() == 107)) {
					Company company = (Company) JdbcUtil.companyDAO.selectByObject(device.getCompanyId());
					if (company.getAllowWeixinSavingMessage() != null && company.getAllowWeixinSavingMessage() == 1) {
						OpenIdsAndMobiles openIdsAndMobiles = new OpenIdsAndMobiles();
						openIdsAndMobiles.setCompanyId(device.getCompanyId());
						openIdsAndMobiles.setUserId(record.getUserId());
						openIdsAndMobiles.setMessageType(0);
						openIdsAndMobiles.setWeixinCharge(1);
						openIdsAndMobiles.setVersion(record.getDiscountFare());
						JdbcUtil.baseDAO.selectOpenIdsAndMobiles(openIdsAndMobiles);
						int messageResult = openIdsAndMobiles.getResult();
						if (messageResult == 10) {
							try {
								// 微信
								if (!StringUtils.isEmpty(openIdsAndMobiles.getOpenIds())) {
									String[] openIds = openIdsAndMobiles.getOpenIds().split(",");

									for (String openId : openIds) {
										SocketChannel socketChannel2 = SocketChannel.open(new InetSocketAddress("127.0.0.1", 10003));
										socketChannel2.configureBlocking(false);
										StringBuffer sb = new StringBuffer();
										sb.append("{");
										sb.append("\"touser\":\"" + openId + "\",");
										sb.append("\"template_id\":\"gHDM5ev90TNlXdUwrfgw-5JU7bbgDh6KpJ0krtbPi9Y\",");
										sb.append("\"data\":{");
										sb.append("\"keyword1\":{\"value\":\"" + record.getUsername() + "\",\"color\":\"#173177\"},");
										sb.append("\"keyword2\":{\"value\":\"" + ((float) record.getOpFare()) / 100 + "\",\"color\":\"#173177\"},");
										sb.append("\"keyword3\":{\"value\":\"" + opTime + "\",\"color\":\"#173177\"},");
										sb.append("\"keyword4\":{\"value\":\"微信充值成功\",\"color\":\"#173177\"}");
										sb.append("}");
										sb.append("}");
										String json = sb.toString();

										ByteBuffer writeBuffer = ByteBuffer.wrap(json.getBytes("UTF-8"));
										socketChannel2.write(writeBuffer);
										socketChannel2.close();
									}
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}

					// try {
					// // 短信
					// if (!StringUtils.isEmpty(record.getMobiles())) {
					// String[] mobiles = record.getMobiles().split(",");
					// for (String mobile : mobiles) {
					// String msg =
					// String.format("尊敬的%s家长，您的学生校园卡领取微信充值%s元，充值时间%s",
					// record.getUsername(), record.getOpFare(), opTime);
					// StringUtil.sendMobileShortMessage(mobile, msg);
					// }
					// }
					// } catch (Exception e) {
					// e.printStackTrace();
					// }
				}
			}
			if (result == 1 && TerminalManager.CompanyIdToMonitorRunningList.containsKey(device.getCompanyId()) && TerminalManager.CompanyIdToMonitorRunningList.get(device.getCompanyId())) {
				// if (recordNO != device.getLastRecordNO()) {
				map.put("type", "cr");
				map.put("a", record.getDeviceName());
				map.put("b", record.getUserNO());
				map.put("c", record.getCardNO());
				map.put("d", record.getUsername());
				map.put("e", (float) record.getOddFare() / 100);
				map.put("f", (float) record.getSubsidyOddFare() / 100);
				map.put("g", (float) record.getDiscountFare() / 100);
				map.put("h", (float) (record.getOpFare() + record.getSubsidyOpFare()) / 100);
				map.put("i", record.getMealName());
				map.put("j", record.getOpTime());
				map.put("k", record.getOpCount());
				map.put("l", record.getSubsidyOpCount());
				map.put("m", record.getRecordNO());
				map.put("n", record.getConsumeTypeDes());
				map.put("o", record.getCookbookName());
				map.put("p", record.getCookbookNum());
				map.put("q", record.getRecordCount());
				map.put("s", record.getSubsidyAuth());
				// 向监控平台发送命令
				TerminalManager.sendToMonitor(map, device.getCompanyId());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

// 相差半分钟校时
// Calendar c1 = Calendar.getInstance();
// c1.set(StringUtil.objToInt("20" +
// StringUtil.getHexStrFromBytes(baseIndex + addIndex + 61, baseIndex +
// addIndex + 61, b)),
// StringUtil.objToInt(StringUtil.getHexStrFromBytes(baseIndex +
// addIndex + 62, baseIndex + addIndex + 62, b)),
// StringUtil.objToInt(StringUtil.getHexStrFromBytes(baseIndex +
// addIndex + 63, baseIndex + addIndex + 63, b)),
// StringUtil.objToInt(StringUtil.getHexStrFromBytes(baseIndex +
// addIndex + 64, baseIndex + addIndex + 64, b)),
// StringUtil.objToInt(StringUtil.getHexStrFromBytes(baseIndex +
// addIndex + 65, baseIndex + addIndex + 65, b)),
// StringUtil.objToInt(StringUtil.getHexStrFromBytes(baseIndex +
// addIndex + 66, baseIndex + addIndex + 66, b)));
// c1.add(Calendar.MONTH, -1);
// Calendar c2 = Calendar.getInstance();
// c2.setTime(new Date());
//
// if (Math.abs(c1.getTimeInMillis() - c2.getTimeInMillis()) > 30000) {
// String sendBufStr = StringUtil.hexLeftPad(PosFrame.Sys07, 2) +
// StringUtil.hexLeftPad(PosSubFrameSys07.SysTime, 2) + "0000" + "0000"
// + StringUtil.timeToHexStr() + "0000";
// String bufLen = StringUtil.hexLeftPad(2 + sendBufStr.length() / 2,
// 4);
// sendBufStr = device.getSn() +
// StringUtil.hexLeftPad(device.getDeviceNum(), 8) +
// CommandDevice.NoSubDeviceNum + DeviceType.Main +
// DeviceType.getDeviceTypeFrame(device) + bufLen
// + sendBufStr;
// byte[] sendBuf = StringUtil.strTobytes(sendBufStr);
// try {
// TerminalManager.sendToPos(inetSocketAddress, sendBuf);
// } catch (Exception e) {
// e.printStackTrace();
// }
// }
//
// int lastBatchId =
// Integer.parseInt(StringUtil.getHexStrFromBytes(baseIndex + 54,
// baseIndex + 55, b), 16);
// long lastBlackNum =
// Long.parseLong(StringUtil.getHexStrFromBytes(baseIndex + 56,
// baseIndex + 59, b), 16);
//
// // 自动下载批次黑名单
// long sysLastBatchId = 0;
// if
// (TerminalManager.CompanyIdToLastBatchIdList.containsKey(device.getCompanyId()))
// {
// sysLastBatchId =
// TerminalManager.CompanyIdToLastBatchIdList.get(device.getCompanyId());
// }
// long sysLastBlackNum = 0;
// if
// (TerminalManager.CompanyIdToLastBlackNumList.containsKey(device.getCompanyId()))
// {
// sysLastBlackNum =
// TerminalManager.CompanyIdToLastBlackNumList.get(device.getCompanyId());
// }
// if (lastBatchId != sysLastBatchId) {
// PosExecBatchBlack black = new PosExecBatchBlack(lastBatchId, device);
// black.run();
// }
//
// // 自动下载黑名单
// if (lastBlackNum != sysLastBlackNum) {
// PosExecCardBlack black = new PosExecCardBlack(lastBlackNum, device);
// black.run();
// }