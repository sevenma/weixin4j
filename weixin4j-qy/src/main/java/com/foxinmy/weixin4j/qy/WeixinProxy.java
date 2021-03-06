package com.foxinmy.weixin4j.qy;

import java.io.InputStream;
import java.util.List;

import com.foxinmy.weixin4j.exception.WeixinException;
import com.foxinmy.weixin4j.http.weixin.JsonResult;
import com.foxinmy.weixin4j.model.Button;
import com.foxinmy.weixin4j.model.MediaCounter;
import com.foxinmy.weixin4j.model.MediaDownloadResult;
import com.foxinmy.weixin4j.model.MediaItem;
import com.foxinmy.weixin4j.model.MediaRecord;
import com.foxinmy.weixin4j.model.MediaUploadResult;
import com.foxinmy.weixin4j.model.Pageable;
import com.foxinmy.weixin4j.model.WeixinAccount;
import com.foxinmy.weixin4j.qy.api.AgentApi;
import com.foxinmy.weixin4j.qy.api.BatchApi;
import com.foxinmy.weixin4j.qy.api.ChatApi;
import com.foxinmy.weixin4j.qy.api.HelperApi;
import com.foxinmy.weixin4j.qy.api.MediaApi;
import com.foxinmy.weixin4j.qy.api.MenuApi;
import com.foxinmy.weixin4j.qy.api.NotifyApi;
import com.foxinmy.weixin4j.qy.api.PartyApi;
import com.foxinmy.weixin4j.qy.api.TagApi;
import com.foxinmy.weixin4j.qy.api.UserApi;
import com.foxinmy.weixin4j.qy.message.ChatMessage;
import com.foxinmy.weixin4j.qy.message.CustomeMessage;
import com.foxinmy.weixin4j.qy.message.NotifyMessage;
import com.foxinmy.weixin4j.qy.model.AgentInfo;
import com.foxinmy.weixin4j.qy.model.AgentOverview;
import com.foxinmy.weixin4j.qy.model.AgentSetter;
import com.foxinmy.weixin4j.qy.model.BatchResult;
import com.foxinmy.weixin4j.qy.model.Callback;
import com.foxinmy.weixin4j.qy.model.ChatInfo;
import com.foxinmy.weixin4j.qy.model.ChatMute;
import com.foxinmy.weixin4j.qy.model.Contacts;
import com.foxinmy.weixin4j.qy.model.IdParameter;
import com.foxinmy.weixin4j.qy.model.Party;
import com.foxinmy.weixin4j.qy.model.Tag;
import com.foxinmy.weixin4j.qy.model.User;
import com.foxinmy.weixin4j.qy.suite.SuitePerCodeHolder;
import com.foxinmy.weixin4j.qy.suite.WeixinTokenSuiteCreator;
import com.foxinmy.weixin4j.qy.token.WeixinTicketCreator;
import com.foxinmy.weixin4j.qy.token.WeixinTokenCreator;
import com.foxinmy.weixin4j.qy.type.ChatType;
import com.foxinmy.weixin4j.qy.type.InviteType;
import com.foxinmy.weixin4j.qy.type.KfType;
import com.foxinmy.weixin4j.qy.type.UserStatus;
import com.foxinmy.weixin4j.token.TokenHolder;
import com.foxinmy.weixin4j.tuple.MpArticle;
import com.foxinmy.weixin4j.type.MediaType;
import com.foxinmy.weixin4j.type.TicketType;
import com.foxinmy.weixin4j.util.Weixin4jSettings;

/**
 * 微信企业号接口实现
 * 
 * @className WeixinProxy
 * @author jy
 * @date 2014年11月19日
 * @since JDK 1.6
 * @see <a href="http://qydev.weixin.qq.com/wiki/index.php">api文档</a>
 */
public class WeixinProxy {

	private final MediaApi mediaApi;
	private final MenuApi menuApi;
	private final NotifyApi notifyApi;
	private final PartyApi partyApi;
	private final UserApi userApi;
	private final TagApi tagApi;
	private final HelperApi helperApi;
	private final AgentApi agentApi;
	private final BatchApi batchApi;
	private final ChatApi chatApi;

	private final TokenHolder tokenHolder;

	private Weixin4jSettings settings;

	/**
	 * 默认使用文件方式保存token、使用weixin4j.properties配置的账号信息
	 */
	public WeixinProxy() {
		this(new Weixin4jSettings());
	}

	/**
	 * 
	 * @param settings
	 *            微信配置信息
	 * @see com.foxinmy.weixin4j.util.Weixin4jSettings
	 */
	public WeixinProxy(Weixin4jSettings settings) {
		this(new TokenHolder(new WeixinTokenCreator(settings.getWeixinAccount()
				.getId(), settings.getWeixinAccount().getSecret()),
				settings.getTokenStorager0()));
		this.settings = settings;
	}

	/**
	 * 第三方套件(永久授权码机制)
	 * 
	 * @param perCodeHolder
	 *            第三方套件永久授权码
	 *            {@link com.foxinmy.weixin4j.qy.api.SuiteApi#getPerCodeHolder(String)}
	 * @param suiteTokenHolder
	 *            第三方套件凭证token
	 *            {@link com.foxinmy.weixin4j.qy.api.SuiteApi#getTokenSuiteHolder(String)}
	 * @see com.foxinmy.weixin4j.qy.api.SuiteApi
	 * @see WeixinSuiteProxy#getWeixinProxy(String, String)
	 */
	public WeixinProxy(SuitePerCodeHolder perCodeHolder,
			TokenHolder suiteTokenHolder) {
		this(new TokenHolder(new WeixinTokenSuiteCreator(perCodeHolder,
				suiteTokenHolder), perCodeHolder.getTokenStorager()));
		this.settings = new Weixin4jSettings(new WeixinAccount(
				perCodeHolder.getAuthCorpId(), null));
	}

	/**
	 * 注意：TokenCreator 需为 <font
	 * color="red">WeixinTokenCreator或WeixinTokenSuiteCreator</font>
	 * 
	 * @see com.foxinmy.weixin4j.qy.token.WeixinTokenCreator.WeixinTokenCreator
	 * @param tokenHolder
	 */
	private WeixinProxy(TokenHolder tokenHolder) {
		this.tokenHolder = tokenHolder;
		this.partyApi = new PartyApi(tokenHolder);
		this.userApi = new UserApi(tokenHolder);
		this.tagApi = new TagApi(tokenHolder);
		this.helperApi = new HelperApi(tokenHolder);
		this.agentApi = new AgentApi(tokenHolder);
		this.batchApi = new BatchApi(tokenHolder);
		this.notifyApi = new NotifyApi(tokenHolder);
		this.menuApi = new MenuApi(tokenHolder);
		this.mediaApi = new MediaApi(tokenHolder);
		this.chatApi = new ChatApi(tokenHolder);
	}

	/**
	 * token获取
	 * 
	 * @return
	 */
	public TokenHolder getTokenHolder() {
		return this.tokenHolder;
	}

	/**
	 * 获取微信账号信息
	 * 
	 * @return
	 */
	public WeixinAccount getWeixinAccount() {
		return this.settings.getWeixinAccount();
	}

	/**
	 * 获取JSSDK Ticket的tokenHolder
	 * 
	 * @param ticketType
	 *            票据类型
	 * @return
	 */
	public TokenHolder getTicketHolder(TicketType ticketType) {
		return new TokenHolder(new WeixinTicketCreator(getWeixinAccount()
				.getId(), ticketType, this.tokenHolder),
				this.settings.getTokenStorager0());
	}

