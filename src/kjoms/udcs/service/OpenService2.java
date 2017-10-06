package kjoms.udcs.service;

import kjoms.udcs.bean.OrgUnitBean;
import kjoms.udcs.bean.ResourceBean;
import kjoms.udcs.bean.UserBean;
import kjoms.udcs.bean.ZTZTBean;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Created by panpan on 2016/11/16.
 * 用于与武大专题图的对接
 */
public interface OpenService2 {
    /**
     * 根据登录Id及密码登录
     *
     * @param loginId
     * @param password
     * @return 登录成功返回登录token
     */
    public String login(String loginId, String password);

    /**
     * 根据token值及登录Id，判断用户是否已登录
     *
     * @param token   token值
     * @param loginId 登录Id,username,viewname 三个组合才算对
     * @return 是否已登录
     */
    public boolean isLogin(String token, String loginId);

    /**
     * 根据token值获取用户登录Id
     *
     * @return 用户登录Id,username,viewname
     */
    public String getLoginIdByToken(String token);


    /**
     * 根据登录Id获取用户资源列表
     *
     * @param loginId 登录Id
     * @return 资源列表
     */
    public List<ResourceBean> getResourceByLoginId(String loginId);

    /**
     * 根据登录Id和资源名称获取用户资源列表
     *
     * @param loginId
     * @param name
     * @return
     */
    public List<ResourceBean> getResourceByLoginIdAndName(String loginId, String name);

    /**
     * 武大专题图注册
     *
     * @param loginId         用户id
     * @param name            资源名称
     * @param title           资源要题
     * @param keywordsArray   关键字列表
     * @param orgnizationName 责任单位名称
     * @param individualName  负责人名称
     * @param fzrOrgName      负责人单位名称
     * @param address         地址
     * @param voice           电话
     * @param fax             传真
     * @param directoryId     资源保存目录id，默认为0 根目录
     * @param url             url
     * @return -1 注册失败，0-成功，1-资源名称已存在，2-用户id为空, 3-资源名称为空，4-摘要为空，5-关键字列表为空，6-url为空
     */
    public int addResource(String loginId, String name, String title, List<String> keywordsArray, String orgnizationName, String individualName, String fzrOrgName, String address, String voice, String fax, String directoryId, String url);

    /**
     * 保存资源信息
     *
     * @param resource 资源信息
     * @return 保存结果
     */
    public boolean saveResource(ResourceBean resource);

    /**
     * 获取用户所有的 武大专题图
     *
     * @param loginId 用户id
     * @return
     */
    public List<ZTZTBean> getAllZTZT(String loginId);

    /**
     * 判断用户能否是有 专题图
     *
     * @param loginId
     * @param ZTZT_ID
     * @return
     */
    public boolean hasZTZT(String loginId, String ZTZT_ID);

    /**
     * 获取用户专题图的数量
     *
     * @param loginId
     * @return
     */
    public int getCountOfZTZT(String loginId);

    /**
     * 分页查询专题图
     *
     * @param loginId
     * @param sizeOfPage 每页数量
     * @param page       当前页数   从0开始
     * @return
     */
    public List<ZTZTBean> getAllZTZTByPage(String loginId, int sizeOfPage, int page);


}
