/*
 * Copyright 2023 Ant Group
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.alipay.antchain.bridge.relayer.core.types.network.response;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import com.alipay.antchain.bridge.commons.core.base.CrossChainMessageReceipt;
import com.alipay.antchain.bridge.relayer.commons.constant.SDPMsgProcessStateEnum;
import com.alipay.antchain.bridge.relayer.core.service.confirm.RemoteCrossChainMsgResult;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class QueryCrossChainMsgReceiptsRespPayload implements IResponsePayload {
    public static QueryCrossChainMsgReceiptsRespPayload decodeFromJson(String json) {
        return JSON.parseObject(json, QueryCrossChainMsgReceiptsRespPayload.class);
    }

    @JSONField(name = "receipts")
    private Map<String, CrossChainMessageReceipt> receipts;

    @JSONField(name = "sdp_states")
    private Map<String, SDPMsgProcessStateEnum> sdpStates;

    @Override
    public String encode() {
        return JSON.toJSONString(this);
    }

    public Map<String, RemoteCrossChainMsgResult> toRemoteCrossChainMsgResultMap() {
        Map<String, RemoteCrossChainMsgResult> remoteCrossChainMsgResultMap = new HashMap<>();
        for (Map.Entry<String, CrossChainMessageReceipt> entry : receipts.entrySet()) {
            SDPMsgProcessStateEnum currState = sdpStates.getOrDefault(entry.getKey(), null);
            remoteCrossChainMsgResultMap.put(
                    entry.getKey(),
                    new RemoteCrossChainMsgResult(entry.getValue(), currState)
            );
        }
        return remoteCrossChainMsgResultMap;
    }
}