	/**
	 * 发送客服消息(需要管理员对应用有使用权限，对收件人touser、toparty、totag有查看权限，否则本次调用失败)
	 * <p>
	 * 1） 发送人员列表存在错误的userid：执行发送，开发者需注意返回结果说明</br>
	 * 2）发送人员不在通讯录权限范围内：不执行发送任务，返回首个出错的userid</br>
	 * 3）发送人员不在应用可见范围内：不执行发送任务，返回首个出错的userid</br>
	 * </p>
	 * 
	 * @param message
	 *            客服消息对象
	 * @return 如果对应用或收件人、部门、标签任何一个无权限，则本次发送失败；如果收件人、部门或标签不存在，发送仍然执行，但返回无效的部分
	 *         </br> { "errcode": 0, "errmsg": "ok", "invaliduser": "UserID1",
	 *         "invalidparty":"PartyID1", "invalidtag":"TagID1" }
	 * @throws WeixinException
	 * @see com.foxinmy.weixin4j.qy.api.NotifyApi
	 * @see <a href=
	 *      "http://qydev.weixin.qq.com/wiki/index.php?title=%E5%8F%91%E9%80%81%E6%8E%A5%E5%8F%A3%E8%AF%B4%E6%98%8E">
	 *      发送接口说明</a>
	 * @see <a href=
	 *      "http://qydev.weixin.qq.com/wiki/index.php?title=%E6%B6%88%E6%81%AF%E7%B1%BB%E5%9E%8B%E5%8F%8A%E6%95%B0%E6%8D%AE%E6%A0%BC%E5%BC%8F">
	 *      发送格式说明</a>
	 * @see com.foxinmy.weixin4j.tuple.Text
	 * @see com.foxinmy.weixin4j.tuple.Image
	 * @see com.foxinmy.weixin4j.tuple.Voice
	 * @see com.foxinmy.weixin4j.tuple.Video
	 * @see com.foxinmy.weixin4j.tuple.File
	 * @see com.foxinmy.weixin4j.tuple.News
	 * @see com.foxinmy.weixin4j.tuple.MpNews
	 * @see com.foxinmy.weixin4j.qy.model.IdParameter
	 */
	public IdParameter sendNotifyMessage(NotifyMessage message)
			throws WeixinException {
		return notifyApi.sendNotifyMessage(message);
	}

	/**
	 * 发送客服消息
	 * 
	 * @param message
	 *            客服消息对象
	 * @return 发送结果
	 * @see <a
	 *      href="http://qydev.weixin.qq.com/wiki/index.php?title=%E4%BC%81%E4%B8%9A%E5%AE%A2%E6%9C%8D%E6%8E%A5%E5%8F%A3%E8%AF%B4%E6%98%8E">客服接口说明</a>
	 * @see com.foxinmy.weixin4j.qy.api.NotifyApi
	 * @see com.foxinmy.weixin4j.tuple.Text
	 * @see com.foxinmy.weixin4j.tuple.Image
	 * @see com.foxinmy.weixin4j.tuple.Voice
	 * @see com.foxinmy.weixin4j.tuple.Video
	 * @see com.foxinmy.weixin4j.tuple.File
	 * @see com.foxinmy.weixin4j.qy.message.CustomeMessage
	 * @throws WeixinException
	 */
	public JsonResult sendCustomeMessage(CustomeMessage message)
			throws WeixinException {
		return notifyApi.sendCustomeMessage(message);
	}

	/**
	 * 获取客服列表
	 * 
	 * @param kfType
	 *            客服类型 为空时返回全部类型的客服
	 * @return 第一个元素为内部客服(internal),第二个参数为外部客服(external)
	 * @see com.foxinmy.weixin4j.qy.api.NotifyApi
	 * @see com.foxinmy.weixin4j.qy.model.IdParameter
	 * @see <a
	 *      href="http://qydev.weixin.qq.com/wiki/index.php?title=%E4%BC%81%E4%B8%9A%E5%AE%A2%E6%9C%8D%E6%8E%A5%E5%8F%A3%E8%AF%B4%E6%98%8E">客服列表</a>
	 * @throws WeixinException
	 */
	public IdParameter[] getKfList(KfType kfType) throws WeixinException {
		return notifyApi.getKfList(kfType);
	}

	/**
	 * 自定义菜单(管理员须拥有应用的管理权限 并且应用必须设置在回调模式)
	 * 
	 * @param buttons
	 *            菜单列表
	 * @param agentid
	 *            应用ID
	 * @throws WeixinException
	 * @see com.foxinmy.weixin4j.qy.api.MenuApi
	 * @see <a href=
	 *      "http://qydev.weixin.qq.com/wiki/index.php?title=%E5%88%9B%E5%BB%BA%E5%BA%94%E7%94%A8%E8%8F%9C%E5%8D%95">
	 *      创建自定义菜单</a>
	 * @see com.foxinmy.weixin4j.model.Button
	 */
	public JsonResult createMenu(List<Button> buttons, int agentid)
			throws WeixinException {
		return menuApi.createMenu(buttons, agentid);
	}

	/**
	 * 查询菜单(管理员须拥有应用的管理权限 并且应用必须设置在回调模式。)
	 * 
	 * @param agentid
	 *            应用ID
	 * @return 菜单集合
	 * @throws WeixinException
	 * @see com.foxinmy.weixin4j.qy.api.MenuApi
	 * @see <a href=
	 *      "http://qydev.weixin.qq.com/wiki/index.php?title=%E8%8E%B7%E5%8F%96%E8%8F%9C%E5%8D%95%E5%88%97%E8%A1%A8">
	 *      查询菜单</a>
	 * @see com.foxinmy.weixin4j.model.Button
	 */
	public List<Button> getMenu(int agentid) throws WeixinException {
		return menuApi.getMenu(agentid);
	}

	/**
	 * 删除菜单(管理员须拥有应用的管理权限 并且应用必须设置在回调模式)
	 * 
	 * @param agentid
	 *            应用ID
	 * @throws WeixinException
	 * @see com.foxinmy.weixin4j.qy.api.MenuApi
	 * @see <a href=
	 *      "http://qydev.weixin.qq.com/wiki/index.php?title=%E5%88%A0%E9%99%A4%E8%8F%9C%E5%8D%95">
	 *      删除菜单</a>
	 * @return 处理结果
	 */
	public JsonResult deleteMenu(int agentid) throws WeixinException {
		return menuApi.deleteMenu(agentid);
	}

	/**
	 * 上传图文消息内的图片:用于上传图片到企业号服务端，接口返回图片url，请注意，该url仅可用于图文消息的发送，
	 * 且每个企业每天最多只能上传100张图片。
	 * 
	 * @param is
	 *            图片数据
	 * @param fileName
	 *            文件名
	 * @see <a
	 *      href="http://qydev.weixin.qq.com/wiki/index.php?title=%E4%B8%8A%E4%BC%A0%E5%9B%BE%E6%96%87%E6%B6%88%E6%81%AF%E5%86%85%E7%9A%84%E5%9B%BE%E7%89%87">上传图文消息内的图片</a>
	 * @return 图片url
	 * @see com.foxinmy.weixin4j.qy.api.MediaApi
	 * @throws WeixinException
	 */
	public String uploadImage(InputStream is, String fileName)
			throws WeixinException {
		return mediaApi.uploadImage(is, fileName);
	}

	/**
	 * 上传媒体文件
	 * <p>
	 * 正常情况下返回{"type":"TYPE","media_id":"MEDIA_ID","created_at":123456789},
	 * 否则抛出异常.
	 * </p>
	 * 
	 * @param agentid
	 *            企业应用ID(<font color="red">大于0时视为上传永久媒体文件</font>)
	 * @param is
	 *            媒体数据流
	 * @param fileName
	 *            文件名
	 * @return 上传到微信服务器返回的媒体标识
	 * @see com.foxinmy.weixin4j.qy.api.MediaApi
	 * @see com.foxinmy.weixin4j.model.MediaUploadResult
	 * @see <a href=
	 *      "http://qydev.weixin.qq.com/wiki/index.php?title=%E4%B8%8A%E4%BC%A0%E4%B8%B4%E6%97%B6%E7%B4%A0%E6%9D%90%E6%96%87%E4%BB%B6">
	 *      上传临时素材文件说明</a>
	 * @see <a href=
	 *      "http://http://qydev.weixin.qq.com/wiki/index.php?title=%E4%B8%8A%E4%BC%A0%E6%B0%B8%E4%B9%85%E7%B4%A0%E6%9D%90">
	 *      上传永久素材文件说明</a>
	 * @throws WeixinException
	 */
	public MediaUploadResult uploadMedia(int agentid, InputStream is,
			String fileName) throws WeixinException {
		return mediaApi.uploadMedia(agentid, is, fileName);
	}

