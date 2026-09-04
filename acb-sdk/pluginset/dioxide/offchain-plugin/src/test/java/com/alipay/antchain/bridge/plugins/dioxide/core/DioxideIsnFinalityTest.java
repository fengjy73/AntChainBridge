package com.alipay.antchain.bridge.plugins.dioxide.core;

import org.junit.Assert;
import org.junit.Test;
import java.util.List;
import java.util.Map;

public class DioxideIsnFinalityTest {

    @Test public void indexedGroupDoesNotInspectAnotherBusinessFailure() {
        DioxideTransaction bad = DioxideTransaction.builder().invocation(
                DioxideTransaction.Invocation.builder().status("IVKRET_EXCEPTION_THROWN").build()).build();
        DioxideTransaction good = DioxideTransaction.builder().invocation(
                DioxideTransaction.Invocation.builder().status("IVKRET_SUCCESS").relays(List.of("child")).build()).build();
        DioxideTransaction group = DioxideTransaction.builder().txHash("group").state("DUS_ARCHIVED")
                .embeddedRelays(List.of(bad, good)).build();
        DioxideTransaction child = DioxideTransaction.builder().txHash("child").state("DUS_ARCHIVED").build();
        DioxideTransaction root = DioxideTransaction.builder().txHash("root").state("DUS_ARCHIVED")
                .invocation(DioxideTransaction.Invocation.builder().relays(List.of("group:1")).build()).build();
        Map<String, DioxideTransaction> nodes = Map.of("group", group, "child", child);
        Assert.assertEquals(DioxideClient.TxFinalityState.FINALIZED,
                DioxideClient.evaluateTxFinalityWithRelays(root, nodes::get).state());
        root.getInvocation().setRelays(List.of("group:0"));
        Assert.assertEquals(DioxideClient.TxFinalityState.FAILED,
                DioxideClient.evaluateTxFinalityWithRelays(root, nodes::get).state());
        root.getInvocation().setRelays(List.of("group:9"));
        Assert.assertEquals(DioxideClient.TxFinalityState.PENDING,
                DioxideClient.evaluateTxFinalityWithRelays(root, nodes::get).state());
    }

    @Test public void differentMembersOfSameGroupRemainDistinctAndFetchOnce() {
        DioxideTransaction first = DioxideTransaction.builder().invocation(
                DioxideTransaction.Invocation.builder().status("IVKRET_SUCCESS").build()).build();
        DioxideTransaction second = DioxideTransaction.builder().invocation(
                DioxideTransaction.Invocation.builder().status("IVKRET_SUCCESS").relays(List.of("pending")).build()).build();
        DioxideTransaction group = DioxideTransaction.builder().state("DUS_ARCHIVED").embeddedRelays(List.of(first, second)).build();
        DioxideTransaction root = DioxideTransaction.builder().txHash("root").state("DUS_ARCHIVED")
                .invocation(DioxideTransaction.Invocation.builder().relays(List.of("group:0", "group:1")).build()).build();
        java.util.concurrent.atomic.AtomicInteger reads = new java.util.concurrent.atomic.AtomicInteger();
        Assert.assertEquals(DioxideClient.TxFinalityState.PENDING,
                DioxideClient.evaluateTxFinalityWithRelays(root, hash -> {
                    if ("group".equals(hash)) { reads.incrementAndGet(); return group; }
                    return null;
                }).state());
        Assert.assertEquals(1, reads.get());
    }
    @Test public void finalizedRootDoesNotHideFailedEmbeddedInvocation() {
        DioxideTransaction root = DioxideTransaction.builder().txHash("root").confirmState("TXN_ARCHIVED")
                .embeddedRelays(List.of(DioxideTransaction.builder().invocation(
                        DioxideTransaction.Invocation.builder().status("IVKRET_EXCEPTION_THROWN").build()).build())).build();
        Assert.assertEquals(DioxideClient.TxFinalityState.FAILED,
                DioxideClient.evaluateTxFinalityWithRelays(root, hash -> null).state());
    }
    @Test public void waitForAllChildrenAndRecognizeBothStateFamilies() {
        DioxideTransaction root = DioxideTransaction.builder().txHash("root").confirmState("TXN_ARCHIVED")
                .invocation(DioxideTransaction.Invocation.builder().status("IVKRET_SUCCESS").relays(List.of("child")).build()).build();
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
