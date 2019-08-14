# spring-boot-quartz

本示例不是最佳实践，最佳实践请转移到：<a href="https://gitee.com/youzhibing/spring-boot-2.0.3/tree/master/spring-boot-quartz-plus" target="_blank">spring-boot-quartz-plus</a>
###登录页：
    http://localhost:9001/quartz/login.shtml
    假的，登录效果没有实现，用户名密码无需输入，直接点登录就会跳转主页
###主页：
    http://localhost:9001/quartz/main.shtml
    不从登录页跳转，直接访问主页也是可以的

###新建job的时候要保证业务job类存
    工程中示例job：com.lee.quartz.job.MyJob，com.lee.quartz.job.FetchDataJob

###数据源问题，默认名是springNonTxDataSource.quartzScheduler
    为什么没有用druid的数据源
    为什么会覆盖掉org.quartz.jobStore.dataSource(org.quartz.dataSource?)配置的数据源名
    StdSchedulerFactory存放着quartz的配置项
    SchedulerFactoryBean
    	afterPropertiesSet() -> prepareScheduler()

###分页
    后端有实现分页，前端页面没有实现

###来都来了，留个心呗