	/**
	 * 下载媒体文件
	 * 
	 * @param agentid
	 *            企业应用Id(<font color="red">大于0时视为获取永久媒体文件</font>)
	 * @param mediaId
	 *            媒体ID
	 * @return 媒体下载结果
	 * @see com.foxinmy.weixin4j.model.MediaDownloadResult
	 * @see com.foxinmy.weixin4j.qy.api.MediaApi
	 * @see <a href=
	 *      "http://qydev.weixin.qq.com/wiki/index.php?title=%E8%8E%B7%E5%8F%96%E4%B8%B4%E6%97%B6%E7%B4%A0%E6%9D%90%E6%96%87%E4%BB%B6">
	 *      获取临时媒体说明</a>
	 * @see <a href=
	 *      "http://qydev.weixin.qq.com/wiki/index.php?title=%E8%8E%B7%E5%8F%96%E6%B0%B8%E4%B9%85%E7%B4%A0%E6%9D%90">
	 *      获取永久媒体说明</a>
	 * @throws WeixinException
	 */
	public MediaDownloadResult downloadMedia(int agentid, String mediaId)
			throws WeixinException {
		return mediaApi.downloadMedia(agentid, mediaId);
	}

	/**
	 * 上传永久图文素材
	 * <p>
	 * 、新增的永久素材也可以在公众平台官网素材管理模块中看到,永久素材的数量是有上限的，请谨慎新增。图文消息素材和图片素材的上限为5000，
	 * 其他类型为1000
	 * </P>
	 * 
	 * @param agentid
	 *            企业应用的id
	 * @param articles
	 *            图文列表
	 * @return 上传到微信服务器返回的媒体标识
	 * @throws WeixinException
	 * @see com.foxinmy.weixin4j.qy.api.MediaApi
	 * @see <a href=
	 *      "http://qydev.weixin.qq.com/wiki/index.php?title=%E4%B8%8A%E4%BC%A0%E6%B0%B8%E4%B9%85%E7%B4%A0%E6%9D%90">
	 *      上传永久媒体素材</a>
	 * @see com.foxinmy.weixin4j.tuple.MpArticle
	 */
	public String uploadMaterialArticle(int agentid, List<MpArticle> articles)
			throws WeixinException {
		return mediaApi.uploadMaterialArticle(agentid, articles);
	}

	/**
	 * 删除永久媒体素材
	 * 
	 * @param agentid
	 *            企业应用ID
	 * @param mediaId
	 *            媒体素材的media_id
	 * @return 处理结果
	 * @throws WeixinException
	 * @see com.foxinmy.weixin4j.qy.api.MediaApi
	 * @see <a href=
	 *      "http://qydev.weixin.qq.com/wiki/index.php?title=%E5%88%A0%E9%99%A4%E6%B0%B8%E4%B9%85%E7%B4%A0%E6%9D%90">
	 *      删除永久媒体素材</a>
	 */
	public JsonResult deleteMaterialMedia(int agentid, String mediaId)
			throws WeixinException {
		return mediaApi.deleteMaterialMedia(agentid, mediaId);
	}

	/**
	 * 下载永久图文素材
	 * 
	 * @param agentid
	 *            企业应用ID
	 * @param mediaId
	 *            媒体素材的media_id
	 * @return 图文列表
	 * @throws WeixinException
	 * @see {@link #downloadMedia(int, String)}
	 * @see com.foxinmy.weixin4j.qy.api.MediaApi
	 * @see com.foxinmy.weixin4j.tuple.MpArticle
	 */
	public List<MpArticle> downloadArticle(int agentid, String mediaId)
			throws WeixinException {
		return mediaApi.downloadArticle(agentid, mediaId);
	}

	/**
	 * 修改永久图文素材
	 * 
	 * @param agentid
	 *            企业应用的id
	 * @param mediaId
	 *            上传后的media_id
	 * @param articles
	 *            图文列表
	 * @return 操作结果
	 * @throws WeixinException
	 * @see com.foxinmy.weixin4j.qy.api.MediaApi
	 * @see <a href=
	 *      "http://qydev.weixin.qq.com/wiki/index.php?title=%E4%BF%AE%E6%94%B9%E6%B0%B8%E4%B9%85%E5%9B%BE%E6%96%87%E7%B4%A0%E6%9D%90">
	 *      修改永久媒体素材</a>
	 * @see com.foxinmy.weixin4j.tuple.MpArticle
	 */
	public String updateMaterialArticle(int agentid, String mediaId,
			List<MpArticle> articles) throws WeixinException {
		return mediaApi.updateMaterialArticle(agentid, mediaId, articles);
	}

	/**
	 * 获取永久媒体素材的总数
	 * 
	 * @param agentid
	 *            企业应用id
	 * @return 总数对象
	 * @throws WeixinException
	 * @see com.foxinmy.weixin4j.qy.api.MediaApi
	 * @see com.foxinmy.weixin4j.model.MediaCounter
	 * @see <a href=
	 *      "http://qydev.weixin.qq.com/wiki/index.php?title=%E8%8E%B7%E5%8F%96%E7%B4%A0%E6%9D%90%E6%80%BB%E6%95%B0">
	 *      获取素材总数</a>
	 */
	public MediaCounter countMaterialMedia(int agentid) throws WeixinException {
		return mediaApi.countMaterialMedia(agentid);
	}

	/**
	 * 获取媒体素材记录列表
	 * 
	 * @param agentid
	 *            企业应用ID
	 * @param mediaType
	 *            素材的类型，图片（image）、视频（video）、语音 （voice）、图文（news）、文件（file）
	 * @param pageable
	 *            分页数据
	 * @return 媒体素材的记录对象
	 * @throws WeixinException
	 * @see com.foxinmy.weixin4j.qy.api.MediaApi
	 * @see com.foxinmy.weixin4j.model.MediaRecord
	 * @see com.foxinmy.weixin4j.type.MediaType
	 * @see com.foxinmy.weixin4j.model.MediaItem
	 * @see com.foxinmy.weixin4j.model.Pageable
	 * @see com.foxinmy.weixin4j.model.Pagedata
	 * @see <a href=
	 *      "http://qydev.weixin.qq.com/wiki/index.php?title=%E8%8E%B7%E5%8F%96%E7%B4%A0%E6%9D%90%E5%88%97%E8%A1%A8">
	 *      获取素材列表</a>
	 */
	public MediaRecord listMaterialMedia(int agentid, MediaType mediaType,
			Pageable pageable) throws WeixinException {
		return mediaApi.listMaterialMedia(agentid, mediaType, pageable);
	}

	/**
	 * 获取全部的媒体素材
	 * 
	 * @param agentid
	 *            企业应用id
	 * @param mediaType
	 *            媒体类型
	 * @return 素材列表
	 * @see com.foxinmy.weixin4j.qy.api.MediaApi
	 * @see {@link #listMaterialMedia(int,MediaType, Pageable)}
	 * @throws WeixinException
	 */
	public List<MediaItem> listAllMaterialMedia(int agentid, MediaType mediaType)
			throws WeixinException {
		return mediaApi.listAllMaterialMedia(agentid, mediaType);
	}

