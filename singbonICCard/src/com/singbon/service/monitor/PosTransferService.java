package com.singbon.service.monitor;

import java.net.InetSocketAddress;
import java.util.List;

import org.springframework.stereotype.Service;

import com.singbon.device.CommandDevice;
import com.singbon.device.DeviceType;
import com.singbon.device.PosFrame;
import com.singbon.device.PosSubFrameBlack;
import com.singbon.device.PosSubFrameSys04;
import com.singbon.device.SendCommand;
import com.singbon.device.TerminalManager;
import com.singbon.entity.Device;
import com.singbon.util.StringUtil;

/**
 * 串口主动采集业务层
 * 
 * @author 郝威
 * 
 */
@Service
public class PosTransferService implements Runnable {

	private Integer mode;
	private Integer accessTimeout;
	private Integer transferInterval;
	private Integer transferUpgradeInterval;
	private Integer blackInterval;
	private List<Device> deviceList;

	public void setMode(Integer mode) {
		this.mode = mode;
	}

	public void setAccessTimeout(Integer accessTimeout) {
		this.accessTimeout = accessTimeout;
	}

	public void setTransferInterval(Integer transferInterval) {
		this.transferInterval = transferInterval;
	}

	public void setTransferUpgradeInterval(Integer transferUpgradeInterval) {
		this.transferUpgradeInterval = transferUpgradeInterval;
	}

	public void setBlackInterval(Integer blackInterval) {
		this.blackInterval = blackInterval;
	}

	public void setDeviceList(List<Device> deviceList) {
		this.deviceList = deviceList;
	}

	public void run() {
		boolean stop = false;
		while (!stop) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e1) {
				stop = true;
			}
			for (Device d : this.deviceList) {
				InetSocketAddress inetSocketAddress = TerminalManager.SNToInetSocketAddressList.get(d.getTransferSn());
				if (inetSocketAddress == null) {
					continue;
				}

				// 当前设备命令列表
				List<SendCommand> sendCommandList = null;
				// 当前命令
				SendCommand sendCommand = null;
				int commandCode = 0;
				// 访问次数
				int sendTime = 0;

				// 同步执行当前命令
				synchronized (TerminalManager.sendCommandObject) {
					sendCommandList = TerminalManager.SNToSendCommandList.get(d.getSn());
					if (sendCommandList != null && sendCommandList.size() > 0) {
						// StringUtil.println("命令个数：" + sendCommandList.size());
						sendCommand = sendCommandList.get(0);
						commandCode = sendCommand.getCommandCode();
						sendTime = sendCommand.getSendTime();
						if (this.mode == 0 && sendTime >= 5) {
							sendCommandList.clear();
							continue;
						}

						// 非升级模式或者是发进入底层和返回应用程序命令
						if (this.mode == 0 || (sendCommand.getFrame() == PosFrame.Sys04 && sendCommand.getSubFrame() != PosSubFrameSys04.UpgradeAppend)) {
							try {
								MonitorService.excuteCommand(d, inetSocketAddress, sendCommand);
								sendCommand.setSendTime(++sendTime);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
				}

				// 升级模式
				if (this.mode == 1 && (sendCommandList == null || sendCommandList.size() == 0)) {
					continue;
				}

				// 升级模式，发进升级命令一次5包
				if (this.mode == 1 && sendCommand.getFrame() == PosFrame.Sys04 && sendCommand.getSubFrame() == PosSubFrameSys04.UpgradeAppend) {
					for (int i = 0; i < 5; i++) {
						// 同步执行当前命令
						synchronized (TerminalManager.sendCommandObject) {
							sendCommandList = TerminalManager.SNToSendCommandList.get(d.getSn());
							sendCommand = sendCommandList.get(0);
							commandCode = sendCommand.getCommandCode();

							try {
								MonitorService.excuteCommand(d, inetSocketAddress, sendCommand);
								sendCommand.setUpgradeIndex(sendCommand.getUpgradeIndex() + 300);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
						try {
							Thread.sleep(transferUpgradeInterval);
						} catch (InterruptedException e) {
							stop = true;
							break;
						}
					}
					try {
						MonitorService.upgradeSync(d, inetSocketAddress, 0);
					} catch (Exception e) {
						e.printStackTrace();
					}

					if (stop == true)
						break;
				}

				// 非升级模式
				if (this.mode == 0) {
					// 如果没有命令，发送采集，间隔1s+终端访问次数*200ms
					if (sendCommandList == null || sendCommandList.size() == 0) {
						String sendBufStr = "0101" + "0000" + "00000000";
						String bufLen = StringUtil.hexLeftPad(2 + sendBufStr.length() / 2, 4);
						sendBufStr = d.getSn() + StringUtil.hexLeftPad(d.getDeviceNum(), 8) + CommandDevice.NoSubDeviceNum + DeviceType.Main + DeviceType.getDeviceTypeFrame(d) + bufLen + sendBufStr;
						byte[] sendBuf = StringUtil.strTobytes(sendBufStr);
						try {
							TerminalManager.sendToPos(inetSocketAddress, sendBuf);
						} catch (Exception e) {
							e.printStackTrace();
						}
						try {
							Thread.sleep(1000);
							for (int i = 0; i < accessTimeout; i++) {
								Thread.sleep(200);
							}
						} catch (InterruptedException e) {
							// e.printStackTrace();
							stop = true;
							break;
						}
						continue;
					}

					// 如果下载黑名单不检测是否命令回复，继续询问下个设备
					if (sendCommand != null && sendCommand.getFrame() == PosFrame.Black && sendCommand.getSubFrame() == PosSubFrameBlack.IncAppend) {
						try {
							if (sendTime <= 1) {
								Thread.sleep(100);
							} else {
								Thread.sleep(blackInterval);
							}
						} catch (InterruptedException e) {
//							e.printStackTrace();
							stop = true;
							break;
						}
						continue;
					}
				}
				// end 非升级模式

				// 检测命令是否执行，比较命令列表里的第0命令和当前执行的命令是否一致
				try {
					// 等待消除网络延时
					if (this.mode == 1 && sendCommand.getSubFrame() == PosSubFrameSys04.UpgradeAppend && sendCommand.getCommandCode() == 0) {
						Thread.sleep(5000);
					} else {
						Thread.sleep(1000);
					}

					for (int i = 0; i < accessTimeout; i++) {
						synchronized (TerminalManager.sendCommandObject) {
							List<SendCommand> sendCommandList2 = TerminalManager.SNToSendCommandList.get(d.getSn());
							if (sendCommandList2.size() > 0) {
								SendCommand tempSendCommand = sendCommandList2.get(0);
								if (sendCommand != tempSendCommand || commandCode != tempSendCommand.getCommandCode()) {
									break;
								}
							} else {
								// 列表为空说明执行完毕
								break;
							}
						}
						Thread.sleep(200);
					}
					Thread.sleep(transferInterval);
				} catch (InterruptedException e) {
					e.printStackTrace();
					stop = true;
					break;
				}
			}
		}
	}
}
