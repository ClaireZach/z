package com.singbon.controller.monitor;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.singbon.controller.BaseController;
import com.singbon.device.SendCommand;
import com.singbon.device.TerminalManager;
import com.singbon.entity.Company;
import com.singbon.entity.Dept;
import com.singbon.entity.Device;
import com.singbon.entity.SysUser;
import com.singbon.service.UpgradeService;
import com.singbon.service.monitor.MonitorService;
import com.singbon.service.monitor.PosTransferService;
import com.singbon.service.monitor.PosUdpService;
import com.singbon.service.systemManager.DeviceService;
import com.singbon.service.systemManager.systemSetting.DeptService;
import com.singbon.util.StringUtil;

/**
 * 监控平台控制类
 * 
 * @author 郝威
 * 
 */
@Controller
@RequestMapping(value = "/monitor/")
// @Scope("prototype")
public class MonitorController extends BaseController {

	@Autowired
	public DeviceService deviceService;
	@Autowired
	public MonitorService monitorService;
	@Autowired
	public DeptService deptService;
	@Autowired
	public UpgradeService upgradeService;

	// 所有设备列表
	List<Device> deviceList = null;
	// 主动udp设备列表
	List<Device> posUdpDeviceList = new ArrayList<>();
	// 中转设备列表
	List<List<Device>> posTransferDeviceList = new ArrayList<>();

	/**
	 * 首页
	 * 
	 * @param request
	 * @param model
	 * @param module
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/index.do")
	public String index(HttpServletRequest request, HttpServletResponse response, Model model) {
		SysUser sysUser = (SysUser) request.getSession().getAttribute("sysUser");
		Company company = (Company) request.getSession().getAttribute("company");
		model.addAttribute("sysUser", sysUser);
		model.addAttribute("company", company);

		List<Dept> deptList = (List<Dept>) this.deptService.selectListByCompanyId(company.getId());
		// 所有设备
		deviceList = this.deviceService.selectDeviceListByCompanyId(company.getId(), new String[] { "1", "2", "3" }, 1);
		for (Device d : deviceList) {
			if (TerminalManager.SNToInetSocketAddressList.containsKey(d.getTransferSn())) {
				d.setIsOnline(1);
			} else {
				d.setIsOnline(0);
			}
		}

		// 关闭老线程
		closeOldThread(company);
		TerminalManager.CompanyIdToMonitorThreadList.remove(company.getId());

		posUdpDeviceList.clear();
		posTransferDeviceList.clear();

		// 中转通信器列表
		List<Device> transferList = new ArrayList<>();
		for (Device d : deviceList) {
			Integer transferId = d.getTransferId();
			if (transferId == null || transferId == 0) {
				posUdpDeviceList.add(d);
			}
			if (d.getDeviceType() == 1) {
				transferList.add(d);
			}
		}
		for (Device d : transferList) {
			List<Device> list = new ArrayList<>();
			list.add(d);
			for (Device t : deviceList) {
				if (d.getId().equals(t.getTransferId())) {
					list.add(t);
				}
			}
			posTransferDeviceList.add(list);
		}

		Integer mode = TerminalManager.CompanyIdToMonitorModeList.get(company.getId());
		if (mode == null)
			mode = 0;
		startNewThread(company, mode);

		request.getSession().setAttribute("companyId", company.getId().toString());

		model.addAttribute("mode", mode);
		model.addAttribute("deptList", deptList);
		model.addAttribute("posUdpDeviceList", posUdpDeviceList);
		model.addAttribute("posTransferDeviceList", posTransferDeviceList);

		String url = request.getRequestURI();
		model.addAttribute("base", url.replace("/index.do", ""));
		return url.replace(".do", "");
	}

	/**
	 * js页面
	 * 
	 * @param request
	 * @param model
	 * @param module
	 * @return
	 */
	@RequestMapping(value = "/js.do")
	public String js(HttpServletRequest request, HttpServletResponse response, Model model) {
		model.addAttribute("base", StringUtil.requestBase(request));
		return StringUtil.requestPath(request, "js");
	}

	/**
	 * 切换模式
	 * 
	 * @param request
	 * @param model
	 * @param module
	 * @return
	 */
	@RequestMapping(value = "/mode.do", method = RequestMethod.POST)
	public void mode(Integer mode, HttpServletRequest request, HttpServletResponse response, Model model) {
		Company company = (Company) request.getSession().getAttribute("company");
		closeOldThread(company);
		for (Device d : deviceList) {
			ArrayList<SendCommand> sendCommandList = TerminalManager.SNToSendCommandList.get(d.getSn());
			if (sendCommandList != null && sendCommandList.size() > 0) {
				sendCommandList.clear();
			}
		}
		TerminalManager.CompanyIdToMonitorModeList.put(company.getId(), mode);
		startNewThread(company, mode);
	}