	/**
	 * 创建部门(根部门的parentid为1)
	 * 
	 * @param party
	 *            部门对象
	 * @see com.foxinmy.weixin4j.qy.model.Party
	 * @see <a href=
	 *      "http://qydev.weixin.qq.com/wiki/index.php?title=%E7%AE%A1%E7%90%86%E9%83%A8%E9%97%A8#.E5.88.9B.E5.BB.BA.E9.83.A8.E9.97.A8">
	 *      创建部门说明</a>
	 * @see com.foxinmy.weixin4j.qy.api.PartyApi
	 * @return 部门ID
	 * @throws WeixinException
	 */
	public int createParty(Party party) throws WeixinException {
		return partyApi.createParty(party);
	}

	/**
	 * 更新部门(如果非必须的字段未指定 则不更新该字段之前的设置值)
	 * 
	 * @param party
	 *            部门对象
	 * @see com.foxinmy.weixin4j.qy.model.Party
	 * @see <a href=
	 *      "http://qydev.weixin.qq.com/wiki/index.php?title=%E7%AE%A1%E7%90%86%E9%83%A8%E9%97%A8#.E6.9B.B4.E6.96.B0.E9.83.A8.E9.97.A8">
	 *      更新部门说明</a>
	 * @see com.foxinmy.weixin4j.qy.api.PartyApi
	 * @return 处理结果
	 * @throws WeixinException
	 */
	public JsonResult updateParty(Party party) throws WeixinException {
		return partyApi.updateParty(party);
	}

	/**
	 * 查询部门列表(以部门的order字段从小到大排列)
	 * 
	 * @param partyId
	 *            部门ID。获取指定部门ID下的子部门 传入0表示获取全部子部门
	 * @see com.foxinmy.weixin4j.qy.model.Party
	 * @see <a href=
	 *      "http://qydev.weixin.qq.com/wiki/index.php?title=%E7%AE%A1%E7%90%86%E9%83%A8%E9%97%A8#.E8.8E.B7.E5.8F.96.E9.83.A8.E9.97.A8.E5.88.97.E8.A1.A8">
	 *      获取部门列表</a>
	 * @see com.foxinmy.weixin4j.qy.api.PartyApi
	 * @return 部门列表
	 * @throws WeixinException
	 */
	public List<Party> listParty(int partyId) throws WeixinException {
		return partyApi.listParty(partyId);
	}

	/**
	 * 删除部门(不能删除根部门；不能删除含有子部门、成员的部门)
	 * 
	 * @param partyId
	 *            部门ID
	 * @see <a href=
	 *      "http://qydev.weixin.qq.com/wiki/index.php?title=%E7%AE%A1%E7%90%86%E9%83%A8%E9%97%A8#.E5.88.A0.E9.99.A4.E9.83.A8.E9.97.A8">
	 *      删除部门说明</a>
	 * @see com.foxinmy.weixin4j.qy.api.PartyApi
	 * @return 处理结果
	 * @throws WeixinException
	 */
	public JsonResult deleteParty(int partyId) throws WeixinException {
		return partyApi.deleteParty(partyId);
	}

	/**
	 * 批量上传部门
	 * 
	 * @param parties
	 *            部门列表
	 * @see com.foxinmy.weixin4j.qy.api.MediaApi
	 * @see com.foxinmy.weixin4j.qy.api.BatchApi
	 * @see {@link #batchReplaceParty(String,Callback)}
	 * @see <a href=
	 *      "http://qydev.weixin.qq.com/wiki/index.php?title=%E5%BC%82%E6%AD%A5%E4%BB%BB%E5%8A%A1%E6%8E%A5%E5%8F%A3#.E9.80.9A.E8.AE.AF.E5.BD.95.E6.9B.B4.E6.96.B0">
	 *      批量任务</a>
	 * @return 上传后的mediaId
	 * @throws WeixinException
	 */
	public String batchUploadParties(List<Party> parties)
			throws WeixinException {
		return mediaApi.batchUploadParties(parties);
	}

	/**
	 * 创建成员
	 * 
	 * @param user
	 *            成员对象
	 * @see com.foxinmy.weixin4j.qy.model.User
	 * @see <a href=
	 *      "http://qydev.weixin.qq.com/wiki/index.php?title=%E7%AE%A1%E7%90%86%E6%88%90%E5%91%98#.E5.88.9B.E5.BB.BA.E6.88.90.E5.91.98">
	 *      创建成员说明</a>
	 * @see com.foxinmy.weixin4j.qy.api.UserApi
	 * @return 处理结果
	 * @throws WeixinException
	 */
	public JsonResult createUser(User user) throws WeixinException {
		return userApi.createUser(user);
	}

	/**
	 * 创建成员
	 * 
	 * @param user
	 *            成员对象
	 * @param avatar
	 *            头像文件 可为空
	 * @see com.foxinmy.weixin4j.qy.model.User
	 * @see <a href=
	 *      "http://qydev.weixin.qq.com/wiki/index.php?title=%E7%AE%A1%E7%90%86%E6%88%90%E5%91%98#.E5.88.9B.E5.BB.BA.E6.88.90.E5.91.98">
	 *      创建成员说明</a>
	 * @see com.foxinmy.weixin4j.qy.api.UserApi
	 * @return 处理结果
	 * @throws WeixinException
	 */
	public JsonResult createUser(User user, InputStream avatar)
			throws WeixinException {
		return userApi.createUser(user, avatar);
	}

	/**
	 * 更新用户(如果非必须的字段未指定 则不更新该字段之前的设置值)
	 * 
	 * @param user
	 *            成员对象
	 * @see com.foxinmy.weixin4j.qy.model.User
	 * @see <a href=
	 *      "http://qydev.weixin.qq.com/wiki/index.php?title=%E7%AE%A1%E7%90%86%E6%88%90%E5%91%98#.E6.9B.B4.E6.96.B0.E6.88.90.E5.91.98">
	 *      更新成员说明</a>
	 * @see com.foxinmy.weixin4j.qy.api.UserApi
	 * @return 处理结果
	 * @throws WeixinException
	 */
	public JsonResult updateUser(User user) throws WeixinException {
		return userApi.updateUser(user);
	}

	/**
	 * 更新用户(如果非必须的字段未指定 则不更新该字段之前的设置值)
	 * 
	 * @param user
	 *            成员对象
	 * @param avatar
	 *            头像文件
	 * @see com.foxinmy.weixin4j.qy.model.User
	 * @see <a href=
	 *      "http://qydev.weixin.qq.com/wiki/index.php?title=%E7%AE%A1%E7%90%86%E6%88%90%E5%91%98#.E6.9B.B4.E6.96.B0.E6.88.90.E5.91.98">
	 *      更新成员说明</a>
	 * @see com.foxinmy.weixin4j.qy.api.UserApi
	 * @return 处理结果
	 * @throws WeixinException
	 */
	public JsonResult updateUser(User user, InputStream avatar)
			throws WeixinException {
		return userApi.updateUser(user, avatar);
	}

	/**
	 * 获取成员信息
	 * 
	 * @param userid
	 *            成员唯一ID
	 * @see com.foxinmy.weixin4j.qy.model.User
	 * @see <a href=
	 *      "http://qydev.weixin.qq.com/wiki/index.php?title=%E7%AE%A1%E7%90%86%E6%88%90%E5%91%98#.E8.8E.B7.E5.8F.96.E6.88.90.E5.91.98">
	 *      获取成员说明</a>
	 * @see com.foxinmy.weixin4j.qy.api.UserApi
	 * @return 成员对象
	 * @throws WeixinException
	 */
	public User getUser(String userid) throws WeixinException {
		return userApi.getUser(userid);
	}

