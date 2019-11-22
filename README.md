# Android快速开发工具

### XCActivityUtil（Activity栈管理工具）
#### 01.获取单例

   getInstance()

   @return XCActivityUtil实例
  
#### 02.获取Activity栈

   getActivityStack()

   @return Activity栈集合

#### 03.添加Activity到堆栈

   addActivity(Activity activity)

   @param activity

#### 04.获取当前Activity（堆栈中最后一个压入的）

   getActivity()

   @return activity
   
#### 05.结束当前Activity（堆栈中最后一个压入的）

   finishActivity()

#### 06.结束指定Activity

   finishActivity(Activity activity)

   @param activity

#### 07.结束指定Activity

   finishActivity(Class<?> activity)

   @param activity 栈内需结束的activity类

#### 08.结束指定Activity

   finishActivity(Class<?>[] activites)

   @param activites 多个栈内需结束的activity类

#### 09.保留指定Activity结束其他

   finishElseActivity(Class<?> activity)

   @param activity
 栈内需保留的activity类

#### 10.保留指定Activity结束其他

   finishElseActivity(Class<?>[] activites)

   @param activites 多个栈内需保留的activity类

#### 11.结束所有Activity

   finishAllActivity()

#### 12.退出应用程序

   AppExit(Context context)

### XCAppUtil（应用相关工具类）
#### 01.显示输入法

   showInputKeyboard(Context context)

   @param context 上下文

#### 02.隐藏输入法

   hideInputKeyboard(Context context)

   @param context 上下文

#### 03.获取应用名称

   getAppName(Context context)

   @param context 上下文

   @return 应用名称

#### 04.获取应用版本号

   getAppVersion(Context context)

   @param context 上下文

   @return 版本号

#### 05.判断指定软件是否安装

   isAppInstalled(Context context, String packageName)

   @param context 上下文

   @param packageName 应用包名

   @return 是否安装

#### 06.判断指定应用是否启动

   isAppRunning(Context context, String packageName)

   @param context 上下文

   @param packageName 应用包名

	 
   @return 是否启动

#### 07.判断指定服务是否启动

   isServiceRunning(Context context, String serviceName)

   @param context 上下文

   @param serviceName 服务名
	
 
   @return 是否启动

#### 08.打开其他app

   setAppIntent(Context context, String packageName, String activityName)

   @param context 上下文
	 

   @param packageName 目标APP包名
	

   @param activityName 目标APP的目标Activity名

#### 09.获取所有APP信息

   getAllAppInfo(Context context)

   @param context 上下文

	 
   @return APP信息集合

#### 10.获取指定app信息

   getAppInfo(Context context, String appName)

   @param context 上下文
	

   @param appName APP名称
	

   @return APP信息

#### 11.获取AndroidManifest.xml中,Application下的<meta-data>元素值

   getMetaDataValue(Context context, String metaDataName)

   @param context 上下文


   @param metaDataName <meta-data>key值

   @return <meta-data>value值

### XCBeanUtil（实体类工具类）

#### 01.获取bean成员变量名集合

   getFieldNameList(Class<?> objectClass)

   @param classBean 实体类

   @return 变量名集合

#### 02.获取get方法

   getGetMethod(Class<?> objectClass, String fieldName)

   @param objectClass 实体类

   @param fieldName 变量名(驼峰)

   @return get方法对象

#### 03.获取set方法

   getSetMethod(Class<?> objectClass, String fieldName)

   @param objectClass 实体类

   @param fieldName 变量名(驼峰)

   @return set方法对象

#### 04.调用对应成员变量的get方法

   invokeGetMethod(Object classObject, String fieldName)

   @param classObject 实体类对象

   @param fieldName 变量名(驼峰)

   @return 是否成功

#### 05.调用对应成员变量的set方法

   invokeSetMethod(Object classObject, String fieldName, Object setValue)

   @param classObject 实体类对象

   @param fieldName 变量名(驼峰)

   @param setValue 设置值

   @return 是否成功

