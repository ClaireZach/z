package com.singbon.controller.balanceCenter;

import java.math.BigDecimal;
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
import com.singbon.device.ConsumeType;
import com.singbon.entity.Company;
import com.singbon.entity.Device;
import com.singbon.entity.Pagination;
import com.singbon.service.CommonService;
import com.singbon.service.systemManager.DeviceService;
import com.singbon.util.ExportUtil;
import com.singbon.util.StringUtil;

/**
 * 消费记录查询控制类
 * 
 * @author 郝威
 * 
 */
@Controller
@RequestMapping(value = "/balanceCenter/consumeRecord")
public class ConsumeRecordController extends BaseController {

	@Autowired
	public CommonService commonService;
	@Autowired
	public DeviceService deviceService;

	/**
	 * 列表
	 * 
	 * @param request
	 * @param model
	 * @param module
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value = "/list.do")
	public String list(@ModelAttribute Pagination pagination, Integer export, Integer exportType, String nameStr, Integer deptId, String deptName, Integer cardTypeId, Integer deviceId,
			String deviceName, Integer consumeType, Integer dateType, String beginDate, String endDate, String includeOff, HttpServletRequest request, HttpServletResponse response, Model model) {
		Company company = (Company) request.getSession().getAttribute("company");

		if (exportType != null && 1 == exportType) {
			pagination.setPageNum(1);
			pagination.setNumPerPage(pagination.getTotalCount());
		}
		String[] columns = { "d.deviceName", "u.userNO", "c.cardNO", "c.cardSN", "u.username", "c.oddFare", "c.subsidyOddFare", "c.discountFare", "c.opFare", "c.subsidyOpFare", "m.mealName",
				"c.opTime", "c.opCount", "c.subsidyOpCount", "c.recordNO", "c.consumeType", "cb.cookbookName", "c.cookbookNum" };
		String fromSql = "consumeRecord c left join user u on c.userId=u.userId left join device d on c.deviceId=d.id left join meal m on c.mealId=m.id left join cookbook cb on c.cookbookCode=cb.cookbookCode and cb.companyId=c.companyId";

		String whereSql = "c.companyId=" + company.getId();
		// 2016.09.02 过滤 补助领取 消费类型
		// and c.consumeType != 9 c.consumeType != 109
		// String whereSql = "c.companyId=" + company.getId() +" "+"and
		// c.consumeType != 9 and c.consumeType != 109";

		if (!StringUtils.isEmpty(nameStr)) {
			whereSql += String.format(" and (userNO like '%%%s%%' or username like '%%%s%%' or shortName like '%%%s%%')", nameStr, nameStr, nameStr);
		}
		/*
		 * if (!StringUtils.isEmpty(deptId)) { whereSql += String.format(
		 * " and u.deptId = %s", deptId); }
		 */
		if (!StringUtils.isEmpty(deptId) && deptId != -1) {
			whereSql += String.format(" and find_in_set(u.deptId,getSubIds(%s,1))>0", deptId);
		}
		if (!StringUtils.isEmpty(cardTypeId) && cardTypeId != -1) {
			whereSql += String.format(" and cardTypeId = %s", cardTypeId);
		}
		if (!StringUtils.isEmpty(deviceId) && deviceId != -1) {
			whereSql += String.format(" and deviceId = %s", deviceId);
		}
		if (!StringUtils.isEmpty(consumeType) && consumeType != -1) {
			whereSql += String.format(" and consumeType = %s", consumeType);
		}
		// 2016.09.02 zch 添加默认显示当天的消费
		String dateColumn = "opTime";
		if (StringUtils.isEmpty(endDate)) {
			endDate = StringUtil.getNowTimeForDataCenterEd();
			whereSql += String.format(" and %s <= '%s'", dateColumn, endDate);
		}
		if (StringUtils.isEmpty(beginDate)) {
			beginDate = StringUtil.getNowTimeForDataCenterBg();
			whereSql += String.format(" and %s >= '%s'", dateColumn, beginDate);
		}
		if (!StringUtils.isEmpty(dateType) && dateType != -1) {
			// String dateColumn = "opTime";\

			if (dateType == 1) {
				dateColumn = "collectTime";
			}
			if (!StringUtils.isEmpty(beginDate)) {
				whereSql += String.format(" and %s >= '%s'", dateColumn, beginDate);
			}

			if (!StringUtils.isEmpty(endDate)) {
				whereSql += String.format(" and %s <= '%s'", dateColumn, endDate);
			}

		}
		if (StringUtils.isEmpty(includeOff)) {
			whereSql += " and u.status != 244";
		}

