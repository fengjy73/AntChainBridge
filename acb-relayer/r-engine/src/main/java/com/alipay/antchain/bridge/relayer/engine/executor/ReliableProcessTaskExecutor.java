package com.alipay.antchain.bridge.relayer.engine.executor;

import java.util.concurrent.ExecutorService;
import javax.annotation.Resource;

import com.alipay.antchain.bridge.relayer.commons.model.BlockchainDistributedTask;
import com.alipay.antchain.bridge.relayer.commons.model.IDistributedTask;
import com.alipay.antchain.bridge.relayer.core.service.reliable.ReliableProcessService;
import com.alipay.antchain.bridge.relayer.engine.checker.IDistributedTaskChecker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ReliableProcessTaskExecutor extends BaseScheduleTaskExecutor {
    @Resource
    private ReliableProcessService reliableProcessService;

    @Autowired
    public ReliableProcessTaskExecutor(
            @Qualifier("reliableProcessTaskExecutorThreadsPool") ExecutorService executorService,
            @Qualifier("defaultDistributedTaskChecker") IDistributedTaskChecker distributedTaskChecker
    ) {
        super(executorService, distributedTaskChecker);
    }

    @Override
    public Runnable genTask(IDistributedTask task) {
        return () -> {
            if (task instanceof BlockchainDistributedTask) {
                try {
                    reliableProcessService.process(
                            ((BlockchainDistributedTask) task).getBlockchainProduct(),
                            ((BlockchainDistributedTask) task).getBlockchainId()
                    );
                } catch (Throwable e) {
                    log.error("ProcessScheduleTaskExecutor failed for blockchain {}",
                            ((BlockchainDistributedTask) task).getBlockchainId(), e);
                }
            }
        };
    }
}
