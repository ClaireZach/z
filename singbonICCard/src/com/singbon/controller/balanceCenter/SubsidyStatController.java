package com.singbon.controller.balanceCenter;

import java.util.ArrayList;
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
import com.singbon.entity.Company;
import com.singbon.entity.Pagination;
import com.singbon.service.CommonService;
import com.singbon.service.systemManager.DeviceService;
import com.singbon.util.ExportUtil;
import com.singbon.util.StringUtil;

/**
 * 补助统计查询控制类
 * 
 * @author 陈梦
 *
 */
@Controller
@RequestMapping(value = "/balanceCenter/subsidyStat")
public class SubsidyStatController extends BaseController {

	@Autowired
	public CommonService commonService;
	@Autowired
	public DeviceService deviceService;
	private Integer totalGainCount = 0;
	private Integer totalNoGainCount = 0;
	private double totalGainFare;
	private double totalNoGainFare;

	/**
	 * 列表
	 * 
	 * @param request
	 * @param model
	 * @param module
	 * @return
	 * @throws Exception
	 */

	@SuppressWarnings({ "rawtypes" })
	@RequestMapping(value = "/list.do")
	public String list(@ModelAttribute Pagination pagination, Integer export, Integer exportType, Integer subsidyVersion, HttpServletRequest request, HttpServletResponse response, Model model) {

		Company company = (Company) request.getSession().getAttribute("company");

		String fromSql = "(select companyId,subsidyVersion,subsidyStatus,subsidyFare,invalidDate from subsidy  UNION ALL "
				+ "select companyId,subsidyVersion,subsidyStatus,subsidyFare,invalidDate from subsidyhistory) t";
		String whereSql = "t.companyId=" + company.getId();
		if (!StringUtils.isEmpty(subsidyVersion) && subsidyVersion != -1) {
			whereSql += String.format(" and subsidyVersion = %s", subsidyVersion);
		}

		String sql = String.format(
				"select subsidyVersion,sum(case subsidyStatus when 2 then 1 else 0 end) readyGainCount," + "SUM(case subsidyStatus when 2 then subsidyFare else 0 end) readyGainFare,"
						+ "sum(case subsidyStatus when 1 then 1 else 0 end) noGainCount,"
						+ "SUM(case subsidyStatus when 1 then subsidyFare else 0 end) noGainFare,invalidDate from %s where %s group by t.subsidyVersion order by subsidyVersion limit %s,%s",
				fromSql, whereSql, pagination.getOffset(), pagination.getNumPerPage());
		String sql1 = String.format("select count(distinct subsidyVersion) as total from %s where %s", fromSql, whereSql);

		List<Map> list = this.commonService.selectListBySql(sql);
		List<String> totalList = new ArrayList<String>();
		Integer totalCount = 0;
		List<Map> total = this.commonService.selectListBySql(sql1);
		for (Map map : total) {
			totalCount = Integer.valueOf(map.get("total").toString());
		}

		for (Map map : list) {
			totalGainCount += StringUtil.objToInt(map.get("readyGainCount"));
			totalGainFare += (double) map.get("readyGainFare");
			totalNoGainCount += StringUtil.objToInt(map.get("noGainCount"));
			totalNoGainFare += (double) (map.get("noGainFare"));

		}
		totalList.add("总计");
		totalList.add(StringUtil.objToString(totalGainCount));
		totalList.add(StringUtil.objToString(totalGainFare));
		totalList.add(StringUtil.objToString(totalNoGainCount));
		totalList.add(StringUtil.objToString(totalNoGainFare));

		model.addAttribute("totalGainCount", totalGainCount);
		model.addAttribute("totalGainFare", totalGainFare);
		model.addAttribute("totalNoGainCount", totalNoGainCount);
		model.addAttribute("totalNoGainFare", totalNoGainFare);

		totalGainCount = 0;
		totalNoGainCount = 0;

		totalGainFare = 0.0f;
		totalNoGainFare = 0.0f;

		if (export != null && 1 == export) {
			String[] expColumns = { "补助版本号", "已领人数", "已领总额", "未领人数", "未领金额" };

			List<List<String>> exportList = new ArrayList<List<String>>();
			for (Map m : list) {
				List<String> list2 = new ArrayList<String>();
				list2.add(StringUtil.objToString(m.get("subsidyVersion")));
				list2.add(StringUtil.objToString(m.get("readyGainCount")));
				list2.add(StringUtil.objToString(m.get("readyGainFare")));
				list2.add(StringUtil.objToString(m.get("noGainCount")));
				list2.add(StringUtil.objToString(m.get("noGainFare")));

				exportList.add(list2);

			}
			exportList.add(totalList);
			ExportUtil.exportExcel("补助统计表" + StringUtil.getNowTime(), expColumns, exportList, response);
			return null;

		}

		model.addAttribute("subsidyVersion", subsidyVersion);
		model.addAttribute("totalCount", totalCount);
		model.addAttribute("pageNum", pagination.getPageNum());
		model.addAttribute("numPerPage", pagination.getNumPerPage());
		model.addAttribute("list", list);

		model.addAttribute("base", StringUtil.requestBase(request));
		return StringUtil.requestPath(request, "list");
	}
}
