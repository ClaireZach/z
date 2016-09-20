package com.singbon.controller.cardManager;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.channels.SocketChannel;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.singbon.controller.BaseController;
import com.singbon.device.TerminalManager;
import com.singbon.entity.Company;
import com.singbon.entity.Device;
import com.singbon.entity.SysUser;
import com.singbon.service.mainCard.CardReaderService;
import com.singbon.service.systemManager.DeviceService;
import com.singbon.util.StringUtil;

/**
 * 读卡机参数下载控制类
 * 
 * @author 郝威
 * 
 */
@Controller
@RequestMapping(value = "/cardManager/cardReader")
public class CardReaderController extends BaseController {

	@Autowired
	public CardReaderService cardReaderService;
	@Autowired
	public DeviceService deviceService;

	/**
	 * 参数下载
	 * 
	 * @param request
	 * @param model
	 * @param module
	 * @return
	 */
	@RequestMapping(value = "/param.do")
	public String param(HttpServletRequest request, HttpServletResponse response, Model model) {
		SysUser sysUser = (SysUser) request.getSession().getAttribute("sysUser");
		Company company = (Company) request.getSession().getAttribute("company");
		Device device = (Device) request.getSession().getAttribute("device");
		model.addAttribute("sysUser", sysUser);
		model.addAttribute("company", company);
		model.addAttribute("device", device);

		if (device != null) {
			model.addAttribute("sn", device.getSn());
			// 读卡机状态
			if (TerminalManager.SNToSocketChannelList.containsKey(device.getSn())) {
				model.addAttribute("cardStatus", 1);
			} else {
				model.addAttribute("cardStatus", 0);
			}
		} else {
			model.addAttribute("cardStatus", 0);
		}
		model.addAttribute("base", StringUtil.requestBase(request));
		return StringUtil.requestPath(request);
	}

	/**
	 * 设备升级
	 * 
	 * @param request
	 * @param model
	 * @param module
	 * @return
	 */
	@RequestMapping(value = "/upgrade.do")
	public String upgrade(HttpServletRequest request, HttpServletResponse response, Model model) {
		Company company = (Company) request.getSession().getAttribute("company");
		model.addAttribute("company", company);
		List<Device> list = this.deviceService.selectDeviceListByCompanyId(company.getId(), new String[] { "8" }, 0);
		for (Device device : list) {
			if (TerminalManager.SNToSocketChannelList.containsKey(device.getSn())) {
				device.setIsOnline(1);
			} else {
				device.setIsOnline(0);
			}
		}
		model.addAttribute("base", StringUtil.requestBase(request));
		model.addAttribute("list", list);
		return StringUtil.requestPath(request);
	}

	/**
	 * 命令处理
	 * 
	 * @param model
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = "/command.do", method = RequestMethod.POST)
	public void command(String comm, String opSn, HttpServletRequest request, HttpServletResponse response, Model model) {
		Company company = (Company) request.getSession().getAttribute("company");
		Device device = (Device) request.getSession().getAttribute("device");
		String sn = device.getSn();

		// 下载单位名称
		if ("name".equals(comm)) {
			SocketChannel socketChannel = TerminalManager.SNToSocketChannelList.get(sn);
			if (socketChannel != null) {
				try {
					cardReaderService.downloadName(company, socketChannel, device);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		// 下载读卡机密码
		else if ("pwd".equals(comm)) {
			SocketChannel socketChannel = TerminalManager.SNToSocketChannelList.get(sn);
			if (socketChannel != null) {
				try {
					cardReaderService.sysPwd(company, socketChannel, device);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		// 下载系统时间
		else if ("sysTime".equals(comm)) {
			SocketChannel socketChannel = TerminalManager.SNToSocketChannelList.get(sn);
			if (socketChannel != null) {
				try {
					cardReaderService.sysTime(company, socketChannel, device);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		// 进入底层
		else if ("upgrade".equals(comm)) {
			PrintWriter p = null;
			try {
				p = response.getWriter();
			} catch (IOException e) {
				e.printStackTrace();
			}
			SocketChannel socketChannel = TerminalManager.SNToSocketChannelList.get(opSn);
			if (socketChannel != null) {
				if (!TerminalManager.FilenameToUpgradeStrsList.containsKey(device.getUpgradeType())) {
					p.print(2);
					return;
				}
				try {
					device = TerminalManager.SNToDeviceList.get(opSn);
					cardReaderService.upgrade(company, socketChannel, device);
					p.print(1);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
}