	private void startNewThread(Company company, Integer mode) {

		List<Thread> threadList = new ArrayList<>();
		if (posUdpDeviceList.size() > 0) {
			// 启动线程
			PosUdpService service = new PosUdpService();
			// service.setMode(mode);
			service.setAccessTimeout(company.getAccessTimeout());
			service.setBlackInterval(company.getBlackInterval());
			service.setDeviceList(posUdpDeviceList);
			Thread posUdpThread = new Thread(service);
			posUdpThread.setName("Co-udp" + company.getId());
			threadList.add(posUdpThread);
			posUdpThread.start();
		}

		if (posTransferDeviceList.size() > 0) {
			for (int i = 0; i < posTransferDeviceList.size(); i++) {
				List<Device> list = posTransferDeviceList.get(i);
				if (list.size() > 1) {
					list = list.subList(1, list.size());
					// 启动线程
					PosTransferService service = new PosTransferService();
					service.setMode(mode);
					service.setAccessTimeout(company.getAccessTimeout());
					service.setBlackInterval(company.getBlackInterval());
					service.setTransferInterval(company.getTransferInterval());
					service.setTransferUpgradeInterval(company.getTransferUpgradeInterval());
					service.setDeviceList(list);
					Thread posTransferThread = new Thread(service);
					posTransferThread.setName("Co-transfer" + i);
					threadList.add(posTransferThread);
					posTransferThread.start();
				}
			}
		}

		// 加入线程列表
		TerminalManager.CompanyIdToMonitorThreadList.put(company.getId(), threadList);
		TerminalManager.CompanyIdToMonitorRunningList.put(company.getId(), true);
	}

	private void closeOldThread(Company company) {
		TerminalManager.CompanyIdToMonitorRunningList.put(company.getId(), false);
		List<Thread> threadList = TerminalManager.CompanyIdToMonitorThreadList.get(company.getId());
		if (threadList != null) {
			for (Thread thread : threadList) {
				if (thread != null && thread.isAlive()) {
					thread.interrupt();
				}
			}
		}
	}

	/**
	 * 关闭指定消费机连接
	 * 
	 * @param request
	 * @param model
	 * @param module
	 * @return
	 */
	@RequestMapping(value = "/removeInetSocketAddress.do", method = RequestMethod.POST)
	public void removeInetSocketAddress(String sn, HttpServletRequest request, HttpServletResponse response, Model model) {
		TerminalManager.SNToInetSocketAddressList.remove(sn);
	}

	/**
	 * 命令
	 * 
	 * @param request
	 * @param model
	 * @param module
	 * @return
	 */
	@RequestMapping(value = "/command.do", method = RequestMethod.POST)
	public void command(String cmd, String sn, Integer deptId, Integer cookbookCode, HttpServletRequest request, HttpServletResponse response, Model model) {
		Company company = (Company) request.getSession().getAttribute("company");

		List<String> snList = new ArrayList<>();
		// 对单个设备
		if (!StringUtils.isEmpty(sn)) {
			snList.add(sn);
			// 对整个营业部门
		} else if (deptId != null) {
			List<Device> deviceList = this.deviceService.selectPosListByDeptId(deptId, 1);
			for (Device d : deviceList) {
				String sn2 = d.getSn();
				if (d.getTransferSn() != null) {
					sn2 = d.getTransferSn();
				}
				if (TerminalManager.SNToInetSocketAddressList.containsKey(sn2)) {
					snList.add(d.getSn());
				}
			}
		}

		if (snList.size() == 0)
			return;
		for (String sn2 : snList) {
			Device device = TerminalManager.SNToDeviceList.get(sn2);
			// 水控不下载菜单
			if (device.getDeviceType() == 3 && cmd.toLowerCase().indexOf("cookbook") != -1)
				return;

			int commandIndex = 1;
			synchronized (TerminalManager.sendCommandObject) {
				ArrayList<SendCommand> sendCommandList = TerminalManager.SNToSendCommandList.get(sn2);
				if (sendCommandList == null) {
					sendCommandList = new ArrayList<>();
					TerminalManager.SNToSendCommandList.put(sn2, sendCommandList);
				} else {
					int size = sendCommandList.size();
					if (size > 0) {
						SendCommand sendCommand = sendCommandList.get(size - 1);
						commandIndex = sendCommand.getCommandCode() + 1;
					}
				}
				this.monitorService.addCommand(company, device, cmd, cookbookCode, commandIndex, sendCommandList);
			}
		}
	}
}