		List<Map> list = this.commonService.selectByPage(columns, null, fromSql, whereSql, pagination);
		List<Map> listAll = this.commonService.selectNoPage(columns, null, fromSql, whereSql);

		int totalCount = Integer.valueOf(list.get(0).get("deviceName").toString());
		list.remove(0);
		listAll.remove(0);
		BigDecimal sumoddFareAll = new BigDecimal(0);
		BigDecimal sumsubsidyOddFareAll = new BigDecimal(0);
		BigDecimal sumdiscountFareAll = new BigDecimal(0);
		BigDecimal sumopFareAll = new BigDecimal(0);

		for (Map m : listAll) {
			m.put("oddFare", Float.valueOf(m.get("oddFare").toString()) / 100);
			m.put("subsidyOddFare", Float.valueOf(m.get("subsidyOddFare").toString()) / 100);
			m.put("discountFare", Float.valueOf(m.get("discountFare").toString()) / 100);
			m.put("opFare", (Float.valueOf(m.get("opFare").toString()) + Float.valueOf(m.get("subsidyOpFare").toString())) / 100);

			//sumoddFareAll += StringUtil.objToFloat(m.get("oddFare"));
			sumoddFareAll = sumoddFareAll.add(new BigDecimal(m.get("oddFare").toString()));
			
			//sumsubsidyOddFareAll += StringUtil.objToFloat(m.get("subsidyOddFare"));
			sumsubsidyOddFareAll = sumsubsidyOddFareAll.add(new BigDecimal(m.get("subsidyOddFare").toString()));
			//sumdiscountFareAll += StringUtil.objToFloat(m.get("discountFare"));
			sumdiscountFareAll = sumdiscountFareAll.add(new BigDecimal(m.get("discountFare").toString()));
			//sumopFareAll += StringUtil.objToFloat(m.get("opFare"));
			sumopFareAll = sumopFareAll.add(new BigDecimal(m.get("opFare").toString()));
		}

		BigDecimal sumoddFare = new BigDecimal(0);
		BigDecimal sumsubsidyOddFare = new BigDecimal(0);
		BigDecimal sumdiscountFare = new BigDecimal(0);
		BigDecimal sumopFare = new BigDecimal(0);

		for (Map m : list) {
			m.put("consumeType", ConsumeType.getTypeDes(Integer.valueOf(m.get("consumeType").toString())));
			m.put("oddFare", Float.valueOf(m.get("oddFare").toString()) / 100);
			m.put("subsidyOddFare", Float.valueOf(m.get("subsidyOddFare").toString()) / 100);
			m.put("discountFare", Float.valueOf(m.get("discountFare").toString()) / 100);
			m.put("opFare", (Float.valueOf(m.get("opFare").toString()) + Float.valueOf(m.get("subsidyOpFare").toString())) / 100);

			//sumoddFare += StringUtil.objToFloat(m.get("oddFare"));
			sumoddFare = sumoddFare.add(new BigDecimal(m.get("oddFare").toString()));
			
			//sumsubsidyOddFare += StringUtil.objToFloat(m.get("subsidyOddFare"));
			sumsubsidyOddFare = sumsubsidyOddFare.add(new BigDecimal(m.get("subsidyOddFare").toString()));
		//	sumdiscountFare += StringUtil.objToFloat(m.get("discountFare"));
			sumdiscountFare = sumdiscountFare.add(new BigDecimal(m.get("discountFare").toString()));
			//sumopFare += StringUtil.objToFloat(m.get("opFare"));
			
			sumopFare  = sumopFare.add(new BigDecimal(m.get("opFare").toString()));
		}

