package com.singbon.controller.admin;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.singbon.controller.BaseController;
import com.singbon.device.TerminalManager;
import com.singbon.entity.Upgrade;
import com.singbon.service.UpgradeService;
import com.singbon.util.StringUtil;

/**
 * 系统升级控制类
 * 
 * @author 郝威
 * 
 */
@Controller
@RequestMapping(value = "/singbon/backgroud/system/admin/setting/upgrade")
public class UpgradeController extends BaseController {

	@Autowired
	public UpgradeService upgradeService;

	/**
	 * 首页
	 * 
	 * @param request
	 * @param model
	 * @param module
	 * @return
	 */
	@RequestMapping(value = "/index.do")
	public String index(HttpServletRequest request, HttpServletResponse response, Model model) {
		model.addAttribute("base", StringUtil.requestBase(request));
		return "admin/setting/upgrade/index";
	}

	/**
	 * 添加
	 * 
	 * @param Upgrade
	 * @param request
	 * @param model
	 */
	@RequestMapping(value = "/add.do", method = RequestMethod.POST)
	public void add(@RequestParam MultipartFile file, HttpServletRequest request, HttpServletResponse response, Model model) {
		PrintWriter p = null;
		try {
			p = response.getWriter();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (file.getSize() == 0) {
			p.print("<script>parent.show(0);</script>");
			return;
		}
		try {
			String truename = file.getOriginalFilename().replace(".hex", "");
			Date date = new Date();
			String filename = String.valueOf(date.getTime());
			long filesize = file.getSize();
			String uploadTime = StringUtil.dateFormat(date, "yyyy-MM-dd HH:mm:ss");
			File tempFile = new File(request.getSession().getServletContext().getRealPath("\\") + "/upgrade/" + filename);
			file.transferTo(tempFile);
			Upgrade upgrade = new Upgrade();
			upgrade.setTruename(truename);
			upgrade.setFilename(filename);
			upgrade.setFilesize((int) filesize);
			upgrade.setUploadTime(uploadTime);
			this.upgradeService.insert(upgrade);
			// 升级命令读取文件
			StringUtil.loadUpgradeFile(request.getSession().getServletContext().getRealPath("\\") + "/upgrade/", upgrade);
		} catch (Exception e) {
			e.printStackTrace();
		}

		p.print("<script>parent.show(1);</script>");
	}

	/**
	 * 删除
	 * 
	 * @param Upgrade
	 * @param request
	 * @param model
	 */
	@RequestMapping(value = "/delete.do", method = RequestMethod.POST)
	public void delete(Integer id, String filename, String truename, HttpServletRequest request, HttpServletResponse response, Model model) {
		PrintWriter p = null;
		try {
			p = response.getWriter();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			this.upgradeService.delete(id);
			File tempFile = new File(request.getSession().getServletContext().getRealPath("\\") + "/upgrade/" + filename);
			if (tempFile.exists()) {
				tempFile.delete();
			}
			TerminalManager.FilenameToUpgradeStrsList.remove(truename);
			TerminalManager.FilenameToUpgradeSizeList.remove(truename);
			p.print(1);
		} catch (Exception e) {
			p.print(0);
			e.printStackTrace();
		}
	}

	/**
	 * 列表
	 * 
	 * @param Upgrade
	 * @param request
	 * @param model
	 */
	@RequestMapping(value = "/list.do")
	public String list(HttpServletRequest request, HttpServletResponse response, Model model) {
		List<Upgrade> list = (List<Upgrade>) this.upgradeService.selectList();
		model.addAttribute("list", list);
		model.addAttribute("base", "/singbon/backgroud/system/admin/setting/upgrade");
		return "admin/setting/upgrade/list";
	}

}