	/**
	 * code获取userid(管理员须拥有agent的使用权限；agentid必须和跳转链接时所在的企业应用ID相同。)
	 * 
	 * @param code
	 *            通过员工授权获取到的code，每次员工授权带上的code将不一样，code只能使用一次，5分钟未被使用自动过期
	 * @see com.foxinmy.weixin4j.qy.model.User
	 * @see com.foxinmy.weixin4j.qy.api.UserApi
	 * @return 成员对象
	 * @see {@link #getUser(String)}
	 * @see {@link #getUserIdByCode(String,int)}
	 * @see <a href=
	 *      "http://qydev.weixin.qq.com/wiki/index.php?title=%E4%BC%81%E4%B8%9A%E8%8E%B7%E5%8F%96code">
	 *      企业获取code</a>
	 * @see <a href=
	 *      "http://qydev.weixin.qq.com/wiki/index.php?title=%E6%A0%B9%E6%8D%AEcode%E8%8E%B7%E5%8F%96%E6%88%90%E5%91%98%E4%BF%A1%E6%81%AF">
	 *      根据code获取成员信息</a>
	 * @throws WeixinException
	 */
	public User getUserByCode(String code) throws WeixinException {
		return userApi.getUserByCode(code);
	}

	/**
	 * 根据code获取成员ID信息
	 * 
	 * @param code
	 *            通过员工授权获取到的code，每次员工授权带上的code将不一样，code只能使用一次，5分钟未被使用自动过期
	 * @return 两个元素的数组 <font color="red">第一个元素为userId或者openId
	 *         第二个元素为deviceId</font>
	 * @see com.foxinmy.weixin4j.qy.api.UserApi
	 * @see <a href=
	 *      "http://qydev.weixin.qq.com/wiki/index.php?title=OAuth%E9%AA%8C%E8%AF%81%E6%8E%A5%E5%8F%A3">
	 *      oauth授权获取用户信息</a>
	 * @throws WeixinException
	 */
	public String[] getUserIdByCode(String code) throws WeixinException {
		return userApi.getUserIdByCode(code);
	}

	/**
	 * 获取部门成员
	 * 
	 * @param departId
	 *            部门ID 必须
	 * @param fetchChild
	 *            是否递归获取子部门下面的成员 非必须
	 * @param userStatus
	 *            成员状态 status可叠加 非必须 未填写则默认为未关注(4)
	 * @param findDetail
	 *            是否获取详细信息
	 * @see com.foxinmy.weixin4j.qy.model.User
	 * @see <a href=
	 *      "http://qydev.weixin.qq.com/wiki/index.php?title=%E7%AE%A1%E7%90%86%E6%88%90%E5%91%98#.E8.8E.B7.E5.8F.96.E9.83.A8.E9.97.A8.E6.88.90.E5.91.98">
	 *      获取部门成员说明</a>
	 * @see com.foxinmy.weixin4j.qy.api.UserApi
	 * @return 成员列表
	 * @throws WeixinException
	 */
	public List<User> listUser(int departId, boolean fetchChild,
			UserStatus userStatus, boolean findDetail) throws WeixinException {
		return userApi.listUser(departId, fetchChild, userStatus, findDetail);
	}

	/**
	 * 获取部门下所有状态成员(不进行递归)
	 * 
	 * @param departId
	 *            部门ID
	 * @see {@link #listUser(int, boolean,UserStatus)}
	 * @see com.foxinmy.weixin4j.qy.api.UserApi
	 * @return 成员列表
	 * @throws WeixinException
	 */
	public List<User> listUser(int departId) throws WeixinException {
		return userApi.listUser(departId);
	}

	/**
	 * 删除成员
	 * 
	 * @param userid
	 *            成员ID
	 * @see <a href=
	 *      "http://qydev.weixin.qq.com/wiki/index.php?title=%E7%AE%A1%E7%90%86%E6%88%90%E5%91%98#.E5.88.A0.E9.99.A4.E6.88.90.E5.91.98">
	 *      删除成员说明</a>
	 * @see com.foxinmy.weixin4j.qy.api.UserApi
	 * @return 处理结果
	 * @throws WeixinException
	 */
	public JsonResult deleteUser(String userid) throws WeixinException {
		return userApi.deleteUser(userid);
	}

	/**
	 * 批量删除成员
	 * 
	 * @param userIds
	 *            成员列表
	 * @see <a href=
	 *      "http://qydev.weixin.qq.com/wiki/index.php?title=%E7%AE%A1%E7%90%86%E6%88%90%E5%91%98#.E6.89.B9.E9.87.8F.E5.88.A0.E9.99.A4.E6.88.90.E5.91.98"
	 *      >批量删除成员说明</a
	 * @see com.foxinmy.weixin4j.qy.api.UserApi
	 * @return 处理结果
	 * @throws WeixinException
	 */
	public JsonResult batchDeleteUser(List<String> userIds)
			throws WeixinException {
		return userApi.batchDeleteUser(userIds);
	}

	/**
	 * 邀请成员关注(管理员须拥有该成员的查看权限)
	 * 
	 * @param userId
	 *            成员ID
	 * @param tips
	 *            推送到微信上的提示语（只有认证号可以使用）。当使用微信推送时，该字段默认为“请关注XXX企业号”，邮件邀请时，该字段无效。
	 * @see com.foxinmy.weixin4j.qy.api.UserApi
	 * @return 调用结果
	 * @see <a href=
	 *      "http://qydev.weixin.qq.com/wiki/index.php?title=%E7%AE%A1%E7%90%86%E6%88%90%E5%91%98#.E9.82.80.E8.AF.B7.E6.88.90.E5.91.98.E5.85.B3.E6.B3.A8">
	 *      邀请成员关注说明</a>
	 * @throws WeixinException
	 */
	public InviteType inviteUser(String userId, String tips)
			throws WeixinException {
		return userApi.inviteUser(userId, tips);
	}

	/**
	 * 创建标签(创建的标签属于管理组;默认为未加锁状态)
	 * 
	 * @param tag
	 *            标签对象；</br> 标签名称，长度为1~64个字节，标签名不可与其他标签重名；</br> 标签id，整型，
	 *            指定此参数时新增的标签会生成对应的标签id，不指定时则以目前最大的id自增。
	 * @see <a href=
	 *      "http://qydev.weixin.qq.com/wiki/index.php?title=%E7%AE%A1%E7%90%86%E6%A0%87%E7%AD%BE#.E5.88.9B.E5.BB.BA.E6.A0.87.E7.AD.BE">
	 *      创建标签说明</a>
	 * @see com.foxinmy.weixin4j.qy.api.TagApi
	 * @return 标签ID
	 * @throws WeixinException
	 */
	public int createTag(Tag tag) throws WeixinException {
		return tagApi.createTag(tag);
	}

	/**
	 * 更新标签(管理组必须是指定标签的创建者)
	 * 
	 * @param tag
	 *            标签信息
	 * @see <a href=
	 *      "http://qydev.weixin.qq.com/wiki/index.php?title=%E7%AE%A1%E7%90%86%E6%A0%87%E7%AD%BE#.E6.9B.B4.E6.96.B0.E6.A0.87.E7.AD.BE.E5.90.8D.E5.AD.97"
	 *      >更新标签说明</a>
	 * @see com.foxinmy.weixin4j.qy.model.Tag
	 * @see com.foxinmy.weixin4j.qy.api.TagApi
	 * @return 处理结果
	 * @throws WeixinException
	 */
	public JsonResult updateTag(Tag tag) throws WeixinException {
		return tagApi.updateTag(tag);
	}

	/**
	 * 删除标签(管理组必须是指定标签的创建者 并且标签的成员列表为空)
	 * 
	 * @param tagId
	 *            标签ID
	 * @return 处理结果
	 * @see <a href=
	 *      "http://qydev.weixin.qq.com/wiki/index.php?title=%E7%AE%A1%E7%90%86%E6%A0%87%E7%AD%BE#.E5.88.A0.E9.99.A4.E6.A0.87.E7.AD.BE">
	 *      删除标签说明</a>
	 * @see com.foxinmy.weixin4j.qy.api.TagApi
	 * @throws WeixinException
	 */
	public JsonResult deleteTag(int tagId) throws WeixinException {
		return tagApi.deleteTag(tagId);
	}

