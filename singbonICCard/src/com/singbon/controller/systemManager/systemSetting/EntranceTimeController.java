package com.singbon.controller.systemManager.systemSetting;

import java.io.PrintWriter;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.singbon.controller.BaseController;
import com.singbon.entity.Company;
import com.singbon.entity.EntranceTime;
import com.singbon.service.systemManager.systemSetting.EntranceTimeService;
import com.singbon.util.StringUtil;

/**
 * 进出门时间段控制类
 * 
 * @author 郝威
 * 
 */
@Controller
@RequestMapping(value = "/systemManager/systemSetting/entranceTime")
public class EntranceTimeController extends BaseController {

	@Autowired
	public EntranceTimeService entranceTimeService;

	/**
	 * 保存修改
	 * 
	 * @param entranceTime
	 * @param request
	 * @param model
	 */
	@RequestMapping(value = "/save.do")
	public void save(@ModelAttribute EntranceTime entranceTime, HttpServletRequest request, HttpServletResponse response, Model model) {
		PrintWriter p = null;
		try {
			p = response.getWriter();
			this.entranceTimeService.update(entranceTime);
			p.print(1);
		} catch (Exception e) {
			e.printStackTrace();
			p.print(0);
		}
	}

	/**
	 * 首页
	 * 
	 * @param entranceTime
	 * @param request
	 * @param model
	 */
	@RequestMapping(value = "/index.do")
	public String index(HttpServletRequest request, HttpServletResponse response, Model model) {
		String url = request.getRequestURI();
		model.addAttribute("base", url.replace("/index.do", ""));
		return StringUtil.requestPath(request, "index");
	}

	/**
	 * 列表
	 * 
	 * @param entranceTime
	 * @param request
	 * @param model
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/list.do")
	public String list(HttpServletRequest request, HttpServletResponse response, Model model) {
		Company company = (Company) request.getSession().getAttribute("company");
		List<EntranceTime> list = (List<EntranceTime>) this.entranceTimeService.selectListByCompanyId(company.getId());
		model.addAttribute("list", list);
		return StringUtil.requestPath(request, "list");
	}

}
