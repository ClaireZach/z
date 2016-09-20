package com.singbon.device;

import java.net.InetSocketAddress;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.singbon.entity.Cookbook;
import com.singbon.entity.Device;
import com.singbon.util.StringUtil;

/**
 * 处理命令回复
 * 
 * @author 郝威
 * 
 */
public class PosExecReplyCommand {

	// 分解命令回复
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void execReplyCommand(Device device, Integer commandCode, byte[] b, Map map, InetSocketAddress inetSocketAddress) {
		map.put("type", "log");
		map.put("time", StringUtil.dateFormat(new Date(), "yyyy-MM-dd HH:mm:ss"));
		map.put("from", device.getDeviceName());

		byte frame = b[31];
		byte subFrame = b[32];
		SendCommand sendCommand = null;

		// 升级模式里下载升级包和返回应用程序（udp设备和BS中转通信器）
		if ((!device.getUpgradeType().contains("BS中转") || device.getUpgradeType().equals("BS中转通信器")) && frame == PosFrame.Sys04
				&& (subFrame == PosSubFrameSys04.UpgradeAppend || subFrame == PosSubFrameSys04.UpgradeEnd)) {
			Integer mode = TerminalManager.CompanyIdToMonitorModeList.get(device.getCompanyId());
			if (mode == null || mode == 0) {
				// 向监控平台发送心跳
				map.remove("type");
				TerminalManager.sendToMonitor(map, device.getCompanyId());
				return;
			}
			if (subFrame == 0x0B) {
				PosExecUpgrade upgrade = new PosExecUpgrade(device, b, map, inetSocketAddress);
				upgrade.run();
				return;
			} else {
				map.put("des", "执行升级完毕成功");
				TerminalManager.sendToMonitor(map, device.getCompanyId());
				return;
			}
		} else {
			synchronized (TerminalManager.sendCommandObject) {
				ArrayList<SendCommand> sendCommandList = TerminalManager.SNToSendCommandList.get(device.getSn());
				if (sendCommandList != null && sendCommandList.size() > 0) {
					sendCommand = sendCommandList.get(0);
					// 升级模式，进入底层和升级命令，中转设备
					if (device.getUpgradeType().contains("BS中转") && !device.getUpgradeType().equals("BS中转通信器") && frame == PosFrame.Sys04
							&& (subFrame == PosSubFrameSys04.UpgradeStart || subFrame == PosSubFrameSys04.UpgradeAppend)) {
						int index = Integer.parseInt(StringUtil.getHexStrFromBytes(b.length - 6, b.length - 3, b), 16);
						if (subFrame == PosSubFrameSys04.UpgradeStart) {
//							System.out.println(device.getDeviceName() + " begin " + index);
							sendCommand.setSubFrame(PosSubFrameSys04.UpgradeAppend);
							sendCommand.setUpgradeIndex(index);
							if (index > 0) {
								index = index / 300 + (index % 300 > 0 ? 1 : 0);
								sendCommand.setCommandCode(index);
							}
						} else {
							// if (index / 300 + index % 300 >=
							// sendCommand.getUpgradeCount()) {
							if (index == sendCommand.getUpgradeTotalSize()) {
//								System.out.println(device.getDeviceName() + " end " + index);
								sendCommand.setSubFrame(PosSubFrameSys04.UpgradeEnd);
							} else {
//								System.out.println(device.getDeviceName() + " " + index+" "+new Date().toLocaleString()+" "+System.currentTimeMillis());
								sendCommand.setUpgradeIndex(index);
								if (index > 0) {
									index = index / 300 + (index % 300 > 0 ? 1 : 0);
									sendCommand.setCommandCode(index);
								}
							}
						}
					} else if (sendCommand.getCommandCode() == commandCode) {
						sendCommandList.remove(sendCommand);
					}
				} else {
					return;
				}
			}
		}

		switch (frame) {
		// 系统07
		case PosFrame.Sys07:
			if (subFrame == PosSubFrameSys07.SysTime) {
				map.put("des", "执行校时成功");
			} else if (subFrame == PosSubFrameSys07.GrantSubsidy) {
				map.put("des", "执行补助授权成功");
			} else if (subFrame == PosSubFrameSys07.DisableSubsidy) {
				map.put("des", "执行补助回收成功");
			}
			break;
		// 系统04
		case PosFrame.Sys04:
			if (subFrame == PosSubFrameSys04.ConsumeParam) {
				map.put("des", "执行消费参数成功");
			} else if (subFrame == PosSubFrameSys04.Meal) {
				map.put("des", "执行餐别限次");
			} else if (subFrame == PosSubFrameSys04.Discount) {
				map.put("des", "执行折扣费率");
			} else if (subFrame == PosSubFrameSys04.SysPwd) {
				map.put("des", "执行下载系统密码成功");
			} else if (subFrame == PosSubFrameSys04.SetCompanyName) {
				map.put("des", "执行下载单位名称成功");
			} else if (subFrame == PosSubFrameSys04.UpgradeStart) {
				map.put("des", "执行进入升级模式成功");
			} else if (subFrame == PosSubFrameSys04.UpgradeEnd) {
				map.put("des", "执行升级完毕成功");
			} else if (subFrame == PosSubFrameSys04.UpgradeAppend) {
				List<String> upgradeList = TerminalManager.FilenameToUpgradeStrsList.get(device.getUpgradeType());
				int upgradeCount = upgradeList.size();
				int index = Integer.parseInt(StringUtil.getHexStrFromBytes(b.length - 6, b.length - 3, b), 16);
				int size = 300;
				map.put("type", "schedule");
				map.remove("time");
				map.remove("from");
				float s = (float) index / 3 / upgradeCount + (float) index % size;
				s = s > 100 ? 100 : s;
				DecimalFormat df = new DecimalFormat("0.00");
				map.put("s", df.format(s));
				// System.out.println("s " + index + " " + df.format(s));
			}
			break;
		// 菜单
		case PosFrame.Cookbook:
			if (subFrame == PosSubFrameCookbook.OrderTime1) {
				map.put("des", "执行订餐时间段1-6成功");
			} else if (subFrame == PosSubFrameCookbook.OrderTime2) {
				map.put("des", "执行订餐时间段7-12成功");
			} else if (subFrame == PosSubFrameCookbook.Update) {
				map.put("des", "执行菜肴清单更新成功");
			} else if (subFrame == PosSubFrameCookbook.Append) {
				Cookbook cookbook = sendCommand.getCookbook();
				String log = String.format("执行菜肴清单追加成功：第%s/%s个，编号：%s，单价：%s，菜名：%s", sendCommand.getCookbookIndex(), sendCommand.getCookbookTotal(), cookbook.getCookbookCode(),
						cookbook.getPrice() / 100, cookbook.getCookbookName());
				map.put("des", log);
			} else if (subFrame == PosSubFrameCookbook.Modify) {
				Cookbook cookbook = sendCommand.getCookbook();
				String log = String.format("执行修改菜肴成功：编号：%s，单价：%s，菜名：%s", cookbook.getCookbookCode(), cookbook.getPrice(), cookbook.getCookbookName());
				map.put("des", log);
			} else if (subFrame == PosSubFrameCookbook.GetLastNum) {
				int code = StringUtil.byteToInt(b[38]) * 256 + StringUtil.byteToInt(b[39]);
				ArrayList<SendCommand> tempList = new ArrayList<>();
				synchronized (TerminalManager.sendCommandObject) {
					ArrayList<SendCommand> sendCommandList = TerminalManager.SNToSendCommandList.get(device.getSn());
					if (sendCommandList != null && sendCommandList.size() > 0) {
						for (SendCommand command : sendCommandList) {
							if (command.getFrame() == PosFrame.Cookbook) {
								int code2 = command.getCookbook().getCookbookCode();
								if (code2 > code) {
									tempList.add(command);
								}
							} else {
								tempList.add(command);
							}
						}
						TerminalManager.SNToSendCommandList.put(device.getSn(), tempList);
					}
				}
				map.put("des", "执行追加菜肴清单初始化成功");
			}
			break;
		// 黑名单
		case PosFrame.Black:
			if (subFrame == PosSubFrameBlack.BatchUpdate) {
				map.put("des", "执行批次更新成功");
			} else if (subFrame == PosSubFrameBlack.BatchAppend) {
				String log = String.format("执行批次追加成功：%s", sendCommand.getBatchNames());
				map.put("des", log);
				//
				// int lastBatchId =
				// Integer.parseInt(StringUtil.getHexStrFromBytes(b.length - 4,
				// b.length - 3, b), 16);
				// TerminalManager.CompanyIdToLastBatchIdList.put(device.getCompanyId(),
				// lastBatchId);
			} else if (subFrame == PosSubFrameBlack.AllUpdate) {
				map.put("des", "执行黑名单更新成功");
			} else if (subFrame == PosSubFrameBlack.IncAppend) {
				String log = String.format("执行黑名单追加成功：%s", sendCommand.getBlackNumsDes());
				map.put("des", log);
			}
			break;
		// 初始化
		case PosFrame.SysInit:
			if (subFrame == PosSubFrameSys07.SysInit) {
				map.put("des", "执行初始化成功");
			}
			break;

		default:
			break;
		}
		if (map.size() > 3) {
			TerminalManager.sendToMonitor(map, device.getCompanyId());
		}
	}
}