	/**
	 * 获取标签列表
	 * 
	 * @see <a href=
	 *      "http://qydev.weixin.qq.com/wiki/index.php?title=%E7%AE%A1%E7%90%86%E6%A0%87%E7%AD%BE#.E8.8E.B7.E5.8F.96.E6.A0.87.E7.AD.BE.E5.88.97.E8.A1.A8">
	 *      获取标签列表说明</a>
	 * @see com.foxinmy.weixin4j.qy.model.Tag
	 * @see com.foxinmy.weixin4j.qy.api.TagApi
	 * @return 标签列表
	 * @throws WeixinException
	 */
	public List<Tag> listTag() throws WeixinException {
		return tagApi.listTag();
	}

	/**
	 * 获取标签成员(管理组须拥有“获取标签成员”的接口权限，标签须对管理组可见；返回列表仅包含管理组管辖范围的成员)
	 * 
	 * @param tagId
	 *            标签ID
	 * @see com.foxinmy.weixin4j.qy.model.User
	 * @see <a href=
	 *      "http://qydev.weixin.qq.com/wiki/index.php?title=%E7%AE%A1%E7%90%86%E6%A0%87%E7%AD%BE#.E8.8E.B7.E5.8F.96.E6.A0.87.E7.AD.BE.E6.88.90.E5.91.98">
	 *      获取标签成员说明</a>
	 * @see com.foxinmy.weixin4j.qy.api.TagApi
	 * @return 成员列表<font color="red">Contacts#getUsers</font>和部门列表<font
	 *         color="red">Contacts#getPartyIds</font>
	 * @throws WeixinException
	 */
	public Contacts getTagUsers(int tagId) throws WeixinException {
		return tagApi.getTagUsers(tagId);
	}

	/**
	 * 新增标签成员(标签对管理组可见且未加锁，成员属于管理组管辖范围)<br>
	 * <font color="red">若部分userid非法，则在text中体现</font>
	 * 
	 * @param tagId
	 *            标签ID
	 * @param userIds
	 *            企业成员ID列表，注意：userlist、partylist不能同时为空
	 * @param partyIds
	 *            企业部门ID列表，注意：userlist、partylist不能同时为空
	 * @see <a href=
	 *      "http://qydev.weixin.qq.com/wiki/index.php?title=%E7%AE%A1%E7%90%86%E6%A0%87%E7%AD%BE#.E5.A2.9E.E5.8A.A0.E6.A0.87.E7.AD.BE.E6.88.90.E5.91.98">
	 *      新增标签成员说明</a>
	 * @see com.foxinmy.weixin4j.qy.api.TagApi
	 * @see com.foxinmy.weixin4j.qy.model.IdParameter
	 * @return 处理结果
	 * @throws WeixinException
	 */
	public IdParameter addTagUsers(int tagId, List<String> userIds,
			List<Integer> partyIds) throws WeixinException {
		return tagApi.addTagUsers(tagId, userIds, partyIds);
	}

	/**
	 * 删除标签成员(标签对管理组可见且未加锁，成员属于管理组管辖范围)<br>
	 * <font color="red">若部分userid非法，则在text中体现</font>
	 * 
	 * @param tagId
	 *            标签ID
	 * @param userIds
	 *            企业成员ID列表，注意：userlist、partylist不能同时为空
	 * @param partyIds
	 *            企业部门ID列表，注意：userlist、partylist不能同时为空
	 * @see <a href=
	 *      "http://qydev.weixin.qq.com/wiki/index.php?title=%E7%AE%A1%E7%90%86%E6%A0%87%E7%AD%BE#.E5.88.A0.E9.99.A4.E6.A0.87.E7.AD.BE.E6.88.90.E5.91.98">
	 *      删除标签成员说明</a>
	 * @see com.foxinmy.weixin4j.qy.api.TagApi
	 * @see com.foxinmy.weixin4j.qy.model.IdParameter
	 * @return 处理结果
	 * @throws WeixinException
	 */
	public IdParameter deleteTagUsers(int tagId, List<String> userIds,
			List<Integer> partyIds) throws WeixinException {
		return tagApi.deleteTagUsers(tagId, userIds, partyIds);
	}

	/**
	 * 获取微信服务器IP地址
	 * 
	 * @return IP地址
	 * @see com.foxinmy.weixin4j.qy.api.HelperApi
	 * @see <a href=
	 *      "http://qydev.weixin.qq.com/wiki/index.php?title=%E5%9B%9E%E8%B0%83%E6%A8%A1%E5%BC%8F#.E8.8E.B7.E5.8F.96.E5.BE.AE.E4.BF.A1.E6.9C.8D.E5.8A.A1.E5.99.A8.E7.9A.84ip.E6.AE.B5">
	 *      获取IP地址</a>
	 * @throws WeixinException
	 */
	public List<String> getCallbackip() throws WeixinException {
		return helperApi.getCallbackip();
	}

	/**
	 * 获取企业号某个应用的基本信息，包括头像、昵称、帐号类型、认证类型、可见范围等信息
	 * 
	 * @param agentid
	 *            授权方应用id
	 * @return 应用信息
	 * @see com.foxinmy.weixin4j.qy.model.AgentInfo
	 * @see com.foxinmy.weixin4j.qy.api.AgentApi
	 * @see <a href=
	 *      "http://qydev.weixin.qq.com/wiki/index.php?title=%E8%8E%B7%E5%8F%96%E4%BC%81%E4%B8%9A%E5%8F%B7%E5%BA%94%E7%94%A8">
	 *      企业号应用的信息</a>
	 * @throws WeixinException
	 */
	public AgentInfo getAgent(int agentid) throws WeixinException {
		return agentApi.getAgent(agentid);
	}

	/**
	 * 设置企业应用的选项设置信息，如：地理位置上报等
	 * 
	 * @param agentSet
	 *            设置参数
	 * @see com.foxinmy.weixin4j.qy.model.AgentSetter
	 * @see com.foxinmy.weixin4j.qy.api.AgentApi
	 * @see <a href=
	 *      "http://qydev.weixin.qq.com/wiki/index.php?title=%E8%AE%BE%E7%BD%AE%E4%BC%81%E4%B8%9A%E5%8F%B7%E5%BA%94%E7%94%A8">
	 *      设置企业号信息</a>
	 * @return 处理结果
	 * @throws WeixinException
	 */
	public JsonResult setAgent(AgentSetter agentSet) throws WeixinException {
		return agentApi.setAgent(agentSet);
	}

	/**
	 * 获取应用概况列表
	 * 
	 * @see com.foxinmy.weixin4j.qy.model.AgentOverview
	 * @see com.foxinmy.weixin4j.qy.api.AgentApi
	 * @see <a href=
	 *      "http://qydev.weixin.qq.com/wiki/index.php?title=%E8%8E%B7%E5%8F%96%E5%BA%94%E7%94%A8%E6%A6%82%E5%86%B5%E5%88%97%E8%A1%A8">
	 *      获取应用概况</a>
	 * @return 应用概况列表
	 * @throws WeixinException
	 */
	public List<AgentOverview> listAgentOverview() throws WeixinException {
		return agentApi.listAgentOverview();
	}

	/**
	 * 批量邀请成员关注
	 * 
	 * @param parameter
	 *            成员ID,标签ID,部门ID
	 * @param callback
	 *            接收任务执行结果的回调地址等信息
	 * @param tips
	 *            推送到微信上的提示语（只有认证号可以使用）。当使用微信推送时，该字段默认为“请关注XXX企业号”，邮件邀请时，该字段无效。
	 * @return 异步任务id，最大长度为64字符
	 * @see com.foxinmy.weixin4j.qy.model.IdParameter
	 * @see com.foxinmy.weixin4j.qy.model.Callback
	 * @see com.foxinmy.weixin4j.qy.api.BatchApi
	 * @see <a href=
	 *      "http://qydev.weixin.qq.com/wiki/index.php?title=%E5%BC%82%E6%AD%A5%E4%BB%BB%E5%8A%A1%E6%8E%A5%E5%8F%A3#.E9.82.80.E8.AF.B7.E6.88.90.E5.91.98.E5.85.B3.E6.B3.A8">
	 *      邀请成员关注</a>
	 * @throws WeixinException
	 */
	public String batchInviteUser(IdParameter parameter, Callback callback,
			String tips) throws WeixinException {
		return batchApi.inviteUser(parameter, callback, tips);
	}

