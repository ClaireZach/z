package com.singbon.controller.systemManager;

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
import com.singbon.entity.SmartSchoolAdmin;
import com.singbon.entity.UserDept;
import com.singbon.service.systemManager.SmartSchoolService;
import com.singbon.service.systemManager.systemSetting.UserDeptService;
import com.singbon.util.StringUtil;

/**
 * 智慧校园控制类
 * 
 * @author 郝威
 * 
 */
@Controller
@RequestMapping(value = "/systemManager/smartSchool")
public class SmartSchoolController extends BaseController {

	@Autowired
	public SmartSchoolService smartSchoolService;
	@Autowired
	public UserDeptService userDeptService;

	/**
	 * 管理员首页
	 * 
	 * @param request
	 * @param model
	 * @param module
	 * @return
	 */
	@RequestMapping(value = "/adminIndex.do")
	public String adminIndex(HttpServletRequest request, HttpServletResponse response, Model model) {
		model.addAttribute("base", StringUtil.requestBase(request));

		return StringUtil.requestPath(request, "adminIndex");
	}
	
	/**
	 * 管理员列表
	 * 
	 * @param request
	 * @param model
	 * @param module
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/adminList.do")
	public String adminList(HttpServletRequest request, HttpServletResponse response, Model model) {
		Company company = (Company) request.getSession().getAttribute("company");
		List<SmartSchoolAdmin> list= (List<SmartSchoolAdmin>) this.smartSchoolService.selectListByCompanyId(company.getId());
		model.addAttribute("base", StringUtil.requestBase(request));
		model.addAttribute("list", list);
		return StringUtil.requestPath(request, "adminList");
	}

	/**
	 * 添加管理员
	 * 
	 * @param batch
	 * @param request
	 * @param model
	 */
	@RequestMapping(value = "/saveAdmin.do")
	public void addEditGroup(@ModelAttribute SmartSchoolAdmin smartSchoolAdmin, HttpServletRequest request, HttpServletResponse response, Model model) {
		Company company = (Company) request.getSession().getAttribute("company");
		smartSchoolAdmin.setCompanyId(company.getId());

		PrintWriter p = null;
		try {
			p = response.getWriter();
			if (smartSchoolAdmin.getId() == null) {
				this.smartSchoolService.insert(smartSchoolAdmin);
			} else {
				this.smartSchoolService.update(smartSchoolAdmin);
			}
			p.print(1);
		} catch (Exception e) {
			if(e.getMessage().contains("Duplicate")){
				p.print(2);
				return;
			}
			p.print(0);
		}
	}

	/**
	 * 删除管理员
	 * 
	 * @param batch
	 * @param request
	 * @param model
	 */
	@RequestMapping(value = "/deleteAdmin.do")
	public void addEditGroup(Integer id, HttpServletRequest request, HttpServletResponse response, Model model) {
		PrintWriter p = null;
		try {
			p = response.getWriter();
			this.smartSchoolService.delete(id);
			p.print(1);
		} catch (Exception e) {
			e.printStackTrace();
			p.print(0);
		}
	}

	/**
	 * 部门管理员首页
	 * 
	 * @param batch
	 * @param request
	 * @param model
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/adminDeptIndex.do")
	public String adminDeptIndex(HttpServletRequest request, HttpServletResponse response, Model model) {
		Company company = (Company) request.getSession().getAttribute("company");
		List<UserDept> userDeptList = (List<UserDept>) this.userDeptService.selectListByCompanyId(company.getId());
		List<SmartSchoolAdmin> amdinList = (List<SmartSchoolAdmin>) this.smartSchoolService.selectListByCompanyId(company.getId());
		model.addAttribute("base", StringUtil.requestBase(request));
		model.addAttribute("userDeptList", userDeptList);
		model.addAttribute("adminList", amdinList);
		return StringUtil.requestPath(request, "adminDeptIndex");
	}

}
