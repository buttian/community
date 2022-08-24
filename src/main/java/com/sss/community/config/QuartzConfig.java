package com.sss.community.config;

import com.sss.community.quartz.PostScoreRefreshJob;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SimpleTriggerFactoryBean;
import com.sss.community.quartz.AlphaJob;


@Configuration
public class QuartzConfig {

    /**
     * 配置JobDetail
     * FactoryBean可以简化Bean的实例化过程：
     * 1.通过FactoryBean封装Bean的实例化过程。
     * 2.将FactoryBean装配到Spring容器里。
     * 3.将FactoryBean注入给其他的Bean。
     * 4.其他的Bean得到的是FactoryBean所管理的对象实例。
     *
     * @return
     */
//    @Bean
    public JobDetailFactoryBean alphaJobDetail() {
        JobDetailFactoryBean factoryBean = new JobDetailFactoryBean();
        factoryBean.setJobClass(AlphaJob.class);
        factoryBean.setName("alphaJob");
        factoryBean.setGroup("alphaJobGroup");
        // 任务是否持久保存
        factoryBean.setDurability(true);
        // 是否可恢复
        factoryBean.setRequestsRecovery(true);
        return factoryBean;
    }

    /**
     * 配置Trigger(SimpleTriggerFactoryBean, CronTriggerFactoryBean)
     * 将FactoryBean注入
     *
     * @param alphaJobDetail
     * @return
     */
//    @Bean
    public SimpleTriggerFactoryBean alphaTrigger(JobDetail alphaJobDetail) {
        SimpleTriggerFactoryBean factoryBean = new SimpleTriggerFactoryBean();
        factoryBean.setJobDetail(alphaJobDetail);
        factoryBean.setName("alphaTrigger");
        factoryBean.setGroup("alphaTriggerGroup");
        // 多久执行一次
        factoryBean.setRepeatInterval(3000);
        // 存储job状态的容器
        factoryBean.setJobDataMap(new JobDataMap());

        return factoryBean;
    }

    /**
     * 刷新帖子分数任务
     *
     * @return
     */
    @Bean
    public JobDetailFactoryBean postScoreRefreshJobDetail() {
        JobDetailFactoryBean factoryBean = new JobDetailFactoryBean();
        factoryBean.setJobClass(PostScoreRefreshJob.class);
        factoryBean.setName("postScoreRefreshJob");
        factoryBean.setGroup("communityJobGroup");
        factoryBean.setDurability(true);
        factoryBean.setRequestsRecovery(true);
        return factoryBean;
    }

    /**
     * @param postScoreRefreshJobDetail
     * @return
     */
    @Bean
    public SimpleTriggerFactoryBean postScoreRefreshTrigger(JobDetail postScoreRefreshJobDetail) {
        SimpleTriggerFactoryBean factoryBean = new SimpleTriggerFactoryBean();
        factoryBean.setJobDetail(postScoreRefreshJobDetail);
        factoryBean.setName("postScoreRefreshTrigger");
        factoryBean.setGroup("communityTriggerGroup");
        factoryBean.setRepeatInterval(1000 * 60 * 5);
        factoryBean.setJobDataMap(new JobDataMap());
        return factoryBean;
    }
}