### XCBitmapUtil（图片异步加载工具）

#### 01.显示view图片

   display(Context context, T container, String url)

   @param context 上下文

   @param container 容器

   @param url 地址

#### 02.显示view图片

   display(Context context, T container, String url, String diskCachePath)

   @param context 上下文

   @param container 容器

   @param url 地址

   @param diskCachePath 本地缓存路径
     
#### 03.显示view图片

   display(Context context, T container, String url, BitmapLoadCallBack<T> callBack)

   @param context 上下文

   @param container 容器

   @param url 地址

   @param callBack 加载回调

#### 04.显示view图片

   display(Context context, T container, String url, String diskCachePath, BitmapLoadCallBack<T> callBack)

   @param context 上下文

   @param container 容器

   @param url 地址

   @param diskCachePath 本地缓存路径

   @param callBack 加载回调

#### 05.显示view图片

   display(Context context, T container, String url, String diskCachePath, BitmapDisplayConfig displayConfig)

   @param context 上下文

   @param container 容器

   @param url 地址

   @param diskCachePath 本地缓存路径

   @param displayConfig 显示配置
	
#### 06.显示view图片

   display(Context context, T container, String url, String diskCachePath, BitmapDisplayConfig displayConfig, BitmapLoadCallBack<T> callBack)

   @param context 上下文

   @param container 容器

   @param url 地址

   @param diskCachePath 本地缓存路径

   @param displayConfig 显示配置 

   @param callBack 加载回调

#### 07.清除缓存

   clearCache(Context context)

   @param context 上下文
  
#### 08.清除缓存

   clearCache(Context context, String diskCachePath)

   @param context 上下文

   @param diskCachePath 本地缓存路径

#### 09.根据url地址获取Bitmap

   getBitMap(final String url)

   @param String url 地址
	

   @return bitmap对象

#### 10.根据本地路径获取Bitmap

   getBitmap(String filePath)

   @param filePath 本地路径
	

   @return bitmap对象

#### 11.根据uri获取Bitmap

   getBitmap(Context context, Uri uri)

   @param context 上下文
	

   @param uri uri对象
	

   @deprecated 默认缩略图是原图大小的1/4

	
   @return bitmap对象

#### 12.根据uri获取Bitmap

   getBitmap(Context context, Uri uri, int inSampleSize)

   @param context 上下文

	
   @param uri uri对象
	

   @param inSampleSize 缩略图缩小倍数
	

   @return bitmap对象

#### 13.将View转化为Bitmap

   getViewToBitmap(View view)

   @param view view对象
	 

   @return bitmap对象

#### 14.将Drawable转化为Bitmap

   getDrawableToBitmap(Drawable drawable)

   @param drawable对象
	 

   @return bitmap对象

#### 15.将Bitmap转化为Drawable

   getBitmapToDrawable(Bitmap bitmap)

   @param bitmap对象	 

   @return drawable对象

#### 16.将Bitmap转换为字节数组

   getBitmapToBytes(Bitmap bitmap)

   @param bitmap bitmap对象


   @return 字节数组

#### 17.将字节数组转换为Bitmap

   getBytesToBitmap(byte[] bytes)

   @param bytes 字节数组

	
   @return bitmap对象

#### 18.缩放图片

   zoomBitmap(Bitmap bitmap, int width, int height)

   @param bitmap bitmap对象
	

   @param width 宽
	

   @param height 高
	

   @return bitmap对象

#### 19.缩放图片

   zoomBitmap(Bitmap bitmap, float multiple)

   @param bitmap bitmap对象

	
   @param multiple 缩放比例

	
   @return bitmap对象

#### 20.旋转图片

   rotateBitmap(Bitmap bitmap, float degrees)

   @param bitmap bitmap对象
	

   @param degrees 角度
	

   @return bitmap对象

