package zhumeng.com.uimei.service.wx.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import zhumeng.com.uimei.dao.wx.TWxUserMapper;
import zhumeng.com.uimei.model.dbo.wx.TWxUser;
import zhumeng.com.uimei.service.wx.TWxUserService;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

/**
 * @Title: TWxUserServiceImpl.java
 * @Package zhumeng.com.uimei.service.wx.impl
 * @Description: TODO(用一句话描述该文件做什么)
 * @author z
 * @date 2018年7月17日
 * @version V1.0
 */
@Service
@Transactional
public class TWxUserServiceImpl implements TWxUserService{

	@Autowired
	private TWxUserMapper wxUserMapper;
	
	@Override
	public void save(TWxUser record) throws Exception {
		// TODO Auto-generated method stub
		if(record.getId()!=null){
			wxUserMapper.updateByPrimaryKeySelective(record);
		}else{
			wxUserMapper.insert(record);
		}
	}


	@Override
	public void update(TWxUser record) throws Exception {
		// TODO Auto-generated method stub
		wxUserMapper.updateByPrimaryKeySelective(record);
	}

	
	@Override
	public List<TWxUser> findList(TWxUser record) throws Exception {
		// TODO Auto-generated method stub
		return wxUserMapper.findForList(record);
	}

	
	@Override
	public Integer findCount(TWxUser record) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void delete(Long id) throws Exception {
		// TODO Auto-generated method stub
		wxUserMapper.deleteByPrimaryKey(id);
	}

	
	@Override
	public TWxUser findByKey(Long id) throws Exception {
		// TODO Auto-generated method stub
		return wxUserMapper.selectByPrimaryKey(id);
	}

	
	@Override
	public PageInfo<TWxUser> findByPage(int pageNo, int pageSize, TWxUser record)
			throws Exception {
		// TODO Auto-generated method stub
		PageHelper.startPage(pageNo, pageSize);
		return new PageInfo<TWxUser>(wxUserMapper.findForList(record));
	}

	

}