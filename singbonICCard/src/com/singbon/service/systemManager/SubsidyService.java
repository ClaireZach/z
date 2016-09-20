package com.singbon.service.systemManager;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.multipart.MultipartFile;

import com.singbon.dao.BaseDAO;
import com.singbon.dao.systemManager.SubsidyDAO;
import com.singbon.entity.Company;
import com.singbon.entity.Subsidy;
import com.singbon.service.BaseService;
import com.singbon.util.StringUtil;

/**
 * 补助管理业务层
 * 
 * @author 郝威
 * 
 */
@Service
public class SubsidyService extends BaseService {

	@Autowired
	public SubsidyDAO subsidyDAO;

	@Override
	public BaseDAO getBaseDAO() {
		return subsidyDAO;
	}

	/**
	 * 添加人员
	 * 
	 * @param list
	 * @throws Exception
	 */
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public void insert(Integer companyId, String[] userIds, Integer subsidyVersion, String invalidDate, Integer subsidyStatus) throws Exception {
		this.subsidyDAO.insert(companyId, userIds, subsidyVersion, invalidDate, subsidyStatus);
	}

	/**
	 * 查询生成补助信息
	 * 
	 * @param list
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	public Map selectSubsidyInfo(Integer companyId) {
		return this.subsidyDAO.selectSubsidyInfo(companyId);
	}

	/**
	 * 添加金额
	 * 
	 * @param list
	 * @throws Exception
	 */
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public void addFare(Integer companyId, float subsidyFare) throws Exception {
		this.subsidyDAO.addFare(companyId, subsidyFare);
	}

	/**
	 * 自动生成金额
	 * 
	 * @param list
	 * @throws Exception
	 */
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public void autoFare(Integer companyId) throws Exception {
		this.subsidyDAO.autoFare(companyId);
	}

	/**
	 * 转移补助
	 * 
	 * @param list
	 * @throws Exception
	 */
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public void transferSubsidy(Integer companyId) throws Exception {
		this.subsidyDAO.transferSubsidy(companyId);
		this.subsidyDAO.deleteAll(companyId);
	}

	/**
	 * 生成补助信息
	 * 
	 * @param list
	 * @throws Exception
	 */
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public void generateSubsidy(Company company, String invalidDate, HttpServletRequest request) throws Exception {
		int subsidyVersion = company.getSubsidyVersion() + 1;
		company.setSubsidyVersion(subsidyVersion);
		company.setSubsidyInvalidDate(invalidDate);
		this.subsidyDAO.generateSubsidy(company.getId(), subsidyVersion, invalidDate);

		request.getSession().setAttribute("company", company);
	}

	/**
	 * 信息导入
	 * 
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings({ "resource", "rawtypes" })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public void doImport(MultipartFile file, Integer companyId, Integer subsidyVersion, String invalidDate, Integer status) throws Exception {
		try {
			HSSFWorkbook wb = new HSSFWorkbook(file.getInputStream());
			HSSFSheet sheet = wb.getSheetAt(0);
			int rowNum = sheet.getLastRowNum();
			for (int i = 1; i <= rowNum; i++) {
				HSSFRow row = sheet.getRow(i);
				String userNO = StringUtil.objToString(row.getCell(0));
				if (userNO.trim().length() == 0)
					break;
				Map map = this.subsidyDAO.selectUserSubsidyInfo(companyId, userNO);
				if (map == null || map.get("userId") == null) {
					throw new RuntimeException("不存在编号" + userNO + "的人员信息");
				}

				float subsidyFare = StringUtil.objToFloat(row.getCell(2));

				Object id = map.get("id");
				if (id == null) {
					this.subsidyDAO.importSubsidy(companyId, StringUtil.objToString(map.get("userId")), subsidyFare, subsidyVersion, invalidDate, status);
				} else {
					Subsidy subsidy = new Subsidy();
					subsidy.setId(StringUtil.objToInt(id));
					subsidy.setSubsidyFare(subsidyFare);
					this.subsidyDAO.update(subsidy);
				}
			}
		} catch (Exception e) {
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			throw new RuntimeException(e.getMessage());
		}
	}
}