#### 21.根据图片路径读取图片旋转角度

   readPictureDegree(String filePath)

   @param filePath 图片路径
	

   @return 旋转角度

#### 22.保存图片到本地

   saveBitmap(Context context, Bitmap bitmap, String pictureName)

   @param context 上下文
	

   @param bitmap bitmap对象
	

   @param pictureName
 图片名

   @return 保存路径

#### 23.从URI获取本地路径

   getAbsoluteImagePath(Context context, Uri uri)

   @param context 上下文

	
   @param uri uri对象
	

   @return 本地路径

#### 24.Bitmap添加水印

   addWatermark(Bitmap src, Bitmap watermark)

   @param src 原图bitmap对象
	

   @param watermark 水印bitmap对象

	
   @return 完成bitmap对象

#### 25.获取模糊Bitmap

   getBlurBitmap(Bitmap sentBitmap, int radius)

   @param sentBitmap bitmap对象
	

   @param radius 模糊半径

	
   @return 完成bitmap对象


### XCColorUtil（颜色工具类）

#### 01.获取将16进制颜色转换为资源的颜色

   getHexToColor(String hex)

   @param hex
 例:### ffffffff


   @return 资源地址

#### 02.获取资源中的颜色

   getResourcesColor(Context context, int color)

   @param context 上下文

	
   @param color
	例:R.id.color
	
 
   @return 色值

#### 03.获取修改透明度的颜色

   getChangeAlpha(int color, int alpha)

   @param color 例:R.id.color
	
 
   @param alpha 透明度
	
 
   @return 色值


### XCDBUtil（数据库操作工具类）

#### 01.创建数据库表
   createTable(Context context, Class<?> tableClass)

   @param context 上下文
	

   @param tableClass
 以实体类名创建表名,成员变量创建字段(只支持String类型变量,相同类名不会重复创建表)
	

   @return 是否成功

#### 02.数据库表是否存在

   isTableExist(Context context, Class<?> tableClass)

   @param context 上下文


   @param tableClass
 以实体类名创建的表
	 

   @return 是否成功

#### 03.删除数据库表

   deleteTable(Context context, Class<?> tableClass)

   @param context 上下文

	
   @param tableClass
 以实体类名创建的表
	 

   @return 是否成功

#### 04.删除数据库

   deleteDB(Context context)

   @param context 上下文

	
   @return 是否成功

#### 05.插入

   insert(Context context, Object classObject)

   @param context 上下文
	

   @param classObject 类对象,操作以该对象类名创建的表,反射get方法获取插入数据,只支持String变量(完全相同的数据不会重复插入)

	 
   @return 是否成功

#### 06.删除

   delete(Context context, Object classObject)

   @param context 上下文
	

   @param classObject 类对象,操作以该对象类名创建的表,反射get方法获取删除条件(条件唯一删除唯一一条数据,条件不唯一删除符合条件的所有数据,
new空对象删除该表所有数据 ) 
  
   @return 是否成功

#### 07.更新

   update(Context context, Object updateObject, Object conditionObject)

   @param context 上下文
	

   @param updateObject
 更新数据类对象,反射get方法获取更新数据(要与查询条件类对象为相同类的对象)
	
  
   @param conditionObject
 查询条件类对象,操作以该对象类名创建的表,反射get方法获取查询条件(条件唯一更新唯一一条数据,
条件不唯一更新符合条件的所有数据, new空对象更新该表所有数据)

   
   @return 是否成功		

#### 08.查询

   query(Context context, T classObject)

   @param context 上下文

	
   @param classObject 类对象,操作以该对象类名创建的表,反射get方法获取查询条件(条件唯一返回唯一一条数据,条件不唯一返回符合条件的所有数据,new空对象查询该表所有数据 )
	
  
   @return 结果集

