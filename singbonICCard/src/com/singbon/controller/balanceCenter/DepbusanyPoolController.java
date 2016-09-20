package com.singbon.controller.balanceCenter;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
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

import com.singbon.controller.BaseController;
import com.singbon.entity.CardRecord;
import com.singbon.entity.Company;
import com.singbon.entity.Pagination;
import com.singbon.entity.SysUser;
import com.singbon.service.CommonService;
import com.singbon.service.SysUserService;
import com.singbon.service.systemManager.systemSetting.DeptService;
import com.singbon.util.StringUtil;

/**
 * 部门营业分析
 * 
 * @author zhaochunhui
 *
 */
@Controller
@RequestMapping(value = "/balanceCenter/depbusanyPool")
public class DepbusanyPoolController extends BaseController {
	@Autowired
	public CommonService commonService;
	@Autowired
	public SysUserService sysUserService;
	@Autowired
	public DeptService deptService;
	private List<SysUser> sysUserList = null;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value = "/list.do")
	public String list(@ModelAttribute Pagination pagination, Integer deptId, String includeSub, Integer export, Integer exportType, String beginDate, String endDate,
			/* String odp, */Integer dateType, Integer chartType, String chartJs, String includeSubdp, String includeOff, String deptName, HttpServletRequest request, HttpServletResponse response,
			Model model) throws IOException {
		Company company = (Company) request.getSession().getAttribute("company");
		String wheresql = "c.consumeType in (1,2,3,101,102,103) and u.companyId =" + company.getId();
		String selectsql = "dp.deptName,d.deptId,d.deviceName,SUM(c.discountFare) discountFare,SUM(c.opFare) opFare,SUM(c.subsidyOpFare) subsidyOpFare,c.opTime,c.collectTime";
		String fromsql = "consumeRecord c LEFT JOIN USER u ON c.userId = u.userId LEFT JOIN device d ON c.deviceId = d.id LEFT JOIN dept dp ON d.deptId = dp.id";
		chartJs = "js/jsm/echart3.js";// js/jsm/echart3pie.js
		if (!StringUtils.isEmpty(chartType) && chartType != -1) {
			if (chartType == 0) {
				chartJs = "js/jsm/echart3bar.js";
			}
			if (chartType == 1) {
				chartJs = "js/jsm/echart3pie.js";
			}
		}
		/*
		 * if (!StringUtils.isEmpty(deptId) && deptId != -1) { if
		 * (StringUtils.isEmpty(includeSub)) { wheresql += String.format(
		 * " and find_in_set(dp.id,getSubIds(%s,1))>0", deptId); } else {
		 * wheresql += String.format(" and dp.id = %s", deptId); } }
		 */
		if (!StringUtils.isEmpty(deptId) && deptId != -1) {
			if (StringUtils.isEmpty(includeSub)) {
				wheresql += String.format(" and find_in_set(dp.id,getSubIds(%s,1))>0", deptId);
			} else {
				wheresql += String.format(" and dp.id = %s", deptId);
			}
		}
		String sql4 = String.format("select %s from %s where %s group by d.deptId order by d.deptId", selectsql, fromsql, wheresql);
		List<Map> list6 = commonService.selectListBySql(sql4);
		Collection threedp = new ArrayList<>();
		Collection threefare = new ArrayList<>();
		if (StringUtils.isEmpty(includeSub)) {
			double tempopfare = 0.0;
			int dplevel = 0;
			ArrayList<String> zchlevel1 = new ArrayList<>();
			zchlevel1.add("0");
			List<Map> dplist = new ArrayList<>();
			boolean flag = true;
			while (flag) {
				String zchsql2 = String.format("select dp.deptName,dp.id,dp.parentId from dept dp where dp.parentId in (%s) and dp.companyId = %s",
						zchlevel1.toString().replace("[", "").replace("]", ""), company.getId());
				List<Map> zchlist2 = commonService.selectListBySql(zchsql2);
				for (int i = 0; i < zchlist2.size(); i++) {
					for (int j = 0; j < list6.size(); j++) {
						if (list6.get(j).get("deptId") != (null) && zchlist2.get(i).get("id") != (null) && list6.get(j).get("deptId").equals(zchlist2.get(i).get("id"))) {
							// if(Float.valueOf(list6.get(j).get("deptId").toString())==Float.valueOf(zchlist2.get(i).get("id").toString())){
							zchlist2.get(i).put("opFare", (Float.valueOf(list6.get(j).get("opFare").toString()) + Float.valueOf(list6.get(j).get("subsidyOpFare").toString())) / 100);
						}
						if (zchlist2.get(i).size() < 4) {
							zchlist2.get(i).put("opFare", tempopfare);
						}
					}
				}
				if (zchlist2.isEmpty()) {
					flag = false;
				} else {
					/*
					 * System.out.println(zchlist2);
					 * System.out.println(++dplevel);
					 */
					++dplevel;
					zchlevel1.clear();
					for (int i = 0; i < zchlist2.size(); i++) {
						zchlevel1.add(StringUtil.objToString(zchlist2.get(i).get("id")));
						zchlist2.get(i).put("dpidlevel", dplevel);
						dplist.add(zchlist2.get(i));
					}
				}
			}

			/* if (dplist.get(0) != null && dplist.get(0).size()>4) { */
			if (!dplist.isEmpty()) {
				if (dplist.get(0).size() > 4) {
					int dplistsize = StringUtil.objToInt((dplist.get(dplist.size() - 1).get("dpidlevel")));
					int dplistsizefor = dplistsize;
					List<Map> pl1 = new ArrayList<>();
					List<Map> pl2 = new ArrayList<>();
					for (int j = dplistsizefor; j > 0; j--) {
						for (int i = 0; i < dplist.size(); i++) {
							if (StringUtil.objToInt(dplist.get(i).get("dpidlevel")) == dplistsize) {
								pl1.add(dplist.get(i));
							} else if (StringUtil.objToInt(dplist.get(i).get("dpidlevel")) == (dplistsize - 1)) {
								pl2.add(dplist.get(i));
							}
						}
						/* System.out.println("pl2  :  " + pl2); */
						for (int a = 0; a < pl2.size(); a++) {
							for (int b = 0; b < pl1.size(); b++) {
								if (pl2.get(a).get("id") == pl1.get(b).get("parentId")) {

									BigDecimal b2 = new BigDecimal(Float.valueOf(pl2.get(a).get("opFare").toString()) + Float.valueOf(pl1.get(b).get("opFare").toString()));
									pl2.get(a).put("opFare", b2.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
									/*
									 * pl2.get(a).put("opFare",
									 * Float.valueOf(pl2.get(a).get("opFare").
									 * toString())+Float.valueOf(pl1.get(b).get(
									 * "opFare").toString()));
									 */
								}
							}
							if (dplist.get(a).get("id") == pl2.get(a).get("id")) {
								dplist.get(a).put("opFare", pl2.get(a).get("opFare"));
							}

						}
						/* System.out.println("pl2  :  " + pl2); */
						pl1.clear();
						pl2.clear();
						dplistsize--;

					}
				}

				/*
				 * for(int i=0;i<dplist.size();i++){
				 * threedp.add(dplist.get(i).get("deptName"));
				 * threefare.add(dplist.get(i).get("opFare")); }
				 */
				if (StringUtils.isEmpty(includeSubdp)) {
					for (int i = 0; i < dplist.size(); i++) {
						if (StringUtil.objToInt(dplist.get(i).get("parentId")) == 0) {
							threedp.add(dplist.get(i).get("deptName"));
							threefare.add(dplist.get(i).get("opFare"));
						}

					}
				} else {
					for (int i = 0; i < dplist.size(); i++) {
						threedp.add(dplist.get(i).get("deptName"));
						threefare.add(dplist.get(i).get("opFare"));
					}
				}
			}
		} else {// 当包含下级部门未选中 显示 单个部门的营业额
			double tempopfare = 0.0;
			int dplevel = 0;
			ArrayList<String> zchlevel1 = new ArrayList<>();
			zchlevel1.add("0");
			List<Map> dplist = new ArrayList<>();
			boolean flag = true;
			while (flag) {
				String zchsql2 = String.format("select dp.deptName,dp.id,dp.parentId from dept dp where dp.parentId in (%s) and dp.companyId = %s",
						zchlevel1.toString().replace("[", "").replace("]", ""), company.getId());
				List<Map> zchlist2 = commonService.selectListBySql(zchsql2);
				for (int i = 0; i < zchlist2.size(); i++) {
					for (int j = 0; j < list6.size(); j++) {
						/*
						 * if(list6.get(j).get("deptId").equals(zchlist2.get(i).
						 * get("id"))){
						 */
						if (list6.get(j).get("deptId") != (null) && zchlist2.get(i).get("id") != (null) && list6.get(j).get("deptId").equals(zchlist2.get(i).get("id"))) {
							zchlist2.get(i).put("opFare", (Float.valueOf(list6.get(j).get("opFare").toString()) + Float.valueOf(list6.get(j).get("subsidyOpFare").toString())) / 100);
						}
						if (zchlist2.get(i).size() < 4) {
							zchlist2.get(i).put("opFare", tempopfare);
						}
					}
				}
				if (zchlist2.isEmpty()) {
					flag = false;
				} else {
					/*
					 * System.out.println(zchlist2);
					 * System.out.println(++dplevel);
					 */
					++dplevel;
					zchlevel1.clear();
					for (int i = 0; i < zchlist2.size(); i++) {
						zchlevel1.add(StringUtil.objToString(zchlist2.get(i).get("id")));
						zchlist2.get(i).put("dpidlevel", dplevel);
						dplist.add(zchlist2.get(i));
					}
				}

			}
			/*
			 * for(int i=0;i<dplist.size();i++){
			 * threedp.add(dplist.get(i).get("deptName"));
			 * threefare.add(dplist.get(i).get("opFare")); }
			 */
			if (StringUtils.isEmpty(includeSubdp)) {
				for (int i = 0; i < dplist.size(); i++) {
					if (StringUtil.objToInt(dplist.get(i).get("parentId")) == 0) {
						threedp.add(dplist.get(i).get("deptName"));
						threefare.add(dplist.get(i).get("opFare"));
					}

				}
			} else {
				for (int i = 0; i < dplist.size(); i++) {
					threedp.add(dplist.get(i).get("deptName"));
					threefare.add(dplist.get(i).get("opFare"));
				}
			}
		}
		/******************************** 图形宽度 ***************************************************/
		double dataWidth = 0;
		double barGap = (threedp.size() + 1) * 30;// 柱体间隔宽度
		for (Iterator it = threedp.iterator(); it.hasNext();) {
			Object o = it.next();
			dataWidth += o.toString().length();
		}
		dataWidth = (dataWidth * 15 + barGap) / 0.75;
		// System.out.println(dataWidth);
		/*****************************************************************************************************/
		model.addAttribute("twodp", threedp);
		model.addAttribute("twofare", threefare);
		model.addAttribute("chartType", chartType);
		model.addAttribute("dataWidth", dataWidth);
		model.addAttribute("chartJs", chartJs);
		model.addAttribute("dateType", dateType);
		model.addAttribute("deptId", deptId);
		model.addAttribute("includeSub", includeSub);
		model.addAttribute("includeSubdp", includeSubdp);
		model.addAttribute("deptName", deptName);
		model.addAttribute("beginDate", beginDate);
		model.addAttribute("endDate", endDate);
		model.addAttribute("includeOff", includeOff);
		model.addAttribute("sysUserList", sysUserList);
		model.addAttribute("recordTypes", CardRecord.recordTypes);
		model.addAttribute("base", StringUtil.requestBase(request));
		return StringUtil.requestPath(request, "list");
	}

}