package com.alipay.antchain.bridge.plugins.dioxide.core;

import org.junit.Assert;
import org.junit.Test;
import java.util.List;
import java.util.Map;

public class DioxideIsnFinalityTest {
    @Test public void finalizedRootDoesNotHideFailedEmbeddedInvocation() {
        DioxideTransaction root = DioxideTransaction.builder().txHash("root").confirmState("TXN_ARCHIVED")
                .embeddedRelays(List.of(DioxideTransaction.builder().invocation(
                        DioxideTransaction.Invocation.builder().status("IVKRET_EXCEPTION_THROWN").build()).build())).build();
        Assert.assertEquals(DioxideClient.TxFinalityState.FAILED,
                DioxideClient.evaluateTxFinalityWithRelays(root, hash -> null).state());
    }
    @Test public void waitForAllChildrenAndRecognizeBothStateFamilies() {
        DioxideTransaction root = DioxideTransaction.builder().txHash("root").confirmState("TXN_ARCHIVED")
                .invocation(DioxideTransaction.Invocation.builder().status("IVKRET_SUCCESS").relays(List.of("child:0")).build()).build();
        DioxideTransaction child = DioxideTransaction.builder().txHash("child").confirmState("TXN_READY").build();
        Assert.assertEquals(DioxideClient.TxFinalityState.PENDING,
                DioxideClient.evaluateTxFinalityWithRelays(root, hash -> child).state());
        child.setConfirmState(null); child.setState("DUS_ARCHIVED");
        Assert.assertEquals(DioxideClient.TxFinalityState.FINALIZED,
                DioxideClient.evaluateTxFinalityWithRelays(root, hash -> child).state());
        child.setConfirmState("TXN_ABORTED");
        Assert.assertEquals(DioxideClient.TxFinalityState.FAILED,
                DioxideClient.evaluateTxFinalityWithRelays(root, hash -> child).state());
    }
    @Test public void missingOrBlankChildIsNotSuccess() {
        DioxideTransaction root = DioxideTransaction.builder().txHash("root").state("DUS_ARCHIVED")
                .invocation(DioxideTransaction.Invocation.builder().relays(List.of("missing")).build()).build();
        Assert.assertEquals(DioxideClient.TxFinalityState.PENDING,
                DioxideClient.evaluateTxFinalityWithRelays(root, hash -> null).state());
        Assert.assertFalse(DioxideClient.isTxFinalized(null));
    }
}