#### 09.是否存在

   isExist(Context context, T classObject)

   @param context 上下文
	

   @param classObject 类对象,操作以该对象类名创建的表,反射get方法获取查询条件(有符合条件的就返回true)
	

   @return 是否存在

### XCFileUtil（文件工具类）

#### 01.SDCard是否存在

   isSDCardExist()

   @return 是否存在

#### 02.获取SDCard根目录

   getSDCardRootDir()

   @return 例:/storage/emulated/#### 0

#### 03.获取手机缓存路径

   getDiskCacheDir(Context context)
  
   @return 不可管理,卸载不会被删.例:
/storage/emulated/#### 0/Android/data/com.xc.sample/cache或
可管理,卸载会被删.例: /data/data/com.xc.sample/cache

#### 04.获取下载文件名
   getDownloadFileName(String downloadUrl)

   @param downloadUrl 下载地址
	

   @return 文件名

#### 05.获取一个已时间命名的文件名

   getFileName(String suffix)

   @param suffix
 文件后缀名

	
   @return 文件名

### XCGlobalExceptionUtil（全局异常捕获）

#### 01.获取单例

   getInstance()

   @return XCGlobalExceptionUtil实例

#### 02.异常监听

   setOnGlobalExceptionListener(OnGlobalExceptionListener globalException)

   @param globalException 实现接口   

### XCHttpUtil（网络请求工具类）

#### 01.通过URL方式请求数据,根据不同的参数类型发送请求get或post(同时请求get和post参时采用post请求,get参拼在url后)

   sendRequest(Context context, HttpParam param, int what, final RequestCallback requestCallback)

   @param context 上下文
	

   @param params 参数
	

   @param what 常量

	
   @param requestCallback 请求回调
	

   @Description ThreadManager提供线程终止

#### 02.通过URL方式请求数据,根据不同的参数类型发送请求get或post(同时请求get和post参时采用post请求,get参拼在url后)

   sendRequest(Context context, HttpParam param, int what, final RequestCallback requestCallback, boolean isShowLog)

   @param context 上下文

	
   @param params 参数

	
   @param what 常量

	
   @param requestCallback 请求回调
	

   @param isShowLog 是否打印Log tagName：HttpThread

   @Description ThreadManager提供线程终止

#### 03.通过URL方式请求数据,根据不同的参数类型发送请求get或post(同时请求get和post参时采用post请求,get参拼在url后)

   sendRequest(final Context context, HttpParam param, final int requestCode, final RequestCallback requestCallback, ConnectionConfig config, boolean isShowLog)
   
   @param context 上下文

	
   @param params 参数
	

   @param what 常量
	

   @param requestCallback 请求回调
	

   @param config 连接配置

   @param isShowLog 是否打印Log tagName：HttpThread

   @Description ThreadManager提供线程终止

#### 04.通过post上传文件(支持多个同时上传)

   uploadFile(Context context, HttpParam param, final UploadCallBack uploadCallBack)

   @param context 上下文

	
   @param params 参数
	
	
   @param uploadCallBack 上传回调
	

   @Description ThreadManager提供线程终止

#### 05.通过post上传文件(支持多个同时上传)

   uploadFile(Context context, HttpParam param, final UploadCallBack uploadCallBack, boolean isShowLog)

   @param context 上下文

	
   @param params 参数
	

   @param uploadCallBack 上传回调
	

   @param isShowLog 是否打印Log tagName：HttpThread


   @Description ThreadManager提供线程终止

#### 06.通过post上传文件(支持多个同时上传)

   uploadFile(Context context, HttpParam param, final UploadCallBack uploadCallBack, ConnectionConfig config, boolean isShowLog)

   @param context 上下文

	
   @param params 参数
	

   @param uploadCallBack 上传回调


   @param config 连接配置	

   @param isShowLog 是否打印Log tagName：HttpThread


   @Description ThreadManager提供线程终止
  