	/**
	 * 批量更新成员,本接口以userid为主键，增量更新企业号通讯录成员。
	 * <p>
	 * 1.模板中的部门需填写部门ID，多个部门用分号分隔，部门ID必须为数字</br>
	 * 2.文件中存在、通讯录中也存在的成员，更新成员在文件中指定的字段值 </br> 3.文件中存在、通讯录中不存在的成员，执行添加操作</br>
	 * 4.通讯录中存在、文件中不存在的成员，保持不变</br>
	 * </p>
	 * 
	 * @param mediaId
	 *            带user信息的cvs文件上传后的media_id
	 * @param callback
	 *            接收任务执行结果的回调地址等信息
	 * @return 异步任务id，最大长度为64字符
	 * @see com.foxinmy.weixin4j.qy.model.Callback
	 * @see com.foxinmy.weixin4j.qy.api.BatchApi
	 * @see <a href=
	 *      "http://qydev.weixin.qq.com/wiki/index.php?title=%E5%BC%82%E6%AD%A5%E4%BB%BB%E5%8A%A1%E6%8E%A5%E5%8F%A3#.E5.A2.9E.E9.87.8F.E6.9B.B4.E6.96.B0.E6.88.90.E5.91.98">
	 *      批量更新成员</a>
	 * @throws WeixinException
	 */
	public String batchSyncUser(String mediaId, Callback callback)
			throws WeixinException {
		return batchApi.syncUser(mediaId, callback);
	}

	/**
	 * 批量覆盖成员,本接口以userid为主键，全量覆盖企业号通讯录成员，任务完成后企业号通讯录成员与提交的文件完全保持一致。
	 * <p>
	 * 1.模板中的部门需填写部门ID，多个部门用分号分隔，部门ID必须为数字</br> 2.文件中存在、通讯录中也存在的成员，完全以文件为准</br>
	 * 3.文件中存在、通讯录中不存在的成员，执行添加操作</br>
	 * 4.通讯录中存在、文件中不存在的成员，执行删除操作。出于安全考虑，如果需要删除的成员多于50人，
	 * 且多于现有人数的20%以上，系统将中止导入并返回相应的错误码
	 * </p>
	 * 
	 * @param mediaId
	 *            带userid信息的cvs文件上传后的media_id
	 * @param callback
	 *            接收任务执行结果的回调地址等信息
	 * @return 异步任务id，最大长度为64字符
	 * @see com.foxinmy.weixin4j.qy.model.Callback
	 * @see com.foxinmy.weixin4j.qy.api.BatchApi
	 * @see <a href=
	 *      "http://qydev.weixin.qq.com/wiki/index.php?title=%E5%BC%82%E6%AD%A5%E4%BB%BB%E5%8A%A1%E6%8E%A5%E5%8F%A3#.E5.85.A8.E9.87.8F.E8.A6.86.E7.9B.96.E6.88.90.E5.91.98">
	 *      批量覆盖成员</a>
	 * @throws WeixinException
	 */
	public String batchReplaceUser(String mediaId, Callback callback)
			throws WeixinException {
		return batchApi.replaceUser(mediaId, callback);
	}

	/**
	 * 批量上传成员
	 * 
	 * @param users
	 *            成员列表
	 * @see com.foxinmy.weixin4j.qy.api.MediaApi
	 * @see com.foxinmy.weixin4j.qy.api.BatchApi
	 * @see {@link #batchSyncUser(String,Callback)}
	 * @see {@link #batchReplaceUser(String,Callback)}
	 * @see <a href=
	 *      "http://qydev.weixin.qq.com/wiki/index.php?title=%E5%BC%82%E6%AD%A5%E4%BB%BB%E5%8A%A1%E6%8E%A5%E5%8F%A3#.E9.80.9A.E8.AE.AF.E5.BD.95.E6.9B.B4.E6.96.B0">
	 *      批量任务</a>
	 * @return 上传后的mediaId
	 * @throws WeixinException
	 */
	public String batchUploadUsers(List<User> users) throws WeixinException {
		return mediaApi.batchUploadUsers(users);
	}

	/**
	 * 批量覆盖部门,本接口以partyid为键，全量覆盖企业号通讯录组织架构，任务完成后企业号通讯录组织架构与提交的文件完全保持一致。
	 * <p>
	 * 1.文件中存在、通讯录中也存在的部门，执行修改操作</br> 2.文件中存在、通讯录中不存在的部门，执行添加操作</br>
	 * 3.文件中不存在、通讯录中存在的部门，当部门为空时，执行删除操作</br>
	 * 4.CSV文件中，部门名称、部门ID、父部门ID为必填字段，部门ID必须为数字；排序为可选字段，置空或填0不修改排序
	 * </p>
	 * 
	 * @param mediaId
	 *            带partyid信息的cvs文件上传后的media_id
	 * @param callback
	 *            接收任务执行结果的回调地址等信息
	 * @return 异步任务id，最大长度为64字符
	 * @see com.foxinmy.weixin4j.qy.model.Callback
	 * @see com.foxinmy.weixin4j.qy.api.BatchApi
	 * @see <a href=
	 *      "http://qydev.weixin.qq.com/wiki/index.php?title=%E5%BC%82%E6%AD%A5%E4%BB%BB%E5%8A%A1%E6%8E%A5%E5%8F%A3#.E5.85.A8.E9.87.8F.E8.A6.86.E7.9B.96.E9.83.A8.E9.97.A8">
	 *      批量覆盖部门</a>
	 * @throws WeixinException
	 */
	public String batchReplaceParty(String mediaId, Callback callback)
			throws WeixinException {
		return batchApi.replaceParty(mediaId, callback);
	}

	/**
	 * 获取异步任务执行的结果
	 * 
	 * @param jobId
	 *            任务ID
	 * @return 效果信息
	 * @see com.foxinmy.weixin4j.qy.model.BatchResult
	 * @see com.foxinmy.weixin4j.qy.api.BatchApi
	 * @see <a href=
	 *      "http://qydev.weixin.qq.com/wiki/index.php?title=%E5%BC%82%E6%AD%A5%E4%BB%BB%E5%8A%A1%E6%8E%A5%E5%8F%A3#.E8.8E.B7.E5.8F.96.E5.BC.82.E6.AD.A5.E4.BB.BB.E5.8A.A1.E7.BB.93.E6.9E.9C">
	 *      获取异步任务执行结果</a>
	 * @throws WeixinException
	 */
	public BatchResult getBatchResult(String jobId) throws WeixinException {
		return batchApi.getBatchResult(jobId);
	}

	/**
	 * userid转换成openid:该接口使用场景为微信支付、微信红包和企业转账，企业号用户在使用微信支付的功能时，
	 * 需要自行将企业号的userid转成openid。 在使用微信红包功能时，需要将应用id和userid转成appid和openid才能使用。
	 * 
	 * @param userid
	 *            企业号内的成员id 必填
	 * @param agentid
	 *            需要发送红包的应用ID，若只是使用微信支付和企业转账，则无需该参数 传入0或负数则忽略
	 * @return 结果数组 第一个元素为对应的openid 第二个元素则为应用的appid(如果有)
	 * @throws WeixinException
	 * @see com.foxinmy.weixin4j.qy.api.UserApi
	 * @see <a href=
	 *      "http://qydev.weixin.qq.com/wiki/index.php?title=Userid%E4%B8%8Eopenid%E4%BA%92%E6%8D%A2%E6%8E%A5%E5%8F%A3">
	 *      userid转换成openid</a>
	 */
	public String[] userid2openid(String userid, int agentid)
			throws WeixinException {
		return userApi.userid2openid(userid, agentid);
	}

