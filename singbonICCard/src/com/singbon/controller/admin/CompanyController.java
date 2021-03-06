package com.singbon.controller.admin;

import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.singbon.entity.Company;
import com.singbon.entity.Pagination;
import com.singbon.service.CommonService;
import com.singbon.service.CompanyService;
import com.singbon.util.StringUtil;

/**
 * 后台单位管理控制类
 * 
 * @author 郝威
 * 
 */
@Controller
@RequestMapping(value = "/singbon/backgroud/system/admin/setting/company")
public class CompanyController {

	@Autowired
	public CompanyService companyService;
	@Autowired
	public CommonService commonService;

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
		return "admin/setting/company/index";
	}

	/**
	 * 添加修改
	 * 
	 * @param batch
	 * @param request
	 * @param model
	 */
	@RequestMapping(value = "/addEdit.do")
	public void addEdit(@ModelAttribute Company company, String status, HttpServletRequest request, HttpServletResponse response, Model model) {
		PrintWriter p = null;
		try {
			p = response.getWriter();
			int count = this.companyService.selectCountByInfo(company);
			if (count > 0) {
				p.print(2);
				return;
			}
			if ("on".equals(status)) {
				company.setEnable(true);
			} else {
				company.setEnable(false);
			}
			if (company.getId() == null) {
				this.companyService.insert(company);
			} else {
				this.companyService.updateAdmin(company);
			}
			p.print(1);
		} catch (Exception e) {
			e.printStackTrace();
			p.print(0);
		}
	}

	/**
	 * 删除
	 * 
	 * @param dept
	 * @param request
	 * @param model
	 */
	@RequestMapping(value = "/delete.do")
	public void delete(Integer id, HttpServletRequest request, HttpServletResponse response, Model model) {
		PrintWriter p = null;
		try {
			p = response.getWriter();
			this.companyService.delete(id);
			p.print(1);
		} catch (Exception e) {
			e.printStackTrace();
			p.print(0);
		}
	}

	/**
	 * 获取单位列表
	 * 
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/list.do")
	public String list(@ModelAttribute Pagination pagination, String nameStr, String includeAll, HttpServletRequest request, HttpServletResponse response, Model model) {
		String[] columns = { "id", "companyName", "serialNumber", "authNumber", "baseSection", "invalidDate", "appId", "mchId", "mchKey", "weixinLimit", "remainingWeixin", "shortLimit",
				"remainingShort", "allowEntranceMessage", "allowPCSavingMessage", "allowWeixinSavingMessage", "enable" };
		String fromSql = "company";
		String whereSql = "1=1 ";
		if (!StringUtils.isEmpty(nameStr)) {
			whereSql += String.format(" and companyName like '%%%s%%'", nameStr);
		}
		if (!"on".equals(includeAll)) {
			whereSql += " and enable=1";
		}
		List<Map> list = this.commonService.selectByPage(columns, null, fromSql, whereSql, pagination);
		int totalCount = Integer.valueOf(list.get(0).get("id").toString());
		list.remove(0);

		model.addAttribute("list", list);
		model.addAttribute("pageNum", pagination.getPageNum());
		model.addAttribute("numPerPage", pagination.getNumPerPage());
		model.addAttribute("totalCount", totalCount);
		model.addAttribute("nameStr", nameStr);
		model.addAttribute("includeAll", includeAll);
		model.addAttribute("base", "/singbon/backgroud/system/admin/setting/company");
		return "admin/setting/company/list";
	}

}