#### 07.通过get下载文件(只支持单个下载)

   downloadFile(Context context, HttpParam param, final DownloadCallBack downloadCallBack)

   @param context 上下文
	

   @param params 参数
	

   @param downloadCallBack 下载回调
  

   @Description ThreadManager提供线程终止

#### 08.通过get下载文件(只支持单个下载)

   downloadFile(Context context, HttpParam param, final DownloadCallBack downloadCallBack, boolean isShowLog)

   @param context 上下文
	

   @param params 参数
	

   @param downloadCallBack 下载回调
  

   @param isShowLog 是否打印Log tagName：HttpThread

   @Description ThreadManager提供线程终止


#### 09.通过get下载文件(只支持单个下载)

   downloadFile(Context context, HttpParam param, final DownloadCallBack downloadCallBack, ConnectionConfig config, boolean isShowLog)

   @param context 上下文

	
   @param params 参数
	

   @param downloadCallBack 下载回调
  

   @param config 连接配置

   @param isShowLog 是否打印Log tagName：HttpThread

   @Description ThreadManager提供线程终止

### XCIOUtil（IO工具类）

#### 01.关闭

   close(Closeable closeable)

   @param closeable closeable对象

#### 02.关闭

   close(Cursor cursor)

   @param cursor cursor对象

### XCJsonUtil（json解析工具类-bean变量名需与json字段名相同）

#### 01.解析单独字段

   parseField(String jsonResult, String fieldName)

   @param jsonResult 返回结果

   @param fieldName 字段名

   @return 字段值

#### 02.解析单独字段

   parseField(String jsonResult, String jsonObjectName, String fieldName)

   @param jsonResult json结果

   @param fieldName 字段名

   @param jsonObjectName jsonObject节点名

   @return 字段值

#### 03.解析单独字段

   parseField(JSONObject jsonObject, String fieldName)

   @param jsonObject jsonObject对象

   @param fieldName 字段名

   @return 字段值

#### 04.解析单独字段

   parseField(JSONObject jsonObject, String jsonObjectName, String fieldName)

   @param jsonObject jsonObject对象

   @param fieldName 字段名

   @param jsonObjectName jsonObject节点名

   @return 字段值

#### 05.解析bean

   parseBean(String jsonResult, Class<T> objectClass)

   @param jsonResult json结果

   @param objectClass 实体类

   @return 结果bean

#### 06.解析bean

   parseBean(String jsonResult, String jsonObjectName, Class<T> objectClass)

   @param jsonResult json结果

   @param jsonObjectName jsonObject节点名

   @param objectClass 实体类

   @return 结果bean

#### 07.解析bean

   parseBean(JSONObject jsonObject, Class<T> objectClass)

   @param jsonObject JsonObject对象

   @param objectClass 实体类

   @return 结果bean

#### 08.解析bean

   parseBean(JSONObject jsonObject, String jsonObjectName, Class<T> objectClass)

   @param jsonObject jsonObject对象

   @param jsonObjectName jsonObject节点名

   @param objectClass 实体类

   @return 结果bean

#### 09.解析ListBean

   parseListBean(String jsonResult, String jsonArrayName, Class<T> objectClass)

   @param jsonResult json结果

   @param jsonArrayName jsonArray节点名

   @param objectClass 实体类

   @return 结果集

#### 10.解析ListBean

   parseListBean(JSONObject jsonObject, String jsonArrayName, Class<T> objectClass)

   @param jsonObject jsonObject对象

   @param jsonArrayName jsonArray节点名

   @param objectClass 实体类

   @return 结果集

#### 11.解析循环ListBean

   parseListBean(String jsonResult, String jsonArrayName, String loopsListName, Class<T> objectClass)

   @param jsonResult json结果

   @param jsonArrayName jsonArray节点名(各个子级数组名必须相同)

   @param loopsListName 循环解析的List容器变量名

   @param objectClass 实体类

   @return 结果集 