	/**
	 * openid转换成userid:该接口主要应用于使用微信支付、微信红包和企业转账之后的结果查询，
	 * 开发者需要知道某个结果事件的openid对应企业号内成员的信息时，可以通过调用该接口进行转换查询。
	 * 
	 * @param openid
	 *            在使用微信支付、微信红包和企业转账之后，返回结果的openid
	 * @return 该openid在企业号中对应的成员userid
	 * @throws WeixinException
	 * @see com.foxinmy.weixin4j.qy.api.UserApi
	 * @see <a href=
	 *      "http://qydev.weixin.qq.com/wiki/index.php?title=Userid%E4%B8%8Eopenid%E4%BA%92%E6%8D%A2%E6%8E%A5%E5%8F%A3">
	 *      openid转换成userid</a>
	 */
	public String openid2userid(String openid) throws WeixinException {
		return userApi.openid2userid(openid);
	}

	/**
	 * 创建会话 <font color="red">如果会话id为空,程序会自动生成一个唯一ID</font>
	 * 
	 * @param chatInfo
	 *            会话信息
	 * @return 会话ID
	 * @see com.foxinmy.weixin4j.qy.api.ChatApi
	 * @see com.foxinmy.weixin4j.qy.model.ChatInfo
	 * @see <a href=
	 *      "http://qydev.weixin.qq.com/wiki/index.php?title=%E4%BC%81%E4%B8%9A%E5%8F%B7%E6%B6%88%E6%81%AF%E6%8E%A5%E5%8F%A3%E8%AF%B4%E6%98%8E#.E5.88.9B.E5.BB.BA.E4.BC.9A.E8.AF.9D">
	 *      创建会话</a>
	 * @throws WeixinException
	 */
	public String createChat(ChatInfo chatInfo) throws WeixinException {
		return chatApi.createChat(chatInfo);
	}

	/**
	 * 获取会话
	 * 
	 * @param chatId
	 *            会话ID
	 * @return 会话信息
	 * @see com.foxinmy.weixin4j.qy.api.ChatApi
	 * @see com.foxinmy.weixin4j.qy.model.ChatInfo
	 * @see <a href=
	 *      "http://qydev.weixin.qq.com/wiki/index.php?title=%E4%BC%81%E4%B8%9A%E5%8F%B7%E6%B6%88%E6%81%AF%E6%8E%A5%E5%8F%A3%E8%AF%B4%E6%98%8E#.E8.8E.B7.E5.8F.96.E4.BC.9A.E8.AF.9D">
	 *      获取会话</a>
	 * @throws WeixinException
	 */
	public ChatInfo getChat(String chatId) throws WeixinException {
		return chatApi.getChat(chatId);
	}

	/**
	 * 更新会话
	 * 
	 * @param chatInfo
	 *            会话信息 至少保持会话ID不能为空
	 * @param operator
	 *            操作人userid
	 * @param addUsers
	 *            会话新增成员列表
	 * @param deleteUsers
	 *            会话退出成员列表
	 * @return 处理结果
	 * @see com.foxinmy.weixin4j.qy.api.ChatApi
	 * @see com.foxinmy.weixin4j.qy.model.ChatInfo
	 * @see <a href=
	 *      "http://qydev.weixin.qq.com/wiki/index.php?title=%E4%BC%81%E4%B8%9A%E5%8F%B7%E6%B6%88%E6%81%AF%E6%8E%A5%E5%8F%A3%E8%AF%B4%E6%98%8E#.E4.BF.AE.E6.94.B9.E4.BC.9A.E8.AF.9D.E4.BF.A1.E6.81.AF">
	 *      修改会话信息</a>
	 * @throws WeixinException
	 */
	public JsonResult updateChat(ChatInfo chatInfo, String operator,
			List<String> addUsers, List<String> deleteUsers)
			throws WeixinException {
		return chatApi.updateChat(chatInfo, operator, addUsers, deleteUsers);
	}

	/**
	 * 退出会话
	 * 
	 * @param chatId
	 *            会话ID
	 * @param operator
	 *            操作人userid
	 * @return 处理结果
	 * @see com.foxinmy.weixin4j.qy.api.ChatApi
	 * @see <a href=
	 *      "http://qydev.weixin.qq.com/wiki/index.php?title=%E4%BC%81%E4%B8%9A%E5%8F%B7%E6%B6%88%E6%81%AF%E6%8E%A5%E5%8F%A3%E8%AF%B4%E6%98%8E#.E9.80.80.E5.87.BA.E4.BC.9A.E8.AF.9D">
	 *      退出会话</a>
	 * @throws WeixinException
	 */
	public JsonResult quitChat(String chatId, String operator)
			throws WeixinException {
		return chatApi.quitChat(chatId, operator);
	}

	/**
	 * 清除会话未读状态
	 * 
	 * @param targetId
	 *            会话值，为userid|chatid，分别表示：成员id|会话id
	 * @param owner
	 *            会话所有者的userid
	 * @param chatType
	 *            会话类型：single|group，分别表示：群聊|单聊
	 * @return 处理结果
	 * @see com.foxinmy.weixin4j.qy.api.ChatApi
	 * @see <a href=
	 *      "http://qydev.weixin.qq.com/wiki/index.php?title=%E4%BC%81%E4%B8%9A%E5%8F%B7%E6%B6%88%E6%81%AF%E6%8E%A5%E5%8F%A3%E8%AF%B4%E6%98%8E#.E6.B8.85.E9.99.A4.E4.BC.9A.E8.AF.9D.E6.9C.AA.E8.AF.BB.E7.8A.B6.E6.80.81">
	 *      清除会话未读状态</a>
	 * @throws WeixinException
	 */
	public JsonResult clearChatNotify(String targetId, String owner,
			ChatType chatType) throws WeixinException {
		return chatApi.clearChatNotify(targetId, owner, chatType);
	}

	/**
	 * 设置成员接收到的消息是否提醒。主要场景是用于对接企业im的在线状态，如成员处于在线状态时，可以设置该成员的消息免打扰。当成员离线时，关闭免打扰状态
	 * ，对微信端进行提醒。
	 * 
	 * @param chatMutes
	 *            提醒参数
	 * @see com.foxinmy.weixin4j.qy.api.ChatApi
	 * @see com.foxinmy.weixin4j.qy.model.ChatMute
	 * @see <a href=
	 *      "http://qydev.weixin.qq.com/wiki/index.php?title=%E4%BC%81%E4%B8%9A%E5%8F%B7%E6%B6%88%E6%81%AF%E6%8E%A5%E5%8F%A3%E8%AF%B4%E6%98%8E#.E8.AE.BE.E7.BD.AE.E6.88.90.E5.91.98.E6.96.B0.E6.B6.88.E6.81.AF.E5.85.8D.E6.89.93.E6.89.B0"
	 *      >设置成员新消息免打扰</a>
	 * @return 列表中不存在的成员，剩余合法成员会继续执行。
	 * @throws WeixinException
	 */
	public List<String> setChatMute(List<ChatMute> chatMutes)
			throws WeixinException {
		return chatApi.setChatMute(chatMutes);
	}

	/**
	 * 发送会话消息
	 * 
	 * @param message
	 *            消息对象
	 * @return 处理结果
	 * @see com.foxinmy.weixin4j.qy.api.ChatApi
	 * @see com.foxinmy.weixin4j.qy.message.ChatMessage
	 * @see <a href=
	 *      "http://qydev.weixin.qq.com/wiki/index.php?title=%E4%BC%81%E4%B8%9A%E5%8F%B7%E6%B6%88%E6%81%AF%E6%8E%A5%E5%8F%A3%E8%AF%B4%E6%98%8E#.E5.8F.91.E6.B6.88.E6.81.AF">
	 *      发送消息</a>
	 * @throws WeixinException
	 */
	public JsonResult sendChatMessage(ChatMessage message)
			throws WeixinException {
		return chatApi.sendChatMessage(message);
	}

	public final static String VERSION = "1.6.7";
}
