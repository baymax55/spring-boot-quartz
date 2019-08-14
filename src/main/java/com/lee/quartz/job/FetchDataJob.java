package com.lee.quartz.job;

import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.utils.DBConnectionManager;
import org.springframework.scheduling.quartz.LocalDataSourceJobStore;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @author bx
 */
@Slf4j
public class FetchDataJob extends QuartzJobBean {

    // 可通过jobDataMap注入进来
    private String schedulerInstanceName = "quartzScheduler";

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        // private String dataSourceName = "quartzDs";                                  // 用此会找不到
        // private String dataSourceName = "springNonTxDataSource.quartzScheduler";     // 不支持事务
        // private String dataSourceName = "springTxDataSource.quartzScheduler";        // 支持事务
        String dsName = LocalDataSourceJobStore.NON_TX_DATA_SOURCE_PREFIX + schedulerInstanceName;    // 不支持事务
        //String dsName = LocalDataSourceJobStore.TX_DATA_SOURCE_PREFIX + schedulerInstanceName;    // 支持事务
        try {
            Connection connection = DBConnectionManager.getInstance().getConnection(dsName);

            String insertSql = "INSERT INTO tbl_sys_user(name, age) VALUES(?,?) ";

            //try with 自动关闭连接
            try (PreparedStatement ps = connection.prepareStatement(insertSql)) {
                ps.setString(1, "张三");
                ps.setInt(2, 25);
                ps.executeUpdate();
            }
            // 将连接归还给连接池
            connection.close();
            log.info("FetchDataJob...");
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
        }
    }

    public void setSchedulerInstanceName(String schedulerInstanceName) {
        this.schedulerInstanceName = schedulerInstanceName;
    }
}