#### 12.解析循环ListBea

   parseListBean(String jsonResult, String[] jsonArrayName, String loopsListName, Class<T> objectClass)

   @param jsonResult json结果

   @param jsonArrayName jsonArray节点名(各个子级数组名)

   @param loopsListName 循环解析的List容器变量名

   @param objectClass 实体类

   @return 结果集 

#### 13.解析循环ListBean

   parseListBean(JSONObject jsonObject, String[] jsonArrayName, String loopsListName, Class<T> objectClass)

   @param jsonObjectjson jsonObject对象

   @param jsonArrayName jsonArray节点名(各个子级数组名)

   @param loopsListName 循环解析的List容器变量名

   @param objectClass 实体类

   @return 结果集 

#### 14.解析循环ListBean

   parseListBean(JSONObject jsonObject, String[] jsonArrayName, String loopsListName, int position, Class<T> objectClass)

   @param jsonObjectjson jsonObject对象

   @param jsonArrayName jsonArray节点名(各个子级数组名)

   @param loopsListName 循环解析的List容器变量名

   @param objectClass 实体类

   @param position 递归变量,必须传#### 0

   @return 结果集 

#### 15.解析循环ListBean

   parseListBean(JSONObject jsonObject, String jsonArrayName, String loopsListName, Class<T> objectClass) 

   @param jsonObjectjson jsonObject对象

   @param jsonArrayName jsonArray节点名(各个子级数组名必须相同)

   @param loopsListName 循环解析的List容器变量名

   @param objectClass 实体类

   @return 结果集 

#### 16.解析ListArray

   parseListArray(String jsonResult, String jsonArrayName)

   @param jsonResult json结果

   @param jsonArrayName jsonArray节点名

   @return 结果集

#### 17.解析ListArray

   parseListArray(JSONObject jsonObject, String jsonArrayName)

   @param jsonObject jsonObject对象

   @param jsonArrayName jsonArray节点名

   @return 结果集

### XCLocationUtil（定位相关工具类）

#### 01.是否开启GPS

   isOpenGPS(Context context) 

   @param context 上下文 

   @return 是否开启

#### 02.强制开启GPS

   openGPS(Context context)

   @param context 上下文

#### 03.根据两点经纬度，计算直线距离多少米

   getDistance(String startLongitude, String startLatitude, String endLongitude, String endLatitude)

   @param startLongitude 起始经度

	
   @param startLatitude
 起始纬度

	
   @param endLongitude
 终点经度


   @param endLatitude
 终点纬度	 

   @return 结果值

### XCLogUtil（Log日志工具类）

#### 01.Log.i

   i(Context context, String msg)

   @param tag为当前类名

   @param msg 打印信息

#### 02.Log.e

   e(Context context, String msg)

   @param tag为当前类名

   @param msg 打印信息

#### 03.Log.d

   d(Context context, String msg)

   @param tag为当前类名

   @param msg 打印信息

### XCMD5Util（MD5工具类）

#### 01.获取字符串MD5值

   getMD5(String str)

   @param str 需要加密字符串

   @return 小写加密结果值

#### 02.获取字符串MD5值

   String getMD5(String str, int caseFlag)

   @param str 需要加密字符串

   @param caseFlag 大小写

   @return 结果值

### XCNetUtil（网络工具类）

#### 01.网络是否可用

   isNetworkUse(Context context)

   @param context 上下文

   @return 是否可用

### XCPhoneUtil（手机相关工具类）

#### 01.获取设备名称

   getDeviceName()

   @return 手机设备名称

#### 02.获取设备型号

   getDeviceModel()

   @return 手机设备型号

#### 03.获取当前系统的android版本号

   getSystemVersion()

   @return 手机操作系统的版本号

#### 04.获取手机IMEI码（国际移动设备身份码，它与每台移动电话机一一对应，而且该码是全世界唯一的）

   getIMEICode(Context context)

   @param context 上下文

   @return 手机IMEI码