		if (export != null && 1 == export) {

			String[] expColumns = { "终端名称", "用户编号", "卡号", "物理卡号", "姓名", "钱包余额", "补助余额", "管理费", "操作额", "餐别名称", "操作时间", "卡计数", "补助卡计数", "记录序号", "记录类型", "菜肴名称", "菜肴份数" };

			List<List<String>> exportList = new ArrayList<List<String>>();
			for (Map m : list) {
				List<String> list2 = new ArrayList<String>();
				list2.add(StringUtil.objToString(m.get("deviceName")));
				list2.add(StringUtil.objToString(m.get("userNO")));
				list2.add(StringUtil.objToString(m.get("cardNO")));
				list2.add(StringUtil.objToString(m.get("cardSN")));
				list2.add(StringUtil.objToString(m.get("username")));
				list2.add(StringUtil.objToString(m.get("oddFare")));
				list2.add(StringUtil.objToString(m.get("subsidyOddFare")));
				list2.add(StringUtil.objToString(m.get("discountFare")));
				list2.add(StringUtil.objToString(m.get("opFare")));
				list2.add(StringUtil.objToString(m.get("mealName")));
				list2.add(StringUtil.objToString(m.get("opTime")));
				list2.add(StringUtil.objToString(m.get("opCount")));
				list2.add(StringUtil.objToString(m.get("subsidyOpCount")));
				list2.add(StringUtil.objToString(m.get("recordNO")));
				list2.add(StringUtil.objToString(m.get("consumeType")));
				list2.add(StringUtil.objToString(m.get("cookbookName")));
				list2.add(StringUtil.objToString(m.get("cookbookNum")));
				exportList.add(list2);
			}

			ExportUtil.exportExcel("消费记录表" + StringUtil.getNowTime(), expColumns, exportList, response);
			return null;
		}

		model.addAttribute("list", list);
		model.addAttribute("pageNum", pagination.getPageNum());
		model.addAttribute("numPerPage", pagination.getNumPerPage());
		model.addAttribute("totalCount", totalCount);
		model.addAttribute("nameStr", nameStr);
		model.addAttribute("deptId", deptId);
		model.addAttribute("deptName", deptName);
		model.addAttribute("cardTypeId", cardTypeId);
		model.addAttribute("deviceId", deviceId);
		model.addAttribute("deviceName", deviceName);
		model.addAttribute("consumeType", consumeType);
		model.addAttribute("dateType", dateType);
		model.addAttribute("beginDate", beginDate);
		model.addAttribute("endDate", endDate);
		model.addAttribute("includeOff", includeOff);

		model.addAttribute("sumoddFare", sumoddFare);
		model.addAttribute("sumsubsidyOddFare", sumsubsidyOddFare);
		model.addAttribute("sumdiscountFare", sumdiscountFare);
		model.addAttribute("sumopFare", sumopFare);

		// float sumoddFareAll = 0.00f;
		// float sumsubsidyOddFareAll = 0.00f;
		// float sumdiscountFareAll = 0.00f;
		// float sumopFareAll = 0.00f;

		model.addAttribute("sumoddFareAll", sumoddFareAll);
		model.addAttribute("sumsubsidyOddFareAll", sumsubsidyOddFareAll);
		model.addAttribute("sumdiscountFareAll", sumdiscountFareAll);
		model.addAttribute("sumopFareAll", sumopFareAll);

		List<Device> deviceList = this.deviceService.selectDeviceListByCompanyId(company.getId(), new String[] { "2", "3" }, 0);
		model.addAttribute("deviceList", deviceList);

		model.addAttribute("base", StringUtil.requestBase(request));
		return StringUtil.requestPath(request, "list");
	}
}