#### 05.获取手机IMSI码（国际移动用户识别码，储存在SIM卡中，可用于区别移动用户的有效信息）

   getIMSICode(Context context)

   @param context 上下文

   @return 手机IMSI码

#### 06.获取手机号

   getMobileNumber(Context context)

   @param context 上下文

   @return 手机号

### XCScreenUtil（屏幕相关工具类）

#### 01.获取屏幕宽

   getScreenWidth(Context context)

   @param context 上下文

   @return 屏幕宽

#### 02.获取屏幕高

   getScreenHeight(Context context)

   @param context 上下文

   @return 屏幕高

#### 03.获取状态栏高

   getStatusBarHeight(Context context)

   @param context 上下文

   @return 状态栏高

#### 04.获取标题栏高

   getTitleBarHeight(Activity activity)

   @param activity 上下文

   @return 标题栏高


### XCStringUtil（字符串工具类）

#### 01.判断字符串是否为空

   isEmpty(String str)

   @param str 字符串

   @return 是否为:非null、非""、非"null"

#### 02.十六进制转字符串

   toStringHex(String str)

   @param str 16进制字符串

   @return 结果值

### XCThreadUtil（线程管理工具类）

#### 01.获取实例

   getInstance()

   @return XCThreadUtil实例

#### 02.添加线程到集合中

   addThreadList(String threadName, Thread thread)

   @param threadName 线程名

   @param thread 线程

#### 03.停止某个线程

   stopSingle(String threadNameOrUrlName)

   @param threadNameOrUrlName 线程名或url名

#### 04.停止所有线程

   stopAll()

#### 05.中断线程

   interruptThread(Thread thread)

   @param thread thread对象

#### 06.获取正在运行的所有线程

   getThreadList()

   @return 线程集合

#### 07.返回正在执行的线程数量

   getThreadCount()

   @return 线程数

### XCTimeUtil（时间工具类）

#### 01.获取当前时间

   getCurrentTime(String dateFormat)

   @param dateFormat 时间格式

   @return 时间格式字符串

#### 02.获取时间

   getTime(String milliseconds, String dateFormat)

   @param milliseconds 时间戳字符串

   @param dateFormat 时间格式

   @return 时间格式字符串

#### 03.获取时间

   getTime(long milliseconds, String dateFormat)

   @param milliseconds 时间戳

   @param dateFormat 时间格式

   @return 时间格式字符串

#### 04.获取当前星期

   getCurrentWeek()

   @return 例:星期一

#### 05.获取星期

   getWeek(String date)

   @param date 日期格式yyyy-MM-dd

   @return 例:星期一

### XCToastUtil（Toast工具类）

#### 01.显示

   show(Context context, String message)

   @param context 上下文

   @param message 内容信息

#### 02.显示

   show(Context context, String message, int gravity)

   @param context 上下文

   @param message 内容信息

   @param gravity
 例:Gravity.CENTER

#### 03.显示

   show(Context context, int messageId)

   @param context 上下文

   @param messageId 例:R.string.test

#### 04.显示

   show(Context context, int messageId, int gravity)

   @param context 上下文

   @param messageId 例:R.string.test

   @param gravity
 例:Gravity.CENTER

### XCUnitUtil（单位转换工具类）

#### 01.将dip转换为px

   getDipToPx(Context context, float dipValue) 

   @param context 上下文

   @param dipValue dip值

   @return px值

#### 02.将px转换为dip

   getPxToDip(Context context, float dipValue) 

   @param context 上下文

   @param pxValue px值

   @return dip值

#### 03.php时间戳转java时间戳

   String getPhpToJavaTimestamp(String phpTimestamp)

   @param phpTimestamp php时间戳(十位)

   @return java时间戳

### XCViewUtil（View相关工具类）

#### 01.测量宽高

   getMeasureSize(int measureSpec)

   @param measureSpec 待测量宽高

   @return 测量后宽高
   
